package controller;

import model.Franchise;
import model.Player;
import service.FranchiseService;
import service.PlayerService;
import service.SelectionService;
import view.FranchiseView;
import view.PlayerView;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class PlayerController {

    private PlayerService playerService;
    private FranchiseService franchiseService;
    private SelectionService selectionService;

    private PlayerView playerView = new PlayerView();
    private FranchiseView franchiseView = new FranchiseView();

    private Scanner scanner;

    public PlayerController(PlayerService playerService,FranchiseService franchiseService,
                            SelectionService selectionService,Scanner scanner) {
        this.playerService = playerService;
        this.franchiseService = franchiseService;
        this.selectionService = selectionService;
        this.scanner = scanner;
    }

    public void registerPlayerFlow() {
        System.out.println();
        System.out.println("======= PLAYER REGISTRATION =======");

        System.out.print("Username: ");
        String username = readLine();

        System.out.print("Password: ");
        String password = readLine();

        System.out.print("Name: ");
        String name = readLine();

        System.out.print("DOB (YYYY-MM-DD): ");
        String DOB = readLine();

        System.out.print("Role (Batsman/Bowler/AllRounder): ");
        String role = readLine();

        System.out.print("Strength: ");
        String strength = readLine();

        System.out.print("Best Figure: ");
        String bestFigure = readLine();

        System.out.print("Experience: ");
        int experience = readInt();

        Integer runs = null;
        Integer wickets = null;

        if (role.equalsIgnoreCase("Batsman")
                || role.equalsIgnoreCase("AllRounder")
                || role.equalsIgnoreCase("All Rounder")) {
            System.out.print("Total Runs: ");
            runs = readInt();
        }

        if (role.equalsIgnoreCase("Bowler")
                || role.equalsIgnoreCase("AllRounder")
                || role.equalsIgnoreCase("All Rounder")) {
            System.out.print("Total Wickets: ");
            wickets = readInt();
        }

        List<Franchise> franchises = franchiseService.getActiveFranchises();
        Integer franchiseId = null;
        Franchise franchise = null;

        if (franchises.isEmpty()) {
            System.out.println();
            System.out.println("No active franchises are currently available.");
            System.out.println("You can register as a player without selecting a franchise.");
        }
        else {
            System.out.println();
            System.out.println("Choose Franchise:");
            System.out.println("0. Register Without Franchise");
            for (int i = 0; i < franchises.size(); i++) {
                System.out.println((i + 1) + ". " + franchises.get(i).getName() +
                        " | ID: " + franchises.get(i).getFranchiseId());
            }

            int choice = readInt();

            if (choice < 0 || choice > franchises.size()) {
                System.out.println("Invalid franchise choice.");
                return;
            }
            if (choice > 0) {
                franchise = franchises.get(choice - 1);
                franchiseId = franchise.getFranchiseId();
            }
        }

        try {
            Player player = playerService.registerPlayer(username,password,name,DOB,role,
                    strength,bestFigure,experience,runs,wickets,franchiseId);
            if (player == null) {
                System.out.println("Username or registration details already exist.");
                return;
            }

            System.out.println();
            System.out.println("========================================");
            System.out.println("Player Registration Successful");
            System.out.println("Player ID     : " + player.getPlayerId());
            if (franchise != null) {
                System.out.println("Franchise     : " + franchise.getName());
                System.out.println("Franchise ID  : " + franchise.getFranchiseId());
            }
            else {
                System.out.println("Franchise     : Not Registered");
            }
            System.out.println("========================================");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    public void startPlayerMenu(Player player) {

        while (true) {
            System.out.println();
            System.out.println("============ PLAYER MENU ============");
            System.out.println("Logged in as: " + player.getName());
            System.out.println("1. View Profile");
            System.out.println("2. Update Profile");
            System.out.println("3. Register to Another Franchise");
            System.out.println("4. View Registered Franchise");
            System.out.println("5. View Selection Status");
            System.out.println("6. View Training Details");
            System.out.println("7. Logout");

            System.out.print("Choose: ");
            int choice = readInt();

            switch (choice) {
                case 1:
                    playerView.displayPlayer(player);
                    break;
                case 2:
                    updateProfile(player);
                    break;
                case 3:
                    registerToAnotherFranchise(player);
                    break;
                case 4:
                    showRegisteredFranchise(player);
                    break;
                case 5:
                    showSelectionStatus(player);
                    break;
                case 6:
                    showTrainingDetails(player);
                    break;
                case 7:
                    System.out.println("Logged out.");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void registerToAnotherFranchise(Player player) {
        System.out.println();
        System.out.println("===== REGISTER TO ANOTHER FRANCHISE =====");

        selectionService.releaseExpiredSelections();
        player = playerService.getPlayer(player.getPlayerId());

        if (selectionService.getSelectedFranchiseId(player.getPlayerId()) != null) {
            System.out.println("You have already been selected and cannot register to another franchise.");
            return;
        }

        List<Franchise> franchises = franchiseService.getActiveFranchises();
        if (franchises.isEmpty()) {
            System.out.println("No franchises available.");
            return;
        }

        List<Franchise> availableFranchises = new ArrayList<>();
        for (Franchise franchise : franchises) {
            if (!player.isRegisteredWithFranchise(franchise.getFranchiseId())) {
                availableFranchises.add(franchise);
            }
        }
        if (availableFranchises.isEmpty()) {
            System.out.println("You are already registered with all available franchises.");
            return;
        }

        for (int i = 0; i < availableFranchises.size(); i++) {
            Franchise franchise = availableFranchises.get(i);
            System.out.println((i + 1) + ". " + franchise.getName() + " | ID: " + franchise.getFranchiseId());
        }

        System.out.print("Choose Franchise: ");
        int choice = readInt();

        if (choice < 1 || choice > availableFranchises.size()) {
            System.out.println("Invalid franchise choice.");
            return;
        }

        Franchise franchise = availableFranchises.get(choice - 1);
        if (playerService.registerPlayerToFranchise(
                player.getPlayerId(),franchise.getFranchiseId())) {

            System.out.println("Successfully registered to " + franchise.getName() + ".");
        }
        else {
            System.out.println("Unable to register to the franchise.");
        }
    }

    private void updateProfile(Player player) {
        System.out.println();
        System.out.println("========= UPDATE PROFILE =========");
        System.out.println("Choose the Field to Update:");
        System.out.println("1. updateName");
        System.out.println("2. updateStrength");
        System.out.println("3. updateBestFigure");
        System.out.println("4. Back");

        int choice = readInt();
        String oldValue;

        try {
            switch (choice) {
                case 1:
                    oldValue = player.getName();
                    System.out.print("Enter new name: ");
                    player.setName(readLine());
                    if (playerService.updatePlayer(player)) {
                        System.out.println("Name updated successfully.");
                    }
                    else {
                        player.setName(oldValue);
                        System.out.println("Name update failed.");
                    }
                    break;
                case 2:
                    oldValue = player.getStrength();
                    System.out.print("Enter new strength: ");
                    player.setStrength(readLine());
                    if (playerService.updatePlayer(player)) {
                        System.out.println("Strength updated successfully.");
                    }
                    else {
                        player.setStrength(oldValue);
                        System.out.println("Strength update failed.");
                    }
                    break;
                case 3:
                    oldValue = player.getBestFigure();
                    System.out.print("Enter new best figure: ");
                    player.setBestFigure(readLine());
                    if (playerService.updatePlayer(player)) {
                        System.out.println("Best Figure updated successfully.");
                    }
                    else {
                        player.setBestFigure(oldValue);
                        System.out.println("Best Figure update failed.");
                    }
                    break;
                case 4:
                    break;
                default:
                    System.out.println("Invalid Choice");
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void showRegisteredFranchise(Player player) {
        selectionService.releaseExpiredSelections();
        player = playerService.getPlayer(player.getPlayerId());

        List<Integer> franchiseIds = player.getFranchiseIds();

        if (franchiseIds.isEmpty()) {
            System.out.println("You are not registered with a franchise.");
            return;
        }

        System.out.println();
        System.out.println("Registered Franchise: ");
        int count = 1;
        for (Integer franchiseId : franchiseIds) {
            Franchise franchise = franchiseService.getFranchise(franchiseId);
            if (franchise != null) {
                System.out.println(count++ + " ." + franchise.getName());
            }
        }
    }

    private void showSelectionStatus(Player player) {
        selectionService.releaseExpiredSelections();
        player = playerService.getPlayer(player.getPlayerId());

        List<Integer> franchiseIds = player.getFranchiseIds();

        if (franchiseIds.isEmpty()) {
            System.out.println("You are not registered with any franchise.");
            return;
        }

        System.out.println();
        System.out.println("======= SELECTION STATUS =======");

        for (Integer franchiseId : franchiseIds) {
            Franchise franchise = franchiseService.getFranchise(franchiseId);
            if (franchise == null) {
                continue;
            }

            System.out.println();
            System.out.println("Franchise: " + franchise.getName());

            if (!selectionService.isSelectionDone(franchiseId)) {
                System.out.println("Selection Status: SELECTION NOT YET DONE");
            }
            else if (selectionService.isPlayerSelected(franchiseId,player.getPlayerId())) {
                System.out.println("Selection Status: SELECTED");
            }
            else {
                System.out.println("Selection Status: NOT SELECTED");
            }
        }

        Integer selectionFranchiseId = selectionService.getSelectedFranchiseId(player.getPlayerId());
        if (selectionFranchiseId != null) {
            Franchise selectionFranchise = franchiseService.getFranchise(selectionFranchiseId);
            if (selectionFranchise != null) {
                System.out.println();
                System.out.println("Selected Franchise: " + selectionFranchise.getName());
            }
        }
    }

    private void showTrainingDetails(Player player) {
        selectionService.releaseExpiredSelections();
        player = playerService.getPlayer(player.getPlayerId());

        List<Integer> franchiseIds = player.getFranchiseIds();

        if (franchiseIds.isEmpty()) {
            System.out.println("You are not registered with any franchise.");
            return;
        }

        System.out.println();
        System.out.println("======= TRAINING DETAILS =======");

        for (Integer franchiseId : franchiseIds) {
            Franchise franchise = franchiseService.getFranchise(franchiseId);
            if (franchise != null) {
                franchiseView.displayTrainingDetails(franchise);
            }
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
