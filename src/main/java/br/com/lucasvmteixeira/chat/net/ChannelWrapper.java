package br.com.lucasvmteixeira.chat.net;

import java.util.Date;

import org.jgroups.JChannel;

import br.com.lucasvmteixeira.chat.entity.Mensagem;
import br.com.lucasvmteixeira.chat.entity.Usuario;

public class ChannelWrapper {
	private JChannel channel;
	private Usuario usuario;

	public ChannelWrapper connect(JChannel channel, Usuario usuario, RecebedorDeMensagens recebedorDeMensagens) {
		this.channel = channel;
		this.channel.setReceiver(recebedorDeMensagens);
		this.usuario = usuario;
		return this;
	}
	
	public void send(String mensagem) throws Exception {
		Mensagem m = new Mensagem();
		m.setDataDeEnvio(new Date());
		m.setSender(this.usuario);
		m.setMensagem(mensagem);
		channel.send(null, m);
	}
}
