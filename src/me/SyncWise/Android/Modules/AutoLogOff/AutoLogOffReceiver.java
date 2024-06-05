/**
 * Copyright 2014 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.AutoLogOff;

import me.SyncWise.Android.App;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class AutoLogOffReceiver extends BroadcastReceiver {
	
	/**
	 * Tag used to identify the source of a log message.
	 */
	protected static String TAG = "AutoLogOffReceiver";

	/*
	 * This method is called when the BroadcastReceiver is receiving an Intent broadcast.
	 *
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive ( Context context , Intent intent ) {
        // Display debug output if required
        if ( AutoLogOffUtils.showDebugOutput )
        	Log.d ( AutoLogOffUtils.TAG , TAG + " : onReceive" );
        
        // Reset the auto log off event for the next day
        AutoLogOffUtils.startAlarmAndListener ( context );
        
        // Declare and initialize a new intent to start a new activity
		Intent logOffIntent = new Intent ( context , App.getNewTastActivity () );
		// Indicate that a new task should be created
		logOffIntent.addFlags ( Intent.FLAG_ACTIVITY_NEW_TASK );
		// Indicate that the current application task should be cleared
		logOffIntent.addFlags ( Intent.FLAG_ACTIVITY_CLEAR_TASK );
		// Start the new activity
		context.startActivity ( logOffIntent );
	}
	
}