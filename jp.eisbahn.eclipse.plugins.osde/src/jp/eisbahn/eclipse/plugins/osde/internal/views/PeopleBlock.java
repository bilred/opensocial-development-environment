package jp.eisbahn.eclipse.plugins.osde.internal.views;


import java.util.List;

import org.apache.shindig.social.opensocial.model.Person;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IDetailsPageProvider;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;

class PeopleBlock extends MasterDetailsBlock {

	private PeoplePart peoplePart;
	private PersonView personView;
	
	public PeopleBlock(PersonView personView) {
		super();
		this.personView = personView;
	}

	@Override
	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
		peoplePart = new PeoplePart(parent, managedForm, personView);
		managedForm.addPart(peoplePart);
	}

	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
	}

	@Override
	protected void registerPages(DetailsPart detailsPart) {
		final IDetailsPage detailsPage = new PersonPage(personView);
		detailsPart.registerPage(Person.class, detailsPage);
		detailsPart.setPageProvider(new IDetailsPageProvider() {

			public IDetailsPage getPage(Object key) {
				if (key.equals(Person.class)) {
					return detailsPage;
				}
				return null;
			}

			public Object getPageKey(Object object) {
				if (object instanceof Person) {
					return Person.class;
				}
				return object.getClass();
			}
			
		});
		sashForm.setWeights(new int[]{30, 70});
	}

	public void setPeople(List<Person> people) {
		peoplePart.setPeople(people);
	}
}