package com.evantage.zitcotest.view.reports;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.hibernate.Session;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

import com.evantage.zitcotest.ZitcotestUI;
import com.evantage.zitcotest.domain.customer.Customer;
import com.evantage.zitcotest.domain.purchase_order_status_updation.PurchaseOrderStatusTypes;
import com.evantage.zitcotest.domain.supplier.Supplier;
import com.evantage.zitcotest.event.DashboardEventBus;
import com.evantage.zitcotest.session.ZitcoSession;
import com.evantage.zitcotest.util.HibernateUtil;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.Reindeer;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class ReportsView extends Panel implements View,Component{
	
	public static final String ID = "reportsWindow";
	private CssLayout reportsPanel;
	private final VerticalLayout root;

	private ReportsView reportsView;
	VerticalLayout mainLayout;
    
	String reportsID = "";

	public ReportsView()
	{
		reportsView=this;
		addStyleName(ValoTheme.PANEL_BORDERLESS);
		setSizeFull();
		DashboardEventBus.register(this);

		root = new VerticalLayout();
		root.setSizeFull();
		root.setMargin(true);
		root.addStyleName("loginform-view");
		setContent(root);
		Responsive.makeResponsive(root);

		Component content = buildComponent();
		root.addComponent(content);
		root.setExpandRatio(content, 1);

	}

	private Component buildComponent(){
		reportsPanel = new CssLayout();
		reportsPanel.addStyleName("dashboard-panels");
		reportsPanel.addComponent(buildLabels());
		reportsPanel.setImmediate(false);
		reportsPanel.addComponent(reportsForm());
		reportsPanel.setSizeFull();
		return reportsPanel;
	}

	private Component buildLabels() {
		CssLayout labels = new CssLayout();
		labels.addStyleName("labels");

		Label reports = new Label("Reports");
		reports.setIcon(FontAwesome.FILE_PDF_O);
		reports.setSizeUndefined();
		reports.addStyleName(ValoTheme.LABEL_H4);
		reports.addStyleName(ValoTheme.LABEL_COLORED);
		labels.addComponent(reports);

		return labels;
	}


	public Component reportsForm(){

		mainLayout=new VerticalLayout();
		
	    TabSheet tabSheet = new TabSheet();
		tabSheet.setStyleName(Reindeer.TABSHEET_MINIMAL);
		
		VerticalLayout suplierLayout = new VerticalLayout();
		suplierLayout.setMargin(true);
		suplierLayout.setSpacing(true);
		
		Tab supplierReportTab = tabSheet.addTab(suplierLayout, "Supplier Wise Report");
		supplierReportTab.setVisible(true);
		supplierReportTab.setCaption("Supplier Wise Report");
		
		final ComboBox supplierName= new ComboBox();
		supplierName.setCaption("Select Supplier");
		supplierName.setContainerDataSource(getSupplierIndexedContainer());
		supplierName.setItemCaptionPropertyId("supplier_name");
		supplierName.setWidth(250, UNITS_PIXELS);
		supplierName.setImmediate(true);
		suplierLayout.addComponent(supplierName);
		
	    Button supplierButton= new Button("Generate Report", new Button.ClickListener() {
	    	@Override
	    	public void buttonClick(ClickEvent event) {
	    		
	    		Supplier tempSupplier = getSupplierByName((Integer) supplierName.getValue());
	    		
	    		final HashMap<String, Object> supplierReportmap = new HashMap();
	    		supplierReportmap.put("supplier_id", tempSupplier.getSupplier_id());
	    		supplierReportmap.put("header", "D:/Zitco_File_Export/logo.png");
//	    		(, sc.getRealPath());
	    		String supplierReportURL = "D:/ZitcoJasperReports/Supplier_Wise_Report.jasper";
	    		generatePDF(supplierReportURL,supplierReportmap);
	    		publishReport("D:/ZitcoJasperReports/Supplier_Wise_Report.pdf");
	    	}
	    });
	    suplierLayout.addComponent(supplierButton);
	    
	    
	    VerticalLayout customerLayout = new VerticalLayout();
	    customerLayout.setMargin(true);
	    customerLayout.setSpacing(true);
		
		Tab customerReportTab = tabSheet.addTab(customerLayout, "Customer Wise Report");
		customerReportTab.setVisible(true);
		customerReportTab.setCaption("Customer Wise Report");
		
		
		final ComboBox customerName = new ComboBox();
        customerName.setCaption("Select Customer");
        Session session=(Session)ZitcoSession.getSession();
        BeanItemContainer<Customer> customerContainer = new BeanItemContainer<Customer>(Customer.class);
        List<Customer> customerList = session.createCriteria(Customer.class).list();
		customerName.setContainerDataSource(customerContainer);
        
		customerName.addItems(customerList);
		customerName.setItemCaptionPropertyId("customerName");
		customerName.setWidth(250, UNITS_PIXELS);
		customerName.setImmediate(true);
		customerLayout.addComponent(customerName);
		session.close();
		
	    Button customer= new Button("Generate Report", new Button.ClickListener() {
	    	@Override
	    	public void buttonClick(ClickEvent event) {
	    		    		
	    		Customer tempCustomer = (Customer) customerName.getValue();
	    		
	    		final HashMap<String, Object> customerReportmap = new HashMap();
	    		customerReportmap.put("customer_id", tempCustomer.getCustomerId());
	    		customerReportmap.put("logo", "D:/Zitco_File_Export/logo.png");
	    		String customerReportURL = "D:/ZitcoJasperReports/Customer_Wise_Report.jasper";
	    		generatePDF(customerReportURL,customerReportmap);
	    		publishReport("D:/ZitcoJasperReports/Customer_Wise_Report.pdf");
	    	}
	    });
	    
	    customerLayout.addComponent(customer);
	    
	    
	    final VerticalLayout statusLayout = new VerticalLayout();
	    statusLayout.setMargin(true);
	    statusLayout.setSpacing(true);
		
		Tab statusReportTab = tabSheet.addTab(statusLayout, "Status Wise Report");
		statusReportTab.setVisible(true);
		statusReportTab.setCaption("Status Wise Report");
		
		final ComboBox status = new ComboBox();
		status.setCaption("Select Status");

		Session tempSession= ZitcoSession.getSession();
		BeanItemContainer<PurchaseOrderStatusTypes> purchaseOrderPaymentTermsContainer = 
				new BeanItemContainer<PurchaseOrderStatusTypes>(PurchaseOrderStatusTypes.class);

		List<PurchaseOrderStatusTypes> purchaseOrderPaymentTermsList =  tempSession.createCriteria(PurchaseOrderStatusTypes.class).list();
		status.setContainerDataSource( purchaseOrderPaymentTermsContainer);

		status.addItems(purchaseOrderPaymentTermsList);
		status.setItemCaptionPropertyId("po_status_type");
		status.setWidth(250, UNITS_PIXELS);
		status.setImmediate(true);
		statusLayout.addComponent(status);
		tempSession.close();
		
	    Button statusButton= new Button("Generate Report", new Button.ClickListener() {
	    	@Override
	    	public void buttonClick(ClickEvent event) {
	    		
	    		PurchaseOrderStatusTypes tempStatus = (PurchaseOrderStatusTypes) status.getValue();
	    		
	    		final HashMap<String, Object> statusReportmap = new HashMap();
	    		statusReportmap.put("po_status_type_id", tempStatus.getPo_status_type_id());
	    		statusReportmap.put("logo", "D:/Zitco_File_Export/logo.png");
	    		String supplierReportURL = "D:/ZitcoJasperReports/Status_Wise_Report.jasper";
	    		generatePDF(supplierReportURL,statusReportmap);
	    		publishReport("D:/ZitcoJasperReports/Status_Wise_Report.pdf");
	    	}
	    });
	    
	    statusLayout.addComponent(statusButton);
	    
//	    
//	    VerticalLayout productLayout = new VerticalLayout();
//	    productLayout.setMargin(true);
//	    productLayout.setSpacing(true);
//		
//		Tab productReportTab = tabSheet.addTab(productLayout, "Product Wise Report");
//		productReportTab.setVisible(true);
//		productReportTab.setCaption("Product Wise Report");
//		
//		
//	    Button product= new Button("Generate Report", new Button.ClickListener() {
//	    	@Override
//	    	public void buttonClick(ClickEvent event) {
////	    		publishReport("D:/ZitcoJasperReports/Supplier_Wise_Report.pdf");
//	    	}
//	    });
//	    
//	    productLayout.addComponent(product);
	    
	    
	    mainLayout.addComponent(tabSheet);
		return mainLayout;

	}
	
	public void generatePDF(final String fileName, final HashMap inputValue){

		try
		{
			ServletContext sc= ZitcotestUI.Servlet.getCurrent().getServletContext();

			String reportFile = fileName;  //sc.getRealPath("/target/classes/reports1/Supplier_Wise_Report.jasper");
			String pdfFile = reportFile.substring(0, reportFile.lastIndexOf(".")) +".pdf";

			Connection connection = HibernateUtil.getConnection();

			try {

				FileInputStream fin = new FileInputStream(reportFile);
				FileOutputStream fout = new FileOutputStream(pdfFile);

				// Fill Report		
				JasperPrint jprint = (JasperPrint) JasperFillManager.fillReport(reportFile, inputValue, connection);
				// Export PDF File
				JasperExportManager.exportReportToPdfFile(jprint, pdfFile);

				fin.close();
				fout.close();
				connection.close();

			} catch (JRException ex) {
				ex.printStackTrace();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e2) {
				e2.printStackTrace();
			} catch (SQLException e3) {
				e3.printStackTrace();
			}

			File helpFile = new File(pdfFile);
			FileInputStream helpFileInputStream = new FileInputStream(helpFile);

		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
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
	
	private IndexedContainer getSupplierIndexedContainer() {

		IndexedContainer suppliers =  new IndexedContainer();
		suppliers.addContainerProperty("supplier_id", String.class, "");
		suppliers.addContainerProperty("supplier_name", String.class, "");

		Session supplierSession=(Session)ZitcoSession.getSession();
		List<Supplier> supplierList = supplierSession.createCriteria(Supplier.class).list();

		Iterator<Supplier> supp_itr = supplierList.iterator();

		String tempSupplierName;
		Integer tempSupplierID;

		while(supp_itr.hasNext()) {
			Supplier supplier = supp_itr.next();
			tempSupplierName = supplier.getSupplier_name();
			tempSupplierID = supplier.getSupplier_id();

			com.vaadin.data.Item item =  suppliers.addItem(supplier.getSupplier_id());

			if(item != null) {
				((com.vaadin.data.Item) item).getItemProperty("supplier_id").setValue(tempSupplierID.toString());
				((com.vaadin.data.Item) item).getItemProperty("supplier_name").setValue(tempSupplierName);
			}
		}
		supplierSession.close();  
		return suppliers;
	}

	public Supplier getSupplierByName(Integer supplierID){

		Session session=(Session)ZitcoSession.getSession();
		List<Supplier> supplierList = session.createCriteria(Supplier.class).list();
		Iterator supplierIterator = supplierList.iterator();
		Supplier tempSupplier;

		while(supplierIterator.hasNext()){
			tempSupplier = (Supplier) supplierIterator.next();

			if(supplierID == tempSupplier.getSupplier_id()){
				return tempSupplier;
			}
		}
		session.close();
		return null;
	}
	
	@Override
	public void enter(ViewChangeEvent event) {

	}
}

