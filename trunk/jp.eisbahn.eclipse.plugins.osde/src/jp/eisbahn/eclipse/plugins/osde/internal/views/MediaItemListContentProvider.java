/**
 * 
 */
package jp.eisbahn.eclipse.plugins.osde.internal.views;

import java.util.List;
import java.util.Map;

import org.apache.shindig.social.opensocial.model.MediaItem;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class MediaItemListContentProvider implements IStructuredContentProvider {
	
	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	public Object[] getElements(Object inputElement) {
		List<MediaItem> mediaItems = (List<MediaItem>)inputElement;
		return mediaItems.toArray();
	}
	
}