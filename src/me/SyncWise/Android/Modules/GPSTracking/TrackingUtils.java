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

import java.util.Calendar;
import java.util.Date;

import me.SyncWise.Android.AppNotifications;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.DeviceTrackingUtils;
import me.SyncWise.Android.Database.DeviceTracking;
import me.SyncWise.Android.Database.DeviceTrackingDao;
import me.SyncWise.Android.Database.PermissionsUtils;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

/**
 * Utilities class for the GPS tracking module.
 * 
 * @author Elias
 *
 */
public final class TrackingUtils {
	
	/**
	 * Flag used to indicate if the debug output is to be displayed or not.
	 */
    static boolean showDebugOutput = false;
	
    /**
     * Tag used to identify the source of a log message.
     */
    public static final String TAG = "TRACKING";
    
    /**
     * Key used to retrieve a shared preference value.
     */
    private static final String FIRST_RUN = TrackingUtils.class.getName () + ".FIRST_RUN";
    
    /**
     * Constant boolean indicating if the provided status of the device is to notified.
     */
    public static final String DEFAULT_NOTIFY_PROVIDER_STATUS = "N";
    
    /**
     * Constant integer hosting the default alarm frequency value in milliseconds.
     */
    public static final long DEFAULT_ALARM_FREQUENCY = 5 * 60 * 1000;
	
    /**
     * Constant integer hosting the minimum alarm frequency value in milliseconds.
     */
    public static final long MIN_ALARM_FREQUENCY = 2 * 60 * 1000;
    
    /**
     * Constant integer hosting the default time out value in milliseconds.
     */
    public static final long DEFAULT_TIME_OUT = 1 * 60 * 1000;
    
    /**
     * Integer hosting the alarm frequency value in milliseconds.
     */
    private static long alarmFrequency = DEFAULT_ALARM_FREQUENCY;
    
    /**
     * Constant integer hosting the default minimum distance between two locations in meters.
     */
    public static final int DEFAULT_TRACKING_CORE_DISTANCE = 150;
    
    /**
     * Integer hosting the minimum distance between two locations in meters.
     */
    private static int minDistanceBetweenLocations = DEFAULT_TRACKING_CORE_DISTANCE;
    
    /**
     * Helper class used to define the various request codes of broadcasts.
     * 
     * @author Elias
     *
     */
    static class RequestCode {
    	
    	/**
    	 * Private request code for the location broadcast sender in the case of a repeating alarm.
    	 */
    	public static final int REPEATING_ALARM = 0;
    	
    	/**
    	 * Private request code for the location broadcast sender in the case of a fixed alarm.
    	 */
    	public static final int FIXED_ALARM = 1;
    	
        /**
         * Private request code for the location broadcast sender in the case of a passive provider.
         */
    	public static final int PASSIVE_PROVIDER = 2;
    	
    }

    /**
     * Helper class used to define the various intent categories.
     * 
     * @author Elias
     *
     */
    static class IntentCategory {
    	
    	/**
    	 * Category name assigned to an intent in the case of a single shot.
    	 */
    	static final String ONE_SHOT_UPDATE = "INTENT_CATEGORY_ONE_SHOT_UPDATE";
    	
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
    	static final String PROVIDER = TrackingUtils.class.getName () + ".PROVIDER";
    	static final String ACCURACY = TrackingUtils.class.getName () + ".ACCURACY";
    	static final String LATITUDE = TrackingUtils.class.getName () + ".LATITUDE";
    	static final String LONGITUDE = TrackingUtils.class.getName () + ".LONGITUDE";
    	static final String TIME = TrackingUtils.class.getName () + ".TIME";
    }
    
	/**
	 * Flag used to indicate if every location update (requested by other applications) should be saved.
	 */
	static boolean saveEveryLocationUpdate = false;

	/**
	 * Setter - {@link #saveEveryLocationUpdate}
	 * 
	 * @param saveEveryLocationUpdate	Flag used to indicate if every location update (requested by other applications) should be saved.
	 */
	public static void saveEveryLocationUpdate ( final boolean saveEveryLocationUpdate ) {
		// Set flag
		TrackingUtils.saveEveryLocationUpdate = saveEveryLocationUpdate;
	}

	/**
	 * Setter - {@link #showDebugOutput}
	 * 
	 * @param showDebugOutput	Flag used to indicate if the debug output is to be displayed or not.
	 */
	public static void showDebugOutput ( final boolean showDebugOutput ) {
		// Set flag
	    TrackingUtils.showDebugOutput = showDebugOutput;
	}

