package com.evantage.zitcotest;

import java.util.Locale;

import javax.servlet.annotation.WebServlet;

import org.hibernate.Session;

import com.evantage.zitcotest.session.ZitcoSession;
//import com.evantage.zitcotest.util.DataProvider;
import com.evantage.zitcotest.util.MD5Utils;
import com.evantage.zitcotest.util.ZitcoDataProvider;
import com.evantage.zitcotest.view.LoginView;
import com.evantage.zitcotest.view.MainView;
import com.evantage.zitcotest.view.supplier.SupplierList;
//import com.evantage.zitcotest.data.DataProvider;
import com.evantage.zitcotest.data.dummy.DummyDataProvider;
import com.evantage.zitcotest.domain.user.User;
import com.evantage.zitcotest.domain.supplier.Supplier;
import com.evantage.zitcotest.event.DashboardEvent.UserLoginRequestedEvent;
import com.evantage.zitcotest.event.DashboardEventBus;
import com.google.common.eventbus.Subscribe;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.Page;
import com.vaadin.server.Page.BrowserWindowResizeEvent;
import com.evantage.zitcotest.event.DashboardEvent.BrowserResizeEvent;
import com.vaadin.server.Page.BrowserWindowResizeListener;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("dashboard")
public class ZitcotestUI extends UI {
    
	private VaadinRequest request;
	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = ZitcotestUI.class,widgetset="com.evantage.zitcotest.DashboardWidgetSet")
	public static class Servlet extends VaadinServlet {
	}
	    //private final DataProvider dataProvider = new DummyDataProvider();
	    private final DashboardEventBus dashboardEventbus = new DashboardEventBus();
	    
	    @Override
	    protected void init(VaadinRequest request) {
	    	//Belo I will compare to encrypted passwords which resulted true...... both are equal	
	    	//System.out.println("Encrypted data = "+MD5Utils.getMD5("zitco123").equals(MD5Utils.getMD5("zitco123")));
	    	//		String array[]={"ahmed","ahmed","ahmed"};
	    	//		for(int i=0;i<array.length;i++)
	    	//		System.out.println(MD5Utils.getMD5(array[i]));
	    	this.request = request;
	    	setLocale(Locale.US);
	    	VaadinSession.getCurrent().setAttribute("request", request);
	    	DashboardEventBus.register(this);
	    	Responsive.makeResponsive(this);
	    	updateContent();

	    	// Some views need to be aware of browser resize events so a
	    	// BrowserResizeEvent gets fired to the event but on every occasion.
	    	Page.getCurrent().addBrowserWindowResizeListener(
	    			new BrowserWindowResizeListener() {
	    				@Override
	    				public void browserWindowResized(
	    						final BrowserWindowResizeEvent event) {
	    					DashboardEventBus.post(new BrowserResizeEvent());
	    				}
	    			});
	    }
	
	
	/**
     * Updates the correct content for this UI based on the current user status.
     * If the user is logged in with appropriate privileges, main view is shown.
     * Otherwise login view is shown.
     */
	private void updateContent() {
		User user = (User) VaadinSession.getCurrent().getAttribute(User.class.getName());
		//        System.out.println(user+" user====== Role= "+user.getRole());
		if (user != null){    //&& "administrator".equalsIgnoreCase(user.getRole())) {
			// Authenticated user
			setContent(new MainView());
			addStyleName("mainview");
			getNavigator().navigateTo(getNavigator().getState());

		} else {
			setContent(new LoginView());
			addStyleName("loginview");
		}
	}
	
	
	 @Subscribe
	    public void userLoginRequested(final UserLoginRequestedEvent event) {
	        //User user = getDataProvider().authenticate(event.getUserName(), event.getPassword());
	     User user=ZitcoDataProvider.getUserObject(event.getUserName(),event.getPassword());   
		 VaadinSession.getCurrent().setAttribute(User.class.getName(), user);
	        updateContent();
	    }


	  /**
     * @return An instance for accessing the (dummy) services layer.
     */
//    public static DataProvider getDataProvider() {
//        return ((ZitcotestUI) getCurrent()).dataProvider;
//    }

    public static DashboardEventBus getDashboardEventbus() {
    	return ((ZitcotestUI) getCurrent()).dashboardEventbus;
    }
    
    public void logOut(){
    	init(request);
    }
    
}