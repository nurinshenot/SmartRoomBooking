package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ForgotPasswordGUI extends JFrame {

    // Diubah suai: txtMatricNo ditukar kepada txtUserID agar lebih universal
    private JTextField txtUserID;
    private JTextField txtEmail;
    private JButton btnRecover;
    private JButton btnBack;

    public ForgotPasswordGUI() {
        // Pengurusan Dimensi Window Standard
        setTitle("Smart Library Room Booking System - Forgot Password");
        setSize(550, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main Panel (Warna latar belakang cyan lembut yang konsisten)
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(new Color(240, 255, 255));

        // Container Kad Putih di tengah
        JPanel formPanel = new JPanel();
        formPanel.setLayout(null);
        formPanel.setBackground(Color.WHITE);
        formPanel.setBounds(65, 30, 400, 300);

        // Tajuk Halaman
        JLabel lblTitle = new JLabel("Reset Password", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(new Color(3, 43, 83));
        lblTitle.setBounds(20, 20, 360, 30);

        // Diubah suai: Label ditukar kepada User ID (Merangkumi Student & Librarian)
        JLabel lblUserID = new JLabel("User ID (Matric No / Staff ID)");
        lblUserID.setBounds(40, 80, 200, 25);
        txtUserID = new JTextField();
        txtUserID.setBounds(40, 105, 320, 25);

        // Label & Input: Email
        JLabel lblEmail = new JLabel("Registered Email");
        lblEmail.setBounds(40, 145, 150, 25);
        txtEmail = new JTextField();
        txtEmail.setBounds(40, 170, 320, 25);

        // Butang: Recover Password
        btnRecover = new JButton("Recover Password");
        btnRecover.setFont(new Font("Arial", Font.BOLD, 14));
        btnRecover.setBackground(new Color(0, 150, 136));
        btnRecover.setForeground(Color.WHITE);
        btnRecover.setBounds(40, 220, 150, 35);

        // Butang: Back to Login
        btnBack = new JButton("Back to Login");
        btnBack.setFont(new Font("Arial", Font.BOLD, 14));
        btnBack.setBackground(new Color(120, 130, 140));
        btnBack.setForeground(Color.WHITE);
        btnBack.setBounds(210, 220, 150, 35);

        // Memasukkan komponen ke dalam panel kad putih
        formPanel.add(lblTitle);
        formPanel.add(lblUserID);
        formPanel.add(txtUserID);
        formPanel.add(lblEmail);
        formPanel.add(txtEmail);
        formPanel.add(btnRecover);
        formPanel.add(btnBack);

        mainPanel.add(formPanel);
        add(mainPanel);

        // --- ACTION LISTENERS ---

        // Aksi Butang Recover Password
        btnRecover.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Diubah suai: Membaca teks dari txtUserID
                String userID = txtUserID.getText().trim();
                String email = txtEmail.getText().trim();

                // Validasi input kosong
                if (userID.isEmpty() || email.isEmpty()) {
                    JOptionPane.showMessageDialog(null, 
                            "Please fill in both User ID and Email!", 
                            "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // DIUBAH SUAI: Query SQL kini mencari berdasarkan kolum 'user_id' yang sah
                String query = "SELECT password FROM users WHERE user_id = ? AND email = ?";
                
                try (Connection conn = database.DatabaseConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement(query)) {
                    
                    ps.setString(1, userID);
                    ps.setString(2, email);
                    
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            // Ambil password asal dari database jika padanan wujud
                            String currentPassword = rs.getString("password");
                            
                            JOptionPane.showMessageDialog(null, 
                                    "Account Verified!\nYour Password is: " + currentPassword, 
                                    "Password Recovery", JOptionPane.INFORMATION_MESSAGE);
                            
                            // Kembali ke skrin Log Masuk
                            SwingUtilities.invokeLater(() -> {
                                dispose();
                                new LoginGUI().setVisible(true);
                            });
                            
                        } else {
                            JOptionPane.showMessageDialog(null, 
                                    "No account found with this User ID and Email combination.", 
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Aksi Butang Patah Balik ke LoginGUI
        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> {
                    dispose();
                    new LoginGUI().setVisible(true);
                });
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ForgotPasswordGUI().setVisible(true);
        });
    }
}