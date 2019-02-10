package com.waslabank.waslabank.models;

import java.io.Serializable;

public class RideModel implements Serializable {
    private String id;
    private String cityId;
    private String address;
    private String lon;
    private String lat;
    private String lonTo;
    private String latTo;
    private String status;
    private String created;
    private String updated;
    private String userId;
    private String addressTo;
    private String cityTo;
    private String requestTime;
    private String view;
    private String requestDate;
    private String lonUpdate;
    private String latUpdate;
    private String fromId;
    private UserModel user;
    private String distance;
    private String seats;
    private String duration;
    private String start;

    public RideModel(String id, String cityId, String address, String lon, String lat, String lonTo, String latTo, String status, String created, String updated, String userId, String addressTo, String cityTo, String requestTime, String view, String requestDate, String lonUpdate, String latUpdate, String fromId, UserModel user, String distance, String seats, String duration, String start) {
        this.id = id;
        this.cityId = cityId;
        this.address = address;
        this.lon = lon;
        this.lat = lat;
        this.lonTo = lonTo;
        this.latTo = latTo;
        this.status = status;
        this.created = created;
        this.updated = updated;
        this.userId = userId;
        this.addressTo = addressTo;
        this.cityTo = cityTo;
        this.requestTime = requestTime;
        this.view = view;
        this.requestDate = requestDate;
        this.lonUpdate = lonUpdate;
        this.latUpdate = latUpdate;
        this.fromId = fromId;
        this.user = user;
        this.distance = distance;
        this.seats = seats;
        this.duration = duration;
        this.start = start;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLonTo() {
        return lonTo;
    }

    public void setLonTo(String lonTo) {
        this.lonTo = lonTo;
    }

    public String getLatTo() {
        return latTo;
    }

    public void setLatTo(String latTo) {
        this.latTo = latTo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAddressTo() {
        return addressTo;
    }

    public void setAddressTo(String addressTo) {
        this.addressTo = addressTo;
    }

    public String getCityTo() {
        return cityTo;
    }

    public void setCityTo(String cityTo) {
        this.cityTo = cityTo;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getLonUpdate() {
        return lonUpdate;
    }

    public void setLonUpdate(String lonUpdate) {
        this.lonUpdate = lonUpdate;
    }

    public String getLatUpdate() {
        return latUpdate;
    }

    public void setLatUpdate(String latUpdate) {
        this.latUpdate = latUpdate;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public String getSeats() {
        return seats;
    }

    public void setSeats(String seats) {
        this.seats = seats;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
