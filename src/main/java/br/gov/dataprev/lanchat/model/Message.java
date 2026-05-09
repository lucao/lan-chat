package br.gov.dataprev.lanchat.model;

import java.time.Instant;
import java.util.UUID;

public class Message {

    public enum Type { TEXT, FILE }
    public enum Status { SENT, DELIVERED, READ }

    private String id;
    private String fromNickname;
    private String toNickname;
    private Type type;
    private String content;       // text content or file name
    private String filePath;      // local path (sender) or saved path (receiver)
    private long fileSize;
    private Instant sentAt;
    private Instant deliveredAt;
    private Instant readAt;
    private Status status;
    private boolean outgoing;

    public Message() {}

    public static Message text(String from, String to, String content) {
        Message m = new Message();
        m.id = UUID.randomUUID().toString();
        m.fromNickname = from;
        m.toNickname = to;
        m.type = Type.TEXT;
        m.content = content;
        m.sentAt = Instant.now();
        m.status = Status.SENT;
        m.outgoing = true;
        return m;
    }

    public static Message file(String from, String to, String fileName, String filePath, long fileSize) {
        Message m = new Message();
        m.id = UUID.randomUUID().toString();
        m.fromNickname = from;
        m.toNickname = to;
        m.type = Type.FILE;
        m.content = fileName;
        m.filePath = filePath;
        m.fileSize = fileSize;
        m.sentAt = Instant.now();
        m.status = Status.SENT;
        m.outgoing = true;
        return m;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFromNickname() { return fromNickname; }
    public void setFromNickname(String fromNickname) { this.fromNickname = fromNickname; }
    public String getToNickname() { return toNickname; }
    public void setToNickname(String toNickname) { this.toNickname = toNickname; }
    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }
    public Instant getSentAt() { return sentAt; }
    public void setSentAt(Instant sentAt) { this.sentAt = sentAt; }
    public Instant getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(Instant deliveredAt) { this.deliveredAt = deliveredAt; }
    public Instant getReadAt() { return readAt; }
    public void setReadAt(Instant readAt) { this.readAt = readAt; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public boolean isOutgoing() { return outgoing; }
    public void setOutgoing(boolean outgoing) { this.outgoing = outgoing; }
}
