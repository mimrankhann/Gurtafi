package com.evantage.zitcotest.util;

import java.sql.DriverManager;

import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.SessionFactory;

import com.mysql.jdbc.Connection;

public class HibernateUtil {
    
	private static final SessionFactory sessionFactory;

    static {
        try {
            sessionFactory = new AnnotationConfiguration().configure().buildSessionFactory();
        } catch (Throwable ex) {
//            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
    public static Connection getConnection(){
    	
    	Connection con = null;
    	
    	try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            con = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/zitco?" +"user=root&password=admin");
        } catch (Exception ex) {
        	System.out.println(ex);
        }
      return con; 
    }
    }
