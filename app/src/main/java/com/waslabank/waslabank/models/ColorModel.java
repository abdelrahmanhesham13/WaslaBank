package com.waslabank.waslabank.models;

public class ColorModel {
    private String name;
    private String nameAr;
    private String id;

    public ColorModel(String name, String nameAr, String id) {
        this.name = name;
        this.nameAr = nameAr;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
