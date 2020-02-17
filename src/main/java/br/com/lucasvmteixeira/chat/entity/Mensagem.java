package br.com.lucasvmteixeira.chat.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import org.jgroups.Address;

public class Mensagem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9080246375777754459L;
	
	private Date dataDeEnvio;
	private Address identificadorDoRemetente;
	
	private GrupoPrivado grupo;
	private String mensagem;
	
	private Set<Usuario> lidoPor;
	private Usuario sender;
	
	public GrupoPrivado getGrupo() {
		return grupo;
	}
	public void setGrupo(GrupoPrivado grupo) {
		this.grupo = grupo;
	}
	public String getMensagem() {
		return mensagem;
	}
	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
	public Date getDataDeEnvio() {
		return dataDeEnvio;
	}
	public void setDataDeEnvio(Date dataDeEnvio) {
		this.dataDeEnvio = dataDeEnvio;
	}
	public Set<Usuario> getLidoPor() {
		return lidoPor;
	}
	public void setLidoPor(Set<Usuario> lidoPor) {
		this.lidoPor = lidoPor;
	}
	public Usuario getSender() {
		return sender;
	}

	public void setSender(Usuario sender) {
		this.sender = sender;
	}
	public Address getIdentificadorDoRemetente() {
		return identificadorDoRemetente;
	}
	public void setIdentificadorDoRemetente(Address identificadorDoRemetente) {
		this.identificadorDoRemetente = identificadorDoRemetente;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dataDeEnvio == null) ? 0 : dataDeEnvio.hashCode());
		result = prime * result + ((identificadorDoRemetente == null) ? 0 : identificadorDoRemetente.hashCode());
		result = prime * result + ((mensagem == null) ? 0 : mensagem.hashCode());
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
		Mensagem other = (Mensagem) obj;
		if (dataDeEnvio == null) {
			if (other.dataDeEnvio != null)
				return false;
		} else if (!dataDeEnvio.equals(other.dataDeEnvio))
			return false;
		if (identificadorDoRemetente == null) {
			if (other.identificadorDoRemetente != null)
				return false;
		} else if (identificadorDoRemetente.compareTo(other.identificadorDoRemetente) != 0)
			return false;
		if (mensagem == null) {
			if (other.mensagem != null)
				return false;
		} else if (!mensagem.equals(other.mensagem))
			return false;
		return true;
	}
}
