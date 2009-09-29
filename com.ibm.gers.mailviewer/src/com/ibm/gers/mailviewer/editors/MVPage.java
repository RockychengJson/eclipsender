package com.ibm.gers.mailviewer.editors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.ibm.gers.mailviewer.Activator;
import com.ibm.gers.mailviewer.Mail;

import exc.client.WCBase64;
import exc.client.WCBase64Exception;
import exs.serv.WCMailAttachment;

public class MVPage extends FormPage {
	
	private final MVPage page_;
	
	private Mail mail_;
	
	private Table tableAttachements;
	
	private Button buttonAdd;
	
	private Button buttonSaveAs;

	public MVPage(FormEditor editor) {
		super(editor, Activator.PAGE_MAIL_VIEWER, Messages.getString("MailViewerPage.title"));
		MVEditorInput editorInput = (MVEditorInput)editor.getEditorInput();
		mail_ = editorInput.getMail();
		this.setPartName(editorInput.getName());
		page_ = this;
	}

	protected void createFormContent(IManagedForm managedForm) {
		final ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
        form.setText(Messages.getString("MailViewerPage.title"));
        form.setMinWidth(500);
        form.setBackgroundImage(Activator.getDefault().getImage(Activator.IMG_FORM_BG));

		form.getBody().setLayout(new ColumnLayout());
		
		createMailSection(form, toolkit, Messages.getString("MailViewerPage.sectionMail.title"));
		createAttachmentsSection(form, toolkit, Messages.getString("MailViewerPage.sectionAttachments.title"));
	}
	
	private void createMailSection(final ScrolledForm form, FormToolkit toolkit, String title) {
		// Create the Mail Section
		Section sectionMail = toolkit.createSection(form.getBody(), Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
        sectionMail.addExpansionListener(new ExpansionAdapter() {
            public void expansionStateChanged(ExpansionEvent e) {
                form.reflow(true);
            }
        });
        sectionMail.setText(title);
        sectionMail.setDescription(Messages.getString("MailViewerPage.sectionMail.description"));
        
        // Create the SectionClient of the Mail Section
        Composite sectionMailClient = toolkit.createComposite(sectionMail);
        
        GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		sectionMailClient.setLayout(layout);
		
		Color labelColor = toolkit.getColors().getColor(IFormColors.TITLE);
		
		// Create controls on the Mail Section
		// TO
		Label labelTo = toolkit.createLabel(sectionMailClient, "TO:");
		labelTo.setForeground(labelColor);
		Text textTo = toolkit.createText(sectionMailClient, mail_.getTo());
		textTo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		textTo.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		textTo.setEditable(false);
		
		// CC
		if (!"".equals(mail_.getCC()) && mail_.getCC() != null){
			Label labelCC = toolkit.createLabel(sectionMailClient, "CC:");
			labelCC.setForeground(labelColor);
			Text textCC = toolkit.createText(sectionMailClient, mail_.getCC());
			textCC.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			textCC.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
			textCC.setEditable(false);
		}
		
		// BCC
		if (!"".equals(mail_.getBCC()) && mail_.getBCC() != null){
			Label labelBCC = toolkit.createLabel(sectionMailClient, "BCC:");
			labelBCC.setForeground(labelColor);
			Text textBCC = toolkit.createText(sectionMailClient, mail_.getBCC());
			textBCC.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			textBCC.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
			textBCC.setEditable(false);
		}
		
		// Subject
		Label labelSubject = toolkit.createLabel(sectionMailClient, "Subject:");
		labelSubject.setForeground(labelColor);
		Text textSubject = toolkit.createText(sectionMailClient, mail_.getSubject());
		textSubject.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		textSubject.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		textSubject.setEditable(false);
		
		// Content
		Label labelContent = toolkit.createLabel(sectionMailClient, "Content:");
		labelContent.setForeground(labelColor);
		labelContent.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		
		Text textContent = toolkit.createText(sectionMailClient, mail_.getBody(), SWT.V_SCROLL | SWT.WRAP);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 400;
		gd.heightHint = 200;
		gd.minimumWidth = 400;
		gd.minimumHeight = 200;
		textContent.setLayoutData(gd);
		textContent.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		
//		ScrolledFormText textContent = new ScrolledFormText(sectionMailClient, SWT.V_SCROLL | SWT.WRAP, true);
//		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
//		gd.minimumWidth = 300;
//		gd.minimumHeight = 200;
//		textContent.setLayoutData(gd);
//		textContent.setBackground(textSubject.getBackground());
//		textContent.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		
//		FormText rtextContent = textContent.getFormText();
//		Display display = Activator.getDefault().getWorkbench().getDisplay();
//		FormColors formColors = new FormColors(display);
//		Color activeLinkColor = formColors.createColor("activeLink", 175,225,200);
//		HyperlinkSettings linkSettings = new HyperlinkSettings(display);
//		linkSettings.setActiveForeground(activeLinkColor);
//		linkSettings.setHyperlinkUnderlineMode(HyperlinkSettings.UNDERLINE_HOVER);
//		rtextContent.setHyperlinkSettings(linkSettings);
//		rtextContent.setText(mail_.getBody(), false, true);
//		rtextContent.addHyperlinkListener(new HyperlinkAdapter() {
//			public void linkActivated(HyperlinkEvent e) {
//				String href = e.getHref().toString().toLowerCase();
//				//java.awt.Desktop.getDesktop().browse(URI.create(href));
//				Program program = Program.findProgram(".html");
//				program.execute(href);
//				}
//			});
		
        // Set the SectionClient of the Mail Section
        sectionMail.setClient(sectionMailClient);
        
        toolkit.paintBordersFor(sectionMailClient);
	}
	
	private void createAttachmentsSection(final ScrolledForm form, FormToolkit toolkit, String title) {
		// Create the Attachments Section
        Section sectionAttachements = toolkit.createSection(form.getBody(), Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
        sectionAttachements.setText(title);
        sectionAttachements.setDescription(Messages.getString("MailViewerPage.sectionAttachments.description"));
        sectionAttachements.addExpansionListener(new ExpansionAdapter() {
            public void expansionStateChanged(ExpansionEvent e) {
                form.reflow(true);
            }
        });
        
        // Create the SectionClient of the Attachments Section
        Composite sectionAttachementsClient = toolkit.createComposite(sectionAttachements);
        
        GridLayout layoutClient = new GridLayout();
        layoutClient.numColumns = 2;
        layoutClient.marginWidth = 0;
        layoutClient.marginHeight = 0;
		sectionAttachementsClient.setLayout(layoutClient);

		// Create a list (table) control on the Attachments Section
		tableAttachements = toolkit.createTable(sectionAttachementsClient, SWT.NULL);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 80;
		gd.widthHint = 200;
		tableAttachements.setLayoutData(gd);
		TableViewer viewer = new TableViewer(tableAttachements);
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				page_.enableButtons(!event.getSelection().isEmpty());
			}
		});
		viewer.setContentProvider(new AttachementsContentProvider());
		viewer.setLabelProvider(new AttachementsLabelProvider());
		viewer.setInput(getEditor().getEditorInput());
		toolkit.paintBordersFor(sectionAttachementsClient);
		
