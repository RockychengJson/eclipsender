package com.ibm.gers.mailviewer.editors;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.ibm.gers.mailviewer.Activator;

import exs.serv.WCMailAttachment;

public class AttachementsLabelProvider extends LabelProvider implements ITableLabelProvider {
	public String getColumnText(Object obj, int index) {
		if (obj instanceof WCMailAttachment) {
			return ((WCMailAttachment)obj).getFileName();
		}
		return obj.toString();
	}
	public Image getColumnImage(Object obj, int index) {
		if (obj instanceof WCMailAttachment) {
			String fileName = ((WCMailAttachment)obj).getFileName();
			if (fileName != null) {
				return Activator.getImageByExt(Activator.getFileFix(fileName, Activator.SUFFIX));
			}
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
		return null;
	}
}
