package Model;

import java.time.LocalDateTime;

public class Reservation {
    private int reservationId;
    private int customerId;
    private int carId;
    private String method; // Phone or Walk-in
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int totalDays;
    private String paymentMethod; // Cash or Credit_Card
    private int totalAmount;

    // Constructor
    public Reservation(int reservationId, int customerId, int carId, String method, LocalDateTime startTime, LocalDateTime endTime, int totalDays, String paymentMethod, int totalAmount) {
        this.reservationId = reservationId;
        this.customerId = customerId;
        this.carId = carId;
        this.method = method;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalDays = totalDays;
        this.paymentMethod = paymentMethod;
        this.totalAmount = totalAmount;
    }
    
    // TODO: Right-click -> Source -> Generate Getters and Setters...
}