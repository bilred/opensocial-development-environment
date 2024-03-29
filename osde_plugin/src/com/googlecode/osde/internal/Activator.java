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
package com.googlecode.osde.internal;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;

import com.googlecode.osde.internal.runtime.LaunchApplicationInformation;
import com.googlecode.osde.internal.shindig.ActivityService;
import com.googlecode.osde.internal.shindig.AppDataService;
import com.googlecode.osde.internal.shindig.ApplicationService;
import com.googlecode.osde.internal.shindig.DatabaseServer;
import com.googlecode.osde.internal.shindig.PersonService;
import com.googlecode.osde.internal.shindig.ShindigServer;
import com.googlecode.osde.internal.ui.views.activities.ActivitiesView;
import com.googlecode.osde.internal.ui.views.appdata.AppDataView;
import com.googlecode.osde.internal.ui.views.apps.ApplicationView;
import com.googlecode.osde.internal.ui.views.people.PersonView;
import com.googlecode.osde.internal.ui.views.userprefs.UserPrefsView;
import com.googlecode.osde.internal.utils.EclipseLogHandler;
import com.googlecode.osde.internal.utils.Logger;

import org.apache.shindig.social.opensocial.hibernate.utils.HibernateUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.classic.Session;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 *
 * Note that when the plug-in shuts down, AbstractUIPlugin automatically saves
 * any plug-in preferences. So anything specified in the preference page will
 * persist next time you activate this plug-in.
 */
public class Activator extends AbstractUIPlugin {

    private static final Logger logger = new Logger(Activator.class);

    // The plug-in ID
    public static final String PLUGIN_ID = "com.googlecode.osde";

    // Work directory name for OSDE
    public static final String WORK_DIR_NAME = ".osde";

    // The shared instance
    private static Activator plugin;

    private final Map<RGB, Color> colorMap = new HashMap<RGB, Color>();

    private SessionFactory sessionFactory;
    private PersonService personService;
    private ApplicationService applicationService;
    private AppDataService appDataService;
    private ActivityService activityService;
    private Session session;
    private boolean runningShindig = false;
    private LaunchApplicationInformation lastApplicationInformation;
    private OsdePreferencesModel preferencesModel;

    private Handler logHandler;

