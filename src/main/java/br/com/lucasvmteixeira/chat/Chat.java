package br.com.lucasvmteixeira.chat;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.CompletableFuture;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jgroups.JChannel;

import br.com.lucasvmteixeira.chat.entity.Usuario;
import br.com.lucasvmteixeira.chat.net.ChannelWrapper;
import br.com.lucasvmteixeira.chat.net.RecebedorDeMensagens;

public class Chat {
	private static final ChannelWrapper canalPrincipal = new ChannelWrapper();

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setTitle("Chat");

		final JPanel painelDeAbertura = Interface.construirPainelDeAbertura();
		painelDeAbertura.setVisible(true);
		final JPanel painelDeAcompanhamentoDeProgressoIndefinido = Interface
				.construirPainelDeAcompanhamentoDeProgressoIndefinido();
		painelDeAcompanhamentoDeProgressoIndefinido.setVisible(false);

		final JPanel painelDeChatPrincipal = Interface.construirPainelDeChat();
		Interface.tabbedPaneForChats.add("Principal", painelDeChatPrincipal);
		Interface.tabbedPaneForChats.setVisible(false);

		final JPanel glassPanel = new JPanel();
		glassPanel.add(painelDeAbertura);
		glassPanel.add(painelDeAcompanhamentoDeProgressoIndefinido);
		glassPanel.add(Interface.tabbedPaneForChats);

		Interface.btnConectar.addActionListener((evt) -> {
			try {
				String nomeDoUsuario = Interface.nickname.getText();

				if (nomeDoUsuario == null) {
					throw new NomeDeUsuarioInvalido();
				}
				if (nomeDoUsuario.length() == 0) {
					throw new NomeDeUsuarioInvalido();
				}

				final Usuario usuario = new Usuario(nomeDoUsuario);

				final CompletableFuture<String> c1 = new CompletableFuture<>();

				painelDeAbertura.setVisible(false);
				painelDeAcompanhamentoDeProgressoIndefinido.setVisible(true);
				Interface.tabbedPaneForChats.setVisible(false);
				glassPanel.repaint();
				new Thread(() -> {
					synchronized (canalPrincipal) {
						try {
							JChannel channel = new JChannel("src/main/resources/udp.xml");
							RecebedorDeMensagens recebedorDeMensagens = new RecebedorDeMensagens(channel, usuario,
									Interface.saida, Interface.usuarios);
							canalPrincipal.connect(channel, usuario, recebedorDeMensagens);
							canalPrincipal.sendNovoUsuario(usuario);
						} catch (Exception e) {
							e.printStackTrace();
							c1.completeExceptionally(new ErroDeConexao());
						}
					}
					c1.complete("OK");
				}).start();

				c1.exceptionally(ex -> {
					JOptionPane.showMessageDialog(null, "Erro ao tentar conectar");
					return "erro";
				});
				c1.thenAccept(str -> {
					if (str == "OK") {
						painelDeAbertura.setVisible(false);
						painelDeAcompanhamentoDeProgressoIndefinido.setVisible(false);
						Interface.tabbedPaneForChats.setVisible(true);

						glassPanel.repaint();
					}
				});

			} catch (NomeDeUsuarioInvalido e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Nome inválido");
			}
		});

		Interface.btnEnviar.addActionListener((evt) -> {
			synchronized (canalPrincipal) {
				try {
					String textoDaMensagem = Interface.entrada.getText();
					if (textoDaMensagem.length() > 0) {
						if (textoDaMensagem.length() <= 2048) {
							canalPrincipal.send(textoDaMensagem);
						} else {
							int n = JOptionPane.showConfirmDialog(null,
									"Mensagem muito grande para ser enviada no chat. Deseja enviar a mensagem como um arquivo de texto?",
									"Erro ao enviar mensagem", JOptionPane.YES_NO_OPTION);
							if (n == JOptionPane.OK_CANCEL_OPTION) {
								//TODO
							}
						}
					}

					Interface.entrada.setText("");
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Erro ao tentar enviar a mensagem");
				}
			}
		});

		Interface.btnIniciarChat.addActionListener((evt) -> {

		});

		frame.getContentPane().add(glassPanel);
		frame.pack();
		frame.setSize(800, 600);
		frame.setLocationRelativeTo(null);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				canalPrincipal.close();
			}
		});
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
