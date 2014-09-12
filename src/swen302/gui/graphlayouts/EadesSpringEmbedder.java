package swen302.gui.graphlayouts;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import swen302.graph.Edge;
import swen302.graph.Graph;
import swen302.graph.Node;

public class EadesSpringEmbedder {

	public Graph graph;
	public Graphics graphics;
	public double MAGNETIC_STRENGTH = 8987551787.3681764;
	public double SPRING_STRENGTH = -1.6;
	private boolean mouseForce;
	private int mouseX;
	private int mouseY;
	private boolean mouseAttractive;
	private Node selectedNode = null;

	public double WALL_CHARGE = 5;

	Node draggingNode;

	private int width, height;

	public EadesSpringEmbedder(Graph graph, int width, int height, Graphics g){
		this.graph = graph;
		graphics = g;
		this.width = width;
		this.height = height;
		graph.generateInitialLayout(800, 600, g);
	}

	public void step(double timeStep, int mouseX, int mouseY){

		this.mouseX = mouseX;
		this.mouseY = mouseY;

		Set<Node> virtualNodes = new HashSet<Node>(graph.nodes);

		for (Edge e : graph.edges) {

			Vector2D vecResult = e.node1.getPosition().subtract(e.node2.getPosition()); //The vector between the two nodes

			if(vecResult.getNorm() == 0) { continue; }

			Vector2D vPos = vecResult.normalize();

			double virtualPosition = e.node1.getPosition().distance(e.node2.getPosition()) / 2;

			vPos = vPos.scalarMultiply(virtualPosition);

			vPos = vecResult.subtract(vPos);

			virtualNodes.add(new Node(new Vector2D(vPos.getX(), vPos.getY()), true));
		}

		for (Node n : graph.nodes) {
			Vector2D tempForce = new Vector2D(0.0, 0.0);

			for (Edge e : n.getSprings()) {
				tempForce = tempForce.add(hookesLaw(n, e.getOtherNode(n)));
			}

			for (Node m : virtualNodes) {
				if (n != m) {
					tempForce = tempForce.add(coulombsLaw(n, m));

					if (!m.IsVirtual) {

						if (m.labelBounds == null) { continue; }

						double x = m.labelBounds.getCenterX();
						double y = m.labelBounds.getCenterY();

						Vector2D label = new Vector2D(x, y);
						tempForce = tempForce.add(coulombsLaw(n, new Node(label)));
					}
				}
			}

			//User interaction
			if (mouseForce) {
				Vector2D vecResult = n.getPosition().subtract(new Vector2D(mouseX, mouseY));
				double dist = n.getPosition().distance(new Vector2D(mouseX, mouseY));

				if(mouseAttractive)
					vecResult = vecResult.negate();

				vecResult = vecResult.scalarMultiply((1/dist*100));
				tempForce = tempForce.add(vecResult);
			}

			tempForce = tempForce.subtract(drag(n.getVelocity()));

			n.force = tempForce;
		}
		for (Node n : graph.nodes) {
			if(n == selectedNode) continue;
			n.updatePosition(timeStep);
			//Stop calculating, will probably leave out.
			//This is so the graph can be made interactive
		}

		mouseForce = false;
	}

	private Node pointInNode(int x, int y){

		for(Node n : graph.nodes) {
			double xPos = n.getPosition().getX();
			double yPos = n.getPosition().getY();

			Vector2D nCenter = new Vector2D(xPos, yPos);

			if (nCenter.distance(new Vector2D(x, y)) < 10)
				return n;
		}

		return null;
	}

	/*
	 * Needs to be redone / thought about in terms of vectors
	 *
	 */
	private Vector2D coulombsLaw(Node n1, Node n2) {
		double distance = n1.getPosition().distance(n2.getPosition());

		//double force = (this.MAGNETIC_STRENGTH*n1.getCharge()*n2.getCharge())/(Math.pow(distance, 2));

		double force = (this.MAGNETIC_STRENGTH*0.000625*0.000625)/(Math.pow(distance, 2));
		Vector2D vecResult = n1.getPosition().subtract(n2.getPosition());
		if (vecResult.getNorm() == 0) { return new Vector2D(0,0); }
		vecResult = vecResult.normalize();

		vecResult = vecResult.scalarMultiply(force);

		return vecResult;
	}

	private Vector2D drag(Vector2D velocity) {
		double force = 0.25*10*velocity.getNorm();
		if (force == 0)
			return new Vector2D(0.0, 0.0);

		return velocity.normalize().scalarMultiply(force);
	}

	private Vector2D hookesLaw(Node n1, Node n2) {
		if(n1 == n2)
			return Vector2D.ZERO;

		// F = -K(x-N)

		double length = 100.0;

		Vector2D vecResult = n1.getPosition().subtract(n2.getPosition()); //The vector between the two nodes
		Vector2D springLength = vecResult.normalize();

		springLength = springLength.scalarMultiply(length);
		vecResult = vecResult.subtract(springLength);

		vecResult = vecResult.scalarMultiply(SPRING_STRENGTH);

		return vecResult;

	}

