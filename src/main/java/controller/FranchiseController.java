package controller;

import model.Franchise;
import model.Player;
import service.FranchiseService;
import service.PlayerService;
import service.SelectionService;
import view.FranchiseView;

import java.util.List;
import java.util.Scanner;

public class FranchiseController {

    private FranchiseService franchiseService;
    private PlayerService playerService;
    private SelectionService selectionService;

    private FranchiseView franchiseView = new FranchiseView();

    private Scanner scanner = new Scanner(System.in);

    public FranchiseController( FranchiseService franchiseService,
                                PlayerService playerService, SelectionService selectionService) {
        this.franchiseService = franchiseService;
        this.playerService = playerService;
        this.selectionService = selectionService;
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

        Franchise franchise = franchiseService.registerFranchise(username, password, name,
                location, date, spots,roleCount);
        if (franchise == null) {
            System.out.println("Username already exists.");
            return;
        }
        System.out.println();
        System.out.println("========================================");
        System.out.println("Franchise Registration Successful");
        System.out.println("Franchise ID: " + franchise.getFranchiseId());
        System.out.println("========================================");
    }

    public void startFranchiseMenu(Franchise franchise) {

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
        if (selectionService.isSelectionDone(franchise.getFranchiseId())) {
            System.out.println();
            System.out.println("Selection has already been completed for " + franchise.getName());
            return;
        }

        selectionService.runSelection(franchise);
        System.out.println();
        System.out.println("Player selection completed.");

        List<Player> selectedPlayers = selectionService.getSelectedPlayers(franchise.getFranchiseId());
        franchiseView.displaySelectedPlayers(selectedPlayers,true);
    }

    private void showSelectedPlayers(Franchise franchise) {
        boolean selectionDone=selectionService.isSelectionDone(franchise.getFranchiseId());

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
                System.out.print("Enter new training date: ");
                franchise.setTrainingDate(readLine());

                franchiseService.updateFranchise(franchise);
                System.out.println("Training date updated.");
                break;
            case 3:
                System.out.print("Enter new training location: ");
                franchise.setLocation(readLine());

                franchiseService.updateFranchise(franchise);
                System.out.println("Training location updated.");
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
        switch (choice){
            case 1:
                System.out.print("New franchise name: ");
                franchise.setName(readLine());
                franchiseService.updateFranchise(franchise);
                System.out.println("Franchise Name updated successfully.");
                break;
            case 2:
                System.out.print("New Available Spots: ");
                franchise.setAvailableSpots(readInt());
                System.out.print("New role count: ");
                franchise.setRoleCount(readLine());
                franchiseService.updateFranchise(franchise);
                System.out.println("Franchise Available Spots & Role Count updated successfully.");
                break;
            case 3:
                break;
            default:
                System.out.println("Invalid Choice");
                return;
        }
    }

    private int readInt() {

        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            }
            catch (Exception e) {
                System.out.print("Enter a valid number: ");
            }
        }
    }

    private String readLine() {
        return scanner.nextLine().trim();
    }
}