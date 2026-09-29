package service;

import model.Franchise;
import model.Player;
import repository.PlayerRepository;
import repository.SelectionRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SelectionService {

    private PlayerRepository playerRepository;
    private SelectionRepository selectionRepository;

    public SelectionService(PlayerRepository playerRepository,SelectionRepository selectionRepository) {
        this.playerRepository = playerRepository;
        this.selectionRepository = selectionRepository;
    }

    public boolean runSelection(Franchise franchise) {
        selectionRepository.releaseExpiredSelections();

        if (selectionRepository.isSelectionDone(franchise.getFranchiseId())
                || LocalDate.parse(franchise.getTrainingDate()).isBefore(LocalDate.now())) {
            return false;
        }
        List<Player> registeredPlayers = playerRepository.findByFranchiseId(franchise.getFranchiseId());

        registeredPlayers.removeIf(player -> selectionRepository.getActiveSelectionFranchiseId(player.getPlayerId()) != null);
        registeredPlayers.removeIf(player -> !player.isEligible());

        registeredPlayers.sort(Comparator.comparingInt(Player::getExperience).reversed());

        List<Player> selectedPlayers = new ArrayList<>();
        int batsmanCount = 0;
        int bowlerCount = 0;
        int allRounderCount = 0;

        for (Player player : registeredPlayers) {
            if (selectedPlayers.size() >= franchise.getAvailableSpots()) {
                break;
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
        if (selectedPlayers.isEmpty()) {
            return false;
        }
        return selectionRepository.completeSelection(franchise.getFranchiseId(),selectedPlayers);
    }

    public List<Player> getSelectedPlayers(int franchiseId) {
        return selectionRepository.findSelectedPlayers(franchiseId);
    }

    public boolean isPlayerSelected(int franchiseId,int playerId) {
        return selectionRepository.isSelected(franchiseId,playerId);
    }

    public boolean isSelectionDone(int franchiseId) {
        return selectionRepository.isSelectionDone(franchiseId);
    }

    public Integer getSelectedFranchiseId(int playerId) {
        return selectionRepository.getSelectionFranchiseId(playerId);
    }

    public boolean releaseExpiredSelections() {
        return selectionRepository.releaseExpiredSelections();
    }

    public boolean clearSelections() {
        return selectionRepository.clear();
    }

    public boolean clearAll() {
        return selectionRepository.clearAll();
    }
}
