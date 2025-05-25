import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {

    // Phương thức băm mật khẩu
    public static String hashPassword(String password) {
        try {
            // Tạo đối tượng MessageDigest với thuật toán SHA-256 để băm mật khẩu
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // Chuyển đổi mật khẩu thành mảng byte và băm
            byte[] hash = digest.digest(password.getBytes());
            // Tạo StringBuilder để xây dựng chuỗi hex từ mảng byte
            StringBuilder hexString = new StringBuilder();
            
            // Duyệt qua mảng byte và chuyển mỗi byte thành chuỗi hex 2 ký tự
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            
            // Trả về chuỗi hex đại diện cho mật khẩu đã băm
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // Xử lý lỗi nếu thuật toán SHA-256 không khả dụng
            e.printStackTrace();
            return null; // Trả về null nếu có lỗi
        }
    }
}