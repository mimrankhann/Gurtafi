package com.evantage.zitcotest.view.purchase_order;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import com.evantage.zitcotest.domain.customer.Customer;
import com.evantage.zitcotest.domain.product.SupplierProduct;
import com.evantage.zitcotest.domain.purchase_order.Currency;
import com.evantage.zitcotest.domain.purchase_order.CurrentPOID;
import com.evantage.zitcotest.domain.purchase_order.Item;
import com.evantage.zitcotest.domain.purchase_order.PurchaseOrder;
import com.evantage.zitcotest.domain.purchase_order.PurchaseOrderItemList;
import com.evantage.zitcotest.domain.purchase_order.PurchaseOrderPaymentTerms;
import com.evantage.zitcotest.domain.purchase_order.SelectedItems;
import com.evantage.zitcotest.domain.supplier.Supplier;
import com.evantage.zitcotest.event.DashboardEventBus;
import com.evantage.zitcotest.session.ZitcoSession;
import com.evantage.zitcotest.view.DashboardViewType;
import com.evantage.zitcotest.view.customer.CustomerList;
import com.evantage.zitcotest.view.purchase_order_status_updation.PurchaseOrderStatusUpdationList;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Responsive;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DateField;
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
public class PurchaseOrderView extends Panel implements View,Component{  

	public static final String ID = "purchaseOrderWindow";
	private CssLayout purchaseOrderPanel;
	private final VerticalLayout root;
	private PurchaseOrderList purchaseOrderList = new PurchaseOrderList();
	private ItemList itemList = new ItemList();
	private BeanItemContainer<SelectedItems> selectedItemContainer = itemList.getSelectedItemContainer();
	
	private Button savaBtn;
	
	@PropertyId("po_date")
	@Temporal(TemporalType.TIMESTAMP)
	private DateField po_date;
	
	@PropertyId("supplier_name")
	private ComboBox supplierName;
	
	@PropertyId("customerName")
	private ComboBox customerName;
	
	@PropertyId("payment_terms")
	private ComboBox paymentTerms;
	
	@PropertyId("currency_name")
	private ComboBox currencyName;
	
	@PropertyId("po_number")
	private TextField poNumber;
	
	@PropertyId("commission")
	private TextField commission;
	
	@PropertyId("payment_due_date") 
	@Temporal(TemporalType.TIMESTAMP)
	private DateField paymentDueDate;
	
	private static PurchaseOrderView purchaseOrderView;
	private VerticalLayout mainLayout=new VerticalLayout();

	private PurchaseOrder purchaseOrder;
    private VerticalSplitPanel vsp=new VerticalSplitPanel();
    private String poID = "";
    
	public PurchaseOrderView()
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
		root.setExpandRatio(content, 1);
	}
	
	public void setData(PurchaseOrderList purchaseOrderList){
		
	}

	public static PurchaseOrderView getPurchaseOrderViewObject(){
		return purchaseOrderView;
	}

	private Component buildComponent(){
		purchaseOrderPanel = new CssLayout();
		purchaseOrderPanel.addStyleName("dashboard-panels");
		//zz
		purchaseOrderPanel.addComponent(buildLabels());
		purchaseOrderPanel.addComponent(purchaseOrderForm());
		purchaseOrderPanel.setSizeFull();
		return purchaseOrderPanel;
	}

	//zz
	private Component buildLabels() {
		CssLayout labels = new CssLayout();
		labels.addStyleName("labels");

		Label supplier = new Label("Indent Form");
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

				if(poID != null && poID.trim().length() > 0){
					updatePurchaseOrder();
				}
				else{
					savePurchaseOrder();
				}

			}
		});

		Button cancelBtn= new Button("Cancel", new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				
				UI.getCurrent().getNavigator().addView("IFList", new PurchaseOrderList());
				UI.getCurrent().getNavigator().navigateTo(DashboardViewType.PURCHASEORDERLIST.getViewName());
			
			}
		});

		mainLayout.setSpacing(true);
		
		FormLayout top_details = new FormLayout();
		top_details.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
		
		poNumber = new TextField("IF Number");
		poNumber.setEnabled (false);
		
		top_details.addComponent(poNumber);
        mainLayout.addComponent(top_details);	

		po_date = new DateField();
		po_date.setValue(new Date());
		po_date.setDateFormat("dd-MM-yyyy");
		po_date.setCaption("IF Date");
		top_details.addComponent(po_date);
		
		supplierName= new ComboBox();
		supplierName.setCaption("Supplier Name");
//		BeanItemContainer<Supplier> supplierContainer= new BeanItemContainer<Supplier>(Supplier.class);
//		Session session=(Session)ZitcoSession.getSession();
        
//		List<Supplier> supplierList = session.createCriteria(Supplier.class).list();
		supplierName.setContainerDataSource(getSupplierIndexedContainer());
        
