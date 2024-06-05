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

import java.util.Calendar;

import me.SyncWise.Android.DateTime;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Utilities class for the auto log off module.
 * 
 * @author Elias
 *
 */
public final class AutoLogOffUtils {

	/**
	 * Flag used to indicate if the debug output is to be displayed or not.
	 */
    static boolean showDebugOutput = false;
    
    /**
     * Tag used to identify the source of a log message.
     */
    public static final String TAG = "AUTO_LOG_OFF";
    
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
    	public static final int FIXED_ALARM = 1;
    	
    }
    
    /**
     * Gets the auto log off time.<br>
     * The auto log off time is set as midnight of the next day.
     * 
     * @return	A {@link java.util.Calendar Calendar} object hosting the auto log off exact time.
     */
    static Calendar getAutoLogOffTime () {
    	// Declare and initialize a calendar object
    	Calendar nextDayMidnight = Calendar.getInstance ();
    	// Set the calendar to the next day
    	nextDayMidnight.add ( Calendar.DATE , 1 );
    	// Set the calendar to midnight
    	nextDayMidnight.set ( Calendar.HOUR_OF_DAY , 0 );
    	nextDayMidnight.set ( Calendar.MINUTE , 0 );
    	nextDayMidnight.set ( Calendar.SECOND , 0 );
    	nextDayMidnight.set ( Calendar.MILLISECOND , 0 );
    	// Return midnight of the next day
    	return nextDayMidnight;
    }
    
    /**
     * Gets a pending intent that starts the auto log off receiver.<br>
     * This intent is intended to be used with the {@link android.app.AlarmManager AlarmManager}.
     * 
     * @param context	The application context
     * @return	A {@link android.app.PendingIntent PendingIntent} used to start {@link me.SyncWise.Android.Modules.AutoLogOff.AutoLogOffReceiver AutoLogOffReceiver}.
     */
    protected static PendingIntent getTimeOutAlarmIntent ( final Context context ) {
    	// Initialize and return a pending intent used to start a service via an alarm manager
    	return PendingIntent.getBroadcast ( context , RequestCode.FIXED_ALARM , new Intent ( context , AutoLogOffReceiver.class ) , PendingIntent.FLAG_UPDATE_CURRENT );
    }
    
    /**
     * Sets a periodic alarm and an appropriate listener to issue and handle auto log off requests.
     * 
     * @param context	The application context.
     */
    public static void startAlarmAndListener ( final Context context ) {
    	// Display debug output if required
        if ( showDebugOutput )
        	Log.d ( TAG , TAG + " : startAlarmAndListener: started" );

        // Retrieve an alarm manager for receiving intents periodically
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService ( Context.ALARM_SERVICE );
        // Declare and initialize a pending intent used to broadcast a auto log off event
        final PendingIntent autoLogOffIntent = getTimeOutAlarmIntent ( context );
        // Cancel any existing alarm
        alarmManager.cancel ( autoLogOffIntent );
        // Compute the auto log off time
        Calendar autoLogOffTime = getAutoLogOffTime ();
        // Display debug output if required
        if ( showDebugOutput )
        	Log.d ( TAG , TAG + " : auto log off time : " + DateTime.getFullDateTime ( context , autoLogOffTime , true , true ) );        
        // Schedule a new alarm
        alarmManager.set ( AlarmManager.RTC_WAKEUP , autoLogOffTime.getTimeInMillis () , autoLogOffIntent );
        // Display debug output if required
        if ( showDebugOutput )
        	Log.d ( TAG , TAG + " : auto log off alarm set." );
        
        // Display debug output if required
        if ( showDebugOutput )
        	Log.d ( TAG , TAG + " : startAlarmAndListener completed" );
    }
	
}