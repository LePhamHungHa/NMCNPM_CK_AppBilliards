import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MenuManager extends JFrame {
    private DefaultTableModel foodTableModel, drinkTableModel;
    private JTable foodTable, drinkTable;
    private JTextField nameField, priceField, quantityField;
    private JComboBox<String> typeComboBox;

    public MenuManager(User currentUser) {
        ImageIcon logoIcon = new ImageIcon("/Img/logo_pool.png");
        this.setIconImage(logoIcon.getImage());

        this.setTitle("Quản lý Thực Đơn - CLB Billiards");
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

        JLabel titleLabel = new JLabel("QUẢN LÝ THỰC ĐƠN");
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

        // Panel chứa hai bảng
        JPanel tablePanel = new JPanel(new GridLayout(2, 1, 10, 10));
        tablePanel.setBackground(Color.WHITE);

        // Bảng đồ ăn
        String[] columnNames = {"Loại", "Tên món", "Giá", "Số lượng trong kho"};
        foodTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        foodTable = new JTable(foodTableModel);
        foodTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        foodTable.setRowHeight(25);
        foodTable.setForeground(Color.BLACK);
        foodTable.setGridColor(new Color(200, 200, 200));
        foodTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        foodTable.getTableHeader().setForeground(Color.BLACK);
        foodTable.getTableHeader().setBackground(new Color(240, 240, 240));

        foodTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = foodTable.getSelectedRow();
                if (selectedRow != -1) {
                    typeComboBox.setSelectedItem(foodTableModel.getValueAt(selectedRow, 0));
                    nameField.setText(foodTableModel.getValueAt(selectedRow, 1).toString());
                    priceField.setText(foodTableModel.getValueAt(selectedRow, 2).toString());
                    quantityField.setText(foodTableModel.getValueAt(selectedRow, 3).toString());
                    drinkTable.clearSelection(); // Xóa lựa chọn ở bảng nước
                }
            }
        });

        JScrollPane foodScrollPane = new JScrollPane(foodTable);
        foodScrollPane.setBorder(BorderFactory.createTitledBorder("Đồ Ăn"));
        tablePanel.add(foodScrollPane);

        // Bảng nước
        drinkTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        drinkTable = new JTable(drinkTableModel);
        drinkTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        drinkTable.setRowHeight(25);
        drinkTable.setForeground(Color.BLACK);
        drinkTable.setGridColor(new Color(200, 200, 200));
        drinkTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        drinkTable.getTableHeader().setForeground(Color.BLACK);
        drinkTable.getTableHeader().setBackground(new Color(240, 240, 240));

        drinkTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = drinkTable.getSelectedRow();
                if (selectedRow != -1) {
                    typeComboBox.setSelectedItem(drinkTableModel.getValueAt(selectedRow, 0));
                    nameField.setText(drinkTableModel.getValueAt(selectedRow, 1).toString());
                    priceField.setText(drinkTableModel.getValueAt(selectedRow, 2).toString());
                    quantityField.setText(drinkTableModel.getValueAt(selectedRow, 3).toString());
                    foodTable.clearSelection(); // Xóa lựa chọn ở bảng đồ ăn
                }
            }
        });

        JScrollPane drinkScrollPane = new JScrollPane(drinkTable);
        drinkScrollPane.setBorder(BorderFactory.createTitledBorder("Nước"));
        tablePanel.add(drinkScrollPane);

        mainPanel.add(tablePanel, BorderLayout.CENTER);

        // Form nhập liệu
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)), 
                "Thông tin món ăn", 0, 0, new Font("Segoe UI", Font.BOLD, 16), Color.BLACK),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        inputPanel.add(createLabel("Loại:"));
        typeComboBox = new JComboBox<>(new String[]{"Đồ Ăn", "Nước"});
        typeComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        inputPanel.add(typeComboBox);

        inputPanel.add(createLabel("Tên món:"));
        nameField = createTextField();
        inputPanel.add(nameField);

        inputPanel.add(createLabel("Giá:"));
        priceField = createTextField();
        inputPanel.add(priceField);

        inputPanel.add(createLabel("Số lượng trong kho:"));
        quantityField = createTextField();
        inputPanel.add(quantityField);

        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        // Panel nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton addButton = createButton("Thêm món", e -> addMenuItem());
        JButton updateButton = createButton("Sửa món", e -> updateMenuItem());
        JButton removeButton = createButton("Xóa món", e -> removeMenuItem());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(removeButton);

        mainPanel.add(buttonPanel, BorderLayout.EAST);

        this.add(mainPanel);
        loadMenuData();
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

    private void addMenuItem() {
        String name = nameField.getText().trim();
        String type = (String) typeComboBox.getSelectedItem();
        String priceStr = priceField.getText().trim();
        String quantityStr = quantityField.getText().trim();

        if (name.isEmpty() || priceStr.isEmpty() || quantityStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        double price;
        int quantity;
        try {
            price = Double.parseDouble(priceStr);
            quantity = Integer.parseInt(quantityStr);
            if (quantity < 0) {
                JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn hoặc bằng 0");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá và số lượng phải là số hợp lệ!");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO menu (name, type, price, quantity) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, type);
            stmt.setDouble(3, price);
            stmt.setInt(4, quantity);
            stmt.executeUpdate();

            if (type.equals("Đồ Ăn")) {
                foodTableModel.addRow(new Object[]{type, name, price, quantity});
            } else {
                drinkTableModel.addRow(new Object[]{type, name, price, quantity});
            }
            clearInputFields();
            JOptionPane.showMessageDialog(this, "Thêm món ăn thành công!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi khi thêm món ăn.");
        }
    }

    private void updateMenuItem() {
        int foodSelectedRow = foodTable.getSelectedRow();
        int drinkSelectedRow = drinkTable.getSelectedRow();
        if (foodSelectedRow == -1 && drinkSelectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một món ăn để sửa.");
            return;
        }

        String oldName = foodSelectedRow != -1 ? foodTableModel.getValueAt(foodSelectedRow, 1).toString() : 
                         drinkTableModel.getValueAt(drinkSelectedRow, 1).toString();
        String name = nameField.getText().trim();
        String type = (String) typeComboBox.getSelectedItem();
        String priceStr = priceField.getText().trim();
        String quantityStr = quantityField.getText().trim();

        if (name.isEmpty() || priceStr.isEmpty() || quantityStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        double price;
        int quantity;
        try {
            price = Double.parseDouble(priceStr);
            quantity = Integer.parseInt(quantityStr);
            if (quantity < 0) {
                JOptionPane.showMessageDialog(this, "Số lượng không thể âm!");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá và số lượng phải là số hợp lệ!");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "UPDATE menu SET name = ?, type = ?, price = ?, quantity = ? WHERE name = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, type);
            stmt.setDouble(3, price);
            stmt.setInt(4, quantity);
            stmt.setString(5, oldName);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                if (foodSelectedRow != -1) {
                    foodTableModel.setValueAt(type, foodSelectedRow, 0);
                    foodTableModel.setValueAt(name, foodSelectedRow, 1);
                    foodTableModel.setValueAt(price, foodSelectedRow, 2);
                    foodTableModel.setValueAt(quantity, foodSelectedRow, 3);
                    if (!type.equals("Đồ Ăn")) {
                        foodTableModel.removeRow(foodSelectedRow);
                        drinkTableModel.addRow(new Object[]{type, name, price, quantity});
                    }
                } else {
                    drinkTableModel.setValueAt(type, drinkSelectedRow, 0);
                    drinkTableModel.setValueAt(name, drinkSelectedRow, 1);
                    drinkTableModel.setValueAt(price, drinkSelectedRow, 2);
                    drinkTableModel.setValueAt(quantity, drinkSelectedRow, 3);
                    if (!type.equals("Nước")) {
                        drinkTableModel.removeRow(drinkSelectedRow);
                        foodTableModel.addRow(new Object[]{type, name, price, quantity});
                    }
                }
                clearInputFields();
                JOptionPane.showMessageDialog(this, "Đã cập nhật món ăn thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy món ăn để cập nhật!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi khi sửa món ăn.");
        }
    }

    private void removeMenuItem() {
        int foodSelectedRow = foodTable.getSelectedRow();
        int drinkSelectedRow = drinkTable.getSelectedRow();
        if (foodSelectedRow == -1 && drinkSelectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một món ăn để xóa.");
            return;
        }

        String name = foodSelectedRow != -1 ? foodTableModel.getValueAt(foodSelectedRow, 1).toString() : 
                      drinkTableModel.getValueAt(drinkSelectedRow, 1).toString();

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "DELETE FROM menu WHERE name = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, name);
            stmt.executeUpdate();

            if (foodSelectedRow != -1) {
                foodTableModel.removeRow(foodSelectedRow);
            } else {
                drinkTableModel.removeRow(drinkSelectedRow);
            }
            clearInputFields();
            JOptionPane.showMessageDialog(this, "Xóa món ăn thành công!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi khi xóa món ăn.");
        }
    }

    private void loadMenuData() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM menu";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            foodTableModel.setRowCount(0);
            drinkTableModel.setRowCount(0);

            while (rs.next()) {
                String name = rs.getString("name");
                String type = rs.getString("type");
                double price = rs.getDouble("price");
                int quantity = rs.getInt("quantity");

                if (type.equals("Đồ Ăn")) {
                    foodTableModel.addRow(new Object[]{type, name, price, quantity});
                } else {
                    drinkTableModel.addRow(new Object[]{type, name, price, quantity});
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Quá trình tải dữ liệu từ MySQL thất bại.");
        }
    }

    private void clearInputFields() {
        nameField.setText("");
        priceField.setText("");
        quantityField.setText("");
        typeComboBox.setSelectedIndex(0);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        User adminUser = new User("admin", "admin123", true, false);
        new MenuManager(adminUser);
    }
}