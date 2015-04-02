package game;

import java.io.IOException;

import model.DECK_SIZE;
import reporting.Reporter;
import testcase.TestCase;
import ui.LevelPicker;
import ui.UI;
import util.MapLoader;
import ai.AI;
import ai.GreedyActionAI;
import ai.GreedyTurnAI;
import ai.StatisticAi;
import ai.evaluation.HeuristicEvaluator;
import ai.evaluation.RolloutEvaluator;
import ai.evolution.IslandHorizonEvolution;

public class PlayTest {

	public AI opponent;
	public Reporter reporter;
	public LevelPicker levelPicker;
	public int level;
	
	public PlayTest(){
		levelPicker = new LevelPicker();
	}
	
	public static void main(String[] args){
		new PlayTest();
	}

	public void play(int level) {
		this.level = level;
		System.out.println(level);
		//Game.main(("p1 greedyaction heuristic 1 p2 islandrolling 1 sleep 200").split(" "));
		final AI p1 = new GreedyActionAI(new HeuristicEvaluator(false));
		final AI p2 = new GreedyTurnAI(new HeuristicEvaluator(false));

		try {
			TestCase.GFX = true;
			new TestCase(new StatisticAi(p1), new StatisticAi(p2), 50, "greedy-action-vs-greedy-turn", MapLoader.get("a"), DECK_SIZE.STANDARD).run();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
}
