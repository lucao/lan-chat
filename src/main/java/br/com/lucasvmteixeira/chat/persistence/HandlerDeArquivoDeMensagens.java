package br.com.lucasvmteixeira.chat.persistence;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.lucasvmteixeira.chat.entity.Mensagem;

public class HandlerDeArquivoDeMensagens implements CompletionHandler<Integer, ByteBuffer> {
	private long position;
	private Mensagens mensagens;
	private Gson gson;
	private final int offsetDeMensagens;

	public HandlerDeArquivoDeMensagens(Mensagens mensagens, int offsetDeMensagens) {
		this.position = 0;
		this.mensagens = mensagens;
		this.gson = new GsonBuilder().create();
		this.offsetDeMensagens = offsetDeMensagens;
	}

	public HandlerDeArquivoDeMensagens(Mensagens mensagens) {
		this.position = 0;
		this.mensagens = mensagens;
		this.gson = new GsonBuilder().create();
		this.offsetDeMensagens = 10;
	}

	@Override
	public void completed(Integer result, ByteBuffer attachment) {
		attachment.flip();

		int mensagensLidas = 0;
		int numeroDeBytesLidos = 0;
		StringBuilder builder = new StringBuilder();
		if (attachment.hasRemaining()) {
			do {
				char lastCharRead = (char) attachment.get();
				numeroDeBytesLidos++;
				if (!isEOL(lastCharRead)) {

					final Mensagem mensagem = this.gson.fromJson(builder.toString(), Mensagem.class);
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

					position += numeroDeBytesLidos;
					mensagensLidas++;
					builder = new StringBuilder();
				} else {
					builder.append(lastCharRead);
				}
			} while (attachment.hasRemaining() || mensagensLidas < this.offsetDeMensagens);
		}
		attachment.clear();
	}

	private boolean isEOL(char character) {
		return ((character == '\n') || (character == '\r')) ? true : false;
	}

	@Override
	public void failed(Throwable exc, ByteBuffer attachment) {

	}

	public long getPosition() {
		return position;
	}
}
