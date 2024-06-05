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

import java.util.Calendar;

import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.IsProcessedUtils;
import me.SyncWise.Android.Database.Visits;
import me.SyncWise.Android.Database.VisitsDao;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

/**
 * Utilities class for the Visit location module.
 * 
 * @author Elias
 *
 */
public class TrackingUtils {

	/**
	 * Flag used to indicate if the debug output is to be displayed or not.
	 */
    static boolean showDebugOutput = false;
	
    /**
     * Tag used to identify the source of a log message.
     */
    public static final String TAG = "VISIT_TRACKING";
    
    /**
     * Constant integer hosting the default time out value in milliseconds.
     */
    public static final long DEFAULT_TIME_OUT = 1 * 60 * 1000;
	
    /**
     * Helper class used to define the various request codes of broadcasts.
     * 
     * @author Elias
     *
     */
    static class RequestCode {
    	
    	/**
    	 * Private request code for the location broadcast sender in the case of a fixed alarm.
    	 */
    	public static final int FIXED_ALARM = 0;
    	
        /**
         * Private request code for the location broadcast sender in the case of a passive provider.
         */
    	public static final int PASSIVE_PROVIDER = 1;
    	
    }
    
    /**
     * Helper class used to define the various intent categories.
     * 
     * @author Elias
     *
     */
    static class IntentCategory {
    	
    	/**
    	 * Category name assigned to an intent in the case of a visit update.
    	 */
    	static final String VISIT_UPDATE = "INTENT_CATEGORY_VISIT_UPDATE";
    	
    	/**
    	 * Category name assigned to an intent in the case of GPS location.
    	 */
    	static final String GPS_LOCATION = "INTENT_CATEGORY_GPS_LOCATION";
    	
    	/**
    	 * Category name assigned to an intent in the case of network location.
    	 */
    	static final String NETWORK_LOCATION = "INTENT_CATEGORY_NETWORK_LOCATION";
    	
    }
	
    /**
     * Helper class used to define various shared preferences keys.
     * 
     * @author Elias
     *
     */
    static class SharedPreferencesKey {
    	static final String VISIT_ID = TrackingUtils.class.getName () + ".VISIT_ID";
    	static final String LATITUDE = TrackingUtils.class.getName () + ".LATITUDE";
    	static final String LONGITUDE = TrackingUtils.class.getName () + ".LONGITUDE";
    }
    
    /**
     * Get a pending intent that starts the location listener receiver.<br>
     * This intent is intended to be used with the {@link android.location.LocationManager LocationManager}.<br>
     * The main purpose of this intent is to capture the one shot (requested) GPS location update.
     * 
     * @param context	The application context.
     * @param visitId	Long hosting the visit id.
     * @return	A {@link android.app.PendingIntent PendingIntent} used to start {@link me.SyncWise.Android.Modules.GPSTracking.PassiveLocationChangedReceiver PassiveLocationChangedReceiver}.
     */
    static PendingIntent getLocationGPSProviderIntent ( final Context context , final long visitId ) {
        // Declare and initialize a new intent to start the location receiver
    	final Intent receiver = new Intent ( context , VisitLocationReceiver.class )
    		.addCategory ( TrackingUtils.IntentCategory.VISIT_UPDATE )
    		.addCategory ( TrackingUtils.IntentCategory.GPS_LOCATION )
    		.addCategory ( String.valueOf ( visitId ) )
    		.putExtra ( SharedPreferencesKey.VISIT_ID , visitId );
        // Initialize and return a pending intent used to receive a location update from the GPS provider
        return PendingIntent.getBroadcast ( context , RequestCode.PASSIVE_PROVIDER , receiver , PendingIntent.FLAG_UPDATE_CURRENT );
    }
    
    /**
     * Gets a pending intent that starts the time out receiver.<br>
     * This intent is intended to be used with the {@link android.app.AlarmManager AlarmManager}.
     * 
     * @param context	The application context
     * @param visitId	Long hosting the visit id.
     * @return	A {@link android.app.PendingIntent PendingIntent} used to start {@link me.SyncWise.Android.Modules.Journey.TimeOutReceiver TimeOutReceiver}.
     */
    protected static PendingIntent getTimeOutAlarmIntent ( final Context context , final long visitId ) {
    	// Initialize and return a pending intent used to start a service via an alarm manager
    	return PendingIntent.getBroadcast ( context , RequestCode.FIXED_ALARM , new Intent ( context , TimeOutReceiver.class ).addCategory ( String.valueOf ( visitId ) ).putExtra ( SharedPreferencesKey.VISIT_ID , visitId ) , PendingIntent.FLAG_UPDATE_CURRENT );
    }
    
    /**
     * Gets the time out time.<br>
     * The time out time is computed by adding to the current time, the time out value.
     * 
     * @return	Long hosting the time out time.
     */
    static long getTimeOutTime () {
    	// Return the current time added with the time out value
    	return Calendar.getInstance ().getTimeInMillis () + DEFAULT_TIME_OUT;
    }
    
