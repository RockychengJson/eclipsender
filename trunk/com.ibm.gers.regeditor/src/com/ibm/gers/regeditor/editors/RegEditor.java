package com.ibm.gers.regeditor.editors;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;

import com.ibm.gers.regeditor.Activator;
import com.ibm.gers.regeditor.preferences.PreferenceConstants;

import exc.com.WCBufferedStream;
import exc.xml.ERSConverter;
import exs.xml.WCXML;
import exs.xml.WCXMLException;
import exs.xml.XMLStatusWriter;

public class RegEditor extends TextEditor {
	
	public final static String PRODUCT_ID = "com.ibm.gers.ERSConverter";
	
	private final static String TEMP_FILE_PREFIX = "temp";
	private final static String XML_EXTENSION = ".xml";
	private final static String REG_EXTENSION = ".reg";
	private final static String POS_CONFIG = "poscfg.xml";
	private final static String TEMP_FOLDER_KEY = "java.io.tmpdir";

	private ColorManager colorManager;
	private ERSConverter converter;
	private long indentity;
	private String tempFolder;
	
	public ERSConverter getERSConverter() {
		return converter;
	}

	public long getIndentity() {
		return indentity;
	}

	public String getTempFolder() {
		return tempFolder;
	}
	
	public String getTempPathWithoutExtension(){
		return (this.tempFolder + TEMP_FILE_PREFIX + String.valueOf(this.indentity));
	}
	
	private IEditorInput getTempEditorInput(IEditorInput input) {
		IFileSystem fileSystem = EFS.getLocalFileSystem();
		IFileStore fileLocation = fileSystem.getStore(new Path(getTempPathWithoutExtension() + XML_EXTENSION)); 
		RegEditorInput tmpEditorInput = new RegEditorInput(fileLocation, input);
		return tmpEditorInput;
	}
	
	private String getWCXMLPath() {
		URL fileURL;
		try {
			fileURL = FileLocator.toFileURL(XMLStatusWriter.class.getClassLoader().getResource(POS_CONFIG));
			String path = fileURL.getPath();
			if (path.endsWith(POS_CONFIG)) {
				path =  path.substring(0, path.length() - 10);
			}
			if (path.startsWith("/")) {
				path = path.substring(1);
			}
			path.replace('/', '\\');
			return path;
		} catch (IOException e) {
			logError("ERSConverter.doRegToXML", e);
			e.printStackTrace();
		}
		
		return null;
	}
	
	private void reg2xml(IEditorInput input){
		try {
			String inFile = ((FileEditorInput)input).getPath().toString();
			if (inFile.endsWith(REG_EXTENSION))
	        {
				inFile = inFile.substring(0, inFile.length() - 4);
	        }
			inFile = inFile.replace('/', '\\');
			this.converter.doRegToXML(inFile, getTempPathWithoutExtension());
		} catch (WCXMLException e) {
			logError("ERSConverter.doRegToXML", e);
			e.printStackTrace();
		}
	}
	
	private void removeTempXmlFile(){
		String fileName =  getTempPathWithoutExtension() + XML_EXTENSION;
		IFileSystem fileSystem = EFS.getLocalFileSystem();
		IFileStore fileLocation = fileSystem.getStore(new Path(fileName));
		try {
			fileLocation.delete(0, null);
		} catch (CoreException e) {
			logError("removeTempXmlFile", e);
			e.printStackTrace();
		}
	}
	
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		
		XMLStatusWriter.init();
		WCXML.PATH_POSCFG = getWCXMLPath();
		this.converter = new ERSConverter();
        XMLStatusWriter.addListener(this.converter);
        
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        if ("true".equals(store.getString(PreferenceConstants.P_CHOICE))){
        	WCBufferedStream.setUseG1Stream(true);
        } else {
        	WCBufferedStream.setUseG1Stream(false);
        }
        
        this.indentity = Calendar.getInstance().getTimeInMillis();
        this.tempFolder = System.getProperty(TEMP_FOLDER_KEY);
        
        reg2xml(input);
        
        super.init(site, getTempEditorInput(input));
	}
	
	public RegEditor() {
		super();
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new XMLConfiguration(colorManager));
		setDocumentProvider(new XMLTextDocumentProvider());
	}
	
	public void dispose() {
		this.colorManager.dispose();
		this.converter = null;
		removeTempXmlFile();
		super.dispose();
	}
	
	protected void performSaveAs(IProgressMonitor progressMonitor) {
		super.performSaveAs(progressMonitor);
		xml2reg();
	}
	
	public void doSave(IProgressMonitor progressMonitor) {
		super.doSave(progressMonitor);
		xml2reg();
	}
	
	private void xml2reg(){
		try {
			RegEditorInput in = (RegEditorInput)this.getEditorInput();
			String inFile = in.getPath().toString();
			if (inFile.endsWith(REG_EXTENSION))
	        {
				inFile = inFile.substring(0, inFile.length() - 4);
	        }
			String outFile = inFile.replace('/', '\\');
			this.converter.doXMLToReg(getTempPathWithoutExtension(), outFile, false);
		} catch (WCXMLException e) {
			logError("xml2reg", e);
			e.printStackTrace();
		}
	}
	
	private void logError(String msg, Throwable exception) {
		Status st = new Status(Status.ERROR, PRODUCT_ID, msg, exception);
		Activator.getDefault().getLog().log(st);
	}

}
