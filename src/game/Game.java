package game;

import java.io.IOException;
import java.net.URL;
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
	private static boolean first = true;
	private static int level = -1;

	public static boolean PLAY_TEST = true;
	public static Reporter reporter;
	
	private static String[] origArgs;
	private static int budget = 100;
	
	public static void main(String[] args) {
		
		System.out.println("args "+args.length);
		
		if (PLAY_TEST)
			args = ("p1 human p2 islandrolling 1 " + budget + " sleep 5").split(" ");
		
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
			origArgs = args;
			reporter = new Reporter();
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
			this.ui = new UI(this.state, (this.player1 == null), (this.player2 == null), PLAY_TEST && first );

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

		if (PLAY_TEST){
			while(first && ui.levelPicker.level == -1){
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (first)
				level = ui.levelPicker.level;
			
			createReport();
			
			if (first)
				ui.frame.getContentPane().remove(0);
			ui.frame.repaint();
		}
		
		
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
			first = false;
			while(true){
				if (ui.action instanceof PlayAgainAction){
					ui.frame.dispose();
					ui = null;
					state = new GameState(state.map);
					Game game = new Game(null, gameArgs);
					game.run();
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
	
	private void createReport() {
		String ai = gameArgs.players[0] == null ? gameArgs.players[1].title() : gameArgs.players[0].title();
		boolean created = false;
		int time = 1000;
		while(!created){
			created = true;
			try {
				reporter.createReport(gameArgs.mapName, ai, budget, level);
			} catch (Exception e) {
				created = false;
				time += 200;
			}
			ui.connection = false;
			ui.frame.repaint();
			try {
				Thread.sleep(Math.min(time, 60000));
			} catch (InterruptedException e) {
				//e.printStackTrace();
			}
		}
		ui.connection = true;
		ui.frame.repaint();
		
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
		boolean updated = false;
		int time = 1000;
		while(!updated){
			updated = true;
			try {
				reporter.updateReport(state.turn, new HeuristicEvaluator(false).eval(state, p1), winner);
			} catch (Exception e) {
				ui.connection = false;
				updated = false;
				time += 200;
			}
			ui.frame.repaint();
			try {
				Thread.sleep(Math.min(time, 60000));
			} catch (InterruptedException e) {
				//e.printStackTrace();
			}
		}
		ui.connection = true;
		ui.frame.repaint();
		
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
