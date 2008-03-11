package org.easy4.tools.generator.wizards;

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (java).
 */

public class BasePage extends WizardPage {
	
	public final static String PAGE_NAME = "org.easy4.tools.beangenerator.wizards.BasePage";
	
	private final static String DESCRIPTION = "This wizard will create one entity bean that only have getters and setters " +
			" , one dao interface which provides the operation names and one dao class which provides the implemetation of the dao interface.";
	
	private static String driverClassName = "";
	
	private static String username = "";
	
	private static String password = "";
	
	private static String url = "";
	
	private static String library = "";
	
	private Text packageNameText;
	
	private Text driverClassNameText;
	
	private Text usernameText;
	
	private Text passwordText;
	
	private Text jdbcURLText;
	
	private Text driverLibraryText;
	
	private ISelection selection;
	

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public BasePage(ISelection selection) {
		super(PAGE_NAME);
		setTitle(MainWizard.TITLE);
		setDescription(DESCRIPTION);
		this.selection = selection;
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;
		
		// packageNameText
		Label label = new Label(container, SWT.NULL);
		label.setText("Package name:");

		packageNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		packageNameText.setLayoutData(gd);
		packageNameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		
		label = new Label(container, SWT.NULL);
		label.setText("");
		
		// driverClassNameText
		label = new Label(container, SWT.NULL);
		label.setText("Driver Class Name:");

		driverClassNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		driverClassNameText.setLayoutData(gd);
		driverClassNameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
				driverClassName = driverClassNameText.getText();
			}
		});
		
		label = new Label(container, SWT.NULL);
		label.setText("");
		
		// usernameText
		label = new Label(container, SWT.NULL);
		label.setText("Database Username:");

		usernameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		usernameText.setLayoutData(gd);
		usernameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
				username = usernameText.getText();
			}
		});
		
		label = new Label(container, SWT.NULL);
		label.setText("");
		
		// passwordText
		label = new Label(container, SWT.NULL);
		label.setText("Database Password:");

		passwordText = new Text(container, SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		passwordText.setLayoutData(gd);
		passwordText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
				password = passwordText.getText();
			}
		});
		
		label = new Label(container, SWT.NULL);
		label.setText("");
		
		// jdbcURLText
		label = new Label(container, SWT.NULL);
		label.setText("JDBC Connect URL:");

		jdbcURLText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		jdbcURLText.setLayoutData(gd);
		jdbcURLText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
				url = jdbcURLText.getText();
			}
		});
		
		label = new Label(container, SWT.NULL);
		label.setText("");
		
		// columnNameStartRowNoText
		label = new Label(container, SWT.NULL);
		label.setText("JDBC Driver Library:");

		driverLibraryText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		driverLibraryText.setLayoutData(gd);
		driverLibraryText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
				library = driverLibraryText.getText();
			}
		});
		
		Button btnSelLib = new Button(container, SWT.PUSH);
		btnSelLib.setText("Browse...");
		btnSelLib.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				selectLibrary();
			}
		});
		
		initialize();
		dialogChanged();
		setControl(container);
	}

	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */

	private void initialize() {
		if (selection != null && selection.isEmpty() == false
				&& selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() > 1)
				return;
			Object obj = ssel.getFirstElement();
			if (obj instanceof IPackageFragment) {
				IPackageFragment packageFragment = (IPackageFragment) obj;
				String path = packageFragment.getPath().removeFirstSegments(2).toString();
				packageNameText.setText(path.replace("/", "."));
				packageNameText.setEditable(false);
			}
		}
		
		driverClassNameText.setText(driverClassName != null ? driverClassName : "");
		usernameText.setText(username != null ? username : "");
		passwordText.setText(password != null ? password : "");
		jdbcURLText.setText(url != null ? url : "");
		driverLibraryText.setText(library != null ? library : "");
	}
	
	/**
	 * Uses the standard container selection dialog to choose the new value for
	 * the container field.
	 */

	private void selectLibrary() {
		FileDialog dialog = new FileDialog(getShell());
		driverLibraryText.setText(dialog.open());
	}
	/**
	 * Ensures that both text fields are set.
	 */

	private void dialogChanged() {

		if (getPackageName().length() == 0) {
			updateStatus("Package Name must be specified");
			return;
		}
		if (getDriverClassName().length() == 0) {
			updateStatus("Driver Class Name must be specified");
			return;
		}
		if (getUsername().length() == 0) {
			updateStatus("Username must be specified");
			return;
		}
		if (getURL().length() == 0) {
			updateStatus("URL must be specified");
			return;
		}
		if (getLibrary().length() == 0) {
			updateStatus("Library must be specified");
			return;
		}

		updateStatus(null);
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}
	
	public String getPackageName(){
		return packageNameText.getText();
	}

	public String getDriverClassName(){
		return driverClassNameText.getText();
	}
	
	public String getUsername(){
		return usernameText.getText();
	}
	
	public String getPassword(){
		return passwordText.getText();
	}
	
	public String getURL(){
		return jdbcURLText.getText();
	}
	
	public String getLibrary(){
		return driverLibraryText.getText();
	}
	
}