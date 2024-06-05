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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

/**
 * Broadcast Receiver used to acquire location updates, both required from the current application and other applications.
 * 
 * @author Elias
 *
 */
public class VisitLocationReceiver extends BroadcastReceiver {

	/**
	 * Tag used to identify the source of a log message.
	 */
	protected static String TAG = "VisitLocationReceiver";
	
	/*
	 * This method is called when the BroadcastReceiver is receiving an Intent broadcast.
	 *
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive ( Context context , Intent intent ) {
		// Display debug output if required
		if ( TrackingUtils.showDebugOutput )
			Log.d ( TrackingUtils.TAG , TAG + " :onReceive" );
		
		// Key used for a Bundle extra holding a Location value when a location change is broadcast using a PendingIntent
		String key = LocationManager.KEY_LOCATION_CHANGED;
		
		// Determine if the current broadcast is initiated by a visit location update
		if ( intent.hasCategory ( TrackingUtils.IntentCategory.VISIT_UPDATE ) ) {
			// Display debug output if required
			if ( TrackingUtils.showDebugOutput )
				Log.d ( TrackingUtils.TAG , TAG + " :onReceive: on-demand visit location update received" );
			
			// Determine if the location update holds a valid location
			if ( intent.hasExtra ( key ) )
				// Store the new location update
				TrackingUtils.storeVisitLocation ( context , intent.getLongExtra ( TrackingUtils.SharedPreferencesKey.VISIT_ID , 0 ) , ( Location ) intent.getExtras ().get ( key ) );
		} // End if
		else
			// Display debug output if required
			if ( TrackingUtils.showDebugOutput )
				Log.d ( TrackingUtils.TAG , TAG + " :onReceive: Invalid visit location" );

	}
	
}