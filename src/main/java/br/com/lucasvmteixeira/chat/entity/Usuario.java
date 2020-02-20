package br.com.lucasvmteixeira.chat.entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.jgroups.Address;

public class Usuario implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6747522460337961540L;

	public static final String canalPrincipal = "channel";
	
	private String nome;
	private Collection<GrupoPrivado> gruposPrivados;
	private transient List<Mensagem> conversas;
	
	private transient Address enderecoConectado;
	
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
	public List<Mensagem> getConversas() {
		return conversas;
	}
	public void setConversas(List<Mensagem> conversas) {
		this.conversas = conversas;
	}
	public Address getEnderecoConectado() {
		return enderecoConectado;
	}
	public void setEnderecoConectado(Address enderecoConectado) {
		this.enderecoConectado = enderecoConectado;
	}
}
