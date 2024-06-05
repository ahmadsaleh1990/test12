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

import me.SyncWise.Android.App;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.PermissionsUtils;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Log;

/**
 * Utilities class related to automatic data synchronization, holding helper methods for additional functionality.
 * 
 * @author Elias
 *
 */
public class AutomaticSyncUtils {

	/**
	 * Flag used to indicate if the debug output is to be displayed or not.
	 */
    static boolean showDebugOutput = true;
	
    /**
     * Tag used to identify the source of a log message.
     */
    public static final String TAG = "AUTOMATIC_SYNC";
	
    /**
     * Key used to retrieve a shared preference value.
     */
    private static final String FIRST_RUN = AutomaticSyncUtils.class.getName () + ".FIRST_RUN";
    
    /**
     * Key used to retrieve a shared preference value.
     */
    private static final String IS_ENABLED = AutomaticSyncUtils.class.getName () + ".IS_ENABLED";
    
    /**
     * Key used to retrieve a shared preference value.
     */
    private static final String AUTO_SYNC_FREQUENCY = AutomaticSyncUtils.class.getName () + ".AUTO_SYNC_FREQUENCY";
    
	/**
	 * String holding the default value used to indicate whether or not to enable auto sync.
	 */
	public static final String DEFAULT_ENABLE_AUTO_SYNC = "N";
    
    /**
     * Constant integer hosting the default alarm frequency value in milliseconds.
     */
    public static final long DEFAULT_AUTO_SYNC_FREQUENCY = 60 * 60 * 1000;
	
    /**
     * Constant integer hosting the minimum alarm frequency value in milliseconds.
     */
    public static final long MIN_AUTO_SYNC_FREQUENCY = 2 * 60 * 1000;
	
    /**
     * Integer hosting the auto sync frequency value in milliseconds.
     */
    private static long autoSyncFrequency = DEFAULT_AUTO_SYNC_FREQUENCY;
    
    /**
     * Flag indicating if the auto sync utilities have been initialized.
     */
    private static boolean initialised = false;
    
    /**
     * Setter - {@link #autoSyncFrequency}
     * 
     * @param autoSyncFrequency	Integer hosting the auto sync frequency value in milliseconds.
     */
    public static void setAutoSyncFrequency ( final long autoSyncFrequency ) {
    	// Set the time period in milliseconds
    	AutomaticSyncUtils.autoSyncFrequency = ( autoSyncFrequency < MIN_AUTO_SYNC_FREQUENCY ? MIN_AUTO_SYNC_FREQUENCY : autoSyncFrequency );
    }
	
    /**
     * Getter - {@link #autoSyncFrequency}
     * 
     * @return	Integer hosting the auto sync frequency value in milliseconds.
     */
    public static long getAutoSyncFrequency () {
    	// Return the time period in milliseconds
        return autoSyncFrequency;
    }
    
    /**
     * Gets a pending intent that starts the automatic sync service.<br>
     * This intent is intended to be used with the {@link android.app.AlarmManager AlarmManager}.
     * 
     * @param context	The application context
     * @return	A {@link android.app.PendingIntent PendingIntent} used to start {@link me.SyncWise.Android.Modules.Sync.Automatic.AutomaticSyncService AutomaticSyncService}.
     */
    private static PendingIntent getFrequencyAlarmIntent ( final Context context ) {
    	// Initialize and return a pending intent used to start a service periodically via an alarm manager
    	return PendingIntent.getService ( context , 0 , new Intent ( context , App.getAutoSyncService () ) , PendingIntent.FLAG_UPDATE_CURRENT );
    }
    
    /**
     * Sets a periodic alarm to fire automatic synchronization updates.
     * 
     * @param context	The application context.
     * @see	#startAlarm(Context, SharedPreferences)t
     */
    public static void startAlarm ( final Context context ) {
        // Retrieve a reference to the shared preferences of the auto sync module
        final SharedPreferences sharedPreferences = context.getSharedPreferences ( AutomaticSyncUtils.class.getName () , Context.MODE_PRIVATE );
        // Set a periodic alarm to fire automatic synchronization updates
        startAlarm ( context , sharedPreferences );
    }
    
