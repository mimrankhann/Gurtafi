package com.evantage.zitcotest.domain.purchase_order_status_updation;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.evantage.zitcotest.domain.purchase_order.PurchaseOrder;

@Entity
@Table(name="purchase_order_upload_file")
public class PurchaseOrderUploadFile implements Serializable{
	
	private Integer po_file_upload_id;
	private String po_file_upload_type;
	private String po_file_upload_number;
	private Date po_file_upload_date;
	private String po_file_upload_url;
//	private Integer po_status_updation_id;
	
	private PurchaseOrderStatusUpdation purchaseOrderStatusUpdation;
	
	@Id
	@GeneratedValue
	public Integer getPo_file_upload_id() {
		return po_file_upload_id;
	}
	public void setPo_file_upload_id(Integer po_file_upload_id) {
		this.po_file_upload_id = po_file_upload_id;
	}
	public String getPo_file_upload_type() {
		return po_file_upload_type;
	}
	public void setPo_file_upload_type(String po_file_upload_type) {
		this.po_file_upload_type = po_file_upload_type;
	}
	public String getPo_file_upload_number() {
		return po_file_upload_number;
	}
	public void setPo_file_upload_number(String po_file_upload_number) {
		this.po_file_upload_number = po_file_upload_number;
	}
	public Date getPo_file_upload_date() {
		return po_file_upload_date;
	}
	public void setPo_file_upload_date(Date po_file_upload_date) {
		this.po_file_upload_date = po_file_upload_date;
	}
	public String getPo_file_upload_url() {
		return po_file_upload_url;
	}
	public void setPo_file_upload_url(String po_file_upload_url) {
		this.po_file_upload_url = po_file_upload_url;
	}
	
//	public Integer getPo_status_updation_id() {
//		return po_status_updation_id;
//	}
//	public void setPo_status_updation_id(Integer po_status_updation_id) {
//		this.po_status_updation_id = po_status_updation_id;
//	}
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "po_status_updation_id")
	public PurchaseOrderStatusUpdation getPurchaseOrderStatusUpdation() {
		return purchaseOrderStatusUpdation;
	}
	public void setPurchaseOrderStatusUpdation(
			PurchaseOrderStatusUpdation purchaseOrderStatusUpdation) {
		this.purchaseOrderStatusUpdation = purchaseOrderStatusUpdation;
	}

}
