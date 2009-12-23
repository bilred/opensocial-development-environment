package com.googlecode.osde.internal.shindig;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;

import com.googlecode.osde.internal.common.AbstractJob;

public class DatabaseLaunchConfigurationDeleter extends AbstractJob {

    public DatabaseLaunchConfigurationDeleter() {
        super("Delete the database launch configuration");
    }

    @Override
    protected void runImpl(IProgressMonitor monitor) throws CoreException {
        monitor.beginTask("Deleting the launch configuration for Database.", 1);
        ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
        ILaunchConfigurationType type = manager
                .getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
        ILaunchConfiguration[] configurations = manager.getLaunchConfigurations(type);
        for (int i = 0; i < configurations.length; i++) {
            if (configurations[i].getName().equals("Shindig Database")) {
                configurations[i].delete();
            }
        }
    }
}
