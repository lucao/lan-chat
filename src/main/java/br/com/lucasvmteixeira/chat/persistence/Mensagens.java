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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import br.com.lucasvmteixeira.chat.Atualizavel;
import br.com.lucasvmteixeira.chat.entity.GrupoPrivado;
import br.com.lucasvmteixeira.chat.entity.Mensagem;
import br.com.lucasvmteixeira.chat.entity.Usuario;

public class Mensagens {
	private Set<Mensagem> mensagens;
	private HandlerDeArquivoDeMensagens handler;

	private List<Atualizavel> observables;

	private static final int OFFSET_PARA_LEITURA = 10;

	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

	public Mensagens() {
		super();
		this.mensagens = Collections.synchronizedSet(new HashSet<Mensagem>());
		this.observables = new ArrayList<Atualizavel>();

		this.handler = new HandlerDeArquivoDeMensagens(this, OFFSET_PARA_LEITURA);

		Runnable readFile = () -> {
			try {
				readFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		};
		
		executor.scheduleWithFixedDelay(readFile, 0, 1, TimeUnit.SECONDS);
		// TODO gravar arquivos com dados
	}

	private synchronized String readFile() throws IOException {
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
			fileChannel.read(buffer, this.handler.getPosition(), buffer, this.handler);

			return new String(buffer.array(), "UTF-8");
		} catch (IOException e) {
			throw e;
		}
	}

	public void addObserver(Atualizavel a) {
		if (!this.observables.contains(a)) {
			this.observables.add(a);
		}
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

	public void carregarMensagens() throws IOException {
		readFile();
	}
}
