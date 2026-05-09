package br.gov.dataprev.lanchat.network;

import br.gov.dataprev.lanchat.model.Message;
import br.gov.dataprev.lanchat.service.StorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.net.StandardSocketOptions;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Handles all TCP communication using java.nio.
 * Protocol: 4-byte big-endian length prefix + JSON envelope bytes.
 * For FILE_DATA the envelope.data field carries the raw bytes (base64 via Jackson).
 */
public class TcpService {

    public static final int DEFAULT_PORT = 45679;

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final StorageService storage;

    private String nickname;
    private int boundPort;
    private ServerSocketChannel serverChannel;
    private Consumer<Message> onMessageReceived;

    public TcpService(StorageService storage) {
        this.storage = storage;
    }

    public int getBoundPort() { return boundPort; }

    public void start(String nickname, Consumer<Message> onMessageReceived) throws IOException {
        this.nickname = nickname;
        this.onMessageReceived = onMessageReceived;
        serverChannel = ServerSocketChannel.open();
        serverChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        // Try DEFAULT_PORT, increment until a free port is found
        int port = DEFAULT_PORT;
        while (true) {
            try {
                serverChannel.bind(new InetSocketAddress(port));
                this.boundPort = port;
                break;
            } catch (java.nio.channels.AlreadyBoundException | java.net.BindException e) {
                port++;
            }
        }
        serverChannel.configureBlocking(true);
        executor.submit(this::acceptLoop);
    }

    public void stop() {
        executor.shutdownNow();
        try { if (serverChannel != null) serverChannel.close(); } catch (IOException ignored) {}
    }

    // ── Sending ──────────────────────────────────────────────────────────────

    public void sendText(String peerAddress, int peerPort, Message msg) {
        executor.submit(() -> {
            try (SocketChannel ch = connect(peerAddress, peerPort)) {
                Envelope env = new Envelope();
                env.setType(Envelope.Type.TEXT);
                env.setMessageId(msg.getId());
                env.setFromNickname(msg.getFromNickname());
                env.setToNickname(msg.getToNickname());
                env.setContent(msg.getContent());
                env.setSentAt(msg.getSentAt().toString());
                writeEnvelope(ch, env);
            } catch (IOException e) { e.printStackTrace(); }
        });
    }

    public void sendFile(String peerAddress, int peerPort, Message msg) {
        executor.submit(() -> {
            try (SocketChannel ch = connect(peerAddress, peerPort)) {
                // 1. Send metadata
                Envelope meta = new Envelope();
                meta.setType(Envelope.Type.FILE_META);
                meta.setMessageId(msg.getId());
                meta.setFromNickname(msg.getFromNickname());
                meta.setToNickname(msg.getToNickname());
                meta.setContent(msg.getContent());
                meta.setFileSize(msg.getFileSize());
                meta.setSentAt(msg.getSentAt().toString());
                writeEnvelope(ch, meta);

                // 2. Stream file in chunks
                try (FileChannel fc = FileChannel.open(Path.of(msg.getFilePath()))) {
                    ByteBuffer buf = ByteBuffer.allocate(64 * 1024);
                    while (fc.read(buf) > 0) {
                        buf.flip();
                        byte[] chunk = new byte[buf.remaining()];
                        buf.get(chunk);
                        buf.clear();
                        Envelope data = new Envelope();
                        data.setType(Envelope.Type.FILE_DATA);
                        data.setMessageId(msg.getId());
                        data.setData(chunk);
                        writeEnvelope(ch, data);
                    }
                }
                // 3. EOF marker
                Envelope eof = new Envelope();
                eof.setType(Envelope.Type.FILE_DATA);
                eof.setMessageId(msg.getId());
                eof.setData(new byte[0]);
                writeEnvelope(ch, eof);
            } catch (IOException e) { e.printStackTrace(); }
        });
    }

    public void sendAck(String peerAddress, int peerPort, String messageId, Envelope.Type ackType) {
        executor.submit(() -> {
            try (SocketChannel ch = connect(peerAddress, peerPort)) {
                Envelope env = new Envelope();
                env.setType(ackType);
                env.setMessageId(messageId);
                env.setFromNickname(nickname);
                writeEnvelope(ch, env);
            } catch (IOException e) { e.printStackTrace(); }
        });
    }

    // ── Server ───────────────────────────────────────────────────────────────

