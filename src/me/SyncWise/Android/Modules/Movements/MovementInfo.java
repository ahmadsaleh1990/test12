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

/**
 * Class used to represent a movement detail.
 * 
 * @author Elias
 *
 */
public class MovementInfo implements Serializable {

	/**
	 * Helper class used to define the various values of a movement info ID.
	 * 
	 * @author Elias
	 *
	 */
	public static class ID {
		public static final int DATE = 1;
		public static final int CURRENCY = 3;
		public static final int GROSS_VALUE = 4;
		public static final int NET_VALUE = 6;
		public static final int TAXES = 7;
		public static final int TOTAL_VALUE = 8;
		public static final int VAN_TYPE = 9;
	}
	
	/**
	 * Default serial version ID.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Integer holding the movement info ID.
	 */
	private final int Id;
	
	/**
	 * String holding the movement detail description.
	 */
	private final String description;
	
	/**
	 * String holding the movement detail value.
	 */
	private String value;
	
	/**
	 * Getter - {@link #Id}
	 * 
	 * @return	Integer holding the movement info ID.
	 */
	public int getId () {
		return Id;
	}
	
	/**
	 * Getter - {@link #description}
	 * 
	 * @return	String holding the movement detail description.
	 */
	public String getDescription () {
		return ( description == null ? "" : description );
	}
	
	/**
	 * Setter - {@link #value}
	 * 
	 * @param	String holding the movement detail value.
	 */
	public void setValue ( final String value ) {
		this.value = value;
	}
	
	/**
	 * Getter - {@link #value}
	 * 
	 * @return	String holding the movement detail value.
	 */
	public String getValue () {
		return ( value == null ? "" : value );
	}
	
	/**
	 * Constructor
	 * 
	 * @param Id	Integer holding the movement info ID.
	 * @param description	String holding the movement detail description.
	 * @param value	String holding the movement detail value.
	 */
	public MovementInfo ( final int Id , final String description , final String value ) {
		// Initialize attributes
		this.Id = Id;
		this.description = description;
		this.value = value;
	}
	
}