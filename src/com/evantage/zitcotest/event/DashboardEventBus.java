package com.evantage.zitcotest.event;

import com.evantage.zitcotest.ZitcotestUI;
import com.evantage.zitcotest.event.DashboardEvent.UserLoginRequestedEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;

/**
 * A simple wrapper for Guava event bus. Defines static convenience methods for
 * relevant actions.
 */
public class DashboardEventBus implements SubscriberExceptionHandler {

    private final EventBus eventBus = new EventBus(this);

    public static void post(final Object event) {
        ZitcotestUI.getDashboardEventbus().eventBus.post(event);
        
        if(event instanceof UserLoginRequestedEvent) {
        	UserLoginRequestedEvent userCredentials = (UserLoginRequestedEvent) event;
        	System.out.println("User Name : " + userCredentials.getUserName());
        }
    }

    public static void register(final Object object) {
        ZitcotestUI.getDashboardEventbus().eventBus.register(object);
    }

    public static void unregister(final Object object) {
        ZitcotestUI.getDashboardEventbus().eventBus.unregister(object);
    }

    @Override
    public final void handleException(final Throwable exception,
            final SubscriberExceptionContext context) {
        exception.printStackTrace();
    }
}
