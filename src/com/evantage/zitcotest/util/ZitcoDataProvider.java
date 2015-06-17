package com.evantage.zitcotest.util;

import java.util.Iterator;
import java.util.List;
import org.hibernate.Session;
import com.evantage.zitcotest.domain.user.User;
import com.evantage.zitcotest.session.ZitcoSession;
import com.vaadin.server.VaadinSession;

public class ZitcoDataProvider {
//	private Session session;
		
	public static boolean authenticate(String userName,String password){
		
        Session session=(Session) ZitcoSession.getSession();
        String tempPassword=MD5Utils.getMD5(password);
        
        List<User> list = session.createCriteria(User.class).list();
	    Iterator iterator=list.iterator();

	    User tempUser;
	    while(iterator.hasNext()){
	    	tempUser=(User) iterator.next();
//	    	System.out.println(tempPassword+"...."+tempUser.getPassword()+"... "+tempPassword.equals(tempUser.getPassword()));
	    	if(userName.equals(tempUser.getUser_name()) && tempPassword.equals(tempUser.getPassword())){
	    		System.out.println("User authenticated....");
	    		VaadinSession.getCurrent().setAttribute("user",tempUser);
	    		session.close();
	    		return true;
	    	}
	    }
	    session.close();
//	    System.out.println("User not authenticated return false");
	    return false;
	}
	
	public static User getUserObject(String userName,String password){
		
		Session session=(Session)ZitcoSession.getSession();
        List<User> list = session.createCriteria(User.class).list();
	    Iterator iterator=list.iterator();

	    User tempUser;
	    while(iterator.hasNext()){
	    	tempUser=(User) iterator.next();
	    	if(userName.equals(tempUser.getUser_name()) && password.equals(tempUser.getPassword())){
	    		session.close();
	    		return tempUser;
	    	}
	    }
	    session.close();
		return new User();
	}


}
