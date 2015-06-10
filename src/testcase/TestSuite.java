package testcase;

import game.GameState;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.DECK_SIZE;
import model.HaMap;
import ui.UI;
import util.CachedLines;
import util.MapLoader;
import action.Action;
import action.SingletonAction;
import ai.AI;
import ai.RandomHeuristicAI;
import ai.GreedyActionAI;
import ai.GreedyTurnAI;
import ai.HeuristicAI;
import ai.HybridAI;
import ai.RandomAI;
import ai.RandomSwitchAI;
import ai.StatisticAi;
import ai.evaluation.HeuristicEvaluator;
import ai.evaluation.LeafParallelizer;
import ai.evaluation.MaterialBalanceEvaluator;
import ai.evaluation.MeanEvaluator;
import ai.evaluation.OpponentRolloutEvaluator;
import ai.evaluation.RolloutEvaluator;
import ai.evaluation.WinLoseEvaluator;
import ai.evaluation.LeafParallelizer.LEAF_METHOD;
import ai.evolution.IslandHorizonEvolution;
import ai.evolution.RollingHorizonEvolution;
import ai.mcts.Mcts;
import ai.mcts.RootParallelizedMcts;
import ai.util.RAND_METHOD;

public class TestSuite {

	private static HaMap tiny;
	private static HaMap small;
	private static HaMap standard;
	
	private static AI randomSwitch = new RandomSwitchAI(0.5, new RandomAI(RAND_METHOD.TREE), new HeuristicAI());

