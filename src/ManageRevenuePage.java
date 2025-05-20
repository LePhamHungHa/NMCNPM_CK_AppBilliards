import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ManageRevenuePage extends JFrame {
    private JComboBox<Integer> yearComboBox;
    private JComboBox<String> monthComboBox;
    private DefaultTableModel revenueModel, yearRevenueModel, invoiceModel;

    public ManageRevenuePage(User currentUser) {
        ImageIcon logoIcon = new ImageIcon("/Img/logo_pool.png");
        this.setIconImage(logoIcon.getImage());

        this.setTitle("Quản lý Thống kê Doanh thu - CLB Billiards");
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

        JLabel titleLabel = new JLabel("QUẢN LÝ THỐNG KÊ DOANH THU");
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
            dispose();
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

        // Panel lọc dữ liệu
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.add(createLabel("Chọn năm:"));
        yearComboBox = new JComboBox<>(getAvailableYears());
        yearComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        filterPanel.add(yearComboBox);

        filterPanel.add(createLabel("Chọn tháng:"));
        monthComboBox = new JComboBox<>(getMonthOptions());
        monthComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        filterPanel.add(monthComboBox);

        // Panel chứa ba bảng
        JPanel tablePanel = new JPanel(new GridLayout(3, 1, 10, 10));
        tablePanel.setBackground(Color.WHITE);

        // Bảng doanh thu theo tháng
        String[] revenueColumns = {"Tháng", "Doanh thu (VNĐ)"};
        revenueModel = new DefaultTableModel(revenueColumns, 0);
        JTable revenueTable = new JTable(revenueModel);
        styleTable(revenueTable);
        JScrollPane revenueScrollPane = new JScrollPane(revenueTable);
        revenueScrollPane.setBorder(BorderFactory.createTitledBorder("📅 Thống kê theo tháng"));
        tablePanel.add(revenueScrollPane);

        // Bảng hóa đơn theo tháng
        String[] invoiceColumns = {"Mã hóa đơn", "Ngày", "Tổng tiền (VNĐ)"};
        invoiceModel = new DefaultTableModel(invoiceColumns, 0);
        JTable invoiceTable = new JTable(invoiceModel);
        styleTable(invoiceTable);
        JScrollPane invoiceScrollPane = new JScrollPane(invoiceTable);
        invoiceScrollPane.setBorder(BorderFactory.createTitledBorder("📄 Hóa đơn theo tháng"));
        tablePanel.add(invoiceScrollPane);

        // Bảng doanh thu theo năm
        String[] yearColumns = {"Năm", "Tổng doanh thu (VNĐ)"};
        yearRevenueModel = new DefaultTableModel(yearColumns, 0);
        JTable yearRevenueTable = new JTable(yearRevenueModel);
        styleTable(yearRevenueTable);
        JScrollPane yearRevenueScrollPane = new JScrollPane(yearRevenueTable);
        yearRevenueScrollPane.setBorder(BorderFactory.createTitledBorder("📆 Doanh thu theo năm"));
        tablePanel.add(yearRevenueScrollPane);

        ActionListener filterAction = e -> loadRevenueData((Integer) yearComboBox.getSelectedItem(), (String) monthComboBox.getSelectedItem());
        yearComboBox.addActionListener(filterAction);
        monthComboBox.addActionListener(filterAction);

        loadRevenueData(2024, "Tất cả");
        loadYearRevenueData();

        mainPanel.add(filterPanel, BorderLayout.WEST);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        this.add(mainPanel);
        this.setVisible(true);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(Color.BLACK);
        return label;
    }

    private Integer[] getAvailableYears() {
        return new Integer[]{2022, 2023, 2024, 2025};
    }

    private String[] getMonthOptions() {
        return new String[]{"Tất cả", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
    }

    private void loadRevenueData(int selectedYear, String selectedMonth) {
        String query;
        if ("Tất cả".equals(selectedMonth)) {
            query = "SELECT DATE_FORMAT(date, '%m') AS month, SUM(total_amount) AS total FROM invoices WHERE YEAR(date) = ? GROUP BY month ORDER BY month";
        } else {
            query = "SELECT DATE_FORMAT(date, '%m') AS month, SUM(total_amount) AS total FROM invoices WHERE YEAR(date) = ? AND MONTH(date) = ? GROUP BY month ORDER BY month";
        }

        Map<String, Double> revenueMap = new LinkedHashMap<>();
        if ("Tất cả".equals(selectedMonth)) {
            for (int i = 1; i <= 12; i++) {
                revenueMap.put(String.format("%02d/%d", i, selectedYear), 0.0);
            }
        } else {
            revenueMap.put(String.format("%s/%d", selectedMonth, selectedYear), 0.0);
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, selectedYear);
            if (!"Tất cả".equals(selectedMonth)) {
                stmt.setInt(2, Integer.parseInt(selectedMonth));
            }
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String month = String.format("%02d/%d", rs.getInt("month"), selectedYear);
                revenueMap.put(month, rs.getDouble("total"));
            }

            revenueModel.setRowCount(0);
            for (var entry : revenueMap.entrySet()) {
                revenueModel.addRow(new Object[]{entry.getKey(), String.format("%,.0f VNĐ", entry.getValue())});
            }

            if (!"Tất cả".equals(selectedMonth)) {
                loadInvoiceData(selectedYear, Integer.parseInt(selectedMonth));
            } else {
                invoiceModel.setRowCount(0);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu doanh thu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void loadInvoiceData(int selectedYear, int selectedMonth) {
        String query = "SELECT invoice_code, date, total_amount FROM invoices WHERE YEAR(date) = ? AND MONTH(date) = ? ORDER BY date";
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                throw new SQLException("Không thể kết nối đến cơ sở dữ liệu!");
            }
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, selectedYear);
                stmt.setInt(2, selectedMonth);
                ResultSet rs = stmt.executeQuery();

                invoiceModel.setRowCount(0);
                while (rs.next()) {
                    invoiceModel.addRow(new Object[]{
                        rs.getString("invoice_code"),
                        rs.getTimestamp("date").toString(),
                        String.format("%,.0f VNĐ", rs.getDouble("total_amount"))
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu hóa đơn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void loadYearRevenueData() {
        String query = "SELECT YEAR(date) AS year, SUM(total_amount) AS total FROM invoices GROUP BY year ORDER BY year";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            yearRevenueModel.setRowCount(0);
            while (rs.next()) {
                yearRevenueModel.addRow(new Object[]{rs.getInt("year"), String.format("%,.0f VNĐ", rs.getDouble("total"))});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu năm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.setForeground(Color.BLACK);
        table.setGridColor(new Color(200, 200, 200));
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setForeground(Color.BLACK);
        header.setBackground(new Color(240, 240, 240));
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        User adminUser = new User("admin", "admin123", true, false);
        new ManageRevenuePage(adminUser);
    }
}