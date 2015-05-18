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

public class TestSuiteFinal {

	private static HaMap tiny;
	private static HaMap small;
	private static HaMap standard;
	
	private static AI randomHeuristic = new RandomHeuristicAI(0.5);

	public static void main(String[] args) {

		try {
			tiny = MapLoader.get("a-tiny");
			small = MapLoader.get("a-small");
			standard = MapLoader.get("a");
		} catch (final IOException e) {
			e.printStackTrace();
		}
		
		if (args[0].equals("mcts-rollouts"))
			MctsRolloutDepthTests(Integer.parseInt(args[1]), args[2]);
		
		if (args[0].equals("mcts-c"))
			MctsCTests(Integer.parseInt(args[1]), args[2]);
		
		if (args[0].equals("mcts-random"))
			MctsRandom(Integer.parseInt(args[1]), args[2]);
		
		if (args[0].equals("mcts-ne-random"))
			MctsNeRandom(Integer.parseInt(args[1]), args[2]);
		
		if (args[0].equals("mcts-trans"))
			MctsTransTests(Integer.parseInt(args[1]), args[2]);
		
		if (args[0].equals("rolling"))
			RollingTests(Integer.parseInt(args[1]), args[2]);
		
		if (args[0].equals("rolling-para"))
			RollingParaTests(Integer.parseInt(args[1]), args[2]);
		
		if (args[0].equals("rolling-greedy"))
			RollingGreedy(Integer.parseInt(args[1]), args[2]);
		
		if (args[0].equals("mcts-greedy"))
			MctsGreedy(Integer.parseInt(args[1]), args[2]);
		
		if (args[0].equals("mcts-vs-rolling"))
			MctsVsRolling(Integer.parseInt(args[1]), args[2]);
		
		if (args[0].equals("last-time"))
			LastTime(Integer.parseInt(args[1]), args[2]);
		
		if (args[0].equals("greedyturn"))
			GreedyTurn(Integer.parseInt(args[1]), args[2]);
		
		if (args[0].equals("baselines"))
			BaseLines(Integer.parseInt(args[1]), args[2]);
		
		if (args[0].equals("ap"))
			AP(Integer.parseInt(args[1]), args[2]);
		
		
		
	}
	
