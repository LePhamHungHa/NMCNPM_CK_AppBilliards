import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class LoginPage extends JFrame {

    public LoginPage() {
        // Thiết lập hình nền và logo cho cửa sổ
        ImageIcon logoIcon = createImageIcon("/img/bidabg.jpg"); 
        if (logoIcon != null) {
            this.setIconImage(logoIcon.getImage()); // Đặt icon cho JFrame
        }

        // Thiết lập tiêu đề và kích thước cửa sổ
        this.setTitle("Đăng nhập");
        this.setSize(1200, 800); // Kích thước cửa sổ
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null); // Căn giữa màn hình

        // Tạo JLayeredPane để chứa nhiều lớp giao diện
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1200, 800));
        layeredPane.setLayout(null); // Sử dụng layout null để tự đặt vị trí

        // Thêm hình nền
        ImageIcon mainbackground = createImageIcon("/img/bidabg.jpg");
        if (mainbackground != null) {
            // Điều chỉnh kích thước hình nền và thêm vào LayeredPane
            Image backgroundImage = mainbackground.getImage().getScaledInstance(1200, 800, Image.SCALE_SMOOTH);
            JLabel backgroundLabel = new JLabel(new ImageIcon(backgroundImage));
            backgroundLabel.setBounds(0, 0, 1200, 800);
            layeredPane.add(backgroundLabel, Integer.valueOf(0)); // Đặt ở lớp dưới cùng
        }

        // Tạo panel form cho các trường đăng nhập
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false); // Nền trong suốt để hiển thị hình nền
        // Thêm viền trắng và khoảng cách
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Sử dụng GridBagConstraints để căn chỉnh các thành phần
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        // Tạo và thêm nhãn, trường nhập liệu cho tài khoản và mật khẩu
        JLabel usernameLabel = new JLabel("Tài khoản:");
        usernameLabel.setForeground(Color.WHITE); // Chữ trắng để nổi bật
        JLabel passwordLabel = new JLabel("Mật khẩu:");
        passwordLabel.setForeground(Color.WHITE);
        JTextField usernameField = new JTextField(20);
        usernameField.setOpaque(false);
        usernameField.setForeground(Color.WHITE);
        usernameField.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setOpaque(false);
        passwordField.setForeground(Color.WHITE);
        passwordField.setBorder(BorderFactory.createLineBorder(Color.WHITE));

        // Căn chỉnh nhãn và trường nhập liệu
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.0; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(usernameLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(usernameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(passwordLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(passwordField, gbc);

        // Tạo nút đăng nhập
        JButton loginButton = new JButton("Đăng nhập");
        loginButton.setBackground(new Color(33, 150, 243)); // Màu xanh dương
        loginButton.setForeground(Color.BLACK);
        loginButton.setFocusPainted(false);
        loginButton.setPreferredSize(new Dimension(120, 30));

        // Tạo panel chứa nút đăng nhập
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(loginButton);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.weightx = 0.0; gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(buttonPanel, gbc);

        // Đặt vị trí formPanel ở giữa LayeredPane
        formPanel.setSize(formPanel.getPreferredSize());
        int formWidth = formPanel.getPreferredSize().width;
        int formHeight = formPanel.getPreferredSize().height;
        formPanel.setBounds((1200 - formWidth) / 2, (800 - formHeight) / 2, formWidth, formHeight);

        // Thêm formPanel vào LayeredPane ở lớp trên
        layeredPane.add(formPanel, Integer.valueOf(1));

        // Thêm LayeredPane vào JFrame
        this.setContentPane(layeredPane);
        this.setVisible(true);

        // Thêm sự kiện cho nút đăng nhập
        loginButton.addActionListener(e -> login(usernameField.getText(), new String(passwordField.getPassword())));

        // Cho phép nhấn Enter để đăng nhập
        passwordField.addActionListener(e -> login(usernameField.getText(), new String(passwordField.getPassword())));

        // Thêm KeyListener để hỗ trợ đăng nhập bằng phím F1
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_F1) {
                    // Gọi phương thức đăng nhập khi nhấn F1
                    login(usernameField.getText(), new String(passwordField.getPassword()));
                }
            }
        });
    }

    // Đăng nhập và xác thực người dùng
    private void login(String username, String password) {
        // Băm mật khẩu nhập vào sử dụng lớp Hash
        String hashedPassword = Hash.hashPassword(password);

        // Xác thực thông tin đăng nhập với UserManager
        User user = UserManager.authenticate(username, hashedPassword);
        if (user != null) {
            // Hiển thị dialog để nhập mã PIN
            JPasswordField pinField = new JPasswordField(15);
            int option = JOptionPane.showConfirmDialog(this, pinField, "Nhập mã PIN của bạn:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (option == JOptionPane.OK_OPTION) {
                String pin = new String(pinField.getPassword());

                // Kiểm tra mã PIN với cơ sở dữ liệu
                if (pin != null && getPinFromDatabase(pin)) {
                    // Chuyển hướng dựa trên quyền admin
                    if (user.isAdmin()) {
                        new MenuPage(user); // Mở trang MenuPage cho admin
                    } else {
                        new MainPage(user); // Mở trang MainPage cho người dùng thường
                    }
                    this.setVisible(false); // Ẩn cửa sổ đăng nhập
                } else {
                    JOptionPane.showMessageDialog(this, "Mã PIN không hợp lệ.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Sai tên người dùng hoặc mật khẩu.");
        }
    }

    // Tải tài nguyên hình ảnh từ classpath
    private ImageIcon createImageIcon(String path) {
        try {
            // Lấy tài nguyên hình ảnh từ classpath
            java.net.URL imgURL = getClass().getResource(path);
            if (imgURL != null) {
                return new ImageIcon(imgURL); // Trả về ImageIcon nếu tìm thấy
            } else {
                System.out.println("Không thể tải tài nguyên từ: " + path);
                return null;
            }
        } catch (Exception e) {
            // Xử lý lỗi khi tải tài nguyên
            System.out.println("Lỗi khi tải tài nguyên từ: " + path);
            e.printStackTrace();
            return null;
        }
    }

    // Kiểm tra mã PIN từ cơ sở dữ liệu
    private boolean getPinFromDatabase(String inputPin) {
        // Truy vấn lấy mã PIN từ bảng club
        String query = "SELECT pin_code FROM club LIMIT 1";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                // So sánh mã PIN nhập vào với mã PIN trong cơ sở dữ liệu
                String storedPin = resultSet.getString("pin_code");
                return storedPin.equals(inputPin);
            }
        } catch (SQLException e) {
            // Xử lý lỗi SQL và in thông tin lỗi
            e.printStackTrace();
        }
        // Trả về false nếu không tìm thấy hoặc có lỗi
        return false;
    }

    // Main: Khởi chạy ứng dụng với giao diện hệ thống
    public static void main(String[] args) {
        try {
            // Áp dụng giao diện hệ thống (native look and feel)
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Xử lý lỗi khi thiết lập giao diện
            e.printStackTrace();
        }

        // Khởi tạo giao diện trên Event Dispatch Thread
        SwingUtilities.invokeLater(LoginPage::new);
    }
}