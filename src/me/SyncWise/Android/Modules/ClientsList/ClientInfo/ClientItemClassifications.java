/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.ClientsList.ClientInfo;

import java.io.Serializable;

/**
 * Class used to represent a client item classification.
 * 
 * @author Elias
 *
 */
public class ClientItemClassifications implements Serializable {

	/**
	 * Default serial version ID.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * String holding the item code.
	 */
	private final String itemCode;
	
	/**
	 * String holding the item name.
	 */
	private final String itemName;
	
	/**
	 * String holding the client item classification level code.
	 */
	private final String clientItemClassificationLevelCode;
	
	/**
	 * String holding the client item classification level description.
	 */
	private final String clientItemClassificationLevelDescription;
	
	/**
	 * Constructor.
	 * 
	 * @param itemCode	String holding the classified item code.
	 * @param itemName	String holding the classified item name.
	 * @param clientItemClassificationLevelCode	String holding the client item classification level code.
	 * @param clientItemClassificationLevelDescription	String holding the client item classification level description.
	 */
	public ClientItemClassifications ( final String itemCode , final String itemName ,
			final String clientItemClassificationLevelCode , final String clientItemClassificationLevelDescription ) {
		this.itemCode = itemCode;
		this.itemName = itemName;
		this.clientItemClassificationLevelCode = clientItemClassificationLevelCode;
		this.clientItemClassificationLevelDescription = clientItemClassificationLevelDescription;
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
		// Check if the object is valid and instance of ClientItemClassifications
		if ( object != null && object instanceof ClientItemClassifications )
			// Compare objects
			isEqual = itemCode.equals ( ( (ClientItemClassifications) object ).getItemCode () )
				&& clientItemClassificationLevelCode.equals ( ( (ClientItemClassifications) object ).getClientItemClassificationLevelCode () );
		// Return flag
		return isEqual;
	}
	
	/*
	 * Returns an integer hash code for this object
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode () {
		final int prime = 31;
		int result = 1;
		result = prime * result + ( ( clientItemClassificationLevelCode == null ) ? 0 : clientItemClassificationLevelCode.hashCode () );
		result = prime * result + ( ( itemCode == null ) ? 0 : itemCode.hashCode () );
		return result;
	}
	
	/**
	 * Getter - {@link #itemCode}
	 * 
	 * @return String holding the item code.
	 */
	public String getItemCode () {
		return itemCode;
	}
	
	/**
	 * Getter - {@link #itemName}
	 * 
	 * @return String holding the item name.
	 */
	public String getItemName () {
		return itemName;
	}
	
	/**
	 * Getter - {@link #clientItemClassificationLevelCode}
	 * 
	 * @return String holding the client item classification level code.
	 */
	public String getClientItemClassificationLevelCode () {
		return clientItemClassificationLevelCode;
	}
	
	/**
	 * Getter - {@link #clientItemClassificationLevelDescription}
	 * 
	 * @return String holding the client item classification level description.
	 */
	public String getClientItemClassificationLevelDescription () {
		return clientItemClassificationLevelDescription;
	}
	
}