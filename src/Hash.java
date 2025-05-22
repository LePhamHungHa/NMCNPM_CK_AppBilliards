import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {
    // Phương thức băm mật khẩu
    public static String hashPassword(String password) {
        try {
            // Sử dụng SHA-256 để băm mật khẩu
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            
            // Chuyển đổi mảng byte thành chuỗi hex
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            
            return hexString.toString(); // Trả về mật khẩu đã băm
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}