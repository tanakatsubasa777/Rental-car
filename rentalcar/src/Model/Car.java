package Model;

public class Car {
    private int carId;
    private String carName;
    private String licensePlate;
    private String carClass;
    private int dailyRate;
    private String status;

    // Constructor
    public Car(int carId, String carName, String licensePlate, String carClass, int dailyRate, String status) {
        this.carId = carId;
        this.carName = carName;
        this.licensePlate = licensePlate;
        this.carClass = carClass;
        this.dailyRate = dailyRate;
        this.status = status;
    }
    
    // TODO: Right-click -> Source -> Generate Getters and Setters...
}