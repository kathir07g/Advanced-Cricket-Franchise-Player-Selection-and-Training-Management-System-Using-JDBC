package model;

public class Franchise {

    private int franchiseId;

    private String username;
    private String password;

    private String name;
    private String location;
    private String trainingDate;

    private int availableSpots;
    private String roleCount;

    private int batsmanCount;
    private int bowlerCount;
    private int allRounderCount;

    public Franchise(int franchiseId, String username, String password, String name,
                     String location, String trainingDate, int availableSpots, String roleCount) {

        this.franchiseId = franchiseId;
        this.username = username;
        this.password = password;
        this.name = name;
        this.location = location;
        this.trainingDate = trainingDate;
        this.availableSpots = availableSpots;

        setRoleCount(roleCount);
    }

    public int getFranchiseId() { return franchiseId; }

    public String getUsername() { return username; }

    public String getPassword() { return password; }

    public String getName() { return name; }

    public String getLocation() { return location; }

    public int getAvailableSpots() { return availableSpots; }

    public String getTrainingDate() { return trainingDate; }

    public String getRoleCount() { return roleCount; }

    public int getBatsmanCount() { return batsmanCount; }

    public int getBowlerCount() { return bowlerCount; }

    public int getAllRounderCount() { return allRounderCount; }

    public void setName(String name) { this.name = name; }

    public void setLocation(String location) { this.location = location; }

    public void setTrainingDate(String trainingDate) { this.trainingDate = trainingDate; }

    public void setAvailableSpots(int availableSpots) { this.availableSpots = availableSpots; }

    public void setRoleCount(String roleCount) {

        this.roleCount = roleCount;
        String[] rolesCount = roleCount.split("-");

        if (rolesCount.length != 3) {
            throw new IllegalArgumentException("Role count must be in format: batsman-bowler-allrounder");
        }
        this.batsmanCount = Integer.parseInt(rolesCount[0]);
        this.bowlerCount = Integer.parseInt(rolesCount[1]);
        this.allRounderCount = Integer.parseInt(rolesCount[2]);
    }

    public void setFranchiseId(int franchiseId) {
        this.franchiseId = franchiseId;
    }

}