	public static void main(String[] args) {

		try {
			tiny = MapLoader.get("a-tiny");
			small = MapLoader.get("a-small");
			standard = MapLoader.get("a");
		} catch (final IOException e) {
			e.printStackTrace();
		}
		
		if (args[0].equals("mcts-rollouts-1"))
			MctsRolloutTests(Integer.parseInt(args[1]), args[2]);
		else if (args[0].equals("mcts-c"))
			MctsCTests(Integer.parseInt(args[1]), args[2]);
		else if (args[0].equals("mcts-cut"))
			MctsCutTests(Integer.parseInt(args[1]), args[2]);
		else if (args[0].equals("mcts-leaf"))
			MctsLeafTests(Integer.parseInt(args[1]), args[2]);
		else if (args[0].equals("mcts-root"))
			MctsRootTests(Integer.parseInt(args[1]), args[2]);
		else if (args[0].equals("rolling-mutrate"))
			RollingMutRate(Integer.parseInt(args[1]), args[2]);
		else if (args[0].equals("rolling-killrate"))
			RollingKillRate(Integer.parseInt(args[1]), args[2]);
		else if (args[0].equals("rolling-vs-greedyaction"))
			RollingVsGreedyAction(Integer.parseInt(args[1]), args[2]);
		else if (args[0].equals("rolling-vs-greedyaction-times"))
			RollingVsGreedyActionTimes(Integer.parseInt(args[1]), args[2]);
		else if (args[0].equals("rolling-vs-greedyturn-times"))
			RollingVsGreedyTurnTimes(Integer.parseInt(args[1]), args[2]);
		else if (args[0].equals("rolling-vs-greedyturn"))
			RollingVsGreedyTurn(Integer.parseInt(args[1]), args[2]);
		else if (args[0].equals("greedy-action-vs-random"))
			GreedyActionVsRandom(Integer.parseInt(args[1]), args[2]);
		else if (args[0].equals("greedy-action-vs-greedy-turn"))
			GreedyActionVsGreedyTurn(Integer.parseInt(args[1]), args[2]);
		else if (args[0].equals("mcts-vs-greedy-turn"))
			MctsVsGreedyTurn(Integer.parseInt(args[1]), args[2]);
		else if (args[0].equals("mcts-vs-greedy-action"))
			MctsVsGreedyAction(Integer.parseInt(args[1]), args[2]);
		else if (args[0].equals("mcts-vs-greedy-action-long"))
			MctsVsGreedyActionLong(Integer.parseInt(args[1]), args[2]);
		else if (args[0].equals("mcts-vs-greedy-turn-AP"))
			MctsVsGreedyTurnAP(Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]));
		else if (args[0].equals("mcts-vs-greedy-action-AP"))
			MctsVsGreedyActionAp(Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4]));
		else if (args[0].equals("rolling-vs-greedy-action-AP"))
			RollingVsGreedyActionAp(Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4]));
		else if (args[0].equals("hybrid-vs-greedy-action"))
			HybridVsGreedyAction(Integer.parseInt(args[1]), args[2]);
		else if (args[0].equals("hybrid-vs-greedy-turn"))
			HybridVsGreedyTurn(Integer.parseInt(args[1]), args[2]);
		else if (args[0].equals("fitness-rolling-mcts"))
			fitnessRollingMcts();
		else if (args[0].equals("fitness-rolling"))
			fitnessRolling();
		else if (args[0].equals("leaf-rolling"))
			leafRolling(Integer.parseInt(args[1]), args[2]);
		else if (args[0].equals("rolling-vs-island"))
			rollingVsIsland(Integer.parseInt(args[1]), args[2]);
		else if (args[0].equals("greedy-vs-action-time"))
			greedyTurnVsGreedyAction(Integer.parseInt(args[1]), args[2]);
		else if (args[0].equals("rolling-vs-greedy-time"))
			rollingVsGreedyTurn(Integer.parseInt(args[1]), args[2]);
		else if (args[0].equals("rolling-vs-history"))
			rollingVsHistory(Integer.parseInt(args[1]), args[2]);
		else if (args[0].equals("mcts-depth-vs-mcts-opponent"))
			mctsDepthVsMctsOpponent(Integer.parseInt(args[1]), args[2]);
		else if (args[0].equals("hybrid-vs-rolling"))
			hybridVsRolling(Integer.parseInt(args[1]), args[2]);
	}
	
	private static void hybridVsRolling(int runs, String size) {
		
		int budget = 10000;
		RollingHorizonEvolution rolling = new RollingHorizonEvolution(true, 100, .5, .75, budget, new RolloutEvaluator(1, 1, new RandomHeuristicAI(0.4), new HeuristicEvaluator(false)));
		
		HybridAI hybrid = new HybridAI(new HeuristicEvaluator(false), 4000, 500,  
				new RolloutEvaluator(50, 1, new RandomHeuristicAI(0.4), new HeuristicEvaluator(false), true), 8,
				new IslandHorizonEvolution(false, 32, .3, .66, 500, new HeuristicEvaluator(true)));
		
		TestCase.GFX = true;
		new TestCase(new StatisticAi(hybrid), new StatisticAi(rolling), runs, "hybrid-vs-rolling", map(size), deck(size)).run();
		
	}
	
	private static void mctsDepthVsMctsOpponent(int runs, String size) {
		
		Mcts mcts1 = new Mcts(5000, new OpponentRolloutEvaluator(1, randomSwitch, new HeuristicEvaluator(true)));
		Mcts mcts2 = new Mcts(5000, new RolloutEvaluator(1, 1, randomSwitch, new HeuristicEvaluator(true)));
		
		TestCase.GFX = true;
		new TestCase(new StatisticAi(mcts1), new StatisticAi(mcts2), runs, "mcts-depth-vs-mcts-opponent", map(size), deck(size)).run();
		
	}

	private static void rollingVsHistory(int runs, String size) {
		for(int budget = 10; budget <= 15000; budget = budget*5){
			RollingHorizonEvolution rolling = new RollingHorizonEvolution(false, 100, .5, .75, budget, new RolloutEvaluator(1, 1, randomSwitch, new HeuristicEvaluator(false)));
			RollingHorizonEvolution history = new RollingHorizonEvolution(true, 100, .5, .75, budget, new RolloutEvaluator(1, 1, randomSwitch, new HeuristicEvaluator(false)));
			new TestCase(new StatisticAi(rolling), new StatisticAi(history), runs, "rolling-vs-history-"+budget+"ms", map(size), deck(size)).run();
		}
		int budget = 12500;
		RollingHorizonEvolution rolling = new RollingHorizonEvolution(false, 100, .5, .75, budget, new RolloutEvaluator(1, 1, randomSwitch, new HeuristicEvaluator(false)));
		IslandHorizonEvolution island = new IslandHorizonEvolution(true, 100, .5, .75, budget, new RolloutEvaluator(1, 1, randomSwitch, new HeuristicEvaluator(false)));
		new TestCase(new StatisticAi(rolling), new StatisticAi(island), runs, "rolling-vs-history-"+budget+"ms", map(size), deck(size)).run();
	}
	
	private static void rollingVsIsland(int runs, String size) {
		for(int budget = 50; budget <= 15000; budget = budget*5){
			RollingHorizonEvolution rolling = new RollingHorizonEvolution(false, 100, .5, .75, budget, new RolloutEvaluator(1, 1, randomSwitch, new HeuristicEvaluator(false)));
			IslandHorizonEvolution island = new IslandHorizonEvolution(false, 100, .5, .75, budget, new RolloutEvaluator(1, 1, randomSwitch, new HeuristicEvaluator(false)));
			new TestCase(new StatisticAi(rolling), new StatisticAi(island), runs, "rolling-vs-island-"+budget+"ms", map(size), deck(size)).run();
		}
		int budget = 12500;
		RollingHorizonEvolution rolling = new RollingHorizonEvolution(false, 100, .5, .75, budget, new RolloutEvaluator(1, 1, randomSwitch, new HeuristicEvaluator(false)));
		IslandHorizonEvolution island = new IslandHorizonEvolution(false, 100, .5, .75, budget, new RolloutEvaluator(1, 1, randomSwitch, new HeuristicEvaluator(false)));
		new TestCase(new StatisticAi(rolling), new StatisticAi(island), runs, "rolling-vs-island-"+budget+"ms", map(size), deck(size)).run();
	}

	private static void rollingVsGreedyTurn(int runs, String size) {
		for(int budget = 10; budget <= 15000; budget = budget*5){
			AI turn = new GreedyTurnAI(new HeuristicEvaluator(false), budget);
			RollingHorizonEvolution rolling = new RollingHorizonEvolution(false, 100, .5, .75, budget, new RolloutEvaluator(1, 1, randomSwitch, new HeuristicEvaluator(false)));
			new TestCase(new StatisticAi(turn), new StatisticAi(rolling), runs, "action-vs-turn-"+budget+"ms", map(size), deck(size)).run();
		}
		int budget = 12500;
		AI turn = new GreedyTurnAI(new HeuristicEvaluator(false), budget);
		RollingHorizonEvolution rolling = new RollingHorizonEvolution(false, 100, .5, .75, budget, new RolloutEvaluator(1, 1, randomSwitch, new HeuristicEvaluator(false)));
		
		new TestCase(new StatisticAi(turn), new StatisticAi(rolling), runs, "rolling-vs-greedy-turn-"+budget+"ms", map(size), deck(size)).run();
		
	}

	private static void greedyTurnVsGreedyAction(int runs, String size) {
		
		//for(int budget = 10; budget <= 15000; budget = budget*5){
		int budget = 12500;	
		AI turn = new GreedyTurnAI(new HeuristicEvaluator(false), budget);
		AI action = new GreedyActionAI(new HeuristicEvaluator(false));
		new TestCase(new StatisticAi(turn), new StatisticAi(action), runs, "action-vs-turn-"+budget+"ms", map(size), deck(size)).run();
		//}
		
	}

	private static void leafRolling(int runs, String size) {
		RollingHorizonEvolution rollingA = new RollingHorizonEvolution(false, 100, .5, .75, 2000, new RolloutEvaluator(1, 1, randomSwitch, new HeuristicEvaluator(false)));
		RollingHorizonEvolution rollingB = new RollingHorizonEvolution(false, 100, .5, .75, 2000, new LeafParallelizer(new RolloutEvaluator(1, 1, randomSwitch, new HeuristicEvaluator(false)), LEAF_METHOD.WORST));
		
		new TestCase(new StatisticAi(rollingA), new StatisticAi(rollingB), runs, "leaf-rolling", map(size), deck(size)).run();
		
	}

	private static void fitnessRolling() {
		
		AI random = new RandomAI(RAND_METHOD.TREE);
		
		GameState state;
		try {
			state = new GameState(MapLoader.get("a"));
			state.init(DECK_SIZE.STANDARD);
			SingletonAction.init(state.map);
			CachedLines.load(state.map);
			
			boolean p1Turn = state.p1Turn;
			for(int i = 0; i < 20; i++){
				p1Turn = state.p1Turn;
				while(p1Turn == state.p1Turn && !state.isTerminal)
					state.update(random.act(state, -1));
			}
			RollingHorizonEvolution rollingA = new RollingHorizonEvolution(false, 100, .5, .75, 5000, new RolloutEvaluator(1, 1, randomSwitch, new HeuristicEvaluator(false)));
			RollingHorizonEvolution rollingB = new RollingHorizonEvolution(false, 100, .5, .75, 5000, new RolloutEvaluator(10, 1, randomSwitch, new HeuristicEvaluator(false)));
			RollingHorizonEvolution rollingC = new RollingHorizonEvolution(false, 100, .5, .75, 5000, new RolloutEvaluator(100, 1, randomSwitch, new HeuristicEvaluator(false)));
			RollingHorizonEvolution rollingD = new RollingHorizonEvolution(false, 100, .5, .75, 5000, new RolloutEvaluator(1000, 1, randomSwitch, new HeuristicEvaluator(false)));
			RollingHorizonEvolution rollingE = new RollingHorizonEvolution(false, 100, .5, .75, 5000, new RolloutEvaluator(10000, 1, randomSwitch, new HeuristicEvaluator(false)));
			
			rollingA.act(state.copy(), -1);
			rollingB.act(state.copy(), -1);
			rollingC.act(state.copy(), -1);
			rollingD.act(state.copy(), -1);
			rollingE.act(state.copy(), -1);
			
			System.out.println(new HeuristicEvaluator(false).eval(state, state.p1Turn));
			
			System.out.println("Rolling 1");
			int i = 0;
			List<Integer> keys = new ArrayList<Integer>();
			keys.addAll(rollingA.fitnesses.keySet());
			Collections.sort(keys);
			for(Integer t : keys){
				System.out.println(t + "\t" + rollingA.fitnesses.get(t));
				i++;
			}
			
			System.out.println("Rolling 10");
			i = 0;
			keys.clear();
			keys.addAll(rollingB.fitnesses.keySet());
			Collections.sort(keys);
			for(Integer t : keys){
				System.out.println(i + "\t" + rollingB.fitnesses.get(t));
				i++;
			}
			
			System.out.println("Rolling 100");
			i = 0;
			keys.clear();
			keys.addAll(rollingC.fitnesses.keySet());
			Collections.sort(keys);
			for(Integer t : keys){
				System.out.println(i + "\t" + rollingC.fitnesses.get(t));
				i++;
			}
			
			System.out.println("Rolling 1000");
			i = 0;
			keys.clear();
			keys.addAll(rollingD.fitnesses.keySet());
			Collections.sort(keys);
			for(Integer t : keys){
				System.out.println(i + "\t" + rollingD.fitnesses.get(t));
				i++;
			}
			
			System.out.println("Rolling 10000");
			i = 0;
			keys.clear();
			keys.addAll(rollingE.fitnesses.keySet());
			Collections.sort(keys);
			for(Integer t : keys){
				System.out.println(i + "\t" + rollingE.fitnesses.get(t));
				i++;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
			
	}
	
	private static void fitnessRollingMcts() {
		
		AI random = new RandomAI(RAND_METHOD.TREE);
		
		GameState state;
		try {
			state = new GameState(MapLoader.get("a"));
			state.init(DECK_SIZE.STANDARD);
			SingletonAction.init(state.map);
			CachedLines.load(state.map);
			
			boolean p1Turn = state.p1Turn;
			for(int i = 0; i < 20; i++){
				p1Turn = state.p1Turn;
				while(p1Turn == state.p1Turn && !state.isTerminal)
					state.update(random.act(state, -1));
			}
			Mcts mcts = new Mcts(5000, new RolloutEvaluator(1, 1, randomSwitch, new HeuristicEvaluator(false)));
			RollingHorizonEvolution rolling = new RollingHorizonEvolution(false, 100, .5, .75, 5000, new RolloutEvaluator(1, 1, randomSwitch, new HeuristicEvaluator(false)));
			
			GameState cloneA = state.copy();
			GameState cloneB = state.copy();
			
			mcts.act(cloneA, -1);
			rolling.act(cloneB, -1);
			
			System.out.println(new HeuristicEvaluator(false).eval(state, state.p1Turn));
			
			System.out.println("MCTS");
			int i = 0;
			List<Integer> keys = new ArrayList<Integer>();
			keys.addAll(mcts.fitnesses.keySet());
			Collections.sort(keys);
			for(Integer t : keys){
				System.out.println(t + "\t" + mcts.fitnesses.get(t));
				i++;
			}
			
			System.out.println("Rolling");
			i = 0;
			keys.clear();
			keys.addAll(rolling.fitnesses.keySet());
			Collections.sort(keys);
			for(Integer t : keys){
				System.out.println(i + "\t" + rolling.fitnesses.get(t));
				i++;
			}
			
			UI ui = new UI(cloneB, true, true, "", "", false);
			for(List<Action> actions : rolling.bestActions){
				ui.actionLayer.addAll(actions);
				ui.repaint();
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ui.actionLayer.clear();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
			
	}

	private static void HybridVsRolling(int runs, String size) {
		
		AI rolling = new IslandHorizonEvolution(false, 100, .5, .75, 10000, new RolloutEvaluator(1, 1, randomSwitch, new HeuristicEvaluator(false)));
		HybridAI hybrid = new HybridAI(new HeuristicEvaluator(false), 4000, 500,  
				new RolloutEvaluator(50, 1, randomSwitch, new HeuristicEvaluator(false), true), 10,
				new IslandHorizonEvolution(false, 32, .3, .66, 500, new HeuristicEvaluator(true)));
		if (size.equals("small"))
			GameState.TURN_LIMIT = 400;
		if (size.equals("standard"))
			GameState.TURN_LIMIT = 600;
		TestCase.GFX = true;
		new TestCase(new StatisticAi(rolling), new StatisticAi(hybrid), runs, "hybrid-vs-rolling", map(size), deck(size)).run();
		
	}
	
	private static void HybridVsGreedyTurn(int runs, String size) {
		
		AI p1 = new GreedyTurnAI(new HeuristicEvaluator(false), 15000);
		HybridAI hybrid = new HybridAI(new MeanEvaluator(), 4000, 500,  
				new RolloutEvaluator(500, 1, randomSwitch, new MeanEvaluator(), true), 10,
				new IslandHorizonEvolution(false, 32, .3, .66, 1000, new MeanEvaluator()));
		if (size.equals("small"))
			GameState.TURN_LIMIT = 400;
		if (size.equals("standard"))
			GameState.TURN_LIMIT = 600;
		TestCase.GFX = true;
		new TestCase(new StatisticAi(p1), new StatisticAi(hybrid), runs, "hybrid-vs-greedyturn", map(size), deck(size)).run();
		
	}

	private static void HybridVsGreedyAction(int runs, String size) {
		
		AI p1 = new GreedyActionAI(new HeuristicEvaluator(false));
		HybridAI hybrid = new HybridAI(new HeuristicEvaluator(false), 4000, 100, 
				new RolloutEvaluator(100, 1, randomSwitch, new HeuristicEvaluator(false), true), 10,
				new IslandHorizonEvolution(false, 32, .3, .66, 1000, new HeuristicEvaluator(true)));
		if (size.equals("small"))
			GameState.TURN_LIMIT = 400;
		if (size.equals("standard"))
			GameState.TURN_LIMIT = 600;
		TestCase.GFX = true;
		new TestCase(new StatisticAi(p1), new StatisticAi(hybrid), runs, "hybrid-vs-greedyturn", map(size), deck(size)).run();
		
	}

	private static void RollingVsGreedyActionAp(int runs, String size, int apFrom, int apTo) {

		AI p1 = new GreedyActionAI(new HeuristicEvaluator(false));
		AI rolling = new RollingHorizonEvolution(false, 100, .5, .75, 3075, new RolloutEvaluator(1, 1, randomSwitch, new HeuristicEvaluator(false)));
		
		for(int ap = apFrom; ap < apTo; ap++){

			GameState.STARTING_AP = Math.max(1, ap - 1);
			GameState.ACTION_POINTS = ap;
			GameState.TURN_LIMIT = 100;
			if (size.equals("small") || ap < 3)
				GameState.TURN_LIMIT = 400;
			if (size.equals("standard") || ap < 1)
				GameState.TURN_LIMIT = 600;
			//TestCase.GFX = true;
			new TestCase(new StatisticAi(p1), new StatisticAi(rolling), runs, "rolling-vs-greedyaction-ap-"+ap, map(size), deck(size)).run();
			
		}

	}

	private static void MctsVsGreedyActionAp(int runs, String size, int apFrom, int apTo) {
		
		AI p1 = new GreedyActionAI(new HeuristicEvaluator(false));
		Mcts mcts = new Mcts(3075, new RolloutEvaluator(1, 1, randomSwitch, new MaterialBalanceEvaluator(true)));
		mcts.c = mcts.c / 2;
		for(int ap = apFrom; ap < apTo; ap++){

			GameState.STARTING_AP = Math.max(1, ap - 1);
			GameState.ACTION_POINTS = ap;
			GameState.TURN_LIMIT = 100;
			if (size.equals("small") || ap < 3)
				GameState.TURN_LIMIT = 400;
			if (size.equals("standard") || ap < 1)
				GameState.TURN_LIMIT = 600;
			TestCase.GFX = true;
			new TestCase(new StatisticAi(p1), new StatisticAi(mcts), runs, "mcts-vs-greedyaction-ap-"+ap, map(size), deck(size)).run();
			
		}
	}

	private static void MctsVsGreedyTurnAP(int runs, String size, int ap) {
		AI p1 = new GreedyTurnAI(new HeuristicEvaluator(false));
		Mcts mcts = new Mcts(3075, new RolloutEvaluator(1, 1, randomSwitch, new HeuristicEvaluator(true)));
		//mcts.c = mcts.c / 2;
		GameState.STARTING_AP = Math.max(1, ap - 1);
		GameState.ACTION_POINTS = ap;
		GameState.TURN_LIMIT = 100;
		if (size.equals("small"))
			GameState.TURN_LIMIT = 400;
		if (size.equals("standard"))
			GameState.TURN_LIMIT = 600;
		TestCase.GFX = true;
		new TestCase(new StatisticAi(p1), new StatisticAi(mcts), runs, "mcts-vs-greedyturn-ap2", map(size), deck(size)).run();
	}

	private static void MctsVsGreedyTurn(int runs, String size) {
		AI p1 = new GreedyTurnAI(new HeuristicEvaluator(false));
		Mcts mcts = new Mcts(6075, new RolloutEvaluator(1, 1, randomSwitch, new MaterialBalanceEvaluator(true)));
		mcts.c = mcts.c / 2;
		new TestCase(new StatisticAi(p1), new StatisticAi(mcts), runs, "mcts-vs-greedyturn", map(size), deck(size)).run();
	}

	private static void MctsVsGreedyAction(int runs, String size) {
		AI p1 = new GreedyActionAI(new HeuristicEvaluator(false));
		Mcts mcts = new Mcts(6075, new RolloutEvaluator(1, 1, randomSwitch, new MaterialBalanceEvaluator(true)));
		mcts.c = mcts.c / 2;
		new TestCase(new StatisticAi(p1), new StatisticAi(mcts), runs, "mcts-vs-greedyaction", map(size), deck(size)).run();
	}

	private static void MctsVsGreedyActionLong(int runs, String size) {
		AI p1 = new GreedyActionAI(new HeuristicEvaluator(false));
		Mcts mcts = new Mcts(60000, new RolloutEvaluator(1, 1, randomSwitch, new MaterialBalanceEvaluator(true)));
		mcts.c = mcts.c / 2;
		Mcts.RECORD_DEPTHS = false;
		new TestCase(new StatisticAi(p1), new StatisticAi(mcts), runs, "mcts-vs-greedyaction-long", map(size), deck(size)).run();
	}
	
	private static void MctsRootTests(int runs, String size) {

		final RolloutEvaluator evaluator1 = new RolloutEvaluator(1, 1,
				randomSwitch, new MaterialBalanceEvaluator(true));

		final Mcts mcts1 = new Mcts(2075, evaluator1);
		final RootParallelizedMcts mcts2 = new RootParallelizedMcts(2075, evaluator1);

		//TestCase.GFX = true;
		
		new TestCase(new StatisticAi(mcts1), new StatisticAi(mcts2), runs,
				"mcts-vs-mcts-root", map(size), deck(size)).run();
	}

	private static void MctsLeafTests(int runs, String size) {

		final RolloutEvaluator evaluator1 = new RolloutEvaluator(1, 1,
				randomSwitch, new MaterialBalanceEvaluator(true));
		final RolloutEvaluator evaluator2 = new RolloutEvaluator(1, 10000,
				randomSwitch, new WinLoseEvaluator());

		final Mcts mcts1 = new Mcts(2075, evaluator1);
		final Mcts mcts2 = new Mcts(2075, new LeafParallelizer(evaluator1,
				LEAF_METHOD.AVERAGE));

		final Mcts mcts3 = new Mcts(2075, evaluator2);
		final Mcts mcts4 = new Mcts(2075, new LeafParallelizer(evaluator2,
				LEAF_METHOD.AVERAGE));

		new TestCase(new StatisticAi(mcts1), new StatisticAi(mcts2), runs,
				"mcts-vs-mcts-leaf", map(size), deck(size)).run();
		new TestCase(new StatisticAi(mcts3), new StatisticAi(mcts4), runs,
				"mcts-vs-mcts-leaf", map(size), deck(size)).run();
	}

	private static void RollingMutRate(int runs, String size) {

		final List<TestCase> tests = new ArrayList<TestCase>();

		final AI p2 = new RollingHorizonEvolution(false, 100, .66, .75, 2000,
				new RolloutEvaluator(1, 1, randomSwitch,
						new HeuristicEvaluator(false)));
		final AI p1 = new RollingHorizonEvolution(false, 100, .33, .75, 2000,
				new RolloutEvaluator(1, 1, randomSwitch,
						new HeuristicEvaluator(false)));
		// AI p3 = new RollingHorizonEvolution(100, .5, .75, 2000, new
		// RolloutEvaluator(1, 1, new RandomHeuristicAI(0.3), new
		// HeuristicEvaluator(false)));

		tests.add(new TestCase(new StatisticAi(p1), new StatisticAi(p2), runs,
				"rolling-mutrate", map(size), deck(size)));
		// tests.add(new TestCase(new StatisticAi(p1), new StatisticAi(p3),
		// runs, "rolling-mutrate", map(size), deck(size)));

		for (final TestCase test : tests)
			test.run();
	}

	private static void RollingKillRate(int runs, String size) {

		final List<TestCase> tests = new ArrayList<TestCase>();

		final AI p2 = new RollingHorizonEvolution(false, 100, .5, .66, 2000,
				new RolloutEvaluator(1, 1, randomSwitch,
						new HeuristicEvaluator(false)));
		final AI p1 = new RollingHorizonEvolution(false, 100, .5, .33, 2000,
				new RolloutEvaluator(1, 1, randomSwitch,
						new HeuristicEvaluator(false)));
		final AI p3 = new RollingHorizonEvolution(false, 100, .5, .85, 2000,
				new RolloutEvaluator(1, 1, randomSwitch,
						new HeuristicEvaluator(false)));

		// tests.add(new TestCase(new StatisticAi(p1), new StatisticAi(p2),
		// runs, "rolling-killrate", map(size), deck(size)));
		tests.add(new TestCase(new StatisticAi(p2), new StatisticAi(p3), runs,
				"rolling-killrate", map(size), deck(size)));

		for (final TestCase test : tests)
			test.run();
	}

	private static void RollingVsGreedyAction(int runs, String size) {
		final AI p2 = new GreedyActionAI(new HeuristicEvaluator(false));
		final AI p1 = new RollingHorizonEvolution(false, 100, .5, .75, 2000,
				new RolloutEvaluator(1, 1, randomSwitch,
						new HeuristicEvaluator(false)));
		new TestCase(p1, p2, runs, "rolling-vs-greedy-action", map(size),
				deck(size)).run();

	}

	private static void RollingVsGreedyActionTimes(int runs, String size) {
		final List<TestCase> tests = new ArrayList<TestCase>();

		final AI p2 = new GreedyActionAI(new HeuristicEvaluator(false));
		final AI rolling1 = new RollingHorizonEvolution(false, 100, .5, .75, 100,
				new RolloutEvaluator(1, 1, randomSwitch,
						new HeuristicEvaluator(false)));
		final AI rolling2 = new RollingHorizonEvolution(false, 100, .5, .75, 500,
				new RolloutEvaluator(1, 1, randomSwitch,
						new HeuristicEvaluator(false)));
		final AI rolling3 = new RollingHorizonEvolution(false, 100, .5, .75, 1000,
				new RolloutEvaluator(1, 1, randomSwitch,
						new HeuristicEvaluator(false)));
		final AI rolling4 = new RollingHorizonEvolution(false, 100, .5, .75, 4000,
				new RolloutEvaluator(1, 1, randomSwitch,
						new HeuristicEvaluator(false)));

		tests.add(new TestCase(rolling1, p2, runs, "rolling-vs-greedy-action",
				map(size), deck(size)));
		tests.add(new TestCase(rolling2, p2, runs, "rolling-vs-greedy-action",
				map(size), deck(size)));
		tests.add(new TestCase(rolling3, p2, runs, "rolling-vs-greedy-action",
				map(size), deck(size)));
		tests.add(new TestCase(rolling4, p2, runs, "rolling-vs-greedy-action",
				map(size), deck(size)));

		for (final TestCase test : tests)
			test.run();

	}

	private static void RollingVsGreedyTurn(int runs, String size) {
		final List<TestCase> tests = new ArrayList<TestCase>();

		final AI p2 = new GreedyTurnAI(new HeuristicEvaluator(false));
		final AI rolling4 = new RollingHorizonEvolution(false, 100, .5, .75, 6000,
				new RolloutEvaluator(1, 1, randomSwitch,
						new HeuristicEvaluator(false)));

		new TestCase(rolling4, p2, runs, "rolling-vs-greedy-action",
				map(size), deck(size)).run();

		for (final TestCase test : tests)
			test.run();

	}
	
	private static void RollingVsGreedyTurnTimes(int runs, String size) {
		final List<TestCase> tests = new ArrayList<TestCase>();

		final AI p2 = new GreedyTurnAI(new HeuristicEvaluator(false));
		final AI rolling1 = new RollingHorizonEvolution(false, 100, .5, .75, 100,
				new RolloutEvaluator(1, 1, randomSwitch,
						new HeuristicEvaluator(false)));
		final AI rolling2 = new RollingHorizonEvolution(false, 100, .5, .75, 500,
				new RolloutEvaluator(1, 1, randomSwitch,
						new HeuristicEvaluator(false)));
		final AI rolling3 = new RollingHorizonEvolution(false, 100, .5, .75, 1000,
				new RolloutEvaluator(1, 1, randomSwitch,
						new HeuristicEvaluator(false)));
		final AI rolling4 = new RollingHorizonEvolution(false, 100, .5, .75, 4000,
				new RolloutEvaluator(1, 1, randomSwitch,
						new HeuristicEvaluator(false)));

		tests.add(new TestCase(rolling1, p2, runs, "rolling-vs-greedy-action",
				map(size), deck(size)));
		tests.add(new TestCase(rolling2, p2, runs, "rolling-vs-greedy-action",
				map(size), deck(size)));
		tests.add(new TestCase(rolling3, p2, runs, "rolling-vs-greedy-action",
				map(size), deck(size)));
		tests.add(new TestCase(rolling4, p2, runs, "rolling-vs-greedy-action",
				map(size), deck(size)));

		for (final TestCase test : tests)
			test.run();

	}

	private static void GreedyActionVsGreedyTurn(int runs, String size) {
		final AI p1 = new GreedyActionAI(new HeuristicEvaluator(false));
		final AI p2 = new GreedyTurnAI(new HeuristicEvaluator(false));

		new TestCase(new StatisticAi(p1), new StatisticAi(p2), runs,
				"greedy-action-vs-greedy-turn", map(size), deck(size)).run();

	}

	private static void GreedyActionVsRandom(int runs, String size) {
		final AI p1 = new GreedyActionAI(new HeuristicEvaluator(false));
		final AI p2 = new RandomAI(RAND_METHOD.TREE);

		new TestCase(p1, p2, runs, "greedy-action-vs-random", map(size),
				deck(size)).run();
	}

	private static void MctsCutTests(int runs, String size) {
		final List<TestCase> tests = new ArrayList<TestCase>();

		final Mcts mcts1 = new Mcts(6075, new RolloutEvaluator(1, 1,
				randomSwitch, new MaterialBalanceEvaluator(true)));
		final Mcts mcts2 = new Mcts(6075, new RolloutEvaluator(1, 1,
				randomSwitch, new MaterialBalanceEvaluator(true)));
		mcts2.cut = true;

		tests.add(new TestCase(new StatisticAi(mcts1), new StatisticAi(mcts2),
				runs, "mcts-vs-cut", map(size), deck(size)));

		for (final TestCase test : tests)
			test.run();

	}

	private static void MctsCTests(int runs, String size) {
		final List<TestCase> tests = new ArrayList<TestCase>();

		final Mcts mcts1 = new Mcts(6075, new RolloutEvaluator(1, 1,
				randomSwitch, new MaterialBalanceEvaluator(true)));
		final Mcts mcts2 = new Mcts(6075, new RolloutEvaluator(1, 1,
				randomSwitch, new MaterialBalanceEvaluator(true)));
		mcts2.c = mcts2.c / 2;
		final Mcts mcts3 = new Mcts(6075, new RolloutEvaluator(1, 1,
				randomSwitch, new MaterialBalanceEvaluator(true)));
		mcts3.c = mcts3.c / 4;

		tests.add(new TestCase(new StatisticAi(mcts1), new StatisticAi(mcts2),
				runs, "mcts-c-vs-05c", map(size), deck(size)));
		tests.add(new TestCase(new StatisticAi(mcts2), new StatisticAi(mcts3),
				runs, "mcts-05c-vs-025c", map(size), deck(size)));

		for (final TestCase test : tests)
			test.run();

	}

	private static void MctsRolloutTests(int runs, String size) {
		final List<TestCase> tests = new ArrayList<TestCase>();

		final Mcts mcts1 = new Mcts(6075, new RolloutEvaluator(1, 10000,
				randomSwitch, new WinLoseEvaluator()));
		final Mcts mcts2 = new Mcts(6075, new RolloutEvaluator(1, 5,
				randomSwitch, new MaterialBalanceEvaluator(true)));
		final Mcts mcts3 = new Mcts(6075, new RolloutEvaluator(1, 1,
				randomSwitch, new MaterialBalanceEvaluator(true)));
		final Mcts mcts4 = new Mcts(6075, new RolloutEvaluator(1, 1,
				randomSwitch, new HeuristicEvaluator(true)));

		tests.add(new TestCase(new StatisticAi(mcts1), new StatisticAi(mcts2),
				runs, "mcts-nodepth-vs-5-depth", map(size), deck(size)));
		tests.add(new TestCase(new StatisticAi(mcts2), new StatisticAi(mcts3),
				runs, "mcts-5depth-vs-1depth", map(size), deck(size)));
		tests.add(new TestCase(new StatisticAi(mcts3), new StatisticAi(mcts4),
				runs, "mcts-1depth-heuristic-vs-1depth-material", small,
				DECK_SIZE.SMALL));

		for (final TestCase test : tests)
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

}
