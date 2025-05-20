import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserManager {

    // Kiểm tra đăng nhập
    public static User authenticate(String username, String password) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                boolean isAdmin = rs.getBoolean("is_admin");
                return new User(username, password, isAdmin, false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;  // Nếu không tìm thấy tài khoản
    }

    // Thêm tài khoản mới
    public static void addUser(String username, String password, boolean isAdmin) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO users (username, password, is_admin) VALUES (?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setBoolean(3, isAdmin);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Lấy danh sách người dùng từ cơ sở dữ liệu
    public static List<User> getUsers() {
        List<User> users = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM users";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                boolean isAdmin = rs.getBoolean("is_admin");
                users.add(new User(username, password, isAdmin, false));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
}
