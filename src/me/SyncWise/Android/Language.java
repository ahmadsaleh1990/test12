/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * Helper Class used to handle various language implementations.
 * 
 * @author Elias
 *
 */
public abstract class Language {
	
	/**
	 * Shared preference file name.
	 */
	public static final String SHARED_PREFERENCES = Language.class.getName () + ".LANGUAGE";
	
	/**
	 * Language key used in the shared preference language file
	 */
	public static final String KEY = Language.class.getName () + ".LANGUAGE";
	
	/**
	 * Default language
	 */
	public static final String DEFAULT = "EN-US";
	
	/**
	 * Retrieves the current language setting.
	 * 
	 * @param context The application context.
	 * @return The current language.
	 */
	public static String getLanguage ( final Context context ) {
		// Open the shared preference language file
		SharedPreferences language = context.getSharedPreferences ( SHARED_PREFERENCES , Context.MODE_PRIVATE );
		// Retrieve the stored language (if none found, use the default language)
		return language.getString ( KEY , DEFAULT );
	}
	
	/**
	 * Saves the current language setting.
	 * 
	 * @param context The application context.
	 * @param language The new language setting to save.
	 */
	public static void setLanguage ( final Context context , final String language ) {
		// Check if the provided language is valid
		if ( TextUtils.isEmpty ( language ) )
			// Invalid language, exit method
			return;
		// Open the shared preference language file
		SharedPreferences sharedPref = context.getSharedPreferences ( SHARED_PREFERENCES , Context.MODE_PRIVATE );
		// Retrieve a new Editor for these preferences to modify them
		SharedPreferences.Editor editor = sharedPref.edit();
		// Set the new language setting preferences editor
		editor.putString ( KEY , language );
		// Commit the preferences changes
		editor.commit();
	}
	
	/**
	 * Determines if the default language setting is applied.
	 * 
	 * @param context The application context.
	 * @return True if the current language setting is the default one, false otherwise.
	 */
	public static boolean isDefault ( final Context context ) {
		// Open the shared preference language file
		SharedPreferences language = context.getSharedPreferences ( SHARED_PREFERENCES , Context.MODE_PRIVATE );
		try {
			// Check if the stored language is the default one
			return DEFAULT.contentEquals ( language.getString ( KEY , DEFAULT ) );
		} catch ( Exception exception ) {
			return false;
		} // End of try-catch block
	}
	
}