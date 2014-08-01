package swen302.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager.LookAndFeelInfo;

public class GuiMain {
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {

				String systemLAF = UIManager.getSystemLookAndFeelClassName();

				//System.out.println("System look-and-feel class name: "+systemLAF);

				for(LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
					if(info.getName().equals("Nimbus"))
						systemLAF = info.getClassName();
					//System.out.println(info.getName()+": "+info.getClassName());
				}

				try {
					UIManager.setLookAndFeel(systemLAF);
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
					throw new RuntimeException(e);
				}

				new MainWindow().setVisible(true);
			}
		});
	}
}
