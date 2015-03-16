package com.ignatieff.tractals;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.ignatieff.tractals.gui.FrameSettings;

public class Renderer {
	
	public static BufferedImage generateFractal(Parser p, FrameSettings fs){
		return generateFractal(p, fs.regex, fs.cl, 400, fs.rotation, fs.rot, fs.decay, fs.depth, fs.f_degree);
	}
	
	public static BufferedImage generateFractal(Parser p, String regex, ColorList cl, int imageSize, double rotation, double rpe, double r, int depth, String f_degree){
		double m = 0.9*imageSize/2 * (1.0-r) / (1.0 - Math.pow(r, depth));
		
		Triangle[] t = Parser.getTriangles(p, regex, cl, f_degree, rotation, m, rpe, r, depth);
		
		Renderer rend = new Renderer(imageSize);
		rend.drawTriangles(t);
		rend.writeToImage(regex);
		return rend.img;
	}
	
	private BufferedImage img;
	private Graphics2D g;
	
	private static AlphaComposite ac_fill   = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f);
	private static AlphaComposite ac_stroke = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
	
	public Renderer(int size){
		img = new BufferedImage(size,size,BufferedImage.TYPE_INT_RGB);
		g = img.createGraphics();
		g.setColor(Color.WHITE);
	}
	
	public void writeData(String output){
		try {
			ImageIO.write(img, "png", new File(output));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void drawTriangles(Triangle[] t){
		for(Triangle k : t)
			drawTriangle(k);
	}
	
	/**
	 * Writes text to a BufferedImage-object in the lower right corner.
	 * Used to write the name of the regex unto the image object.
	 * @param image The image to write on.
	 * @param textToWrite The text to write
	 * @return A new BufferedImage, which has text written unto it.
	 */
	public void writeToImage(String text){
		g.setFont(new Font("Sans-Serif", Font.BOLD, 17));
		FontMetrics fm = g.getFontMetrics(); 
		int w = fm.stringWidth(text) + 10;
		int h = fm.getHeight() + 3;
		g.setColor(Color.WHITE);
		g.fillRect(0, img.getHeight()-h, w, h);
		g.setColor(Color.BLACK);
		g.drawString(text, 5, img.getHeight() - 6);
		g.dispose();
	}
	
	public void drawTriangle(Triangle t){
		
		g.setColor(t.getC());
		
		Polygon p = t.getPolygon(img.getWidth());
		
		if(p.npoints<3)return;
		
		setStroke(0);
		g.setComposite(ac_fill);
		g.fillPolygon(p);
		
		setStroke(t.getM()/33.0);
		
		g.setComposite(ac_stroke);
		g.drawPolygon(p);
	}
	
	
	@SuppressWarnings("unused")
	private static void printPolygon(Polygon p){
		for(int i=0; i<p.npoints; i++)
			System.out.println("v["+i+"]: (" +p.xpoints[i]+", "+p.ypoints[i]+")");
	}
	
	private void setStroke(double f){
		g.setStroke(new BasicStroke((float)f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
	}
}
