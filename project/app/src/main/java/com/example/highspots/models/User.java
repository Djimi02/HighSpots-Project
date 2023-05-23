package com.example.highspots.models;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String dbID;
    private String nickName;
    private String email;
    private List<String> foundSpots = new ArrayList<>();
    private List<String> visitedSpots = new ArrayList<>();
    private List<String> ratedSpots = new ArrayList<>();
    private int numberOfDoneRatings = 0;

    public User() { }

    public User(String dbID, String nickName, String email) {
        this.dbID = dbID;
        this.nickName = nickName;
        this.email = email;
    }

    public String getDbID() {
        return dbID;
    }

    public void setDbID(String dbID) {
        this.dbID = dbID;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getFoundSpots() {
        return foundSpots;
    }

    public void setFoundSpots(List<String> foundSpots) {
        this.foundSpots = foundSpots;
    }

    public List<String> getVisitedSpots() {
        return visitedSpots;
    }

    public void setVisitedSpots(List<String> visitedSpots) {
        this.visitedSpots = visitedSpots;
    }

    public void addFoundSpot(String foundSpot) {
        this.foundSpots.add(foundSpot);
    }

    public void removeFoundSpot(String foundSpot) {
        this.foundSpots.remove(foundSpot);
    }

    public void addVisitedSpot(String visitedSpot) {
        this.visitedSpots.add(visitedSpot);
    }

    public void removeVisitedSpot(String visitedSpot) {
        this.visitedSpots.remove(visitedSpot);
    }

    public int getNumberOfDoneRatings() {
        return numberOfDoneRatings;
    }

    public void setNumberOfDoneRatings(int numberOfDoneRatings) {
        this.numberOfDoneRatings = numberOfDoneRatings;
    }

    public List<String> getRatedSpots() {
        return ratedSpots;
    }

    public void setRatedSpots(List<String> ratedSpots) {
        this.ratedSpots = ratedSpots;
    }

    public void addRatedSpot(String newlyRatedSpot) {
        this.ratedSpots.add(newlyRatedSpot);
    }

    public void incrementNumberOfDoneRatings() {
        this.numberOfDoneRatings++;
    }
}
