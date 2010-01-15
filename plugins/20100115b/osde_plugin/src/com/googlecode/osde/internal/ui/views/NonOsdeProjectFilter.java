package com.googlecode.osde.internal.ui.views;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.googlecode.osde.internal.OsdeProjectNature;
import com.googlecode.osde.internal.utils.Logger;

public class NonOsdeProjectFilter extends ViewerFilter {
	
	private static Logger logger = new Logger(NonOsdeProjectFilter.class);
	
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
        if(element instanceof IAdaptable){
            IAdaptable adaptable = (IAdaptable) element;
            Object adapted = adaptable.getAdapter(IProject.class);
            if(adapted instanceof IProject){
                IProject project = (IProject) adapted;
                try {
					return project.isOpen() && project.hasNature(OsdeProjectNature.ID);
				} catch (CoreException e) {
					logger.error(e.getMessage(), e);
				}
            }
        }
        return true;

	}

}