	private static void AP(int runs, String size) {
		
		final List<TestCase> testsAP1 = new ArrayList<TestCase>();
		final List<TestCase> testsAP3 = new ArrayList<TestCase>();
		final List<TestCase> testsAP5 = new ArrayList<TestCase>();
		final List<TestCase> testsAP10 = new ArrayList<TestCase>();
		final List<TestCase> testsAP20 = new ArrayList<TestCase>();
		final List<TestCase> testsAP40 = new ArrayList<TestCase>();
		
		int budget = 1000;
		final AI greedyaction = new GreedyActionAI(new HeuristicEvaluator(true));

		final Mcts mcts = new Mcts(budget, new RolloutEvaluator(1, 1, new RandomHeuristicAI(1), new HeuristicEvaluator(true)));
		final Mcts mctsc0 = new Mcts(budget, new RolloutEvaluator(1, 1, new RandomHeuristicAI(1), new HeuristicEvaluator(true)));
		mctsc0.c = 0;
		final Mcts mctscut = new Mcts(budget, new RolloutEvaluator(1, 1, new RandomHeuristicAI(.5), new HeuristicEvaluator(true)));
		mctscut.cut = true;
		final IslandHorizonEvolution rollingisland = new IslandHorizonEvolution(true, 100, .1, .5, budget, 
				new RolloutEvaluator(1, 1, new RandomHeuristicAI(0.5), new HeuristicEvaluator(false)));
		
		testsAP1.add(new TestCase(new StatisticAi(greedyaction), new StatisticAi(mcts),
			runs, "greedyaction-vs-mcts-AP1", map(size), deck(size)));
		testsAP1.add(new TestCase(new StatisticAi(greedyaction), new StatisticAi(mctsc0),
				runs, "greedyaction-vs-mctsc0-AP1", map(size), deck(size)));
		testsAP1.add(new TestCase(new StatisticAi(greedyaction), new StatisticAi(mctscut),
				runs, "greedyaction-vs-mctscut-AP1", map(size), deck(size)));
		testsAP1.add(new TestCase(new StatisticAi(greedyaction), new StatisticAi(rollingisland),
				runs, "greedyaction-vs-rollingisland-AP1", map(size), deck(size)));
		
		testsAP3.add(new TestCase(new StatisticAi(greedyaction), new StatisticAi(mcts),
				runs, "greedyaction-vs-mcts-AP3", map(size), deck(size)));
		testsAP3.add(new TestCase(new StatisticAi(greedyaction), new StatisticAi(mctsc0),
				runs, "greedyaction-vs-mctsc0-AP3", map(size), deck(size)));
		testsAP3.add(new TestCase(new StatisticAi(greedyaction), new StatisticAi(mctscut),
				runs, "greedyaction-vs-mctscut-AP3", map(size), deck(size)));
		testsAP3.add(new TestCase(new StatisticAi(greedyaction), new StatisticAi(rollingisland),
				runs, "greedyaction-vs-rollingisland-AP3", map(size), deck(size)));
		
		testsAP5.add(new TestCase(new StatisticAi(greedyaction), new StatisticAi(mcts),
				runs, "greedyaction-vs-mcts-AP5", map(size), deck(size)));
		testsAP5.add(new TestCase(new StatisticAi(greedyaction), new StatisticAi(mctsc0),
				runs, "greedyaction-vs-mctsc0-AP5", map(size), deck(size)));
		testsAP5.add(new TestCase(new StatisticAi(greedyaction), new StatisticAi(mctscut),
				runs, "greedyaction-vs-mctscut-AP5", map(size), deck(size)));
		testsAP5.add(new TestCase(new StatisticAi(greedyaction), new StatisticAi(rollingisland),
				runs, "greedyaction-vs-rollingisland-AP5", map(size), deck(size)));	
		
		testsAP10.add(new TestCase(new StatisticAi(greedyaction), new StatisticAi(mcts),
				runs, "greedyaction-vs-mcts-AP10", map(size), deck(size)));
		testsAP10.add(new TestCase(new StatisticAi(greedyaction), new StatisticAi(mctsc0),
				runs, "greedyaction-vs-mctsc0-AP10", map(size), deck(size)));
		testsAP10.add(new TestCase(new StatisticAi(greedyaction), new StatisticAi(mctscut),
				runs, "greedyaction-vs-mctscut-AP10", map(size), deck(size)));
		testsAP10.add(new TestCase(new StatisticAi(greedyaction), new StatisticAi(rollingisland),
				runs, "greedyaction-vs-rollingisland-AP10", map(size), deck(size)));
		
		testsAP20.add(new TestCase(new StatisticAi(greedyaction), new StatisticAi(mcts),
				runs, "greedyaction-vs-mcts-AP20", map(size), deck(size)));
		testsAP20.add(new TestCase(new StatisticAi(greedyaction), new StatisticAi(mctsc0),
				runs, "greedyaction-vs-mctsc0-AP20", map(size), deck(size)));
		testsAP20.add(new TestCase(new StatisticAi(greedyaction), new StatisticAi(mctscut),
				runs, "greedyaction-vs-mctscut-AP20", map(size), deck(size)));
		testsAP20.add(new TestCase(new StatisticAi(greedyaction), new StatisticAi(rollingisland),
				runs, "greedyaction-vs-rollingisland-AP20", map(size), deck(size)));
		
		testsAP40.add(new TestCase(new StatisticAi(greedyaction), new StatisticAi(mcts),
				runs, "greedyaction-vs-mcts-AP40", map(size), deck(size)));
		testsAP40.add(new TestCase(new StatisticAi(greedyaction), new StatisticAi(mctsc0),
				runs, "greedyaction-vs-mctsc0-AP40", map(size), deck(size)));
		testsAP40.add(new TestCase(new StatisticAi(greedyaction), new StatisticAi(mctscut),
				runs, "greedyaction-vs-mctscut-AP40", map(size), deck(size)));
		testsAP40.add(new TestCase(new StatisticAi(greedyaction), new StatisticAi(rollingisland),
				runs, "greedyaction-vs-rollingisland-AP40", map(size), deck(size)));
		
		for (final TestCase test : testsAP1){
			test.ap = 1;
			test.run();
		}
		
		for (final TestCase test : testsAP3){
			test.ap = 3;
			test.run();
		}
		
		for (final TestCase test : testsAP5){
			test.ap = 5;
			test.run();
		}
		
		for (final TestCase test : testsAP10){
			test.ap = 10;
			test.run();
		}
		
		for (final TestCase test : testsAP20){
			test.ap = 20;
			test.run();
		}
		
		for (final TestCase test : testsAP40){
			test.ap = 40;
			test.run();
		}
		
	}
	
