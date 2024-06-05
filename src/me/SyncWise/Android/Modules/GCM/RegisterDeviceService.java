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

import me.SyncWise.Android.Database.DatabaseUtils;

import com.google.android.gcm.GCMRegistrar;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

/**
 * Service used to force a device registration on GCM and own server.
 * 
 * @author Elias
 *
 */
public class RegisterDeviceService extends Service {

    /**
     * Tag used to identify the source of a log message.
     */
    private static final String TAG = "RegisterDeviceService";
	
    /*
     * Called by the system every time a client explicitly starts the service.
     *
     * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
     */
    @Override
    public int onStartCommand ( Intent intent , int flags, int startId ) {
    	// Display debug output if required
        if ( CommonUtilities.showDebugOutput )
        	Log.d ( CommonUtilities.TAG , TAG + " : onStartCommand" );

        // Register device asynchronously
        new Thread ( null , runnable , TAG ).start ();
        // Make the system to re-create the service if this service's process is killed while it is started
        return START_STICKY;
    }
    
    /*
     * Return the communication channel to the service.
     *
     * @see android.app.Service#onBind(android.content.Intent)
     */
    @Override
    public IBinder onBind ( Intent intent ) {
    	// Clients can not bind to the service
        return null;
    }
    
    /*
     * Called by the system to notify a Service that it is no longer used and is being removed.
     *
     * @see android.app.Service#onDestroy()
     */
    @Override
    public void onDestroy () {
    	// Display debug output if required
        if ( CommonUtilities.showDebugOutput )
        	Log.d ( CommonUtilities.TAG , TAG + " : onDestroy" );
    }
    
    /**
     * Runnable object used to run statements in a different thread.
     */
    private Runnable runnable = new Runnable() {
        public void run () {
            // Force a device registration
        	registerDevice ();
            // Stop the service
        	RegisterDeviceService.this.stopSelf();
        }
    };
    
    /**
     * Registers the device on GCM and own servers (if not already done).
     */
    private void registerDevice () {
    	// Check if the sender id is valid
    	if ( CommonUtilities.getSenderId () == null ) {
    		// Cannot register device
        	// Display debug output if required
            if ( CommonUtilities.showDebugOutput )
            	Log.d ( CommonUtilities.TAG , TAG + " : registerDevice: invalid sender id, cannot register device." );
    		// Do nothing
    		return;
    	} // End if
    	
    	// Check if the device is owned
    	if ( DatabaseUtils.getCurrentUserCode ( getApplicationContext () ) == null || DatabaseUtils.getCurrentCompanyCode ( getApplicationContext () ) == null ) {
    		// Cannot register device
        	// Display debug output if required
            if ( CommonUtilities.showDebugOutput )
            	Log.d ( CommonUtilities.TAG , TAG + " : registerDevice: invalid device owner user code, cannot register device." );
    		// Do nothing
    		return;
    	} // End if
    	
    	// Retrieve the registration Id
    	String registrationId = GCMRegistrar.getRegistrationId ( this );
    	// Check if the registration Id is valid
    	if ( TextUtils.isEmpty ( registrationId ) ) {
        	// Display debug output if required
            if ( CommonUtilities.showDebugOutput )
            	Log.d ( CommonUtilities.TAG , TAG + " : registerDevice: device not registered on GCM server, registring ..." );
    		// Automatically register device on GCM server
    		GCMRegistrar.register ( this , CommonUtilities.SENDER_ID );
    		// Exit method
    		return;
    	} // End if
    	// Otherwise the device is registered on GCM server
    	// Display debug output if required
        if ( CommonUtilities.showDebugOutput )
        	Log.d ( CommonUtilities.TAG , TAG + " : registerDevice: device already registered on GCM server (regId=" + registrationId + ")" );
    	// Check if the device is registered on the server
    	if ( ! GCMRegistrar.isRegisteredOnServer ( this ) ) {
        	// Display debug output if required
            if ( CommonUtilities.showDebugOutput )
            	Log.d ( CommonUtilities.TAG , TAG + " : registerDevice: device not registered on server, registring ..." );
            // Register device on server
            ServerUtilities.register ( this , registrationId );
    		// Exit method
    		return;
    	} // End if
    	// Otherwise the device is registered on server
    	// Display debug output if required
        if ( CommonUtilities.showDebugOutput )
        	Log.d ( CommonUtilities.TAG , TAG + " : registerDevice: device already registered on server" );
    }
    
}