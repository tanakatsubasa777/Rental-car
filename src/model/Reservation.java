package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 予約・貸出情報を保持するモデルクラス
 */
public class Reservation {
    private int reservationId;
    private int customerId;
    private int carId;
    private int staffId;
    private LocalDateTime startDatetime;
    private LocalDateTime returnDatetime;
    private BigDecimal deposit;
    private String status; // Reserved(予約済), Rented(貸出中), Returned(返却済)

    // 新規予約作成用のコンストラクタ
    public Reservation(int customerId, int carId, int staffId, LocalDateTime startDatetime, LocalDateTime returnDatetime, BigDecimal deposit, String status) {
        this.customerId = customerId;
        this.carId = carId;
        this.staffId = staffId;
        this.startDatetime = startDatetime;
        this.returnDatetime = returnDatetime;
        this.deposit = deposit;
        this.status = status;
    }

    // 【研修ポイント】 getter と setter 
    // カプセル化の原則に従い、フィールドはprivateにし、外部からは以下のメソッド経由でアクセスさせます。
    public int getReservationId() { return reservationId; }
    public void setReservationId(int reservationId) { this.reservationId = reservationId; }
    public int getCustomerId() { return customerId; }
    public int getCarId() { return carId; }
    public int getStaffId() { return staffId; }
    public LocalDateTime getStartDatetime() { return startDatetime; }
    public LocalDateTime getReturnDatetime() { return returnDatetime; }
    public BigDecimal getDeposit() { return deposit; }
    public String getStatus() { return status; }
}