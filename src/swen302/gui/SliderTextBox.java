package swen302.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class SliderTextBox extends JPanel {
	private static final long serialVersionUID = 1L;

	private double min, max, _default;
	private int default_int;

	private JSlider slider;
	private JTextField textbox;

	private double currentValue;

	public SliderTextBox(String title, double _min, double _max, double __default) {
		this.min = _min;
		this.max = _max;
		this._default = __default;

		default_int = (int)((_default - min) / (max - min) * 100000);

		slider = new JSlider(0, 100000, 0);
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if(slider.getValue() == default_int)
					setValue(_default, false);
				else
					setValue(slider.getValue() * (max - min) / 100000.0 + min, false);
			}
		});

		textbox = new JTextField("");
		setValue(_default, false);
		textbox.setInputVerifier(new InputVerifier() {
			@Override
			public boolean verify(JComponent input) {
				try {
					double d = Double.parseDouble(textbox.getText());
					if(Double.isInfinite(d) || Double.isNaN(d))
						throw new NumberFormatException();
				} catch(NumberFormatException e) {
					setValue(currentValue, false);
				}
				return true;
			}
		});
		textbox.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				update();
			}
			@Override
			public void insertUpdate(DocumentEvent e) {
				update();
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				update();
			}

			private void update() {
				try {
					setValue(Double.parseDouble(textbox.getText()), true);
				} catch(NumberFormatException e) {
				}
			}
		});

		JLabel label = new JLabel(title);

		GridBagConstraints gbc = new GridBagConstraints();
		setLayout(new GridBagLayout());

		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		add(label, gbc);
		gbc.gridy = 1;
		add(slider, gbc);
		gbc.gridy = 2;
		add(textbox, gbc);

		textbox.setHorizontalAlignment(JTextField.RIGHT);
	}

	@Override
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

	private boolean isChangingValue;
	public void setValue(double value, boolean dontUpdateTextbox) {
		if(isChangingValue)
			return;

		isChangingValue = true;
		try {
			currentValue = value;
			onChanged(value);
			slider.setValue((int)((value - min) / (max - min) * 100000));
			if(!dontUpdateTextbox)
				textbox.setText(String.format("%.3f", value));
		} finally {
			isChangingValue = false;
		}
	}

	public void onChanged(double value) {

	}
}
