package com.evantage.zitcotest.view.user;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.evantage.zitcotest.domain.customer.Customer;
import com.evantage.zitcotest.domain.user.User;
import com.evantage.zitcotest.event.DashboardEventBus;
import com.evantage.zitcotest.session.ZitcoSession;
import com.evantage.zitcotest.util.MD5Utils;
import com.evantage.zitcotest.view.DashboardViewType;
import com.evantage.zitcotest.view.customer.CustomerList;
import com.evantage.zitcotest.view.supplier.SupplierList;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ValoTheme;

	@SuppressWarnings("serial")
	public class UserView extends Panel implements View,Component{  
		
		public static final String ID = "userWindow";
		private CssLayout userPanel;
		private final VerticalLayout root;

		@PropertyId("name")
		private TextField name;
		
		@PropertyId("role")
		private ComboBox role;

		@PropertyId("user_name")
		private TextField userName;
		@PropertyId("password")
		private PasswordField password;
		
		private UserView userView;
		private VerticalLayout mainLayout;
		
//		private User user;
		String userID = "";
		
		public UserView()
		{
			userView=this;
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
		
		public void setData(UserList userList){
			
		}

		public UserView getuserViewObject(){
			return userView;
		}

		private Component buildComponent(){
			userPanel = new CssLayout();
			userPanel.addStyleName("dashboard-panels");
			userPanel.addComponent(buildLabels());
			userPanel.addComponent(userForm());
			userPanel.setSizeFull();
			return userPanel;
		}

		private Component buildLabels() {
			CssLayout labels = new CssLayout();
			labels.addStyleName("labels");

			Label customer = new Label("Add User");
			customer.setSizeUndefined();
			customer.addStyleName(ValoTheme.LABEL_H4);
			customer.addStyleName(ValoTheme.LABEL_COLORED);
			labels.addComponent(customer);
			return labels;
		}

		public Component userForm(){
			mainLayout=new VerticalLayout();
			
			Button savaBtn = new Button("Save", new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					if(userID != null && userID.trim().length() > 0){
						updateUser();
					}
					else{
						saveUser();
					}
				}
			});

			
			Button cancelBtn= new Button("Cancel", new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					//zz
					UI.getCurrent().getNavigator().addView("user", new UserList());
					UI.getCurrent().getNavigator().navigateTo(DashboardViewType.USERLIST.getViewName());
				}
			});

			HorizontalLayout root = new HorizontalLayout();
			root.setSizeFull(); 
			root.setCaption("user Profile");
			root.setIcon(FontAwesome.USER);
			root.setWidth(100.0f, Unit.PERCENTAGE);
			root.setSpacing(true);
			root.setMargin(true);
			root.addStyleName("profile-form");

			FormLayout details = new FormLayout();
			details.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
			
			root.addComponent(details);
			root.setExpandRatio(details, 1);
			
			name =new TextField("Name");
			name.setNullRepresentation("");
			details.addComponent(name);
			
			role = new ComboBox();
			role.setCaption("Role");
			role.setWidth("80px");
			role.setWidth(350, UNITS_PIXELS);
			role.addItem("Administrator");
			role.addItem("Supervisor");
			role.addItem("Accountant");
			details.addComponent(role);
			
			userName = new TextField("User Name");
			userName.setNullRepresentation("");
			details.addComponent(userName);
			
			password = new PasswordField("Password");
			password.setNullRepresentation("");
			details.addComponent(password);
			
			mainLayout.addComponent(details);
			HorizontalLayout buttonLayout=new HorizontalLayout();
			buttonLayout.addComponent(savaBtn);
			buttonLayout.addComponent(cancelBtn);
			
			mainLayout.addComponent(buttonLayout);
			mainLayout.setComponentAlignment(buttonLayout, Alignment.BOTTOM_RIGHT);
			return mainLayout;

		}
		
		public void saveUser(){
			if(saveValidation()){
				Session session=ZitcoSession.getSession();
				User user = new User();
				user.setName(name.getValue());
				user.setRole((String) role.getValue());
				user.setUser_name(userName.getValue());
				String encryptedPassword=MD5Utils.getMD5(password.getValue());
				user.setPassword(encryptedPassword);
				session.save(user);
				session.getTransaction().commit();
				session.close();
				setFiledsEmpty();

				UI.getCurrent().getNavigator().addView("user", new UserList());
				UI.getCurrent().getNavigator().navigateTo(DashboardViewType.USERLIST.getViewName());
				Notification.show("User Saved Successfully.....",Notification.TYPE_WARNING_MESSAGE);
			}
			else{
				Notification.show("Please Fill All The Fields First.....",Notification.TYPE_WARNING_MESSAGE);
			}
		}

		public void updateUser(){
			if(updateValidation()){
				Session session=ZitcoSession.getSession();
				User user = new User();
				user.setId(Integer.parseInt(userID));
				user.setName(name.getValue());
				user.setRole((String) role.getValue());
				user.setUser_name(userName.getValue());
				String encryptedPassword=MD5Utils.getMD5(password.getValue());
				user.setPassword(encryptedPassword);
				session.update(user);
				session.getTransaction().commit();
				session.close();
				setFiledsEmpty();

				UI.getCurrent().getNavigator().addView("user", new UserList());
				UI.getCurrent().getNavigator().navigateTo(DashboardViewType.USERLIST.getViewName());
				Notification.show("User Updated Successfully.....",Notification.TYPE_WARNING_MESSAGE);
			}
			else{
				Notification.show("Please Fill All The Fields First.....",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		
		public boolean saveValidation(){

			if(name.getValue() == "" || userName.getValue() == "" || password.getValue()==""
					|| role.getValue()== "" ){
				return false;
			}

			return true;
		}

		public boolean updateValidation(){

			if(name.getValue().trim().length() <= 0 || userName.getValue().trim().length() <= 0 || 
					password.getValue().trim().length() <= 0 || ((String) role.getValue()).trim().length() <= 0){
				return false;
			}

			return true;
		}		
		
		

		public boolean validation(){
			
//			if(userId.getValue() =="" || userFirstName.getValue() == "" || userLastName.getValue()==""
//					|| sex.getValue()=="" || email.getValue()==""){
//				if(userId.getValue() ==null || userFirstName.getValue() == null || userLastName.getValue()==null
//						|| sex.getValue()==null || email.getValue()==null){
//					return false;
//				}
//			}
//			
			return true;
			
		}
		public void setFiledsEmpty(){
			name.setValue("");
			role.setValue("");
		    userName.setValue("");
		    password.setValue("");
		}
		
	
		@Override
		public void enter(ViewChangeEvent event) {
			try{
				userID = UI.getCurrent().getSession().getAttribute("userID").toString();
			}
			catch(Exception e){
				userID = "";
			}
			
			if(userID != null && userID.trim().length() > 0){

				BeanItemContainer<User> container = new BeanItemContainer<User>(User.class);
				Session session=(Session)ZitcoSession.getSession();
				List<User> list = session.createCriteria(User.class).list();
				Iterator iterator=list.iterator(); 

				User tempUser;
				while(iterator.hasNext()){
					tempUser=(User) iterator.next();
					if(Integer.parseInt(userID) == tempUser.getId()){
						name.setValue(tempUser.getName());
						role.setValue(tempUser.getRole());
						userName.setValue(tempUser.getUser_name());
						password.setValue(tempUser.getPassword());
					}
				}
				UI.getCurrent().getSession().setAttribute("userID","");
				session.close();
				
			}
			else{
				setFiledsEmpty();
			}
			
		}
	}

