import java.util.UUID;

public class User {
    private String username;
    private String password;
    private boolean isAdmin; // Kiểm tra xem người dùng có quyền admin không
    private String key; // Khóa của người dùng
    private boolean isLocked; // Kiểm tra xem tài khoản có bị khóa không

    // Constructor
    public User(String username, String password, boolean isAdmin, boolean isLocked) {
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
        this.isLocked = isLocked; // Khởi tạo trạng thái khóa
        if (!isAdmin && isLocked) {
            this.key = generateKey(); // Tạo khóa ngẫu nhiên cho các tài khoản không phải admin bị khóa
        } else {
            this.key = null;
        }
    }

    private String generateKey() {
        return UUID.randomUUID().toString();
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public String getKey() {
        return key;
    }

    public boolean isLocked() {
        return isLocked;
    }

    // Setters
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public void setLocked(boolean isLocked) {
        this.isLocked = isLocked; 
        if (isLocked) {
            this.key = generateKey(); 
        } else {
            this.key = null; 
        }
    }
    public void setKey(String key) {
        this.key = key; // Setter cho key
    }
}
