package com.ignatieff.tractals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Pattern;

import org.matheclipse.parser.client.eval.DoubleEvaluator;
import org.matheclipse.parser.client.eval.DoubleVariable;
import org.matheclipse.parser.client.eval.IDoubleValue;

public class Parser {
			
	public ArrayList<String> strings;
	
	private DoubleEvaluator engine;
	private IDoubleValue id;
	private String f_degree;
	
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
	
	public static Triangle[] getTriangles(Parser p, String regex, ColorList cl, String f_degree, double theta, double size, double z, double r, int depth){
				
		ArrayList<Triangle> t = new ArrayList<Triangle>();
		
		
		for(int i=0; i<p.strings.size(); i++){
			
			String s = p.strings.get(i);
			Point q = Triangle.getPositionFromString(size, theta, r, s, z, f_degree);
			int n = s.length();

			int degree = p.getDegree(n);
			
			double t_size = size * Math.pow(r, n);
			double t_rot  = Triangle.g(n, theta,z);
			
			Triangle k = new Triangle(q, cl.get(n), degree, n, t_size, t_rot);
			
			t.add(k);
		}	
			
		return t.toArray(new Triangle[0]);
	}		
			
	public Parser(String f_degree){
		this.f_degree = f_degree;
		strings = null;
		id = new DoubleVariable(0.0);
		
		engine = new DoubleEvaluator();
		engine.defineVariable("d", id);
	}
	
	public int getDegree(int depth){
		id.setValue(depth);
		return (int)engine.evaluate(f_degree);
	}
	
	public void removeNonMatches(String regex){
		if(regex.equals(".*")||regex.equals(""))return;
		Pattern p = Pattern.compile(regex);
		
		ArrayList<String> matches = new ArrayList<String>();
		
		for(int i=0; i<strings.size(); i++){
			String s = strings.get(i);
			if(p.matcher(s).matches()){
				matches.add(s);
			}
		}
		
		strings = matches;
	}
	
	public void generateAllStrings(int maxLen){
		strings = new ArrayList<String>();
		generateAllStrings(maxLen, 0,true);
		strings.add("");
	}
	
	private String[] getBaseCase(int degree){
		String[] baseCase = new String[degree];
		for(int i=0; i<degree; i++)
			baseCase[i]=""+i;
		return baseCase;
	}
	
	public String[] generateAllStrings(int maxLen, int depth, boolean first){
		
		id.setValue(depth);
		int degree = (int)engine.evaluate(f_degree);
		
		if(maxLen==0)return getBaseCase(degree);
		
		String[] pre = generateAllStrings(maxLen-1, depth+1, false);
		String[] r = null;
		if(!first)r = new String[pre.length*degree];
		
		if(pre.length==0)return getBaseCase(degree);
		
		for(int i=0; i<pre.length; i++){
			if(!first){
				for(int d=0; d<degree; d++)
					r[i*degree+d]=d+""+pre[i];
				
			}
			strings.add(pre[i]);
		}
		
		return r;
	}
}
