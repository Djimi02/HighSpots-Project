package com.example.highspots.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Spot implements Serializable {

    private List<String> features = new ArrayList<>();
    private String location;
    private String dbID;
    private double rating;
    private int numberOfRatings;
    private List<String> visitors;
    private String imageName;
    private String creatorID;

    public Spot() { }

    public Spot(List<String> features, String location, String dbID, double rating, int numberOfRatings, List<String> visitors, String creatorID, String imageName) {
        this.features = features;
        this.location = location;
        this.dbID = dbID;
        this.rating = rating;
        this.numberOfRatings = numberOfRatings;
        this.visitors = visitors;
        this.creatorID = creatorID;
        this.imageName = imageName;
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

    public String getDbID() {
        return dbID;
    }

    public void setDbID(String dbID) {
        this.dbID = dbID;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getNumberOfRatings() {
        return numberOfRatings;
    }

    public void setNumberOfRatings(int numberOfRatings) {
        this.numberOfRatings = numberOfRatings;
    }

    public List<String> getVisitors() {
        return visitors;
    }

    public void setVisitors(List<String> visitors) {
        this.visitors = visitors;
    }

    public void addVisitor(String visitorID) {
        this.visitors.add(visitorID);
    }

    public String getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public void addNewRating(double rating) {
        double newRating = ((this.rating * numberOfRatings) + rating) / (numberOfRatings + 1);
        this.numberOfRatings++;
        this.rating = newRating;
    }
}
