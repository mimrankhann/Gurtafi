package com.evantage.zitcotest.component;

import org.hibernate.Session;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.BeanItemContainer;
//import com.evantage.zitcotest.domain.user.Supplier;
import com.evantage.zitcotest.domain.user.User;
import com.evantage.zitcotest.event.DashboardEvent.CloseOpenWindowsEvent;
import com.evantage.zitcotest.event.DashboardEvent.ProfileUpdatedEvent;
import com.evantage.zitcotest.event.DashboardEventBus;
import com.evantage.zitcotest.session.ZitcoSession;
import com.evantage.zitcotest.util.ZitcoDataProvider;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.UserError;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class ProfilePreferencesWindow extends Window {

    public static final String ID = "profilepreferenceswindow";

    private final BeanFieldGroup<User> fieldGroup;
    private User tempUser;
    private BeanItemContainer<User> container; 
    /*
     * Fields for editing the User object are defined here as class members.
     * They are later bound to a FieldGroup by calling
     * fieldGroup.bindMemberFields(this). The Fields' values don't need to be
     * explicitly set, calling fieldGroup.setItemDataSource(user) synchronizes
     * the fields with the user object.
     */

    private ProfilePreferencesWindow(final User user,
        final boolean preferencesTabOpen) {
        this.tempUser=user;
    	addStyleName("profile-window");
        setId(ID);
        Responsive.makeResponsive(this);

        setModal(true);
        setCloseShortcut(KeyCode.ESCAPE, null);
        setResizable(false);
        setClosable(false);
        setHeight(90.0f, Unit.PERCENTAGE);

        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setMargin(new MarginInfo(true, false, false, false));
        setContent(content);

        TabSheet detailsWrapper = new TabSheet();
        detailsWrapper.setSizeFull();
        detailsWrapper.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
        detailsWrapper.addStyleName(ValoTheme.TABSHEET_ICONS_ON_TOP);
        detailsWrapper.addStyleName(ValoTheme.TABSHEET_CENTERED_TABS);
        content.addComponent(detailsWrapper);
        content.setExpandRatio(detailsWrapper, 1f);

        detailsWrapper.addComponent(buildProfileTab());
        detailsWrapper.addComponent(buildPreferencesTab());

        if (preferencesTabOpen) {
            detailsWrapper.setSelectedTab(1);
        }

        content.addComponent(buildFooter());

        fieldGroup = new BeanFieldGroup<User>(User.class);
        fieldGroup.bindMemberFields(this);
        fieldGroup.setItemDataSource(user);
    }

    private Component buildPreferencesTab() {
        VerticalLayout root = new VerticalLayout();
        root.setCaption("Preferences");
        root.setIcon(FontAwesome.COGS);
        root.setSpacing(true);
        root.setMargin(true);
        root.setSizeFull();

        Label message = new Label("Not implemented in this demo");
        message.setSizeUndefined();
        message.addStyleName(ValoTheme.LABEL_LIGHT);
        root.addComponent(message);
        root.setComponentAlignment(message, Alignment.MIDDLE_CENTER);

        return root;
    }

    private Component buildProfileTab() {
        HorizontalLayout root = new HorizontalLayout();
        root.setCaption("Profile");
        root.setIcon(FontAwesome.USER);
        root.setWidth(100.0f, Unit.PERCENTAGE);
        root.setSpacing(true);
        root.setMargin(true);
        root.addStyleName("loginform-view");

        VerticalLayout pic = new VerticalLayout();
        pic.setSizeUndefined();
        pic.setSpacing(true);
        Image profilePic = new Image(null, new ThemeResource(
                "img/profile-pic-300px.jpg"));
        profilePic.setWidth(100.0f, Unit.PIXELS);
        pic.addComponent(profilePic);

        Button upload = new Button("Change…", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                Notification.show("Not implemented in this demo");
            }
        });
        upload.addStyleName(ValoTheme.BUTTON_TINY);
        pic.addComponent(upload);

        root.addComponent(pic);

        FormLayout details = new FormLayout();
        details.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        root.addComponent(details);
        root.setExpandRatio(details, 1);


        Label section = new Label("Contact Info");
        section.addStyleName(ValoTheme.LABEL_H4);
        section.addStyleName(ValoTheme.LABEL_COLORED);
        details.addComponent(section);

