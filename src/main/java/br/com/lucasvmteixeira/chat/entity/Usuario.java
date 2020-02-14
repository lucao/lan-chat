package br.com.lucasvmteixeira.chat.entity;

import java.util.Collection;

public class Usuario {
	private String nome;
	private Collection<GrupoPrivado> gruposPrivados;
	
	private String canalConectado;
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public Collection<GrupoPrivado> getGruposPrivados() {
		return gruposPrivados;
	}
	public void setGruposPrivados(Collection<GrupoPrivado> gruposPrivados) {
		this.gruposPrivados = gruposPrivados;
	}
	public String getCanalConectado() {
		return canalConectado;
	}
	public void setCanalConectado(String canalConectado) {
		this.canalConectado = canalConectado;
	}
}