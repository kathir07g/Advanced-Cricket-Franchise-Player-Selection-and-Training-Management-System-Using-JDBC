package repository;

import model.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerRepositoryImpl implements PlayerRepository {

    @Override
    public void save(Player player) {

        String sql = """
                INSERT INTO player
                (username,password,name,dob,role,strength,bestFigure,experience,totalRuns,totalWickets)
                VALUES (?,?,?,?,?,?,?,?,?,?) 
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1,player.getUsername());
            ps.setString(2,player.getPassword());
            ps.setString(3,player.getName());
            ps.setDate(4,Date.valueOf(player.getDOB()));
            ps.setString(5,player.getRole());
            ps.setString(6,player.getStrength());
            ps.setString(7,player.getBestFigure());
            ps.setInt(8,player.getExperience());

            if (player.getTotalRuns() == null) {
                ps.setNull(9,Types.INTEGER);
            }
            else {
                ps.setInt(9,player.getTotalRuns());
            }

            if (player.getTotalWickets() == null) {
                ps.setNull(10,Types.INTEGER);
            }
            else {
                ps.setInt(10,player.getTotalWickets());
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int playerId = rs.getInt(1);
                    player.setPlayerId(playerId);
                    savePlayerFranchises(playerId,player.getFranchiseIds());
                }
            }

        } catch (SQLException e) {
            System.out.println("Error while saving player.");
            e.printStackTrace();
        }
    }

    private void savePlayerFranchises(int playerId,List<Integer> franchiseIds) {

        String sql = """ 
                INSERT INTO player_franchise
                (playerId,franchiseId)
                VALUES (?,?) 
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            for (Integer franchiseId : franchiseIds) {
                ps.setInt(1,playerId);
                ps.setInt(2,franchiseId);
                ps.addBatch();
            }

            ps.executeBatch();

        } catch (SQLException e) {
            System.out.println("Error while saving player franchise.");
            e.printStackTrace();
        }
    }

    @Override
    public boolean registerToFranchise(int playerId, int franchiseId) {

        String sql = """
            INSERT INTO player_franchise (playerId, franchiseId)
            VALUES (?, ?)
            """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            ps.setInt(2, franchiseId);

            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error while registering player to franchise.");
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Player findById(int playerId) {

        String sql = """
            SELECT playerId, username, password, name, dob, role,
                   strength, bestFigure, experience, totalRuns, totalWickets
            FROM player
            WHERE playerId = ?
            """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, playerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Player player = mapPlayer(rs);

                    String franchiseSql = """
                        SELECT franchiseId
                        FROM player_franchise
                        WHERE playerId = ?
                        """;

                    try (PreparedStatement fps = connection.prepareStatement(franchiseSql)) {

                        fps.setInt(1, playerId);
                        try (ResultSet frs = fps.executeQuery()) {
                            while (frs.next()) {
                                player.addFranchiseId(frs.getInt("franchiseId"));
                            }
                        }
                    }
                    return player;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while finding player.");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Player findByUsername(String username) {

        String sql = """ 
                SELECT playerId,username,password,name,dob,role,
                strength,bestFigure,experience,totalRuns,totalWickets
                FROM player
                WHERE username = ? 
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1,username);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapPlayer(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error while finding player by username.");
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Player> findAll() {

        List<Player> players = new ArrayList<>();

        String sql = """ 
                SELECT playerId,username,password,name,dob,role,
                strength,bestFigure,experience,totalRuns,totalWickets
                FROM player 
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                players.add(mapPlayer(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error while finding all players.");
            e.printStackTrace();
        }

        return players;
    }

    @Override
    public List<Player> findByFranchiseId(int franchiseId) {

        List<Player> players = new ArrayList<>();

        String sql = """ 
SELECT p.playerId,p.username,p.password,p.name,p.dob,p.role,
                p.strength,p.bestFigure,p.experience,p.totalRuns,p.totalWickets
                FROM player p
                INNER JOIN player_franchise pf
                ON p.playerId = pf.playerId
                WHERE pf.franchiseId = ? 
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1,franchiseId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    players.add(mapPlayer(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error while finding franchise players.");
            e.printStackTrace();
        }

        return players;
    }

    @Override
    public void update(Player player) {

        String sql = """ 
                UPDATE player
                SET name = ?,strength = ?,bestFigure = ?
                WHERE playerId = ? 
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1,player.getName());
            ps.setString(2,player.getStrength());
            ps.setString(3,player.getBestFigure());
            ps.setInt(4,player.getPlayerId());

            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error while updating player.");
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAll() {
        String sql = "DELETE FROM player_franchise";
        String playerSql = "DELETE FROM player";
        String resetSql = "ALTER TABLE player AUTO_INCREMENT = 1";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             PreparedStatement playerPs = connection.prepareStatement(playerSql);
             PreparedStatement resetPs = connection.prepareStatement(resetSql)) {

            ps.executeUpdate();
            playerPs.executeUpdate();
            resetPs.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error while clearing player data.");
            e.printStackTrace();
        }
    }

    private Player mapPlayer(ResultSet rs) throws SQLException {

        int playerId = rs.getInt("playerId");

        List<Integer> franchiseIds = new ArrayList<>();

        String sql = """ 
            SELECT franchiseId FROM player_franchise
            WHERE playerId = ? 
            """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, playerId);
            try (ResultSet franchiseRs = ps.executeQuery()) {
                while (franchiseRs.next()) {
                    franchiseIds.add(franchiseRs.getInt("franchiseId"));
                }
            }
        }
        return new Player(
                playerId,
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("name"),
                rs.getDate("dob").toString(),
                rs.getString("role"),
                rs.getString("strength"),
                rs.getString("bestFigure"),
                rs.getInt("experience"),
                rs.getObject("totalRuns", Integer.class),
                rs.getObject("totalWickets", Integer.class),
                franchiseIds);
    }

    private List<Integer> findFranchiseIds(int playerId) {

        List<Integer> franchiseIds = new ArrayList<>();

        String sql = """ 
                SELECT franchiseId
                FROM player_franchise
                WHERE playerId = ? 
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1,playerId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    franchiseIds.add(rs.getInt("franchiseId"));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error while finding player franchises.");
            e.printStackTrace();
        }

        return franchiseIds;
    }
}