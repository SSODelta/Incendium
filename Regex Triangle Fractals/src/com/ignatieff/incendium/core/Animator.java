package com.ignatieff.incendium.core;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.Executor;
import org.matheclipse.parser.client.eval.DoubleEvaluator;
import org.matheclipse.parser.client.eval.DoubleVariable;
import org.matheclipse.parser.client.eval.IDoubleValue;

public class Animator {
	
	private DoubleEvaluator engine;
	private String f_rot, f_decay, f_degree, regex;
	private IDoubleValue it;
	private ColorList cl;
	private int size, depth;
	private Parser p;
	public double progress;
	
	public Animator(String regex, ColorList cl, int size, int depth, String f_rot, String f_decay, String f_degree){
		
		System.out.println("Created new Animator\nregex="+regex+"\nsize="+size+"\ndepth="+depth+"\nf_rot="+f_rot+"\nf_decay="+f_decay+"\nf_degree="+f_degree);
		
		it = new DoubleVariable(0.0);
		progress=0.0;
		
		engine = new DoubleEvaluator();
		engine.defineVariable("t", it);

		this.regex    = regex;
		this.f_rot    = f_rot;
		this.f_decay  = f_decay;
		this.f_degree = f_degree;
		this.cl       = cl;
		this.size     = size;
		this.depth    = depth;
		
		
		p = new Parser(f_degree, depth, regex);
		
		p.generateAllStrings();
		
		p.sort();
	}
	
	
	/**
	 * Generates a Fractal-animation based on regular expression.
	 * For more information on the technicals aspects of this, check out my blog at:
	 * 
	 * @param regex The regular expression to use for generating the Fractal.
	 * @param output The string of the output file path.
	 * @param cl The ColorList-object to use for coloring the triangles.
	 * @param frames The number of frames in this animation.
	 * @param revolutions The number of full revolution the frame itself turns during the course of this animation.
	 * @param depth The fractal depth (layers of recursion).
	 * @param f_rot The string for the function that specifies the rotation as function of 't'.
	 * @param f_decay The string for the function that specifies the size decay as function of 't'.
	 */
	public static void generateAndSaveWEBM(String regex, String output, ColorList cl, int frames, double revolutions, int depth, String f_rot, String f_decay, String f_degree){
		Animator a = new Animator(regex, cl, frames, depth, f_rot, f_decay, f_degree);
		System.out.println("Rendering animation...\n"
						 + "|------------------------------|-----------------------------|");
		System.out.println(" 0%                           50%                        100%");
		System.out.print("|");
		
		double frame_rotation = revolutions / (double) frames;
		
		a.renderAnimation("img", frames,0,frames, frame_rotation);
		a.convertToWEBM(output, frames);
	}
	
	
	public void renderAnimation(String output, int frames){
		renderAnimation(output, frames, 0, frames, 0);
	}
	
	public void renderAnimation(String output, int frames, double t_start, double t_end, double spin){
		
		int len = 5;//(int)Math.ceil(Math.log10(frames));
		
		int mod = frames / 59;
		
		double dt = (t_end - t_start) / (double) frames;
		for(int i=0; i<frames; i++){
			progress = (double) i / (2.0*frames);
			if(i%mod==0)System.out.print("-");
			double t = t_start + i*dt;
			BufferedImage img = renderImage(p,t, spin*i);
			writeImage(img, "tmp/"+output+pad(i,len)+".png");
		}
		System.out.print("|\n");
	}
	
	private String pad(int input, int len){
		String k = ""+input;
		while(k.length()<len)
			k="0"+k;
		return k;
	}
	
	private void writeImage(BufferedImage img, String output){
		try {
			ImageIO.write(img, "png", new File(output));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private BufferedImage renderImage(Parser p, double t, double spin){
		it.setValue(t);
		double rot = engine.evaluate(f_rot);
		double dec = engine.evaluate(f_decay);
		
		return Renderer.generateFractal(p, regex, cl, size, spin, rot, dec, depth, f_degree);
	}
	
	public void convertToWEBM(String output, int frames){
		try {
			
			Executor exec = new DefaultExecutor();
			exec.getStreamHandler().stop();
			CommandLine cl = new CommandLine("ext/encode.bat");
			cl.addArgument(""+(frames-1));
			cl.addArgument(output);
			exec.execute(cl);
			progress=1.0;
			
		} catch (ExecuteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