		Composite buttonsComposite = toolkit.createComposite(sectionAttachementsClient);
		buttonsComposite.setLayout(new GridLayout());
		buttonsComposite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		buttonAdd = toolkit.createButton(buttonsComposite, Messages.getString("MailViewerPage.buttonOpen"), SWT.PUSH);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		buttonAdd.setLayoutData(gd);
		buttonAdd.setEnabled(false);
		buttonAdd.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				TableItem[] items = page_.tableAttachements.getSelection();
				TableItem item = items.length > 0 ? items[0] : null;
				if (item != null) {
					WCMailAttachment attachment = (WCMailAttachment) item.getData();
					String fileName = attachment.getFileName();
					try {
						String fPrefix = Activator.getFileFix(fileName, Activator.PREFIX);
						String fSuffix = Activator.getFileFix(fileName, Activator.SUFFIX);
						File fTemp = File.createTempFile(fPrefix, fSuffix);
						FileOutputStream writeFile = new FileOutputStream(fTemp);
						writeFile.write(WCBase64.decode(attachment.getData()));
						writeFile.close();
						Program program = Program.findProgram(fSuffix);
						program.execute(fTemp.getAbsolutePath());
					} catch (IOException e) {
						page_.showError(e);
					} catch (WCBase64Exception e) {
						page_.showError(e);
					}
				}
			}});
		buttonSaveAs = toolkit.createButton(buttonsComposite, Messages.getString("MailViewerPage.buttonSaveAs"), SWT.PUSH);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		buttonSaveAs.setLayoutData(gd);
		buttonSaveAs.setEnabled(false);
		buttonSaveAs.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				TableItem items[] = page_.tableAttachements.getSelection();
				TableItem item = items.length > 0 ? items[0] : null;
				if (item != null) {
					WCMailAttachment attachment = (WCMailAttachment) item.getData();
					String fileName = attachment.getFileName();
					FileDialog dialog = new FileDialog (getSite().getShell(), SWT.SAVE);
					dialog.setFilterNames (new String [] {"File", "All Files (*.*)"});
					dialog.setFilterExtensions (new String [] {Activator.getFileFix(fileName, Activator.SUFFIX), "*.*"});
					dialog.setFileName (fileName);
					String absolutePath = dialog.open();
					if (absolutePath != null) {
						File fTemp = new File(absolutePath);
						try {
							FileOutputStream writeFile = new FileOutputStream(fTemp);
							writeFile.write(WCBase64.decode(attachment.getData()));
							writeFile.close();
						} catch (FileNotFoundException e) {
							page_.showError(e);
						} catch (IOException e) {
							page_.showError(e);
						} catch (WCBase64Exception e) {
							page_.showError(e);
						}
					}
				}
			}});
		
		// Set the SectionClient of the Attachments Section
		sectionAttachements.setClient(sectionAttachementsClient);
	}
	
	private void enableButtons(boolean enable){
		buttonAdd.setEnabled(enable);
		buttonSaveAs.setEnabled(enable);
	}
	
	public void showError(String msg){
		Exception e = new Exception(msg);
		showError(e);
	}
	
	private void showError(Exception e){
		MessageBox messageBox = new MessageBox(getSite().getShell(), SWT.ICON_ERROR | SWT.OK);
        messageBox.setText("Exception !!!");
        messageBox.setMessage(e.getMessage());
        messageBox.open();
	}

}
