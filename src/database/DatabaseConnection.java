/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Tukar nama database ikut apa yang awak akan create dalam MySQL/XAMPP esok (cth: library_db)
    private static final String URL = "jdbc:mysql://localhost:3306/library_db";
    private static final String USER = "root"; // Default username XAMPP
    private static final String PASSWORD = ""; // Default password XAMPP (biasanya kosong)
    
    private static Connection connection = null;

    /**
     * Method untuk dapatkan sambungan ke database (Singleton-like pattern)
     */
    public static Connection getConnection() {
        try {
            // Jika sambungan belum ada atau dah tertutup, buka sambungan baru
            if (connection == null || connection.isClosed()) {
                // Daftarkan Driver MySQL (Pastikan esok dah add MySQL JDBC Driver dekat Libraries!)
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Database Connected Successfully!");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found. Make sure to add it to Libraries!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Failed to connect to the database!");
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Method untuk tutup sambungan apabila aplikasi ditutup
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}