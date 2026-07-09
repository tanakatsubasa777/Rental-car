package model;
 
public class Car {

    private int carId;

    private String carNumber;

    private String carMaker;

    private String carType;

    private String carStatus; // Available, Rented, Maintenance

    private String carModel;

    private int carFee;

    private boolean carExist;
 
    public Car() {}
 
    public Car(int carId, String carNumber, String carMaker, String carType, String carStatus, String carModel, int carFee) {

        this.carId = carId;

        this.carNumber = carNumber;

        this.carMaker = carMaker;

        this.carType = carType;

        this.carStatus = carStatus;

        this.carModel = carModel;

        this.carFee = carFee;

        this.carExist = true;

    }
 
    // --- Getter & Setter ---

    public int getCarId() { return carId; }

    public void setCarId(int carId) { this.carId = carId; }

    public String getCarNumber() { return carNumber; }

    public void setCarNumber(String carNumber) { this.carNumber = carNumber; }

    public String getCarMaker() { return carMaker; }

    public void setCarMaker(String carMaker) { this.carMaker = carMaker; }

    public String getCarType() { return carType; }

    public void setCarType(String carType) { this.carType = carType; }

    public String getCarStatus() { return carStatus; }

    public void setCarStatus(String carStatus) { this.carStatus = carStatus; }

    public String getCarModel() { return carModel; }

    public void setCarModel(String carModel) { this.carModel = carModel; }

    public int getCarFee() { return carFee; }

    public void setCarFee(int carFee) { this.carFee = carFee; }

    public boolean isCarExist() { return carExist; }

    public void setCarExist(boolean carExist) { this.carExist = carExist; }

    @Override

    public String toString() {

        return String.format("[%s] %s %s (1日: ¥%,d) - 状態: %s", 

            carType, carMaker, carModel, carFee, carStatus);

    }

}