//		supplierName.addItems(supplierList);
        
		supplierName.setItemCaptionPropertyId("supplier_name");
		supplierName.setWidth(250, UNITS_PIXELS);
		supplierName.setImmediate(true);
        top_details.addComponent(supplierName);
        
        supplierName.addValueChangeListener(new ValueChangeListener () {
			@Override
			public void valueChange(ValueChangeEvent event) {

//************ Assigning new IF value via getPOID() -- Start
				
				Customer customer = (Customer)customerName.getValue();
				Integer supplier = (Integer) supplierName.getValue();
				
				if(customer != null && supplier !=null){
					BeanItemContainer<Supplier> supplierContainer= new BeanItemContainer<Supplier>(Supplier.class);
					Session session=(Session)ZitcoSession.getSession();
					List<Supplier> supplierList = session.createCriteria(Supplier.class).list();
					Iterator supplierIterator = supplierList.iterator();

					Supplier tempSupplier;

					while(supplierIterator.hasNext()){
						tempSupplier = (Supplier) supplierIterator.next();

						if(tempSupplier.getSupplier_id() == supplier){
							poNumber.setValue("IF00" + getPOID() + "-"+tempSupplier.getSupplier_short_name() +"/"+customer.getCustomerShortName());
						}
					}
					session.close();

				}
				else{
					Notification.show("Please Select Customer & Product First for IF ID generation.....",Notification.TYPE_WARNING_MESSAGE);
				}
				
//************ Assigning new IF value via getPOID() -- End
				
			}
		});
        
        customerName = new ComboBox();
        customerName.setCaption("Customer Name");
        Session session=(Session)ZitcoSession.getSession();
        BeanItemContainer<Customer> customerContainer = new BeanItemContainer<Customer>(Customer.class);
        List<Customer> customerList = session.createCriteria(Customer.class).list();
		customerName.setContainerDataSource(customerContainer);
        
		customerName.addItems(customerList);
		customerName.setItemCaptionPropertyId("customerName");
		customerName.setWidth(250, UNITS_PIXELS);
		customerName.setImmediate(true);
		top_details.addComponent(customerName);
		session.close();
		customerName.addValueChangeListener(new ValueChangeListener () {
			@Override
			public void valueChange(ValueChangeEvent event) {

//************ Assigning new IF value via getPOID() -- Start
				
				Customer customer = (Customer)customerName.getValue();
				Integer supplier = (Integer) supplierName.getValue();

				if(customer != null && supplier !=null){
					BeanItemContainer<Supplier> supplierContainer= new BeanItemContainer<Supplier>(Supplier.class);
					Session session=(Session)ZitcoSession.getSession();
					List<Supplier> supplierList = session.createCriteria(Supplier.class).list();
					Iterator supplierIterator = supplierList.iterator();

					Supplier tempSupplier;

					while(supplierIterator.hasNext()){
						tempSupplier = (Supplier) supplierIterator.next();

						if(tempSupplier.getSupplier_id() == supplier){
							poNumber.setValue("IF00" + getPOID() + "-"+tempSupplier.getSupplier_short_name() +"/"+customer.getCustomerShortName());
						}
					}
					session.close();
				}
				else{
					Notification.show("Please Select Customer & Product First for IF ID generation.....",Notification.TYPE_WARNING_MESSAGE);
				}

				
//************ Assigning new IF value via getPOID() -- End

			}
		});
		
		
//******** Adding products in table - Start
        mainLayout.addComponent(itemList);
//******** Adding products in table - End  
        
		FormLayout bottom_details = new FormLayout();
		bottom_details.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
		
		commission = new TextField("Commissoin %");
		bottom_details.addComponent(commission);
		
		
//********* Purchase Order payment terms in drop down from table - Start		
		
		paymentTerms = new ComboBox();
		paymentTerms.setCaption("Payment Terms");

//		Session purchaseOrderPaymentTermsSession= ZitcoSession.getSession();
//		BeanItemContainer<PurchaseOrderPaymentTerms> purchaseOrderPaymentTermsContainer = 
				new BeanItemContainer<PurchaseOrderPaymentTerms>(PurchaseOrderPaymentTerms.class);

//		List<PurchaseOrderPaymentTerms> purchaseOrderPaymentTermsList = purchaseOrderPaymentTermsSession.createCriteria(PurchaseOrderPaymentTerms.class).list();
		paymentTerms.setContainerDataSource( getPaymentTermsIndexedContainer());

//		paymentTerms.addItems(purchaseOrderPaymentTermsList);
		paymentTerms.setItemCaptionPropertyId("po_payment_terms");
		paymentTerms.setWidth(250, UNITS_PIXELS);
		paymentTerms.setImmediate(true);
		bottom_details.addComponent(paymentTerms);
//		purchaseOrderPaymentTermsSession.close();
		
//********* Purchase Order payment terms in drop down from table - End
		
		
		
