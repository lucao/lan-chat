package br.com.lucasvmteixeira.chat.components;

import java.util.Collection;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import br.com.lucasvmteixeira.chat.Atualizavel;
import br.com.lucasvmteixeira.chat.entity.Usuario;

public class JListObservable extends JList<String> implements Atualizavel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3648186686515496324L;

	@Override
	public void atualizar(Object o) {
		try {
			@SuppressWarnings("unchecked")
			Collection<Usuario> usuarios = (Collection<Usuario>) o;

			DefaultListModel<String> defaultModel = new DefaultListModel<String>();
			for (Usuario usuario : usuarios) {
				defaultModel.addElement(usuario.getNome());
			}
			this.setModel(defaultModel);
		} catch (ClassCastException e) {
			return;
		}
	}

}
