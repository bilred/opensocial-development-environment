package jp.eisbahn.eclipse.plugins.osde.internal.views;

import jp.eisbahn.eclipse.plugins.osde.internal.Activator;
import jp.eisbahn.eclipse.plugins.osde.internal.ConnectionException;
import jp.eisbahn.eclipse.plugins.osde.internal.shindig.ApplicationService;

import org.apache.shindig.social.opensocial.hibernate.entities.ApplicationImpl;
import org.apache.shindig.social.opensocial.model.Activity;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class ActivityListLabelProvider extends LabelProvider implements ITableLabelProvider {
	
	public Image getColumnImage(Object element, int columnIndex) {
		switch(columnIndex) {
		case 0:
			ImageDescriptor descriptor = Activator.getDefault().getImageRegistry().getDescriptor("icons/comment_yellow.gif");
			return descriptor.createImage();
		default:
			return null;
		}
	}

	public String getColumnText(Object element, int columnIndex) {
		Activity activity = (Activity)element;
		switch(columnIndex) {
		case 1:
			return activity.getTitle();
		case 2:
			try {
				ApplicationService applicationService = Activator.getDefault().getApplicationService();
				ApplicationImpl application = applicationService.getApplication(activity.getAppId());
				return application.getTitle();
			} catch (ConnectionException e) {
				e.printStackTrace();
				return null;
			}
		default:
			return null;
		}
	}
	
}