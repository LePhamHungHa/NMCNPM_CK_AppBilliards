import javax.swing.*;
import java.awt.*;

public class AdminPage extends JFrame {

    public AdminPage(User currentUser) {
        // Đặt hình ảnh logo cho JFrame
        ImageIcon logoIcon = new ImageIcon("/Img/logo_pool.png");
        this.setIconImage(logoIcon.getImage());

        // Kiểm tra nếu không phải admin, chuyển về LoginPage
        if (!currentUser.isAdmin()) {
            JOptionPane.showMessageDialog(this, "Bạn không có quyền truy cập trang quản lý admin!");
            new LoginPage();
            setVisible(false);
            return;
        }

        this.setTitle("Quản lý Admin - CLB Billiards");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(screenSize.width, screenSize.height);
        this.setLocationRelativeTo(null);

        // Set up background image
        ImageIcon mainBackground = new ImageIcon("Img/logo_pool.png");
        Image backgroundImage = mainBackground.getImage().getScaledInstance(screenSize.width, screenSize.height, Image.SCALE_SMOOTH);

        // Panel chính với hình nền và lớp mờ
        JPanel adminPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                g.setColor(new Color(50, 50, 50, 150)); // Lớp mờ xám đậm nhẹ
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        adminPanel.setLayout(new BorderLayout(15, 15));
        adminPanel.setOpaque(false);

        // Thanh tiêu đề trên cùng
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(33, 150, 243)); // Xanh dương chuyên nghiệp
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        JLabel welcomeLabel = new JLabel("Chào mừng đến với trang quản lý LPHH Billiards!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(Color.BLACK);
        headerPanel.add(welcomeLabel);
        
        adminPanel.add(headerPanel, BorderLayout.NORTH);

        // Panel chứa các nút chức năng
        JPanel adminButtons = new JPanel();
        adminButtons.setLayout(new GridLayout(6, 1, 0, 20)); // 6 hàng, cách nhau 20px
        adminButtons.setOpaque(false);
        adminButtons.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));
        adminButtons.setMaximumSize(new Dimension(300, 400));

        // Tạo các nút với giao diện đẹp hơn
        JButton manageStaffButton = createStyledButton("Quản lý nhân viên");
        JButton manageCustomersButton = createStyledButton("Quản lý khách hàng");
        JButton addaccountButton = createStyledButton("Quản lý account đăng nhập");
        JButton addMenu = createStyledButton("Quản lý thực đơn");
        JButton manageRevenueButton = createStyledButton("Quản lý thống kê doanh thu");
        JButton backButton = createStyledButton("Quay lại");

        // Lắng nghe sự kiện các nút (giữ nguyên logic cũ)
        manageStaffButton.addActionListener(e -> {
            new ManageStaffPage(currentUser);
            setVisible(false);
        });

        manageCustomersButton.addActionListener(e -> {
            new ManageCustomersPage(currentUser);
            setVisible(false);
        });

        addaccountButton.addActionListener(e -> {
            new AccountLogin(currentUser);
            setVisible(false);
        });

        addMenu.addActionListener(e -> {
            new MenuManager(currentUser);
            setVisible(false);
        });

        manageRevenueButton.addActionListener(e -> {
            new ManageRevenuePage(currentUser);
            setVisible(false);
        });

        backButton.addActionListener(e -> {
            new MenuPage(currentUser);
            setVisible(false);
        });

        // Thêm nút vào adminButtons
        adminButtons.add(manageStaffButton);
        adminButtons.add(manageCustomersButton);
        adminButtons.add(addaccountButton);
        adminButtons.add(addMenu);
        adminButtons.add(manageRevenueButton);
        adminButtons.add(backButton);

        // Panel giữa để căn giữa các nút
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(adminButtons);

        adminPanel.add(centerPanel, BorderLayout.CENTER);

        this.add(adminPanel);
        this.setVisible(true);
    }

    // Phương thức tạo nút với giao diện chuyên nghiệp
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        button.setBackground(new Color(33, 150, 243)); // Xanh dương
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(25, 118, 210), 1),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        button.setMaximumSize(new Dimension(250, 50));
        button.setPreferredSize(new Dimension(250, 50));

        // Hiệu ứng hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(25, 118, 210)); // Đậm hơn khi hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(33, 150, 243));
            }
        });

        return button;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        User adminUser = new User("admin", "admin123", true, false);
        new AdminPage(adminUser);
    }
}