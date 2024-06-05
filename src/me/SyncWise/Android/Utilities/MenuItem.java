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

import android.text.TextUtils;

/**
 * Utility class that provides helper methods related {@link android.view.MenuItem MenuItem} objects.
 * 
 * @author Elias
 *
 */
public class MenuItem {
	
	/**
	 * Enables the provided menu item.<br>
	 * It becomes enabled and visible.
	 * 
	 * @param menuItem	The {@link android.view.MenuItem MenuItem} object to enable.
	 * @see MenuItem#enable(android.view.MenuItem, int)
	 * @see MenuItem#enable(android.view.MenuItem, String)
	 */
	public static void enable ( final android.view.MenuItem menuItem ) {
		// Check if the menu item is valid
		if ( menuItem == null )
			// Invalid menu item
			return;
		// Enable the menu item
		menuItem.setEnabled ( true );
		// Make the menu item visible
		menuItem.setVisible ( true );
	}
	
	/**
	 * Enables the provided menu item and sets its title.<br>
	 * It becomes enabled and visible.<br>
	 * If the resource id is valid, it is set to the menu item as title.
	 * 
	 * @param menuItem	The {@link android.view.MenuItem MenuItem} object to enable.
	 * @param titleResourceId	title (string) resource id.
	 * @see MenuItem#enable(android.view.MenuItem)
	 * @see MenuItem#enable(android.view.MenuItem, String)
	 */
	public static void enable ( final android.view.MenuItem menuItem , final int titleResourceId ) {
		// Check if the menu item is valid
		if ( menuItem == null )
			// Invalid menu item
			return;
		// Check if the resource id is valid
		if ( titleResourceId > 0 )
			// Set the menu item title
			menuItem.setTitle ( titleResourceId );
		// Enable the menu item
		enable ( menuItem );
	}
	
	/**
	 * Enables the provided menu item and sets its title.<br>
	 * It becomes enabled and visible.<br>
	 * If the string is valid, it is set to the menu item as title.
	 * 
	 * @param menuItem	The {@link android.view.MenuItem MenuItem} object to enable.
	 * @param title	String holding the menu item title.
	 * @see MenuItem#enable(android.view.MenuItem)
	 * @see MenuItem#enable(android.view.MenuItem, int)
	 */
	public static void enable ( final android.view.MenuItem menuItem , final String title ) {
		// Check if the menu item is valid
		if ( menuItem == null )
			// Invalid menu item
			return;
		// Check if the title is valid
		if ( ! TextUtils.isEmpty ( title ) )
			// Set the menu item title
			menuItem.setTitle ( title );
		// Enable the menu item
		enable ( menuItem );
	}

}