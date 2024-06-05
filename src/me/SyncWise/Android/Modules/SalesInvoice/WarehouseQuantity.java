/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.SalesInvoice;

/**
 * Custom class used to represent warehouse quantities concerning items.
 * 
 * @author Elias
 *
 */
public class WarehouseQuantity {

	/**
	 * String hosting the related item code.
	 */
	private final String ItemCode;
	
	/**
	 * Double holding the warehouses quantity of the big unit of measurement.
	 */
	private double quantityBig;
	
	/**
	 * Double holding the warehouses quantity of the medium unit of measurement.
	 */
	private double quantityMedium;
	
	/**
	 * Double holding the warehouses quantity of the small unit of measurement.
	 */
	private double quantitySmall;
	
	/**
	 * Double holding the basic unit minimum quantity.
	 */
	private double minimumQuantity;
	
	/**
	 * Constructor.
	 * 
	 * @param ItemCode	String hosting the related item code.
	 */
	public WarehouseQuantity ( final String ItemCode ) {
		this.ItemCode = ItemCode;
	}

	/*
	 * Compares this instance with the specified object and indicates if they are equal.
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals ( Object object ) {
		// Flag used to indicate if the objects are equal
		boolean isEqual = false;
		// Check if the object is valid and instance of WarehouseQuantity
		if ( object != null && object instanceof WarehouseQuantity )
			// Compare objects
			isEqual = ItemCode.equals ( ( (WarehouseQuantity) object ).getItemCode () );
		// Return flag
		return isEqual;
	}

	/*
	 * Returns an integer hash code for this object.
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode () {
		final int prime = 31;
		int result = 1;
		result = prime * result + ( ( ItemCode == null ) ? 0 : ItemCode.hashCode () );
		return result;
	}
	
	/**
	 * Getter - {@link #itemCode}
	 * 
	 * @return String hosting the related item code.
	 */
	public String getItemCode () {
		return ItemCode;
	}
	
	/**
	 * Getter - {@link #quantityBig}
	 * 
	 * @return Double holding the warehouses quantity of the big unit of measurement.
	 */
	public double getQuantityBig () {
		return quantityBig;
	}
	
	/**
	 * Setter - {@link #quantityBig}
	 * 
	 * @param quantityBig Double holding the warehouses quantity of the big unit of measurement.
	 */
	public void setQuantityBig ( double quantityBig ) {
		this.quantityBig = quantityBig;
	}
	
	/**
	 * Getter - {@link #quantityMedium}
	 * 
	 * @return Double holding the warehouses quantity of the medium unit of measurement.
	 */
	public double getQuantityMedium () {
		return quantityMedium;
	}
	
	/**
	 * Setter - {@link #quantityMedium}
	 * 
	 * @param quantityMedium Double holding the warehouses quantity of the medium unit of measurement.
	 */
	public void setQuantityMedium ( double quantityMedium ) {
		this.quantityMedium = quantityMedium;
	}

	
	/**
	 * Getter - {@link #quantitySmall}
	 * 
	 * @return Double holding the warehouses quantity of the small unit of measurement.
	 */
	public double getQuantitySmall () {
		return quantitySmall;
	}

	/**
	 * Setter - {@link #quantitySmall}
	 * 
	 * @param quantitySmall Double holding the warehouses quantity of the small unit of measurement.
	 */
	public void setQuantitySmall ( double quantitySmall ) {
		this.quantitySmall = quantitySmall;
	}
	
	/**
	 * Getter - {@link #minimumQuantity}
	 * 
	 * @return Double holding the basic unit minimum quantity.
	 */
	public double getMinimumQuantity () {
		return minimumQuantity;
	}

	/**
	 * Setter - {@link #minimumQuantity}
	 * 
	 * @param minimumQuantity Double holding the basic unit minimum quantity.
	 */
	public void setMinimumQuantity ( double minimumQuantity ) {
		this.minimumQuantity = minimumQuantity;
	}
	
}