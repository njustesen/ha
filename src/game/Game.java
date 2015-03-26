package game;

import java.io.IOException;
import java.util.Stack;

import reporting.Reporter;
import model.HaMap;
import ui.LevelPicker;
import ui.UI;
import util.CachedLines;
import util.MapLoader;
import util.pool.ObjectPools;
import action.Action;
import action.PlayAgainAction;
import action.SingletonAction;
import action.UndoAction;
import ai.AI;
import ai.evaluation.HeuristicEvaluator;
import ai.evolution.AiVisualizor;

public class Game {

	private static final long TIME_LIMIT = -1;
	public GameState state;
	public UI ui;
	public AI player1;
	public AI player2;
	public GameArguments gameArgs;
	private Stack<GameState> history;
	private int lastTurn;

	public static boolean PLAY_TEST = true;
	public static LevelPicker levelPicker;
	public static int level = -1;
	public static Reporter reporter;
	
	private static String[] origArgs;
	private static int budget = 6000;
	
	public static void main(String[] args) {
		
		if (args.length == 0 && PLAY_TEST)
			args = ("p1 human p2 greedyturn heuristic " + budget + " sleep 500").split(" ");
		
		//args = ("p1 human p2 islandrolling 1 sleep 500").split(" ");
		
		origArgs = args;
		
		GameArguments gameArgs = new GameArguments(args);
		
		HaMap map;
		try{
			map = MapLoader.get(gameArgs.mapName);
		} catch (IOException e){
			System.out.println("Map not found.");
			return;
		}
		
		if (PLAY_TEST){
			GameState.RANDOMNESS = true;
			String ai = "";
			if (gameArgs.players[0] != null)
				ai = gameArgs.players[0].title();
			else if (gameArgs.players[1] != null)
				ai = gameArgs.players[1].title();
			if (level == -1){
				origArgs = args;
				levelPicker = new LevelPicker();
				reporter = new Reporter();
			}
			while(true){
				try {
					Thread.sleep(100);
					if (levelPicker.level != -1){
						level = levelPicker.level;
						//levelPicker.frame.dispose();
						reporter.createReport(map.name, ai, budget, level);
						break;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		try {
			GameState state = ObjectPools.borrowState(map);
			final Game game = new Game(state, gameArgs);
			game.run();
		} catch (final Exception e) {
			e.printStackTrace();
		}

	}

	public Game(GameState state, GameArguments gameArgs) {
		this.gameArgs = gameArgs;
		this.player1 = gameArgs.players[0];
		this.player2 = gameArgs.players[1];
		
		history = new Stack<GameState>();
		if (state == null)
			this.state = new GameState(null);
		else
			this.state = state;

		if (this.state.map == null){
			try {
				this.state = new GameState(MapLoader.get(gameArgs.mapName));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if (SingletonAction.positions == null)
			SingletonAction.init(this.state.map);
		
		if (gameArgs.gfx){
			this.ui = new UI(this.state, (this.player1 == null),
					(this.player2 == null));

			if (player1 instanceof AiVisualizor && !PLAY_TEST)
				((AiVisualizor)player1).enableVisualization(ui);
			if (player2 instanceof AiVisualizor && !PLAY_TEST)
				((AiVisualizor)player2).enableVisualization(ui);
		
		}
		
		history = new Stack<GameState>();
		if (CachedLines.posMap.isEmpty() || this.state.map != CachedLines.map)
			CachedLines.load(this.state.map);

	}

	public void run() {

		state.init(gameArgs.deckSize);
		GameState initState = ObjectPools.borrowState(state.map);
		initState.imitate(state);
		history.add(initState);
		lastTurn = 5;

		if (player1 != null)
			player1.init(state, -1);
		if (player2 != null)
			player2.init(state, -1);

		while (!state.isTerminal) {

			if (ui != null) {
				ui.state = state.copy();
				ui.repaint();
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
				}
			}
			
			if (state.p1Turn && player1 != null){
				act(player1, player2);
				try {
					Thread.sleep(gameArgs.sleep);
				} catch (InterruptedException e) {
				}
			}else if (!state.p1Turn && player2 != null){
				act(player2, player1);
				try {
					Thread.sleep(gameArgs.sleep);
				} catch (InterruptedException e) {
				}
			}else if (ui.action != null) {

				if (ui.action instanceof UndoAction)
					undoAction();
				else {
					state.update(ui.action);
					if (PLAY_TEST && ui.action == SingletonAction.endTurnAction)
						updateReport();
				}
				ui.lastAction = ui.action;
				ui.resetActions();

			}
			
			if (PLAY_TEST && state.isTerminal)
				updateReport();

			if (state.APLeft != lastTurn) {
				if (state.APLeft < lastTurn){
					GameState clone = ObjectPools.borrowState(state.map);
					clone.imitate(state);
					history.add(clone);
				}
				lastTurn = state.APLeft;
			}

			if (state.APLeft == 5) {
				history.clear();
				GameState clone = ObjectPools.borrowState(state.map);
				clone.imitate(state);
				history.add(clone);
				lastTurn = 5;
			}

		}
		if (ui != null) {
			ui.state = state.copy();
			ui.repaint();
		}
		if (PLAY_TEST){
			while(true){
				if (ui.action instanceof PlayAgainAction){
					ui.frame.dispose();
					ui = null;
					main(origArgs);
					break;
				}
				try {
					ui.repaint();
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private void updateReport() {
		boolean p1 = player1 == null ? true : false;
		String winner = "";
		if (state.isTerminal){
			if (state.getWinner() == 1)
				if (p1)
					winner = "human";
				else
					winner = "ai";
			else if (state.getWinner() == 2)
				if (p1)
					winner = "ai";
				else
					winner = "human";
			else
				winner = "draw";
		}
		reporter.updateReport(state.turn, new HeuristicEvaluator(false).eval(state, p1), winner);
	}

	private void act(AI player, AI other) {
		GameState clone = ObjectPools.borrowState(state.map);
		clone.imitate(state);
		if (!GameState.OPEN_HANDS)
			clone.hideCards(!state.p1Turn);
		Action action = player.act(clone, TIME_LIMIT);
		if (action == null)
			action = SingletonAction.endTurnAction;
		state.update(action);
		if (ui != null)
			ui.lastAction = action;
		if (other == null)
			try {
				Thread.sleep(gameArgs.sleep);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
	}

	private void undoAction() {

		if (state.APLeft == 5)
			return;

		if (state.isTerminal)
			return;

		if (history.size() > 1){
			history.pop();
			
			state = ObjectPools.borrowState(state.map);
			state.imitate(history.peek());
		}
	}

}
