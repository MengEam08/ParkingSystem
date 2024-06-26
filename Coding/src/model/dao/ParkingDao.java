package model.dao;

import entity.Parking;
import entity.Vehicle;

import java.util.List;

public interface ParkingDao {
    int addParking(Parking parking);
    void deleteAll();
    List<Parking> getAllParkings();
    void deleteById(int id);
    void updateParkingById(Parking parking);
    Parking searchParkingById(int id);
    void createRecordListTable();
    void populateRecordListTable();
    void updateRecordListTable();

}
