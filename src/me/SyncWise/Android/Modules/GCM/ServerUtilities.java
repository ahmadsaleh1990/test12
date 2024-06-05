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

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.UsersDao;
import me.SyncWise.Android.Modules.Sync.SyncUtils;
import android.content.Context;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;

/**
 * Utilities class used to communicate with the server.
 * 
 * @author Elias
 *
 */
public final class ServerUtilities {

	/**
	 * String holding the key of the {@code REGISTRATION ID} argument.
	 */
	public static final String REGISTRATION_ID = "regId";
	
    /**
     * Tag used to identify the source of a log message.
     */
    static final String TAG = "ServerUtilities";
    
	/**
	 * Constant integer hosting the maximum number of server registration attempts.
	 */
    private static final int MAX_ATTEMPTS = 5;
    
    /**
     * Default back off value in milliseconds.
     */
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    
    /**
     * Random Seed.
     */
    private static final Random random = new Random();

    /**
     * Register this account/device pair within the server.
     *
     * @return whether the registration succeeded or not.
     */
    static boolean register ( final Context context , final String regId ) {
    	// Display debug output if required
        if ( CommonUtilities.showDebugOutput )
        	Log.i ( CommonUtilities.TAG , TAG + " : register: registering device (regId = " + regId + ")" );
        // Get the register URL
        String registerUrl = CommonUtilities.getRegisterURL ( context );
        // Check if the register URL is valid
        if ( registerUrl == null ) {
        	// Display debug output if required
            if ( CommonUtilities.showDebugOutput )
            	Log.e ( CommonUtilities.TAG , TAG + " : register: invalid register url." );
        	// Exit method
        	return false;
        } // End if
		// Declare and initialize a map used to host the Http post arguments
		Map < String , String > arguments = new LinkedHashMap < String , String > ();
		// Add the user code argument
		arguments.put ( UsersDao.Properties.UserCode.columnName , DatabaseUtils.getCurrentUserCode ( context ) );
		// Add the company code argument
		arguments.put ( UsersDao.Properties.CompanyCode.columnName , DatabaseUtils.getCurrentCompanyCode ( context ) );
		// Add the registration Id argument
		arguments.put ( REGISTRATION_ID , regId );
		// Add the checksum argument
		arguments.put ( SyncUtils.CHECKSUM , SyncUtils.getArgumentsChecksum ( context , arguments ) );
		// Compute a pseudo-random back off value
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt ( 1000 );
        // Try to register the device on server as many attempts as needed
        for ( int i = 1 ; i <= MAX_ATTEMPTS ; i++ ) {
        	// Display debug output if required
            if ( CommonUtilities.showDebugOutput )
            	Log.d ( CommonUtilities.TAG , TAG + " : Attempt #" + i + " to register" );
            try {
            	// Fire the http post request and retrieve the http response
            	HttpResponse response = SyncUtils.post ( registerUrl , arguments , context );
				// Check if the response is valid
				if ( response == null )
					// Invalid response
					throw new IOException ();
				// Retrieve the string response
				String result = EntityUtils.toString ( response.getEntity () );
				// Check if registration was successful
				if ( ! result.equalsIgnoreCase ( CommonUtilities.SUCCESS ) )
					// Invalid response
					throw new IOException ();
				// Otherwise, registration is successful
	        	// Display debug output if required
	            if ( CommonUtilities.showDebugOutput )
	            	Log.d ( CommonUtilities.TAG , TAG + " : Device registered on server" );
				// Indicate that the device is registered on the server
                GCMRegistrar.setRegisteredOnServer ( context , true );
                // Indicate that the registration is successful
                return true;
            } catch ( IOException exception ) {
	        	// Display debug output if required
	            if ( CommonUtilities.showDebugOutput )
	            	Log.e ( CommonUtilities.TAG , TAG + " : Failed to register on attempt #" + i );
	            // Check if the maximum number of attempts is reached
	            if ( i == MAX_ATTEMPTS ) {
		        	// Display debug output if required
		            if ( CommonUtilities.showDebugOutput )
		            	Log.e ( CommonUtilities.TAG , TAG + " : Maximum number of attempts reached." );
	            	// Exit loop
	            	break;
	            } // End if
                try {
		        	// Display debug output if required
		            if ( CommonUtilities.showDebugOutput )
		            	Log.d ( CommonUtilities.TAG , TAG + " : Attempt #" + i + " : unrecoverable error. Sleeping for " + backoff + " ms before retry" );
		            // Back off
                    Thread.sleep ( backoff );
                } catch ( InterruptedException InterruptedException ) {
    	        	// Display debug output if required
    	            if ( CommonUtilities.showDebugOutput )
    	            	Log.d ( CommonUtilities.TAG , TAG + " : Thread interrupted: abort remaining retries!" );
                    // Activity finished before registration is complete - exit.
                    Thread.currentThread ().interrupt ();
                    // Indicate that the registration failed
                    return false;
                } // End of try-catch block
                // Increase back off value exponentially
                backoff *= 2;
            } // End of try-catch block
        } // End for loop
        // Indicate that the registration failed
        return false;
    }
    
}