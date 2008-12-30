package jp.eisbahn.eclipse.plugins.osde.internal.views;

import jp.eisbahn.eclipse.plugins.osde.internal.Activator;
import jp.eisbahn.eclipse.plugins.osde.internal.shindig.LaunchShindigAction;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;

public abstract class AbstractView extends ViewPart {
	
	private LaunchShindigAction launchShindigAction;

	protected void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				AbstractView.this.fillContextMenu(manager);
			}
		});
	}
	
	public void createPartControl(Composite parent) {
		createForm(parent);
		makeActions();
		hookContextMenu();
		contributeToActionBars();
	}

	protected abstract void createForm(Composite parent);

	protected void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	protected void fillLocalPullDown(IMenuManager manager) {
		manager.add(launchShindigAction);
//		manager.add(new Separator());
	}

	protected void fillContextMenu(IMenuManager manager) {
		manager.add(launchShindigAction);
//		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	protected void fillLocalToolBar(IToolBarManager manager) {
		manager.add(launchShindigAction);
	}

	protected void makeActions() {
		launchShindigAction = new LaunchShindigAction(this);
		launchShindigAction.setText("Launch Apache Shindig");
		launchShindigAction.setToolTipText("Launch Apache Shindig server.");
		launchShindigAction.setImageDescriptor(
				Activator.getDefault().getImageRegistry().getDescriptor("icons/icon_component.gif"));
	}

}
