package com.waslabank.waslabank.models;

public class DailyRideModel {

    private String id;
    private String requestTime;
    private String lon;
    private String lat;
    private String lonTo;
    private String latTo;
    private String address;
    private String addressTo;
    private String weekDay;

    private String status;
    private String userId;
    private String created;


    public DailyRideModel(String id, String requestTime, String lon, String lat, String lonTo, String latTo, String address, String addressTo, String weekDay, String status, String userId, String created) {
        this.id = id;
        this.requestTime = requestTime;
        this.lon = lon;
        this.lat = lat;
        this.lonTo = lonTo;
        this.latTo = latTo;
        this.address = address;
        this.addressTo = addressTo;
        this.weekDay = weekDay;
        this.status = status;
        this.userId = userId;
        this.created = created;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressTo() {
        return addressTo;
    }

    public void setAddressTo(String addressTo) {
        this.addressTo = addressTo;
    }

    public String getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(String weekDay) {
        this.weekDay = weekDay;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
}