    /**
     * Setter - {@link #alarmFrequency}
     * 
     * @param alarmFrequency	Integer hosting the alarm frequency value in milliseconds.
     */
    private static void setAlarmFrequency ( final long alarmFrequency ) {
    	// Set the time period in milliseconds
        TrackingUtils.alarmFrequency = ( alarmFrequency < MIN_ALARM_FREQUENCY ? MIN_ALARM_FREQUENCY : alarmFrequency );
    }
    
    /**
     * Setter - {@link #minDistanceBetweenLocations}
     * 
     * @param minDistanceBetweenLocations	Integer hosting the minimum distance between two locations in meters.
     */
    private static void setMinDistanceBetweenLocations ( final int minDistanceBetweenLocations ) {
    	// Set the minimum distance between two locations in meters
        TrackingUtils.minDistanceBetweenLocations = ( minDistanceBetweenLocations < 0 ? 0 : minDistanceBetweenLocations );
    }
    
    /**
     * Flag indicating if the tracking utilities have been initialized.
     */
    private static boolean initialised = false;
    
    /**
     * Gets a pending intent that starts the location broadcast service.<br>
     * This intent is intended to be used with the {@link android.app.AlarmManager AlarmManager}.
     * 
     * @param context	The application context
     * @return	A {@link android.app.PendingIntent PendingIntent} used to start {@link me.SyncWise.Android.Modules.GPSTracking.LocationBroadcastService LocationBroadcastService}.
     */
    private static PendingIntent getFrequencyAlarmIntent ( final Context context ) {
    	// Initialize and return a pending intent used to start a service periodically via an alarm manager
    	return PendingIntent.getService ( context , RequestCode.REPEATING_ALARM , new Intent ( context , LocationBroadcastService.class ) , PendingIntent.FLAG_UPDATE_CURRENT );
    }
    
    /**
     * Gets a pending intent that starts the time out receiver.<br>
     * This intent is intended to be used with the {@link android.app.AlarmManager AlarmManager}.
     * 
     * @param context	The application context
     * @return	A {@link android.app.PendingIntent PendingIntent} used to start {@link me.SyncWise.Android.Modules.GPSTracking.TimeOutReceiver TimeOutReceiver}.
     */
    protected static PendingIntent getTimeOutAlarmIntent ( final Context context ) {
    	// Initialize and return a pending intent used to start a service via an alarm manager
    	return PendingIntent.getBroadcast ( context , RequestCode.FIXED_ALARM , new Intent ( context , TimeOutReceiver.class ) , PendingIntent.FLAG_UPDATE_CURRENT );
    }
    
    /**
     * Gets a pending intent that starts the location listener receiver.<br>
     * This intent is intended to be used with the {@link android.location.LocationManager LocationManager}.<br>
     * The main purpose of this intent is to capture all location updates that android broadcast, even from other applications.
     * 
     * @param context	The application context
     * @return	A {@link android.app.PendingIntent PendingIntent} used to start {@link me.SyncWise.Android.Modules.GPSTracking.PassiveLocationChangedReceiver PassiveLocationChangedReceiver}.
     */
    private static PendingIntent getLocationListenerIntent ( final Context context ) {
    	// Initialize and return a pending intent used to start a broadcast receiver in order to handle a location updates
    	return PendingIntent.getBroadcast ( context , RequestCode.PASSIVE_PROVIDER , new Intent ( context , PassiveLocationChangedReceiver.class ) , PendingIntent.FLAG_UPDATE_CURRENT );
    }
    
    /**
     * Get a pending intent that starts the location listener receiver.<br>
     * This intent is intended to be used with the {@link android.location.LocationManager LocationManager}.<br>
     * The main purpose of this intent is to capture the one shot (requested) GPS location update.
     * 
     * @param context	The application context.
     * @return	A {@link android.app.PendingIntent PendingIntent} used to start {@link me.SyncWise.Android.Modules.GPSTracking.PassiveLocationChangedReceiver PassiveLocationChangedReceiver}.
     */
    static PendingIntent getLocationGPSProviderIntent ( final Context context ) {
        // Declare and initialize a new intent to start the location receiver
        final Intent receiver = new Intent ( context , PassiveLocationChangedReceiver.class )
        	.addCategory ( TrackingUtils.IntentCategory.ONE_SHOT_UPDATE )
        	.addCategory ( TrackingUtils.IntentCategory.GPS_LOCATION );
        // Initialize and return a pending intent used to receive a location update from the GPS provider
        return PendingIntent.getBroadcast ( context , 0 , receiver , PendingIntent.FLAG_UPDATE_CURRENT );
    }
    
