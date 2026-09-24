package repository;

import model.Player;

import java.util.List;

public interface SelectionRepository {

    void saveSelectedPlayers(int franchiseId, List<Player> players);

    List<Player> findSelectedPlayers(int franchiseId);

    boolean isSelected(int franchiseId, int playerId);

    Integer getSelectionFranchiseId(int playerId);

    void lockPlayerToFranchise(int playerId, int franchiseId);

    boolean isSelectionDone(int franchiseId);

    void markSelectionDone(int franchiseId);

    void clear();
}