package ui;

import game.PlayTest;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.JFrame;

import lib.ImageLib;

public class LevelPicker extends JComponent implements MouseListener {

	public JFrame frame;
	private int width;
	private int height; 
	public int level;
	private boolean loading;
	
	public LevelPicker(){
		frame = new JFrame();
		width = 800;
		height = 600;
		frame.setSize(width, height);
		frame.setTitle("Hero AI");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(this);
		frame.setVisible(true);
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
		if (loading){
			g.setFont(new Font("Arial", 1, 32));
			g.drawString("LOADING", 300, 300);
		} else {
			g.drawString("Select how experienced you are playing Hero Academy.", 220, 100);
			g.drawString("If you haven't played Hero Academy before, select 'Beginner'.", 202, 130);
	
			g.drawImage(ImageLib.lib.get("beginner"), 260, 200, null);
			g.drawImage(ImageLib.lib.get("intermediate"), 260, 300, null);
			g.drawImage(ImageLib.lib.get("expert"), 260, 400, null);
		}
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getX() >= 260 && e.getX() <= 260+256 &&
				e.getY() >= 200 && e.getY() <= 200+64){
			click(1);
		}
		else if (e.getX() >= 260 && e.getX() <= 260+256 &&
				e.getY() >= 300 && e.getY() <= 300+64){
			click(2);
		}
		else if (e.getX() >= 260 && e.getX() <= 260+256 &&
				e.getY() >= 400 && e.getY() <= 400+64){
			click(3);
		}
	}

	private void click(int level) {
		
		loading = true;
		this.level = level;
		
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
