package com.evantage.zitcotest.domain.purchase_order;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="current_po_id")
public class CurrentPOID {
	    
		private Integer id;
		private Integer current_po_id;
        
		
		@Id
		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		
		public Integer getCurrent_po_id() {
			return current_po_id;
		}

		public void setCurrent_po_id(Integer current_po_id) {
			this.current_po_id = current_po_id;
		}
		
}
