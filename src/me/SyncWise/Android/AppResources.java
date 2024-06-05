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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;

/**
 * Helper Class used to manage the application resources.
 * 
 * @author Elias
 * @sw.todo	<b>Foreign Language Implementation :</b><br>
 * Implement a way to store and retrieve foreign languages (indicated throughout the class by {@code TODO}).
 *
 */
public class AppResources {
	
	/**
	 * Singleton instance of {@code AppResources}.
	 */
    private static AppResources singleton;
    
    /**
     * List of integers holding string resources IDs, used to indicate if the string resources have values in a foreign language.<br>
     * String resources whose IDs are NOT in the list can be retrieved using the {@link android.content.Context#getString(int) getString(id)} method.<br>
     * Otherwise, they have a foreign language value.
     */
    private List < Integer > foreignStringsIDs;
    
    /**
     * Initializes and keeps track of only one class instance.
     * 
     * @param context	The application context.
     * @return	An instance of {@link me.SyncWise.Android.AppResources AppResources}.
     */
    public static AppResources getInstance ( final Context context ) {
    	// Check if the singleton instance is initialized
    	if ( singleton == null )
    		// The singleton instance is not initialized
    		singleton = new AppResources ( context );
    	// Return the singleton instance
        return singleton;
    }
    
    /**
     * Constructor.
     * Made private for singleton implementation.
     * 
     * @param context	The application context.
     */
    private AppResources ( final Context context ) {
    	// Initialize attributes
    	initializeGetString ( context );
    }
	
	/**
	 * Retrieves and returns the string mapped to the provided resource id
	 * 
	 * @param context	The application context.
	 * @param resourceID	string resource id.
	 * @return	String mapped to the string resource id.<br>
	 * An empty string is returned if the string resource id is invalid.
	 */
	public String getString ( final Context context , final Integer resourceID ) {
		// Check if the resource ID is valid
		if ( resourceID == null )
			// Invalid resource id, return empty string
			return "";
		try {
			// Check if the list of foreign string resources IDs is valid
			if ( foreignStringsIDs == null )
				// Retrieve the localized formatted string from the application's package's default string table
				return context.getString ( resourceID );
			// Otherwise search for the required resource id in the list
			int index = Collections.binarySearch ( foreignStringsIDs , resourceID );
			// Determine if the computed index is valid
			if ( index < 0 )
				// Retrieve the localized formatted string from the application's package's default string table
				return context.getString ( resourceID );
			else {
				// TODO : Foreign language implementation.
			} // End else
		} catch ( Exception exception ) {
			// Error occurred, return empty string
			return "";
		} // End of try-catch block
		return null;
	}
    
    /**
     * Initializes the string resources array.<br>
     * <b>Attention ! </b> The array initialization (which may be heavy) is done on the main UI thread.<br>
     * The method should be called asynchronously (on a separate thread) to avoid <a href="http://developer.android.com/training/articles/perf-anr.html" >ANRs</a>.
     * 
     * @param context	The application context.
     */
	private void initializeGetString ( final Context context ) {
		// Declare a class object used to reference the inner class 'string' of the class 'R' of the current application, holding the string resources id 
		Class < ? > stringResources = null;
		try {
			// Retrieve a reference to the R.string class
			stringResources = Class.forName ( context.getPackageName () + ".R$string" );
		} catch ( Exception exception ) {
			// Do nothing (impossible to happen)
		}
		// Determine if the current language setting is the default one
		if ( ! Language.isDefault ( context.getApplicationContext () ) ) {

			// TODO : Foreign Language Implementation
			
			// A foreign language is set
			// Initialize the list of string resources IDs
			foreignStringsIDs = new ArrayList < Integer > ();
			// Retrieve the string fields of the auto generated R class
			Field fields [] = stringResources.getFields ();
			// Iterate over all the string fields
			for ( Field field : fields )
				try {
					// Determine if the current string resource has a foreign language value
					if ( true /* TODO : Determine if the current string resource has a foreign language value */ )
						// Indicate that the string resource has a foreign language value
						foreignStringsIDs.add ( field.getInt ( null ) );
				} catch ( Exception exception ) {
					// Do nothing (impossible to happen)
				} // End of try-catch block
			// Sort the list of IDs
			Collections.sort ( foreignStringsIDs );
		} // End if
	}
	
}