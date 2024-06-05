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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Broadcast Receiver used to start the GPS tracking after the device boot is complete.
 * 
 * @author Elias
 *
 */
public class StartupBroadcastReceiver extends BroadcastReceiver {
	
	/*
	 * This method is called when the BroadcastReceiver is receiving an Intent broadcast.
	 *
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
    @Override
    public void onReceive ( Context context , Intent intent ) {
    	// Start the location updates alarm and listener
        TrackingUtils.startAlarmAndListener ( context );
    }
	
}
