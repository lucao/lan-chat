package br.com.lucasvmteixeira.chat;

import java.util.List;

import org.jgroups.Address;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

import br.com.lucasvmteixeira.chat.entity.Configuracao;
import br.com.lucasvmteixeira.chat.entity.Mensagem;
import br.com.lucasvmteixeira.chat.net.ChannelWrapper;
import br.com.lucasvmteixeira.chat.persistence.Mensagens;
import br.com.lucasvmteixeira.chat.persistence.Usuarios;

public class RecebedorDeMensagens extends ReceiverAdapter {
	private View lastView;
	private Usuarios usuarios;
	private Mensagens mensagens;
	
	private ChannelWrapper channel;

	public RecebedorDeMensagens(ChannelWrapper channel) {
		super();
		this.usuarios = new Usuarios();
		this.mensagens = new Mensagens();
		this.channel = channel;
	}

	@Override
	public void receive(Message msg) {
		Address sender = msg.getSrc();
		try {
			Mensagem mensagem = (Mensagem) msg.getObject();
			synchronized (this.usuarios) {
				if (this.usuarios.getUsuariosConectadosSemIdentificacao().contains(sender)) {
					this.usuarios.getUsuariosConectadosSemIdentificacao().remove(sender);
	
					if (!this.usuarios.getUsuariosConectados().containsKey(sender)) {
						this.usuarios.getUsuariosConectados().put(sender, mensagem.getSender());
					}
				}
			}
			synchronized (mensagens) {
				mensagens.add(mensagem);
			}
		} catch (ClassCastException e) {
			Configuracao configuracao = (Configuracao) msg.getObject();
			synchronized (this.usuarios) {
				if (this.usuarios.getUsuariosConectadosSemIdentificacao().contains(sender)) {
					this.usuarios.getUsuariosConectadosSemIdentificacao().remove(sender);
	
					if (!this.usuarios.getUsuariosConectados().containsKey(sender)) {
						this.usuarios.getUsuariosConectados().put(sender, configuracao.getSender());
					}
				}
			}
			
			synchronized (mensagens) {
				mensagens.ler(configuracao.getMensagemLida(), configuracao.getSender());
			}
			
			for (Mensagem mensagem: mensagens.naoLidasPor(configuracao.getSender())) {
				try {
					channel.send(sender, mensagem);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	@Override
	public void viewAccepted(View view) {
		if (lastView == null) {
			synchronized (this.usuarios) {
				this.usuarios.getUsuariosConectadosSemIdentificacao().addAll(view.getMembers());
			}
		} else {
			List<Address> newMembers = View.newMembers(lastView, view);
			List<Address> exMembers = View.leftMembers(lastView, view);
			synchronized (this.usuarios) {
				this.usuarios.getUsuariosConectadosSemIdentificacao().addAll(newMembers);
				this.usuarios.getUsuariosConectados().keySet().removeAll(exMembers);
			}
		}
		//TODO atualizar mensagens
		lastView = view;
	}
}
