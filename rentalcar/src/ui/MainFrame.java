package ui;

 

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

import dao.CarDAO;
import dao.CustomerDAO;
import dao.ReservationDAO;
import exception.RentalBusinessException;
import model.Car;
import model.Customer;
import model.Reservation;

 

/**
* レンタカー予約管理システムのメインウィンドウ（新入社員研修用GUI）。
* 【追加】車両の返却機能を搭載しました。
*/
public class MainFrame extends JFrame {
    private CarDAO carDAO = new CarDAO();
    private CustomerDAO customerDAO = new CustomerDAO();
    private ReservationDAO reservationDAO = new ReservationDAO();

 

    // UIコンポーネント
    private JComboBox<String> comboTypeFilter;
    private JTable carTable;
    private DefaultTableModel tableModel;

    // 顧客入力フィールド
    private JTextField txtName, txtBirth, txtPhone, txtLicenseNum, txtLicenseDate;

    // 【追加】返却用の入力フィールド
    private JTextField txtReturnCarId;

 

    public MainFrame() {
        setTitle("【高級車専門】レンタカー予約・貸出・返却管理システム");
        setSize(850, 650); // 返却パネルが増えた分、画面を少し縦長にしました
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

 

        // 上部：車両検索パネル
        add(createSearchPanel(), BorderLayout.NORTH);

 

        // 中央：車両一覧テーブル
        add(createTablePanel(), BorderLayout.CENTER);

 

        // 下部：予約パネル と 返却パネル をまとめる
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(createBookingPanel(), BorderLayout.CENTER);
        southPanel.add(createReturnPanel(), BorderLayout.SOUTH); // 【追加】返却パネル
        add(southPanel, BorderLayout.SOUTH);

 

        // 起動時の初期データ読み込み
        loadAvailableCars("ALL");
    }

 

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("1. 空車確認・車両検索"));
        comboTypeFilter = new JComboBox<>(new String[]{"ALL", "Hypercar", "Sports", "SUV", "Sedan"});
        panel.add(new JLabel("クラス:"));
        panel.add(comboTypeFilter);
        JButton btnSearch = new JButton("検索");
        btnSearch.addActionListener(e -> loadAvailableCars((String) comboTypeFilter.getSelectedItem()));
        panel.add(btnSearch);
        return panel;
    }

 

    private JScrollPane createTablePanel() {
        String[] cols = {"ID", "ナンバー", "メーカー", "モデル", "クラス", "1日料金", "状態"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        carTable = new JTable(tableModel);
        carTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return new JScrollPane(carTable);
    }

 

    private JPanel createBookingPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("2. 顧客審査 & 予約受付"));
        JPanel form = new JPanel(new GridLayout(3, 4, 5, 5));
        form.add(new JLabel(" 氏名:")); form.add(txtName = new JTextField("山田 太郎"));
        form.add(new JLabel(" 生年月日(YYYY-MM-DD):")); form.add(txtBirth = new JTextField("1995-05-15"));
        form.add(new JLabel(" 電話番号:")); form.add(txtPhone = new JTextField("090-1234-5678"));
        form.add(new JLabel(" 免許番号:")); form.add(txtLicenseNum = new JTextField("123456789012"));
        form.add(new JLabel(" 免許取得日:")); form.add(txtLicenseDate = new JTextField("2018-04-01"));
        panel.add(form, BorderLayout.CENTER);

 

        JButton btnBook = new JButton("審査実行 ＆ 予約確定");
        btnBook.setBackground(new Color(70, 130, 180));
        btnBook.setForeground(Color.WHITE);
        btnBook.addActionListener(e -> handleBooking());
        panel.add(btnBook, BorderLayout.SOUTH);
        return panel;
    }

 

    /**
     * 【追加】 車両の返却を行うパネルを作成します。
     */
    private JPanel createReturnPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("3. 車両の返却・ステータス復旧"));

 

        panel.add(new JLabel("返却された車両のID:"));
        txtReturnCarId = new JTextField(5);
        panel.add(txtReturnCarId);

 

        JButton btnReturn = new JButton("返却実行 (Availableに戻す)");
        btnReturn.setBackground(new Color(46, 139, 87)); // 少し緑色にして区別
        btnReturn.setForeground(Color.WHITE);
        btnReturn.addActionListener(e -> handleReturn());
        panel.add(btnReturn);

 

        return panel;
    }

 

    private void loadAvailableCars(String type) {
        tableModel.setRowCount(0);
        try {
            List<Car> cars = carDAO.findAvailableCarsByType(type);
            for (Car c : cars) {
                tableModel.addRow(new Object[]{ c.getCarId(), c.getCarNumber(), c.getCarMaker(), c.getCarModel(), c.getCarType(), c.getCarFee(), c.getCarStatus() });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "DBエラー: " + e.getMessage());
        }
    }

 

    private void handleBooking() {
        int row = carTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "予約する車両を選択してください。"); return;
        }
        int carId = (int) tableModel.getValueAt(row, 0);

 

        try {
            LocalDate birth = LocalDate.parse(txtBirth.getText().trim());
            LocalDate licDate = LocalDate.parse(txtLicenseDate.getText().trim());

 

            Customer customer = new Customer(txtName.getText().trim(), birth, txtPhone.getText().trim(), txtLicenseNum.getText().trim(), licDate);
            customer.validateEligibility(); // 審査

 

            int customerId = customerDAO.insert(customer);
            Reservation res = new Reservation(customerId, carId, 1, LocalDateTime.now(), LocalDateTime.now().plusDays(1), new java.math.BigDecimal("50000"), "Rented");
            int resId = reservationDAO.insert(res);
            carDAO.updateStatus(carId, "Rented");

 

            JOptionPane.showMessageDialog(this, "【予約・貸出完了】\n予約ID: " + resId + "\n車両ID: " + carId + "\nステータスを「Rented」にしました。リストから消えます。");
            loadAvailableCars((String) comboTypeFilter.getSelectedItem());
        } catch (RentalBusinessException ex) {
            JOptionPane.showMessageDialog(this, "審査エラー: " + ex.getMessage(), "エラー", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "処理エラー: " + ex.getMessage(), "エラー", JOptionPane.ERROR_MESSAGE);
        }
    }

 

    /**
     * 【追加】 車両の返却処理ロジック
     * 指定された車両IDのステータスを「Available」に更新し、再び貸出可能な状態にします。
     */
    private void handleReturn() {
        String inputId = txtReturnCarId.getText().trim();
        if (inputId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "返却する車両のIDを入力してください。", "入力エラー", JOptionPane.WARNING_MESSAGE);
            return;
        }

 

        try {
            int carId = Integer.parseInt(inputId);

            // データベースの車両ステータスを「Available(空車)」に更新する
            carDAO.updateStatus(carId, "Available");

            JOptionPane.showMessageDialog(this, 
                "【返却完了】\n車両ID: " + carId + " を返却処理しました。\nステータスが「Available(空車)」に戻り、再び予約可能になりました！", 
                "返却完了", JOptionPane.INFORMATION_MESSAGE);

            // 入力欄を空にする
            txtReturnCarId.setText("");

            // 車両一覧を再読み込み（返却した車がリストに復活する）
            loadAvailableCars((String) comboTypeFilter.getSelectedItem());

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "車両IDは半角数字で入力してください。", "入力エラー", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "データベース処理中にエラーが発生しました。\n" + ex.getMessage(), "DBエラー", JOptionPane.ERROR_MESSAGE);
        }
    }

 

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}

