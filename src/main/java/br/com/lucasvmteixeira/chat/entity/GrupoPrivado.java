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
}