	private static void BaseLines(int runs, String size) {
		
		final List<TestCase> tests = new ArrayList<TestCase>();
		
		int budget = 6000;
		
		final AI greedyturn = new GreedyTurnAI(new HeuristicEvaluator(true), budget);
		final AI greedyaction = new GreedyActionAI(new HeuristicEvaluator(true));
		
		final AI random = new RandomAI(RAND_METHOD.TREE);
		
		tests.add(new TestCase(new StatisticAi(greedyturn), new StatisticAi(greedyaction),
				runs, "greedyturn-vs-greedyaction", map(size), deck(size)));
		
		tests.add(new TestCase(new StatisticAi(greedyturn), new StatisticAi(random),
				runs, "greedyturn-vs-random", map(size), deck(size)));
		
		tests.add(new TestCase(new StatisticAi(greedyaction), new StatisticAi(random),
				runs, "greedyaction-vs-random", map(size), deck(size)));
		
		for (final TestCase test : tests)
			test.run();
		
	}

	private static void GreedyTurn(int runs, String size) {
		
		final List<TestCase> tests = new ArrayList<TestCase>();
		
		int budget = 6000;
		
		final Mcts mcts = new Mcts(budget, new RolloutEvaluator(1, 1, new RandomHeuristicAI(1), new HeuristicEvaluator(true)));
		mcts.c = 0;
		
		final IslandHorizonEvolution rollingisland = new IslandHorizonEvolution(true, 100, .1, .5, budget, 
				new RolloutEvaluator(1, 1, new RandomHeuristicAI(0.5), new HeuristicEvaluator(false)));
		
		final AI greedtyurn = new GreedyTurnAI(new HeuristicEvaluator(true), budget);
		
		tests.add(new TestCase(new StatisticAi(mcts), new StatisticAi(greedtyurn),
				runs, "mcts-vs-greedtyurn", map(size), deck(size)));
		
		tests.add(new TestCase(new StatisticAi(rollingisland), new StatisticAi(greedtyurn),
				runs, "rollingisland-vs-greedtyurn", map(size), deck(size)));
		
		for (final TestCase test : tests)
			test.run();
	}



	private static void LastTime(int runs, String size) {
		
		final List<TestCase> tests = new ArrayList<TestCase>();
		
		int budget = 3000;
		
		final Mcts mcts_23_4375 = new Mcts(budget/32, new RolloutEvaluator(1, 1, new RandomHeuristicAI(1), new HeuristicEvaluator(true)));
		mcts_23_4375.c = 0;
		
		final Mcts mcts_11_71875 = new Mcts(budget/64, new RolloutEvaluator(1, 1, new RandomHeuristicAI(1), new HeuristicEvaluator(true)));
		mcts_11_71875.c = 0;
		
		final IslandHorizonEvolution rollingisland_46_875 = new IslandHorizonEvolution(true, 100, .1, .5, budget/64, 
				new RolloutEvaluator(1, 1, new RandomHeuristicAI(0.5), new HeuristicEvaluator(false)));
		
		final IslandHorizonEvolution rollingisland_23_4375 = new IslandHorizonEvolution(true, 100, .1, .5, budget/128, 
				new RolloutEvaluator(1, 1, new RandomHeuristicAI(0.5), new HeuristicEvaluator(false)));
		
		final IslandHorizonEvolution rollingisland_11_71875 = new IslandHorizonEvolution(true, 100, .1, .5, budget/256, 
				new RolloutEvaluator(1, 1, new RandomHeuristicAI(0.5), new HeuristicEvaluator(false)));
		
		final AI greedtyurn = new GreedyTurnAI(new HeuristicEvaluator(true), 3000);
		
		tests.add(new TestCase(new StatisticAi(mcts_23_4375), new StatisticAi(greedtyurn),
				runs, "mcts_23_4375-vs-greedtyurn", map(size), deck(size)));
		
		tests.add(new TestCase(new StatisticAi(mcts_11_71875), new StatisticAi(greedtyurn),
				runs, "mcts_11_71875-vs-greedtyurn", map(size), deck(size)));
		
		tests.add(new TestCase(new StatisticAi(rollingisland_46_875), new StatisticAi(greedtyurn),
				runs, "rollingisland_46_875-vs-greedtyurn", map(size), deck(size)));
		
		tests.add(new TestCase(new StatisticAi(rollingisland_23_4375), new StatisticAi(greedtyurn),
				runs, "rollingisland_23_4375-vs-greedtyurn", map(size), deck(size)));
		
		tests.add(new TestCase(new StatisticAi(rollingisland_11_71875), new StatisticAi(greedtyurn),
				runs, "rollingisland_11_71875-vs-greedtyurn", map(size), deck(size)));
		
		for (final TestCase test : tests)
			test.run();
		
	}
	
