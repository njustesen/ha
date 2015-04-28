package ai.neat;

import model.Card;
import game.GameState;
import ai.AI;
import ai.evaluation.MeanEvaluator;
import ai.neat.jneat.Network;

public class NaiveNeatAI extends NeatAI{
	
	boolean simple;
	
	public NaiveNeatAI(Network net, boolean debug, boolean simple) {
		super(net, debug);
		simple = simple;
	}
	
	public double[] stateToArray(GameState state) {
		
		//int inputs = state.map.width * state.map.height * 11 + 5 + 10;
		//int inputs = 180;
		//int inputs = 323;
		int inputs = 5;
		if (!simple)
			inputs = state.map.width * state.map.height * 13 + 5 + 10;
		double[] arr = new double[inputs];
		
		// BASE
		arr[0] = crystalHP(state, state.p1Turn) / MeanEvaluator.MAX_CRYSTAL_TINY;
		arr[1] = crystalHP(state, !state.p1Turn) / MeanEvaluator.MAX_CRYSTAL_TINY;
		arr[2] = unitHP(state, state.p1Turn) / 4200;
		arr[3] = unitHP(state, !state.p1Turn) / 4200;

		// Bias
		arr[4] = 1.0;
		
		// FOR EACH SQUARE
		int i = 5;
		
		for(int x = 0; x < state.map.width; x++){
			for(int y = 0; y < state.map.height; y++){
				for(double d : squareArray(state, x, y)){
					arr[i++] = d;
				}
			}
		}
		
		// Own hand
		if (state.p1Turn){
			arr[i++] = state.p1Hand.has(Card.ARCHER) ? 1 : 0;
			arr[i++] = state.p1Hand.has(Card.CLERIC) ? 1 : 0;
			arr[i++] = state.p1Hand.has(Card.KNIGHT) ? 1 : 0;
			arr[i++] = state.p1Hand.has(Card.NINJA) ? 1 : 0;
			arr[i++] = state.p1Hand.has(Card.WIZARD) ? 1 : 0;
			arr[i++] = state.p1Hand.has(Card.DRAGONSCALE) ? 1 : 0;
			arr[i++] = state.p1Hand.has(Card.INFERNO) ? 1 : 0;
			arr[i++] = state.p1Hand.has(Card.REVIVE_POTION) ? 1 : 0;
			arr[i++] = state.p1Hand.has(Card.SCROLL) ? 1 : 0;
			arr[i++] = state.p1Hand.has(Card.SHINING_HELM) ? 1 : 0;
		} else {
			arr[i++] = state.p2Hand.has(Card.ARCHER) ? 1 : 0;
			arr[i++] = state.p2Hand.has(Card.CLERIC) ? 1 : 0;
			arr[i++] = state.p2Hand.has(Card.KNIGHT) ? 1 : 0;
			arr[i++] = state.p2Hand.has(Card.NINJA) ? 1 : 0;
			arr[i++] = state.p2Hand.has(Card.WIZARD) ? 1 : 0;
			arr[i++] = state.p2Hand.has(Card.DRAGONSCALE) ? 1 : 0;
			arr[i++] = state.p2Hand.has(Card.INFERNO) ? 1 : 0;
			arr[i++] = state.p2Hand.has(Card.REVIVE_POTION) ? 1 : 0;
			arr[i++] = state.p2Hand.has(Card.SCROLL) ? 1 : 0;
			arr[i++] = state.p2Hand.has(Card.SHINING_HELM) ? 1 : 0;
		}
		
		return arr;
	}
	
	private double[] squareArray(GameState state, int x, int y){
		
		double hp = 0.0;
		double p1Unit = 0.0;
		double p2Unit = 0.0;
		double knight = 0.0;
		double cleric = 0.0;
		double archer = 0.0;
		double wizard = 0.0;
		double ninja = 0.0;
		double crystal = 0.0;
		double runemetal = 0.0;
		double dragonscale = 0.0;
		double shininghelm = 0.0;
		double scroll = 0.0;
		
		if (state.units[x][y] != null){
			hp = (double)state.units[x][y].hp / ((double)state.units[x][y].unitClass.maxHP * 1.5);
			p1Unit = 0.0;
			p2Unit = 0.0;
			if (state.units[x][y].p1Owner == state.p1Turn){
				p1Unit = 1;
				p2Unit = 0;
			} else if (state.units[x][y].p1Owner != state.p1Turn){
				p1Unit = 0;
				p2Unit = 1;
			}
			if (state.units[x][y].unitClass.card == Card.ARCHER)
				archer = 1.0;
			if (state.units[x][y].unitClass.card == Card.CLERIC)
				cleric = 1.0;
			if (state.units[x][y].unitClass.card == Card.CRYSTAL)
				crystal = 1.0;
			if (state.units[x][y].unitClass.card == Card.KNIGHT)
				knight = 1.0;
			if (state.units[x][y].unitClass.card == Card.NINJA)
				ninja = 1.0;
			if (state.units[x][y].unitClass.card == Card.WIZARD)
				wizard = 1.0;
			
			for(Card card : state.units[x][y].equipment){
				if (card == Card.DRAGONSCALE)
					dragonscale = 1;
				if (card == Card.RUNEMETAL)
					runemetal = 1;
				if (card == Card.SCROLL)
					scroll = 1;
				if (card == Card.SHINING_HELM)
					shininghelm = 1;
			}
				
		}
		
		double[] arr = new double[13];
		arr[0] = hp;
		arr[1] = knight;
		arr[2] = cleric;
		arr[3] = archer;
		arr[4] = wizard;
		arr[5] = ninja;
		arr[6] = crystal;
		arr[7] = runemetal;
		arr[8] = dragonscale;
		arr[9] = shininghelm;
		arr[10] = scroll;
		arr[11] = p1Unit;
		arr[12] = p2Unit;
		
		return arr;

	}

	@Override
	public void init(GameState state, long ms) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String title() {
		return "Naive AI";
	}

	@Override
	public AI copy() {
		//return new NaiveNeatAI(net.);
		// TODO: how to copy network?
		return null;
	}

}
