package com.evantage.zitcotest.domain.purchase_order;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="selected_items")
public class SelectedItems {

	private Integer id;
	private Integer po_id;
	private String supplier_id;
	private String selected_item;
	private Integer quantity;
	private Integer price;
	
		
	@Id 
	@GeneratedValue
	public Integer getId() {
		return id;
	}
			
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getPo_id() {
		return po_id;
	}
	public void setPo_id(Integer po_id) {
		this.po_id = po_id;
	}
	
	public String getSupplier_id() {
		return supplier_id;
	}

	public void setSupplier_id(String supplier_id) {
		this.supplier_id = supplier_id;
	}

	public String getSelected_item() {
		return selected_item;
	}
	public void setSelected_item(String selected_item) {
		this.selected_item = selected_item;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}
	
	
	
}

