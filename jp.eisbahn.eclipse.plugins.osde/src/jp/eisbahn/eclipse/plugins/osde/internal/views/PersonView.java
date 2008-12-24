package jp.eisbahn.eclipse.plugins.osde.internal.views;

import java.util.List;

import javax.persistence.EntityManager;

import jp.eisbahn.eclipse.plugins.osde.internal.shindig.PersonService;

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
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.classic.Session;

public class PersonView extends ViewPart {

	private Action connectAction;
	private Action disconnectAction;
	
	private PersonService service = null;
	private PeopleBlock block;

	private SessionFactory sessionFactory;

	private final class DisconnectAction extends Action {
		public void run() {
			
		}
	}
	
	@Override
	public void dispose() {
		super.dispose();
		if (sessionFactory != null && !sessionFactory.isClosed()) {
			sessionFactory.close();
		}
	}

	private class ConnectAction extends Action {
		public void run() {
			sessionFactory = new AnnotationConfiguration().configure().buildSessionFactory();
			Session session = sessionFactory.openSession();
			service = new PersonService(session);
			block.setPeople(service.getPeople());
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
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(connectAction);
		manager.add(disconnectAction);
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(connectAction);
		manager.add(disconnectAction);
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
	}

	public void setFocus() {
	}
	
	public PersonService getPersonService() {
		return service;
	}
	
}