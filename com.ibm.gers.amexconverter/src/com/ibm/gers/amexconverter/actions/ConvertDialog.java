package com.ibm.gers.amexconverter.actions;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

import com.ibm.gers.amexconverter.Activator;
import com.ibm.gers.amexconverter.preferences.PreferenceConstants;

import exc.client.GHost;
import exc.com.GRow;
import exc.gx.G;

public class ConvertDialog extends Dialog {
	
	private final static int BUTTON_CONN = 100;
	private final static int BUTTON_CONV = 101;
	
	private Label lblUserid;
	private Text eUserid;
	private Label lblPassword;
	private Text ePassword;
	private Button chkbox;
	private Label lblString;
	private Text eString;
	private Label lblResult;
	private Text eResult;
	
	private static Clipboard clipboard_ = null;
	private static IPreferenceStore store;

	protected ConvertDialog(Shell parentShell) {
		super(parentShell);
	}

	protected Control createDialogArea(Composite parent) {
		
		getShell().setText("AmexConverter");
		clipboard_ = Toolkit.getDefaultToolkit().getSystemClipboard();
		store = Activator.getDefault().getPreferenceStore();
		
		Composite container = (Composite) super.createDialogArea(parent);
		
		FillLayout layout = new FillLayout(SWT.VERTICAL);
		layout.spacing = 2;
		layout.marginWidth = 10;
		container.setLayout(layout);
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		
		Group group1 = new Group(container, SWT.NONE);
		group1.setText("1.Authenticate");
		group1.setLayout(gridLayout);
		
		boolean isRemember = store.getBoolean(PreferenceConstants.P_CHECKBOX);
		
		// UserId
		lblUserid = new Label(group1, SWT.NONE);
		lblUserid.setText("Userid:");
		eUserid = new Text(group1, SWT.BORDER);
		eUserid.setText(isRemember ? store.getString(PreferenceConstants.P_USERID) : "");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 200;
		eUserid.setLayoutData(gd);
		
		// Keep login Checkbox
		chkbox = new Button(group1, SWT.CHECK);
		chkbox.setText("Remember");
		chkbox.setSelection(isRemember);
		
		// Password
		lblPassword = new Label(group1, SWT.NONE);
		lblPassword.setText("Password:");
		ePassword = new Text(group1, SWT.BORDER | SWT.PASSWORD);
		ePassword.setText(isRemember ? store.getString(PreferenceConstants.P_PASSWORD) : "");
		ePassword.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Group group2 = new Group(container, SWT.NONE);
		group2.setText("2.Convert String");
		gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		group2.setLayout(gridLayout);
		
		lblString = new Label(group2, SWT.NONE);
		lblString.setText("String:");
		eString = new Text(group2, SWT.BORDER);
		eString.setText("");
		eString.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		eString.setEnabled(Activator.getDefault().isConnected());
		
		lblResult = new Label(group2, SWT.NONE);
		lblResult.setText("Result:");
		eResult = new Text(group2, SWT.BORDER);
		eResult.setText("");
		eResult.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		eResult.setEnabled(false);
		
		//new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);

		return container;
	}

	protected void createButtonsForButtonBar(Composite parent) {
		boolean connected = Activator.getDefault().isConnected();
		createButton(parent, BUTTON_CONN, "Connect", !connected);
		createButton(parent, BUTTON_CONV, "Convert", connected);
		getButton(BUTTON_CONN).setEnabled(!connected);
		getButton(BUTTON_CONV).setEnabled(connected);
	}
	
	protected void buttonPressed(int buttonId) {
		super.buttonPressed(buttonId);
		
		if (buttonId == BUTTON_CONN) {
			
			if(eUserid.getText().trim().equals("")){
				showMessage("Please input the userid.");
				eUserid.setFocus();
				return;
			}
			if(ePassword.getText().trim().equals("")){
				showMessage("Please input the password.");
				ePassword.setFocus();
				return;
			}
			
			if (Activator.getDefault().isConnected()){
				Activator.getDefault().setConnected(false);
				Activator.getDefault().setHost(null);
			}
			
			String sConnType = store.getString(PreferenceConstants.P_CONNTYPE);
			String sURL = store.getString(PreferenceConstants.P_URL);
			int sPort = store.getInt(PreferenceConstants.P_PORT);
			String sWURL = store.getString(PreferenceConstants.P_WURL); 
			String sWCGI = store.getString(PreferenceConstants.P_WCGI);

			store.setValue(PreferenceConstants.P_CHECKBOX, chkbox.getSelection());
			
			if (chkbox.getSelection()){
				store.setValue(PreferenceConstants.P_USERID, eUserid.getText());
				store.setValue(PreferenceConstants.P_PASSWORD, ePassword.getText());
			} else {
				store.setValue(PreferenceConstants.P_USERID, "");
				store.setValue(PreferenceConstants.P_PASSWORD, "");
			}
			
			Activator.getDefault().savePluginPreferences();
			
	        if (sConnType.equals("HTTP")){
	        	String wurl = sWURL;
	            String wcgi = sWCGI;
	            G.setHost(wurl, wcgi);
	        } else {
	        	String url = sURL;
				int port = sPort;
				G.setHost(url, port);
	        }
	        
			Activator.getDefault().setHost(G.getHost());
			
			GHost host = Activator.getDefault().getHost();
			
			String userid = eUserid.getText();
		    String password = ePassword.getText();
		    host.setUserid(userid);
		    host.setPassword(password);
		    GRow parm = new GRow();
		    parm.addText(userid);
		    parm.addText(password);
		    int rc = host.exec("mcuserlogon", parm);
		    if (rc == 0){
		    	Activator.getDefault().setConnected(true);
		    	getButton(BUTTON_CONN).setEnabled(false);
		    	getButton(BUTTON_CONV).setEnabled(true);
		    	eString.setEnabled(true);
		    	return;
		    }
	        
		    eString.setEnabled(false);
		    getButton(BUTTON_CONV).setEnabled(false);
		    showMessage(host.getText());
			
		} else if (buttonId == BUTTON_CONV) {
			String str;
			GRow res;
			
			if (!Activator.getDefault().isConnected()){
				showMessage("Connect to central first please.");
				return;
			}
			
			GRow parm = new GRow();
			parm.setText(0, "D");
			GRow flds = parm.getRow(1);
			flds.addText(eString.getText());
			GHost host = Activator.getDefault().getHost();
			int rc = host.exec("mcencrypt", parm);
			if (rc == 0){
				res = (GRow)host.getResult();
		        str = res.getText(0);
		        eResult.setText(str);
		        clipboard_.setContents(new StringSelection(str), null);
		        return;
			}
			
			parm.setText(0, "E");
		    rc = host.exec("mcencrypt", parm);
		    if (rc == 0){
		        res = (GRow) host.getResult();
		        str = res.getText(0);
		        eResult.setText(str);
		        clipboard_.setContents(new StringSelection(str), null);
		        return;
		    }
		    
		    showMessage(host.getText());
		    Activator.getDefault().setConnected(false);
		    getButton(BUTTON_CONN).setEnabled(true);
		    eString.setEnabled(false);
		    getButton(BUTTON_CONV).setEnabled(false);
		    
		} 
	}

	private void showMessage(String message){
		MessageBox messageBox = new MessageBox(getShell(), SWT.OK | SWT.ERROR);
		messageBox.setText("Message");
		messageBox.setMessage(message);
		messageBox.open();
	}
	
}
