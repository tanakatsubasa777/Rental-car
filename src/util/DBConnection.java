package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * XAMPP (MySQL) データベースへの接続を管理するユーティリティクラス。
 */
public class DBConnection {
    // 【研修ポイント】接続情報は定数（static final）として一元管理します。
    // XAMPPのデフォルト設定ではユーザー名は "root"、パスワードは ""（空文字）です。
    private static final String URL = "jdbc:mysql://localhost:3306/luxury_rentacar?useSSL=false&characterEncoding=UTF-8&serverTimezone=Asia/Tokyo";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // インスタンス化を防止するためプライベートコンストラクタにします
    private DBConnection() {}

    /**
     * データベースへの接続を取得します。
     * @return Connection オブジェクト
     * @throws SQLException 接続に失敗した場合
     */
    public static Connection getConnection() throws SQLException {
        try {
            // MySQL JDBCドライバの明示的な読み込み（現代のJavaでは省略可能な場合もありますが、研修として明記）
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            // 【研修ポイント】技術的なエラーをわかりやすいメッセージに変換して投げ直します
            throw new SQLException("MySQL JDBCドライバが見つかりません。ライブラリの構成を確認してください。", e);
        }
    }
}