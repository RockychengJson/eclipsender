package org.easy4.tools.generator.wizards;

import java.util.Arrays;
import java.util.List;

import org.easy4.tools.generator.DataBase;
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
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class ListPage extends WizardPage {
	
	public final static String PAGE_NAME = "org.easy4.tools.beangenerator.wizards.ListPage";

	private final static String DESCRIPTION = "Show the table list below.";
	
	private CheckboxTableViewer chkTableViewer;
	
	private TableViewer tableViewer;
	
	private Button btnFetch;
	
	private Button btnAll;
	
	private Button btnNone;

	protected ListPage() {
		super(PAGE_NAME);
		setTitle(MainWizard.TITLE);
		setDescription(DESCRIPTION);
		setPageComplete(false);
		
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());
		setControl(container);
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
			BasePage basePage = (BasePage) getPreviousPage();
			DataBase db = new DataBase();
			// Set param of db
			db.setDriverClass(basePage.getDriverClassName());
			db.setUsername(basePage.getUsername());
			db.setPassword(basePage.getPassword());
			db.setUrl(basePage.getURL());
			db.setLibrary(basePage.getLibrary());
			List tables = db.getTables();
			tableViewer.setInput(tables);
			btnAll.setEnabled(tables.size() > 0);
			btnNone.setEnabled(tables.size() > 0);
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
	
	public org.easy4.tools.generator.entities.Table[] getTables(){
		Object[] chkItems = chkTableViewer.getCheckedElements();
		List tableList = Arrays.asList(chkItems);
		return (org.easy4.tools.generator.entities.Table[]) tableList.toArray(new org.easy4.tools.generator.entities.Table[0]);
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
			org.easy4.tools.generator.entities.Table table;
			table = (org.easy4.tools.generator.entities.Table) element;
			if (columnIndex == 0) return table.getName();
			if (columnIndex == 1) return table.getDescription();
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

}
