package me.SyncWise.Android.Modules.Printing;

import java.io.Serializable;

public class ItemDistrbutionPrinting  implements Serializable{
    private static final long serialVersionUID = 1L;
    private String itemCode;
    private Double quantityMedium;
    private Double quantitySmall;
    private String clientName;
    
	public String getItemCode() {
		return itemCode;
	}
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}
	public Double getQuantityMedium() {
		return quantityMedium;
	}
	public void setQuantityMedium(Double quantityMedium) {
		this.quantityMedium = quantityMedium;
	}
	public Double getQuantitySmall() {
		return quantitySmall;
	}
	public void setQuantitySmall(Double quantitySmall) {
		this.quantitySmall = quantitySmall;
	}
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
    
    
    
    
}
