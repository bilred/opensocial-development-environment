package jp.eisbahn.eclipse.plugins.osde.internal.views;

import java.util.List;

import org.apache.shindig.social.opensocial.model.Activity;
import org.apache.shindig.social.opensocial.model.Person;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IDetailsPageProvider;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;

public class ActivitiesBlock extends MasterDetailsBlock {

	private ActivitiesPart activitiesPart;
	private ActivitiesView activitiesView;
	
	public ActivitiesBlock(ActivitiesView activitiesView) {
		super();
		this.activitiesView = activitiesView;
	}

	@Override
	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
		activitiesPart = new ActivitiesPart(parent, managedForm, activitiesView);
		managedForm.addPart(activitiesPart);
	}

	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
	}

	@Override
	protected void registerPages(DetailsPart detailsPart) {
		final IDetailsPage detailsPage = new ActivityPage(activitiesView);
		detailsPart.registerPage(Activity.class, detailsPage);
		detailsPart.setPageProvider(new IDetailsPageProvider() {

			public IDetailsPage getPage(Object key) {
				if (key.equals(Activity.class)) {
					return detailsPage;
				}
				return null;
			}

			public Object getPageKey(Object object) {
				if (object instanceof Activity) {
					return Activity.class;
				}
				return object.getClass();
			}
			
		});
		sashForm.setWeights(new int[]{50, 50});
	}

	public void setPeople(List<Person> people) {
		activitiesPart.setPeople(people);
	}
}