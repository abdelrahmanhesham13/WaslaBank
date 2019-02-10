package com.waslabank.waslabank.models;

public class CarModel {
    private String id;
    private String name;
    private String nameAr;


    public CarModel(String id, String name, String nameAr) {
        this.id = id;
        this.name = name;
        this.nameAr = nameAr;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameAr() {
        return nameAr;
    }

    public void setNameAr(String nameAr) {
        this.nameAr = nameAr;
    }
}
