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
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;

/**
 * Service used to force a location update.<br>
 * This service is started periodically using the {@link android.app.AlarmManager AlarmManager}.
 * 
 * @author Elias
 *
 */
public class LocationBroadcastService extends Service {

    /**
     * Tag used to identify the source of a log message.
     */
    private static final String TAG = "LocationBroadcastService";
    
    /*
     * Called by the system every time a client explicitly starts the service.
     *
     * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
     */
    @Override
    public int onStartCommand ( Intent intent , int flags, int startId ) {
    	// Display debug output if required
        if ( TrackingUtils.showDebugOutput )
        	Log.d ( TrackingUtils.TAG , TAG + " : onStartCommand" );
        if( PermissionsUtils.getEnableGpsTracking(this , DatabaseUtils.getCurrentUserCode ( this ) , DatabaseUtils.getCurrentCompanyCode ( this ) ))
		 {	
       // Retrieve a location update asynchronously
       new Thread ( null , runnable , TAG ).start ();
       }else{
       	LocationBroadcastService.this.stopSelf();
       }
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
        if ( TrackingUtils.showDebugOutput )
        	Log.d ( TrackingUtils.TAG , TAG + " : onDestroy" );
    }
    
    /**
     * Runnable object used to run statements in a different thread.
     */
    private Runnable runnable = new Runnable() {
        public void run () {
            // Force a location update
        	forceLocationUpdate ();
            // Stop the service
            LocationBroadcastService.this.stopSelf();
        }
    };
    
    /**
     * Requests a single location update from the system using both providers.<br>
     * The result (location) is broadcasted by the system and acquired using a receiver.
     */
    private void forceLocationUpdate () {
        // Display debug output if required
        if ( TrackingUtils.showDebugOutput )
        	Log.d ( TrackingUtils.TAG , TAG + " : Force a single location update" );
        
    	// Retrieve a LocationManager for controlling location
        final LocationManager locationManager = (LocationManager) getApplicationContext ().getSystemService ( Context.LOCATION_SERVICE );

        // Get a pending intent used to receive a GPS location update
        final PendingIntent oneshotGPSReceiver = TrackingUtils.getLocationGPSProviderIntent ( getApplicationContext () );
        // Get a pending intent used to receiver a network location update
        final PendingIntent oneshotNetworkReceiver = TrackingUtils.getLocationNetworkProviderIntent ( getApplicationContext () );
        
        // Keep track of successful requests
        boolean requestGPSLocationStatus = true;
        boolean requestNetworkLocationStatus = true;
        
        try {
        	// Register for a Network single location update using the pending intent
            locationManager.requestSingleUpdate ( LocationManager.NETWORK_PROVIDER , oneshotNetworkReceiver );
            // Display debug output if required
            if ( TrackingUtils.showDebugOutput )
            	Log.d ( TrackingUtils.TAG , TAG + " : Network Location Requested" );
        } catch ( IllegalArgumentException exception ) {
        	// Exception thrown if the Network provider is off, disabled, unavailable ...
        	// Reset flag
        	requestNetworkLocationStatus = false;
            // Display debug output if required
            if ( TrackingUtils.showDebugOutput )
            	Log.w ( TrackingUtils.TAG , TAG + " : IllegalArgumentException during Network location request. Details : " + exception.getMessage () );
        } // End of try-catch block
        
        try {
        	// Register for a GPS single location update using the pending intent
            locationManager.requestSingleUpdate ( LocationManager.GPS_PROVIDER , oneshotGPSReceiver );
            // Display debug output if required
            if ( TrackingUtils.showDebugOutput )
            	Log.d ( TrackingUtils.TAG , TAG + " : GPS Location Requested" );
        } catch ( IllegalArgumentException exception ) {
        	// Exception thrown if the GPS provider is off, disabled, unavailable ...
        	// Reset flag
        	requestGPSLocationStatus = false;
            // Display debug output if required
            if ( TrackingUtils.showDebugOutput )
            	Log.w ( TrackingUtils.TAG , TAG + " : IllegalArgumentException during GPS location request. Details : " + exception.getMessage () );
        } // End of try-catch block
        
        // Determine if both requests failed
        if ( requestGPSLocationStatus == false && requestNetworkLocationStatus == false ) {
        	// All location requests have failed
        	// Add an invalid location into DB
        	TrackingUtils.addInvalidLocation ( getBaseContext () );
        	// Display debug output if required
        	if (TrackingUtils.showDebugOutput)
        		Log.w ( TrackingUtils.TAG , TAG + " : IllegalArgumentException during call to locationManager.requestSingleUpdate - probable cause is that all location providers are off." );
        } // End if
        else {
        	// Otherwise, at least one location was successfully requested
            // Declare and initialize a pending intent used to broadcast a time out event
            final PendingIntent alarmIntent = TrackingUtils.getTimeOutAlarmIntent ( getApplicationContext () );
            // Retrieve an alarm manager for receiving intents periodically
            final AlarmManager alarmManager = (AlarmManager) getApplicationContext ().getSystemService ( Context.ALARM_SERVICE );
            // Cancel any existing alarm
            alarmManager.cancel ( alarmIntent );
            // Schedule a new alarm
            alarmManager.set ( AlarmManager.RTC_WAKEUP , TrackingUtils.getTimeOutTime () , alarmIntent );
            // Display debug output if required
            if ( TrackingUtils.showDebugOutput )
            	Log.d ( TrackingUtils.TAG , TAG + " : Time out alarm set." );
        } // End else
    }

}