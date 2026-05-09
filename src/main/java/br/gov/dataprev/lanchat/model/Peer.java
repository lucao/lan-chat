package br.gov.dataprev.lanchat.model;

import java.time.Instant;

public class Peer {
    private String nickname;
    private String address;
    private int port;
    private Instant lastSeen;

    public Peer() {}

    public Peer(String nickname, String address, int port) {
        this.nickname = nickname;
        this.address = address;
        this.port = port;
        this.lastSeen = Instant.now();
    }

    public void touch() { this.lastSeen = Instant.now(); }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }
    public Instant getLastSeen() { return lastSeen; }
    public void setLastSeen(Instant lastSeen) { this.lastSeen = lastSeen; }

    @Override public String toString() { return nickname; }
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Peer p)) return false;
        return nickname.equals(p.nickname);
    }
    @Override public int hashCode() { return nickname.hashCode(); }
}
