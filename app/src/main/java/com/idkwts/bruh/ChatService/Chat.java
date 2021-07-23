package com.idkwts.bruh.ChatService;

public class Chat {

    private String sender, message, type, receiver, messageID, time, date, replyto;
    private boolean isseen;

    public Chat(String sender, String message, String type, String receiver, String messageID, String time, String date, String replyto, boolean isseen) {
        this.sender = sender;
        this.message = message;
        this.type = type;
        this.receiver = receiver;
        this.messageID = messageID;
        this.time = time;
        this.date = date;
        this.replyto = replyto;
        this.isseen = isseen;
    }

    public Chat() {

    }

    public String getReplyto() { return replyto; }

    public void setReplyto(String replyto) { this.replyto = replyto; }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }
}
