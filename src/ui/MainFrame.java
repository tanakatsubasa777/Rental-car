package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableModel;

import util.DBConnection;

/**
 * 3.2 受付スタッフ機能 ＆ 3.3 管理者機能 統合システム
 * 【ブラック・ラグジュアリーデザイン 視認性修正版】
 */
public class MainFrame extends JFrame {
    
    private JTabbedPane tabbedPane;
    private DefaultTableModel carTableModel, staffTableModel, dashboardTableModel, adminCarTableModel;
    private JTable carTable, staffTable, dashboardTable, adminCarTable;
    
    private JLabel lblCarImage;
    
    // 貸出用フィールド
    private JTextField txtName, txtBirth, txtPhone, txtLicenseNum, txtLicenseDate, txtDeposit, txtPreDamage, txtStartTime; 
    private JComboBox<String> comboTypeFilter;
    
    // 返却用フィールド
    private JTextField txtReturnResId, txtReturnTime, txtOvertime;
    private JComboBox<String> comboPostDamage; 
    private JTextField txtDamageMemo, txtPenaltyFee;

    // 管理者用フィールド
    private JTextField txtCarMaker, txtCarModel, txtCarNum, txtCarType, txtCarFee;
    private JTextField txtEditCarId, txtEditCarNum, txtEditCarFee;
    private JTextField txtStaffName, txtStaffLogin, txtStaffPass;
    private JComboBox<String> comboRole;

    public MainFrame() {
        setGlobalFont(new FontUIResource("SansSerif", Font.PLAIN, 15));
        
        // ★ダークテーマの適用
        applyDarkTheme();

        setTitle("高級車専門 レンタカー予約管理システム - Luxury Black Edition");
        setSize(1250, 950);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(30, 30, 30));

        tabbedPane = new JTabbedPane();
        
        tabbedPane.addTab("【受付】貸出・返却業務", createStaffTab());
        tabbedPane.addTab("【管理】車両情報", createAdminCarTab());
        tabbedPane.addTab("【管理】スタッフ情報", createAdminStaffTab());
        tabbedPane.addTab("【管理】売上・稼働状況", createDashboardTab());

        add(tabbedPane);
        