//********* Purchase Order currency in drop down from table - Start		
				currencyName = new ComboBox();
				currencyName.setCaption("Currency");

//				Session currencySession= ZitcoSession.getSession();
//				BeanItemContainer<Currency> currencyContainer = 
//						new BeanItemContainer<Currency>(Currency.class);

//				List<Currency> currencyList = currencySession.createCriteria(Currency.class).list();
				currencyName.setContainerDataSource(getCurrencyIndexedContainer());

//				currencyName.addItems(currencyList);
				currencyName.setItemCaptionPropertyId("currency_name");
				currencyName.setWidth(250, UNITS_PIXELS);
				paymentTerms.setImmediate(true);
				bottom_details.addComponent(currencyName);
//				currencySession.close();
				
//********* Purchase Order currency in drop down from table - End		
		
		mainLayout.addComponent(bottom_details);
		
		HorizontalLayout buttonLayout=new HorizontalLayout();
		buttonLayout.addComponent(savaBtn);
		buttonLayout.addComponent(cancelBtn);

		mainLayout.addComponent(buttonLayout);
		mainLayout.setComponentAlignment(buttonLayout, Alignment.BOTTOM_RIGHT);
		vsp.addComponent(mainLayout);
		return  mainLayout;
	}

	
//************ Get Updated Purchase Order ID for purchase order form	
	public int getPOID(){
		
		BeanItemContainer<CurrentPOID> currentPOID= new BeanItemContainer<CurrentPOID>(CurrentPOID.class);
		Session currentPOIDSession=(Session)ZitcoSession.getSession();
		CurrentPOID tempCurrentPOIDObject = new CurrentPOID();
		Integer tempCurrentPOID = 0;
		Integer updatedCurrentPOID;
		int value=0;
		
		List<CurrentPOID> currentPOIDList = currentPOIDSession.createCriteria(CurrentPOID.class).list();
		Iterator currentPOIDIterator = currentPOIDList.iterator();
		while(currentPOIDIterator.hasNext()){
			CurrentPOID cpoid = (CurrentPOID) currentPOIDIterator.next();
			tempCurrentPOID = cpoid.getCurrent_po_id();
			value = tempCurrentPOID.intValue();
		}
		
		currentPOIDSession.getTransaction().commit();
		currentPOIDSession.close();
		return ++value;
		
	}
	
	