//         = new OptionalSelect<Integer>();
//        newsletterField.addOption(0, "Daily");
//        newsletterField.addOption(1, "Weekly");
//        newsletterField.addOption(2, "MnewsletterFieldonthly");
//        details.addComponent(newsletterField);

//        section = new Label("Additional Info");
//        section.addStyleName(ValoTheme.LABEL_H4);
//        section.addStyleName(ValoTheme.LABEL_COLORED);
//        details.addComponent(section);

//        website = new TextField("Website");
//        website.setInputPrompt("http://");
//        website.setWidth("100%");
//        website.setNullRepresentation("");
//        details.addComponent(website);
//
//        email = new TextField("Email");
//        email.setWidth("100%");
//        email.setNullRepresentation("");
//        details.addComponent(email);

        return root;
    }

    private Component buildFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Unit.PERCENTAGE);

        Button ok = new Button("OK");
        ok.addStyleName(ValoTheme.BUTTON_PRIMARY);
        ok.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    
                    // Updated user should also be persisted to database. But
                    // not in this demo.
                    //ZitcoDataProvider.getUserObject(user, password);
                    
                    Session session=ZitcoSession.getSession();
					User user = tempUser;
					
//					user.setName(firstName.getValue());
//					user.setRole(role.getValue());
//					user.setPhone(phone.getValue());
//					user.setLocation(location.getValue());
//					
//					//user.setSex((String) sex.getValue());
//					//user.setTitle((String) title.getValue());
//				    user.setEmail(email.getValue());
////				    if(compareBean(tempUser,user)){
				    session.saveOrUpdate(user);
				    session.getTransaction().commit();
				    session.close();
				    
				    Notification success = new Notification(
                            "Profile updated successfully");
                    success.setDelayMsec(2000);
                    success.setStyleName("bar success small");
                    success.setPosition(Position.BOTTOM_CENTER);
                    success.show(Page.getCurrent());

                    DashboardEventBus.post(new ProfileUpdatedEvent());
                    close();
                    fieldGroup.commit();
//				    }
//				    
////                    DashboardEventBus.post(new ProfileUpdatedEvent());
//				    Notification success = new Notification(
//                            "Profile Not updated due to no change made");
//                    success.setDelayMsec(2000);
//                    success.setStyleName("bar success small");
//                    success.setPosition(Position.BOTTOM_CENTER);
//                    success.show(Page.getCurrent());
//                    session.close();
//				    close();
                } catch (CommitException e) {
                    Notification.show("Error while updating profile",
                            Type.ERROR_MESSAGE);
                    System.out.println(e);
                }

            }
        });
        ok.focus();
        footer.addComponent(ok);
        footer.setComponentAlignment(ok, Alignment.TOP_RIGHT);
        return footer;
    }
    
    public static boolean compareBean(User source,User desti){
//    	String userFirstName = source.getFirstName();
//    	String userLastName = source.getLastName();
//    	String userPhone = source.getPhone();
//    	String userSex = source.getSex();
//    	String userLocation=source.getLocation();
//    	String userEmail=source.getEmail();
//    	String userTitle=source.getTitle();
//    	
//    	if(userFirstName.equals(desti.getFirstName()) &&
//    	   userLastName.equals(desti.getLocation())  &&
//    	   userPhone.equals(desti.getPhone())  && 
//    	   userSex.equals(desti.getSex())  &&
//    	   userLocation.equals(desti.getLocation())  &&
//    	   userEmail.equals(desti.getEmail())  &&
//    	   userTitle.equals(desti.getTitle())) 
//    	   {
//    			return true;
//    	   }
    	return false;
    	
    }

    public static void open(final User user, final boolean preferencesTabActive) {
        DashboardEventBus.post(new CloseOpenWindowsEvent());
        Window w = new ProfilePreferencesWindow(user, preferencesTabActive);
        UI.getCurrent().addWindow(w);
        w.focus();
    }
}
