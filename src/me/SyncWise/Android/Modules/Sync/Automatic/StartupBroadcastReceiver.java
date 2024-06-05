/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Sync.Automatic;

import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.PermissionsUtils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Broadcast Receiver used to start the AUTO SYNC after the device boot is complete.
 * 
 * @author Elias
 *
 */
public class StartupBroadcastReceiver extends BroadcastReceiver {

    /**
     * Tag used to identify the source of a log message.
     */
    private static final String TAG = "AutomaticSyncStartupBroadcastReceiver";
	
	/*
	 * This method is called when the BroadcastReceiver is receiving an Intent broadcast.
	 *
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
    @Override
    public void onReceive ( Context context , Intent intent ) {
    	// Display debug output if required
        if ( AutomaticSyncUtils.showDebugOutput )
        	Log.d ( AutomaticSyncUtils.TAG , TAG + " : onReceive: start" );
    	
        // Determine if the auto sync feature is enabled
    	if ( PermissionsUtils.getEnableAutoSync ( context , DatabaseUtils.getCurrentUserCode ( context ) , DatabaseUtils.getCurrentCompanyCode ( context ) ) )
	    	// Start the auto sync alarm
	        AutomaticSyncUtils.startAlarm ( context );
    	else
    		// Otherwise the auto sync feature is disabled
    		AutomaticSyncUtils.stopAlarm ( context );
    	
    	// Display debug output if required
        if ( AutomaticSyncUtils.showDebugOutput )
        	Log.d ( AutomaticSyncUtils.TAG , TAG + " : onReceive: end" );
    }
	
}