// *************** Save Purchase Order	
	public void savePurchaseOrder(){
		
		Supplier tempSupplier;
		Customer tempCustomer;
		
		PurchaseOrder purchaseOrder = new PurchaseOrder();
		Session session=ZitcoSession.getSession();
		
//		if(selectedItemsList.size() > 0){
			
			purchaseOrder.setPo_number(poNumber.getValue());
		    purchaseOrder.setPo_status_type_id(1);
		    purchaseOrder.setPo_status_type("INITIAL STAGE");
			purchaseOrder.setPo_date(po_date.getValue());
			
			tempSupplier = getSupplierByName((Integer) supplierName.getValue());
			purchaseOrder.setSupplier_name(tempSupplier.getSupplier_name());
			purchaseOrder.setSupplier_id(tempSupplier.getSupplier_id());
			tempCustomer = (Customer) customerName.getValue();
			
			PurchaseOrderPaymentTerms selectedPaymentTerm =  getPurchaseOrderPaymentTermsObjectByID(
					(Integer) paymentTerms.getValue());
			String paymentTerms = selectedPaymentTerm.getPo_payment_terms();
			Integer paymentTermsID = selectedPaymentTerm.getPo_payment_terms_id();
			purchaseOrder.setPayment_terms(paymentTerms);
			purchaseOrder.setPo_payment_terms_id(paymentTermsID);
			
			//zzzz
			Currency tempCurrencyObject = getCurrencyObjectByID((Integer) currencyName.getValue());
			
			String tempCurrnecyName = tempCurrencyObject.getCurrency_name();
			Integer tempCurrencyID = tempCurrencyObject.getCurrency_id();
			
			purchaseOrder.setCurrency_name(tempCurrnecyName);
			purchaseOrder.setCurrency_id(tempCurrencyID);
			
			if(paymentTerms.equalsIgnoreCase("CREDIT 30 DAYS")){

				purchaseOrder.setPayment_due_date(addDays(new Date(), 30));
			}
			
			purchaseOrder.setCustomer_name(tempCustomer.getCustomerName());
			purchaseOrder.setCustomer_id(tempCustomer.getCustomerId());
			
			Integer totalAmount = itemList.getTotalAmountOfSelectedItems();
			purchaseOrder.setPo_total_amount(totalAmount);
			
			Integer tempCommission = Integer.parseInt(commission.getValue().trim());
			Integer totalCommission = (tempCommission.intValue() * totalAmount.intValue()) / 100;
			purchaseOrder.setCommission(totalCommission);
			session.save(purchaseOrder);
			
			List<SelectedItems> selectedItemList= selectedItemContainer.getItemIds();
			Iterator selectedItemListIterator = selectedItemList.iterator(); 
			SelectedItems tempSelectedItem;
			PurchaseOrderItemList purchaseOrderItemList;
			String selectedItem;
			Integer quantity;
			
			while(selectedItemListIterator.hasNext()){
				purchaseOrderItemList=new PurchaseOrderItemList();
				tempSelectedItem = (SelectedItems) selectedItemListIterator.next();
				selectedItem = tempSelectedItem.getSelected_item();
				quantity = tempSelectedItem.getQuantity();
				
				purchaseOrderItemList.setItem_name(selectedItem);
				purchaseOrderItemList.setItem_quantity(quantity);
				purchaseOrderItemList.setItem_price(tempSelectedItem.getPrice());
				System.out.println(selectedItem+"  "+quantity);
				purchaseOrderItemList.setPurchaseOrder(purchaseOrder);
				purchaseOrderItemList.getPurchaseOrder().getPurchaseOrderItemList().add(purchaseOrderItemList);
				session.save(purchaseOrderItemList);
//				System.out.println("Selected Item is = "+tempSelectedItem.getSelected_item());
			}
			session.getTransaction().commit();
			session.close();
			selectedItemContainer.removeAllItems();
			setFiledsEmpty();
				
//******************* Increment in POID -- Start
			BeanItemContainer<CurrentPOID> currentPOID= new BeanItemContainer<CurrentPOID>(CurrentPOID.class);
			Session currentPOIDSession=(Session)ZitcoSession.getSession();
			CurrentPOID tempCurrentPOIDObject = new CurrentPOID();
			Integer tempCurrentPOID = 0;
			Integer updatedCurrentPOID;
			int value=0;
			
			List<CurrentPOID> currentPOIDList = currentPOIDSession.createCriteria(CurrentPOID.class).list();
			Iterator currentPOIDIterator = currentPOIDList.iterator();
			while(currentPOIDIterator.hasNext()){
				CurrentPOID cpoid = (CurrentPOID) currentPOIDIterator.next();
				tempCurrentPOID = cpoid.getCurrent_po_id();
				value = tempCurrentPOID.intValue();
			}
			    value = ++value;
			    tempCurrentPOIDObject.setId(new Integer(value));
				tempCurrentPOIDObject.setCurrent_po_id(new Integer(value));
				currentPOIDSession.save(tempCurrentPOIDObject);
				currentPOIDSession.getTransaction().commit();
				currentPOIDSession.close();
//******************* Increment in POID -- End	
			UI.getCurrent().getNavigator().addView("IFUpdationList", new PurchaseOrderStatusUpdationList());
			UI.getCurrent().getNavigator().addView("IFList", new PurchaseOrderList());
			UI.getCurrent().getNavigator().navigateTo(DashboardViewType.PURCHASEORDERLIST.getViewName());
			Notification.show("IF Saved Successfully.....",Notification.TYPE_WARNING_MESSAGE);
	}