    /**
     * Requests a single location update from the system using a fine accuracy.<br>
     * The result (location) is broadcasted by the system and acquired using a receiver.
     * 
     * @param context	The application context
     * @param visitId	Long hosting the visit id.
     */
    public static void forceLocationUpdate ( final Context context , final long visitId ) {
    	// Display debug output if required
    	if ( TrackingUtils.showDebugOutput )
    		Log.d ( TrackingUtils.TAG , TAG + " : Force a single visit location update" );
    	
    	// Retrieve a LocationManager for controlling location
        final LocationManager locationManager = (LocationManager) context.getSystemService ( Context.LOCATION_SERVICE );
        
        // Get a pending intent used to receive a GPS location update
        final PendingIntent visitLocationUpdateGPSReceiver = TrackingUtils.getLocationGPSProviderIntent ( context , visitId );
        
        // Keep track of successful requests
        boolean requestGPSLocationStatus = true;
        
        try {
        	// Register for a GPS single location update using the pending intent
            locationManager.requestSingleUpdate ( LocationManager.GPS_PROVIDER , visitLocationUpdateGPSReceiver );
        	// Display debug output if required
        	if ( TrackingUtils.showDebugOutput )
        		Log.d ( TrackingUtils.TAG , TAG + " : GPS Location Requested" );
        } catch ( IllegalArgumentException exception ) {
        	// Exception thrown if the GPS provider is off, disabled, unavailable ...
        	// Reset flag
        	requestGPSLocationStatus = false;
        	// Display debug output if required
        	if ( TrackingUtils.showDebugOutput )
        		Log.d ( TrackingUtils.TAG , TAG + " : IllegalArgumentException during GPS location request. Details : " + exception.getMessage () );
        } // End of try-catch block
        
        // Determine if request succeeded
        if ( requestGPSLocationStatus ) {
        	// Visit location was successfully requested
            // Declare and initialize a pending intent used to broadcast a time out event
            final PendingIntent alarmIntent = TrackingUtils.getTimeOutAlarmIntent ( context , visitId );
            // Retrieve an alarm manager for receiving intents periodically
            final AlarmManager alarmManager = (AlarmManager) context.getSystemService ( Context.ALARM_SERVICE );
            // Cancel any existing alarm
            alarmManager.cancel ( alarmIntent );
            // Schedule a new alarm
            alarmManager.set ( AlarmManager.RTC_WAKEUP , TrackingUtils.getTimeOutTime () , alarmIntent );
        	// Display debug output if required
        	if ( TrackingUtils.showDebugOutput )
        		Log.d ( TrackingUtils.TAG , TAG + " : Time out alarm set." );
        } // End if
    }

    /**
     * Stores the new provided location in a shared preferences file.<br>
     * The location is stored based on the provided intent category.
     * 
     * @param context	The application context.
     * @param visitId	Long hosting the visit id.
     * @param location	A location to save that consists of a latitude, longitude, time stamp, and other information such as bearing, altitude and velocity.
     */ 
	public static void storeVisitLocation ( final Context context , final long visitId , final Location location ) {
    	// Compute the shared preferences file name
    	String fileName = TAG + visitId;
		// Open the shared preference file
		SharedPreferences sharedPref = context.getSharedPreferences ( fileName , Context.MODE_PRIVATE );
		// Retrieve a new Editor for these preferences to modify them
		SharedPreferences.Editor editor = sharedPref.edit();
		// Set the location data in the preferences editor
		editor.putString ( SharedPreferencesKey.LATITUDE , String.valueOf ( location.getLatitude () ) );
		editor.putString ( SharedPreferencesKey.LONGITUDE , String.valueOf ( location.getLongitude () ) );
		// Commit the preferences changes
		editor.commit();
		
    	// Display debug output if required
        if ( TrackingUtils.showDebugOutput )
        	Log.d ( TrackingUtils.TAG , TAG + " : storeVisitLocation : visit location stored for " + visitId );
	}

    /**
     * Updates the corresponding visit by adding any stored location.
     * 
     * @param context	The application context.
     */
	public static void addStoredLocation ( final Context context , final long visitId ) {
    	// Display debug output if required
        if ( TrackingUtils.showDebugOutput )
        	Log.d ( TrackingUtils.TAG , TAG + " : addStoredLocation : looking for stored locations for " + visitId );
        
		// Open the shared preference file for the visit location
    	SharedPreferences sharedPref = context.getSharedPreferences ( TAG + visitId , Context.MODE_PRIVATE );
    	// Check if a visit location is stored
    	if ( ! sharedPref.contains ( SharedPreferencesKey.LONGITUDE ) ) {
        	// Display debug output if required
            if ( TrackingUtils.showDebugOutput )
            	Log.d ( TrackingUtils.TAG , TAG + " : addStoredLocation : no visit location found for " + visitId );
            // Do nothing
            return;
    	} // End if
    	
    	// Display debug output if required
        if ( TrackingUtils.showDebugOutput )
        	Log.d ( TrackingUtils.TAG , TAG + " : addStoredLocation : visit location found for " + visitId );
        
        // Retrieve the visit from DB
        Visits visit = DatabaseUtils.getInstance ( context ).getDaoSession ().getVisitsDao ().queryBuilder ()
        		.where ( VisitsDao.Properties.VisitID.eq ( visitId ) ).unique ();
        
        // Check if the visit object is valid
        if ( visit == null ) {
        	// Display debug output if required
            if ( TrackingUtils.showDebugOutput )
            	Log.d ( TrackingUtils.TAG , TAG + " : addStoredLocation : invalid visit object for " + visitId );
            // Do nothing
            return;
    	} // End if
        // Check if the visit object is not processed yet
        if ( visit.getIsProcessed () == IsProcessedUtils.isSync () ) {
        	// Display debug output if required
            if ( TrackingUtils.showDebugOutput )
            	Log.d ( TrackingUtils.TAG , TAG + " : addStoredLocation : visit object already processed for " + visitId );
            // Do nothing
            return;
    	} // End if
        
        // Otherwise update the visit
        visit.setLatitude ( sharedPref.getString ( SharedPreferencesKey.LATITUDE , "0" ) );
        visit.setLongitude ( sharedPref.getString ( SharedPreferencesKey.LONGITUDE , "0" ) );
        DatabaseUtils.getInstance ( context ).getDaoSession ().getVisitsDao ().update ( visit );
		// Clear stored location
		SharedPreferences.Editor editor = sharedPref.edit ();
		editor.clear ();
		editor.commit ();
	}
    
}