	public void draw(Graphics2D graphics) {

		graphics.setColor(Color.BLACK);

		for (Node n : graph.nodes) {

			for (Edge cn : n.getConnections()){

				int scalecount = cn.duplicateCount+ 1;


				Vector2D v1 = cn.node1.getPosition();
				Vector2D v2 = v1;
				Vector2D v4 =  cn.node2.getPosition();
				Vector2D v3 =  v4;

				Vector2D perp = new Vector2D(v1.getY() * -1, v1.getX());
				perp = perp.normalize();
				perp = perp.scalarMultiply(scalecount * 10);

				if (cn.node1.getPosition().getX() > cn.node2.getPosition().getX() )
				{
					 v2 = v1.add(perp);
					 v3 =  cn.node2.getPosition().add(perp);
				}
				else {
					v2 = v1.subtract(perp);
					v3 =  cn.node2.getPosition().subtract(perp);
				}




				if ( cn.node1.equals(cn.node2) ){




				}






				Path2D.Double path = new Path2D.Double();
				path.moveTo(cn.node1.getPosition().getX(), cn.node1.getPosition().getY());

				path.curveTo(
						v2.getX(),
						v2.getY(),
						v3.getX(),
						v3.getY(),
						v4.getX(),
						v4.getY()
				);

				graphics.draw(path);

//				graphics.drawLine(
//						(int)cn.node1.getPosition().getX(),
//						(int)cn.node1.getPosition().getY(),
//						(int)cn.node2.getPosition().getX(),
//						(int)cn.node2.getPosition().getY()
//				);
			}
		}

		for (Node n : graph.nodes) {

			double xPos = n.getPosition().getX();
			double yPos = n.getPosition().getY();

			Vector2D nCenter = new Vector2D(xPos, yPos);
			if (nCenter.distance(new Vector2D(mouseX, mouseY)) < 10)
				graphics.setColor(Color.BLUE);
			else
				graphics.setColor(Color.LIGHT_GRAY);

			graphics.fillOval((int)n.getPosition().getX()-10, (int)n.getPosition().getY()-10, 20, 20);
			graphics.setColor(Color.BLACK);
			graphics.drawOval((int)n.getPosition().getX()-10, (int)n.getPosition().getY()-10, 20, 20);

			Rectangle2D stringBounds = n.labelBounds;

			graphics.setColor(new Color(200, 240, 240, 100));
			graphics.fillRect((int)(n.getPosition().getX()+10  - n.labelBounds.getWidth()/2), (int)n.getPosition().getY()-20,
					(int)stringBounds.getWidth(), (int)stringBounds.getHeight());
			graphics.setColor(Color.black);
			graphics.drawString(n.getLabel(), (int)(n.getPosition().getX()+10 - n.labelBounds.getWidth()/2), (int)n.getPosition().getY()-10);

			//Draw the arrows on the edges
			for (Edge e : n.getConnections()) {

				Vector2D node1 = n.getPosition();
				Vector2D node2 = e.getOtherNode(n).getPosition();

				Vector2D tipOfArrow = node1;
				Vector2D bottomOfArrow = node1;

				tipOfArrow = node1.subtract(node2);
				if (tipOfArrow.getNorm() == 0) { continue; }
				tipOfArrow = tipOfArrow.normalize().scalarMultiply(node1.distance(node2)-10);
				tipOfArrow = node1.subtract(tipOfArrow);

				bottomOfArrow = node1.subtract(node2);
				if (bottomOfArrow.getNorm() == 0) { continue; }
				bottomOfArrow = bottomOfArrow.normalize().scalarMultiply(node1.distance(node2)-20);
				bottomOfArrow = node1.subtract(bottomOfArrow);

				int[] xPoints = new int[] {0, -5, 5};
				int[] yPoints = new int[] {0, 5, 5};

				Vector2D v = node1.subtract(node2);
				double angle = Math.atan2(v.getY(), v.getX())-Math.PI/2;

				graphics.translate(tipOfArrow.getX(), tipOfArrow.getY());
				graphics.rotate(angle);

				graphics.fillPolygon(xPoints, yPoints, 3);

				graphics.rotate(-angle);
				graphics.translate(-tipOfArrow.getX(), -tipOfArrow.getY());
			}

		}
		//Lol variable name
		Node npm = this.pointInNode(mouseX, mouseY);
		npm = selectedNode != null ? selectedNode : npm;
		if (npm != null) {
			Rectangle2D stringBounds = npm.labelBounds;

			graphics.setColor(new Color(100, 215, 215));
			graphics.fillRect((int)(npm.getPosition().getX()+8 - npm.labelBounds.getWidth()/2), (int)npm.getPosition().getY()-22,
					(int)stringBounds.getWidth()+4, (int)stringBounds.getHeight()+4);
			graphics.setColor(Color.black);
			graphics.drawString(npm.getLabel(), (int)(npm.getPosition().getX()+8 - npm.labelBounds.getWidth()/2), (int)npm.getPosition().getY()-8);
			graphics.drawRect((int)(npm.getPosition().getX()+8 - npm.labelBounds.getWidth()/2), (int)npm.getPosition().getY()-22,
					(int)stringBounds.getWidth()+4, (int)stringBounds.getHeight()+4);
		}

	}

	public void selectNode(int mouseX, int mouseY){
		selectedNode = pointInNode(mouseX, mouseY);
	}

	public void releaseNode(){
		selectedNode = null;
	}

	public void moveNode(int pMouseX, int pMouseY, int mouseX, int mouseY){
		if(selectedNode != null){
			double x = selectedNode.getPosition().getX();
			double y = selectedNode.getPosition().getY();
			x += mouseX-pMouseX;
			y += mouseY-pMouseY;
			selectedNode.setPosition(new Vector2D(x, y));
		}
	}

	public void addForce(int mouseX, int mouseY, boolean mouseAttractive) {
		mouseForce= true;
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		this.mouseAttractive = mouseAttractive;
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

}