        tabbedPane.addChangeListener(e -> refreshAllData());
        refreshAllData();
    }

    private void setGlobalFont(FontUIResource font) {
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, font);
            }
        }
    }

    /**
     * ★真っ白になるバグを修正したダークテーマ設定
     */
    private void applyDarkTheme() {
        Color darkBg = new Color(30, 30, 30);       // 背景（黒）
        Color componentBg = new Color(70, 70, 70);  // 入力欄の背景（少し明るいグレーで見やすく）
        Color textColor = Color.WHITE;              // 文字色（純白）
        Color accentGold = new Color(212, 175, 55); // ゴールド

        UIManager.put("Panel.background", darkBg);
        UIManager.put("Label.foreground", textColor);
        
        // 入力ボックスの色
        UIManager.put("TextField.background", componentBg);
        UIManager.put("TextField.foreground", textColor);
        UIManager.put("TextField.caretForeground", Color.WHITE);
        UIManager.put("ComboBox.background", componentBg);
        UIManager.put("ComboBox.foreground", textColor);
        
        // ★ここが原因でした！タブの色を強制的にダークに設定
        UIManager.put("TabbedPane.background", new Color(50, 50, 50));
        UIManager.put("TabbedPane.foreground", textColor);
        UIManager.put("TabbedPane.selected", new Color(90, 90, 90));
        UIManager.put("TabbedPane.contentAreaColor", darkBg);
        
        // テーブルの色
        UIManager.put("Table.background", new Color(40, 40, 40));
        UIManager.put("Table.foreground", textColor);
        UIManager.put("Table.gridColor", new Color(100, 100, 100));
        UIManager.put("Table.selectionBackground", accentGold);
        UIManager.put("Table.selectionForeground", Color.BLACK);
        UIManager.put("TableHeader.background", new Color(20, 20, 20));
        UIManager.put("TableHeader.foreground", accentGold);
        
        UIManager.put("TitledBorder.titleColor", accentGold);
        UIManager.put("OptionPane.background", darkBg);
        UIManager.put("OptionPane.messageForeground", textColor);
    }

    // =========================================================
    // 1. 受付スタッフ機能
    // =========================================================
    private JPanel createStaffTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("1. 空車検索"));
        comboTypeFilter = new JComboBox<>(new String[]{"ALL", "Hypercar", "Sports", "SUV", "Sedan"});
        searchPanel.add(new JLabel("クラス:")); searchPanel.add(comboTypeFilter);
        JButton btnSearch = new JButton("検索");
        styleButton(btnSearch, new Color(100, 100, 100)); 
        btnSearch.addActionListener(e -> loadCarsToTable((String) comboTypeFilter.getSelectedItem()));
        searchPanel.add(btnSearch);
        panel.add(searchPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 0));
        String[] cols = {"ID", "メーカー", "モデル", "ナンバー", "クラス", "1日料金", "状態"};
        carTableModel = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        carTable = new JTable(carTableModel); carTable.setRowHeight(30);
        
        JScrollPane scrollPane = new JScrollPane(carTable);
        scrollPane.getViewport().setBackground(new Color(30,30,30));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBorder(BorderFactory.createTitledBorder("車両写真"));
        imagePanel.setPreferredSize(new Dimension(380, 0)); 
        
        lblCarImage = new JLabel("※表から車をクリックすると写真が出ます", SwingConstants.CENTER);
        lblCarImage.setForeground(new Color(150, 150, 150));
        imagePanel.add(lblCarImage, BorderLayout.CENTER);
        centerPanel.add(imagePanel, BorderLayout.EAST);
        
        panel.add(centerPanel, BorderLayout.CENTER);

        carTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && carTable.getSelectedRow() != -1) {
                int carId = (int) carTableModel.getValueAt(carTable.getSelectedRow(), 0);
                showCarImage(carId);
            }
        });

        JPanel southPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        
        JPanel bookingPanel = new JPanel(new GridLayout(5, 4, 5, 5));
        bookingPanel.setBorder(BorderFactory.createTitledBorder("2. 予約受付・顧客審査・貸出"));
        bookingPanel.add(new JLabel(" 氏名:")); bookingPanel.add(txtName = new JTextField("山田 太郎"));
        bookingPanel.add(new JLabel(" 生年月日(YYYY-MM-DD):")); bookingPanel.add(txtBirth = new JTextField("1995-05-15"));
        bookingPanel.add(new JLabel(" 免許証番号:")); bookingPanel.add(txtLicenseNum = new JTextField("123456789012"));
        bookingPanel.add(new JLabel(" 免許取得日:")); bookingPanel.add(txtLicenseDate = new JTextField("2018-04-01"));
        bookingPanel.add(new JLabel(" 電話番号:")); bookingPanel.add(txtPhone = new JTextField("090-1234-5678"));
        bookingPanel.add(new JLabel(" 貸出前点検(傷など):")); bookingPanel.add(txtPreDamage = new JTextField("特になし"));
        
        String defaultStartTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        bookingPanel.add(new JLabel(" 貸出日時:")); bookingPanel.add(txtStartTime = new JTextField(defaultStartTime));
        bookingPanel.add(new JLabel(" デポジット現金:")); bookingPanel.add(txtDeposit = new JTextField("100000"));
        
        JButton btnBook = new JButton("審査＆貸出実行");
        styleButton(btnBook, new Color(41, 128, 185));
        btnBook.addActionListener(e -> handleBooking());
        bookingPanel.add(new JLabel()); bookingPanel.add(btnBook);
        southPanel.add(bookingPanel);

        JPanel returnPanel = new JPanel(new GridLayout(4, 4, 5, 5));
        returnPanel.setBorder(BorderFactory.createTitledBorder("3. 車両返却・事後点検・自動精算"));
        
        returnPanel.add(new JLabel(" 予約ID:")); returnPanel.add(txtReturnResId = new JTextField());
        String testReturnTime = LocalDateTime.now().plusHours(26).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        returnPanel.add(new JLabel(" 実返却日時:")); returnPanel.add(txtReturnTime = new JTextField(testReturnTime));
        returnPanel.add(new JLabel(" 超過時間(h):")); returnPanel.add(txtOvertime = new JTextField("0"));
        
        returnPanel.add(new JLabel(" 事後点検(新たな傷):"));
        comboPostDamage = new JComboBox<>(new String[]{"特になし", "あり"});
        returnPanel.add(comboPostDamage);
        
        returnPanel.add(new JLabel(" ダメージメモ:")); txtDamageMemo = new JTextField(); txtDamageMemo.setEnabled(false);
        returnPanel.add(txtDamageMemo);
        returnPanel.add(new JLabel(" 罰金(円):")); txtPenaltyFee = new JTextField("0"); txtPenaltyFee.setEnabled(false);
        returnPanel.add(txtPenaltyFee);
        
        comboPostDamage.addActionListener(e -> {
            boolean isDamaged = "あり".equals(comboPostDamage.getSelectedItem());
            txtDamageMemo.setEnabled(isDamaged); txtPenaltyFee.setEnabled(isDamaged);
            if (!isDamaged) { txtDamageMemo.setText(""); txtPenaltyFee.setText("0"); }
        });

        JButton btnReturn = new JButton("精算＆返却実行");
        styleButton(btnReturn, new Color(39, 174, 96));
        btnReturn.addActionListener(e -> handleReturn());
        returnPanel.add(new JLabel()); returnPanel.add(btnReturn);
        
        southPanel.add(returnPanel);
        panel.add(southPanel, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * ボタンの背景が白くならないようにする処理
     */
    private void styleButton(JButton btn, Color bgColor) {
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        btn.setFont(new Font("SansSerif", Font.BOLD, 15));
        btn.setOpaque(true); // Windows環境でも背景色を強制する
    }

    private void showCarImage(int carId) {
        String imagePath = "images/car_" + carId + ".jpg"; 
        File file = new File(imagePath);
        
        if (file.exists()) {
            ImageIcon icon = new ImageIcon(imagePath);
            Image img = icon.getImage();
            Image scaledImg = img.getScaledInstance(350, 230, Image.SCALE_SMOOTH);
            lblCarImage.setIcon(new ImageIcon(scaledImg));
            lblCarImage.setText(""); 
        } else {
            lblCarImage.setIcon(null); 
            lblCarImage.setText("画像なし (images/car_" + carId + ".jpg)");
        }
    }

    // =========================================================
    // 貸出＆返却のロジック処理
    // =========================================================

    private void handleBooking() {
        int row = carTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "貸出車両を選択してください。"); return; }
        
        try (Connection conn = DBConnection.getConnection()) {
            LocalDate birth = LocalDate.parse(txtBirth.getText());
            LocalDate license = LocalDate.parse(txtLicenseDate.getText());
            LocalDate today = LocalDate.now();
            
            if (Period.between(birth, today).getYears() < 25) {
                JOptionPane.showMessageDialog(this, "【予約拒否】25歳未満のお客様への貸出は禁止されています。", "審査エラー", JOptionPane.ERROR_MESSAGE); return;
            }
            if (Period.between(license, today).getYears() < 1) {
                JOptionPane.showMessageDialog(this, "【予約拒否】免許取得から1年未満のお客様への貸出は禁止されています。", "審査エラー", JOptionPane.ERROR_MESSAGE); return;
            }

            int deposit = Integer.parseInt(txtDeposit.getText().replace(",", ""));
            if (deposit < 10000) { JOptionPane.showMessageDialog(this, "【予約拒否】デポジットは最低 10,000円 以上必要です。", "入力エラー", JOptionPane.WARNING_MESSAGE); return; }

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime startDatetime = LocalDateTime.parse(txtStartTime.getText(), dtf);

            PreparedStatement psCust = conn.prepareStatement("INSERT INTO Customers (Name, Birth_Date, Phone, License_Number, License_Date) VALUES (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            psCust.setString(1, txtName.getText()); psCust.setString(2, birth.toString()); psCust.setString(3, txtPhone.getText()); psCust.setString(4, txtLicenseNum.getText()); psCust.setString(5, license.toString());
            psCust.executeUpdate();
            ResultSet rsCust = psCust.getGeneratedKeys(); rsCust.next(); int custId = rsCust.getInt(1);

            int carId = (int) carTableModel.getValueAt(row, 0);
            
            PreparedStatement psRes = conn.prepareStatement("INSERT INTO Reservations (Customer_ID, Car_ID, Staff_ID, Start_Datetime, Deposit, Status) VALUES (?,?,1,?,?, 'Rented')", Statement.RETURN_GENERATED_KEYS);
            psRes.setInt(1, custId); psRes.setInt(2, carId); psRes.setTimestamp(3, Timestamp.valueOf(startDatetime)); psRes.setInt(4, deposit);
            psRes.executeUpdate();
            ResultSet rsRes = psRes.getGeneratedKeys(); rsRes.next(); int resId = rsRes.getInt(1);

            PreparedStatement psInsp = conn.prepareStatement("INSERT INTO Inspections (Reservation_ID, Timing, Damage_Record) VALUES (?, 'Pre-Rental', ?)");
            psInsp.setInt(1, resId); psInsp.setString(2, txtPreDamage.getText()); psInsp.executeUpdate();

            conn.prepareStatement("UPDATE Cars SET Car_Status = 'Rented' WHERE Car_ID = " + carId).executeUpdate();

            JOptionPane.showMessageDialog(this, "【貸出完了】\n予約ID: " + resId + "\n貸出日時: " + startDatetime.format(dtf) + "\nステータスを「Rented」に更新しました。");
            refreshAllData();
        } catch (DateTimeParseException ex) { JOptionPane.showMessageDialog(this, "日時の形式が間違っています。(例: 2026-07-09 10:00)", "エラー", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "入力エラー: " + e.getMessage(), "エラー", JOptionPane.ERROR_MESSAGE); }
    }

    private void handleReturn() {
        try (Connection conn = DBConnection.getConnection()) {
            int resId = Integer.parseInt(txtReturnResId.getText());
            
            String fetchSql = "SELECT r.Start_Datetime, r.Car_ID, r.Deposit, c.Car_Fee, c.Car_Model FROM Reservations r JOIN Cars c ON r.Car_ID = c.Car_ID WHERE r.Reservation_ID = ? AND r.Status = 'Rented'";
            PreparedStatement psGetRes = conn.prepareStatement(fetchSql);
            psGetRes.setInt(1, resId);
            ResultSet rs = psGetRes.executeQuery();
            if (!rs.next()) { JOptionPane.showMessageDialog(this, "有効な貸出中データが見つかりません。"); return; }
            
            LocalDateTime startDatetime = rs.getTimestamp("Start_Datetime").toLocalDateTime();
            int carId = rs.getInt("Car_ID"); int deposit = rs.getInt("Deposit"); int carFee = rs.getInt("Car_Fee"); String model = rs.getString("Car_Model");

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime returnDatetime = LocalDateTime.parse(txtReturnTime.getText(), dtf);
            Duration duration = Duration.between(startDatetime, returnDatetime);
            long totalHours = duration.toHours();
            if(totalHours < 0) { JOptionPane.showMessageDialog(this, "返却日時が貸出日時より過去です！"); return; }

            long overtimeHours = Math.max(0, totalHours - 24);
            int overtimeFee = (int) (overtimeHours * 10000);
            
            int penaltyFee = 0;
            String damageRecord = "特になし";
            if ("あり".equals(comboPostDamage.getSelectedItem())) {
                penaltyFee = Integer.parseInt(txtPenaltyFee.getText().replace(",", ""));
                damageRecord = txtDamageMemo.getText();
            }
            
            int totalCost = carFee + overtimeFee + penaltyFee; 
            int settlement = totalCost - deposit; 
            String settlementText = (settlement > 0) ? "【追加請求額】: ¥" + String.format("%,d", settlement) : "【ご返金額】: ¥" + String.format("%,d", Math.abs(settlement));

            PreparedStatement psInsp = conn.prepareStatement("INSERT INTO Inspections (Reservation_ID, Timing, Damage_Record) VALUES (?, 'Post-Rental', ?)");
            psInsp.setInt(1, resId); psInsp.setString(2, damageRecord); psInsp.executeUpdate();

            PreparedStatement psRes = conn.prepareStatement("UPDATE Reservations SET Return_Datetime = ?, Overtime_Fee = ?, Penalty_Fee = ?, Status = 'Returned' WHERE Reservation_ID = ?");
            psRes.setTimestamp(1, Timestamp.valueOf(returnDatetime)); psRes.setInt(2, overtimeFee); psRes.setInt(3, penaltyFee); psRes.setInt(4, resId); psRes.executeUpdate();

            conn.prepareStatement("UPDATE Cars SET Car_Status = 'Available' WHERE Car_ID = " + carId).executeUpdate();

            String receipt = String.format(
                "■ ご返却・精算レシート ■\n\n貸出日時: %s\n返却日時: %s\nご利用時間: 計 %d 時間\n-----------------------------------\n1日基本料金(24h): ¥%,d\n超過時間(%d時間): ¥%,d\n罰金(ペナルティ): ¥%,d\n-----------------------------------\n合計利用額: ¥%,d\nお預かり済デポジット: ¥%,d\n-----------------------------------\n%s\n\n車両ステータスを「Available (空車)」に復旧しました。",
                startDatetime.format(dtf), returnDatetime.format(dtf), totalHours, carFee, overtimeHours, overtimeFee, penaltyFee, totalCost, deposit, settlementText
            );
            JOptionPane.showMessageDialog(this, receipt, "精算・返却完了", JOptionPane.INFORMATION_MESSAGE);
            refreshAllData();

        } catch (DateTimeParseException ex) { JOptionPane.showMessageDialog(this, "日時の形式が間違っています。", "エラー", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "罰金は半角数字で入力してください。", "エラー", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "エラー: " + e.getMessage(), "エラー", JOptionPane.ERROR_MESSAGE); }
    }

    // =========================================================
    // 2. 管理者タブの構築
    // =========================================================
    private JPanel createAdminCarTab() {
        JPanel panel = new JPanel(new BorderLayout(10,10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addPanel.setBorder(BorderFactory.createTitledBorder("新規車両の登録"));
        addPanel.add(new JLabel("メーカー:")); addPanel.add(txtCarMaker = new JTextField(7));
        addPanel.add(new JLabel("モデル:")); addPanel.add(txtCarModel = new JTextField(10));
        addPanel.add(new JLabel("ナンバー:")); addPanel.add(txtCarNum = new JTextField(8));
        addPanel.add(new JLabel("クラス:")); addPanel.add(txtCarType = new JTextField(7));
        addPanel.add(new JLabel("1日料金:")); addPanel.add(txtCarFee = new JTextField(7));
        JButton btnAddCar = new JButton("登録"); styleButton(btnAddCar, new Color(100,100,100)); btnAddCar.addActionListener(e -> addCar()); addPanel.add(btnAddCar);
        
        String[] cols = {"ID", "メーカー", "モデル", "ナンバー", "クラス", "1日料金", "状態"};
        adminCarTableModel = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        adminCarTable = new JTable(adminCarTableModel); adminCarTable.setRowHeight(30);
        
        JScrollPane scrollPane = new JScrollPane(adminCarTable);
        scrollPane.getViewport().setBackground(new Color(30,30,30));
        
        adminCarTable.getSelectionModel().addListSelectionListener(e -> {
            int row = adminCarTable.getSelectedRow();
            if(row != -1) {
                txtEditCarId.setText(adminCarTableModel.getValueAt(row, 0).toString());
                txtEditCarNum.setText(adminCarTableModel.getValueAt(row, 3).toString());
                txtEditCarFee.setText(adminCarTableModel.getValueAt(row, 5).toString());
            }
        });
        
        JPanel editPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        editPanel.setBorder(BorderFactory.createTitledBorder("登録済み車両の更新 (上のテーブルから選択)"));
        editPanel.add(new JLabel("車両ID:")); editPanel.add(txtEditCarId = new JTextField(3)); txtEditCarId.setEditable(false);
        editPanel.add(new JLabel("ナンバー変更:")); editPanel.add(txtEditCarNum = new JTextField(10));
        editPanel.add(new JLabel("料金変更:")); editPanel.add(txtEditCarFee = new JTextField(7));
        JButton btnUpdateCar = new JButton("情報を更新する"); styleButton(btnUpdateCar, new Color(100,100,100)); btnUpdateCar.addActionListener(e -> updateCar()); editPanel.add(btnUpdateCar);

        panel.add(addPanel, BorderLayout.NORTH); panel.add(scrollPane, BorderLayout.CENTER); panel.add(editPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createAdminStaffTab() {
        JPanel panel = new JPanel(new BorderLayout(10,10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"スタッフID", "氏名", "ログインID", "役職"};
        staffTableModel = new DefaultTableModel(cols, 0); staffTable = new JTable(staffTableModel); staffTable.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(staffTable); scrollPane.getViewport().setBackground(new Color(30,30,30));
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addPanel.setBorder(BorderFactory.createTitledBorder("新規スタッフ登録"));
        addPanel.add(new JLabel("氏名:")); addPanel.add(txtStaffName = new JTextField(7));
        addPanel.add(new JLabel("ログインID:")); addPanel.add(txtStaffLogin = new JTextField(7));
        addPanel.add(new JLabel("パスワード:")); addPanel.add(txtStaffPass = new JTextField(7));
        addPanel.add(new JLabel("役職:")); comboRole = new JComboBox<>(new String[]{"Clerk", "Manager"}); addPanel.add(comboRole);
        JButton btnAddStaff = new JButton("登録"); styleButton(btnAddStaff, new Color(100,100,100)); btnAddStaff.addActionListener(e -> addStaff()); addPanel.add(btnAddStaff);
        panel.add(addPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createDashboardTab() {
        JPanel panel = new JPanel(new BorderLayout(10,10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"予約ID", "顧客名", "車両モデル", "ステータス", "デポジット", "超過料金", "ペナルティ(罰金)"};
        dashboardTableModel = new DefaultTableModel(cols, 0); dashboardTable = new JTable(dashboardTableModel); dashboardTable.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(dashboardTable); scrollPane.getViewport().setBackground(new Color(30,30,30));
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton btnRefresh = new JButton("売上・稼働状況を更新"); styleButton(btnRefresh, new Color(100,100,100)); btnRefresh.addActionListener(e -> loadDashboard());
        panel.add(btnRefresh, BorderLayout.NORTH);
        return panel;
    }

    private void refreshAllData() {
        loadCarsToTable("ALL"); loadAdminCars(); loadStaffs(); loadDashboard();
    }

    private void loadCarsToTable(String filter) {
        carTableModel.setRowCount(0);
        String sql = "SELECT * FROM Cars WHERE Car_Status = 'Available'";
        if (!"ALL".equals(filter)) sql += " AND Car_Type = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (!"ALL".equals(filter)) pstmt.setString(1, filter);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) { carTableModel.addRow(new Object[]{ rs.getInt("Car_ID"), rs.getString("Car_Maker"), rs.getString("Car_Model"), rs.getString("Car_Number"), rs.getString("Car_Type"), rs.getInt("Car_Fee"), rs.getString("Car_Status") }); }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void loadAdminCars() {
        adminCarTableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM Cars")) {
            while (rs.next()) { adminCarTableModel.addRow(new Object[]{ rs.getInt("Car_ID"), rs.getString("Car_Maker"), rs.getString("Car_Model"), rs.getString("Car_Number"), rs.getString("Car_Type"), rs.getInt("Car_Fee"), rs.getString("Car_Status") }); }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void addCar() {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO Cars (Car_Maker, Car_Model, Car_Number, Car_Type, Car_Fee) VALUES (?,?,?,?,?)");
            ps.setString(1, txtCarMaker.getText()); ps.setString(2, txtCarModel.getText()); ps.setString(3, txtCarNum.getText()); ps.setString(4, txtCarType.getText()); ps.setInt(5, Integer.parseInt(txtCarFee.getText()));
            ps.executeUpdate(); JOptionPane.showMessageDialog(this, "車両を登録しました。"); refreshAllData();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void updateCar() {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("UPDATE Cars SET Car_Number = ?, Car_Fee = ? WHERE Car_ID = ?");
            ps.setString(1, txtEditCarNum.getText()); ps.setInt(2, Integer.parseInt(txtEditCarFee.getText())); ps.setInt(3, Integer.parseInt(txtEditCarId.getText()));
            ps.executeUpdate(); JOptionPane.showMessageDialog(this, "車両情報を更新しました！"); refreshAllData();
        } catch (Exception e) {}
    }

    private void addStaff() {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO Staffs (Staff_Name, Login_ID, Password, Role) VALUES (?,?,?,?)");
            ps.setString(1, txtStaffName.getText()); ps.setString(2, txtStaffLogin.getText()); ps.setString(3, txtStaffPass.getText()); ps.setString(4, (String)comboRole.getSelectedItem());
            ps.executeUpdate(); JOptionPane.showMessageDialog(this, "スタッフを登録しました。"); refreshAllData();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadStaffs() {
        staffTableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM Staffs")) {
            while (rs.next()) staffTableModel.addRow(new Object[]{rs.getInt("Staff_ID"), rs.getString("Staff_Name"), rs.getString("Login_ID"), rs.getString("Role")});
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadDashboard() {
        dashboardTableModel.setRowCount(0);
        String sql = "SELECT r.Reservation_ID, c.Name, ca.Car_Model, r.Status, r.Deposit, r.Overtime_Fee, r.Penalty_Fee " + 
                     "FROM Reservations r JOIN Customers c ON r.Customer_ID = c.Customer_ID " + 
                     "JOIN Cars ca ON r.Car_ID = ca.Car_ID ORDER BY r.Reservation_ID DESC";
        try (Connection conn = DBConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                dashboardTableModel.addRow(new Object[]{ 
                    rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5), rs.getInt(6), rs.getInt(7) 
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void main(String[] args) {
        // ★真っ白になる原因だった SystemLookAndFeel を解除し、
        // どのPC環境でも同じ「クロスプラットフォーム」デザインを使用するように変更
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}