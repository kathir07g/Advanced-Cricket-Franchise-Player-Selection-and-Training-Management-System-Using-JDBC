package repository;

import model.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SelectionRepositoryImpl implements SelectionRepository {

    @Override
    public boolean saveSelectedPlayers(int franchiseId,List<Player> players) {

        if (players == null || players.isEmpty()) {
            return true;
        }

        String sql = """
                INSERT INTO selection (playerId,franchiseId,status)
                VALUES (?,?, 'SELECTED')
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            connection.setAutoCommit(false);

            for (Player player : players) {
                ps.setInt(1,player.getPlayerId());
                ps.setInt(2,franchiseId);
                ps.addBatch();
            }
            ps.executeBatch();
            connection.commit();
            return true;
        } catch (SQLException e) {
            System.out.println("Error while saving selected players.");
            return false;
        }
    }

    @Override
    public List<Player> findSelectedPlayers(int franchiseId) {

        List<Player> players = new ArrayList<>();

        String sql = """
                SELECT p.playerId,p.username,p.password,p.name,p.dob,p.role,
                p.strength,p.bestFigure,p.experience,p.totalRuns,p.totalWickets
                FROM player p
                INNER JOIN selection s ON p.playerId = s.playerId
                WHERE s.franchiseId = ? AND s.status = 'SELECTED'
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
            System.out.println("Error while finding selected players.");
        }
        return players;
    }

    @Override
    public boolean isSelected(int franchiseId,int playerId) {

        String sql = """
                SELECT 1 FROM selection
                WHERE franchiseId = ? AND playerId = ? AND status = 'SELECTED'
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1,franchiseId);
            ps.setInt(2,playerId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Error while checking player selection.");
            return false;
        }
    }

    @Override
    public Integer getSelectionFranchiseId(int playerId) {

        String sql = """
                SELECT s.franchiseId
                FROM selection s
                INNER JOIN franchise f
                ON s.franchiseId = f.franchiseId
                WHERE s.playerId = ?
                AND s.status = 'SELECTED'
                AND f.trainingDate >= CURDATE()
                LIMIT 1
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1,playerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("franchiseId");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while finding player's selection franchise.");
        }
        return null;
    }

    @Override
    public Integer getActiveSelectionFranchiseId(int playerId) {
        String sql = """
            SELECT s.franchiseId
            FROM selection s
            INNER JOIN franchise f ON s.franchiseId = f.franchiseId
            WHERE s.playerId = ? AND s.status = 'SELECTED'
            AND f.trainingDate >= CURDATE()
            LIMIT 1
            """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1,playerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("franchiseId");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while finding active selection franchise.");
        }
        return null;
    }

    @Override
    public boolean completeSelection(int franchiseId,List<Player> players) {
        if (players == null || players.isEmpty()) {
            return false;
        }
        String selectionSql = """
            INSERT INTO selection (playerId,franchiseId,status)
            VALUES (?,?, 'SELECTED')
            """;

        String removeOtherSql = """
            DELETE FROM player_franchise
            WHERE playerId = ? AND franchiseId <> ?
            """;

        String statusSql = """
            INSERT INTO selection_status (franchiseId,selectionDone)
            VALUES (?,TRUE)
            ON DUPLICATE KEY UPDATE selectionDone = TRUE
            """;

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement selectionPs = connection.prepareStatement(selectionSql);
                 PreparedStatement removePs = connection.prepareStatement(removeOtherSql);
                 PreparedStatement statusPs = connection.prepareStatement(statusSql)) {
                for (Player player : players) {
                    selectionPs.setInt(1,player.getPlayerId());
                    selectionPs.setInt(2,franchiseId);
                    selectionPs.addBatch();

                    removePs.setInt(1,player.getPlayerId());
                    removePs.setInt(2,franchiseId);
                    removePs.addBatch();
                }
                selectionPs.executeBatch();
                removePs.executeBatch();

                statusPs.setInt(1,franchiseId);
                statusPs.executeUpdate();

                connection.commit();
                return true;
            } catch (SQLException e) {
                connection.rollback();
                System.out.println("Error while completing player selection.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error while connecting to the database.");
            return false;
        }
    }

    @Override
    public boolean releaseExpiredSelections() {

        String selectionSql = """
                            DELETE s
                            FROM selection s
                            INNER JOIN franchise f
                            ON s.franchiseId = f.franchiseId
                            WHERE s.status = 'SELECTED'
                            AND f.trainingDate < CURDATE()
                            """;

        String statusSql = """
                        DELETE ss
                        FROM selection_status ss
                        INNER JOIN franchise f
                        ON ss.franchiseId = f.franchiseId
                        WHERE f.trainingDate < CURDATE()
                        """;

        String registrationSql = """
            DELETE pf
            FROM player_franchise pf
            INNER JOIN franchise f
            ON pf.franchiseId = f.franchiseId
            INNER JOIN selection s
            ON s.playerId = pf.playerId
            AND s.franchiseId = pf.franchiseId
            WHERE s.status = 'SELECTED'
            AND f.trainingDate < CURDATE()
            """;

        try (Connection connection = DBConnection.getConnection()) {

            connection.setAutoCommit(false);

            try (PreparedStatement registrationPs = connection.prepareStatement(registrationSql);
                 PreparedStatement selectionPs = connection.prepareStatement(selectionSql)) {

                registrationPs.executeUpdate();
                selectionPs.executeUpdate();

                try (PreparedStatement statusPs = connection.prepareStatement(statusSql)) {
                    statusPs.executeUpdate();
                }

                connection.commit();
                return true;

            } catch (SQLException e) {
                connection.rollback();
                System.out.println("Error while releasing expired selections.");
                return false;
            }

        } catch (SQLException e) {
            System.out.println("Error while connecting to the database.");
            return false;
        }
    }

    @Override
    public boolean isSelectionDone(int franchiseId) {

        String sql = """
                SELECT selectionDone FROM selection_status
                WHERE franchiseId = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1,franchiseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("selectionDone");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while checking selection status.");
        }
        return false;
    }

    @Override
    public boolean clear() {

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
            return true;
        } catch (SQLException e) {
            System.out.println("Error while clearing selection data.");
            return false;
        }
    }

    @Override
    public boolean clearAll() {

        String selectionStatusSql = "DELETE FROM selection_status";
        String selectionSql = "DELETE FROM selection";
        String playerFranchiseSql = "DELETE FROM player_franchise";
        String playerSql = "DELETE FROM player";
        String franchiseSql = "DELETE FROM franchise";

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement selectionStatusPs = connection.prepareStatement(selectionStatusSql);
                 PreparedStatement selectionPs = connection.prepareStatement(selectionSql);
                 PreparedStatement playerFranchisePs = connection.prepareStatement(playerFranchiseSql);
                 PreparedStatement playerPs = connection.prepareStatement(playerSql);
                 PreparedStatement franchisePs = connection.prepareStatement(franchiseSql)) {

                selectionStatusPs.executeUpdate();
                selectionPs.executeUpdate();
                playerFranchisePs.executeUpdate();
                playerPs.executeUpdate();
                franchisePs.executeUpdate();
                connection.commit();
                return true;
            } catch (SQLException e) {
                connection.rollback();
                System.out.println("Error while clearing all data.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error while connecting to the database.");
            return false;
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
                rs.getObject("totalRuns",Integer.class),
                rs.getObject("totalWickets",Integer.class),
                null
        );
    }
}
