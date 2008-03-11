package com.ibm.scm.pdsctools.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.ibm.scm.pdsctools.Activator;

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
		store.setDefault(PreferenceConstants.P_LOGIN, "http://scmdevr1.ie.ibm.com:9080/PDSCWebv2/Login");
		store.setDefault(PreferenceConstants.P_DETAIL, "http://scmdevr1.ie.ibm.com:9080/PDSCWebv2/List");
		store.setDefault(PreferenceConstants.P_CNUM, "201789672");
	}

}
