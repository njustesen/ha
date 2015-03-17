package ai.evolution;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JFrame;

import com.sun.corba.se.impl.oa.poa.ActiveObjectMap.Key;

import ui.UI;
import util.Statistics;

public class RollingHorizonVisualizor extends JComponent implements KeyListener {

	public UI ui;
	public boolean rendering;
	public boolean p1;
	
	private JFrame frame;
	private int width;
	private int height;
	
	private int div;

	private RollingHorizonEvolution rolling;
	private List<Point> points;
	private boolean control;
	
	public RollingHorizonVisualizor(UI ui, RollingHorizonEvolution rolling) {
		super();
		this.ui = ui;
		this.rolling = rolling;
		this.rendering = false;
		frame = new JFrame();
		frame.addKeyListener(this);
		width = 800;
		height = 260;
		div = 10;
		frame.setSize(width, height+32);
		frame.setTitle("Evolution");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(this);
		frame.setVisible(true);
		this.points = new ArrayList<Point>();
		this.control = false;
	}
	
	public void update(){

		rendering = true;
		points.clear();
		
		try {
				
			double xprog;
			int x;
			int y;
			double min = Collections.min(rolling.fitnesses.values());
			double max = Collections.max(rolling.fitnesses.values());
			double val;
			List<Integer> keys = new ArrayList<Integer>();
			keys.addAll(rolling.fitnesses.keySet());
			Collections.sort(keys);
			
			for(int gen : keys){
				xprog =  ((double)gen)/((double)rolling.fitnesses.size());
				x = (int) (div + (((double)(width-div-div)) * xprog));
				val = rolling.fitnesses.get(gen);
				val = (val - min) / (max -min);
				y = (int) ((height-div) - val * (height-div-div));	
				points.add(new Point(x,y));
				if (rolling.bestActions.size() > gen)
					ui.actionLayer = rolling.bestActions.get(gen);
				if (!control){
					repaint();
					ui.repaint();
					Thread.sleep(20);
				}
			}

			repaint();
			ui.repaint();
			Thread.sleep(20);
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		ui.actionLayer.clear();
		rendering = false;
		
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);
		
		g.setColor(Color.BLACK);
		g.drawLine(div, div, div, height-div);
		g.drawLine(div, height-div, width-div, height-div);
		
		if (p1)
			g.setColor(Color.RED);
		else
			g.setColor(Color.BLUE);
		
		int lastX = 0;
		int lastY = 0;
		int i = 0;
		for(Point point : points){
			
			if (i > 0)
				g.drawLine(lastX, lastY, point.x, point.y);
			
			lastX = point.x;
			lastY = point.y;
			i++;
			
		}
		
		g.setColor(Color.BLACK);
		g.drawOval(lastX-5, lastY-5, 10, 10);
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_CONTROL){
			control = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_CONTROL){
			control = false;
		}
	}
	
}