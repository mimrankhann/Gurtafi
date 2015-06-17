package com.evantage.zitcotest.view.purchase_order_status_updation;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.evantage.zitcotest.domain.product.SupplierProduct;
import com.evantage.zitcotest.domain.purchase_order.Item;
import com.evantage.zitcotest.domain.purchase_order.SelectedItems;
import com.evantage.zitcotest.domain.supplier.Supplier;
import com.evantage.zitcotest.session.ZitcoSession;
import com.evantage.zitcotest.view.purchase_order.PurchaseOrderView;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ValoTheme;

public class FileUploadItemList extends CustomField<Item>{
    
	@PropertyId("item_name")
	private ComboBox addCombo,editCombo,deleteCombo;
	@PropertyId("quantity")
	private TextField quantity;
	
	@PropertyId("item_price")
	private TextField price;
	
	private Table itemTable = new Table();
	private VerticalLayout main = new VerticalLayout();
	
	private Button add, edit, delete;
	private BeanItemContainer<Item> container= new BeanItemContainer<Item>(Item.class);
	private BeanItemContainer<SelectedItems> selectedItemContainer = new BeanItemContainer<SelectedItems>(SelectedItems.class);
	private Supplier tempSupplier;
	private final ComboBox supplierProductCombo = new ComboBox();
	private Supplier supplierComboObject;
	
