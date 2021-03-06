package br.com.lucasvmteixeira.chat.net;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

import br.com.lucasvmteixeira.chat.Atualizavel;
import br.com.lucasvmteixeira.chat.UsuarioNaoConectado;
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
			this.usuarios.addObserver(o);
		}
	}

	@Override
	public void receive(Message msg) {
		Address sender = msg.getSrc();

		try {
			Mensagem mensagem = (Mensagem) msg.getObject();
			
			mensagem.getSender().setEnderecoConectado(sender);
			synchronized (this.usuarios) {
				if (this.usuarios.containsUsuarioSemIdentificacao(sender)) {
					if (!this.usuarios.containsUsuario(sender)) {
						this.usuarios.putUsuarioConectado(sender, mensagem.getSender());
					}
				}
			}
			
			synchronized (mensagens) {
				mensagens.add(mensagem);
				mensagens.ler(mensagem, mensagem.getSender());
			}

			if (mensagem.getGrupo() != null) {
				try {
					this.usuarios.updateGrupoDoUsuario(usuarioConectado.getEnderecoConectado(), mensagem.getGrupo());
				} catch (UsuarioNaoConectado e) {
					e.printStackTrace();
				}
			}

		} catch (ClassCastException e) {
			Configuracao configuracao = (Configuracao) msg.getObject();
			
			configuracao.getSender().setEnderecoConectado(sender);
			synchronized (this.usuarios) {
				if (configuracao.isPedidoDeAtualizacao()) {
					this.viewAccepted(channel.getView());
				}
				if (this.usuarios.containsUsuarioSemIdentificacao(sender)) {
					if (!this.usuarios.containsUsuario(sender)) {
						this.usuarios.putUsuarioConectado(sender, configuracao.getSender());

						Configuracao c = new Configuracao();
						c.setSender(this.usuarioConectado);
						try {
							channel.send(sender, c);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}

				if (configuracao.getGrupoPrivado() != null) {
					try {
						this.usuarios.updateGrupoDoUsuario(usuarioConectado.getEnderecoConectado(),
								configuracao.getGrupoPrivado());
					} catch (UsuarioNaoConectado e1) {
						e1.printStackTrace();
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
					e1.printStackTrace();
				}
			}
		}
	}

	@Override
	public void viewAccepted(View view) {
		if (lastView == null) {
			synchronized (this.usuarios) {
				this.usuarios.addUsuariosSemIdentificacao(view.getMembers());
				if (this.usuarios.containsUsuarioSemIdentificacao(this.channel.getAddress())) {
					if (!this.usuarios.containsUsuario(this.channel.getAddress())) {
						this.usuarioConectado.setEnderecoConectado(this.channel.getAddress());
						this.usuarios.putUsuarioConectado(this.channel.getAddress(), this.usuarioConectado);
					}
				}
			}
		} else {
			List<Address> newMembers = View.newMembers(lastView, view);
			List<Address> exMembers = View.leftMembers(lastView, view);
			synchronized (this.usuarios) {
				this.usuarios.addUsuariosSemIdentificacao(newMembers);
				this.usuarios.removeUsuarios(exMembers);
			}
		}

		lastView = view;
	}
}
