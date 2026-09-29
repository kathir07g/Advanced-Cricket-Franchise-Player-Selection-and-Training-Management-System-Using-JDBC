package controller;

import model.Franchise;
import model.Player;
import service.FranchiseService;
import service.PlayerService;
import service.SelectionService;
import util.PasswordUtil;
import view.LoginView;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class LoginController {

    private static final String ADMIN_PIN = System.getenv("CRICKET_ADMIN_PIN");
    private PlayerService playerService;
    private FranchiseService franchiseService;
    private SelectionService selectionService;

    private PlayerController playerController;
    private FranchiseController franchiseController;

    private LoginView loginView = new LoginView();

    private Scanner scanner;

    public LoginController(PlayerService playerService,FranchiseService franchiseService,SelectionService selectionService,
                           PlayerController playerController,FranchiseController franchiseController,Scanner scanner) {
        this.playerService = playerService;
        this.franchiseService = franchiseService;
        this.selectionService = selectionService;
        this.playerController = playerController;
        this.franchiseController = franchiseController;
        this.scanner = scanner;
    }

    public void start() {

        while (true) {
            loginView.showHomeMenu();
            System.out.print("Choose: ");
            int choice = readInt();

            switch (choice) {
                case 1:
                    adminLogin();
                    break;
                case 2:
                    playerLogin();
                    break;
                case 3:
                    franchiseLogin();
                    break;
                case 4:
                    playerController.registerPlayerFlow();
                    break;
                case 5:
                    franchiseController.registerFranchiseFlow();
                    break;
                case 6:
                    System.out.println("Thank you for using the system.");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void addDefaultFranchise() {
        System.out.println();
        System.out.println("======= ADD DEFAULT FRANCHISE =======");
        System.out.println("1. CSK");
        System.out.println("2. MI");
        System.out.println("3. SRH");
        System.out.println("4. Back");

        System.out.print("Choose: ");
        int choice = readInt();

        switch (choice) {
            case 1:
                addCSK();
                break;
            case 2:
                addMI();
                break;
            case 3:
                addSRH();
                break;
            case 4:
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private void adminLogin() {
        System.out.println();
        System.out.println("========== ADMIN LOGIN ==========");

        if (ADMIN_PIN == null || ADMIN_PIN.isBlank()) {
            System.out.println("Admin PIN is not configured.");
            System.out.println("Please set the CRICKET_ADMIN_PIN environment variable.");
            return;
        }

        System.out.print("Enter Admin PIN: ");
        String pin = scanner.nextLine().trim();

        if (!pin.equals(ADMIN_PIN)) {
            System.out.println("Invalid Admin PIN.");
            return;
        }

        System.out.println("Admin login successful.");
        adminMenu();
    }

    private void adminMenu() {

        while (true) {
            System.out.println();
            System.out.println("========== ADMIN MENU ==========");
            System.out.println("1. Show Registered Players");
            System.out.println("2. Show Registered Franchises");
            System.out.println("3. Show Selected Players");
            System.out.println("4. Add Default Franchise");
            System.out.println("5. Clear Data");
            System.out.println("6. Back");

            System.out.print("Choose: ");
            int choice = readInt();

            switch (choice) {
                case 1:
                    showRegisteredPlayers();
                    break;
                case 2:
                    showRegisteredFranchises();
                    break;
                case 3:
                    showSelectedPlayers();
                    break;
                case 4:
                    addDefaultFranchise();
                    break;
                case 5:
                    clearData();
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void playerLogin() {
        selectionService.releaseExpiredSelections();

        System.out.println();
        System.out.println("========== PLAYER LOGIN ==========");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        Player player = playerService.login(username,password);
        if (player == null) {
            loginView.loginFailed();
            return;
        }

        System.out.println("Login successful. Welcome " + player.getName());
        playerController.startPlayerMenu(player);
    }

    private void franchiseLogin() {
        System.out.println();
        System.out.println("======== FRANCHISE LOGIN ========");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        Franchise franchise = franchiseService.login(username,password);
        if (franchise == null) {
            loginView.loginFailed();
            return;
        }
        if (franchise.isFirstLogin()) {
            changeDefaultFranchisePassword(franchise);
            return;
        }
        System.out.println("Login successful. Welcome " + franchise.getName());
        franchiseController.startFranchiseMenu(franchise);
    }

    private void changeDefaultFranchisePassword(Franchise franchise) {
        System.out.println();
        System.out.println("========================================");
        System.out.println("FIRST LOGIN - PASSWORD CHANGE REQUIRED");
        System.out.println("========================================");
        System.out.println("You are using the temporary password.");
        System.out.println("Please create a new password.");
        while (true) {
            System.out.print("Enter new password: ");
            String newPassword = scanner.nextLine().trim();

            System.out.print("Confirm new password: ");
            String confirmPassword = scanner.nextLine().trim();

            if (!newPassword.equals(confirmPassword)) {
                System.out.println("Passwords do not match.");
                continue;
            }
            if (PasswordUtil.matches(newPassword,franchise.getPassword())) {
                System.out.println("New password must be different from the current password.");
                continue;
            }
            try {
                if (franchiseService.changePassword(franchise,newPassword)) {
                    System.out.println("Password changed successfully.");
                    System.out.println("Please login again using your new password.");
                    return;
                }
                System.out.println("Failed to change password.");
                return;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void showRegisteredPlayers() {
        selectionService.releaseExpiredSelections();
        List<Franchise> franchises = franchiseService.getActiveFranchises();

        if (franchises.isEmpty()) {
            System.out.println("No franchises available.");
            return;
        }

        System.out.println();
        System.out.println("======= REGISTERED PLAYERS =======");
        for (Franchise franchise : franchises) {
            System.out.println();
            System.out.println("Franchise: " + franchise.getName());
            System.out.println("Franchise ID: " + franchise.getFranchiseId());

            List<Player> players = playerService.getPlayersByFranchise(franchise.getFranchiseId());
            if (players.isEmpty()) {
                System.out.println("No players registered.");
                continue;
            }

            int count = 1;
            for (Player player : players) {
                System.out.println(count++ + ". " + player.getName() + " | Player ID: " + player.getPlayerId() + " | Role: " + player.getRole());
            }
        }
    }

    private void showRegisteredFranchises() {
        selectionService.releaseExpiredSelections();

        List<Franchise> franchises = franchiseService.getActiveFranchises();
        if (franchises.isEmpty()) {
            System.out.println("No franchises registered.");
            return;
        }

        System.out.println();
        System.out.println("======= REGISTERED FRANCHISES =======");
        int count = 1;
        for (Franchise franchise : franchises) {
            System.out.println();
            System.out.println(count++ + ". " + franchise.getName());
            System.out.println("Franchise ID : " + franchise.getFranchiseId());
            System.out.println("Location     : " + franchise.getLocation());
            System.out.println("Training Date: " + franchise.getTrainingDate());
            System.out.println("Available Spots: " + franchise.getAvailableSpots());
            System.out.println("Role Count   : " + franchise.getRoleCount());
        }
    }

    private void showSelectedPlayers() {
        selectionService.releaseExpiredSelections();
        List<Franchise> franchises = franchiseService.getActiveFranchises();

        if (franchises.isEmpty()) {
            System.out.println("No franchises available.");
            return;
        }

        System.out.println();
        System.out.println("======= SELECTED PLAYERS =======");
        for (Franchise franchise : franchises) {
            System.out.println();
            System.out.println("Franchise: " + franchise.getName());
            System.out.println("Franchise ID: " + franchise.getFranchiseId());

            if (!selectionService.isSelectionDone(franchise.getFranchiseId())) {
                System.out.println("Selection has not been completed.");
                continue;
            }

            List<Player> players = selectionService.getSelectedPlayers(franchise.getFranchiseId());
            if (players.isEmpty()) {
                System.out.println("No players selected.");
                continue;
            }

            int count = 1;
            for (Player player : players) {
                System.out.println(count++ + ". " + player.getName() + " | Player ID: " + player.getPlayerId() + " | Role: " + player.getRole());
            }
        }
    }

    private void clearData() {
        System.out.println();
        System.out.println("========== CLEAR ALL DATA ==========");
        System.out.println("1. Clear Players");
        System.out.println("2. Clear Franchises");
        System.out.println("3. Clear All");
        System.out.println("4. Back");

        System.out.print("Choose: ");
        int choice = readInt();

        if (choice == 4) {
            return;
        }

        System.out.print("Type YES to confirm: ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("YES")) {
            System.out.println("Clear operation cancelled.");
            return;
        }

        switch (choice) {
            case 1:
                System.out.println(playerService.clearPlayers() ? "All player data cleared." : "Failed to clear player data.");
                break;
            case 2:
                System.out.println(franchiseService.clearFranchises() ? "All franchise data cleared." : "Failed to clear franchise data.");
                break;
            case 3:
                System.out.println(selectionService.clearAll() ? "All data cleared." : "Failed to clear all data.");
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private void addCSK() {
        if (franchiseService.getFranchiseByName("CSK") != null) {
            System.out.println("CSK is already registered.");
            return;
        }
        String trainingDate = LocalDate.now().plusDays(7).toString();
        addDefault("csk","csk123","CSK","Chepauk Stadium, Chennai",trainingDate,10,"5-3-2");
    }

    private void addMI() {
        if (franchiseService.getFranchiseByName("MI") != null) {
            System.out.println("MI is already registered.");
            return;
        }
        String trainingDate = LocalDate.now().plusDays(10).toString();
        addDefault("mi","mi123","MI","Wankhede Stadium, Mumbai",trainingDate,7,"3-2-2");
    }

    private void addSRH() {
        if (franchiseService.getFranchiseByName("SRH") != null) {
            System.out.println("SRH is already registered.");
            return;
        }
        String trainingDate = LocalDate.now().plusDays(14).toString();
        addDefault("srh","srh123","SRH","Rajiv Gandhi International Cricket Stadium, Hyderabad",trainingDate,8,"3-2-3");
    }

    private void addDefault(String username,String password,String name,String location,String date,int spots,String roleCount) {
        try {
            Franchise franchise = franchiseService.registerDefaultFranchise(username,password,name,location,date,spots,roleCount);
            if (franchise != null) {
                System.out.println(name + " added successfully.");
                System.out.println("Franchise ID: " + franchise.getFranchiseId());
            }
            else {
                System.out.println(name + " could not be added.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (Exception e) {
                System.out.print("Enter a valid number: ");
            }
        }
    }
}
