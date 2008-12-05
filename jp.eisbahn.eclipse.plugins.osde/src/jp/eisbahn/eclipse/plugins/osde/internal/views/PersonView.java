package jp.eisbahn.eclipse.plugins.osde.internal.views;

import java.util.List;

import javax.persistence.EntityManager;

import jp.eisbahn.eclipse.plugins.osde.internal.shindig.PersonService;

import org.apache.shindig.social.opensocial.jpa.hibernate.Bootstrap;
import org.apache.shindig.social.opensocial.model.Person;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.part.ViewPart;

public class PersonView extends ViewPart {

	private Action connectAction;
	private Action disconnectAction;
	private Action reloadAction;
	
	private PersonService service = null;
	private PeopleBlock block;
	
	private PersonService s2 = null;

	private final class DisconnectAction extends Action {
		public void run() {
			service.close();
			service = null;
		}
	}

	private class ConnectAction extends Action {
		public void run() {
			Bootstrap b = new Bootstrap("org.h2.Driver",
					"jdbc:h2:tcp://localhost:9092/shindig", "sa", "", "1", "1");
			EntityManager em = b.getEntityManager("hibernate");
			service = new PersonService(em);
			//
			List<Person> people = service.getPeople();
			block.setPeople(people);
		}
	}

	private class ReloadAction extends Action {
		public void run() {
			if (s2 == null) {
				Bootstrap b = new Bootstrap("org.h2.Driver",
						"jdbc:h2:tcp://localhost:9092/shindig", "sa", "", "1", "1");
				EntityManager em = b.getEntityManager("hibernate");
				s2 = new PersonService(em);
			}
			Person johnDoe = s2.getJohnDoe();
			System.out.println(johnDoe.getDisplayName());
		}
	}

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
		manager.add(connectAction);
		manager.add(new Separator());
		manager.add(disconnectAction);
		manager.add(reloadAction);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(connectAction);
		manager.add(disconnectAction);
		manager.add(reloadAction);
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(connectAction);
		manager.add(disconnectAction);
		manager.add(reloadAction);
	}

	private void makeActions() {
		connectAction = new ConnectAction();
		connectAction.setText("Connect");
		connectAction.setToolTipText("Connect to Shindig Database");
		connectAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		//
		disconnectAction = new DisconnectAction();
		disconnectAction.setText("Disconnect");
		disconnectAction.setToolTipText("Disconnect to Shindig Database");
		disconnectAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		//
		reloadAction = new ReloadAction();
		reloadAction.setText("Reload");
		reloadAction.setToolTipText("Reload");
		reloadAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
	}

	public void setFocus() {
	}
	
	public PersonService getPersonService() {
		return service;
	}
	
}