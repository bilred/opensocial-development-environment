package jp.eisbahn.eclipse.plugins.osde.internal.utils;

import jp.eisbahn.eclipse.plugins.osde.internal.Activator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

public class ProjectPreferenceUtils {
	
	private static final String LOCAL_WEB_SERVER_PORT = "localWebServerPort";
	
	public static int getLocalWebServerPort(IProject project) throws CoreException {
		String localWebServerPort = project.getPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, LOCAL_WEB_SERVER_PORT));
		if (localWebServerPort == null || localWebServerPort.length() == 0) {
			return 8081;
		} else {
			try {
				return Integer.parseInt(localWebServerPort);
			} catch(NumberFormatException e) {
				e.printStackTrace();
				return 8081;
			}
		}
	}
	
	public static void setLocalWebServerPort(IProject project, int port) throws CoreException {
		project.setPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, LOCAL_WEB_SERVER_PORT), String.valueOf(port));
		// TODO ローカルWebサーバの起動設定の更新
		// TODO ポート番号が記述されている箇所の更新
	}

}
