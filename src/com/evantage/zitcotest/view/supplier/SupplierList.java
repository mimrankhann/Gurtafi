package com.evantage.zitcotest.view.supplier;

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
import com.evantage.zitcotest.domain.customer.Customer;
import com.evantage.zitcotest.domain.Transaction;
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

	public class SupplierList extends VerticalLayout implements View,Component {
		   
		private VaadinSession session=UI.getCurrent().getSession();
		    public static final String VIEW="supplierlist";
			public SupplierList supplierList;
		    private final Table table;
		    private Button createReport; 
		    private static final String[] DEFAULT_COLLAPSIBLE = { "country", "city",
		            "theater", "room", "title", "seats" };
		    private HorizontalLayout header;
		    private HorizontalLayout tools;
		    		    
		    public SupplierList() {
		        setSizeFull();
		        addStyleName("transactions");
		        DashboardEventBus.register(this);
				supplierList=this;
		        addComponent(buildToolbar());

		        table = buildTable();
		        addComponent(table);
		        setExpandRatio(table, 1);
		    }
		    
		    public SupplierList getSupplierListObject(){
		    	return supplierList;
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

		        Label title = new Label("Supplier List");
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
                    	// Page.getCurrent().setLocation("/zitcoTest/supplierView");
                    	
                    	UI.getCurrent().getNavigator().navigateTo(DashboardViewType.SUPPLIERVIEW.getViewName());
                    	
                    	// DashboardEventBus.post(new SupplierView());
                    	
                    	
                    	//removeComponent(table);
                    	//removeComponent(buildToolbar());
//                    	Iterator addIterator=getComponentIterator();
//                    	Object tempObj=null;
//                    	while(addIterator.hasNext()){
//                    		tempObj = addIterator.next();
//                    		if(tempObj.equals(table)){
//                    			removeComponent(header);
//                    			replaceComponent(table,supplierView);
//                            	requestRepaint();
//                            	setMargin(false);
//                    		}
//                    	}
                    	
                        
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
//                    			replaceComponent(supplierView,supplierList);
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

		                        return filterByProperty("supplier_id", item,
		                                event.getText())
		                                || filterByProperty("supplier_name", item,
		                                        event.getText())
		                                || filterByProperty("supplier_short_name", item,
		                                        event.getText())
		                                || filterByProperty("supplier_address", item,
		                                        event.getText())
		                                || filterByProperty("supplier_phone", item,
		                                        event.getText())
		                                || filterByProperty("supplier_email", item,
		                                        event.getText())
		                                || filterByProperty("supplier_NTN", item,
		                                        event.getText());

		                    }

		                    @Override
		                    public boolean appliesToProperty(final Object propertyId) {
		                        if (propertyId.equals("supplier_id")
		                                || propertyId.equals("supplier_name")
		                                || propertyId.equals("supplier_short_name")
		                                || propertyId.equals("supplier_address")
		                                || propertyId.equals("supplier_phone")
		                                || propertyId.equals("supplier_email")
		                                || propertyId.equals("supplier_NTN")) {
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

		        BeanItemContainer<Supplier> container = new BeanItemContainer<Supplier>(Supplier.class);
		        Session session=(Session)ZitcoSession.getSession();
		        List<Supplier> list = session.createCriteria(Supplier.class).list();
			    Iterator iterator=list.iterator();
			    
			    Supplier tempSupplier;
			    while(iterator.hasNext()){
			    	tempSupplier=(Supplier) iterator.next();
			    	container.addBean(tempSupplier);
			    }
      
			    table.setContainerDataSource(container);
		        table.setSortContainerPropertyId("time");
		        table.setSortAscending(false);

		        table.setColumnAlignment("Seats", Align.RIGHT);
		        table.setColumnAlignment("Price", Align.RIGHT);

		        table.setVisibleColumns("supplier_name", "supplier_short_name", "supplier_address","supplier_phone","supplier_email","supplier_NTN");
		        table.setColumnHeaders("Name","Short Name","Address","Phone","Email","NTN");
//		        table.setFooterVisible(true);
		        session.close();
		        
		        table.addListener(new ItemClickListener() {
					
					@Override
					public void itemClick(ItemClickEvent event) {
						//System.out.println(event.getItemId());
						if (event.isDoubleClick()){
							BeanItem<Supplier> item= (BeanItem<Supplier>)event.getItem();
							UI.getCurrent().getSession().setAttribute("supplierID", item.getBean().getSupplier_id());
							UI.getCurrent().getNavigator().navigateTo(DashboardViewType.SUPPLIERVIEW.getViewName());  //+"/"+item.getBean().getSupplier_id());
							
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
