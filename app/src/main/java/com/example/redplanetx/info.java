package com.example.redplanetx;

public class info {
    private int sol;
    private String earthDate;
    private int numPhotos;

    public info(int sol, String earthDate, int numPhotos) {
        this.sol = sol;
        this.earthDate = earthDate;
        this.numPhotos = numPhotos;
    }

    public int getSol() {
        return sol;
    }

    public String getEarthDate() {
        return earthDate;
    }

    public int getNumPhotos() {
        return numPhotos;
    }
}
