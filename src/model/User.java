package model;

public class User {
    // Diubah suai: Ditukar menjadi id dan name supaya konsisten dengan struktur pangkalan data baharu
    private String id; // Boleh jadi matricNo (Student) atau staffID (Librarian)
    private String name;
    private String password;
    private String email;
    private String phoneNo;
    
    // Constructor baharu tanpa userID lama
    public User(String name, String password, String email, String phoneNo, String id) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.phoneNo = phoneNo;
        this.id = id;
    }
    
    // Getter dan Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhoneNo() {
        return phoneNo;
    }
    
    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }
    
    // Method kosong tambahan (Boleh dioverride oleh sub-class jika perlu)
    public void displayRole() {
        System.out.println("Role: General User");
    }
    
    public void login() {
        System.out.println("Login successful");
    }
    
    public void logout() {
        System.out.println("Logout successful");
    }
    
    public void updateProfile() {
        System.out.println("Profile updated");
    }
    
    public void forgotPassword() {
        System.out.println("Password reset");
    }
}