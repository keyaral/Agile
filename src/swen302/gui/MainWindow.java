package swen302.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class MainWindow {

	private JFrame window;

	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenuItem fileLoadJAR, fileLoadAdvanced, fileLoadConfig, fileSaveConfig, fileExit;

	public MainWindow() {
		window = new JFrame("UltimaTracer 9000");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		menuBar = new JMenuBar();

		fileMenu = new JMenu("File");
		fileLoadJAR = fileMenu.add("Load JAR...");
		fileLoadAdvanced = fileMenu.add("Load Advanced...");
		fileMenu.addSeparator();
		fileLoadConfig = fileMenu.add("Load Config...");
		fileSaveConfig = fileMenu.add("Save Config...");
		fileMenu.addSeparator();
		fileExit = fileMenu.add("Exit");

		fileLoadJAR.setEnabled(false);
		fileLoadAdvanced.setEnabled(false);
		fileLoadConfig.setEnabled(false);
		fileSaveConfig.setEnabled(false);

		fileExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		menuBar.add(fileMenu);

		window.setLayout(new BorderLayout());
		window.add(menuBar, BorderLayout.NORTH);
		window.add(new JButton("Filters"), BorderLayout.WEST);
		window.add(new JButton("Graph"), BorderLayout.CENTER);

		window.pack();
		window.setLocationRelativeTo(null);
	}

	public void setVisible(boolean visible) {
		window.setVisible(visible);
	}
}
