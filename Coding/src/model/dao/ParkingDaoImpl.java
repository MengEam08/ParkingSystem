package model.dao;

import entity.Parking;
import entity.Student;
import entity.Vehicle;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ParkingDaoImpl implements ParkingDao {

    private static final String JDBC_URL = "jdbc:mariadb://localhost:3309/parking_slot_db";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "20030830";

    @Override
    public int addParking(Parking parking) {
        String sql = "INSERT INTO parking_record (entry_time, exit_time, student_id, vehicle_id) VALUES (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setTimestamp(1, Timestamp.valueOf(parking.getEntryTime()));
            preparedStatement.setTimestamp(2, parking.getExitTime() != null ? Timestamp.valueOf(parking.getExitTime()) : null);
            preparedStatement.setInt(3, parking.getStudent().getId());
            preparedStatement.setInt(4, parking.getVehicle().getId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Return the generated ID
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return 0; // Return appropriate value based on your application logic
    }

    @Override
    public void deleteAll() {
        String sql = "DELETE FROM parking_record";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    @Override
    public List<Parking> getAllParkings() {
        String sql = "SELECT * FROM parking_record";
        List<Parking> parkingList = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                int recordId = resultSet.getInt("record_id");
                LocalDateTime entryTime = resultSet.getTimestamp("entry_time").toLocalDateTime();
                LocalDateTime exitTime = resultSet.getTimestamp("exit_time") != null ? resultSet.getTimestamp("exit_time").toLocalDateTime() : null;
                int studentId = resultSet.getInt("student_id");
                int vehicleId = resultSet.getInt("vehicle_id");

                Student student = fetchStudentById(studentId);
                Vehicle vehicle = fetchVehicleById(vehicleId);

                Parking parking = new Parking(recordId, entryTime, exitTime, student, vehicle);
                parkingList.add(parking);
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return parkingList;
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM parking_record WHERE record_id = ?";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    @Override
    public void updateParkingById(Parking parking) {
        String sql = "UPDATE parking_record SET entry_time = ?, exit_time = ? WHERE record_id = ?";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setTimestamp(1, Timestamp.valueOf(parking.getEntryTime()));
            preparedStatement.setTimestamp(2, parking.getExitTime() != null ? Timestamp.valueOf(parking.getExitTime()) : null);
            preparedStatement.setInt(3, parking.getRecordId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Updated parking record successfully");
            } else {
                System.out.println("Failed to update parking record");
            }

        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    @Override
    public Parking searchParkingById(int id) {
        String sql = "SELECT * FROM parking_record WHERE record_id = ?";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int recordId = resultSet.getInt("record_id");
                LocalDateTime entryTime = resultSet.getTimestamp("entry_time").toLocalDateTime();
                LocalDateTime exitTime = resultSet.getTimestamp("exit_time") != null ? resultSet.getTimestamp("exit_time").toLocalDateTime() : null;
                int studentId = resultSet.getInt("student_id");
                int vehicleId = resultSet.getInt("vehicle_id");

                Student student = fetchStudentById(studentId);
                Vehicle vehicle = fetchVehicleById(vehicleId);

                return new Parking(recordId, entryTime, exitTime, student, vehicle);
            }

        } catch (SQLException e) {
            handleSQLException(e);
        }
        return null;
    }

    @Override
    public void createRecordListTable() {
        String sql = "CREATE TABLE IF NOT EXISTS record_list ( " +
                "record_id INT PRIMARY KEY, " +
                "entry_time DATETIME, " +
                "exit_time DATETIME, " +
                "student_id INT, " +
                "student_name VARCHAR(255), " +
                "student_gender VARCHAR(10), " +
                "vehicle_id INT, " +
                "vehicle_type VARCHAR(50) " +
                ")";

        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             Statement statement = connection.createStatement()) {

            statement.executeUpdate(sql);
            System.out.println("record_list table created successfully.");

        } catch (SQLException e) {
            handleSQLException(e);
        }
    }


    @Override
    public void populateRecordListTable() {
        String sql = "INSERT INTO record_list (record_id, entry_time, exit_time, student_id, student_name, student_gender, vehicle_id, vehicle_type) " +
                "SELECT pr.record_id, pr.entry_time, pr.exit_time, s.student_id, s.student_name, s.student_gender, v.vehicle_id, v.vehicle_type " +
                "FROM parking_record pr " +
                "JOIN student s ON pr.student_id = s.student_id " +
                "JOIN vehicle v ON pr.vehicle_id = v.vehicle_id";

        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.executeUpdate();
            System.out.println("record_list table populated successfully.");

        } catch (SQLException e) {
            handleSQLException(e);
        }
    }


    @Override
    public void updateRecordListTable() {
        String sql = "UPDATE record_list rl " +
                "JOIN parking_record pr ON rl.record_id = pr.record_id " +
                "JOIN student s ON pr.student_id = s.student_id " +
                "JOIN vehicle v ON pr.vehicle_id = v.vehicle_id " +
                "SET rl.entry_time = pr.entry_time, " +
                "    rl.exit_time = pr.exit_time, " +
                "    rl.student_id = s.student_id, " +
                "    rl.student_name = s.student_name, " +
                "    rl.student_gender = s.student_gender, " +
                "    rl.vehicle_id = v.vehicle_id, " +
                "    rl.vehicle_type = v.vehicle_type";

        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.executeUpdate();
            System.out.println("record_list table updated successfully.");

        } catch (SQLException e) {
            handleSQLException(e);
        }
    }
    public void addRecordListEntry(Parking parking, Student student, Vehicle vehicle) {
        String sql = "INSERT INTO record_list (record_id, entry_time, exit_time, student_id, student_name, student_gender, vehicle_id, vehicle_type) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, parking.getRecordId());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(parking.getEntryTime()));
            preparedStatement.setTimestamp(3, parking.getExitTime() != null ? Timestamp.valueOf(parking.getExitTime()) : null);
            preparedStatement.setInt(4, student.getId());
            preparedStatement.setString(5, student.getName());
            preparedStatement.setString(6, student.getGender());
            preparedStatement.setInt(7, vehicle.getId());
            preparedStatement.setString(8, vehicle.getVehicleType().name());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            handleSQLException(e);
        }
    }



    private Student fetchStudentById(int studentId) throws SQLException {
        String sql = "SELECT * FROM student WHERE student_id = ?";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, studentId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("student_fullname");
                String gender = resultSet.getString("student_gender");
                return new Student(studentId, name, gender);
            }
        }
        return null;
    }

    private Vehicle fetchVehicleById(int vehicleId) throws SQLException {
        String sql = "SELECT * FROM vehicle WHERE vehicle_id = ?";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, vehicleId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String vehicleType = resultSet.getString("vehicle_type");
                Vehicle.VehicleType type = Vehicle.VehicleType.valueOf(vehicleType); // Assuming VehicleType is an enum
                return new Vehicle(vehicleId, type);
            }
        }
        return null;
    }

    private void handleSQLException(SQLException e) {
        // Log the exception or rethrow as needed
        e.printStackTrace(); // For simplicity, just print the stack trace
    }
}
