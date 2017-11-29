package ru.ilka.chat.entity;

public class Message implements ChatEntity {

    private static final long serialVersionUID = 205242440943911308L;

    private MessageType type;
    private String text;
    private String senderClientName;

    public Message() {
    }

    public Message(MessageType type, String text, String senderClientName) {
        this.type = type;
        this.text = text;
        this.senderClientName = senderClientName;
    }

    public Message(Message message) {
        this.type = message.type;
        this.text = message.text;
        this.senderClientName = message.senderClientName;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSenderClientName() {
        return senderClientName;
    }

    public void setSenderClientName(String senderClientName) {
        this.senderClientName = senderClientName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        if (type != message.type) return false;
        return text.equals(message.text);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + text.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                ", text='" + text + '\'' +
                ", sender='" + senderClientName + '\'' +
                '}';
    }
}
