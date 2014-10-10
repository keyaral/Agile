package swen302.gui.graphlayouts;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import swen302.graph.Edge;
import swen302.graph.Graph;
import swen302.graph.GraphListener;
import swen302.graph.Node;
import swen302.graph.PetriTransitionNode;
import swen302.gui.VertexGraphPane;

public class EadesSpringEmbedder {

	public Graph graph;
	public Graphics graphics;
	public static double MAGNETIC_STRENGTH = 8987551787.3681764;
	public static double SPRING_STRENGTH = -1.6;
	public static double SPRING_LENGTH = 100;
	private boolean mouseForce;
	private double mouseX;
	private double mouseY;
	private boolean mouseAttractive;
	private Node selectedNode = null;
	private VertexGraphPane graphPane;


	public AffineTransform afm;
	public double scale;
	public double scaleX,scaleY;

	public double WALL_CHARGE = 5;

	Node draggingNode;

	private int width, height;

	private GraphListener graphListener = new GraphListener() {
		@Override
		public void onNodeAdded(Node n) {
			n.randPosition(new Random());
			n.mass = 1.0f;
			graph.onLabelsChanged(graphics);
		}
	};

	public EadesSpringEmbedder(Graph graph, int width, int height, Graphics g, VertexGraphPane gp){
		synchronized(graph) {
			this.graph = graph;
			graphics = g;
			this.width = width;
			this.height = height;
			graph.generateInitialLayout(800, 600, g);
			graph.addListener(graphListener);
			graphPane = gp;
			scale = 1;
			afm = new AffineTransform();
		}
	}