	private static void MctsNeRandom(int runs, String size) {
		
		final List<TestCase> tests = new ArrayList<TestCase>();
		
		int budget = 6000;
		
		final Mcts mcts0 = new Mcts(budget, new RolloutEvaluator(1, 1,new RandomHeuristicAI(0), new HeuristicEvaluator(true)));
		mcts0.c = 0;
		final Mcts mcts1 = new Mcts(budget, new RolloutEvaluator(1, 1,new RandomHeuristicAI(1), new HeuristicEvaluator(true)));
		mcts1.c = 0;
		
		tests.add(new TestCase(new StatisticAi(mcts0), new StatisticAi(mcts1),
				runs, "ne-mcts-r0-vs-ne-mcts-r1", map(size), deck(size)));
		
		TestCase.GFX = true;
		
		for (final TestCase test : tests)
			test.run();
		
	}
	
	private static void MctsRandom(int runs, String size) {
		
		final List<TestCase> tests = new ArrayList<TestCase>();
		
		int budget = 6000;
		
		final Mcts mcts0 = new Mcts(budget, new RolloutEvaluator(1, 1,new RandomHeuristicAI(0), new HeuristicEvaluator(true)));
		final Mcts mcts1 = new Mcts(budget, new RolloutEvaluator(1, 1,new RandomHeuristicAI(1), new HeuristicEvaluator(true)));
		
		tests.add(new TestCase(new StatisticAi(mcts0), new StatisticAi(mcts1),
				runs, "mcts-r0-vs-mcts-r1", map(size), deck(size)));
		
		TestCase.GFX = true;
		
		for (final TestCase test : tests)
			test.run();
		
	}

	private static void MctsVsRolling(int runs, String size) {
		
		final List<TestCase> tests = new ArrayList<TestCase>();
		
		int budget = 6000;
		
		final Mcts mcts = new Mcts(budget, new RolloutEvaluator(1, 1,new RandomHeuristicAI(1), new HeuristicEvaluator(true)));
		mcts.c = 0;
		
		final IslandHorizonEvolution rollingisland = new IslandHorizonEvolution(true, 100, .1, .5, budget, 
				new RolloutEvaluator(1, 1, new RandomHeuristicAI(0.5), new HeuristicEvaluator(false)));
		
		tests.add(new TestCase(new StatisticAi(mcts), new StatisticAi(rollingisland),
				runs, "mcts-vs-rollingisland", map(size), deck(size)));
		
		TestCase.GFX = true;
		
		for (final TestCase test : tests)
			test.run();
		
	}

	private static void MctsGreedy(int runs, String size) {
		
		final List<TestCase> tests = new ArrayList<TestCase>();
		
		int budget = 3000;
		
		final Mcts mcts14c0_3000 = new Mcts(budget, new RolloutEvaluator(1, 1,new RandomHeuristicAI(1), new HeuristicEvaluator(true)));
		mcts14c0_3000.c = 0;
		
		final Mcts mcts14c0_1500 = new Mcts(budget/2, new RolloutEvaluator(1, 1,new RandomHeuristicAI(1), new HeuristicEvaluator(true)));
		mcts14c0_1500.c = 0;
		
		final Mcts mcts14c0_750 = new Mcts(budget/4, new RolloutEvaluator(1, 1,new RandomHeuristicAI(1), new HeuristicEvaluator(true)));
		mcts14c0_750.c = 0;
		
		final Mcts mcts14c0_375 = new Mcts(budget/8, new RolloutEvaluator(1, 1,new RandomHeuristicAI(1), new HeuristicEvaluator(true)));
		mcts14c0_375.c = 0;
		
		final Mcts mcts14c0_187_5 = new Mcts(budget/16, new RolloutEvaluator(1, 1,new RandomHeuristicAI(1), new HeuristicEvaluator(true)));
		mcts14c0_187_5.c = 0;
		
		final AI greedtyurn = new GreedyTurnAI(new HeuristicEvaluator(true), 3000);
		
		tests.add(new TestCase(new StatisticAi(mcts14c0_3000), new StatisticAi(greedtyurn),
				runs, "mcts14c0_3000-vs-greedtyurn_3000", map(size), deck(size)));
		
		tests.add(new TestCase(new StatisticAi(mcts14c0_1500), new StatisticAi(greedtyurn),
				runs, "mcts14c0_1500-vs-greedtyurn_3000", map(size), deck(size)));
		
		tests.add(new TestCase(new StatisticAi(mcts14c0_750), new StatisticAi(greedtyurn),
				runs, "mcts14c0_750-vs-greedtyurn_3000", map(size), deck(size)));
		
		tests.add(new TestCase(new StatisticAi(mcts14c0_375), new StatisticAi(greedtyurn),
				runs, "mcts14c0_375-vs-greedtyurn_3000", map(size), deck(size)));
		
		tests.add(new TestCase(new StatisticAi(mcts14c0_187_5), new StatisticAi(greedtyurn),
				runs, "mcts14c0_87_5-vs-greedtyurn_3000", map(size), deck(size)));
		
		
		for (final TestCase test : tests)
			test.run();
		
	}

