package com.ibm.gers.mailviewer.editors;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class AttachementsContentProvider implements IStructuredContentProvider {
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof MVEditorInput) {
			MVEditorInput input = (MVEditorInput) inputElement;
			return input.getMail().getAttachments();
		}
		return new Object[0];
	}
	public void dispose() {
	}
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}
