package com.evantage.zitcotest.view.purchase_order_status_updation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

import com.evantage.zitcotest.ZitcotestUI;
import com.evantage.zitcotest.domain.customer.Customer;
import com.evantage.zitcotest.domain.product.SupplierProduct;
import com.evantage.zitcotest.domain.purchase_order.CurrentPOID;
import com.evantage.zitcotest.domain.purchase_order.Item;
import com.evantage.zitcotest.domain.purchase_order.PurchaseOrder;
import com.evantage.zitcotest.domain.purchase_order.PurchaseOrderItemList;
import com.evantage.zitcotest.domain.purchase_order.PurchaseOrderPaymentTerms;
import com.evantage.zitcotest.domain.purchase_order.SelectedItems;
import com.evantage.zitcotest.domain.purchase_order_status_updation.FileUploadTypes;
import com.evantage.zitcotest.domain.purchase_order_status_updation.PurchaseOrderStatusTypes;
import com.evantage.zitcotest.domain.purchase_order_status_updation.PurchaseOrderStatusUpdation;
import com.evantage.zitcotest.domain.purchase_order_status_updation.PurchaseOrderUploadFile;
import com.evantage.zitcotest.domain.supplier.Supplier;
import com.evantage.zitcotest.event.DashboardEventBus;
import com.evantage.zitcotest.session.ZitcoSession;
import com.evantage.zitcotest.view.DashboardViewType;
import com.evantage.zitcotest.view.customer.CustomerList;
import com.evantage.zitcotest.view.purchase_order.ItemList;
import com.evantage.zitcotest.view.purchase_order.PurchaseOrderUploader;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Responsive;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Window;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class PurchaseOrderStatusUpdationView extends Panel implements View,Component{  

	public static final String ID = "purchaseOrderWindow";
	private CssLayout purchaseOrderPanel;
	private final VerticalLayout root;
	private PurchaseOrderStatusUpdationList purchaseOrderList = new PurchaseOrderStatusUpdationList();
	private ItemList itemList = new ItemList();
	private BeanItemContainer<SelectedItems> selectedItemContainer = itemList.getSelectedItemContainer();
	
	private PurchaseOrderUploader receiver = new PurchaseOrderUploader(); 
	private Table fileUploadTable = new Table();
	private BeanItemContainer<PurchaseOrderUploadFile> purchaseOrderFileUploadContainer = 
			new BeanItemContainer<PurchaseOrderUploadFile>(PurchaseOrderUploadFile.class);
	
	@PropertyId("po_number")
	private TextField poNumber;
	
//	@PropertyId("po_status")
//	private ComboBox poStatus;
	
	@PropertyId("shipment_timeline")
	private TextField shipmentTimeline;
	
	@PropertyId("EDD")
	@Temporal(TemporalType.TIMESTAMP)
	private DateField EDD;
	
	@PropertyId("EDA")
	@Temporal(TemporalType.TIMESTAMP)
	private DateField EDA;
	
	@PropertyId("file_upload_type")
	private ComboBox fileUploadType;

	@PropertyId("payment_due_date") 
	@Temporal(TemporalType.TIMESTAMP)
	private DateField paymentDueDate;
	
	
	private static PurchaseOrderStatusUpdationView purchaseOrderView;
	private VerticalLayout mainLayout=new VerticalLayout();

	private PurchaseOrder purchaseOrder;
    private VerticalSplitPanel vsp=new VerticalSplitPanel();
    private String poID = "";
    private String poNo = "";
    private String poStatusType;
    private Button savaBtn;
	public PurchaseOrderStatusUpdationView()
	{
		purchaseOrderView=this;
		addStyleName(ValoTheme.PANEL_BORDERLESS);
		setSizeFull();
		DashboardEventBus.register(this);

		root = new VerticalLayout();
		root.setSizeFull();
		root.setMargin(true);
		root.addStyleName("loginform-view");
		root.setSizeFull();
		setContent(root);
		Responsive.makeResponsive(root);
        
		Component content = buildComponent();
		root.addComponent(content);
		//root.addComponent(root);
		initializeTable();
		root.setExpandRatio(content, 1);
	}
	
	public static PurchaseOrderStatusUpdationView getPurchaseOrderViewObject(){
		return purchaseOrderView;
	}

	private Component buildComponent(){
		purchaseOrderPanel = new CssLayout();
		purchaseOrderPanel.addStyleName("dashboard-panels");
		purchaseOrderPanel.addComponent(buildLabels());
		purchaseOrderPanel.addComponent(purchaseOrderForm());
		purchaseOrderPanel.setSizeFull();
		return purchaseOrderPanel;
	}

	private Component buildLabels() {
		CssLayout labels = new CssLayout();
		labels.addStyleName("labels");

		Label supplier = new Label("Indent Form Updation");
		supplier.setSizeUndefined();
		supplier.addStyleName(ValoTheme.LABEL_H4);
		supplier.addStyleName(ValoTheme.LABEL_COLORED);
		labels.addComponent(supplier);
		return labels;
	}


	public Component purchaseOrderForm() {
		
		savaBtn = new Button("Save", new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {

//				if(poID != null && poID.trim().length() > 0){
//					updatePurchaseOrder();
//				}
//				else{
					savePurchaseOrderStatusUpdation();
//				}
				
//				if(validation()){
				
//				}else{
//					Notification.show("Please fill all the fields first=",Notification.TYPE_HUMANIZED_MESSAGE);
//				}
			}
//			else{
//				Notification.show("Please Select Item First",Notification.TYPE_WARNING_MESSAGE);
//			}
//		}
	});

		Button cancelBtn= new Button("Cancel", new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				UI.getCurrent().getNavigator().addView("IFUpdationList", new PurchaseOrderStatusUpdationList());
				UI.getCurrent().getNavigator().navigateTo(DashboardViewType.PURCHASEORDERSTATUSUPDATIONLIST.getViewName());
			}
		});

		mainLayout.setSpacing(true);
		
		FormLayout top_details = new FormLayout();
		top_details.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
		
		top_details.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
		
