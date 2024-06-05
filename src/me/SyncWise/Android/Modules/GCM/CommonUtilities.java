/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

/**
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.SyncWise.Android.Modules.GCM;

import me.SyncWise.Android.Database.ConnectionSettings;
import me.SyncWise.Android.Database.ConnectionSettingsDao;
import me.SyncWise.Android.Database.ConnectionSettingsUtils;
import me.SyncWise.Android.Database.DatabaseUtils;
import android.content.Context;

/**
 * Utilities class providing methods and constants related to Google Cloud Messaging.
 * 
 * @author Elias
 *
 */
public final class CommonUtilities {

	/**
	 * Flag used to indicate if the debug output is to be displayed or not.
	 */
    static boolean showDebugOutput = false;
	
    /**
     * Google API project id registered to use GCM.
     */
    static String SENDER_ID;

	/**
	 * String holding the expected Success message if no errors occur on the server side.
	 */
	public static final String SUCCESS = "SUCCESS";
    
    /**
     * Tag used to identify the source of a log message.
     */
    static final String TAG = "GCM";

    /**
     * Intent used to display a message in the screen.
     */
    static final String DISPLAY_MESSAGE_ACTION = "com.google.android.gcm.demo.app.DISPLAY_MESSAGE";

    /**
     * Intent's extra that contains the message to be displayed.
     */
    static final String EXTRA_MESSAGE = "message";
    
    /**
     * Setter - {@link #SENDER_ID}
     * 
     * @param SENDER_ID	Google API project id registered to use GCM.
     */
    public static void setSenderId ( final String SENDER_ID ) {
    	// Set the project Id
    	CommonUtilities.SENDER_ID = SENDER_ID;
    }

    /**
     * Getter - {@link #SENDER_ID}
     * 
     * @return	Google API project id registered to use GCM.
     */
    public static String getSenderId () {
    	// Return the project Id
    	return SENDER_ID;
    }
    
    /**
     * Gets the server URL.<br>
     * The GPRS type is always used.
     * 
     * @param context	The application context.
     * @return	String hosting the server URL.
     */
    private static String getServerURL ( final Context context ) {
        // Search for the GPRS link
        ConnectionSettings connection = DatabaseUtils.getInstance ( context ).getDaoSession ().getConnectionSettingsDao ().queryBuilder ()
        		.where ( ConnectionSettingsDao.Properties.ConnectionSettingType.eq ( ConnectionSettingsUtils.Type.GPRS ) ).unique ();
        // Check if the connection is valid
        if ( connection == null )
        	// Invalid connection
        	return null;
		// Compute and return the server url
		return connection.getConnectionSettingURL ();
    }
    
    /**
     * Gets the register method URL.
     * 
     * @param context	The application context.
     * @return	String hosting the register URL.
     */
    static String getRegisterURL ( final Context context ) {
    	// Retrieve the url
    	String url = getServerURL ( context );
    	// Compute and return the required path, or return null if invalid
    	return url == null ? null : ConnectionSettingsUtils.getRegisterMethodPath ( url );
    }
    
}