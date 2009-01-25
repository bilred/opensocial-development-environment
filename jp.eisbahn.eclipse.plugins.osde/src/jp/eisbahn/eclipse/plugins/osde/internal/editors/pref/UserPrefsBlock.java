/**
 * 
 */
package jp.eisbahn.eclipse.plugins.osde.internal.editors.pref;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IDetailsPageProvider;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;

public class UserPrefsBlock extends MasterDetailsBlock {
	
	private UserPrefsPart userPrefsPart;
	
	private UserPrefsPage page;
	
	public UserPrefsBlock(UserPrefsPage page) {
		super();
		this.page = page;
	}

	@Override
	protected void createMasterPart(IManagedForm managedForm, Composite parent) {
		userPrefsPart = new UserPrefsPart(parent, managedForm, page);
		managedForm.addPart(userPrefsPart);
	}

	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
	}

	@Override
	protected void registerPages(DetailsPart detailsPart) {
		final IDetailsPage detailsPage = new UserPrefPage(page);
		detailsPart.registerPage(UserPrefModel.class, detailsPage);
		detailsPart.setPageProvider(new IDetailsPageProvider() {

			public IDetailsPage getPage(Object key) {
				if (key.equals(UserPrefModel.class)) {
					return detailsPage;
				}
				return null;
			}

			public Object getPageKey(Object object) {
				if (object instanceof UserPrefModel) {
					return UserPrefModel.class;
				}
				return object.getClass();
			}
			
		});
		sashForm.setWeights(new int[]{45, 55});
	}

	public void updateUserPrefModel() {
		userPrefsPart.markDirty();
	}
	
}