package br.gov.dataprev.lanchat.network;

/**
 * JSON-serializable envelope sent over TCP.
 * type: TEXT | FILE_META | FILE_DATA | ACK_DELIVERED | ACK_READ
 */
public class Envelope {

    public enum Type { TEXT, FILE_META, FILE_DATA, ACK_DELIVERED, ACK_READ }

    private Type type;
    private String messageId;
    private String fromNickname;
    private String toNickname;
    private String content;       // text or file name
    private long fileSize;
    private String sentAt;        // ISO-8601
    private byte[] data;          // file chunk

    public Envelope() {}

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }
    public String getFromNickname() { return fromNickname; }
    public void setFromNickname(String fromNickname) { this.fromNickname = fromNickname; }
    public String getToNickname() { return toNickname; }
    public void setToNickname(String toNickname) { this.toNickname = toNickname; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }
    public String getSentAt() { return sentAt; }
    public void setSentAt(String sentAt) { this.sentAt = sentAt; }
    public byte[] getData() { return data; }
    public void setData(byte[] data) { this.data = data; }
}
