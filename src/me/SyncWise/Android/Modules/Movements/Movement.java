/**
 * Copyright 2015 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Movements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import me.SyncWise.Android.Database.Items;
import me.SyncWise.Android.Database.Reasons;
import me.SyncWise.Android.Database.Units;

/**
 * Class used to represent movements.
 * 
 * @author Elias
 *
 */
public class Movement implements Serializable {

	/**
	 * Default serial version ID.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Reference to the item object.
	 */
	private final Items item;
	
	/**
	 * Reference to the unit of measurement object.
	 */
	private final Units unit;
	
	/**
	 * Double holding the tax percentage.
	 */
	private double tax;
	
	/**
	 * List of strings holding the item's divisions codes.
	 */
	private ArrayList < String > divisionCodes;
	
	/**
	 * List of strings holding the item's bar codes.
	 */
	private ArrayList < String > barCodes;
	
	/**
	 * Integer holding the quantity of the big unit of measurement.
	 */
	private int quantityBig;
	
	/**
	 * Integer holding the suggested quantity of the big unit of measurement.
	 */
	private int suggestedBig;
	
	/**
	 * Integer holding the quantity of the medium unit of measurement.
	 */
	private int quantityMedium;
	
	/**
	 * Integer holding the suggested quantity of the big unit of measurement.
	 */
	private int suggestedMedium;
	
	/**
	 * Integer holding the quantity of the small unit of measurement.
	 */
	private int quantitySmall;
	
	/**
	 * Integer holding the suggested quantity of the big unit of measurement.
	 */
	private int suggestedSmall;
	
	/**
	 * Double holding the unit price of the big unit of measurement.
	 */
	private double priceBig;
	
	/**
	 * Double holding the unit price of the medium unit of measurement.
	 */
	private double priceMedium;
	
	/**
	 * Double holding the unit price of the small unit of measurement.
	 */
	private double priceSmall;
	
	/**
	 * A Date object hosting the movement expiry date.
	 */
	private Date expiryDate;
	
	/**
	 * A Date object hosting the suggested movement expiry date.
	 */
	private Date suggestedExpiryDate;
	
	/**
	 * A {@link me.SyncWise.Android.Database.Reasons Reasons} object hosting the movement reason.
	 */
	private Reasons reason;
	
	/**
	 * A {@link me.SyncWise.Android.Database.Reasons Reasons} object hosting the suggested movement reason.
	 */
	private Reasons suggestedReason;
	
	/**
	 * Getter - {@link #reason}
	 * 
	 * @return A {@link me.SyncWise.Android.Database.Reasons Reasons} object hosting the movement reason.
	 */
	public Reasons getReason () {
		return reason;
	}
	
	/**
	 * Setter - {@link #suggestedReason}
	 * 
	 * @param reason A {@link me.SyncWise.Android.Database.Reasons Reasons} object hosting the suggested movement reason.
	 */
	public void setSuggestedReason ( Reasons suggestedReason ) {
		this.suggestedReason = suggestedReason;
	}
	
	/**
	 * Getter - {@link #suggestedReason}
	 * 
	 * @return A {@link me.SyncWise.Android.Database.Reasons Reasons} object hosting the suggested movement reason.
	 */
	public Reasons getSuggestedReason () {
		return suggestedReason;
	}
	
	/**
	 * Setter - {@link #reason}
	 * 
	 * @param reason A {@link me.SyncWise.Android.Database.Reasons Reasons} object hosting the movement reason.
	 */
	public void setReason ( Reasons reason ) {
		this.reason = reason;
	}

	/**
	 * Setter - {@link #priceBig}
	 * 
	 * @param priceBig	Double holding the unit price of the big unit of measurement.
	 */
	public void setPriceBig ( final double priceBig ) {
		// Check if the price is valid
		if ( priceBig >= 0 )
			// Set the price
			this.priceBig = priceBig;
	}
	
	/**
	 * Getter - {@link #priceBig}
	 * 
	 * @return	Double holding the unit price of the big unit of measurement.
	 */
	public double getPriceBig () {
		return priceBig;
	}
	
