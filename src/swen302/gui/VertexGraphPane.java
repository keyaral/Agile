package swen302.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.Timer;

import swen302.graph.Graph;
import swen302.gui.graphlayouts.EadesSpringEmbedder;

public class VertexGraphPane extends JPanel {
	private static final long serialVersionUID = 1L;
	private Timer timer; 
	private boolean doAnimation;   // for starting and stoping animation
	public EadesSpringEmbedder graph;
	
	public VertexGraphPane(){
		super();
		
		doAnimation = true;
		
		timer = new Timer(10, new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent ae) {
		        graph.step(0.1);

		        repaint();
		    }
		});
	}
	
	public void setGraph(Graph graph) {
		
		if(graph != null){
			this.graph = new EadesSpringEmbedder(graph, getWidth(), getHeight());
			timer.start();
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;
		
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		if(graph == null)
			return;
		
		graph.draw(g2d);
	}
}
