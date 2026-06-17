package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginGUI extends JFrame {

    private JTextField txtUserID;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnForgotPassword;
    private JButton btnRegister;

    public LoginGUI() {
        setTitle("Smart Library Room Booking System");
        setSize(550, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(240, 240, 240));

        JLabel lblTitle = new JLabel("Smart Library Room Booking System");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 15));
        lblTitle.setBounds(120, 50, 320, 30);

        JLabel lblUserID = new JLabel("User ID");
        lblUserID.setBounds(120, 140, 80, 25);

        txtUserID = new JTextField();
        txtUserID.setBounds(200, 140, 220, 25);

        JLabel lblPassword = new JLabel("Password");
        lblPassword.setBounds(120, 180, 80, 25);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(200, 180, 220, 25);

        btnLogin = new JButton("Login");
        btnLogin.setBounds(120, 240, 300, 35);

        btnForgotPassword = new JButton("Forgot Password");
        btnForgotPassword.setBounds(270, 290, 150, 30);

        btnRegister = new JButton("Register Account");
        btnRegister.setBounds(270, 335, 150, 30);

        panel.add(lblTitle);
        panel.add(lblUserID);
        panel.add(txtUserID);
        panel.add(lblPassword);
        panel.add(txtPassword);
        panel.add(btnLogin);
        panel.add(btnForgotPassword);
        panel.add(btnRegister);

        add(panel);

        // --- Event Butang Login ---
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String userID = txtUserID.getText().trim();
                String password = String.valueOf(txtPassword.getPassword());

                // BETULKAN DI SINI: Cipta objek Validator terlebih dahulu kerana method di dalamnya bukan static
                utility.Validator validator = new utility.Validator();
                if (!validator.validateLoginFields(userID, password)) {
                    JOptionPane.showMessageDialog(null, 
                            "Please fill in all fields correctly (Password min 4 chars)!", 
                            "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Logik Semakan ke MySQL Database (Menggunakan kolum universal user_id)
                String query = "SELECT name, role FROM users WHERE user_id = ? AND password = ?";
                
                try (Connection conn = database.DatabaseConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement(query)) {
                    
                    ps.setString(1, userID);
                    ps.setString(2, password);
                    
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            String name = rs.getString("name");
                            String role = rs.getString("role"); 
                            
                            JOptionPane.showMessageDialog(null, "Login Successful! Welcome " + name + " (" + role + ")");
                            
                            // Menghantar data sesi (userID dan role) ke DashboardGUI
                            SwingUtilities.invokeLater(() -> {
                                dispose(); 
                                new view.DashboardGUI(userID, role).setVisible(true);
                            });
                            
                        } else {
                            JOptionPane.showMessageDialog(null, "Invalid User ID or Password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // --- Event Butang Forgot Password ---
        btnForgotPassword.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> {
                    dispose();
                    new ForgotPasswordGUI().setVisible(true);
                });
            }
        });

        // --- Event Butang Register ---
        btnRegister.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> {
                    dispose(); 
                    new RegisterAccount().setVisible(true); 
                });
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginGUI().setVisible(true);
        });
    }
}