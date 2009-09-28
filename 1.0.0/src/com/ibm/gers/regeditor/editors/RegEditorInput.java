package com.ibm.gers.regeditor.editors;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;

public class RegEditorInput extends FileStoreEditorInput implements
		IStorageEditorInput {
	
	private IFileStore fileStore;
	private IEditorInput originalInput;
	
	public RegEditorInput(IFileStore fileStore, IEditorInput orgInput){
		super(fileStore);
		this.fileStore = fileStore;
		this.originalInput = orgInput;
	}

	public IStorage getStorage() throws CoreException {
		RegStorage storage = new RegStorage(this.fileStore);
		return storage;
	}
	
	public String getName() {
		if (this.originalInput != null) {
			return originalInput.getName();
		}
		return fileStore.getName();
	}
	
	public String getToolTipText() {
		if (this.originalInput != null) {
			return originalInput.getToolTipText();
		}
		return fileStore.toString();
	}
	
	public IPath getPath() {
		if (this.originalInput instanceof FileEditorInput) {
			return ((FileEditorInput)this.originalInput).getPath();
		}
		
		return null;
	}

}
