package controller;

import database.DatabaseConnection;
import factory.RoomFactory; // Pastikan package factory di-import
import model.Room;
import model.Booking;
import model.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingController {

    public BookingController() {
    }

    /**
     * Menarik semua senarai bilik daripada database MySQL menggunakan Factory Pattern
     */
    public Room[] getRoomList() {
        List<Room> rooms = new ArrayList<>();
        String query = "SELECT * FROM rooms";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                // MENGGUNAKAN FACTORY: Mengatasi isu abstract class Room
                Room room = RoomFactory.createRoom(
                    rs.getString("room_type"),   // Ditambah lajur jenis bilik
                    rs.getString("room_id"),
                    rs.getString("room_name"),
                    rs.getInt("capacity"),
                    rs.getString("facilities")
                );
                
                if (room != null) {
                    room.setAvailable(rs.getBoolean("is_available"));
                    rooms.add(room);
                }
            }
        } catch (SQLException e) {
            System.out.println("Ralat semasa mengambil senarai bilik!");
            e.printStackTrace();
        }
        return rooms.toArray(new Room[0]);
    }

    /**
     * Memeriksa jika bilik bertindih dengan tempahan sedia ada
     */
    public boolean checkRoomAvailability(String roomID, String date, String time) {
        Room room = findRoomById(roomID);
        if (room == null || !room.isAvailable()) {
            return false;
        }

        String query = "SELECT COUNT(*) FROM bookings WHERE room_id = ? AND booking_date = ? AND time_slot = ? AND status = 'CONFIRMED'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, roomID);
            stmt.setString(2, date);
            stmt.setString(3, time);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count == 0; 
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Menyimpan tempahan baru ke dalam database MySQL
     */
    public Booking createBooking(Student student, String roomID, String date, String time) {
        if (!checkRoomAvailability(roomID, date, time)) {
            System.out.println("Bilik tidak kosong pada slot tersebut.");
            return null;
        }

        Room room = findRoomById(roomID);
        String query = "INSERT INTO bookings (student_id, student_name, room_id, booking_date, time_slot, status) VALUES (?, ?, ?, ?, ?, 'CONFIRMED')";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, student.getStudentId());   
            stmt.setString(2, student.getStudentName()); 
            stmt.setString(3, roomID);
            stmt.setString(4, date);
            stmt.setString(5, time);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        Booking newBooking = new Booking(generatedId, date, time, student, room);
                        return newBooking;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("\n=== DATABASE ERROR DETECTED ===");
            System.out.println("Mesej Ralat: " + e.getMessage());
            System.out.println("===============================\n");
        }
        return null;
    }

    /**
     * Mengambil semua sejarah tempahan untuk pelajar tertentu
     */
    public List<Booking> getBookingsForStudent(Student student) {
        List<Booking> result = new ArrayList<>();
        String query = "SELECT * FROM bookings WHERE student_id = ?";
        
        class RawBooking {
            int bookingId;
            String roomId;
            String date;
            String timeSlot;
            String status;

            RawBooking(int id, String rId, String d, String t, String s) {
                this.bookingId = id;
                this.roomId = rId;
                this.date = d;
                this.timeSlot = t;
                this.status = s;
            }
        }
        
        List<RawBooking> rawList = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, student.getStudentId());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rawList.add(new RawBooking(
                        rs.getInt("booking_id"),
                        rs.getString("room_id"),
                        rs.getString("booking_date"),
                        rs.getString("time_slot"),
                        rs.getString("status")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Ralat pemprosesan jadual bookings: " + e.getMessage());
            return result;
        }

        for (RawBooking raw : rawList) {
            Room room = findRoomById(raw.roomId);
            Booking b = new Booking(raw.bookingId, raw.date, raw.timeSlot, student, room);
            b.setStatus(raw.status);
            result.add(b);
        }
        
        return result;
    }

    /**
     * Mengemas kini status tempahan kepada 'CANCELLED'
     */
    public boolean cancelBooking(String bookingID) {
        String query = "UPDATE bookings SET status = 'CANCELLED' WHERE booking_id = ? AND status = 'CONFIRMED'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            String cleanID = bookingID.trim().toUpperCase();
            if (cleanID.startsWith("B")) {
                cleanID = cleanID.substring(1); 
            }
            
            stmt.setInt(1, Integer.parseInt(cleanID)); 
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0; 
            
        } catch (NumberFormatException e) {
            System.out.println("Format ID Tempahan tidak sah: " + bookingID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Mendapatkan status semasa sesuatu tempahan
     */
    public String getBookingStatus(String bookingID) {
        String query = "SELECT status FROM bookings WHERE booking_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            String cleanID = bookingID.trim().toUpperCase();
            if (cleanID.startsWith("B")) {
                cleanID = cleanID.substring(1);
            }
            
            stmt.setInt(1, Integer.parseInt(cleanID));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("status");
                }
            }
        } catch (NumberFormatException e) {
            return "Invalid ID format.";
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Booking not found.";
    }

    /**
     * Helper Method: Mencari maklumat bilik berdasarkan ID dengan Factory Pattern
     */
    private Room findRoomById(String id) {
        String query = "SELECT * FROM rooms WHERE room_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // MENGGUNAKAN FACTORY: Mengatasi isu abstract class Room
                    Room room = RoomFactory.createRoom(
                        rs.getString("room_type"),
                        rs.getString("room_id"),
                        rs.getString("room_name"),
                        rs.getInt("capacity"),
                        rs.getString("facilities")
                    );
                    if (room != null) {
                        room.setAvailable(rs.getBoolean("is_available"));
                    }
                    return room;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}