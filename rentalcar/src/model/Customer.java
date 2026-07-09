package model;
 
import java.time.LocalDate;
import java.time.Period;

import exception.RentalBusinessException;
 
public class Customer {

    private int customerId;

    private String name;

    private LocalDate birthDate;

    private String phone;

    private String licenseNumber;

    private LocalDate licenseDate;
 
    // コンストラクタ

    public Customer(String name, LocalDate birthDate, String phone, String licenseNumber, LocalDate licenseDate) {

        this.name = name;

        this.birthDate = birthDate;

        this.phone = phone;

        this.licenseNumber = licenseNumber;

        this.licenseDate = licenseDate;

    }
 
    /**

     * 【研修ポイント】実務型オブジェクト指向の重要ロジック！

     * 「顧客の資格審査」をサービス層に散らかさず、顧客オブジェクト自身のメソッドとしてカプセル化します。

     * @throws RentalBusinessException 審査基準を満たさない場合

     */

    public void validateEligibility() throws RentalBusinessException {

        LocalDate today = LocalDate.now();
 
        // 1. 年齢審査：25歳以上であること (TC-CUS-002 / ERR-AGE-01)

        int age = Period.between(this.birthDate, today).getYears();

        if (age < 25) {

            throw new RentalBusinessException("ERR-AGE-01", 

                "貸出拒否: ご利用は25歳以上のお客様に限らせていただきます。(現在 " + age + " 歳)");

        }
 
        // 2. 免許歴審査：取得から1年以上経過していること (TC-CUS-003 / ERR-LIC-01)

        int licenseYears = Period.between(this.licenseDate, today).getYears();

        if (licenseYears < 1) {

            throw new RentalBusinessException("ERR-LIC-01", 

                "貸出拒否: 免許取得から1年未満の方はご利用いただけません。(取得日: " + this.licenseDate + ")");

        }

    }
 
    // --- Getter & Setter ---

    public int getCustomerId() { return customerId; }

    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getName() { return name; }

    public LocalDate getBirthDate() { return birthDate; }

    public String getPhone() { return phone; }

    public String getLicenseNumber() { return licenseNumber; }

    public LocalDate getLicenseDate() { return licenseDate; }

}
 