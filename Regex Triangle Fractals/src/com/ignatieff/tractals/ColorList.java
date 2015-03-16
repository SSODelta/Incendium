package com.ignatieff.tractals;

import java.awt.Color;

public class ColorList {
	
	private Color[] colors;
	
	public ColorList(Color[] cols){
		colors = cols;
	}
	
	public ColorList(Color a, Color b, int n){
		this(createGradient(a,b,n));
	}
	
	public Color get(int i){
		return colors[i];
	}
	
	public Color[] get(){
		return colors;
	}
	
	private static Color[] createGradient(Color a, Color b, int n){
		Color[] colors = new Color[n];
		
		double dr = b.getRed() -   a.getRed();
		double db = b.getBlue() -  a.getBlue();
		double dg = b.getGreen() - a.getGreen();
		
		double dc = 1.0/((double)n-1);
		for(int i=0; i<n; i++){
			colors[i] = new Color((int)(a.getRed()   + dr*dc*i),
								  (int)(a.getGreen() + dg*dc*i),
								  (int)(a.getBlue()  + db*dc*i));
		}
		
		return colors;
	}
}
