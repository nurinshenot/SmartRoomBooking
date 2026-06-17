package utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet; 
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDatabase {
    
    public UserDatabase() {
        // Constructor kosong
    }

    // =========================================================================
    // FUNGSI LOG MASUK (LOGIN USER) - DISERAGAMKAN MENGGUNAKAN USER_ID
    // =========================================================================
    public static String loginUser(String userID, String password) {
        // DIUBAH SUAI: Semak berdasarkan kolum global 'user_id'
        String query = "SELECT role FROM users WHERE user_id = ? AND password = ?";
        
        // DIUBAH SUAI: Menggunakan DatabaseConnection berpusat
        try (Connection conn = database.DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, userID);
            ps.setString(2, password);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Memulangkan role ('LIBRARIAN' atau 'STUDENT')
                    return rs.getString("role"); 
                }
            }
        } catch (SQLException e) {
            System.out.println("Ralat loginUser di UserDatabase: " + e.getMessage());
            e.printStackTrace();
        }
        return null; 
    }

    // =========================================================================
    // FUNGSI DAFTAR PELAJAR BAHARU - DISERAGAMKAN MENGGUNAKAN USER_ID
    // =========================================================================
    public static boolean registerStudent(String name, String password, String email, String phoneNo, String userID) {
        // DIUBAH SUAI: Memasukkan data terus ke kolum 'user_id'
        String insertUserSQL = "INSERT INTO users (user_id, name, password, email, phone_no, role) VALUES (?, ?, ?, ?, ?, 'STUDENT')";
        
        try (Connection conn = database.DatabaseConnection.getConnection();
             PreparedStatement psUser = conn.prepareStatement(insertUserSQL)) {
            
            psUser.setString(1, userID);
            psUser.setString(2, name);     
            psUser.setString(3, password); 
            psUser.setString(4, email);
            psUser.setString(5, phoneNo);
            
            int rowsInserted = psUser.executeUpdate();
            return rowsInserted > 0;
            
        } catch (SQLException e) {
            System.out.println("Ralat SQL Semasa Mendaftar di UserDatabase: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // =========================================================================
    // FUNGSI SENARAI PELAJAR - DISEMPURNAKAN UNTUK KOLUM USER_ID
    // =========================================================================
    public static List<String[]> getAllStudents() {
        List<String[]> studentList = new ArrayList<>();
        // DIUBAH SUAI: Mengambil kolum 'user_id' menggantikan 'matric_no' lama
        String query = "SELECT name, user_id, email, phone_no FROM users WHERE role = 'STUDENT'";

        try (Connection conn = database.DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String[] student = new String[4];
                student[0] = rs.getString("name");
                student[1] = rs.getString("user_id"); // Diubah suai ke user_id
                student[2] = rs.getString("email");
                student[3] = rs.getString("phone_no");
                studentList.add(student);
            }
        } catch (SQLException e) {
            System.out.println("Ralat getAllStudents di UserDatabase: " + e.getMessage());
            e.printStackTrace();
        }
        return studentList;
    }
}