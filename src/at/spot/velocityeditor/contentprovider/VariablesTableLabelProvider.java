package at.spot.velocityeditor.contentprovider;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import at.spot.velocityeditor.contentprovider.dto.Variable;

public class VariablesTableLabelProvider extends LabelProvider implements ITableLabelProvider {

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return ((Variable) element).getName();
		case 1:
			return ((Variable) element).getValue();
		}
		
		return "";
	}
}
