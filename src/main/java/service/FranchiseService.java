package service;

import model.Franchise;
import repository.FranchiseRepository;
import util.PasswordUtil;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class FranchiseService {

    private FranchiseRepository franchiseRepository;

    public FranchiseService(FranchiseRepository franchiseRepository) {
        this.franchiseRepository = franchiseRepository;
    }

    public Franchise registerFranchise(String username,String password,String name,
                                       String location,String trainingDate,int availableSpots,String roleCount) {

        validate(username,password,name,location,trainingDate,availableSpots,roleCount);
        if (LocalDate.parse(trainingDate).isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Training date cannot be in the past.");
        }

        if (franchiseRepository.findByUsername(username) != null) {
            return null;
        }
        if (franchiseRepository.findByName(name) != null) {
            return null;
        }

        Franchise franchise = new Franchise(0,username,PasswordUtil.hash(password),false,name,
                location,trainingDate,availableSpots,roleCount);

        if (!franchiseRepository.save(franchise)) {
            return null;
        }
        return franchise;
    }

    public Franchise registerDefaultFranchise(String username,String password,String name,
                                              String location,String trainingDate,
                                              int availableSpots,String roleCount) {
        validate(username,password,name,location,trainingDate,availableSpots,roleCount);

        if (franchiseRepository.findByUsername(username) != null) {
            return null;
        }
        if (franchiseRepository.findByName(name) != null) {
            return null;
        }

        Franchise franchise = new Franchise(0,username,PasswordUtil.hash(password),true,
                name,location,trainingDate,availableSpots,roleCount);
        if (!franchiseRepository.saveDefaultFranchise(franchise)) {
            return null;
        }
        return franchise;
    }

    public Franchise login(String username,String password) {

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return null;
        }

        Franchise franchise = franchiseRepository.findByUsername(username);

        if (franchise == null) {
            return null;
        }
        if (PasswordUtil.matches(password,franchise.getPassword())) {
            return franchise;
        }
        return null;
    }

    public boolean changePassword(Franchise franchise,String newPassword) {
        if (franchise == null) {
            return false;
        }
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
        if (newPassword.length() < 6) {
            throw new IllegalArgumentException("Password must contain at least 6 characters.");
        }

        String hashedPassword = PasswordUtil.hash(newPassword);
        boolean updated = franchiseRepository.updatePassword(franchise.getFranchiseId(), hashedPassword);
        if (updated) {
            franchise.setFirstLogin(false);
            return true;
        }
        return false;
    }

    public Franchise getFranchise(int franchiseId) {
        return franchiseRepository.findById(franchiseId);
    }

    public List<Franchise> getAllFranchises() {
        return franchiseRepository.findAll();
    }

    public List<Franchise> getActiveFranchises() {
        return franchiseRepository.findActiveFranchises();
    }

    public boolean isActiveFranchise(int franchiseId) {
        Franchise franchise = franchiseRepository.findById(franchiseId);
        if (franchise == null) {
            return false;
        }
        return !LocalDate.parse(franchise.getTrainingDate()).isBefore(LocalDate.now());
    }

    public boolean updateFranchise(Franchise franchise) {

        validateUpdate(franchise);
        return franchiseRepository.update(franchise);
    }

    public Franchise getFranchiseByName(String name) {
        return franchiseRepository.findByName(name);
    }

    public boolean clearFranchises() {
        return franchiseRepository.deleteAll();
    }

    private void validate(String username,String password,String name,String location,String trainingDate,
                          int availableSpots,String roleCount) {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Franchise name cannot be empty.");
        }
        if (location == null || location.isBlank()) {
            throw new IllegalArgumentException("Location cannot be empty.");
        }
        validateDate(trainingDate);
        if (availableSpots < 1) {
            throw new IllegalArgumentException("Available spots must be greater than 0.");
        }

        validateRoleCount(roleCount,availableSpots);
    }

    private void validateUpdate(Franchise franchise) {
        if (franchise == null) {
            throw new IllegalArgumentException("Franchise cannot be null.");
        }
        if (franchise.getName() == null || franchise.getName().isBlank()) {
            throw new IllegalArgumentException("Franchise name cannot be empty.");
        }
        if (franchise.getLocation() == null || franchise.getLocation().isBlank()) {
            throw new IllegalArgumentException("Location cannot be empty.");
        }
        Franchise existing = franchiseRepository.findByName(franchise.getName());
        if (existing != null && existing.getFranchiseId() != franchise.getFranchiseId()) {
            throw new IllegalArgumentException("Franchise name already exists.");
        }
        validateDate(franchise.getTrainingDate());
        validateRoleCount(franchise.getRoleCount(),franchise.getAvailableSpots());
    }

    private void validateDate(String date) {
        try {
            LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Date must be in YYYY-MM-DD format.");
        }
    }

    private void validateRoleCount(String roleCount,int availableSpots) {
        if (roleCount == null || roleCount.isBlank()) {
            throw new IllegalArgumentException("Role count cannot be empty.");
        }
        String[] values = roleCount.split("-");
        if (values.length != 3) {
            throw new IllegalArgumentException("Role count must be in format: batsman-bowler-allrounder");
        }
        try {
            int batsman = Integer.parseInt(values[0].trim());
            int bowler = Integer.parseInt(values[1].trim());
            int allRounder = Integer.parseInt(values[2].trim());
            if (batsman < 0 || bowler < 0 || allRounder < 0) {
                throw new IllegalArgumentException("Role counts cannot be negative.");
            }
            if (batsman + bowler + allRounder != availableSpots) {
                throw new IllegalArgumentException("Role count total must match available spots.");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Role count must contain valid numbers.");
        }
    }
}
