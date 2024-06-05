/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.ClientStock;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import me.SyncWise.Android.Database.AssetsStatus;
import me.SyncWise.Android.Database.Items;
import me.SyncWise.Android.Database.Units;

/**
 * Class used to represent client stock counts.
 * 
 * @author Elias
 *
 */
public class Stock implements Serializable {

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
	 * Boolean indicating if the current item belongs to the must stock list.
	 */
	private boolean isMustStockList;
	
	/**
	 * Boolean indicating if the current item which belongs to the must stock list is confirmed.
	 */
	private boolean isConfirmed;
	
	/**
	 * Boolean indicating if the current item is previously ordered.
	 */
	private boolean isHistory;
	
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
	 * Integer holding the quantity of the medium unit of measurement.
	 */
	private int quantityMedium;
	
	/**
	 * Integer holding the quantity of the small unit of measurement.
	 */
	private double quantitySmall;
	
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
	 * A Date object hosting the client stock count expiry date.
	 */
	private Date expiryDate;
	
	/**
	 * Flag indicating if the client stock count is merchandized.
	 */
	private boolean isMerchandize;
	
	/**
	 * Flag indicating if the client stock count expiry date is over six months.
	 */
	private boolean isOverSixMonths;
	/**
	 * Flag indicating if the client stock count expiry availables.
	 */
	private boolean availables;
	
	private double ShelfPriceSmall;
	private double ShelfPriceMedium;
	private double ShelfPriceBig;
	
	
	public double getShelfPriceSmall() {
		return ShelfPriceSmall;
	}

	public void setShelfPriceSmall(double shelfPriceSmall) {
		ShelfPriceSmall = shelfPriceSmall;
	}

	public double getShelfPriceMedium() {
		return ShelfPriceMedium;
	}

	public void setShelfPriceMedium(double shelfPriceMedium) {
		ShelfPriceMedium = shelfPriceMedium;
	}

	public double getShelfPriceBig() {
		return ShelfPriceBig;
	}

	public void setShelfPriceBig(double shelfPriceBig) {
		ShelfPriceBig = shelfPriceBig;
	}
	
	public boolean isAvailables() {
		return availables;
	}

	public void setAvailables(boolean availables) {
		this.availables = availables;
	}

	private AssetsStatus status;
	
	private AssetsStatus existence;
	
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
	 * Getter - {@link #isMustStockList}
	 * 
	 * @return	Boolean indicating if the current item belongs to the must stock list.
	 */
	public boolean isMustStockList () {
		return isMustStockList;
	}

	/**
	 * Setter - {@link #isMustStockList}
	 * 
	 * @param isMustStockList	Boolean indicating if the current item belongs to the must stock list.
	 */
	public void setMustStockList ( final boolean isMustStockList ) {
		this.isMustStockList = isMustStockList;
	}
	
	/**
	 * Getter - {@link #isConfirmed}
	 * 
	 * @return	Boolean indicating if the current item which belongs to the must stock list is confirmed.
	 */
	public boolean isConfirmed () {
		return isConfirmed;
	}

	/**
	 * Setter - {@link #isConfirmed}
	 * 
	 * @param isConfirmed	Boolean indicating if the current item which belongs to the must stock list is confirmed.
	 */
	public void setConfirmed ( final boolean isConfirmed ) {
		this.isConfirmed = isConfirmed;
	}

	/**
	 * Getter - {@link #isHistory}
	 * 
	 * @return	Boolean indicating if the current item is previously ordered.
	 */
	public boolean isHistory () {
		return isHistory;
	}

	/**
	 * Setter - {@link #isHistory}
	 * 
	 * @param isHistory	Boolean indicating if the current item is previously ordered.
	 */
	public void setHistory ( final boolean isHistory ) {
		this.isHistory = isHistory;
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
	public void setQuantitySmall ( final double quantitySmall ) {
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
	public double getQuantitySmall () {
		return quantitySmall;
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
	 * @param priceMedium	Double holding the unit price of the small unit of measurement.
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
	 * Setter - {@link #isMerchandize}
	 * 
	 * @param reason	Flag indicating if the client stock count is merchandized.
	 */
	public void setMerchandize ( final boolean isMerchandize ) {
		this.isMerchandize = isMerchandize;
	}
	
	/**
	 * Getter - {@link #isMerchandize}
	 * 
	 * @return	Flag indicating if the client stock count is merchandized.
	 */
	public boolean isMerchandize () {
		return isMerchandize;
	}
	
	/**
	 * Setter - {@link #isOverSixMonths}
	 * 
	 * @param reason	Flag indicating if the client stock count expiry date is over six months.
	 */
	public void setOverSixMonths ( final boolean isOverSixMonths ) {
		this.isOverSixMonths = isOverSixMonths;
	}
	
	/**
	 * Getter - {@link #isOverSixMonths}
	 * 
	 * @return	Flag indicating if the client stock count expiry date is over six months.
	 */
	public boolean isOverSixMonths () {
		return isOverSixMonths;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param item	Reference to the item object.
	 * @param unit	Reference to the unit of measurement object.
	 */
	public Stock ( final Items item , final Units unit ) {
		// Initialize the attributes
		this.item = item;
		this.unit = unit;
	}

	public AssetsStatus getStatus () {
		return status;
	}

	public void setStatus ( AssetsStatus status ) {
		this.status = status;
	}

	public AssetsStatus getExistence () {
		return existence;
	}

	public void setExistence ( AssetsStatus existence ) {
		this.existence = existence;
	}
	
}