//}

	
// ***************** Update Purchase Order	
	public void updatePurchaseOrder(){
		Supplier tempSupplier;
		Customer tempCustomer;
		
		BeanItemContainer<SelectedItems> selectedItemContainer=itemList.getSelectedItemContainer();
		List<SelectedItems> selectedItemsList = selectedItemContainer.getItemIds();
		
		if(selectedItemsList.size() > 0){
//			Session session=ZitcoSession.getSession();
			System.out.println(poID);
//			PurchaseOrder purchaseOrder = new PurchaseOrder();
//			purchaseOrder.setPo_id(Integer.parseInt(poID));
//			purchaseOrder.setPo_number(poNumber.getValue());
//			purchaseOrder.setPo_date(po_date.getValue());
//			tempSupplier = (Supplier) supplierName.getValue();
//			purchaseOrder.setSupplier_name(tempSupplier.getSupplier_name());
//			purchaseOrder.setSupplier_id(tempSupplier.getSupplier_id());
//			tempCustomer = (Customer) customerName.getValue();
			
//			PurchaseOrderPaymentTerms selectedPaymentTerm =  (PurchaseOrderPaymentTerms) paymentTerms.getValue();
//			String paymentTerms = selectedPaymentTerm.getPo_payment_terms();
//			purchaseOrder.setPayment_terms(paymentTerms);
			
//			if(paymentTerms.equalsIgnoreCase("CREDIT 30 DAYS")){
//
//				purchaseOrder.setPayment_due_date(addDays(new Date(), 30));
//			}
			
//			purchaseOrder.setCustomer_name(tempCustomer.getCustomerName());
//			purchaseOrder.setCustomer_id(tempCustomer.getCustomerId());
//			purchaseOrder.setEDD(EDD.getValue());
//			purchaseOrder.setEDA(EDA.getValue());
			//Below Set total amount of products.....
			
//			purchaseOrder.setPo_total_amount(itemList.getTotalAmountOfSelectedItems());
//			session.update(purchaseOrder);
//			System.out.println("after save po");
		//	 Till Above works fine
			
			Session session=ZitcoSession.getSession();
			
//			BeanItemContainer<PurchaseOrder> selectedPurchaseOrderContainer= new BeanItemContainer<PurchaseOrder>(PurchaseOrder.class);
//			Session selectedPurchaseOrderSession=(Session)ZitcoSession.getSession();
			
			List<PurchaseOrder> selectedPurchaseOrderList = session.createCriteria(PurchaseOrder.class).list();
			Iterator selectedPurchaseOrderIterator = selectedPurchaseOrderList.iterator();
			PurchaseOrder selectedPurchaseOrder;
			
			Iterator selectedItemListIterator=selectedItemsList.iterator();
			SelectedItems tempSelectedItem;
			PurchaseOrderItemList purchaseOrderItemList;
			String selectedItem;
			Integer quantity;
			
			while(selectedPurchaseOrderIterator.hasNext()){
				
				selectedPurchaseOrder = (PurchaseOrder) selectedPurchaseOrderIterator.next();
				
				if(Integer.parseInt(poID) ==  selectedPurchaseOrder.getPo_id()){
					
					selectedPurchaseOrder.setPo_id(Integer.parseInt(poID));
					PurchaseOrderPaymentTerms selectedPaymentTerm =  getPurchaseOrderPaymentTermsObjectByID(
							(Integer) paymentTerms.getValue());
					String paymentTerms = selectedPaymentTerm.getPo_payment_terms();
					selectedPurchaseOrder.setPayment_terms(paymentTerms);
					selectedPurchaseOrder.setPo_total_amount(itemList.getTotalAmountOfSelectedItems());
					session.update(selectedPurchaseOrder);
					
					Set<PurchaseOrderItemList> selectedPurchaseOrderItemsList  =  selectedPurchaseOrder.getPurchaseOrderItemList();
					Iterator selectedPurchaseOrderItemsIterator = selectedPurchaseOrderItemsList.iterator();
					PurchaseOrderItemList poil;
					PurchaseOrder tempPurchaseOrder;
					
					List<SelectedItems> selectedItemList= selectedItemContainer.getItemIds();
					Iterator selectedItemListIterator1 = selectedItemList.iterator(); 
					SelectedItems tempSelectedItem1;
					
					while(selectedPurchaseOrderItemsIterator.hasNext()){
						poil = (PurchaseOrderItemList) selectedPurchaseOrderItemsIterator.next();
						tempPurchaseOrder = poil.getPurchaseOrder();
						
						    while(selectedItemListIterator1.hasNext()){
						    	tempSelectedItem1 = (SelectedItems) selectedItemListIterator1.next();
						    	//						    poil.setPurchaseOrder(selectedPurchaseOrder);
						    	if(tempPurchaseOrder.getPo_id() == Integer.parseInt(poID)){
						    		poil.setItem_name(tempSelectedItem1.getSelected_item());
						    		poil.setItem_quantity(tempSelectedItem1.getQuantity());
						    		poil.setItem_price(tempSelectedItem1.getQuantity() * tempSelectedItem1.getPrice());
						    		poil.setPurchaseOrder(selectedPurchaseOrder);
						    		
						    		if(poil.getPurchaseOrder().getPurchaseOrderItemList().contains(poil)){
//						    			poil.getPurchaseOrder().getPurchaseOrderItemList().add(poil);
						    			session.update(poil);
						    		}
						    		else{
						    			poil.getPurchaseOrder().getPurchaseOrderItemList().add(poil);
						    			session.save(poil);
						    		}
						    		
						    		//	session.delete(poil);
						    		
						    		System.out.println("after update()" + poil.getItem_name());
						    	}
						}
					}
				}
				
			}

			session.getTransaction().commit();
			session.close();
			selectedItemContainer.removeAllItems();
			setFiledsEmpty();
			
//			Set<PurchaseOrderItemList> tempListPurchaseOrderItems = (Set<PurchaseOrderItemList>) purchaseOrder.getPurchaseOrderItemList();
//			Iterator tempPurchaseOrderItems = tempListPurchaseOrderItems.iterator();
//			
//			System.out.println("purchaseOrderItemContainerSize() = "+tempListPurchaseOrderItems.size()+" and isEmpty()"+tempListPurchaseOrderItems.isEmpty());
//			
//			while(selectedItemListIterator.hasNext()){
//				
//				while(tempPurchaseOrderItems.hasNext()){
//				
//					purchaseOrderItemList=(PurchaseOrderItemList) tempPurchaseOrderItems.next();
//					System.out.println("purchaseOrderItemList Object = "+purchaseOrderItemList);
//					System.out.println("purchaseOrderItemList Object Item Name = "+purchaseOrderItemList.getItem_name());
//					
//                    if(Integer.parseInt(poID) ==  purchaseOrder.getPo_id()){
//                    	tempSelectedItem = (SelectedItems) selectedItemListIterator.next();
//                    	selectedItem = tempSelectedItem.getSelected_item();
//                    	quantity = tempSelectedItem.getQuantity();
//
//                    	purchaseOrderItemList.setItem_name(selectedItem);
//                    	purchaseOrderItemList.setItem_quantity(quantity);
//                    	purchaseOrderItemList.setItem_price(tempSelectedItem.getPrice());
//                    	System.out.println(selectedItem+"  "+quantity);
//                    	purchaseOrderItemList.setPurchaseOrder(purchaseOrder);
//                    	purchaseOrderItemList.getPurchaseOrder().getPurchaseOrderItemList().add(purchaseOrderItemList);
//                    	session.update(purchaseOrderItemList);
//                    	System.out.println("after update()" + purchaseOrderItemList.getItem_name());
//                    	session.getTransaction().commit();
//                    }
//				}
//			}
			
			
//			System.out.println("after update()");
			UI.getCurrent().getNavigator().addView("IFList", purchaseOrderList);
			UI.getCurrent().getNavigator().navigateTo(DashboardViewType.PURCHASEORDERLIST.getViewName());
			Notification.show("IF Updated Successfully.....",Notification.TYPE_WARNING_MESSAGE);
			
//			BeanItemContainer<PurchaseOrderItemList> purchaseOrderItemListContainer = 
//					new BeanItemContainer<PurchaseOrderItemList>(PurchaseOrderItemList.class);
//			List<PurchaseOrderItemList> purchaseOrderList = purchaseOrderItemListContainer.getItemIds();   
//		    Iterator purchaseOrderItemIterator =  purchaseOrderList.iterator();
//		    
//		    PurchaseOrderItemList purchaseOrderItemListObject;
//		    PurchaseOrder tempPurchaseOrder;
//		    PurchaseOrderItemList tempPurchaseOrderItemListObject;
//		    
//		    while(purchaseOrderItemIterator.hasNext()){
//		    	purchaseOrderItemListObject = (PurchaseOrderItemList)purchaseOrderItemIterator.next();
//		    	tempPurchaseOrder =  (PurchaseOrder) purchaseOrderItemListObject.getPurchaseOrder();
//		    	
//		    	if(Integer.parseInt(poID) == tempPurchaseOrder.getPo_id()){
////		    		System.out.println("Selected Supplier found and id = "+ selectedSupplier.getSupplier_id());
//		    		tempPurchaseOrderItemListObject = new PurchaseOrderItemList();
//		    		tempPurchaseOrderItemListObject.setItem_name(item_name);
//		    		tempPurchaseOrderItemListObject.setItem_price(item_price);
//		    		tempPurchaseOrderItemListObject.setItem_quantity(item_quantity);
//		    		supplierProduct.setSupplier(selectedSupplier);
//		    		supplierProduct.getSupplier().getSupplierProduct().add(supplierProduct);
//					session.update(supplierProduct);
//		    	}
//		    }
			
			
			
			
//			List<PurchaseOrderItemList> currentPOItemList = session.createCriteria(PurchaseOrderItemList.class).list();
//			Iterator currentPOItemIterator = currentPOItemList.iterator();
//			PurchaseOrderItemList purchaseOrderItemList;
//			PurchaseOrder purchaseOrderObjectForItemList;
//			while(currentPOItemIterator.hasNext()){
//				purchaseOrderItemList = (PurchaseOrderItemList) currentPOItemIterator.next();
////				purchaseOrderObjectForItemList = purchaseOrderItemList.getPurchaseOrder();
//				
//				if(Integer.parseInt(poID) == purchaseOrderObjectForItemList.getPo_id() ){
//					System.out.println("poid is = " + purchaseOrderObjectForItemList.getPo_id());
//				}
//			}
			
//			Iterator selectedItemListIterator=selectedItemsList.iterator();
//			SelectedItems tempSelectedItem;
//			PurchaseOrderItemList purchaseOrderItemList;
//			String selectedItem;
//			Integer quantity;
//			
//			purchaseOrderItemList=new PurchaseOrderItemList();
//			
//			while(selectedItemListIterator.hasNext()){
//				
//				tempSelectedItem = (SelectedItems) selectedItemListIterator.next();
//				
//				if(poID == tem ){}
//				
//				selectedItem = tempSelectedItem.getSelected_item();
//				quantity = tempSelectedItem.getQuantity();
//				
//				purchaseOrderItemList.setItem_name(selectedItem);
//				purchaseOrderItemList.setItem_quantity(quantity);
//				purchaseOrderItemList.setItem_price(tempSelectedItem.getPrice());
//				System.out.println(selectedItem+"  "+quantity);
//				purchaseOrderItemList.setPurchaseOrder(purchaseOrder);
//				purchaseOrderItemList.getPurchaseOrder().getPurchaseOrderItemList().add(purchaseOrderItemList);
//				session.update(purchaseOrderItemList);
//			}
//			
					
	}
}
	
	
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
		commission.setValue("");
		supplierName.setValue(Supplier.class);
	}


	@Override
	public void enter(ViewChangeEvent event) {
		try{
			poID = UI.getCurrent().getSession().getAttribute("poID").toString();
		}
		catch(Exception e){
			poID = "";
		}
		
		if(poID != null && poID.trim().length() > 0){
			
//			BeanItemContainer<PurchaseOrder> poContainer = new BeanItemContainer<PurchaseOrder>(PurchaseOrder.class);
			Session poSession=(Session)ZitcoSession.getSession();
			List<PurchaseOrder> poList = poSession.createCriteria(PurchaseOrder.class).list();
			Iterator poIterator=poList.iterator(); 

			PurchaseOrder tempPurchaseOrder;
			
			while(poIterator.hasNext()){
				tempPurchaseOrder=(PurchaseOrder) poIterator.next();
				if(Integer.parseInt(poID) == tempPurchaseOrder.getPo_id()){
					poNumber.setValue(tempPurchaseOrder.getPo_number());
					po_date.setValue(tempPurchaseOrder.getPo_date());
					po_date.setEnabled(false);
//					customerName.setValue(tempPurchaseOrder.getCustomer_name());
					customerName.setEnabled(false);
					supplierName.select(tempPurchaseOrder.getSupplier_id());
//					supplierName.setValue(tempPurchaseOrder.getSupplier_name());
					paymentTerms.select(tempPurchaseOrder.getPo_payment_terms_id());
					currencyName.select(tempPurchaseOrder.getCurrency_id());
					
					if(tempPurchaseOrder.getCommission() != null){
						commission.setValue(tempPurchaseOrder.getCommission() * 100 / tempPurchaseOrder.getPo_total_amount()+"");
					}
					else{
						commission.setValue("");
					}
				}
			}
			
			poSession.close();
			
//			selectedItemContainer.removeAllItems();
			
//			BeanItemContainer<SelectedItems> itemsContainer = new BeanItemContainer<SelectedItems>(SelectedItems.class);
			Session itemsSession=(Session)ZitcoSession.getSession();
			List<PurchaseOrder> itemsList = itemsSession.createCriteria(PurchaseOrder.class).list();
			Iterator itemsIterator=itemsList.iterator(); 
			
			Table tempTable = itemList.getItemTable();
			
			//PurchaseOrderItemList tempPurchaseOrderItemList;
			PurchaseOrder tempPurchaseOrder1;
			PurchaseOrderItemList tempSelectedItems;  //= new SelectedItems();
			SelectedItems selectedItem ;
			
			
			
			while(itemsIterator.hasNext()){
				tempPurchaseOrder = (PurchaseOrder) itemsIterator.next();
				//tempPurchaseOrder1 = tempPurchaseOrder.getPurchaseOrder();
				
				if(Integer.parseInt(poID) == tempPurchaseOrder.getPo_id()){
					Set<PurchaseOrderItemList> purchaseOrderItemList=  tempPurchaseOrder.getPurchaseOrderItemList();
					Iterator purchaseOrderItemListIterator = purchaseOrderItemList.iterator();
					while(purchaseOrderItemListIterator.hasNext()){
					selectedItem= new SelectedItems();
					tempSelectedItems = (PurchaseOrderItemList) purchaseOrderItemListIterator.next();
					selectedItem.setSelected_item(tempSelectedItems.getItem_name());
					selectedItem.setQuantity(tempSelectedItems.getItem_quantity());
					selectedItem.setPrice(tempSelectedItems.getItem_price());
						//tempPurchaseOrderItemList.setItem_name();
						selectedItemContainer.addBean(selectedItem);
						//tempTable.addItem(tempPurchaseOrderItemList);
					}
				}
			}
			
			tempTable.setContainerDataSource(selectedItemContainer);
			tempTable.setVisible(true);
			tempTable.setWidth("100%");
			tempTable.setHeight("150px");
			tempTable.setImmediate(true);
			tempTable.setVisibleColumns("selected_item","quantity","price");
			tempTable.setColumnHeaders("Product Name","Quantity","Amount");
			savaBtn.setVisible(false);
			UI.getCurrent().getSession().setAttribute("poID","");
			itemsSession.close();
			
		}
	
		else{
			setFiledsEmpty();
		}

	}
	
