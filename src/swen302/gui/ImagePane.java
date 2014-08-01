package swen302.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import javax.swing.JPanel;

public class ImagePane extends JPanel {
	private static final long serialVersionUID = 1L;

	public Image image;

	{
		setBackground(Color.GRAY);
	}

	public void setImage(Image image) {
		this.image = image;
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if(image == null)
			return;

		int cw = getWidth(), ch = getHeight();
		int iw = image.getWidth(null), ih = image.getHeight(null);

		int dx1 = 0, dy1 = 0, dx2 = cw, dy2 = ch;

		if(iw < cw) {
			dx1 = (cw - iw) / 2;
			dx2 = dx1 + iw;
		}

		if(ih < ch) {
			dy1 = (ch - ih) / 2;
			dy2 = dy1 + ih;
		}

		((Graphics2D)g).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

		g.drawImage(image, dx1, dy1, dx2, dy2, 0, 0, iw, ih, null);
	}
}
