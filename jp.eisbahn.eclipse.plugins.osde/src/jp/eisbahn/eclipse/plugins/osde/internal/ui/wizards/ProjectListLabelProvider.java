package jp.eisbahn.eclipse.plugins.osde.internal.ui.wizards;

import jp.eisbahn.eclipse.plugins.osde.internal.Activator;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

class ProjectListLabelProvider extends LabelProvider implements ITableLabelProvider {

	public Image getColumnImage(Object element, int columnIndex) {
		switch(columnIndex) {
		case 0:
			ImageDescriptor descriptor = Activator.getDefault().getImageRegistry().getDescriptor("icons/project.gif");
			return descriptor.createImage();
		default:
			return null;
		}
	}

	public String getColumnText(Object element, int columnIndex) {
		IProject project = (IProject)element;
		switch(columnIndex) {
		case 1:
			return project.getName();
		default:
			return null;
		}
	}
	
}