    /**
     * This method is called upon plug-in activation
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        preferencesModel = new OsdePreferencesModel(getPreferenceStore());
        logHandler = new EclipseLogHandler();

        java.util.logging.Logger.getLogger(PLUGIN_ID).addHandler(logHandler);

        new ShindigServer().createConfiguration();
        new DatabaseServer().createConfiguration();
        registerIcon();
    }

    /**
     * This method is called when the plug-in is stopped
     */
    public void stop(BundleContext context) throws Exception {
        (new DefaultScope()).getNode(Activator.PLUGIN_ID).flush();
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

        ShindigServer shindig = new ShindigServer();
        DatabaseServer database = new DatabaseServer();

        shindig.stop();
        database.stop();

        shindig.deleteConfiguration();
        database.deleteConfiguration();
        File tmpDir = new File(getOsdeConfiguration().getJettyDir());
        File[] files = tmpDir.listFiles();
        for (File file : files) {
            if (file.getName().startsWith("osde_")) {
                file.delete();
            }
        }
        disposeColors();
        java.util.logging.Logger.getLogger(PLUGIN_ID).removeHandler(logHandler);
        plugin = null;
        super.stop(context);
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

    private void registerIcon() {
        ImageRegistry registry = getImageRegistry();
        registerIcon(registry, "icons/icon_user.gif");
        registerIcon(registry, "icons/icon_component.gif");
        registerIcon(registry, "icons/action_refresh.gif");
        registerIcon(registry, "icons/comment_yellow.gif");
        registerIcon(registry, "icons/project.gif");
        registerIcon(registry, "icons/icon_key.gif");
        registerIcon(registry, "icons/icon_world.gif");
        registerIcon(registry, "icons/16-em-pencil.gif");
        registerIcon(registry, "icons/icon_settings.gif");
        registerIcon(registry, "icons/page_component.gif");
        registerIcon(registry, "icons/16-em-cross.gif");
        registerIcon(registry, "icons/icon_home.gif");
        registerIcon(registry, "icons/list_settings.gif");
        registerIcon(registry, "icons/opensocial.gif");
        registerIcon(registry, "icons/i_require.gif");
        registerIcon(registry, "icons/i_optional.gif");
        registerIcon(registry, "icons/i_param.gif");
        registerIcon(registry, "icons/i_icon.gif");
        registerIcon(registry, "icons/i_enumvalue.gif");
        registerIcon(registry, "icons/icon_extension.gif");
        registerIcon(registry, "icons/16-arrow-right.gif");
        registerIcon(registry, "icons/16-circle-red-remove.gif");
        registerIcon(registry, "icons/16-circle-blue.gif");
    }

    public ImageDescriptor registerIcon(ImageRegistry registry, String iconPath) {
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
            } catch (MalformedURLException e) {
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
        } catch (MalformedURLException e) {
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
            try {
                session.close();
            } catch(Exception e) {
                // Ignore this exception, only logging it.
                logger.warn("Closing the session failed.", e);
            }
        }
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            try {
                sessionFactory.close();
            } catch(Exception e) {
                // Ignore this exception, only logging it.
                logger.warn("Closing the session factory failed.", e);
            }
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
                    PersonView personView =
                            (PersonView) window.getActivePage().showView(
                                    PersonView.ID, null, IWorkbenchPage.VIEW_CREATE);
                    personView.disconnectedDatabase();
                    monitor.worked(1);
                    AppDataView appDataView =
                            (AppDataView) window.getActivePage().showView(
                                    AppDataView.ID, null, IWorkbenchPage.VIEW_CREATE);
                    appDataView.disconnectedDatabase();
                    monitor.worked(1);
                    ActivitiesView activitiesView =
                            (ActivitiesView) window.getActivePage().showView(
                                    ActivitiesView.ID, null, IWorkbenchPage.VIEW_CREATE);
                    activitiesView.disconnectedDatabase();
                    monitor.worked(1);
                    UserPrefsView userPrefsView =
                            (UserPrefsView) window.getActivePage().showView(
                                    UserPrefsView.ID, null, IWorkbenchPage.VIEW_CREATE);
                    userPrefsView.disconnectedDatabase();
                    monitor.worked(1);
                    ApplicationView applicationView =
                            (ApplicationView) window.getActivePage().showView(
                                    ApplicationView.ID, null, IWorkbenchPage.VIEW_CREATE);
                    applicationView.disconnectedDatabase();
                    monitor.worked(1);
                } catch (PartInitException e) {
                    logger.warn("Disconnecting Shindig Database failed.", e);
                }
            }
        });
    }

    public void connect(final IWorkbenchWindow window) {
        logger.info("Connecting to Shindig database");
        Job job = new Job("Connecting to Shindig database.") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                monitor.beginTask("Connecting to Shindig database.", 4);
                monitor.subTask("Building Hibernate SessionFactory.");
                File configFile =
                        new File(HibernateUtils.configFileDir, HibernateUtils.configFileName);
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
                            PersonView personView =
                                    (PersonView) window.getActivePage().showView(
                                            PersonView.ID, null, IWorkbenchPage.VIEW_CREATE);
                            personView.connectedDatabase();
                            AppDataView appDataView =
                                    (AppDataView) window.getActivePage().showView(
                                            AppDataView.ID, null, IWorkbenchPage.VIEW_CREATE);
                            appDataView.connectedDatabase();
                            ActivitiesView activitiesView =
                                    (ActivitiesView) window.getActivePage().showView(
                                            ActivitiesView.ID, null, IWorkbenchPage.VIEW_CREATE);
                            activitiesView.connectedDatabase();
                            UserPrefsView userPrefsView =
                                    (UserPrefsView) window.getActivePage().showView(
                                            UserPrefsView.ID, null, IWorkbenchPage.VIEW_CREATE);
                            userPrefsView.connectedDatabase();
                            ApplicationView applicationView =
                                (ApplicationView) window.getActivePage().showView(
                                        ApplicationView.ID, null, IWorkbenchPage.VIEW_CREATE);
                            applicationView.connectedDatabase();
                        } catch (PartInitException e) {
                            logger.error("Connecting to Shindig Database failed.", e);
                        } catch (JDBCException e) {
                            MessageDialog.openError(window.getShell(), "Error",
                                    "Database connection failed.\n  Cause: " + e.getErrorCode()
                                            + ": " + e.getSQLException().getMessage());
                        } catch (HibernateException e) {
                            MessageDialog.openError(window.getShell(), "Error",
                                    "Database connection failed.\n  Cause: " + e.getMessage());
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
        return preferencesModel.getOsdeConfiguration();
    }

    public OsdeConfig getDefaultOsdeConfiguration() {
        return preferencesModel.getDefaultOsdeConfiguration();
    }

    public void storePreference(String name, String value){
        preferencesModel.store(name, value);
    }

    public void storePreference(String name, boolean value){
        preferencesModel.store(name, value);
    }

    public void storePreferences(Map<String, Object> pref){
        preferencesModel.store(pref);
    }

    public void storePreference(String name, Map<String, String> value){
        preferencesModel.store(name, value);
    }


    public LaunchApplicationInformation getLastApplicationInformation() {
        return lastApplicationInformation;
    }

    public void setLastApplicationInformation(
            LaunchApplicationInformation lastApplicationInformation) {
        this.lastApplicationInformation = lastApplicationInformation;
    }

    public File getWorkDirectory() {
        OsdeConfig config = getOsdeConfiguration();
        String workDirectory = config.getWorkDirectory();
        if (workDirectory == null) {
            String userHome = System.getProperty("user.home");
            File dir = new File(userHome, WORK_DIR_NAME);
            dir.mkdirs();
            workDirectory = dir.getAbsolutePath();
            storePreference(OsdeConfig.WORK_DIRECTORY, workDirectory);
        }
        return new File(workDirectory);
    }

    /**
     * Returns a URI locating a resource bundled within this plugin.
     *
     * @param path a plugin-absolute classpath preceded with "/"
     */
    public static String getResourceUrl(String path) throws IOException {
        return FileLocator.toFileURL(new URL(getDefault().getBundle().getEntry(path)
                .toExternalForm())).toExternalForm();
    }

    public OsdePreferencesModel getPreferenceModel() {
        return preferencesModel;
    }

}
