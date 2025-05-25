import javax.swing.*;
import java.awt.*;

public class AdminPage extends JFrame {

    public AdminPage(User currentUser) {
        // Đặt hình ảnh logo cho JFrame
        ImageIcon logoIcon = new ImageIcon("/Img/logo_pool.png");
        this.setIconImage(logoIcon.getImage());

        // Kiểm tra quyền admin: Nếu không phải admin, hiển thị thông báo và chuyển về LoginPage
        if (!currentUser.isAdmin()) {
            JOptionPane.showMessageDialog(this, "Bạn không có quyền truy cập trang quản lý admin!");
            new LoginPage();
            setVisible(false);
            return;
        }

        // Cấu hình tiêu đề và kích thước cửa sổ
        this.setTitle("Quản lý Admin - CLB Billiards");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(screenSize.width, screenSize.height);
        this.setLocationRelativeTo(null);

        // Thiết lập hình nền
        ImageIcon mainBackground = new ImageIcon("Img/logo_pool.png");
        Image backgroundImage = mainBackground.getImage().getScaledInstance(screenSize.width, screenSize.height, Image.SCALE_SMOOTH);

        // Tạo panel chính với hình nền và lớp mờ
        JPanel adminPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                g.setColor(new Color(50, 50, 50, 150));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        adminPanel.setLayout(new BorderLayout(15, 15));
        adminPanel.setOpaque(false);

        // Tạo thanh tiêu đề trên cùng
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(33, 150, 243)); // Màu xanh dương chuyên nghiệp
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        // Thêm nhãn chào mừng
        JLabel welcomeLabel = new JLabel("Chào mừng đến với trang quản lý LPHH Billiards!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(Color.BLACK);
        headerPanel.add(welcomeLabel);
        
        adminPanel.add(headerPanel, BorderLayout.NORTH);

        // Tạo panel chứa các nút chức năng
        JPanel adminButtons = new JPanel();
        adminButtons.setLayout(new GridLayout(6, 1, 0, 20)); // Sắp xếp 6 nút theo hàng dọc, cách nhau 20px
        adminButtons.setOpaque(false);
        adminButtons.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));
        adminButtons.setMaximumSize(new Dimension(300, 400));

        // Tạo các nút chức năng với giao diện tùy chỉnh
        JButton manageStaffButton = createStyledButton("Quản lý nhân viên");
        JButton manageCustomersButton = createStyledButton("Quản lý khách hàng");
        JButton addaccountButton = createStyledButton("Quản lý account đăng nhập");
        JButton addMenu = createStyledButton("Quản lý thực đơn");
        JButton manageRevenueButton = createStyledButton("Quản lý thống kê doanh thu");
        JButton backButton = createStyledButton("Quay lại");

        // Thêm sự kiện cho các nút: Chuyển đến các trang tương ứng và ẩn cửa sổ hiện tại
        manageStaffButton.addActionListener(e -> {
            new ManageStaffPage(currentUser); // Mở trang quản lý nhân viên
            setVisible(false);
        });

        manageCustomersButton.addActionListener(e -> {
            new ManageCustomersPage(currentUser); // Mở trang quản lý khách hàng
            setVisible(false);
        });

        addaccountButton.addActionListener(e -> {
            new AccountLogin(currentUser); // Mở trang quản lý tài khoản đăng nhập
            setVisible(false);
        });

        addMenu.addActionListener(e -> {
            new MenuManager(currentUser); // Mở trang quản lý thực đơn
            setVisible(false);
        });

        manageRevenueButton.addActionListener(e -> {
            new ManageRevenuePage(currentUser); // Mở trang quản lý doanh thu
            setVisible(false);
        });

        backButton.addActionListener(e -> {
            new MenuPage(currentUser); // Quay lại trang menu chính
            setVisible(false);
        });

        // Thêm các nút vào panel
        adminButtons.add(manageStaffButton);
        adminButtons.add(manageCustomersButton);
        adminButtons.add(addaccountButton);
        adminButtons.add(addMenu);
        adminButtons.add(manageRevenueButton);
        adminButtons.add(backButton);

        // Tạo panel giữa để căn giữa các nút
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(adminButtons);

        adminPanel.add(centerPanel, BorderLayout.CENTER);

        this.add(adminPanel);
        this.setVisible(true);
    }

    // Tạo nút với giao diện tùy chỉnh và hiệu ứng hover
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        button.setBackground(new Color(33, 150, 243)); // Màu xanh dương
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(25, 118, 210), 1),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        button.setMaximumSize(new Dimension(250, 50));
        button.setPreferredSize(new Dimension(250, 50));

        //Hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(25, 118, 210)); // Màu xanh đậm hơn khi hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(33, 150, 243)); // Trả về màu gốc
            }
        });

        return button;
    }

    public static void main(String[] args) {
        try {
            // Áp dụng giao diện hệ thống (native look and feel)
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Tạo người dùng admin mặc định và mở trang AdminPage
        User adminUser = new User("admin", "admin123", true, false);
        new AdminPage(adminUser);
    }
}