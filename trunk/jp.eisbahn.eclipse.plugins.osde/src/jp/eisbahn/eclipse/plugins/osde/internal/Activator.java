package jp.eisbahn.eclipse.plugins.osde.internal;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jp.eisbahn.eclipse.plugins.osde.internal.shindig.DatabaseLaunchConfigurationCreator;
import jp.eisbahn.eclipse.plugins.osde.internal.shindig.ShindigLaunchConfigurationCreator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "jp.eisbahn.eclipse.plugins.osde";

	// The shared instance
	private static Activator plugin;
	
	private Map<RGB, Color> colorMap = new HashMap<RGB, Color>();
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		// Apache Shindig起動設定の作成
		(new ShindigLaunchConfigurationCreator()).create(getStatusMonitor());
		// Derby起動設定の作成
		(new DatabaseLaunchConfigurationCreator()).create(getStatusMonitor());
		// イメージレジストリの作成
		registIcon();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		(new ShindigLaunchConfigurationCreator()).delete(getStatusMonitor());
		(new DatabaseLaunchConfigurationCreator()).delete(getStatusMonitor());
		plugin = null;
		disposeColors();
		super.stop(context);
	}
	
	private IProgressMonitor getStatusMonitor() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench != null) {
			WorkbenchWindow workbenchWindow = (WorkbenchWindow)workbench.getActiveWorkbenchWindow();
			if (workbenchWindow != null) {
				IActionBars bars = workbenchWindow.getActionBars();
				IStatusLineManager lineManager = bars.getStatusLineManager();
				IProgressMonitor monitor = lineManager.getProgressMonitor();
				return monitor;
			}
		}
		return new NullProgressMonitor();
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
	
	private void disposeColors() {
		Collection<Color> colors = colorMap.values();
		for (Color color : colors) {
			color.dispose();
		}
	}
	
	/**
	 * 指定された色を示すオブジェクトを返します。
	 * @param rgb 色の情報
	 * @return 色を示すオブジェクト
	 */
	public Color getColor(RGB rgb) {
		synchronized (colorMap) {
			Color color = colorMap.get(rgb);
			if (color == null) {
				color = new Color(Display.getCurrent(), rgb);
				colorMap.put(rgb, color);
			}
			return color;
		}
	}
	
	private void registIcon() {
		ImageRegistry registry = getImageRegistry();
		registIcon(registry, "icons/icon_user.gif");
	}
	
    public ImageDescriptor registIcon(ImageRegistry registry, String iconPath) {
        ImageDescriptor descriptor = createSystemImageDescriptor(iconPath);
        registry.put(iconPath, descriptor);
        return descriptor;
    }
    
    public ImageDescriptor createImageDescriptor(String url) {
    	ImageRegistry registry = getImageRegistry();
    	ImageDescriptor imageDescriptor = registry.getDescriptor(url);
    	if (imageDescriptor == null) {
            try {
                // イメージディスクリプタを生成
                imageDescriptor = ImageDescriptor.createFromURL(new URL(url));
            } catch(MalformedURLException e) {
                imageDescriptor = ImageDescriptor.getMissingImageDescriptor();
            }
            registry.put(url, imageDescriptor);
    	}
    	return imageDescriptor;
    }

    /**
     * 指定されたパスのイメージを示すイメージディスクリプタを生成して返します。
     * @param path イメージのパス
     * @return 生成されたイメージディスクリプタ
     */
    private ImageDescriptor createSystemImageDescriptor(String path) {
        ImageDescriptor descriptor;
        try {
            // プラグインインストールディレクトリを取得
            URL installUrl = getInstallUrl();
            // イメージディスクリプタを生成
            descriptor = ImageDescriptor.createFromURL(new URL(installUrl, path));
        } catch(MalformedURLException e) {
            descriptor = ImageDescriptor.getMissingImageDescriptor();
        }
        return descriptor;
    }

    /**
     * プラグインがインストールされているディレクトリを示すURLを返します。
     * @return プラグインがインストールされているディレクトリを示すURL
     */
    public URL getInstallUrl() {
        Bundle bundle = getBundle();
        return bundle.getEntry("/");
    }

}
