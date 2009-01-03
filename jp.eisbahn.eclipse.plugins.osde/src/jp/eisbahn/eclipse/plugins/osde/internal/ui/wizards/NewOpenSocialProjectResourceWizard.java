package jp.eisbahn.eclipse.plugins.osde.internal.ui.wizards;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.EnumMap;

import jp.eisbahn.eclipse.plugins.osde.internal.Activator;
import jp.eisbahn.eclipse.plugins.osde.internal.OsdeProjectNature;
import jp.eisbahn.eclipse.plugins.osde.internal.editors.GadgetXmlEditor;
import jp.eisbahn.eclipse.plugins.osde.internal.runtime.WebServerLaunchConfigurationCreator;
import jp.eisbahn.eclipse.plugins.osde.internal.utils.ProjectPreferenceUtils;
import jp.eisbahn.eclipse.plugins.osde.internal.utils.StatusUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.statushandlers.IStatusAdapterConstants;
import org.eclipse.ui.statushandlers.StatusAdapter;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

import com.google.gadgets.ViewName;

public class NewOpenSocialProjectResourceWizard extends BasicNewResourceWizard implements IExecutableExtension {

	public static final String WIZARD_ID = "jp.eisbahn.eclipse.plugins.osde.ui.wizards.new.project";
	
	private  WizardNewProjectCreationPage mainPage;
	
	private WizardNewGadgetXmlPage gadgetXmlPage;
	
	private WizardNewViewPage viewPage;
	
	private WizardServerPage serverPage;
	
	private IProject newProject;
	
	public NewOpenSocialProjectResourceWizard() {
		super();
		IDialogSettings workbenchSetting = Activator.getDefault().getDialogSettings();
		IDialogSettings section = workbenchSetting.getSection("NewOpenSocialProjectResourceWizard");
		if (section == null) {
			section = workbenchSetting.addNewSection("NewOpenSocialProjectResourceWizard");
		}
		setDialogSettings(section);
	}
	
	public void addPages() {
		super.addPages();
		//
		mainPage = new WizardNewProjectCreationPage("basicNewProjectPage");
		mainPage.setTitle("OpenSocial Project");
		mainPage.setDescription("Create a new OpenSocial project resource.");
		addPage(mainPage);
		//
		gadgetXmlPage = new WizardNewGadgetXmlPage("newGadgetXmlPage");
		gadgetXmlPage.setTitle("Application settings");
		gadgetXmlPage.setDescription("Define this application settings.");
		addPage(gadgetXmlPage);
		//
		viewPage = new WizardNewViewPage("newViewPage");
		viewPage.setTitle("View settings");
		viewPage.setDescription("Define the view settings.");
		addPage(viewPage);
		//
		serverPage = new WizardServerPage("serverPage");
		serverPage.setTitle("Server settings");
		serverPage.setDescription("Settings about execution environment of this application.");
		addPage(serverPage);
	}
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection currentSelection) {
		super.init(workbench, currentSelection);
		setNeedsProgressMonitor(true);
		setWindowTitle("New OpenSocial project");
	}
	
	public IProject getNewProject() {
		return newProject;
	}

	@Override
	public boolean performFinish() {
		createNewProject();
		if (newProject == null) {
			return false;
		}
		selectAndReveal(newProject);
		return true;
	}
	
	private IProject createNewProject() {
		if (newProject != null) {
			return newProject;
		}
		final IProject newProjectHandle = mainPage.getProjectHandle();
		final GadgetXmlData gadgetXmlData = gadgetXmlPage.getInputedData();
		final EnumMap<ViewName, GadgetViewData> gadgetViewData = viewPage.getInputedData();
		final ServerData serverData = serverPage.getInputedData();
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					newProjectHandle.create(monitor);
					newProjectHandle.open(monitor);
					IProjectDescription description = newProjectHandle.getDescription();
					if (!description.hasNature(OsdeProjectNature.ID)) {
						String[] ids = description.getNatureIds();
						String[] newIds = new String[ids.length + 1];
						System.arraycopy(ids, 0, newIds, 0, ids.length);
						newIds[ids.length] = OsdeProjectNature.ID;
						description.setNatureIds(newIds);
						newProjectHandle.setDescription(description, monitor);
					}
					final IFile gadgetXmlFile = (new GadgetXmlFileGenerator(newProjectHandle, gadgetXmlData, gadgetViewData, serverData)).generate(monitor);
					(new JavaScriptFileGenerator(newProjectHandle, gadgetXmlData, gadgetViewData)).generate(monitor);
					(new WebServerLaunchConfigurationCreator()).create(newProjectHandle, serverData.getLocalPort(), monitor);
					ProjectPreferenceUtils.setLocalWebServerPort(newProjectHandle, serverData.getLocalPort());
					monitor.beginTask("Opening the Gadget XML file.", 1);
					getShell().getDisplay().syncExec(new Runnable() {
						public void run() {
							IWorkbenchWindow dw = getWorkbench().getActiveWorkbenchWindow();
							try {
								if (dw != null) {
									IWorkbenchPage page = dw.getActivePage();
									if (page != null) {
										IDE.openEditor(page, gadgetXmlFile, GadgetXmlEditor.ID, true);
									}
								}
							} catch (PartInitException e) {
								throw new RuntimeException(e);
							}
						}
					});
					monitor.worked(1);
					monitor.done();
				} catch(CoreException e) {
					throw new InvocationTargetException(e);
				} catch(UnsupportedEncodingException e) {
					throw new InvocationTargetException(e);
				} catch (MalformedURLException e) {
					throw new InvocationTargetException(e);
				} catch (IOException e) {
					throw new InvocationTargetException(e);
				}
			}
		};
		try {
			getContainer().run(true, true, op);
		} catch(InterruptedException e) {
			throw new RuntimeException(e);
		} catch(InvocationTargetException e) {
			Throwable t = e.getTargetException();
			if (t.getCause() instanceof CoreException) {
				CoreException cause = (CoreException)t.getCause();
				StatusAdapter status = new StatusAdapter(StatusUtil.newStatus(cause.getStatus().getSeverity(), "Error occurd at creating project.", cause));
				status.setProperty(IStatusAdapterConstants.TITLE_PROPERTY, "Error occurd at creating project.");
				StatusManager.getManager().handle(status, StatusManager.BLOCK);
			} else {
				StatusAdapter status = new StatusAdapter(StatusUtil.newStatus(IStatus.WARNING, "Error occurd at creating project.", t));
				status.setProperty(IStatusAdapterConstants.TITLE_PROPERTY, "Error occurd at creating project.");
				StatusManager.getManager().handle(status, StatusManager.BLOCK);
			}
			return null;
		}
		newProject = newProjectHandle;
		return newProject;
	}
	
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data) {
	}

}