	/**
	 * Setter - {@link #priceMedium}
	 * 
	 * @param priceMedium	Double holding the unit price of the medium unit of measurement.
	 */
	public void setPriceMedium ( final double priceMedium ) {
		// Check if the price is valid
		if ( priceMedium >= 0 )
			// Set the price
			this.priceMedium = priceMedium;
	}
	
	/**
	 * Getter - {@link #priceMedium}
	 * 
	 * @return	Double holding the unit price of the medium unit of measurement.
	 */
	public double getPriceMedium () {
		return priceMedium;
	}
	
	/**
	 * Setter - {@link #priceSmall}
	 * 
	 * @param priceSmall	Double holding the unit price of the small unit of measurement.
	 */
	public void setPriceSmall ( final double priceSmall ) {
		// Check if the price is valid
		if ( priceSmall >= 0 )
			// Set the price
			this.priceSmall = priceSmall;
	}
	
	/**
	 * Getter - {@link #priceSmall}
	 * 
	 * @return	Double holding the unit price of the small unit of measurement.
	 */
	public double getPriceSmall () {
		return priceSmall;
	}
	
	/**
	 * Getter - {@link #item}
	 * 
	 * @return	Reference to the item object.
	 */
	public Items getItem () {
		return item;
	}
	
	/**
	 * Getter - {@link #unit}
	 * 
	 * @return	Reference to the unit of measurement object.
	 */
	public Units getUnit () {
		return unit;
	}
	
	/**
	 * Setter - {@link #tax}
	 * 
	 * @param tax	Double holding the tax percentage.
	 */
	public void setTax ( final double tax ) {
		// Check if the tax is valid
		if ( tax >= 0 || tax <= 100 )
			// Set the tax
			this.tax = tax;
	}
	
	/**
	 * Setter - {@link #tax}
	 * 
	 * @return	Double holding the tax percentage.
	 */
	public double getTax () {
		return tax;
	}
	
	/**
	 * Setter - {@link #divisionCodes}
	 * 
	 * @param divisionCodes	List of strings holding the item's divisions codes.
	 */
	public void setDivisionCodes ( final ArrayList < String > divisionCodes ) {
		this.divisionCodes = divisionCodes;
	}
	
	/**
	 * Getter - {@link #divisionCodes}
	 * 
	 * @return	List of strings holding the item's divisions codes.
	 */
	public ArrayList < String > getDivisionCodes () {
		return divisionCodes;
	}
	
	/**
	 * Getter - {@link #barCodes}
	 * 
	 * @return	List of strings holding the item's bar codes.
	 */
	public ArrayList < String > getBarCodes () {
		return barCodes;
	}

	/**
	 * Setter - {@link #barCodes}
	 * 
	 * @param barCodes	List of strings holding the item's bar codes.
	 */
	public void setBarCodes ( ArrayList < String > barCodes ) {
		this.barCodes = barCodes;
	}

	/**
	 * Setter - {@link #quantityBig}
	 * 
	 * @param quantityBig	Integer holding the quantity of the big unit of measurement.
	 */
	public void setQuantityBig ( final int quantityBig ) {
		// Check if the quantity is valid
		if ( quantityBig >= 0 )
			// Set the quantity
			this.quantityBig = quantityBig;
	}
	
	/**
	 * Getter - {@link #quantityBig}
	 * 
	 * @return	Integer holding the quantity of the big unit of measurement.
	 */
	public int getQuantityBig () {
		return quantityBig;
	}
	
	/**
	 * Setter - {@link #quantityMedium}
	 * 
	 * @param quantityMedium	Integer holding the quantity of the medium unit of measurement.
	 */
	public void setQuantityMedium ( final int quantityMedium ) {
		// Check if the quantity is valid
		if ( quantityMedium >= 0 )
			// Set the quantity
			this.quantityMedium = quantityMedium;
	}
	
