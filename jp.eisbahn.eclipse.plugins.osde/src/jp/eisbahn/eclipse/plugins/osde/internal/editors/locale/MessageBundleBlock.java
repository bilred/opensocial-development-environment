/**
 * 
 */
package jp.eisbahn.eclipse.plugins.osde.internal.editors.locale;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IDetailsPageProvider;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;

public class MessageBundleBlock extends MasterDetailsBlock {
	
	private SuportedLocalePart messageBundlesPart;
	
	private LocalePage page;
	
	public MessageBundleBlock(LocalePage page) {
		super();
		this.page = page;
	}

	@Override
	protected void createMasterPart(IManagedForm managedForm, Composite parent) {
		messageBundlesPart = new SuportedLocalePart(parent, managedForm, page);
		managedForm.addPart(messageBundlesPart);
	}

	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
	}

	@Override
	protected void registerPages(DetailsPart detailsPart) {
		final IDetailsPage detailsPage = new MessageBundlePage(page);
		detailsPart.registerPage(LocaleModel.class, detailsPage);
		detailsPart.setPageProvider(new IDetailsPageProvider() {

			public IDetailsPage getPage(Object key) {
				if (key.equals(LocaleModel.class)) {
					return detailsPage;
				}
				return null;
			}

			public Object getPageKey(Object object) {
				if (object instanceof LocaleModel) {
					return LocaleModel.class;
				}
				return object.getClass();
			}
			
		});
		sashForm.setWeights(new int[]{45, 55});
	}

	public void updateLocaleModel() {
		messageBundlesPart.markDirty();
	}
	
	public List<LocaleModel> getLocaleModels() {
		return messageBundlesPart.getLocaleModels();
	}
	
}