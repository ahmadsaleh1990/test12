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

import java.io.Serializable;
import java.util.ArrayList;

import me.SyncWise.Android.Database.Items;
import me.SyncWise.Android.Database.ItemsUtils;
import me.SyncWise.Android.Database.Units;

/**
 * Class used to represent sales invoices.
 * 
 * @author Elias
 *
 */
public class Invoice implements Serializable {

	/**
	 * Default serial version ID.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constant integer holding the default state value.
	 */
	public static final int STATE_DEFAULT = 0;
	
	/**
	 * Constant integer holding the missed state value.
	 */
	public static final int STATE_MISSED = 1;
	
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
	private int quantitySmall;
	
	/**
	 * Integer holding the ordered quantity of the big unit of measurement.
	 */
	private int orderedBig;
	
	/**
	 * Integer holding the ordered quantity of the medium unit of measurement.
	 */
	private int orderedMedium;
	
	/**
	 * Integer holding the ordered quantity of the small unit of measurement.
	 */
	private int orderedSmall;
	
	/**
	 * Integer holding the missed quantity of the big unit of measurement.
	 */
	private int missedBig;
	
	/**
	 * Integer holding the missed quantity of the medium unit of measurement.
	 */
	private int missedMedium;
	
	/**
	 * Integer holding the missed quantity of the small unit of measurement.
	 */
	private int missedSmall;
	
	/**
	 * Integer holding the vehicle stock quantity of the big unit of measurement.
	 */
	private int vehicleBig;
	
	/**
	 * Integer holding the vehicle stock quantity of the big unit of measurement.
	 */
	private int vehicleMedium;
	
	/**
	 * Integer holding the vehicle stock quantity of the big unit of measurement.
	 */
	private int vehicleSmall;
	
	/**
	 * Integer holding the counted quantity of the big unit of measurement.
	 */
	private int countedBig;
	
	/**
	 * Integer holding the counted quantity of the big unit of measurement.
	 */
	private int countedMedium;
	
	/**
	 * Integer holding the counted quantity of the big unit of measurement.
	 */
	private int countedSmall;

	/**
	 * Integer holding the suggested quantity of the big unit of measurement.
	 */
	private int suggestedBig;

	/**
	 * Integer holding the suggested quantity of the big unit of measurement.
	 */
	private int suggestedMedium;

	/**
	 * Integer holding the suggested quantity of the big unit of measurement.
	 */
	private int suggestedSmall;

	/**
	 * Integer holding the free quantity of the big unit of measurement.
	 */
	private int freeBig;

	/**
	 * Integer holding the free quantity of the big unit of measurement.
	 */
	private int freeMedium;

	/**
	 * Integer holding the free quantity of the big unit of measurement.
	 */
	private int freeSmall;

	/**
	 * Integer holding the basket quantity of the big unit of measurement.
	 */
	private int basketBig;

	/**
	 * Integer holding the basket quantity of the big unit of measurement.
	 */
	private int basketMedium;