//********* Purchase Order Status Types add in drop down from table - Start		
		
		poNumber = new TextField("IF Number");
		top_details.addComponent(poNumber);
		
//		poStatus = new ComboBox();
//		poStatus.setCaption("IF Status");

//		Session purchaseOrderPaymentTermsSession= ZitcoSession.getSession();
//		BeanItemContainer<PurchaseOrderStatusTypes> purchaseOrderPaymentTermsContainer = 
//				new BeanItemContainer<PurchaseOrderStatusTypes>(PurchaseOrderStatusTypes.class);
//
//		List<PurchaseOrderStatusTypes> purchaseOrderPaymentTermsList = purchaseOrderPaymentTermsSession.createCriteria(PurchaseOrderStatusTypes.class).list();
//		poStatus.setContainerDataSource( purchaseOrderPaymentTermsContainer);
//
//		poStatus.addItems(purchaseOrderPaymentTermsList);
//		poStatus.setItemCaptionPropertyId("po_status_type");
//		poStatus.setWidth(250, UNITS_PIXELS);
//		poStatus.setImmediate(true);
//		top_details.addComponent(poStatus);
//		purchaseOrderPaymentTermsSession.close();
		
//********* Purchase Order Status Types add in drop down from table - End
		
		shipmentTimeline = new TextField("Shipment Timeline");
		top_details.addComponent(shipmentTimeline);
		
		EDD = new DateField();
		EDD.setValue(new Date());
		EDD.setDateFormat("dd-MM-yyyy");
		EDD.setCaption("Expected Date Departure");
		top_details.addComponent(EDD);
		
		EDA = new DateField();
		EDA.setValue(new Date());
		EDA.setDateFormat("dd-MM-yyyy");
		EDA.setCaption("Expected Date Arrival");
		top_details.addComponent(EDA);
		
		
		Button fileUploadButton = new Button("File Upload", new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				
				final Window addWindow = new Window("Upload Files");
				VerticalLayout mainItemLayout=new VerticalLayout();
				HorizontalLayout buttonLayout = new HorizontalLayout();
			    addWindow.setModal(false);
			    Panel itemPanel = new Panel();
			    addWindow.setContent(itemPanel);
			    addWindow.setWidth("400px");
			    addWindow.setHeight("400px");
			    
			    final FormLayout windowForm = new FormLayout();
			    windowForm.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
			    
			    fileUploadType = new ComboBox();
			    fileUploadType.setCaption("Select Type");

				Session fileUploadTypeSession= ZitcoSession.getSession();
				BeanItemContainer<FileUploadTypes> fileUploadTypesContainer = 
						new BeanItemContainer<FileUploadTypes>(FileUploadTypes.class);

				List<FileUploadTypes> fileUploadTypeList = fileUploadTypeSession.createCriteria(FileUploadTypes.class).list();
				fileUploadType.setContainerDataSource( fileUploadTypesContainer);

				fileUploadType.addItems(fileUploadTypeList);
				fileUploadType.setItemCaptionPropertyId("file_upload_type");
				fileUploadType.setWidth(200, UNITS_PIXELS);
				fileUploadType.setImmediate(true);
				windowForm.addComponent(fileUploadType);
				fileUploadTypeSession.close();
			    
				fileUploadType.addValueChangeListener(new ValueChangeListener () {
					@Override
					public void valueChange(ValueChangeEvent event) {
						FileUploadTypes fileUploadTypeObject = (FileUploadTypes) fileUploadType.getValue();
						final String fileUploadTypeName = fileUploadTypeObject.getFile_upload_type().trim();
						final TextField invoiceNo;
						final DateField invoiceDate;
						final TextField blNo;
						final DateField blDate;
						final TextField filePONumber;
						final DateField filePODate;
						
						windowForm.removeAllComponents();
						windowForm.addComponent(fileUploadType);
						
						if(fileUploadTypeName.equalsIgnoreCase("INVOICE") || fileUploadTypeName.equalsIgnoreCase("B/L")
								|| fileUploadTypeName.equalsIgnoreCase("PO")){
							
							if(fileUploadTypeName.equalsIgnoreCase("INVOICE")){
								invoiceNo = new TextField("Invoice #");
								windowForm.addComponent(invoiceNo);
								invoiceDate = new DateField();
								invoiceDate.setValue(new Date());
								invoiceDate.setDateFormat("dd-MM-yyyy");
								invoiceDate.setCaption("Invoice Date");
								windowForm.addComponent(invoiceDate);
								windowForm.addComponent(receiver.getFileUploaderComponent());
								
								Button invoiceButton=new Button("OK",new ClickListener(){
					                 
									@Override
									public void buttonClick(ClickEvent event) {
										PurchaseOrderUploadFile purchaseOrderUploadFileItem = new PurchaseOrderUploadFile();
									    purchaseOrderUploadFileItem.setPo_file_upload_type(fileUploadTypeName);
									    purchaseOrderUploadFileItem.setPo_file_upload_date(invoiceDate.getValue());
									    purchaseOrderUploadFileItem.setPo_file_upload_number(invoiceNo.getValue());
									    purchaseOrderUploadFileItem.setPo_file_upload_url(receiver.getFileName());
//									    purchaseOrderUploadFileItem.setPo_status_updation_id(this);
									    addUploadedFilesInTable(purchaseOrderUploadFileItem);
										
										UI.getCurrent().removeWindow(addWindow);
									}
								});
								windowForm.addComponent(invoiceButton);
							}
							else if(fileUploadTypeName.equalsIgnoreCase("B/L")){
								Component c = receiver.getFileUploaderComponent();
//								windowForm.removeComponent(c);
								blNo = new TextField("B/L #");
								windowForm.addComponent(blNo);
								blDate = new DateField();
								blDate.setValue(new Date());
								blDate.setDateFormat("dd-MM-yyyy");
								blDate.setCaption("B/L Date");
								windowForm.addComponent(blDate);
								windowForm.addComponent(c);
//						        UI.getCurrent().removeWindow(addWindow);
								
								Button blButton=new Button("OK",new ClickListener(){
					                 
									@Override
									public void buttonClick(ClickEvent event) {
										
										PurchaseOrderUploadFile purchaseOrderUploadFileItem = new PurchaseOrderUploadFile();
									    purchaseOrderUploadFileItem.setPo_file_upload_type(fileUploadTypeName);
									    purchaseOrderUploadFileItem.setPo_file_upload_date(blDate.getValue());
									    purchaseOrderUploadFileItem.setPo_file_upload_number(blNo.getValue());
									    purchaseOrderUploadFileItem.setPo_file_upload_url(receiver.getFileName());
//									    purchaseOrderUploadFileItem.setPo_status_updation_id(this);
									    addUploadedFilesInTable(purchaseOrderUploadFileItem);
										
										UI.getCurrent().removeWindow(addWindow);
									}
								});
								windowForm.addComponent(blButton);
							}
							else if(fileUploadTypeName.equalsIgnoreCase("PO")){
								Component c = receiver.getFileUploaderComponent();
//								windowForm.removeComponent(c);
								filePONumber = new TextField("PO #");
								windowForm.addComponent(filePONumber);
								filePODate = new DateField();
								filePODate.setValue(new Date());
								filePODate.setDateFormat("dd-MM-yyyy");
								filePODate.setCaption("PO Date");
								windowForm.addComponent(filePODate);
								windowForm.addComponent(c);
//						        UI.getCurrent().removeWindow(addWindow);
								
								Button blButton=new Button("OK",new ClickListener(){
					                 
									@Override
									public void buttonClick(ClickEvent event) {
										
										PurchaseOrderUploadFile purchaseOrderUploadFileItem = new PurchaseOrderUploadFile();
									    purchaseOrderUploadFileItem.setPo_file_upload_type(fileUploadTypeName);
									    purchaseOrderUploadFileItem.setPo_file_upload_date(filePODate.getValue());
									    purchaseOrderUploadFileItem.setPo_file_upload_number(filePONumber.getValue());
									    purchaseOrderUploadFileItem.setPo_file_upload_url(receiver.getFileName());
//									    purchaseOrderUploadFileItem.setPo_status_updation_id(this);
									    addUploadedFilesInTable(purchaseOrderUploadFileItem);
										
										UI.getCurrent().removeWindow(addWindow);
									}
								});
								windowForm.addComponent(blButton);
							}
							
						}
						else if(fileUploadTypeName.equalsIgnoreCase("PACKAGING LIST") || 
								fileUploadTypeName.equalsIgnoreCase("OTHER") || 
								fileUploadTypeName.equalsIgnoreCase("SWIFT COPY")){
							    Component c = receiver.getFileUploaderComponent();
							    windowForm.addComponent(c);

							    Button blButton=new Button("OK",new ClickListener(){
									@Override
									public void buttonClick(ClickEvent event) {
										
										PurchaseOrderUploadFile purchaseOrderUploadFileItem = new PurchaseOrderUploadFile();
									    purchaseOrderUploadFileItem.setPo_file_upload_type(fileUploadTypeName);
									    purchaseOrderUploadFileItem.setPo_file_upload_url(receiver.getFileName());
//									    purchaseOrderUploadFileItem.setPo_status_updation_id(this);
									    addUploadedFilesInTable(purchaseOrderUploadFileItem);
										UI.getCurrent().removeWindow(addWindow);
									}
								});
								windowForm.addComponent(blButton);
						}
						
					}
				});
				
				
//			    Button windowDeleteButton=new Button("Save",new ClickListener(){
//	                 
//					@Override
//					public void buttonClick(ClickEvent event) {
//						
//						UI.getCurrent().removeWindow(addWindow);
//					}
//				});
//				
//				Button windowCancelButton=new Button("Cancel",new ClickListener(){
//					@Override
//					public void buttonClick(ClickEvent event) {
//						UI.getCurrent().removeWindow(addWindow);
//					}
//				});
//				
				mainItemLayout.addComponent(windowForm);
//				buttonLayout.addComponent(windowDeleteButton);
//				buttonLayout.addComponent(windowCancelButton);
//				mainItemLayout.addComponent(buttonLayout);
//				mainItemLayout.setComponentAlignment(buttonLayout, Alignment.BOTTOM_RIGHT);
				addWindow.setContent(mainItemLayout);
				//addWindow.setContent(buttonLayout);
				UI.getCurrent().addWindow(addWindow);
			    
			}
			
		});
		
		top_details.addComponent(fileUploadButton);
		
		top_details.addComponent(fileUploadTable);
		
		HorizontalLayout buttonLayout=new HorizontalLayout();
		buttonLayout.addComponent(savaBtn);
		buttonLayout.addComponent(cancelBtn);
		top_details.addComponent(buttonLayout);
		top_details.setComponentAlignment(buttonLayout, Alignment.BOTTOM_RIGHT);
		mainLayout.addComponent(top_details);
		vsp.addComponent(mainLayout);
		return  mainLayout;

	}
	
	public void initializeTable(){
		
		fileUploadTable.addStyleName(ValoTheme.TABLE_BORDERLESS);
		fileUploadTable.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
		fileUploadTable.addStyleName(ValoTheme.TABLE_COMPACT);
		fileUploadTable.setImmediate(true);
		fileUploadTable.setColumnCollapsingAllowed(true);
		fileUploadTable.setColumnCollapsible("time", false);
		fileUploadTable.setColumnCollapsible("price", false);
		fileUploadTable.setColumnReorderingAllowed(true);
		fileUploadTable.setSelectable(true);
		fileUploadTable.setWidth("100%");
		fileUploadTable.setHeight("150px");

		fileUploadTable.setContainerDataSource(purchaseOrderFileUploadContainer);
		fileUploadTable.setVisible(true);
		fileUploadTable.setVisibleColumns("po_file_upload_type","po_file_upload_number","po_file_upload_date","po_file_upload_url");
		fileUploadTable.setColumnHeaders("File Upload Type","Number","Date","File Name");
	}

 public void addUploadedFilesInTable(PurchaseOrderUploadFile purchaseOrderUploadFileBean){
	 purchaseOrderFileUploadContainer.addBean(purchaseOrderUploadFileBean); 	
 }	
	
	
