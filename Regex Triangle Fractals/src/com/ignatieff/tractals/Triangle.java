package com.ignatieff.tractals;

import java.awt.Color;
import java.awt.Polygon;

import org.matheclipse.parser.client.eval.DoubleEvaluator;
import org.matheclipse.parser.client.eval.DoubleVariable;
import org.matheclipse.parser.client.eval.IDoubleValue;
//http://webmup.com/2snIr/
public class Triangle implements Comparable<Triangle> {
	private Point pos;
	private double m, phi;
	private int depth, degree;
	private Color c;
	
	private static double PI_2 = (double)2 * Math.PI;

	public static Point ORIGO = new Point(0,0);
	
	public Triangle(int degree){
		this(0, degree);
	}
	
	public Triangle(double m, int degree){
		this(m,degree,0);
	}
	
	public Triangle(double m, int degree, double phi){
		this(ORIGO, degree,m,phi);
	}
	
	public Triangle(Point p, int degree, double m, double phi){
		this(p,Color.WHITE, degree,-1, m, phi);
	}
	
	public Triangle(Point p, Color c, int degree, int depth, double m, double phi){
		this.setC(c);
		this.setDegree(degree);
		this.setPos(p);
		this.setM(m);
		this.setPhi(phi);
	}
	
	public void setDegree(int degree) {
		this.degree = degree;
	}

	public static double g(int n, double theta, double z){
		return theta + n*z;
	}
	
	private static Point EMPTY = new Point(0,0);
	
	private static short[] toShortArr(String s){
		char[] c = s.toCharArray();
		short[] sh = new short[c.length];
		
		for(int i=0; i<c.length; i++){
			switch(c[i]){
				case '0':
					sh[i]=0;
					break;
				case '1':
					sh[i]=1;
					break;
				case '2':
					sh[i]=2;
					break;
				case '3':
					sh[i]=3;
					break;
				case '4':
					sh[i]=4;
					break;
				case '5':
					sh[i]=5;
					break;
				case '6':
					sh[i]=6;
					break;
				case '7':
					sh[i]=7;
					break;
				case '8':
					sh[i]=8;
					break;
				case '9':
					sh[i]=9;
					break;
			}
		}
		return sh;
	}
	
	public static Point getPositionFromString(double m, double theta, double r, String s, double z, String f_degree){
		return getPositionFromString(theta,0,r,toShortArr(s),z, f_degree).times(m);
	}
	
	public static Point getPositionFromString(double theta, int k, double r, short[] c, double z, String f_degree){
		if(c.length==0)return EMPTY;
		
		IDoubleValue val = new DoubleVariable(0.0);
		DoubleEvaluator engine = new DoubleEvaluator();
		engine.defineVariable("d",val);
		
		val.setValue(k);
		int max = (int)engine.evaluate(f_degree);
		Point p = getPositionFromChar(theta, k, c[0],z, max).add(getPositionFromString(theta, k+1, r, splice(c),z, f_degree).times(r));
		return p;
	}
	
	private static Point getPositionFromChar(double theta, int n, short c, double z, int max){		
		Point p = new Point(Math.cos(g(n,theta,z) + ((double)c)*2/(double)max*Math.PI),
							Math.sin(g(n,theta,z) + ((double)c)*2/(double)max*Math.PI));
		return p;
	}
	
	private static short[] splice(short[] c){
		short[] s = new short[c.length-1];
		for(int i=1; i<c.length; i++)
			s[i-1]=c[i];
		return s;
	}

	public Point getPos() {
		return pos;
	}

	public void setPos(Point pos) {
		this.pos = pos;
	}

	public double getPhi() {
		return phi;
	}

	public void setPhi(double phi) {
		this.phi = phi;
	}

	public double getM() {
		return m;
	}

	public void setM(double m) {
		this.m = m;
	}
	
	public Polygon getPolygon(int size){
		
		Point[] q = getEdges();
		int[] x_points = new int[degree];
		int[] y_points = new int[degree];
		
		for(int i=0; i<degree; i++){
			x_points[i] = (int)(q[i].x+size/2+pos.x);
			y_points[i] = (int)(q[i].y+size/2+pos.y);
		}
		
		return new Polygon(	x_points,
							y_points,
							degree);
	}
	
	public Point[] getEdges(){
		Point[] points = new Point[degree];
		
		for(int i=0; i<degree; i++){
			points[i] = new Point(m*Math.cos(phi+i*PI_2/degree),			//P0
					  			  m*Math.sin(phi+i*PI_2/degree));	
		}
		return points;
	}
	
	@Override
	public String toString(){
		return "["+pos.x+", "+pos.y+"]: phi=" + phi + "|m=" + m;
	}

	public Color getC() {
		return c;
	}

	public void setC(Color c) {
		this.c = c;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	@Override
	public int compareTo(Triangle o) {
		return (int)(this.getM() - o.getM());
	}
}
