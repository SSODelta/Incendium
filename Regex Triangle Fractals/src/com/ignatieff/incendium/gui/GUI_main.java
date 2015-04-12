package com.ignatieff.incendium.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;

import org.matheclipse.parser.client.eval.DoubleEvaluator;
import org.matheclipse.parser.client.eval.DoubleVariable;
import org.matheclipse.parser.client.eval.IDoubleValue;

import com.ignatieff.incendium.core.ColorList;
import com.ignatieff.incendium.core.Parser;
import com.ignatieff.incendium.core.Renderer;

public class GUI_main {
	
	private JFrame frame;
	private JPanel container;
	private JLabel content;
	
	private KeyListener listener, listener2;

	private DoubleEvaluator engine;
	private IDoubleValue id, it;
	private Animator a;
	private String status_text;
	private SwingWorker<Void, String> previewThread;
	
	private JProgressBar PROG_BAR;
	
	private JTextField	TEXT_REGEX,
						TEXT_ROTATION,
						TEXT_SIZE_DECAY,
						TEXT_POLYGON_DEGREE,
						TEXT_TOTAL_NO_FRAMES;
	private TextBox TEXT_FRACTAL_DEPTH;
	private JCheckBox 	BOX_GRADIENT;
	
	private ColorChooser cc;
						
	private int current_frame, max_frames;
	private Map<Integer, BufferedImage> FRAMES;

	private GridBagConstraints c;
	private String regex;
	private int depth;
	private double rot;
	private double decay;
	private String f_degree, output;
	private Timer t_progress;
	private boolean done, CONTROL, SHIFT;
	private static FileFilter 	filter_webm = new FileFilter() {
		public String getDescription() {
			return "WEBM Image Files (*.webm)";
		}
		
		public boolean accept(File f) {
			return f.getName().toLowerCase().endsWith(".pdf");
		}
	},							filter_properties = new FileFilter() {
		public String getDescription() {
			return "Incendium Configuration Files (*.inc)";
		}
		
		public boolean accept(File f) {
			return f.getName().toLowerCase().endsWith(".inc");
		}
	},							filter_imgs = new FileFilter() {
		public String getDescription() {
			return "Portable Network Graphics (*.png)";
		}
		
		public boolean accept(File f) {
			return f.getName().toLowerCase().endsWith(".png");
		}
	};
	
	private DocumentListener dl = new DocumentListener() {
		  public void changedUpdate(DocumentEvent e) {
			    warn();
			  }
			  public void removeUpdate(DocumentEvent e) {
			    warn();
			  }
			  public void insertUpdate(DocumentEvent e) {
			    warn();
			  }

			  public void warn() {
				  FRAMES.clear();
			  }
			};
	
