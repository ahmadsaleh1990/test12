/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.GCM;

import me.SyncWise.Android.Network;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Broadcast Receiver used to receive network changes events.<br>
 * This receiver checks is the device is properly registered.
 * 
 * @author Elias
 *
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

	/*
	 * This method is called when the BroadcastReceiver is receiving an Intent broadcast.
	 *
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive ( Context context , Intent intent ) {
		// Check for network availability
		if ( Network.networkAvailability ( context ) )
			// Start the register device service (used to register the device on GCM and own servers)
			context.startService ( new Intent ( context , RegisterDeviceService.class ) );
	}
	
}