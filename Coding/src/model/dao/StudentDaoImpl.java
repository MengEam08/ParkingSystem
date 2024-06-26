
package model.dao;

import entity.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.sql.DriverManager.getConnection;

public class StudentDaoImpl implements StudentDao {

    private static final String JDBC_URL = "jdbc:mariadb://localhost:3309/parking_slot_db";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "20030830";

    @Override
    public int addStudent(Student student) {
        String sql = "INSERT INTO student (student_fullname, student_gender) VALUES (?, ?)";

        try (Connection connection = getConnection(
                "jdbc:mariadb://localhost:3309/parking_slot_db",
                "root",
                "20030830");
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, student.getName());
            preparedStatement.setString(2, student.getGender());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Successfully added student");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // You might want to return something meaningful here based on your application logic
    }

    @Override
    public void deleteAll() {
        String sql = "DELETE FROM student";
        try (Connection connection = getConnection(
                "jdbc:mariadb://localhost:3309/parking_slot_db",
                "root",
                "20030830");
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
            System.out.println("All students deleted successfully");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Student> getAllStudents() {
        String sql = "SELECT * FROM student";
        List<Student> studentList = new ArrayList<>();

        try (Connection connection = getConnection(
                "jdbc:mariadb://localhost:3309/parking_slot_db",
                "root",
                "20030830");
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                Student student = new Student(
                        resultSet.getInt("student_id"),
                        resultSet.getString("student_fullname"),
                        resultSet.getString("student_gender")
                );
                studentList.add(student);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return studentList;
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM student WHERE student_id = ?";

        try (Connection connection = getConnection(
                "jdbc:mariadb://localhost:3309/parking_slot_db",
                "root",
                "20030830");
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);

            int rowsAffected = preparedStatement.executeUpdate();
            String message = (rowsAffected > 0) ? "Successfully deleted student" : "Failed to delete student";
            System.out.println(message);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void updateStudentById(Student student) {
        String sql = "UPDATE student SET student_fullname = ?, student_gender = ? WHERE student_id = ?";

        try (Connection connection = getConnection(
                "jdbc:mariadb://localhost:3309/parking_slot_db",
                "root",
                "20030830");
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, student.getName());
            preparedStatement.setString(2, student.getGender());
            preparedStatement.setInt(3, student.getId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Updated successfully");
            } else {
                System.out.println("Update failed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Student searchStudentById(int id) {
        String sql = "SELECT * FROM student WHERE student_id = ?";

        try (Connection connection = getConnection(
                "jdbc:mariadb://localhost:3309/parking_slot_db",
                "root",
                "20030830");
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new Student(
                        resultSet.getInt("student_id"),
                        resultSet.getString("student_fullname"),
                        resultSet.getString("student_gender")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int addStudentAndGetId(Student student) throws SQLException {
        String query = "INSERT INTO student (student_fullname, student_gender) VALUES (?, ?)";
        try (Connection conn =
                     getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, student.getName());
            stmt.setString(2, student.getGender());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // Return the generated ID
            } else {
                throw new SQLException("Creating student failed, no ID obtained.");
            }
        }
    }
}