	private static void RollingGreedy(int runs, String size) {
		
		final List<TestCase> tests = new ArrayList<TestCase>();
		
		int budget = 6000;
		
		final IslandHorizonEvolution rollingisland_6000 = new IslandHorizonEvolution(true, 100, .1, .5, budget, 
				new RolloutEvaluator(1, 1, new RandomHeuristicAI(0.5), new HeuristicEvaluator(false)));
		
		final IslandHorizonEvolution rollingisland_1500 = new IslandHorizonEvolution(true, 100, .1, .5, budget/2, 
				new RolloutEvaluator(1, 1, new RandomHeuristicAI(0.5), new HeuristicEvaluator(false)));
		
		final IslandHorizonEvolution rollingisland_750 = new IslandHorizonEvolution(true, 100, .1, .5, budget/4, 
				new RolloutEvaluator(1, 1, new RandomHeuristicAI(0.5), new HeuristicEvaluator(false)));
		
		final IslandHorizonEvolution rollingisland_375 = new IslandHorizonEvolution(true, 100, .1, .5, budget/8, 
				new RolloutEvaluator(1, 1, new RandomHeuristicAI(0.5), new HeuristicEvaluator(false)));
		
		final IslandHorizonEvolution rollingisland_187_5 = new IslandHorizonEvolution(true, 100, .1, .5, budget/16, 
				new RolloutEvaluator(1, 1, new RandomHeuristicAI(0.5), new HeuristicEvaluator(false)));
		
		final AI greedtyurn = new GreedyTurnAI(new HeuristicEvaluator(true), 3000);
		final AI greedyaction = new GreedyActionAI(new HeuristicEvaluator(true));
		
		tests.add(new TestCase(new StatisticAi(rollingisland_6000), new StatisticAi(greedyaction),
				runs, "rollingisland-vs-greedyaction", map(size), deck(size)));
		/*
		tests.add(new TestCase(new StatisticAi(rollingisland_1500), new StatisticAi(greedtyurn),
				runs, "rollingisland_1500-vs-greedtyurn_3000", map(size), deck(size)));
		
		tests.add(new TestCase(new StatisticAi(rollingisland_750), new StatisticAi(greedtyurn),
				runs, "rollingisland_750-vs-greedtyurn_3000", map(size), deck(size)));
		
		tests.add(new TestCase(new StatisticAi(rollingisland_375), new StatisticAi(greedtyurn),
				runs, "rollingisland_375-vs-greedtyurn_3000", map(size), deck(size)));
		
		tests.add(new TestCase(new StatisticAi(rollingisland_187_5), new StatisticAi(greedtyurn),
				runs, "rollingisland_187_5-vs-greedtyurn_3000", map(size), deck(size)));
		*/
		for (final TestCase test : tests)
			test.run();
		
	}

