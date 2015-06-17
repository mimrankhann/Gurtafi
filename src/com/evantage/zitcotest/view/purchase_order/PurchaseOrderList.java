package com.evantage.zitcotest.view.purchase_order;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.hibernate.Session;
import org.vaadin.maddon.FilterableListContainer;

import com.evantage.zitcotest.ZitcotestUI;
import com.evantage.zitcotest.session.ZitcoSession;
import com.evantage.zitcotest.util.HibernateUtil;
import com.evantage.zitcotest.view.DashboardViewType;
import com.evantage.zitcotest.domain.Transaction;
import com.evantage.zitcotest.domain.purchase_order.PurchaseOrder;
import com.evantage.zitcotest.domain.purchase_order.SelectedItems;
import com.evantage.zitcotest.domain.supplier.Supplier;
import com.evantage.zitcotest.event.DashboardEvent.BrowserResizeEvent;
import com.evantage.zitcotest.event.DashboardEventBus;
import com.google.common.eventbus.Subscribe;
import com.mysql.jdbc.Connection;
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
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

	@SuppressWarnings({ "serial", "unchecked" })

	public class PurchaseOrderList extends VerticalLayout implements View,Component {
		//zz
		private VaadinSession session=UI.getCurrent().getSession();
		public static final String VIEW="purchaseorderlist";
		public static PurchaseOrderList purchaseOrderList; 
		private final Table table;
		private Button createReport; 
		private static final String[] DEFAULT_COLLAPSIBLE = { "country", "city",
			"theater", "room", "title", "seats" };
		private HorizontalLayout header;
		private HorizontalLayout tools;
		private PurchaseOrder purchaseOrder = new PurchaseOrder();
		

		public PurchaseOrderList() {
			setSizeFull();
			addStyleName("transactions");
			DashboardEventBus.register(this);
			purchaseOrderList=this;
			addComponent(buildToolbar());

			table = buildTable();
			addComponent(table);
			setExpandRatio(table, 1);
		}

		public static PurchaseOrderList getPurchaseOrderListObject(){
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

			Label title = new Label("IF List");
			title.setSizeUndefined();
			title.addStyleName(ValoTheme.LABEL_H1);
			title.addStyleName(ValoTheme.LABEL_NO_MARGIN);
			header.addComponent(title);

			createReport = buildCreateReport();
			//HorizontalLayout tools = new HorizontalLayout(buildFilter(), createReport);

			tools = new HorizontalLayout(buildFilter());
			tools.addComponent(createReport);
			Button addRecord=new Button("Add Record",new ClickListener() {
				@Override
				public void buttonClick(final ClickEvent event) {
					
					UI.getCurrent().getNavigator().addView("IFView", new PurchaseOrderView());
					UI.getCurrent().getNavigator().navigateTo(DashboardViewType.PURCHASEORDERVIEW.getViewName());
					
//					UI.getCurrent().getNavigator().navigateTo(DashboardViewType.PURCHASEORDERVIEW.getViewName());

				}
			});
			tools.addComponent(addRecord);

			tools.setSpacing(true);
			tools.addStyleName("toolbar");
			header.addComponent(tools);

			return header;
		}

		private Button buildCreateReport() {
			final Button createReport = new Button("Create Report");
			createReport.setDescription("Create a new report from the selected transactions");
			final Connection connection = HibernateUtil.getConnection();
			
			createReport.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(final ClickEvent event) {
					PurchaseOrder selectedItem =  (PurchaseOrder)table.getValue();
					if(selectedItem != null){
						final HashMap<String, Object> purchaseOrderReportmap = new HashMap();
						purchaseOrderReportmap.put("po_id", selectedItem.getPo_id());
						purchaseOrderReportmap.put("logo", "D:/Zitco_File_Export/logo.png");
						String purhcaseOrderReportURL = "D:/ZitcoJasperReports/PurchaseOrder.jasper";
						generatePDF(purhcaseOrderReportURL,purchaseOrderReportmap);
						publishReport("D:/ZitcoJasperReports/PurchaseOrder.pdf");
		    		}
					else{
						Notification.show("Please Select IF First....", Notification.TYPE_HUMANIZED_MESSAGE);
					}
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
									|| filterByProperty("customer_name", item,
											event.getText())
									|| filterByProperty("currency_name", item,
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
									|| propertyId.equals("customer_name")
									|| propertyId.equals("currency_name")
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

			table.setContainerDataSource(container);
			table.setSortContainerPropertyId("time");
			table.setSortAscending(false);

			table.setColumnAlignment("Seats", Align.RIGHT);
			table.setColumnAlignment("Price", Align.RIGHT);

			table.setVisibleColumns("po_number", "po_date","supplier_name","customer_name", "payment_terms","currency_name","po_total_amount","commission");
			table.setColumnHeaders("IF Number","IF Date","Supplier Name","Customer Name","Payment Terms","Currency","IF Total Amount","Commission");

//			table.setFooterVisible(true);

			table.addListener(new ItemClickListener() {

				@Override
				public void itemClick(ItemClickEvent event) {
					
					if (event.isDoubleClick()){
						BeanItem<PurchaseOrder> item= (BeanItem<PurchaseOrder>)event.getItem();
						UI.getCurrent().getSession().setAttribute("poID", item.getBean().getPo_id());
						UI.getCurrent().getNavigator().addView("IFView",  new PurchaseOrderView()); //purchaseOrderList);
						UI.getCurrent().getNavigator().navigateTo(DashboardViewType.PURCHASEORDERVIEW.getViewName());  //+"/"+item.getBean().getCustomer_id());
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
		
		public void generatePDF(final String fileName, final HashMap inputValue){

				try
					{
						ServletContext sc= ZitcotestUI.Servlet.getCurrent().getServletContext();
						String reportFile = fileName;  
						String pdfFile = reportFile.substring(0, reportFile.lastIndexOf(".")) +".pdf";

						Connection connection = HibernateUtil.getConnection();

						try {
//							String subReport = "D:/ZitcoJasperReports/Supplier_Wise_SubReport.jasper";

							FileInputStream fin = new FileInputStream(reportFile);
							FileOutputStream fout = new FileOutputStream(pdfFile);
					
					// Fill Report		
					JasperPrint jprint = (JasperPrint) JasperFillManager.fillReport(reportFile, inputValue, connection);
					// Export PDF File
					JasperExportManager.exportReportToPdfFile(jprint, pdfFile);
							
							fin.close();
							fout.close();
							connection.close();
							
						} catch (JRException ex) {
							ex.printStackTrace();
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						} catch (IOException e2) {
							e2.printStackTrace();
						} catch (SQLException e3) {
							e3.printStackTrace();
						}

						File helpFile = new File(pdfFile);
						FileInputStream helpFileInputStream = new FileInputStream(helpFile);
					} catch (FileNotFoundException e)
					{
						e.printStackTrace();
					}
		}

		private void publishReport(final String path){

			StreamSource s = new StreamSource() {

				public java.io.InputStream getStream() {
					try {
						File f = new File(path);
						FileInputStream fis = new FileInputStream(f);
						return fis;
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					return null;
				}
			};

			StreamSource ss = new StreamSource(){
				@Override
				public InputStream getStream() {
					return null;
				}

			};

			StreamResource r = new StreamResource(s, path);
			r.setCacheTime(-1);
			Embedded e = new Embedded();
			e.setSizeFull();
			e.setType(Embedded.TYPE_BROWSER);
			r.setMIMEType("application/pdf");
			e.setSource(r);
			e.setHeight("650px");
			Window plaintReoprtWindow = new Window("Report");
			plaintReoprtWindow.center();
			plaintReoprtWindow.setModal(true);
			plaintReoprtWindow.setHeight("700px");
			plaintReoprtWindow.setWidth("900px");
			plaintReoprtWindow.setContent(e);
			ZitcotestUI.getCurrent().addWindow(plaintReoprtWindow);
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



