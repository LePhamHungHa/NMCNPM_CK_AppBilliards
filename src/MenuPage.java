import javax.swing.*;
import java.awt.*;

public class MenuPage extends JFrame {
    private User currentUser;

    public MenuPage(User user) {
        ImageIcon logoIcon = new ImageIcon("/Img/logo_pool.png");
        this.setIconImage(logoIcon.getImage());

        this.currentUser = user;
        this.setTitle("Menu Chính");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(screenSize.width, screenSize.height);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ImageIcon mainBackground = new ImageIcon("Img/logo_pool.png");
        Image backgroundImage = mainBackground.getImage().getScaledInstance(screenSize.width, screenSize.height, Image.SCALE_SMOOTH);

        // Panel chính với GridBagLayout để căn giữa
        JPanel menuPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, null);
            }
        };
        menuPanel.setOpaque(false);

        // Panel nội dung để chứa welcome và buttons
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // Welcome labels
        JLabel welcome1 = new JLabel("Chào mừng ");
        welcome1.setFont(new Font("Times New Roman", Font.BOLD, 24));
        welcome1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel welcomeLabel = new JLabel(user.getUsername());
        welcomeLabel.setFont(new Font("Times New Roman", Font.BOLD, 24));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(welcome1);
        contentPanel.add(welcomeLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Panel cho các nút
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setOpaque(false);

        if (user.isAdmin()) {
            JButton adminButton = new JButton("Vào trang Admin");
            adminButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
            adminButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            adminButton.setMaximumSize(new Dimension(200, 40));
            adminButton.setPreferredSize(new Dimension(200, 40));
            adminButton.addActionListener(e -> {
                new AdminPage(currentUser);
                dispose();
            });
            buttonsPanel.add(adminButton);
            buttonsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        }

        JButton shopButton = new JButton("Vào trang quản lý bida");
        shopButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
        shopButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        shopButton.setMaximumSize(new Dimension(200, 40));
        shopButton.setPreferredSize(new Dimension(200, 40));
        shopButton.addActionListener(e -> {
            new MainPage(currentUser);
            dispose();
        });
        buttonsPanel.add(shopButton);
        buttonsPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton logoutButton = new JButton("Đăng xuất");
        logoutButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.setMaximumSize(new Dimension(200, 40));
        logoutButton.setPreferredSize(new Dimension(200, 40));
        logoutButton.addActionListener(e -> {
            new LoginPage();
            dispose();
        });
        buttonsPanel.add(logoutButton);

        contentPanel.add(buttonsPanel);

        // Thêm contentPanel vào menuPanel với GridBagConstraints để căn giữa
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        menuPanel.add(contentPanel, gbc);

        this.add(menuPanel);
        this.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        User adminUser = new User("admin", "admin123", true, false);
        new MenuPage(adminUser);
    }
}