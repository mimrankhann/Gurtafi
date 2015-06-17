package com.evantage.zitcotest.view.supplier;

import java.util.Iterator;
import java.util.List;
import org.hibernate.Session;
import com.evantage.zitcotest.domain.supplier.Supplier;
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
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

	@SuppressWarnings("serial")
	public class SupplierView extends Panel implements View,Component{  
		
		public static final String ID = "supplierWindow";
		private CssLayout supplierPanel;
		private final VerticalLayout root;

		@PropertyId("supplier_name")
		private TextField supplierName;
		
		@PropertyId("supplier_short_name")
		private TextField supplierShortName;
		
		@PropertyId("supplier_address")
		private TextField supplierAddress;
		@PropertyId("supplier_phone")
		private TextField supplierPhone;
		@PropertyId("supplier_email")
		private TextField supplierEmail;
		@PropertyId("supplier_NTN")
		private TextField supplierNTN;
		private SupplierView supplierView;
		private VerticalLayout mainLayout;
		private Supplier supplier;
		
		String supplierID = "";
		
		public SupplierView()
		{
			supplierView=this;
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
			root.setExpandRatio(content, 1);	

		}
		
		public void setData(SupplierList supplierList){
			
		}

		public SupplierView getSupplierViewObject(){
			return supplierView;
		}

		private Component buildComponent(){
			supplierPanel = new CssLayout();
			supplierPanel.addStyleName("dashboard-panels");
			//zz
			supplierPanel.addComponent(buildLabels());
			supplierPanel.addComponent(supplierForm());
			supplierPanel.setSizeFull();
			return supplierPanel;
		}

		//zz
		private Component buildLabels() {
			CssLayout labels = new CssLayout();
			labels.addStyleName("labels");

			Label supplier = new Label("Add New Supplier");
			supplier.setSizeUndefined();
			supplier.addStyleName(ValoTheme.LABEL_H4);
			supplier.addStyleName(ValoTheme.LABEL_COLORED);
			labels.addComponent(supplier);
			return labels;
		}


		public Component supplierForm(){
			mainLayout=new VerticalLayout();
			
				Button savaBtn = new Button("Save", new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					
					if(supplierID != null && !supplierID.equals("")){
						updateSupplier();
					}
					else{
						saveSupplier();
					}
				}
			});
			
			
			Button cancelBtn= new Button("Cancel", new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					UI.getCurrent().getNavigator().addView("supplier", new SupplierList());
					UI.getCurrent().getNavigator().navigateTo(DashboardViewType.SUPPLIERLIST.getViewName());
				}
			});

			HorizontalLayout root = new HorizontalLayout();
			root.setSizeFull(); 
			root.setCaption("Supplier Profile");
			root.setIcon(FontAwesome.USER);
			root.setWidth(100.0f, Unit.PERCENTAGE);
			root.setSpacing(true);
			root.setMargin(true);
			root.addStyleName("profile-form");

			FormLayout details = new FormLayout();
			details.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
			
			root.addComponent(details);
			root.setExpandRatio(details, 1);

			supplierName = new TextField("Name");
			supplierName.setNullRepresentation("");
			details.addComponent(supplierName);
            
			supplierShortName = new TextField("Short Name");
			supplierShortName.setNullRepresentation("");
			details.addComponent(supplierShortName);
            
			
			supplierAddress = new TextField("Address");
			supplierAddress.setNullRepresentation("");
			details.addComponent(supplierAddress);


			supplierPhone = new TextField("Phone");
			supplierPhone.setNullRepresentation("");
			details.addComponent(supplierPhone);


			supplierEmail = new TextField("Email Address");
			supplierEmail.setInputPrompt("http://");
			//upplierEmail.setWidth("100%");
			supplierEmail.setNullRepresentation("");
			details.addComponent(supplierEmail);

			supplierNTN = new TextField("NTN");
			supplierNTN.setNullRepresentation("");
			details.addComponent(supplierNTN);

			mainLayout.addComponent(details);
			HorizontalLayout buttonLayout=new HorizontalLayout();
			buttonLayout.addComponent(savaBtn);
			buttonLayout.addComponent(cancelBtn);
			
			mainLayout.addComponent(buttonLayout);
			mainLayout.setComponentAlignment(buttonLayout, Alignment.BOTTOM_RIGHT);
			return mainLayout;

		}
        
		public void saveSupplier(){
			
			if(saveValidation()){
				Session session=ZitcoSession.getSession();
				Supplier supplier = new Supplier();
				supplier.setSupplier_name(supplierName.getValue());
				supplier.setSupplier_short_name(supplierShortName.getValue());
				supplier.setSupplier_address(supplierAddress.getValue());
				supplier.setSupplier_phone(supplierPhone.getValue());
				supplier.setSupplier_email(supplierEmail.getValue());
				supplier.setSupplier_NTN(supplierNTN.getValue());
				session.save(supplier);
				session.getTransaction().commit();
				session.close();
				setFiledsEmpty();
				UI.getCurrent().getNavigator().addView("supplier", new SupplierList());
				UI.getCurrent().getNavigator().navigateTo(DashboardViewType.SUPPLIERLIST.getViewName());
				Notification.show("Supplier Information Save Successfully.....",Notification.TYPE_WARNING_MESSAGE);
			}
			else{
				Notification.show("Please Fill All The Fields First.....",Notification.TYPE_WARNING_MESSAGE);
			}
		}

		public void updateSupplier(){
			if(updateValidation()){
				Session session=ZitcoSession.getSession();
				Supplier supplier = new Supplier();
				supplier.setSupplier_id(Integer.parseInt(supplierID));
				supplier.setSupplier_name(supplierName.getValue());
				supplier.setSupplier_short_name(supplierShortName.getValue());
				supplier.setSupplier_address(supplierAddress.getValue());
				supplier.setSupplier_phone(supplierPhone.getValue());
				supplier.setSupplier_email(supplierEmail.getValue());
				supplier.setSupplier_NTN(supplierNTN.getValue());
				session.update(supplier);
				session.getTransaction().commit();
				session.close();
				setFiledsEmpty();
				UI.getCurrent().getNavigator().addView("supplier", new SupplierList());
				UI.getCurrent().getNavigator().navigateTo(DashboardViewType.SUPPLIERLIST.getViewName());
				Notification.show("Supplier Information Updated Successfully.....",Notification.TYPE_WARNING_MESSAGE);
			}
			else{
				Notification.show("Please Fill All The Fields First.....",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		
		
		public boolean saveValidation(){

			if(supplierName.getValue() == "" || supplierShortName.getValue() == "" || supplierAddress.getValue()==""
					|| supplierPhone.getValue()== "" || supplierEmail.getValue() == "" || supplierNTN.getValue() == ""){

				return false;
			}

			return true;
		}

		public boolean updateValidation(){
			
			if(supplierName.getValue().trim().length() <= 0 || supplierShortName.getValue().trim().length() <= 0 || 
					supplierAddress.getValue().trim().length() <= 0 || supplierPhone.getValue().trim().length() <= 0
					|| supplierEmail.getValue().trim().length() <= 0 || supplierNTN.getValue().trim().length() <= 0){
				return false;
			}

			return true;
		}
		
		
		public void setFiledsEmpty(){
			supplierName.setValue("");
			supplierShortName.setValue("");
			supplierAddress.setValue("");
			supplierPhone.setValue("");
			supplierEmail.setValue("");
			supplierNTN.setValue("");
		}
		
	
		@Override
		public void enter(ViewChangeEvent event) {
			
			try{
				supplierID = UI.getCurrent().getSession().getAttribute("supplierID").toString();
		    }
			catch(Exception e){
				supplierID = "";
			}
			
			if(supplierID != null && supplierID.trim().length() >0){
				BeanItemContainer<Supplier> container = new BeanItemContainer<Supplier>(Supplier.class);
				Session session=(Session)ZitcoSession.getSession();
				List<Supplier> list = session.createCriteria(Supplier.class).list();
				Iterator iterator=list.iterator(); 

				Supplier tempSupplier;
				while(iterator.hasNext()){
					tempSupplier=(Supplier) iterator.next();
					if(Integer.parseInt(supplierID) == tempSupplier.getSupplier_id()){
						supplierName.setValue(tempSupplier.getSupplier_name());
						supplierShortName.setValue(tempSupplier.getSupplier_short_name());
						supplierAddress.setValue(tempSupplier.getSupplier_address());
						supplierPhone.setValue(tempSupplier.getSupplier_phone());
						supplierEmail.setValue(tempSupplier.getSupplier_email());
						supplierNTN.setValue(tempSupplier.getSupplier_NTN());
					}
				}
				UI.getCurrent().getSession().setAttribute("supplierID","");
				session.close();
			}
			else{
				setFiledsEmpty();
			}
			
		}
	}
