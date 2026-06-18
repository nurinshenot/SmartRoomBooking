package view;

import controller.RoomManagementController;
import model.Room;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LibrarianGUI extends JFrame {

    private RoomManagementController controller = new RoomManagementController();

    //Paparan
    private JTable mainTable;
    private DefaultTableModel tableModel;
    private JLabel lblTitle;
    
    // Sidebar
    private JButton btnRoomList, btnViewBooking, btnLogout;
    
    //  Form (Input) 
    private JTextField txtRoomName, txtCapacity, txtFacilities;
    private JComboBox<String> cmbRoomType;
    private JLabel lblAutoIdValue; // Untuk tunjuk ID yang bakal dijana automatik
    
    
    private JButton btnSaveRoom, btnClearForm, btnDeleteRoom;
    private JPanel formPanel; // Panel kontainer untuk borang

    public LibrarianGUI() {
        setTitle("Smart Library Room Management System");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initComponents();
        
       
        refreshRoomTableAndForm();
    }

    private void initComponents() {
     
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setBackground(new Color(33, 47, 61));
        sidebarPanel.setPreferredSize(new Dimension(180, 600));
        sidebarPanel.setLayout(null);

        btnRoomList = new JButton("Room List");
        btnRoomList.setBounds(20, 40, 140, 35);
        sidebarPanel.add(btnRoomList);

        btnViewBooking = new JButton("View Booking");
        btnViewBooking.setBounds(20, 90, 140, 35);
        sidebarPanel.add(btnViewBooking);

        btnLogout = new JButton("Logout");
        btnLogout.setBounds(20, 500, 140, 35);
        sidebarPanel.add(btnLogout);

        add(sidebarPanel, BorderLayout.WEST);

       
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(244, 246, 247));
        mainPanel.setLayout(null);

      
        lblTitle = new JLabel("Discussion Room Management");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(23, 32, 42));
        lblTitle.setBounds(25, 15, 400, 30);
        mainPanel.add(lblTitle);

        
        tableModel = new DefaultTableModel();
        mainTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(mainTable);
        scrollPane.setBounds(25, 60, 700, 220);
        mainPanel.add(scrollPane);

        
        formPanel = new JPanel();
        formPanel.setBorder(BorderFactory.createTitledBorder("Room Information Form (Add / Edit)"));
        formPanel.setBackground(new Color(255, 255, 255));
        formPanel.setLayout(null);
        formPanel.setBounds(25, 295, 700, 240);

        // Baris 1: Auto-Generated Room ID & Room Type
        JLabel lblRoomId = new JLabel("Generated ID:");
        lblRoomId.setBounds(30, 30, 100, 25);
        formPanel.add(lblRoomId);

        lblAutoIdValue = new JLabel("R01"); 
        lblAutoIdValue.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblAutoIdValue.setForeground(Color.RED);
        lblAutoIdValue.setBounds(130, 30, 100, 25);
        formPanel.add(lblAutoIdValue);

        // Baris 2: Room Name & Capacity
        JLabel lblRoomType = new JLabel("Room Type:");
        lblRoomType.setBounds(350, 30, 100, 25);
        formPanel.add(lblRoomType);

        cmbRoomType = new JComboBox<>(new String[]{"STANDARD", "PREMIUM"});
        cmbRoomType.setBounds(450, 30, 200, 25);
        formPanel.add(cmbRoomType);

        JLabel lblRoomName = new JLabel("Room Name:");
        lblRoomName.setBounds(30, 70, 100, 25);
        formPanel.add(lblRoomName);

        txtRoomName = new JTextField();
        txtRoomName.setBounds(130, 70, 200, 25);
        formPanel.add(txtRoomName);

        JLabel lblCapacity = new JLabel("Capacity:");
        lblCapacity.setBounds(350, 70, 100, 25);
        formPanel.add(lblCapacity);

        txtCapacity = new JTextField();
        txtCapacity.setBounds(450, 70, 200, 25);
        formPanel.add(txtCapacity);

        // Baris 3: Facilities
        JLabel lblFacilities = new JLabel("Facilities:");
        lblFacilities.setBounds(30, 110, 100, 25);
        formPanel.add(lblFacilities);

        txtFacilities = new JTextField();
        txtFacilities.setBounds(130, 110, 520, 25);
        formPanel.add(txtFacilities);

        // Baris 4: ButangForm
        btnSaveRoom = new JButton("Save / Update Room");
        btnSaveRoom.setBounds(130, 160, 160, 35);
        formPanel.add(btnSaveRoom);

        btnDeleteRoom = new JButton("Delete Selected");
        btnDeleteRoom.setBackground(new Color(236, 112, 99));
        btnDeleteRoom.setForeground(Color.WHITE);
        btnDeleteRoom.setBounds(310, 160, 140, 35);
        formPanel.add(btnDeleteRoom);

        btnClearForm = new JButton("Clear / Reset");
        btnClearForm.setBounds(470, 160, 130, 35);
        formPanel.add(btnClearForm);

        mainPanel.add(formPanel);
        add(mainPanel, BorderLayout.CENTER);

        //Action Listeners
        // 1. KLIK PADA JADUAL UNTUK UPDATE, DELETE DAN VIEW
        mainTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = mainTable.getSelectedRow();
                if (selectedRow != -1) {
                    // Isi form menggunakan data dari baris jadual yang dipilih
                    lblAutoIdValue.setText(mainTable.getValueAt(selectedRow, 0).toString());
                    txtRoomName.setText(mainTable.getValueAt(selectedRow, 1).toString());
                    cmbRoomType.setSelectedItem(mainTable.getValueAt(selectedRow, 2).toString());
                    txtCapacity.setText(mainTable.getValueAt(selectedRow, 3).toString());
                    txtFacilities.setText(mainTable.getValueAt(selectedRow, 4).toString());
                    
                    
                    lblAutoIdValue.setForeground(Color.BLUE);
                }
            }
        });

        // 2. BUTANG SAVE / UPDATE & ERROR HANDLING
        btnSaveRoom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = lblAutoIdValue.getText();
                String name = txtRoomName.getText().trim();
                String type = cmbRoomType.getSelectedItem().toString();
                String capStr = txtCapacity.getText().trim();
                String facilities = txtFacilities.getText().trim();

                // (Borang tidak boleh dibiarkan kosong)
                if (name.isEmpty() || capStr.isEmpty() || facilities.isEmpty()) {
                    JOptionPane.showMessageDialog(LibrarianGUI.this, 
                        "Error: All form fields (Room Name, Capacity, and Facilities) must be filled!", 
                        "Form Validation Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return; 
                }

                int capacity = 0;
                
                //  Exception Handling: Room Capacity use number only
                try {
                    capacity = Integer.parseInt(capStr);
                    
                    // Tambahan semakan: Pastikan kapasiti tidak negatif atau kosong
                    if (capacity <= 0) {
                        JOptionPane.showMessageDialog(LibrarianGUI.this, 
                            "Error: Capacity must be a positive number greater than 0!", 
                            "Invalid Value Error", 
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    // Blok ini menangkap ralat jika pengguna memasukkan huruf/simbol ke dalam kapasiti
                    JOptionPane.showMessageDialog(LibrarianGUI.this, 
                        "Error: Please enter a valid whole number for Capacity!", 
                        "Data Format Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return; // Sekat daripada sistem crash
                }

                // Semak sama ada ID ini sudah ada dalam DB untuk tentukan sama ada UPDATE atau INSERT
                boolean idExists = false;
                for (int i = 0; i < mainTable.getRowCount(); i++) {
                    if (mainTable.getValueAt(i, 0).toString().equals(id)) {
                        idExists = true;
                        break;
                    }
                }

                if (idExists) {
                    // MOD EDIT: Jalankan kemas kini
                    controller.updateRoomStatus(id, true);
                    JOptionPane.showMessageDialog(LibrarianGUI.this, "Room " + id + " updated successfully!");
                } else {
                    // MOD ADD NEW: Guna Factory Pattern di Controller
                    boolean success = controller.addRoom(type, id, name, capacity, facilities);
                    if (success) {
                        JOptionPane.showMessageDialog(LibrarianGUI.this, "New Room added successfully!");
                    } else {
                        JOptionPane.showMessageDialog(LibrarianGUI.this, "Failed to add room.");
                    }
                }
                refreshRoomTableAndForm();
            }
        });

        // 3. BUTANG DELETE (Telah Dikemaskini dengan Sekatan Business Rule)
        btnDeleteRoom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = mainTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(LibrarianGUI.this, "Please select a room from the table to delete!");
                    return;
                }
                String id = mainTable.getValueAt(selectedRow, 0).toString();
                int confirm = JOptionPane.showConfirmDialog(LibrarianGUI.this, "Delete Room " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean isDeleted = controller.deleteRoom(id);
                    
                    if (isDeleted) {
                        JOptionPane.showMessageDialog(LibrarianGUI.this, "Room " + id + " deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        refreshRoomTableAndForm();
                    } else {
                        JOptionPane.showMessageDialog(LibrarianGUI.this, 
                            "Cannot delete room! This room currently has active student bookings.", 
                            "System Restriction Error", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // 4. BUTANG CLEAR FORM
        btnClearForm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFormFields();
                lblAutoIdValue.setText(controller.generateNextRoomId());
                lblAutoIdValue.setForeground(Color.RED);
            }
        });

        // SIDEBAR NAVIGATION
        btnRoomList.addActionListener(e -> {
            lblTitle.setText("Discussion Room Management");
            formPanel.setVisible(true);
            refreshRoomTableAndForm();
        });

        btnViewBooking.addActionListener(e -> {
            lblTitle.setText("Current Student Bookings");
            formPanel.setVisible(false); 
            showBookingListTable();
        });

        btnLogout.addActionListener(e -> {
            dispose();
        });
    }

    private void refreshRoomTableAndForm() {
        tableModel.setColumnIdentifiers(new Object[]{"Room ID", "Room Name", "Room Type", "Capacity", "Facilities"});
        tableModel.setRowCount(0);
        
        for (Room room : controller.getAllRooms()) {
            tableModel.addRow(new Object[]{
                room.getRoomId(), room.getRoomName(), room.getRoomType(), room.getCapacity(), room.getFacilities()
            });
        }
        clearFormFields();
        
        lblAutoIdValue.setText(controller.generateNextRoomId());
        lblAutoIdValue.setForeground(Color.RED);
    }

    private void clearFormFields() {
        txtRoomName.setText("");
        txtCapacity.setText("");
        txtFacilities.setText("");
        cmbRoomType.setSelectedIndex(0);
        mainTable.clearSelection();
    }

    private void showBookingListTable() {
        tableModel.setColumnIdentifiers(new Object[]{"Booking ID", "Student ID", "Room ID", "Time Slot", "Status"});
        tableModel.setRowCount(0);
        
        java.util.List<Object[]> liveBookings = controller.getAllBookings();
        
        for (Object[] row : liveBookings) {
            tableModel.addRow(row);
        }
    }
}