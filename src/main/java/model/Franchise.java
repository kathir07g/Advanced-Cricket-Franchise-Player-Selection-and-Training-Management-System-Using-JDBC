package model;

public class Franchise {

    private int franchiseId;

    private String username;
    private String password;
    private boolean firstLogin;

    private String name;
    private String location;
    private String trainingDate;

    private int availableSpots;
    private String roleCount;

    private int batsmanCount;
    private int bowlerCount;
    private int allRounderCount;

    public Franchise(int franchiseId,String username,String password,boolean firstLogin,
                     String name,String location,String trainingDate,
                     int availableSpots,String roleCount) {
        this.franchiseId = franchiseId;
        this.username = username;
        this.password = password;
        this.firstLogin = firstLogin;
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

    public boolean isFirstLogin() {
        return firstLogin;
    }

    public void setFirstLogin(boolean firstLogin) {
        this.firstLogin = firstLogin;
    }

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

    public void setAvailableSpots(int availableSpots) {
        if (availableSpots < 1) {
            throw new IllegalArgumentException("Available spots must be greater than 0.");
        }
        this.availableSpots = availableSpots;
    }

    public void setRoleCount(String roleCount) {

        if (roleCount == null || roleCount.isBlank()) {
            throw new IllegalArgumentException("Role count cannot be empty.");
        }

        String[] rolesCount = roleCount.split("-");
        if (rolesCount.length != 3) {
            throw new IllegalArgumentException("Role count must be in format: batsman-bowler-allrounder");
        }

        try {
            int batsman = Integer.parseInt(rolesCount[0].trim());
            int bowler = Integer.parseInt(rolesCount[1].trim());
            int allRounder = Integer.parseInt(rolesCount[2].trim());

            if (batsman < 0 || bowler < 0 || allRounder < 0) {
                throw new IllegalArgumentException("Role counts cannot be negative.");
            }

            if (batsman + bowler + allRounder != availableSpots) {
                throw new IllegalArgumentException("Role count total must match available spots.");
            }

            this.batsmanCount = batsman;
            this.bowlerCount = bowler;
            this.allRounderCount = allRounder;
            this.roleCount = batsman + "-" + bowler + "-" + allRounder;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Role count must contain valid numbers.");
        }
    }

    public void setFranchiseId(int franchiseId) { this.franchiseId = franchiseId; }
}
