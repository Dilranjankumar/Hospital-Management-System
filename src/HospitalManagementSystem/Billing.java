package HospitalManagementSystem;
import HospitalManagementSystem.Billing;

import java.sql.*;
import java.util.Scanner;

public class Billing {

    private Connection connection;
    private Scanner scanner;
    private Doctor doctor;

    public Billing(Connection connection, Scanner scanner, Doctor doctor) {
        this.connection = connection;
        this.scanner = scanner;
        this.doctor = doctor;
    }

    public void generateBill(int patientId, double appointmentCost) {
        try {
            // Retrieve patient information
            String patientQuery = "SELECT * FROM patients WHERE id=?";
            PreparedStatement patientStatement = connection.prepareStatement(patientQuery);
            patientStatement.setInt(1, patientId);
            ResultSet patientResult = patientStatement.executeQuery();

            if (patientResult.next()) {
                String patientName = patientResult.getString("name");
                int patientAge = patientResult.getInt("age");
                // Print patient information
                System.out.println("Patient Name: " + patientName);
                System.out.println("Patient Age: " + patientAge);

                // Retrieve appointment details for the patient
                String appointmentQuery = "SELECT * FROM appointments WHERE patient_id=?";
                PreparedStatement appointmentStatement = connection.prepareStatement(appointmentQuery);
                appointmentStatement.setInt(1, patientId);
                ResultSet appointmentResult = appointmentStatement.executeQuery();

                double totalBill = 0;

                // Iterate through appointments to calculate total bill
                while (appointmentResult.next()) {
                    int appointmentId = appointmentResult.getInt("id");
                    String appointmentDate = appointmentResult.getString("appointment_date");

                    // Print appointment details
                    System.out.println("Appointment ID: " + appointmentId);
                    System.out.println("Appointment Date: " + appointmentDate);
                    System.out.println("Appointment Cost: $" + appointmentCost);

                    // Add appointment cost to total bill
                    totalBill += appointmentCost;
                }

                // Print total bill
                System.out.println("Total Bill: $" + totalBill);
            } else {
                System.out.println("Patient not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
