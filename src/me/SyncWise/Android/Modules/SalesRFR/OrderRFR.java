/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.SalesRFR;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import me.SyncWise.Android.Database.Items;
import me.SyncWise.Android.Database.ItemsUtils;
import me.SyncWise.Android.Database.Reasons;
import me.SyncWise.Android.Database.Units;

/**
 * Class used to represent sales orders.
 * 
 * @author Elias
 *
 */
public class OrderRFR implements Serializable {

	/**
	 * Constant integer holding the default state value.
	 */
	public static final int STATE_DEFAULT = 0;
	
 
	
	/**
	 * Constant integer holding the ordered state value.
	 */
	public static final int STATE_ORDERED = 2;
	
	/**
	 * Constant integer holding the basket state value.
	 */
	public static final int STATE_BASKET = 3;
	
	/**
	 * Constant integer holding the free state value.
	 */
	public static final int STATE_FREE = 4;
	private double caseTaxAmount;
	 
	public double getCaseTaxAmount() {
		return caseTaxAmount;
	}

	public void setCaseTaxAmount(double caseTaxAmount) {
		this.caseTaxAmount = caseTaxAmount;
	}
	
	private double unitTaxAmount;
	 
	public double getUnitTaxAmount() {
		return unitTaxAmount;
	}

	public void setUnitTaxAmount(double unitTaxAmount) {
		this.unitTaxAmount = unitTaxAmount;
	}
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
	 * Boolean used to indicate if this order item belongs to the priority items list.
	 */
	private boolean isPriority;
	
	/**
	 * Map holding the quantity of the big unit of measurement for all reasons.
	 */
	private HashMap < String , Integer > quantityBig;
	
	/**
	 * Map holding the quantity of the medium unit of measurement for all reasons.
	 */
	private HashMap < String , Integer > quantityMedium;
	
	/**
	 * Map holding the quantity of the small unit of measurement for all reasons.
	 */
	private HashMap < String , Integer > quantitySmall;
	
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
	 * List holding the reasons of returned items. 
	 */
	private ArrayList < Reasons > reasonOfReturn;

	/**
	 * Map holding the expiry dates for all reasons
	 */
	private HashMap<String, Date > expiryDate;
	
	/**
	 * String holding the the item lot.
	 */
	private String itemLot;
	
	/**
	 * Integer holding the basket quantity of the big unit of measurement.
	 */
	private int basketBig;

	private String itemBarcodes;
	public String getItemBarcodes() {
		return itemBarcodes;
	}

	public void setItemBarcodes(String itemBarcodes) {
		this.itemBarcodes = itemBarcodes;
	}
	/**
	 * Integer holding the basket quantity of the big unit of measurement.
	 */
	private int basketMedium;

	/**
	 * Integer holding the basket quantity of the big unit of measurement.
	 */
	private int basketSmall;
	/**
	 * Getter - {@link #itemLot}
	 * 
	 * @return	String holding the the item lot.
	 */
	public String getItemLot () {
		return itemLot;
	}

	/**
	 * Setter - {@link #itemLot}
	 * 
	 * @param itemLot	String holding the the item lot.
	 */
	public void setItemLot ( String itemLot ) {
		this.itemLot = itemLot;
	}
	
	/**
	 * Setter - {@link #isPriority}
	 * 
	 * @param isPriority	Boolean used to indicate if this order item belongs to the priority items list.
	 */
	public void setPriority ( final boolean isPriority ) {
		this.isPriority = isPriority;
	}
	
	/**
	 * Setter - {@link #isPriority}
	 * 
	 * @return	Boolean used to indicate if this order item belongs to the priority items list.
	 */
	public boolean isPriority () {
		return isPriority;
	}
	/**
	 * Double holding the discount percentage provided by the user over the ordered item.
	 */
	private double detailDiscountPercentage;
	
	/**
	 * Double holding a backup of the discount percentage provided by the user over the ordered item.
	 */
	private double detailDiscountPercentageBackup;
	
	/**
	 * Double holding the discount percentage provided by promotions over the ordered item.
	 */
	private double promotionDiscountPercentage;
	
	/**
	 * Double holding the discount percentage provided by promotions over the basket item.
	 */
	private double promotionDiscountPercentageBasket;
	
