package com.ignatieff.incendium.gui;

import java.awt.Color;

import com.ignatieff.incendium.core.ColorList;

public class FrameSettings {
	public String regex, f_degree;
	public int depth;
	public ColorList cl;
	public double rot, decay, rotation;
	
	public static FrameSettings DEFAULT = new FrameSettings(".*",new ColorList(Color.RED, Color.yellow, 10),0,7,0,0.5,"3,4");
	
	public FrameSettings(String regex, ColorList cl, int rotation, int depth, double rot, double decay, String f_degree){
		this.regex=regex;
		this.depth=depth;
		this.rot=rot;
		this.rotation=rotation;
		this.decay=decay;
		this.f_degree=f_degree;
		this.cl=cl;
	}
}
