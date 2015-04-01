package ui;

import game.PlayTest;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.JFrame;

import libs.ImageLib;

public class LevelPicker extends JComponent implements MouseListener {

	public JFrame frame;
	public int width;
	public int height; 
	public int level;
	private boolean loading;
	private boolean showRules;
	
	public LevelPicker(){
		//frame = new JFrame();
		width = 800;
		height = 600;
		showRules = false;
		//frame.setSize(width, height);
		//frame.setTitle("Hero AI");
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//frame.getContentPane().add(this);
		//frame.setVisible(true);
		setSize(width, height);
		this.addMouseListener(this);
		this.level = -1;
		loading = false;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.setColor(Color.darkGray);
		g.fillRect(0, 0, width, height);
		
		g.setColor(Color.lightGray);
		if (showRules){
			g.setFont(new Font("Arial", 1, 32));
			g.drawString("RULES", 60, 70);
			g.setFont(new Font("Arial", 1, 16));	
			g.drawString("The first player to destroy the opponents crystals wins the game.", 60, 100);
			g.drawString("You will however lose the game if you run out of units.", 60, 120);
			g.drawString("Players can make up to 5 actions each turn. The blue 'wheel'", 60, 160);
			g.drawString("shows you how many action points you have left. You can always", 60, 180);
			g.drawString("undo an action by clicking on the blue wheel.", 60, 200);
			
			g.setFont(new Font("Arial", 1, 32));
			
			g.drawImage(ImageLib.lib.get("ap-4"), 580, 140, null);
			
			g.drawImage(ImageLib.lib.get("play"), width/2 - 128, 300, null);
			
		} else if (loading){
			g.setFont(new Font("Arial", 1, 32));
			g.drawString("LOADING", 275, 140);
			g.setFont(new Font("Arial", 1, 12));
			g.drawString("If this takes more than a few seconds, check your internet connnection.", 142, 280);
		} else {
			g.drawString("Select how experienced you are playing Hero Academy.", 180, 50);
			g.drawString("If you haven't played Hero Academy before, select 'Beginner'.", 164, 72);
			g.drawString("Your choice will NOT affect the level of the AI.", 204, 94);
			
			g.drawImage(ImageLib.lib.get("beginner"), width/2 - 128, 140, null);
			g.drawImage(ImageLib.lib.get("intermediate"), width/2 - 128, 220, null);
			g.drawImage(ImageLib.lib.get("expert"), width/2 - 128, 300, null);
			
			g.drawString("Results of the game are automatically sent to a web server.", 166, 408);
			g.drawString("PLEASE REMAIN CONNECTED", 260, 450);
			
		}
		
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getX() >= width/2 - 128 && e.getX() <= width/2 + 128 &&
				e.getY() >= 140 && e.getY() <= 140+64){
			if (!showRules)
				click(1);
		}
		else if (e.getX() >= width/2 - 128 && e.getX() <= width/2 + 128 &&
				e.getY() >= 220 && e.getY() <= 220+64){
			if (!showRules)
				click(2);
		}
		else if (e.getX() >= width/2 - 128 && e.getX() <= width/2 + 128 &&
				e.getY() >= 300 && e.getY() <= 300+64){
			if (showRules)
				click1();
			else 
				click(3);
		}
	}
	
	private void click1() {
		loading = true;
		this.level = 1;
		showRules = false;
		repaint();
		System.out.println("Level " + level);
	}

	private void click(int level) {
		
		if (level == 1){
			showRules = true;
			repaint();
		} else {
			loading = true;
			this.level = level;
			repaint();
			System.out.println("Level " + level);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