    private void acceptLoop() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                SocketChannel client = serverChannel.accept();
                executor.submit(() -> handleClient(client));
            } catch (IOException e) {
                if (!serverChannel.isOpen()) break;
            }
        }
    }

    private void handleClient(SocketChannel ch) {
        try (ch) {
            // First envelope is always TEXT, FILE_META, or ACK
            Envelope first = readEnvelope(ch);
            if (first == null) return;

            switch (first.getType()) {
                case TEXT -> {
                    Message msg = new Message();
                    msg.setId(first.getMessageId());
                    msg.setFromNickname(first.getFromNickname());
                    msg.setToNickname(first.getToNickname());
                    msg.setType(Message.Type.TEXT);
                    msg.setContent(first.getContent());
                    msg.setSentAt(Instant.parse(first.getSentAt()));
                    msg.setDeliveredAt(Instant.now());
                    msg.setStatus(Message.Status.DELIVERED);
                    msg.setOutgoing(false);
                    Platform.runLater(() -> onMessageReceived.accept(msg));
                }
                case FILE_META -> receiveFile(ch, first);
                case ACK_DELIVERED -> {
                    Platform.runLater(() -> onMessageReceived.accept(ack(first, Message.Status.DELIVERED)));
                }
                case ACK_READ -> {
                    Platform.runLater(() -> onMessageReceived.accept(ack(first, Message.Status.READ)));
                }
                default -> {}
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void receiveFile(SocketChannel ch, Envelope meta) throws IOException {
        String fileName = meta.getContent();
        Path dest = storage.getReceivedFilesDir().resolve(fileName);
        // avoid overwrite
        int i = 1;
        while (Files.exists(dest)) {
            String name = fileName.contains(".")
                ? fileName.substring(0, fileName.lastIndexOf('.')) + "_" + i + fileName.substring(fileName.lastIndexOf('.'))
                : fileName + "_" + i;
            dest = storage.getReceivedFilesDir().resolve(name);
            i++;
        }

        try (FileChannel fc = FileChannel.open(dest,
                java.nio.file.StandardOpenOption.CREATE,
                java.nio.file.StandardOpenOption.WRITE)) {
            while (true) {
                Envelope chunk = readEnvelope(ch);
                if (chunk == null || chunk.getData() == null || chunk.getData().length == 0) break;
                fc.write(ByteBuffer.wrap(chunk.getData()));
            }
        }

        Message msg = new Message();
        msg.setId(meta.getMessageId());
        msg.setFromNickname(meta.getFromNickname());
        msg.setToNickname(meta.getToNickname());
        msg.setType(Message.Type.FILE);
        msg.setContent(meta.getContent());
        msg.setFilePath(dest.toString());
        msg.setFileSize(meta.getFileSize());
        msg.setSentAt(Instant.parse(meta.getSentAt()));
        msg.setDeliveredAt(Instant.now());
        msg.setStatus(Message.Status.DELIVERED);
        msg.setOutgoing(false);
        Platform.runLater(() -> onMessageReceived.accept(msg));
    }

    private Message ack(Envelope env, Message.Status status) {
        Message m = new Message();
        m.setId(env.getMessageId());
        m.setFromNickname(env.getFromNickname());
        m.setStatus(status);
        if (status == Message.Status.DELIVERED) m.setDeliveredAt(Instant.now());
        else m.setReadAt(Instant.now());
        return m;
    }

    // ── Wire helpers ─────────────────────────────────────────────────────────

    private void writeEnvelope(SocketChannel ch, Envelope env) throws IOException {
        byte[] json = mapper.writeValueAsBytes(env);
        ByteBuffer buf = ByteBuffer.allocate(4 + json.length);
        buf.putInt(json.length);
        buf.put(json);
        buf.flip();
        while (buf.hasRemaining()) ch.write(buf);
    }

    private Envelope readEnvelope(SocketChannel ch) throws IOException {
        ByteBuffer lenBuf = ByteBuffer.allocate(4);
        if (readFully(ch, lenBuf) < 4) return null;
        lenBuf.flip();
        int len = lenBuf.getInt();
        if (len <= 0 || len > 10 * 1024 * 1024) return null;
        ByteBuffer body = ByteBuffer.allocate(len);
        readFully(ch, body);
        body.flip();
        byte[] bytes = new byte[body.remaining()];
        body.get(bytes);
        return mapper.readValue(bytes, Envelope.class);
    }

    private int readFully(SocketChannel ch, ByteBuffer buf) throws IOException {
        int total = 0;
        while (buf.hasRemaining()) {
            int n = ch.read(buf);
            if (n == -1) break;
            total += n;
        }
        return total;
    }

    private SocketChannel connect(String addr, int port) throws IOException {
        SocketChannel ch = SocketChannel.open();
        ch.connect(new InetSocketAddress(addr, port));
        return ch;
    }
}
