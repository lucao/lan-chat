package br.com.lucasvmteixeira.chat.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.jgroups.Address;

import br.com.lucasvmteixeira.chat.UsuarioNaoConectado;
import br.com.lucasvmteixeira.chat.persistence.Usuarios;

public class Usuario implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6747522460337961540L;

	public static final String canalPrincipal = "channel";

	private String nome;
	private Set<GrupoPrivado> gruposPrivados;
	private transient List<Mensagem> conversas;

	private transient Address enderecoConectado;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Set<GrupoPrivado> getGruposPrivados() {
		return gruposPrivados;
	}

	public void setGruposPrivados(Set<GrupoPrivado> gruposPrivados) {
		this.gruposPrivados = gruposPrivados;
	}

	public List<Mensagem> getConversas() {
		return conversas;
	}

	public void setConversas(List<Mensagem> conversas) {
		this.conversas = conversas;
	}

	public Address getEnderecoConectado() throws UsuarioNaoConectado {
		if (this.enderecoConectado == null) {
			this.enderecoConectado = Usuarios.getEnderecoConectado(this);
			if (this.enderecoConectado == null) {
				throw new UsuarioNaoConectado();
			}
		}
		return this.enderecoConectado;
	}

	public void setEnderecoConectado(Address enderecoConectado) {
		this.enderecoConectado = enderecoConectado;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Usuario other = (Usuario) obj;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		return true;
	}
}
