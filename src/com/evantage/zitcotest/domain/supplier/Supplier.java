package com.evantage.zitcotest.domain.supplier;

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

import com.evantage.zitcotest.domain.product.SupplierProduct;

@Entity
@Table(name="supplier")
public class Supplier implements Serializable {
	
	private Integer supplier_id;
	private String supplier_name;
	private String supplier_short_name;
    private String supplier_address;
    private String supplier_phone;
    private String supplier_email;
    private String supplier_NTN;
    
    private Set<SupplierProduct> supplierProduct = new HashSet<SupplierProduct>(0);
	
	public Supplier() {
	}
 
	public Supplier(Integer supplier_id,String supplier_name,String supplier_short_name,String supplier_address,String supplier_phone,
			String supplier_email, String supplier_NTN){
		this.supplier_id = supplier_id;
		this.supplier_name = supplier_name;
		this.supplier_short_name = supplier_short_name;
		this.supplier_address = supplier_address;
		this.supplier_phone = supplier_phone;
		this.supplier_email = supplier_email;
		this.supplier_NTN = supplier_NTN;
	} 
 
	public Supplier(Integer supplier_id,String supplier_name,String supplier_short_name,String supplier_address,String supplier_phone,
			Set<SupplierProduct> supplierProduct) {
		this.supplier_id = supplier_id;
		this.supplier_name = supplier_name;
		this.supplier_short_name = supplier_short_name;
		this.supplier_address = supplier_address;
		this.supplier_phone = supplier_phone;
		this.supplier_email = supplier_email;
		this.supplier_NTN = supplier_NTN;
		this.supplierProduct = supplierProduct;
	}
	
	@OneToMany(fetch=FetchType.EAGER, mappedBy = "supplier")
	public Set<SupplierProduct> getSupplierProduct() {
		return supplierProduct;
	}

	public void setSupplierProduct(Set<SupplierProduct> supplierProduct) {
		this.supplierProduct = supplierProduct;
	}
	
	@Id
	@GeneratedValue
	public Integer getSupplier_id() {
		return supplier_id;
	}

	public void setSupplier_id(Integer supplier_id) {
		this.supplier_id = supplier_id;
	}
	public String getSupplier_name() {
		return supplier_name;
	}
	public void setSupplier_name(String supplier_name) {
		this.supplier_name = supplier_name;
	}
	
	public String getSupplier_short_name() {
		return supplier_short_name;
	}

	public void setSupplier_short_name(String supplier_short_name) {
		this.supplier_short_name = supplier_short_name;
	}

	public String getSupplier_address() {
		return supplier_address;
	}
	public void setSupplier_address(String supplier_address) {
		this.supplier_address = supplier_address;
	}
	public String getSupplier_phone() {
		return supplier_phone;
	}
	public void setSupplier_phone(String supplier_phone) {
		this.supplier_phone = supplier_phone;
	}
	public String getSupplier_email() {
		return supplier_email;
	}
	public void setSupplier_email(String supplier_email) {
		this.supplier_email = supplier_email;
	}
	public String getSupplier_NTN() {
		return supplier_NTN;
	}
	public void setSupplier_NTN(String supplier_NTN) {
		this.supplier_NTN = supplier_NTN;
	}

}
