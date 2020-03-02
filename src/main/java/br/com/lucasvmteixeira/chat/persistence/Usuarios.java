package br.com.lucasvmteixeira.chat.persistence;

import java.util.ArrayList;
import java.util.Collections;
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
	private static Map<Address, Usuario> usuariosConectados;
	private static Map<Usuario, Address> enderecosDeUsuarios;
	private Set<Address> usuariosConectadosSemIdentificacao;

	private List<Atualizavel> observables;

	public Usuarios() {
		this.observables = new ArrayList<Atualizavel>();
		Usuarios.usuariosConectados = Collections.synchronizedMap(new HashMap<Address, Usuario>());
		Usuarios.enderecosDeUsuarios = Collections.synchronizedMap(new HashMap<Usuario, Address>());
		this.usuariosConectadosSemIdentificacao = new HashSet<Address>();
	}

	public void addObserver(Atualizavel o) {
		if (!this.observables.contains(o)) {
			this.observables.add(o);
		}
	}

	public void putUsuarioConectado(Address sender, Usuario usuarioSender) {
		usuarioSender.setEnderecoConectado(sender);

		Usuarios.usuariosConectados.put(sender, usuarioSender);
		Usuarios.enderecosDeUsuarios.put(usuarioSender, sender);
		this.usuariosConectadosSemIdentificacao.remove(sender);
		for (Atualizavel o : this.observables) {
			o.atualizar(Usuarios.usuariosConectados.values());
		}
	}

	public boolean containsUsuario(Address sender) {
		return Usuarios.usuariosConectados.containsKey(sender);
	}

	public boolean containsUsuarioSemIdentificacao(Address sender) {
		return this.usuariosConectadosSemIdentificacao.contains(sender);
	}

	public void addUsuariosSemIdentificacao(List<Address> members) {
		this.usuariosConectadosSemIdentificacao.addAll(members);
	}

	public void removeUsuarios(List<Address> members) {
		for (Address member : members) {
			Usuarios.enderecosDeUsuarios.remove(Usuarios.usuariosConectados.get(member));
			Usuarios.usuariosConectados.remove(member);

			this.usuariosConectadosSemIdentificacao.remove(member);
		}

		for (Atualizavel o : this.observables) {
			o.atualizar(Usuarios.usuariosConectados.values());
		}
	}

	public void updateGrupoDoUsuario(Address member, GrupoPrivado grupo) {
		Usuario usuario = Usuarios.usuariosConectados.get(member);

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

	public static Address getEnderecoConectado(Usuario usuario) {
		return Usuarios.enderecosDeUsuarios.get(usuario);
	}
}
