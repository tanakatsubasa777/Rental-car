package main;

public class main {
	import javax.swing.SwingUtilities;
	import javax.swing.UIManager;
	 
	import ui.MainFrame;
	 
	/**
	* レンタカー予約管理システムの起動クラス
	*/
	public class Main {
	    /**
	     * 【研修ポイント】 
	     * Javaアプリケーションは必ずこの public static void main メソッドから処理が開始されます。
	     * ここではUI（MainFrame）を呼び出して画面を表示する役割だけを持たせています。
	     */
	    public static void main(String[] args) {
	        // 画面のデザインをWindows/Mac等のOS標準の見た目に合わせる設定
	        try {
	            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	 
	        // 安全に画面を起動するためのSwingのおまじない
	        SwingUtilities.invokeLater(() -> {
	            MainFrame frame = new MainFrame();
	            frame.setVisible(true); // 画面を表示する
	        });
	    }
	}
}
