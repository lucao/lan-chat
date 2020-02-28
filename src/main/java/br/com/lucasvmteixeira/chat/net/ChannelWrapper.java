package br.com.lucasvmteixeira.chat.net;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jgroups.JChannel;

import br.com.lucasvmteixeira.chat.GrupoVazio;
import br.com.lucasvmteixeira.chat.entity.Configuracao;
import br.com.lucasvmteixeira.chat.entity.GrupoPrivado;
import br.com.lucasvmteixeira.chat.entity.Mensagem;
import br.com.lucasvmteixeira.chat.entity.Usuario;

public class ChannelWrapper {
	private JChannel channel;
	private Usuario usuario;

	public ChannelWrapper connect(JChannel channel, Usuario usuario, RecebedorDeMensagens recebedorDeMensagens)
			throws Exception {
		this.channel = channel;
		this.channel.setReceiver(recebedorDeMensagens);
		this.channel.setName(usuario.getNome());
		this.channel.connect(Usuario.canalPrincipal);
		channel.getState(null, 0);
		this.usuario = usuario;
		return this;
	}

	public void send(String mensagem) throws Exception {
		Mensagem m = new Mensagem();
		m.setDataDeEnvio(new Date());
		m.setSender(this.usuario);
		m.setMensagem(mensagem);

		// TODO controle assíncrono de envio
		this.channel.send(null, m);
	}
	
	public void send(String mensagem, GrupoPrivado grupo) throws Exception {
		Mensagem m = new Mensagem();
		m.setDataDeEnvio(new Date());
		m.setSender(this.usuario);
		m.setMensagem(mensagem);
		m.setGrupo(grupo);

		// TODO controle assíncrono de envio
		for (Usuario usuario: grupo.getUsuarios()) {
			this.channel.send(usuario.getEnderecoConectado(), m);
		}
	}

	public void sendNovoUsuario(Usuario usuario) throws Exception {
		Configuracao c = new Configuracao();
		c.setSender(usuario);

		this.channel.send(null, c);
	}

	public void close() {
		if (this.channel != null) {
			this.channel.close();
		}
	}

	public void criarNovoGrupo(Usuario usuarioCriador, String nome, Collection<Usuario> usuarios) throws Exception {
		GrupoPrivado grupo = new GrupoPrivado();
		grupo.setNome(nome);
		grupo.setUsuarioCriador(usuarioCriador);
		if (usuarios != null) {
			grupo.setUsuarios(usuarios.stream().collect(Collectors.toSet()));
			grupo.getUsuarios().add(usuario);
		} else {
			throw new GrupoVazio();
		}

		Configuracao c = new Configuracao();
		c.setSender(usuarioCriador);
		c.setGrupoPrivado(grupo);
		for (Usuario usuario : grupo.getUsuarios()) {
			this.channel.send(usuario.getEnderecoConectado(), c);
		}
	}

	public void atualizar() throws Exception {
		Configuracao c = new Configuracao();
		c.setSender(usuario);
		c.setPedidoDeAtualizacao(true);

		this.channel.send(null, c);
	}

	public boolean isConnected() {
		return channel.isConnected();
	}
}
