/**
 * 
 */
package com.ibm.scm.pdsctools.wizards;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.httpclient.NameValuePair;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.w3c.dom.Document;

import com.ibm.scm.pdsctools.Activator;
import com.ibm.scm.pdsctools.entities.Operation;
import com.ibm.scm.pdsctools.preferences.PreferenceConstants;
import com.ibm.scm.pdsctools.utils.HttpTool;
import com.ibm.scm.pdsctools.utils.XmlTool;

/**
 * @author Simon
 *
 */
public class OpListPage extends WizardPage {
	
	public static final String PAGE_NAME = "com.ibm.scm.pdsctools.wizards.OpListPage";
	
	private static final String DESCRIPTION = "2. Get the operation list from PDSC.";
	
	private static final String DEFUALT_FILTER_TEXT = "type filter text here";
	
	private Text filterText;
	
	private CheckboxTableViewer chkTableViewer;
	
	private TableViewer tableViewer;
	
	private Button btnFetch;
	
	private Button btnAll;
	
	private Button btnNone;
	
	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public OpListPage() {
		super(PAGE_NAME);
		setTitle(PDSCWizard.TITLE);
		setDescription(DESCRIPTION);
		setPageComplete(false);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());
		setControl(container);
		// Create the search textbox
		filterText = new Text(container, SWT.BORDER);
		filterText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		filterText.setText(DEFUALT_FILTER_TEXT);
		filterText.addModifyListener(new ModifyListener(){
			
			private Timer timer;
			
			private String filterString;
			
			public void modifyText(ModifyEvent e) {
				if (timer != null){
					timer.cancel();
					timer.purge();
				}
				timer = new Timer();
				filterString = filterText.getText();
				timer.schedule(new TimerTask(){

					public void run() {
						// Set filter
						final TableViewerFilter filter = new TableViewerFilter(filterString);
						Display.getDefault().asyncExec(new Runnable(){

							public void run() {
								ViewerFilter[] filters = tableViewer.getFilters();
								for(int i=0; i<filters.length; i++){
									tableViewer.removeFilter(filters[i]);
								}
								tableViewer.addFilter(filter);
								timer.cancel();
							}
							
						});
					}
				}, 500);
			}
			
		});
		//filterText.addKeyListener(listener)
		// Create tableviewer
		Composite tvContainer = new Composite(container, SWT.NONE);
		tvContainer.setLayout(new FillLayout());
		tvContainer.setLayoutData(new GridData(GridData.FILL_BOTH));
		createTableView(tvContainer);
		// Set content provicer
		tableViewer.setContentProvider(new TableViewerContentProvider());
		// Set label provider
		tableViewer.setLabelProvider(new TableViewerLabelProvider());
		Composite btnGroup = new Composite(container, SWT.NONE);
		btnGroup.setLayout(new GridLayout(3, false));
		btnGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// Create Fetch Button
		createFetchButton(btnGroup);
		// Create All Button
		createAllButton(btnGroup);
		// Create None Button
		createNoneButton(btnGroup);
		// Set Finish Button
		setPageComplete(false);
	}
	
	private void createTableView(Composite parent) {
		tableViewer = new TableViewer(parent, 
				SWT.CHECK | SWT.MULTI | SWT.BORDER | SWT.H_SCROLL);
		Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		TableLayout layout = new TableLayout();
		table.setLayout(layout);
		layout.addColumnData(new ColumnWeightData(200));
		new TableColumn(table, SWT.NONE).setText("Name");
		layout.addColumnData(new ColumnWeightData(300));
		new TableColumn(table, SWT.NONE).setText("Description");
		chkTableViewer = new CheckboxTableViewer(table);
		chkTableViewer.addCheckStateListener(new ICheckStateListener(){

			public void checkStateChanged(CheckStateChangedEvent event) {
				updateStatus(chkTableViewer.getCheckedElements().length > 0);
			}
			
		});
	}
	
	private void tableViewerBound(){
		// Set input data 
		try{
			IPreferenceStore store = Activator.getDefault().getPreferenceStore();
			String url = store.getString(PreferenceConstants.P_LOGIN);
			String cnum = store.getString(PreferenceConstants.P_CNUM);
			HttpTool httpTool = ((PDSCWizard)getWizard()).getHttpTool();
			NameValuePair[] params = new NameValuePair[1];
			params[0] = new NameValuePair("CNUM", cnum);
			Document html = httpTool.getHtmlDocument(url, params);
			XmlTool xmlTool = new XmlTool();
			List opList = xmlTool.getOperationList(html);
			tableViewer.setInput(opList);
			btnAll.setEnabled(opList.size() > 0);
			btnNone.setEnabled(opList.size() > 0);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private void createFetchButton(Composite parent) {
		btnFetch = new Button(parent, SWT.NONE);
		btnFetch.setText("Fetch");
		btnFetch.setLayoutData(new GridData(80, 24));
		btnFetch.addMouseListener(new MouseListener(){
			private boolean pressed = false;
			public void mouseDoubleClick(MouseEvent e) {}
			public void mouseDown(MouseEvent e) {
				pressed = true;
			}
			public void mouseUp(MouseEvent e) {
				if (pressed) {
					btnFetch.setEnabled(false);
					tableViewerBound();
					btnFetch.setEnabled(true);
					pressed = false;
				}
			}
			
		});
	}
	
	private void createAllButton(Composite parent) {
		btnAll = new Button(parent, SWT.NONE);
		btnAll.setEnabled(false);
		btnAll.setText("All");
		btnAll.setLayoutData(new GridData(80, 24));
		btnAll.addMouseListener(new MouseListener(){
			private boolean pressed = false;
			public void mouseDoubleClick(MouseEvent e) {}
			public void mouseDown(MouseEvent e) {
				pressed = true;
			}
			public void mouseUp(MouseEvent e) {
				if (pressed) {
					if (chkTableViewer != null) {
						chkTableViewer.setAllChecked(true);
						updateStatus(chkTableViewer.getCheckedElements().length > 0);
					}
					pressed = false;
				}
			}
			
		});
	}
	
	private void createNoneButton(Composite parent) {
		btnNone = new Button(parent, SWT.NONE);
		btnNone.setEnabled(false);
		btnNone.setText("None");
		btnNone.setLayoutData(new GridData(80, 24));
		btnNone.addMouseListener(new MouseListener(){
			private boolean pressed = false;
			public void mouseDoubleClick(MouseEvent e) {}
			public void mouseDown(MouseEvent e) {
				pressed = true;
			}
			public void mouseUp(MouseEvent e) {
				if (pressed) {
					if (chkTableViewer != null) {
						chkTableViewer.setAllChecked(false);
						updateStatus(chkTableViewer.getCheckedElements().length > 0);
					}
					pressed = false;
				}
			}
			
		});
	}
	
	private void updateStatus(boolean complete){
		setPageComplete(complete);
	}
	
	/**
	 * TableViewerContentProvider
	 * 
	 * @author Simon
	 *
	 */
	private static class TableViewerContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof List){
				return ((List)inputElement).toArray();
			} else {
				return new Object[0];
			}
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}
	
	/**
	 * TableViewerLabelProvider
	 * 
	 * @author Simon
	 *
	 */
	private static class TableViewerLabelProvider implements ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			Operation op = (Operation) element;
			if (columnIndex == 0) return op.getName();
			if (columnIndex == 1) return op.getDescription();
			return "";
		}

		public void addListener(ILabelProviderListener listener) {
		}

		public void dispose() {
		}

		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
		}
		
	}
	
	private static class TableViewerFilter extends ViewerFilter {

		private String filterText;
		
		public TableViewerFilter(String filterText){
			if (filterText != null){
				this.filterText = filterText.toLowerCase();
			}
		}
		
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (element instanceof Operation) {
				Operation operation = (Operation) element;
				String opName = operation.getName().toLowerCase();
				if (opName.indexOf(filterText) > -1){
					return true;
				}
			}
			return false;
		}
		
	}

	/**
	 * @return the chkTableViewer
	 */
	public CheckboxTableViewer getChkTableViewer() {
		return chkTableViewer;
	}
	/**
	 * @param chkTableViewer the chkTableViewer to set
	 */
	public void setChkTableViewer(CheckboxTableViewer chkTableViewer) {
		this.chkTableViewer = chkTableViewer;
	}
	/**
	 * @return the tableViewer
	 */
	public TableViewer getTableViewer() {
		return tableViewer;
	}
	/**
	 * @param tableViewer the tableViewer to set
	 */
	public void setTableViewer(TableViewer tableViewer) {
		this.tableViewer = tableViewer;
	}
	
}

