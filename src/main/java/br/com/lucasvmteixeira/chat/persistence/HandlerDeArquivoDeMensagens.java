package br.com.lucasvmteixeira.chat.persistence;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.lucasvmteixeira.chat.entity.Mensagem;

public class HandlerDeArquivoDeMensagens implements CompletionHandler<Integer, ByteBuffer> {
	private long position;
	private Mensagens mensagens;
	private Gson gson;
	private final int offsetDeMensagens;
	
	private static final Pattern pattern = Pattern.compile("\\r?\\n");

	public HandlerDeArquivoDeMensagens(Mensagens mensagens, int offsetDeMensagens) {
		this.position = 0;
		this.mensagens = mensagens;
		this.gson = new GsonBuilder().create();
		this.offsetDeMensagens = offsetDeMensagens;
	}
	@Override
	public void completed(Integer result, ByteBuffer attachment) {

		int mensagensLidas = 0;
		StringBuilder builder = new StringBuilder();
		if (attachment.hasRemaining()) {
			if (attachment.hasArray()) {
				attachment.flip();
				String[] stringRead = HandlerDeArquivoDeMensagens.pattern.split(new String(attachment.array()));

				// excluindo última mensagem lida caso esteja incompleta
				for (int i = 0; i < stringRead.length - 1 || mensagensLidas <= this.offsetDeMensagens; i++) {
					String string = stringRead[i];
					position += string.getBytes().length;

					final Mensagem mensagem = this.gson.fromJson(string, Mensagem.class);
					mensagensLidas++;

					if (mensagem != null) {
						if (mensagem.getDataDeEnvio() != null && mensagem.getMensagem() != null
								&& mensagem.getSender() != null) {
							synchronized (this.mensagens) {
								this.mensagens.add(mensagem);
							}
						} else {
							System.out.println(builder.toString());
						}
					} else {
						System.out.println(builder.toString());
					}
				}
			}
		}
		attachment.clear();
	}

	@Override
	public void failed(Throwable exc, ByteBuffer attachment) {
		// TODO
	}

	public long getPosition() {
		return position;
	}
}
