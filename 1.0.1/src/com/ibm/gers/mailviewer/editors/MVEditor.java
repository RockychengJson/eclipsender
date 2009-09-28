package com.ibm.gers.mailviewer.editors;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.*;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.ide.FileStoreEditorInput;

import com.ibm.gers.mailviewer.Mail;

import exs.serv.WCMailException;

/**
 * An GERS Mail Viewer which is built on a FormEditor.
 */
public class MVEditor extends FormEditor implements IResourceChangeListener{

	/**
	 * Create an editor.
	 */
	public MVEditor() {
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}
	
	/**
	 * The <code>MultiPageEditorExample</code> implementation of this method
	 * checks that the input is an instance of <code>IFileEditorInput</code>.
	 */
	public void init(IEditorSite site, IEditorInput editorInput)
		throws PartInitException {
		String file = null;
		
		if (editorInput instanceof FileStoreEditorInput)
			file = ((FileStoreEditorInput)editorInput).getURI().getPath();
		else if (editorInput instanceof IFileEditorInput)
			file = ((FileEditorInput)editorInput).getPath().toString();
		else
			throw new PartInitException("Invalid Input: Must be IFileEditorInput or FileStoreEditorInput.");
		
		Mail mail = new Mail();
		
		try {
			mail.fromDisk(file);
		} catch (WCMailException e) {
			throw new PartInitException("Invalid Input: " + e.getMessage());
		}
		
		setPartName(editorInput.getName());
		MVEditorInput input = new MVEditorInput(editorInput, mail);
		super.init(site, input);
	}

	/**
	 * The <code>MultiPageEditorPart</code> implementation of this 
	 * <code>IWorkbenchPart</code> method disposes all nested editors.
	 * Subclasses may extend.
	 */
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
	}
	
	/* (non-Javadoc)
	 * Method declared on IEditorPart.
	 */
	public boolean isSaveAsAllowed() {
		return false;
	}
	public void doSave(IProgressMonitor monitor) {}
	
	public void doSaveAs() {}
	
	public void resourceChanged(IResourceChangeEvent arg0) {
		// TODO Auto-generated method stub
	}
	protected void addPages() {
		// Add the page to the editor
		try {
			int index = addPage(new MVPage(this));
			setPageText(index, Messages.getString("MailViewerPage.name"));
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

}
