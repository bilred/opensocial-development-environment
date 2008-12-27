package jp.eisbahn.eclipse.plugins.osde.internal.views;

import java.util.List;

import org.apache.shindig.social.opensocial.hibernate.entities.RelationshipImpl;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class FriendListContentProvider implements IStructuredContentProvider {

	public Object[] getElements(Object inputElement) {
		return ((List<RelationshipImpl>)inputElement).toArray();
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

}
