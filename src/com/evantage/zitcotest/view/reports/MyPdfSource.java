package com.evantage.zitcotest.view.reports;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import com.vaadin.server.StreamResource.StreamSource;

public class MyPdfSource implements StreamSource{
    private String fileName;
    private MyPdfSource streamSource;
    private ByteArrayOutputStream bos; 
    
	public MyPdfSource(String fileName){
//		new MyPdfSource(fileName); 
		bos = new ByteArrayOutputStream();
	}
	
	@Override
	public InputStream getStream() {
            return new ByteArrayInputStream(bos.toByteArray());
    	}
}
