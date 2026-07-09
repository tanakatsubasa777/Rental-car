package Model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Customer {
    private int customerId;
    private String name;
    private LocalDate birthDate;
    private String phone;
    private String licenseNumber;
    private LocalDate licenseDate;

    // Constructor
    public Customer(int customerId, String name, LocalDate birthDate, String phone, String licenseNumber, LocalDate licenseDate) {
        this.customerId = customerId;
        this.name = name;
        this.birthDate = birthDate;
        this.phone = phone;
        this.licenseNumber = licenseNumber;
        this.licenseDate = licenseDate;
    }

    // Validation Methods
    public int calculateAge() {
        return (int) ChronoUnit.YEARS.between(this.birthDate, LocalDate.now());
    }

    public int calculateDrivingExperienceDays() {
        return (int) ChronoUnit.DAYS.between(this.licenseDate, LocalDate.now());
    }

    public boolean isEligible() {
        if (calculateAge() < 25) {
            System.out.println("エラー: 25歳未満のため貸出不可"); 
            return false;
        }
        if (calculateDrivingExperienceDays() < 365) {
            System.out.println("エラー: 免許取得1年未満のため貸出不可"); 
            return false;
        }
        return true;
    }
    
    // TODO: Right-click -> Source -> Generate Getters and Setters...
}