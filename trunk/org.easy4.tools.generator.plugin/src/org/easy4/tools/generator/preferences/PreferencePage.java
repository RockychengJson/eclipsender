package org.easy4.tools.generator.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.easy4.tools.generator.Activator;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class PreferencePage extends FieldEditorPreferencePage 
		implements IWorkbenchPreferencePage {

	public PreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("The preference page of BeanGenerator");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		Composite parent = getFieldEditorParent();
		addField(new StringFieldEditor(Constants.P_ENT_PRE, "Entity Bean Prefix:", parent));
		addField(new StringFieldEditor(Constants.P_ENT_SUB, "Entity Bean Subfix:", parent));
		addField(new StringFieldEditor(Constants.P_DAO_PRE, "DAO Class Prefix:", parent));
		addField(new StringFieldEditor(Constants.P_DAO_SUB, "DAO Class Subfix:", parent));
		addField(new StringFieldEditor(Constants.P_DAI_PRE, "DAO Interface Prefix:", parent));
		addField(new StringFieldEditor(Constants.P_DAI_SUB, "DAO Interface Subfix:", parent));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
}