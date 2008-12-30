package jp.eisbahn.eclipse.plugins.osde.internal.views;

import java.util.List;

import jp.eisbahn.eclipse.plugins.osde.internal.Activator;
import jp.eisbahn.eclipse.plugins.osde.internal.ConnectionException;
import jp.eisbahn.eclipse.plugins.osde.internal.shindig.PersonService;

import org.apache.shindig.social.opensocial.model.Person;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;

public class PersonView extends AbstractView {
	
	public static final String ID = "jp.eisbahn.eclipse.plugins.osde.internal.views.PersonView";
	
	private PeopleBlock block;

	public PersonView() {
	}
	
	protected void createForm(Composite parent) {
		IManagedForm managedForm = new ManagedForm(parent);
		block = new PeopleBlock(this);
		block.createContent(managedForm);
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