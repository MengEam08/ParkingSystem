
package model.dao;

import entity.Vehicle;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.sql.DriverManager.getConnection;

public class VehicleDaoImpl implements VehicleDao {

    private static final String JDBC_URL = "jdbc:mariadb://localhost:3309/parking_slot_db";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "20030830";

    @Override
    public int addVehicle(Vehicle vehicle) {
        String sql = "INSERT INTO vehicle (vehicle_type) VALUES (?)";

        try (Connection connection = getConnection(
                "jdbc:mariadb://localhost:3309/parking_slot_db",
                "root",
                "20030830");
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, vehicle.getVehicleType().name());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Successfully added vehicle");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0; // You might want to return something meaningful here based on your application logic
    }

    @Override
    public void deleteAll() {
        String sql = "DELETE FROM vehicle";
        try (Connection connection = getConnection(
                "jdbc:mariadb://localhost:3309/parking_slot_db",
                "root",
                "20030830");
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
            System.out.println("All vehicles deleted successfully");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Vehicle> getAllVehicles() {
        String sql = "SELECT * FROM vehicle";
        List<Vehicle> vehicleList = new ArrayList<>();

        try (Connection connection = getConnection(
                "jdbc:mariadb://localhost:3309/parking_slot_db",
                "root",
                "20030830");
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("vehicle_id");
                String vehicleType = resultSet.getString("vehicle_type");
                Vehicle.VehicleType type = Vehicle.VehicleType.valueOf(vehicleType); // Convert String to enum

                Vehicle vehicle = new Vehicle(id, type);
                vehicleList.add(vehicle);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return vehicleList;
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM vehicle WHERE vehicle_id = ?";

        try (Connection connection = getConnection(
                "jdbc:mariadb://localhost:3309/parking_slot_db",
                "root",
                "20030830");
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);

            int rowsAffected = preparedStatement.executeUpdate();
            String message = (rowsAffected > 0) ? "Successfully deleted vehicle" : "Failed to delete vehicle";
            System.out.println(message);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void updateVehicleById(Vehicle vehicle) {
        String sql = "UPDATE vehicle SET vehicle_type = ? WHERE vehicle_id = ?";

        try (Connection connection = getConnection(
                "jdbc:mariadb://localhost:3309/parking_slot_db",
                "root",
                "20030830");
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, vehicle.getVehicleType().name());
            preparedStatement.setInt(2, vehicle.getId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Updated successfully");
            } else {
                System.out.println("Update failed");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Vehicle searchVehicleByID(int id) {
        String sql = "SELECT * FROM vehicle WHERE vehicle_id = ?";

        try (Connection connection = getConnection(
                "jdbc:mariadb://localhost:3309/parking_slot_db",
                "root",
                "20030830");
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String vehicleType = resultSet.getString("vehicle_type");
                Vehicle.VehicleType type = Vehicle.VehicleType.valueOf(vehicleType); // Convert String to enum

                return new Vehicle(id, type);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public int addVehicleAndGetId(Vehicle vehicle) throws SQLException {
        String query = "INSERT INTO vehicle (vehicle_type) VALUES (?)";
        try (Connection conn = getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, vehicle.getVehicleType().name());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // Return the generated ID
            } else {
                throw new SQLException("Creating vehicle failed, no ID obtained.");
            }
        }
    }
}
