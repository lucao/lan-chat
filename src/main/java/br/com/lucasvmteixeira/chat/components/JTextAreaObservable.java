package br.com.lucasvmteixeira.chat.components;

import java.text.SimpleDateFormat;

import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import br.com.lucasvmteixeira.chat.Atualizavel;
import br.com.lucasvmteixeira.chat.entity.GrupoPrivado;
import br.com.lucasvmteixeira.chat.entity.Mensagem;

public class JTextAreaObservable extends JTextArea implements Atualizavel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7238100695416133973L;
	
	private GrupoPrivado grupoDaTextArea;

	public JTextAreaObservable() {
		super();
		DefaultCaret caret = (DefaultCaret) this.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		this.grupoDaTextArea = null;
		this.setEditable(false);
	}
	
	public JTextAreaObservable(GrupoPrivado grupo) {
		super();
		DefaultCaret caret = (DefaultCaret) this.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		this.grupoDaTextArea = grupo;
		this.setEditable(false);
	}

	@Override
	public synchronized void atualizar(Object o) {
		try {
			Mensagem mensagem = (Mensagem) o;
			if (this.grupoDaTextArea == null) {
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
				
			} else if (this.grupoDaTextArea.equals(mensagem.getGrupo())) {
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
			}
		} catch (ClassCastException e) {
			return;
		}
	}

}
