package br.com.lucasvmteixeira.chat.persistence;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import br.com.lucasvmteixeira.chat.Atualizavel;
import br.com.lucasvmteixeira.chat.entity.GrupoPrivado;
import br.com.lucasvmteixeira.chat.entity.Mensagem;
import br.com.lucasvmteixeira.chat.entity.Usuario;

public class Mensagens {
	private Set<Mensagem> mensagens;
	private HandlerDeArquivoDeMensagens handler;

	private List<Atualizavel> observables;

	public Mensagens() {
		super();
		this.mensagens = new HashSet<Mensagem>();
		this.observables = new ArrayList<Atualizavel>();
		
		readFile();

	}

	private void readFile() {
		Path path = Paths.get("data.dat");

		boolean pathExists = Files.exists(path, new LinkOption[] { LinkOption.NOFOLLOW_LINKS });
		AsynchronousFileChannel fileChannel;
		try {
			if (pathExists) {
				fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.READ, StandardOpenOption.WRITE);
			} else {
				fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
			}

			ByteBuffer buffer = ByteBuffer.allocate(1024);
			this.handler = new HandlerDeArquivoDeMensagens(this);
			fileChannel.read(buffer, this.handler.getPosition(), buffer, this.handler);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addObserver(Atualizavel a) {
		this.observables.add(a);
	}

	public List<Mensagem> doGrupo(GrupoPrivado grupo) {
		return mensagens.stream().filter(msg -> msg.getGrupo().equals(grupo)).collect(Collectors.toList());
	}

	public Map<GrupoPrivado, List<Mensagem>> porGrupo() {
		return mensagens.stream().collect(Collectors.groupingBy(Mensagem::getGrupo));
	}

	public void add(Mensagem mensagem) {
		mensagens.add(mensagem);
		for (Atualizavel o : this.observables) {
			o.atualizar(mensagem);
		}
	}

	public Set<Mensagem> naoLidasPor(Usuario sender) {
		// TODO Auto-generated method stub
		return null;
	}

	public void ler(Mensagem mensagemLida, Usuario sender) {
	}

	public Set<Mensagem> enviadasPor(Usuario usuarioConectado) {
		// TODO Auto-generated method stub
		return null;
	}
}
