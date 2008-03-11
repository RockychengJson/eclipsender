package org.easy4.tools.generator.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import org.easy4.tools.generator.Activator;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(Constants.P_ENT_PRE, "");
		store.setDefault(Constants.P_ENT_PRE, "");
		store.setDefault(Constants.P_DAO_PRE, "");
		store.setDefault(Constants.P_DAO_SUB, "DAO");
		store.setDefault(Constants.P_DAI_PRE, "");
		store.setDefault(Constants.P_DAI_SUB, "DAOIF");
	}

}
