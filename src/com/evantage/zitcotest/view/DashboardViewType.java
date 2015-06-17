package com.evantage.zitcotest.view;

import com.evantage.zitcotest.view.customer.CustomerList;
import com.evantage.zitcotest.view.customer.CustomerView;
import com.evantage.zitcotest.view.payment_terms.PaymentTermsList;
import com.evantage.zitcotest.view.payment_terms.PaymentTermsView;
import com.evantage.zitcotest.view.product.ProductList;
import com.evantage.zitcotest.view.product.ProductView;
import com.evantage.zitcotest.view.purchase_order.PurchaseOrderList;
import com.evantage.zitcotest.view.purchase_order.PurchaseOrderView;
import com.evantage.zitcotest.view.purchase_order_status_updation.PurchaseOrderStatusUpdationList;
import com.evantage.zitcotest.view.purchase_order_status_updation.PurchaseOrderStatusUpdationView;
import com.evantage.zitcotest.view.reports.ReportsView;
import com.evantage.zitcotest.view.supplier.SupplierList;
import com.evantage.zitcotest.view.supplier.SupplierView;
import com.evantage.zitcotest.view.user.UserList;
import com.evantage.zitcotest.view.user.UserView;
import com.vaadin.navigator.View;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;

public enum DashboardViewType {
    DASHBOARD("dashboard", DashboardView.class, FontAwesome.HOME, true,false),
    CUSTOMERVIEW("customerview",CustomerView.class,FontAwesome.FEMALE,true,true),
    CUSTOMERLIST("customer",CustomerList.class,FontAwesome.USER_MD,true,false),
    SUPPLIERVIEW("supplierview",SupplierView.class,FontAwesome.FEMALE,true,true),
    SUPPLIERLIST("supplier",SupplierList.class,FontAwesome.USER_MD,true,false),
    PRODUCTVIEW("productview",ProductView.class,FontAwesome.FEMALE,true,true),
    PRODUCTLIST("product",ProductList.class,FontAwesome.SHOPPING_CART,true,false),
    PAYMENTTERMSVIEW("paymenttermsview",PaymentTermsView.class,FontAwesome.FEMALE,true,true),
    PAYMENTTERMSLIST("paymentTerms",PaymentTermsList.class,FontAwesome.RECYCLE,true,false),
    USERVIEW("userview",UserView.class,FontAwesome.FEMALE,true,true),
    USERLIST("user",UserList.class,FontAwesome.USERS,true,false),
    PURCHASEORDERVIEW("IFView",PurchaseOrderView.class,FontAwesome.FEMALE,true,true),
    PURCHASEORDERLIST("IFList",PurchaseOrderList.class,FontAwesome.INDENT,true,false),
    PURCHASEORDERSTATUSUPDATIONVIEW("IFUpdationView",PurchaseOrderStatusUpdationView.class,FontAwesome.FEMALE,true,true),
    PURCHASEORDERSTATUSUPDATIONLIST("IFUpdationList",PurchaseOrderStatusUpdationList.class,FontAwesome.DATABASE,true,false),
    REPORTSVIEW("reports",ReportsView.class,FontAwesome.FILE_PDF_O,true,false);

    
    private final String viewName;
    private final Class<? extends View> viewClass;
    private final Resource icon;
    private final boolean stateful;
    private final boolean excludeFromMenu;

    private DashboardViewType(final String viewName,
            final Class<? extends View> viewClass, final Resource icon,
            final boolean stateful, final boolean excludeFromMenu) {
        this.viewName = viewName;
        this.viewClass = viewClass;
        this.icon = icon;
        this.stateful = stateful;
        this.excludeFromMenu = excludeFromMenu;
    }

    public boolean isStateful() {
        return stateful;
    }

    public String getViewName() {
        return viewName;
    }

    public Class<? extends View> getViewClass() {
        return viewClass;
    }

    public Resource getIcon() {
        return icon;
    }

    public boolean isExcludeFromMenu() {
		return excludeFromMenu;
	}

	public static DashboardViewType getByViewName(final String viewName) {
        DashboardViewType result = null;
        for (DashboardViewType viewType : values()) {
            if (viewType.getViewName().equals(viewName)) {
                result = viewType;
                break;
            }
        }
        return result;
    }

}
