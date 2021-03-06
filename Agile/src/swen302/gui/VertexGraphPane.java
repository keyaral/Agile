package swen302.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JPanel;
import javax.swing.Timer;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import swen302.graph.Graph;
import swen302.graph.Node;
import swen302.gui.graphlayouts.EadesSpringEmbedder;

public class VertexGraphPane extends JPanel {
	private static final long serialVersionUID = 1L;
	private Timer timer;
	private int mouseX;
	private int mouseY;
	private double xDiff,yDiff;
	public EadesSpringEmbedder graph;
	//private MiniMap minimap;

	private List<GraphHoverListener> hoverListeners = new CopyOnWriteArrayList<>();
	public void addHoverListener(GraphHoverListener l) {hoverListeners.add(l);}
	private void fireHoverEvent(Node node) {
		for(GraphHoverListener l : hoverListeners)
			l.onMouseHover(node);
	}

	public VertexGraphPane(/*MiniMap m*/){
		super();
		xDiff = 0;
		yDiff = 0;
		//this.minimap = m;

		MouseAdapter ma = new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(graph != null)
					graph.releaseNode();
			}

			private Node hoverNode = null;

			private void checkHoverNode() {
				Node newHN = (graph == null ? null : graph.findNodeAtPoint(mouseX, mouseY));
				if(newHN != hoverNode) {
					hoverNode = newHN;
					fireHoverEvent(hoverNode);
				}
			}

			public void mousePressed(MouseEvent e){
				if(graph != null)
					graph.selectNode(e.getX(), e.getY());
				mouseX = e.getX();
				mouseY = e.getY();
				checkHoverNode();
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				if(graph != null){
					if(!graph.moveNode(mouseX, mouseY, e.getX(), e.getY())){
						xDiff -= (mouseX - e.getX())/graph.afm.getScaleX();
						yDiff -= (mouseY - e.getY())/graph.afm.getScaleY();
					}
				}
				mouseX = e.getX();
				mouseY = e.getY();
				checkHoverNode();
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				mouseX = e.getX();
				mouseY = e.getY();
				checkHoverNode();
			}
		};

		this.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if(graph != null){
					graph.scaleX = e.getX();
					graph.scaleY = e.getY();
					graph.scale += 0.1*e.getWheelRotation()*-1;

					Point2D.Double graphSpaceMouseCoords = new Point2D.Double();
					try {
						graph.afm.inverseTransform(new Point2D.Double(e.getX(), e.getY()), graphSpaceMouseCoords);
					} catch(NoninvertibleTransformException ex) {
						// transform shouldn't be noninvertible; something went wrong, so reset it
						graph.afm = new AffineTransform();
						graphSpaceMouseCoords.setLocation(e.getX(), e.getY());
					}

					graph.afm.translate(graphSpaceMouseCoords.getX(), graphSpaceMouseCoords.getY());
					if(e.getWheelRotation() == 1) {
						graph.afm.scale(0.9, 0.9);
					} else {
						graph.afm.scale(1.1, 1.1);
					}
					graph.afm.translate(-graphSpaceMouseCoords.getX(), -graphSpaceMouseCoords.getY());
				}
			}
		});

		this.addMouseListener(ma);
		this.addMouseMotionListener(ma);

		timer = new Timer(10, new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent ae) {
//		    	if (mouseDown) {
//		        	graph.addForce(mouseX, mouseY, mouseAttractive);
//		        }

		    	graph.step(0.1, mouseX, mouseY);

		        repaint();
		        //minimap.repaint();
		    }
		});

		addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {}

			@Override
			public void componentResized(ComponentEvent e) {
				if(graph != null){
					graph.setSize(getWidth(), getHeight());
				}
			}

			@Override
			public void componentMoved(ComponentEvent e) {}

			@Override
			public void componentHidden(ComponentEvent e) {}
		});
	}

	public void setGraph(Graph graph) {

		if(graph != null){
			this.graph = new EadesSpringEmbedder(graph, getWidth(), getHeight(), this.getGraphics(),this);
			xDiff = 0;
			yDiff = 0;
			//this.minimap.changeGraph(this.graph.graph);
			timer.start();
		}
	}

	public Vector2D getPosDiff(){
		return new Vector2D(xDiff, yDiff);
	}

	private boolean labelsChanged = false;

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());

		if(graph == null)
			return;

		if(labelsChanged)
			labelsChanged = false;

		graph.draw(g2d);
	}

	public void onLabelsChanged() {
		labelsChanged = true;
	}
}
