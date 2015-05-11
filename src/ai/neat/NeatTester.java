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
	private static GameArguments gameArgs = new GameArguments(false, null, null, "a-tiny", DECK_SIZE.TINY);
	private static double level = 1;

	//private static final String pop_file = "";
	//private static final String pop_file = "pop_1000";
	private static final String pop_file = "pop_1_normal";
	//private static final String pop_file = "pop_473";
	private static final boolean SIMPLE = false;
	
	public static void main(String[] args) throws Exception{
		//gameArgs.sleep = 200;
		
		Population pop = null;
		int gen = 1;
		if (pop_file != ""){
			pop = new Population(pop_file);
			gen = Integer.parseInt(pop_file.split("_")[1]) + 1;
		}
		
		Network bestNet = null;
		double bestFitness = -1;
		bestFitness = -1;
		
		Iterator itr_organism = pop.getOrganisms().iterator();
		while (itr_organism.hasNext()) {
			Organism organism = ((Organism) itr_organism.next());
			//System.out.println(fitness);
			double fitness = organism.getFitness();
			if (fitness > bestFitness){
				bestFitness = fitness;
				bestNet = organism.net;
			}
		}

		show(bestNet);
		
	}
	
	private static RandomSwitchAI randomSwitch = new RandomSwitchAI(level, new RandomAI(RAND_METHOD.BRUTE), new GreedyActionAI(new HeuristicEvaluator(false)));
	private static int runs = 10000;
	private static String size = "tiny";
	
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
		
		TestCase test = 
				new TestCase(
						new StatisticAi(new NaiveNeatAI(net, false, SIMPLE)), 
						new StatisticAi(randomSwitch), 
						runs, 
						"neat-complex-vs-egreedy1"+pop_file, 
						map(size), 
						deck(size)
						);
		
		test.run();
		
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
