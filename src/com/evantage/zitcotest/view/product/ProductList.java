package com.evantage.zitcotest.view.product;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.vaadin.maddon.FilterableListContainer;

import com.evantage.zitcotest.DashboardNavigator;
import com.evantage.zitcotest.ZitcotestUI;
import com.evantage.zitcotest.session.ZitcoSession;
import com.evantage.zitcotest.view.DashboardMenu;
import com.evantage.zitcotest.view.DashboardView;
import com.evantage.zitcotest.view.DashboardViewType;
import com.evantage.zitcotest.view.purchase_order.PurchaseOrderView;
import com.evantage.zitcotest.domain.customer.Customer;
import com.evantage.zitcotest.domain.Transaction;
import com.evantage.zitcotest.domain.product.SupplierProduct;
import com.evantage.zitcotest.domain.supplier.Supplier;
import com.evantage.zitcotest.event.DashboardEvent.BrowserResizeEvent;
import com.evantage.zitcotest.event.DashboardEvent.UserLoginRequestedEvent;
import com.evantage.zitcotest.event.DashboardEventBus;
import com.google.common.eventbus.Subscribe;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ClientConnector;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
	
	@SuppressWarnings({ "serial", "unchecked" })

	public class ProductList extends VerticalLayout implements View,Component {
            //zz
		    private VaadinSession session=UI.getCurrent().getSession();
		    public static final String VIEW="ProductList";
			public ProductList productList; 
		    private final Table table;
		    private Button createReport; 
		    private static final String[] DEFAULT_COLLAPSIBLE = { "country", "city",
		            "theater", "room", "title", "seats" };
		    private HorizontalLayout header;
		    private HorizontalLayout tools;
		    		    
		    public ProductList() {
		        setSizeFull();
		        addStyleName("transactions");
		        DashboardEventBus.register(this);
				productList=this;
		        addComponent(buildToolbar());

		        table = buildTable();
		        addComponent(table);
		        setExpandRatio(table, 1);
		    }
		    
		    public ProductList getProductListObject(){
		    	return productList;
		    }
		    
		    @Override
		    public void detach() {
		        super.detach();
		        // A new instance of TransactionsView is created every time it's
		        // navigated to so we'll need to clean up references to it on detach.
		        
		        //zzz - previously it raised null pointer exception .....uncomment
		        //DashboardEventBus.unregister(this);
		    }

		    private Component buildToolbar() {
		        header= new HorizontalLayout();
		        header.addStyleName("viewheader");
		        header.setSpacing(true);
		        Responsive.makeResponsive(header);

		        Label title = new Label("Product List");
		        title.setSizeUndefined();
		        title.addStyleName(ValoTheme.LABEL_H1);
		        title.addStyleName(ValoTheme.LABEL_NO_MARGIN);
		        header.addComponent(title);

		        createReport = buildCreateReport();
		        //HorizontalLayout tools = new HorizontalLayout(buildFilter(), createReport);
		        
		        tools = new HorizontalLayout(buildFilter());
//		        tools.addComponent(createReport);
		        Button addRecord=new Button("Add Record",new ClickListener() {
                    @Override
                    public void buttonClick(final ClickEvent event) {
                    	UI.getCurrent().getNavigator().navigateTo(DashboardViewType.PRODUCTVIEW.getViewName());            	
                        
                    }
                });
		        tools.addComponent(addRecord);
		        
//		        Button editButton=new Button("Edit Record",new ClickListener() {
//                    @Override
//                    public void buttonClick(final ClickEvent event) {
//                        
//                    	Iterator addIterator=getComponentIterator();
//                    	Object tempObj=null;
//                    	while(addIterator.hasNext()){
//                    		tempObj = addIterator.next();
//                    		System.out.println(tempObj);
//                    		if(tempObj.equals(table)){
//                    			removeComponent(header);
//                    			replaceComponent(table,supplierView);
//                            	requestRepaint();
//                            	setMargin(false);
//                    		}else if(tempObj.equals(supplierView)){
//                    			
//                    			removeComponent(header);
//                    			session.unlock();
//                    			replaceComponent(supplierView,ProductList);
//                    			requestRepaint();
//                            	setMargin(false);
//                    		}
//                    	}                        
//                    }
//                });
		        
//		        tools.addComponent(editButton);
		        tools.setSpacing(true);
		        tools.addStyleName("toolbar");
		        header.addComponent(tools);

		        return header;
		    }

		    private Button buildCreateReport() {
		        final Button createReport = new Button("Create Report");
		        createReport
		                .setDescription("Create a new report from the selected transactions");
		        createReport.addClickListener(new ClickListener() {
		            @Override
		            public void buttonClick(final ClickEvent event) {
//		                createNewReportFromSelection();
		            }
		        });
		        createReport.setEnabled(true);
		        return createReport;
		    }
		    
		    
		    private Component buildFilter() {
		        final TextField filter = new TextField();
		        filter.addTextChangeListener(new TextChangeListener() {
		            @Override
		            public void textChange(final TextChangeEvent event) {
		                Filterable data = (Filterable) table.getContainerDataSource();
		                data.removeAllContainerFilters();
		                data.addContainerFilter(new Filter() {
		                    @Override
		                    public boolean passesFilter(final Object itemId,
		                            final Item item) {

		                        if (event.getText() == null
		                                || event.getText().equals("")) {
		                            return true;
		                        }

		                        return filterByProperty("supplier_product_id", item,
		                                event.getText())
		                                || filterByProperty("product_name", item,
		                                        event.getText())
		                                || filterByProperty("product_price", item,
		                                        event.getText())
//		                                || filterByProperty("product_type", item,
//		                                        event.getText())
		                                || filterByProperty("ci_no", item,
		                                        event.getText())
		                                || filterByProperty("cas_no", item,
		                                        event.getText());

		                    }

		                    @Override
		                    public boolean appliesToProperty(final Object propertyId) {
		                        if (propertyId.equals("supplier_product_id")
		                                || propertyId.equals("product_name")
		                                || propertyId.equals("product_price")
//		                                || propertyId.equals("product_type")
		                                || propertyId.equals("ci_no")
		                                || propertyId.equals("cas_no")) {
		                            return true;
		                        }
		                        return false;
		                    }
		                });
		            }
		        });

		        filter.setInputPrompt("Filter");
		        filter.setIcon(FontAwesome.SEARCH);
		        filter.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
		        filter.addShortcutListener(new ShortcutListener("Clear",
		                KeyCode.ESCAPE, null) {
		            @Override
		            public void handleAction(final Object sender, final Object target) {
		                filter.setValue("");
		                ((Filterable) table.getContainerDataSource())
		                        .removeAllContainerFilters();
		            }
		        });
		        return filter;
		    }

		    @SuppressWarnings("deprecation")
			private Table buildTable() {
		        final Table table = new Table() ;
		        table.setSizeFull();
		        table.addStyleName(ValoTheme.TABLE_BORDERLESS);
		        table.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
		        table.addStyleName(ValoTheme.TABLE_COMPACT);
		        table.setSelectable(true);

		        table.setColumnCollapsingAllowed(true);
		        table.setColumnCollapsible("time", false);
		        table.setColumnCollapsible("price", false);

		        table.setColumnReorderingAllowed(true);

		        BeanItemContainer<SupplierProduct> container = new BeanItemContainer<SupplierProduct>(SupplierProduct.class);
		        Session session=(Session)ZitcoSession.getSession();
		        List<SupplierProduct> list = session.createCriteria(SupplierProduct.class).list();
			    Iterator iterator=list.iterator();
			    
			    SupplierProduct tempProduct;
			    while(iterator.hasNext()){
			    	tempProduct=(SupplierProduct) iterator.next();
			    	container.addBean(tempProduct);
			    }
                session.close();
			    table.setContainerDataSource(container);
		        table.setSortContainerPropertyId("time");
		        table.setSortAscending(false);

		        table.setColumnAlignment("Seats", Align.RIGHT);
		        table.setColumnAlignment("Price", Align.RIGHT);

		        table.setVisibleColumns("supplier_product_id","product_name", "product_price", "ci_no","cas_no");
		        table.setColumnHeaders("Product ID","Product Name","Product Price","CI #","CAS #");
//		        table.setFooterVisible(true);
		        
		        
		        table.addListener(new ItemClickListener() {
					
					@Override
					public void itemClick(ItemClickEvent event) {

						if (event.isDoubleClick()){
							BeanItem<SupplierProduct> item= (BeanItem<SupplierProduct>)event.getItem();
							UI.getCurrent().getSession().setAttribute("productID", item.getBean().getSupplier_product_id());
							UI.getCurrent().getNavigator().navigateTo(DashboardViewType.PRODUCTVIEW.getViewName()); //+"/"+item.getBean().getSupplier_product_id());
						}
						
					}
				});

		        // Allow dragging items to the reports menu
//		        table.setDragMode(TableDragMode.MULTIROW);
//		        table.setMultiSelect(true);

		        table.addActionHandler(new TransactionsActionHandler());

		        table.addValueChangeListener(new ValueChangeListener() {
		          

					@Override
					public void valueChange(ValueChangeEvent event) {
						// TODO Auto-generated method stub
						  if (table.getValue() instanceof Set) {
			                    Set<Object> val = (Set<Object>) table.getValue();
			                    createReport.setEnabled(val.size() > 0);
			                }
					}
		        });
		        table.setImmediate(true);

		        return table;
		    }

		    private boolean defaultColumnsVisible() {
		        boolean result = true;
		        for (String propertyId : DEFAULT_COLLAPSIBLE) {
		            if (table.isColumnCollapsed(propertyId) == Page.getCurrent()
		                    .getBrowserWindowWidth() < 800) {
		                result = false;
		            }
		        }
		        return result;
		    }

		    @Subscribe
		    public void browserResized(final BrowserResizeEvent event) {
		        // Some columns are collapsed when browser window width gets small
		        // enough to make the table fit better.
		        if (defaultColumnsVisible()) {
		            for (String propertyId : DEFAULT_COLLAPSIBLE) {
		                table.setColumnCollapsed(propertyId, Page.getCurrent()
		                        .getBrowserWindowWidth() < 800);
		            }
		        }
		    }
		    
		    

		    private boolean filterByProperty(final String prop, final Item item,
		            final String text) {
		        if (item == null || item.getItemProperty(prop) == null
		                || item.getItemProperty(prop).getValue() == null) {
		            return false;
		        }
		        String val = item.getItemProperty(prop).getValue().toString().trim()
		                .toLowerCase();
		        if (val.contains(text.toLowerCase().trim())) {
		            return true;
		        }
		        return false;
		    }

//		    void createNewReportFromSelection() {
//		        UI.getCurrent().getNavigator()
//		                .navigateTo(DashboardViewType.REPORTS.getViewName());
////		        DashboardEventBus.post(new TransactionReportEvent(
////		                (Collection<Transaction>) table.getValue()));
//		    }

		    @Override
		    public void enter(final ViewChangeEvent event) {
		    }

		    private class TransactionsActionHandler implements Handler {
		        private final Action report = new Action("Create Report");

		        private final Action discard = new Action("Discard");

		        private final Action details = new Action("Movie details");

				@Override
				public Action[] getActions(Object target, Object sender) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public void handleAction(Action action, Object sender, Object target) {
					// TODO Auto-generated method stub
					
				}

//		        @Override
//		        public void handleAction(final Action action, final Object sender,
//		                final Object target) {
//		            if (action == report) {
//		                createNewReportFromSelection();
//		            } else if (action == discard) {
//		                Notification.show("Not implemented in this demo");
//		            } else if (action == details) {
//		                Item item = ((Table) sender).getItem(target);
//		                if (item != null) {
//		                    Long movieId = (Long) item.getItemProperty("movieId")
//		                            .getValue();
//		                    MovieDetailsWindow.open(ZitcotestUI.getDataProvider()
//		                            .getMovie(movieId), null, null);
//		                }
//		            }
//		        }

//		        @Override
//		        public Action[] getActions(final Object target, final Object sender) {
//		            return new Action[] { details, report, discard };
//		        }
		    }

		    private class TempTransactionsContainer extends
		            FilterableListContainer<Transaction> {

		        public TempTransactionsContainer(
		                final Collection<Transaction> collection) {
		            super(collection);
		        }

		        // This is only temporarily overridden until issues with
		        // BeanComparator get resolved.
		        @Override
		        public void sort(final Object[] propertyId, final boolean[] ascending) {
		            final boolean sortAscending = ascending[0];
		            final Object sortContainerPropertyId = propertyId[0];
		            Collections.sort(getBackingList(), new Comparator<Transaction>() {
		                @Override
		                public int compare(final Transaction o1, final Transaction o2) {
		                    int result = 0;
		                    if ("time".equals(sortContainerPropertyId)) {
		                        result = o1.getTime().compareTo(o2.getTime());
		                    } else if ("country".equals(sortContainerPropertyId)) {
		                        result = o1.getCountry().compareTo(o2.getCountry());
		                    } else if ("city".equals(sortContainerPropertyId)) {
		                        result = o1.getCity().compareTo(o2.getCity());
		                    } else if ("theater".equals(sortContainerPropertyId)) {
		                        result = o1.getTheater().compareTo(o2.getTheater());
		                    } else if ("room".equals(sortContainerPropertyId)) {
		                        result = o1.getRoom().compareTo(o2.getRoom());
		                    } else if ("title".equals(sortContainerPropertyId)) {
		                        result = o1.getTitle().compareTo(o2.getTitle());
		                    } else if ("seats".equals(sortContainerPropertyId)) {
		                        result = new Integer(o1.getSeats()).compareTo(o2
		                                .getSeats());
		                    } else if ("price".equals(sortContainerPropertyId)) {
		                        result = new Double(o1.getPrice()).compareTo(o2
		                                .getPrice());
		                    }

		                    if (!sortAscending) {
		                        result *= -1;
		                    }
		                    return result;
		                }
		            });
		        }

		    }

}