	private static void RollingTests(int runs, String size) {
		
		final List<TestCase> tests = new ArrayList<TestCase>();
		
		int budget = 1000;
		
		final RollingHorizonEvolution rolling0 = new RollingHorizonEvolution(true, 100, .1, .5, budget, 
				new RolloutEvaluator(1, 1, new RandomHeuristicAI(0), new HeuristicEvaluator(false)));
		
		final RollingHorizonEvolution rolling05 = new RollingHorizonEvolution(true, 100, .1, .5, budget, 
				new RolloutEvaluator(1, 1, new RandomHeuristicAI(0.5), new HeuristicEvaluator(false)));
		
		final RollingHorizonEvolution rolling1 = new RollingHorizonEvolution(true, 100, .1, .5, budget, 
				new RolloutEvaluator(1, 1, new RandomHeuristicAI(1), new HeuristicEvaluator(false)));
		
		// ---
		
		final RollingHorizonEvolution rolling05r1 = new RollingHorizonEvolution(true, 100, .1, .5, budget, 
				new RolloutEvaluator(1, 1, new RandomHeuristicAI(0.5), new HeuristicEvaluator(false)));
		
		final RollingHorizonEvolution rolling05r2 = new RollingHorizonEvolution(true, 100, .1, .5, budget, 
				new RolloutEvaluator(2, 1, new RandomHeuristicAI(0.5), new HeuristicEvaluator(false)));
		
		final RollingHorizonEvolution rolling05r5 = new RollingHorizonEvolution(true, 100, .1, .5, budget, 
				new RolloutEvaluator(5, 1, new RandomHeuristicAI(0.5), new HeuristicEvaluator(false)));
		
		final RollingHorizonEvolution rolling05r10 = new RollingHorizonEvolution(true, 100, .1, .5, budget, 
				new RolloutEvaluator(10, 1, new RandomHeuristicAI(0.5), new HeuristicEvaluator(false)));
		
		final RollingHorizonEvolution rolling05r50 = new RollingHorizonEvolution(true, 100, .1, .5, budget, 
				new RolloutEvaluator(50, 1, new RandomHeuristicAI(0.5), new HeuristicEvaluator(false)));
		
		// --
		
		final RollingHorizonEvolution rolling05r1noHistory = new RollingHorizonEvolution(true, 100, .1, .5, budget, 
				new RolloutEvaluator(1, 1, new RandomHeuristicAI(0.5), new HeuristicEvaluator(false)));
		rolling05r1noHistory.useHistory = false;
		
		final RollingHorizonEvolution rollingheuristic = new RollingHorizonEvolution(true, 100, .1, .5, budget, 
				new HeuristicEvaluator(false));
		
		final AI greedyaction = new GreedyActionAI(new HeuristicEvaluator(true));
		
		final AI greedyturn = new GreedyTurnAI(new HeuristicEvaluator(true), budget);
		
		tests.add(new TestCase(new StatisticAi(rolling05r1), new StatisticAi(rolling05r1noHistory),
				runs, "rolling05r1-vs-rolling05r1nohis", map(size), deck(size)));
		
		tests.add(new TestCase(new StatisticAi(rolling05r1), new StatisticAi(rollingheuristic),
				runs, "rolling05r1-vs-rollingheuristic", map(size), deck(size)));
		
		
		for (final TestCase test : tests)
			test.run();
		
	}

	private static void RollingParaTests(int runs, String size) {
		
		final List<TestCase> tests = new ArrayList<TestCase>();
		
		int budget = 1000;
		
		final RollingHorizonEvolution rolling05r1_1000 = new RollingHorizonEvolution(true, 100, .1, .5, budget, 
				new RolloutEvaluator(1, 1, new RandomHeuristicAI(0.5), new HeuristicEvaluator(false)));
		
		final IslandHorizonEvolution rollingisland05r1_1000 = new IslandHorizonEvolution(true, 100, .1, .5, budget, 
				new RolloutEvaluator(1, 1, new RandomHeuristicAI(0.5), new HeuristicEvaluator(false)));
		
		final RollingHorizonEvolution rolling05r1_2000 = new RollingHorizonEvolution(true, 100, .1, .5, budget*2, 
				new RolloutEvaluator(1, 1, new RandomHeuristicAI(0.5), new HeuristicEvaluator(false)));
		
		final IslandHorizonEvolution rollingisland05r1_2000 = new IslandHorizonEvolution(true, 100, .1, .5, budget*2, 
				new RolloutEvaluator(1, 1, new RandomHeuristicAI(0.5), new HeuristicEvaluator(false)));
		
		tests.add(new TestCase(new StatisticAi(rolling05r1_1000), new StatisticAi(rollingisland05r1_1000),
				runs, "rolling05r1_1000-vs-rollingisland05r1_1000", map(size), deck(size)));
		
		tests.add(new TestCase(new StatisticAi(rolling05r1_2000), new StatisticAi(rollingisland05r1_2000),
				runs, "rolling05r1_2000-vs-rollingisland05r1_2000", map(size), deck(size)));
		
		for (final TestCase test : tests)
			test.run();
		
	}


