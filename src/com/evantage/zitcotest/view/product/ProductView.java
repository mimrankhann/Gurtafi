package com.evantage.zitcotest.view.product;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.evantage.zitcotest.domain.customer.Customer;
import com.evantage.zitcotest.domain.product.SupplierProduct;
import com.evantage.zitcotest.domain.product.ProductType;
import com.evantage.zitcotest.domain.purchase_order.Item;
import com.evantage.zitcotest.domain.supplier.Supplier;
import com.evantage.zitcotest.event.DashboardEventBus;
import com.evantage.zitcotest.session.ZitcoSession;
import com.evantage.zitcotest.view.DashboardViewType;
import com.evantage.zitcotest.view.customer.CustomerList;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Select;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

	@SuppressWarnings("serial")
	public class ProductView extends Panel implements View,Component{  
		
		public static final String ID = "productWindow";
		private CssLayout productPanel;
		private final VerticalLayout root;
		private BeanItemContainer<ProductType> productTypeContainer = new BeanItemContainer<ProductType>(ProductType.class);
		
		@PropertyId("product_id")
		private TextField productId;
		
		@PropertyId("supplier_name")
		private ComboBox supplierName;
		@PropertyId("product_name")
		private TextField productName;
		@PropertyId("product_price")
		private TextField productPrice;
		@PropertyId("ci_no")
		private TextField ci_no;
		@PropertyId("cas_no")
		private TextField cas_no;
		
		private ProductView ProductView;
		private VerticalLayout mainLayout;
		private SupplierProduct product;
		
		private String productID = "";
		
		public ProductView()
		{
			ProductView=this;
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
		
		public void setData(ProductList ProductList){
			
		}

		public ProductView getProductViewObject(){
			return ProductView;
		}

		private Component buildComponent(){
			productPanel = new CssLayout();
			productPanel.addStyleName("dashboard-panels");
			productPanel.addComponent(buildLabels());
			productPanel.addComponent(productForm());
			productPanel.setSizeFull();
			return productPanel;
		}

		private Component buildLabels() {
			CssLayout labels = new CssLayout();
			labels.addStyleName("labels");

			Label customer = new Label("Add New Product");
			customer.setSizeUndefined();
			customer.addStyleName(ValoTheme.LABEL_H4);
			customer.addStyleName(ValoTheme.LABEL_COLORED);
			labels.addComponent(customer);
			return labels;
		}


		public Component productForm(){

			mainLayout=new VerticalLayout();
			
			Button savaBtn = new Button("Save", new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {

					if(productID != null && !productID.equals("")){
						updateProduct();
					}
					else{
						saveProduct();
					}
				}
			});
			
			Button cancelBtn= new Button("Cancel", new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					//zz
					UI.getCurrent().getNavigator().addView("product", new ProductList());
					UI.getCurrent().getNavigator().navigateTo(DashboardViewType.PRODUCTLIST.getViewName());
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
			
//************ Below set Supplier Name ComboBox values from bean			

//			Session supplierSession=(Session)ZitcoSession.getSession();
//	        List<Supplier> supplierList = supplierSession.createCriteria(Supplier.class).list();
			supplierName = new ComboBox();
			supplierName.setCaption("Supplier Name");
			supplierName.setContainerDataSource(getSupplierContainer());
//			supplierName.addItems(supplierList);
			supplierName.setItemCaptionPropertyId("supplier_name");
			supplierName.setWidth(350, UNITS_PIXELS);
			supplierName.setImmediate(true);
//			supplierSession.close(); 
			details.addComponent(supplierName);
//************	End		
			productName = new TextField("Product Name");
			details.addComponent(productName);
			
			productPrice = new TextField("Product Price");
			details.addComponent(productPrice);
//************ Below set Product Type ComboBox values from bean
//	        Session session=(Session)ZitcoSession.getSession();
//	        List<SupplierProduct> productTypeList = session.createCriteria(ProductType.class).list();
//			
//			productType = new ComboBox();
//			productType.setCaption("Product Type");
//			productType.setContainerDataSource(productTypeContainer);
//			productType.addItems(productTypeList);
//			productType.setItemCaptionPropertyId("product_type");
//			productType.setWidth(350, UNITS_PIXELS);
//			productType.setImmediate(true);
//			session.close(); 
//			details.addComponent(productType);
//************	End		
			ci_no = new TextField("CI #");
			details.addComponent(ci_no);
            
			cas_no = new TextField("CAS #");
			details.addComponent(cas_no);
			
			mainLayout.addComponent(details);
			HorizontalLayout buttonLayout=new HorizontalLayout();
			buttonLayout.addComponent(savaBtn);
			buttonLayout.addComponent(cancelBtn);
			
			mainLayout.addComponent(buttonLayout);
			mainLayout.setComponentAlignment(buttonLayout, Alignment.BOTTOM_RIGHT);
			return mainLayout;

		}
		
		public void saveProduct(){
			
			if(saveValidation()){
				Session tempSession = ZitcoSession.getSession();
				List<Supplier> supplierList = tempSession.createCriteria(Supplier.class).list();
				ProductType tempProductType;

				Session session=ZitcoSession.getSession();
				SupplierProduct supplierProduct = new SupplierProduct();
				supplierProduct.setProduct_name(productName.getValue());
				supplierProduct.setProduct_price(Integer.parseInt(productPrice.getValue()));
				supplierProduct.setCi_no(ci_no.getValue());
				supplierProduct.setCas_no(cas_no.getValue());
				session.save(supplierProduct);

				Supplier tempSupplierProduct = getSupplierByName((Integer) supplierName.getValue());
				supplierProduct.setSupplier(tempSupplierProduct);
				supplierProduct.getSupplier().getSupplierProduct().add(supplierProduct);
				session.save(supplierProduct);

				session.getTransaction().commit();
				session.close();
				tempSession.close();
				setFiledsEmpty();
				
				UI.getCurrent().getNavigator().addView("product", new ProductList());
				UI.getCurrent().getNavigator().navigateTo(DashboardViewType.PRODUCTLIST.getViewName());
				Notification.show("Product Information Save Successfully.....",Notification.TYPE_WARNING_MESSAGE);
			}
			else{
				Notification.show("Please Fill All The Fields First.....",Notification.TYPE_WARNING_MESSAGE);
			}

		}

		public void updateProduct(){
			
			if(updateValidation()){
				Session tempSession = ZitcoSession.getSession();
				List<Supplier> supplierList = tempSession.createCriteria(Supplier.class).list();
				ProductType tempProductType;

				Session session=ZitcoSession.getSession();
				SupplierProduct supplierProduct = new SupplierProduct();
				supplierProduct.setSupplier_product_id(Integer.parseInt(productID));
				supplierProduct.setProduct_name(productName.getValue());
				supplierProduct.setProduct_price(Integer.parseInt(productPrice.getValue()));
				supplierProduct.setCi_no(ci_no.getValue());
				supplierProduct.setCas_no(cas_no.getValue());
				session.update(supplierProduct);

				Supplier tempSupplierProduct = getSupplierByName((Integer) supplierName.getValue());
				supplierProduct.setSupplier(tempSupplierProduct);
				supplierProduct.getSupplier().getSupplierProduct().add(supplierProduct);
				session.update(supplierProduct);
				
				session.getTransaction().commit();
				session.close();
				tempSession.close();
				setFiledsEmpty();
				
				UI.getCurrent().getNavigator().addView("product", new ProductList());
				UI.getCurrent().getNavigator().navigateTo(DashboardViewType.PRODUCTLIST.getViewName());
				Notification.show("Product Information Updated Successfully.....",Notification.TYPE_WARNING_MESSAGE);
			}
			else{
				Notification.show("Please Fill All The Fields First.....",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		
		public boolean saveValidation(){

			if(productName.getValue() == "" || productPrice.getValue() == "" || ci_no.getValue()==""
					|| cas_no.getValue()== "" || supplierName.getValue() == "" ){
				return false;
			}

			return true;
		}

		public boolean updateValidation(){

			if(productName.getValue().trim().length() <= 0 || productPrice.getValue().trim().length() <= 0 || 
					ci_no.getValue().trim().length() <= 0 || cas_no.getValue().trim().length() <= 0){
				return false;
			}

			return true;		
		}
        
		public void setFiledsEmpty(){
			
//			supplierName.select("");
			productName.setValue("");
			productPrice.setValue("");
//			productType.select("");
			ci_no.setValue("");
			cas_no.setValue("");
			
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
		
		@Override
		public void enter(ViewChangeEvent event) {
			
			try{
				productID = UI.getCurrent().getSession().getAttribute("productID").toString();
			}
			catch(Exception e){
				productID = "";
			}
			
			if(productID != null && productID.trim().length() > 0){

				BeanItemContainer<SupplierProduct> container = new BeanItemContainer<SupplierProduct>(SupplierProduct.class);
				Session session=(Session)ZitcoSession.getSession();
				List<SupplierProduct> list = session.createCriteria(SupplierProduct.class).list();
				Iterator iterator=list.iterator(); 
				
				SupplierProduct tempSupplierProduct;
				Supplier tempSupplier;
				while(iterator.hasNext()){
					tempSupplierProduct=(SupplierProduct) iterator.next();
					if(Integer.parseInt(productID) == tempSupplierProduct.getSupplier_product_id()){
						tempSupplier = tempSupplierProduct.getSupplier();
						supplierName.select(tempSupplier.getSupplier_id());
						productName.setValue(tempSupplierProduct.getProduct_name());
						productPrice.setValue(tempSupplierProduct.getProduct_price().toString());
//						productType.select(tempSupplierProduct.getProduct_type());
						ci_no.setValue(tempSupplierProduct.getCi_no());
						cas_no.setValue(tempSupplierProduct.getCas_no());
					}
				}
				UI.getCurrent().getSession().setAttribute("productID","");
				session.close();
			}
			else{
				setFiledsEmpty();
			}
		}
		
		private IndexedContainer getSupplierContainer() {
			
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
		
	}
