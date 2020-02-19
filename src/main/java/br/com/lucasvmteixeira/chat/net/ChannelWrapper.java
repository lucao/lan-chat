package br.com.lucasvmteixeira.chat.net;

import java.io.Serializable;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;

import br.com.lucasvmteixeira.chat.RecebedorDeMensagens;
import br.com.lucasvmteixeira.chat.entity.Usuario;

public class ChannelWrapper {
	private JChannel channel;

	public ChannelWrapper connect(JChannel channel, Usuario usuario, RecebedorDeMensagens recebedorDeMensagens) {
		this.channel = channel;
		this.channel.setReceiver(recebedorDeMensagens);
		return this;
	}

	public void send(Message msg) throws Exception {
		channel.send(msg);
	}

	public void send(Address dst, Serializable obj) throws Exception {
		channel.send(dst, obj);
	}

	public void send(Address dst, byte[] buf) throws Exception {
		channel.send(dst, buf);
	}

	public void send(Address dst, byte[] buf, int off, int len) throws Exception {
		channel.send(dst, buf, off, len);
	}
}
