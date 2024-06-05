package me.SyncWise.Android.Modules.Printing;

import java.io.Serializable;

public class LoadPrinting implements Serializable{
	   private static final long serialVersionUID = 1L;
	    private String itemCode;
	    private String itemName;
	    private Double quantityMedium;
	    private Double quantitySmall;
	    private Double missedQuantityMedium;
	    private Double missedQuantitySmall;
	    private Double stockUnit;
	    private Double stockBox;
		private Double stockQuantity;
	    private Double missedBasicUnitQuantity;
	    private Double basicUnitQuantity;
	    private Double priceAmount;
	    private Double totaLineAmount;
	    public Double getTotaLineAmount() {
			return totaLineAmount;
		}
		public void setTotaLineAmount(Double totaLineAmount) {
			this.totaLineAmount = totaLineAmount;
		}
		public Double getPriceAmount() {
			return priceAmount;
		}
		public void setPriceAmount(Double priceAmount) {
			this.priceAmount = priceAmount;
		}
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
		public Double getMissedQuantityMedium() {
			return missedQuantityMedium;
		}
		public void setMissedQuantityMedium(Double missedQuantityMedium) {
			this.missedQuantityMedium = missedQuantityMedium;
		}
		public Double getMissedQuantitySmall() {
			return missedQuantitySmall;
		}
		public void setMissedQuantitySmall(Double missedQuantitySmall) {
			this.missedQuantitySmall = missedQuantitySmall;
		}
		public Double getStockUnit() {
			return stockUnit;
		}
		public void setStockUnit(Double stockUnit) {
			this.stockUnit = stockUnit;
		}
		public Double getStockBox() {
			return stockBox;
		}
		public void setStockBox(Double stockBox) {
			this.stockBox = stockBox;
		}
		public Double getStockQuantity() {
			return stockQuantity;
		}
		public void setStockQuantity(Double stockQuantity) {
			this.stockQuantity = stockQuantity;
		}
		public Double getMissedBasicUnitQuantity() {
			return missedBasicUnitQuantity;
		}
		public void setMissedBasicUnitQuantity(Double missedBasicUnitQuantity) {
			this.missedBasicUnitQuantity = missedBasicUnitQuantity;
		}
		public Double getBasicUnitQuantity() {
			return basicUnitQuantity;
		}
		public void setBasicUnitQuantity(Double basicUnitQuantity) {
			this.basicUnitQuantity = basicUnitQuantity;
		}
	
	    
}
