package com.googlecode.osde.internal.ui.views;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class OsdeExplorerLabelProvider implements ILabelProvider {

	private WorkbenchLabelProvider provider;
	
	public OsdeExplorerLabelProvider() {
		provider = new WorkbenchLabelProvider();
	}
	
	public Image getImage(Object element) {
		return provider.getImage(element);
	}

	public String getText(Object element) {
		return provider.getText(element);
	}

	public void addListener(ILabelProviderListener listener) {
		provider.addListener(listener);
	}

	public void dispose() {
		provider.dispose();
	}

	public boolean isLabelProperty(Object element, String property) {
		return provider.isLabelProperty(element, property);
	}

	public void removeListener(ILabelProviderListener listener) {
		provider.removeListener(listener);
	}

}
