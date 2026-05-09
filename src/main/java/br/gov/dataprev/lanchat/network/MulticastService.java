package br.gov.dataprev.lanchat.network;

import br.gov.dataprev.lanchat.model.Peer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Sends periodic multicast announcements and listens for peers.
 * Packet format: "LANCHAT:<nickname>:<tcpPort>"
 */
public class MulticastService {

    public static final String MULTICAST_GROUP = "239.255.42.99";
    public static final int MULTICAST_PORT = 45678;
    private static final int ANNOUNCE_INTERVAL_SEC = 3;
    private static final int PEER_TIMEOUT_SEC = 10;

    private final ObservableList<Peer> peers = FXCollections.observableArrayList();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    private String nickname;
    private int tcpPort;
    private DatagramChannel channel;
    private InetSocketAddress groupAddress;
    private NetworkInterface networkInterface;

    public void start(String nickname, int tcpPort) throws IOException {
        this.nickname = nickname;
        this.tcpPort = tcpPort;
        groupAddress = new InetSocketAddress(MULTICAST_GROUP, MULTICAST_PORT);
        networkInterface = findMulticastInterface();

        channel = DatagramChannel.open(StandardProtocolFamily.INET);
        // SO_REUSEADDR must be set BEFORE bind so multiple instances on same host can share the port
        channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        channel.bind(new InetSocketAddress(MULTICAST_PORT));
        channel.setOption(StandardSocketOptions.IP_MULTICAST_IF, networkInterface);
        channel.configureBlocking(false);

        InetAddress group = InetAddress.getByName(MULTICAST_GROUP);
        channel.join(group, networkInterface);

        scheduler.scheduleAtFixedRate(this::announce, 0, ANNOUNCE_INTERVAL_SEC, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(this::receive, 0, 200, TimeUnit.MILLISECONDS);
        scheduler.scheduleAtFixedRate(this::evictStale, 5, 5, TimeUnit.SECONDS);
    }

    public void stop() {
        scheduler.shutdownNow();
        try { if (channel != null) channel.close(); } catch (IOException ignored) {}
    }

    public ObservableList<Peer> getPeers() { return peers; }

    private void announce() {
        try {
            String msg = "LANCHAT:" + nickname + ":" + tcpPort;
            ByteBuffer buf = ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8));
            channel.send(buf, groupAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receive() {
        try {
            ByteBuffer buf = ByteBuffer.allocate(512);
            SocketAddress src = channel.receive(buf);
            if (src == null) return;
            buf.flip();
            String msg = StandardCharsets.UTF_8.decode(buf).toString();
            if (!msg.startsWith("LANCHAT:")) return;
            String[] parts = msg.split(":");
            if (parts.length < 3) return;
            String peerNick = parts[1];
            int peerPort = Integer.parseInt(parts[2]);
            if (peerNick.equals(nickname)) return;
            String addr = ((InetSocketAddress) src).getAddress().getHostAddress();
            Platform.runLater(() -> upsertPeer(peerNick, addr, peerPort));
        } catch (IOException ignored) {}
    }

    private void upsertPeer(String nick, String addr, int port) {
        for (Peer p : peers) {
            if (p.getNickname().equals(nick)) {
                p.setAddress(addr);
                p.setPort(port);
                p.touch();
                return;
            }
        }
        peers.add(new Peer(nick, addr, port));
    }

    private void evictStale() {
        long now = System.currentTimeMillis();
        Platform.runLater(() -> peers.removeIf(p ->
            now - p.getLastSeen().toEpochMilli() > PEER_TIMEOUT_SEC * 1000L));
    }

    private NetworkInterface findMulticastInterface() throws SocketException {
        var interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface ni = interfaces.nextElement();
            if (!ni.isUp() || ni.isLoopback() || !ni.supportsMulticast()) continue;
            // Must have at least one IPv4 address — avoids "Not configured for IPv4" on VPN/virtual adapters
            boolean hasIPv4 = ni.getInterfaceAddresses().stream()
                .anyMatch(a -> a.getAddress() instanceof Inet4Address);
            if (hasIPv4) return ni;
        }
        // fallback: loopback (works for single-machine testing)
        return NetworkInterface.getByInetAddress(InetAddress.getLoopbackAddress());
    }
}
