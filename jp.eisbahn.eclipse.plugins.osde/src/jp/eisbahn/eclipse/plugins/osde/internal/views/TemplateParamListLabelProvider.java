package jp.eisbahn.eclipse.plugins.osde.internal.views;

import java.util.Map;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class TemplateParamListLabelProvider extends LabelProvider implements ITableLabelProvider {
	
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		Map.Entry<String, String> entry = (Map.Entry<String, String>)element;
		switch(columnIndex) {
		case 0:
			return entry.getKey();
		case 1:
			return entry.getValue();
		default:
			return null;
		}
	}
	
}