	public GUI_main(){
		FRAMES = new HashMap<Integer, BufferedImage>();
		done = true;
		frame = new JFrame("Incendium - Fractal animation editor");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		status_text="Default";
		
		c = new GridBagConstraints();
		content = new JLabel(new ImageIcon(new BufferedImage(1,1,BufferedImage.TYPE_INT_RGB))){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			protected void paintChildren(Graphics g) {
				super.paintChildren(g);
				g.setColor(Color.white);
				g.drawString(status_text, 5, 15);
			};
		};
		
		cc = new ColorChooser(140,100,new ColorList(Color.RED, Color.YELLOW,9));
		
		container = new JPanel();
		a = null;
		
		t_progress = new Timer(100, new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(PROG_BAR==null)return;
				
				int prog_a = 100, prog_prev = 100;
				
				if(a!=null)prog_a=(int)(100*a.progress);
				if(previewThread!=null)prog_prev=previewThread.getProgress();
			
				PROG_BAR.setValue(Math.min(prog_a, prog_prev));
			}});

		
		t_progress.start();
		
		id = new DoubleVariable(0.0);
		it = new DoubleVariable(0.0);
		
		engine = new DoubleEvaluator();

		engine.defineVariable("t", it);
		engine.defineVariable("d", id);
		container.setSize(new Dimension(400,400));

		listener = new GeneralKeyListener();
		listener2 = new ArrowKeyListener();
		frame.addKeyListener(listener);
		frame.addKeyListener(listener2);
		frame.getContentPane().add(container);
		frame.setResizable(false);
		addComponents();
		current_frame = 0;
		content.setFocusable(true);
		content.addKeyListener(listener2);
		
		frame.getContentPane().setPreferredSize(new Dimension(675, 400));
		frame.pack();
		frame.setFocusable(true);
		frame.setVisible(true);
	}
	
	public void drawCurrentSetting(){
		drawCurrentSetting(false);
	}
	
	public FrameSettings getFrameSettings(){
		it.setValue(current_frame);
		regex = TEXT_REGEX.getText();
		depth = Integer.parseInt(TEXT_FRACTAL_DEPTH.getText());
		rot =  engine.evaluate(TEXT_ROTATION.getText());
		decay = engine.evaluate(TEXT_SIZE_DECAY.getText());
		f_degree = TEXT_POLYGON_DEGREE.getText();
		max_frames = Integer.parseInt(TEXT_TOTAL_NO_FRAMES.getText());
		
		changeColorSize(depth+1);
		
		return new FrameSettings(regex, cc.getColorList(),0,depth,rot,decay,f_degree);
	}
	
	public void drawCurrentSetting(boolean override){
		
		if(!done){return;}
		
		done=false;
		status_text = "Loading frame "+(current_frame+1)+"...";
		content.repaint();
		
		frame.requestFocus();
		
		FrameSettings fs = getFrameSettings();
		if(!override && FRAMES.containsKey(current_frame)){
			status_text = "Frame "+(current_frame+1);
			drawFrame(FRAMES.get(current_frame));
			done=true;
			return;
		}

		previewThread = new PreviewThread(fs);
		
		previewThread.execute();
		
	}
	
	public void drawFrame(BufferedImage img){
		content.setIcon(new ImageIcon(img));
		
		container.validate();
		c.gridx=0;
		c.gridy=0;
		c.weightx=0;
		c.weighty=1;
		c.gridheight=10;
		
		content.setVisible(true);
		container.add(content,c);
		
		frame.repaint();
		container.repaint();
	}
	
	public void saveConfiguration(){
		JFileChooser openFile = new JFileChooser();
        openFile.setFileFilter(filter_properties);
        int r = openFile.showSaveDialog(null);
        if(r!=JFileChooser.APPROVE_OPTION)return;
        output = openFile.getSelectedFile().getAbsolutePath();
        output = output.replace(".inc.inc", ".inc");
        if(!output.endsWith(".inc"))output = output+".inc";
        if(output==null)return;
        try {
			saveConfig(output);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	
	public void loadConfiguration(){
		JFileChooser openFile = new JFileChooser();
        openFile.setFileFilter(filter_properties);
        int r = openFile.showOpenDialog(null);
        if(r!=JFileChooser.APPROVE_OPTION)return;
        output = openFile.getSelectedFile().getAbsolutePath();
        output = output.replace(".inc.inc", ".inc");
        if(!output.endsWith(".inc"))output = output+".inc";
        if(output==null)return;
        try {
			loadConfig(output);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void saveConfig(String output) throws FileNotFoundException, IOException{
		Properties p = new Properties();
		p.setProperty("regex", TEXT_REGEX.getText());
		p.setProperty("rotation", TEXT_ROTATION.getText());
		p.setProperty("size_decay", TEXT_SIZE_DECAY.getText());
		p.setProperty("poly_degree", TEXT_POLYGON_DEGREE.getText());
		p.setProperty("fractal_depth", TEXT_FRACTAL_DEPTH.getText());
		p.setProperty("total_frames", TEXT_TOTAL_NO_FRAMES.getText());
		p.setProperty("color", cc.toString());
		
		p.store(new FileOutputStream(output), "Data-file for a regex fractal animation @"+System.currentTimeMillis());
	}
	
	public class GeneralKeyListener implements KeyListener {
		@Override
		public void keyTyped(KeyEvent e) {
			int keyCode = e.getKeyCode();
			switch(keyCode){
				case KeyEvent.VK_CONTROL:
					CONTROL=true;
					break;
				case KeyEvent.VK_SHIFT:
					SHIFT=true;
					break;
			}
		}

		@Override
		public void keyPressed(KeyEvent e) {
			int keyCode = e.getKeyCode();
			switch(keyCode){
				case KeyEvent.VK_CONTROL:
					CONTROL=true;
					break;
				case KeyEvent.VK_SHIFT:
					SHIFT=true;
					break;
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			int keyCode = e.getKeyCode();
			switch(keyCode){
			
				case KeyEvent.VK_S:
					if(CONTROL)
						saveConfiguration();
					break;

				case KeyEvent.VK_L:
					if(CONTROL)
						loadConfiguration();
					break;

				case KeyEvent.VK_O:
					if(CONTROL)
						loadConfiguration();
					break;
			
				case KeyEvent.VK_ENTER:
					if(TEXT_REGEX.getText().endsWith("8008135")){
						drawBuffer();
						break;
					}
					drawCurrentSetting();
					break;

				case KeyEvent.VK_TAB:
					TEXT_REGEX.requestFocus();
					break;
					
				case KeyEvent.VK_CONTROL:
					CONTROL = false;
					break;
					
				case KeyEvent.VK_SHIFT:
					SHIFT = false;
					break;

				case KeyEvent.VK_R:
					if(CONTROL){
						renderFrame();break;}
					if(SHIFT){
	                renderAnimation();break;}
					drawCurrentSetting(true);
					break;
			}
		}
	}
	
	public void drawBuffer(){
		try {
			BufferedImage img = ImageIO.read(new File("bin/dat/apx_regex.dln"));
			drawFrame(img);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public class ArrowKeyListener implements KeyListener {
		@Override
		public void keyTyped(KeyEvent e) {}

		@Override
		public void keyPressed(KeyEvent e) {}

		@Override
		public void keyReleased(KeyEvent e) {
			int keyCode = e.getKeyCode();
			switch(keyCode){
				case KeyEvent.VK_LEFT:
					if(current_frame>0)current_frame--;
					drawCurrentSetting();
					break;
				case KeyEvent.VK_RIGHT:
					if(current_frame<max_frames-1)current_frame++;
					drawCurrentSetting();
					break;
				case KeyEvent.VK_UP:
					current_frame=0;
					drawCurrentSetting();
					break;
				case KeyEvent.VK_DOWN:
					current_frame=max_frames-1;
					drawCurrentSetting();
					break;
				
					
				}
		}
	}
	
	private void renderFrame(){
        JFileChooser openFile = new JFileChooser();
        openFile.setFileFilter(filter_imgs);
        int r = openFile.showSaveDialog(null);
        if(r!=JFileChooser.APPROVE_OPTION)return;
        output = openFile.getSelectedFile().getAbsolutePath();
        if(!output.endsWith(".png"))output=output+".png";
        if(output==null)return;
        
        status_text = "Rendering...";
        
        FrameSettings fs = getFrameSettings();
        
        Parser p = new Parser(fs.f_degree, fs.depth, fs.regex);
        p.generateAllStrings();
        p.sort();
        
        BufferedImage img = Renderer.generateFractal(p, fs);
        String ext = output.substring(output.lastIndexOf(".")+1, output.length());
        try {
			ImageIO.write(img, ext, new File(output));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        status_text = "Done! Export frame "+(current_frame+1);
	}
	
	private void renderAnimation(){
        JFileChooser openFile = new JFileChooser();
        openFile.setFileFilter(filter_webm);
        int r = openFile.showSaveDialog(null);
        if(r!=JFileChooser.APPROVE_OPTION)return;
        output = openFile.getSelectedFile().getAbsolutePath();
        output = output.replace(".webm.webm", ".webm");
        if(!output.endsWith(".webm"))output = output+".webm";
        if(output==null)return;
        SwingWorker<Void, String> rt = new RenderThread(regex, TEXT_ROTATION.getText(), TEXT_SIZE_DECAY.getText(), f_degree, cc.getColorList(), depth);
        rt.execute();
	}
	
	public void loadConfig(String path) throws FileNotFoundException, IOException{
		Properties p = new Properties();
		p.load(new FileInputStream(path));
		TEXT_REGEX.setText(p.getProperty("regex"));
		TEXT_ROTATION.setText(p.getProperty("rotation"));
		TEXT_SIZE_DECAY.setText(p.getProperty("size_decay"));
		TEXT_POLYGON_DEGREE.setText(p.getProperty("poly_degree"));
		TEXT_FRACTAL_DEPTH.setText(p.getProperty("fractal_depth"));
		TEXT_TOTAL_NO_FRAMES.setText(p.getProperty("total_frames"));
		try {
			String s = p.getProperty("color");
			BOX_GRADIENT.setSelected(s.startsWith("1"));
			cc.setGradient(s.startsWith("1"));
			cc.colors = ColorChooser.getColorListFromString(s).get();
			if(cc.getGradient()){
				cc.makeGradient();
				cc.repaint();
			}
			cc.repaint();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public class RenderThread extends SwingWorker<Void, String> {
		
		private String _regex, _f_rot, _f_decay, _f_degree;
		private ColorList _cl;
		private int _depth;
		
		public RenderThread(String regex, String f_rot, String f_decay, String f_degree, ColorList cl, int depth){
			this._regex=regex;
			this._f_rot=f_rot;
			this._f_decay=f_decay;
			this._f_degree=f_degree;
			this._cl=cl;
			this._depth=depth;
		}

		@Override
		protected Void doInBackground() throws Exception {
			
			a = new Animator(_regex, _cl, 400, _depth, _f_rot, _f_decay, _f_degree);
			double frame_rotation = 0;// / (double) frames;
			
			a.renderAnimation("img", max_frames,0,max_frames, frame_rotation);
			a.convertToWEBM(output, max_frames);

			return null;
		}
		
		public void done(){
			status_text = "Done! Exported as "+output.substring(output.replace("\\", "/").lastIndexOf("/")+1, output.length());
		}
	}
	
	public class PreviewThread extends SwingWorker<Void, String>{
		
		private FrameSettings fs;
		private BufferedImage img;
		
		public PreviewThread(FrameSettings fs){
			this.fs = fs;
		}
		
		public BufferedImage getImage(){
			return img;
		}
		
		public void done(){
			drawFrame(img);
			FRAMES.put(current_frame, img);
			status_text="Frame "+(current_frame+1);
			done = true;
		}

		@Override
		protected Void doInBackground() throws Exception {
			
			this.setProgress(0);
			
			Parser p = new Parser(fs.f_degree, fs.depth,fs.regex);

			this.setProgress(5);

			p.generateAllStrings();

			this.setProgress(30);
			
			p.sort();

			this.setProgress(35);
			
			img = Renderer.generateFractal(p, fs);

			this.setProgress(100);
			
			return null;
		}

	}
	
	private void changeColorSize(int i){
		if(i<3)return;
		if(cc.getGradient()){
			Color first = cc.colors[0];
			Color last  = cc.colors[cc.colors.length-1];

			cc.colors = new ColorList(first,last,i).get();
			cc.num = i;
			cc.repaint();
		} else {
			Color[] newC = new Color[i];
			for(int q=0; q<i; q++){
				if(q<cc.colors.length){
					newC[q]=cc.colors[q];
					continue;
				}
				newC[q] = cc.colors[cc.colors.length-1];
			}
			cc.colors=newC;
			cc.num=i;
			cc.repaint();
		}
	}
	
	private void addComponents(){
		
		container.setLayout(new GridBagLayout());
		
		//Add drawing frame
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx=0;
		c.gridy=0;
		c.weightx=0.5;
		c.weighty=0.8;
		c.gridheight=12;
		
		content.setVisible(true);
		container.add(content,c);
		
		content.addKeyListener(listener);
		container.addKeyListener(listener);
		
		
		JLabel l;
		
		//Add labels
			//First label
			l = new JLabel("Regular expression");
			c.gridy=0;
			c.gridx=1;
			c.gridheight=1;
			c.insets = new Insets(0,10,0,0);
			container.add(l,c);

			//Second label
			l = new JLabel("Rotation (t)");
			c.gridy=1;
			container.add(l,c);

			//Third label
			l = new JLabel("Size decay (t)");
			c.gridy=2;
			container.add(l,c);

			//Fourth label
			l = new JLabel("Polygon degree (d)");
			c.gridy=3;
			container.add(l,c);

			//Fifth label
			l = new JLabel("Fractal depth");
			c.gridy=4;
			container.add(l,c);

			//Sixth label
			l = new JLabel("Total no. frames");
			c.gridy=5;
			container.add(l,c);

			//Seventh label
			l = new JLabel("Color");
			c.gridy=6;
			container.add(l,c);
		
		//Add textboxes
			//First textbox
			TEXT_REGEX = new JTextField(".*");
			c.gridy = 0;
			c.gridx = 2;
			container.add(TEXT_REGEX,c);
			TEXT_REGEX.getDocument().addDocumentListener(dl);
			TEXT_REGEX.addKeyListener(listener);
			
			//Second textbox
			TEXT_ROTATION = new JTextField("0");
			c.gridy = 1;
			container.add(TEXT_ROTATION,c);
			TEXT_ROTATION.getDocument().addDocumentListener(dl);
			TEXT_ROTATION.addKeyListener(listener);
			
			//Third textbox
			TEXT_SIZE_DECAY = new JTextField("0.5");
			c.gridy = 2;
			container.add(TEXT_SIZE_DECAY,c);
			TEXT_SIZE_DECAY.getDocument().addDocumentListener(dl);
			TEXT_SIZE_DECAY.addKeyListener(listener);
			
			//Fourth textbox
			TEXT_POLYGON_DEGREE = new JTextField("3");
			c.gridy = 3;
			container.add(TEXT_POLYGON_DEGREE,c);
			TEXT_POLYGON_DEGREE.getDocument().addDocumentListener(dl);
			TEXT_POLYGON_DEGREE.addKeyListener(listener);
		
			//Fifth textbox
			TEXT_FRACTAL_DEPTH = new TextBox("8");
			c.gridy = 4;
			container.add(TEXT_FRACTAL_DEPTH,c);
			TEXT_FRACTAL_DEPTH.getDocument().addDocumentListener(dl);
			TEXT_FRACTAL_DEPTH.addKeyListener(listener);
			
			//Sixth textbox
			TEXT_TOTAL_NO_FRAMES = new JTextField("100");
			c.gridy = 5;
			container.add(TEXT_TOTAL_NO_FRAMES,c);
			TEXT_TOTAL_NO_FRAMES.addKeyListener(listener);

		//ColorChooser
		c.gridx=1;
		int prev_ipady = c.ipady;
		double prev_weighty = c.weighty;
		c.weighty = 0.8;
		c.ipady = 10;
		c.gridy=7;
		cc.addKeyListener(listener);
		container.add(cc,c);
		c.ipady=prev_ipady;
		c.weighty=prev_weighty;

		BOX_GRADIENT = new JCheckBox("Gradient?");
		BOX_GRADIENT.setSelected(true);
		BOX_GRADIENT.addKeyListener(listener);
		BOX_GRADIENT.addItemListener(new ItemListener() {
		      public void itemStateChanged(ItemEvent e) {
		          cc.setGradient(BOX_GRADIENT.isSelected());
		          FRAMES.clear();
		        }
		      });
		c.gridx=2;
		BOX_GRADIENT.addKeyListener(listener);
		container.add(BOX_GRADIENT, c);
		
			
		//Buttons
		JButton btn;
			//First button
			btn = new JButton("Render animation");
			c.gridx=1;
			c.gridy=8;
			container.add(btn, c);
			btn.addKeyListener(listener);
			btn.addActionListener(new ActionListener() {

	            @Override
	            public void actionPerformed(ActionEvent arg0) {
	                renderAnimation();
	            }
	        });
			
			//First button
			btn = new JButton("Render frame");
			c.gridy=9;
			btn.addKeyListener(listener);
			container.add(btn, c);
			
			
			c.insets = new Insets(0,0,0,0);
			
			//First button
			btn = new JButton("Save project");
			c.gridx=2;
			c.gridy=8;
			container.add(btn, c);
			btn.addKeyListener(listener);
			btn.addActionListener(new ActionListener() {

	            @Override
	            public void actionPerformed(ActionEvent arg0) {
	                saveConfiguration();
	            }
	        });

			//First button
			btn = new JButton("Load project...");
			c.gridy=9;
			container.add(btn, c);
			btn.addKeyListener(listener);
			btn.addActionListener(new ActionListener() {

	            @Override
	            public void actionPerformed(ActionEvent arg0) {
	                loadConfiguration();
	            }
	        });
			
		PROG_BAR = new JProgressBar();
		PROG_BAR.setMaximum(100);
		PROG_BAR.setMinimum(0);
		PROG_BAR.addKeyListener(listener);
		c.gridx=0;
		c.gridwidth=3;
		c.gridy=10;
		container.add(PROG_BAR,c);
		
		c.gridwidth=1;

	}

}
