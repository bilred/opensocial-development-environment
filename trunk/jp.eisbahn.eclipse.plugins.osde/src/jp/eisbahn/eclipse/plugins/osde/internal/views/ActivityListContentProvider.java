/**
 * 
 */
package jp.eisbahn.eclipse.plugins.osde.internal.views;

import java.util.List;

import org.apache.shindig.social.opensocial.model.Activity;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ActivityListContentProvider implements IStructuredContentProvider {
	
	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	public Object[] getElements(Object inputElement) {
		return ((List<Activity>)inputElement).toArray();
	}
	
}