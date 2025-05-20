import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainPage extends JFrame {

    public static final int PRICE_PER_MINUTE = 1000; // Giờ chơi tính theo phút 

    private Map<Integer, TableInfo> tableInfoMap; // Lưu thông tin các bàn (TableInfo) theo số bàn
    private Map<Integer, JButton> tableButtonsMap; // Lưu các nút đại diện cho bàn
    private JPanel tableDetailsPanel; // Hiển thị chi tiết bàn được chọn
    private Map<Integer, JButton> openTableButtonsMap; // Lưu các nút "Mở Bàn" theo số bàn

    private User currentUser; // Thông tin người dùng hiện tại
    private DatabaseHelper dbHelper; // Kết nối cơ sở dữ liệu

    private Map<String, Double> menuItems = new HashMap<>(); // Menu

    public MainPage(User currentUser) {
        if (currentUser == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        this.currentUser = currentUser;
        this.dbHelper = new DatabaseHelper();

        setTitle("Quản Lý Bàn Bida - Chuyên Nghiệp");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        tableInfoMap = new HashMap<>();
        tableButtonsMap = new HashMap<>();
        openTableButtonsMap = new HashMap<>();

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(245, 245, 245));

        JToolBar controlPanel = createControlPanel(currentUser); // Tạo thanh công cụ điều khiển
        mainPanel.add(controlPanel, BorderLayout.NORTH);

        JPanel tablesPanel = createTablesPanel(); 
        mainPanel.add(tablesPanel, BorderLayout.CENTER);

        tableDetailsPanel = createTableDetailsPanel(); // CHi tiết các bàn
        mainPanel.add(tableDetailsPanel, BorderLayout.EAST);

        add(mainPanel);
        setVisible(true);

        loadMenuItems(); // Tải danh sách món ăn từ cơ sở dữ liệu
        restoreTableStates(); // Khôi phục trạng thái các bàn từ cơ sở dữ liệu
    }

    // Hàm khôi phục trạng thái các bàn khi khởi động ứng dụng
    private void restoreTableStates() {
        List<DatabaseHelper.Table> tables = dbHelper.getAllTables();
        for (DatabaseHelper.Table table : tables) {
            int tableNumber = table.getTableNumber();
            if (table.getStatus().equals("Đang sử dụng") && table.getStartTime() > 0) {
                JLabel timerLabel = new JLabel();
                TableInfo info = new TableInfo(table.getStartTime(), timerLabel); // Khởi tạo TableInfo với thời gian bắt đầu
                tableInfoMap.put(tableNumber, info);
                info.orders = dbHelper.getOrdersForTable(tableNumber); // Lấy danh sách đơn hàng từ DB
                info.orderTotal = info.orders.values().stream().mapToDouble(Double::doubleValue).sum(); // Tính tổng tiền đơn hàng

                JButton tableButton = tableButtonsMap.get(tableNumber);
                JButton openTableButton = openTableButtonsMap.get(tableNumber);
                if (tableButton != null) {
                    tableButton.setText("<html><center>Bàn " + tableNumber + "<br>Đang Sử Dụng</center></html>");
                    tableButton.setBackground(new Color(244, 67, 54));
                }
                if (openTableButton != null) {
                    openTableButton.setEnabled(false);
                }
            }
        }
    }

    // Hàm tạo thanh công cụ điều khiển (bao gồm nút thêm/xóa bàn cho admin)
    private JToolBar createControlPanel(User currentUser) {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBackground(new Color(33, 150, 243));
        toolBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("QUẢN LÝ BÀN BIDA");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        toolBar.add(titleLabel);

        toolBar.add(Box.createHorizontalGlue());

        if (currentUser.isAdmin()) {
            JButton addTableButton = new JButton("Thêm Bàn");
            addTableButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            addTableButton.setBackground(new Color(76, 175, 80));
            addTableButton.setForeground(Color.BLACK);
            addTableButton.setFocusPainted(false);
            addTableButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
            addTableButton.addActionListener(e -> showAddTableDialog());
            toolBar.add(addTableButton);

            toolBar.add(Box.createHorizontalStrut(10));

            JButton removeTableButton = new JButton("Xóa Bàn");
            removeTableButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            removeTableButton.setBackground(new Color(244, 67, 54));
            removeTableButton.setForeground(Color.BLACK);
            removeTableButton.setFocusPainted(false);
            removeTableButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
            removeTableButton.addActionListener(e -> showRemoveTableDialog());
            toolBar.add(removeTableButton);

            toolBar.add(Box.createHorizontalStrut(10));
        }

        JButton actionButton;
        if (currentUser.isAdmin()) {
            actionButton = new JButton("Quay Lại");
            actionButton.addActionListener(e -> {
                new MenuPage(currentUser);
                setVisible(false);
            });
        } else {
            actionButton = new JButton("Đăng Xuất");
            actionButton.addActionListener(e -> {
                new LoginPage();
                setVisible(false);
            });
        }
        actionButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        actionButton.setBackground(new Color(244, 67, 54));
        actionButton.setForeground(Color.BLACK);
        actionButton.setFocusPainted(false);
        actionButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        toolBar.add(actionButton);

        return toolBar;
    }

    // Hàm tạo panel chứa các bàn bida
    private JPanel createTablesPanel() {
        List<DatabaseHelper.Table> tables = dbHelper.getAllTables();
        JPanel tablesPanel = new JPanel(new GridLayout(0, 4, 15, 15));
        tablesPanel.setBackground(new Color(245, 245, 245));
        tablesPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        tableButtonsMap.entrySet().removeIf(entry -> !tables.stream().anyMatch(t -> t.getTableNumber() == entry.getKey()));
        openTableButtonsMap.entrySet().removeIf(entry -> !tables.stream().anyMatch(t -> t.getTableNumber() == entry.getKey()));
        tableInfoMap.entrySet().removeIf(entry -> !tables.stream().anyMatch(t -> t.getTableNumber() == entry.getKey()));

        for (DatabaseHelper.Table table : tables) {
            int tableNumber = table.getTableNumber();
            JPanel tablePanel;
            if (!tableButtonsMap.containsKey(tableNumber)) {
                tablePanel = createTablePanel(tableNumber, currentUser);
            } else {
                tablePanel = (JPanel) tableButtonsMap.get(tableNumber).getParent().getParent();
            }
            tablesPanel.add(tablePanel);

            JButton tableButton = tableButtonsMap.get(tableNumber);
            if (table.getStatus().equals("Đang sử dụng")) {
                tableButton.setText("<html><center>Bàn " + tableNumber + "<br>Đang Sử Dụng</center></html>");
                tableButton.setBackground(new Color(244, 67, 54));
                openTableButtonsMap.get(tableNumber).setEnabled(false);
            } else {
                tableButton.setText("<html><center>Bàn " + tableNumber + "<br>Trống</center></html>");
                tableButton.setBackground(new Color(76, 175, 80));
                openTableButtonsMap.get(tableNumber).setEnabled(true);
            }
        }

        JScrollPane scrollPane = new JScrollPane(tablesPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBackground(new Color(245, 245, 245));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.add(scrollPane, BorderLayout.CENTER);

        return containerPanel;
    }

    // Hàm tạo panel cho từng bàn
    private JPanel createTablePanel(int tableNumber, User currentUser) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(new Color(255, 255, 255));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 189, 189), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setPreferredSize(new Dimension(150, 150));

        JButton tableButton = new JButton("<html><center>Bàn " + tableNumber + "<br>Trống</center></html>");
        tableButton.setBackground(new Color(76, 175, 80));
        tableButton.setForeground(Color.WHITE);
        tableButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tableButton.setOpaque(true);
        tableButton.setBorderPainted(false);
        tableButton.setFocusPainted(false);
        tableButtonsMap.put(tableNumber, tableButton);

        tableButton.addActionListener(e -> updateTableDetailsPanel(tableNumber));

        JButton openTableButton = new JButton("Mở Bàn");
        openTableButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        openTableButton.setBackground(new Color(33, 150, 243));
        openTableButton.setForeground(Color.BLACK);
        openTableButton.setFocusPainted(false);
        openTableButton.addActionListener(e -> openTable(tableNumber, tableButton, openTableButton));
        openTableButtonsMap.put(tableNumber, openTableButton);

        JButton closeButton = new JButton("Đóng");
        closeButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        closeButton.setBackground(new Color(244, 67, 54));
        closeButton.setForeground(Color.BLACK);
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> {
            if (currentUser.isAdmin()) {
                if (tableInfoMap.containsKey(tableNumber)) {
                    closeTableManager(tableNumber);
                    openTableButton.setEnabled(true);
                }
            } else {
                if (tableInfoMap.containsKey(tableNumber)) {
                    closeTableManager(tableNumber);
                    openTableButton.setEnabled(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Bàn chưa được mở!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        panel.add(tableButton, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        buttonPanel.setBackground(new Color(255, 255, 255));
        buttonPanel.add(openTableButton);
        buttonPanel.add(closeButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Hàm tạo panel chi tiết thông tin bàn
    private JPanel createTableDetailsPanel() {
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(new Color(255, 255, 255));
        detailsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(189, 189, 189)), "Thông Tin Bàn",
                0, 0, new Font("Segoe UI", Font.BOLD, 16), new Color(33, 150, 243)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        detailsPanel.setPreferredSize(new Dimension(400, getHeight()));

        JLabel infoLabel = new JLabel("Chọn bàn để xem thông tin.");
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        infoLabel.setForeground(new Color(117, 117, 117));
        detailsPanel.add(Box.createVerticalGlue());
        detailsPanel.add(infoLabel);
        detailsPanel.add(Box.createVerticalGlue());

        return detailsPanel;
    }

    // Hàm tải danh sách món ăn từ cơ sở dữ liệu
    private void loadMenuItems() {
        List<MenuItem> items = dbHelper.fetchMenuItems();
        for (MenuItem item : items) {
            double price = item.getPrice();
            if (price < 1000) price *= 1000;
            menuItems.put(item.getName(), price);
        }
    }

    // Hàm mở bàn mới
    private void openTable(int tableNumber, JButton tableButton, JButton openTableButton) {
        if (!tableInfoMap.containsKey(tableNumber)) {
            long startTime = System.currentTimeMillis();
            JLabel timerLabel = new JLabel();
            TableInfo info = new TableInfo(startTime, timerLabel);
            tableInfoMap.put(tableNumber, info);

            tableButton.setText("<html><center>Bàn " + tableNumber + "<br>Đang Sử Dụng</center></html>");
            tableButton.setBackground(new Color(244, 67, 54));
            openTableButton.setEnabled(false);

            dbHelper.updateTableStatus(tableNumber, "Đang sử dụng", startTime);
        }
        updateTableDetailsPanel(tableNumber);
    }

    // Hàm cập nhật panel chi tiết bàn
    private void updateTableDetailsPanel(Integer tableNumber) {
        tableDetailsPanel.removeAll();

        JLabel infoLabel = new JLabel("Thông tin Bàn " + tableNumber);
        infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        infoLabel.setForeground(new Color(33, 150, 243));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        tableDetailsPanel.add(infoLabel);
        tableDetailsPanel.add(Box.createVerticalStrut(10));

        TableInfo info = tableInfoMap.get(tableNumber);
        if (info != null) {
            JPanel timerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            timerPanel.setBackground(new Color(255, 255, 255));
            JLabel timerTextLabel = new JLabel("Thời gian: ");
            timerTextLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            info.timerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            timerPanel.add(timerTextLabel);
            timerPanel.add(info.timerLabel);

            JLabel ordersLabel = new JLabel("Đồ ăn và nước uống:");
            ordersLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

            JList<String> ordersList = new JList<>(info.orders.keySet().toArray(new String[0]));
            ordersList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            JScrollPane ordersScrollPane = new JScrollPane(ordersList);
            ordersScrollPane.setPreferredSize(new Dimension(350, 150));

            JButton orderButton = new JButton("Menu");
            orderButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            orderButton.setBackground(new Color(33, 150, 243));
            orderButton.setForeground(Color.BLACK);
            orderButton.setFocusPainted(false);
            orderButton.addActionListener(e -> openMenu(info, tableNumber));

            JButton payButton = new JButton("Thanh Toán");
            payButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            payButton.setBackground(new Color(76, 175, 80));
            payButton.setForeground(Color.BLACK);
            payButton.setFocusPainted(false);
            payButton.addActionListener(e -> {
                new InvoicePage(tableNumber, info, currentUser, MainPage.this); // Truyền tham chiếu MainPage
                closeTableManager(tableNumber);
                openTableButtonsMap.get(tableNumber).setEnabled(true);
            });

            tableDetailsPanel.add(timerPanel);
            tableDetailsPanel.add(Box.createVerticalStrut(10));
            tableDetailsPanel.add(ordersLabel);
            tableDetailsPanel.add(Box.createVerticalStrut(5));
            tableDetailsPanel.add(ordersScrollPane);
            tableDetailsPanel.add(Box.createVerticalStrut(10));
            tableDetailsPanel.add(orderButton);
            tableDetailsPanel.add(Box.createVerticalStrut(5));
            tableDetailsPanel.add(payButton);
        } else {
            JLabel emptyTableLabel = new JLabel("Bàn đang trống.");
            emptyTableLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            emptyTableLabel.setForeground(new Color(117, 117, 117));
            emptyTableLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            tableDetailsPanel.add(Box.createVerticalGlue());
            tableDetailsPanel.add(emptyTableLabel);
            tableDetailsPanel.add(Box.createVerticalGlue());
        }

        tableDetailsPanel.revalidate();
        tableDetailsPanel.repaint();
    }

    // Hàm mở menu để thêm món
    private void openMenu(TableInfo info, int tableNumber) {
        JDialog menuDialog = new JDialog(this, "Thêm Món - Bàn " + tableNumber, true);
        menuDialog.setSize(700, 500);
        menuDialog.setLocationRelativeTo(this);
        menuDialog.setLayout(new BorderLayout(10, 10));
        menuDialog.getContentPane().setBackground(Color.WHITE);

        JToolBar dialogToolBar = new JToolBar();
        dialogToolBar.setFloatable(false);
        dialogToolBar.setBackground(new Color(33, 150, 243));
        dialogToolBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        JLabel dialogTitle = new JLabel("MENU ĐỒ ĂN & NƯỚC UỐNG");
        dialogTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        dialogTitle.setForeground(Color.WHITE);
        dialogToolBar.add(dialogTitle);
        menuDialog.add(dialogToolBar, BorderLayout.NORTH);

        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        typePanel.setBackground(Color.WHITE);
        JLabel typeLabel = new JLabel("Chọn loại:");
        typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JComboBox<String> typeComboBox = new JComboBox<>(new String[]{"Đồ Ăn", "Nước"});
        typeComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        typePanel.add(typeLabel);
        typePanel.add(typeComboBox);

        String[] columnNames = {"Tên món", "Giá (VNĐ)", "Số lượng còn"};
        DefaultTableModel menuModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable menuTable = new JTable(menuModel);
        menuTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        menuTable.setRowHeight(30);
        menuTable.setGridColor(new Color(200, 200, 200));
        menuTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        menuTable.getTableHeader().setBackground(new Color(240, 240, 240));
        menuTable.getTableHeader().setForeground(Color.BLACK);
        menuTable.getColumnModel().getColumn(0).setPreferredWidth(300);
        menuTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        menuTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        menuTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        loadMenuItemsToTable(menuModel, "Đồ Ăn");
        typeComboBox.addActionListener(e -> loadMenuItemsToTable(menuModel, (String) typeComboBox.getSelectedItem()));

        JScrollPane menuScrollPane = new JScrollPane(menuTable);
        menuScrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        quantityPanel.setBackground(Color.WHITE);
        JLabel quantityLabel = new JLabel("Số lượng:");
        quantityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JTextField quantityField = new JTextField(5);
        quantityField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        quantityField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        quantityPanel.add(quantityLabel);
        quantityPanel.add(quantityField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton addButton = new JButton("Thêm vào bàn");
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addButton.setBackground(new Color(33, 150, 243));
        addButton.setForeground(Color.BLACK);
        addButton.setFocusPainted(false);
        addButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        addButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                addButton.setBackground(new Color(25, 118, 210));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                addButton.setBackground(new Color(33, 150, 243));
            }
        });

        JButton cancelButton = new JButton("Đóng");
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelButton.setBackground(new Color(244, 67, 54));
        cancelButton.setForeground(Color.BLACK);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        cancelButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                cancelButton.setBackground(new Color(211, 47, 47));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                cancelButton.setBackground(new Color(244, 67, 54));
            }
        });

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        addButton.addActionListener(e -> {
            int selectedRow = menuTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(menuDialog, "Vui lòng chọn một món từ bảng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String name = (String) menuModel.getValueAt(selectedRow, 0);
            double price = parsePrice((String) menuModel.getValueAt(selectedRow, 1));
            int availableQuantity = Integer.parseInt((String) menuModel.getValueAt(selectedRow, 2));
            String quantityStr = quantityField.getText().trim();

            if (quantityStr.isEmpty()) {
                JOptionPane.showMessageDialog(menuDialog, "Vui lòng nhập số lượng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int quantity = Integer.parseInt(quantityStr);
                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(menuDialog, "Số lượng phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (quantity > availableQuantity) {
                    JOptionPane.showMessageDialog(menuDialog, "Số lượng đặt vượt quá số lượng trong kho cho " + name + "!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int newQuantity = availableQuantity - quantity;
                if (dbHelper.updateMenuItemQuantity(name, newQuantity)) {
                    String formattedPrice = String.format("%.0f VND", price);
                    String orderDetail = quantity + "x " + name + ": " + formattedPrice;
                    double orderPrice = price * quantity;
                    info.orders.put(orderDetail, orderPrice);
                    info.orderTotal += orderPrice;
                    dbHelper.saveOrder(tableNumber, orderDetail, orderPrice);
                    updateTableDetailsPanel(tableNumber);
                    quantityField.setText("");
                    loadMenuItemsToTable(menuModel, (String) typeComboBox.getSelectedItem());
                } else {
                    JOptionPane.showMessageDialog(menuDialog, "Không thể cập nhật số lượng trong kho cho " + name, "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(menuDialog, "Số lượng không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> menuDialog.dispose());

        menuDialog.add(typePanel, BorderLayout.WEST);
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(Color.WHITE);
        southPanel.add(quantityPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        menuDialog.add(menuScrollPane, BorderLayout.CENTER);
        menuDialog.add(southPanel, BorderLayout.SOUTH);

        menuDialog.setVisible(true);
    }

    // Hàm hiển thị danh sách món ăn theo tùy chọn bảng
    private void loadMenuItemsToTable(DefaultTableModel model, String type) {
        model.setRowCount(0);
        List<MenuItem> filteredItems = getMenuItemsByType(type);
        for (MenuItem item : filteredItems) {
            int currentQuantity = dbHelper.getMenuItemQuantity(item.getName());
            if (currentQuantity != -1) {
                double price = item.getPrice();
                if (price < 1000) price *= 1000;
                model.addRow(new Object[]{
                    item.getName(),
                    String.format("%.0f VND", price),
                    String.valueOf(currentQuantity)
                });
            }
        }
    }

    // Hàm lọc món theo loại
    private List<MenuItem> getMenuItemsByType(String type) {
        List<MenuItem> allItems = dbHelper.fetchMenuItems();
        List<MenuItem> filteredItems = new ArrayList<>();
        for (MenuItem item : allItems) {
            if (item.getType().equalsIgnoreCase(type)) {
                filteredItems.add(item);
            }
        }
        return filteredItems;
    }

    // Hàm chuyển đổi chuỗi giá thành số
    private double parsePrice(String priceString) {
        String numericPrice = priceString.replace(" VND", "");
        return Double.parseDouble(numericPrice);
    }

    // Hàm đóng bàn
    private void closeTableManager(int tableNumber) {
        TableInfo info = tableInfoMap.remove(tableNumber);
        if (info != null) {
            info.stopTimer();
            tableButtonsMap.get(tableNumber).setText("<html><center>Bàn " + tableNumber + "<br>Trống</center></html>");
            tableButtonsMap.get(tableNumber).setBackground(new Color(76, 175, 80));
            dbHelper.clearInvoiceInfo(tableNumber);
            updateTableDetailsPanel(tableNumber);
        }
    }

    // Hàm tính chi phí chơi dựa trên thời gian
    public double calculatePlayCostForTable(int tableNumber) {
        TableInfo info = tableInfoMap.get(tableNumber);
        if (info != null) {
            return info.calculatePlayCost(PRICE_PER_MINUTE);
        }
        return 0.0;
    }

    // Hàm cập nhật lại panel chứa bàn
    private void updateTablesPanel() {
        JPanel tablesPanel = createTablesPanel();
        getContentPane().removeAll();
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.add(createControlPanel(currentUser), BorderLayout.NORTH);
        mainPanel.add(tablesPanel, BorderLayout.CENTER);
        mainPanel.add(tableDetailsPanel, BorderLayout.EAST);
        add(mainPanel);
        revalidate();
        repaint();
    }

    // Hàm hiển thị dialog thêm bàn
    private void showAddTableDialog() {
        JDialog addDialog = new JDialog(this, "Thêm Bàn Mới", true);
        addDialog.setSize(300, 150);
        addDialog.setLocationRelativeTo(this);
        addDialog.setLayout(new BorderLayout(10, 10));

        JPanel inputPanel = new JPanel(new FlowLayout());
        JLabel label = new JLabel("Số lượng bàn muốn thêm:");
        JTextField quantityField = new JTextField(5);
        inputPanel.add(label);
        inputPanel.add(quantityField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Thêm");
        JButton cancelButton = new JButton("Hủy");

        addButton.addActionListener(e -> {
            try {
                int quantity = Integer.parseInt(quantityField.getText().trim());
                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(addDialog, "Số lượng phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int maxTableNumber = dbHelper.getAllTables().stream()
                    .mapToInt(DatabaseHelper.Table::getTableNumber)
                    .max().orElse(0);
                for (int i = 1; i <= quantity; i++) {
                    dbHelper.addTable(maxTableNumber + i);
                }
                updateTablesPanel();
                addDialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(addDialog, "Vui lòng nhập số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> addDialog.dispose());

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        addDialog.add(inputPanel, BorderLayout.CENTER);
        addDialog.add(buttonPanel, BorderLayout.SOUTH);
        addDialog.setVisible(true);
    }

    // Hàm hiển thị dialog xóa bàn
    private void showRemoveTableDialog() {
        JDialog removeDialog = new JDialog(this, "Xóa Bàn", true);
        removeDialog.setSize(300, 150);
        removeDialog.setLocationRelativeTo(this);
        removeDialog.setLayout(new BorderLayout(10, 10));

        JPanel inputPanel = new JPanel(new FlowLayout());
        JLabel label = new JLabel("Nhập số bàn muốn xóa:");
        JTextField tableField = new JTextField(5);
        inputPanel.add(label);
        inputPanel.add(tableField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton removeButton = new JButton("Xóa");
        JButton cancelButton = new JButton("Hủy");

        removeButton.addActionListener(e -> {
            try {
                int tableNumber = Integer.parseInt(tableField.getText().trim());
                List<DatabaseHelper.Table> tables = dbHelper.getAllTables();
                if (tableNumber <= 0 || tables.stream().noneMatch(t -> t.getTableNumber() == tableNumber)) {
                    JOptionPane.showMessageDialog(removeDialog, "Số bàn không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (tableInfoMap.containsKey(tableNumber)) {
                    JOptionPane.showMessageDialog(removeDialog, "Không thể xóa bàn đang sử dụng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (tables.size() == 1) {
                    JOptionPane.showMessageDialog(removeDialog, "Phải giữ lại ít nhất 1 bàn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                dbHelper.removeTable(tableNumber);
                tableButtonsMap.remove(tableNumber);
                openTableButtonsMap.remove(tableNumber);
                updateTablesPanel();
                removeDialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(removeDialog, "Vui lòng nhập số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> removeDialog.dispose());

        removeDialog.add(inputPanel, BorderLayout.CENTER);
        removeDialog.add(buttonPanel, BorderLayout.SOUTH);
        removeDialog.setVisible(true);
    }

    public static class MenuItem {
        private String name;
        private double price;
        private String type;

        public MenuItem(String name, double price, String type) {
            this.name = name;
            this.price = price;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }

        public String getType() {
            return type;
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        User currentUser = new User("admin", "admin123", true, false);
        if (currentUser.isLocked()) {
            JOptionPane.showMessageDialog(null, "Tài khoản của bạn đã bị khóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } else {
            new MainPage(currentUser);
        }
    }
}