package me.SyncWise.Android.Modules.Movements;

import java.io.Serializable;

public class ItemLoadQuantity implements Serializable{
	/**
	 * Default serial version ID.
	 */
	private static final long serialVersionUID = 1L;
	private String itemCode;
	private Double qttySmall;
	private Double qttMedium;
	private Double goodQuantity; 
	public Double getGoodQuantity() {
		return goodQuantity;
	}

	public void setGoodQuantity(Double goodQuantity) {
		this.goodQuantity = goodQuantity;
	}

	public String getItemCode() {
		return itemCode;
	}
	
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}
	
	public Double  getQttySmall() {
		return qttySmall;
	}
	
	public void setQttySmall(Double  qttySmall) {
		this.qttySmall = qttySmall;
	}

	public Double  getQttMedium() {
		return qttMedium;
	}
	
	public void setQttMedium(Double  qttMedium) {
		this.qttMedium = qttMedium;
	}

}
