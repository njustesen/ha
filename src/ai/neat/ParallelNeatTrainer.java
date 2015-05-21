package ai.neat;

import game.Game;
import game.GameArguments;
import game.GameState;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import ui.UI;
import model.DECK_SIZE;
import ai.GreedyActionAI;
import ai.RandomAI;
import ai.RandomSwitchAI;
import ai.evaluation.HeuristicEvaluator;
import ai.neat.jneat.Neat;
import ai.neat.jneat.Network;
import ai.neat.jneat.Organism;
import ai.neat.jneat.Population;
import ai.neat.jneat.Species;
import ai.util.RAND_METHOD;

public class ParallelNeatTrainer {

	private static final int POP_SIZE = 64;
	private static final double PROP_LINK = 0.5;
	private static final boolean RECURRENT = false;
	private static final int GENERATIONS = 50000;
	private static final int MATCHES = 20;
	private static Random random;
	private static GameArguments gameArgs = new GameArguments(false, null, null, "a", DECK_SIZE.STANDARD);

	private static final double step = 0.05;
	
	private static double level = 0.0;
	private static String pop_file = "pop_";
	//private static final String pop_file = "pop_3259";
	private static final boolean SIMPLE = false;
																																											
	public static void main(String[] args) throws Exception{
		if (args.length>0)
			pop_file = pop_file + args[0];
		else
			pop_file = "";
		if (args.length>1)
			level = Double.parseDouble(args[1]);
		
		//gameArgs.sleep = 200;
		Neat.readParam("parameters.ne");
		Neat.p_num_trait_params = 20;
		//Neat.p_dropoff_age = 50;
		Neat.p_age_significance = 1.1;
		Neat.p_survival_thresh = 0.9;
		Neat.p_compat_threshold = 0.2;
		
		int inputs = 5;
		if (!SIMPLE){
			int squares = 0;
			if (gameArgs.mapName.equals("a"))
				squares = 9*5;
			else if (gameArgs.mapName.equals("a-small"))
				squares = 7*4;
			else if (gameArgs.mapName.equals("a-tiny"))
				squares = 5*3;
			inputs = squares * 13 + 5 + 10;
		}
		
		Population pop = null;
		int gen = 1;
		if (pop_file != ""){
			pop = new Population(pop_file);
			gen = Integer.parseInt(pop_file.split("_")[1]) + 1;
		} else {
			pop = new Population(POP_SIZE, inputs, 1, 10, RECURRENT, PROP_LINK);
		}
		
		random = new Random();
		
		pop.verify();
		double bestFitness = -1;
		Network bestNetwork = null;
		double avg = 0;
		int g = gen;
		for (; gen <= g + GENERATIONS; gen++) {
			System.out.println("\n---------------- Generation ---------------------- " + gen);
			
			Iterator itr_organism = pop.getOrganisms().iterator();
			int processors = Runtime.getRuntime().availableProcessors();
			System.out.println("Processors: " + processors);
			ExecutorService executor = Executors.newFixedThreadPool(processors);
			
			List<NeatWorker> workers = new ArrayList<NeatWorker>();
			int idx = 0;
			int istep = (int) ((double)pop.getOrganisms().size() / (double)processors);
			
			for(int i = 0; i < processors; i++){
				NeatWorker worker = new NeatWorker(new ArrayList<Organism>(), MATCHES, level, gameArgs, SIMPLE, i);
				for(int m = idx; m < idx + istep; m++){
					if (m >= pop.getOrganisms().size())
						break;
					worker.organisms.add((Organism)(pop.getOrganisms().get(m)));
				}
				idx += istep;
				workers.add(worker);
			}
			
			List<Future<Double>> futures = executor.invokeAll(workers);
			for(Future<Double> future : futures){
				//System.out.println("done:"+future.isDone());
				//System.out.println("cacelled:"+future.isCancelled());
				//System.out.println("value:"+future.get().doubleValue());
			}
			double sum = 0;
			for(NeatWorker worker : workers){
				sum += worker.sum;
				if (worker.bestFitness > bestFitness){
					bestFitness = worker.bestFitness;
					bestNetwork = worker.bestNet;
				}
			}
			
			System.out.println("\nDone");
			avg = sum / pop.getOrganisms().size();
			
			//compute average and max fitness for each species
			// Necessary? OR does it happen in epoch?
			Iterator itr_specie;
			itr_specie = pop.species.iterator();
			while (itr_specie.hasNext()) {
				Species _specie = ((Species) itr_specie.next());
				_specie.compute_average_fitness();
				_specie.compute_max_fitness();
			}
			
			saveStats(bestFitness, avg, gen, level, pop.getOrganisms().size());
			
			if (bestFitness == 100)
				level += step;
			
			// EPOCH
			pop.epoch(gen);
			
			pop.print_to_filename("pop_"+gen);
			//pop.print_to_file_by_species("pop_spe_"+gen);
			
			//System.out.print("\n  Population : innov num   = " + pop.getCur_innov_num());
			//System.out.print("\n             : cur_node_id = " + pop.getCur_node_id());
			//System.out.print("\n   result    : " + pop.);
		}

		saveStats(bestFitness, avg, gen, level, pop.getOrganisms().size());
		pop.print_to_filename("pop_"+gen);
		
	}
	

	private static void saveStats(double best, double mean, int gen, double level, int organisms) {
		
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("neat-stats" + "-" + gameArgs.mapName + "-simple" + SIMPLE, true)))) {
			out.println(gen+"\t"+best+"\t"+mean + "\t" + level + "\t" + organisms);
			System.out.println(gen+"\t"+best+"\t"+mean + "\t" + level + "\t" + organisms);
		}catch (IOException e) {
		    System.out.println("could not save to neat-stats");
		}
		
	}
	
	
}
