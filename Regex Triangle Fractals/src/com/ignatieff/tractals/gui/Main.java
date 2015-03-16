package com.ignatieff.tractals.gui;
	
import java.awt.image.BufferedImage;
	
public class Main {
	
	/**
	 * TO-DO List
	 * -----------------------------
	 *  x Colors when invalid input
	 *  x Bug-free (lol)
	 */
	
	public static void main(String[] args) {
		
		GUI_main g = new GUI_main();
		BufferedImage img = new BufferedImage(400,400,BufferedImage.TYPE_INT_RGB);
		g.drawFrame(img);	
		
	}
}	