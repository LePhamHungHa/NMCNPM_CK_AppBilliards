import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static String URL;
    private static String USER;
    private static String PASSWORD;
    private static int TIMEOUT = 10;

    static {
        InputStream inputStream = null;
        try {
            // Tải driver JDBC cho MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Đọc tệp cấu hình config.properties từ classpath
            inputStream = DatabaseConnection.class.getClassLoader().getResourceAsStream("config.properties");
            if (inputStream == null) {
                throw new Exception("Không tìm thấy tệp config.properties trong classpath.");
            }

            // Tạo đối tượng Properties và tải dữ liệu từ tệp cấu hình
            Properties properties = new Properties();
            properties.load(inputStream);

            // Lấy thông tin kết nối từ tệp config.properties
            URL = properties.getProperty("db.url");
            USER = properties.getProperty("db.username");
            PASSWORD = properties.getProperty("db.password");

            // Kiểm tra nếu các giá trị cấu hình bị thiếu
            if (URL == null || USER == null || PASSWORD == null) {
                throw new Exception("Cấu hình kết nối không hợp lệ.");
            }
        } catch (Exception e) {
            // Xử lý lỗi khi đọc tệp cấu hình hoặc tải driver
            System.err.println("Không thể đọc tệp cấu hình!");
            e.printStackTrace();
        } finally {
            // Đóng InputStream để giải phóng tài nguyên
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    System.err.println("Không thể đóng tệp config.properties!");
                }
            }
        }
    }

    // Kết nối tới cơ sở dữ liệu
    public static Connection getConnection() throws SQLException {
        // Tạo URL kết nối với thời gian timeout
        String urlWithTimeout = String.format("%s?connectTimeout=%d", URL, TIMEOUT);
        // In thông báo để theo dõi quá trình kết nối
        System.out.println("Đang kết nối đến: " + urlWithTimeout);
        // Trả về đối tượng Connection bằng cách sử dụng DriverManager
        return DriverManager.getConnection(urlWithTimeout, USER, PASSWORD);
    }
}