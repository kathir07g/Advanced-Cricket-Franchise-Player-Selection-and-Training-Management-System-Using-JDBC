package controller;

import model.Franchise;
import model.Player;
import service.FranchiseService;
import service.PlayerService;
import service.SelectionService;
import view.LoginView;

import java.util.List;
import java.util.Scanner;

public class LoginController {

    private static final String ADMIN_PIN = "Your Pin";
    private PlayerService playerService;
    private FranchiseService franchiseService;
    private SelectionService selectionService;

    private PlayerController playerController;
    private FranchiseController franchiseController;

    private LoginView loginView = new LoginView();

    private Scanner scanner = new Scanner(System.in);

    public LoginController(PlayerService playerService, FranchiseService franchiseService, SelectionService selectionService,
                           PlayerController playerController, FranchiseController franchiseController) {

        this.playerService = playerService;
        this.franchiseService = franchiseService;
        this.selectionService = selectionService;
        this.playerController = playerController;
        this.franchiseController = franchiseController;
    }

    public void start() {

        while (true) {
            loginView.showHomeMenu();
            System.out.println("Choose: ");
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
                    addDefaultFranchise();
                    break;
                case 7:
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
            System.out.println("4. Clear Data");
            System.out.println("5. Back");

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
                    clearData();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void playerLogin() {

        System.out.println();
        System.out.println("========== PLAYER LOGIN ==========");

        System.out.print("Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        Player player = playerService.login(username, password);
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

        Franchise franchise = franchiseService.login(username, password);
        if (franchise == null) {
            loginView.loginFailed();
            return;
        }
        System.out.println("Login successful. Welcome " + franchise.getName());
        franchiseController.startFranchiseMenu(franchise);
    }

    private void showRegisteredPlayers() {
        List<Franchise> franchises = franchiseService.getAllFranchises();

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
        List<Franchise> franchises = franchiseService.getAllFranchises();

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
        List<Franchise> franchises = franchiseService.getAllFranchises();

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

        switch (choice) {
            case 1:
                playerService.clearPlayers();
                System.out.println("All player data cleared.");
                break;
            case 2:
                franchiseService.clearFranchises();
                System.out.println("All franchise data cleared.");
                break;
            case 3:
                selectionService.clearSelections();
                playerService.clearPlayers();
                franchiseService.clearFranchises();
                System.out.println("All data cleared.");
                break;
            case 4:
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private void addCSK() {

        if (franchiseService.getFranchiseByName("CSK") != null) {
            System.out.println("CSK is already available.");
            return;
        }

        Franchise franchise = franchiseService.registerFranchise("csk", "csk123", "CSK",
                "Chepauk Stadium, Chennai", "2026-10-01", 10, "5-3-2");
        if (franchise != null) {
            System.out.println("CSK added successfully.");
            System.out.println("Franchise ID: " + franchise.getFranchiseId());
        }
    }

    private void addMI() {
        if (franchiseService.getFranchiseByName("MI") != null) {
            System.out.println("MI is already available.");
            return;
        }

        Franchise franchise = franchiseService.registerFranchise("mi", "mi123", "MI",
                "Wankhede Stadium, Mumbai", "2026-10-05", 12, "6-3-3");
        if (franchise != null) {
            System.out.println("MI added successfully.");
            System.out.println("Franchise ID: " + franchise.getFranchiseId());
        }
    }

    private void addSRH() {
        if (franchiseService.getFranchiseByName("SRH") != null) {
            System.out.println("SRH is already available.");
            return;
        }

        Franchise franchise = franchiseService.registerFranchise("srh", "srh123", "SRH",
                "Rajiv Gandhi Stadium, Hyderabad", "2026-10-03", 8, "4-2-2");
        if (franchise != null) {
            System.out.println("SRH added successfully.");
            System.out.println("Franchise ID: " + franchise.getFranchiseId());
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