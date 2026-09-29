package service;

import model.Franchise;
import model.Player;
import repository.PlayerRepository;
import util.PasswordUtil;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class PlayerService {

    private PlayerRepository playerRepository;
    private FranchiseService franchiseService;

    public PlayerService(PlayerRepository playerRepository,FranchiseService franchiseService) {
        this.playerRepository = playerRepository;
        this.franchiseService = franchiseService;
    }

    public Player registerPlayer(String username,String password,String name,String DOB,
                                 String role,String strength,String bestFigure,int experience,
                                 Integer runs,Integer wickets,Integer franchiseId) {

        validate(username,password,name,DOB,role,experience,runs,wickets);
        if (playerRepository.findByUsername(username) != null) {
            return null;
        }

        List<Integer> franchiseIds = new ArrayList<>();
        if (franchiseId != null) {
            if (!franchiseService.isActiveFranchise(franchiseId)) {
                return null;
            }
            franchiseIds.add(franchiseId);
        }
        role = normalizeRole(role);
        Player player = new Player(0,username,PasswordUtil.hash(password),name,DOB,
                role,strength,bestFigure,experience,runs,wickets,franchiseIds);

        if (!playerRepository.save(player)) {
            return null;
        }
        return player;
    }

    public boolean registerPlayerToFranchise(int playerId,int franchiseId) {
        Player player = playerRepository.findById(playerId);

        if (player == null || player.isRegisteredWithFranchise(franchiseId)) {
            return false;
        }
        if (!franchiseService.isActiveFranchise(franchiseId)) {
            return false;
        }
        boolean registered = playerRepository.registerToFranchise(playerId,franchiseId);

        if (registered) {
            player.addFranchiseId(franchiseId);
        }
        return registered;
    }

    public Player login(String username,String password) {

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return null;
        }

        Player player = playerRepository.findByUsername(username);

        if (player == null) {
            return null;
        }
        if (PasswordUtil.matches(password,player.getPassword())) {
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

    public boolean updatePlayer(Player player) {

        if (player == null || player.getName() == null || player.getName().isBlank()) {
            return false;
        }
        return playerRepository.update(player);
    }

    public boolean clearPlayers() {
        return playerRepository.deleteAll();
    }

    private void validate(String username,String password,String name,String DOB,String role,
                          int experience,Integer runs,Integer wickets) {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        if (DOB == null || DOB.isBlank()) {
            throw new IllegalArgumentException("DOB cannot be empty.");
        }

        try {
            LocalDate.parse(DOB);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("DOB must be in YYYY-MM-DD format.");
        }

        if (LocalDate.parse(DOB).isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("DOB cannot be a future date.");
        }
        if (role == null || (!role.equalsIgnoreCase("Batsman")
                && !role.equalsIgnoreCase("Bowler")
                && !role.equalsIgnoreCase("AllRounder")
                && !role.equalsIgnoreCase("All Rounder"))) {
            throw new IllegalArgumentException("Role must be Batsman, Bowler or AllRounder.");
        }
        if (experience < 0) {
            throw new IllegalArgumentException("Experience cannot be negative.");
        }
        if (runs != null && runs < 0) {
            throw new IllegalArgumentException("Runs cannot be negative.");
        }
        if (wickets != null && wickets < 0) {
            throw new IllegalArgumentException("Wickets cannot be negative.");
        }
    }

    public boolean removeOtherFranchises(int playerId,int selectedFranchiseId) {
        return playerRepository.removeOtherFranchises(playerId,selectedFranchiseId);
    }

    private String normalizeRole(String role) {
        if (role.equalsIgnoreCase("Batsman")) {
            return "Batsman";
        }
        if (role.equalsIgnoreCase("Bowler")) {
            return "Bowler";
        }
        return "AllRounder";
    }
}
