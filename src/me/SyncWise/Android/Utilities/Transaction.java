/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Utilities;

/**
 * Utility class that provides helper methods related to Transactions.
 * 
 * @author Elias
 *
 */
public class Transaction {
	
	/**
	 * String holding the default value used to indicate whether or not to enforce the must stock list items confirmation.
	 */
	public static final String DEFAULT_ENFORCE_MSL_CONFIRMATION = "Y";
	
	/**
	 * String holding the default value used to indicate whether or not to enable promotions in the sales order taking.
	 */
	public static final String DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS = "N";
	/**
	 * String holding the default value used to indicate whether or not to enable Direct Return .
	 */
	public static final String ENABLE_Direct_Return = "N";
	/**
	 * String holding the default value used to indicate whether or not to enable Sales Order Cash Or Credit .
	 */
	public static final String Enable_Sales_Order_Cash_Or_Credit="N";
	/**
	 * String holding the default value used to indicate whether or not to allow an multiple Collection.
	 */
	public static final String DEFAULT_ALLOW_MULTIPLE_COLLECTION = "N";
	
	/**
	 * Integer holding the default value used to as RFR limit amount.
	 */
	public static final Integer DEFAULT_RFR_LIMIT_AMOUNT = 0;
	
	/**
	 * Integer holding the default value used to as RFR limit margin.
	 */
	public static final Integer DEFAULT_RFR_LIMIT_MARGIN = 0;
	
	/**
	 * Integer holding the default minimum RFR expiry date period.
	 */
	public static final Integer DEFAULT_MIN_RFR_EXPIRY_DATE = 180;
	
}