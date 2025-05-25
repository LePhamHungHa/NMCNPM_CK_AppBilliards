import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;

public class InvoicePage extends JFrame {

    private User currentUser; // Thông tin người dùng đang đăng nhập
    private MainPage mainPage; // Tham chiếu đến MainPage để tính chi phí giờ chơi

    // Constructor: Khởi tạo giao diện hóa đơn cho một bàn cụ thể
    public InvoicePage(int tableNumber, TableInfo tableInfo, User currentUser, MainPage mainPage) {
        this.mainPage = mainPage; // Lưu tham chiếu đến MainPage
        
        ImageIcon logoIcon = new ImageIcon("/Img/logo_pool.png");
        this.setIconImage(logoIcon.getImage());
        // Đặt tiêu đề và kích thước cửa sổ
        setTitle("Hóa Đơn - Bàn " + tableNumber);
        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.currentUser = currentUser;

        // Tạo mã hóa đơn ngẫu nhiên
        String invoiceCode = generateInvoiceCode();

        // Tạo panel chính với bố cục BorderLayout và nền trắng
        JPanel contentPane = new JPanel(new BorderLayout(15, 15));
        contentPane.setBackground(Color.WHITE);
        contentPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setContentPane(contentPane);

        // Tạo phần tiêu đề hóa đơn
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Thêm thông tin CLB Billiards
        JLabel clubNameLabel = new JLabel("LPHH BILLIARDS");
        clubNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        clubNameLabel.setForeground(Color.BLACK);
        clubNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(clubNameLabel);

        JLabel addressLabel = new JLabel("1111 Võ Văn Ngân, Thủ Đức");
        addressLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addressLabel.setForeground(Color.BLACK);
        addressLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(addressLabel);

        JLabel phoneLabel = new JLabel("012.345.6789");
        phoneLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        phoneLabel.setForeground(Color.BLACK);
        phoneLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(phoneLabel);

        headerPanel.add(Box.createVerticalStrut(10));

        // Thêm thông tin bàn, mã hóa đơn và người in
        JPanel tableAndInvoicePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        tableAndInvoicePanel.setBackground(Color.WHITE);

        JLabel tableLabel = new JLabel("Bàn " + tableNumber + " | ");
        tableLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tableLabel.setForeground(Color.BLACK);
        tableAndInvoicePanel.add(tableLabel);

        JLabel invoiceLabel = new JLabel("Mã hóa đơn: " + invoiceCode + " | ");
        invoiceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        invoiceLabel.setForeground(Color.BLACK);
        tableAndInvoicePanel.add(invoiceLabel);

        JLabel employeeLabel = new JLabel("Người in: " + currentUser.getUsername());
        employeeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        employeeLabel.setForeground(Color.BLACK);
        tableAndInvoicePanel.add(employeeLabel);

        headerPanel.add(tableAndInvoicePanel);

        // Thêm thông tin thời gian bắt đầu và kết thúc
        JPanel timePanel = new JPanel(new GridLayout(2, 1, 0, 5));
        timePanel.setBackground(Color.WHITE);
        JLabel startTimeLabel = new JLabel("Giờ bắt đầu: " + formatDate(tableInfo.startTime));
        startTimeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        startTimeLabel.setForeground(Color.BLACK);
        JLabel endTimeLabel = new JLabel("Giờ kết thúc: " + formatDate(System.currentTimeMillis()));
        endTimeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        endTimeLabel.setForeground(Color.BLACK);
        timePanel.add(startTimeLabel);
        timePanel.add(endTimeLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(timePanel);

        contentPane.add(headerPanel, BorderLayout.NORTH);

        // Tạo bảng hiển thị danh sách các món đã order
        String[] columns = {"Tên", "SL", "Giá", "Tổng"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép chỉnh sửa dữ liệu trong bảng
            }
        };
        JTable orderTable = new JTable(model);
        orderTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        orderTable.setRowHeight(25);
        orderTable.setForeground(Color.BLACK);
        orderTable.setGridColor(new Color(200, 200, 200));
        orderTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        orderTable.getTableHeader().setForeground(Color.BLACK);
        orderTable.getTableHeader().setBackground(new Color(240, 240, 240));

        // Duyệt qua danh sách các món đã gọi và thêm vào bảng
        for (Map.Entry<String, Double> entry : tableInfo.orders.entrySet()) {
            String item = entry.getKey();
            double totalPrice = entry.getValue();

            // Tách thông tin món ăn và tính số lượng
            String[] itemParts = item.split(":");
            String itemName = itemParts[0].trim();
            String priceString = itemParts[1].trim().split(" ")[0];
            double unitPrice = Double.parseDouble(priceString.replace(",", ""));
            int quantity = (int) (totalPrice / unitPrice);

            model.addRow(new Object[]{itemName, quantity, unitPrice, totalPrice});
        }

        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        contentPane.add(scrollPane, BorderLayout.CENTER);

        // Tạo phần chân trang hiển thị tổng tiền và nút in hóa đơn
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.setBackground(Color.WHITE);

        JPanel totalPanel = new JPanel(new GridLayout(3, 1, 0, 5));
        totalPanel.setBackground(Color.WHITE);
        totalPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Tính chi phí giờ chơi từ MainPage (kiểm tra null để tránh lỗi)
        double playCost = (mainPage != null) ? mainPage.calculatePlayCostForTable(tableNumber) : 0.0;
        JLabel serviceTotalLabel = new JLabel("Tổng dịch vụ: " + tableInfo.orderTotal + " VND");
        serviceTotalLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        serviceTotalLabel.setForeground(Color.BLACK);
        JLabel playCostLabel = new JLabel("Tiền tính giờ: " + playCost + " VND");
        playCostLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        playCostLabel.setForeground(Color.BLACK);
        JLabel totalAmountLabel = new JLabel("Tổng thanh toán: " + (tableInfo.orderTotal + playCost) + " VND");
        totalAmountLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalAmountLabel.setForeground(Color.BLACK);

        totalPanel.add(serviceTotalLabel);
        totalPanel.add(playCostLabel);
        totalPanel.add(totalAmountLabel);

        footerPanel.add(totalPanel);

        // Tạo nút in hóa đơn với hiệu ứng hover
        JButton printButton = new JButton("In Hóa Đơn");
        printButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        printButton.setBackground(new Color(33, 150, 243));
        printButton.setForeground(Color.BLACK);
        printButton.setFocusPainted(false);
        printButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        printButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        printButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                printButton.setBackground(new Color(25, 118, 210)); // Đổi màu khi hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                printButton.setBackground(new Color(33, 150, 243)); // Trở lại màu gốc
            }
        });
        printButton.addActionListener(e -> {
            // Hiển thị thông báo chưa kết nối máy in
            JOptionPane.showMessageDialog(this, "Chưa kết nối máy in", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        });
        footerPanel.add(Box.createVerticalStrut(10));
        footerPanel.add(printButton);

        contentPane.add(footerPanel, BorderLayout.SOUTH);
        setVisible(true);

        // Lưu hóa đơn vào cơ sở dữ liệu
        double totalAmount = tableInfo.orderTotal + playCost;
        saveInvoiceToDatabase(invoiceCode, new Timestamp(System.currentTimeMillis()), "Đã thanh toán", currentUser.getUsername(), totalAmount);
    }

    // Lưu thông tin hóa đơn vào cơ sở dữ liệu
    private void saveInvoiceToDatabase(String invoiceCode, Timestamp date, String status, String employeeName, double totalAmount) {
        // Chuẩn bị truy vấn SQL để thêm hóa đơn
        String insertQuery = "INSERT INTO invoices (invoice_code, date, status, employee_name, total_amount) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            // Gán các tham số cho truy vấn
            preparedStatement.setString(1, invoiceCode);
            preparedStatement.setTimestamp(2, date);
            preparedStatement.setString(3, status);
            preparedStatement.setString(4, employeeName);
            preparedStatement.setDouble(5, totalAmount);
            // Thực thi truy vấn để lưu hóa đơn
            preparedStatement.executeUpdate();
            System.out.println("Hóa đơn đã được lưu vào cơ sở dữ liệu.");
        } catch (Exception e) {
            // Xử lý lỗi SQL và hiển thị thông báo lỗi
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu hóa đơn vào cơ sở dữ liệu.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Định dạng thời gian thành chuỗi dễ đọc
    private String formatDate(long timestamp) {
        // Sử dụng SimpleDateFormat để định dạng thời gian thành "HH:mm dd/MM/yyyy"
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        return sdf.format(new Date(timestamp));
    }

    // Tạo mã hóa đơn ngẫu nhiên
    private String generateInvoiceCode() {
        // Tạo số ngẫu nhiên từ 100 đến 99998 và thêm tiền tố "HD-"
        Random random = new Random();
        int invoiceNumber = 100 + random.nextInt(99999);
        return "HD-" + invoiceNumber;
    }
}