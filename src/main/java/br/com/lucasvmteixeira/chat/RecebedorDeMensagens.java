package br.com.lucasvmteixeira.chat;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

import br.com.lucasvmteixeira.chat.entity.Configuracao;
import br.com.lucasvmteixeira.chat.entity.Mensagem;
import br.com.lucasvmteixeira.chat.entity.Usuario;
import br.com.lucasvmteixeira.chat.persistence.Mensagens;
import br.com.lucasvmteixeira.chat.persistence.Usuarios;

public class RecebedorDeMensagens extends ReceiverAdapter {
	private View lastView;
	private Usuarios usuarios;
	private Mensagens mensagens;

	private JChannel channel;
	private final Usuario usuarioConectado;

	public RecebedorDeMensagens(JChannel channel, Usuario usuarioConectado, Atualizavel... observers) {
		super();
		this.usuarios = new Usuarios();
		this.mensagens = new Mensagens();
		this.channel = channel;
		this.usuarioConectado = usuarioConectado;

		for (Atualizavel o : observers) {
			this.mensagens.addObserver(o);
		}
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
				mensagens.ler(mensagem, mensagem.getSender());
			}

			Configuracao configuracao = new Configuracao();
			configuracao.setSender(this.usuarioConectado);
			configuracao.setMensagemLida(mensagem);

			try {
				channel.send(null, configuracao);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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

			for (Mensagem mensagem : mensagens.enviadasPor(this.usuarioConectado).stream()
					.filter(Predicate.not(mensagens.naoLidasPor(configuracao.getSender())::contains))
					.collect(Collectors.toList())) {
				try {
					channel.send(sender, mensagem);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
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
		// TODO atualizar mensagens
		lastView = view;
	}
}
