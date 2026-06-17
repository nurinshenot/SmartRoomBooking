package controller;

import database.DatabaseConnection;
import model.Room;
import factory.RoomFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomManagementController {

    // Constructor kosong sbb kita dah tak pakai mock data ArrayList di sini
    public RoomManagementController() {}

    // =========================================================================
    // 1. READ: Ambil Semua Bilik Dari MySQL (Polymorphism & Factory Pattern)
    // =========================================================================
    public List<Room> getAllRooms() {
        List<Room> list = new ArrayList<>();
        String query = "SELECT * FROM rooms";
        
        // Guna try-with-resources supaya connection ditutup automatik bila selesai
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                String id = rs.getString("room_id");
                String name = rs.getString("room_name");
                String type = rs.getString("room_type"); // 'STANDARD' atau 'PREMIUM'
                int capacity = rs.getInt("capacity");
                String facilities = rs.getString("facilities");
                boolean isAvailable = rs.getBoolean("is_available");
                
                // Guna Factory Pattern untuk cipta objek anak yang betul secara dinamik
                Room room = RoomFactory.createRoom(type, id, name, capacity, facilities);
                if (room != null) {
                    room.setAvailable(isAvailable);
                    list.add(room); // Masukkan dalam senarai untuk dihantar ke GUI
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching rooms from database!");
            e.printStackTrace();
        }
        return list;
    }

    // =========================================================================
    // 2. CREATE: Tambah Bilik Baru Ke Dalam MySQL
    // =========================================================================
    public boolean addRoom(String type, String roomId, String roomName, int capacity, String facilities) {
        String query = "INSERT INTO rooms (room_id, room_name, room_type, capacity, facilities) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, roomId);
            stmt.setString(2, roomName);
            stmt.setString(3, type.toUpperCase()); // Pastikan sentiasa huruf besar
            stmt.setInt(4, capacity);
            stmt.setString(5, facilities);
            
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0; // Pulangkan true jika berjaya insert
            
        } catch (SQLException e) {
            System.out.println("Error inserting room into database!");
            e.printStackTrace();
            return false;
        }
    }

    // =========================================================================
    // 3. DELETE: Padam Bilik Dengan Sekatan Berkembar (VERSI DIAGNOSTIK)
    // =========================================================================
    public boolean deleteRoom(String roomId) {
        if (roomId == null) return false;
        
        String cleanRoomId = roomId.trim(); // Contoh: "R01"
        
        // Ekstrak nombor sahaja jika DB simpan format nombor (Contoh: "R01" -> "1")
        String numericId = cleanRoomId.replaceAll("[^0-9]", "");
        if (!numericId.isEmpty()) {
            numericId = String.valueOf(Integer.parseInt(numericId));
        }

        // Query semakan yang sangat longgar (Janji ada perkataan ID tersebut dan status bukan 'cancelled'/'rejected')
        String checkQuery = "SELECT COUNT(*) FROM bookings WHERE (room_id LIKE ? OR room_id LIKE ?) AND LOWER(status) NOT IN ('cancelled', 'rejected', 'completed')";
        String deleteQuery = "DELETE FROM rooms WHERE room_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            
            System.out.println("====== SEMAKAN DIAGNOSTIK DELETE ======");
            System.out.println("Memeriksa sekatan untuk Room ID: " + cleanRoomId + " atau " + numericId);
            
            // 1. Jalankan semakan ke atas jadual bookings
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, "%" + cleanRoomId + "%");
                checkStmt.setString(2, "%" + numericId + "%");
                
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        int count = rs.getInt(1);
                        System.out.println("Log Sistem -> Bilik ini mempunyai [" + count + "] tempahan aktif.");
                        
                        if (count > 0) {
                            System.out.println("KESIMPULAN -> SEKATAN BERJAYA DIKUNCI! Padam dibatalkan.");
                            System.out.println("=======================================");
                            return false; // Pulangkan false ke GUI untuk sekat pemadaman
                        }
                    }
                }
            }
            
            // 2. Jika count == 0 (tiada booking aktif langsung), baru padam dari jadual rooms
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                deleteStmt.setString(1, cleanRoomId);
                int rowsDeleted = deleteStmt.executeUpdate();
                System.out.println("Log Sistem -> Status padam dari MySQL: " + (rowsDeleted > 0 ? "BERJAYA" : "GAGAL"));
                System.out.println("=======================================");
                return rowsDeleted > 0;
            }
            
        } catch (SQLException e) {
            System.out.println("!!! RALAT KRITIKAL: Nama kolum database 'room_id' atau 'status' dalam table bookings mungkin salah/tidak wujud !!!");
            e.printStackTrace();
            return false;
        }
    }
    // =========================================================================
    // 4. UPDATE: Kemas Kini Status Ketersediaan Bilik (Available/Maintenance)
    // =========================================================================
    public boolean updateRoomStatus(String roomId, boolean status) {
        String query = "UPDATE rooms SET is_available = ? WHERE room_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setBoolean(1, status);
            stmt.setString(2, roomId);
            
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
            
        } catch (SQLException e) {
            System.out.println("Error updating room status!");
            e.printStackTrace();
            return false;
        }
    }
    
    // =========================================================================
    // 5. UTILITY: Jana ID Bilik Seterusnya Secara Automatik (TELAH DIBETULKAN)
    // =========================================================================
    public String generateNextRoomId() {
        String query = "SELECT room_id FROM rooms ORDER BY room_id DESC LIMIT 1";
        String nextId = "R01"; // Default jika database kosong
        
        // Membaiki pepijat syntax package dengan menggunakan DatabaseConnection secara langsung
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                String lastId = rs.getString("room_id"); // Contoh: "R10"
                String numberPart = lastId.substring(1); // "10"
                int currentNum = Integer.parseInt(numberPart);
                int nextNum = currentNum + 1;
                
                if (nextNum < 10) {
                    nextId = String.format("R0%d", nextNum);
                } else {
                    nextId = String.format("R%d", nextNum);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error generating automatic Room ID!");
            e.printStackTrace();
        }
        return nextId;
    }

    // =========================================================================
    // 6. READ: Ambil Senarai Tempahan Untuk Dipaparkan Pada JTable Librarian
    // =========================================================================
    public List<Object[]> getAllBookings() {
        List<Object[]> bookingList = new ArrayList<>();
        String query = "SELECT booking_id, student_name, room_id, time_slot, status FROM bookings";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Object[] bookingData = {
                    "B0" + rs.getInt("booking_id"), // Hiasan paparan ID (cth: B01)
                    rs.getString("student_name"),
                    rs.getString("room_id"),
                    rs.getString("time_slot"),
                    rs.getString("status")
                };
                bookingList.add(bookingData);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching bookings from database!");
            e.printStackTrace();
        }
        return bookingList;
    }
}