	/**
	 * Integer holding the basket quantity of the big unit of measurement.
	 */
	private int basketSmall;
	
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
	 * Getter - {@link #item}
	 * 
	 * @return	Reference to the item object.
	 */
	public Items getItem () {
		return item;
	}
	/**
	 * Double holding the unit price of the medium unit of measurement.
	 */
	private double priceMediumOld;
	
	public double getPriceMediumOld() {
		return priceMediumOld;
	}

	public void setPriceMediumOld(double priceMediumOld) {
		this.priceMediumOld = priceMediumOld;
	}

	public double getPriceSmallOld() {
		return priceSmallOld;
	}

	public void setPriceSmallOld(double priceSmallOld) {
		this.priceSmallOld = priceSmallOld;
	}

	/**
	 * Double holding the unit price of the small unit of measurement.
	 */
	private double priceSmallOld;
	/**
	 * Getter - {@link #unit}
	 * 
	 * @return	Reference to the unit of measurement object.
	 */
	public Units getUnit () {
		return unit;
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
	 * Setter - {@link #orderedBig}
	 * 
	 * @param orderedBig	Integer holding the ordered quantity of the big unit of measurement.
	 */
	public void setOrderedBig ( final int orderedBig ) {
		// Check if the ordered quantity is valid
		if ( orderedBig >= 0 )
			// Set the ordered quantity
			this.orderedBig = orderedBig;
	}
	
	/**
	 * Getter - {@link #orderedBig}
	 * 
	 * @return	Integer holding the ordered quantity of the big unit of measurement.
	 */
	public int getOrderedBig () {
		return orderedBig;
	}
	
	/**
	 * Setter - {@link #orderedMedium}
	 * 
	 * @param orderedMedium	Integer holding the ordered quantity of the medium unit of measurement.
	 */
	public void setOrderedMedium ( final int orderedMedium ) {
		// Check if the ordered quantity is valid
		if ( orderedMedium >= 0 )
			// Set the ordered quantity
			this.orderedMedium = orderedMedium;
	}
	
	/**
	 * Getter - {@link #orderedMedium}
	 * 
	 * @return	Integer holding the ordered quantity of the medium unit of measurement.
	 */
	public int getOrderedMedium () {
		return orderedMedium;
	}
	
	/**
	 * Setter - {@link #orderedSmall}
	 * 
	 * @param orderedSmall	Integer holding the ordered quantity of the small unit of measurement.
	 */
	public void setOrderedSmall ( final int orderedSmall ) {
		// Check if the ordered quantity is valid
		if ( orderedSmall >= 0 )
			// Set the ordered quantity
			this.orderedSmall = orderedSmall;
	}
	
	/**
	 * Getter - {@link #orderedSmall}
	 * 
	 * @return	Integer holding the ordered quantity of the small unit of measurement.
	 */
	public int getOrderedSmall () {
		return orderedSmall;
	}
	
	/**
	 * Setter - {@link #missedBig}
	 * 
	 * @param missedBig	Integer holding the missed quantity of the big unit of measurement.
	 */
	public void setMissedBig ( final int missedBig ) {
		// Check if the missed quantity is valid
		if ( missedBig >= 0 )
			// Set the missed quantity
			this.missedBig = missedBig;
	}
	
	/**
	 * Getter - {@link #missedBig}
	 * 
	 * @return	Integer holding the missed quantity of the big unit of measurement.
	 */
	public int getMissedBig () {
		return missedBig;
	}
	
	/**
	 * Setter - {@link #missedMedium}
	 * 
	 * @param missedMedium	Integer holding the missed quantity of the medium unit of measurement.
	 */
	public void setMissedMedium ( final int missedMedium ) {
		// Check if the missed quantity is valid
		if ( missedMedium >= 0 )
			// Set the missed quantity
			this.missedMedium = missedMedium;
	}
	
	/**
	 * Getter - {@link #missedMedium}
	 * 
	 * @return	Integer holding the missed quantity of the medium unit of measurement.
	 */
	public int getMissedMedium () {
		return missedMedium;
	}
	
	/**
	 * Setter - {@link #missedSmall}
	 * 
	 * @param missedSmall	Integer holding the missed quantity of the small unit of measurement.
	 */
	public void setMissedSmall ( final int missedSmall ) {
		// Check if the missed quantity is valid
		if ( missedSmall >= 0 )
			// Set the missed quantity
			this.missedSmall = missedSmall;
	}
	
	/**
	 * Getter - {@link #missedSmall}
	 * 
	 * @return	Integer holding the missed quantity of the small unit of measurement.
	 */
	public int getMissedSmall () {
		return missedSmall;
	}
	
	/**
	 * Setter - {@link #vehicleBig}
	 * 
	 * @param vehicleBig	Integer holding the vehicle stock quantity of the big unit of measurement.
	 */
	public void setVehicleBig ( final int vehicleBig ) {
		// Check if the vehicle stock quantity is valid
		if ( vehicleBig >= 0 )
			// Set the vehicle stock quantity
			this.vehicleBig = vehicleBig;
	}
	
	/**
	 * Getter - {@link #vehicleBig}
	 * 
	 * @return	Integer holding the vehicle stock quantity of the big unit of measurement.
	 */
	public int getVehicleBig () {
		return vehicleBig;
	}
	
	/**
	 * Setter - {@link #vehicleMedium}
	 * 
	 * @param vehicleMedium	Integer holding the vehicle stock quantity of the medium unit of measurement.
	 */
	public void setVehicleMedium ( final int vehicleMedium ) {
		// Check if the vehicle stock quantity is valid
		if ( vehicleMedium >= 0 )
			// Set the vehicle stock quantity
			this.vehicleMedium = vehicleMedium;
	}
	
	/**
	 * Getter - {@link #vehicleMedium}
	 * 
	 * @return	Integer holding the vehicle stock quantity of the medium unit of measurement.
	 */
	public int getVehicleMedium () {
		return vehicleMedium;
	}
	
	/**
	 * Setter - {@link #vehicleSmall}
	 * 
	 * @param vehicleSmall	Integer holding the vehicle stock quantity of the small unit of measurement.
	 */
	public void setVehicleSmall ( final int vehicleSmall ) {
		// Check if the vehicle stock quantity is valid
		if ( vehicleSmall >= 0 )
			// Set the vehicle stock quantity
			this.vehicleSmall = vehicleSmall;
	}
	
	/**
	 * Getter - {@link #vehicleSmall}
	 * 
	 * @return	Integer holding the vehicle stock quantity of the small unit of measurement.
	 */
	public int getVehicleSmall () {
		return vehicleSmall;
	}
	
	/**
	 * Setter - {@link #countedBig}
	 * 
	 * @param countedBig	Integer holding the counted quantity of the big unit of measurement.
	 */
	public void setCountedBig ( final int countedBig ) {
		// Check if the counted quantity is valid
		if ( countedBig >= 0 )
			// Set the counted quantity
			this.countedBig = countedBig;
	}
	
	/**
	 * Getter - {@link #countedBig}
	 * 
	 * @return	Integer holding the counted quantity of the big unit of measurement.
	 */
	public int getCountedBig () {
		return countedBig;
	}
	
	/**
	 * Setter - {@link #countedMedium}
	 * 
	 * @param countedMedium	Integer holding the counted quantity of the medium unit of measurement.
	 */
	public void setCountedMedium ( final int countedMedium ) {
		// Check if the counted quantity is valid
		if ( countedMedium >= 0 )
			// Set the counted quantity
			this.countedMedium = countedMedium;
	}
	
	/**
	 * Getter - {@link #countedMedium}
	 * 
	 * @return	Integer holding the counted quantity of the medium unit of measurement.
	 */
	public int getCountedMedium () {
		return countedMedium;
	}
	
	/**
	 * Setter - {@link #countedSmall}
	 * 
	 * @param quantitySmall	Integer holding the counted quantity of the small unit of measurement.
	 */
	public void setCountedSmall ( final int countedSmall ) {
		// Check if the counted quantity is valid
		if ( countedSmall >= 0 )
			// Set the counted quantity
			this.countedSmall = countedSmall;
	}
	
	/**
	 * Getter - {@link #countedSmall}
	 * 
	 * @return	Integer holding the counted quantity of the small unit of measurement.
	 */
	public int getCountedSmall () {
		return countedSmall;
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
	 * Setter - {@link #freeBig}
	 * 
	 * @param freeBig	Integer holding the free quantity of the big unit of measurement.
	 */
	public void setFreeBig ( final int freeBig ) {
		// Check if the free quantity is valid
		if ( freeBig >= 0 )
			// Set the free quantity
			this.freeBig = freeBig;
	}
	
	/**
	 * Getter - {@link #freeBig}
	 * 
	 * @return	Integer holding the free quantity of the big unit of measurement.
	 */
	public int getFreeBig () {
		return freeBig;
	}
	
	/**
	 * Setter - {@link #freeMedium}
	 * 
	 * @param freeMedium	Integer holding the free quantity of the medium unit of measurement.
	 */
	public void setFreeMedium ( final int freeMedium ) {
		// Check if the free quantity is valid
		if ( freeMedium >= 0 )
			// Set the free quantity
			this.freeMedium = freeMedium;
	}
	
	/**
	 * Getter - {@link #freeMedium}
	 * 
	 * @return	Integer holding the free quantity of the medium unit of measurement.
	 */
	public int getFreeMedium () {
		return freeMedium;
	}
	
	/**
	 * Setter - {@link #freeSmall}
	 * 
	 * @param freeSmall	Integer holding the free quantity of the small unit of measurement.
	 */
	public void setFreeSmall ( final int freeSmall ) {
		// Check if the free quantity is valid
		if ( freeSmall >= 0 )
			// Set the free quantity
			this.freeSmall = freeSmall;
	}
	
	/**
	 * Getter - {@link #freeSmall}
	 * 
	 * @return	Integer holding the free quantity of the small unit of measurement.
	 */
	public int getFreeSmall () {
		return freeSmall;
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
	 * Returns the big unit of measurement of the indicated state.  
	 * 
	 * @param state	Integer holding the required state.
	 * @return	Integer holding the big unit of measurement of the indicated state.
	 */
	public int getBig ( final int state ) {
		switch ( state ) {
		case STATE_DEFAULT:
			return quantityBig;
		case STATE_ORDERED:
			return orderedBig;
		case STATE_MISSED:
			return missedBig;
		case STATE_FREE:
			return freeBig;
		case STATE_BASKET:
			return basketBig;
		default:
			return 0;
		}
	}
	
	/**
	 * Returns the medium unit of measurement of the indicated state.  
	 * 
	 * @param state	Integer holding the required state.
	 * @return	Integer holding the medium unit of measurement of the indicated state.
	 */
	public int getMedium ( final int state ) {
		switch ( state ) {
		case STATE_DEFAULT:
			return quantityMedium;
		case STATE_ORDERED:
			return orderedMedium;
		case STATE_MISSED:
			return missedMedium;
		case STATE_FREE:
			return freeMedium;
		case STATE_BASKET:
			return basketMedium;
		default:
			return 0;
		}
	}
	
	/**
	 * Returns the small unit of measurement of the indicated state.  
	 * 
	 * @param state	Integer holding the required state.
	 * @return	Integer holding the small unit of measurement of the indicated state.
	 */
	public int getSmall ( final int state ) {
		switch ( state ) {
		case STATE_DEFAULT:
			return quantitySmall;
		case STATE_ORDERED:
			return orderedSmall;
		case STATE_MISSED:
			return missedSmall;
		case STATE_FREE:
			return freeSmall;
		case STATE_BASKET:
			return basketSmall;
		default:
			return 0;
		}
	}
	
	/**
	 * Determines if the current invoice has placed quantities of the provided state.
	 * 
	 * @param state	Integer holding the required state.
	 * @return	Boolean used to determine if the invoice has valid quantities.
	 */
	public boolean hasValidQuantities ( final int state ) {
		int big = getBig ( state );
		int medium = getMedium ( state );
		int small = getSmall ( state );
		return big != 0 || medium != 0 || small != 0;
	}
	
	/**
	 * Computes and returns the basic unit quantity of the indicated state.
	 * 
	 * @param state	Integer holding the required state.
	 * @return	Integer holding the basic unit quantity of the indicated state.
	 */
	public int getBasicUnitQuantity ( final int state ) {
		int big = getBig ( state );
		int medium = getMedium ( state );
		int small = getSmall ( state );
		return big * getItem ().getUnitBigMedium () * getItem ().getUnitMediumSmall ()
				+ medium * getItem ().getUnitMediumSmall ()
				+ small;
	}
	
	/**
	 * Computes and returns the total vehicle stock.
	 * 
	 * @return	Integer holding the total vehicle stock.
	 */
	public int getBasicVehicleStock () {
		return vehicleBig * getItem ().getUnitBigMedium () * getItem ().getUnitMediumSmall ()
				+ vehicleMedium * getItem ().getUnitMediumSmall ()
				+ vehicleSmall;
	}
	
	/**
	 * Computes and returns the total applied discount percentage of the indicated state.
	 * 
	 * @param state	Integer holding the required state.
	 * @return	Double holding the total applied discount percentage of the indicated state.
	 */
	public double getDiscountPercentage ( final int state ) {
		double total = 0.0;
		switch ( state ) {
		case STATE_DEFAULT:
			total = detailDiscountPercentage;
			break;
		case STATE_ORDERED:
			total = headerDiscountPercentage + detailDiscountPercentage + promotionDiscountPercentage;
			break;
		case STATE_MISSED:
			total = 0.0;
			break;
		case STATE_FREE:
			total = 100;
			break;
		case STATE_BASKET:
			total = promotionDiscountPercentageBasket;
			break;
		default:
			total = 0.0;
			break;
		}
		return total < 0 ? 0 : total > 100 ? 100 : total;
	}
	
	/**
	 * Computes and returns the total applied discount amount of the indicated state.
	 * 
	 * @param state	Integer holding the required state.
	 * @return	Double holding the total applied discount amount of the indicated state.
	 */
	public double getDiscountAmount ( final int state ) {
		return getTotalAmount ( state , false , false ) * getDiscountPercentage ( state ) / 100;
	}
	
	/**
	 * Computes and returns the total applied tax amount of the indicated state.
	 * 
	 * @param state	Integer holding the required state.
	 * @return	Double holding the total applied tax amount of the indicated state.
	 */
	public double getTaxAmount ( final int state ) {
		return getTotalAmount ( state , false , false ) * ( 100 + getTax () ) / 100;
	}
	
	/**
	 * Computes and returns the total amount of the indicated state.
	 * 
	 * @param state	Integer holding the required state.
	 * @param applyDiscount	Boolean indicating if the discount percentage should be applied.
	 * @param applyTax	Boolean indicating if the tax percentage should be applied.
	 * @return	Double holding the total amount of the indicated state.
	 */
	public double getTotalAmount ( final int state , final boolean applyDiscount , final boolean applyTax ) {
		int big = getBig ( state );
		int medium = getMedium ( state );
		int small = getSmall ( state );
		double total = big * priceBig + medium * priceMedium + small * priceSmall;
		if ( applyDiscount )
			return total - getDiscountAmount ( state );
		if ( applyTax )
			return total * ( 100 + getTax () ) / 100;
		return total;
	}
	
	/**
	 * Returns the allowed free quantity for the current invoice.
	 * 
	 * @return	Integer holding the allowed free quantity.
	 */
	public int getAllowedFreeQuantity () {
		// Compute total quantities
		int orderedQuantity = getBasicUnitQuantity ( STATE_DEFAULT );
		int basketQuantity =  getQuantityPromotedBasket ();
		int freeQuantity = getQuantityPromotedFree ();
		int vehicleQuantity = getBasicVehicleStock ();
		int totalQuantity = orderedQuantity + basketQuantity + freeQuantity;
		// Compute missed and ordered quantities
		int totalMissed = validateQuantity ( totalQuantity - vehicleQuantity );
		int tempTotalMissed = totalMissed;
		// Priority goes for : ordered, basket then free quantities
		return validateQuantity ( freeQuantity - tempTotalMissed );
	}
	
	/**
	 * Sets the reflected free quantity.<br>
	 * This method is used in the info request mode only, no calculations are made regarding promotions whatsoever.
	 * 
	 * @param freeQuantity	Integer holding the reflected free quantity.
	 */
	public void setReflectedFreeQuantity ( final int freeQuantity ) {
		// Calculate the big medium small conversations
		int mediumSmall = item.getUnitMediumSmall () <= 1 ? 1 : item.getUnitMediumSmall ();
		int bigSmall = item.getUnitBigMedium () <= 1 ? mediumSmall : item.getUnitBigMedium () * mediumSmall;
		// Compute free quantities
		freeBig = ItemsUtils.isBig ( item ) ? (int) ( freeQuantity / bigSmall ) : 0;
		freeMedium = ItemsUtils.isMedium ( item ) ? (int) ( ( freeQuantity - freeBig * bigSmall ) / mediumSmall ) : 0;
		freeSmall = (int) ( freeQuantity - freeBig * bigSmall - freeMedium * mediumSmall );
	}
	
	/**
	 * Clears the current detail discount percentage (the backup value is preserved).
	 */
	public void clearDetailDiscountPercentage () {
		detailDiscountPercentage = 0;
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
	 * Returns and validates the provided quantity.
	 * 
	 * @param	quantity	Integer holding the quantity to validate.
	 * @return	Integer holding the validated quantity.
	 */
	private int validateQuantity ( final int quantity ) {
		return quantity < 0 ? 0 : quantity;
	}
	
	/**
	 * Refreshes the invoice state.<br>
	 * The ordered and missed quantities are computed.
	 */
	public void refreshInvoiceState () {
		orderedBig = 0;
		orderedMedium = 0;
		orderedSmall = 0;
		freeBig = 0;
		freeMedium = 0;
		freeSmall = 0;
		basketBig = 0;
		basketMedium = 0;
		basketSmall = 0;
		missedBig = 0;
		missedMedium = 0;
		missedSmall = 0;
		// Calculate the big medium small conversations
		int mediumSmall = item.getUnitMediumSmall () <= 1 ? 1 : item.getUnitMediumSmall ();
		int bigSmall = item.getUnitBigMedium () <= 1 ? mediumSmall : item.getUnitBigMedium () * mediumSmall;
		// Compute total quantities
		int orderedQuantity = getBasicUnitQuantity ( STATE_DEFAULT );
		int basketQuantity =  getQuantityPromotedBasket ();
		int freeQuantity = getQuantityPromotedFree ();
		int vehicleQuantity = getBasicVehicleStock ();
		int totalQuantity = orderedQuantity + basketQuantity + freeQuantity;
		// Compute missed and ordered quantities
		int totalMissed = validateQuantity ( totalQuantity - vehicleQuantity );
		int tempTotalMissed = totalMissed;
		// Priority goes for : ordered, basket then free quantities
		int totalFree = validateQuantity ( freeQuantity - tempTotalMissed );
		tempTotalMissed = validateQuantity ( tempTotalMissed - freeQuantity );
		int totalBasket = validateQuantity ( basketQuantity - tempTotalMissed );
		tempTotalMissed = validateQuantity ( tempTotalMissed - basketQuantity );;
		int totalOrdered = validateQuantity ( orderedQuantity - tempTotalMissed );
		// Compute ordered quantities
		orderedBig = ItemsUtils.isBig ( item ) ? (int) ( totalOrdered / bigSmall ) : 0;
		orderedMedium = ItemsUtils.isMedium ( item ) ? (int) ( ( totalOrdered - orderedBig * bigSmall ) / mediumSmall ) : 0;
		orderedSmall = (int) ( totalOrdered - orderedBig * bigSmall - orderedMedium * mediumSmall );
		// Compute free quantities
		freeBig = ItemsUtils.isBig ( item ) ? (int) ( totalFree / bigSmall ) : 0;
		freeMedium = ItemsUtils.isMedium ( item ) ? (int) ( ( totalFree - freeBig * bigSmall ) / mediumSmall ) : 0;
		freeSmall = (int) ( totalFree - freeBig * bigSmall - freeMedium * mediumSmall );		
		// Compute basket quantities
		basketBig = ItemsUtils.isBig ( item ) ? totalBasket / bigSmall : 0;
		basketMedium = ItemsUtils.isMedium ( item ) ? ( totalBasket - basketBig * bigSmall ) / mediumSmall : 0;
		basketSmall = totalBasket - basketBig * bigSmall - basketMedium * mediumSmall;
		// Compute missed quantities
		missedBig = ItemsUtils.isBig ( item ) ? totalMissed / bigSmall : 0;
		missedMedium = ItemsUtils.isMedium ( item ) ? ( totalMissed - missedBig * bigSmall ) / mediumSmall : 0;
		missedSmall = totalMissed - missedBig * bigSmall - missedMedium * mediumSmall;
	}

	/**
	 * Constructor.
	 * 
	 * @param item	Reference to the item object.
	 * @param unit	Reference to the unit of measurement object.
	 */
	public Invoice ( final Items item , final Units unit ) {
		// Initialize the attributes
		this.item = item;
		this.unit = unit;
	}
	
}