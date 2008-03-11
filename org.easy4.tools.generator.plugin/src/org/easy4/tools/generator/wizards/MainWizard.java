package org.easy4.tools.generator.wizards;

import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.easy4.tools.generator.Generator;
import org.easy4.tools.generator.entities.Table;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.operation.*;
import java.lang.reflect.InvocationTargetException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.*;

/**
 * This is a sample new wizard. Its role is to create a new file 
 * resource in the provided container. If the container resource
 * (a folder or a project) is selected in the workspace 
 * when the wizard is opened, it will accept it as the target
 * container. The wizard creates one file with the extension
 * "java". If a sample multi-page editor (also available
 * as a template) is registered for the same extension, it will
 * be able to open it.
 */

public class MainWizard extends Wizard implements INewWizard {
	
	private final static String NAME = "Bean Generator";
	
	protected final static String TITLE = "Data Access Operation Bean Generator";
	
	private BasePage basePage;
	
	private ListPage listPage;
	
	private ISelection selection;

	/**
	 * Constructor for Wizard.
	 */
	public MainWizard() {
		super();
		setWindowTitle(NAME);
		setNeedsProgressMonitor(true);
	}
	
	/**
	 * Adding the page to the wizard.
	 */

	public void addPages() {
		basePage = new BasePage(selection);
		listPage = new ListPage();
		addPage(basePage);
		addPage(listPage);
	}

	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	public boolean performFinish() {
		final String packageName = basePage.getPackageName();
		final Table[] tables = listPage.getTables();
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					doFinish(packageName, tables, monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * The worker method. It will find the container, create the
	 * file if missing or just replace its contents, and open
	 * the editor on the newly created file.
	 */

	private void doFinish(
		String packageName,
		Table[] tables,
		IProgressMonitor monitor)
		throws CoreException {
		// create a sample file
		monitor.beginTask("Creating ... ", tables.length);
		IPackageFragment pkg = null;
		if (selection != null && selection.isEmpty() == false
				&& selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() > 1)
				return;
			Object obj = ssel.getFirstElement();

			if (obj instanceof IJavaProject){
				// Select on the project name node
				IJavaProject project = (IJavaProject) obj;
				IPackageFragmentRoot[] pkgRoots = project.getAllPackageFragmentRoots();
				IPackageFragmentRoot root = null;
				for (int i = 0; i<pkgRoots.length; i++){
					if (pkgRoots[i].getKind() == IPackageFragmentRoot.K_SOURCE){
						root = pkgRoots[i];
						break;
					}
				}
				if (root.getPackageFragment(packageName).exists()){
					pkg = root.getPackageFragment(packageName);
				} else {
					pkg = root.createPackageFragment(packageName, true, null);
				}
			} else if (obj instanceof IPackageFragmentRoot){
				// Select on the source folder
				IPackageFragmentRoot root = (IPackageFragmentRoot) obj;
				if (root.getPackageFragment(packageName).exists()){
					pkg = root.getPackageFragment(packageName);
				} else {
					pkg = root.createPackageFragment(packageName, true, null);
				}
			} else if (obj instanceof IPackageFragment){
				// Slect on the package node
				pkg = (IPackageFragment) obj;
			} else {
				throwCoreException("You must click on the source folder or on the package!");
			}
		}
		for (int i=0; i<tables.length; i++){
			monitor.setTaskName("Create beans of table " + tables[i].getName() + "...");
			createFiles(tables[i], pkg);
			monitor.worked(1);
		}
		monitor.done();
	}
	
	private void createFiles(Table table, IPackageFragment pkg){
		
		try {
			Generator generator = new Generator();
			generator.generate(table, pkg);
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
		} catch (ParseErrorException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void throwCoreException(String message) throws CoreException {
		IStatus status =
			new Status(IStatus.ERROR, "org.easy4.tools.beangenerator.plugin", IStatus.OK, message, null);
		throw new CoreException(status);
	}

	/**
	 * We will accept the selection in the workbench to see if
	 * we can initialize from it.
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}
}