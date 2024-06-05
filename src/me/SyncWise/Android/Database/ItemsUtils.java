/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Database;

import android.text.TextUtils;

/**
 * Utilities class for generated GreenDao Entities, holding helper methods for additional functionality related to the {@link me.SyncWise.Android.Database.Items Items} objects.
 * 
 * @author Elias
 *
 */
public class ItemsUtils {
	
	/**
	 * Helper class used to define the various values of an item type.
	 * 
	 * @author Elias
	 *
	 */
	public static class Type {
		
		/**
		 * Integer holding the regular item type
		 */
		public static final int REGULAR = 1;
		
		/**
		 * Integer holding the asset type
		 */
		public static final int ASSET = 2;
		
	}
	
	/**
	 * Determines if an item is a regular item.
	 * 
	 * @param item	Reference to an item object.
	 * @return	Boolean indicating if the item is a regular item.
	 */
	public static boolean isRegular ( final Items item ) {
		// Check if the item is valid
		if ( item == null || item.getItemType () == null )
			// Invalid item
			return false;
		// Otherwise the item is valid
		// Determine if the item is a regular item
		return ( item.getItemType () == ItemsUtils.Type.REGULAR );
	}
	
	/**
	 * Determines if an item is a Case item.
	 * 
	 * @param item	Reference to an item object.
	 * @return	Boolean indicating if the item is a regular item.
	 */
	public static boolean isCase ( final Items item ) {
		// Check if the item is valid
		if ( item == null || item.getUnitBigMedium() == null || item.getUnitMediumSmall()==null)
			// Invalid item
			return false;
		// Otherwise the item is valid
		// Determine if the item has a case
		if(item.getUnitBigMedium() ==  1 && item.getUnitMediumSmall() > 1)
		
			return  true;
		else
			return false;
		}
	
	
	/**
	 * Determines if an item is an asset.
	 * 
	 * @param item	Reference to an item object.
	 * @return	Boolean indicating if the item is an asset.
	 */
	public static boolean isAsset ( final Items item ) {
		// Check if the item is valid
		if ( item == null || item.getItemType () == null )
			// Invalid item
			return false;
		// Otherwise the item is valid
		// Determine if the item is an asset
		return ( item.getItemType () == ItemsUtils.Type.ASSET );
	}

	/**
	 * Determines if an item has a medium unit of measurement.
	 * 
	 * @param item	Reference to an item object.
	 * @return	Boolean indicating if the item has a medium unit of measurement.
	 */
	public static boolean isMedium ( final Items item ) {
		// Check if the item is valid
		if ( item == null )
			// Invalid item
			return false;
		// Otherwise the item is valid
		// Determine if the item has a medium unit of measurement
		return ( item.getUnitMediumSmall () <= 1 ? false : true );
	}
	
	
	
	/**
	 * Determines if an item has a big unit of measurement.
	 * 
	 * @param item	Reference to an item object.
	 * @return	Boolean indicating if the item has a medium unit of measurement.
	 */
	public static boolean isBig ( final Items item ) {
		// Check if the item is valid
		if ( item == null )
			// Invalid item
			return false;
		// Otherwise the item is valid
		// Determine if the item has a big unit of measurement
		return ( item.getUnitBigMedium () <= 1 ? false : true );
	}
	
	/**
	 * Determines if an item is taxable.
	 * 
	 * @param item	Reference to an item object.
	 * @return	Boolean indicating if the item is taxable.
	 */
	public static boolean isTaxable ( final Items item ) {
		// Check if the item is valid
		if ( item == null )
			// Invalid item
			return false;
		// Otherwise the item is valid
		// Determine if the item is taxable
		return ( ! TextUtils.isEmpty ( item.getTaxCode () ) );
	}
	
	/**
	 * Gets the big unit of measurement description.
	 * 
	 * @param unit	The provided {@link me.SyncWise.Android.Database.Units Units}.
	 * @return String holding the big unit of measurement description.
	 */
	public static String getBigDescription ( final Units unit ) {
		if ( unit == null || unit.getUnitBigDescription () == null )
			return "";
		return unit.getUnitBigDescription ();
	}
	
	/**
	 * Gets the medium unit of measurement description.
	 * 
	 * @param unit	The provided {@link me.SyncWise.Android.Database.Units Units}.
	 * @return String holding the medium unit of measurement description.
	 */
	public static String getMediumDescription ( final Units unit ) {
		if ( unit == null || unit.getUnitMediumDescription () == null )
			return "";
		return unit.getUnitMediumDescription ();
	}
	
	/**
	 * Gets the small unit of measurement description.
	 * 
	 * @param unit	The provided {@link me.SyncWise.Android.Database.Units Units}.
	 * @return String holding the small unit of measurement description.
	 */
	public static String getSmallDescription ( final Units unit ) {
		if ( unit == null || unit.getUnitSmallDescription () == null )
			return "";
		return unit.getUnitSmallDescription ();
	}
	
}