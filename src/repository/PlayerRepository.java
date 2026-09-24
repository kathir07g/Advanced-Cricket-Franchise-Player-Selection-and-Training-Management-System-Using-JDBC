package repository;

import model.Player;

import java.util.List;

public interface PlayerRepository {

    void save(Player player);

    Player findById(int playerId);

    Player findByUsername(String username);

    List<Player> findAll();

    List<Player> findByFranchiseId(int franchiseId);

    void update(Player player);

    boolean registerToFranchise(int playerId, int franchiseId);

    void deleteAll();

}