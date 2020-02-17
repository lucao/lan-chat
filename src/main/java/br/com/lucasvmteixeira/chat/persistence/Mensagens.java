package br.com.lucasvmteixeira.chat.persistence;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import br.com.lucasvmteixeira.chat.entity.GrupoPrivado;
import br.com.lucasvmteixeira.chat.entity.Mensagem;
import br.com.lucasvmteixeira.chat.entity.Usuario;

public class Mensagens {
	private Set<Mensagem> mensagens;
	
	private File arquivoLocalDeDados;

	public Mensagens() {
		super();
		this.mensagens = new HashSet<Mensagem>();
	}

	public List<Mensagem> doGrupo(GrupoPrivado grupo) {
		return mensagens.stream().filter(msg -> msg.getGrupo().equals(grupo)).collect(Collectors.toList());
	}

	public Map<GrupoPrivado, List<Mensagem>> porGrupo() {
		return mensagens.stream().collect(Collectors.groupingBy(Mensagem::getGrupo));
	}
	
	public void add(Mensagem mensagem) {
		mensagens.add(mensagem);
	}

	public List<Mensagem> naoLidasPor(Usuario sender) {
		// TODO Auto-generated method stub
		return null;
	}

	public void ler(Mensagem mensagemLida, Usuario sender) {		
	}
}