	public FileUploadItemList() {
		
		main.setSizeFull();
		itemTable.addStyleName(ValoTheme.TABLE_BORDERLESS);
		itemTable.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
		itemTable.addStyleName(ValoTheme.TABLE_COMPACT);
		itemTable.setImmediate(true);
		itemTable.setColumnCollapsingAllowed(true);
		itemTable.setColumnCollapsible("time", false);
		itemTable.setColumnCollapsible("price", false);
		itemTable.setColumnReorderingAllowed(true);
		itemTable.setSelectable(true);
		itemTable.setWidth("100%");
        itemTable.setHeight("170px");
        
		HorizontalLayout buttonBar = new HorizontalLayout();
		
		add = new Button("Add Product", new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				
				final Window addWindow = new Window("ADD PRODUCT");
				addWindow.center();
				addWindow.setResizable(false);
				VerticalLayout mainItemLayout=new VerticalLayout();
				HorizontalLayout buttonLayout = new HorizontalLayout();
			    addWindow.setModal(false);
			    Panel itemPanel = new Panel();
			    addWindow.setContent(itemPanel);
			    addWindow.setWidth("400px");
			    addWindow.setHeight("400px");
			    
			    final FormLayout windowForm = new FormLayout();
			    windowForm.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
			    
				
				supplierProductCombo.setCaption("Select Product");
				supplierProductCombo.setNullSelectionAllowed(false);
				supplierProductCombo.setTextInputAllowed(false);
				supplierProductCombo.setWidth(250, UNITS_PIXELS);
				supplierProductCombo.setImmediate(true);

//******* Get PurchaseOrderViewObject & Selected Supplier from PurchaseOrderView - Start
				    	PurchaseOrderView purchaseOrderVeiwObject = PurchaseOrderView.getPurchaseOrderViewObject();
				    	tempSupplier = purchaseOrderVeiwObject.getPurchaseOrderViewSupplier();
//******* Get PurchaseOrderViewObject & Selected Supplier from PurchaseOrderView - End				

				    	
//******* Assign Selected Supplier Products - Start
				    	
						BeanItemContainer<SupplierProduct> supplierProductContainer= new BeanItemContainer<SupplierProduct>(SupplierProduct.class);
					    Session supplierProductSession=(Session)ZitcoSession.getSession();
				        List<SupplierProduct> supplierProductList = supplierProductSession.createCriteria(SupplierProduct.class).list();
				        
						if(tempSupplier != null){
							Iterator supplierProductIterator = supplierProductList.iterator();
					        SupplierProduct tempSupplierProduct;
					        
					        while(supplierProductIterator.hasNext()){
					        	tempSupplierProduct = (SupplierProduct) supplierProductIterator.next();
					        	Supplier tempSupplierObject = tempSupplierProduct.getSupplier();
					        	
					        	int supplierID = tempSupplier.getSupplier_id().intValue();
					        	int supplierProductID = tempSupplierObject.getSupplier_id().intValue();
					        	
					        	if(supplierID == supplierProductID){
					        		supplierProductCombo.addItem(tempSupplierProduct.getProduct_name());
					        	}
					        }
					        windowForm.addComponent(supplierProductCombo);
					        supplierProductSession.close(); 
					        
					        quantity = new TextField("Quantity");
							quantity.setNullRepresentation("");
							windowForm.addComponent(quantity);
							
							price = new TextField("Price");
							price.setNullRepresentation("");
							windowForm.addComponent(price);
						}
						else{
							Notification.show("Please Select Supplier First from IF.....",Notification.TYPE_WARNING_MESSAGE);
						}
//******* Assign Selected Supplier Products - End
						

//******* Assigning Product Price - Start
						supplierProductCombo.addListener(new ValueChangeListener() {
							@Override
							public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
								String tempSupplierProductObject = (String) supplierProductCombo.getValue();
								
								BeanItemContainer<SupplierProduct> supplierProductContainer= new BeanItemContainer<SupplierProduct>(SupplierProduct.class);
							    Session supplierProductSession1=(Session)ZitcoSession.getSession();
						        List<SupplierProduct> supplierProductList = supplierProductSession1.createCriteria(SupplierProduct.class).list();
						        
						        Iterator supplierProductIterator = supplierProductList.iterator();
						        SupplierProduct tempSupplierProduct;
							        
								if(tempSupplierProductObject != null){
									 while(supplierProductIterator.hasNext()){
										 tempSupplierProduct = (SupplierProduct) supplierProductIterator.next();
										 
										 if(tempSupplierProduct.getProduct_name().equalsIgnoreCase(tempSupplierProductObject.trim())){
											 price.setValue(tempSupplierProduct.getProduct_price()+"");
											 //supplierComboObject =tempSupplier;
										 }
									 }
								}
								supplierProductSession1.close();
							}
							
							
						});
//******* Assigning Product Price - End	

				Button windowDeleteButton=new Button("Add",new ClickListener(){

					@Override
					public void buttonClick(ClickEvent event) {
						
						SelectedItems selectedItem = new SelectedItems();
						Supplier supplierComboObject = tempSupplier;
						
						if(quantity.getValue() != null && price.getValue() != null){
							String tempQuantity = quantity.getValue();
							selectedItem.setSelected_item(supplierComboObject.getSupplier_name().trim());
							selectedItem.setQuantity(Integer.parseInt(tempQuantity));
							int tempPrice = new Integer(tempQuantity) * Integer.parseInt(price.getValue());
							selectedItem.setPrice(tempPrice);
						}
						
						selectedItemContainer.addBean(selectedItem);

						itemTable.setContainerDataSource(selectedItemContainer);
						
				        itemTable.setWidth("100%");
				        itemTable.setHeight("150px");
				        itemTable.setImmediate(true);
						itemTable.setVisibleColumns("selected_item","quantity","price");
				        itemTable.setColumnHeaders("Product Name","Quantity","Amount");
				        
				        UI.getCurrent().removeWindow(addWindow);
					}
				});
				
				Button windowCancelButton=new Button("Cancel",new ClickListener(){
					@Override
					public void buttonClick(ClickEvent event) {
						UI.getCurrent().removeWindow(addWindow);
					}
				});
				
				mainItemLayout.addComponent(windowForm);
				buttonLayout.addComponent(windowDeleteButton);
				buttonLayout.addComponent(windowCancelButton);
				mainItemLayout.addComponent(buttonLayout);
				mainItemLayout.setComponentAlignment(buttonLayout, Alignment.BOTTOM_RIGHT);
				addWindow.setContent(mainItemLayout);
				//addWindow.setContent(buttonLayout);
				UI.getCurrent().addWindow(addWindow);
		
			}
		});
		
	}

	@Override
	protected Component initContent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<? extends Item> getType() {
		// TODO Auto-generated method stub
		return null;
	}
}
