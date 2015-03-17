package ai.evolution;

import game.GameState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ui.UI;
import model.HaMap;
import action.Action;
import ai.AI;
import ai.evaluation.IStateEvaluator;

public class RollingHorizonEvolution implements AI, AiVisualizor {

	public int popSize;
	public int budget;
	public double killRate;
	public double mutRate;
	public IStateEvaluator evaluator;
	
	public List<Double> generations;
	public List<Double> bestVisits;

	public List<Action> actions;
	
	public final List<Genome> pop;
	public Map<Integer, Double> fitnesses;
	public List<List<Action>> bestActions;
	
	private RollingHorizonVisualizor visualizor;
	private Map<Long, Double> visited;
	private final Random random;
	
	public RollingHorizonEvolution(int popSize, double mutRate, double killRate, int budget, IStateEvaluator evaluator) {
		super();
		this.popSize = popSize;
		this.mutRate = mutRate;
		this.budget = budget;
		this.evaluator = evaluator;
		this.killRate = killRate;
		pop = new ArrayList<Genome>();
		actions = new ArrayList<Action>();
		random = new Random();
		this.generations = new ArrayList<Double>();
		this.bestVisits = new ArrayList<Double>();
		this.fitnesses = new HashMap<Integer, Double>();
		this.bestActions = new ArrayList<List<Action>>();
		this.visited = new HashMap<Long, Double>();
	}
	
	public void enableVisualization(UI ui){
		this.visualizor = new RollingHorizonVisualizor(ui, this);
	}

	@Override
	public Action act(GameState state, long ms) {

		if (actions.isEmpty())
			search(state);

		final Action next = actions.get(0);
		actions.remove(0);
		return next;
	}

	public void search(GameState state) {

		Long start = System.currentTimeMillis();
		
		fitnesses.clear();
		bestActions.clear();
		visited.clear();
		
		setup(state);

		final List<Genome> killed = new ArrayList<Genome>();
		final GameState clone = new GameState(state.map);
		clone.imitate(state);
		
		int g = 0;
		
		while (System.currentTimeMillis() < start + budget) {

			g++;
			
			// Test pop
			double val = 0;
			for (final Genome genome : pop) {
				// System.out.print("|");
				clone.imitate(state);
				clone.update(genome.actions);
				val = evaluator.eval(clone, state.p1Turn);
				if (genome.visits == 0 || val < genome.value){
					/*
					Long hash = clone.hash();
					if (visited.containsKey(hash) && visited.get(hash) > val)
						visited.put(hash, val);
					else if (visited.containsKey(hash))
						val = visited.get(hash);
					else 
						visited.put(hash, val);
					*/
					genome.value = val;
					
				}
				genome.visits++;
			}

			// Kill worst genomes
			Collections.sort(pop);
			killed.clear();
			final int idx = (int) Math.floor(pop.size() * killRate);
			for (int i = idx; i < pop.size(); i++)
				killed.add(pop.get(i));
			
			// Crossover new ones
			for (final Genome genome : killed) {
				final int a = random.nextInt(idx);
				int b = random.nextInt(idx);
				while (b == a)
					b = random.nextInt(idx);

				clone.imitate(state);
				genome.crossover(pop.get(a), pop.get(b), clone);

				// Mutation
				if (Math.random() < mutRate) {
					clone.imitate(state);
					genome.mutate(clone);
				}

			}
			
			// TODO: Only if needed?!
			fitnesses.put(g, pop.get(0).fitness());
			bestActions.add(clone(pop.get(0).actions));
			
		}

		//System.out.println("Best Genome: " + pop.get(0).actions);
		//System.out.println("Visits: " + pop.get(0).visits);
		//System.out.println("Value: " + pop.get(0).avgValue());

		if (visualizor != null){
			visualizor.p1 = state.p1Turn;
			visualizor.update();
			while(visualizor.rendering){
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			};
		}
		
		actions = pop.get(0).actions;
		
		generations.add((double)g);
		bestVisits.add((double)(pop.get(0).visits));

	}
	
	private Map<Integer, Double> clone(Map<Integer, Double> other) {
		Map<Integer, Double> clone = new HashMap<Integer, Double>();
		for (Integer i : other.keySet())
			clone.put(i, other.get(i));
		return clone;
	}
	
	private List<Action> clone(List<Action> other) {
		List<Action> actions = new ArrayList<Action>();
		actions.addAll(other);
		return actions;
	}

	private void setup(GameState state) {

		pop.clear();
		final GameState clone = new GameState(state.map);

		for (int i = 0; i < popSize; i++) {
			clone.imitate(state);
			final Genome genome = new WeakGenome();
			genome.random(clone);
			pop.add(genome);
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
		return "Rolling Horizon Evolution";
	}

	@Override
	public AI copy() {
		if (visualizor!=null){
			RollingHorizonEvolution evo = new RollingHorizonEvolution(popSize, mutRate, killRate, budget, evaluator.copy());
			return evo;
		}
		
		return new RollingHorizonEvolution(popSize, mutRate, killRate, budget, evaluator.copy());
		
	}

}
