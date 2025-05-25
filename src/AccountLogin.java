import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;

public class AccountLogin extends JFrame {
    private User currentUser;
    private DefaultTableModel tableModel;
    private JTable customerTable;
    private JTextField idField, usernameField, passwordField;
    private JCheckBox adminCheckBox;

    
    public AccountLogin(User currentUser) {
        this.currentUser = currentUser;
        ImageIcon logoIcon = new ImageIcon("/Img/Logo_pool.png");
        this.setIconImage(logoIcon.getImage());

        this.setTitle("Quản lý Tài khoản Đăng nhập - CLB Billiards");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(screenSize.width, screenSize.height);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Tạo panel chính với nền trắng và bố cục BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Tạo thanh công cụ với tiêu đề và nút Quay lại
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBackground(new Color(33, 150, 243)); // Màu xanh dương
        toolBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("QUẢN LÝ TÀI KHOẢN ĐĂNG NHẬP");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        toolBar.add(titleLabel);

        toolBar.add(Box.createHorizontalGlue());

        JButton backButton = new JButton("Quay lại");
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backButton.setBackground(new Color(244, 67, 54)); // Màu đỏ
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

        // Tạo bảng hiển thị thông tin tài khoản
        String[] columnNames = {"ID", "Tài khoản", "Mật khẩu (Hash)", "Quyền Admin"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép chỉnh sửa trực tiếp trên bảng
            }
        };
        customerTable = new JTable(tableModel);
        customerTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        customerTable.setRowHeight(25);
        customerTable.setForeground(Color.BLACK);
        customerTable.setGridColor(new Color(200, 200, 200));
        customerTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        customerTable.getTableHeader().setForeground(Color.BLACK);
        customerTable.getTableHeader().setBackground(new Color(240, 240, 240));

