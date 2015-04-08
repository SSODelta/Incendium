package com.ignatieff.incendium.gui;
	
import java.awt.image.BufferedImage;

public class Main {
	
	/**
	 * TO-DO List
	 * -----------------------------
	 *  x Colors when invalid input
	 *  x Bug-free (lol)
	 */
	
	public static void main(String[] args) {
		/*
		FrameSettings fs = FrameSettings.DEFAULT;
		
		Parser p = new Parser(fs.f_degree, fs.depth,fs.regex);

		p.generateAllStrings();

		p.sort();

		Renderer.generateFractal(p, fs);*/
		
		GUI_main g = new GUI_main();
		BufferedImage img = new BufferedImage(400,400,BufferedImage.TYPE_INT_RGB);
		g.drawFrame(img);	
	}
}	