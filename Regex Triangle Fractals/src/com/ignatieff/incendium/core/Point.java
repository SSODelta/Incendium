package com.ignatieff.incendium.core;

public class Point {
	public double x = 0, y = 0;
	
	public Point(double x, double y){
		this.x=x;
		this.y=y;
	}
	
	public double getRadius(){
		return Math.hypot(x, y);
	}
	
	public Point add(Point p){
		return new Point(x+p.x,y+p.y);
	}
	
	public Point times(double k){
		return new Point(x*k,y*k);
	}

	public Point divided(double k){
		return new Point(x/k,y/k);
	}

	public double getAngle(){
		return Math.atan2(y, x);
	}

	public String toString(){
		return "("+x+", "+y+")";
	}
}
