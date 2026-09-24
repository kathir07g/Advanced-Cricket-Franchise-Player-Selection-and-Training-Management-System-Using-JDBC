package service;

import model.Franchise;
import model.Player;
import repository.PlayerRepository;
import repository.SelectionRepository;

import java.util.ArrayList;
import java.util.List;

public class SelectionService {

    private PlayerRepository playerRepository;
    private SelectionRepository selectionRepository;

    public SelectionService(PlayerRepository playerRepository, SelectionRepository selectionRepository) {
        this.playerRepository = playerRepository;
        this.selectionRepository = selectionRepository;
    }

    public void runSelection(Franchise franchise) {

        if (selectionRepository.isSelectionDone(franchise.getFranchiseId())) {
            return;
        }

        List<Player> registeredPlayers = playerRepository.findByFranchiseId(franchise.getFranchiseId());

        List<Player> selectedPlayers = new ArrayList<>();

        int batsmanCount = 0;
        int bowlerCount = 0;
        int allRounderCount = 0;

        for (Player player : registeredPlayers) {

            if (selectionRepository.getSelectionFranchiseId(player.getPlayerId()) != null) {
                continue;
            }

            if (selectedPlayers.size() >= franchise.getAvailableSpots()) {
                break;
            }

            if (!player.isEligible()) {
                continue;
            }

            String role = player.getRole() == null ? "" : player.getRole().toLowerCase();

            switch (role) {

                case "batsman":
                    if (batsmanCount < franchise.getBatsmanCount()) {
                        selectedPlayers.add(player);
                        batsmanCount++;
                    }
                    break;

                case "bowler":
                    if (bowlerCount < franchise.getBowlerCount()) {
                        selectedPlayers.add(player);
                        bowlerCount++;
                    }
                    break;

                case "allrounder":
                case "all rounder":
                    if (allRounderCount < franchise.getAllRounderCount()) {
                        selectedPlayers.add(player);
                        allRounderCount++;
                    }
                    break;

                default:
                    break;
            }
        }

        selectionRepository.saveSelectedPlayers(franchise.getFranchiseId(), selectedPlayers);
        selectionRepository.markSelectionDone(franchise.getFranchiseId());
    }

    public List<Player> getSelectedPlayers(int franchiseId) {
        return selectionRepository.findSelectedPlayers(franchiseId);
    }

    public boolean isPlayerSelected(int franchiseId, int playerId) {
        return selectionRepository.isSelected(franchiseId, playerId);
    }

    public boolean isSelectionDone(int franchiseId) {
        return selectionRepository.isSelectionDone(franchiseId);
    }

    public Integer getSelectedFranchiseId(int playerId) {
        return selectionRepository.getSelectionFranchiseId(playerId);
    }

    public void clearSelections() {
        selectionRepository.clear();
    }
}