package br.gov.dataprev.lanchat.service;

import br.gov.dataprev.lanchat.model.Message;
import br.gov.dataprev.lanchat.model.Peer;
import br.gov.dataprev.lanchat.network.Envelope;
import br.gov.dataprev.lanchat.network.MulticastService;
import br.gov.dataprev.lanchat.network.TcpService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatController {

    private final StorageService storage = new StorageService();
    private final MulticastService multicast = new MulticastService();
    private final TcpService tcp = new TcpService(storage);

    private String nickname;
    private final ObservableMap<String, ObservableList<Message>> conversations =
            FXCollections.observableHashMap();
    private final ObservableMap<String, Integer> unreadCounts =
            FXCollections.observableHashMap();

    public void start(String nickname) throws IOException {
        this.nickname = nickname;
        storage.saveNickname(nickname);
        tcp.start(nickname, this::onIncoming);
        multicast.start(nickname, tcp.getBoundPort());
    }

    public void stop() {
        multicast.stop();
        tcp.stop();
    }

    public String getNickname() { return nickname; }
    public StorageService getStorage() { return storage; }
    public ObservableList<Peer> getPeers() { return multicast.getPeers(); }
    public ObservableMap<String, Integer> getUnreadCounts() { return unreadCounts; }

    public int getUnread(String peerNickname) {
        return unreadCounts.getOrDefault(peerNickname, 0);
    }

    public void clearUnread(String peerNickname) {
        unreadCounts.put(peerNickname, 0);
    }

    public ObservableList<Message> getConversation(String peerNickname) {
        return conversations.computeIfAbsent(peerNickname, k -> {
            List<Message> history = storage.loadHistory(k);
            return FXCollections.observableArrayList(history);
        });
    }

    public void sendText(Peer peer, String text) {
        Message msg = Message.text(nickname, peer.getNickname(), text);
        getConversation(peer.getNickname()).add(msg);
        persist(peer.getNickname());
        tcp.sendText(peer.getAddress(), peer.getPort(), msg);
    }

    public void sendFile(Peer peer, java.io.File file) {
        Message msg = Message.file(nickname, peer.getNickname(), file.getName(), file.getAbsolutePath(), file.length());
        getConversation(peer.getNickname()).add(msg);
        persist(peer.getNickname());
        tcp.sendFile(peer.getAddress(), peer.getPort(), msg);
    }

    public void markRead(Peer peer, Message msg) {
        if (msg.getReadAt() != null) return;
        msg.setReadAt(java.time.Instant.now());
        msg.setStatus(Message.Status.READ);
        persist(peer.getNickname());
        // find peer address from multicast list
        multicast.getPeers().stream()
            .filter(p -> p.getNickname().equals(msg.getFromNickname()))
            .findFirst()
            .ifPresent(p -> tcp.sendAck(p.getAddress(), p.getPort(), msg.getId(), Envelope.Type.ACK_READ));
    }

    private void onIncoming(Message msg) {
        // ACK update (no fromNickname conversation key needed, just update existing msg)
        if (msg.getStatus() == Message.Status.DELIVERED && !msg.isOutgoing() && msg.getType() == null) {
            updateStatus(msg);
            return;
        }
        if (msg.getStatus() == Message.Status.READ && msg.getType() == null) {
            updateStatus(msg);
            return;
        }

        String peer = msg.isOutgoing() ? msg.getToNickname() : msg.getFromNickname();
        ObservableList<Message> conv = getConversation(peer);

        // Send delivered ACK back
        if (!msg.isOutgoing()) {
            multicast.getPeers().stream()
                .filter(p -> p.getNickname().equals(msg.getFromNickname()))
                .findFirst()
                .ifPresent(p -> tcp.sendAck(p.getAddress(), p.getPort(), msg.getId(), Envelope.Type.ACK_DELIVERED));
        }

        conv.add(msg);
        persist(peer);
        // increment unread only for incoming messages
        if (!msg.isOutgoing()) {
            unreadCounts.merge(peer, 1, Integer::sum);
        }
    }

    private void updateStatus(Message ack) {
        conversations.values().forEach(list -> list.stream()
            .filter(m -> m.getId().equals(ack.getId()))
            .findFirst()
            .ifPresent(m -> {
                if (ack.getDeliveredAt() != null) {
                    m.setDeliveredAt(ack.getDeliveredAt());
                    m.setStatus(Message.Status.DELIVERED);
                }
                if (ack.getReadAt() != null) {
                    m.setReadAt(ack.getReadAt());
                    m.setStatus(Message.Status.READ);
                }
                persist(m.getToNickname());
            }));
    }

    private void persist(String peerNickname) {
        ObservableList<Message> conv = conversations.get(peerNickname);
        if (conv != null) storage.saveHistory(peerNickname, new ArrayList<>(conv));
    }
}
