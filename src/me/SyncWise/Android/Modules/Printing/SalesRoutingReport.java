package me.SyncWise.Android.Modules.Printing;

import java.io.Serializable;

public class SalesRoutingReport implements Serializable{
    private static final long serialVersionUID = 1L;
    private String inv;
    private Long tin;
    private Long tout;
    private String clientCode;
    private String clientName;
    private String type;
    private String amount;
	public String getInv() {
		return inv;
	}
	public void setInv(String inv) {
		this.inv = inv;
	}
	public Long getTin() {
		return tin;
	}
	public void setTin(Long tin) {
		this.tin = tin;
	}
	public Long getTout() {
		return tout;
	}
	public void setTout(Long tout) {
		this.tout = tout;
	}
	public String getClientCode() {
		return clientCode;
	}
	public void setClientCode(String clientCode) {
		this.clientCode = clientCode;
	}
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
}
