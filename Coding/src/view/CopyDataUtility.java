package view;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CopyDataUtility {

    private static final String JDBC_URL = "jdbc:mariadb://localhost:3309/parking_slot_db";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "20030830";

    public static void copyDataToRecordList() {
        copyStudentDataToRecordList();
        copyVehicleDataToRecordList();
        copyParkingRecordDataToRecordList();
    }

    private static void copyStudentDataToRecordList() {
        String sql = "INSERT INTO record_list (student_id, student_name) " +
                "SELECT student_id, student_fullname FROM student";

        executeInsert(sql);
    }

    private static void copyVehicleDataToRecordList() {
        String sql = "INSERT INTO record_list (vehicle_id, vehicle_type) " +
                "SELECT vehicle_id, vehicle_type FROM vehicle";

        executeInsert(sql);
    }

    private static void copyParkingRecordDataToRecordList() {
        String sql = "INSERT INTO record_list (student_id, vehicle_id, entry_time, exit_time) " +
                "SELECT pr.student_id, pr.vehicle_id, pr.entry_time, pr.exit_time FROM parking_record pr";

        executeInsert(sql);
    }

    private static void executeInsert(String sql) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println(rowsAffected + " rows inserted into record_list table.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        copyDataToRecordList();
    }
}
