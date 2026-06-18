package model;

public class Booking {
    private int bookingId;
    private Student student; // Diperlukan oleh controller: b.getStudent()
    private Room room;       // Diperlukan oleh controller: b.getRoom()
    private String bookingDate;
    private String timeSlot;
    private String status;

    // Constructor yang sepadan dengan createBooking dalam Controller
    public Booking(int bookingId, String bookingDate, String timeSlot, Student student, Room room) {
        this.bookingId = bookingId;
        this.bookingDate = bookingDate;
        this.timeSlot = timeSlot;
        this.student = student;
        this.room = room;
        this.status = "CONFIRMED"; // Status default apabila berjaya diproses
    }

    // --- GETTER & SETTER ---
    public int getBookingId() { return bookingId; }
    
    /**
     * Mengembalikan ID Tempahan dalam format String 'B01', 'B02', dll.
     */
    public String getFormattedBookingId() {
        return String.format("B%02d", this.bookingId);
    }

    public String getStudentId() { return student != null ? student.getStudentId() : ""; }
    public String getRoomId() { return room != null ? room.getRoomId() : ""; }
    public String getBookingDate() { return bookingDate; }
    public String getTimeSlot() { return timeSlot; }
    public String getStatus() { return status; }
    
    // Kaedah tambahan yang dipanggil oleh BookingController
    public Student getStudent() { return student; }
    public Room getRoom() { return room; }
    public String getBookingTime() { return timeSlot; } // Alias untuk timeSlot jika digunakan berganti

    public void setStatus(String status) {
        this.status = status;
    }

    // Simulasi simpan/padam data
    public void saveBooking() {
        System.out.println("Booking " + bookingId + " saved to simulation database.");
    }

    public void deleteBooking() {
        this.status = "CANCELLED";
        System.out.println("Booking " + bookingId + " status updated to CANCELLED.");
    }
}