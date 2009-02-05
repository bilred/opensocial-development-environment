/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package jp.eisbahn.eclipse.plugins.osde.internal;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jp.eisbahn.eclipse.plugins.osde.internal.shindig.ActivityService;
import jp.eisbahn.eclipse.plugins.osde.internal.shindig.AppDataService;
import jp.eisbahn.eclipse.plugins.osde.internal.shindig.ApplicationService;
import jp.eisbahn.eclipse.plugins.osde.internal.shindig.DatabaseLaunchConfigurationCreator;
import jp.eisbahn.eclipse.plugins.osde.internal.shindig.PersonService;
import jp.eisbahn.eclipse.plugins.osde.internal.shindig.ShindigLaunchConfigurationCreator;
import jp.eisbahn.eclipse.plugins.osde.internal.ui.views.activities.ActivitiesView;
import jp.eisbahn.eclipse.plugins.osde.internal.ui.views.appdata.AppDataView;
import jp.eisbahn.eclipse.plugins.osde.internal.ui.views.people.PersonView;
import jp.eisbahn.eclipse.plugins.osde.internal.ui.views.userprefs.UserPrefsView;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.classic.Session;
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

	private SessionFactory sessionFactory;
	private PersonService personService;
	private ApplicationService applicationService;
	private AppDataService appDataService;
	private ActivityService activityService;
	private Session session;

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
		(new ShindigLaunchConfigurationCreator()).create(getStatusMonitor());
		(new DatabaseLaunchConfigurationCreator()).create(getStatusMonitor());
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
		session.close();
		sessionFactory.close();
		personService = null;
		applicationService = null;
		appDataService = null;
		activityService = null;
		sessionFactory = null;
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
		registIcon(registry, "icons/icon_component.gif");
		registIcon(registry, "icons/action_refresh.gif");
		registIcon(registry, "icons/comment_yellow.gif");
		registIcon(registry, "icons/project.gif");
		registIcon(registry, "icons/icon_key.gif");
		registIcon(registry, "icons/icon_world.gif");
		registIcon(registry, "icons/16-em-pencil.gif");
		registIcon(registry, "icons/icon_settings.gif");
		registIcon(registry, "icons/page_component.gif");
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
                imageDescriptor = ImageDescriptor.createFromURL(new URL(url));
            } catch(MalformedURLException e) {
                imageDescriptor = ImageDescriptor.getMissingImageDescriptor();
            }
            registry.put(url, imageDescriptor);
    	}
    	return imageDescriptor;
    }

    private ImageDescriptor createSystemImageDescriptor(String path) {
        ImageDescriptor descriptor;
        try {
            URL installUrl = getInstallUrl();
            descriptor = ImageDescriptor.createFromURL(new URL(installUrl, path));
        } catch(MalformedURLException e) {
            descriptor = ImageDescriptor.getMissingImageDescriptor();
        }
        return descriptor;
    }

    public URL getInstallUrl() {
        Bundle bundle = getBundle();
        return bundle.getEntry("/");
    }
    
	public void connect(final IWorkbenchWindow window) {
		Job job = new Job("Connect to Shindig database.") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("Connect to Shindig database.", 4);
				monitor.subTask("Building Hibernate SessionFactory.");
				sessionFactory = new AnnotationConfiguration().configure().buildSessionFactory();
				monitor.worked(1);
				monitor.subTask("Opening Hibernate session.");
				session = sessionFactory.openSession();
				monitor.worked(1);
				monitor.subTask("Creating services.");
				personService = new PersonService(session);
				applicationService = new ApplicationService(session);
				appDataService = new AppDataService(session);
				activityService = new ActivityService(session);
				monitor.worked(1);
				monitor.subTask("Notify each view about connecting with database.");
				window.getShell().getDisplay().syncExec(new Runnable() {
					public void run() {
						try {
							PersonView personView = (PersonView)window.getActivePage().showView(PersonView.ID);
							personView.connectedDatabase();
							AppDataView appDataView = (AppDataView)window.getActivePage().showView(AppDataView.ID);
							appDataView.connectedDatabase();
							ActivitiesView activitiesView = (ActivitiesView)window.getActivePage().showView(ActivitiesView.ID);
							activitiesView.connectedDatabase();
							UserPrefsView userPrefsView = (UserPrefsView)window.getActivePage().showView(UserPrefsView.ID);
							userPrefsView.connectedDatabase();
						} catch (PartInitException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				monitor.worked(1);
				monitor.done();
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}
	
	public PersonService getPersonService() throws ConnectionException {
		if (personService != null) {
			return personService;
		} else {
			throw new ConnectionException("Not connect yet.");
		}
	}

	public ApplicationService getApplicationService() throws ConnectionException {
		if (applicationService != null) {
			return applicationService;
		} else {
			throw new ConnectionException("Not connect yet.");
		}
	}

	public AppDataService getAppDataService() throws ConnectionException {
		if (appDataService != null) {
			return appDataService;
		} else {
			throw new ConnectionException("Not connect yet.");
		}
	}

	public ActivityService getActivityService() throws ConnectionException {
		if (activityService != null) {
			return activityService;
		} else {
			throw new ConnectionException("Not connect yet.");
		}
	}

}
