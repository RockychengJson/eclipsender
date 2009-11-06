package com.ibm.gers.amexconverter.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.ibm.gers.amexconverter.Activator;

public class PreferenceInitializer  extends AbstractPreferenceInitializer {

	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.P_CONNTYPE, "Direct");
		store.setDefault(PreferenceConstants.P_URL, "127.0.0.1");
		store.setDefault(PreferenceConstants.P_PORT, 10116);
		store.setDefault(PreferenceConstants.P_WURL, "");
		store.setDefault(PreferenceConstants.P_WCGI, "");
		store.setDefault(PreferenceConstants.P_USERID, "");
		store.setDefault(PreferenceConstants.P_PASSWORD, "");
		store.setDefault(PreferenceConstants.P_CHECKBOX, false);
	}

}
