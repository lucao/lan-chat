package br.com.lucasvmteixeira.chat;

import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

import javax.swing.JFrame;

public class Chat {

	public static void main(String[] args) {
		// Create frame with title Registration Demo
		JFrame frame = new JFrame();
		frame.setTitle("Chat");

		// Add panel to frame
		frame.add(Interface.painelDeChat());
		frame.pack();
		frame.setSize(400, 400);
		frame.setLocationRelativeTo(null);
		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				//TODO
			}
		});
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	

}
