package br.com.lucasvmteixeira.chat.persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgroups.Address;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import br.com.lucasvmteixeira.chat.Atualizavel;
import br.com.lucasvmteixeira.chat.entity.GrupoPrivado;
import br.com.lucasvmteixeira.chat.entity.Usuario;

public class Usuarios {
	private static Map<Address, Usuario> usuariosConectados;
	private static Map<Usuario, Address> enderecosDeUsuarios;
	private Set<Address> usuariosConectadosSemIdentificacao;

	private Set<Usuario> usuariosQueJaUtilizaramOChatNaRedeLocal;

	private List<Atualizavel> observables;

	public Usuarios() {
		this.observables = new ArrayList<Atualizavel>();
		Usuarios.usuariosConectados = Collections.synchronizedMap(new HashMap<Address, Usuario>());
		Usuarios.enderecosDeUsuarios = Collections.synchronizedMap(new HashMap<Usuario, Address>());
		this.usuariosConectadosSemIdentificacao = new HashSet<Address>();

		readFile();
		// TODO gravar em arquivo usuários que já utilizaram o chat
	}

	private void readFile() {
		Path path = Paths.get("users.dat");
		boolean pathExists = Files.exists(path, new LinkOption[] { LinkOption.NOFOLLOW_LINKS });

		Gson gson = new GsonBuilder().create();
		try {
			if (!pathExists) {
				Files.createFile(path);
			} else {
				this.usuariosQueJaUtilizaramOChatNaRedeLocal = gson.fromJson(
						String.join(System.lineSeparator(), Files.readAllLines(path)), new TypeToken<Set<Usuario>>() {
						}.getType());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
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
