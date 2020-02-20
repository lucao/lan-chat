package br.com.lucasvmteixeira.chat.components;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import br.com.lucasvmteixeira.chat.entity.Usuario;

public class CustomCellRendererUsuario implements ListCellRenderer<Usuario> {
	protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

	@Override
	public Component getListCellRendererComponent(JList<? extends Usuario> jlist, Usuario usuario, int index,
			boolean isSelected, boolean cellHasFocus) {
		JLabel label = (JLabel) defaultRenderer.getListCellRendererComponent(jlist, usuario, index, isSelected,
				cellHasFocus);

		if (isSelected) {
			label.setBackground(jlist.getSelectionBackground());
			label.setForeground(jlist.getSelectionForeground());
		} else {
			label.setBackground(jlist.getBackground());
			label.setForeground(jlist.getForeground());
		}
		label.setFont(jlist.getFont());

		label.setEnabled(jlist.isEnabled());

		label.setText(usuario.getNome());

		return label;
	}
}