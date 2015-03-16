package com.ignatieff.tractals.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JColorChooser;
import javax.swing.JPanel;

import org.apache.commons.math3.exception.OutOfRangeException;

import com.ignatieff.tractals.ColorList;

public class ColorChooser extends JPanel implements MouseListener, MouseMotionListener {
	
	/**
	 * 
	 */
	private boolean gradient;
	private static final long serialVersionUID = -4210086868648349099L;
	public int width, height;
	public int num;
	public Color[] colors;
	
	public void setGradient(boolean g){
		gradient=g;
		if(g){
			colors = new ColorList(colors[0], colors[colors.length-1], num).get();
			repaint();
		}
	}
	public boolean getGradient(){
		return gradient;
	}
	
	public ColorChooser(int width, int height, int num){
		this(width,height);
		colors = new Color[num];
		gradient = false;
		this.num=num;
		repaint();
	}
	
	private String colorToString(Color c){
		return c.getRed()+","+c.getGreen()+","+c.getBlue();
	}
	
	@Override
	public String toString(){
		if(gradient){
			return "1" + colorToString(colors[0]) + "-" 
					+ colorToString(colors[colors.length-1]) + "-"+colors.length;
		}
		StringBuilder s = new StringBuilder('0');
		for(int i=0; i<colors.length; i++){
			s.append(colorToString(colors[i]));
			if(i!=colors.length-1)s.append('-');
		}
		return s.toString();
	}
	
	public static ColorList getColorListFromString(String s) throws Exception{
		//Format: RRR,GGG,BBB-RRR,GGG,BBB
		boolean gradient = s.startsWith("1");
		String[] subs = s.substring(1).split("-");
		if(gradient){
			Color a = getColorFromString(subs[0]),
				  b = getColorFromString(subs[1]);
			return new ColorList(a,b,Integer.parseInt(subs[2]));
		}
		Color[] colors = new Color[subs.length];
		for(int i=0; i<colors.length; i++){
			colors[i] = getColorFromString(subs[i]);
		}
		return new ColorList(colors);
	}
	
	private static Color getColorFromString(String s) throws Exception{
		String[] subsets = s.split(",");
		if(subsets.length!=3)
			throw new Exception("Can't create ColorChooser except if len(subsets(s))!=3:\n"+s);
		int r = -1,g = -1,b = -1;
		try{
			r = Integer.parseInt(subsets[0]);
			g = Integer.parseInt(subsets[1]);
		 	b = Integer.parseInt(subsets[2]);
		} catch (NumberFormatException e){
			throw new NumberFormatException("One of r,g,b is not castable to an integer: "+r+", "+g+", "+b);
		}
		return new Color(r,g,b);
	}
	
	public ColorChooser(int width, int height, ColorList cl){
		this(width,height);
		this.colors = cl.get();
		gradient = true;
		this.num = colors.length;
		repaint();
	}
	
	private ColorChooser(int width, int height){
		this.width = width;
		this.height = height;
		this.addMouseListener((MouseListener)this);
		this.addMouseMotionListener((MouseMotionListener)this);
	}
	
	public void makeGradient(){
		ColorList cl = new ColorList(colors[0], colors[colors.length-1], colors.length);
		this.colors = cl.get();
		repaint();
	}
	
	public ColorList getColorList(){
		return new ColorList(colors);
	}
	
	private void chooseColor(int i){
		Color c = JColorChooser.showDialog(null, "Choose a color", colors[i]);
		if(c!=null)colors[i]=c;
		repaint();
	}
	
	protected void paintChildren(Graphics g) {
		super.paintChildren(g);
		
		int w = width / colors.length;
		
		for(int i=0; i<colors.length; i++){
			g.setColor(colors[i]);
			g.fillRect(i*w, 0, w, height);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		int w = width / colors.length;
		int i = (int)Math.floor(e.getX() / w);
		
		if(i==0 || i==colors.length-1){
			chooseColor(i);
		} else if(!gradient){
			chooseColor(i);
		}
		if(gradient)
			makeGradient();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseMoved(MouseEvent e) {

		int w = width / colors.length;
		int i = (int)Math.floor(e.getX() / w);
		
		if(!gradient || (gradient && (i==0 || i==num-1))){
			setCursor (Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		} else {
			setCursor (Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	};
	
}
