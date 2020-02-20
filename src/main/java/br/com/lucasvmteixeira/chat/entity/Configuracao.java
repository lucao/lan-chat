package br.com.lucasvmteixeira.chat.entity;

import java.io.Serializable;

public class Configuracao implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4523969415023951955L;
	
	private Usuario sender;
	private Mensagem mensagemLida;
	

	public Usuario getSender() {
		return sender;
	}

	public void setSender(Usuario sender) {
		this.sender = sender;
	}

	public Mensagem getMensagemLida() {
		return mensagemLida;
	}

	public void setMensagemLida(Mensagem mensagemLida) {
		this.mensagemLida = mensagemLida;
	}
}
