/**
 * 
 */
package jp.eisbahn.eclipse.plugins.osde.internal.views;

import java.util.Map;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class TemplateParamListContentProvider implements IStructuredContentProvider {
	
	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	public Object[] getElements(Object inputElement) {
		Map<String, String> templateParams = (Map<String, String>)inputElement;
		return templateParams.entrySet().toArray();
	}
	
}