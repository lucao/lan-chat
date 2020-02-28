package br.com.lucasvmteixeira.chat.components.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import br.com.lucasvmteixeira.chat.Interface;
import br.com.lucasvmteixeira.chat.entity.GrupoPrivado;
import br.com.lucasvmteixeira.chat.net.ChannelWrapper;

public class EnvioDeMensagemParaGrupo implements ActionListener {
	private ChannelWrapper channel;
	private GrupoPrivado grupo;

	public EnvioDeMensagemParaGrupo(ChannelWrapper channel, GrupoPrivado grupo) {
		this.channel = channel;
		this.grupo = grupo;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		synchronized (this.channel) {
			try {
				String textoDaMensagem = Interface.tabbedPaneForChats.mapaEntradaPrivado.get(this.grupo).getText();
				if (textoDaMensagem.length() > 0) {
					if (textoDaMensagem.length() <= 2048) {
						this.channel.send(textoDaMensagem, this.grupo);
					} else {
						int n = JOptionPane.showConfirmDialog(null,
								"Mensagem muito grande para ser enviada no chat. Deseja enviar a mensagem como um arquivo de texto?",
								"Erro ao enviar mensagem", JOptionPane.YES_NO_OPTION);
						if (n == JOptionPane.OK_CANCEL_OPTION) {
							// TODO
						}
					}
				}

				Interface.entrada.setText("");
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(null, "Erro ao tentar enviar a mensagem");
			}
		}
	}

}
