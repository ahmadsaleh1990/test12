/**
 * Copyright 2014 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.ClientStock;

import java.io.Serializable;

/**
 * Class used to represent a merchandising picture.
 * 
 * @author Elias
 *
 */
public class Picture implements Serializable {
	
	/**
	 * Default serial version ID.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * String used to host the picture transaction code.
	 */
	private final String transactionCode;
	
	/**
	 * String used to host the picture item code.
	 */
	private final String itemCode;
	
	/**
	 * Integer used to host the picture line ID.
	 */
	private final int lineID;
	
	/**
	 * String used to host the picture path on disk.
	 */
	private final String path;
	
	/**
	 * String used to host the temporary picture path on disk.
	 */
	private final String tempPath;
	
	/**
	 * Constructor.
	 * 
	 * @param code	String used to host the picture code.
	 * @param itemCode	String used to host the picture item code.
	 * @param lineID	Integer used to host the picture line ID.
	 * @param path	String used to host the picture path on disk.
	 * @param tempPath	String used to host the temporary picture path on disk.
	 */
	public Picture ( final String transactionCode , final String itemCode , final int lineID , final String path , final String tempPath ) {
		// Set the picture id and path
		this.transactionCode = transactionCode;
		this.itemCode = itemCode;
		this.lineID = lineID;
		this.path = path;
		this.tempPath = tempPath;
	}
	
	/**
	 * Getter - {@link #transactionCode}
	 * 
	 * @return	String used to host the picture transaction code.
	 */
	public String getTransactionCode () {
		return transactionCode;
	}
	
	/**
	 * Getter - {@link #itemCode}
	 * 
	 * @return	String used to host the picture item code.
	 */
	public String getItemCode () {
		return itemCode;
	}
	
	/**
	 * Getter - {@link #lineID}
	 * 
	 * @return	Integer used to host the picture line ID.
	 */
	public int getLineID () {
		return lineID;
	}
	
	/**
	 * Getter - {@link #path}
	 * 
	 * @return	String used to host the picture path on disk.
	 */
	public String getPath () {
		return path;
	}
	
	/**
	 * Getter - {@link #tempPath}
	 * 
	 * @return	String used to host the temporary picture path on disk.
	 */
	public String getTempPath () {
		return tempPath;
	}
	
}