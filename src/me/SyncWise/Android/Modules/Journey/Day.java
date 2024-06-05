/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Journey;

import java.io.Serializable;

/**
 * Class used to represent a journey day.
 * 
 * @author Elias
 *
 */
public class Day implements Serializable {
	
	/**
	 * Default serial version ID.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * String holding the day's abbreviation.
	 */
	private final String day;
	
	/**
	 * String holding the day's label.
	 */
	private final String label;
	
	/**
	 * Boolean indicating if the current day object represents today.
	 */
	private boolean today;
	
	/**
	 * Getter - {@link #day}
	 * 
	 * @return	String holding the day's abbreviation.
	 */
	public String getDay () {
		return day;
	}
	
	/**
	 * Getter - {@link #label}
	 * 
	 * @return	String holding the day's label.
	 */
	public String getLabel () {
		return label;
	}
	
	/**
	 * Setter - {@link #today}
	 * 
	 * @param today	Boolean indicating if the current day object represents today.
	 */
	public void setToday ( final boolean today ) {
		this.today = today;
	}
	
	/**
	 * {@link #today}
	 * 
	 * @return	Boolean indicating if the current day object represents today.
	 */
	public boolean isToday () {
		return today;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param day	String holding the day's abbreviation.
	 * @param label	String holding the day's label.
	 */
	public Day ( final String day , final String label ) {
		this.day = day;
		this.label = label;
	}
	
}