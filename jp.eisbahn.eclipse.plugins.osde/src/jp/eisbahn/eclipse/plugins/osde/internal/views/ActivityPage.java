package jp.eisbahn.eclipse.plugins.osde.internal.views;

import static jp.eisbahn.eclipse.plugins.osde.internal.utils.Gadgets.string;
import static jp.eisbahn.eclipse.plugins.osde.internal.utils.Gadgets.stringFromTimeMillis;
import static jp.eisbahn.eclipse.plugins.osde.internal.utils.Gadgets.trim;

import org.apache.shindig.social.opensocial.model.Activity;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

public class ActivityPage implements IDetailsPage {
	
	private IManagedForm managedForm;
	
	private ActivitiesView activitiesView;
	
	private Activity activity;

	private Text titleText;

	private Text titleIdText;

	private Text bodyText;

	private Text bodyIdText;

	private Text externalIdText;

	private Text postedTimeText;

	private Text priorityText;

	private Text updatedText;

	private Text urlText;

	private TableViewer templateParamsList;

	private TableViewer mediaItemList;
	
	public ActivityPage(ActivitiesView activitiesView) {
		super();
		this.activitiesView = activitiesView;
	}

	public void createContents(Composite parent) {
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		parent.setLayout(layout);
		FormToolkit toolkit = managedForm.getToolkit();
		//
		Section detailSection = toolkit.createSection(parent, Section.TITLE_BAR);
		detailSection.setText("Details");
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		detailSection.setLayoutData(layoutData);
		Composite detailPane = toolkit.createComposite(detailSection);
		detailPane.setLayout(new GridLayout(2, false));
		detailSection.setClient(detailPane);
		//
		toolkit.createLabel(detailPane, "Title:");
		titleText = toolkit.createText(detailPane, "", SWT.BORDER);
		titleText.setEditable(false);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		titleText.setLayoutData(layoutData);
		//
		toolkit.createLabel(detailPane, "Title ID:");
		titleIdText = toolkit.createText(detailPane, "", SWT.BORDER);
		titleIdText.setEditable(false);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		titleIdText.setLayoutData(layoutData);
		//
		toolkit.createLabel(detailPane, "Body:");
		bodyText = toolkit.createText(detailPane, "", SWT.BORDER | SWT.MULTI);
		bodyText.setEditable(false);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.heightHint = 45;
		bodyText.setLayoutData(layoutData);
		//
		toolkit.createLabel(detailPane, "Body ID:");
		bodyIdText = toolkit.createText(detailPane, "", SWT.BORDER);
		bodyIdText.setEditable(false);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		bodyIdText.setLayoutData(layoutData);
		//
		toolkit.createLabel(detailPane, "URL:");
		urlText = toolkit.createText(detailPane, "", SWT.BORDER);
		urlText.setEditable(false);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		urlText.setLayoutData(layoutData);
		//
		toolkit.createLabel(detailPane, "Posted time:");
		postedTimeText = toolkit.createText(detailPane, "", SWT.BORDER);
		postedTimeText.setEditable(false);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		postedTimeText.setLayoutData(layoutData);
		//
		toolkit.createLabel(detailPane, "Updated:");
		updatedText = toolkit.createText(detailPane, "", SWT.BORDER);
		updatedText.setEditable(false);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		updatedText.setLayoutData(layoutData);
		//
		toolkit.createLabel(detailPane, "Priority:");
		priorityText = toolkit.createText(detailPane, "", SWT.BORDER);
		priorityText.setEditable(false);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		priorityText.setLayoutData(layoutData);
		//
		toolkit.createLabel(detailPane, "External ID:");
		externalIdText = toolkit.createText(detailPane, "", SWT.BORDER);
		externalIdText.setEditable(false);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		externalIdText.setLayoutData(layoutData);
		//
		Section mediaItemsSection = toolkit.createSection(parent, Section.TITLE_BAR);
		mediaItemsSection.setText("Media items");
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		mediaItemsSection.setLayoutData(layoutData);
		Composite mediaItemsPane = toolkit.createComposite(mediaItemsSection);
		mediaItemsPane.setLayout(new GridLayout(1, false));
		mediaItemsSection.setClient(mediaItemsPane);
		//
		Table table = toolkit.createTable(mediaItemsPane, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		layoutData = new GridData(GridData.FILL_BOTH);
		layoutData.heightHint = 100;
		table.setLayoutData(layoutData);
		TableColumn column = new TableColumn(table, SWT.LEFT, 0);
		column.setText("MIME type");
		column.setWidth(100);
		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText("Type");
		column.setWidth(100);
		column = new TableColumn(table, SWT.LEFT, 2);
		column.setText("URL");
		column.setWidth(150);
		mediaItemList = new TableViewer(table);
		mediaItemList.setContentProvider(new MediaItemListContentProvider());
		mediaItemList.setLabelProvider(new MediaItemListLabelProvider());
		//
		Section templateParamsSection = toolkit.createSection(parent, Section.TITLE_BAR);
		templateParamsSection.setText("Template parameters");
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		templateParamsSection.setLayoutData(layoutData);
		Composite templateParamsPane = toolkit.createComposite(templateParamsSection);
		templateParamsPane.setLayout(new GridLayout(1, false));
		templateParamsSection.setClient(templateParamsPane);
		//
		table = toolkit.createTable(templateParamsPane, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		layoutData = new GridData(GridData.FILL_BOTH);
		layoutData.heightHint = 100;
		table.setLayoutData(layoutData);
		column = new TableColumn(table, SWT.LEFT, 0);
		column.setText("Key");
		column.setWidth(150);
		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText("Value");
		column.setWidth(150);
		templateParamsList = new TableViewer(table);
		templateParamsList.setContentProvider(new TemplateParamListContentProvider());
		templateParamsList.setLabelProvider(new TemplateParamListLabelProvider());
	}

	public void initialize(IManagedForm managedForm) {
		this.managedForm = managedForm;
	}

	public void commit(boolean onSave) {
	}

	public void dispose() {
	}

	public boolean isDirty() {
		return false;
	}

	public boolean isStale() {
		return false;
	}

	public void refresh() {
	}

	public void setFocus() {
	}

	public boolean setFormInput(Object input) {
		return false;
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		activity = (Activity)((IStructuredSelection)selection).getFirstElement();
		titleText.setText(trim(activity.getTitle()));
		titleIdText.setText(trim(activity.getTitleId()));
		bodyText.setText(trim(activity.getBody()));
		bodyIdText.setText(trim(activity.getBodyId()));
		externalIdText.setText(trim(activity.getExternalId()));
		postedTimeText.setText(stringFromTimeMillis(activity.getPostedTime()));
		priorityText.setText(string(activity.getPriority()));
		updatedText.setText(string(activity.getUpdated()));
		urlText.setText(trim(activity.getUrl()));
		templateParamsList.setInput(activity.getTemplateParams());
		mediaItemList.setInput(activity.getMediaItems());
	}
	
}