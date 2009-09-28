package com.ibm.gers.regeditor.editors;

import java.io.InputStream;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

public class RegStorage implements IStorage {
	
	private IFileStore fileStore;
	
	public RegStorage(IFileStore fileStore){
		this.fileStore = fileStore;
	}

	public InputStream getContents() throws CoreException {
		return fileStore.openInputStream(EFS.NONE, null);
	}

	public IPath getFullPath() {
		String path = "";
		try {
			path = fileStore.toLocalFile(EFS.NONE, null).getPath();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return new Path(path);
	}

	public String getName() {
		return fileStore.getName();
	}

	public boolean isReadOnly() {
		return false;
	}

	public Object getAdapter(Class adapter) {
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

}
