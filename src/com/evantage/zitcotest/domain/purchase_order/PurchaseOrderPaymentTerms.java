package com.evantage.zitcotest.domain.purchase_order;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="po_payment_terms")
public class PurchaseOrderPaymentTerms {
	
	private Integer po_payment_terms_id;
	private String po_payment_terms;
	
	
	@Id 
	@GeneratedValue
	public Integer getPo_payment_terms_id() {
		return po_payment_terms_id;
	}
	public void setPo_payment_terms_id(Integer po_payment_terms_id) {
		this.po_payment_terms_id = po_payment_terms_id;
	}
	public String getPo_payment_terms() {
		return po_payment_terms;
	}
	public void setPo_payment_terms(String po_payment_terms) {
		this.po_payment_terms = po_payment_terms;
	}
	
}