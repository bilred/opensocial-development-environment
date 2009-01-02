package jp.eisbahn.eclipse.plugins.osde.internal.views;

import java.util.Map;

import jp.eisbahn.eclipse.plugins.osde.internal.Activator;
import jp.eisbahn.eclipse.plugins.osde.internal.ConnectionException;
import jp.eisbahn.eclipse.plugins.osde.internal.shindig.AppDataService;
import jp.eisbahn.eclipse.plugins.osde.internal.shindig.ApplicationService;
import jp.eisbahn.eclipse.plugins.osde.internal.shindig.PersonService;

import org.apache.shindig.social.opensocial.hibernate.entities.ApplicationImpl;
import org.apache.shindig.social.opensocial.model.Person;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

public class AppDataView extends AbstractView {
	
	public static final String ID = "jp.eisbahn.eclipse.plugins.osde.internal.views.AppDataView";
	private Combo personCombo;
	private Combo applicationCombo;
	private List keyList;
	private Text valueText;
	private Action reloadAction;
	
	public AppDataView() {
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
				loadPeopleAndApplications();
			}
		};
		reloadAction.setText("Reload");
		reloadAction.setToolTipText("Reload people and applications.");
		reloadAction.setImageDescriptor(
				Activator.getDefault().getImageRegistry().getDescriptor("icons/action_refresh.gif"));
	}

	protected void createForm(Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		Form form = toolkit.createForm(parent);
		Composite body = form.getBody();
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		body.setLayout(layout);
		Section section = toolkit.createSection(form.getBody(), Section.TITLE_BAR);
		GridData layoutData = new GridData(GridData.FILL_BOTH);
		section.setLayoutData(layoutData);
		section.setText("AppData");
		Composite composite = toolkit.createComposite(section);
		layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		composite.setLayout(layout);
		section.setClient(composite);
		//
		Composite selectionPanel = toolkit.createComposite(composite);
		layout = new GridLayout();
		layout.numColumns = 4;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		selectionPanel.setLayout(layout);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		selectionPanel.setLayoutData(layoutData);
		toolkit.createLabel(selectionPanel, "Person:");
		personCombo = new Combo(selectionPanel, SWT.READ_ONLY);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		personCombo.setLayoutData(layoutData);
		personCombo.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				updateDataMap();
			}
		});
		toolkit.createLabel(selectionPanel, "Application:");
		applicationCombo = new Combo(selectionPanel, SWT.READ_ONLY);
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		applicationCombo.setLayoutData(layoutData);
		applicationCombo.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				updateDataMap();
			}
		});
		//
		SashForm sashForm = new SashForm(composite, SWT.HORIZONTAL);
		layoutData = new GridData(GridData.FILL_BOTH);
		sashForm.setLayoutData(layoutData);
		sashForm.setSashWidth(5);
		keyList = new List(sashForm, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);
		keyList.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				String value = (String)keyList.getData(keyList.getItem(keyList.getSelectionIndex()));
				valueText.setText(value);
			}
		});
		valueText = new Text(sashForm, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		valueText.setEditable(false);
		valueText.setBackground(toolkit.getColors().getBackground());
		sashForm.setWeights(new int[] {40, 60});
	}
	
	public void setFocus() {
	}
	
	private void updateDataMap() {
		try {
			Person person = (Person)personCombo.getData(personCombo.getText());
			ApplicationImpl application = (ApplicationImpl)applicationCombo.getData(applicationCombo.getText());
			AppDataService appDataService = Activator.getDefault().getAppDataService();
			Map<String, String> dataMap = appDataService.getApplicationDataMap(person, application);
			keyList.removeAll();
			valueText.setText("");
			if (dataMap != null) {
				for (Map.Entry<String, String> entry : dataMap.entrySet()) {
					keyList.add(entry.getKey());
					keyList.setData(entry.getKey(), entry.getValue());
				}
			}
		} catch(ConnectionException e) {
			MessageDialog.openError(getSite().getShell(), "Error", "Shindig database not started yet.");
		}
	}
	
	private void loadPeopleAndApplications() {
		try {
			PersonService personService = Activator.getDefault().getPersonService();
			java.util.List<Person> people = personService.getPeople();
			personCombo.removeAll();
			for (Person person : people) {
				personCombo.add(person.getId());
				personCombo.setData(person.getId(), person);
			}
			personCombo.select(0);
			ApplicationService applicationService = Activator.getDefault().getApplicationService();
			java.util.List<ApplicationImpl> applications = applicationService.getApplications();
			applicationCombo.removeAll();
			for (ApplicationImpl application : applications) {
				applicationCombo.add(application.getTitle());
				applicationCombo.setData(application.getTitle(), application);
			}
			applicationCombo.select(0);
			updateDataMap();
		} catch(ConnectionException e) {
			MessageDialog.openError(getSite().getShell(), "Error", "Shindig database not started yet.");
		}
	}

	public void connectedDatabase() {
		loadPeopleAndApplications();
	}
	
}