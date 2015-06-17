package com.evantage.zitcotest.view.purchase_order;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
//import com.vaadin.terminal.FileResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

public class PurchaseOrderUploader implements Receiver, SucceededListener { // Show uploaded file in this placeholder

	private static final long serialVersionUID = 1L;

//	final Embedded image = new Embedded("Uploaded Image");
//          image.setVisible(false);

// Implement both receiver that saves upload in a file and
// listener for successful upload

	
    private File file;
    private String fileName;
    private String mimeType;
    
    public OutputStream receiveUpload(String fileName,
                                      String mimeType) {
        
    	// Create upload stream
    	ByteArrayOutputStream baos;
    	FileOutputStream fos = null; // Stream to write to
        try {
            // Open the file for writing.
            file = new File("D:\\Zitco_File_Uploads\\" + fileName);
//            if(file.exists() == false){
//            	file.createNewFile();
//            }
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
    }

    public void uploadSucceeded(SucceededEvent event) {
        // Show the uploaded file in the image viewer
//        image.setVisible(true);
//        image.setSource(new FileResource(file));
//        System.out.println("Inside upload Succeeded method()))))))");
    	this.fileName = event.getFilename();
    	this.mimeType =event.getMIMEType();
    	Notification.show("File Uploaded Successfully",Notification.TYPE_HUMANIZED_MESSAGE);
    }
   
    public Component getFileUploaderComponent(){

    	Upload upload = new Upload("",this);
		upload.setButtonCaption("Upload");
		upload.addSucceededListener(this);
		
		Panel panel = new Panel();
		Layout panelContent = new VerticalLayout();
		panelContent.addComponent(upload);
//		panelContent.addComponent(image);
		panel.setContent(panelContent);
		return panel;
		
    }
    
    public String getFileName() {
        return fileName;
    }

    public String getMimeType() {
        return mimeType;
    }
    

}
