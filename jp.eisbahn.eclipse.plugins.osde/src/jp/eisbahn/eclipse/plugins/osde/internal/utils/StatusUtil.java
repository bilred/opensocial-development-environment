package jp.eisbahn.eclipse.plugins.osde.internal.utils;

import jp.eisbahn.eclipse.plugins.osde.internal.Activator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * {@link IStatus}に関する汎用的な処理を提供するユーティリティクラスです。
 * @author yoichiro
 */
public class StatusUtil {

	/**
	 * 指定された情報に基づいて、{@link IStatus}オブジェクトを生成して返します。
	 * @param severity ステータスのレベル
	 * @param message メッセージ
	 * @param exception 発生した例外
	 * @return {@link IStatus} 生成された{@link IStatus}オブジェクト
	 */
	public static IStatus newStatus(int severity, String message, Throwable exception) {
		String statusMessage = message;
		if (message == null || message.trim().length() == 0) {
			if (exception == null) {
				throw new IllegalArgumentException();
			} else if (exception.getMessage() == null) {
				statusMessage = exception.toString();
			} else {
				statusMessage = exception.getMessage();
			}
		}
		return new Status(severity, Activator.PLUGIN_ID, severity, statusMessage, exception);
	}

}
