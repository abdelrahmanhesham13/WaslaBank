package com.waslabank.waslabank.models;

import java.io.Serializable;

public class NotificationModel implements Serializable {
    private String id;
    private String title;
    private String type;
    private String userId;
    private String created;
    private String offerId;
    private String status;
    private String requestId;
    private String fromId;
    private String titleDelivery;
    private UserModel mUserModel;

    public NotificationModel(String id, String title, String type, String userId, String created, String offerId, String status, String requestId, String fromId, String titleDelivery, UserModel mUserModel) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.userId = userId;
        this.created = created;
        this.offerId = offerId;
        this.status = status;
        this.requestId = requestId;
        this.fromId = fromId;
        this.titleDelivery = titleDelivery;
        this.mUserModel = mUserModel;
    }

    public String getTitleDelivery() {
        return titleDelivery;
    }

    public void setTitleDelivery(String titleDelivery) {
        this.titleDelivery = titleDelivery;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public UserModel getmUserModel() {
        return mUserModel;
    }

    public void setmUserModel(UserModel mUserModel) {
        this.mUserModel = mUserModel;
    }
}
