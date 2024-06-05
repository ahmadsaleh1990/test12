package me.SyncWise.Android.Modules.Printing;

import java.io.Serializable;

public class vehiclesStockPrinting  implements Serializable{
	  
	    private static final long serialVersionUID = 1L;
	 
	    private String VehicleCode;
	  
	    private String VehicleName;
	  
	    private String itemCode;
	  
	    private String itemName;
	 
	    private Double gooodquantityMedium;
	   
	    private Double goodquantitySmall;
	  
	    private Double damageQuantityMedium;
	  
	    private Double damageQuantitySmall;
	
	    public String getVehicleCode() {
			return VehicleCode;
		}
		
	    public void setVehicleCode(String vehicleCode) {
			VehicleCode = vehicleCode;
		}
	
	    public String getVehicleName() {
			return VehicleName;
		}
		
	    public void setVehicleName(String vehicleName) {
			VehicleName = vehicleName;
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
	
	    public Double getGooodquantityMedium() {
			return gooodquantityMedium;
		}
		
	    public void setGooodquantityMedium(Double gooodquantityMedium) {
			this.gooodquantityMedium = gooodquantityMedium;
		}
	
	    public Double getGoodquantitySmall() {
			return goodquantitySmall;
		}
		
	    public void setGoodquantitySmall(Double goodquantitySmall) {
			this.goodquantitySmall = goodquantitySmall;
		}
		
	    public Double getDamageQuantityMedium() {
			return damageQuantityMedium;
		}
	
	    public void setDamageQuantityMedium(Double damageQuantityMedium) {
		
			this.damageQuantityMedium = damageQuantityMedium;
		}
	
	    public Double getDamageQuantitySmall() {
			return damageQuantitySmall;
		}
		
	    public void setDamageQuantitySmall(Double damageQuantitySmall) {
			this.damageQuantitySmall = damageQuantitySmall;
		}
    
    }
