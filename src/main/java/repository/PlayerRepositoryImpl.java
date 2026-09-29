package repository;

import model.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerRepositoryImpl implements PlayerRepository {

    @Override
    public boolean save(Player player) {

        String sql = """
                INSERT INTO player
                (username,password,name,dob,role,strength,bestFigure,experience,totalRuns,totalWickets)
                VALUES (?,?,?,?,?,?,?,?,?,?)
                """;

        String franchiseSql = """
                INSERT INTO player_franchise
                (playerId,franchiseId)
                VALUES (?,?)
                """;

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                try (PreparedStatement ps = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
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
                        if (!rs.next()) {
                            connection.rollback();
                            return false;
                        }
                        player.setPlayerId(rs.getInt(1));
                    }
                }

                try (PreparedStatement ps = connection.prepareStatement(franchiseSql)) {
                    for (Integer franchiseId : player.getFranchiseIds()) {
                        ps.setInt(1,player.getPlayerId());
                        ps.setInt(2,franchiseId);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }

                connection.commit();
                return true;
            } catch (SQLException | IllegalArgumentException e) {
                connection.rollback();
                System.out.println("Error while saving player.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error while connecting to the database.");
            return false;
        }
    }

    @Override
    public boolean registerToFranchise(int playerId,int franchiseId) {

        String sql = """
                INSERT INTO player_franchise (playerId,franchiseId)
                VALUES (?,?)
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1,playerId);
            ps.setInt(2,franchiseId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error while registering player to franchise.");
            return false;
        }
    }

    @Override
    public boolean removeOtherFranchises(int playerId,int selectedFranchiseId) {
        String sql = """
            DELETE FROM player_franchise
            WHERE playerId = ? AND franchiseId <> ?
            """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1,playerId);
            ps.setInt(2,selectedFranchiseId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error while removing other franchise registrations.");
            return false;
        }
    }

    @Override
    public Player findById(int playerId) {

        String sql = """
                SELECT playerId,username,password,name,dob,role,
                strength,bestFigure,experience,totalRuns,totalWickets
                FROM player
                WHERE playerId = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1,playerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapPlayer(connection,rs,true);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while finding player.");
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
                    return mapPlayer(connection,rs,true);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while finding player by username.");
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
                players.add(mapPlayer(connection,rs,true));
            }
        } catch (SQLException e) {
            System.out.println("Error while finding all players.");
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
                    players.add(mapPlayer(connection,rs,false));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while finding franchise players.");
        }
        return players;
    }

    @Override
    public boolean update(Player player) {

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
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error while updating player.");
            return false;
        }
    }

    @Override
    public boolean deleteAll() {

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
            return true;
        } catch (SQLException e) {
            System.out.println("Error while clearing player data.");
            return false;
        }
    }

    private Player mapPlayer(Connection connection,ResultSet rs,boolean loadFranchises) throws SQLException {

        List<Integer> franchiseIds = new ArrayList<>();

        if (loadFranchises) {
            String sql = "SELECT franchiseId FROM player_franchise WHERE playerId = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1,rs.getInt("playerId"));
                try (ResultSet franchiseRs = ps.executeQuery()) {
                    while (franchiseRs.next()) {
                        franchiseIds.add(franchiseRs.getInt("franchiseId"));
                    }
                }
            }
        }

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
                franchiseIds
        );
    }
}
