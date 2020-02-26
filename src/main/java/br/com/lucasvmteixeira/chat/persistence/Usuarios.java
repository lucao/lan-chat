package br.com.lucasvmteixeira.chat.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgroups.Address;

import br.com.lucasvmteixeira.chat.Atualizavel;
import br.com.lucasvmteixeira.chat.entity.GrupoPrivado;
import br.com.lucasvmteixeira.chat.entity.Usuario;

public class Usuarios {
	private Map<Address, Usuario> usuariosConectados;
	private Set<Address> usuariosConectadosSemIdentificacao;

	private List<Atualizavel> observables;

	public Usuarios() {
		this.observables = new ArrayList<Atualizavel>();
		this.usuariosConectados = new HashMap<Address, Usuario>();
		this.usuariosConectadosSemIdentificacao = new HashSet<Address>();
	}

	public void addObserver(Atualizavel o) {
		this.observables.add(o);
	}

	public void putUsuarioConectado(Address sender, Usuario usuarioSender) {
		this.usuariosConectados.put(sender, usuarioSender);
		this.usuariosConectadosSemIdentificacao.remove(sender);
		for (Atualizavel o : this.observables) {
			o.atualizar(this.usuariosConectados.values());
		}
	}

	public boolean containsUsuario(Address sender) {
		return this.usuariosConectados.containsKey(sender);
	}

	public boolean containsUsuarioSemIdentificacao(Address sender) {
		return this.usuariosConectadosSemIdentificacao.contains(sender);
	}

	public void addUsuariosSemIdentificacao(List<Address> members) {
		this.usuariosConectadosSemIdentificacao.addAll(members);
	}

	public void removeUsuarios(List<Address> members) {
		this.usuariosConectadosSemIdentificacao.removeAll(members);
		this.usuariosConectados.keySet().removeAll(members);
		for (Atualizavel o : this.observables) {
			o.atualizar(this.usuariosConectados.values());
		}
	}

	public void updateGrupoDoUsuario(Address member, GrupoPrivado grupo) {
		Usuario usuario = this.usuariosConectados.get(member);

		if (usuario.getGruposPrivados() == null) {
			usuario.setGruposPrivados(new HashSet<GrupoPrivado>());
		}
		if (grupo.getUsuarios().contains(usuario)) {
			if (usuario.getGruposPrivados().add(grupo)) {
				for (Atualizavel o : this.observables) {
					o.atualizar(usuario);
				}
			}
		}
	}
}