	/**
	 * Double holding the discount percentage provided by overall discounts over the ordered item.
	 */
	private double headerDiscountPercentage;
	
	/**
	 * Integer holding the basic unit quantity provided by promotions that are free.
	 */
	private int quantityPromotedFree;
	
	/**
	 * Integer holding the basic unit quantity provided by promotions that are baskets.
	 */
	private int quantityPromotedBasket;
	
	/**
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
	 * Getter - {@link #quantityBig}
	 * 
	 * @return	Map holding the quantity of the big unit of measurement for all reasons.
	 */
	public HashMap < String , Integer > getQuantityBig () {
		if ( this.quantityBig != null )
			return quantityBig;
		else
			return new HashMap < String , Integer > ();
	}
	
	/**
	 * Gets the quantity of the big unit of measurement for the provided reason code.
	 * 
	 * @param reasonCode	String hosting the reason code.
	 * @return	Returned quantity of the big unit of measurement of this item for the provided reason code.
	 */
	public int getQuantityBig ( String reasonCode ) {
		if ( this.quantityBig != null )
			if ( this.quantityBig.containsKey ( reasonCode ) )
				return this.quantityBig.get ( reasonCode ); 
		return 0;
	}
	
	/**
	 * Gets the total quantity of the big unit of measurement for all reasons for the current item.
	 * 
	 * @return	Integer holding the total quantity of returned items for all reasons.
	 */
	public int getQuantityBigSummation () {
		int sum = 0;
		if ( this.quantityBig != null )
			for ( Integer quantity : this.quantityBig.values () )
					sum += quantity;
		return sum;
	}
	
	/**
	 * Sets the quantity of the big unit of measurement for the provided reason code.
	 * 
	 * @param reasonCode	String hosting the reason code.
	 * @param quantityBig	Integer holding the returned quantity of the big unit of measurement of this item for the provided reason code.
	 */
	public void setQuantityBig ( String reasonCode, int quantityBig ) {
		if ( this.quantityBig == null )
			this.quantityBig = new HashMap < String , Integer > ();
		this.quantityBig.put ( reasonCode , quantityBig );
	}
	
	/**
	 * Getter - {@link #quantityMedium}
	 * 
	 * @return	Map holding the quantity of the medium unit of measurement for all reasons.
	 */
	public HashMap < String , Integer > getQuantityMedium () {
		if ( this.quantityMedium != null )
			return quantityMedium;
		else 
			return new HashMap < String , Integer > ();
	}
	
	/**
	 * Gets the quantity of the medium unit of measurement for the provided reason code.
	 * 
	 * @param reasonCode	String hosting the reason code.
	 * @return	Returned quantity of the medium unit of measurement of this item for the provided reason code.
	 */
	public int getQuantityMedium ( String reasonCode ) {
		if ( this.quantityMedium != null )
			if ( this.quantityMedium.containsKey ( reasonCode ) )
				return this.quantityMedium.get ( reasonCode ); 
		return 0;
	}
	
	/**
	 * Gets the total quantity of the medium unit of measurement for all reasons for the current item.
	 * 
	 * @return	Integer holding the total quantity of returned items for all reasons.
	 */
	public int getQuantityMediumSummation () {
		int sum = 0;
		if ( this.quantityMedium != null )
			for ( Integer quantity : this.quantityMedium.values () )
					sum += quantity;
		return sum;
	}
	
	/**
	 * Sets the quantity of the medium unit of measurement for the provided reason code.
	 * 
	 * @param reasonCode	String hosting the reason code.
	 * @param quantityMedium	Integer holding the returned quantity of the medium unit of measurement of this item for the provided reason code.
	 */
	public void setQuantityMedium ( String reasonCode , int quantityMedium ) {
		if ( this.quantityMedium == null )
			this.quantityMedium = new HashMap < String , Integer > ();
		this.quantityMedium.put ( reasonCode , quantityMedium );
	}

	/**
	 * Getter - {@link #quantitySmall}
	 * 
	 * @return	Map holding the quantity of the small unit of measurement for all reasons.
	 */
	public HashMap < String , Integer > getQuantitySmall () {
		if ( this.quantitySmall != null )
			return quantitySmall;
		else 
			return new HashMap < String , Integer > ();
	}
	