private IndexedContainer getCurrencyIndexedContainer() {
		
		IndexedContainer currencyContainer =  new IndexedContainer();
		currencyContainer.addContainerProperty("currency_id", String.class, "");
		currencyContainer.addContainerProperty("currency_name", String.class, "");

		Session currencySession=(Session)ZitcoSession.getSession();
		List<Currency> paymentTermsList = currencySession.createCriteria(Currency.class).list();

		Iterator<Currency> currencyIterator = paymentTermsList.iterator();
		
		String tempCurrency;
		Integer tempCurrencyID;
		
		while(currencyIterator.hasNext()) {
			Currency poCurrency = currencyIterator.next();
			tempCurrency = poCurrency.getCurrency_name();
			tempCurrencyID = poCurrency.getCurrency_id();
            
			com.vaadin.data.Item item =  currencyContainer.addItem(poCurrency.getCurrency_id());

			if(item != null) {
				((com.vaadin.data.Item) item).getItemProperty("currency_id").setValue(tempCurrencyID.toString());
				((com.vaadin.data.Item) item).getItemProperty("currency_name").setValue(tempCurrency);
			}
		}
		currencySession.close();  
		return currencyContainer;
	}			

	private IndexedContainer getPaymentTermsIndexedContainer() {
		
		IndexedContainer paymentTermsContainer =  new IndexedContainer();
		paymentTermsContainer.addContainerProperty("po_payment_terms_id", String.class, "");
		paymentTermsContainer.addContainerProperty("po_payment_terms", String.class, "");

		Session paymentTermsSession=(Session)ZitcoSession.getSession();
		List<PurchaseOrderPaymentTerms> paymentTermsList = paymentTermsSession.createCriteria(PurchaseOrderPaymentTerms.class).list();

		Iterator<PurchaseOrderPaymentTerms> paymentTermsIterator = paymentTermsList.iterator();
		
		String tempPaymentTerm;
		Integer tempPaymentID;
		
		while(paymentTermsIterator.hasNext()) {
			PurchaseOrderPaymentTerms poPaymentTerms = paymentTermsIterator.next();
			tempPaymentTerm = poPaymentTerms.getPo_payment_terms();
			tempPaymentID = poPaymentTerms.getPo_payment_terms_id();
            
			com.vaadin.data.Item item =  paymentTermsContainer.addItem(poPaymentTerms.getPo_payment_terms_id());

			if(item != null) {
				((com.vaadin.data.Item) item).getItemProperty("po_payment_terms_id").setValue(tempPaymentID.toString());
				((com.vaadin.data.Item) item).getItemProperty("po_payment_terms").setValue(tempPaymentTerm);
			}
		}
		paymentTermsSession.close();  
		return paymentTermsContainer;
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
	
//*************** Add days in a calender and return updated date object -- Start	
	public static Date addDays(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }
//*************** Add days in a calender and return updated date object -- End

	public Supplier getPurchaseOrderViewSupplier(){

		Integer supplier = (Integer) supplierName.getValue();

		if(supplier !=null){

			Session session=(Session)ZitcoSession.getSession();
			List<Supplier> supplierList = session.createCriteria(Supplier.class).list();
			Iterator supplierIterator = supplierList.iterator();

			Supplier tempSupplier;

			while(supplierIterator.hasNext()){
				tempSupplier = (Supplier) supplierIterator.next();

				if(tempSupplier.getSupplier_id() == supplier){
					return tempSupplier;
				}
			}
			session.close();
			
		}
		return null;
	}
	
	
//********* Get Supplier's Object by Its ID - Start
	
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

//********* Get Supplier's Object by Its ID - End	

	
//********* Get PO Payment Term's Object by Its ID - Start
	
		public PurchaseOrderPaymentTerms getPurchaseOrderPaymentTermsObjectByID(Integer poPaymentTermsID){

			Session session=(Session)ZitcoSession.getSession();
			List<PurchaseOrderPaymentTerms> poTermsList = session.createCriteria(PurchaseOrderPaymentTerms.class).list();
			Iterator poTermsIterator = poTermsList.iterator();
			PurchaseOrderPaymentTerms tempPOPaymentTerms;

			while(poTermsIterator.hasNext()){
				tempPOPaymentTerms = (PurchaseOrderPaymentTerms) poTermsIterator.next();

				if(poPaymentTermsID == tempPOPaymentTerms.getPo_payment_terms_id()){
					return tempPOPaymentTerms;
				}
			}
			session.close();
			return null;
		}

//********* Get PO Payment Term's Object by Its ID - End		
	

//********* Get Currency's Object by Its ID - Start
		
		public Currency getCurrencyObjectByID(Integer currencyID){

			Session session=(Session)ZitcoSession.getSession();
			List<Currency> currencyList = session.createCriteria(Currency.class).list();
			Iterator currencyIterator = currencyList.iterator();
			Currency tempCurrencyObject;

			while(currencyIterator.hasNext()){
				tempCurrencyObject = (Currency) currencyIterator.next();

				if(currencyID == tempCurrencyObject.getCurrency_id()){
					return tempCurrencyObject;
				}
			}
			session.close();
			return null;
		}

	//********* Get Currency's Object by Its ID - End	
		
		
		
}
