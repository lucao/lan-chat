package br.com.lucasvmteixeira.chat.entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public class GrupoPrivado implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3898530148223626334L;
	
	private String nome;
	private Collection<Usuario> usuarios;
	private Usuario usuarioCriador;
	private transient List<Mensagem> mensagens;
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public Collection<Usuario> getUsuarios() {
		return usuarios;
	}
	public void setUsuarios(Collection<Usuario> usuarios) {
		this.usuarios = usuarios;
	}
	public List<Mensagem> getMensagens() {
		return mensagens;
	}
	public void setMensagens(List<Mensagem> mensagens) {
		this.mensagens = mensagens;
	}
	public Usuario getUsuarioCriador() {
		return usuarioCriador;
	}
	public void setUsuarioCriador(Usuario usuarioCriador) {
		this.usuarioCriador = usuarioCriador;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
		result = prime * result + ((usuarioCriador == null) ? 0 : usuarioCriador.hashCode());
		result = prime * result + ((usuarios == null) ? 0 : usuarios.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GrupoPrivado other = (GrupoPrivado) obj;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		if (usuarioCriador == null) {
			if (other.usuarioCriador != null)
				return false;
		} else if (!usuarioCriador.equals(other.usuarioCriador))
			return false;
		if (usuarios == null) {
			if (other.usuarios != null)
				return false;
		} else if (!usuarios.equals(other.usuarios))
			return false;
		return true;
	}
}
