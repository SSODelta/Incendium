package com.ignatieff.incendium.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import org.matheclipse.parser.client.eval.DoubleEvaluator;
import org.matheclipse.parser.client.eval.DoubleVariable;
import org.matheclipse.parser.client.eval.IDoubleValue;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;

public class Parser {
			
	public ArrayList<String> strings;
	
	private DoubleEvaluator engine;
	private IDoubleValue id;
	private String f_degree;
	private int[] degs;
	private Automaton lang;
	private boolean acceptAll;
	private int depth, currentLayer;
	
	public void sort(){
		Comparator<String> x = new Comparator<String>(){
			@Override
			public int compare(String o1, String o2){
				if(o1.length() > o2.length())
					return -1;
				
				if(o2.length() > o1.length())
				    return 1;

				return 0;
				
			}
		};

		Collections.sort(strings,  x);
	}
	
	public static RegularPolygon[] getRegularPolygons(Parser p, String regex, ColorList cl, String f_degree, double theta, double size, double z, double r, int depth){
				
		ArrayList<RegularPolygon> t = new ArrayList<RegularPolygon>();
		
		int[] depths = p.getDepths();
		for(int i=0; i<p.strings.size(); i++){
			
			String s = p.strings.get(i);
			Point q = RegularPolygon.getPositionFromString(size, theta, r, s, z, depths);
			int n = s.length();

			int degree = depths[n];
			
			double t_size = size * Math.pow(r, n);
			double t_rot  = RegularPolygon.g(n, theta,z);
			
			RegularPolygon k = new RegularPolygon(q, cl.get(n), degree, n, t_size, t_rot);
			
			t.add(k);
		}	
			
		return t.toArray(new RegularPolygon[0]);
	}		
			
	public Parser(String f_degree, int depth, String regex){
		this.f_degree = f_degree;
		this.depth = depth;
		strings = new ArrayList<String>();
		id = new DoubleVariable(0.0);
		currentLayer = 0;
		engine = new DoubleEvaluator();
		engine.defineVariable("d", id);
		lang = getAutomaton(regex);
		acceptAll = regex.equals(".*") || regex.equals("");
	}
	
	/**
	 * Converts a regex-string to an Automaton-object.
	 * @param regex The string representation of the regular expression.
	 * @return An Automaton-object representing the regular expression.
	 */
	private static Automaton getAutomaton(String regex){
		RegExp r = new RegExp(regex);
		return r.toAutomaton(true);
	}
	
	public int getNextDepth(){
		if(!f_degree.contains(",")){
			currentLayer++;
			id.setValue(currentLayer);
			return (int) engine.evaluate(f_degree);
		}	
		String nextNumber = f_degree.substring(0, f_degree.indexOf(","));
		int num = Integer.parseInt(nextNumber);
		f_degree = f_degree.substring(f_degree.indexOf(",")+1);
		f_degree = f_degree + ","+num;
		return num;
	}
	
	public int[] getDepths(){
		if(degs!=null)return degs;
		currentLayer=0;
		int[] depths = new int[depth+1];
		for(int i=0; i<depths.length; i++){
			depths[i] = getNextDepth();
		}
		degs=depths;
		return depths;
	}
			
	public void generateAllStrings(){
		generateAllStrings(new StringBuilder(), getDepths());
	}
	
	public void generateAllStrings(StringBuilder sb, int[] depth){

		String s = sb.toString();
		
		
		if(acceptAll || lang.run(s)){
			strings.add(s);
		}
		
		if(depth.length==1)return;
		
		int d = depth[0];
		int[] newDepths = Arrays.copyOfRange(depth, 1, depth.length);
		for(int i=0; i<d; i++){
			generateAllStrings(new StringBuilder(sb).append(i), newDepths);
		}
	}
	
}
