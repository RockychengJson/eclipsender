package com.ibm.gers.mailviewer.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.ibm.gers.mailviewer.Mail;

public class MVEditorInput implements IEditorInput {
	
	private IEditorInput editorInput_;
	
	private Mail mail_;
	
	public MVEditorInput(IEditorInput editorInput, Mail mail){
		editorInput_ = editorInput;
		mail_ = mail;
	}
	
	public Mail getMail() {
		return mail_;
	}

	public boolean exists() {
		if (editorInput_.exists() && mail_.getTo() != null && "".equals(mail_.getTo())){
			return true;
		}
		return false;
	}

	public ImageDescriptor getImageDescriptor() {
		return editorInput_.getImageDescriptor();
	}

	public String getName() {
		return editorInput_.getName();
	}

	public IPersistableElement getPersistable() {
		return editorInput_.getPersistable();
	}

	public String getToolTipText() {
		return editorInput_.getToolTipText();
	}

	public Object getAdapter(Class arg0) {
		return null;
	}

}
