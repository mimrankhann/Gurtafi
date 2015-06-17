package com.evantage.zitcotest.domain.purchase_order_status_updation;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="file_upload_types")
public class FileUploadTypes implements Serializable{
	
	private Integer file_upload_type_id;
	private String file_upload_type;
	
	@Id
	@GeneratedValue
	public Integer getFile_upload_type_id() {
		return file_upload_type_id;
	}
	public void setFile_upload_type_id(Integer file_upload_type_id) {
		this.file_upload_type_id = file_upload_type_id;
	}
	public String getFile_upload_type() {
		return file_upload_type;
	}
	public void setFile_upload_type(String file_upload_type) {
		this.file_upload_type = file_upload_type;
	}
	
	

}
