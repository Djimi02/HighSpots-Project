package com.example.highspots.models;

import com.example.highspots.enums.Feature;
import com.example.highspots.enums.Pot;

import java.util.ArrayList;
import java.util.List;

public class Spot {

    private List<Feature> features = new ArrayList<>();
    private List<Pot> pots = new ArrayList<>();

    public Spot() { }

    public Spot(List<Feature> features, List<Pot> pots) {
        this.features = features;
        this.pots = pots;
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

    public void addPot(Pot pot) {
        this.pots.add(pot);
    }

    public void addFeature(Feature feature) {
        this.features.add(feature);
    }
}
