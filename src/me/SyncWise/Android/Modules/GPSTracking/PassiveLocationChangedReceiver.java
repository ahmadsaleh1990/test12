/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.GPSTracking;

import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.PermissionsUtils;
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
public class PassiveLocationChangedReceiver extends BroadcastReceiver {
	
	/**
	 * Tag used to identify the source of a log message.
	 */
	protected static String TAG = "PassiveLocationChangedReceiver";
	  
	/*
	 * This method is called when the BroadcastReceiver is receiving an Intent broadcast.
	 *
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive ( Context context , Intent intent ) {
		 if( PermissionsUtils.getEnableGpsTracking(context , DatabaseUtils.getCurrentUserCode ( context ) , DatabaseUtils.getCurrentCompanyCode ( context ) ))
		 {	
		// Key used for a Bundle extra holding a Location value when a location change is broadcast using a PendingIntent
		String key = LocationManager.KEY_LOCATION_CHANGED;

		// Determine if the current broadcast is initiated by a one shot location update
		if ( intent.hasCategory ( TrackingUtils.IntentCategory.ONE_SHOT_UPDATE ) ) {
			// Display debug output if required
			if ( TrackingUtils.showDebugOutput )
				Log.d ( TrackingUtils.TAG , TAG + " :onReceive: on-demand location update received" );
			
			// Determine if the location update holds a valid location
			// AND is initiated by a GPS location update request
			if ( intent.hasExtra ( key )
					&& intent.hasCategory ( TrackingUtils.IntentCategory.GPS_LOCATION )
					&& ! intent.hasCategory ( TrackingUtils.IntentCategory.NETWORK_LOCATION ) )
				// Store the new location update
				TrackingUtils.storeNewLocation ( context , ( Location ) intent.getExtras ().get ( key ) , TrackingUtils.IntentCategory.GPS_LOCATION );
			// Determine if the location update holds a valid location
			// AND is initiated by a Network location update request
			else if ( intent.hasExtra ( key )
					&& ! intent.hasCategory ( TrackingUtils.IntentCategory.GPS_LOCATION )
					&& intent.hasCategory ( TrackingUtils.IntentCategory.NETWORK_LOCATION ) )
				// Store the new location update
				TrackingUtils.storeNewLocation ( context , ( Location ) intent.getExtras ().get ( key ) , TrackingUtils.IntentCategory.NETWORK_LOCATION );
			// Determine if the location update holds a valid location
			else if ( intent.hasExtra ( key ) ) {
				// Display debug output if required
				if ( TrackingUtils.showDebugOutput )
					Log.w ( TrackingUtils.TAG , TAG + " :onReceive: on-demand location has no valid intent category, treating location as NOT requested" );
				// Add the new location update
				TrackingUtils.addNewLocation ( context , ( Location ) intent.getExtras ().get ( key ) , false );
			} // End else if
			// Display debug output if required
			else if ( TrackingUtils.showDebugOutput )
				Log.w ( TrackingUtils.TAG , TAG + " :onReceive: location update => MISSING" );
		} // End if
		
		else if ( intent.hasExtra ( key ) ) {
			// Display debug output if required
			if ( TrackingUtils.showDebugOutput )
				Log.d ( TrackingUtils.TAG , TAG + " :onReceive: Location update received by the Passive provider requested from another application.");
			// Location update receiver by the Passive provider requested from another application
			// Add the new location update
			TrackingUtils.addNewLocation ( context , ( Location ) intent.getExtras ().get ( key ) , false );
		} // End if
		
		else
			// Invalid broadcast
			// Display debug output if required
			if ( TrackingUtils.showDebugOutput )
				Log.w ( TrackingUtils.TAG , TAG + " :onReceive: Unknown update received" );
	}
	}
}
