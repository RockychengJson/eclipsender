package com.ibm.gers.mailviewer;

import java.net.URL;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.ibm.gers.MailViewer";

	public static final String IMG_FORM_BG = "formBg";
	
	public static final String IMG_LINKTO_HELP = "linkto_help";
	
	public static final String IMG_FORM_ICON = "mail_16";
	
	public static final String IMG_FORM_ICON32 = "mail_32";
	
	public static final int PREFIX = 1;
	
	public static final int SUFFIX = 2;
	
	// Page ids
	public static final String PAGE_MAIL_VIEWER = "MailViewerPage";
	
	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	protected void initializeImageRegistry(ImageRegistry registry) {
		registerImage(registry, IMG_FORM_BG, "form_banner.gif");
		registerImage(registry, IMG_LINKTO_HELP, "linkto_help.gif");
		registerImage(registry, IMG_FORM_ICON, "mail_16.png");
		registerImage(registry, IMG_FORM_ICON32, "mail_32.png");
	}
	
	private void registerImage(ImageRegistry registry, String key,
			String fileName) {
		try {
			IPath path = new Path("icons/" + fileName); //$NON-NLS-1$
			URL url = find(path);
			if (url!=null) {
				ImageDescriptor desc = ImageDescriptor.createFromURL(url);
				registry.put(key, desc);
			}
		} catch (Exception e) {
		}
	}
	
	public Image getImage(String key) {
		return getImageRegistry().get(key);
	}
	
	public static Image getImageByExt(String ext){
		ImageRegistry registry = getDefault().getImageRegistry();
		if (registry.get(ext) == null) {
			Program p = Program.findProgram(ext);
			ImageData data = p.getImageData();
			ImageDescriptor image = ImageDescriptor.createFromImageData(data);
			registry.put(ext, image);
		}
		return registry.get(ext);
	}
	
	public static String getFileFix(String fileName, int fix){
		if (fileName != null) {
			if (fileName.indexOf(".") > -1){
				if (fix == PREFIX)
					return fileName.substring(0, fileName.lastIndexOf("."));
				if (fix == SUFFIX)
					return fileName.substring(fileName.lastIndexOf("."));
			} else {
				return fileName;
			}
		}
		return null;
	}
}
