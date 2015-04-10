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
		
	}
	
	private static void MctsCTests(int runs, String size) {
		final List<TestCase> tests = new ArrayList<TestCase>();
		/*
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
		*/
		
		final Mcts mcts10 = new Mcts(6000, new RolloutEvaluator(1, 1,randomHeuristic, new HeuristicEvaluator(true)));
		mcts10.c = mcts10.c / 192;
		
		final Mcts mcts11 = new Mcts(6000, new RolloutEvaluator(1, 1,randomHeuristic, new HeuristicEvaluator(true)));
		mcts11.c = 0;
		
		final AI greedyaction = new GreedyActionAI(new HeuristicEvaluator(true));
		  /*
		tests.add(new TestCase(new StatisticAi(mcts1), new StatisticAi(greedyaction),
				runs, "mcts-c2-vs-greedyaction-2", map(size), deck(size)));
		tests.add(new TestCase(new StatisticAi(mcts2), new StatisticAi(greedyaction),
				runs, "mcts-c4-vs-greedyaction-2", map(size), deck(size)));
		tests.add(new TestCase(new StatisticAi(mcts3), new StatisticAi(greedyaction),
				runs, "mcts-c8-vs-greedyaction-2", map(size), deck(size)));
		
		tests.add(new TestCase(new StatisticAi(mcts7), new StatisticAi(greedyaction),
				runs, "mcts-c48-vs-greedyaction", map(size), deck(size)));
		tests.add(new TestCase(new StatisticAi(mcts8), new StatisticAi(greedyaction),
				runs, "mcts-c64-vs-greedyaction", map(size), deck(size)));
		tests.add(new TestCase(new StatisticAi(mcts9), new StatisticAi(greedyaction),
				runs, "mcts-c92-vs-greedyaction", map(size), deck(size)));
		*/
		tests.add(new TestCase(new StatisticAi(mcts10), new StatisticAi(greedyaction),
				runs, "mcts-c192-vs-greedyaction", map(size), deck(size)));
		tests.add(new TestCase(new StatisticAi(mcts11), new StatisticAi(greedyaction),
				runs, "mcts-c0-vs-greedyaction", map(size), deck(size)));
		
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
