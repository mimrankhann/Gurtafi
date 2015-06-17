package com.evantage.zitcotest.domain.configure;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "configure")
public class Configure implements Serializable{
	
	private Integer	configure_ID;
		
	private String	dbDriver;
	private String	dbUrl;
	private String	dbUserName;
	private String	dbPassword;
	private String  db_dialect;
	
	@Id
	@GeneratedValue
	public Integer getConfigure_ID() {
		return configure_ID;
	}
	public void setConfigure_ID(Integer configure_ID) {
		this.configure_ID = configure_ID;
	}
	public String getDbDriver() {
		return dbDriver;
	}
	public void setDbDriver(String dbDriver) {
		this.dbDriver = dbDriver;
	}
	public String getDbUrl() {
		return dbUrl;
	}
	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}
	public String getDbUserName() {
		return dbUserName;
	}
	public void setDbUserName(String dbUserName) {
		this.dbUserName = dbUserName;
	}
	public String getDbPassword() {
		return dbPassword;
	}
	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}
	public String getDb_dialect() {
		return db_dialect;
	}
	public void setDb_dialect(String db_dialect) {
		this.db_dialect = db_dialect;
	}
	
}
