/**
 * Copyright 2015 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.SalesInvoice;

/**
 * Class used to represent a sales invoice detail.
 * 
 * @author Elias
 *
 */
public class SalesInvoiceDetails {
	
	double grossAmount = 0;
	double discountAmount = 0;
	double taxes = 0;
	double totalValue = 0;
	
	/**
	 * Getter - {@link #grossAmount}
	 * 
	 * @return the grossAmount
	 */
	public double getGrossAmount () {
		return grossAmount;
	}
	
	/**
	 * Setter - {@link #grossAmount}
	 * 
	 * @param grossAmount the grossAmount to set
	 */
	public void setGrossAmount ( double grossAmount ) {
		this.grossAmount = grossAmount;
	}
	
	/**
	 * Getter - {@link #discountAmount}
	 * 
	 * @return the discountAmount
	 */
	public double getDiscountAmount () {
		return discountAmount;
	}
	
	/**
	 * Setter - {@link #discountAmount}
	 * 
	 * @param discountAmount the discountAmount to set
	 */
	public void setDiscountAmount ( double discountAmount ) {
		this.discountAmount = discountAmount;
	}
	
	/**
	 * Getter - {@link #taxes}
	 * 
	 * @return the taxes
	 */
	public double getTaxes () {
		return taxes;
	}
	
	/**
	 * Setter - {@link #taxes}
	 * 
	 * @param taxes the taxes to set
	 */
	public void setTaxes ( double taxes ) {
		this.taxes = taxes;
	}
	
	/**
	 * Getter - {@link #totalValue}
	 * 
	 * @return the totalValue
	 */
	public double getTotalValue () {
		return totalValue;
	}
	
	/**
	 * Setter - {@link #totalValue}
	 * 
	 * @param totalValue the totalValue to set
	 */
	public void setTotalValue ( double totalValue ) {
		this.totalValue = totalValue;
	}

}