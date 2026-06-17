package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterAccount extends JFrame {

    // Diubah suai: txtMatric ditukar kepada txtUserID secara universal
    private JTextField txtName, txtEmail, txtPhone, txtUserID;
    private JPasswordField txtPassword, txtConfirmPassword;
    private JButton btnRegister, btnBack; 

    public RegisterAccount() {

        setTitle("Register Account");
        setSize(550, 500); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(230, 245, 245));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(null);
        formPanel.setBounds(80, 20, 360, 410); 
        formPanel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Register Account");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(new Color(0, 51, 102));
        lblTitle.setBounds(90, 20, 220, 30);

        // 1. INPUT NAMA
        JLabel lblName = new JLabel("Name");
        lblName.setBounds(30, 70, 100, 25);

        txtName = new JTextField();
        txtName.setBounds(130, 70, 200, 25);

        // 2. DIUBAH SUAI: INPUT USER ID (Menggantikan Matric No)
        JLabel lblUserID = new JLabel("User ID");
        lblUserID.setBounds(30, 110, 100, 25);
        
        txtUserID = new JTextField();
        txtUserID.setBounds(130, 110, 200, 25);

        // 3. INPUT EMAIL
        JLabel lblEmail = new JLabel("Email");
        lblEmail.setBounds(30, 150, 100, 25);

        txtEmail = new JTextField();
        txtEmail.setBounds(130, 150, 200, 25);

        // 4. INPUT PHONE NO
        JLabel lblPhone = new JLabel("Phone No");
        lblPhone.setBounds(30, 190, 100, 25);
        
        txtPhone = new JTextField();
        txtPhone.setBounds(130, 190, 200, 25);

        // 5. INPUT PASSWORD
        JLabel lblPassword = new JLabel("Password");
        lblPassword.setBounds(30, 230, 100, 25); 

        txtPassword = new JPasswordField();
        txtPassword.setBounds(130, 230, 200, 25);

        // 6. INPUT CONFIRM PASSWORD
        JLabel lblConfirmPassword = new JLabel("Confirm Password");
        lblConfirmPassword.setBounds(30, 270, 100, 25); 

        txtConfirmPassword = new JPasswordField();
        txtConfirmPassword.setBounds(130, 270, 200, 25);

        // BUTTON REGISTER
        btnRegister = new JButton("Register");
        btnRegister.setBounds(30, 330, 140, 35); 
        btnRegister.setBackground(new Color(0, 200, 100));
        btnRegister.setForeground(Color.WHITE);

        // BUTTON BACK TO LOGIN
        btnBack = new JButton("Back to Login");
        btnBack.setBounds(190, 330, 140, 35); 
        btnBack.setBackground(new Color(150, 150, 150));
        btnBack.setForeground(Color.WHITE);

        formPanel.add(lblTitle);
        formPanel.add(lblName);
        formPanel.add(txtName);
        formPanel.add(lblUserID); 
        formPanel.add(txtUserID); 
        formPanel.add(lblEmail);
        formPanel.add(txtEmail);
        formPanel.add(lblPhone);  
        formPanel.add(txtPhone);  
        formPanel.add(lblPassword);
        formPanel.add(txtPassword);
        formPanel.add(lblConfirmPassword);
        formPanel.add(txtConfirmPassword);
        formPanel.add(btnRegister);
        formPanel.add(btnBack); 

        panel.add(formPanel);
        add(panel);

        // --- Event untuk Butang Register ---
        btnRegister.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                String name = txtName.getText().trim();
                String userID = txtUserID.getText().trim(); // Diubah suai: Membaca sebagai userID
                String email = txtEmail.getText().trim();
                String phoneNo = txtPhone.getText().trim();    
                String password = String.valueOf(txtPassword.getPassword());
                String confirmPassword = String.valueOf(txtConfirmPassword.getPassword());

                if (name.isEmpty() || userID.isEmpty() || email.isEmpty() || 
                    phoneNo.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {

                    JOptionPane.showMessageDialog(null, "Please fill in all fields!", "Warning", JOptionPane.WARNING_MESSAGE);

                } else if (!password.equals(confirmPassword)) {

                    JOptionPane.showMessageDialog(null, "Passwords do not match!", "Warning", JOptionPane.WARNING_MESSAGE);

                } else {
                    
                    // DIUBAH SUAI: Sasaran simpanan ditukar kepada nama kolum 'user_id' yang sah
                    String sql = "INSERT INTO users (user_id, name, password, email, phone_no, role) VALUES (?, ?, ?, ?, ?, 'STUDENT')";
                    boolean success = false;

                    try (Connection conn = database.DatabaseConnection.getConnection();
                         PreparedStatement ps = conn.prepareStatement(sql)) {
                        
                        ps.setString(1, userID);
                        ps.setString(2, name);
                        ps.setString(3, password);
                        ps.setString(4, email);
                        ps.setString(5, phoneNo);
                        
                        int rowsInserted = ps.executeUpdate();
                        if (rowsInserted > 0) {
                            success = true;
                        }
                    } catch (SQLException ex) {
                        System.out.println("SQL Error During Registration: " + ex.getMessage());
                        ex.printStackTrace();
                    }

                    if (success) {
                        JOptionPane.showMessageDialog(null, "Registration Successful! Saved to Database.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                        new LoginGUI().setVisible(true); 
                    } else {
                        JOptionPane.showMessageDialog(null, 
                            "Registration Failed!\nThis User ID or Email has already been registered.", 
                            "Account Already Exists", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Event untuk Butang Back
        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose(); 
                new LoginGUI().setVisible(true); 
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new RegisterAccount().setVisible(true);
        });
    }
}