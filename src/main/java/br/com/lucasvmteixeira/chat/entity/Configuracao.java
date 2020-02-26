package br.com.lucasvmteixeira.chat.entity;

import java.io.Serializable;

public class Configuracao implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4523969415023951955L;
	
	private Usuario sender;
	private Mensagem mensagemLida;
	private GrupoPrivado grupoPrivado;
	private boolean pedidoDeAtualizacao;

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
	
	public GrupoPrivado getGrupoPrivado() {
		return grupoPrivado;
	}

	public void setGrupoPrivado(GrupoPrivado grupoPrivado) {
		this.grupoPrivado = grupoPrivado;
	}
	
	@Override
	public String toString() {
		return "Configuracao [sender=" + sender + ", mensagemLida=" + mensagemLida + "]";
	}

	public boolean isPedidoDeAtualizacao() {
		return pedidoDeAtualizacao;
	}

	public void setPedidoDeAtualizacao(boolean pedidoDeAtualizacao) {
		this.pedidoDeAtualizacao = pedidoDeAtualizacao;
	}	
}
