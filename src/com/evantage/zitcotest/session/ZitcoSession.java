package com.evantage.zitcotest.session;

import org.hibernate.Session;
import com.evantage.zitcotest.util.HibernateUtil;

public class ZitcoSession {
	
	private static Session session=null;
	
	public static Session getSession(){
		session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
        return session;
 	}
	
	public static void closeSession(){
		
			session.flush();
			session.close();
		
	}

}
