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

		int mensagensLidas = 0;
		StringBuilder builder = new StringBuilder();
		if (attachment.hasRemaining()) {
			if (attachment.hasArray()) {
				attachment.flip();
				String[] stringRead = new String(attachment.array()).split("\\r?\\n");

				// excluindo última mensagem lida caso esteja incompleta
				for (int i = 0; i < stringRead.length - 1 || mensagensLidas <= 10; i++) {
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
