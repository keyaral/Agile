package swen302.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import swen302.gui.graphlayouts.EadesSpringEmbedder;
import swen302.vertexgraph.VertexGraph;

public class VertexGraphPane extends JPanel {
	private static final long serialVersionUID = 1L;

	public EadesSpringEmbedder graph;

	public void setGraph(VertexGraph graph) {
		
		this.graph = new EadesSpringEmbedder(graph, getWidth(), getHeight());

		repaint();
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
