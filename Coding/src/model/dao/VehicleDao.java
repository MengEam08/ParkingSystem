package model.dao;

import entity.Vehicle;

import java.sql.SQLException;
import java.util.List;

public interface VehicleDao {
    int addVehicle(Vehicle vehicle);
    void deleteAll();
    List<Vehicle> getAllVehicles();
    void deleteById(int id);
    void updateVehicleById(Vehicle vehicle);
    Vehicle searchVehicleByID(int id);

    int addVehicleAndGetId(Vehicle vehicle) throws SQLException;

}

