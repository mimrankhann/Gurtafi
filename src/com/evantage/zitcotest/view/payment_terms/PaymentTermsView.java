package com.evantage.zitcotest.view.payment_terms;

import java.util.Iterator;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import com.evantage.zitcotest.domain.purchase_order.PurchaseOrderPaymentTerms;
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
public class PaymentTermsView extends Panel implements View,Component{
	
	public static final String ID = "paymentTermsWindow";
	private CssLayout paymentTermsPanel;
	private final VerticalLayout root;

	@PropertyId("po_payment_terms_id")
	private TextField poPaymentTermsID;
	
	@PropertyId("po_payment_terms")
	private TextField poPaymentTerms;
	
	
	private PaymentTermsView paymentTermsView;
	VerticalLayout mainLayout;

	String paymentTermsID = "";

	public PaymentTermsView()
	{
		paymentTermsView=this;
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


	public PaymentTermsView getpaymentTermsViewObject(){
		return paymentTermsView;
	}

	private Component buildComponent(){
		paymentTermsPanel = new CssLayout();
		paymentTermsPanel.addStyleName("dashboard-panels");
		paymentTermsPanel.addComponent(buildLabels());
		paymentTermsPanel.setImmediate(false);
		paymentTermsPanel.addComponent(paymentTermsForm());
		paymentTermsPanel.setSizeFull();
		return paymentTermsPanel;
	}

	//zz
	private Component buildLabels() {
		CssLayout labels = new CssLayout();
		labels.addStyleName("labels");

		Label paymentTerms = new Label("Add Payment Terms");
		paymentTerms.setSizeUndefined();
		paymentTerms.addStyleName(ValoTheme.LABEL_H4);
		paymentTerms.addStyleName(ValoTheme.LABEL_COLORED);
		labels.addComponent(paymentTerms);

		return labels;
	}


	public Component paymentTermsForm(){

		mainLayout=new VerticalLayout();

		Button savaBtn = new Button("Save", new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {

				if(paymentTermsID != null && paymentTermsID.trim().length() > 0){
					updatePurchaseOrderPaymentTerms();
				}
				else{
					savePaymentTerms();
				}
			}
		});

		Button cancelBtn= new Button("Cancel", new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				UI.getCurrent().getNavigator().addView("paymentTerms", new PaymentTermsList());
				UI.getCurrent().getNavigator().navigateTo(DashboardViewType.PAYMENTTERMSLIST.getViewName());
			}
		});

		HorizontalLayout root = new HorizontalLayout();
		root.setSizeFull(); 
		root.setCaption("PurchaseOrderPaymentTerms Profile");
		root.setIcon(FontAwesome.USER);
		root.setWidth(100.0f, Unit.PERCENTAGE);
		root.setSpacing(true);
		root.setMargin(true);
		root.addStyleName("profile-form");


		FormLayout details = new FormLayout();
		details.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
		root.addComponent(details);
		root.setExpandRatio(details, 1);


		poPaymentTerms = new TextField("Payment Term");
		poPaymentTerms.setNullRepresentation("");
		poPaymentTerms.setRequired(true);
		details.addComponent(poPaymentTerms);
        
		
		mainLayout.addComponent(details);
		HorizontalLayout buttonLayout=new HorizontalLayout();
		buttonLayout.addComponent(savaBtn);
		buttonLayout.addComponent(cancelBtn);

		mainLayout.addComponent(buttonLayout);
		mainLayout.setComponentAlignment(buttonLayout, Alignment.BOTTOM_RIGHT);
		return mainLayout;

	}
	public void savePaymentTerms(){
		if(saveValidation()){
			Session session=ZitcoSession.getSession();
			PurchaseOrderPaymentTerms paymentTerms = new PurchaseOrderPaymentTerms();
			paymentTerms.setPo_payment_terms(poPaymentTerms.getValue());
			session.save(paymentTerms);
			session.getTransaction().commit();
			session.close();
			setFiledsEmpty();
			UI.getCurrent().getNavigator().addView("paymentTerms", new PaymentTermsList());
			UI.getCurrent().getNavigator().navigateTo(DashboardViewType.PAYMENTTERMSLIST.getViewName());
			Notification.show("PurchaseOrderPaymentTerms Saved Successfully.....",Notification.TYPE_WARNING_MESSAGE);
		}
		else{
			Notification.show("Please Fill All The Fields First.....",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	public void updatePurchaseOrderPaymentTerms(){
		if(updateValidation()){
			Session session=ZitcoSession.getSession();
			PurchaseOrderPaymentTerms paymentTerms = new PurchaseOrderPaymentTerms();
			paymentTerms.setPo_payment_terms_id(new Integer(paymentTermsID));
			paymentTerms.setPo_payment_terms(poPaymentTerms.getValue());
			session.update(paymentTerms);
			session.getTransaction().commit();
			session.close();
			setFiledsEmpty();
			UI.getCurrent().getNavigator().addView("paymentTerms", new PaymentTermsList());
			UI.getCurrent().getNavigator().navigateTo(DashboardViewType.PAYMENTTERMSLIST.getViewName());
			Notification.show("PurchaseOrderPaymentTerms Updated Successfully.....",Notification.TYPE_WARNING_MESSAGE);	
		}
		else{
			Notification.show("Please Fill All The Fields First.....",Notification.TYPE_WARNING_MESSAGE);
		}
	}
	
	public boolean saveValidation(){
					
		if(poPaymentTerms.getValue() == "") {
			return false;
		}
		return true;
	}
	
	public boolean updateValidation(){
		
		if(poPaymentTerms.getValue().trim().length() <= 0) {
			return false;
		}
		return true;
	}	
	
	

	public void setFiledsEmpty(){
		poPaymentTerms.setValue("");
	}


	@Override
	public void enter(ViewChangeEvent event) {
		
		try{
			paymentTermsID = UI.getCurrent().getSession().getAttribute("paymentTermsID").toString();
		}
		catch(Exception e){
			paymentTermsID = "";
		}
			
		
		if(paymentTermsID != null && paymentTermsID.trim().length() > 0){

			BeanItemContainer<PurchaseOrderPaymentTerms> container = new BeanItemContainer<PurchaseOrderPaymentTerms>(PurchaseOrderPaymentTerms.class);
			Session session=(Session)ZitcoSession.getSession();
			List<PurchaseOrderPaymentTerms> list = session.createCriteria(PurchaseOrderPaymentTerms.class).list();
			Iterator iterator=list.iterator(); 

			PurchaseOrderPaymentTerms tempPurchaseOrderPaymentTerms;
			while(iterator.hasNext()){
				tempPurchaseOrderPaymentTerms=(PurchaseOrderPaymentTerms) iterator.next();
				if(Integer.parseInt(paymentTermsID) == tempPurchaseOrderPaymentTerms.getPo_payment_terms_id()){
					poPaymentTerms.setValue(tempPurchaseOrderPaymentTerms.getPo_payment_terms());
				}
			}
			UI.getCurrent().getSession().setAttribute("paymentTermsID","");
			session.close();
		}
		else{
			setFiledsEmpty();
		}

	}

}

