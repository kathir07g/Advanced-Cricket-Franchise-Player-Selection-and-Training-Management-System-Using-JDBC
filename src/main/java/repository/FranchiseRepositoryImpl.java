package repository;

import model.Franchise;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FranchiseRepositoryImpl implements FranchiseRepository {

    @Override
    public void save(Franchise franchise) {
        String sql = """
            INSERT INTO franchise
            (username, password, name, location, trainingDate, availableSpots, roleCount)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, franchise.getUsername());
            ps.setString(2, franchise.getPassword());
            ps.setString(3, franchise.getName());
            ps.setString(4, franchise.getLocation());
            ps.setDate(5, Date.valueOf(franchise.getTrainingDate()));
            ps.setInt(6, franchise.getAvailableSpots());
            ps.setString(7, franchise.getRoleCount());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int franchiseId = rs.getInt(1);
                    franchise.setFranchiseId(franchiseId);
                    System.out.println("Franchise saved with ID: " + franchiseId);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while saving franchise.");
            e.printStackTrace();
        }
    }

    @Override
    public Franchise findById(int franchiseId) {

        String sql = """ 
                SELECT franchiseId,username,password,name,location,
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
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Franchise findByUsername(String username) {

        String sql = """ 
                SELECT franchiseId,username,password,name,location,
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
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Franchise> findAll() {

        List<Franchise> franchises = new ArrayList<>();

        String sql = """ 
                SELECT franchiseId,username,password,name,location,
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
            e.printStackTrace();
        }

        return franchises;
    }

    @Override
    public void update(Franchise franchise) {

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

            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error while updating franchise.");
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAll() {

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
        } catch (SQLException e) {
            System.out.println("Error while clearing franchise data.");
            e.printStackTrace();
        }
    }

    @Override
    public Franchise findByName(String name) {
        String sql = "SELECT franchiseId, username, password, name, location, trainingDate, availableSpots, roleCount FROM franchise WHERE name = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapFranchise(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while finding franchise.");
            e.printStackTrace();
        }
        return null;
    }

    private Franchise mapFranchise(ResultSet rs) throws SQLException {

        return new Franchise(
                rs.getInt("franchiseId"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("name"),
                rs.getString("location"),
                rs.getDate("trainingDate").toString(),
                rs.getInt("availableSpots"),
                rs.getString("roleCount")
        );
    }
}