// *************** Save Purchase Order Status Updation	
	public void savePurchaseOrderStatusUpdation(){
		
		Session session=ZitcoSession.getSession();
		PurchaseOrderStatusUpdation purchaseOrderStatusUpdationObject = new PurchaseOrderStatusUpdation();

//******* Update Status in Purchase Order - Start		
		updateStatusInPurchaseOrder();
//******* Update Status in Purchase Order - End		

		purchaseOrderStatusUpdationObject.setPo_status("INTRANSIT");
		purchaseOrderStatusUpdationObject.setShipment_timeline(shipmentTimeline.getValue());
		purchaseOrderStatusUpdationObject.setEDD(EDD.getValue());
		purchaseOrderStatusUpdationObject.setEDA(EDA.getValue());
		purchaseOrderStatusUpdationObject.setPo_id(Integer.parseInt(poID));
		session.save(purchaseOrderStatusUpdationObject);
		
		List<PurchaseOrderUploadFile> purchaseOrderUploadFileList = purchaseOrderFileUploadContainer.getItemIds();
		Iterator purchaseOrderUploadFileIterator = purchaseOrderUploadFileList.iterator();
		
		PurchaseOrderUploadFile tempPurchaseOrderUploadFileObj;
		PurchaseOrderUploadFile getValueOfPurchaseOrderUploadFileObjectFromBean;
		
		while(purchaseOrderUploadFileIterator.hasNext()){
			getValueOfPurchaseOrderUploadFileObjectFromBean = 
					(PurchaseOrderUploadFile) purchaseOrderUploadFileIterator.next();
			
			tempPurchaseOrderUploadFileObj = new PurchaseOrderUploadFile();
			String fileUploadType = getValueOfPurchaseOrderUploadFileObjectFromBean.getPo_file_upload_type();
			
			if(fileUploadType.equalsIgnoreCase("INVOICE")  || fileUploadType.equalsIgnoreCase("B/L")
					|| fileUploadType.equalsIgnoreCase("PO")){
				tempPurchaseOrderUploadFileObj.setPo_file_upload_type(fileUploadType);
				tempPurchaseOrderUploadFileObj.setPo_file_upload_number(
						getValueOfPurchaseOrderUploadFileObjectFromBean.getPo_file_upload_number());
				tempPurchaseOrderUploadFileObj.setPo_file_upload_date(
						getValueOfPurchaseOrderUploadFileObjectFromBean.getPo_file_upload_date());
				tempPurchaseOrderUploadFileObj.setPo_file_upload_url(
						getValueOfPurchaseOrderUploadFileObjectFromBean.getPo_file_upload_url());
			}
			else if(fileUploadType.equalsIgnoreCase("PACKAGING LIST") || fileUploadType.equalsIgnoreCase("OTHER") || 
					fileUploadType.equalsIgnoreCase("SWIFT COPY")){
				tempPurchaseOrderUploadFileObj.setPo_file_upload_type(fileUploadType);
				tempPurchaseOrderUploadFileObj.setPo_file_upload_url(
						getValueOfPurchaseOrderUploadFileObjectFromBean.getPo_file_upload_url());
			}
			
			tempPurchaseOrderUploadFileObj.setPurchaseOrderStatusUpdation(purchaseOrderStatusUpdationObject);
			tempPurchaseOrderUploadFileObj.getPurchaseOrderStatusUpdation().
			getPurchaseOrderStatusUpdationItemList().add(tempPurchaseOrderUploadFileObj);
			session.save(tempPurchaseOrderUploadFileObj);
		}
		session.getTransaction().commit();
		session.close();
		purchaseOrderFileUploadContainer.removeAllItems();
		setFiledsEmpty();

		UI.getCurrent().getNavigator().addView("IFUpdationList", new PurchaseOrderStatusUpdationList());
		UI.getCurrent().getNavigator().navigateTo(DashboardViewType.PURCHASEORDERSTATUSUPDATIONLIST.getViewName());
		Notification.show("IF Status Updated Successfully.....",Notification.TYPE_WARNING_MESSAGE);
	}

