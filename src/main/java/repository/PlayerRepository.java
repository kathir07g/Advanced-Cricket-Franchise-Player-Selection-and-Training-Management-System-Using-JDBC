package repository;

import model.Player;

import java.util.List;

public interface PlayerRepository {

    boolean save(Player player);

    Player findById(int playerId);

    Player findByUsername(String username);

    List<Player> findAll();

    List<Player> findByFranchiseId(int franchiseId);

    boolean update(Player player);

    boolean registerToFranchise(int playerId,int franchiseId);

    boolean removeOtherFranchises(int playerId,int selectedFranchiseId);

    boolean deleteAll();
}
