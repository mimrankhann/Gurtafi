package com.evantage.zitcotest.view.purchase_order_status_updation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;

import com.evantage.zitcotest.domain.purchase_order.PurchaseOrder;
import com.evantage.zitcotest.session.ZitcoSession;
import com.evantage.zitcotest.view.DashboardViewType;
//import com.evantage.zitcotest.view.purchase_order_status_updation.PurchaseOrderStatusUpdationList.TransactionsActionHandler;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.Page;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class FileUploadWithProgressMonitoring extends VerticalLayout {

    private Label state = new Label();
    private Label result = new Label();
    private Label fileName = new Label();
    private Label textualProgress = new Label();

    private ProgressIndicator pi = new ProgressIndicator();
    private Table table=new Table();
    
    private LineBreakCounter counter = new LineBreakCounter();

    private Upload upload = new Upload();
    
    public void addFileInATable(String FileName){
    	table.setSizeFull();
		table.addStyleName(ValoTheme.TABLE_BORDERLESS);
		table.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
		table.addStyleName(ValoTheme.TABLE_COMPACT);
		table.setSelectable(true);

		table.setColumnCollapsingAllowed(true);
		table.setColumnCollapsible("time", false);
		table.setColumnCollapsible("price", false);

		table.setColumnReorderingAllowed(true);

//		table.setContainerDataSource(container);
		table.setSortContainerPropertyId("time");
		table.setSortAscending(false);

		table.setColumnAlignment("Seats", Align.RIGHT);
		table.setColumnAlignment("Price", Align.RIGHT);

		table.setVisibleColumns("fileName", "po_date","supplier_name","customer_name", "payment_terms","po_total_amount","commission");
		table.setColumnHeaders("PO Number","PO Date","Supplier Name","Customer Name","Payment Terms","PO Total Amount","Commission");

//		table.setFooterVisible(true);

		table.addListener(new ItemClickListener() {

			@Override
			public void itemClick(ItemClickEvent event) {
				
				if (event.isDoubleClick()){
					BeanItem<PurchaseOrder> item= (BeanItem<PurchaseOrder>)event.getItem();
					UI.getCurrent().getSession().setAttribute("poID", item.getBean().getPo_id());
					UI.getCurrent().getNavigator().addView("IFUpdationView",  new PurchaseOrderStatusUpdationView()); //purchaseOrderList);
					UI.getCurrent().getNavigator().navigateTo(DashboardViewType.PURCHASEORDERSTATUSUPDATIONVIEW.getViewName());  //+"/"+item.getBean().getCustomer_id());
				}

			}
		});

	//		table.addActionHandler(new TransactionsActionHandler());

		table.addValueChangeListener(new ValueChangeListener() {


			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				if (table.getValue() instanceof Set) {
					Set<Object> val = (Set<Object>) table.getValue();
//					createReport.setEnabled(val.size() > 0);
				}
			}
		});
		table.setImmediate(true);

    }
    
    
    public FileUploadWithProgressMonitoring() {
        setMargin(true);
        setSpacing(true);

//        addComponent(new Label("Upload a file"));

        // make analyzing start immediatedly when file is selected
        upload.setImmediate(true);
        upload.setButtonCaption("Upload File");
        //zzz
        upload.setReceiver(counter);
        addComponent(upload);

        CheckBox handBrake = new CheckBox("Simulate slow server");
        handBrake.setValue(true);
        counter.setSlow(true);
        handBrake.setDescription("This is to show progress indicator even with rather small files.");
        handBrake.addListener(new Property.ValueChangeListener() {
            private static final long serialVersionUID = -6857112166321059475L;

            public void valueChange(ValueChangeEvent event) {
//            	counter.setSlow(((ClickEvent) event).getButton().isEnabled());
            }
        });
        		
//        		new Button.ClickListener() {
//            public void buttonClick(ClickEvent event) {
//                counter.setSlow(event.getButton().isEnabled()); //booleanValue());
//            }
//        }, null);

        final Button cancelProcessing = new Button("Cancel processing");
        cancelProcessing.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                upload.interruptUpload();
            }
        });
        cancelProcessing.setEnabled(false);
        cancelProcessing.setStyleName("small");
        addComponent(cancelProcessing);

        handBrake.setImmediate(true);

        addComponent(handBrake);

        Panel p = new Panel("Status");
        p.setSizeUndefined();
        FormLayout l = new FormLayout();
        l.setMargin(true);
        p.setContent(l);
        state.setCaption("Current state");
        state.setValue("Idle");
        l.addComponent(state);
        fileName.setCaption("File name");
        l.addComponent(fileName);
        result.setCaption("Line breaks counted");
        l.addComponent(result);
        pi.setCaption("Progress");
        pi.setVisible(false);
        l.addComponent(pi);
        textualProgress.setVisible(false);
        l.addComponent(textualProgress);

        addComponent(p);

        upload.addListener(new Upload.StartedListener() {
            public void uploadStarted(StartedEvent event) {
                // this method gets called immediatedly after upload is
                // started
                pi.setValue(0f);
                pi.setVisible(true);
                pi.setPollingInterval(500); // hit server frequantly to get
                textualProgress.setVisible(true);
                // updates to client
                state.setValue("Uploading");
                fileName.setValue(event.getFilename());

                cancelProcessing.setEnabled(true);
            }
        });

        upload.addListener(new Upload.ProgressListener() {
            public void updateProgress(long readBytes, long contentLength) {
                // this method gets called several times during the update
                pi.setValue(new Float(readBytes / (float) contentLength));
                textualProgress.setValue("Processed " + readBytes
                        + " bytes of " + contentLength);
                result.setValue(counter.getLineBreakCount() + " (counting...)");
            }

        });

        upload.addListener(new Upload.SucceededListener() {
            public void uploadSucceeded(SucceededEvent event) {
                //result.setValue(counter.getLineBreakCount() + " (total)");
            }
        });

        upload.addListener(new Upload.FailedListener() {
            public void uploadFailed(FailedEvent event) {
                result.setValue(counter.getLineBreakCount()
                        + " (counting interrupted at "
                        + Math.round(100 * (Float) pi.getValue()) + "%)");
            }
        });

        upload.addListener(new Upload.FinishedListener() {
            public void uploadFinished(FinishedEvent event) {
                state.setValue("Idle");
                pi.setVisible(false);
                textualProgress.setVisible(false);
                cancelProcessing.setEnabled(false);
            }
        });

    }
    
    
    
    public static class LineBreakCounter implements Receiver {

        private String fileName;
        private String mtype;

        private int counter;
        private int total;
        private boolean sleep;
        private File file;
        
        /**
         * return an OutputStream that simply counts lineends
         */
        public OutputStream receiveUpload(String filename, String MIMEType) {
        	System.out.println("File Name = "+ filename + "mimeType = "+MIMEType);
        	// Create upload stream
        	ByteArrayOutputStream baos;
        	FileOutputStream fos = null; // Stream to write to
            try {
                // Open the file for writing.
                file = new File("D:\\Zitco_File_Uploads\\" + filename);
//                if(file.exists() == false){
//                	file.createNewFile();
//                }
                fos = new FileOutputStream(file);
            } catch (final java.io.FileNotFoundException e) {
                new Notification("Could not open file<br/>",
                                 e.getMessage(),
                                 Notification.Type.ERROR_MESSAGE)
                    .show(Page.getCurrent());
                return null;
            } catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
            return fos; // Return the output stream to write to

            
//            counter = 0;
//            total = 0;
//            fileName = filename;
//            mtype = MIMEType;
//            return new OutputStream() {
//                private static final int searchedByte = '\n';
//
//                @Override
//                public void write(int b) throws IOException {
//                    total++;
//                    if (b == searchedByte) {
//                        counter++;
//                    }
//                    if (sleep && total % 1000 == 0) {
//                        try {
//                            Thread.sleep(100);
//                        } catch (InterruptedException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            };
        }

        public String getFileName() {
            return fileName;
        }

        public String getMimeType() {
            return mtype;
        }

        public int getLineBreakCount() {
            return counter;
        }

        public void setSlow(boolean value) {
            sleep = value;
        }
      
        
    }
    
    

}
