package com.evantage.zitcotest.domain.purchase_order_status_updation;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.evantage.zitcotest.domain.purchase_order.PurchaseOrderItemList;

@Entity
@Table(name="purchase_order_status_updation")
public class PurchaseOrderStatusUpdation implements Serializable{

	private Integer po_status_updation_id;
	private String po_status;
	private Integer po_status_id;
	private String shipment_timeline;
	private Date EDD;
	private Date EDA;
	private Integer po_id;
	
private Set<PurchaseOrderUploadFile> purchaseOrderStatusUpdationItemList = new HashSet<PurchaseOrderUploadFile>(0);
	
	public PurchaseOrderStatusUpdation() {
	}
 
	public PurchaseOrderStatusUpdation(Integer po_status_updation_id, String po_status, Integer po_status_id ,String shipment_timeline,
			Date EDD, Date EDA, Integer po_id) {
		this.po_status_updation_id = po_status_updation_id;
		this.po_status = po_status;
		this.po_status_id = po_status_id;
		this.shipment_timeline = shipment_timeline;
		this.EDD = EDD;
		this.EDA = EDA;
		this.po_id = po_id;
	} 
 
	public PurchaseOrderStatusUpdation(Integer po_status_updation_id, String po_status, Integer po_status_id ,String shipment_timeline,
			Date EDD, Date EDA, Integer po_id, Set<PurchaseOrderUploadFile> purchaseOrderStatusUpdationItemList) {
		this.po_status_updation_id = po_status_updation_id;
		this.po_status = po_status;
		this.po_status_id = po_status_id;
		this.shipment_timeline = shipment_timeline;
		this.EDD = EDD;
		this.EDA = EDA;
		this.po_id = po_id;
		this.purchaseOrderStatusUpdationItemList = purchaseOrderStatusUpdationItemList;
	}
	
	
	@Id
	@GeneratedValue
	public Integer getPo_status_updation_id() {
		return po_status_updation_id;
	}
	public void setPo_status_updation_id(Integer po_status_updation_id) {
		this.po_status_updation_id = po_status_updation_id;
	}
	public String getPo_status() {
		return po_status;
	}
	public void setPo_status(String po_status) {
		this.po_status = po_status;
	}
	
	public Integer getPo_status_id() {
		return po_status_id;
	}

	public void setPo_status_id(Integer po_status_id) {
		this.po_status_id = po_status_id;
	}

	public String getShipment_timeline() {
		return shipment_timeline;
	}
	public void setShipment_timeline(String shipment_timeline) {
		this.shipment_timeline = shipment_timeline;
	}
	
	public Date getEDD() {
		return EDD;
	}

	public void setEDD(Date eDD) {
		EDD = eDD;
	}

	public Date getEDA() {
		return EDA;
	}

	public void setEDA(Date eDA) {
		EDA = eDA;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "purchaseOrderStatusUpdation")
	public Set<PurchaseOrderUploadFile> getPurchaseOrderStatusUpdationItemList() {
		return purchaseOrderStatusUpdationItemList;
	}

	public void setPurchaseOrderStatusUpdationItemList(
			Set<PurchaseOrderUploadFile> purchaseOrderStatusUpdationItemList) {
		this.purchaseOrderStatusUpdationItemList = purchaseOrderStatusUpdationItemList;
	}

	public Integer getPo_id() {
		return po_id;
	}
	public void setPo_id(Integer po_id) {
		this.po_id = po_id;
	}
	
	
}
