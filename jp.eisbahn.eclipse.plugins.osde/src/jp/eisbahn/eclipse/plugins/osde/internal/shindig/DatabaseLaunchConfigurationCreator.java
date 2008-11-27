package jp.eisbahn.eclipse.plugins.osde.internal.shindig;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import jp.eisbahn.eclipse.plugins.osde.internal.Activator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;

public class DatabaseLaunchConfigurationCreator {
	
	public void delete(IProgressMonitor monitor) throws CoreException {
		try {
			monitor.beginTask("Deleting the launch configuration for Database.", 1);
			ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
			ILaunchConfigurationType type = manager.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
			ILaunchConfiguration[] configurations = manager.getLaunchConfigurations(type);
			for (int i = 0; i < configurations.length; i++) {
				if (configurations[i].getName().equals("Shindig Database")) {
					configurations[i].delete();
				}
			}
		} finally {
			monitor.done();
		}
	}
	
	public void create(IProgressMonitor monitor) throws CoreException, MalformedURLException, IOException {
		try {
			monitor.beginTask("Creating the launch configuration for Database", 3);
			monitor.subTask("Deleting the setting that already exists.");
			ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
			ILaunchConfigurationType type = manager.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
			ILaunchConfiguration[] configurations = manager.getLaunchConfigurations(type);
			for (int i = 0; i < configurations.length; i++) {
				if (configurations[i].getName().equals("Shidig Database")) {
					configurations[i].delete();
				}
			}
			monitor.worked(1);
			monitor.subTask("Building the classpath.");
			IPath systemLibs = new Path(JavaRuntime.JRE_CONTAINER);
			IRuntimeClasspathEntry systemLibsEntry = JavaRuntime.newRuntimeContainerClasspathEntry(
					systemLibs, IRuntimeClasspathEntry.STANDARD_CLASSES);
			systemLibsEntry.setClasspathProperty(IRuntimeClasspathEntry.BOOTSTRAP_CLASSES);
			IRuntimeClasspathEntry derbyEntry = createRuntimeClasspathEntry("/derby/derby.jar");
			IRuntimeClasspathEntry derbynetEntry = createRuntimeClasspathEntry("/derby/derbynet.jar");
			ILaunchConfigurationWorkingCopy wc = type.newInstance(null, "Shindig Database");
			List<String> classpath = new ArrayList<String>();
			classpath.add(systemLibsEntry.getMemento());
			classpath.add(derbyEntry.getMemento());
			classpath.add(derbynetEntry.getMemento());
			monitor.worked(1);
			monitor.subTask("Creating the launch configuration.");
			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, classpath);
			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, false);
			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, "org.apache.derby.drda.NetworkServerControl");
			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, "start");
			wc.doSave();
			monitor.worked(1);
		} finally {
			monitor.done();
		}
	}
	
	private IRuntimeClasspathEntry createRuntimeClasspathEntry(String path) throws MalformedURLException, IOException {
		URL url = getBundleEntryUrl(path);
		IRuntimeClasspathEntry entry = JavaRuntime.newArchiveRuntimeClasspathEntry(new Path(url.getPath()));
		entry.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);
		return entry;
	}
	
	private URL getBundleEntryUrl(String path) throws MalformedURLException, IOException {
		return FileLocator.toFileURL(new URL(
				Activator.getDefault().getBundle().getEntry(path).toExternalForm()));
	}

}
