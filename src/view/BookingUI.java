/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import model.Room;
import controller.BookingController;
import model.Booking;
import model.Student;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BookingUI extends JFrame {
    private String sessionToken;
    private Student currentStudent;
    private BookingController controller;

    private JLabel welcomeLabel;
    private JTable availableRoomsTable;
    private DefaultTableModel roomsTableModel;
    
    private JTable myBookingsTable;
    private DefaultTableModel bookingsTableModel;
    
    private JTextField dateField;            
    private JComboBox<String> timeComboBox;  
    private JComboBox<String> roomTypeComboBox; 
    private JTextField roomIdField;
    private JTextField manageBookingIdField;
    private JLabel manageStatusLabel;

    // Kod baharu: Terima objek Student yang sah dari skrin Login!
    public BookingUI(Student loggedInStudent) {
        controller = new BookingController();
        
        // Pegang data student yang betul-betul datang dari database
        this.currentStudent = loggedInStudent;
        this.sessionToken = loggedInStudent.getStudentId();

        setTitle("Room Booking System - Dashboard");
        setSize(950, 580); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        add(createDashboardPanel());
    }

    public void start() {
        SwingUtilities.invokeLater(() -> {
            setVisible(true);
            refreshMyBookings();
        });
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(240, 240, 240));
        
        welcomeLabel = new JLabel("Logged in as: " + currentStudent.getStudentName() , SwingConstants.LEFT);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        JButton exitBtn = new JButton("Exit System");
        headerPanel.add(welcomeLabel, BorderLayout.CENTER);
        headerPanel.add(exitBtn, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Book a Room", createBookRoomPanel());
        tabbedPane.addTab("My Bookings", createViewBookingsPanel());
        tabbedPane.addTab("Manage Bookings", createManageBookingPanel());
        
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 1) {
                refreshMyBookings();
            }
        });

        panel.add(tabbedPane, BorderLayout.CENTER);
        exitBtn.addActionListener(e -> System.exit(0));

        return panel;
    }

    private JPanel createBookRoomPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6); 
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1: Date Input
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.0;
        inputPanel.add(new JLabel("Select Date (YYYY-MM-DD):"), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.5;
        String todayDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        dateField = new JTextField(todayDate, 10);
        inputPanel.add(dateField, gbc);

        // Row 1: Time Slot Input
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.0;
        inputPanel.add(new JLabel("Select Time Slot:"), gbc);

        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 0.5;
        String[] timeSlots = {
            "8.00-9.00", "9.00-10.00", "10.00-11.00", 
            "11.00-12.00", "12.00-13.00", "13.00-14.00", 
            "14.00-15.00", "15.00-16.00", "16.00-17.00"
        };
        timeComboBox = new JComboBox<>(timeSlots);
        inputPanel.add(timeComboBox, gbc);

        // Row 2: Room Type Input Filter
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0;
        inputPanel.add(new JLabel("Room Type:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.5;
        String[] roomTypes = {"All Types", "Standard Room", "Premium Room"};
        roomTypeComboBox = new JComboBox<>(roomTypes);
        inputPanel.add(roomTypeComboBox, gbc);

        // Row 2: Check Availability Button
        gbc.gridx = 3; gbc.gridy = 1; gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST; 
        JButton checkBtn = new JButton("Check Availability");
        inputPanel.add(checkBtn, gbc);

        String[] columnNames = {"Room ID", "Room Name", "Type", "Capacity", "Facilities"};
        roomsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        availableRoomsTable = new JTable(roomsTableModel);
        availableRoomsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        availableRoomsTable.setRowHeight(24);
        
        availableRoomsTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        availableRoomsTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        availableRoomsTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        availableRoomsTable.getColumnModel().getColumn(3).setPreferredWidth(60);
        availableRoomsTable.getColumnModel().getColumn(4).setPreferredWidth(300);
        
        availableRoomsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && availableRoomsTable.getSelectedRow() != -1) {
                roomIdField.setText(availableRoomsTable.getValueAt(availableRoomsTable.getSelectedRow(), 0).toString());
            }
        });

        JScrollPane scrollPane = new JScrollPane(availableRoomsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Available Rooms"));

        JPanel bookPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        bookPanel.add(new JLabel("Selected Room ID:"));
        roomIdField = new JTextField(10);
        bookPanel.add(roomIdField);
        JButton bookBtn = new JButton("Book Room");
        bookPanel.add(bookBtn);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bookPanel, BorderLayout.SOUTH);

        checkBtn.addActionListener(e -> checkRoomAvailability());
        bookBtn.addActionListener(e -> performBooking());

        return panel;
    }

    private JPanel createViewBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columnNames = {"Booking ID", "Room ID", "Booking Date", "Time Slot", "Status"};
        bookingsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        myBookingsTable = new JTable(bookingsTableModel);
        myBookingsTable.setRowHeight(24);
        JScrollPane scrollPane = new JScrollPane(myBookingsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("My Booking History"));

        JButton refreshBtn = new JButton("Refresh List");
        refreshBtn.addActionListener(e -> refreshMyBookings());

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(refreshBtn, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createManageBookingPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Booking ID:"), gbc);

        gbc.gridx = 1;
        manageBookingIdField = new JTextField(15);
        panel.add(manageBookingIdField, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton checkStatusBtn = new JButton("Check Status");
        JButton cancelBtn = new JButton("Cancel Booking");
        btnPanel.add(checkStatusBtn);
        btnPanel.add(cancelBtn);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        panel.add(btnPanel, gbc);

        manageStatusLabel = new JLabel("Status: Waiting for input...");
        manageStatusLabel.setForeground(Color.BLUE);
        gbc.gridy = 2;
        panel.add(manageStatusLabel, gbc);

        checkStatusBtn.addActionListener(e -> {
            String id = manageBookingIdField.getText().trim();
            if(id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a Booking ID.");
                return;
            }
            String status = controller.getBookingStatus(id);
            manageStatusLabel.setText("Status: " + status);
        });

        cancelBtn.addActionListener(e -> {
            String id = manageBookingIdField.getText().trim();
            if(id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a Booking ID.");
                return;
            }
            
            int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel Booking ID: " + id + "?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                if (controller.cancelBooking(id)) {
                    JOptionPane.showMessageDialog(this, "Booking " + id + " has been successfully cancelled.");
                    manageStatusLabel.setText("Status: CANCELLED");
                    refreshMyBookings(); 
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to cancel booking. Please make sure:\n1. The entered ID is correct.\n2. The original status is still 'CONFIRMED'.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return panel;
    }

    private String getFormattedDate() {
        return dateField.getText().trim();
    }

    private String getSelectedTime() {
        return (String) timeComboBox.getSelectedItem(); 
    }
    
    private String getSelectedRoomType() {
        return (String) roomTypeComboBox.getSelectedItem();
    }

    private void checkRoomAvailability() {
        String date = getFormattedDate();
        String time = getSelectedTime();
        String selectedType = getSelectedRoomType();

        if (date.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a date first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        roomsTableModel.setRowCount(0); 
        Room[] allRooms = controller.getRoomList(); 
        boolean found = false;
        
        if (allRooms == null || allRooms.length == 0) {
            JOptionPane.showMessageDialog(this, "No rooms found in the system database.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (Room r : allRooms) {
            if (controller.checkRoomAvailability(r.getRoomId(), date, time)) {
                String typeString = (r.getRoomType() != null) ? r.getRoomType().toUpperCase() : "";
                String nameString = (r.getRoomName() != null) ? r.getRoomName().toUpperCase() : "";
                
                // Flexible check: captures if type contains "PREMIUM"/"VIP" or if the name implies it
                boolean isPremium = typeString.contains("PREMIUM") || typeString.contains("VIP") || 
                                    nameString.contains("PREMIUM") || nameString.contains("VIP") || 
                                    nameString.contains("SUITE")   || nameString.contains("STUDIO");
                
                boolean matchesFilter = false;
                if (selectedType.equals("All Types")) {
                    matchesFilter = true;
                } else if (selectedType.equals("Standard Room") && !isPremium) {
                    matchesFilter = true;
                } else if (selectedType.equals("Premium Room") && isPremium) {
                    matchesFilter = true;
                }
                
                if (matchesFilter) {
                    String displayType = isPremium ? "Premium Room" : "Standard Room";
                    
                    roomsTableModel.addRow(new Object[]{ 
                        r.getRoomId(), 
                        r.getRoomName(), 
                        displayType, 
                        r.getCapacity(), 
                        r.getFacilities() 
                    });
                    found = true;
                }
            }
        }

        if (!found) {
            JOptionPane.showMessageDialog(this, "No vacant rooms match your current filters for this slot.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void performBooking() {
        String date = getFormattedDate();
        String time = getSelectedTime();
        String roomId = roomIdField.getText().trim();

        if (date.isEmpty() || roomId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please ensure both Date and Room ID fields are filled.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (controller.checkRoomAvailability(roomId, date, time)) {
                Booking b = controller.createBooking(currentStudent, roomId, date, time);
                if (b != null) {
                    JOptionPane.showMessageDialog(this, "Booking confirmed!\nYour Booking ID is: " + b.getFormattedBookingId(), "Success", JOptionPane.INFORMATION_MESSAGE);
                    roomIdField.setText("");
                    roomsTableModel.setRowCount(0); 
                    refreshMyBookings(); 
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to save booking. Please ensure Student ID 'CD25008' exists inside your database students table.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "The selected room is unavailable on this date or time slot.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "System Error: " + ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void refreshMyBookings() {
        List<Booking> myBookings = controller.getBookingsForStudent(currentStudent);
        bookingsTableModel.setRowCount(0);

        if (myBookings != null) {
            for (Booking b : myBookings) {
                bookingsTableModel.addRow(new Object[]{
                    b.getFormattedBookingId(), 
                    b.getRoomId(),
                    b.getBookingDate(), 
                    b.getTimeSlot(),    
                    b.getStatus()       
                });
            }
        }
    }

    public static void main(String[] args) {
        // Cipta objek student olok-olok HANYA jika fail ini di-run secara berasingan (Testing Mode)
        model.Student mockStudent = new model.Student("Test Student", "password", "test@student.edu", "0123", "CD25008");
        
        SwingUtilities.invokeLater(() -> {
            new BookingUI(mockStudent).start();
        });
    }
}