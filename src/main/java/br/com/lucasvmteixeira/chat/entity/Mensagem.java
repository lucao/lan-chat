package br.com.lucasvmteixeira.chat.entity;

import java.io.Serializable;
import java.util.Date;

public class Mensagem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9080246375777754459L;
	
	private Date dataDeEnvio;
	private Usuario sender;
	private GrupoPrivado grupo;
	private String mensagem;
	
	public Usuario getSender() {
		return sender;
	}
	public void setSender(Usuario sender) {
		this.sender = sender;
	}
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
	
}
