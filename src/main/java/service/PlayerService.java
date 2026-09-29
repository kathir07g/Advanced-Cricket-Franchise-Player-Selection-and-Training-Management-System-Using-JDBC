package service;

import model.Player;
import repository.PlayerRepository;

import java.util.ArrayList;
import java.util.List;

public class PlayerService {

    private PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Player registerPlayer(String username, String password, String name, String DOB,
                                 String role, String strength, String bestFigure, int experience,
                                 Integer runs, Integer wickets, int franchiseId) {

        if (playerRepository.findByUsername(username) != null) {
            return null;
        }
        List<Integer> franchiseIds = new ArrayList<>();
        franchiseIds.add(franchiseId);

        Player player = new Player(0, username, password, name, DOB,
                role, strength, bestFigure, experience, runs, wickets, franchiseIds);

        playerRepository.save(player);

        return player;
    }

    public boolean registerPlayerToFranchise(int playerId, int franchiseId) {
        Player player = playerRepository.findById(playerId);

        if (player == null) {
            return false;
        }
        if (player.isRegisteredWithFranchise(franchiseId)) {
            return false;
        }

        boolean registered = playerRepository.registerToFranchise(playerId, franchiseId);

        if (registered) {
            player.addFranchiseId(franchiseId);
        }
        return registered;
    }

    public Player login(String username, String password) {
        Player player = playerRepository.findByUsername(username);

        if (player == null) {
            return null;
        }
        if (player.getPassword().equals(password)) {
            return player;
        }

        return null;
    }

    public Player getPlayer(int playerId) {
        return playerRepository.findById(playerId);
    }

    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    public List<Player> getPlayersByFranchise(int franchiseId) {
        return playerRepository.findByFranchiseId(franchiseId);
    }

    public void updatePlayer(Player player) {
        playerRepository.update(player);
    }

    public void clearPlayers() {
        playerRepository.deleteAll();
    }
}