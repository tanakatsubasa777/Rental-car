package rentalcar;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Customer {
    // Attributes (属性)
    private String name;
    private LocalDate birthDate;
    private String phoneNumber;
    private String licenseNumber;
    private LocalDate licenseDate;

    // Constructor (コンストラクタ)
    public Customer(String name, LocalDate birthDate, String phoneNumber, String licenseNumber, LocalDate licenseDate) {
        this.name = name;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
        this.licenseNumber = licenseNumber;
        this.licenseDate = licenseDate;
    }

    // Calculate exact age in years
    public int calculateAge() {
        return (int) ChronoUnit.YEARS.between(this.birthDate, LocalDate.now());
    }

    // Calculate driving experience in days
    public int calculateDrivingExperienceDays() {
        return (int) ChronoUnit.DAYS.between(this.licenseDate, LocalDate.now());
    }

    // Validation Check (審査ロジック)
    public boolean isEligible() {
        if (calculateAge() < 25) {
            System.out.println("エラー: 25歳未満のため貸出不可"); 
            return false;
        }
        if (calculateDrivingExperienceDays() < 365) {
            System.out.println("エラー: 免許取得1年未満のため貸出不可"); 
            return false;
        }
        System.out.println("審査通過: 顧客情報が登録可能です。");
        return true;
    }
}