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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.QueryBuilder;
import me.SyncWise.Android.Network;
import me.SyncWise.Android.Database.ConnectionSettings;
import me.SyncWise.Android.Database.ConnectionSettingsDao;
import me.SyncWise.Android.Database.ConnectionSettingsUtils;
import me.SyncWise.Android.Database.DaoSession;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.IsProcessedUtils;
import me.SyncWise.Android.Database.MovementHeaders;
import me.SyncWise.Android.Database.MovementHeadersDao;
import me.SyncWise.Android.Database.PermissionsUtils;
import me.SyncWise.Android.Database.UsersDao;
import me.SyncWise.Android.Database.Visits;
import me.SyncWise.Android.Database.VisitsDao;
import me.SyncWise.Android.Modules.Call.CallAction;
import me.SyncWise.Android.Modules.ClientStock.ClientStockDetailsActivity;
import me.SyncWise.Android.Modules.Collections.CollectionDetailsActivity;
import me.SyncWise.Android.Modules.DataManagement.DataManagementActivity.RequestCode;
import me.SyncWise.Android.Modules.Movements.MovementDetailsActivity;
import me.SyncWise.Android.Modules.SalesInvoice.SalesInvoiceDetailsActivity;
import me.SyncWise.Android.Modules.SalesOrder.SalesOrderDetailsActivity;
import me.SyncWise.Android.Modules.SalesRFR.SalesRFRDetailsActivity;
import me.SyncWise.Android.Modules.Sync.MySSLSocketFactory;
import me.SyncWise.Android.Modules.Sync.SyncHelper;
import me.SyncWise.Android.Modules.Sync.SyncListener;
import me.SyncWise.Android.Modules.Sync.SyncUtils;
import me.SyncWise.Android.Modules.Sync.Synchronization;
import me.SyncWise.Android.Modules.Sync.TlsSniSocketFactory;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

/**
 * Service used to force an automatic synchronization to the server.<br>
 * This service is started periodically using the {@link android.app.AlarmManager AlarmManager}.
 * 
 * @author Elias
 *
 */
public abstract class AutomaticSyncService extends Service implements SyncListener , Synchronization {

    /**
     * Tag used to identify the source of a log message.
     */
    private static final String TAG = "AutomaticSyncService";
    
    /**
     * Connection object used to connect to the server through the network.
     */
    private ConnectionSettings connection;
    
	/**
	 * Integer used to host the total number of synchronizations for sending data.
	 */
	protected static int totalSet;

	/**
	 * List of objects lists used to host the sent entities.
	 */
	protected static ArrayList < ArrayList < ArrayList < Object > > > objectsSet;
	
