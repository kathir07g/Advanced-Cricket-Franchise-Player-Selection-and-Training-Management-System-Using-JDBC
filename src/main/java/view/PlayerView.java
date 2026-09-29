package view;

import model.Franchise;
import model.Player;

public class PlayerView {
    public void displayPlayer(Player player) {
        System.out.println();
        System.out.println("----------------------------------------");
        System.out.println("Player ID     : " + player.getPlayerId());
        System.out.println("Name          : " + player.getName());
        System.out.println("Username      : " + player.getUsername());
        System.out.println("DOB           : " + player.getDOB());
        System.out.println("Age           : " + player.getAge());
        System.out.println("Role          : " + player.getRole());
        System.out.println("Strength      : " + player.getStrength());
        System.out.println("Best Figure   : " + player.getBestFigure());
        System.out.println("Experience    : " + player.getExperience());
        System.out.println("Runs          : " + (player.getTotalRuns() != null ? player.getTotalRuns() : "N/A"));
        System.out.println("Wickets       : " + (player.getTotalWickets() != null ? player.getTotalWickets() : "N/A"));
        System.out.println("Franchise ID  : " + (player.getFranchiseIds() != null ? player.getFranchiseIds() : "Not Registered"));
        System.out.println("----------------------------------------");
    }

    public void displayPlayerList(java.util.List<Player> players) {
        if (players == null || players.isEmpty()) {
            System.out.println("No registered players.");
            return;
        }
        int count = 1;

        for (Player player : players) {
            System.out.println(count++ + ". " + player.getName() + " | Player ID: " + player.getPlayerId());
        }
    }

    public void showSelectionStatus(String status) {
        System.out.println();
        System.out.println("Selection Status: "+status);
    }
}