        // Thêm sự kiện click để hiển thị thông tin tài khoản vào các trường nhập liệu
        customerTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = customerTable.getSelectedRow();
                if (selectedRow != -1) {
                    idField.setText(tableModel.getValueAt(selectedRow, 0).toString());
                    usernameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    passwordField.setText(tableModel.getValueAt(selectedRow, 2).toString());
                    adminCheckBox.setSelected((Boolean) tableModel.getValueAt(selectedRow, 3));
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(customerTable);
        scrollPane.setPreferredSize(new Dimension(750, 300));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Tạo form nhập liệu
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)), 
                "Thông tin tài khoản", 0, 0, new Font("Segoe UI", Font.BOLD, 16), Color.BLACK),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        inputPanel.add(createLabel("ID:"));
        idField = createTextField();
        inputPanel.add(idField);

        inputPanel.add(createLabel("Tài khoản:"));
        usernameField = createTextField();
        inputPanel.add(usernameField);

        inputPanel.add(createLabel("Mật khẩu:"));
        passwordField = createTextField();
        inputPanel.add(passwordField);

        inputPanel.add(createLabel("Quyền Admin:"));
        adminCheckBox = new JCheckBox();
        adminCheckBox.setBackground(Color.WHITE);
        inputPanel.add(adminCheckBox);

        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        // Tạo panel chứa các nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton addButton = createButton("Thêm tài khoản", e -> openStaffSelectionDialog());
        JButton removeButton = createButton("Xóa tài khoản", e -> removeAccount());
        JButton contractButton = createButton("Tạo hợp đồng", e -> openContractCreationDialog());

        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(contractButton);

        mainPanel.add(buttonPanel, BorderLayout.EAST);

        this.add(mainPanel);
        loadCustomerData(); // Tải dữ liệu tài khoản từ database 
        this.setVisible(true);
    }

    //  form nhập liệu
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

    // tạo nút có hover
    private JButton createButton(String text, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(new Color(33, 150, 243)); // Màu xanh dương
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.addActionListener(action);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(25, 118, 210)); // Màu xanh đậm hơn khi hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(33, 150, 243)); // Trở lại màu gốc
            }
        });
        return button;
    }

    // Mở cửa sổ chọn nhân viên để thêm tài khoản
    private void openStaffSelectionDialog() {
       
        JDialog staffDialog = new JDialog(this, "Chọn nhân viên", true);
        staffDialog.setSize(1000, 800);
        staffDialog.setLocationRelativeTo(this);

        // Tạo bảng hiển thị ID và tên nhân viên
        DefaultTableModel staffTableModel = new DefaultTableModel(new String[]{"ID", "Tên Nhân Viên"}, 0);
        JTable staffTable = new JTable(staffTableModel);
        JScrollPane scrollPane = new JScrollPane(staffTable);
        staffDialog.add(scrollPane, BorderLayout.CENTER);

        // Tải dữ liệu nhân viên từ cơ sở dữ liệu
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT id, name FROM staff";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String staffId = rs.getString("id");
                String staffName = rs.getString("name");
                staffTableModel.addRow(new Object[]{staffId, staffName});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu nhân viên.");
        }

        // Thêm sự kiện chọn nhân viên từ bảng
        staffTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = staffTable.getSelectedRow();
                if (selectedRow != -1) {
                    String selectedId = (String) staffTable.getValueAt(selectedRow, 0);
                    String selectedName = (String) staffTable.getValueAt(selectedRow, 1);

                    // Kiểm tra xem tài khoản đã tồn tại chưa
                    if (isAccountExists(selectedId)) {
                        JOptionPane.showMessageDialog(this, "Tài khoản cho nhân viên " + selectedName + " đã tồn tại!");
                    } else {
                        setStaffInfo(selectedId); // Đặt ID nhân viên vào trường nhập liệu
                        staffDialog.dispose(); 
                        addAccount();
                    }
                }
            }
        });

        staffDialog.setVisible(true);
    }

    // Mở cửa sổ chọn nhân viên để tạo hợp đồng
    private void openContractCreationDialog() {
        JDialog staffDialog = new JDialog(this, "Chọn nhân viên", true);
        staffDialog.setSize(1000, 800);
        staffDialog.setLocationRelativeTo(this);

        // Tạo bảng hiển thị ID và tên nhân viên
        DefaultTableModel staffTableModel = new DefaultTableModel(new String[]{"ID", "Tên Nhân Viên"}, 0);
        JTable staffTable = new JTable(staffTableModel);
        JScrollPane scrollPane = new JScrollPane(staffTable);
        staffDialog.add(scrollPane, BorderLayout.CENTER);

        // Tải dữ liệu nhân viên từ cơ sở dữ liệu
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT id, name FROM staff";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String staffId = rs.getString("id");
                String staffName = rs.getString("name");
                staffTableModel.addRow(new Object[]{staffId, staffName});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu nhân viên.");
        }

        // Thêm sự kiện chọn nhân viên để tạo hợp đồng
        staffTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = staffTable.getSelectedRow();
                if (selectedRow != -1) {
                    String selectedId = (String) staffTable.getValueAt(selectedRow, 0);
                    String selectedName = (String) staffTable.getValueAt(selectedRow, 1);
                    createContractForStaff(selectedId, selectedName); // Tạo hợp đồng PDF
                    staffDialog.dispose(); // Đóng dialog
                }
            }
        });

        staffDialog.setVisible(true);
    }

    // Tạo hợp đồng PDF cho nhân viên được chọn
    private void createContractForStaff(String staffId, String staffName) {
       
        String contractDetails = "Hợp đồng cho nhân viên " + staffName + " (ID: " + staffId + ")\n\n"
                + "Nội dung hợp đồng:\n"
                + "1. Điều khoản 1\n"
                + "2. Điều khoản 2\n"
                + "3. Điều khoản 3";

        
        String directoryPath = "D:/ATBMHTTT/Contract";
        String fileName = "Contract_" + staffId + "_" + staffName + ".pdf";
        String filePath = directoryPath + "/" + fileName;

        // Tạo thư mục nếu chưa tồn tại
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        
        try {
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            String fontPath = "/fonts/DejaVuSans.ttf";
            BaseFont baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            com.itextpdf.text.Font font = new com.itextpdf.text.Font(baseFont, 12);

            Paragraph paragraph = new Paragraph(contractDetails, font);
            document.add(paragraph);

            document.close();
            JOptionPane.showMessageDialog(this, "Hợp đồng đã được tạo thành công tại: " + filePath);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi khi tạo hợp đồng PDF.");
        }
    }

    // Đặt ID nhân viên vào trường nhập liệu
    private void setStaffInfo(String staffId) {
        // Gán ID nhân viên vào trường idField
        idField.setText(staffId);
    }

    // Thêm tài khoản mới vào cơ sở dữ liệu
    private void addAccount() {
        // Lấy dữ liệu từ các trường nhập liệu
        String id = idField.getText().trim();
        String name = usernameField.getText().trim();
        String pass = passwordField.getText().trim();
        boolean isAdmin = adminCheckBox.isSelected();

        // Kiểm tra xem các trường có trống không
        if (id.isEmpty() || name.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        // Kiểm tra ID đã tồn tại
        if (isAccountExists(id)) {
            JOptionPane.showMessageDialog(this, "Tài khoản với ID " + id + " đã tồn tại, vui lòng chọn ID khác.");
            return;
        }

        // Kiểm tra tên tài khoản đã tồn tại
        if (isUsernameExists(name)) {
            JOptionPane.showMessageDialog(this, "Tên tài khoản " + name + " đã tồn tại, vui lòng chọn tên tài khoản khác.");
            return;
        }

        // Băm mật khẩu
        String hashedPassword;
        try {
            hashedPassword = Hash.hashPassword(pass);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi khi băm mật khẩu.");
            return;
        }

        // Thêm tài khoản vào cơ sở dữ liệu
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO users (id, username, password, is_admin, public_key) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, id);
            stmt.setString(2, name);
            stmt.setString(3, hashedPassword);
            stmt.setBoolean(4, isAdmin);
            stmt.setString(5, "");
            stmt.executeUpdate();

            // Cập nhật bảng giao diện
            tableModel.addRow(new Object[]{id, name, hashedPassword, isAdmin});
            clearInputFields(); // Xóa các trường nhập liệu
            JOptionPane.showMessageDialog(this, "Tài khoản đã được thêm thành công!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi khi thêm tài khoản.");
        }
    }

    // Kiểm tra xem ID tài khoản đã tồn tại trong cơ sở dữ liệu
    private boolean isAccountExists(String userId) {
        // Truy vấn cơ sở dữ liệu để kiểm tra ID
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT COUNT(*) FROM users WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Trả về true nếu ID đã tồn tại
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi khi kiểm tra ID tài khoản.");
        }
        return false;
    }

    // Kiểm tra xem tên tài khoản đã tồn tại trong cơ sở dữ liệu
    private boolean isUsernameExists(String username) {
        // Truy vấn cơ sở dữ liệu để kiểm tra tên tài khoản
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT COUNT(*) FROM users WHERE username = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Trả về true nếu tên tài khoản đã tồn tại
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi khi kiểm tra tên tài khoản.");
        }
        return false;
    }

    // Xóa tài khoản được chọn
    private void removeAccount() {
        // Lấy dòng được chọn từ bảng
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một tài khoản để xóa.");
            return;
        }

        // Lấy ID và tên tài khoản từ dòng được chọn
        String id = tableModel.getValueAt(selectedRow, 0).toString();
        String username = (String) tableModel.getValueAt(selectedRow, 1);

        // Xóa tài khoản khỏi cơ sở dữ liệu
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "DELETE FROM users WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, id);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Tài khoản đăng nhập " + username + " đã được xóa thành công!");
                tableModel.removeRow(selectedRow); // Xóa dòng khỏi bảng giao diện
                clearInputFields(); // Xóa các trường nhập liệu
            } else {
                JOptionPane.showMessageDialog(this, "Không thể xóa tài khoản, vui lòng thử lại.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi khi xóa tài khoản.");
        }
    }

    // Tải dữ liệu tài khoản từ cơ sở dữ liệu vào bảng
    private void loadCustomerData() {
        // Truy vấn cơ sở dữ liệu để lấy danh sách tài khoản
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT id, username, password, is_admin FROM users";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            tableModel.setRowCount(0); // Xóa dữ liệu cũ trong bảng

            // Thêm từng tài khoản vào bảng
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("username");
                String pass = rs.getString("password");
                boolean isAdmin = rs.getBoolean("is_admin");
                tableModel.addRow(new Object[]{id, name, pass, isAdmin});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Quá trình tải dữ liệu từ MySQL thất bại.");
        }
    }

    // Xóa nội dung các trường nhập liệu
    private void clearInputFields() {
        // Đặt lại các trường nhập liệu về rỗng và bỏ chọn checkbox
        idField.setText("");
        usernameField.setText("");
        passwordField.setText("");
        adminCheckBox.setSelected(false);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        User adminUser = new User("admin", "admin123", true, false);
        new AccountLogin(adminUser);
    }
}