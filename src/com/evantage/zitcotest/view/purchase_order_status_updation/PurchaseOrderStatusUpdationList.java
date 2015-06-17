package com.evantage.zitcotest.view.purchase_order_status_updation;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.vaadin.maddon.FilterableListContainer;

import com.evantage.zitcotest.session.ZitcoSession;
import com.evantage.zitcotest.view.DashboardViewType;
import com.evantage.zitcotest.domain.Transaction;
import com.evantage.zitcotest.domain.purchase_order.PurchaseOrder;
import com.evantage.zitcotest.domain.purchase_order_status_updation.PurchaseOrderStatusTypes;
import com.evantage.zitcotest.domain.purchase_order_status_updation.PurchaseOrderStatusUpdation;
import com.evantage.zitcotest.domain.purchase_order_status_updation.PurchaseOrderUploadFile;
import com.evantage.zitcotest.event.DashboardEvent.BrowserResizeEvent;
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
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings({ "serial", "unchecked" })

public class PurchaseOrderStatusUpdationList extends VerticalLayout implements View,Component {
	//zz
	private VaadinSession session=UI.getCurrent().getSession();
	public static final String VIEW="purchaseorderlist";
	public static PurchaseOrderStatusUpdationList purchaseOrderList; 
	private final Table table ;
	private Button createReport; 
	private static final String[] DEFAULT_COLLAPSIBLE = { "country", "city",
		"theater", "room", "title", "seats" };
	private HorizontalLayout header;
	private HorizontalLayout tools;
	//		private PurchaseOrder purchaseOrder = new PurchaseOrder();
	private PurchaseOrder clickedPurchaseOrder = null;
	private String currentPOStatus ="";

	public PurchaseOrderStatusUpdationList() {
		setSizeFull();
		addStyleName("transactions");
		DashboardEventBus.register(this);
		purchaseOrderList=this;
		addComponent(buildToolbar());

		table = buildTable();
		addComponent(table);
		setExpandRatio(table, 1);
	}

	public static PurchaseOrderStatusUpdationList getPurchaseOrderListObject(){
		return purchaseOrderList;
	}

	@Override
	public void detach() {
		super.detach();
	}

