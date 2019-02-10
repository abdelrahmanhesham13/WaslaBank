package com.waslabank.waslabank.models;

public class ReviewModel {
    String id;
    String userId;
    String fromId;
    String requestId;
    String created;
    String comment;
    String rating;


    public ReviewModel(String id, String userId, String fromId, String requestId, String created, String comment, String rating) {
        this.id = id;
        this.userId = userId;
        this.fromId = fromId;
        this.requestId = requestId;
        this.created = created;
        this.comment = comment;
        this.rating = rating;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
