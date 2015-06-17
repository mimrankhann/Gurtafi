package com.evantage.zitcotest.view.customer;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.evantage.zitcotest.domain.customer.Customer;
import com.evantage.zitcotest.event.DashboardEventBus;
import com.evantage.zitcotest.session.ZitcoSession;
import com.evantage.zitcotest.view.DashboardViewType;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class CustomerView extends Panel implements View,Component{
	
	public static final String ID = "customerWindow";
	private CssLayout customerPanel;
	private final VerticalLayout root;

	@PropertyId("customerId")
	private TextField customerId;
	
	@PropertyId("customerName")
	private TextField customerName;
	
	@PropertyId("customerShortName")
	private TextField customerShortName;

	
	@PropertyId("customerAddress")
	private TextField customerAddress;
	@PropertyId("customerPhone")
	private TextField customerPhone;
	@PropertyId("customerEmail")
	private TextField customerEmail;

	@PropertyId("customerNTN")
	private TextField customerNTN;
	private CustomerView customerView;
	VerticalLayout mainLayout;

	String customerID = "";

	public CustomerView()
	{
		customerView=this;
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


	public void setData(CustomerList customerList){

	}

	public CustomerView getcustomerViewObject(){
		return customerView;
	}

	private Component buildComponent(){
		customerPanel = new CssLayout();
		customerPanel.addStyleName("dashboard-panels");
		customerPanel.addComponent(buildLabels());
		customerPanel.setImmediate(false);
		customerPanel.addComponent(customerForm());
		customerPanel.setSizeFull();
		return customerPanel;
	}

	//zz
	private Component buildLabels() {
		CssLayout labels = new CssLayout();
		labels.addStyleName("labels");

		Label customer = new Label("Add New Customer");
		customer.setSizeUndefined();
		customer.addStyleName(ValoTheme.LABEL_H4);
		customer.addStyleName(ValoTheme.LABEL_COLORED);
		labels.addComponent(customer);

		return labels;
	}


	public Component customerForm(){

		mainLayout=new VerticalLayout();

		Button savaBtn = new Button("Save", new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {

				if(customerID != null && customerID.trim().length() > 0){
					updateCustomer();
				}
				else{
					saveCustomer();
				}
			}
		});

		Button cancelBtn= new Button("Cancel", new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				UI.getCurrent().getNavigator().addView("customer", new CustomerList());
				UI.getCurrent().getNavigator().navigateTo(DashboardViewType.CUSTOMERLIST.getViewName());
			}
		});

		HorizontalLayout root = new HorizontalLayout();
		root.setSizeFull(); 
		root.setCaption("Customer Profile");
		root.setIcon(FontAwesome.USER);
		root.setWidth(100.0f, Unit.PERCENTAGE);
		root.setSpacing(true);
		root.setMargin(true);
		root.addStyleName("profile-form");


		FormLayout details = new FormLayout();
		details.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
		root.addComponent(details);
		root.setExpandRatio(details, 1);


		customerName = new TextField("Customer Name");
		customerName.setRequired(true);
		customerName.setNullRepresentation("");
		details.addComponent(customerName);
        
		
		customerShortName = new TextField("Customer Short Name");
		customerShortName.setRequired(true);
		customerShortName.setNullRepresentation("");
		details.addComponent(customerShortName);
		
		customerAddress = new TextField("Address");
        customerAddress.setNullRepresentation("");		
		customerAddress.setRequired(true);
		details.addComponent(customerAddress);


		customerPhone = new TextField("Phone");
		customerPhone.setRequired(true);
		customerPhone.setNullRepresentation("");
		details.addComponent(customerPhone);


		customerEmail = new TextField("Email Address");
		customerEmail.setInputPrompt("http://");
		customerEmail.setWidth("100%");
		customerEmail.setRequired(true);
		customerEmail.setNullRepresentation("");
		details.addComponent(customerEmail);

		customerNTN = new TextField("NTN");
		customerNTN.setRequired(true);
		customerNTN.setNullRepresentation("");
		details.addComponent(customerNTN);

		mainLayout.addComponent(details);
		HorizontalLayout buttonLayout=new HorizontalLayout();
		buttonLayout.addComponent(savaBtn);
		buttonLayout.addComponent(cancelBtn);

		mainLayout.addComponent(buttonLayout);
		mainLayout.setComponentAlignment(buttonLayout, Alignment.BOTTOM_RIGHT);
		return mainLayout;

	}
	public void saveCustomer(){
		if(saveValidation()){
			Session session=ZitcoSession.getSession();
			Customer customer = new Customer();
			customer.setCustomerName(customerName.getValue());
			customer.setCustomerShortName(customerShortName.getValue());
			customer.setCustomerAddress(customerAddress.getValue());
			customer.setCustomerPhone(customerPhone.getValue());
			customer.setCustomerEmail(customerEmail.getValue());
			customer.setCustomerNTN(customerNTN.getValue());
			session.save(customer);
			session.getTransaction().commit();
			session.close();
			setFiledsEmpty();
			UI.getCurrent().getNavigator().addView("customer", new CustomerList());
			UI.getCurrent().getNavigator().navigateTo(DashboardViewType.CUSTOMERLIST.getViewName());
			Notification.show("Customer Saved Successfully.....",Notification.TYPE_WARNING_MESSAGE);
		}
		else{
			Notification.show("Please Fill All The Fields First.....",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	public void updateCustomer(){
		if(updateValidation()){
			Session session=ZitcoSession.getSession();
			Customer customer = new Customer();
			customer.setCustomerId(Integer.parseInt(customerID));
			customer.setCustomerName(customerName.getValue());
			customer.setCustomerShortName(customerShortName.getValue());
			customer.setCustomerAddress(customerAddress.getValue());
			customer.setCustomerPhone(customerPhone.getValue());
			customer.setCustomerEmail(customerEmail.getValue());
			customer.setCustomerNTN(customerNTN.getValue());
			session.update(customer);
			session.getTransaction().commit();
			session.close();
			setFiledsEmpty();
			UI.getCurrent().getNavigator().addView("customer", new CustomerList());
			UI.getCurrent().getNavigator().navigateTo(DashboardViewType.CUSTOMERLIST.getViewName());
			Notification.show("Customer Updated Successfully.....",Notification.TYPE_WARNING_MESSAGE);	
		}
		else{
			Notification.show("Please Fill All The Fields First.....",Notification.TYPE_WARNING_MESSAGE);
		}
	}
	
	public boolean saveValidation(){
		
			if(customerName.getValue() == "" || customerShortName.getValue() == "" || customerAddress.getValue()==""
					|| customerPhone.getValue()== "" || customerEmail.getValue() == "" || customerNTN.getValue() == ""){
				return false;
			}
					
		return true;
	}
	
	public boolean updateValidation(){
		
		if(customerName.getValue().trim().length() <= 0 || customerShortName.getValue().trim().length() <= 0 || 
				customerAddress.getValue().trim().length() <= 0 || customerPhone.getValue().trim().length() <= 0
				|| customerEmail.getValue().trim().length() <= 0 || customerNTN.getValue().trim().length() <= 0){
			return false;
		}

		return true;
	}
	
	public void setFiledsEmpty(){
		customerName.setValue("");
		customerShortName.setValue("");
		customerAddress.setValue("");
		customerPhone.setValue("");
		customerEmail.setValue("");
		customerNTN.setValue("");
	}


	@Override
	public void enter(ViewChangeEvent event) {
		
		try{
			customerID = UI.getCurrent().getSession().getAttribute("customerID").toString();
		}
		catch(Exception e){
			customerID = "";
		}
			
		
		if(customerID != null && customerID.trim().length() > 0){

			BeanItemContainer<Customer> container = new BeanItemContainer<Customer>(Customer.class);
			Session session=(Session)ZitcoSession.getSession();
			List<Customer> list = session.createCriteria(Customer.class).list();
			Iterator iterator=list.iterator(); 

			Customer tempCustomer;
			while(iterator.hasNext()){
				tempCustomer=(Customer) iterator.next();
				if(Integer.parseInt(customerID) == tempCustomer.getCustomerId()){
					customerName.setValue(tempCustomer.getCustomerName());
					customerShortName.setValue(tempCustomer.getCustomerShortName());
					customerAddress.setValue(tempCustomer.getCustomerAddress());
					customerPhone.setValue(tempCustomer.getCustomerPhone());
					customerEmail.setValue(tempCustomer.getCustomerEmail());
					customerNTN.setValue(tempCustomer.getCustomerNTN());
				}
			}
			UI.getCurrent().getSession().setAttribute("customerID","");
			session.close();
		}
		else{
			setFiledsEmpty();
		}

	}

}
