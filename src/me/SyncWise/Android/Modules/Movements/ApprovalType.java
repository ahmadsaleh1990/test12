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


public class ApprovalType {

	public static final int TYPE_USER = 1;
	
	public static final int TYPE_BARCODE = 2;
	
	private final int type;
	
	private final String description;

	
	/**
	 * Getter - {@link #type}
	 * 
	 * @return the type
	 */
	public int getType () {
		return type;
	}

	
	/**
	 * Getter - {@link #description}
	 * 
	 * @return the description
	 */
	public String getDescription () {
		return description;
	}

	/**
	 * Constructor.
	 * 
	 * @param type
	 * @param description
	 */
	public ApprovalType ( int type , String description ) {
		super ();
		this.type = type;
		this.description = description;
	}
	
}
