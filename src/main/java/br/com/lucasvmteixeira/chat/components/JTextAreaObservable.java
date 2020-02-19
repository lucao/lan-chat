package br.com.lucasvmteixeira.chat.components;

import java.text.SimpleDateFormat;

import javax.swing.JTextArea;

import br.com.lucasvmteixeira.chat.Atualizavel;
import br.com.lucasvmteixeira.chat.entity.Mensagem;

public class JTextAreaObservable extends JTextArea implements Atualizavel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7238100695416133973L;

	@Override
	public void atualizar(Object o) {
		try {
			Mensagem mensagem = (Mensagem) o;
			StringBuilder builder = new StringBuilder();
			builder.append("Enviado por: ");
			builder.append(mensagem.getSender().getNome());
			builder.append(" em ");
			builder.append(new SimpleDateFormat("dd/MM/yyyy hh:mm").format(mensagem.getDataDeEnvio()));
			builder.append('\n');
			builder.append(mensagem.getMensagem());
			builder.append('\n');
			builder.append('\n');
			this.append(builder.toString());
		} catch (ClassCastException e) {
			return;
		}
	}

}
