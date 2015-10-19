package ai;

import java.util.ArrayList;
import java.util.List;

import game.GameState;
import action.Action;
import ai.evolution.IslandHorizonEvolution;
import ai.evolution.RollingHorizonEvolution;
import ai.mcts.Mcts;
import ai.mcts.RootParallelizedMcts;
import ai.movesearch.ParallelMoveSearcher;
import ai.util.AiStatistics;

public class StatisticAi implements AI {
	
	public AI ai;
	public AiStatistics aiStatistics;
	public List<Double> times;
	
	private Action action;
	private Class<AI> aiClass;
	
	@SuppressWarnings("unchecked")
	public StatisticAi(AI ai) {
		super();
		this.aiStatistics = new AiStatistics();
		this.times = new ArrayList<Double>();
		aiStatistics.statsLists.put("Time spend", times);
		this.ai = ai;
		this.aiClass = (Class<AI>) ai.getClass();
	}

	public Action act(GameState state, long ms){
		Long start = System.currentTimeMillis();
		action = ai.act(state, ms);
		times.add((double)(System.currentTimeMillis() - start));
		if (aiClass.equals(RollingHorizonEvolution.class)){
			aiStatistics.statsLists.put("Generations", ((RollingHorizonEvolution)ai).generations);
			aiStatistics.statsLists.put("Vists (selected genome)", ((RollingHorizonEvolution)ai).bestVisits);
			List<Double> count = new ArrayList<Double>(); 
			count.add(0d + ((RollingHorizonEvolution)ai).count);
			aiStatistics.statsLists.put("Moves considered", count);
		} else if (aiClass.equals(GreedyActionAI.class)){
			aiStatistics.statsLists.put("Branching factor", ((GreedyActionAI)ai).branching);
			aiStatistics.statsLists.put("Pruned branching factor", ((GreedyActionAI)ai).prunedBranching);
		}else if (aiClass.equals(GreedyTurnAI.class)){
			List<Double> count = new ArrayList<Double>(); 
			count.add(0d + ParallelMoveSearcher.UNIQUE_COUNTER);
			aiStatistics.statsLists.put("Considered moves", count);
		}else if (aiClass.equals(Mcts.class)){
			aiStatistics.statsLists.put("Iterations", ((Mcts)ai).rollouts);
			aiStatistics.statsLists.put("Avg. depths", ((Mcts)ai).avgDepths);
			aiStatistics.statsLists.put("Min. depths", ((Mcts)ai).minDepths);
			aiStatistics.statsLists.put("Max. depths", ((Mcts)ai).maxDepths);
			aiStatistics.statsLists.put("Moves considered", ((Mcts)ai).counts);
		}else if (aiClass.equals(RootParallelizedMcts.class)){
			aiStatistics.statsLists.put("Iterations", ((RootParallelizedMcts)ai).rollouts);
		} else if (aiClass.equals(IslandHorizonEvolution.class)){
			aiStatistics.statsLists.put("Generations", ((IslandHorizonEvolution)ai).generations);
			aiStatistics.statsLists.put("Best visits", ((IslandHorizonEvolution)ai).bestVisits);
		}
		return action;
	}
	
	public void init(GameState state, long ms){
		ai.init(state, ms);
	}
	
	public String header(){
		return ai.header();
	}
	
	public String title(){
		return ai.title();
	}

	@Override
	public String toString() {
		return aiStatistics.toString();
	}

	@Override
	public AI copy() {
		return new StatisticAi(ai.copy());
	}
	
}
