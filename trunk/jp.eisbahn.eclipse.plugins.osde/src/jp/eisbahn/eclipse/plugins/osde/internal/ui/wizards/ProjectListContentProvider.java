/**
 * 
 */
package jp.eisbahn.eclipse.plugins.osde.internal.ui.wizards;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

class ProjectListContentProvider implements IStructuredContentProvider {
	
	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	public Object[] getElements(Object inputElement) {
		return ((List<IProject>)inputElement).toArray();
	}
	
}