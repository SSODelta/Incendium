package com.ignatieff.incendium.gui;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class TextBox extends JTextField implements DocumentListener {

	private static final long serialVersionUID = -1827779163315163887L;

	public TextBox(String s){
		super(s);
		getDocument().addDocumentListener((DocumentListener)this);
	}
	
	public void change(){
		
	}
	
	@Override
	public void insertUpdate(DocumentEvent e) {
		change();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		change();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		change();
	}

}
