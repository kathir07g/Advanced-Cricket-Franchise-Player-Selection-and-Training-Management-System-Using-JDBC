package repository;

import model.Franchise;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FranchiseRepositoryImpl implements FranchiseRepository {

    @Override
    public boolean save(Franchise franchise) {
        String sql = """
                INSERT INTO franchise
                (username,password,name,location,trainingDate,availableSpots,roleCount)
                VALUES (?,?,?,?,?,?,?)
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1,franchise.getUsername());
            ps.setString(2,franchise.getPassword());
            ps.setString(3,franchise.getName());
            ps.setString(4,franchise.getLocation());
            ps.setDate(5,Date.valueOf(franchise.getTrainingDate()));
            ps.setInt(6,franchise.getAvailableSpots());
            ps.setString(7,franchise.getRoleCount());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    franchise.setFranchiseId(rs.getInt(1));
                    return true;
                }
            }
        } catch (SQLException | IllegalArgumentException e) {
            System.out.println("Error while saving franchise.");
        }
        return false;
    }

    @Override
    public boolean saveDefaultFranchise(Franchise franchise) {
        String sql = """
            INSERT INTO franchise
            (username,password,firstLogin,name,location,trainingDate,availableSpots,roleCount)
            VALUES (?,?,?,?,?,?,?,?)
            """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1,franchise.getUsername());
            ps.setString(2,franchise.getPassword());
            ps.setBoolean(3,true);
            ps.setString(4,franchise.getName());
            ps.setString(5,franchise.getLocation());
            ps.setDate(6,Date.valueOf(franchise.getTrainingDate()));
            ps.setInt(7,franchise.getAvailableSpots());
            ps.setString(8,franchise.getRoleCount());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    franchise.setFranchiseId(rs.getInt(1));
                    franchise.setFirstLogin(true);
                    return true;
                }
            }
        } catch (SQLException | IllegalArgumentException e) {
            System.out.println("Error while saving default franchise.");
        }
        return false;
    }

    @Override
    public boolean updatePassword(int franchiseId,String password) {
        String sql = """
            UPDATE franchise
            SET password = ?,firstLogin = FALSE
            WHERE franchiseId = ?
            """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1,password);
            ps.setInt(2,franchiseId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error while updating franchise password.");
            return false;
        }
    }

    @Override
    public Franchise findById(int franchiseId) {

        String sql = """
                SELECT franchiseId,username,password,firstLogin,name,location,
                trainingDate,availableSpots,roleCount
                FROM franchise
                WHERE franchiseId = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1,franchiseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapFranchise(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while finding franchise.");
        }
        return null;
    }

    @Override
    public Franchise findByUsername(String username) {

        String sql = """
                SELECT franchiseId,username,password,firstLogin,name,location,
                trainingDate,availableSpots,roleCount
                FROM franchise
                WHERE username = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1,username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapFranchise(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while finding franchise by username.");
        }
        return null;
    }

    @Override
    public List<Franchise> findAll() {

        List<Franchise> franchises = new ArrayList<>();

        String sql = """
                SELECT franchiseId,username,password,firstLogin,name,location,
                trainingDate,availableSpots,roleCount
                FROM franchise
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                franchises.add(mapFranchise(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error while finding franchises.");
        }
        return franchises;
    }

    @Override
    public List<Franchise> findActiveFranchises() {
        List<Franchise> franchises = new ArrayList<>();
        String sql = """
            SELECT franchiseId,username,password,firstLogin,name,location,
            trainingDate,availableSpots,roleCount
            FROM franchise
            WHERE trainingDate >= CURDATE()
            """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                franchises.add(mapFranchise(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error while finding active franchises.");
        }
        return franchises;
    }

    @Override
    public boolean update(Franchise franchise) {

        String sql = """
                UPDATE franchise
                SET name = ?,location = ?,trainingDate = ?,
                availableSpots = ?,roleCount = ?
                WHERE franchiseId = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1,franchise.getName());
            ps.setString(2,franchise.getLocation());
            ps.setDate(3,Date.valueOf(franchise.getTrainingDate()));
            ps.setInt(4,franchise.getAvailableSpots());
            ps.setString(5,franchise.getRoleCount());
            ps.setInt(6,franchise.getFranchiseId());
            return ps.executeUpdate() > 0;
        } catch (SQLException | IllegalArgumentException e) {
            System.out.println("Error while updating franchise.");
            return false;
        }
    }

    @Override
    public boolean deleteAll() {

        String sql = "DELETE FROM player_franchise";
        String franchiseSql = "DELETE FROM franchise";
        String resetSql = "ALTER TABLE franchise AUTO_INCREMENT = 101";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             PreparedStatement franchisePs = connection.prepareStatement(franchiseSql);
             PreparedStatement resetPs = connection.prepareStatement(resetSql)) {

            ps.executeUpdate();
            franchisePs.executeUpdate();
            resetPs.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error while clearing franchise data.");
            return false;
        }
    }

    @Override
    public Franchise findByName(String name) {

        String sql = """
                SELECT franchiseId,username,password,firstLogin,name,location,
                trainingDate,availableSpots,roleCount
                FROM franchise
                WHERE name = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1,name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapFranchise(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while finding franchise.");
        }
        return null;
    }

    private Franchise mapFranchise(ResultSet rs) throws SQLException {
        return new Franchise(
                rs.getInt("franchiseId"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getBoolean("firstLogin"),
                rs.getString("name"),
                rs.getString("location"),
                rs.getDate("trainingDate").toString(),
                rs.getInt("availableSpots"),
                rs.getString("roleCount")
        );
    }
}
