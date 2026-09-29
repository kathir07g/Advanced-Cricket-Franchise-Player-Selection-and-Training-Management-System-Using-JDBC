package repository;

import model.Player;

import java.util.List;

public interface SelectionRepository {

    boolean saveSelectedPlayers(int franchiseId,List<Player> players);

    List<Player> findSelectedPlayers(int franchiseId);

    boolean isSelected(int franchiseId,int playerId);

    Integer getSelectionFranchiseId(int playerId);

    Integer getActiveSelectionFranchiseId(int playerId);

    boolean isSelectionDone(int franchiseId);

    boolean completeSelection(int franchiseId,List<Player> players);

    boolean releaseExpiredSelections();

    boolean clear();

    boolean clearAll();
}