	private Component buildToolbar() {
		header= new HorizontalLayout();
		header.addStyleName("viewheader");
		header.setSpacing(true);
		Responsive.makeResponsive(header);

		Label title = new Label("IF List For Status Updation");
		title.setSizeUndefined();
		title.addStyleName(ValoTheme.LABEL_H1);
		title.addStyleName(ValoTheme.LABEL_NO_MARGIN);
		header.addComponent(title);

		createReport = buildCreateReport();
		//HorizontalLayout tools = new HorizontalLayout(buildFilter(), createReport);

		tools = new HorizontalLayout(buildFilter());
		//			tools.addComponent(createReport);

		Button updateIFStatus=new Button("Update IF Status",new ClickListener() {
			//				clickedPurchaseOrder =
			@Override
			public void buttonClick(final ClickEvent event) {

				try{
					clickedPurchaseOrder = (PurchaseOrder) table.getValue();
					currentPOStatus = clickedPurchaseOrder.getPo_status_type();
				}
				catch(Exception e){
					currentPOStatus = "";
					clickedPurchaseOrder = null;
					Notification.show("Please Select IF First..",Notification.TYPE_HUMANIZED_MESSAGE);
				}

				if(currentPOStatus != null && currentPOStatus != ""){

					if(currentPOStatus.equalsIgnoreCase("INTRANSIT") || 
							currentPOStatus.equalsIgnoreCase("PENDING PAYMENT")){

						final Window addWindow = new Window("Change IF Status");
						VerticalLayout mainItemLayout=new VerticalLayout();
						HorizontalLayout buttonLayout = new HorizontalLayout();
						addWindow.setModal(false);
						Panel itemPanel = new Panel();
						addWindow.setContent(itemPanel);
						addWindow.setWidth("400px");
						addWindow.setHeight("400px");

						final FormLayout windowForm = new FormLayout();
						windowForm.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);

						final ComboBox poStatus = new ComboBox();
						poStatus.setCaption("IF Status");

						Session purchaseOrderPaymentTermsSession= ZitcoSession.getSession();
						BeanItemContainer<PurchaseOrderStatusTypes> purchaseOrderPaymentTermsContainer = 
								new BeanItemContainer<PurchaseOrderStatusTypes>(PurchaseOrderStatusTypes.class);

						List<PurchaseOrderStatusTypes> purchaseOrderPaymentTermsList = purchaseOrderPaymentTermsSession.createCriteria(PurchaseOrderStatusTypes.class).list();
						poStatus.setContainerDataSource( purchaseOrderPaymentTermsContainer);

						poStatus.addItems(purchaseOrderPaymentTermsList);
						poStatus.setItemCaptionPropertyId("po_status_type");
						poStatus.setWidth(250, UNITS_PIXELS);
						poStatus.setImmediate(true);
						windowForm.addComponent(poStatus);
						purchaseOrderPaymentTermsSession.close();

						Button windowSaveButton=new Button("Update",new ClickListener(){
							@Override
							public void buttonClick(ClickEvent event) {
								PurchaseOrderStatusTypes poStatusType = (PurchaseOrderStatusTypes) poStatus.getValue();

								if(poStatusType.getPo_status_type().equalsIgnoreCase("INTRANSIT") || 
										poStatusType.getPo_status_type().equalsIgnoreCase("PENDING PAYMENT") || 
										poStatusType.getPo_status_type().equalsIgnoreCase("CLOSED")){

									Session session=ZitcoSession.getSession();
									List<PurchaseOrder> selectedPurchaseOrderList = session.createCriteria(PurchaseOrder.class).list();
									Iterator selectedPurchaseOrderIterator = selectedPurchaseOrderList.iterator();
									PurchaseOrder selectedPurchaseOrder;

									while(selectedPurchaseOrderIterator.hasNext()){

										selectedPurchaseOrder = (PurchaseOrder) selectedPurchaseOrderIterator.next();

										if(selectedPurchaseOrder.getPo_id() == clickedPurchaseOrder.getPo_id()){
											selectedPurchaseOrder.setPo_id(clickedPurchaseOrder.getPo_id());
											selectedPurchaseOrder.setPo_status_type(poStatusType.getPo_status_type());
											selectedPurchaseOrder.setPo_status_type_id(poStatusType.getPo_status_type_id());
											session.update(selectedPurchaseOrder);
											System.out.println("Update Status Type in PurchaseOrder also...");
										}
									}
									session.getTransaction().commit();
									session.close();
									//zzzzzzzz

									Session poStatusUpdationsession=ZitcoSession.getSession();
									PurchaseOrderStatusUpdation purchaseOrderStatusUpdationObject = new PurchaseOrderStatusUpdation();

									List<PurchaseOrderStatusUpdation> poStatusUpdationList = poStatusUpdationsession.createCriteria(PurchaseOrderStatusUpdation.class).list();
									Iterator poStatusUpdationIterator = poStatusUpdationList.iterator();

									PurchaseOrderStatusUpdation tempPOStatusUpdationObj;

									while(poStatusUpdationIterator.hasNext()){
										tempPOStatusUpdationObj = (PurchaseOrderStatusUpdation) poStatusUpdationIterator.next();

										if(clickedPurchaseOrder.getPo_id() == tempPOStatusUpdationObj.getPo_id()){
											tempPOStatusUpdationObj.setPo_status_updation_id(tempPOStatusUpdationObj.getPo_status_updation_id());
											tempPOStatusUpdationObj.setPo_status(poStatusType.getPo_status_type());
											tempPOStatusUpdationObj.setPo_status_id(poStatusType.getPo_status_type_id());
											poStatusUpdationsession.update(tempPOStatusUpdationObj);
											System.out.println("Update Status Type in PurchaseOrderStatusUpdation also...");
										}

									}
									poStatusUpdationsession.getTransaction().commit();
									poStatusUpdationsession.close();
									UI.getCurrent().removeWindow(addWindow);
									UI.getCurrent().getNavigator().addView("IFUpdationList", new PurchaseOrderStatusUpdationList());
									UI.getCurrent().getNavigator().navigateTo(DashboardViewType.PURCHASEORDERSTATUSUPDATIONLIST.getViewName());
									Notification.show("IF Status Changed Successfully....",Notification.TYPE_HUMANIZED_MESSAGE);

								}
								else{
									Notification.show("You Are Not Allowed to Select Initial OR Closed Status..",Notification.TYPE_HUMANIZED_MESSAGE);
								}
							}
						});

						Button windowCancelButton=new Button("Cancel",new ClickListener(){
							@Override
							public void buttonClick(ClickEvent event) {
								UI.getCurrent().removeWindow(addWindow);
							}
						});

						buttonLayout.addComponent(windowSaveButton);
						buttonLayout.addComponent(windowCancelButton);
						mainItemLayout.addComponent(windowForm);
						mainItemLayout.addComponent(buttonLayout);
						mainItemLayout.setComponentAlignment(buttonLayout, Alignment.BOTTOM_RIGHT);
						//							windowForm.addComponent(mainItemLayout);
						//							addWindow.setContent(buttonLayout);
						addWindow.setContent(mainItemLayout);
						UI.getCurrent().addWindow(addWindow);
					}
					else{
						Notification.show("You Are Not Allowed to Change Status Initial OR Closed IF....",Notification.TYPE_HUMANIZED_MESSAGE);
					}
				}
			}
		});

		tools.addComponent(updateIFStatus);
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
				//			                createNewReportFromSelection();
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

