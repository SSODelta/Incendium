package com.ignatieff.tractals.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;

import javax.swing.JFrame;

public class GUI_project {
	
	private JFrame frame;
	private GridBagConstraints c;
	
	public GUI_project(){
		frame = new JFrame("Incendium - Project manager");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.getContentPane().setPreferredSize(new Dimension(200, 320));
		frame.pack();
		frame.setFocusable(true);
		frame.setVisible(true);

		c = new GridBagConstraints();
	}
	
	public void addComponents(){
		
	}
}
