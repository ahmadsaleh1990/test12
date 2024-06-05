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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import me.SyncWise.Android.Database.ConnectionSettings;
import me.SyncWise.Android.Database.ConnectionSettingsDao;
import me.SyncWise.Android.Database.ConnectionSettingsUtils;
import me.SyncWise.Android.Database.DaoSession;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.DeviceSerials;
import me.SyncWise.Android.Database.DeviceSerialsDao;
import me.SyncWise.Android.Database.DeviceTracking;
import me.SyncWise.Android.Database.PermissionsUtils;
import me.SyncWise.Android.Database.UsersDao;
import me.SyncWise.Android.Gson.CommonUtilities;
import me.SyncWise.Android.Modules.Sync.SyncUtils;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Service used to send location updates to the server.<br>
 * This service is started periodically using the {@link android.app.AlarmManager AlarmManager}.
 * 
 * @author Elias
 *
 */
public class TimeOutBroadcastService extends Service {

    /**
     * Tag used to identify the source of a log message.
     */
    private static final String TAG = "TimeOutBroadcastService";
    
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
       // Send locations to the server asynchronously
       new Thread ( null , runnable , TAG ).start ();
       }
       else{
       	TimeOutBroadcastService.this.stopSelf();
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
            // Send locations
        	sendLocations ();
            // Stop the service
        	TimeOutBroadcastService.this.stopSelf ();
        }
    };
    
    /**
     * Sends locations that are not processed to the server.
     */
    private void sendLocations () {
        // Display debug output if required
        if ( TrackingUtils.showDebugOutput )
        	Log.d ( TrackingUtils.TAG , TAG + " : sendLocations: About to send locations to server." );
        
        // Retrieve a reference to the dao session
        DaoSession daoSession = DatabaseUtils.getInstance ( getApplicationContext () ).getDaoSession ();
        // Search for the GPRS link
        ConnectionSettings connection = daoSession.getConnectionSettingsDao ().queryBuilder ()
        		.where ( ConnectionSettingsDao.Properties.ConnectionSettingType.eq ( ConnectionSettingsUtils.Type.GPRS ) ).unique ();
        // Check if the connection is valid
        if ( connection == null ) {
            // Display debug output if required
            if ( TrackingUtils.showDebugOutput )
            	Log.d ( TrackingUtils.TAG , TAG + " : sendLocations: Invalid url." );
        	// Invalid connection
        	return;
        } // End if
        
        // Retrieve the user code
        String userCode = DatabaseUtils.getCurrentUserCode ( getApplicationContext () );
        // Retrieve the company code
        String companyCode = DatabaseUtils.getCurrentCompanyCode ( getApplicationContext () );
        // Retrieve a list of all device tracking
        List < DeviceTracking > deviceTracking = daoSession.getDeviceTrackingDao ().loadAll ();
		// Query DB for the device serial
		List < DeviceSerials > deviceSerials = daoSession.getDeviceSerialsDao ().queryBuilder ()
				.where ( DeviceSerialsDao.Properties.DeviceSerialCode.eq ( android.os.Build.SERIAL ) ,
						DeviceSerialsDao.Properties.UserCode.eq ( userCode ) ).list ();
        // Check if there is at least one device tracking or device serial
        if ( deviceTracking.isEmpty () && ! deviceSerials.isEmpty () ) {
            // Display debug output if required
            if ( TrackingUtils.showDebugOutput )
            	Log.d ( TrackingUtils.TAG , TAG + " : sendLocations: no new data found." );
        	// No need to sent data
        	return;
        } // End id
        else
            // Display debug output if required
            if ( TrackingUtils.showDebugOutput )
            	Log.d ( TrackingUtils.TAG , TAG + " : sendLocations: new data found." );
        
		// Build a set Gson object
		Gson gson = CommonUtilities.buildSetGson ();
		// Declare and initialize a map used to host the Http post arguments
		Map < String , String > arguments = new LinkedHashMap < String , String > ();
		List < String > tables = new ArrayList < String > ();
		List < String > json = new ArrayList < String > ();
		// Compute the server url
		String url = ConnectionSettingsUtils.getSetMethodPath ( connection.getConnectionSettingURL () );
		// Declare and initialize a string used to host the table argument
		String table = null;

		// Check if the device serial is stored in DB
		if ( deviceSerials.isEmpty () ) {
            // Display debug output if required
            if ( TrackingUtils.showDebugOutput )
            	Log.d ( TrackingUtils.TAG , TAG + " : sendLocations: device serial not set :  " + android.os.Build.SERIAL );
			// Declare and initialize the appropriate device serial for the current user
			deviceSerials.add ( new DeviceSerials ( null , // ID
					android.os.Build.SERIAL , // DeviceSerialCode
					userCode , // UserCode
					companyCode , // CompanyCode
					Calendar.getInstance ().getTime () ) ); // StampDate
			// Add the device serial table
			table = DatabaseUtils.getInstance ( getApplicationContext() ).getDaoSession ().getDeviceSerialsDao ().getTablename ();
			// Add the device serial to the lists
			tables.add ( daoSession.getDeviceSerialsDao ().getTablename () );
			json.add ( gson.toJson ( deviceSerials ) );
		} // End if
		
		// Check if there are device tracking to send
		if ( ! deviceTracking.isEmpty () ) {
            // Display debug output if required
            if ( TrackingUtils.showDebugOutput )
            	Log.d ( TrackingUtils.TAG , TAG + " : sendLocations: new locations : " + deviceTracking.size () );
			// Initialize / update the table argument
			table = table == null ? daoSession.getDeviceTrackingDao ().getTablename () : table + "," + daoSession.getDeviceTrackingDao ().getTablename ();
			// Add the device tracking to the lists
			tables.add ( daoSession.getDeviceTrackingDao ().getTablename () );
			json.add ( gson.toJson ( deviceTracking ) );
		} // End if
		
		// Add the user code to the arguments
		arguments.put ( UsersDao.Properties.UserCode.columnName , userCode );
		// Add the company code to the arguments
		arguments.put ( UsersDao.Properties.CompanyCode.columnName , companyCode );
		// Add the table argument
		arguments.put ( SyncUtils.TABLE , table );
		// Add the tables count argument
		arguments.put ( SyncUtils.TABLES_COUNT , String.valueOf ( tables.size () ) );
		// Iterate over the lists
		for ( int i = 0 ; i < tables.size () ; i ++ ) 
			 // Add the json argument
			arguments.put ( tables.get ( i ) , json.get ( i ) );
		// Add the checksum argument
		arguments.put ( SyncUtils.CHECKSUM , SyncUtils.getArgumentsChecksum ( getApplicationContext () , arguments ) );
		// Get an http response (via an http post resquest) for the provided url and arguments
		HttpResponse response = SyncUtils.post ( url , arguments );
		// Check if the response is valid
		if ( response == null )
			// Invalid response
			return;
		try {
			// Retrieve the string response
			String result = EntityUtils.toString ( response.getEntity () );
			// Check if no errors occurred
			if ( result.equalsIgnoreCase ( SyncUtils.SUCCESS ) ) {
				// Insert device serial (if needed)
				if ( deviceSerials.get ( 0 ).getId () == null )
					daoSession.getDeviceSerialsDao ().insert ( deviceSerials.get ( 0 ) );
				// Delete device tracking (if needed)
				if ( ! deviceTracking.isEmpty () )
					daoSession.getDeviceTrackingDao ().deleteInTx ( deviceTracking );
			} // End if
		} catch ( ParseException exception ) {
			// Do nothing
		} catch ( IOException exception ) {
			// Do nothing
		} catch ( JsonSyntaxException exception ) {
			// Do nothing
		} // End of try-catch block
    }
	
}