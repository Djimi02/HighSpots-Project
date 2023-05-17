package com.example.highspots.models;

import com.example.highspots.enums.Feature;
import com.example.highspots.enums.Pot;

import java.util.ArrayList;
import java.util.List;

public class Spot {

    private List<Feature> features = new ArrayList<>();
    private List<Pot> pots = new ArrayList<>();

    private String location;

    public Spot() { }

    public Spot(List<Feature> features, List<Pot> pots, String location) {
        this.features = features;
        this.pots = pots;
        this.location = location;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    public List<Pot> getPots() {
        return pots;
    }

    public void setPots(List<Pot> pots) {
        this.pots = pots;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void addPot(Pot pot) {
        this.pots.add(pot);
    }

    public void addFeature(Feature feature) {
        this.features.add(feature);
    }
}