	/**
	 * Getter - {@link #quantityMedium}
	 * 
	 * @return	Integer holding the quantity of the medium unit of measurement.
	 */
	public int getQuantityMedium () {
		return quantityMedium;
	}
	
	/**
	 * Setter - {@link #quantitySmall}
	 * 
	 * @param quantitySmall	Integer holding the quantity of the small unit of measurement.
	 */
	public void setQuantitySmall ( final int quantitySmall ) {
		// Check if the quantity is valid
		if ( quantitySmall >= 0 )
			// Set the quantity
			this.quantitySmall = quantitySmall;
	}
	
	/**
	 * Getter - {@link #quantitySmall}
	 * 
	 * @return	Integer holding the quantity of the small unit of measurement.
	 */
	public int getQuantitySmall () {
		return quantitySmall;
	}
	
	/**
	 * Setter - {@link #suggestedBig}
	 * 
	 * @param suggestedBig	Integer holding the suggested quantity of the big unit of measurement.
	 */
	public void setSuggestedBig ( final int suggestedBig ) {
		// Check if the suggested quantity is valid
		if ( suggestedBig >= 0 )
			// Set the suggested quantity
			this.suggestedBig = suggestedBig;
	}
	
	/**
	 * Getter - {@link #suggestedBig}
	 * 
	 * @return	Integer holding the suggested quantity of the big unit of measurement.
	 */
	public int getSuggestedBig () {
		return suggestedBig;
	}
	
	/**
	 * Setter - {@link #suggestedMedium}
	 * 
	 * @param suggestedMedium	Integer holding the suggested quantity of the medium unit of measurement.
	 */
	public void setSuggestedMedium ( final int suggestedMedium ) {
		// Check if the suggested quantity is valid
		if ( suggestedMedium >= 0 )
			// Set the suggested quantity
			this.suggestedMedium = suggestedMedium;
	}
	
	/**
	 * Getter - {@link #suggestedMedium}
	 * 
	 * @return	Integer holding the suggested quantity of the medium unit of measurement.
	 */
	public int getSuggestedMedium () {
		return suggestedMedium;
	}
	
	/**
	 * Setter - {@link #suggestedSmall}
	 * 
	 * @param quantitySmall	Integer holding the suggested quantity of the small unit of measurement.
	 */
	public void setSuggestedSmall ( final int suggestedSmall ) {
		// Check if the suggested quantity is valid
		if ( suggestedSmall >= 0 )
			// Set the suggested quantity
			this.suggestedSmall = suggestedSmall;
	}
	
	/**
	 * Getter - {@link #suggestedSmall}
	 * 
	 * @return	Integer holding the suggested quantity of the small unit of measurement.
	 */
	public int getSuggestedSmall () {
		return suggestedSmall;
	}
	
	/**
	 * Setter - {@link #expiryDate}
	 * 
	 * @param reason	A Date object hosting the client stock count expiry date.
	 */
	public void setExpiryDate ( final Date expiryDate ) {
		this.expiryDate = expiryDate;
	}
	
	/**
	 * Getter - {@link #expiryDate}
	 * 
	 * @return	A Date object hosting the client stock count expiry date.
	 */
	public Date getExpiryDate () {
		return expiryDate;
	}
	
	/**
	 * Setter - {@link #suggestedExpiryDate}
	 * 
	 * @param reason	A Date object hosting the suggested movement expiry date.
	 */
	public void setSuggestedExpiryDate ( final Date suggestedExpiryDate ) {
		this.suggestedExpiryDate = suggestedExpiryDate;
	}
	
	/**
	 * Getter - {@link #suggestedExpiryDate}
	 * 
	 * @return	A Date object hosting the suggested movement expiry date.
	 */
	public Date getSuggestedExpiryDate () {
		return suggestedExpiryDate;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param item	Reference to the item object.
	 * @param unit	Reference to the unit of measurement object.
	 */
	public Movement ( final Items item , final Units unit ) {
		// Initialize the attributes
		this.item = item;
		this.unit = unit;
	}
	
}