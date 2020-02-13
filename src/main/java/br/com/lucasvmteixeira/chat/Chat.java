package br.com.lucasvmteixeira.chat;

import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Chat {

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setTitle("Chat");
		
		final JPanel painelDeAbertura = Interface.construirPainelDeAbertura();
		painelDeAbertura.setVisible(true);
		final JPanel painelDeChat = Interface.construirPainelDeChat();
		painelDeChat.setVisible(false);
		final JPanel painelDeUsuarios = Interface.construirPainelDeUsuarios();
		painelDeUsuarios.setVisible(false);
		
		final JPanel glassPanel = new JPanel();
		glassPanel.add(painelDeAbertura);
		glassPanel.add(painelDeUsuarios);
		glassPanel.add(painelDeChat);				
		
		Interface.btnConectar.addActionListener((evt) -> {
			painelDeAbertura.setVisible(false);
			painelDeChat.setVisible(false);
			painelDeUsuarios.setVisible(true);
			
			glassPanel.repaint();
		});
		
		
		frame.getContentPane().add(glassPanel);
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
