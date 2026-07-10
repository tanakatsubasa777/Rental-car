package exception;

/**
 * 顧客審査（年齢・免許歴）や在庫不足など、業務ルール違反時に発生させる例外クラス。
 */
public class RentalBusinessException extends Exception {
    private String errorCode;

    public RentalBusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}