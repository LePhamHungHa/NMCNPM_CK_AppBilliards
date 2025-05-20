import javax.swing.JLabel;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TableInfo {
    public long startTime; // Thời gian bắt đầu sử dụng bàn 
    public Map<String, Double> orders; // Danh sách menu order
    public double orderTotal; // Tổng tiền của các món đã order
    public JLabel timerLabel; // Thời gian sử dụng bàn
    public Timer timer; // Bộ đếm thời gian thực

    // Khởi tạo thông tin bàn
    public TableInfo(long startTime, JLabel timerLabel) {
        this.startTime = startTime;
        this.orders = new HashMap<>(); // Khởi tạo danh sách đơn hàng rỗng
        this.orderTotal = 0.0; // Tổng tiền ban đầu là 0
        this.timerLabel = timerLabel;
        this.timer = new Timer(); // Tạo bộ đếm thời gian
        startTimer(); // Bắt đầu chạy timer
    }

    // Hàm khởi động timer để cập nhật thời gian
    private void startTimer() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (timerLabel != null) {
                    long elapsedTime = System.currentTimeMillis() - startTime; // Tính thời gian đã trôi qua
                    long minutes = (elapsedTime / 1000) / 60; // Chuyển thành phút
                    long seconds = (elapsedTime / 1000) % 60; // Chuyển thành giây
                    timerLabel.setText(String.format("%02d:%02d", minutes, seconds)); // Hiển thị thời gian dạng phút:giây
                }
            }
        }, 0, 1000); // Chạy ngay lập tức và lặp lại mỗi giây
    }

    // Hàm dừng timer khi đóng bàn
    public void stopTimer() {
        if (timer != null) {
            timer.cancel(); 
        }
    }

    // Hàm tính chi phí chơi dựa trên thời gian sử dụng
    public double calculatePlayCost(int pricePerMinute) {
        long elapsedTime = System.currentTimeMillis() - startTime; // Tính thời gian đã trôi qua
        long minutes = (elapsedTime / 1000) / 60; // Chuyển thành phút
        return minutes * pricePerMinute; // Phút * giá
    }
}