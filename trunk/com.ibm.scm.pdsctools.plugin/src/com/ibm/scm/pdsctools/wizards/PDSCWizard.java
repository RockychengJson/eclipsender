package com.ibm.scm.pdsctools.wizards;

import org.apache.commons.httpclient.NameValuePair;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.operation.*;
import org.eclipse.jface.preference.IPreferenceStore;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;

import org.eclipse.ui.*;
import org.eclipse.ui.ide.IDE;
import org.w3c.dom.Document;

import com.ibm.scm.pdsctools.Activator;
import com.ibm.scm.pdsctools.entities.Operation;
import com.ibm.scm.pdsctools.entities.Parameter;
import com.ibm.scm.pdsctools.preferences.PreferenceConstants;
import com.ibm.scm.pdsctools.utils.HttpTool;
import com.ibm.scm.pdsctools.utils.XmlMaker;
import com.ibm.scm.pdsctools.utils.XmlTool;

/**
 * @author Simon
 */
public class PDSCWizard extends Wizard implements INewWizard {
	
	public final static String TITLE = "PDSC Configuration File";
	
	private HttpTool httpTool;
	
	private ISelection selection;

	/**
	 * Constructor for PDSCWizard.
	 */
	public PDSCWizard() {
		super();
		setWindowTitle("PDSCTools");
		setNeedsProgressMonitor(true);
		httpTool = new HttpTool();
	}
	
	/**
	 * Adding the page to the wizard.
	 */

	public void addPages() {
		addPage(new BasePage(selection));
		addPage(new OpListPage());
	}

	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	public boolean performFinish() {
		BasePage basePage = (BasePage) getPage(BasePage.PAGE_NAME);
		OpListPage opListPage = (OpListPage) getPage(OpListPage.PAGE_NAME);
		final String containerName = basePage.getContainerName();
		final String fileName = basePage.getFileName();
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		final String detailURL = store.getString(PreferenceConstants.P_DETAIL);
		final Operation[] selectedOperations = getOperations(opListPage.getChkTableViewer().getCheckedElements());
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					doFinish(containerName, fileName, detailURL, selectedOperations, monitor);
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
	
	private Operation[] getOperations (Object[] checkedItems){
		Operation[] operations = new Operation[checkedItems.length];
		for(int i=0; i<checkedItems.length; i++){
			operations[i] = new Operation();
			operations[i].setName(((Operation)checkedItems[i]).getName());
			operations[i].setDescription(((Operation)checkedItems[i]).getDescription());
			operations[i].setParameters(((Operation)checkedItems[i]).getParameters());
		}
		return operations;
	}
	
	/**
	 * The worker method. It will find the container, create the
	 * file if missing or just replace its contents, and open
	 * the editor on the newly created file.
	 */

	private void doFinish(
		String containerName,
		String fileName,
		String detailURL,
		Operation[] operations,
		IProgressMonitor monitor)
		throws CoreException {
		// create a sample file
		monitor.beginTask("Creating " + fileName, operations.length + 2);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = root.findMember(new Path(containerName));
		if (!resource.exists() || !(resource instanceof IContainer)) {
			throwCoreException("Container \"" + containerName + "\" does not exist.");
		}
		IContainer container = (IContainer) resource;
		final IFile file = container.getFile(new Path(fileName));
		monitor.worked(1);
		XmlTool xmlTool = new XmlTool();
		XmlMaker xmlMaker = new XmlMaker(file);
		for (int i=0; i<operations.length; i++){
			try {
				monitor.setTaskName("Fetch " + operations[i].getName() + " parameters...");
				NameValuePair[] params = new NameValuePair[1];
				params[0] = new NameValuePair("sub", operations[i].getName());
				Document detailHtml = httpTool.getHtmlDocument(detailURL, params);
				List parameterList = xmlTool.getDetails(detailHtml);
				Parameter[] parameters = (Parameter[]) parameterList.toArray(new Parameter[0]);
				operations[i].setParameters(parameters);
				xmlMaker.addOperation(operations[i]);
				monitor.worked(1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		xmlMaker.make();
		monitor.setTaskName("Opening file for editing...");
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				IWorkbench workbench = PlatformUI.getWorkbench();
				IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
				IWorkbenchPage page = workbenchWindow.getActivePage();
				try {
					IDE.openEditor(page, file, true);
				} catch (PartInitException e) {
				} 
			}
		});
		monitor.worked(1);
	}

	private void throwCoreException(String message) throws CoreException {
		IStatus status = new Status(IStatus.ERROR, "PDSCTools", IStatus.OK, message, null);
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
	
	public HttpTool getHttpTool(){
		return this.httpTool;
	}
}