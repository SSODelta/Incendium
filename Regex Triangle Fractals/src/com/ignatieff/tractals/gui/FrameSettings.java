package com.ignatieff.tractals.gui;

import com.ignatieff.tractals.ColorList;

public class FrameSettings {
	public String regex, f_degree;
	public int depth;
	public ColorList cl;
	public double rot, decay, rotation;
	
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
