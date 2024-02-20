package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class Patient {

    private Connection connection;
    private Scanner scanner;

    public Patient(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    public void addPatient() {
        System.out.print("Enter Patient Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Patient Age: ");
        int age = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter Patient Gender: ");
        String gender = scanner.nextLine();

        try {
            String query = "INSERT INTO patients(name, age, gender) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, age);
            preparedStatement.setString(3, gender);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Patient added successfully!");
            } else {
                System.out.println("Failed to add patient.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void viewPatients() {
        String query = "SELECT * FROM patients";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            System.out.println("Patients:");
            System.out.println("*-----------*---------------*-------*--------*");
            System.out.println("| Patient ID| Name          | Age   | Gender |");
            System.out.println("*-----------*---------------*-------*--------*");

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                String gender = resultSet.getString("gender");
                System.out.printf("|%-11d|%-15s|%-7d|%-8s|\n", id, name, age, gender);
            }
            System.out.println("*-----------*---------------*-------*--------*");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePatientInformation() {
        System.out.print("Enter Patient ID to update information: ");
        int patientId = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter New Patient Name: ");
        String newName = scanner.nextLine();
        System.out.print("Enter New Patient Age: ");
        int newAge = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter New Patient Gender: ");
        String newGender = scanner.nextLine();

        try {
            String query = "UPDATE patients SET name=?, age=?, gender=? WHERE id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, newName);
            preparedStatement.setInt(2, newAge);
            preparedStatement.setString(3, newGender);
            preparedStatement.setInt(4, patientId);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Patient information updated successfully!");
            } else {
                System.out.println("Failed to update patient information. Please check the patient ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletePatient() {
        System.out.print("Enter Patient ID to delete: ");
        int patientId = scanner.nextInt();

        try {
            String query = "DELETE FROM patients WHERE id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, patientId);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Patient deleted successfully!");
            } else {
                System.out.println("Failed to delete patient. Please check the patient ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void searchPatients() {
        System.out.print("Enter Patient Name or ID to search: ");
        String searchQuery = scanner.nextLine();

        try {
            String query = "SELECT * FROM patients WHERE name LIKE ? OR id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, "%" + searchQuery + "%");
            preparedStatement.setString(2, searchQuery);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                System.out.println("Search results:");
                System.out.println("*-----------*---------------*-------*--------*");
                System.out.println("| Patient ID| Name          | Age   | Gender |");
                System.out.println("*-----------*---------------*-------*--------*");
                do {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    int age = resultSet.getInt("age");
                    String gender = resultSet.getString("gender");
                    System.out.printf("|%-11d|%-15s|%-7d|%-8s|\n", id, name, age, gender);
                } while (resultSet.next());
                System.out.println("*-----------*---------------*-------*--------*");
            } else {
                System.out.println("No matching patients found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void viewPatientAppointments() {
        System.out.print("Enter Patient ID to view appointments: ");
        int patientId = scanner.nextInt();

        try {
            String query = "SELECT * FROM appointments WHERE patient_id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, patientId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                System.out.println("Patient Appointments:");
                System.out.println("*-------------------*-------------------*---------------------*");
                System.out.println("| Appointment ID    | Doctor Name       | Appointment Date    |");
                System.out.println("*-------------------*-------------------*---------------------*");
                do {
                    int appointmentId = resultSet.getInt("id");
                    int doctorId = resultSet.getInt("doctor_id");
                    String appointmentDate = resultSet.getString("appointment_date");
                    String doctorName = getDoctorName(connection, doctorId);
                    System.out.printf("|%-19d|%-19s|%-21s|\n", appointmentId, doctorName, appointmentDate);
                } while (resultSet.next());
                System.out.println("*-------------------*-------------------*---------------------*");
            } else {
                System.out.println("No appointments found for this patient.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public boolean getPatientById(int id) {
        String query = "SELECT * FROM patients WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next(); // If resultSet has next, then patient with given ID exists
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Error occurred while executing query
        }
    }

    private String getDoctorName(Connection connection, int doctorId) throws SQLException {
        String query = "SELECT name FROM doctors WHERE id=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, doctorId);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next() ? resultSet.getString("name") : "Unknown";
        }
    }
}
