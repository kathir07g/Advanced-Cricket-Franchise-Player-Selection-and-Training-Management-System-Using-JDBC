package view;

import model.Franchise;
import model.Player;

import java.util.List;

public class FranchiseView {
    public void displayFranchise(Franchise franchise) {
        System.out.println();
        System.out.println("========================================");
        System.out.println("Franchise ID  : " + franchise.getFranchiseId());
        System.out.println("Franchise     : " + franchise.getName());
        System.out.println("Username      : " + franchise.getUsername());
        System.out.println("Location      : " + franchise.getLocation());
        System.out.println("Training Date : " + franchise.getTrainingDate());
        System.out.println("Available     : " + franchise.getAvailableSpots());
        System.out.println("Role Count    : " + franchise.getRoleCount());
        System.out.println("========================================");
    }

    public void displayRegisteredPlayers(List<Player> players) {
        System.out.println();
        System.out.println("----------------------------------------");
        System.out.println("Registered Players");
        System.out.println("----------------------------------------");
        if (players == null || players.isEmpty()) {
            System.out.println("No players registered.");
            return;
        }

        int count = 1;
        for (Player player : players) {
            System.out.println(count++ + ". " + player.getName() + " | ID: " + player.getPlayerId() + " | Role: " + player.getRole());
        }
    }

    public void displaySelectedPlayers(List<Player> players,boolean selectionDone) {
        System.out.println();
        System.out.println("----------------------------------------");
        System.out.println("Selected Players");
        System.out.println("----------------------------------------");

        if (!selectionDone) {
            System.out.println("Selection not yet done.");
            return;
        }

        if (players == null || players.isEmpty()) {
            System.out.println("No players selected.");
            return;
        }

        int count = 1;
        for (Player player : players) {
            System.out.println(count++ + ". " + player.getName() + " | ID: " + player.getPlayerId() + " | Role: " + player.getRole());
        }
    }

    public void displayTrainingDetails(Franchise franchise) {
        System.out.println();
        System.out.println("----------------------------------------");
        System.out.println("Training Details");
        System.out.println("----------------------------------------");
        System.out.println("Franchise : " + franchise.getName());
        System.out.println("Date      : " + franchise.getTrainingDate());
        System.out.println("Location  : " + franchise.getLocation());
        System.out.println("----------------------------------------");
    }
}