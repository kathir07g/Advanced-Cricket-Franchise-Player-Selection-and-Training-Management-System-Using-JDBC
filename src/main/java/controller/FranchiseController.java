package controller;

import model.Franchise;
import model.Player;
import service.FranchiseService;
import service.PlayerService;
import service.SelectionService;
import view.FranchiseView;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class FranchiseController {

    private FranchiseService franchiseService;
    private PlayerService playerService;
    private SelectionService selectionService;

    private FranchiseView franchiseView = new FranchiseView();

    private Scanner scanner;

    public FranchiseController(FranchiseService franchiseService,PlayerService playerService,
                               SelectionService selectionService,Scanner scanner) {
        this.franchiseService = franchiseService;
        this.playerService = playerService;
        this.selectionService = selectionService;
        this.scanner = scanner;
    }

    public void registerFranchiseFlow() {
        System.out.println();
        System.out.println("====== FRANCHISE REGISTRATION ======");

        System.out.print("Username: ");
        String username = readLine();

        System.out.print("Password: ");
        String password = readLine();

        System.out.print("Franchise Name: ");
        String name = readLine();

        System.out.print("Location: ");
        String location = readLine();

        System.out.print("Training Date (YYYY-MM-DD): ");
        String date = readLine();

        System.out.print("Available Spots: ");
        int spots = readInt();

        System.out.print("Role Count (batsman-bowler-allrounder): ");
        String roleCount = readLine();

        try {
            Franchise franchise = franchiseService.registerFranchise(username,password,name,
                    location,date,spots,roleCount);

            if (franchise == null) {
                System.out.println("Username or franchise name already exists, or registration failed.");
                return;
            }

            System.out.println();
            System.out.println("========================================");
            System.out.println("Franchise Registration Successful");
            System.out.println("Franchise ID: " + franchise.getFranchiseId());
            System.out.println("========================================");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    public void startFranchiseMenu(Franchise franchise) {
        selectionService.releaseExpiredSelections();

        while (true) {
            System.out.println();
            System.out.println("========== FRANCHISE MENU ==========");
            System.out.println("Franchise: " + franchise.getName());
            System.out.println("1. View Franchise Profile");
            System.out.println("2. View Registered Players");
            System.out.println("3. Run Player Selection");
            System.out.println("4. View Selected Players");
            System.out.println("5. Training Management");
            System.out.println("6. Update Franchise Details");
            System.out.println("7. Logout");

            System.out.print("Choose: ");
            int choice = readInt();

            switch (choice) {
                case 1:
                    franchiseView.displayFranchise(franchise);
                    break;
                case 2:
                    showRegisteredPlayers(franchise);
                    break;
                case 3:
                    runSelection(franchise);
                    break;
                case 4:
                    showSelectedPlayers(franchise);
                    break;
                case 5:
                    trainingManagement(franchise);
                    break;
                case 6:
                    updateFranchise(franchise);
                    break;
                case 7:
                    System.out.println("Logged out.");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void showRegisteredPlayers(Franchise franchise) {
        List<Player> players = playerService.getPlayersByFranchise(franchise.getFranchiseId());
        franchiseView.displayRegisteredPlayers(players);
    }

    private void runSelection(Franchise franchise) {

        if (franchise.getTrainingDate() == null || LocalDate.parse(franchise.getTrainingDate()).isBefore(LocalDate.now())) {
            System.out.println();
            System.out.println("Training date has expired. Player selection cannot be run.");
            return;
        }
        if (selectionService.isSelectionDone(franchise.getFranchiseId())) {
            System.out.println();
            System.out.println("Selection has already been completed for " + franchise.getName());
            return;
        }
        if (selectionService.runSelection(franchise)) {
            System.out.println();
            System.out.println("Player selection completed.");
        }
        else {
            System.out.println();
            System.out.println("Player selection failed. Please check the database connection and try again.");
            return;
        }

        List<Player> selectedPlayers = selectionService.getSelectedPlayers(franchise.getFranchiseId());
        franchiseView.displaySelectedPlayers(selectedPlayers,true);
    }

    private void showSelectedPlayers(Franchise franchise) {
        boolean selectionDone = selectionService.isSelectionDone(franchise.getFranchiseId());
        List<Player> selectedPlayers = selectionService.getSelectedPlayers(franchise.getFranchiseId());
        franchiseView.displaySelectedPlayers(selectedPlayers,selectionDone);
    }

    private void trainingManagement(Franchise franchise) {
        System.out.println();
        System.out.println("======= TRAINING MANAGEMENT =======");
        System.out.println("1. View Training Details");
        System.out.println("2. Update Training Date");
        System.out.println("3. Update Training Location");
        System.out.println("4. Back");

        System.out.print("Choose: ");
        int choice = readInt();

        switch (choice) {
            case 1:
                franchiseView.displayTrainingDetails(franchise);
                break;
            case 2:
                String oldDate = franchise.getTrainingDate();
                try {
                    System.out.print("Enter new training date: ");
                    franchise.setTrainingDate(readLine());
                    if (franchiseService.updateFranchise(franchise)) {
                        System.out.println("Training date updated.");
                    }
                    else {
                        franchise.setTrainingDate(oldDate);
                        System.out.println("Training date update failed.");
                    }
                } catch (IllegalArgumentException e) {
                    franchise.setTrainingDate(oldDate);
                    System.out.println(e.getMessage());
                }
                break;
            case 3:
                String oldLocation = franchise.getLocation();
                try {
                    System.out.print("Enter new training location: ");
                    franchise.setLocation(readLine());
                    if (franchiseService.updateFranchise(franchise)) {
                        System.out.println("Training location updated.");
                    }
                    else {
                        franchise.setLocation(oldLocation);
                        System.out.println("Training location update failed.");
                    }
                } catch (IllegalArgumentException e) {
                    franchise.setLocation(oldLocation);
                    System.out.println(e.getMessage());
                }
                break;
            case 4:
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private void updateFranchise(Franchise franchise) {
        System.out.println();
        System.out.println("======= UPDATE FRANCHISE =======");
        System.out.println("Choose the Field to Update:");
        System.out.println("1. updateName");
        System.out.println("2. updateAvailableSpotsAndRoleCount");
        System.out.println("3. Back");

        int choice = readInt();

        switch (choice) {
            case 1:
                String oldName = franchise.getName();
                try {
                    System.out.print("New franchise name: ");
                    franchise.setName(readLine());
                    if (franchiseService.updateFranchise(franchise)) {
                        System.out.println("Franchise Name updated successfully.");
                    }
                    else {
                        franchise.setName(oldName);
                        System.out.println("Franchise Name update failed.");
                    }
                } catch (IllegalArgumentException e) {
                    franchise.setName(oldName);
                    System.out.println(e.getMessage());
                }
                break;
            case 2:
                int oldSpots = franchise.getAvailableSpots();
                String oldRoleCount = franchise.getRoleCount();
                try {
                    System.out.print("New Available Spots: ");
                    int newSpots = readInt();
                    System.out.print("New role count: ");
                    String newRoleCount = readLine();
                    franchise.setAvailableSpots(newSpots);
                    franchise.setRoleCount(newRoleCount);
                    if (franchiseService.updateFranchise(franchise)) {
                        System.out.println("Franchise Available Spots & Role Count updated successfully.");
                    }
                    else {
                        franchise.setAvailableSpots(oldSpots);
                        franchise.setRoleCount(oldRoleCount);
                        System.out.println("Franchise update failed.");
                    }
                } catch (IllegalArgumentException e) {
                    franchise.setAvailableSpots(oldSpots);
                    franchise.setRoleCount(oldRoleCount);
                    System.out.println(e.getMessage());
                }
                break;
            case 3:
                break;
            default:
                System.out.println("Invalid Choice");
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

    private String readLine() {
        return scanner.nextLine().trim();
    }
}
