package jp.eisbahn.eclipse.plugins.osde.internal.views;

import java.util.List;

import jp.eisbahn.eclipse.plugins.osde.internal.Activator;
import jp.eisbahn.eclipse.plugins.osde.internal.ConnectionException;
import jp.eisbahn.eclipse.plugins.osde.internal.shindig.LaunchShindigAction;
import jp.eisbahn.eclipse.plugins.osde.internal.shindig.PersonService;

import org.apache.shindig.social.opensocial.model.Person;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.part.ViewPart;

public class PersonView extends ViewPart {
	
	public static final String ID = "jp.eisbahn.eclipse.plugins.osde.internal.views.PersonView";
	
	private LaunchShindigAction launchShindigAction;

	private PeopleBlock block;

	public PersonView() {
	}
	
	public void createPartControl(Composite parent) {
		createForm(parent);
		makeActions();
		hookContextMenu();
		contributeToActionBars();
	}

	private void createForm(Composite parent) {
		IManagedForm managedForm = new ManagedForm(parent);
		block = new PeopleBlock(this);
		block.createContent(managedForm);
	}
	
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				PersonView.this.fillContextMenu(manager);
			}
		});
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(launchShindigAction);
//		manager.add(new Separator());
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(launchShindigAction);
//		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(launchShindigAction);
	}

	private void makeActions() {
		launchShindigAction = new LaunchShindigAction(this);
		launchShindigAction.setText("Launch Shindig");
		launchShindigAction.setToolTipText("Launch Apache Shindig server.");
		launchShindigAction.setImageDescriptor(
				Activator.getDefault().getImageRegistry().getDescriptor("icons/icon_component.gif"));
	}

	public void setFocus() {
	}

	public void connectedDatabase() {
		try {
			PersonService personService = Activator.getDefault().getPersonService();
			List<Person> people = personService.getPeople();
			block.setPeople(people);
		} catch(ConnectionException e) {
			MessageDialog.openError(getSite().getShell(), "Error", "Shindig database not started yet.");
		}
	}
	
}