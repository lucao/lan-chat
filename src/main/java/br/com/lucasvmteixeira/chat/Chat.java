package br.com.lucasvmteixeira.chat;

import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jgroups.JChannel;

import br.com.lucasvmteixeira.chat.entity.Usuario;
import br.com.lucasvmteixeira.chat.net.ChannelWrapper;

public class Chat {
	private static ChannelWrapper canalPrincipal = new ChannelWrapper();

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
		glassPanel.add(painelDeChat);
		glassPanel.add(painelDeUsuarios);
		
		Interface.btnConectar.addActionListener((evt) -> {
			try {
				String nomeDoUsuario = Interface.nickname.getText();
				
				if (nomeDoUsuario == null) {
					throw new NomeDeUsuarioInvalido();
				}
				if (nomeDoUsuario.length() == 0) {
					throw new NomeDeUsuarioInvalido();
				}
				
				Usuario usuario = new Usuario(nomeDoUsuario);
				synchronized (canalPrincipal) {
					try {
						canalPrincipal.connect(new JChannel("src/main/resources/udp.xml"), usuario);
					} catch (Exception e) {
						throw new ErroDeConexao();
					}
				}
				
				painelDeAbertura.setVisible(false);
				painelDeUsuarios.setVisible(false);
				painelDeChat.setVisible(true);			
				
				glassPanel.repaint();
			} catch (NomeDeUsuarioInvalido e) {
				JOptionPane.showMessageDialog(null, "Nome inválido");
			} catch (ErroDeConexao e) {
				JOptionPane.showMessageDialog(null, "Erro ao tentar conectar");
			}
		});
		
		Interface.btnIniciarChat.addActionListener((evt) -> {
			painelDeAbertura.setVisible(false);
			painelDeUsuarios.setVisible(false);
			painelDeChat.setVisible(true);
			
			glassPanel.repaint();
		});
		
		frame.getContentPane().add(glassPanel);
		frame.pack();
		frame.setSize(600, 600);
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
