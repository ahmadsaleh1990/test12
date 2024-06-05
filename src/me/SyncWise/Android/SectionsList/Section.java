/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.SectionsList;

/**
 * Class used to represent a section (separator) in a list view.<br>
 * The section is mainly used in the {@link me.SyncWise.Android.SectionsList.MultipleListAdapter SectionsListAdapter} and is internally handled by {@link me.SyncWise.Android.SectionsList.SectionAdapter SectionAdapter}.
 * 
 * @author Elias
 *
 */
public class Section {
	
	/**
	 * Enumeration used to define section background type.
	 * 
	 * @author Elias
	 *
	 */
	public static enum BackgroundType {
		COLOR ,
		DRAWABLE
	}
	
	/**
	 * String hosting the section name.
	 */
	private final String name;
	
	/**
	 * Integer hosting the section's text color.
	 */
	private final Integer textColor;
	
	/**
	 * Integer hosting the section's background color.
	 */
	private final Integer backgroundColor;
	
	/**
	 * Integer hosting the section's background resource ID.
	 */
	private final Integer backgroundResourceID;
	
	/**
	 * Integer hosting the section's icon resource ID.
	 */
	private final Integer iconResourceID;
	
	/**
	 * Getter - {@link #name}
	 * 
	 * @return	String hosting the section name.
	 */
	public String getName () {
		return name;
	}
	
	/**
	 * Getter - {@link #textColor}
	 * 
	 * @return	Integer hosting the section's text color.
	 */
	public Integer getTextColor () {
		// Return the text color
		return textColor;
	}
	
	/**
	 * Getter - {@link #backgroundColor}
	 * 
	 * @return	Integer hosting the section's background color.
	 */
	public Integer getBackgroundColor () {
		// Return the background color
		return backgroundColor;
	}
	
	/**
	 * Getter - {@link #backgroundResourceID}
	 * 
	 * @return	Integer hosting the section's background resource ID.
	 */
	public Integer getBackgroundResourceID () {
		// Return the background resource ID
		return backgroundResourceID;
	}
	
	/**
	 * Getter - {@link #iconResourceID}
	 * 
	 * @return	Integer hosting the section's icon resource ID.
	 */
	public Integer getIconResourceID () {
		// Return the icon resource ID
		return iconResourceID;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param name	String hosting the section name.
	 * @param textColor	Integer hosting the section's text color.
	 * @param backgroundType	An enumeration constant of {@link me.SyncWise.Android.SectionsList.Section.BackgroundType BackgroundType} indicating the section's background type.
	 * @param background	Integer hosting the section's background.
	 * @param icon	Integer hosting the section's icon.
	 */
	public Section ( final String name , final Integer textColor , final BackgroundType backgroundType , final Integer background , final Integer icon ) {
		// Initialize attributes
		this.name = ( name == null ? "" : name );
		this.textColor = textColor;
		this.iconResourceID = icon;
		// Determine the background type
		switch ( backgroundType ) {
		case COLOR:
			// The section has a background color
			this.backgroundColor = background;
			this.backgroundResourceID = null;
			break;
		case DRAWABLE:
			// The section has a background drawable
			this.backgroundColor = null;
			this.backgroundResourceID = background;
			break;
		default:
			// The section has an unknown background type
			this.backgroundColor = null;
			this.backgroundResourceID = null;
			break;
		} // End of switch
	}
	
}