package com.evantage.zitcotest.domain.purchase_order;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="purchase_order_item_list")
public class PurchaseOrderItemList implements Serializable{
	
	private Integer purchase_order_item_list_id;
	private String  item_name;
    private Integer  item_quantity;
    private Integer item_price;
//    private Integer po_id;
    
    private PurchaseOrder purchaseOrder;
    
    @Id
	@GeneratedValue
	public Integer getPurchase_order_item_list_id() {
		return purchase_order_item_list_id;
	}


	public void setPurchase_order_item_list_id(Integer purchase_order_item_list_id) {
		this.purchase_order_item_list_id = purchase_order_item_list_id;
	}


	public String getItem_name() {
		return item_name;
	}


	public void setItem_name(String item_name) {
		this.item_name = item_name;
	}


	public Integer getItem_quantity() {
		return item_quantity;
	}


	public void setItem_quantity(Integer item_quantity) {
		this.item_quantity = item_quantity;
	}
	

	public Integer getItem_price() {
		return item_price;
	}

	public void setItem_price(Integer item_price) {
		this.item_price = item_price;
	}


//	public Integer getPo_id() {
//		return po_id;
//	}
//
//
//	public void setPo_id(Integer po_id) {
//		this.po_id = po_id;
//	}


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "po_id")
	public PurchaseOrder getPurchaseOrder() {
		return purchaseOrder;
	}
	public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
		this.purchaseOrder = purchaseOrder;
	}

}

