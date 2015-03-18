package ai.evolution;

import game.GameState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import ui.UI;
import action.Action;
import action.SingletonAction;
import ai.AI;
import ai.evaluation.IStateEvaluator;

public class IslandHorizonEvolution implements AI, AiVisualizor {

	public int popSize;
	public int budget;
	public double killRate;
	public double mutRate;
	public IStateEvaluator evaluator;
	
	public List<Action> actions;
	
	private List<RollingThread> threads;
	private ExecutorService executor;
	private List<Future<RollingHorizonEvolution>> futures;
	
	public IslandHorizonEvolution(int popSize, double mutRate, double killRate, int budget, IStateEvaluator evaluator) {
		super();
		this.popSize = popSize;
		this.mutRate = mutRate;
		this.budget = budget;
		this.evaluator = evaluator;
		this.killRate = killRate;
		this.threads = new ArrayList<RollingThread>();
		this.futures = new ArrayList<Future<RollingHorizonEvolution>>();
		this.actions = new ArrayList<Action>();
		setup();
	}
	
	private void setup() {
		int processors = Runtime.getRuntime().availableProcessors();
		System.out.println("Processors: " + processors);
		this.executor = Executors.newFixedThreadPool(processors);
		for(int i=0; i < processors; i++) {
			RollingThread thread = new RollingThread(new RollingHorizonEvolution(popSize, mutRate, killRate, budget, evaluator.copy()), new GameState(null));
			threads.add(thread);
		}
		if (threads.size() > 1){
			for(int i = 0; i < threads.size(); i++){
				if (threads.size() > i + 1){
					threads.get(i).rolling.neighbor = threads.get(i+1).rolling;
				} else {
					threads.get(i).rolling.neighbor = threads.get(0).rolling;
				}
			}
		}
	}

	public void enableVisualization(UI ui){
		for(RollingThread thread : threads)
			thread.rolling.enableVisualization(ui);
	}

	@Override
	public Action act(GameState state, long ms) {

		if (actions.isEmpty())
			search(state);

		if (actions.isEmpty())
			return SingletonAction.endTurnAction;
		
		final Action next = actions.get(0);
		actions.remove(0);
		return next;
	}

	public void search(GameState state) {

		for(RollingThread thread : threads)
			thread.state.imitate(state);
		
		futures.clear();
		try {
			futures = executor.invokeAll(threads);
			actions.clear();
			double bestFitness = -1000000;
			for(Future<RollingHorizonEvolution> f : futures){
				double fitness = f.get().fitnesses.get(f.get().fitnesses.size()-1);
				if (fitness > bestFitness && !f.get().actions.isEmpty()){
					bestFitness = fitness;
					actions = f.get().actions;
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

	}
	
	@Override
	public void init(GameState state, long ms) {
		// TODO: 
	}
	
	@Override
	public String header() {
		String name = title()+"\n";
		name += "Pop. size = " + popSize + "\n";
		name += "Budget = " + budget + "ms.\n";
		name += "Mut. rate = " + mutRate + "\n";
		name += "Kill rate = " + killRate + "\n";
		name += "State evaluator = " + evaluator.title() + "\n";
		
		return name;
	}


	@Override
	public String title() {
		return "Parallelized (island) Rolling Horizon Evolution";
	}

	@Override
	public AI copy() {
		
		return null;
		
	}

}
