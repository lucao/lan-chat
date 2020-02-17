package br.com.lucasvmteixeira.chat.persistence;

import java.util.Map;
import java.util.Set;

import org.jgroups.Address;

import br.com.lucasvmteixeira.chat.entity.Usuario;

public class Usuarios {
	private Map<Address, Usuario> usuariosConectados;
	private Set<Address> usuariosConectadosSemIdentificacao;
	
	public Map<Address, Usuario> getUsuariosConectados() {
		return usuariosConectados;
	}
	public void setUsuariosConectados(Map<Address, Usuario> usuariosConectados) {
		this.usuariosConectados = usuariosConectados;
	}
	public Set<Address> getUsuariosConectadosSemIdentificacao() {
		return usuariosConectadosSemIdentificacao;
	}
	public void setUsuariosConectadosSemIdentificacao(Set<Address> usuariosConectadosSemIdentificacao) {
		this.usuariosConectadosSemIdentificacao = usuariosConectadosSemIdentificacao;
	}
}
