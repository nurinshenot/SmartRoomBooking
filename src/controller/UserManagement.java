/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserManagement {

    // DIUBAH SUAI: Menggunakan penanda universal userID menggantikan matricNo yang lama
    private String currentUserID;
    private String currentName;
    private String currentRole;

    public UserManagement() {
        // Constructor dikosongkan kerana logik membaca terus melalui DatabaseConnection
    }

    // =========================================================================
    // 1. FUNGSI LOG MASUK (LOGIN) - Diubah suai menggunakan user_id dan role
    // =========================================================================
    public boolean login(String userID, String password) {
        // Mengambil nama dan role sekali semasa log masuk
        String query = "SELECT name, role FROM users WHERE user_id = ? AND password = ?";
        
        try (Connection conn = database.DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, userID);
            ps.setString(2, password);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    this.currentUserID = userID;
                    this.currentName = rs.getString("name");
                    this.currentRole = rs.getString("role");
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println("Ralat Log Masuk di UserManagement: " + e.getMessage());
            e.printStackTrace();
        }
        return false; 
    }

    // =========================================================================
    // 2. FUNGSI DAFTAR PELAJAR BAHARU (REGISTER) - Menggunakan universal user_id
    // =========================================================================
    public boolean registerNewStudent(String name, String userID, String email, String phoneNo, String password) {
        String sql = "INSERT INTO users (user_id, name, password, email, phone_no, role) VALUES (?, ?, ?, ?, ?, 'STUDENT')";
        
        try (Connection conn = database.DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, userID);
            ps.setString(2, name);
            ps.setString(3, password);
            ps.setString(4, email);
            ps.setString(5, phoneNo);
            
            int rowsInserted = ps.executeUpdate();
            return rowsInserted > 0;
            
        } catch (SQLException e) {
            System.out.println("Ralat Pendaftaran di UserManagement: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // =========================================================================
    // 3. FUNGSI MENGAMBIL DATA SESI PENGGUNA
    // =========================================================================
    public String getCurrentUserID() {
        return currentUserID;
    }

    public String getCurrentName() {
        return currentName;
    }

    public String getCurrentRole() {
        return currentRole;
    }
}