						return filterByProperty("po_number", item,
								event.getText())
								|| filterByProperty("supplier_name", item,
										event.getText())
										|| filterByProperty("po_status_type", item,
												event.getText())		
												|| filterByProperty("customer_name", item,
														event.getText())
														|| filterByProperty("po_date", item,
																event.getText())
																|| filterByProperty("payment_terms", item,
																		event.getText())
																		|| filterByProperty("po_total_amount", item,
																				event.getText());

					}

					@Override
					public boolean appliesToProperty(final Object propertyId) {
						if (propertyId.equals("po_number")
								|| propertyId.equals("supplier_name")
								|| propertyId.equals("po_status_type")
								|| propertyId.equals("customer_name")
								|| propertyId.equals("po_date")
								|| propertyId.equals("payment_terms")
								|| propertyId.equals("po_total_amount")) {
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

		BeanItemContainer<PurchaseOrder> container = new BeanItemContainer<PurchaseOrder>(PurchaseOrder.class);
		Session session=(Session)ZitcoSession.getSession();
		List<PurchaseOrder> list = session.createCriteria(PurchaseOrder.class).list();
		Iterator iterator=list.iterator();

		PurchaseOrder tempSupplier;
		while(iterator.hasNext()){
			tempSupplier=(PurchaseOrder) iterator.next();
			container.addBean(tempSupplier);
		}
		session.close();
		table.setContainerDataSource(container);
		table.setSortContainerPropertyId("time");
		table.setSortAscending(false);

		table.setColumnAlignment("Seats", Align.RIGHT);
		table.setColumnAlignment("Price", Align.RIGHT);

		table.setVisibleColumns("po_number", "po_date","po_status_type","supplier_name","customer_name", "payment_terms","currency_name","po_total_amount","commission");
		table.setColumnHeaders("IF Number","IF Date","IF Status","Supplier Name","Customer Name","Payment Terms","Currency","IF Total Amount","Commission");

		table.setCellStyleGenerator(new Table.CellStyleGenerator() {

			public String getStyle(Object itemId, Object propertyId) {
				return null;
			}

			@Override
			public String getStyle(Table source, Object itemId,
					Object propertyId) {
				// TODO Auto-generated method stub
				if (propertyId == null) {
					// Styling for row
					Item item = table.getItem(itemId);
					String firstName = (String) item.getItemProperty("po_status_type").getValue();

					if (firstName.equalsIgnoreCase("INITIAL STAGE")) {
						return "highlight-green";
					} else if (firstName.equalsIgnoreCase("INTRANSIT")) {
						return "highlight-red";
					} else if (firstName.equalsIgnoreCase("PENDING PAYMENT")) {
						return "highlight-purple";
					}
					else if (firstName.equalsIgnoreCase("CLOSED")) {
						return "highlight-yellow";
					} 
					else {
						return null;
					}
				} else {

					return null;
				}
			}
		});

		table.addListener(new ItemClickListener() {

			@Override
			public void itemClick(ItemClickEvent event) {

				if (event.isDoubleClick()){
					BeanItem<PurchaseOrder> item= (BeanItem<PurchaseOrder>)event.getItem();
					UI.getCurrent().getSession().setAttribute("poID", item.getBean().getPo_id());
					UI.getCurrent().getSession().setAttribute("poNumber",item.getBean().getPo_number());
					UI.getCurrent().getSession().setAttribute("poStatusType",item.getBean().getPo_status_type());
					UI.getCurrent().getNavigator().addView("IFUpdationView",  new PurchaseOrderStatusUpdationView()); 
					UI.getCurrent().getNavigator().navigateTo(DashboardViewType.PURCHASEORDERSTATUSUPDATIONVIEW.getViewName()); 
				}

			}
		});

		// Allow dragging items to the reports menu
		//			        table.setDragMode(TableDragMode.MULTIROW);
		//			        table.setMultiSelect(true);

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

	//			    void createNewReportFromSelection() {
	//			        UI.getCurrent().getNavigator()
	//			                .navigateTo(DashboardViewType.REPORTS.getViewName());
	////			        DashboardEventBus.post(new TransactionReportEvent(
	////			                (Collection<Transaction>) table.getValue()));
	//			    }

	@Override
	public void enter(final ViewChangeEvent event) {
		UI.getCurrent().getNavigator().addView("IFUpdationList", new PurchaseOrderStatusUpdationList());
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

		//			        @Override
		//			        public void handleAction(final Action action, final Object sender,
		//			                final Object target) {
		//			            if (action == report) {
		//			                createNewReportFromSelection();
		//			            } else if (action == discard) {
		//			                Notification.show("Not implemented in this demo");
		//			            } else if (action == details) {
		//			                Item item = ((Table) sender).getItem(target);
		//			                if (item != null) {
		//			                    Long movieId = (Long) item.getItemProperty("movieId")
		//			                            .getValue();
		//			                    MovieDetailsWindow.open(ZitcotestUI.getDataProvider()
		//			                            .getMovie(movieId), null, null);
		//			                }
		//			            }
		//			        }

		//			        @Override
		//			        public Action[] getActions(final Object target, final Object sender) {
		//			            return new Action[] { details, report, discard };
		//			        }
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




