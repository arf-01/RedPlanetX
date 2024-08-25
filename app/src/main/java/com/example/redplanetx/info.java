package com.example.redplanetx;

public class info {
    private int sol;
    private String earthDate;
    private int numPhotos;
    private String roverName;

    public info(int sol, String earthDate, int numPhotos,String roverName) {
        this.sol = sol;
        this.earthDate = earthDate;
        this.numPhotos = numPhotos;
        this.roverName=roverName;
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

    public String getRoverName() {
        return roverName;
    }
}
