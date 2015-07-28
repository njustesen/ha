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

import sun.java2d.pipe.SpanShapeRenderer.Simple;
import testcase.TestCase;
import ui.UI;
import util.MapLoader;
import model.DECK_SIZE;
import model.HaMap;
import ai.GreedyActionAI;
import ai.HeuristicAI;
import ai.RandomAI;
import ai.RandomSwitchAI;
import ai.StatisticAi;
import ai.evaluation.HeuristicEvaluator;
import ai.neat.jneat.Neat;
import ai.neat.jneat.Network;
import ai.neat.jneat.Organism;
import ai.neat.jneat.Population;
import ai.neat.jneat.Species;
import ai.util.RAND_METHOD;

public class NeatTester {

	private static Random random;
	//private static GameArguments gameArgs = new GameArguments(false, null, null, "a", DECK_SIZE.STANDARD);
	private static GameArguments gameArgs = new GameArguments(false, null, null, "a", DECK_SIZE.STANDARD);
	
	private static double level = 1;

	//private static final String pop_file = "";
	//private static final String pop_file = "pop_1000";
	//private static final String pop_file = "pop_1_normal";
	//private static final String pop_file = "pop_3817_small";
	private static final String pop_file = "best-standard_pop_746";
	//private static final String pop_file = "best-finalpop_655";
	//private static final String pop_file = "pop_3817";
	
	private static final boolean SIMPLE = false;
	private static final int MATCHES = 1;
	
	public static void main(String[] args) throws Exception{
		
		Population pop = null;
		int gen = 1;
		if (pop_file != ""){
			pop = new Population(pop_file);
			gen = Integer.parseInt(pop_file.split("_")[pop_file.split("_").length-1]) + 1;
		}
		
		Network bestNet = null;
		double bestFitness = -1;
		bestFitness = -1;
		int i = 0;
		Organism bestOrg = null;
		Iterator itr_organism = pop.getOrganisms().iterator();
		while (itr_organism.hasNext()) {
			Organism organism = ((Organism) itr_organism.next());
			//System.out.println(fitness);
			double fitness = fitnessVsPseudoRandom(organism.getNet(), MATCHES) * 100.0;
			System.out.print("|");
			if (fitness > bestFitness){
				bestFitness = fitness;
				bestNet = organism.net;
				bestOrg = organism;
			}
		}

		//saveBest(bestOrg);
		show(bestNet);
		
	}
	
	private static void saveBest(Organism bestOrg) {
		Population bestPop = new Population();
		bestPop.organisms = new Vector();
		bestPop.species = new Vector();
		bestPop.organisms.add(bestOrg);
		bestPop.species.add(bestOrg.getSpecies());
		bestPop.print_to_filename("best-" + pop_file);
	
	}

	private static RandomSwitchAI randomSwitch = new RandomSwitchAI(level, new RandomAI(RAND_METHOD.BRUTE), new GreedyActionAI(new HeuristicEvaluator(false)));
	private static int runs = 1;
	private static String size = "standard";
	
	private static HaMap tiny;
	private static HaMap small;
	private static HaMap standard;
	
	private static void show(Network net) {
		
		try {
			tiny = MapLoader.get("a-tiny");
			small = MapLoader.get("a-small");
			standard = MapLoader.get("a");
		} catch (final IOException e) {
			e.printStackTrace();
		}
		
		TestCase.P1START = true;
		TestCase.GFX = true;
		TestCase.SLEEP = 200;
		
		TestCase test = 
				new TestCase(
						new StatisticAi(new NaiveNeatAI(net, false, SIMPLE)), 
						new StatisticAi(randomSwitch), 
						runs, 
						"vistest"+pop_file+Math.random(), 
						map(size), 
						deck(size)
						);
		
		/*
		TestCase test = 
				new TestCase(
						new StatisticAi(randomSwitch), 
						new StatisticAi(randomSwitch), 
						runs, 
						"greedyaction-vs-greedyaction_p1starting", 
						map(size), 
						deck(size)
						);
		*/
		
		test.run();
		
	}
	
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

	private static DECK_SIZE deck(String size) {
		if (size.equals("tiny"))
			return DECK_SIZE.TINY;
		if (size.equals("small"))
			return DECK_SIZE.SMALL;
	
		return DECK_SIZE.STANDARD;
	}
	
	private static HaMap map(String size) {
		if (size.equals("tiny"))
			return tiny;
		if (size.equals("small"))
			return small;
		return standard;
	}

	private static double score(int p, double winner) {
		if (p == winner)
			return 1;
		if (p == 0)
			return 0.5;
		return 0;
	}
	
}
