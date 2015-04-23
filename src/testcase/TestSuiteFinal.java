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
		
		if (args[0].equals("rolling"))
			RollingTests(Integer.parseInt(args[1]), args[2]);
		
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
		
		// --
		
		final AI greedyaction = new GreedyActionAI(new HeuristicEvaluator(true));
		
		final AI greedyturn = new GreedyTurnAI(new HeuristicEvaluator(true), budget);
		
		tests.add(new TestCase(new StatisticAi(rolling0), new StatisticAi(greedyturn),
				runs, "rolling0r1-vs-greedyturn", map(size), deck(size)));
		
		tests.add(new TestCase(new StatisticAi(rolling05), new StatisticAi(greedyturn),
				runs, "rolling05r1-vs-greedyturn", map(size), deck(size)));
		
		tests.add(new TestCase(new StatisticAi(rolling1), new StatisticAi(greedyturn),
				runs, "rolling1r1-vs-greedyturn", map(size), deck(size)));
		
		tests.add(new TestCase(new StatisticAi(rolling05r2), new StatisticAi(greedyturn),
				runs, "rolling05r2-vs-greedyturn", map(size), deck(size)));
		
		tests.add(new TestCase(new StatisticAi(rolling05r5), new StatisticAi(greedyturn),
				runs, "rolling05r5-vs-greedyturn", map(size), deck(size)));
		
		tests.add(new TestCase(new StatisticAi(rolling05r10), new StatisticAi(greedyturn),
				runs, "rolling05r10-vs-greedyturn", map(size), deck(size)));
		
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
