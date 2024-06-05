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

import java.io.Serializable;

/**
 * Utility class that represents a {@link android.support.v4.app.Fragment Fragment}, providing a description of a fragment.<br>
 * This class is mainly used to describe fragment details and transfer them between activities, adapters, ...
 * 
 * @author Elias
 *
 */
public class AppFragment implements Serializable {

	/**
	 * Default serial version ID.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * String holding the fragment title.
	 */
	private final String title;
	
	/**
	 * Integer holding the layout resource id.
	 */
	private final int layout;
	
	/**
	 * Constructor.
	 * 
	 * @param title	String holding the fragment title.
	 * @param layout	Integer holding the layout resource id.
	 */
	public AppFragment ( final String title , final int layout ) {
		// Initialize attributes
		this.title = title;
		this.layout = layout;
	}
	
	/**
	 * Getter - {@link #title}
	 * 
	 * @return	String holding the fragment title.
	 */
	public String getTitle () {
		// Return the title
		return title;
	}
	
	/**
	 * Getter - {@link #layout}
	 * 
	 * @return	Integer holding the layout resource id.
	 */
	public int getLayout () {
		// Return the layout resource id
		return layout;
	}
	
}