/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.SalesSummary;

import java.io.Serializable;

/**
 * Class used to represent a client summary action.
 * 
 * @author Elias
 *
 */
public class Summary2 implements Serializable {

	/**
	 * Helper class used to define the various values of a summary ID.
	 * 
	 * @author Elias
	 *
	 */
	public static class ID {
		public static final int SALES_ORDERS = 1;
		//public static final int SALES_RFR = 2;
		public static final int SALES_INVOICE = 3;
		//public static final int SALES_RETURN = 4;
		//public static final int CLIENT_STOCK_COUNT = 5;
		//public static final int CLIENT_ASSET = 6;
		//public static final int COLLECTION = 7;
		//public static final int DIRECT_RETURN = 8;
	}
	
	/**
	 * Default serial version ID.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Integer holding the summary ID.
	 */
	private final int Id;
	
	/**
	 * String holding the summary description.
	 */
	private final String description;
	
	/**
	 * Getter - {@link #Id}
	 * 
	 * @return	Integer holding the summary ID.
	 */
	public int getId () {
		return Id;
	}
	
	/**
	 * Getter - {@link #description}
	 * 
	 * @return	String holding the summary description.
	 */
	public String getDescription () {
		return ( description == null ? "" : description );
	}
	
	/**
	 * Constructor
	 * 
	 * @param Id	Integer holding the summary ID.
	 * @param description	String holding the summary description.
	 */
	public Summary2 ( final int Id , final String description ) {
		// Initialize attributes
		this.Id = Id;
		this.description = description;
	}
	
}