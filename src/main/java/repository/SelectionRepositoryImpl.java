package repository;

import model.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SelectionRepositoryImpl implements SelectionRepository {

    @Override
    public void saveSelectedPlayers(int franchiseId, List<Player> players) {

        String sql = """ 
                INSERT INTO selection (playerId, franchiseId, status)
                VALUES (?, ?, 'SELECTED') 
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            for (Player player : players) {
                ps.setInt(1, player.getPlayerId());
                ps.setInt(2, franchiseId);
                ps.addBatch();
            }
            ps.executeBatch();

        } catch (SQLException e) {
            System.out.println("Error while saving selected players.");
            e.printStackTrace();
        }
    }

    @Override
    public List<Player> findSelectedPlayers(int franchiseId) {

        List<Player> players = new ArrayList<>();

        String sql = """ 
                SELECT p.playerId, p.username, p.password, p.name, p.dob, p.role,
                p.strength, p.bestFigure, p.experience, p.totalRuns, p.totalWickets
                FROM player p
                INNER JOIN selection s ON p.playerId = s.playerId
                WHERE s.franchiseId = ? AND s.status = 'SELECTED' 
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, franchiseId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    players.add(mapPlayer(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error while finding selected players.");
            e.printStackTrace();
        }

        return players;
    }

    @Override
    public boolean isSelected(int franchiseId, int playerId) {

        String sql = """ 
                SELECT 1 FROM selection
                WHERE franchiseId = ? AND playerId = ? AND status = 'SELECTED' 
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, franchiseId);
            ps.setInt(2, playerId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.out.println("Error while checking player selection.");
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public Integer getSelectionFranchiseId(int playerId) {

        String sql = """ 
                SELECT franchiseId FROM selection
                WHERE playerId = ? AND status = 'SELECTED' LIMIT 1 
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, playerId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("franchiseId");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error while finding player's selection franchise.");
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void lockPlayerToFranchise(int playerId, int franchiseId) {
        // Player is locked when the player is inserted into selection.
    }

    @Override
    public boolean isSelectionDone(int franchiseId) {

        String sql = """ 
                SELECT selectionDone FROM selection_status
                WHERE franchiseId = ? 
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, franchiseId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("selectionDone");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error while checking selection status.");
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void markSelectionDone(int franchiseId) {

        String sql = """ 
                INSERT INTO selection_status (franchiseId, selectionDone)
                VALUES (?, TRUE)
                ON DUPLICATE KEY UPDATE selectionDone = TRUE 
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, franchiseId);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error while marking selection as completed.");
            e.printStackTrace();
        }
    }

    @Override
    public void clear() {
        String sql = "DELETE FROM selection";
        String statusSql = "DELETE FROM selection_status";
        String resetSql = "ALTER TABLE selection AUTO_INCREMENT = 1";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             PreparedStatement statusPs = connection.prepareStatement(statusSql);
             PreparedStatement resetPs = connection.prepareStatement(resetSql)) {

            ps.executeUpdate();
            statusPs.executeUpdate();
            resetPs.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error while clearing selection data.");
            e.printStackTrace();
        }
    }

    private Player mapPlayer(ResultSet rs) throws SQLException {

        return new Player(
                rs.getInt("playerId"),
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
                null
        );
    }
}