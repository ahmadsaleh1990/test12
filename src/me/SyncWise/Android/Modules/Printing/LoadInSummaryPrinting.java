package me.SyncWise.Android.Modules.Printing;

import java.io.Serializable;

public class LoadInSummaryPrinting  implements Serializable{
	private static final long serialVersionUID = 1L;
    
	private String itemCode;
    
	private String itemName;
  
	private Double quantityMedium;
   
	private Double quantitySmall;
  
	private Double baseUnit;
  
    private Double firstLoadQuantityMedium=0d;
  
    private Double firstLoadQuantitySmall=0d;
	
    private Double offLoadQuantityMedium=0d;
    
    private Double offLoadQuantitySmall=0d;
    
    private Double quantitySmallSales=0d;
    
    private Double quantityMediumSales=0d;
    
    private Double stockQuantitySmall=0d;
    
    private Double stockQuantityMedium=0d;
    
    private Double quantityMediumReturn=0d;
    
    private Double quantitySmallReturn=0d;
    
    private String clientCode;
    
    private String clientName;
	
    private String transactionCode;
    
    private Double totalAmount;
    
	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getTransactionCode() {
		return transactionCode;
	}

	public void setTransactionCode(String transactionCode) {
		this.transactionCode = transactionCode;
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

	public Double getQuantityMediumReturn() {
		return quantityMediumReturn;
	}

	public void setQuantityMediumReturn(Double quantityMediumReturn) {
		this.quantityMediumReturn = quantityMediumReturn;
	}

	public Double getQuantitySmallReturn() {
		return quantitySmallReturn;
	}

	public void setQuantitySmallReturn(Double quantitySmallReturn) {
		this.quantitySmallReturn = quantitySmallReturn;
	}

	public Double getQuantitySmallSales() {
		return quantitySmallSales;
	}
	
	public void setQuantitySmallSales(Double quantitySmallSales) {
		this.quantitySmallSales = quantitySmallSales;
	}
	
	public Double getQuantityMediumSales() {
		return quantityMediumSales;
	}
	
	public void setQuantityMediumSales(Double quantityMediumSales) {
		this.quantityMediumSales = quantityMediumSales;
	}
	
	public Double getStockQuantitySmall() {
		return stockQuantitySmall;
	}
	
	public void setStockQuantitySmall(Double stockQuantitySmall) {
		this.stockQuantitySmall = stockQuantitySmall;
	}
	
	public Double getStockQuantityMedium() {
		return stockQuantityMedium;
	}
	
	public void setStockQuantityMedium(Double stockQuantityMedium) {
		this.stockQuantityMedium = stockQuantityMedium;
	}
	
	public Double getOffLoadQuantityMedium() {
		return offLoadQuantityMedium;
	}
	
	public void setOffLoadQuantityMedium(Double offLoadQuantityMedium) {
		this.offLoadQuantityMedium = offLoadQuantityMedium;
	}
	
	public Double getOffLoadQuantitySmall() {
		return offLoadQuantitySmall;
	}

	public void setOffLoadQuantitySmall(Double offLoadQuantitySmall) {
	
		this.offLoadQuantitySmall = offLoadQuantitySmall;
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
	
	public Double getBaseUnit() {
		return baseUnit;
	}
	
	public void setBaseUnit(Double baseUnit) {
		this.baseUnit = baseUnit;
	}

	
	public Double getFirstLoadQuantityMedium() {
		return firstLoadQuantityMedium;
	}
	
	public void setFirstLoadQuantityMedium(Double firstLoadQuantityMedium) {
		this.firstLoadQuantityMedium = firstLoadQuantityMedium;
	}
	
	public Double getFirstLoadQuantitySmall() {
		return firstLoadQuantitySmall;
	}
	
	public void setFirstLoadQuantitySmall(Double firstLoadQuantitySmall) {
		this.firstLoadQuantitySmall = firstLoadQuantitySmall;
	}


}
