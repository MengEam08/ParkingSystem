package main;
import entity.Parking;
import entity.Student;
import entity.Vehicle;
import model.dao.ParkingDao;
import model.dao.ParkingDaoImpl;
import model.dao.StudentDao;
import model.dao.StudentDaoImpl;
import model.dao.VehicleDao;
import model.dao.VehicleDaoImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ParkingSystem {
    private JTextField tfEntryTime;
    private JComboBox<String> cbVehicleType;
    private JButton btnCommit;
    private JPanel mainPanel;
    private JTextField tfStuName;
    private JTextField tfStuGen;
    private JList<String> lst1;
    private JButton btnCancel;
    private JButton btnAddNew;
    private JButton btnClear;
    private JButton btnDelete;
    private JButton btnSearchByID;
    private JButton btnDealeteById;
    private JButton btnShow;
    private JButton btnUpdateById;

    // Fields for the update functionality
    private JTextField txtId;
    private JTextField txtEntryTime;
    private JTextField txtExitTime;
    private JTextField txtStudentName;
    private JTextField txtStudentGender;
    private JTextField txtVehicleType;

    private DefaultListModel<String> listModel;
    private AtomicInteger recordIdGenerator;
    private AtomicInteger studentIdGenerator;
    private AtomicInteger vehicleIdGenerator;

    public ParkingSystem() {
        listModel = new DefaultListModel<>();
        lst1.setModel(listModel);
        recordIdGenerator = new AtomicInteger(1); // Starting record ID
        studentIdGenerator = new AtomicInteger(1); // Starting student ID
        vehicleIdGenerator = new AtomicInteger(1); // Starting vehicle ID

        // Initialize the update text fields
        txtId = new JTextField();
        txtEntryTime = new JTextField();
        txtExitTime = new JTextField();
        txtStudentName = new JTextField();
        txtStudentGender = new JTextField();
        txtVehicleType = new JTextField();

        // Set current time in entry time field
        tfEntryTime.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));

        btnCommit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Validate and parse input fields
                    if (tfStuName.getText().isEmpty() ||
                            tfStuGen.getText().isEmpty() ||
                            cbVehicleType.getSelectedItem() == null) {
                        JOptionPane.showMessageDialog(null, "Please fill all required fields.");
                        return;
                    }

                    // Parse input data
                    LocalTime entryTime = parseTime(tfEntryTime.getText().trim());
                    LocalDateTime entryDateTime = LocalDateTime.of(LocalDate.now(), entryTime); // Combine current date with the provided time
                    String studentName = tfStuName.getText().trim();
                    String studentGender = tfStuGen.getText().trim();
                    String vehicleType = cbVehicleType.getSelectedItem().toString().trim();

                    // Create Student and Vehicle objects
                    Student student = Student.builder().name(studentName).gender(studentGender).build();
                    Vehicle vehicle = Vehicle.builder().vehicleType(Vehicle.VehicleType.valueOf(vehicleType)).build();

                    // Use DAOs to add data to respective tables
                    StudentDao studentDao = new StudentDaoImpl();
                    int studentId = studentDao.addStudentAndGetId(student); // Add student and get generated ID
                    student.setId(studentId); // Set the generated ID back to the student object

                    VehicleDao vehicleDao = new VehicleDaoImpl();
                    int vehicleId = vehicleDao.addVehicleAndGetId(vehicle); // Add vehicle and get generated ID
                    vehicle.setId(vehicleId); // Set the generated ID back to the vehicle object

                    // Create Parking object
                    Parking parking = Parking.builder()
                            .entryTime(entryDateTime)
                            .exitTime(null) // Assuming exit time is not provided initially
                            .student(student)
                            .vehicle(vehicle)
                            .build();

                    // Use DAO to insert data into the parking_record table
                    ParkingDao parkingDao = new ParkingDaoImpl();
                    int recordId = parkingDao.addParking(parking);

                    // Fetch newly inserted record from parking_record to populate record_list
                    parking = parkingDao.searchParkingById(recordId);

                    // Use DAOs to fetch complete data from student and vehicle tables
                    student = studentDao.searchStudentById(parking.getStudent().getId());
                    vehicle = vehicleDao.searchVehicleByID(parking.getVehicle().getId());

                    // Add record to record_list table
                    ParkingDaoImpl parkingDaoImpl = (ParkingDaoImpl) parkingDao;
                    parkingDaoImpl.addRecordListEntry(parking, student, vehicle);

                    // Update listModel to display data from record_list
                    listModel.addElement(formatListEntry(parking));

                    JOptionPane.showMessageDialog(null, "Successfully added parking record!");

                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(null, "Error: Please enter valid time in the format HH:mm.");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Error: Invalid number format.");
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                }
            }
        });

        btnAddNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tfStuName.setText("");
                tfStuGen.setText("");
                cbVehicleType.setSelectedItem(null);
            }
        });

        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listModel.clear();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Delete data from parking table
                    ParkingDao parkingDao = new ParkingDaoImpl();
                    parkingDao.deleteAll();

                    // Delete data from student table
                    StudentDao studentDao = new StudentDaoImpl();
                    studentDao.deleteAll();

                    // Delete data from vehicle table
                    VehicleDao vehicleDao = new VehicleDaoImpl();
                    vehicleDao.deleteAll();

                    // Clear the list model
                    listModel.clear();

                    JOptionPane.showMessageDialog(null, "All data deleted successfully!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                }
            }
        });

        btnSearchByID.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchIdStr = JOptionPane.showInputDialog("Enter Record ID to search:");
                if (searchIdStr != null && !searchIdStr.isEmpty()) {
                    try {
                        int searchId = Integer.parseInt(searchIdStr);
                        ParkingDao parkingDao = new ParkingDaoImpl();
                        Parking parking = parkingDao.searchParkingById(searchId);
                        if (parking != null) {
                            // Display the result in lst1
                            listModel.clear();
                            String listEntry = String.format("Record ID: %d, Entry Time: %s, Student: %s, Vehicle ID: %d",
                                    parking.getRecordId(), parking.getEntryTime(),
                                    parking.getStudent().getName(), parking.getVehicle().getId());
                            listModel.addElement(listEntry);
                        } else {
                            JOptionPane.showMessageDialog(null, "No parking record found with ID: " + searchId);
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Error: Please enter a valid number for the ID.");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                    }
                }
            }
        });

        btnDealeteById.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String deleteIdStr = JOptionPane.showInputDialog("Enter Record ID to delete:");
                if (deleteIdStr != null && !deleteIdStr.isEmpty()) {
                    try {
                        int deleteId = Integer.parseInt(deleteIdStr);
                        ParkingDao parkingDao = new ParkingDaoImpl();
                        parkingDao.deleteById(deleteId);
                        JOptionPane.showMessageDialog(null, "Record with ID: " + deleteId + " deleted successfully.");

                        // Remove the entry from listModel if it's displayed there
                        for (int i = 0; i < listModel.size(); i++) {
                            String entry = listModel.getElementAt(i);
                            if (entry.contains("Record ID: " + deleteId)) {
                                listModel.removeElementAt(i);
                                break;
                            }
                        }

                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Error: Please enter a valid number for the ID.");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                    }
                }
            }
        });
        btnShow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ParkingDao parkingDao = new ParkingDaoImpl();
                List<Parking> allParkings = parkingDao.getAllParkings();
                listModel.clear();
                for (Parking parking : allParkings) {
                    listModel.addElement(formatListEntry(parking));
                }
            }

        });

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "Confirm Exit", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        btnUpdateById.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Prompt user for record ID
                String updateIdStr = JOptionPane.showInputDialog("Enter Record ID to update:");
                if (updateIdStr != null && !updateIdStr.isEmpty()) {
                    try {
                        int id = Integer.parseInt(updateIdStr);

                        // Retrieve input data for updating
                        String entryTimeStr = JOptionPane.showInputDialog("Enter new Entry Time (YYYY-MM-DDTHH:MM:SS) or leave empty:");
                        String exitTimeStr = JOptionPane.showInputDialog("Enter new Exit Time (YYYY-MM-DDTHH:MM:SS) or leave empty:");
                        String studentName = JOptionPane.showInputDialog("Enter new Student Name or leave empty:");
                        String studentGender = JOptionPane.showInputDialog("Enter new Student Gender or leave empty:");
                        String vehicleType = JOptionPane.showInputDialog("Enter new Vehicle Type or leave empty:");

                        LocalDateTime entryTime = entryTimeStr.isEmpty() ? null : LocalDateTime.parse(entryTimeStr);
                        LocalDateTime exitTime = exitTimeStr.isEmpty() ? null : LocalDateTime.parse(exitTimeStr);

                        // Create instances of the DAOs
                        ParkingDaoImpl parkingDao = new ParkingDaoImpl();
                        StudentDaoImpl studentDao = new StudentDaoImpl();
                        VehicleDaoImpl vehicleDao = new VehicleDaoImpl();

                        // Fetch the existing parking record
                        Parking parking = parkingDao.searchParkingById(id);
                        if (parking != null) {
                            // Update parking record if entry time is provided
                            if (entryTime != null) {
                                parking.setEntryTime(entryTime);
                            }
                            // Update parking record if exit time is provided
                            if (exitTime != null) {
                                parking.setExitTime(exitTime);
                            }
                            parkingDao.updateParkingById(parking);

                            // Fetch and update student record if student name or gender is provided
                            Student student = parking.getStudent();
                            if (!studentName.isEmpty()) {
                                student.setName(studentName);
                            }
                            if (!studentGender.isEmpty()) {
                                student.setGender(studentGender);
                            }
                            studentDao.updateStudentById(student);

                            // Fetch and update vehicle record if vehicle type is provided
                            Vehicle vehicle = parking.getVehicle();
                            if (!vehicleType.isEmpty()) {
                                vehicle.setVehicleType(Vehicle.VehicleType.valueOf(vehicleType));
                            }
                            vehicleDao.updateVehicleById(vehicle);

                            // Notify the user
                            JOptionPane.showMessageDialog(null, "Records updated successfully!");

                            // Update the displayed list entry if it's shown
                            for (int i = 0; i < listModel.size(); i++) {
                                String entry = listModel.getElementAt(i);
                                if (entry.contains("Record ID: " + id)) {
                                    String listEntry = String.format("Record ID: %d, Entry Time: %s, Student: %s, Vehicle ID: %d",
                                            parking.getRecordId(), parking.getEntryTime(),
                                            parking.getStudent().getName(), parking.getVehicle().getId());
                                    listModel.setElementAt(listEntry, i);
                                    break;
                                }
                            }

                        } else {
                            JOptionPane.showMessageDialog(null, "Record not found!");
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Error: Please enter a valid number for the ID.");
                    } catch (DateTimeParseException ex) {
                        JOptionPane.showMessageDialog(null, "Error: Please enter valid date-time in the format YYYY-MM-DDTHH:MM:SS.");
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(null, "Invalid vehicle type: " + ex.getMessage());
                    }
                }
            }
        });

    }
    private String formatListEntry(Parking parking) {
        return String.format("Record ID: %d, Entry Time: %s, Student Name: %s, Student Gender: %s, Vehicle ID: %d, Vehicle Type: %s",
                parking.getRecordId(), parking.getEntryTime(),
                parking.getStudent().getName(), parking.getStudent().getGender(),
                parking.getVehicle().getId(), parking.getVehicle().getVehicleType());
    }

    private LocalTime parseTime(String time) throws DateTimeParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return LocalTime.parse(time, formatter);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("ParkingSystem");
        frame.setContentPane(new ParkingSystem().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1200,700));
        frame.pack();
        frame.setVisible(true);
    }

    public void start() {
        JFrame frame = new JFrame("ParkingSystem");
        frame.setContentPane(this.mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1200,700));
        frame.pack();
        frame.setVisible(true);
    }
}
