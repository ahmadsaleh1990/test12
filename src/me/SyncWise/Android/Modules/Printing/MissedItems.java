package me.SyncWise.Android.Modules.Printing;

import java.io.Serializable;

public class MissedItems implements Serializable{
	    private static final long serialVersionUID = 1L;
	    private String itemCode;
	    private String itemName;
	    private Double quantityMedium;
	    private Double quantitySmall;
	    private Double baseUnit;
		public String getItemCode() {
			return itemCode;
		}
		public void setItemCode(String itemCode) {
			this.itemCode = itemCode;
		}
		public String getItemName() {
			return itemName;
		}
		public void setItemName(String itemName) {
			this.itemName = itemName;
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
		public Double getBaseUnit() {
			return baseUnit;
		}
		public void setBaseUnit(Double baseUnit) {
			this.baseUnit = baseUnit;
		}
}
