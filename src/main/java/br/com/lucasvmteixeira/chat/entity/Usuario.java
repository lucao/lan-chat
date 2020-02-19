package br.com.lucasvmteixeira.chat.entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public class Usuario implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6747522460337961540L;

	public static final String canalPrincipal = "channel";
	
	private String nome;
	private transient Collection<GrupoPrivado> gruposPrivados;
	private transient List<Mensagem> conversas;
	
	private String canalConectado;
	
	public Usuario(String nomeDoUsuario) {
		super();
		canalConectado = canalPrincipal;
		this.nome = nomeDoUsuario;
	}
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
	public List<Mensagem> getConversas() {
		return conversas;
	}
	public void setConversas(List<Mensagem> conversas) {
		this.conversas = conversas;
	}
}
