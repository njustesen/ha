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
import ai.HeuristicAI;
import ai.RandomAI;
import ai.RandomSwitchAI;
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
	private static double level = .8;

	//private static final String pop_file = "";
	private static final String pop_file = "pop_473";
	
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
	
	private static void show(Network net) {
		
		Game game = new Game(new GameState(null), gameArgs);
		
		game.state = new GameState(game.state.map);
		game.player1 = new NaiveNeatAI(net, true);
		game.player2 = new RandomSwitchAI(level, new RandomAI(RAND_METHOD.TREE), new GreedyActionAI(new HeuristicEvaluator(false)));
		game.ui = new UI(game.state, false, false, false);
		game.gameArgs.sleep = 500;
		game.run();
		
	}

	private static double score(int p, double winner) {
		if (p == winner)
			return 1;
		if (p == 0)
			return 0.5;
		return 0;
	}
	
}
