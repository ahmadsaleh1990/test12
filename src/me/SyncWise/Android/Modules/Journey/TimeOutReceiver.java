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

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

/**
 * Broadcast Receiver used to implement tracking time out.<br>
 * The tracking time out is used to store the acquired location in the database, and to cancel any pending location requests.
 * 
 * @author Elias
 *
 */
public class TimeOutReceiver extends BroadcastReceiver {

	/**
	 * Tag used to identify the source of a log message.
	 */
	protected static String TAG = "TimeOutReceiver";

	/*
	 * This method is called when the BroadcastReceiver is receiving an Intent broadcast.
	 *
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive ( Context context , Intent intent ) {
        // Display debug output if required
        if ( TrackingUtils.showDebugOutput )
        	Log.d ( TrackingUtils.TAG , TAG + " : onReceive" );
        
        // Cancel all location requests
        // Retrieve a LocationManager for controlling location
        final LocationManager locationManager = (LocationManager) context.getSystemService ( Context.LOCATION_SERVICE );
        
        // Get a pending intent used to receive a GPS location update
        final PendingIntent visitLocationUpdateGPSReceiver = TrackingUtils.getLocationGPSProviderIntent ( context , intent.getLongExtra ( TrackingUtils.SharedPreferencesKey.VISIT_ID , 0 ) );
        
        // Removes all location updates for the specified pending intents
        locationManager.removeUpdates ( visitLocationUpdateGPSReceiver );
        
        // Display debug output if required
        if ( TrackingUtils.showDebugOutput )
        	Log.d ( TrackingUtils.TAG , TAG + " : onReceive: all location updates cancelled." );
        
		// Check for any stored location and add it
        TrackingUtils.addStoredLocation ( context , intent.getLongExtra ( TrackingUtils.SharedPreferencesKey.VISIT_ID , 0 ) );
	}
        
}