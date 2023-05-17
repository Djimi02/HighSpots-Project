package com.example.highspots.models;

import com.example.highspots.enums.Features;
import com.example.highspots.enums.Pot;

import java.util.ArrayList;
import java.util.List;

public class Spot {

    private List<Features> features = new ArrayList<>();
    private List<Pot> pots = new ArrayList<>();

    public Spot() { }

    public Spot(List<Features> features, List<Pot> pots) {
        this.features = features;
        this.pots = pots;
    }

    public List<Features> getFeatures() {
        return features;
    }

    public void setFeatures(List<Features> features) {
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

    public void addFeature(Features feature) {
        this.features.add(feature);
    }
}
