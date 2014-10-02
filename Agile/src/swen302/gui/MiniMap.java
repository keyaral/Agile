package swen302.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import swen302.graph.Edge;
import swen302.graph.Graph;
import swen302.graph.Node;

public class MiniMap extends JPanel {

	private Graph graph;

	public void changeGraph(Graph g){
		this.graph = g;
	}

	@Override
	public void paint(Graphics g){

		Graphics2D g2d = (Graphics2D)g;
		int w = this.getWidth();
		int h = this.getHeight();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, w, h);

		if(graph != null){
			synchronized(graph) {
				int minX = Integer.MAX_VALUE;
				int maxX = Integer.MIN_VALUE;
				int minY = Integer.MAX_VALUE;
				int maxY = Integer.MIN_VALUE;

				for(Node n : graph.nodes){
					minX = (int) Math.min(n.getPosition().getX(), minX);
					maxX = (int) Math.max(n.getPosition().getX(), maxX);
					minY = (int) Math.min(n.getPosition().getY(), minY);
					maxY = (int) Math.max(n.getPosition().getY(), maxY);
				}
				//g2d.translate(-minX, -minY);

				int graphWidth = maxX - minX;
				int graphHeight = maxY - minY;

				double xScale = (double)this.getWidth() / (double)(graphWidth-10);
				double yScale = (double)this.getHeight() / (double)(graphHeight-10);

				double usedScale = Math.min(xScale,yScale);

				//g2d.scale(usedScale, usedScale);


				g2d.setColor(Color.BLACK);
				for(Edge e : graph.edges){
					g2d.drawLine((int)(e.node1.getPosition().getX()), (int)(e.node1.getPosition().getY()), (int)(e.node2.getPosition().getX()), (int)(e.node2.getPosition().getY()));
				}
			}
		}
	}

}
