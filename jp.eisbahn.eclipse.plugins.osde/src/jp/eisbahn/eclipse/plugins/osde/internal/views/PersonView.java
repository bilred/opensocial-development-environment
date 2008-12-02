package jp.eisbahn.eclipse.plugins.osde.internal.views;

import java.util.List;

import javax.persistence.EntityManager;

import jp.eisbahn.eclipse.plugins.osde.internal.Activator;
import jp.eisbahn.eclipse.plugins.osde.internal.shindig.PersonService;

import org.apache.shindig.social.opensocial.jpa.eclipselink.Bootstrap;
import org.apache.shindig.social.opensocial.model.Person;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.ScrolledPageBook;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.ViewPart;

public class PersonView extends ViewPart {

	private Action connectAction;
	private Action disconnectAction;
	
	private PersonService service = null;
	private TableViewer personList;

	private class PersonListContentProvider implements IStructuredContentProvider {
		
		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		public Object[] getElements(Object inputElement) {
			return ((List<Person>)inputElement).toArray();
		}
		
	}
	
	private class PersonListLabelProvider extends LabelProvider implements ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			switch(columnIndex) {
			case 0:
				ImageDescriptor descriptor = Activator.getDefault().getImageRegistry().getDescriptor("icons/icon_user.gif");
				return descriptor.createImage();
			default:
				return null;
			}
		}

		public String getColumnText(Object element, int columnIndex) {
			Person person = (Person)element;
			switch(columnIndex) {
			case 1:
				return person.getId();
			case 2:
				return person.getDisplayName();
			default:
				return null;
			}
		}
		
	}

	private final class DisconnectAction extends Action {
		public void run() {
			service.close();
			service = null;
		}
	}

	private class ConnectAction extends Action {
		public void run() {
			Bootstrap b = new Bootstrap("org.apache.derby.jdbc.ClientDriver",
					"jdbc:derby://localhost:1527/testdb;create=true", "sa", " ", "1", "1");
			EntityManager em = b.getEntityManager("default");
			service = new PersonService(em);
			//
			List<Person> people = service.getPeople();
			personList.setInput(people);
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
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		Form form = toolkit.createForm(parent);
		form.setText("People");
		Composite body = form.getBody();
//		body.setLayout(new GridLayout(2, false));
		//
		// Person list
//		Table table = toolkit.createTable(body, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
//		table.setHeaderVisible(true);
//		table.setLinesVisible(true);
//		GridData layoutData = new GridData(GridData.FILL_VERTICAL);
//		layoutData.widthHint = 200;
//		table.setLayoutData(layoutData);
//		TableColumn column = new TableColumn(table, SWT.LEFT, 0);
//		column.setText("");
//		column.setWidth(20);
//		column = new TableColumn(table, SWT.LEFT, 1);
//		column.setText("ID");
//		column.setWidth(80);
//		column = new TableColumn(table, SWT.LEFT, 2);
//		column.setText("Display name");
//		column.setWidth(100);
//		personList = new TableViewer(table);
//		personList.setContentProvider(new PersonListContentProvider());
//		personList.setLabelProvider(new PersonListLabelProvider());
		//
		ScrolledPageBook pageBook = toolkit.createPageBook(body, SWT.TOP);
		Composite basicPage = pageBook.createPage("basic");
		toolkit.createLabel(basicPage, "hoge");
		pageBook.showPage("basic");
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

}