// ***************** Update Status in Purchase Order - Start 
	public void updateStatusInPurchaseOrder(){
		
			Session session=ZitcoSession.getSession();
//			BeanItemContainer<PurchaseOrder> selectedPurchaseOrderContainer= new BeanItemContainer<PurchaseOrder>(PurchaseOrder.class);
			List<PurchaseOrder> selectedPurchaseOrderList = session.createCriteria(PurchaseOrder.class).list();
			Iterator selectedPurchaseOrderIterator = selectedPurchaseOrderList.iterator();
			PurchaseOrder selectedPurchaseOrder;
			
			while(selectedPurchaseOrderIterator.hasNext()){
				
				selectedPurchaseOrder = (PurchaseOrder) selectedPurchaseOrderIterator.next();
				
				if(selectedPurchaseOrder.getPo_id() == Integer.parseInt(poID)){
					selectedPurchaseOrder.setPo_id(Integer.parseInt(poID));
					selectedPurchaseOrder.setPo_status_type("INTRANSIT");
					selectedPurchaseOrder.setPo_status_type_id(2);
					session.update(selectedPurchaseOrder);
					System.out.println("Update Status Type in PurchaseOrder also...");
				}
			}
			session.getTransaction().commit();
			session.close();
			
}
//******************* Update Status in Purchase Order - End

	
	public boolean validation(){

//		if(customerId.getValue() =="" || poNumber.getValue() == "" || poDate.getValue()==""
//				|| items.getValue()=="" || quantity.getValue()==""){
//			if(purchaseOrderId.getValue() ==null || purchaseOrderName.getValue() == null || purchaseOrderAddress.getValue()==null
//					|| purchaseOrderEmail.getValue()==null || purchaseOrderNTN.getValue()==null){
//				return false;
//			}
//		}

		return true;

	}
	
	
	public void setFiledsEmpty(){
		
		shipmentTimeline.setValue("");
		poNumber.setValue("");
		EDD.setValue(new Date());
		EDA.setValue(new Date());
	}


	@Override
	public void enter(ViewChangeEvent event) {
		try{
			poID = UI.getCurrent().getSession().getAttribute("poID").toString();
			poNo = UI.getCurrent().getSession().getAttribute("poNumber").toString();
			poStatusType = UI.getCurrent().getSession().getAttribute("poStatusType").toString();
		}
		catch(Exception e){
			poID = "";
			poNo = "";
		}
		
		if(poID != null && poID.trim().length() > 0){
			
			poNumber.setValue(poNo);
		}
		
		if(poStatusType != null && poStatusType.trim().length() > 0){
			
			String tempPOStatus = poStatusType.trim();
			Integer poUploadFileID = 0;
			
			if(tempPOStatus.equalsIgnoreCase("INTRANSIT") || tempPOStatus.equalsIgnoreCase("PENDING PAYMENT") ||
			   tempPOStatus.equalsIgnoreCase("CLOSED")){
				
				Session session=ZitcoSession.getSession();
				List<PurchaseOrderStatusUpdation> selectedPurchaseOrderList = 
						session.createCriteria(PurchaseOrderStatusUpdation.class).list();
				Iterator selectedPurchaseOrderIterator = selectedPurchaseOrderList.iterator();
				PurchaseOrderStatusUpdation selectedPurchaseOrder;
				
				while(selectedPurchaseOrderIterator.hasNext()){
					
					selectedPurchaseOrder = (PurchaseOrderStatusUpdation) selectedPurchaseOrderIterator.next();
					
					if(selectedPurchaseOrder.getPo_id() == Integer.parseInt(poID)){
						shipmentTimeline.setValue(selectedPurchaseOrder.getShipment_timeline());
						EDD.setValue(selectedPurchaseOrder.getEDD());
						EDA.setValue(selectedPurchaseOrder.getEDA());
						poUploadFileID = selectedPurchaseOrder.getPo_status_updation_id();
					} 
				}
				session.close();
				
				purchaseOrderFileUploadContainer.removeAllItems();
				
				if(poUploadFileID > 0){
					
					Session fileUploadSession = ZitcoSession.getSession();
					List<PurchaseOrderUploadFile> poUploadFilesList = fileUploadSession.createCriteria(PurchaseOrderUploadFile.class).list();
					Iterator poUploadFileIterator = poUploadFilesList.iterator();

					PurchaseOrderUploadFile tempPOUploadFileObj;
					PurchaseOrderStatusUpdation tempPOStatusUpdationObj;
					
					while(poUploadFileIterator.hasNext()){

						tempPOUploadFileObj = (PurchaseOrderUploadFile) poUploadFileIterator.next();
						tempPOStatusUpdationObj = tempPOUploadFileObj.getPurchaseOrderStatusUpdation();
						
						if(tempPOStatusUpdationObj.getPo_status_updation_id() == poUploadFileID){
							addUploadedFilesInTable(tempPOUploadFileObj);
//							System.out.println("fileUploadFileObjectId = "+tempPOUploadFileObj.getPo_file_upload_id()+"poUploadFileID= "+poUploadFileID);
						}
						
					}
					fileUploadSession.close();

				}
				savaBtn.setVisible(false);
				
				fileUploadTable.addListener(new ItemClickListener() {

					@Override
					public void itemClick(ItemClickEvent event) {

						if (event.isDoubleClick()){
							BeanItem<PurchaseOrderUploadFile> item= (BeanItem<PurchaseOrderUploadFile>)event.getItem();
							String fileURL = "D:/Zitco_File_Uploads/"+item.getBean().getPo_file_upload_url();
							publishReport(fileURL);
						}

					}
				});
			}
		} 
	}

	
	private void publishReport(final String path){

		StreamSource s = new StreamSource() {

			public java.io.InputStream getStream() {
				try {
					File f = new File(path);
					FileInputStream fis = new FileInputStream(f);
					return fis;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				return null;
			}
		};

		StreamSource ss = new StreamSource(){
			@Override
			public InputStream getStream() {
				return null;
			}

		};

		StreamResource r = new StreamResource(s, path);
		r.setCacheTime(-1);
		Embedded e = new Embedded();
		e.setSizeFull();
		e.setType(Embedded.TYPE_BROWSER);
		r.setMIMEType("application/pdf");
		e.setSource(r);
		e.setHeight("650px");
		Window plaintReoprtWindow = new Window("Report");
		plaintReoprtWindow.center();
		plaintReoprtWindow.setModal(true);
		plaintReoprtWindow.setHeight("700px");
		plaintReoprtWindow.setWidth("900px");
		plaintReoprtWindow.setContent(e);
		ZitcotestUI.getCurrent().addWindow(plaintReoprtWindow);
	}	

	
//*************** Add days in a calender and return updated date object -- Start	
	public static Date addDays(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }
//*************** Add days in a calender and return updated date object -- End

//public Supplier getPurchaseOrderViewSupplier(){
//	return (Supplier) supplierName.getValue();
//}

	
}


