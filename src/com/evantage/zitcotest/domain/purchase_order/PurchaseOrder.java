package com.evantage.zitcotest.domain.purchase_order;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Date;


@Entity
@Table(name="purchase_order")
public class PurchaseOrder {
	
	private Integer po_id;
	private String po_number;
	private Integer po_status_type_id;
	private String po_status_type;
	private String currency_name;
	private Integer currency_id;
	private String supplier_name;
	private Integer supplier_id;
	private String customer_name;
	private Integer customer_id;
	private Date po_date;
	private String payment_terms;
	private Integer po_payment_terms_id;
    private Date payment_due_date;
    private Integer commission;
    private Integer po_total_amount;
    

	private Set<PurchaseOrderItemList> purchaseOrderItemList = new HashSet<PurchaseOrderItemList>(0);
	
	public PurchaseOrder() {
	}
 
	public PurchaseOrder(Integer po_id, String po_number,Integer po_status_type_id, String po_status_type, 
			String currency_name, Integer currency_id,String supplier_name, Date po_date, String payment_terms,
			Integer po_payment_terms_id, Date payment_due_date, Integer commission, Integer po_total_amount) {
		this.po_id=po_id;
		this.po_number=po_number;
		this.po_status_type_id = po_status_type_id;
		this.po_status_type = po_status_type;
		this.currency_name = currency_name;
		this.currency_id = currency_id;
		this.supplier_name=supplier_name;
		this.po_date=po_date;
		this.payment_terms=payment_terms;
		this.po_payment_terms_id = po_payment_terms_id;
		this.payment_due_date=payment_due_date;
		this.commission=commission;
		this.po_total_amount=po_total_amount;
	} 
 
	public PurchaseOrder(Integer po_id, String po_number,Integer po_status_type_id, String po_status_type, 
			String currency_name, Integer currency_id, String supplier_name, Date po_date, String payment_terms,
			Integer po_payment_terms_id, Date payment_due_date, Integer commission, Integer po_total_amount, 
			Set<PurchaseOrderItemList> purchaseOrderItemList) {
		this.po_id=po_id;
		this.po_number=po_number;
		this.po_status_type_id = po_status_type_id;
		this.po_status_type = po_status_type;
		this.currency_name = currency_name;
		this.currency_id = currency_id;
		this.supplier_name=supplier_name;
		this.po_date=po_date;
		this.payment_terms=payment_terms;
		this.po_payment_terms_id = po_payment_terms_id;
		this.payment_due_date=payment_due_date;
		this.commission=commission;
		this.po_total_amount=po_total_amount;
		this.purchaseOrderItemList=purchaseOrderItemList;
	}
	
	@Id
	@GeneratedValue
	public Integer getPo_id() {
		return po_id;
	}
	public void setPo_id(Integer po_id) {
		this.po_id = po_id;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "purchaseOrder")
	public Set<PurchaseOrderItemList> getPurchaseOrderItemList() {
		return purchaseOrderItemList;
	}
	public void setPurchaseOrderItemList(
			Set<PurchaseOrderItemList> purchaseOrderItemList) {
		this.purchaseOrderItemList = purchaseOrderItemList;
	}
	
	public String getPo_number() {
		return po_number;
	}
	public void setPo_number(String po_number) {
		this.po_number = po_number;
	}
	
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
	
	public String getCurrency_name() {
		return currency_name;
	}

	public void setCurrency_name(String currency_name) {
		this.currency_name = currency_name;
	}
	
	public Integer getCurrency_id() {
		return currency_id;
	}

	public void setCurrency_id(Integer currency_id) {
		this.currency_id = currency_id;
	}

	public Date getPo_date() {
		return po_date;
	}
	public void setPo_date(Date po_date) {
		this.po_date = po_date;
	}
 
	public String getSupplier_name() {
		return supplier_name;
	}

	public void setSupplier_name(String supplier_name) {
		this.supplier_name = supplier_name;
	}

	public String getCustomer_name() {
		return customer_name;
	}

	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}

	public String getPayment_terms() {
		return payment_terms;
	}

	public void setPayment_terms(String payment_terms) {
		this.payment_terms = payment_terms;
	}
	
	public Integer getPo_payment_terms_id() {
		return po_payment_terms_id;
	}

	public void setPo_payment_terms_id(Integer po_payment_terms_id) {
		this.po_payment_terms_id = po_payment_terms_id;
	}

	public Date getPayment_due_date() {
		return payment_due_date;
	}

	public void setPayment_due_date(Date payment_due_date) {
		this.payment_due_date = payment_due_date;
	}
    

	public Integer getCommission() {
		return commission;
	}

	public void setCommission(Integer commission) {
		this.commission = commission;
	}

	public Integer getSupplier_id() {
		return supplier_id;
	}

	public void setSupplier_id(Integer supplier_id) {
		this.supplier_id = supplier_id;
	}

	public Integer getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(Integer customer_id) {
		this.customer_id = customer_id;
	}

	public Integer getPo_total_amount() {
		return po_total_amount;
	}

	public void setPo_total_amount(Integer po_total_amount) {
		this.po_total_amount = po_total_amount;
	}

}
