package ai.neat;

import game.Game;
import game.GameArguments;
import game.GameState;

import java.util.List;
import java.util.concurrent.Callable;

import ai.GreedyActionAI;
import ai.RandomAI;
import ai.RandomSwitchAI;
import ai.evaluation.HeuristicEvaluator;
import ai.neat.jneat.Network;
import ai.neat.jneat.Organism;
import ai.util.RAND_METHOD;

public class NeatWorker implements Callable<Double>{

	public List<Organism> organisms;
	public int matches;
	public double level;
	public GameArguments gameArgs;
	public boolean simple;
	public double average;
	public double bestFitness;
	public Network bestNet;
	public double sum;
	public int id;
	
	public NeatWorker(List<Organism> organisms, int matches, double level,
			GameArguments gameArgs, boolean simple, int id) {
		super();
		this.organisms = organisms;
		this.matches = matches;
		this.level = level;
		this.gameArgs = gameArgs;
		this.simple = simple;
		this.id = id;
	}

	@Override
	public Double call() throws Exception {
		System.out.println("starting thread " + id + " with " + organisms.size() + " organisms.");
		sum = 0;
		bestFitness = -1;
		for(Organism org : organisms){
			double fitness = fitnessVsPseudoRandom(org.getNet(), matches) * 100.0;
			org.setFitness(fitness);
			System.out.print(id);
			sum += fitness;
			if (fitness > bestFitness){
				bestFitness = fitness;
				bestNet = org.net;
			}
		}
		
		average = (double)sum / (double)organisms.size();
		System.out.println("stopping thread " + id);
		return bestFitness;
	}
	
	private double fitnessVsPseudoRandom(Network net, int runs) {
		RandomSwitchAI randomSwitch = new RandomSwitchAI(level, new RandomAI(RAND_METHOD.BRUTE), new GreedyActionAI(new HeuristicEvaluator(false)));
		randomSwitch.prob = level;
		double sum = 0;
		Game game = new Game(new GameState(null), gameArgs);
		for(int i = 0; i < runs; i++){
			if (i != 0)
				game.state = new GameState(game.state.map);
			game.player1 = new NaiveNeatAI(net, false, simple);
			game.player2 = randomSwitch;
			
			game.run();
			double val = game.state.getWinner();
			sum += score(1, val);
		}
		
		return sum / (double)runs;
	}
	
	private double score(int p, double winner) {
		if (p == winner)
			return 1;
		if (p == 0)
			return 0.5;
		return 0;
	}

}
