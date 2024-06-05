/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android;

import java.io.File;
import java.io.IOException;

import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.PermissionsUtils;
import me.SyncWise.Android.Modules.AutoLogOff.AutoLogOffUtils;
import me.SyncWise.Android.Modules.ClientsList.ClientInfo.ClientInfoActivity;
import me.SyncWise.Android.Modules.GCM.CommonUtilities;
import me.SyncWise.Android.Modules.GPSTracking.TrackingUtils;
import me.SyncWise.Android.Modules.Sync.Automatic.AutomaticSyncUtils;
import android.app.Application;
import android.os.Environment;

/**
 * Base class used to maintain global application state. The class is instantiated when the process for the application/package is created. 
 * 
 * @author Elias
 *
 */
public class App extends Application {
	
	/**
	 * {@link java.lang.Class Class} activity reference used to identify the data management activity.
	 */
	private static Class < ? > dataManagementActivity;
	
	/**
	 * {@link java.lang.Class Class} activity reference used to start with a new task.<br>
	 * This is mainly used to kill the current application process, by basically clearing the current task.
	 */
	private static Class < ? > newTastActivity;
	
	/**
	 * {@link java.lang.Class Class} activity reference used to identify the call menu activity.
	 */
	private static Class < ? > callMenuActivityClass;
	
	/**
	 * {@link java.lang.Class Class} service reference used to identify the automatic synchronization service.
	 */
	private static Class < ? > autoSyncService;
	
	/**
	 * {@link java.lang.Class Class} activity reference used to identify the client info activity.
	 */
	private static Class < ? > clientInfoActicityClass;
	
	/**
	 * Setter - {@link #dataManagementActivity}
	 * 
	 * @param callMenuActivityClass	{@link java.lang.Class Class} activity reference used to identify the data management activity.
	 */
	public static void setDataManagementActivity ( final Class < ? > dataManagementActivity ) {
		App.dataManagementActivity = dataManagementActivity;
	}
	
	/**
	 * Getter - {@link #dataManagementActivity}
	 * 
	 * @return	{@link java.lang.Class Class} activity reference used to identify the data management activity.
	 */
	public static Class < ? > getDataManagementActivity () {
		return dataManagementActivity;
	}
	
	/**
	 * Setter - {@link #newTastActivity}
	 * 
	 * @param callMenuActivityClass	{@link java.lang.Class Class} activity reference used to start with a new task.
	 */
	public static void setNewTastActivity ( final Class < ? > newTastActivity ) {
		App.newTastActivity = newTastActivity;
	}
	
	/**
	 * Getter - {@link #newTastActivity}
	 * 
	 * @return	{@link java.lang.Class Class} activity reference used to start with a new task.
	 */
	public static Class < ? > getNewTastActivity () {
		return newTastActivity;
	}
	
	/**
	 * Setter - {@link #callMenuActivityClass}
	 * 
	 * @param callMenuActivityClass	{@link java.lang.Class Class} activity reference used to identify the call menu activity.
	 */
	public static void setCallMenuActivityClass ( final Class < ? > callMenuActivityClass ) {
		App.callMenuActivityClass = callMenuActivityClass;
	}
	
	/**
	 * Getter - {@link #callMenuActivityClass}
	 * 
	 * @return	{@link java.lang.Class Class} activity reference used to identify the call menu activity.
	 */
	public static Class < ? > getCallMenuActivityClass () {
		return callMenuActivityClass;
	}
	
	/**
	 * Setter - {@link #autoSyncService}
	 * 
	 * @return	{@link java.lang.Class Class} service reference used to identify the automatic synchronization service.
	 */
	public static Class < ? > getAutoSyncService () {
		return autoSyncService;
	}

	/**
	 * Getter - {@link #autoSyncService}
	 * 
	 * @param autoSyncService	{@link java.lang.Class Class} service reference used to identify the automatic synchronization service.
	 */
	public static void setAutoSyncService ( final Class < ? > autoSyncService ) {
		App.autoSyncService = autoSyncService;
	}
	
	/**
	 * Getter - {@link #clientInfoActicityClass}
	 * 
	 * @return {@link java.lang.Class Class} activity reference used to identify the client info activity.
	 */
	public static Class < ? > getClientInfoActicityClass () {
		return clientInfoActicityClass;
	}
	
	/**
	 * Setter - {@link #clientInfoActicityClass}
	 * 
	 * @param clientInfoActicityClass {@link java.lang.Class Class} activity reference used to identify the client info activity.
	 */
	public static void setClientInfoActicityClass ( final Class < ? > clientInfoActicityClass ) {
		App.clientInfoActicityClass = clientInfoActicityClass;
	}

	/*
	 * Called when the application is starting.
	 *
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate () {
		// Retrieve the user code
		String userCode = DatabaseUtils.getCurrentUserCode ( getApplicationContext () );
		// Retrieve the company code
		String companyCode = DatabaseUtils.getCurrentCompanyCode ( getApplicationContext () );
		
		 if( PermissionsUtils.getEnableGpsTracking(getApplicationContext () , DatabaseUtils.getCurrentUserCode ( getApplicationContext () ) , DatabaseUtils.getCurrentCompanyCode ( getApplicationContext () ) ))
        // Initialize the tracking module
        TrackingUtils.initialiseModule ( getApplicationContext () , PermissionsUtils.getTrackingFrequency ( getApplicationContext () , userCode , companyCode ) );
        
		 // Initialize the auto sync frequency
        AutomaticSyncUtils.initialiseModule ( getApplicationContext () , PermissionsUtils.getAutoSyncFrequency ( getApplicationContext () , userCode , companyCode ) );
        // Initialize the auto log off module
        AutoLogOffUtils.startAlarmAndListener ( getApplicationContext () );
    	// Post / cancel the GPS and/or Network notification accordingly
    	TrackingUtils.NotifyProviderStatus ( getApplicationContext () );
    	
    	// By default use the internal client info activity
    	setClientInfoActicityClass ( ClientInfoActivity.class );
    	if ( isExternalStorageWritable() ) {
	   		
            File appDirectory = new File( Environment.getExternalStorageDirectory() + "/ABALog" );
            File logDirectory = new File( appDirectory + "/log" );
            File logFile = new File( logDirectory, "logcat" + System.currentTimeMillis() + ".txt" );

            // create app folder
            if ( !appDirectory.exists() ) {
                appDirectory.mkdir();
            }

            // create log folder
            if ( !logDirectory.exists() ) {
                logDirectory.mkdir();
            }

            // clear the previous logcat and then write the new one to the file
            try {
                Process process = Runtime.getRuntime().exec("logcat -c");
                process = Runtime.getRuntime().exec("logcat -f " + logFile);
            } catch ( IOException e ) {
                e.printStackTrace();
            }

        } else if ( isExternalStorageReadable() ) {
            // only readable
        } else {
            // not accessible
        }
    	// Initialize GCM module
    	CommonUtilities.setSenderId ( "795121092260" );
	}
	/* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if ( Environment.MEDIA_MOUNTED.equals( state ) ) {
            return true;
        }
        return false;
    }
    
    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if ( Environment.MEDIA_MOUNTED.equals( state ) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals( state ) ) {
            return true;
        }
        return false;
    }
}