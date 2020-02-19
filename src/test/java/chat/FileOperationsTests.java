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
import org.junit.Before;
import org.junit.Test;

public class FileOperationsTests {

	@Before
	public void beforeTest() throws IOException {
		Path path = Paths.get("data.dat");

		boolean pathExists = Files.exists(path, new LinkOption[] { LinkOption.NOFOLLOW_LINKS });

		FileUtils.writeStringToFile(path.toFile(),
				"abcdefghijklmnopqrstuvxyz",
				Charset.defaultCharset());
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