    /**
     * Sets a periodic alarm to fire automatic synchronization updates.
     * 
     * @param context	The application context.
     * @param sharedPreferences	Shared preference file of the auto sync module.
     * @see	#startAlarm(Context)
     */
    public static void startAlarm ( final Context context , final SharedPreferences sharedPreferences ) {
    	// Display debug output if required
        if ( showDebugOutput )
        	Log.d ( TAG , TAG + " : startAlarm: autoSyncFrequency = " + ( autoSyncFrequency == DEFAULT_AUTO_SYNC_FREQUENCY ? "default: " : "" ) + autoSyncFrequency / 1000 + " secs" );

        // Retrieve an alarm manager for receiving intents periodically
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService ( Context.ALARM_SERVICE );
        // Declare and initialize a pending intent used to start a service periodically via an alarm manager
        final PendingIntent alarmIntent = getFrequencyAlarmIntent ( context );
        // Cancel any existing alarm
        alarmManager.cancel ( alarmIntent );
        // Schedule a new alarm
        alarmManager.setInexactRepeating ( AlarmManager.ELAPSED_REALTIME_WAKEUP , SystemClock.elapsedRealtime () , getAutoSyncFrequency () , alarmIntent );
        // Indicate that the alarm is enabled
        setPreferences ( sharedPreferences , true , getAutoSyncFrequency () );
        
        // Display debug output if required
        if ( showDebugOutput )
        	Log.d ( TAG , TAG + " : startAlarm completed" );
    }
    
    /**
     * Cancels the periodic alarm and listener used to fire automatic synchronization updates.
     * 
     * @param context	The application context.
     * @param sharedPreferences	Shared preference file of the auto sync module.
     * @see	#stopAlarm(Context)
     */
    public static void stopAlarm ( final Context context , final SharedPreferences sharedPreferences ) {
        // Retrieve an alarm manager for receiving intents periodically
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService ( Context.ALARM_SERVICE );
        // Declare and initialize a pending intent used to start a service periodically via an alarm manager
        final PendingIntent alarmIntent = getFrequencyAlarmIntent ( context );
        // Cancel any existing alarm
        alarmManager.cancel ( alarmIntent );
        // Indicate that the alarm is disabled
        setPreferences ( sharedPreferences , false , null );
        
        // Display debug output if required
        if ( showDebugOutput )
        	Log.d ( TAG , TAG + " : stopAlarm completed" );
    }
    
    /**
     * Cancels the periodic alarm and listener used to fire automatic synchronization updates.
     * 
     * @param context	The application context.
     * @see	#stopAlarm(Context, SharedPreferences)
     */
    public static void stopAlarm ( final Context context ) {
        // Retrieve a reference to the shared preferences of the auto sync module
        final SharedPreferences sharedPreferences = context.getSharedPreferences ( AutomaticSyncUtils.class.getName () , Context.MODE_PRIVATE );
        // Cancel the periodic alarm and listener used to fire automatic synchronization updates
        stopAlarm ( context , sharedPreferences );
    }
    
    /**
     * Sets the auto sync module preferences.
     * 
     * @param sharedPreferences	Shared preference file of the auto sync module.
     * @param isEnabled	Flag indicating if the auto sync module is enabled or not.
     * @param frequency	Integer hosting the auto sync alarm frequency, or {@code NULL} if it is not set.
     */
    private static void setPreferences ( final SharedPreferences sharedPreferences , final boolean isEnabled , final Long frequency ) {
		// Retrieve a new Editor for these preferences to modify them
		SharedPreferences.Editor editor = sharedPreferences.edit ();
		// Set the enabled status in the preferences editor
		editor.putBoolean ( IS_ENABLED , isEnabled );
		// Set the frequency in the preferences editor (in milliseconds)
		editor.putLong ( AUTO_SYNC_FREQUENCY , frequency == null ? -1 : frequency );
		// Commit the preferences changes
		editor.commit ();
    }
    
    /**
     * Initializes the auto sync module using the default values.<br>
     * This method should be called from the Application's {@link Application#onCreate() onCreate} method.<br>
     * The default values used by the module are:<br>
     * <ul>
     * <li>Default value of {@link #autoSyncFrequency} : {@link #DEFAULT_AUTO_SYNC_FREQUENCY}.</li>
     * </ul>
     * 
     * @param context	The application context.
     * @see #initialiseModule(Context, long)
     */
    public static void initialiseModule ( final Context context ) {
    	// Check if the auto sync module is previously initialized
        if ( ! initialised ) {
        	// Display debug output if required
            if ( showDebugOutput )
            	Log.d ( TAG , TAG + " : initialise module" );
            
            // Retrieve a reference to the shared preferences of the auto sync module
            final SharedPreferences sharedPreferences = context.getSharedPreferences ( AutomaticSyncUtils.class.getName () , Context.MODE_PRIVATE );
            // Determine if this is the first run of the auto sync module
            if ( ! sharedPreferences.getBoolean ( FIRST_RUN , Boolean.FALSE ) ) {
            	// Display debug output if required
                if ( showDebugOutput )
                	Log.d ( TAG , TAG + " : initialise module: first time ever run -> start alarm" );
            
                // Start the location updates alarm
                startAlarm ( context , sharedPreferences );
                // Indicate that the auto sync module first run occurred
                sharedPreferences.edit ().putBoolean ( FIRST_RUN , Boolean.TRUE ).apply ();
                sharedPreferences.edit ().apply ();
            } // End if
            // Indicate that the AUTO SYNC module is initialized
            initialised = true;
        } // End if
    }
    
