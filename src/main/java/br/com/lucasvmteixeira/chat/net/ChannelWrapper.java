package br.com.lucasvmteixeira.chat.net;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jgroups.JChannel;

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
		this.usuario = usuario;
		return this;
	}

	public void send(String mensagem) throws Exception {
		Mensagem m = new Mensagem();
		m.setDataDeEnvio(new Date());
		m.setSender(this.usuario);
		m.setMensagem(mensagem);
		
		//TODO controle assíncrono de envio
		this.channel.send(null, m);
	}

	public void sendNovoUsuario(Usuario usuario) throws Exception {
		Configuracao c = new Configuracao();
		c.setSender(usuario);
		
		//TODO controle assíncrono de envio
		this.channel.send(null, c);
	}

	public void close() {
		this.channel.close();
	}

	public void criarNovoGrupo(List<Usuario> list) throws Exception {
		GrupoPrivado grupo = new GrupoPrivado();
		grupo.setUsuarios(list);
		if (usuario.getGruposPrivados() == null) {
			usuario.setGruposPrivados(new ArrayList<GrupoPrivado>());
		}
		usuario.getGruposPrivados().add(grupo);
		
		for (Usuario usuario: grupo.getUsuarios()) {
			Configuracao c = new Configuracao();
			c.setSender(usuario);
			this.channel.send(usuario.getEnderecoConectado(), c);
		}
	}
}
