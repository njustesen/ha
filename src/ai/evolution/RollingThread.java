package ai.evolution;

import game.GameState;

import java.util.concurrent.Callable;

public class RollingThread implements Callable<RollingHorizonEvolution> {
	
	public RollingHorizonEvolution rolling;
	
	public GameState state;

	private SharedStateTable table;
	
	public RollingThread(RollingHorizonEvolution rolling, GameState state, SharedStateTable table) {
		super();
		this.rolling = rolling;
		this.state = state;
		this.table = table;
	}

	@Override
	public RollingHorizonEvolution call() throws Exception {
		rolling.table = table;
		rolling.search(state);
		return rolling;
	} 
	
}
