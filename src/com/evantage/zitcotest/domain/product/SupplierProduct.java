package com.evantage.zitcotest.domain.product;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.evantage.zitcotest.domain.supplier.Supplier;

@Entity
@Table(name="supplier_product")
public class SupplierProduct implements Serializable{
	
	private Integer supplier_product_id;
	private String product_name;
	private Integer product_price;
//	private String product_type;
	private String ci_no;
	private String cas_no;
	private Supplier supplier;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "supplier_id")
	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}
	

	@Id 
	@GeneratedValue
	public Integer getSupplier_product_id() {
		return supplier_product_id;
	}

	public void setSupplier_product_id(Integer supplier_product_id) {
		this.supplier_product_id = supplier_product_id;
	}

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}
	
	public Integer getProduct_price() {
		return product_price;
	}

	public void setProduct_price(Integer product_price) {
		this.product_price = product_price;
	}

//	public String getProduct_type() {
//		return product_type;
//	}
//	public void setProduct_type(String product_type) {
//		this.product_type = product_type;
//	}
	
	public String getCi_no() {
		return ci_no;
	}
	
	public void setCi_no(String ci_no) {
		this.ci_no = ci_no;
	}
	public String getCas_no() {
		return cas_no;
	}
	public void setCas_no(String cas_no) {
		this.cas_no = cas_no;
	}
	
}
