package model;

// Menggunakan konsep pewarisan (Inheritance) daripada kelas induk User
public class Librarian extends User {
    
    private String staffID; // Diubah suai: Nama pemboleh ubah diselaraskan menjadi staffID
    
    // Diubah suai: Constructor menerima staffID dan menghantarnya ke kelas induk User sebagai ID unik
    public Librarian(String name, String password, String email, String phoneNo, String staffID) {
        
        // Memanggil constructor kelas induk User (staffID bertindak sebagai ID unik utama menggantikan matricNo)
        super(name, password, email, phoneNo, staffID);
        
        this.staffID = staffID; // Memperbetulkan ralat logik pemetaan parameter asal
    }
    
    // Getter dan Setter untuk staffID
    public String getStaffID() {
        return staffID;
    }

    public void setStaffID(String staffID) {
        this.staffID = staffID;
    }
    
    @Override
    public void displayRole() {
        System.out.println("Role: Librarian");
    }
}