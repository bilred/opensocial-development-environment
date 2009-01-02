package jp.eisbahn.eclipse.plugins.osde.internal.views;

import org.apache.shindig.social.opensocial.model.MediaItem;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class MediaItemListLabelProvider extends LabelProvider implements ITableLabelProvider {
	
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		MediaItem mediaItem = (MediaItem)element;
		switch(columnIndex) {
		case 0:
			return mediaItem.getMimeType();
		case 1:
			return mediaItem.getType().name();
		case 2:
			return mediaItem.getUrl();
		default:
			return null;
		}
	}
	
}