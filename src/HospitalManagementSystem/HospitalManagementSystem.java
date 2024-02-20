package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem {
    private static final double APPOINTMENT_COST = 100.0; // Example cost, you can adjust it as needed

    private static final String url = "jdbc:mysql://localhost:3305/hospital";
    private static final String username = "root";
    private static final String password = "W7301@jqir#";
    private static final Connection connection; // Declare connection variable
    private static final Scanner scanner = new Scanner(System.in);

    static {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        connection = conn;
    }

    private static final Billing billing = new Billing(connection, scanner, null); // Initialize with null
    private static final Doctor doctor = new Doctor(connection, scanner, billing); // Initialize with billing

    public static void main(String[] args) {
        Patient patient = new Patient(connection, scanner);

        while (true) {
            System.out.println("Hospital Management System");
            System.out.println("1. Add Patient");
            System.out.println("2. View Patients");
            System.out.println("3. Update Patient Information");
            System.out.println("4. Delete Patient");
            System.out.println("5. Search Patients");
            System.out.println("6. View Patient Appointments");
            System.out.println("7. Add Doctor");
            System.out.println("8. View Doctors");
            System.out.println("9. Update Doctor Information");
            System.out.println("10. Delete Doctor");
            System.out.println("11. Search Doctors");
            System.out.println("12. View Doctor Appointments");
            System.out.println("13. Book Appointment");
            System.out.println("14. View Appointments");
            System.out.println("15. Cancel Appointment");
            System.out.println("16. Billing / Invoice");
            System.out.println("17. check Appointment");
            System.out.println("18. Exit");
            System.out.println("Enter Your Choice:");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    patient.addPatient();
                    System.out.println();
                    break;
                case 2:
                    patient.viewPatients();
                    System.out.println();
                    break;
                case 3:
                    patient.updatePatientInformation();
                    System.out.println();
                    break;
                case 4:
                    patient.deletePatient();
                    System.out.println();
                    break;
                case 5:
                    patient.searchPatients();
                    System.out.println();
                    break;
                case 6:
                    patient.viewPatientAppointments();
                    System.out.println();
                    break;
                case 7:
                    doctor.addDoctor();
                    System.out.println();
                    break;
                case 8:
                    doctor.viewDoctors();
                    System.out.println();
                    break;
                case 9:
                    doctor.updateDoctorInformation();
                    System.out.println();
                    break;
                case 10:
                    doctor.deleteDoctor();
                    System.out.println();
                    break;
                case 11:
                    doctor.searchDoctors();
                    System.out.println();
                    break;
                case 12:
                    doctor.viewDoctorAppointments();
                    System.out.println();
                    break;
                case 13:
                    bookAppointment(patient, doctor, connection);
                    System.out.println();
                    break;
                case 14:
                    viewAppointments(connection);
                    System.out.println();
                    break;
                case 15:
                    cancelAppointment(connection);
                    System.out.println();
                    break;
                case 16:
                    generatePatientBill(billing);
                    break;
                case 17:
                    doctor.checkAppointments();
                    System.out.println();
                    break;

                case 18:
                    System.out.println("Thank You! For Using Hospital Management System!");
                    return;
                default:
                    System.out.println("Enter valid Choice!!! ");
            }
        }
    }

    private static void bookAppointment(Patient patient, Doctor doctor, Connection connection) {
        System.out.println("Book Appointment:");
        System.out.print("Enter Patient ID: ");
        int patientId = scanner.nextInt();
        System.out.print("Enter Doctor ID: ");
        int doctorId = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter Appointment Date (yyyy-MM-dd): ");
        String appointmentDate = scanner.nextLine();

        if (patient.getPatientById(patientId) && doctor.getDoctorById(doctorId)) {
            if (checkDoctorAvailability(doctorId, appointmentDate, connection)) {
                try {
                    String query = "INSERT INTO appointments(patient_id, doctor_id, appointment_date) VALUES (?, ?, ?)";
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setInt(1, patientId);
                    preparedStatement.setInt(2, doctorId);
                    preparedStatement.setString(3, appointmentDate);

                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Appointment booked successfully!");
                    } else {
                        System.out.println("Failed to book appointment.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Doctor not available on this date.");
            }
        } else {
            System.out.println("Either doctor or patient doesn't exist.");
        }
    }

    private static boolean checkDoctorAvailability(int doctorId, String appointmentDate, Connection connection) {
        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id=? AND appointment_date=?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setString(2, appointmentDate);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void viewAppointments(Connection connection) {
        System.out.println("View Appointments:");
        System.out.print("Enter Patient ID: ");
        int patientId = scanner.nextInt();

        try {
            String query = "SELECT a.id, d.name AS doctor_name, a.appointment_date " +
                    "FROM appointments a " +
                    "JOIN doctors d ON a.doctor_id = d.id " +
                    "WHERE a.patient_id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, patientId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                System.out.println("Appointments for Patient ID " + patientId + ":");
                System.out.println("*-------------------*---------------------*---------------------*");
                System.out.println("| Appointment ID    | Doctor Name         | Appointment Date    |");
                System.out.println("*-------------------*---------------------*---------------------*");
                do {
                    int appointmentId = resultSet.getInt("id");
                    String doctorName = resultSet.getString("doctor_name");
                    String appointmentDate = resultSet.getString("appointment_date");
                    System.out.printf("|%-19d|%-21s|%-21s|\n", appointmentId, doctorName, appointmentDate);
                } while (resultSet.next());
                System.out.println("*-------------------*---------------------*---------------------*");
            } else {
                System.out.println("No appointments found for this patient.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void generatePatientBill(Billing billing) {
        System.out.print("Enter Patient ID to generate bill: ");
        int patientId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        // Generate bill for the patient
        billing.generateBill(patientId, APPOINTMENT_COST); // Pass the appointment cost here
        System.out.println();
    }

    private static void cancelAppointment(Connection connection) {
        System.out.println("Cancel Appointment:");
        System.out.print("Enter Appointment ID to cancel: ");
        int appointmentId = scanner.nextInt();

        try {
            String query = "DELETE FROM appointments WHERE id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, appointmentId);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Appointment canceled successfully!");
            } else {
                System.out.println("Failed to cancel appointment. Please check the appointment ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
