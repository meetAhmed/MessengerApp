package app.messenger.gosling.james.messengerapp.Model;

import java.util.Comparator;

public class messageModel {

    String messageText;
    Long messageTime;
    String sender;
    String receiver;
    String type;
    String imageAddress;
    String nodeAddress;

    public messageModel(String messageText, String sender, String receiver, String type, String imageAddress, String nodeAddress) {
        this.messageText = messageText;
        this.messageTime = System.currentTimeMillis();
        this.sender = sender;
        this.receiver = receiver;
        this.type = type;
        this.imageAddress = imageAddress;
        this.nodeAddress = nodeAddress;
    }

    public messageModel() {
    }

    public String getMessageText() {
        return messageText;
    }

    public Long getMessageTime() {
        return messageTime;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getType() {
        return type;
    }

    public String getImageAddress() {
        return imageAddress;
    }

    public String getNodeAddress() {
        return nodeAddress;
    }

    public static Comparator<messageModel> COMPARE_BY_TIME = new Comparator<messageModel>() {

        @Override
        public int compare(messageModel one, messageModel other) {
            return one.getMessageTime().compareTo(other.getMessageTime());
        }
    };
}
