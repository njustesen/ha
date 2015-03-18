package ai.evolution;

import game.GameState;

import java.util.concurrent.Callable;

public class RollingThread implements Callable<RollingHorizonEvolution> {
	
	public RollingHorizonEvolution rolling;
	
	public GameState state;
	
	public RollingThread(RollingHorizonEvolution rolling, GameState state) {
		super();
		this.rolling = rolling;
		this.state = state;
	}

	@Override
	public RollingHorizonEvolution call() throws Exception {
		rolling.search(state);
		return rolling;
	} 
	
}
