package br.com.lucasvmteixeira.chat.net;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import br.com.lucasvmteixeira.chat.entity.GrupoPrivado;
import br.com.lucasvmteixeira.chat.entity.Mensagem;

public class Mensagens {
	private Set<Mensagem> mensagens;

	public Mensagens(Set<Mensagem> mensagens) {
		super();
		//TODO read from file;
		this.mensagens = mensagens;
	}

	public List<Mensagem> doGrupo(GrupoPrivado grupo) {
		return mensagens.stream().filter(msg -> msg.getGrupo().equals(grupo)).collect(Collectors.toList());
	}

	public Map<GrupoPrivado, List<Mensagem>> porGrupo() {
		return mensagens.stream().collect(Collectors.groupingBy(Mensagem::getGrupo));
	}
}
