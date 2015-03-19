package ai.neat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lib.UnitClassLib;
import model.Card;
import model.CardType;
import model.Position;
import game.GameState;
import action.Action;
import action.SingletonAction;
import ai.AI;
import ai.neat.jneat.NNode;
import ai.neat.jneat.Network;
import ai.util.ActionPruner;

public abstract class NeatAI implements AI{

	private static final ActionPruner pruner = new ActionPruner();
	
	public Network net;
	
	public NeatAI(Network net) {
		super();
		this.net = net;
	}

	@Override
	public Action act(GameState state, long ms) {
		GameState clone = state.copy();
		List<Action> possible = new ArrayList<Action>();
		state.possibleActions(possible);
		pruner.prune(possible, state);
		Collections.shuffle(possible);
		boolean started = false;
		double bestVal = 0;
		Action bestAction = SingletonAction.endTurnAction;
		//System.out.println("----------------");
		for(Action action : possible){
			if (started)
				clone.imitate(state);
			clone.update(action);
			double val = eval(clone);
			//System.out.println(val + "\t" + action);
			if (started || val > bestVal){
				bestVal = val;
				bestAction = action;
			}
			started = true;
		}
		return bestAction;
	}

	private double eval(GameState clone) {
		net.flush();
		net.load_sensors(stateToArray(clone));
		net.activate();
		return ((NNode)net.getOutputs().get(0)).getActivation();
	}
	
	public abstract double[] stateToArray(GameState state);
	
	protected double unitHP(GameState state, boolean p1) {
		double hp = 0;
		
		for(int x = 0; x < state.map.width; x++){
			for(int y = 0; y < state.map.height; y++){
				if (state.units[x][y] != null && 
						state.units[x][y].p1Owner == p1 && 
						state.units[x][y].unitClass.card != Card.CRYSTAL)
					hp+=state.units[x][y].hp;
			}
		}
		
		for(Card card : Card.values()){
			if (card.type != CardType.UNIT)
				continue;
			if (p1 && state.p1Hand.contains(card))
				hp += UnitClassLib.lib.get(card).maxHP * state.p1Hand.count(card);
			else if (!p1 && state.p2Hand.contains(card))
				hp += UnitClassLib.lib.get(card).maxHP * state.p2Hand.count(card);
			if (p1 && state.p1Deck.contains(card))
				hp += UnitClassLib.lib.get(card).maxHP * state.p1Deck.count(card);
			else if (!p1 && state.p2Deck.contains(card))
				hp += UnitClassLib.lib.get(card).maxHP * state.p2Deck.count(card);
		}
		
		return hp;
	}

	protected double crystalHP(GameState state, boolean p1) {
		double hp = 0;
		if (p1){
			for(Position pos : state.map.p1Crystals)
				if (state.unitAt(pos) != null && state.unitAt(pos).unitClass.card == Card.CRYSTAL)
					hp += state.unitAt(pos).hp;
		}else{
			for(Position pos : state.map.p2Crystals)
				if (state.unitAt(pos) != null && state.unitAt(pos).unitClass.card == Card.CRYSTAL)
					hp += state.unitAt(pos).hp;
		}
		
		return hp;
	}
	@Override
	public String header() {
		
		String header = title() + "\n";
		header += "Network size (nodes): " + net.getAllnodes().size() + "\n";
		header += "Network size (inputs): " + net.getInputs().size() + "\n";
		header += "Network size (outputs): " + net.getOutputs().size() + "\n";
		header += "Network size (hidden): " + (net.getAllnodes().size() - (net.getOutputs().size() + net.getInputs().size())) + "\n";
		
		return header;
		
	}
	
	@Override
	public void init(GameState state, long ms) {
		// TODO Auto-generated method stub
		
	}

}
