package jp.eisbahn.eclipse.plugins.osde.internal.views;

import jp.eisbahn.eclipse.plugins.osde.internal.Activator;
import jp.eisbahn.eclipse.plugins.osde.internal.ConnectionException;
import jp.eisbahn.eclipse.plugins.osde.internal.shindig.PersonService;

import org.apache.shindig.social.opensocial.model.Person;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;

public class ActivitiesView extends AbstractView {
	
	public static final String ID = "jp.eisbahn.eclipse.plugins.osde.internal.views.ActivitiesView";

	private Action reloadAction;

	private ActivitiesBlock block;
	
	public ActivitiesView() {
	}
	
	@Override
	protected void fillContextMenu(IMenuManager manager) {
		super.fillContextMenu(manager);
		manager.add(reloadAction);
	}

	@Override
	protected void fillLocalPullDown(IMenuManager manager) {
		super.fillLocalPullDown(manager);
		manager.add(reloadAction);
	}

	@Override
	protected void fillLocalToolBar(IToolBarManager manager) {
		super.fillLocalToolBar(manager);
		manager.add(reloadAction);
	}

	@Override
	protected void makeActions() {
		super.makeActions();
		reloadAction = new Action() {
			@Override
			public void run() {
				loadPeople();
			}
		};
		reloadAction.setText("Reload");
		reloadAction.setToolTipText("Reload people.");
		reloadAction.setImageDescriptor(
				Activator.getDefault().getImageRegistry().getDescriptor("icons/action_refresh.gif"));
	}

	protected void createForm(Composite parent) {
		IManagedForm managedForm = new ManagedForm(parent);
		block = new ActivitiesBlock(this);
		block.createContent(managedForm);
	}
	
	public void setFocus() {
	}
	
	private void loadPeople() {
		try {
			PersonService personService = Activator.getDefault().getPersonService();
			java.util.List<Person> people = personService.getPeople();
			block.setPeople(people);
		} catch(ConnectionException e) {
			MessageDialog.openError(getSite().getShell(), "Error", "Shindig database not started yet.");
		}
	}

	public void connectedDatabase() {
		loadPeople();
	}
	
}