	public void step(double timeStep, int pixelMouseX, int pixelMouseY){

		synchronized(graph) {

			{
				Point2D mouseGraphSpaceCoords = transformMouseCoords(new Point2D.Double(pixelMouseX, pixelMouseY));
				this.mouseX = mouseGraphSpaceCoords.getX();
				this.mouseY = mouseGraphSpaceCoords.getY();
			}

			Set<Node> virtualNodes = new HashSet<Node>(graph.nodes);

			if(false) // This is broken and makes graphs spin endlessly
			for (Edge e : graph.edges) {

				Vector2D vecResult = e.node1.getPosition().subtract(e.node2.getPosition()); //The vector between the two nodes

				if(vecResult.getNorm() == 0) { continue; }

				Vector2D vPos = e.node2.getPosition().add(vecResult.scalarMultiply(0.5));

				virtualNodes.add(new Node(vPos, true));
			}

			for (Node n : graph.nodes) {
				Vector2D tempForce = new Vector2D(0.0, 0.0);

				for (Edge e : n.getSprings()) {
					tempForce = tempForce.add(hookesLaw(n, e.getOtherNode(n)).scalarMultiply(1.0/n.getSprings().size()));
				}

				for (Node m : virtualNodes) {
					if (n != m) {
						tempForce = tempForce.add(coulombsLaw(n, m));

						if (!m.IsVirtual) {

							if (m.labelBounds == null) { continue; }

							double x = m.getPosition().getX();
							double y = m.getPosition().getY() + 10 - m.labelBounds.getHeight()/2;

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
				n.updatePosition(timeStep);
				//Stop calculating, will probably leave out.
				//This is so the graph can be made interactive
			}

			mouseForce = false;

		}
	}

	private Node pointInNode(double x, double y){

		synchronized(graph) {
			Vector2D mousePt = new Vector2D(x, y);
			for(Node n : graph.nodes) {
				double xPos = n.getPosition().getX();
				double yPos = n.getPosition().getY();

				Vector2D nCenter = new Vector2D(xPos, yPos);

				if (nCenter.distance(mousePt) < 10)
					return n;
			}
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

		if(distance < 10) distance = 10; // avoid excessive forces when nodes overlap

		double force = MAGNETIC_STRENGTH/(Math.pow(distance, 2));
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

		double length = SPRING_LENGTH;

		Vector2D vecResult = n1.getPosition().subtract(n2.getPosition()); //The vector between the two nodes
		if(vecResult.getNorm() < 0.0000000001)
			return Vector2D.ZERO;

		double distance = vecResult.getNorm();
		if(distance > 1000)
			distance = 1000;

		vecResult = vecResult.normalize().scalarMultiply(distance - length);

		vecResult = vecResult.scalarMultiply(SPRING_STRENGTH);

		return vecResult;

	}

	public void draw(Graphics2D graphics) {

		synchronized(graph) {
			graphics.setTransform(afm);

			//calc bounding box of graph
			double minX = Integer.MAX_VALUE;
			double maxX = Integer.MIN_VALUE;
			double minY = Integer.MAX_VALUE;
			double maxY = Integer.MIN_VALUE;

			for(Node n : graph.nodes){
				double xPos = n.getPosition().getX();
				double yPos = n.getPosition().getY();
				minX = Math.min(minX, xPos);
				maxX = Math.max(maxX, xPos);
				minY = Math.min(minY, yPos);
				maxY = Math.max(maxY, yPos);
			}

			double centerX = (maxX + minX)/2;
			double centerY = (maxY + minY)/2;

			double diffX = this.width/2 - centerX;
			double diffY = this.height/2 -centerY;

			for(Node n : graph.nodes){
				n.setPosition(n.getPosition().add(new Vector2D(diffX, diffY).add(graphPane.getPosDiff())));
			}




			if(selectedNode!=null){
				selectedNode.setPosition(new Vector2D(mouseX, mouseY));
			}




			graphics.setColor(Color.BLACK);

			for (Node n : graph.nodes) {

				for (Edge cn : n.getConnections()) {

					Path2D.Double path = new Path2D.Double();
					path.moveTo(cn.node1.getPosition().getX(), cn.node1
							.getPosition().getY());

					int scalecount = cn.duplicateCount + 1;

					Vector2D v1 = cn.node1.getPosition();
					Vector2D v2 = v1;
					Vector2D v4 = cn.node2.getPosition();
					Vector2D v3 = v4;

					Vector2D labelPos = null;

					if (cn.node1.equals(cn.node2)) {

						Vector2D lineMid = v1.add(new Vector2D(20 * scalecount, 0));

						Vector2D perp = new Vector2D(0, -15 * scalecount);

						Vector2D p1v2 = v1.add(perp);
						Vector2D p1v3 = lineMid.add(perp);

						Vector2D p2v2 = v1.subtract(perp);
						Vector2D p2v3 = lineMid.subtract(perp);

						path.curveTo(p1v2.getX(), p1v2.getY(), p1v3.getX(),
								p1v3.getY(), lineMid.getX(), lineMid.getY());

						path.curveTo(p2v3.getX(), p2v3.getY(), p2v2.getX(),
								p2v2.getY(), v1.getX(), v1.getY());

						labelPos = v1.add(new Vector2D(20 * scalecount, 0));
					} else {
						Vector2D diff = v1.subtract(v4);

						Vector2D perp = new Vector2D(diff.getY() * -1,
								diff.getX());

						perp = perp.normalize();
						perp = perp.scalarMultiply(scalecount * 20);

						v2 = v1.add(perp);
						v3 = cn.node2.getPosition().add(perp);

						path.curveTo(v2.getX(), v2.getY(), v3.getX(),
								v3.getY(), v4.getX(), v4.getY());

						double t = 0.45;

						labelPos = v1
								.scalarMultiply((1 - t) * (1 - t) * (1 - t))
								.add(v2.scalarMultiply((1 - t) * (1 - t) * t
										* 3))
								.add(v3.scalarMultiply((1 - t) * t * t * 3))
								.add(v4.scalarMultiply(t * t * t));

						double intersectionT = findBezierCircleIntersection(v1,v2,v3,v4, cn.node2.getPosition(), 10);
						cn.arrowPt = evalBezierCurve(v1,v2,v3,v4, intersectionT);
						cn.arrowAngle = evalBezierCurveDirection(v1,v2,v3,v4, intersectionT).scalarMultiply(-1);
					}

					graphics.drawString(String.valueOf(cn.label),
							(int) labelPos.getX(), (int) labelPos.getY());

					graphics.draw(path);

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

				Shape nodeShape;
				if(n instanceof PetriTransitionNode)
					nodeShape = new Rectangle2D.Double(xPos-10, yPos-10, 20, 20);
				else
					nodeShape = new Ellipse2D.Double(xPos-10, yPos-10, 20, 20);

				graphics.fill(nodeShape);
				graphics.setColor(Color.BLACK);
				graphics.draw(nodeShape);

				Rectangle2D stringBounds = n.labelBounds;

				graphics.setColor(new Color(200, 240, 240, 100));
				graphics.fill(new Rectangle2D.Double(xPos  - n.labelBounds.getWidth()/2, yPos-20, stringBounds.getWidth(), stringBounds.getHeight()));

				graphics.setColor(Color.black);
				graphics.drawString(n.getLabel(), (float)(xPos - (n.labelBounds.getWidth()/2)), (float)(yPos-10));

				int[] arrowXPoints = new int[] {0, 5, 5};
				int[] arrowYPoints = new int[] {0, -5, 5};

				//Draw the arrows on the edges
				for (Edge e : n.getConnections()) {

					Vector2D v = e.arrowAngle;
					if(v == null) continue;
					double angle = Math.atan2(v.getY(), v.getX());

					Vector2D arrowPt = e.arrowPt.add(e.arrowAngle.scalarMultiply(0));

					AffineTransform oldTransform = graphics.getTransform();
					graphics.translate(arrowPt.getX(), arrowPt.getY());
					graphics.rotate(angle);

					graphics.fillPolygon(arrowXPoints, arrowYPoints, 3);

					graphics.setTransform(oldTransform);
				}

			}

			Node npm = this.pointInNode(mouseX, mouseY);
			npm = selectedNode != null ? selectedNode : npm;
			if (npm != null) {
				Rectangle2D stringBounds = npm.labelBounds;

				graphics.setColor(new Color(100, 215, 215));
				graphics.fillRect((int)(npm.getPosition().getX() - npm.labelBounds.getWidth()/2), (int)npm.getPosition().getY()-22,
						(int)stringBounds.getWidth()+4, (int)stringBounds.getHeight()+4);
				graphics.setColor(Color.black);
				graphics.drawString(npm.getLabel(), (int)(npm.getPosition().getX() - npm.labelBounds.getWidth()/2), (int)npm.getPosition().getY()-8);
				graphics.drawRect((int)(npm.getPosition().getX() - npm.labelBounds.getWidth()/2), (int)npm.getPosition().getY()-22,
						(int)stringBounds.getWidth()+4, (int)stringBounds.getHeight()+4);
			}
		}
	}

	private static Vector2D evalBezierCurve(Vector2D v1, Vector2D v2, Vector2D v3, Vector2D v4, double t) {
		return v1.scalarMultiply((1 - t) * (1 - t) * (1 - t))
				.add(v2.scalarMultiply((1 - t) * (1 - t) * t * 3))
				.add(v3.scalarMultiply((1 - t) * t * t * 3))
				.add(v4.scalarMultiply(t * t * t));
	}
	private static Vector2D evalBezierCurveDirection(Vector2D v1, Vector2D v2, Vector2D v3, Vector2D v4, double t) {
		return evalBezierCurve(v1,v2,v3,v4, t+0.001).subtract(evalBezierCurve(v1,v2,v3,v4, t-0.001)).normalize();
	}
	private static double findBezierCircleIntersection(Vector2D v1, Vector2D v2, Vector2D v3, Vector2D v4, Vector2D centre, double radius) {
		// binary search
		double maxT = 1, minT = 0;
		while(maxT - minT > 0.001) {
			double midT = (maxT + minT) * 0.5;
			double midRadius = evalBezierCurve(v1, v2, v3, v4, midT).distance(centre);
			if(midRadius > radius)
				minT = midT;
			else
				maxT = midT;
		}
		return (minT + maxT) * 0.5;
	}

	public void selectNode(int mouseX, int mouseY){
		Point2D mouseGraphSpaceCoords = transformMouseCoords(new Point2D.Double(mouseX, mouseY));
		selectedNode = pointInNode(mouseGraphSpaceCoords.getX(), mouseGraphSpaceCoords.getY());
	}

	public void releaseNode(){
		selectedNode = null;
	}

	public boolean moveNode(int pMouseX, int pMouseY, int mouseX, int mouseY){
		Point2D mouseGraphSpaceCoords = transformMouseCoords(new Point2D.Double(mouseX, mouseY));
		if(selectedNode != null){
			selectedNode.setPosition(new Vector2D(mouseGraphSpaceCoords.getX(), mouseGraphSpaceCoords.getY()));
			return true;
		}
		return false;
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	/**
	 * @param mouse The mouse coordinates, in pixels.
	 * @return The mouse coordinates in "graph space" - i.e. before the <var>afm</var> transform is applied.
	 */
	public Point2D transformMouseCoords(Point2D mouse) {
		try {
			return afm.inverseTransform(mouse, new Point2D.Double());
		} catch(NoninvertibleTransformException ex) {
			// transform shouldn't be noninvertible; something went wrong, so reset it
			afm = new AffineTransform();
			return mouse;
		}
	}
}
