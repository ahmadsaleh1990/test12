package me.SyncWise.Android.Modules.DirectReturn;

import java.io.Serializable;

public class OrderDirectReturnInfo  implements Serializable {

	/**
	 * Helper class used to define the various values of an order info ID.
	 * 
	 * @author ahmad
	 *
	 */
	public static class ID {
		public static final int DELIVERY_DATE = 1;
		public static final int NOTE = 2;
		public static final int CLIENT_REFERENCE_NUMBER = 3;
		public static final int CURRENCY = 4;
		public static final int GROSS_VALUE = 5;
		public static final int TAXES = 6;
		public static final int TOTAL_VALUE = 7;
	}
	
	/**
	 * Default serial version ID.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Integer holding the order info ID.
	 */
	private final int Id;
	
	/**
	 * String holding the sales order detail description.
	 */
	private final String description;
	
	/**
	 * String holding the sales order detail value.
	 */
	private String value;
	
	/**
	 * Getter - {@link #Id}
	 * 
	 * @return	Integer holding the order info ID.
	 */
	public int getId () {
		return Id;
	}
	
	/**
	 * Getter - {@link #description}
	 * 
	 * @return	String holding the sales order detail description.
	 */
	public String getDescription () {
		return ( description == null ? "" : description );
	}
	
	/**
	 * Setter - {@link #value}
	 * 
	 * @param	String holding the sales order detail value.
	 */
	public void setValue ( final String value ) {
		this.value = value;
	}
	
	/**
	 * Getter - {@link #value}
	 * 
	 * @return	String holding the sales order detail value.
	 */
	public String getValue () {
		return ( value == null ? "" : value );
	}
	
	/**
	 * Constructor
	 * 
	 * @param Id	Integer holding the order info ID.
	 * @param description	String holding the sales order detail description.
	 * @param value	String holding the sales order detail value.
	 */
	public OrderDirectReturnInfo ( final int Id , final String description , final String value ) {
		// Initialize attributes
		this.Id = Id;
		this.description = description;
		this.value = value;
	}
}
