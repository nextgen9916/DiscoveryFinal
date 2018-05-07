package com.example.demo.controller;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name= "device_info")
public class Discover {
	
		
	@Id
	//@Column(name="ip")
	private String ip;
	
	//@Column(name="snmp")
	private String snmp;
	
	//@Column(name="netconf")
	private String netconf;
	
	 public Discover() {
	    }
	
	public String getSnmp() {
		return snmp;
	}
	public void setSnmp(String snmp) {
		this.snmp = snmp;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String string) {
		this.ip = string;
	}
	
	public String getNetconf() {
		return netconf;
	}
	public void setNetconf(String netconf) {
		this.netconf = netconf;
	}
		
	/*@Override
	public String toString(){
		return "device_info [ip=" + ip + ",snmp=" + snmp+ ",netconf=" + netconf + "]";
		
	}*/
	
}
