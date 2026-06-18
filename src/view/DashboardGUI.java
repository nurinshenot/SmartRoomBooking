package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DashboardGUI extends JFrame {

    private JButton btnMyProfile;
    private JButton btnActionService; 
    private JButton btnLogout;
    private String loggedInUser; // Menyimpan data sesi ID pengguna (Universal: Boleh memegang Matric No / Staff ID)
    private String userRole;     // Menyimpan data sesi peranan ('STUDENT' / 'LIBRARIAN')

    // Constructor menerima DUA parameter (userID & role) daripada LoginGUI
    public DashboardGUI(String userID, String role) {
        this.loggedInUser = userID;
        this.userRole = role;

        // Window Setup
        setTitle("Smart Library Room Booking System - Dashboard");
        setSize(550, 450); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main background panel (Soft light mint/cyan color)
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(new Color(240, 255, 255)); 

        // White Card container in the center
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(null);
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBounds(40, 30, 455, 350); 
        
        // Main Header Title (Deep Navy Blue)
        JLabel lblTitle = new JLabel("Smart Library Room Booking Systemt", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(new Color(3, 43, 83));
        lblTitle.setBounds(20, 25, 415, 30);

        // Memaparkan ID Pengguna (userID) dan Peranan yang sedang aktif
        JLabel lblWelcome = new JLabel("Welcome, " + loggedInUser + " (" + userRole + ")!");
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 14));
        lblWelcome.setForeground(new Color(21, 122, 151));
        lblWelcome.setBounds(40, 75, 350, 25);

        // Helper tetapan gaya butang
        Font buttonFont = new Font("Arial", Font.BOLD, 15);
        Color buttonGreen = new Color(0, 190, 98);

        // Button 1: My Profile
        btnMyProfile = new JButton("My Profile");
        btnMyProfile.setFont(buttonFont);
        btnMyProfile.setBackground(buttonGreen);
        btnMyProfile.setForeground(Color.WHITE);
        btnMyProfile.setFocusPainted(false);
        btnMyProfile.setBorderPainted(false);
        btnMyProfile.setOpaque(true);
        btnMyProfile.setBounds(110, 120, 235, 35);

        // =========================================================================
        // LOGIK POLIMORFIK: Menetapkan nama butang secara dinamik mengikut role
        // =========================================================================
        String buttonLabel = "Room Booking"; // Default untuk Student
        if (userRole != null && userRole.equalsIgnoreCase("LIBRARIAN")) {
            buttonLabel = "Room Management"; // Ditukar jika Librarian log masuk
        }

        btnActionService = new JButton(buttonLabel);
        btnActionService.setFont(buttonFont);
        btnActionService.setBackground(buttonGreen);
        btnActionService.setForeground(Color.WHITE);
        btnActionService.setFocusPainted(false);
        btnActionService.setBorderPainted(false);
        btnActionService.setOpaque(true);
        btnActionService.setBounds(110, 195, 235, 35);
        // =========================================================================

        // Button 3: Logout
        btnLogout = new JButton("Logout");
        btnLogout.setFont(buttonFont);
        btnLogout.setBackground(buttonGreen);
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setOpaque(true);
        btnLogout.setBounds(110, 270, 235, 35);

        // Memasukkan komponen ke dalam panel kad putih
        cardPanel.add(lblTitle);
        cardPanel.add(lblWelcome);
        cardPanel.add(btnMyProfile);
        cardPanel.add(btnActionService);
        cardPanel.add(btnLogout);

        mainPanel.add(cardPanel);
        add(mainPanel);

        // --- Action Listeners ---

        // Aksi Butang My Profile
        btnMyProfile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Opening My Profile...");
                dispose(); 
                
                // DIBAIKI DI SINI: Ditambah 'userRole' sebagai argumen kedua supaya sepadan dengan constructor UpdateProfileGUI
                new UpdateProfileGUI(loggedInUser, userRole).setVisible(true); 
            }
        });

        // Aksi Butang Dinamik (Library Service / Room Management)
        btnActionService.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (userRole != null && userRole.equalsIgnoreCase("LIBRARIAN")) {
                    JOptionPane.showMessageDialog(null, "Opening Librarian Room Management Module...");
                    dispose(); 
                    new view.LibrarianGUI().setVisible(true); 
                    
                } else {
                    // Ambil data sesi ID pengguna
                    String studentIdFromDatabase = loggedInUser; 
                    
                    // Sediakan pemboleh ubah untuk menyimpan data sebenar dari database
                    String realName = "STUDENT " + studentIdFromDatabase; // default jika gagal cari
                    String realPassword = "1234";
                    String realEmail = "student@mail.com";
                    String realPhone = "01X-XXXXXXX";
                    
                    // JALANKAN QUERY SQL UNTUK TARIK DATA SEBENAR DARI TABLE USERS
                    try {
                        // Pastikan menukar nama database 'smart_library' mengikut nama sebenar database awak
                        String url = "jdbc:mysql://localhost:3306/smart_library"; 
                        String dbUser = "root";
                        String dbPass = ""; // Kosongkan atau isi jika ada password
                        
                        java.sql.Connection conn = java.sql.DriverManager.getConnection(url, dbUser, dbPass);
                        String sql = "SELECT * FROM users WHERE user_id = ?";
                        java.sql.PreparedStatement pstmt = conn.prepareStatement(sql);
                        pstmt.setString(1, studentIdFromDatabase);
                        
                        java.sql.ResultSet rs = pstmt.executeQuery();
                        if (rs.next()) {
                            // Ekstrak data terus dari kolum database awak!
                            realName = rs.getString("name");        // Akan mengambil "Nurin"
                            realPassword = rs.getString("password");
                            realEmail = rs.getString("email");      // Akan mengambil "ABC"
                            realPhone = rs.getString("phone_no");   // Akan mengambil "0199"
                        }
                        
                        rs.close();
                        pstmt.close();
                        conn.close();
                    } catch (Exception ex) {
                        System.out.println("Database error semasa menarik profil: " + ex.getMessage());
                        // Jika gagal, ia akan menggunakan data default di atas supaya sistem tidak crash
                    }
                    
                    JOptionPane.showMessageDialog(null, "Opening Student Booking Dashboard...");
                    dispose();
                    
                    // Bina objek Student menggunakan data TULEN yang ditarik dari database
                    model.Student dbStudent = new model.Student(
                        realName, 
                        realPassword, 
                        realEmail, 
                        realPhone, 
                        studentIdFromDatabase
                    );
                    
                    // Hantar ke BookingUI
                    new view.BookingUI(dbStudent).start(); 
                }
            }
        });

        // Aksi Butang Logout
        btnLogout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(null, 
                        "Are you sure you want to logout?", "Logout", 
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    dispose();
                    new LoginGUI().setVisible(true); // Kembali ke skrin Login
                }
            }
        });
    }

    // Main method untuk tujuan pengujian terus ('Run File')
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DashboardGUI("ADMIN101", "LIBRARIAN").setVisible(true);
        });
    }
}