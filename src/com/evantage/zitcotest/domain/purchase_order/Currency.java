package com.evantage.zitcotest.domain.purchase_order;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="currency")
public class Currency implements Serializable {
	
	private Integer currency_id;
	private String currency_name;
	private String currency_sign;
	
	@Id 
	@GeneratedValue
	public Integer getCurrency_id() {
		return currency_id;
	}
	public void setCurrency_id(Integer currency_id) {
		this.currency_id = currency_id;
	}
	public String getCurrency_name() {
		return currency_name;
	}
	public void setCurrency_name(String currency_name) {
		this.currency_name = currency_name;
	}
	public String getCurrency_sign() {
		return currency_sign;
	}
	public void setCurrency_sign(String currency_sign) {
		this.currency_sign = currency_sign;
	}
	
}