	private static void MctsCTests(int runs, String size) {
		final List<TestCase> tests = new ArrayList<TestCase>();
		final Mcts mcts0 = new Mcts(6000, new RolloutEvaluator(1, 1,randomHeuristic, new HeuristicEvaluator(true)));
		
		final Mcts mcts1 = new Mcts(6000, new RolloutEvaluator(1, 1,randomHeuristic, new HeuristicEvaluator(true)));
		mcts1.c = mcts1.c / 2;
		final Mcts mcts2 = new Mcts(6000, new RolloutEvaluator(1, 1,randomHeuristic, new HeuristicEvaluator(true)));
		mcts2.c = mcts2.c / 4;
		final Mcts mcts3 = new Mcts(6000, new RolloutEvaluator(1, 1,randomHeuristic, new HeuristicEvaluator(true)));
		mcts3.c = mcts3.c / 8;
		
		final Mcts mcts4 = new Mcts(6000, new RolloutEvaluator(1, 1,randomHeuristic, new HeuristicEvaluator(true)));
		mcts4.c = mcts4.c / 16;
		
		final Mcts mcts5 = new Mcts(6000, new RolloutEvaluator(1, 1,randomHeuristic, new HeuristicEvaluator(true)));
		mcts5.c = mcts5.c / 24;
		
		final Mcts mcts6 = new Mcts(6000, new RolloutEvaluator(1, 1,randomHeuristic, new HeuristicEvaluator(true)));
		mcts6.c = mcts6.c / 32;
		
		final Mcts mcts7 = new Mcts(6000, new RolloutEvaluator(1, 1,randomHeuristic, new HeuristicEvaluator(true)));
		mcts7.c = mcts7.c / 48;
		
		final Mcts mcts8 = new Mcts(6000, new RolloutEvaluator(1, 1,randomHeuristic, new HeuristicEvaluator(true)));
		mcts8.c = mcts8.c / 64;
		
		final Mcts mcts9 = new Mcts(6000, new RolloutEvaluator(1, 1,randomHeuristic, new HeuristicEvaluator(true)));
		mcts9.c = mcts9.c / 92;
		
		final Mcts mcts10 = new Mcts(6000, new RolloutEvaluator(1, 1,randomHeuristic, new HeuristicEvaluator(true)));
		mcts10.c = mcts10.c / 192;
		
		final Mcts mcts11 = new Mcts(6000, new RolloutEvaluator(1, 1,randomHeuristic, new HeuristicEvaluator(true)));
		mcts11.c = 0;
		
		final Mcts mcts11nonrandom = new Mcts(6000, new RolloutEvaluator(1, 1,new RandomHeuristicAI(1), new HeuristicEvaluator(true)));
		mcts11nonrandom.c = 0;
		
		final Mcts mcts11random = new Mcts(6000, new RolloutEvaluator(1, 1,new RandomHeuristicAI(0), new HeuristicEvaluator(true)));
		mcts11random.c = 0;
		
		final Mcts mcts12cut = new Mcts(6000, new RolloutEvaluator(1, 1,new RandomHeuristicAI(1), new HeuristicEvaluator(true)));
		//mcts12cut.c = 0;
		mcts12cut.cut = true;
		
		final Mcts mcts12cut05parallel = new Mcts(6000, new LeafParallelizer(new RolloutEvaluator(1, 1,new RandomHeuristicAI(.5), new HeuristicEvaluator(true)), LEAF_METHOD.AVERAGE));
		//mcts12cut.c = 0;
		mcts12cut05parallel.cut = true;
		
		final Mcts mcts1305 = new Mcts(6000, new RolloutEvaluator(1, 1,new RandomHeuristicAI(.5), new HeuristicEvaluator(true)));
		mcts1305.c = 0;
		//mcts1305.cut = true;
		
		final Mcts mcts1305parallel = new Mcts(6000, new LeafParallelizer(new RolloutEvaluator(1, 1,new RandomHeuristicAI(.5), new HeuristicEvaluator(true)), LEAF_METHOD.AVERAGE));
		mcts1305parallel.c = 0;
		//mcts1305parallel.cut = true;
		
		final Mcts mcts12collapse = new Mcts(6000, new RolloutEvaluator(1, 1,new RandomHeuristicAI(1), new HeuristicEvaluator(true)));
		//mcts12collapse.c = 0;
		mcts12collapse.collapse = true;
		
		final AI greedyaction = new GreedyActionAI(new HeuristicEvaluator(true));
		
		final Mcts mcts14c0 = new Mcts(6000, new RolloutEvaluator(1, 1,new RandomHeuristicAI(1), new HeuristicEvaluator(true)));
		mcts14c0.c = 0;

		final Mcts mcts14cut05 = new Mcts(6000, new RolloutEvaluator(1, 1,new RandomHeuristicAI(.5), new HeuristicEvaluator(true)));
		//mcts12cut.c = 0;
		mcts14cut05.cut = true;
		
		final RootParallelizedMcts mcts14cut05rootparallel = new RootParallelizedMcts(6000, new RolloutEvaluator(1, 1,new RandomHeuristicAI(.5), new HeuristicEvaluator(true)));
		//mcts12cut.c = 0;
		mcts14cut05rootparallel.cut = true;
		
		tests.add(new TestCase(new StatisticAi(mcts14cut05), new StatisticAi(mcts14cut05rootparallel),
				runs, "mcts-cut05-vs-mcts-cut05-rootparallel", map(size), deck(size)));
		/*
		tests.add(new TestCase(new StatisticAi(mcts1305), new StatisticAi(mcts1305parallel),
				runs, "mcts-c0-05-vs-mcts-c0-05-parallel", map(size), deck(size)));
		*/
		for (final TestCase test : tests)
			test.run();

	}
	
