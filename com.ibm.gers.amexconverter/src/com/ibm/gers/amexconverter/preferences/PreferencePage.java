package com.ibm.gers.amexconverter.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.ibm.gers.amexconverter.Activator;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	
	private RadioGroupFieldEditor fConnType;
	private StringFieldEditor fURL;
	private IntegerFieldEditor fPort;
	private StringFieldEditor fWURL;
	private StringFieldEditor fWCGI;
	
	public PreferencePage() {
		super(GRID);
	}
	
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		//setDescription("The preference page of AmexConverter.");
	}

	protected void createFieldEditors() {
		
		fConnType = new RadioGroupFieldEditor(PreferenceConstants.P_CONNTYPE,
						"Connection via:", 
						1,
						new String[][] {{"Direct", "Direct" }, {"HTTP", "HTTP"}}, 
						getFieldEditorParent());
		
		fURL = new StringFieldEditor(
					PreferenceConstants.P_URL, 
					"URL:", 
					getFieldEditorParent());
		fPort = new IntegerFieldEditor(
					PreferenceConstants.P_PORT, 
					"Port:", 
					getFieldEditorParent());
		fWURL = new StringFieldEditor(
					PreferenceConstants.P_WURL, 
					"WURL:", 
					getFieldEditorParent());
		fWCGI = new StringFieldEditor(
					PreferenceConstants.P_WCGI, 
					"WCGI:", 
					getFieldEditorParent());
		
		String sType = getPreferenceStore().getString(PreferenceConstants.P_CONNTYPE);
		
		if ("HTTP".equals(sType)){
			fURL.setEnabled(false, getFieldEditorParent());
			fPort.setEnabled(false, getFieldEditorParent());
			fWURL.setEnabled(true, getFieldEditorParent());
			fWCGI.setEnabled(true, getFieldEditorParent());
		} else {
			fURL.setEnabled(true, getFieldEditorParent());
			fPort.setEnabled(true, getFieldEditorParent());
			fWURL.setEnabled(false, getFieldEditorParent());
			fWCGI.setEnabled(false, getFieldEditorParent());
		}
		
		addField(fConnType);
		addField(fURL);
		addField(fPort);
		addField(fWURL);
		addField(fWCGI);
	}

	public void propertyChange(PropertyChangeEvent event) {
		super.propertyChange(event);
		if (event.getSource() instanceof RadioGroupFieldEditor){
			if (!event.getOldValue().equals(event.getNewValue())){
				if ("HTTP".equals(event.getNewValue())){
					fURL.setEnabled(false, getFieldEditorParent());
					fPort.setEnabled(false, getFieldEditorParent());
					fWURL.setEnabled(true, getFieldEditorParent());
					fWCGI.setEnabled(true, getFieldEditorParent());
				} else {
					fURL.setEnabled(true, getFieldEditorParent());
					fPort.setEnabled(true, getFieldEditorParent());
					fWURL.setEnabled(false, getFieldEditorParent());
					fWCGI.setEnabled(false, getFieldEditorParent());
				}
			}
		}
	}

}
