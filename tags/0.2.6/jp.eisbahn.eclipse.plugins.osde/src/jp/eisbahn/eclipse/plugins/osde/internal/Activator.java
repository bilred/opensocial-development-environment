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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jp.eisbahn.eclipse.plugins.osde.internal.runtime.LaunchApplicationInformation;
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

import org.apache.commons.codec.binary.Base64;
import org.apache.shindig.social.opensocial.hibernate.utils.HibernateUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.classic.Session;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.google.gadgets.GadgetXmlParser;

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
	private boolean runningShindig = false;
	private LaunchApplicationInformation lastApplicationInformation;
	private GadgetXmlParser gadgetXmlParser;

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
		gadgetXmlParser = new GadgetXmlParser();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		savePluginPreferences();
		if (session != null) {
			session.close();
		}
		if (sessionFactory != null) {
			sessionFactory.close();
		}
		personService = null;
		applicationService = null;
		appDataService = null;
		activityService = null;
		sessionFactory = null;
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunch[] launches = manager.getLaunches();
		for (ILaunch launch : launches) {
			String name = launch.getLaunchConfiguration().getName();
			if (name.equals("Shindig Database") || name.equals("Apache Shindig")) {
				launch.terminate();
			}
		}
		(new ShindigLaunchConfigurationCreator()).delete(getStatusMonitor());
		(new DatabaseLaunchConfigurationCreator()).delete(getStatusMonitor());
		File tmpDir = new File(getOsdeConfiguration().getJettyDir());
		File[] files = tmpDir.listFiles();
		for (File file : files) {
			if (file.getName().startsWith("osde_")) {
				file.delete();
			}
		}
		disposeColors();
		plugin = null;
		super.stop(context);
	}
	
	public IProgressMonitor getStatusMonitor() {
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
		registIcon(registry, "icons/16-em-cross.gif");
		registIcon(registry, "icons/icon_home.gif");
		registIcon(registry, "icons/list_settings.gif");
		registIcon(registry, "icons/opensocial.gif");
		registIcon(registry, "icons/i_require.gif");
		registIcon(registry, "icons/i_optional.gif");
		registIcon(registry, "icons/i_param.gif");
		registIcon(registry, "icons/i_icon.gif");
		registIcon(registry, "icons/i_enumvalue.gif");
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
    
    public void disconnect(final IWorkbenchWindow window, final IProgressMonitor monitor) {
		if (session != null && session.isConnected()) {
			session.close();
		}
		if (sessionFactory != null && !sessionFactory.isClosed()) {
			sessionFactory.close();
		}
    	session = null;
    	sessionFactory = null;
    	personService = null;
    	appDataService = null;
    	applicationService = null;
    	activityService = null;
    	monitor.worked(1);
    	window.getShell().getDisplay().syncExec(new Runnable() {
			public void run() {
				try {
					PersonView personView = (PersonView)window.getActivePage().showView(PersonView.ID);
					personView.disconnectedDatabase();
					monitor.worked(1);
					AppDataView appDataView = (AppDataView)window.getActivePage().showView(AppDataView.ID);
					appDataView.disconnectedDatabase();
					monitor.worked(1);
					ActivitiesView activitiesView = (ActivitiesView)window.getActivePage().showView(ActivitiesView.ID);
					activitiesView.disconnectedDatabase();
					monitor.worked(1);
					UserPrefsView userPrefsView = (UserPrefsView)window.getActivePage().showView(UserPrefsView.ID);
					userPrefsView.disconnectedDatabase();
					monitor.worked(1);
				} catch (PartInitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
    }
    
	public void connect(final IWorkbenchWindow window) {
		Job job = new Job("Connect to Shindig database.") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("Connect to Shindig database.", 4);
				monitor.subTask("Building Hibernate SessionFactory.");
				File configFile = new File(HibernateUtils.configFileDir, HibernateUtils.configFileName);
				Configuration configure = new AnnotationConfiguration().configure(configFile);
				sessionFactory = configure.buildSessionFactory();
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
						} catch(JDBCException e) {
							MessageDialog.openError(
									window.getShell(), "Error",
									"Database connection failed.\n  Cause: " + e.getErrorCode() + ": " + e.getSQLException().getMessage());
						} catch(HibernateException e) {
							MessageDialog.openError(
									window.getShell(), "Error", "Database connection failed.\n  Cause: " + e.getMessage());
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
	
	public boolean isRunningShindig() {
		return runningShindig;
	}
	
	public void setRunningShindig(boolean runningShindig) {
		this.runningShindig = runningShindig;
	}

	public OsdeConfig getOsdeConfiguration() {
		try {
			Preferences store = getPluginPreferences();
			OsdeConfig config = new OsdeConfig();
			config.setDefaultCountry(store.getString(OsdeConfig.DEFAULT_COUNTRY));
			config.setDefaultLanguage(store.getString(OsdeConfig.DEFAULT_LANGUAGE));
			config.setDatabaseDir(store.getString(OsdeConfig.DATABASE_DIR));
			config.setDocsSiteMap(decodeSiteMap(store.getString(OsdeConfig.DOCS_SITE_MAP)));
			config.setJettyDir(store.getString(OsdeConfig.JETTY_DIR));
			config.setUseInternalDatabase(store.getBoolean(OsdeConfig.USE_INTERNAL_DATABASE));
			config.setExternalDatabaseType(store.getString(OsdeConfig.EXTERNAL_DATABASE_TYPE));
			config.setExternalDatabaseHost(store.getString(OsdeConfig.EXTERNAL_DATABASE_HOST));
			config.setExternalDatabasePort(store.getString(OsdeConfig.EXTERNAL_DATABASE_PORT));
			config.setExternalDatabaseUsername(store.getString(OsdeConfig.EXTERNAL_DATABASE_USERNAME));
			config.setExternalDatabasePassword(store.getString(OsdeConfig.EXTERNAL_DATABASE_PASSWORD));
			config.setExternalDatabaseName(store.getString(OsdeConfig.EXTERNAL_DATABASE_NAME));
			return config;
		} catch(IOException e) {
			e.printStackTrace();
			throw new IllegalStateException(e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new IllegalStateException(e);
		}
	}
	
	public void storePreferences(OsdeConfig config) {
		storePreferences(getPluginPreferences(), config);
	}
	
	public OsdeConfig getDefaultOsdeConfiguration() {
		try {
			Preferences store = getPluginPreferences();
			OsdeConfig config = new OsdeConfig();
			config.setDefaultCountry(store.getDefaultString(OsdeConfig.DEFAULT_COUNTRY));
			config.setDefaultLanguage(store.getDefaultString(OsdeConfig.DEFAULT_LANGUAGE));
			config.setDatabaseDir(store.getDefaultString(OsdeConfig.DATABASE_DIR));
			config.setDocsSiteMap(decodeSiteMap(store.getDefaultString(OsdeConfig.DOCS_SITE_MAP)));
			config.setJettyDir(store.getDefaultString(OsdeConfig.JETTY_DIR));
			config.setUseInternalDatabase(store.getDefaultBoolean(OsdeConfig.USE_INTERNAL_DATABASE));
			config.setExternalDatabaseType(store.getDefaultString(OsdeConfig.EXTERNAL_DATABASE_TYPE));
			config.setExternalDatabaseHost(store.getDefaultString(OsdeConfig.EXTERNAL_DATABASE_HOST));
			config.setExternalDatabasePort(store.getDefaultString(OsdeConfig.EXTERNAL_DATABASE_PORT));
			config.setExternalDatabaseUsername(store.getDefaultString(OsdeConfig.EXTERNAL_DATABASE_USERNAME));
			config.setExternalDatabasePassword(store.getDefaultString(OsdeConfig.EXTERNAL_DATABASE_PASSWORD));
			config.setExternalDatabaseName(store.getDefaultString(OsdeConfig.EXTERNAL_DATABASE_NAME));
			return config;
		} catch(IOException e) {
			e.printStackTrace();
			throw new IllegalStateException(e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new IllegalStateException(e);
		}
	}

	public void storePreferences(Preferences store, OsdeConfig config) {
		try {
			store.setValue(OsdeConfig.DEFAULT_COUNTRY, config.getDefaultCountry());
			store.setValue(OsdeConfig.DEFAULT_LANGUAGE, config.getDefaultLanguage());
			store.setValue(OsdeConfig.DATABASE_DIR, config.getDatabaseDir());
			store.setValue(OsdeConfig.DOCS_SITE_MAP, encodeSiteMap(config.getDocsSiteMap()));
			store.setValue(OsdeConfig.JETTY_DIR, config.getJettyDir());
			store.setValue(OsdeConfig.USE_INTERNAL_DATABASE, config.isUseInternalDatabase());
			store.setValue(OsdeConfig.EXTERNAL_DATABASE_HOST, config.getExternalDatabaseHost());
			store.setValue(OsdeConfig.EXTERNAL_DATABASE_PORT, config.getExternalDatabasePort());
			store.setValue(OsdeConfig.EXTERNAL_DATABASE_USERNAME, config.getExternalDatabaseUsername());
			store.setValue(OsdeConfig.EXTERNAL_DATABASE_PASSWORD, config.getExternalDatabasePassword());
			store.setValue(OsdeConfig.EXTERNAL_DATABASE_TYPE, config.getExternalDatabaseType());
			store.setValue(OsdeConfig.EXTERNAL_DATABASE_NAME, config.getExternalDatabaseName());
		} catch(IOException e) {
			e.printStackTrace();
			throw new IllegalStateException(e);
		}
	}

	public LaunchApplicationInformation getLastApplicationInformation() {
		return lastApplicationInformation;
	}

	public void setLastApplicationInformation(LaunchApplicationInformation lastApplicationInformation) {
		this.lastApplicationInformation = lastApplicationInformation;
	}
	
	private String encodeSiteMap(Map<String, String> siteMap) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(baos);
		out.writeObject(siteMap);
		out.flush();
		byte[] bytes = baos.toByteArray();
		byte[] encoded = Base64.encodeBase64(bytes);
		return new String(encoded, "UTF-8");
	}
	
	private Map<String, String> decodeSiteMap(String encodeSiteMap) throws IOException, ClassNotFoundException {
		if (encodeSiteMap != null && encodeSiteMap.length() > 0) {
			byte[] bytes = encodeSiteMap.getBytes("UTF-8");
			byte[] decoded = Base64.decodeBase64(bytes);
			ByteArrayInputStream bais = new ByteArrayInputStream(decoded);
			ObjectInputStream in = new ObjectInputStream(bais);
			return (Map<String, String>)in.readObject();
		} else {
			return null;
		}
	}
	
	public GadgetXmlParser getGadgetXmlParser() {
		return gadgetXmlParser;
	}
	
}