    /**
     * Get a pending intent that starts the location listener receiver.<br>
     * This intent is intended to be used with the {@link android.location.LocationManager LocationManager}.<br>
     * The main purpose of this intent is to capture the one shot (requested) Network location update.
     * 
     * @param context	The application context.
     * @return	A {@link android.app.PendingIntent PendingIntent} used to start {@link me.SyncWise.Android.Modules.GPSTracking.PassiveLocationChangedReceiver PassiveLocationChangedReceiver}.
     */
    static PendingIntent getLocationNetworkProviderIntent ( final Context context ) {
        // Declare and initialize a new intent to start the location receiver
        final Intent receiver = new Intent ( context , PassiveLocationChangedReceiver.class )
        	.addCategory ( TrackingUtils.IntentCategory.ONE_SHOT_UPDATE )
        	.addCategory ( TrackingUtils.IntentCategory.NETWORK_LOCATION );
        // Initialize and return a pending intent used to receive a location update from the GPS provider
        return PendingIntent.getBroadcast ( context , 0 , receiver , PendingIntent.FLAG_UPDATE_CURRENT );
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
     * Sets a periodic alarm and an appropriate listener to receive location updates.
     * 
     * @param context	The application context.
     */
    public static void startAlarmAndListener ( final Context context ) {
    	// Display debug output if required
        if ( showDebugOutput )
        	Log.d ( TAG , TAG + " : startAlarmAndListener: alarmFrequency = " + ( alarmFrequency == DEFAULT_ALARM_FREQUENCY ? "default: " : "" ) + alarmFrequency / 1000 + " secs" );

        // Retrieve an alarm manager for receiving intents periodically
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService ( Context.ALARM_SERVICE );
        // Declare and initialize a pending intent used to start a service periodically via an alarm manager
        final PendingIntent alarmIntent = getFrequencyAlarmIntent ( context );
        // Declare and initialize a pending intent used to broadcast a time out event
        final PendingIntent timeOutIntent = getTimeOutAlarmIntent ( context );
        // Cancel any existing alarm
        alarmManager.cancel ( alarmIntent );
        alarmManager.cancel ( timeOutIntent );
        // Schedule a new alarm
        alarmManager.setInexactRepeating ( AlarmManager.ELAPSED_REALTIME_WAKEUP , SystemClock.elapsedRealtime () , alarmFrequency , alarmIntent );

        // Retrieve a LocationManager for controlling location
        final LocationManager locationManager = (LocationManager) context.getSystemService ( Context.LOCATION_SERVICE );
        // Declare and initialize a pending intent used to perform a broadcast to passively receive location updates
        final PendingIntent locationListenerPassivePendingIntent = getLocationListenerIntent ( context );
        // Register for location updates using the named provider, and a pending intent
        locationManager.requestLocationUpdates ( LocationManager.PASSIVE_PROVIDER , 0 , 0 , locationListenerPassivePendingIntent );
        
        // Display debug output if required
        if ( showDebugOutput )
        	Log.d ( TAG , TAG + " : startAlarmAndListener completed" );
    }
    
    /**
     * Cancels the periodic alarm and listener used to receive location updates.
     * 
     * @param context	The application context.
     */
    public static void stopAlarmAndListener ( final Context context ) {
        // Retrieve an alarm manager for receiving intents periodically
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService ( Context.ALARM_SERVICE );
        // Declare and initialize a pending intent used to start a service periodically via an alarm manager
        final PendingIntent alarmIntent = getFrequencyAlarmIntent ( context );
        // Declare and initialize a pending intent used to broadcast a time out event
        final PendingIntent timeOutIntent = getTimeOutAlarmIntent ( context );
        // Cancel any existing alarm
        alarmManager.cancel ( alarmIntent );
        alarmManager.cancel ( timeOutIntent );

        // Retrieve a LocationManager for controlling location
        final LocationManager locationManager = (LocationManager) context.getSystemService ( Context.LOCATION_SERVICE );
        // Declare and initialize a pending intent used to perform a broadcast to passively receive location updates
        final PendingIntent locationListenerPassivePendingIntent = getLocationListenerIntent ( context );
        // Removes all location updates for the specified pending intent
        locationManager.removeUpdates ( locationListenerPassivePendingIntent );
        
        // Display debug output if required
        if ( showDebugOutput )
        	Log.d ( TAG , TAG + " : stopAlarmAndListener completed" );
    }
    
    /**
     * Initializes the tracking module using the default values.<br>
     * This method should be called from the Application's {@link Application#onCreate() onCreate} method.<br>
     * The default values used by the module are:<br>
     * <ul>
     * <li>Default value of {@link #alarmFrequency} : {@link #DEFAULT_ALARM_FREQUENCY}.</li>
     * <li>Default value of {@link #saveEveryLocationUpdate} : {@code false}</li>
     * </ul>
     * 
     * @param context	The application context.
     * @see #initialiseModule(Context, long)
     */
    public static void initialiseModule ( final Context context ) {
    	// Check if the GPS tracking module is previously initialized
        if ( ! initialised ) {
        	// Display debug output if required
            if ( showDebugOutput )
            	Log.d ( TAG , TAG + " : initialise module" );
            
            // Set the tracking core distance
            setMinDistanceBetweenLocations ( PermissionsUtils.getTrackingCoreDistance ( context , DatabaseUtils.getCurrentUserCode ( context ) , DatabaseUtils.getCurrentCompanyCode ( context ) ) );
        	// Display debug output if required
            if ( showDebugOutput )
            	Log.d ( TAG , TAG + " : tracking core distance = " + minDistanceBetweenLocations );
            
            // Retrieve a reference to the shared preferences of the tracking module
            final SharedPreferences sharedPreferences = context.getSharedPreferences ( TrackingUtils.class.getName () , Context.MODE_PRIVATE );
            // Determine if this is the first run of the tracking module
            if ( ! sharedPreferences.getBoolean ( FIRST_RUN , Boolean.FALSE ) ) {
            	// Display debug output if required
                if ( showDebugOutput )
                	Log.d ( TAG , TAG + " : initialise module: first time ever run -> start alarm and listener" );
                
                // Start the location updates alarm and listener
                startAlarmAndListener (context);
                // Indicate that the tracking module first run occurred
                sharedPreferences.edit ().putBoolean ( FIRST_RUN , Boolean.TRUE ).apply ();
                sharedPreferences.edit ().apply ();
            } // End if
            // Indicate that the GPS tracking module is initialized
            initialised = true;
        } // End if
    }

    /**
     * Initializes the tracking module.
     * 
     * @param context	The application context.
     * @param alarmFrequency	The alarm frequency in milliseconds.
     * @see #initialiseModule(Context)
     */
    public static void initialiseModule ( final Context context , final long alarmFrequency ) {
    	// Check if the GPS tracking module is previously initialized
        if ( ! initialised ) {
        	// Set the alarm frequency
            setAlarmFrequency ( alarmFrequency );
            // Initialize the module
            initialiseModule ( context );
         } // End if
    }
    
    /**
     * Initializes the tracking module.
     * 
     * @param context	The application context.
     * @param alarmFrequency	The alarm frequency in milliseconds.
     * @param saveEveryLocationUpdate	Flag used to indicate if every location update (requested by other applications) should be saved.
     * @see #initialiseModule(Context)
     */
    public static void initialiseModule ( final Context context , final long alarmFrequency , final boolean saveEveryLocationUpdate ) {
    	// Check if the GPS tracking module is previously initialized
        if ( ! initialised ) {
        	// Set the alarm frequency
        	setAlarmFrequency ( alarmFrequency );
        	// Set the flag
            TrackingUtils.saveEveryLocationUpdate = saveEveryLocationUpdate;
            // Initialize the module
            initialiseModule(context);
         } // End if
    }
    
    /**
     * Force a location update by starting the location broadcast service which will in its turn request for a location update.
     * 
     * @param context	The application context.
     */
    public static void forceLocationUpdate ( final Context context ) {
    	// Start the location broadcast service
        context.startService ( new Intent ( context , LocationBroadcastService.class ) );
    }

    /**
     * Stores the new provided location in a shared preferences file.<br>
     * The location is stored based on the provided intent category.
     * 
     * @param context	The application context.
     * @param location	A location to save that consists of a latitude, longitude, time stamp, and other information such as bearing, altitude and velocity.
     * @param intentCategory	String hosting an intent category.
     * @see	IntentCategory
     */
    static void storeNewLocation ( final Context context , final Location location , final String intentCategory ) {
    	// Determine the shared preferences file name
    	String fileName = null;
    	// Determine the provided intent category
    	if ( IntentCategory.GPS_LOCATION.equals ( intentCategory ) )
    		fileName = IntentCategory.GPS_LOCATION;
    	else if ( IntentCategory.NETWORK_LOCATION.equals ( intentCategory ) )
    		fileName = IntentCategory.NETWORK_LOCATION;
    	else {
    		// Invalid intent category
        	// Display debug output if required
            if ( TrackingUtils.showDebugOutput )
            	Log.w ( TrackingUtils.TAG , TAG + " : storeNewLocation : invalid intent category : " + intentCategory );
    		return;
    	} // End else
        
		// Open the shared preference file
		SharedPreferences sharedPref = context.getSharedPreferences ( fileName , Context.MODE_PRIVATE );
		// Retrieve a new Editor for these preferences to modify them
		SharedPreferences.Editor editor = sharedPref.edit();
		// Set the location data in the preferences editor
		editor.putString ( SharedPreferencesKey.PROVIDER , location.getProvider () );
		editor.putInt ( SharedPreferencesKey.ACCURACY , (int) location.getAccuracy () );
		editor.putString ( SharedPreferencesKey.LATITUDE , String.valueOf ( location.getLatitude () ) );
		editor.putString ( SharedPreferencesKey.LONGITUDE , String.valueOf ( location.getLongitude () ) );
		editor.putLong ( SharedPreferencesKey.TIME , location.getTime () );
		// Commit the preferences changes
		editor.commit();
		
    	// Display debug output if required
        if ( TrackingUtils.showDebugOutput )
        	Log.d ( TrackingUtils.TAG , TAG + " : storeNewLocation : location stored under : " + intentCategory );
    }
    
    /**
     * Adds any stored location. Stored locations are processed by priority : GPS locations has higher priorities then NETWORK locations.
     * If no locations are found, an invalid location is added.
     * 
     * @param context	The application context.
     */
    static void addStoredLocation ( final Context context ) {
    	// Display debug output if required
        if ( TrackingUtils.showDebugOutput )
        	Log.d ( TrackingUtils.TAG , TAG + " : addStoredLocation : looking for stored locations" );
    	
		// Open the shared preference file for GPS and network locations
    	SharedPreferences sharedPref = null;
		SharedPreferences sharedPref_GPS = context.getSharedPreferences ( IntentCategory.GPS_LOCATION , Context.MODE_PRIVATE );
		SharedPreferences sharedPref_Network = context.getSharedPreferences ( IntentCategory.NETWORK_LOCATION , Context.MODE_PRIVATE );
		// Check if a GPS location is stored
		if ( sharedPref_GPS.contains ( SharedPreferencesKey.PROVIDER ) )
			// Set the shared preferences to use
			sharedPref = sharedPref_GPS;
		// Otherwise, check if a Network location is stored
		else if ( sharedPref_Network.contains ( SharedPreferencesKey.PROVIDER ) )
			// Set the shared preferences to use
			sharedPref = sharedPref_Network;
		
    	// Display debug output if required
        if ( TrackingUtils.showDebugOutput ) {
        	if ( sharedPref_GPS.contains ( SharedPreferencesKey.PROVIDER ) )
        		Log.d ( TrackingUtils.TAG , TAG + " : addStoredLocation : location found under : " + IntentCategory.GPS_LOCATION );
        	if ( sharedPref_Network.contains ( SharedPreferencesKey.PROVIDER ) )
        		Log.d ( TrackingUtils.TAG , TAG + " : addStoredLocation : location found under : " + IntentCategory.NETWORK_LOCATION );
        	if ( sharedPref != null )
        		Log.d ( TrackingUtils.TAG , TAG + " : addStoredLocation : about to add location found under : " + ( sharedPref == sharedPref_GPS ? IntentCategory.GPS_LOCATION : IntentCategory.NETWORK_LOCATION ) );
        	else
        		Log.d ( TrackingUtils.TAG , TAG + " : addStoredLocation : no location found" );
        } // End if
		
		// Check if the shared preferences is valid
		if ( sharedPref != null ) {
			// Create a new location
			Location location = new Location ( sharedPref.getString ( SharedPreferencesKey.PROVIDER , null ) );
			location.setTime ( sharedPref.getLong ( SharedPreferencesKey.TIME , 0 ) );
			location.setLatitude ( Double.parseDouble ( sharedPref.getString ( SharedPreferencesKey.LATITUDE , "0" ) ) );
			location.setLongitude ( Double.parseDouble ( sharedPref.getString ( SharedPreferencesKey.LONGITUDE , "0" ) ) );
			location.setAccuracy ( sharedPref.getInt ( SharedPreferencesKey.ACCURACY , 0 ) );
			// Clear any stored locations
			SharedPreferences.Editor editor = null;
			editor = sharedPref_GPS.edit ();
			editor.clear ();
			editor.commit ();
			editor = sharedPref_Network.edit ();
			editor.clear ();
			editor.commit ();
			// Add the new location
			addNewLocation ( context , location );
		} // End if
		// Otherwise, there are no locations stored
		else
			// Add an invalid location
			addInvalidLocation ( context );
    }
    
    /**
     * Adds the new provided location into the database.<br>
     * For the location update to be saved, it should either be requested (by the current application) or allowed to be saved (requested by another application).
     * 
     * @param context	The application context.
     * @param location	A location to save that consists of a latitude, longitude, time stamp, and other information such as bearing, altitude and velocity.
     * @param isRequested	Boolean used to indicate if the location update is requested.
     */
    static void addNewLocation ( final Context context , final Location location , final boolean isRequested ) {
    	// Display debug output if required
        if ( showDebugOutput )
        	Log.d ( TAG , TAG + " : About to add a new location update which is " + ( isRequested ? "" : "NOT " ) + "requested. All location updates should " + ( saveEveryLocationUpdate ? "" : "NOT " ) + "be saved." );
        
    	// Determine if the location update is requested and/or all location updates (by other apps) should be saved
    	if ( isRequested || saveEveryLocationUpdate )
    		// Add the new location
    		addNewLocation ( context , location );
    	else
        	// Display debug output if required
            if ( showDebugOutput )
            	Log.d ( TAG , TAG + " : Location update ignored." );
    }
    
    /**
     * Adds the new provided location into the database.<br>
     * If the provided location is within the required distance (or less) than the previously stored location, the new location is ignored.
     * 
     * @param context	The application context.
     * @param location	A location to save that consists of a latitude, longitude, time stamp, and other information such as bearing, altitude and velocity.
     */
    private static void addNewLocation ( final Context context , final Location location ) {
    	// Flag used to determine whether a new location should be stored or not
    	boolean createNewLocation = true;
    	try {
    		// Determine if the minimum distance between locations is valid (greater than 0)
    		if ( minDistanceBetweenLocations > 0 ) {
		    	// Retrieve the last location
		    	DeviceTracking lastLocation = DatabaseUtils.getInstance ( context ).getDaoSession ().getDeviceTrackingDao ().queryBuilder ().orderDesc ( DeviceTrackingDao.Properties.Id ).limit ( 1 ).unique ();
		    	// Determine if the last location update is valid
		    	if ( lastLocation != null && ! TextUtils.isEmpty ( lastLocation.getLatitude () ) && ! TextUtils.isEmpty ( lastLocation.getLongitude () ) ) {
		    		// Determine if both locations were issued using the same provider
		    		// ALSO Compute the distance between the current and last location and compare it with the minimum required distance
		    		if ( similarProviders ( lastLocation.getProvider () , location.getProvider () )
		    				&& distance ( location.getLatitude () , location.getLongitude () , Double.valueOf ( lastLocation.getLatitude () ) , Double.valueOf ( lastLocation.getLongitude () ) ) < minDistanceBetweenLocations ) {
		    			// Both locations were issued using the same provider
		    			// The distance between the two locations is less than the minimum distance required
		    			// Ignore the new location
		    			createNewLocation = false;
		    	    	// Display debug output if required
		    	        if ( showDebugOutput )
		    	        	Log.d ( TAG , TAG + " : New location at lat [" + location.getLatitude () + "] and long [" + location.getLongitude () + "] acquired by " + location.getProvider () + " within the allowed min distance [" + distance ( location.getLatitude () , location.getLongitude () , Double.valueOf ( lastLocation.getLatitude () ) , Double.valueOf ( lastLocation.getLongitude () ) ) + " < " + minDistanceBetweenLocations + " ]. New location is ignored." );
		    		} // End if
		    	} // End if
	    	} // End if
    	} catch ( Exception exception ) {
    		// Store a new location
    		createNewLocation = true;
    	} // End of try-catch block
    	
    	// Determine if a new location should be stored
    	if ( createNewLocation) {
	    	// Insert into DB a new location update
	    	DatabaseUtils.getInstance ( context ).getDaoSession ().getDeviceTrackingDao ().insert ( new DeviceTracking ( null , // ID
	    			android.os.Build.SERIAL , // DeviceSerialID
	    			String.valueOf ( location.getLatitude () ) , // Latitude
	    			String.valueOf ( location.getLongitude () ) , // Longitude
	    			(int) location.getAccuracy () , // Accuracy
	    			new Date ( location.getTime () ) , // Date
	    			location.getProvider () , // Provider
	    			( ( (LocationManager) context.getSystemService ( Context.LOCATION_SERVICE ) ).isProviderEnabled ( LocationManager.GPS_PROVIDER ) ? DeviceTrackingUtils.enabledGPS () : DeviceTrackingUtils.disabeldGPS () ) , // IsGPSEnabled
	    			( ( (LocationManager) context.getSystemService ( Context.LOCATION_SERVICE ) ).isProviderEnabled ( LocationManager.NETWORK_PROVIDER ) ? DeviceTrackingUtils.enabledNetwork () : DeviceTrackingUtils.disabeldNetwork () ) , // IsNetworkEnabled
	    			Calendar.getInstance ().getTime () ) ); // StampDate
	    	// Display debug output if required
	        if ( showDebugOutput )
	        	Log.d ( TAG , TAG + " : New location at lat [" + location.getLatitude () + "] and long [" + location.getLongitude () + "] added." );
    	} // End if
    }
    
    /**
     * Adds an invalid location into the database.
     * 
     * @param context	The application context.
     */
    static void addInvalidLocation ( final Context context ) {
    	// Insert into DB a new location update
    	DatabaseUtils.getInstance ( context ).getDaoSession ().getDeviceTrackingDao ().insert ( new DeviceTracking ( null , // ID
    			android.os.Build.SERIAL , // DeviceSerialID
    			null , // Latitude
    			null , // Longitude
    			null , // Accuracy
    			Calendar.getInstance ().getTime () , // Date
    			null , // Provider
    			( ( (LocationManager) context.getSystemService ( Context.LOCATION_SERVICE ) ).isProviderEnabled ( LocationManager.GPS_PROVIDER ) ? DeviceTrackingUtils.enabledGPS () : DeviceTrackingUtils.disabeldGPS () ) , // IsGPSEnabled
    			( ( (LocationManager) context.getSystemService ( Context.LOCATION_SERVICE ) ).isProviderEnabled ( LocationManager.NETWORK_PROVIDER ) ? DeviceTrackingUtils.enabledNetwork () : DeviceTrackingUtils.disabeldNetwork () ) , // IsNetworkEnabled
    			Calendar.getInstance ().getTime () ) ); // StampDate
    	// Display debug output if required
        if ( showDebugOutput )
        	Log.d ( TAG , TAG + " : Invalid location added." );
    }
    
    /**
     * Compares two providers and determines if there are the same provider.
     * 
     * @param provider1	String holding the first provider name.
     * @param provider2	String holding the second provider name.
     * @return	{@code true} if both providers are the same, {@code false} otherwise.
     */
    static boolean similarProviders ( final String provider1 , final String provider2 ) {
    	// Determine if both providers are NULL
    	if ( provider1 == null && provider2 == null )
    		// Similar providers
    		return true;
    	// Determine if one of the providers is NULL
    	else if ( provider1 == null || provider2 == null )
    		// Different providers
    		return false;
    	// Otherwise both providers are not NULL
    	else
    		// Compare both providers
    		return provider1.equals ( provider2 );
    }
    
    /**
     * Computes and returns the distance between two locations in meters.
     * 
     * @param lat1	Latitude value of the first location.
     * @param lng1	Longitude value of the first location.
     * @param lat2	Latitude value of the second location.
     * @param lng2	Longitude value of the second location.
     * @return	Distance between the two locations.
     */
    public static double distance ( final double lat1 , final double lng1 , final double lat2 , final double lng2 ) {
        // Store the earth radius in meters
    	double earthRadius = 6371 * 1000;
    	// Compute and return the distance between the two locations
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;
        // Return the computed distance
        return dist;
    }
    
    /**
     * Based on the GPS status, a notification is canceled from / posted to the status bar.<br>
     * The main role of this notification is to indicate to the user that the GPS is disabled.<br>
     * Clicking on the notification will open the appropriate settings page for the user to enable GPS.
     * 
     * @param context	The application context.
     */
    @SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public static void NotifyProviderStatus ( final Context context ) {
    	// Check if the provider status is to be notified
    	if ( ! PermissionsUtils.getNotifyProviderStatus ( context , DatabaseUtils.getCurrentUserCode ( context ) , DatabaseUtils.getCurrentCompanyCode ( context ) ) )
    		// Do nothing
    		return;
    	
		// Determine if the GPS and network providers are enabled
    	boolean isGPSEnabled = ( (LocationManager) context.getSystemService ( Context.LOCATION_SERVICE ) ).isProviderEnabled ( LocationManager.GPS_PROVIDER );
    	boolean isNetworkEnabled = ( (LocationManager) context.getSystemService ( Context.LOCATION_SERVICE ) ).isProviderEnabled ( LocationManager.NETWORK_PROVIDER );
    	
    	// Check if both providers are enabled
		if ( isGPSEnabled )
			// GPS is enabled
	    	// Cancel a previously shown notification
	    	( (NotificationManager) context.getSystemService ( Context.NOTIFICATION_SERVICE ) ).cancel ( AppNotifications.gpsDisabled () );
		// Otherwise GPS is disabled
		else {
			// Build a notification
			Notification.Builder builder = new Notification.Builder ( context )
					.setLargeIcon ( BitmapFactory.decodeResource ( context.getResources () , R.drawable.ic_launcher ) )
			        .setSmallIcon ( R.drawable.gps_disabled )
			        .setContentTitle ( AppResources.getInstance ( context ).getString ( context , R.string.gps_disabled_notification_title ) )
			        .setContentText ( AppResources.getInstance ( context ).getString ( context , R.string.gps_disabled_notification_text ) )
			        .setContentIntent ( PendingIntent.getActivity ( context , 0 , new Intent ( android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS ) , PendingIntent.FLAG_UPDATE_CURRENT ) );
			// Determine the android version
			if ( android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 )
				// Post a notification to be shown in the status bar
				( (NotificationManager) context.getSystemService ( Context.NOTIFICATION_SERVICE ) ).notify ( AppNotifications.gpsDisabled () , builder.build () );
			else
				// Post a notification to be shown in the status bar
				( (NotificationManager) context.getSystemService ( Context.NOTIFICATION_SERVICE ) ).notify ( AppNotifications.gpsDisabled () , builder.getNotification () );
		} // End else
		
    	// Check if both providers are enabled
		if ( isNetworkEnabled ) {
			// Network is enabled
	    	// Cancel a previously shown notification
	    	( (NotificationManager) context.getSystemService ( Context.NOTIFICATION_SERVICE ) ).cancel ( AppNotifications.networkDisabled () );
		} // End if
		// Check if the Network provider is disabled
		else {
			// Otherwise Network is disabled
			// Build a notification
			Notification.Builder builder = new Notification.Builder ( context )
					.setLargeIcon ( BitmapFactory.decodeResource ( context.getResources () , R.drawable.ic_launcher ) )
			        .setSmallIcon ( R.drawable.gps_disabled )
			        .setContentTitle ( AppResources.getInstance ( context ).getString ( context , R.string.network_disabled_notification_title ) )
			        .setContentText ( AppResources.getInstance ( context ).getString ( context , R.string.network_disabled_notification_text ) )
			        .setContentIntent ( PendingIntent.getActivity ( context , 0 , new Intent ( android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS ) , PendingIntent.FLAG_UPDATE_CURRENT ) );
			// Determine the android version
			if ( android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN )
				// Post a notification to be shown in the status bar
				( (NotificationManager) context.getSystemService ( Context.NOTIFICATION_SERVICE ) ).notify ( AppNotifications.networkDisabled () , builder.build () );
			else
				// Post a notification to be shown in the status bar
				( (NotificationManager) context.getSystemService ( Context.NOTIFICATION_SERVICE ) ).notify ( AppNotifications.networkDisabled () , builder.getNotification () );
		} // End else
    }
    
}