	private static void MctsTransTests(int runs, String size) {
		final List<TestCase> tests = new ArrayList<TestCase>();
		
		int budget = 2000;
		
		final Mcts mcts14c0 = new Mcts(budget, new RolloutEvaluator(1, 1,new RandomHeuristicAI(1), new HeuristicEvaluator(true)));
		mcts14c0.c = 0;
		
		final Mcts mcts14c0notrans = new Mcts(budget, new RolloutEvaluator(1, 1,new RandomHeuristicAI(1), new HeuristicEvaluator(true)));
		mcts14c0notrans.c = 0;
		mcts14c0notrans.useTrans = false;
		
		final Mcts mcts14cut05 = new Mcts(budget, new RolloutEvaluator(1, 1,new RandomHeuristicAI(.5), new HeuristicEvaluator(true)));
		mcts14cut05.cut = true;
		
		final Mcts mcts14cut05notrans = new Mcts(budget, new RolloutEvaluator(1, 1,new RandomHeuristicAI(.5), new HeuristicEvaluator(true)));
		mcts14cut05notrans.cut = true;
		mcts14cut05notrans.useTrans = false;
		
		tests.add(new TestCase(new StatisticAi(mcts14c0), new StatisticAi(mcts14c0notrans),
				runs, "mcts14c0-vs-mcts14c0notrans", map(size), deck(size)));
		
		tests.add(new TestCase(new StatisticAi(mcts14cut05), new StatisticAi(mcts14cut05notrans),
				runs, "mcts14cut05-vs-mcts14cut05notrans", map(size), deck(size)));
		
		for (final TestCase test : tests)
			test.run();

	}
	
	private static void MctsRolloutDepthTests(int runs, String size) {
		final List<TestCase> tests = new ArrayList<TestCase>();
		
		final Mcts mcts10000 = new Mcts(6000, new RolloutEvaluator(1, 10000,
				randomHeuristic, new HeuristicEvaluator(true)));
		final Mcts mcts10 = new Mcts(6000, new RolloutEvaluator(1, 10,
				randomHeuristic, new HeuristicEvaluator(true)));
		final Mcts mcts5 = new Mcts(6000, new RolloutEvaluator(1, 5,
				randomHeuristic, new HeuristicEvaluator(true)));
		
		final Mcts mcts1 = new Mcts(6000, new RolloutEvaluator(1, 1,randomHeuristic, new HeuristicEvaluator(true)));
		final AI greedyaction = new GreedyActionAI(new HeuristicEvaluator(true));
		  
		tests.add(new TestCase(new StatisticAi(mcts10000), new StatisticAi(greedyaction),
				runs, "mcts-nodepth-vs-greedyaction-2", map(size), deck(size)));
		tests.add(new TestCase(new StatisticAi(mcts10), new StatisticAi(greedyaction),
				runs, "mcts-10depth-vs-greedyaction-2", map(size), deck(size)));
		tests.add(new TestCase(new StatisticAi(mcts5), new StatisticAi(greedyaction),
				runs, "mcts-5depth-vs-greedyaction-2", map(size), deck(size)));
		
		//tests.add(new TestCase(new StatisticAi(mcts1), new StatisticAi(greedyaction),
		//		runs, "mcts-1depth-vs-greedyaction", map(size), deck(size)));
		
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
