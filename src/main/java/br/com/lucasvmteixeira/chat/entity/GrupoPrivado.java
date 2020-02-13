package br.com.lucasvmteixeira.chat.entity;

import java.util.Collection;

public class GrupoPrivado {
	private String nome;
	private Collection<Usuario> usuarios;
	
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
}
