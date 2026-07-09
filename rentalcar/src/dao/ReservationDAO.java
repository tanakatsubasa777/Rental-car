package dao;
 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import model.Reservation;
import util.DBConnection;
 
/**
* 予約・貸出管理テーブル(Reservations)へアクセスするDAOクラス
*/
public class ReservationDAO {
 
    /**
     * 新規予約をデータベースに登録します。(テスト仕様: RSV-001)
     * @param reservation 登録する予約オブジェクト
     * @return 成功した場合は自動採番された予約IDを返す
     */
    public int insert(Reservation reservation) throws SQLException {
        // 【研修ポイント】 外部キー(FK)として、顧客ID・車両ID・スタッフIDを保存します。
        // これにより、別のテーブル同士が「リレーション(関係)」を持つことになります。
        String sql = "INSERT INTO Reservations (customer_id, car_id, staff_id, start_datetime, return_datetime, deposit, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, reservation.getCustomerId());
            pstmt.setInt(2, reservation.getCarId());
            pstmt.setInt(3, reservation.getStaffId());
            // 【研修ポイント】 JavaのLocalDateTimeとMySQLのDATETIME型の相互変換
            pstmt.setTimestamp(4, Timestamp.valueOf(reservation.getStartDatetime()));
            pstmt.setTimestamp(5, Timestamp.valueOf(reservation.getReturnDatetime()));
            pstmt.setBigDecimal(6, reservation.getDeposit());
            pstmt.setString(7, reservation.getStatus());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("予約の登録に失敗しました。");
            }
 
            // 発行された予約IDを取得して返す
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("予約IDの取得に失敗しました。");
                }
            }
        }
    }
}