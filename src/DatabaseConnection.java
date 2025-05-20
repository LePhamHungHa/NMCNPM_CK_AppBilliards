//import java.io.IOException;
//import java.io.InputStream;
//import java.security.PrivateKey;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//import java.util.Properties;
//
//import Security.RSA;
//
//public class DatabaseConnection {
//
//    private static String[] URL;
//    private static String PORT;
//    private static String DATABASE;
//    private static String USER;
//    private static String PASSWORD;
//    private static PrivateKey privateKey;
//    private static int TIMEOUT = 10;
//
//    static {
//    	InputStream inputStream = null;
//        try {
//        	Class.forName("com.mysql.cj.jdbc.Driver");
//        	
//            inputStream = DatabaseConnection.class.getClassLoader().getResourceAsStream("config.properties");
//            if (inputStream == null) {
//                throw new Exception("Không tìm thấy tệp config.properties trong classpath.");
//            }
//
//            Properties properties = new Properties();
//            properties.load(inputStream);
//
//            // Lấy khóa riêng từ class RSA
//            privateKey = RSA.getPrivateKey();
//
//            // Giải mã URL, username, và password
//            String encryptedUrls = properties.getProperty("db.urls");
//            String encryptedPort = properties.getProperty("db.port");
//            String encryptedDatabase = properties.getProperty("db.database");
//            String encryptedUser = properties.getProperty("db.username");
//            String encryptedPassword = properties.getProperty("db.password");
//            
//         // Kiểm tra nếu giá trị không hợp lệ
//            if (encryptedUrls == null || encryptedPort == null || encryptedDatabase == null ||
//                encryptedUser == null || encryptedPassword == null) {
//                throw new Exception("Cấu hình kết nối không hợp lệ.");
//            }
//            
//         // Tách chuỗi URLs thành mảng trước khi giải mã
//            String[] encryptedUrlArray = encryptedUrls.split(",");
//
//            // Giải mã các giá trị
//            String[] decryptedUrls = new String[encryptedUrlArray.length];
//            for (int i = 0; i < encryptedUrlArray.length; i++) {
//                decryptedUrls[i] = RSA.decrypt(encryptedUrlArray[i].trim(), privateKey); // Giải mã từng URL
//            }
//            
//            URL = decryptedUrls;
//            PORT = RSA.decrypt(encryptedPort, privateKey);
//            DATABASE = RSA.decrypt(encryptedDatabase, privateKey);
//            USER = RSA.decrypt(encryptedUser, privateKey);
//            PASSWORD = RSA.decrypt(encryptedPassword, privateKey);
//            
//
//        } catch (Exception e) {
//            System.err.println("Không thể đọc tệp cấu hình!");
//            e.printStackTrace();
//        }finally {
//            // Đóng InputStream sau khi sử dụng
//            if (inputStream != null) {
//                try {
//                    inputStream.close();
//                } catch (Exception e) {
//                    System.err.println("Không thể đóng tệp config.properties!");
//                }
//            }
//        }
//    }
//
//    // Phương thức trả về PrivateKey
//public static PrivateKey getPrivateKey() {
//        return privateKey;
//    }
//    
//    // Kết nối tới cơ sở dữ liệu
//    public static Connection getConnection() throws SQLException {
//        for (String ip : URL) {
//            // Đảm bảo URL có định dạng đúng
//        	String url = String.format("jdbc:mysql://%s:%s/%s?connectTimeout=%d", 
//                    ip.trim(), PORT, DATABASE, TIMEOUT);
//            Connection connection = null;
//            try {
//                System.out.println("Đang thử kết nối đến: " + url);
//                connection = DriverManager.getConnection(url, USER, PASSWORD);
//                System.out.println("Kết nối thành công đến: " + ip);
//                return connection;
//            } catch (SQLException e) {
//                System.err.println("Lỗi kết nối đến: " + ip + " - " + e.getMessage());
//            }
//        }
//        throw new SQLException("Không thể kết nối đến bất kỳ máy chủ nào trong danh sách!");
//    }
//}
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
            Class.forName("com.mysql.cj.jdbc.Driver");

            inputStream = DatabaseConnection.class.getClassLoader().getResourceAsStream("config.properties");
            if (inputStream == null) {
                throw new Exception("Không tìm thấy tệp config.properties trong classpath.");
            }
            Properties properties = new Properties();
            properties.load(inputStream);
            // Lấy thông tin kết nối từ file config.properties
            URL = properties.getProperty("db.url");
            USER = properties.getProperty("db.username");
            PASSWORD = properties.getProperty("db.password");

            // Kiểm tra nếu giá trị không hợp lệ
            if (URL == null || USER == null || PASSWORD == null) {
                throw new Exception("Cấu hình kết nối không hợp lệ.");
            }
        } catch (Exception e) {
            System.err.println("Không thể đọc tệp cấu hình!");
            e.printStackTrace();
        } finally {
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
        String urlWithTimeout = String.format("%s?connectTimeout=%d", URL, TIMEOUT);
        System.out.println("Đang kết nối đến: " + urlWithTimeout);
        return DriverManager.getConnection(urlWithTimeout, USER, PASSWORD);
    }
}
