package model;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Player {

    private int playerId;

    private String username;
    private String password;

    private String name;
    private String DOB;
    private String role;
    private String strength;
    private String bestFigure;
    private int experience;

    private Integer totalRuns;
    private Integer totalWickets;

    // A player can register with multiple franchises
    private List<Integer> franchiseIds;

    public Player(int playerId, String username, String password, String name, String DOB,
                  String role, String strength, String bestFigure, int experience,
                  Integer totalRuns, Integer totalWickets, List<Integer> franchiseIds) {

        this.playerId = playerId;
        this.username = username;
        this.password = password;
        this.name = name;
        this.DOB = DOB;
        this.role = role;
        this.strength = strength;
        this.bestFigure = bestFigure;
        this.experience = experience;
        this.totalRuns = totalRuns;
        this.totalWickets = totalWickets;

        if (franchiseIds == null) {
            this.franchiseIds = new ArrayList<>();
        }
        else {
            this.franchiseIds = new ArrayList<>(franchiseIds);
        }
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getDOB() {
        return DOB;
    }

    public String getRole() {
        return role;
    }

    public String getStrength() {
        return strength;
    }

    public String getBestFigure() {
        return bestFigure;
    }

    public int getExperience() {
        return experience;
    }

    public Integer getTotalRuns() {
        return totalRuns;
    }

    public Integer getTotalWickets() {
        return totalWickets;
    }

    public List<Integer> getFranchiseIds() {
        return new ArrayList<>(franchiseIds);
    }

    public void addFranchiseId(int franchiseId) {
        if (!franchiseIds.contains(franchiseId)) {
            franchiseIds.add(franchiseId);
        }
    }

    public boolean isRegisteredWithFranchise(int franchiseId) {
        return franchiseIds.contains(franchiseId);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }

    public void setBestFigure(String bestFigure) {
        this.bestFigure = bestFigure;
    }

    public int getAge() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate birthDate = LocalDate.parse(DOB, formatter);

        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public boolean isEligible() {
        if (role == null) {
            return false;
        }
        if (role.equalsIgnoreCase("Batsman")) {
            return totalRuns != null && totalRuns >= 2000;
        }
        if (role.equalsIgnoreCase("Bowler")) {
            return totalWickets != null && totalWickets >= 90;
        }
        if (role.equalsIgnoreCase("AllRounder") || role.equalsIgnoreCase("All Rounder")) {
            return (totalRuns != null && totalRuns >= 1500) || (totalWickets != null && totalWickets >= 90);
        }
        return false;
    }

}