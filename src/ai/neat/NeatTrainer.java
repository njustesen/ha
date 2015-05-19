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

public class NeatTrainer {

	private static final int POP_SIZE = 64;
	private static final double PROP_LINK = 0.5;
	private static final boolean RECURRENT = false;
	private static final int GENERATIONS = 50000;
	private static final int MATCHES = 20;
	private static Random random;
	private static GameArguments gameArgs = new GameArguments(false, null, null, "a-tiny", DECK_SIZE.TINY);

	private static final double step = 0.05;
	
	private static double level = 0.0;
	private static final String pop_file = "";
	//private static final String pop_file = "pop_3259";
	private static final boolean SIMPLE = false;
																																											
	public static void main(String[] args) throws Exception{
		
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
			if (gameArgs.mapName.equals("a-small"))
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
		
		Network bestNet = null;
		double bestFitness = -1;
		double sum = 0;
		double avg = 0;
		double c = 0;
		int g = gen;
		for (; gen <= g + GENERATIONS; gen++) {
			System.out.println("\n---------------- Generation ---------------------- " + gen);
			bestFitness = -1;
			
			Iterator itr_organism = pop.getOrganisms().iterator();
			while (itr_organism.hasNext()) {
				Organism organism = ((Organism) itr_organism.next());
				//double fitness = fitness(organism, pop.getOrganisms());
				double fitness = fitnessVsPseudoRandom(organism.getNet(), MATCHES) * 100.0;
				//System.out.println(fitness);
				organism.setFitness(fitness);
				c++;
				sum += fitness;
				if (fitness > bestFitness){
					bestFitness = fitness;
					bestNet = organism.net;
				}
				System.out.print("|");
			}
			
			System.out.println("\nDone");
			avg = sum / c;
			
			//compute average and max fitness for each species
			// Necessary? OR does it happen in epoch?
			Iterator itr_specie;
			itr_specie = pop.species.iterator();
			while (itr_specie.hasNext()) {
				Species _specie = ((Species) itr_specie.next());
				_specie.compute_average_fitness();
				_specie.compute_max_fitness();
			}
			
			saveStats(bestFitness, avg, gen, level);
			
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
		
		show(bestNet);

		saveStats(bestFitness, avg, gen, level);
		pop.print_to_filename("pop_"+gen);
		
	}
	
	private static void show(Network net) {
		
		double sum = 0;
		Game game = new Game(new GameState(null), gameArgs);
		
		game.state = new GameState(game.state.map);
		//game.player1 = new NaiveNeatAI(net, false);
		game.player1 = new CompressedNeatAI(net, false);
		game.player2 = new RandomAI(RAND_METHOD.TREE);
		//game.ui = new UI(game.state, false, false, false);
		game.run();
		double val = game.state.getWinner();
		sum += score(1, val);
		
	}

	private static void saveStats(double best, double mean, int gen, double level) {
		
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("neat-stats" + "-" + gameArgs.mapName + "-simple" + SIMPLE, true)))) {
			out.println(gen+"\t"+best+"\t"+mean + "\t" + level);
		}catch (IOException e) {
		    System.out.println("could not save to neat-stats");
		}
		
	}
	
	private static RandomSwitchAI randomSwitch = new RandomSwitchAI(level, new RandomAI(RAND_METHOD.BRUTE), new GreedyActionAI(new HeuristicEvaluator(false)));
	
	private static double fitnessVsPseudoRandom(Network net, int runs) {
		randomSwitch.prob = level;
		double sum = 0;
		Game game = new Game(new GameState(null), gameArgs);
		for(int i = 0; i < runs; i++){
			if (i != 0)
				game.state = new GameState(game.state.map);
			game.player1 = new NaiveNeatAI(net, false, SIMPLE);
			game.player2 = randomSwitch;
			
			game.run();
			double val = game.state.getWinner();
			sum += score(1, val);
		}
		
		return sum / (double)runs;
	}

	private static double fitness(Network net, Vector organisms, int runs) {
		
		List<Organism> played = new ArrayList<Organism>();
		
		double sum = 0;
		Game game = new Game(new GameState(null), gameArgs);
		for(int i = 0; i < runs; i++){
			if (i != 0)
				game.state = new GameState(game.state.map);
			Organism other = null;
			while(other == null || other.getNet().equals(net))
				other = (Organism) organisms.get(random.nextInt(organisms.size()));
			game.player1 = new NaiveNeatAI(net, false, SIMPLE);
			game.player2 = new NaiveNeatAI(other.getNet(), false, SIMPLE);
			game.run();
			double val = game.state.getWinner();
			sum += score(1, val);
			played.add(other);
		}
		
		return sum;
	}

	private static double score(int p, double winner) {
		if (p == winner)
			return 1;
		if (p == 0)
			return 0.5;
		return 0;
	}
	
}
