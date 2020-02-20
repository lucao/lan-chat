package br.com.lucasvmteixeira.chat.components;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.swing.JTabbedPane;

import br.com.lucasvmteixeira.chat.Atualizavel;
import br.com.lucasvmteixeira.chat.Interface;
import br.com.lucasvmteixeira.chat.entity.GrupoPrivado;
import br.com.lucasvmteixeira.chat.entity.Usuario;

public class JTabbedPaneObservable extends JTabbedPane implements Atualizavel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1438233564553619947L;

	private Set<GrupoPrivado> tabsIdentifiers = new HashSet<GrupoPrivado>();

	@Override
	public synchronized void atualizar(Object o) {
		try {
			@SuppressWarnings("unchecked")
			Collection<Usuario> usuarios = (Collection<Usuario>) o;

			for (Usuario usuario : usuarios) {
				if (usuario.getGruposPrivados() != null) {
					for (GrupoPrivado grupoNaoCriado : usuario.getGruposPrivados().stream()
							.filter(Predicate.not(tabsIdentifiers::contains)).collect(Collectors.toList())) {
						synchronized (Interface.class) {
							Interface.construirPainelDeChatPrivado(grupoNaoCriado);
						}
					}
				}
			}
		} catch (ClassCastException e) {
			return;
		}
	}

}
