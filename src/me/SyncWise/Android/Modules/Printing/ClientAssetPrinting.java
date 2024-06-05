package me.SyncWise.Android.Modules.Printing;

import java.io.Serializable;

public class ClientAssetPrinting implements Serializable{
	    private static final long serialVersionUID = 1L;
	    private String itemName;
	    private String assetCode;
	    private String assetName;
	    private String statusName;
	    private String existanceName;
		
	    
	    public String getItemName() {
			return itemName;
		}
		public void setItemName(String itemName) {
			this.itemName = itemName;
		}
		public String getAssetCode() {
			return assetCode;
		}
		public void setAssetCode(String assetCode) {
			this.assetCode = assetCode;
		}
		public String getAssetName() {
			return assetName;
		}
		public void setAssetName(String assetName) {
			this.assetName = assetName;
		}
		public String getStatusName() {
			return statusName;
		}
		public void setStatusName(String statusName) {
			this.statusName = statusName;
		}
		public String getExistanceName() {
			return existanceName;
		}
		public void setExistanceName(String existanceName) {
			this.existanceName = existanceName;
		}
		public static long getSerialversionuid() {
			return serialVersionUID;
		}
}
