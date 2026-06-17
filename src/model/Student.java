package model;

/**
 * Model class untuk Student yang mewarisi kelas User.
 * Bersih, tersusun, dan serasi dengan modul Booking & User Management.
 */
public class Student extends User {
    
    private String matricNo;
    
    // Constructor dengan 5 parameter utama
    public Student(String name, String password, String email, String phoneNo, String matricNo) {
        // Memanggil constructor kelas induk (User)
        super(name, password, email, phoneNo, matricNo);
        this.matricNo = matricNo;
    }
    
    // --- GETTER & SETTER UTAMA ---
    
    public String getMatricNo() {
        return matricNo;
    }

    public void setMatricNo(String matricNo) {
        this.matricNo = matricNo;
    }
    
    // --- JALAN PINTAS (HELPER METHODS) UNTUK BOOKING UI ---
    // Ditambah supaya kod BookingUI kawan awak yang memanggil .getStudentId() 
    // atau .getStudentName() tidak pecah/error lagi.
    
    public String getStudentId() {
        return this.matricNo; // Mengembalikan matricNo sebagai ID pelajar
    }
    
    public String getStudentName() {
        return getName(); // Mengambil data nama dari kelas induk User
    }
    
    // --- POLIMORFISME (OVERRIDE) ---
    
    @Override
    public void displayRole() {
        System.out.println("Role: Student");
    }
}