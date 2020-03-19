package br.com.lucasvmteixeira.chat.components;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
	
	private List<Mensagem> mensagens;
	private Mensagem ultimaMensagem;

	private GrupoPrivado grupoDaTextArea;
	//TODO implementar lógica de lista de mensagens, para exibir na ordem em que foram enviadas
	
	public JTextAreaObservable() {
		super();
		this.mensagens = new ArrayList<Mensagem>();
		DefaultCaret caret = (DefaultCaret) this.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		this.grupoDaTextArea = null;
		this.setEditable(false);
	}

	public JTextAreaObservable(GrupoPrivado grupo) {
		super();
		this.mensagens = new ArrayList<Mensagem>();
		DefaultCaret caret = (DefaultCaret) this.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		this.grupoDaTextArea = grupo;
		this.setEditable(false);
	}

	@Override
	public synchronized void atualizar(Object o) {
		try {
			Mensagem mensagem = (Mensagem) o;
			
			if (mensagem.getDataDeEnvio().before(ultimaMensagem.getDataDeEnvio())) {
				this.setText(null);
				Collections.sort(mensagens, (mensagem1, mensagem2) -> {
					//TODO
					return 0;
				});
			} else {
				publicarMensagem(mensagem);
				ultimaMensagem = mensagem;
			}
			
			mensagens.add(mensagem);
		} catch (ClassCastException e) {
			return;
		}
	}

	private void publicarMensagem(Mensagem mensagem) {
		if (this.grupoDaTextArea == null && mensagem.getGrupo() == null) {
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

		} else {
			if (this.grupoDaTextArea != null) {
				if (this.grupoDaTextArea.equals(mensagem.getGrupo())) {
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
			}
		}
	}

}
