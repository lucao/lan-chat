package br.com.lucasvmteixeira.chat.components;

import java.util.Collection;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import br.com.lucasvmteixeira.chat.Atualizavel;
import br.com.lucasvmteixeira.chat.entity.Usuario;

public class JListObservable extends JList<Usuario> implements Atualizavel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3648186686515496324L;
		
	public JListObservable() {
		super();
		this.setCellRenderer(new CustomCellRendererUsuario());
	}

	@Override
	public synchronized void atualizar(Object o) {
		try {
			@SuppressWarnings("unchecked")
			Collection<Usuario> usuarios = (Collection<Usuario>) o;

			DefaultListModel<Usuario> defaultModel = new DefaultListModel<Usuario>();
			for (Usuario usuario : usuarios) {
				defaultModel.addElement(usuario);
			}
			this.setModel(defaultModel);
		} catch (ClassCastException e) {
			return;
		}
	}

}
