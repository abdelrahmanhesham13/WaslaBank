package com.waslabank.waslabank.models;

public class MessageModel {

    String chatId;
    String toId;
    String fromId;
    String date;
    String message;
    boolean isMine;

    public MessageModel(String chatId, String toId, String fromId, String date, String message, boolean isMine) {
        this.chatId = chatId;
        this.toId = toId;
        this.fromId = fromId;
        this.date = date;
        this.message = message;
        this.isMine = isMine;
    }

    public String getChatId() {
        return chatId;
    }

    public String getToId() {
        return toId;
    }

    public String getFromId() {
        return fromId;
    }

    public String getDate() {
        return date;
    }

    public String getMessage() {
        return message;
    }

    public boolean isMine() {
        return isMine;
    }
}