    /*
     * Called by the system every time a client explicitly starts the service.
     *
     * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
     */
    @Override
    public int onStartCommand ( Intent intent , int flags, int startId ) {
    	// Display debug output if required
        if ( AutomaticSyncUtils.showDebugOutput )
        	Log.d ( AutomaticSyncUtils.TAG , TAG + " : onStartCommand" );

        // Display debug output if required
        if ( AutomaticSyncUtils.showDebugOutput )
        	Log.d ( AutomaticSyncUtils.TAG , TAG + " : onStartCommand: Force an automatic sync" );
        
		// Perform validity check
		if ( ! performValidyCheck () ) {
	        // Display debug output if required
	        if ( AutomaticSyncUtils.showDebugOutput )
	        	Log.d ( AutomaticSyncUtils.TAG , TAG + " : onStartCommand: Validity check failed" );
            // Stop the service
        	AutomaticSyncService.this.stopSelf();
			// Do nothing
			return START_STICKY;
		} // End if
		
		// Check if auto sync can be performed
		if ( ! canPerformAutoSync () ) {
	        // Display debug output if required
	        if ( AutomaticSyncUtils.showDebugOutput )
	        	Log.d ( AutomaticSyncUtils.TAG , TAG + " : onStartCommand: Cannot perform auto sync." );
            // Stop the service
        	AutomaticSyncService.this.stopSelf();
			// Do nothing
			return START_STICKY;
		} // End if
		
        // Retrieve a reference to the dao session
        DaoSession daoSession = DatabaseUtils.getInstance ( getApplicationContext () ).getDaoSession ();
        
        List < Visits > v = daoSession.getVisitsDao().queryRaw ( 
   				"WHERE " + VisitsDao.Properties.IsProcessed.columnName + "=? AND " + VisitsDao.Properties.VisitID.columnName + "!=?" ,
   				new String [] { String.valueOf ( IsProcessedUtils.isNotSync () ) , String.valueOf ( CallAction.getVisitID ( this ) ) } );
      	 List < MovementHeaders >	mh = daoSession.getMovementHeadersDao().queryRaw ( 
   				"WHERE " + MovementHeadersDao.Properties.IsProcessed.columnName + " =? " ,
   				new String [] { String.valueOf ( IsProcessedUtils.isNotSync () )    } );
   	Boolean b=false;
      	 if(v != null && v.size()!=0)
   	{  
      		 b=true;
    
       } // End if
   	if( mh != null && mh.size()!=0 )
   	{ 
   		b=true;
    
       } // End if  
       if( b == false ) {
   		AutomaticSyncService.this.stopSelf();
       //  Invalid connection
       	return START_STICKY;
       }   
        
        
        // Search for the GPRS link
        connection = daoSession.getConnectionSettingsDao ().queryBuilder ()
        		.where ( ConnectionSettingsDao.Properties.ConnectionSettingType.eq ( ConnectionSettingsUtils.Type.GPRS ) ).unique ();
        // Check if the connection is valid
        if ( connection == null ) {
            // Display debug output if required
            if ( AutomaticSyncUtils.showDebugOutput )
            	Log.d ( AutomaticSyncUtils.TAG , TAG + " : onStartCommand: Invalid url." );
            // Stop the service
        	AutomaticSyncService.this.stopSelf();
        	// Invalid connection
        	return START_STICKY;
        } // End if
        
        // Check if the user code is valid
        String userCode = DatabaseUtils.getCurrentUserCode ( getApplicationContext () );
        String companyCode = DatabaseUtils.getCurrentCompanyCode ( getApplicationContext () );
        if ( userCode == null || companyCode == null ) {
            // Display debug output if required
            if ( AutomaticSyncUtils.showDebugOutput )
            	Log.d ( AutomaticSyncUtils.TAG , TAG + " : onStartCommand: Invalid user / company code." );
            // Stop the service
        	AutomaticSyncService.this.stopSelf();
        	// Invalid connection
        	return START_STICKY;
        } // End if
		
		// Compute the URL string
		String url = connection.getConnectionSettingURL ();
		// Try to connect to service
//		SyncHelper syncHelper = new SyncHelper ( ConnectionSettingsUtils.getConnectMethodPath ( url ) );
//		syncHelper.set ( getApplicationContext () , new ArrayList < AbstractDao < ? , ? > > () , new ArrayList < ArrayList < Object > > () , userCode , companyCode , AutomaticSyncService.this , RequestCode.PING , true );
		Handler handler = new Handler();
		TaskCanceler taskCanceler;
		IsLisencedSet d = new IsLisencedSet(getApplicationContext () , url,userCode,companyCode );
		taskCanceler = new TaskCanceler(d);
		handler.postDelayed(taskCanceler, 50000);
		d.execute() ;
        // Make the system to re-create the service if this service's process is killed while it is started
        return START_STICKY;
    }
    /**
   	 * Gets an http response (via an http post request) for the provided url and arguments.
   	 * 
   	 * @param url	String holding the url.
   	 * @param arguments	Map hosting the http arguments as key and value pairs.
   	 * @return	The Http response as a result of the http post request.
   	 */
   	public static String postData ( final String url , Map < String , String > arguments ,Context context) {
   		// Check if the url is valid
   		if ( TextUtils.isEmpty ( url ) )
   			// Invalid url
   			return null;
   		// Declare and initialize a collection of HTTP protocol and framework parameters.
   		HttpParams httpParameters = new BasicHttpParams ();
   		
   		String userCode = DatabaseUtils.getCurrentUserCode ( context );
   	      // Retrieve the company code
   	    String companyCode = DatabaseUtils.getCurrentCompanyCode ( context );
   	    int timeout=0;
   	    if(userCode==null || companyCode==null)
   	    timeout=30000;
   	    else
   	    	timeout=PermissionsUtils.getTimeOut (context,  userCode ,companyCode);

   		// Set the timeout in milliseconds until a connection is established
   		HttpConnectionParams.setConnectionTimeout ( httpParameters , timeout );
   		// Sets the default socket timeout in milliseconds which is the timeout for waiting for data
   		HttpConnectionParams.setSoTimeout ( httpParameters , timeout );
   	//	SchemeRegistry schemeRegistry = new SchemeRegistry ();
   		try {
   		//KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
   	  //  trustStore.load(null, null);

   	   // SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
   	   // sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
    
       //	schemeRegistry.register (new Scheme ("http",PlainSocketFactory.getSocketFactory (), 8090));
       
      // 	schemeRegistry.register(new Scheme("https", sf, 443));

   		} catch (Exception e) {}

       	httpParameters.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);
   		// Initialize an http client using its default implementation
   		//HttpClient httpClient = new DefaultHttpClient (new ThreadSafeClientConnManager(httpParameters, schemeRegistry), httpParameters );
       	SchemeRegistry registry = new SchemeRegistry();
    	registry.register(new Scheme("https", new TlsSniSocketFactory(), 443));
    	registry.register (new Scheme ("http",PlainSocketFactory.getSocketFactory (), 8090));
    	HttpClient httpClient  = new DefaultHttpClient(new ThreadSafeClientConnManager(httpParameters, registry),httpParameters);
    	
       	
       	// Initialize a http post request using the specified url
   		HttpPost httpPost = new HttpPost ( url );
   		// Check if the arguments map is valid
   		if ( arguments != null ) {
   			// Declare a list of name value pairs, used to encapsulate the arguments
   			List < NameValuePair > nameValuePairs = new ArrayList < NameValuePair > ( arguments.size () );
   		    String key, value;
   		    Iterator < String > itKeys = arguments.keySet ().iterator (); 
   		 
   		    // Iterate over all argument keys
   		    while ( itKeys.hasNext () ) {
   		    	// Retrieve the argument key
   		    	key = itKeys.next ();
   		    	// Retrieve the argument value
   		    	value = arguments.get ( key );
   		    	// Build a basic name value pair using the key and value above
   		        nameValuePairs.add ( new BasicNameValuePair ( key , value ) );
   		    } // End while loop
   		 
   		    try {
   		    // Hand the entity to the request
   		    	httpPost.setEntity ( new UrlEncodedFormEntity ( nameValuePairs , HTTP.UTF_8 ) );
   		    	// Execute the http post request using the default context
   		    	HttpResponse response = httpClient.execute ( httpPost );
   		    	// Return the http response
   		    	if ( response == null )
   					// Invalid response
   					return "-1:ERROR invalid response";
   			 
   					// Retrieve the string response
   					String result = EntityUtils.toString ( response.getEntity () );
   					Log.e( "Response", "  " + result.toString() );
   					return result ;//answer[1];
   				} catch ( ParseException exception ) {
   					// Do nothing
   				 
   		    } catch ( UnsupportedEncodingException exception ) {
   				// Do nothing.
   			} catch ( ClientProtocolException exception ) {
   				// Do nothing.
   			} catch ( IOException exception ) {
   			//	String error=exception.getMessage().toString();
   			//	Log.e("ali","  "+error);
   				// Do nothing.
   			} // End try-catch block
   		} // End if
   		return null;
   	}
   	
   	
    
   private class IsLisencedSet extends AsyncTask <Void, Void, String>  {
   	 private Context activity;
        private String url;
   	 private String userCode;
   	 private String companyCode;
   	
   	public IsLisencedSet (final Context activity, final String url ,final String userCode,final String companyCode) {
   	    this.url = url;
   	    this.userCode=userCode;
   	    this.companyCode=companyCode;
   	    this.activity = activity;
   	}

   	@Override
   	protected void onPreExecute () {
   		// Display indeterminate progress before starting a new thread
           super.onPreExecute();
         
   	}
     
   	@Override
   	protected String doInBackground ( Void ... params ) {
   		 
   		
   	    Map < String , String > arguments = new HashMap < String , String > ();
   		
   		// Add the user code argument
   		arguments.put ( UsersDao.Properties.UserCode.columnName , userCode );
   		
   		// Add the company code argument
   		arguments.put ( UsersDao.Properties.CompanyCode.columnName , companyCode );
   		
   		String serial=android.os.Build.SERIAL;
   		
   		arguments.put ( "SerialNumber" , serial );
   	 
   		// Add the checksum argument
   		arguments.put ( SyncUtils.CHECKSUM , SyncUtils.getArgumentsChecksum ( activity , arguments ) );
   		// Fire the http post request and retrieve the http response
   		String res= postData (   ConnectionSettingsUtils.getIsLisencedMethodPath ( url )   ,arguments ,activity ); 
		// Fire the http post request and retrieve the http response
		if(res==null)
			return "down";
		
		return res;
	}

	@Override
	protected void onPostExecute(String result) {
		
		final String answer   = result.toString()  ;
		if(answer.equals("down"))
		{       
			 
		return;
		}
       		// TODO Auto-generated method stub
   			if ( answer  .equals("1" ) ) {
   				try {
   					 
   					SyncHelper syncHelper = new SyncHelper ( ConnectionSettingsUtils.getConnectMethodPath ( url ) );
   					syncHelper.set ( getApplicationContext () , new ArrayList < AbstractDao < ? , ? > > () , new ArrayList < ArrayList < Object > > () , userCode , companyCode , AutomaticSyncService.this , RequestCode.PING , true );
   				}
   				catch ( Exception ex) {
   				 
   				}
   				finally {
   				
   				}
   			}else{
   			 	AutomaticSyncService.this.stopSelf();
   			}
   					 
   	}
   }
   	public class TaskCanceler implements Runnable{
   	    private AsyncTask task;
   	    private Context context;
   	    public TaskCanceler(AsyncTask task) {
   	        this.task = task;
   	    }
   	
   	     @Override
   	     public void run() {
   	        if (task.getStatus() == AsyncTask.Status.RUNNING )
   	        {
   	        	task.cancel(true);
   	       
   	   		 //	AppDialog.getInstance ().displayAlert(context , null , getString ( R.string.no_network_connection_message1 ) , AppDialog.ButtonsType.OK,null );
   			
   	        }
   	        }
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
        if ( AutomaticSyncUtils.showDebugOutput )
        	Log.d ( AutomaticSyncUtils.TAG , TAG + " : onDestroy" );
    }
    
	/**
	 * Performs a validity check to ensure that the data management process can proceed.
	 * 
	 * @return	Boolean indication if the validity check succeeded (returns {@code true}) or failed (returns {@code false}).
	 */
	public boolean performValidyCheck () {
		// Check for network availability
		if ( ! Network.networkAvailability ( this ) )
			// Exit method
			return false;
		// Otherwise method can proceed
		return true;
	}
    
    /**
     * Determines whether the auto sync module can be performed or not.<br>
     * The decision is based on whether the user is using conflicting modules that might conflict data if any changes occur.<br>
     * For example, if the user is using the Sales Order module, the auto sync should not be performed until the user leaves the screen (if the module will send the performed sales orders).<br>
     * In order to perform any additional checking, override this method.
     * 
     * @return	Boolean indicating if the auto sync can be performed or not.
     */
    public boolean canPerformAutoSync () {
    	/*
    	 * Determine if the one of the following screens is displayed:
    	 * - sales order details activity
    	 * - request for return activity
    	 * - collection details activity
    	 * - client stock count details activity
    	 * - movement details activity
    	 */
    	if ( SalesOrderDetailsActivity.isDisplayed || SalesRFRDetailsActivity.isDisplayed || CollectionDetailsActivity.isDisplayed || ClientStockDetailsActivity.isDisplayed || MovementDetailsActivity.isDisplayed || SalesInvoiceDetailsActivity.isDisplayed)
    		// Cannot perform auto sync
    		return false;
    	// Otherwise, the auto sync can be performed
    	return true;
    }

	/*
	 * Gets all the abstract dao tables that should be requested during sync, along with their appropriate entities types.
	 *
	 * @see me.SyncWise.Android.Modules.Sync.Synchronization#getSyncTables_Get()
	 */
	@Override
	public Map < AbstractDao < ? , ? > , Type > getSyncTables_Get () {
		// Get not used in automatic sync
		return null;
	}

	/*
	 * Gets all the abstract dao tables that should be delivered during sync, grouped in multiple lists to preserver relationships.
	 *
	 * @see me.SyncWise.Android.Modules.Sync.Synchronization#getSyncTables_Set()
	 */
	@Override
	public abstract ArrayList < ArrayList < AbstractDao < ? , ? > > > getSyncTables_Set ();

	/**
	 * Determines if the sync progress has ended.<br>
	 * If so, a validation check is performed over the sent data, and a dialog is displayed accordingly.
	 */
	private void checkSetProgress () {
		// Check if the entities lists is valid
		if ( objectsSet == null ) {
			// Invalid list
            // Stop the service
        	AutomaticSyncService.this.stopSelf();
		} // End if
		
		// Check if upload finished
		else if ( totalSet == objectsSet.size () ) {
			// Sync ended
			// Check if all data is valid
			boolean valid = true;
			// Iterate over all entities 
			for ( ArrayList < ArrayList < Object > > objects : objectsSet )
				// Check if list is valid
				if ( objects == null ) {
					// Invalid entities list
					valid = false;
					break;
				} // End if
			
			// Check if all entities are valid
			if ( valid ) {
				// Update the sent objects (the is processed value should be updated in DB)
				try {
					// Begin transaction
					DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ().beginTransaction ();
					
					// Iterate over all entities list
					for ( ArrayList < ArrayList < Object > > entities : objectsSet )
						// Iterate over each group
						for ( ArrayList < Object > group : entities ) {
							// Check if the list is empty
							if ( group.isEmpty () )
								// Skip the current empty list
								continue;
							// Otherwise check if the objects have the is processed field
							// Retrieve a reference to the entity class
							Class < ? > entityClass = group.get ( 0 ).getClass ();
							// Retrieve the entity's methods
							Method methods [] = entityClass.getDeclaredMethods ();
							// Determine if the current entity has a setter for an IsProcessed field
							Method setProcessed = null;
							// Iterate over all the entity methods
							for ( Method method : methods )
								// Determine if the current method is an IsProcessed setter
								if ( method.getName ().equalsIgnoreCase ( "setisprocessed" ) ) {
									// Initialize the IsProcessed setter
									setProcessed = method;
									// Exit loop
									break;
								} // End if
							// Make sure that the class has the is processed field
							if ( setProcessed == null )
								// No need to update the current list
								continue;
							// Iterate over each entity
							for ( Object entity : group )
								// Update data
								DatabaseUtils.getInstance ( this ).getDaoSession ().update ( entity );
						} // End for each
						
					// Commit changes
					DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ().setTransactionSuccessful ();
					
					// Clear cached objects in session
					DatabaseUtils.getInstance ( this ).getDaoSession ().clear ();
				} catch ( Exception exception ) {
					// Invalid entities list
					valid = false;
				} finally {
    				// End transaction
    				DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ().endTransaction ();
    			} // End try-catch-finally block
			} // End if

			// Check if all entities are valid
			if ( ! valid ) {
				// Clear cached objects in session
				DatabaseUtils.getInstance ( this ).getDaoSession ().clear ();
		    	// Display debug output if required
		        if ( AutomaticSyncUtils.showDebugOutput )
		        	Log.d ( AutomaticSyncUtils.TAG , TAG + " : checkSetProgress: auto sync failed." );
			} // End else
			else {
		    	// Display debug output if required
		        if ( AutomaticSyncUtils.showDebugOutput )
		        	Log.d ( AutomaticSyncUtils.TAG , TAG + " : checkSetProgress: auto sync ended successfully." );
			} // End if
			
			try {
				// Begin transaction
				DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ().beginTransaction ();
					
				// Execute data collector
				SyncUtils.dataCollector ( this );
				
				// Commit changes
				DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ().setTransactionSuccessful ();
				
				// Clear variables
				totalSet = 0;
				objectsSet.clear ();
				objectsSet = null;
			} catch ( Exception exception ) {
				// Invalid entities list
				valid = false;
			} finally {
				// End transaction
				DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ().endTransaction ();
			} // End try-catch-finally block

            // Stop the service
        	AutomaticSyncService.this.stopSelf();
		} // End else if
	}
	
	/*
	 * Call back method executed before determining if the sync result is a success or a failure.
	 *
	 * @see me.SyncWise.Android.Modules.Sync.SyncListener#onPreFinish(int)
	 */
	@Override
	public void onPreFinish ( int requestCode ) {
		// Do nothing.
	}

	/*
	 * Call back method executed if the get sync process is a success.
	 *
	 * @see me.SyncWise.Android.Modules.Sync.SyncListener#onGetSuccess(de.greenrobot.dao.AbstractDao, java.util.ArrayList, int)
	 */
	@Override
	public void onGetSuccess ( AbstractDao < ? , ? > dao , ArrayList < Object > entities , int requestCode ) {
		// Do nothing.
	}

	/*
	 * Call back method executed if the set sync process is a success.
	 *
	 * @see me.SyncWise.Android.Modules.Sync.SyncListener#onSetSuccess(int)
	 */
	@Override
	public void onSetSuccess ( ArrayList < ArrayList < Object > > entities , int requestCode ) {
		// Determine if this is a ping to the server
		if ( requestCode == RequestCode.PING ) {
			// Retrieve sync tables
			ArrayList < ArrayList < AbstractDao < ? , ? > > > tables = getSyncTables_Set ();
			// Check if the sync tables
			// AND the connection object are valid
			if ( connection == null || tables == null || tables.isEmpty () ) {
		    	// Display debug output if required
		        if ( AutomaticSyncUtils.showDebugOutput )
		        	Log.d ( AutomaticSyncUtils.TAG , TAG + " : onSetSuccess: invalid data. Auto sync ended prematurely." );
				// Stop the service
				AutomaticSyncService.this.stopSelf();
				// Exit method
				return;
			} // End if
			
			// Compute the URL string
			String url = connection.getConnectionSettingURL ();
			// Retrieve a reference to the dao session
			DaoSession daoSession = DatabaseUtils.getInstance ( this ).getDaoSession ();
			// Set the total number of tables
			totalSet = tables.size ();
			// Initialize the entities list
			objectsSet = new ArrayList < ArrayList < ArrayList < Object > > > ();
			ArrayList < ArrayList < Object > > _entities;
			// Iterate over all tables
			for ( ArrayList < AbstractDao < ? , ? > > group : tables ) {
				// Initialize the entities list
				_entities = new ArrayList < ArrayList<Object> > ();
				for ( AbstractDao < ? , ? > dao : group ) {
					// Declare and initialize a list of objects
					ArrayList < Object > objects = null;
					// Check if dao has custom query
					QueryBuilder < ? > queryBuilder = AutoSyncCustomQueryBuilder.getCustomQueryBuilderAutoSync ( this , dao , daoSession );
					// Check if dao has custom result
					List < ? > customResult = AutoSyncCustomResult.getCustomResultAutoSync ( this , dao , daoSession );
					// Check if dao has IsProcessed
					Property isProcessed = DatabaseUtils.getIsProcessed ( dao );
					// Determine if dao has a custom query
					if ( queryBuilder != null )
						// Execute custom query
						objects = new ArrayList < Object > ( queryBuilder.list () );
					// Determine if dao has a custom result
					else if ( customResult != null ) {
						objects = new ArrayList < Object > ();
						objects.addAll ( customResult );
					} // End else if
					// Determine if dao has IsProcessed field
					else if ( isProcessed != null ) 
						// Query entities based on IsProcessed field
						objects = new ArrayList < Object > ( dao.queryBuilder ().where ( isProcessed.eq ( IsProcessedUtils.isNotSync () ) ).list () );
					else
						// Load all entities
						objects = new ArrayList < Object > ( dao.loadAll () );
					
					// Update entities accordingly
					SyncUtils.updateProcessedEntities ( this , objects );
					// Add the objects to the entities list
					_entities.add ( objects );
				} // End for each
				
				// Clear cached objects in session
				DatabaseUtils.getInstance ( this ).getDaoSession ().clear ();
				
				// Send the dao table data
				SyncHelper syncHelper = new SyncHelper ( ConnectionSettingsUtils.getSetMethodPath ( url ) );
				syncHelper.set ( getApplicationContext () , group , _entities , DatabaseUtils.getCurrentUserCode ( this ) , DatabaseUtils.getCurrentCompanyCode ( this ) , this , RequestCode.ON_GOING , true );
			} // End for each
		} // End if
		else {
			// Sync succeeded
			// Check if total is valid
			if ( totalSet == 0 )
				// Ignore sync
				return;
			// Initialize objects list if needed
			if ( objectsSet == null )
				objectsSet = new ArrayList < ArrayList < ArrayList < Object > > > ();
			// Add the retrieved users to the entities list
			objectsSet.add ( entities );
			// Check sync progress
			checkSetProgress ();
		} // End else
	}

	/*
	 * Call back method executed if the get sync process is a failure.
	 *
	 * @see me.SyncWise.Android.Modules.Sync.SyncListener#onGetFailure(de.greenrobot.dao.AbstractDao, int)
	 */
	@Override
	public void onGetFailure ( AbstractDao < ? , ? > dao , int requestCode ) {
		// Do nothing.
	}

	/*
	 * Call back method executed if the set sync process is a failure.
	 *
	 * @see me.SyncWise.Android.Modules.Sync.SyncListener#onSetFailure(int)
	 */
	@Override
	public void onSetFailure ( int requestCode ) {
		// Sync failed
		// Determine if this is a ping to the server
		if ( requestCode == RequestCode.PING ) {
	    	// Display debug output if required
	        if ( AutomaticSyncUtils.showDebugOutput )
	        	Log.d ( AutomaticSyncUtils.TAG , TAG + " : onSetFailure: invalid ping. Auto sync ended." );
			// Stop the service
			AutomaticSyncService.this.stopSelf();
			// Exit method
			return;
		} // End if
		// Check if total is valid
		if ( totalSet == 0 ) {
			// Stop the service
			AutomaticSyncService.this.stopSelf();
			// Ignore sync
			return;
		} // End if
		// Initialize objects list if needed
		if ( objectsSet == null )
			objectsSet = new ArrayList < ArrayList < ArrayList < Object > > > ();
		// Add null to the list
		objectsSet.add ( null );
		// Check sync progress
		checkSetProgress ();
	}

	/*
	 * Call back method executed after the sync process executed.
	 *
	 * @see me.SyncWise.Android.Modules.Sync.SyncListener#onPostFinish(int)
	 */
	@Override
	public void onPostFinish ( int requestCode ) {
		// Do nothing.
	}
    
}