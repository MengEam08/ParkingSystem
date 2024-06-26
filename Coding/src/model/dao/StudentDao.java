package model.dao;

import entity.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public interface StudentDao {
    int addStudent(Student student);
    void deleteAll();
    List<Student> getAllStudents();
    void deleteById(int id);
    void updateStudentById(Student student);
    Student searchStudentById(int id);
    int addStudentAndGetId(Student student) throws SQLException;
}

