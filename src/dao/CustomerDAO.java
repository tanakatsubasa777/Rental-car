package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import model.Customer;
import util.DBConnection;

public class CustomerDAO {

    /**
     * 新規顧客をデータベースに登録し、自動生成されたIDをモデルにセットします。(TC-CUS-001)
     */
    public int insert(Customer customer) throws SQLException {
        String sql = "INSERT INTO Customers (Name, Birth_Date, Phone, License_Number, License_Date) VALUES (?, ?, ?, ?, ?)";
        
        // RETURN_GENERATED_KEYS を指定することで、AUTO_INCREMENTで採番されたIDを取得可能にします
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, customer.getName());
            pstmt.setDate(2, Date.valueOf(customer.getBirthDate()));
            pstmt.setString(3, customer.getPhone());
            pstmt.setString(4, customer.getLicenseNumber());
            pstmt.setDate(5, Date.valueOf(customer.getLicenseDate()));
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("顧客登録に失敗しました。");
            }

            // 自動生成されたIDの取得
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int newId = generatedKeys.getInt(1);
                    customer.setCustomerId(newId);
                    return newId;
                } else {
                    throw new SQLException("IDの取得に失敗しました。");
                }
            }
        }
    }
}