	/**
	 * Gets the quantity of the small unit of measurement for the provided reason code.
	 * 
	 * @param reasonCode	String hosting the reason code.
	 * @return	Returned quantity of the small unit of measurement of this item for the provided reason code.
	 */
	public int getQuantitySmall ( String reasonCode ) {
		if ( this.quantitySmall != null)
			if ( this.quantitySmall.containsKey ( reasonCode ) )
				return this.quantitySmall.get ( reasonCode ); 
		return 0;
	}
	
	/**
	 * Gets the total quantity of the small unit of measurement for all reasons for the current item.
	 * 
	 * @return	Integer holding the total quantity of returned items for all reasons.
	 */
	public int getQuantitySmallSummation () {
		int sum = 0;
		if ( this.quantitySmall != null )
			for ( Integer quantity : this.quantitySmall.values () )
					sum+=quantity ;
		return sum;
	}

	/**
	 * Sets the quantity of the small unit of measurement for the provided reason code.
	 * 
	 * @param reasonCode	String hosting the reason code.
	 * @param quantityBig	Integer holding the returned quantity of the small unit of measurement of this item for the provided reason code.
	 */
	public void setQuantitySmall ( String reasonCode , int quantitySmall ) {
		if ( this.quantitySmall == null )
			this.quantitySmall = new HashMap < String , Integer > ();
		this.quantitySmall.put ( reasonCode , quantitySmall );
	}
	
	/**
	 * Returns the basic unit quantity of returned items for all units of measurements for all reasons.
	 * 
	 * @return	Total basic unit quantity of the returned items for all reasons.
	 */
	public int getBasicQuantity () {
		return getQuantityBigSummation () * getItem ().getUnitBigMedium () * getItem ().getUnitMediumSmall () + getQuantityMediumSummation () * getItem ().getUnitMediumSmall () + getQuantitySmallSummation ();
	}
	
	/**
	 * Returns the basic unit quantity of returned items for all units of measurements for the provided reason.
	 * 
	 * @param reasonCode	String hosting the reason code.
	 * @return	Total basic unit quantity of the returned items for the provided reason.
	 */
	public int getQuantitySummation ( String reasonCode ) {
		return getQuantityBig ( reasonCode ) * getItem ().getUnitBigMedium () * getItem ().getUnitMediumSmall () + getQuantityMedium ( reasonCode ) * getItem ().getUnitMediumSmall () + getQuantitySmall ( reasonCode );
	}
	
	/**
	 * Getter - {@link #reasonOfReturn}
	 * 
	 * @return	List holding the reasons of returned items.
	 */
	public ArrayList < Reasons > getReasonOfReturn () {
		if ( this.reasonOfReturn != null )
			return reasonOfReturn;
		else 
			return new ArrayList < Reasons > ();
	}
	
	/**
	 * Indicates if the provided reason code is already mapped to a returned quantity.
	 * 
	 * @param reasonCode	String holding the reason code to search for.
	 * @return	Flag indicating if the provided reason is mapped for a returned quantity.
	 */
	public boolean containsReasonCode ( String reasonCode ) {
		if ( reasonOfReturn == null || reasonOfReturn.isEmpty () )
			return false;
		for ( Reasons reason : reasonOfReturn )
			if ( reason.getReasonCode ().equals ( reasonCode ) )
				return true;
		return false;
	}
	
	/**
	 * Adds the provided reason object to the list of reasons of returns.
	 * 
	 * @param reasonOfReturn	A {@link me.SyncWise.Android.Database.Reasons Reasons} object to add to the main list.
	 */
	public void setReasonOfReturn ( Reasons reasonOfReturn ) {
		if ( this.reasonOfReturn == null )
			this.reasonOfReturn = new  ArrayList < Reasons > ();
		if ( ! this.reasonOfReturn.contains ( reasonOfReturn ) )
			this.reasonOfReturn.add ( reasonOfReturn );
	}

	/**
	 * @return the expiryDate
	 */
	
	/**
	 * Getter - {@link #expiryDate}
	 * 
	 * @return	Map holding the expiry dates for all reasons.
	 */
	public HashMap < String , Date > getExpiryDate () {
		if ( this.expiryDate != null )
			return expiryDate;
		else 
			return new HashMap < String , Date > ();
	}

