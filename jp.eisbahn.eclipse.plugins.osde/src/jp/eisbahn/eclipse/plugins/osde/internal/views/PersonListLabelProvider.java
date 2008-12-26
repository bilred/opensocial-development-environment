package jp.eisbahn.eclipse.plugins.osde.internal.views;

import jp.eisbahn.eclipse.plugins.osde.internal.Activator;

import org.apache.shindig.social.opensocial.model.Person;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

class PersonListLabelProvider extends LabelProvider implements ITableLabelProvider {

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
//		case 2:
//			return person.getDisplayName();
		default:
			return null;
		}
	}
	
}