    /**
     * Updates the auto sync module (if any modification should be applied).
     * 
     * @param context	The application context.
     */
    public static void updateModule ( final Context context ) {
    	// Display debug output if required
        if ( showDebugOutput )
        	Log.d ( TAG , TAG + " : update module" );
        
        // Retrieve a reference to the shared preferences of the auto sync module
        final SharedPreferences sharedPreferences = context.getSharedPreferences ( AutomaticSyncUtils.class.getName () , Context.MODE_PRIVATE );
        
        // Determine if this is the first run of the auto sync module
        if ( ! sharedPreferences.getBoolean ( FIRST_RUN , Boolean.FALSE ) ) {
        	// Display debug output if required
            if ( showDebugOutput )
            	Log.d ( TAG , TAG + " : module never run, update ignored" );
        	// Do nothing
        	return;
        } // End if
        
        // Retrieve the user code
        String userCode = DatabaseUtils.getCurrentUserCode ( context );
        // Retrieve the company code
        String companyCode = DatabaseUtils.getCurrentCompanyCode ( context );
        
        // Determine if the auto sync module should be enabled
        boolean shouldEnable = PermissionsUtils.getEnableAutoSync ( context , userCode , companyCode );
        // Determine if the auto sync module is previously enabled
        boolean isEnabled = sharedPreferences.getBoolean ( IS_ENABLED , false );
        
    	// Retrieve the last set frequency
    	long lastFrequency = sharedPreferences.getLong ( AUTO_SYNC_FREQUENCY , -1 );
    	// Retrieve the current frequency
    	long currentFrequency = PermissionsUtils.getAutoSyncFrequency ( context , userCode , companyCode );
        
        // Check if the module is already up to date
        if ( shouldEnable == isEnabled ) {
        	// Check if the auto sync module is enabled but the frequency should be modified
        	if ( isEnabled && lastFrequency != currentFrequency ) {
            	// Set the auto sync frequency
                setAutoSyncFrequency ( currentFrequency );
                // Start the location updates alarm
                startAlarm ( context , sharedPreferences );
            	// Display debug output if required
            	if ( showDebugOutput )
                	Log.d ( TAG , TAG + " : module frequency updated from (" + lastFrequency + ") to (" + currentFrequency + ")" );
        	} // End if
        	// Display debug output if required
        	else if ( showDebugOutput )
            	Log.d ( TAG , TAG + " : module already up to date" );
            // Do nothing
            return;
        } // End if
        // Otherwise determine if the module should be enabled
        else if ( shouldEnable ) {
        	// Set the auto sync frequency
            setAutoSyncFrequency ( currentFrequency );
        	// Display debug output if required
        	if ( showDebugOutput )
            	Log.d ( TAG , TAG + " : module frequency set to (" + currentFrequency + ")" );
        	// Enable the auto sync module
        	startAlarm ( context , sharedPreferences );
        } // End else if
        else
        	// Otherwise disable the auto sync module
        	stopAlarm ( context , sharedPreferences );
        
    	// Display debug output if required
        if ( showDebugOutput )
        	Log.d ( TAG , TAG + " : module " + ( shouldEnable ? "enabled" : "disabled" ) );
    }
    
    /**
     * Initializes the auto sync module.
     * 
     * @param context	The application context.
     * @param autoSyncFrequency	The auto sync frequency in milliseconds.
     * @see #initialiseModule(Context)
     */
    public static void initialiseModule ( final Context context , final long autoSyncFrequency ) {
    	// Check if the auto sync module is previously initialized
        if ( ! initialised ) {
        	// Set the auto sync frequency
            setAutoSyncFrequency ( autoSyncFrequency );
            // Initialize the module
            initialiseModule ( context );
         } // End if
    }
    
}