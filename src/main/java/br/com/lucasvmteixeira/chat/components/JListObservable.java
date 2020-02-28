package br.com.lucasvmteixeira.chat.components;

import java.util.Collection;
import java.util.Collections;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import br.com.lucasvmteixeira.chat.Atualizavel;
import br.com.lucasvmteixeira.chat.entity.Usuario;

public class JListObservable extends JList<Usuario> implements Atualizavel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3648186686515496324L;
	
	private Collection<Usuario> usuarios;
		
	@SuppressWarnings("unchecked")
	public JListObservable() {
		super();
		this.setCellRenderer(new CustomCellRendererUsuario());
		usuarios = Collections.EMPTY_LIST;
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized void atualizar(Object o) {
		try {
			this.usuarios = (Collection<Usuario>) o;
			DefaultListModel<Usuario> defaultModel = new DefaultListModel<Usuario>();
			for (Usuario usuario : usuarios) {
				defaultModel.addElement(usuario);
			}
			this.setModel(defaultModel);
		} catch (ClassCastException e) {
			return;
		}
	}

	public Collection<Usuario> getUsuarios() {
		return usuarios;
	}
}
