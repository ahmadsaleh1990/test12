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
 * Class used to represent a client information.
 * 
 * @author Elias
 *
 */
public class ClientInfo implements Serializable {

	/**
	 * Helper class used to define the various values of a client info ID.
	 * 
	 * @author Elias
	 *
	 */
	public static class ID {
		public static final int STATUS = 1;
		public static final int PHONE = 2;
		public static final int EMAIL = 3;
		public static final int PARENT_CLIENT = 4;
		public static final int VAT_NUMBER = 5;
	}
	
	/**
	 * Default serial version ID.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Integer holding the client info ID.
	 */
	private final int Id;
	
	/**
	 * String holding the client info description.
	 */
	private final String description;
	
	/**
	 * String holding the client info value.
	 */
	private String value;
	
	/**
	 * Getter - {@link #Id}
	 * 
	 * @return	Integer holding the client info ID.
	 */
	public int getId () {
		return Id;
	}
	
	/**
	 * Getter - {@link #description}
	 * 
	 * @return	 String holding the client info description.
	 */
	public String getDescription () {
		return ( description == null ? "" : description );
	}
	
	/**
	 * Setter - {@link #value}
	 * 
	 * @param value	String holding the client info value.
	 */
	public void setValue ( final String value ) {
		this.value = value;
	}
	
	/**
	 * Getter - {@link #value}
	 * 
	 * @return	String holding the client info value.
	 */
	public String getValue () {
		return ( value == null ? "" : value );
	}
	
	/**
	 * Constructor
	 * 
	 * @param Id	Integer holding the order info ID.
	 * @param description	String holding the client info description.
	 * @param value	String holding the client info value.
	 */
	public ClientInfo ( final int Id , final String description , final String value ) {
		// Initialize attributes
		this.Id = Id;
		this.description = description;
		this.value = value;
	}
	
	/**
	 * Constructor
	 * 
	 * @param description	String holding the client info description.
	 * @param value	String holding the client info value.
	 */
	public ClientInfo ( final String description , final String value ) {
		// Overloaded constructor
		this ( 0 , description , value );
	}
	
}