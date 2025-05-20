import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ManageStaffPage extends JFrame {
    private DefaultTableModel tableModel;
    private JTable staffTable;
    private JTextField idField, nameField, phoneField, locationField;

    public ManageStaffPage(User currentUser) {
        ImageIcon logoIcon = new ImageIcon("/Img/logo_pool.png");
        this.setIconImage(logoIcon.getImage());

        this.setTitle("Quản lý Nhân viên - CLB Billiards");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(screenSize.width, screenSize.height);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Panel chính với nền trắng
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Thanh tiêu đề
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBackground(new Color(33, 150, 243)); // Xanh dương
        toolBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("QUẢN LÝ NHÂN VIÊN");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        toolBar.add(titleLabel);

        toolBar.add(Box.createHorizontalGlue());

        JButton backButton = new JButton("Quay lại");
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backButton.setBackground(new Color(244, 67, 54)); // Đỏ
        backButton.setForeground(Color.BLACK);
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        backButton.addActionListener(e -> {
            new AdminPage(currentUser);
            setVisible(false);
        });
        backButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                backButton.setBackground(new Color(211, 47, 47));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                backButton.setBackground(new Color(244, 67, 54));
            }
        });
        toolBar.add(backButton);

        mainPanel.add(toolBar, BorderLayout.NORTH);

        // Bảng nhân viên
        String[] columnNames = {"ID", "Tên nhân viên", "Số điện thoại", "Địa chỉ"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho chỉnh sửa trực tiếp trên bảng
            }
        };
        staffTable = new JTable(tableModel);
        staffTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        staffTable.setRowHeight(25);
        staffTable.setForeground(Color.BLACK);
        staffTable.setGridColor(new Color(200, 200, 200));
        staffTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        staffTable.getTableHeader().setForeground(Color.BLACK);
        staffTable.getTableHeader().setBackground(new Color(240, 240, 240));

        // Thêm sự kiện click để hiển thị thông tin lên các trường nhập liệu
        staffTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = staffTable.getSelectedRow();
                if (selectedRow != -1) {
                    idField.setText(tableModel.getValueAt(selectedRow, 0).toString());
                    nameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    phoneField.setText(tableModel.getValueAt(selectedRow, 2).toString());
                    locationField.setText(tableModel.getValueAt(selectedRow, 3).toString());
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(staffTable);
        scrollPane.setPreferredSize(new Dimension(750, 300));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Form nhập liệu
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)), 
                "Thông tin nhân viên", 0, 0, new Font("Segoe UI", Font.BOLD, 16), Color.BLACK),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        inputPanel.add(createLabel("ID:"));
        idField = createTextField();
        inputPanel.add(idField);

        inputPanel.add(createLabel("Tên nhân viên:"));
        nameField = createTextField();
        inputPanel.add(nameField);

        inputPanel.add(createLabel("Số điện thoại:"));
        phoneField = createTextField();
        inputPanel.add(phoneField);

        inputPanel.add(createLabel("Địa chỉ:"));
        locationField = createTextField();
        inputPanel.add(locationField);

        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        // Panel nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton addButton = createButton("Thêm nhân viên", e -> addStaff());
        JButton editButton = createButton("Sửa nhân viên", e -> editStaff());
        JButton removeButton = createButton("Xóa nhân viên", e -> removeStaff());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(removeButton);

        mainPanel.add(buttonPanel, BorderLayout.EAST);

        this.add(mainPanel);
        loadStaffData();
        this.setVisible(true);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(Color.BLACK);
        return label;
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField(20);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return textField;
    }

    private JButton createButton(String text, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(new Color(33, 150, 243)); // Xanh dương
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.addActionListener(action);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(25, 118, 210));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(33, 150, 243));
            }
        });
        return button;
    }

    private void addStaff() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String location = locationField.getText().trim();

        if (name.isEmpty() || phone.isEmpty() || location.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO staff (id, name, phone, location) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, id);
            stmt.setString(2, name);
            stmt.setString(3, phone);
            stmt.setString(4, location);
            stmt.executeUpdate();

            tableModel.addRow(new Object[]{id, name, phone, location});
            clearInputFields();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi khi thêm nhân viên.");
        }
    }

    private void editStaff() {
        int selectedRow = staffTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một nhân viên để sửa.");
            return;
        }

        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String location = locationField.getText().trim();

        if (name.isEmpty() || phone.isEmpty() || location.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE staff SET name = ?, phone = ?, location = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, phone);
            stmt.setString(3, location);
            stmt.setString(4, id);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                tableModel.setValueAt(name, selectedRow, 1);
                tableModel.setValueAt(phone, selectedRow, 2);
                tableModel.setValueAt(location, selectedRow, 3);
                JOptionPane.showMessageDialog(this, "Thông tin của nhân viên " + name + " đã được cập nhật thành công");
                clearInputFields();
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy nhân viên " + name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi khi sửa nhân viên.");
        }
    }

    private void removeStaff() {
        int selectedRow = staffTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một nhân viên để xóa.");
            return;
        }

        String id = tableModel.getValueAt(selectedRow, 0).toString();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "DELETE FROM staff WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, id);
            stmt.executeUpdate();

            tableModel.removeRow(selectedRow);
            clearInputFields();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi khi xóa nhân viên.");
        }
    }

    private void clearInputFields() {
        idField.setText("");
        nameField.setText("");
        phoneField.setText("");
        locationField.setText("");
    }

    private void loadStaffData() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM staff";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                String location = rs.getString("location");
                tableModel.addRow(new Object[]{id, name, phone, location});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi khi tải dữ liệu nhân viên.");
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        User adminUser = new User("admin", "admin123", true, false);
        new ManageStaffPage(adminUser);
    }
}