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

		final JPanel glassPanel = new JPanel();
		glassPanel.add(painelDeAbertura);
		glassPanel.add(painelDeChat);

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
						JChannel channel = new JChannel("src/main/resources/udp.xml");
						RecebedorDeMensagens recebedorDeMensagens = new RecebedorDeMensagens(channel, usuario,
								Interface.saida, Interface.usuarios);
						canalPrincipal.connect(channel, usuario, recebedorDeMensagens);
					} catch (Exception e) {
						throw new ErroDeConexao();
					}
				}

				painelDeAbertura.setVisible(false);
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
			painelDeChat.setVisible(true);

			glassPanel.repaint();
		});

		frame.getContentPane().add(glassPanel);
		frame.pack();
		frame.setSize(800, 600);
		frame.setLocationRelativeTo(null);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				// TODO
			}
		});
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