	/**
	 * Gets the expiry date of the returned items for the provided reason code.
	 * 
	 * @param reasonCode	String hosting the reason code.
	 * @return	Expiry date of the returned item for the provided reason code. 
	 */
	public Date getExpiryDate ( String reasonCode ){
		if ( this.expiryDate != null )
			if ( this.expiryDate.containsKey ( reasonCode ) )
				return this.expiryDate.get ( reasonCode ); 
		return null;
	}
	
	/**
	 * Sets the expiry date of the returned item for the provided reason code.
	 * 
	 * @param reasonCode	String hosting the reason code.
	 * @param expiryDate	Date object holding the expiry date of the returned item for the provided reason code.
	 */
	public void setExpiryDate ( String reasonCode , Date expiryDate ) {
		if ( this.expiryDate == null )
			this.expiryDate = new HashMap < String , Date > ();
		this.expiryDate.put ( reasonCode , expiryDate );
	}

	/**
	 * Constructor.
	 * 
	 * @param item	Reference to the item object.
	 * @param unit	Reference to the unit of measurement object.
	 */
	public OrderRFR ( final Items item , final Units unit ) {
		// Initialize the attributes
		this.item = item;
		this.unit = unit;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Setter - {@link #detailDiscountPercentage}
	 * 
	 * @param detailDiscountPercentage	Double holding the discount percentage provided by the user over the ordered item.
	 */
	public void setDetailDiscountPercentage ( final double detailDiscountPercentage ) {
		// Check if the percentage is valid
		if ( detailDiscountPercentage >= 0 && detailDiscountPercentage <= 100 ) {
			// Set the percentage
			this.detailDiscountPercentage = detailDiscountPercentage;
			this.detailDiscountPercentageBackup = detailDiscountPercentage;
		} // End if
	}
	
	/**
	 * Getter - {@link #detailDiscountPercentage}
	 * 
	 * @return Double holding the discount percentage provided by the user over the ordered item.
	 */
	public double getDetailDiscountPercentage () {
		// Return the percentage
		return detailDiscountPercentage;
	}
	
	/**
	 * Setter - {@link #promotionDiscountPercentage}
	 * 
	 * @param promotionDiscountPercentage	Double holding the discount percentage provided by the promotions over the ordered item.
	 */
	public void setPromotionDiscountPercentage ( final double promotionDiscountPercentage ) {
		// Check if the percentage is valid
		if ( promotionDiscountPercentage >= 0 && promotionDiscountPercentage <= 100 )
			// Set the percentage
			this.promotionDiscountPercentage = promotionDiscountPercentage;
	}
	
	/**
	 * Getter - {@link #promotionDiscountPercentage}
	 * 
	 * @return Double holding the discount percentage provided by the promotions over the ordered item.
	 */
	public double getPromotionDiscountPercentage () {
		// Return the percentage
		return promotionDiscountPercentage;
	}
	
	/**
	 * Setter - {@link #promotionDiscountPercentageBasket}
	 * 
	 * @param promotionDiscountPercentageBasket	Double holding the discount percentage provided by the promotions over the basket item.
	 */
	public void setPromotionDiscountPercentageBasket ( final double promotionDiscountPercentageBasket ) {
		// Check if the percentage is valid
		if ( promotionDiscountPercentageBasket >= 0 && promotionDiscountPercentageBasket <= 100 )
			// Set the percentage
			this.promotionDiscountPercentageBasket = promotionDiscountPercentageBasket;
	}
	
	/**
	 * Getter - {@link #promotionDiscountPercentageBasket}
	 * 
	 * @return Double holding the discount percentage provided by the promotions over the basket item.
	 */
	public double getPromotionDiscountPercentageBasket () {
		// Return the percentage
		return promotionDiscountPercentageBasket;
	}
	
	/**
	 * Setter - {@link #headerDiscountPercentage}
	 * 
	 * @param headerDiscountPercentage	Double holding the discount percentage provided by the overall discounts over the ordered item.
	 */
	public void setHeaderDiscountPercentage ( final double headerDiscountPercentage ) {
		// Check if the percentage is valid
		if ( headerDiscountPercentage >= 0 && headerDiscountPercentage <= 100 )
			// Set the percentage
			this.headerDiscountPercentage = headerDiscountPercentage;
	}
	
	/**
	 * Getter - {@link #headerDiscountPercentage}
	 * 
	 * @return Double holding the discount percentage provided by the overall discounts over the ordered item.
	 */
	public double getHeaderDiscountPercentage () {
		// Return the percentage
		return headerDiscountPercentage;
	}
	
	/**
	 * Setter - {@link #quantityPromotedFree}
	 * 
	 * @param quantityPromotedFree	Integer holding the basic unit quantity provided by promotions that are free.
	 */
	public void setQuantityPromotedFree ( final int quantityPromotedFree ) {
		// Check if the promoted free quantity is valid
		if ( quantityPromotedFree >= 0 )
			// Set the percentage
			this.quantityPromotedFree = quantityPromotedFree;
	}
	
	/**
	 * Getter - {@link #quantityPromotedFree}
	 * 
	 * @return Integer holding the basic unit quantity provided by promotions that are free.
	 */
	public int getQuantityPromotedFree () {
		// Return the quantity
		return quantityPromotedFree;
	}
	
	/**
	 * Setter - {@link #quantityPromotedFree}
	 * 
	 * @param quantityPromotedBasket	Integer holding the basic unit quantity provided by promotions that are baskets.
	 */
	public void setQuantityPromotedBasket ( final int quantityPromotedBasket ) {
		// Check if the promoted basket quantity is valid
		if ( quantityPromotedBasket >= 0 )
			// Set the percentage
			this.quantityPromotedBasket = quantityPromotedBasket;
	}
	
	/**
	 * Getter - {@link #quantityPromotedBasket}
	 * 
	 * @return Integer holding the basic unit quantity provided by promotions that are baskets.
	 */
	public int getQuantityPromotedBasket () {
		// Return the quantity
		return quantityPromotedBasket;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Restores the detail discount percentage from the backup value.
	 */
	public void restoreDetailDiscountPercentage () {
		detailDiscountPercentage = detailDiscountPercentageBackup;
	}
	
	/**
	 * Clears instant promotion entries (discounts, quantities, ...).
	 */
	public void clearInstantPromotions () { 
		promotionDiscountPercentage = 0;
		quantityPromotedFree = 0;
	}
	
	/**
	 * Clears instant promotion entries (discounts, quantities, ...).
	 */
	public void clearBasketPromotions () { 
		promotionDiscountPercentageBasket = 0;
		quantityPromotedBasket = 0;
	}
	
	/**
	 * Setter - {@link #basketBig}
	 * 
	 * @param basketBig	Integer holding the basket quantity of the big unit of measurement.
	 */
	public void setBasketBig ( final int basketBig ) {
		// Check if the basket quantity is valid
		if ( basketBig >= 0 )
			// Set the basket quantity
			this.basketBig = basketBig;
	}
	
	/**
	 * Getter - {@link #basketBig}
	 * 
	 * @return	Integer holding the basket quantity of the big unit of measurement.
	 */
	public int getBasketBig () {
		return basketBig;
	}
	
	/**
	 * Setter - {@link #basketMedium}
	 * 
	 * @param basketMedium	Integer holding the basket quantity of the medium unit of measurement.
	 */
	public void setBasketMedium ( final int basketMedium ) {
		// Check if the basket quantity is valid
		if ( basketMedium >= 0 )
			// Set the basket quantity
			this.basketMedium = basketMedium;
	}
	
	/**
	 * Getter - {@link #basketMedium}
	 * 
	 * @return	Integer holding the basket quantity of the medium unit of measurement.
	 */
	public int getBasketMedium () {
		return basketMedium;
	}
	
	/**
	 * Setter - {@link #basketSmall}
	 * 
	 * @param basketSmall	Integer holding the basket quantity of the small unit of measurement.
	 */
	public void setBasketSmall ( final int basketSmall ) {
		// Check if the basket quantity is valid
		if ( basketSmall >= 0 )
			// Set the basket quantity
			this.basketSmall = basketSmall;
	}
	
	/**
	 * Getter - {@link #basketSmall}
	 * 
	 * @return	Integer holding the basket quantity of the small unit of measurement.
	 */
	public int getBasketSmall () {
		return basketSmall;
	}

}