package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdateProfileGUI extends JFrame {

    private JTextField txtUserID;
    private JTextField txtName;
    private JTextField txtEmail;
    private JTextField txtPhoneNumber;
    private JButton btnUpdateProfile;
    private JButton btnBack; 
    private String currentUserID;   // Menyimpan data sesi ID pengguna
    private String currentUserRole; // Menyimpan data sesi peranan pengguna

    // Constructor menerima userID dan role untuk mengekalkan keaktifan sesi
    public UpdateProfileGUI(String userID, String role) {
        this.currentUserID = userID;
        this.currentUserRole = role;

        setTitle("Smart Library Room Booking System - My Profile");
        setSize(550, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(new Color(240, 255, 255)); 

        JPanel formPanel = new JPanel();
        formPanel.setLayout(null);
        formPanel.setBackground(Color.WHITE);
        formPanel.setBounds(65, 25, 400, 360); 

        JLabel lblTitle = new JLabel("My Profile", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(new Color(3, 43, 83));
        lblTitle.setBounds(20, 15, 360, 30);

        // 1. User ID (Read-Only / Tidak boleh diedit)
        JLabel lblUserID = new JLabel("User ID");
        lblUserID.setBounds(40, 65, 100, 25);
        txtUserID = new JTextField(currentUserID); 
        txtUserID.setBounds(150, 65, 200, 25);
        txtUserID.setEditable(false); 
        txtUserID.setBackground(new Color(245, 245, 245)); 

        // 2. Name
        JLabel lblName = new JLabel("Name");
        lblName.setBounds(40, 105, 100, 25);
        txtName = new JTextField();
        txtName.setBounds(150, 105, 200, 25);

        // 3. Email
        JLabel lblEmail = new JLabel("Email");
        lblEmail.setBounds(40, 145, 100, 25);
        txtEmail = new JTextField();
        txtEmail.setBounds(150, 145, 200, 25);

        // 4. Phone Number
        JLabel lblPhone = new JLabel("Phone Number");
        lblPhone.setBounds(40, 185, 100, 25);
        txtPhoneNumber = new JTextField();
        txtPhoneNumber.setBounds(150, 185, 200, 25);

        // 5. Butang: Update Profile
        btnUpdateProfile = new JButton("Update Profile");
        btnUpdateProfile.setFont(new Font("Arial", Font.BOLD, 14));
        btnUpdateProfile.setBackground(new Color(0, 190, 98));
        btnUpdateProfile.setForeground(Color.WHITE);
        btnUpdateProfile.setFocusPainted(false);
        btnUpdateProfile.setBorderPainted(false);
        btnUpdateProfile.setOpaque(true);
        btnUpdateProfile.setBounds(40, 240, 310, 35);

        // 6. Butang: Back to Dashboard
        btnBack = new JButton("Back to Dashboard");
        btnBack.setFont(new Font("Arial", Font.BOLD, 14));
        btnBack.setBackground(new Color(120, 130, 140));
        btnBack.setForeground(Color.WHITE);
        btnBack.setFocusPainted(false);
        btnBack.setBorderPainted(false);
        btnBack.setOpaque(true);
        btnBack.setBounds(40, 290, 310, 35); 

        formPanel.add(lblTitle);
        formPanel.add(lblUserID);
        formPanel.add(txtUserID);
        formPanel.add(lblName);
        formPanel.add(txtName);
        formPanel.add(lblEmail);
        formPanel.add(txtEmail);
        formPanel.add(lblPhone);
        formPanel.add(txtPhoneNumber);
        formPanel.add(btnUpdateProfile);
        formPanel.add(btnBack);

        mainPanel.add(formPanel);
        add(mainPanel);

        // --- Ambil Data Asal dari Database Auto-Load ---
        loadUserProfile();

        // --- Pengendali Acara / Action Listeners ---

        // Aksi Simpan Kemas Kini Profil
        btnUpdateProfile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = txtName.getText().trim();
                String email = txtEmail.getText().trim();
                String phone = txtPhoneNumber.getText().trim();

                // Cipta objek Validator terlebih dahulu (Menghindari ralat panggilan non-static)
                utility.Validator validator = new utility.Validator();
                if (!validator.validateInput(name) || !validator.validateInput(email) || !validator.validateInput(phone)) {
                    JOptionPane.showMessageDialog(null, 
                            "Please fill in all information correctly!", 
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Query UPDATE menggunakan kolum pangkalan data yang baharu (user_id)
                String updateSQL = "UPDATE users SET name = ?, email = ?, phone_no = ? WHERE user_id = ?";
                try (Connection conn = database.DatabaseConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement(updateSQL)) {
                    
                    ps.setString(1, name);
                    ps.setString(2, email);
                    ps.setString(3, phone);
                    ps.setString(4, currentUserID);
                    
                    int rowsUpdated = ps.executeUpdate();
                    if (rowsUpdated > 0) {
                        JOptionPane.showMessageDialog(null, "Profile Updated Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Aksi Patah Balik ke Halaman Dashboard
        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose(); 
                // Membawa semula parameter ID dan peranan kembali ke Dashboard agar sesi tidak hilang
                new DashboardGUI(currentUserID, currentUserRole).setVisible(true); 
            }
        });
    }

    // --- LENGKAPAN METHOD: Menarik maklumat profil dari MySQL secara automatik ---
    private void loadUserProfile() {
        String query = "SELECT name, email, phone_no FROM users WHERE user_id = ?";
        
        try (Connection conn = database.DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, currentUserID);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Isi semula ruangan teks input dengan data asal dari database
                    txtName.setText(rs.getString("name"));
                    txtEmail.setText(rs.getString("email"));
                    txtPhoneNumber.setText(rs.getString("phone_no"));
                } else {
                    JOptionPane.showMessageDialog(null, "User profile data not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to load profile: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Main method dengan data mockup untuk pengujian terus ('Run File')
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new UpdateProfileGUI("B032110001", "STUDENT").setVisible(true);
        });
    }
}