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

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import swen302.graph.Graph;
import swen302.gui.graphlayouts.EadesSpringEmbedder;

public class VertexGraphPane extends JPanel {
	private static final long serialVersionUID = 1L;
	private Timer timer;
	private boolean doAnimation;   // for starting and stoping animation
	private boolean mouseDown;
	private int mouseX;
	private int mouseY;
	private boolean mouseAttractive;
	public EadesSpringEmbedder graph;

	public VertexGraphPane(){
		super();

		doAnimation = true;
		mouseDown = false;

		MouseAdapter ma = new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				mouseDown = false;
				graph.releaseNode();
			}

			public void mousePressed(MouseEvent e){
				mouseDown = true;
				graph.selectNode(e.getX(), e.getY());
				mouseX = e.getX();
				mouseY = e.getY();
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				mouseAttractive = SwingUtilities.isLeftMouseButton(e);
				mouseDown = true;
				graph.moveNode(mouseX, mouseY, e.getX(), e.getY());
				mouseX = e.getX();
				mouseY = e.getY();
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				mouseDown = false;
				mouseX = e.getX();
				mouseY = e.getY();
			}
		};

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
			this.graph = new EadesSpringEmbedder(graph, getWidth(), getHeight(), this.getGraphics());
			timer.start();
		}
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

		if(labelsChanged) {
			graph.graph.onLabelsChanged(g2d);
			labelsChanged = false;
		}

		graph.draw(g2d);
	}

	public void onLabelsChanged() {
		labelsChanged = true;
	}
}
