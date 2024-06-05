/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.SalesOrder;

import java.io.Serializable;
import java.util.ArrayList;

import me.SyncWise.Android.Database.Items;
import me.SyncWise.Android.Database.ItemsUtils;
import me.SyncWise.Android.Database.PromotionUtils;
import me.SyncWise.Android.Database.TransactionPromotionDetails;
import me.SyncWise.Android.Database.Units;

/**
 * Class used to represent sales orders.
 * 
 * @author Elias
 *
 */
public class Order implements Serializable {

	/**
	 * Default serial version ID.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Reference to the item object.
	 */
	private final Items item;
	private int quantityPromotedFree;
	private double caseTaxAmount;
    private String reasonCode;
    private int reasonPos;
	public int getReasonPos() {
		return reasonPos;
	}
	public void setReasonPos(int reasonPos) {
		this.reasonPos = reasonPos;
	}
	
	
	public String getReasonCode() {
		return reasonCode;
	}
	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}
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
	public void setQuantityPromotedFree ( final int quantityPromotedFree ) {
		// Check if the promoted free quantity is valid
		if ( quantityPromotedFree >= 0 )
			// Set the percentage
			this.quantityPromotedFree = quantityPromotedFree;
	}private double promotionDiscountPercentage;
	private String promotionID;
	public String getPromotionID() {
		return promotionID;
	}
	public void setPromotionID(String promotionID) {
		this.promotionID = promotionID;
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
	public void clearInstantPromotions () { 
		promotionDiscountPercentage = 0;
		quantityPromotedFree = 0;
	}
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
	 * Reference to the unit of measurement object.
	 */
	private final Units unit;
	
	/**
	 * Boolean indicating if the current item belongs to the must stock list.
	 */
	private boolean isNewSKUList;
	private boolean isMustStockList;
	private boolean isForcedMustStockList;
	public boolean isForcedMustStockList() {
		return isForcedMustStockList;
	}
	public void setForcedMustStockList(boolean isForcedMustStockList) {
		this.isForcedMustStockList = isForcedMustStockList;
	}
	private boolean isForcedNewItemList;
	public boolean isForcedNewItemList() {
		return isForcedNewItemList;
	}
	public void setForcedNewItemList(boolean isForcedNewItemList) {
		this.isForcedNewItemList = isForcedNewItemList;
	}
	/**
	 * Boolean indicating if the current item which belongs to the must stock list is confirmed.
	 */
	private boolean isConfirmed;
	
	/**
	 * Boolean indicating if the current item is previously ordered.
	 */
	private boolean isHistory;
	
	//Ahmad 
   private String clientItemCode;
   private String itemBarcodes;
  
   /**
	 * Integer holding the suggested quantity of the big unit of measurement.
	 */
	private int suggestedBig;
	private String lastChar;
	public String getLastChar() {
		return lastChar;
	}

	public void setLastChar(String lastChar) {
		this.lastChar = lastChar;
	}

	/**
	 * Integer holding the suggested quantity of the big unit of measurement.
	 */
	private int suggestedMedium;

	/**
	 * Integer holding the suggested quantity of the big unit of measurement.
	 */
	private int suggestedSmall;

	public int getSuggestedBig() {
		return suggestedBig;
	}

	public void setSuggestedBig(int suggestedBig) {
		this.suggestedBig = suggestedBig;
	}

	public int getSuggestedMedium() {
		return suggestedMedium;
	}

	public void setSuggestedMedium(int suggestedMedium) {
		this.suggestedMedium = suggestedMedium;
	}

	public int getSuggestedSmall() {
		return suggestedSmall;
	}

	public void setSuggestedSmall(int suggestedSmall) {
		this.suggestedSmall = suggestedSmall;
	}

	public String getClientItemCode() {
	return clientItemCode;
   }

   public void setClientItemCode(String clientItemCode) {
	this.clientItemCode = clientItemCode;
   }

  public String getItemBarcodes() {
	return itemBarcodes;
  }

  public void setItemBarcodes(String itemBarcodes) {
	this.itemBarcodes = itemBarcodes;
  }

	//Ahmad  max Quantity Free Case from promotion type limited
	private double maxQuantityFreeCase;

	private double maxQuantityFreeUnit;
	public double getMaxQuantityFreeUnit() {
		return maxQuantityFreeUnit;
	}

	public void setMaxQuantityFreeUnit(double maxQuantityFreeUnit) {
		this.maxQuantityFreeUnit = maxQuantityFreeUnit;
	}

	public double getMaxQuantityFreeCase() {
		return maxQuantityFreeCase;
	}

	public void setMaxQuantityFreeCase(double maxQuantityFreeCase) {
		this.maxQuantityFreeCase = maxQuantityFreeCase;
	}

	/**
	 * Boolean indicating if the current item is previously ordered.
	 */
	private boolean isMax;
	 
	public boolean isMax() {
		return isMax;
	}

	public void setMax(boolean isMax) {
		this.isMax = isMax;
	}

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
	 * Integer holding the quantity of the big unit of measurement.
	 */
	private int quantityBig;
	
	/**
	 * Integer holding the counted quantity of the big unit of measurement.
	 */
	private int countedBig;
	
	/**
	 * Integer holding the quantity of the medium unit of measurement.
	 */
	private int quantityMedium;
	private int sequence ;
	/**
	 * Integer holding the counted quantity of the big unit of measurement.
	 */
	private int countedMedium;
	
	/**
	 * Integer holding the quantity of the small unit of measurement.
	 */
	private int quantitySmall;
	
	/**
	 * Integer holding the counted quantity of the big unit of measurement.
	 */
	private int countedSmall;
	
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
	 * Integer holding the quantity of the free unit of measurement.
	 */
	private int quantityFree;
	
	
	
	/**
	 * Integer holding the quantity of the free case  unit of measurement.
	 */
	private int quantityFreeCase;
	
	 

	/**
	 * detailDiscountPercentage	Double holding the discount percentage provided by the user over the ordered item.
	 */
	private double detailDiscountPercentage;
	
	/**
	 * Reference to the transactionPromotionDetails object.
	 */
	private ArrayList <TransactionPromotionDetails> transactionPromotionDetails;
	
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
public Boolean availability;
public Boolean appFree;

	public Boolean getAppFree() {
	return appFree;
}

public void setAppFree(Boolean appFree) {
	this.appFree = appFree;
}

	public void setAvailability ( Integer availability ) {
		if ( availability == null )
			this.availability = null;
		else if ( availability.equals ( 1 ) )
			this.availability = true;
		else if ( availability.equals ( 0 ) )
			this.availability = false;
	}
	
	public Integer getAvailability () {
		if ( availability == null )
			return null;
		if ( availability )
			return 1;
		return 0;
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
	public boolean isNewSKUList () {
		return isNewSKUList;
	}

 
	public void setNewSKUList ( final boolean isNewSKUList ) {
		this.isNewSKUList = isNewSKUList;
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
	
	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
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
	 * @return the quantityFree
	 */
	public int getQuantityFree() {
		int tempQuantity = quantityFree  ;
	 
		return tempQuantity;
			
	}
	 
	public int getQuantityFreeAdded(){
		return quantityFree;
	}

	/**
	 * @param quantityFree the quantityFree to set
	 */
	public void setQuantityFree(int quantityFree) {
		this.quantityFree = quantityFree;
	}
	/**
	 * @param quantityFreeCase the quantityFreeCase to set
	 */
	public void setQuantityFreeCase(int quantityFreeCase) {
		this.quantityFreeCase = quantityFreeCase;
	}

	public int getQuantityFreeCase() {
		return quantityFreeCase;
	}

	

	/**
	 * @return the parentLineID
	 */
	public int getParentLineID() {
		if( !this.transactionPromotionDetails.isEmpty() )
			return transactionPromotionDetails.get( 0 ).getParentLineID();
		return -1;
	}
	
	/**
	 * Setter - {@link #detailDiscountPercentage}
	 * 
	 * @param detailDiscountPercentage	Double holding the discount percentage provided by the user over the ordered item.
	 */
	public void setDetailDiscountPercentage ( final double detailDiscountPercentage ) {
		// Check if the percentage is valid
		if ( detailDiscountPercentage >= 0 && detailDiscountPercentage <= 100 )
			// Set the percentage
			this.detailDiscountPercentage = detailDiscountPercentage;
	}
	
	/**
	 * Getter - {@link #headerDiscountPercentage}
	 * 
	 * @return Double holding the discount percentage provided by the user over the ordered item.
	 */
	public double getDetailDiscountPercentage () {
		// Return the percentage
		return detailDiscountPercentage;
	}

	/**
	 * @return the discountPercentage
	 */
	public double getDiscountPercentage () {
		double temppercentage = detailDiscountPercentage;
		 
		return temppercentage>100?100:temppercentage;
	}

	/**
	 * @return the discountAmount
	 */
	public double getDiscountAmount () {
		double tempamount = 0;
 
		return tempamount;
	}

	/**
	 * @return the transactionPromotionDetails
	 */
	public ArrayList<TransactionPromotionDetails> getTransactionPromotionDetails() {
		return transactionPromotionDetails;
	}

	/**
	 * @param transactionPromotionDetails the transactionPromotionDetails to set
	 */
	public void setTransactionPromotionDetails(
			ArrayList<TransactionPromotionDetails> transactionPromotionDetails) {
			this.transactionPromotionDetails = transactionPromotionDetails;
	}
	
	/**
	 * @param transactionPromotionDetails the transactionPromotionDetails to set one item
	 */
	public void setTransactionPromotionDetails( TransactionPromotionDetails transactionPromotionDetails) {
		this.transactionPromotionDetails.add( transactionPromotionDetails );
	}
	
	/**
	 * @param transactionPromotionDetails the transactionPromotionDetails to set one item
	 */
	public void setOrUpdateTransactionPromotionDetails( TransactionPromotionDetails transactionPromotionDetails) {
		boolean flag = false;
		for ( int i = 0 ;i<this.transactionPromotionDetails.size () ; i ++ ) {
			if ( transactionPromotionDetails.getPromotionID ().equals ( this.transactionPromotionDetails.get ( i ).getPromotionID () )
					&& transactionPromotionDetails.getPromotionType().equals ( this.transactionPromotionDetails.get ( i ).getPromotionType () ) ){
				 this.transactionPromotionDetails.get( i ).setDiscountAmount(transactionPromotionDetails.getDiscountAmount());
				 this.transactionPromotionDetails.get( i ).setDiscountPercentage(transactionPromotionDetails.getDiscountPercentage());
				 this.transactionPromotionDetails.get( i ).setItemCodeOffered(transactionPromotionDetails.getItemCodeOffered());
				 this.transactionPromotionDetails.get( i ).setItemCodeOrdered(transactionPromotionDetails.getItemCodeOrdered());
				 this.transactionPromotionDetails.get( i ).setQuantityOffered(transactionPromotionDetails.getQuantityOffered());
				 this.transactionPromotionDetails.get( i ).setQuantityOrdered(transactionPromotionDetails.getQuantityOrdered());
				 this.transactionPromotionDetails.get( i ).setTotalLineAmount(transactionPromotionDetails.getTotalLineAmount());
				 flag = true;
				 break;
			}
		}
		if(!flag)
			setTransactionPromotionDetails(transactionPromotionDetails);
	}

	/**
	 * Constructor.
	 * 
	 * @param item	Reference to the item object.
	 * @param unit	Reference to the unit of measurement object.
	 */
	public Order ( final Items item , final Units unit ) {
		// Initialize the attributes
		this.item = item;
		this.unit = unit;
		this.transactionPromotionDetails = new ArrayList<TransactionPromotionDetails>();
	}
	private int freeBig;

	/**
	 * Integer holding the free quantity of the big unit of measurement.
	 */
	private int freeMedium;
	private int basketBig;

	/**
	 * Integer holding the basket quantity of the big unit of measurement.
	 */
	private int basketMedium;

	/**
	 * Integer holding the basket quantity of the big unit of measurement.
	 */
	private int basketSmall;
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
	 * Integer holding the free quantity of the big unit of measurement.
	 */
	private int freeSmall;
	public void clearTransactionPromotionDetails(){
		this.transactionPromotionDetails.clear();
	}
	public void refreshInvoiceState () {
	 
		freeBig = 0;
		freeMedium = 0;
		freeSmall = 0;
		basketBig = 0;
		basketMedium = 0;
		basketSmall = 0;
		 
		// Calculate the big medium small conversations
		int mediumSmall = item.getUnitMediumSmall () <= 1 ? 1 : item.getUnitMediumSmall ();
		int bigSmall = item.getUnitBigMedium () <= 1 ? mediumSmall : item.getUnitBigMedium () * mediumSmall;
		// Compute total quantities
		 
	 	int basketQuantity =  getQuantityPromotedBasket ();
		int freeQuantity = getQuantityPromotedFree ();
	 
		int totalBasket = basketQuantity;
		 	// Priority goes for : ordered, basket then free quantities
		int totalFree =  freeQuantity ;
	 
		// Compute ordered quantities
	 
		freeBig = ItemsUtils.isBig ( item ) ? (int) ( totalFree / bigSmall ) : 0;
		freeMedium =  ItemsUtils.isMedium ( item ) ? (int) ( ( totalFree - freeBig * bigSmall ) / mediumSmall ) : 0;
		freeSmall =  (int) ( totalFree - freeBig * bigSmall - freeMedium * mediumSmall );	
	 
		// Compute basket quantities
		basketBig = ItemsUtils.isBig ( item ) ? totalBasket / bigSmall : 0;
		basketMedium =  ItemsUtils.isMedium ( item ) ? ( totalBasket - basketBig * bigSmall ) / mediumSmall : 0;
		basketSmall =  totalBasket - basketBig * bigSmall - basketMedium * mediumSmall;
		 
	 	}	public void clearBasketPromotions () { 
			promotionDiscountPercentageBasket = 0;
			quantityPromotedBasket = 0;
		}
	private double promotionDiscountPercentageBasket;
	public void setPromotionDiscountPercentageBasket ( final double promotionDiscountPercentageBasket ) {
		// Check if the percentage is valid
		if ( promotionDiscountPercentageBasket >= 0 && promotionDiscountPercentageBasket <= 100 )
			// Set the percentage
			this.promotionDiscountPercentageBasket = promotionDiscountPercentageBasket;
	}
	
	private double basketPriceBig;
		
		/**
	 * Double holding the unit price of the medium unit of measurement.
	 */
	private double basketPriceMedium;
	
	/**
	 * Double holding the unit price of the small unit of measurement.
	 */
	private double basketPriceSmall;
	
	public double getBasketPriceBig() {
		return basketPriceBig;
	}
	
	public void setBasketPriceBig(double basketPriceBig) {
		this.basketPriceBig = basketPriceBig;
	}
	
	public double getBasketPriceMedium() {
		return basketPriceMedium;
	}
	
	public void setBasketPriceMedium(double basketPriceMedium) {
		this.basketPriceMedium = basketPriceMedium;
	}
	
	public double getBasketPriceSmall() {
		return basketPriceSmall;
	}
	
	public void setBasketPriceSmall(double basketPriceSmall) {
		this.basketPriceSmall = basketPriceSmall;
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
	private int quantityBasket;
	public void setQuantityBasket ( final int quantityBasket ) {
		// Check if the promoted basket quantity is valid
		if ( quantityBasket >= 0 )
			// Set the percentage
			this.quantityBasket = quantityBasket;
	}
	
	/**
	 * Getter - {@link #quantityPromotedBasket}
	 * 
	 * @return Integer holding the basic unit quantity provided by promotions that are baskets.
	 */
	public int getQuantityBasket () {
		// Return the quantity
		return quantityBasket;
	}
	private int quantityPromotedBasket;
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
	public void clearDetailDiscountPercentage () {
		detailDiscountPercentage = 0;
	}
}