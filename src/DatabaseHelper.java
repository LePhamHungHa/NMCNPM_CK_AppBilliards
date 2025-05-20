import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper {

    // Lấy danh sách món ăn từ bảng menu
    public List<MainPage.MenuItem> fetchMenuItems() {
        List<MainPage.MenuItem> menuItems = new ArrayList<>();
        String query = "SELECT name, price, type FROM menu";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                String type = rs.getString("type");
                menuItems.add(new MainPage.MenuItem(name, price, type));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return menuItems;
    }

    // Lấy số lượng trong kho của một món dựa trên tên
    public int getMenuItemQuantity(String name) {
        String query = "SELECT quantity FROM menu WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("quantity");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Cập nhật số lượng trong kho của một món
    public boolean updateMenuItemQuantity(String name, int newQuantity) {
        String query = "UPDATE menu SET quantity = ? WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, newQuantity);
            stmt.setString(2, name);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy danh sách tất cả các bàn
    public List<Table> getAllTables() {
        List<Table> tables = new ArrayList<>();
        String sql = "SELECT table_number, status, start_time FROM tables";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int tableNumber = rs.getInt("table_number");
                String status = rs.getString("status");
                long startTime = rs.getLong("start_time");
                tables.add(new Table(tableNumber, status, startTime));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tables;
    }

    // Thêm một bàn mới
    public boolean addTable(int tableNumber) {
        String sql = "INSERT INTO tables (table_number, status, start_time) VALUES (?, 'Trống', 0)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, tableNumber);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa một bàn
    public boolean removeTable(int tableNumber) {
        String sql = "DELETE FROM tables WHERE table_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, tableNumber);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật trạng thái và thời gian bắt đầu của bàn
    public boolean updateTableStatus(int tableNumber, String status, long startTime) {
        String sql = "UPDATE tables SET status = ?, start_time = ?, last_updated = CURRENT_TIMESTAMP WHERE table_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setLong(2, startTime);
            pstmt.setInt(3, tableNumber);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lưu đơn hàng cho bàn
    public boolean saveOrder(int tableNumber, String orderDetail, double orderPrice) {
        String sql = "INSERT INTO orders (table_number, order_detail, order_price) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, tableNumber);
            pstmt.setString(2, orderDetail);
            pstmt.setDouble(3, orderPrice);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy danh sách đơn hàng của một bàn
    public Map<String, Double> getOrdersForTable(int tableNumber) {
        Map<String, Double> orders = new HashMap<>();
        String sql = "SELECT order_detail, order_price FROM orders WHERE table_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, tableNumber);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                orders.put(rs.getString("order_detail"), rs.getDouble("order_price"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    // Xóa thông tin bàn và đơn hàng khi đóng bàn
    public boolean clearInvoiceInfo(int tableNumber) {
        String updateTableSql = "UPDATE tables SET status = 'Trống', start_time = 0, last_updated = CURRENT_TIMESTAMP WHERE table_number = ?";
        String deleteOrdersSql = "DELETE FROM orders WHERE table_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt1 = conn.prepareStatement(updateTableSql);
             PreparedStatement pstmt2 = conn.prepareStatement(deleteOrdersSql)) {
            pstmt1.setInt(1, tableNumber);
            pstmt1.executeUpdate();
            pstmt2.setInt(1, tableNumber);
            pstmt2.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lớp Table bên trong DatabaseHelper
    public static class Table {
        private int tableNumber;
        private String status;
        private long startTime;

        public Table(int tableNumber, String status, long startTime) {
            this.tableNumber = tableNumber;
            this.status = status;
            this.startTime = startTime;
        }

        public int getTableNumber() {
            return tableNumber;
        }

        public String getStatus() {
            return status;
        }

        public long getStartTime() {
            return startTime;
        }
    }
}