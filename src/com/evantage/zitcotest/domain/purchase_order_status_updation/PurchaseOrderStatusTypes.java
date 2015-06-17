package com.evantage.zitcotest.domain.purchase_order_status_updation;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="purchase_order_status_types")
public class PurchaseOrderStatusTypes implements Serializable{

	private Integer po_status_type_id;
	private String po_status_type;
	
	@Id
	@GeneratedValue
	public Integer getPo_status_type_id() {
		return po_status_type_id;
	}
	public void setPo_status_type_id(Integer po_status_type_id) {
		this.po_status_type_id = po_status_type_id;
	}
	public String getPo_status_type() {
		return po_status_type;
	}
	public void setPo_status_type(String po_status_type) {
		this.po_status_type = po_status_type;
	}
	
	
}
