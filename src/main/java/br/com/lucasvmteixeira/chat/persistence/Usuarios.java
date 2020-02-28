package br.com.lucasvmteixeira.chat.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.jgroups.Address;

import br.com.lucasvmteixeira.chat.Atualizavel;
import br.com.lucasvmteixeira.chat.entity.GrupoPrivado;
import br.com.lucasvmteixeira.chat.entity.Usuario;

public class Usuarios {
	private Map<Address, Usuario> usuariosConectados;
	private Map<Usuario, Address> enderecosDeUsuarios;
	private Set<Address> usuariosConectadosSemIdentificacao;

	private List<Atualizavel> observables;

	public Usuarios() {
		this.observables = new ArrayList<Atualizavel>();
		this.usuariosConectados = new HashMap<Address, Usuario>();
		this.enderecosDeUsuarios = new HashMap<Usuario, Address>();
		this.usuariosConectadosSemIdentificacao = new HashSet<Address>();
	}

	public void addObserver(Atualizavel o) {
		this.observables.add(o);
	}

	public void putUsuarioConectado(Address sender, Usuario usuarioSender) {
		usuarioSender.setEnderecoConectado(sender);

		this.usuariosConectados.put(sender, usuarioSender);
		this.enderecosDeUsuarios.put(usuarioSender, sender);
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
		for (Address member : members) {
			this.enderecosDeUsuarios.remove(this.usuariosConectados.get(member));
			this.usuariosConectados.remove(member);

			this.usuariosConectadosSemIdentificacao.remove(member);
		}

		for (Atualizavel o : this.observables) {
			o.atualizar(this.usuariosConectados.values());
		}
	}

	public void updateGrupoDoUsuario(Address member, GrupoPrivado grupo) {
		Usuario usuario = this.usuariosConectados.get(member);

		if (usuario.getGruposPrivados() == null) {
			usuario.setGruposPrivados(new HashSet<GrupoPrivado>());
		}
		Set<Usuario> usuariosDoGrupo = usuariosConectados.values().stream()
				.filter(u -> this.usuariosConectados.values().contains(u)).collect(Collectors.toSet());
		if (usuariosDoGrupo.contains(usuario)) {
			if (usuario.getGruposPrivados().add(grupo)) {
				for (Atualizavel o : this.observables) {
					o.atualizar(usuario);
				}
			}
		}
	}

	public Address getEnderecoConectado(Usuario usuario) {
		return enderecosDeUsuarios.get(usuario);
	}
}
