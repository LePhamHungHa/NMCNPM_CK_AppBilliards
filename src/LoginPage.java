import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class LoginPage extends JFrame {

    public LoginPage() {
        // Background app
        ImageIcon logoIcon = createImageIcon("/img/bidabg.jpg"); 
        if (logoIcon != null) {
            this.setIconImage(logoIcon.getImage()); // Đặt icon cho JFrame
        }

        // Thiết lập cửa sổ đăng nhập
        this.setTitle("Đăng nhập");
        this.setSize(1200, 800); // Thay đổi kích thước cửa sổ
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null); // Đặt cửa sổ ở giữa màn hình

        // Tạo panel chính với LayeredPane để chứa nhiều lớp
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1200, 800)); // Đặt kích thước của LayeredPane
        layeredPane.setLayout(null); // Sử dụng layout null để tự đặt vị trí

        // Thêm background
        ImageIcon mainbackground = createImageIcon("/img/bidabg.jpg"); // Thay đổi đường dẫn tệp hình nền
        if (mainbackground != null) {
            Image backgroundImage = mainbackground.getImage().getScaledInstance(1200, 800, Image.SCALE_SMOOTH);
            JLabel backgroundLabel = new JLabel(new ImageIcon(backgroundImage));
            backgroundLabel.setBounds(0, 0, 1200, 800); // Đặt vị trí background
            layeredPane.add(backgroundLabel, Integer.valueOf(0)); // Đặt background ở lớp 0
        }

        // Tạo panel form cho các trường đăng nhập
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false); // Làm nền trong suốt
        // Thêm viền và nền mờ (tùy chọn) để nổi bật
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 2), // Viền trắng
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Sử dụng GridBagConstraints để điều chỉnh vị trí thành phần
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);  // Khoảng cách giữa các thành phần
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        // Tạo và thêm nhãn tài khoản và mật khẩu
        JLabel usernameLabel = new JLabel("Tài khoản:");
        usernameLabel.setForeground(Color.WHITE); // Đặt màu chữ trắng để nổi bật
        JLabel passwordLabel = new JLabel("Mật khẩu:");
        passwordLabel.setForeground(Color.WHITE); // Đặt màu chữ trắng
        JTextField usernameField = new JTextField(20);
        usernameField.setOpaque(false); // Làm trường nhập liệu trong suốt
        usernameField.setForeground(Color.WHITE); // Chữ trắng
        usernameField.setBorder(BorderFactory.createLineBorder(Color.WHITE)); // Viền trắng
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setOpaque(false); // Làm trường nhập liệu trong suốt
        passwordField.setForeground(Color.WHITE); // Chữ trắng
        passwordField.setBorder(BorderFactory.createLineBorder(Color.WHITE)); // Viền trắng

        // Căn chỉnh nhãn và trường nhập liệu
        // Tài khoản
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(usernameField, gbc);

        // Mật khẩu
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(passwordField, gbc);

        // Tạo các nút và thêm vào formPanel
        JButton loginButton = new JButton("Đăng nhập");
        loginButton.setBackground(new Color(33, 150, 243)); // Màu xanh dương
        loginButton.setForeground(Color.BLACK); 
        loginButton.setFocusPainted(false);

        // Đặt kích thước bằng nhau cho các nút
        Dimension buttonSize = new Dimension(120, 30); // Tăng chiều cao cho nút đẹp hơn
        loginButton.setPreferredSize(buttonSize);

        // Panel cho các nút để sắp xếp chúng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false); // Nền trong suốt
        buttonPanel.add(loginButton);

        // Thêm các nút vào formPanel
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(buttonPanel, gbc);

        // Tính toán preferred size của formPanel
        formPanel.setSize(formPanel.getPreferredSize());

        // Đặt formPanel ở giữa màn hình
        int formWidth = formPanel.getPreferredSize().width;
        int formHeight = formPanel.getPreferredSize().height;
        formPanel.setBounds((1200 - formWidth) / 2, (800 - formHeight) / 2, formWidth, formHeight);

        // Thêm formPanel vào LayeredPane ở lớp trên cùng
        layeredPane.add(formPanel, Integer.valueOf(1));

        // Thêm LayeredPane vào JFrame
        this.setContentPane(layeredPane);
        this.setVisible(true);

        loginButton.addActionListener(e -> login(usernameField.getText(), new String(passwordField.getPassword())));

        // Sử dụng nút Enter
        passwordField.addActionListener(e -> login(usernameField.getText(), new String(passwordField.getPassword())));

        // Thêm KeyListener vào trường mật khẩu để lắng nghe sự kiện nhấn phím F1
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // Kiểm tra nếu phím F1 được nhấn
                if (e.getKeyCode() == KeyEvent.VK_F1) {
                    // Gọi phương thức đăng nhập khi F1 được nhấn
                    login(usernameField.getText(), new String(passwordField.getPassword()));
                }
            }
        });
    }

    // Phương thức login
    private void login(String username, String password) {
        // Băm mật khẩu người dùng nhập
        String hashedPassword = Hash.hashPassword(password);

        // Xác thực người dùng với mật khẩu đã băm
        User user = UserManager.authenticate(username, hashedPassword);
        if (user != null) {
            // Kiểm tra mã PIN sau khi xác thực thành công
            JPasswordField pinField = new JPasswordField(15); // Tạo JPasswordField để giấu mã PIN
            int option = JOptionPane.showConfirmDialog(this, pinField, "Nhập mã PIN của bạn:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (option == JOptionPane.OK_OPTION) {
                String pin = new String(pinField.getPassword());

                if (pin != null && getPinFromDatabase(pin)) {
                    if (user.isAdmin()) {
                        new MenuPage(user); // Truyền đối tượng user tới trang MenuPage
                    } else {
                        new MainPage(user); // Truyền đối tượng user tới trang MainPage
                    }
                    this.setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(this, "Mã PIN không hợp lệ.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Sai tên người dùng hoặc mật khẩu.");
        }
    }

    // Phương thức để tải tài nguyên hình ảnh từ JAR hoặc classpath
    private ImageIcon createImageIcon(String path) {
        try {
            // Lấy tài nguyên từ classpath (JAR hoặc thư mục resources)
            java.net.URL imgURL = getClass().getResource(path);
            if (imgURL != null) {
                return new ImageIcon(imgURL);
            } else {
                System.out.println("Không thể tải tài nguyên từ: " + path);
                return null;
            }
        } catch (Exception e) {
            System.out.println("Lỗi khi tải tài nguyên từ: " + path);
            e.printStackTrace();
            return null;
        }
    }

    // Phương thức lấy mã PIN từ cơ sở dữ liệu
    private boolean getPinFromDatabase(String inputPin) {
        String query = "SELECT pin_code FROM club LIMIT 1";  // Câu truy vấn lấy mã PIN từ bảng club

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            // Thực thi câu truy vấn và lấy kết quả
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                // Lấy mã PIN từ cơ sở dữ liệu
                String storedPin = resultSet.getString("pin_code");
                
                // So sánh mã PIN nhập vào với mã PIN trong cơ sở dữ liệu
                return storedPin.equals(inputPin);
            }
        } catch (SQLException e) {
            e.printStackTrace();  // In ra lỗi nếu có sự cố khi kết nối hoặc truy vấn
        }

        return false;  // Trả về false nếu không tìm thấy hoặc có lỗi
    }

    public static void main(String[] args) {
        // Đặt Look and Feel trước khi tạo giao diện
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace(); // Có lỗi sẽ báo
        }

        // Tạo giao diện trên Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(LoginPage::new);
    }
}

