package HospitalManagementSystem;
import HospitalManagementSystem.Billing;

import java.sql.*;
import java.util.Scanner;

public class Doctor {

    private Connection connection;
    private Scanner scanner;
    private Billing billing;

    public Doctor(Connection connection, Scanner scanner , Billing billing) {
        this.connection = connection;
        this.scanner = scanner;
        this.billing = billing;
    }

    public void addDoctor() {
        System.out.print("Enter Doctor Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Doctor Specialization: ");
        String specialization = scanner.nextLine();

        try {
            String query = "INSERT INTO doctors(name, specialization) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, specialization);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Doctor added successfully!");
            } else {
                System.out.println("Failed to add doctor.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void viewDoctors() {
        String query = "SELECT * FROM doctors";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            System.out.println("Doctors:");
            System.out.println("*------------*------------------*-------------------*");
            System.out.println("| Doctor ID  | Name             | Specialization    |");
            System.out.println("*------------*------------------*-------------------*");

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String specialization = resultSet.getString("specialization");
                System.out.printf("|%-12d|%-18s|%-19s|\n", id, name, specialization);
            }
            System.out.println("*------------*------------------*-------------------*");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public boolean getDoctorById(int id) {
        String query = "SELECT * FROM doctors WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next(); // If resultSet has next, then doctor with given ID exists
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Error occurred while executing query
        }
    }

    public void updateDoctorInformation() {
        System.out.print("Enter Doctor ID to update information: ");
        int doctorId = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter New Doctor Name: ");
        String newName = scanner.nextLine();
        System.out.print("Enter New Doctor Specialization: ");
        String newSpecialization = scanner.nextLine();

        try {
            String query = "UPDATE doctors SET name=?, specialization=? WHERE id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, newName);
            preparedStatement.setString(2, newSpecialization);
            preparedStatement.setInt(3, doctorId);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Doctor information updated successfully!");
            } else {
                System.out.println("Failed to update doctor information. Please check the doctor ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteDoctor() {
        System.out.print("Enter Doctor ID to delete: ");
        int doctorId = scanner.nextInt();

        try {
            String query = "DELETE FROM doctors WHERE id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctorId);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Doctor deleted successfully!");
            } else {
                System.out.println("Failed to delete doctor. Please check the doctor ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void searchDoctors() {
        System.out.print("Enter Doctor Name or ID to search: ");
        String searchQuery = scanner.nextLine();

        try {
            String query = "SELECT * FROM doctors WHERE name LIKE ? OR id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, "%" + searchQuery + "%");
            preparedStatement.setString(2, searchQuery);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                System.out.println("Search results:");
                System.out.println("*------------*------------------*-------------------*");
                System.out.println("| Doctor ID  | Name             | Specialization    |");
                System.out.println("*------------*------------------*-------------------*");
                do {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    String specialization = resultSet.getString("specialization");
                    System.out.printf("|%-12d|%-18s|%-19s|\n", id, name, specialization);
                } while (resultSet.next());
                System.out.println("*------------*------------------*-------------------*");
            } else {
                System.out.println("No matching doctors found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void viewDoctorAppointments() {
        System.out.print("Enter Doctor ID to view appointments: ");
        int doctorId = scanner.nextInt();

        try {
            String query = "SELECT * FROM appointments WHERE doctor_id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctorId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                System.out.println("Doctor Appointments:");
                System.out.println("*-------------------*---------------------*");
                System.out.println("| Appointment ID    | Appointment Date    |");
                System.out.println("*-------------------*---------------------*");
                do {
                    int appointmentId = resultSet.getInt("id");
                    String appointmentDate = resultSet.getString("appointment_date");
                    System.out.printf("|%-19d|%-21s|\n", appointmentId, appointmentDate);
                } while (resultSet.next());
                System.out.println("*-------------------*---------------------*");
            } else {
                System.out.println("No appointments found for this doctor.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

public void checkAppointments() {
    try {
        System.out.print("Enter Doctor ID: ");
        int doctorId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        // Retrieve doctor's appointments
        String query = "SELECT * FROM appointments WHERE doctor_id=? AND checked=0";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, doctorId);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            System.out.println("Appointments for Doctor ID " + doctorId + ":");
            System.out.println("*-------------------*---------------------*---------------------*");
            System.out.println("| Appointment ID    | Appointment Date    | Patient ID          |");
            System.out.println("*-------------------*---------------------*---------------------*");
            do {
                int appointmentId = resultSet.getInt("id");
                String appointmentDate = resultSet.getString("appointment_date");
                int patientId = resultSet.getInt("patient_id");
                System.out.printf("|%-19d|%-21s|%-21d|\n", appointmentId, appointmentDate, patientId);
            } while (resultSet.next());

            System.out.println("*-------------------*---------------------*---------------------*");
            System.out.print("Enter Appointment ID to check: ");
            int appointmentIdToCheck = scanner.nextInt();

            // Mark appointment as checked
            String updateQuery = "UPDATE appointments SET checked=1 WHERE id=?";
            PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
            updateStatement.setInt(1, appointmentIdToCheck);
            int rowsAffected = updateStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Appointment checked successfully!");

                // Generate bill for the patient
                String patientIdQuery = "SELECT patient_id FROM appointments WHERE id=?";
                PreparedStatement patientIdStatement = connection.prepareStatement(patientIdQuery);
                patientIdStatement.setInt(1, appointmentIdToCheck);
                ResultSet patientIdResultSet = patientIdStatement.executeQuery();
                if (patientIdResultSet.next()) {
                    int patientId = patientIdResultSet.getInt("patient_id");
                    billing.generateBill(patientId, 100.0); // Example: Generate bill with amount 100.0
                }
            } else {
                System.out.println("Failed to check appointment. Please try again.");
            }
        } else {
            System.out.println("No unchecked appointments found for this doctor.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
}
