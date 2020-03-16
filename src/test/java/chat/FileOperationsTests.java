package chat;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.lucasvmteixeira.chat.entity.Mensagem;

public class FileOperationsTests {

	@Before
	public void beforeTest() throws IOException {
		Path path = Paths.get("data.dat");

		boolean pathExists = Files.exists(path, new LinkOption[] { LinkOption.NOFOLLOW_LINKS });

		FileUtils.writeStringToFile(path.toFile(), "abcdefghijklmnopqrstuvxyz\n" + "abcdefghijklmnopqrstuvxyz\n"
				+ "abcdefghijklmnopqrstuvxyz\n" + "abcdefghijklmnopqrstuvxyz\n", Charset.defaultCharset());
	}

	@Test
	public void readFileAsynchronously() {
		Path path = Paths.get("data.dat");

		boolean pathExists = Files.exists(path, new LinkOption[] { LinkOption.NOFOLLOW_LINKS });

		AsynchronousFileChannel fileChannel;

		try {
			if (pathExists) {
				fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.READ, StandardOpenOption.WRITE);
			} else {
				fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
			}

			ByteBuffer buffer = ByteBuffer.allocate(8);
			long position = 0;

			fileChannel.read(buffer, position, buffer, new CompletionHandler<Integer, ByteBuffer>() {
				@Override
				public void completed(Integer result, ByteBuffer attachment) {

					System.out.println("result = " + result);

					attachment.flip();
					byte[] data = new byte[attachment.limit()];
					attachment.get(data);
					System.out.println(new String(data));
					attachment.clear();
				}

				@Override
				public void failed(Throwable exc, ByteBuffer attachment) {

				}
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void readFileAsynchronouslyAllContent() {
		Path path = Paths.get("data.dat");

		boolean pathExists = Files.exists(path, new LinkOption[] { LinkOption.NOFOLLOW_LINKS });

		AsynchronousFileChannel fileChannel;

		try {
			if (pathExists) {
				fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.READ, StandardOpenOption.WRITE);
			} else {
				fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
			}

			ByteBuffer buffer = ByteBuffer.allocate(64);
			final StringBuilder builder = new StringBuilder();
			CompletionHandler<Integer, ByteBuffer> completionHandler = new CompletionHandler<Integer, ByteBuffer>() {
				@Override
				public void completed(Integer result, ByteBuffer attachment) {
					try {
						System.out.println("Limit do buffer: " + String.valueOf(attachment.limit()));
						int mensagensLidas = 0;

						attachment.flip();
						if (attachment.hasRemaining()) {
							if (attachment.hasArray()) {
								String[] stringRead = new String(attachment.array()).split("\\r?\\n");
								// excluir último registro lido

								for (int i = 0; i < stringRead.length - 1; i++) {
									String string = stringRead[i];

									System.out.println("Mensagem lida: " + string);
									builder.append(string + "\n");

									mensagensLidas++;
								}

								System.out.println("Número de mensagens lidas: " + String.valueOf(mensagensLidas));
								attachment.clear();

								System.out.println("Lendo a partir do: " + String.valueOf(builder.toString().length()));
								
								ByteBuffer buffer2 = ByteBuffer.allocate(64);
								fileChannel.read(buffer2, builder.toString().length(), buffer2, this);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				@Override
				public void failed(Throwable exc, ByteBuffer attachment) {

				}
			};
			fileChannel.read(buffer, 0L, buffer, completionHandler);
			Thread.sleep(3000);

		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static boolean isEOL(byte character) {
		return ((character == '\n') || (character == '\r')) ? true : false;
	}

	@Test
	public void readFileAsynchronouslySeveralTimes() {
		Path path = Paths.get("data.dat");

		boolean pathExists = Files.exists(path, new LinkOption[] { LinkOption.NOFOLLOW_LINKS });

		AsynchronousFileChannel fileChannel;

		try {
			if (pathExists) {
				fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.READ, StandardOpenOption.WRITE);
			} else {
				fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
			}

			ByteBuffer buffer = ByteBuffer.allocate(8);
			long position = 0;

			fileChannel.read(buffer, position, buffer, new CompletionHandler<Integer, ByteBuffer>() {
				@Override
				public void completed(Integer result, ByteBuffer attachment) {

					System.out.println("result = " + result);

					attachment.flip();
					byte[] data = new byte[attachment.limit()];
					attachment.get(data);
					System.out.println(new String(data));
					attachment.clear();
				}

				@Override
				public void failed(Throwable exc, ByteBuffer attachment) {

				}
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
