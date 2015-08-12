package examples;

import ai.AI;
import ai.GreedyActionAI;
import ai.GreedyTurnAI;
import ai.RandomAI;
import ai.evaluation.HeuristicEvaluator;
import ai.util.RAND_METHOD;
import model.DECK_SIZE;
import game.Game;
import game.GameArguments;

public class Examples {

	public static void main(String[] args) {
		
		//humanVsHuman();
		humanVsAI();
		
	}

	private static void humanVsAI() {
		
		int budget = 4000; // 4 sec for AI's
		
		AI p1 = null;
		//AI p2 = new RandomAI(RAND_METHOD.BRUTE);
		//AI p2 = new GreedyActionAI(new HeuristicEvaluator(false));
		AI p2 = new GreedyTurnAI(new HeuristicEvaluator(false), budget);
		
		GameArguments gameArgs = new GameArguments(true, p1, p2, "a", DECK_SIZE.STANDARD);
		gameArgs.budget = budget; 
		Game game = new Game(null, gameArgs);
		
		game.run();
		
	}

	private static void humanVsHuman() {
		
		AI p1 = null;
		AI p2 = null;
		
		Game game = new Game(null, new GameArguments(true, p1, p2, "a", DECK_SIZE.STANDARD));
		game.run();
		
	}

}
