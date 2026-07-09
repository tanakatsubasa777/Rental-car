package dao;
 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Car;
import util.DBConnection;
 
public class CarDAO {
 
    /**
     * 空車（Available）および指定したクラスの車両を検索します。(TC-005)
     */
    public List<Car> findAvailableCarsByType(String carType) throws SQLException {
        List<Car> list = new ArrayList<>();
        // 【研修ポイント】SQLインジェクションを防ぐため、変数を直接文字列結合せず "?" を使います。
        String sql = "SELECT * FROM Cars WHERE Car_Status = 'Available' AND Car_Exist = TRUE";
        if (carType != null && !carType.isEmpty() && !"ALL".equalsIgnoreCase(carType)) {
            sql += " AND Car_Type = ?";
        }
 
        // 【研修ポイント】Try-with-resources構文を使うと、ConnectionやPreparedStatementが自動でクローズされます。
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (carType != null && !carType.isEmpty() && !"ALL".equalsIgnoreCase(carType)) {
                pstmt.setString(1, carType); // "?" に値を安全にセット
            }
 
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Car car = new Car(
                        rs.getInt("Car_ID"),
                        rs.getString("Car_Number"),
                        rs.getString("Car_Maker"),
                        rs.getString("Car_Type"),
                        rs.getString("Car_Status"),
                        rs.getString("Car_Model"),
                        rs.getInt("Car_Fee")
                    );
                    list.add(car);
                }
            }
        }
        return list;
    }
 
    /**
     * 車両ステータスを更新します。(予約時・貸出時・返却時に使用)
     */
    public boolean updateStatus(int carId, String newStatus) throws SQLException {
        String sql = "UPDATE Cars SET Car_Status = ? WHERE Car_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, carId);
            return pstmt.executeUpdate() > 0;
        }
    }
}