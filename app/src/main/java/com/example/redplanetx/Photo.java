package com.example.redplanetx;

public class Photo {
    private String imgSrc;
    private String earthDate;

    public Photo(String imgSrc, String earthDate) {
        this.imgSrc = imgSrc;
        this.earthDate = earthDate;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public String getEarthDate() {
        return earthDate;
    }
}
