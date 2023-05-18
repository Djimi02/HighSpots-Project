package com.example.highspots.models;

import com.example.highspots.enums.Feature;
import com.example.highspots.enums.Pot;

import java.util.ArrayList;
import java.util.List;

public class Spot {

    private List<String> features = new ArrayList<>();

    private String location;

    public Spot() { }

    public Spot(List<String> features,String location) {
        this.features = features;
        this.location = location;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void addFeature(String feature) {
        this.features.add(feature);
    }
}
