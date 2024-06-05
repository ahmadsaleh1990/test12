/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.DataManagement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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

import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppFragmentPagerAdapter;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.Network;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Vibration;
 
import me.SyncWise.Android.Database.ClientAssetStatusDao;
import me.SyncWise.Android.Database.ClientAssetsPicturesDao;
import me.SyncWise.Android.Database.ClientAssetsPicturesUtils;
import me.SyncWise.Android.Database.ClientDocumentImagesDao;
import me.SyncWise.Android.Database.ClientDocumentImagesUtils;
import me.SyncWise.Android.Database.ClientStockCountHeadersDao;
import me.SyncWise.Android.Database.CollectionHeadersDao;
import me.SyncWise.Android.Database.ConnectionSettings;
import me.SyncWise.Android.Database.ConnectionSettingsDao;
import me.SyncWise.Android.Database.ConnectionSettingsUtils;
import me.SyncWise.Android.Database.DaoSession;
import me.SyncWise.Android.Database.DatabaseUtils;
  
import me.SyncWise.Android.Database.DeviceSerials;
import me.SyncWise.Android.Database.DeviceSerialsDao;
import me.SyncWise.Android.Database.Divisions;
import me.SyncWise.Android.Database.DivisionsDao;
import me.SyncWise.Android.Database.ForceSyncJourneys;
import me.SyncWise.Android.Database.ForceSyncJourneysDao;
import me.SyncWise.Android.Database.IsProcessedUtils;
import me.SyncWise.Android.Database.JourneysDao;
import me.SyncWise.Android.Database.JourneysUtils;
import me.SyncWise.Android.Database.MovementHeadersDao;
import me.SyncWise.Android.Database.NewClientImagesDao;
import me.SyncWise.Android.Database.NewClientImagesUtils;
import me.SyncWise.Android.Database.PermissionsUtils;
import me.SyncWise.Android.Database.SOSTrackerHeadersDao;
import me.SyncWise.Android.Database.ShareOfShelfTrackerDao;
import me.SyncWise.Android.Database.StatusUtils;
import me.SyncWise.Android.Database.SurveyAnswersDao;
import me.SyncWise.Android.Database.TransactionHeadersDao;
import me.SyncWise.Android.Database.UserDivisions;
import me.SyncWise.Android.Database.UserDivisionsDao;
import me.SyncWise.Android.Database.Users;
import me.SyncWise.Android.Database.UsersDao;
import me.SyncWise.Android.Database.Visits;
import me.SyncWise.Android.Database.VisitsDao;
import me.SyncWise.Android.Gson.BaseTimerFragmentActivity;
import me.SyncWise.Android.Gson.CommonUtilities;
import me.SyncWise.Android.Modules.DataManagement.DataManagementActivity.RequestCode;
import me.SyncWise.Android.Modules.Sync.CustomQueryBuilder;
import me.SyncWise.Android.Modules.Sync.CustomResult;
import me.SyncWise.Android.Modules.Sync.MySSLSocketFactory;
import me.SyncWise.Android.Modules.Sync.NullHostNameVerifier;
import me.SyncWise.Android.Modules.Sync.SyncHelper;
import me.SyncWise.Android.Modules.Sync.SyncListener;
import me.SyncWise.Android.Modules.Sync.SyncUtils;
import me.SyncWise.Android.Modules.Sync.Synchronization;
import me.SyncWise.Android.Modules.Sync.TLSSocketFactory;
import me.SyncWise.Android.Modules.Sync.TlsSniSocketFactory;
import me.SyncWise.Android.Modules.Sync.Automatic.AutomaticSyncUtils;
import me.SyncWise.Android.Utilities.AppFragment;
import me.SyncWise.Android.Widgets.Baguette;
import me.SyncWise.Android.Widgets.CustomViewPager;
import me.SyncWise.Android.Widgets.ViewPagerIndicator.TabPageIndicator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.QueryBuilder;

/**
 * Activity used as database management screen, used to export the local database to the SD card.
 * 
 * @author Elias
 * @sw.todo	<b>Database Activity Implementation :</b><br>
 * <ul>
 * <li>Extend this class.</li>
 * <li>Implement the {@link #getSyncTables_Set()} method to determine the tables to export.</li>
 * <li>Implement the {@link #getSyncTables_Get()} method to determine the tables to import.</li>
 * <li>Add your new class and the permissions below in the {@code AndroidManifest.xml} file :<br>
 * <ul>
 * <li>{@code <uses-permission android:name="android.permission.VIBRATE"/>}</li>
 * <li>{@code <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />}</li>
 * <li>{@code <uses-permission android:name="android.permission.INTERNET" />}</li>
 * <li>{@code<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />}</li>
 * </ul></li>
 * </ul>
 *
 */
public abstract class DataManagementActivity extends BaseTimerFragmentActivity implements SyncListener , Synchronization {
	
	/**
	 * Bundle key used to indicate if a direct sync is to be issued.
	 */
	public final static String DIRECT_SYNC = DataManagementActivity.class.getName() + ".DIRECT_SYNC";
	
	/**
	 * Flag indicating if the debug mode is enabled.
	 */
	private static final boolean debug = true;
	
	/**
	 * Integer used to host the total number of synchronizations for sending data.
	 */
	protected static int totalSet;
	
	/**
	 * Integer used to host the total number of synchronizations for getting data.
	 */
	protected static int totalGet;
	
	/**
	 * List of abstract dao tables that are retrieved during sync.
	 */
	protected static ArrayList < AbstractDao < ? , ? > > tablesGet;
	
	/**
	 * Map of objects lists used to host the retrieved entities.
	 */
	protected static HashMap < String , ArrayList < Object > > objectsGet;
	
	/**
	 * List of objects lists used to host the sent entities.
	 */
	protected static ArrayList < ArrayList < ArrayList < Object > > > objectsSet;
	
	/**
	 * List of abstract dao group tables used to host the failed tables.
	 */
	private static ArrayList < String > failedTables;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #setThenGet}.
	 */
	public static final String SET_THEN_GET = DataManagementActivity.class.getName () + ".SET_THEN_GET";
	
	/**
	 * Flag used to indicate if data should be exported after successfully importing data.
	 */
	private boolean setThenGet;

	/**
	 * List of available {@link me.SyncWise.Android.Database.ConnectionSettings ConnectionSettings} objects for synchronization.
	 */
	private List < ConnectionSettings > connections;

	/**
	 * String used to host the current URL used to perform synchronization.
	 */
	private String url;
	
	/**
	 * Helper class used to define the various sync request codes.
	 * 
	 * @author Elias
	 *
	 */
	public static class RequestCode {
		public static final int PING = 0;
		public static final int ON_GOING = 1;
	}
	
	/*
	 * Gets all the abstract dao tables that should be requested during sync, along with their appropriate entities types.
	 *
	 * @see me.SyncWise.Android.Modules.Sync.Synchronization#getSyncTables_Get()
	 */
	public abstract Map < AbstractDao < ? , ? > , Type > getSyncTables_Get ();
	
	/*
	 * Gets all the abstract dao tables that should be delivered during sync, grouped in multiple lists to preserver relationships.
	 *
	 * @see me.SyncWise.Android.Modules.Sync.Synchronization#getSyncTables_Set()
	 */
	public abstract ArrayList < ArrayList < AbstractDao < ? , ? > > > getSyncTables_Set ();
	
	/*
	 * Called when the activity is starting.
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate ( Bundle savedInstanceState ) {
		// Superclass method call	
		super.onCreate ( savedInstanceState );
		// Set the activity content from a layout resource.
		setContentView ( R.layout.data_management_activity );
		// Change the title associated with this activity
		setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.database_activity_title ) );
		// Apply the view pager indicator theme
		setTheme ( R.style.Theme_PageIndicatorDefaults );
		// Indicate that the screen should remain on
		findViewById ( android.R.id.content ).setKeepScreenOn ( true );
		
		// Determine if a saved instance state is provided
		if ( savedInstanceState != null ) {
			// Restore the content provided by the saved instance state 
			setThenGet = savedInstanceState.getBoolean ( SET_THEN_GET );
		} // End if
		setThenGet=false;
		// Declare and initialize a list used to host the fragments descriptions
		ArrayList < AppFragment > fragments = new ArrayList < AppFragment > ();
		// Define the required fragments
		fragments.add ( new AppFragment ( AppResources.getInstance ( this ).getString ( this , R.string.export_label ) , R.layout.data_export_fragment ) );
		fragments.add ( new AppFragment ( AppResources.getInstance ( this ).getString ( this , R.string.import_label ) , R.layout.data_import_fragment ) );
		// Add any additional fragments
		fragments.addAll ( additionalFragments () );
//		Visits visit= DatabaseUtils.getInstance ( this ).getDaoSession ().getVisitsDao ().queryBuilder()
//				.where(VisitsDao.Properties.VisitID.eq("20191030080039068")).unique();
//		if(visit!=null)
//		{
//			String j=visit.getJourneyCode();
//			String c=visit.getCompanyCode();
//			visit.setCompanyCode(j);
//			visit.setJourneyCode(c);
//			DatabaseUtils.getInstance ( this ).getDaoSession ().getVisitsDao ().update(visit);
//		}
		// Retrieve a reference to the indicator
		TabPageIndicator indicator = (TabPageIndicator) findViewById ( R.id.indicator );
		// Retrieve a reference to the view pager
		ViewPager viewPager = (ViewPager) findViewById ( R.id.pager );
		
		// Set the fragment pager adapter
		viewPager.setAdapter ( new AppFragmentPagerAdapter ( getSupportFragmentManager() , fragments , getFragmentClass () ) );
		// Link the view pager to the indicator
		indicator.setViewPager ( viewPager );
		
		// Determine if the debug mode is enabled
		if ( debug )
	    	// Display the export button label
	    	( (Button) findViewById ( R.id.button_export_database ) ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.export_label ) );
		else
			// Hide the export button label
			findViewById ( R.id.button_export_database ).setVisibility ( View.GONE );
    	
    	// Retrieve the list of all connection settings
    	connections = DatabaseUtils.getInstance ( this ).getDaoSession ().getConnectionSettingsDao ().loadAll ();
    	// Declare and initialize a new connection adapter used to populate the connection settings (URLs)
    	ConnectionAdapter connectionAdapter = new ConnectionAdapter ( this , android.R.layout.simple_spinner_item , connections );
		// Sets the layout resource to create the drop down views
    	connectionAdapter.setDropDownViewResource ( R.layout.data_management_activity_connection_item );
    	// Set the spinner adapter
    	( (Spinner) findViewById ( R.id.spinner ) ).setAdapter ( connectionAdapter );
    	String userCode = DatabaseUtils.getCurrentUserCode ( getApplicationContext () );
        // Retrieve the company code
        String companyCode = DatabaseUtils.getCurrentCompanyCode ( getApplicationContext () );
    	
        if( PermissionsUtils.getSendSerialTablet( this, userCode, companyCode ) ){
    		sendSeial d = new sendSeial(this);
		 

			
		 
		    d.execute( "") ;	
    	} 
    	// Retrieve the list of all connection settings
    	connections = DatabaseUtils.getInstance ( this ).getDaoSession ().getConnectionSettingsDao ().loadAll ();
    	Integer nbrDay1=16    ;
    	Calendar today = Calendar.getInstance ();
	 	Calendar date = Calendar.getInstance ();
		date.set (  2020  ,0 ,
		nbrDay1 , 0 , 0 , 0 );
		date.set ( Calendar.MILLISECOND , 0 );
		Integer nbrDay2=17    ; 
	 	Calendar date1= Calendar.getInstance ();
		date1.set ( 2020 ,0 ,
				nbrDay2 , 0 , 0 , 0 );
		date1.set ( Calendar.MILLISECOND , 0 );
	 	
		List <Visits> v=	DatabaseUtils.getInstance ( this ).getDaoSession ()
				.getVisitsDao ().queryBuilder().where(VisitsDao.Properties.StampDate.gt(date.getTime()),
						VisitsDao.Properties.StampDate.lt( date1.getTime())).list();
		if ( v != null && v.size()>0 ){
		for(Visits vv:v)
			{vv.setStampDate(date1.getTime());
			DatabaseUtils.getInstance ( this ).getDaoSession ()
			.getVisitsDao ().update(vv);
			}
			
 
    	String SQL1 = "UPDATE " + TransactionHeadersDao.TABLENAME + " " +
				"SET " + TransactionHeadersDao.Properties.IsProcessed.columnName + " = ?   " 
				+"WHERE " + TransactionHeadersDao.Properties.StampDate.columnName + " > ? and "
				+ TransactionHeadersDao.Properties.StampDate.columnName + " < ?   "
				 ;
		// Compute the selection arguments
		String selectionArguments1 [] = new String [] {
				String.valueOf ( IsProcessedUtils.isNotSync () ) , String.valueOf ( date.getTimeInMillis ()  )
				,String.valueOf ( date1.getTimeInMillis ()  )
		 
		};
		
 
		DatabaseUtils.getInstance ( DataManagementActivity.this ).getDaoSession ().getDatabase ().execSQL ( SQL1 , selectionArguments1 );

	 
    	  SQL1 = "UPDATE " + JourneysDao.TABLENAME + " " +
				"SET " + JourneysDao.Properties.IsProcessed.columnName + " = ?   " 
				+"WHERE " + JourneysDao.Properties.StampDate.columnName + " > ? and "
				+ JourneysDao.Properties.StampDate.columnName + " < ?   " 	 ;
		// Compute the selection arguments
		  selectionArguments1   = new String [] {
				String.valueOf ( IsProcessedUtils.isNotSync () ) , String.valueOf ( date.getTimeInMillis ()  )
				,String.valueOf ( date1.getTimeInMillis ()  )
		 
		};
		
 
		DatabaseUtils.getInstance ( DataManagementActivity.this ).getDaoSession ().getDatabase ().execSQL ( SQL1 , selectionArguments1 );

		
 
  	  SQL1 = "UPDATE " + VisitsDao.TABLENAME + " " +
				"SET " + VisitsDao.Properties.IsProcessed.columnName + " = ?  , " 
				+ VisitsDao.Properties.StampDate.columnName + " = ?"
				+"WHERE " + VisitsDao.Properties.StampDate.columnName + " > ? and "
				+ VisitsDao.Properties.StampDate.columnName + " < ?   " 	 ;
		// Compute the selection arguments
		  selectionArguments1   = new String [] {
				String.valueOf ( IsProcessedUtils.isNotSync () ) ,String.valueOf ( date.getTimeInMillis ()  ), String.valueOf ( date.getTimeInMillis ()  )
				,String.valueOf ( date1.getTimeInMillis ()  )
		 
		};
		

		DatabaseUtils.getInstance ( DataManagementActivity.this ).getDaoSession ().getDatabase ().execSQL ( SQL1 , selectionArguments1 );

		
	 
	  	  SQL1 =     "UPDATE " + CollectionHeadersDao.TABLENAME + " " +
					 "SET " + CollectionHeadersDao.Properties.IsProcessed.columnName + " = ?   " 
					+ "WHERE " + CollectionHeadersDao.Properties.StampDate.columnName + " > ? and "
					+  CollectionHeadersDao.Properties.StampDate.columnName + " < ?   " 	 ;
			// Compute the selection arguments
			  selectionArguments1   = new String [] {
					String.valueOf ( IsProcessedUtils.isNotSync () ) , String.valueOf ( date.getTimeInMillis ()  )
					,String.valueOf ( date1.getTimeInMillis ()  )
			 
			};
			

			DatabaseUtils.getInstance ( DataManagementActivity.this ).getDaoSession ().getDatabase ().execSQL ( SQL1 , selectionArguments1 );

		
			  SQL1 =     "UPDATE " + ClientStockCountHeadersDao.TABLENAME + " " +
						 "SET " + ClientStockCountHeadersDao.Properties.IsProcessed.columnName + " = ?   " 
						+ "WHERE " + ClientStockCountHeadersDao.Properties.StampDate.columnName + " > ? and "
						+  ClientStockCountHeadersDao.Properties.StampDate.columnName + " < ?   " 	 ;
				// Compute the selection arguments
				  selectionArguments1   = new String [] {
						String.valueOf ( IsProcessedUtils.isNotSync () ) , String.valueOf ( date.getTimeInMillis ()  )
						,String.valueOf ( date1.getTimeInMillis ()  )
				 
				};
				

				DatabaseUtils.getInstance ( DataManagementActivity.this ).getDaoSession ().getDatabase ().execSQL ( SQL1 , selectionArguments1 );
				
				
				SQL1 =     "UPDATE " + ClientAssetStatusDao.TABLENAME + " " +
						 "SET " + ClientAssetStatusDao.Properties.IsProcessed.columnName + " = ?   " 
						+ "WHERE " + ClientAssetStatusDao.Properties.StampDate.columnName + " > ? and "
						+  ClientAssetStatusDao.Properties.StampDate.columnName + " < ?   " 	 ;
				// Compute the selection arguments
				  selectionArguments1   = new String [] {
						String.valueOf ( IsProcessedUtils.isNotSync () ) , String.valueOf ( date.getTimeInMillis ()  )
						,String.valueOf ( date1.getTimeInMillis ()  )
				 
				};
				

				DatabaseUtils.getInstance ( DataManagementActivity.this ).getDaoSession ().getDatabase ().execSQL ( SQL1 , selectionArguments1 );

				
				SQL1 =     "UPDATE " + MovementHeadersDao.TABLENAME + " " +
						 "SET " + MovementHeadersDao.Properties.IsProcessed.columnName + " = ?   " 
						+ "WHERE " + MovementHeadersDao.Properties.StampDate.columnName + " > ? and "
						+  MovementHeadersDao.Properties.StampDate.columnName + " < ?   " 	 ;
				// Compute the selection arguments
				  selectionArguments1   = new String [] {
						String.valueOf ( IsProcessedUtils.isNotSync () ) , String.valueOf ( date.getTimeInMillis ()  )
						,String.valueOf ( date1.getTimeInMillis ()  )
				 
				};
				

				DatabaseUtils.getInstance ( DataManagementActivity.this ).getDaoSession ().getDatabase ().execSQL ( SQL1 , selectionArguments1 );

				
				
				SQL1 =     "UPDATE " + SurveyAnswersDao.TABLENAME + " " +
						 "SET " + SurveyAnswersDao.Properties.IsProcessed.columnName + " = ?   " 
						+ "WHERE " + SurveyAnswersDao.Properties.StampDate.columnName + " > ? and "
						+  SurveyAnswersDao.Properties.StampDate.columnName + " < ?   " 	 ;
				// Compute the selection arguments
				  selectionArguments1   = new String [] {
						String.valueOf ( IsProcessedUtils.isNotSync () ) , String.valueOf ( date.getTimeInMillis ()  )
						,String.valueOf ( date1.getTimeInMillis ()  )
				 
				};
				

				DatabaseUtils.getInstance ( DataManagementActivity.this ).getDaoSession ().getDatabase ().execSQL ( SQL1 , selectionArguments1 );

				
				SQL1 =     "UPDATE " + ShareOfShelfTrackerDao.TABLENAME + " " +
						 "SET " + ShareOfShelfTrackerDao.Properties.IsProcessed.columnName + " = ?   " 
						+ "WHERE " + ShareOfShelfTrackerDao.Properties.StampDate.columnName + " > ? and "
						+  ShareOfShelfTrackerDao.Properties.StampDate.columnName + " < ?   " 	 ;
				// Compute the selection arguments
				  selectionArguments1   = new String [] {
						String.valueOf ( IsProcessedUtils.isNotSync () ) , String.valueOf ( date.getTimeInMillis ()  )
						,String.valueOf ( date1.getTimeInMillis ()  )
				 
				};
				

				DatabaseUtils.getInstance ( DataManagementActivity.this ).getDaoSession ().getDatabase ().execSQL ( SQL1 , selectionArguments1 );

		
		 
				

		 
				
 
				
		
		}
    	// Check if a direct sync is requested
    	if ( getIntent ().getBooleanExtra ( DIRECT_SYNC , false ) ) {
    		// Import data to server
    		getData ( null );
    	} // End if
    	  File sdCard = Environment.getExternalStorageDirectory();
// 	     File appDirectory = new File( Environment.getExternalStorageDirectory() + "/BALADNA" );
// 	     File logDirectory = new File( appDirectory + "/log" );
 	deleteFilesOlderThanNdays(10,sdCard + "//" +"ABALog"+ "//" +"log");
 	deleteFilesOlderThanNdays(0,sdCard + "//" +"Transaction"    );
	}public static void deleteFilesOlderThanNdays(int daysBack, String dirWay ) {
		 
		File directory = new File(dirWay);
		if(directory.exists()){

		    File[] listFiles = directory.listFiles();            
		    long purgeTime = System.currentTimeMillis() - (daysBack * 24 * 60 * 60 * 1000);
		    for(File listFile : listFiles) {
		       if(listFile.lastModified() < purgeTime) {
		            if(!listFile.delete()) {
		                System.err.println("Unable to delete file: " + listFile);
		            }
		        }
		    }
		} else {
		     
		}
		}
			
	private class sendSeial extends AsyncTask<String, Void, String>  {
		 
		 private Activity activity;
		
		public sendSeial(Activity activity) {
		    this.activity = activity;
		  
		}

		@Override
	protected void onPreExecute () {
		 
		       super.onPreExecute();
		  }
	  
	@Override
	protected String doInBackground(String... urls) {
		String userCode = DatabaseUtils.getCurrentUserCode ( getApplicationContext () );
       // Retrieve the company code
       String companyCode = DatabaseUtils.getCurrentCompanyCode ( getApplicationContext () );
   	
    
       	List < DeviceSerials > deviceSerials = DatabaseUtils.getInstance(activity).getDaoSession().getDeviceSerialsDao ().queryBuilder ()
   				.where ( DeviceSerialsDao.Properties.DeviceSerialCode.eq ( android.os.Build.SERIAL ) ,
   						DeviceSerialsDao.Properties.UserCode.eq ( userCode ) ).list ();
   						
   		if ( deviceSerials.isEmpty () ) {
             
               
   			// Declare and initialize the appropriate device serial for the current user
   			DeviceSerials ds= new DeviceSerials ( null , // ID
   					android.os.Build.SERIAL , // DeviceSerialCode
   					userCode , // UserCode
   					companyCode , // CompanyCode
   					Calendar.getInstance ().getTime () ) ; // StampDate
   			//DatabaseUtils.getInstance(this).getDaoSession().getDeviceSerialsDao ().insert(ds);
   			deviceSerials.add (  ds ); // StampDate
   			
   			// Build a set Gson object
   			Gson gson = CommonUtilities.buildSetGson ();
   			// Declare and initialize a map used to host the Http post arguments
   			Map < String , String > arguments = new LinkedHashMap < String , String > ();
   			List < String > tables = new ArrayList < String > ();
   			List < String > json = new ArrayList < String > ();
   			  ConnectionSettings connection = DatabaseUtils.getInstance(activity).getDaoSession().getConnectionSettingsDao ().queryBuilder ()
   		        		.where ( ConnectionSettingsDao.Properties.ConnectionSettingType.eq ( ConnectionSettingsUtils.Type.GPRS ) ).unique ();
   		      
   			// Compute the server url
   			String url = ConnectionSettingsUtils.getSetMethodPath ( connection.getConnectionSettingURL () );
   			// Declare and initialize a string used to host the table argument
   			String table = null;
   			// Add the device serial table
   			table = DatabaseUtils.getInstance ( getApplicationContext() ).getDaoSession ().getDeviceSerialsDao ().getTablename ();
   			// Add the device serial to the lists
   			tables.add ( DatabaseUtils.getInstance(activity).getDaoSession().getDeviceSerialsDao ().getTablename () );
   			json.add ( gson.toJson ( deviceSerials ) );
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
   				return "";
   			try {
   				// Retrieve the string response
   				String result = EntityUtils.toString ( response.getEntity () );
   				// Check if no errors occurred
   				if ( result.equalsIgnoreCase ( SyncUtils.SUCCESS ) ) {
   					// Insert device serial (if needed)
   					if ( deviceSerials.get ( 0 ).getId () == null )
   						DatabaseUtils.getInstance(activity).getDaoSession().getDeviceSerialsDao ().insert ( deviceSerials.get ( 0 ) );
   				 
   				 
   				} // End if
   			} catch ( ParseException exception ) {
   				// Do nothing
   			} catch ( IOException exception ) {
   				// Do nothing
   			} catch ( JsonSyntaxException exception ) {
   				// Do nothing
   			} // End of try-catch block
   		 
       	}
	return  "";//"Connected";////aaaaaaa
	}
	
	@Override
	protected void onPostExecute(String result) {
		 		 
		}
	}
    /*
     * Called to retrieve per-instance state from an activity before being killed so that the state can be restored in onCreate(Bundle) or onRestoreInstanceState(Bundle).
     *
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    protected void onSaveInstanceState ( Bundle outState ) {
    	// Superclass method call
    	super.onSaveInstanceState ( outState );
    	// Save the content of setThenGet in the outState bundle
    	outState.putBoolean ( SET_THEN_GET , setThenGet );
    }
	
	/**
	 * Provides additional fragments to the view pager.<br>
	 * Override this method if additional fragments are needed.<br>
	 * All the fragments are based on {@link me.SyncWise.Android.Modules.DataManagement.DataManagementFragment DataManagementFragment}.
	 * The default implementation returns an empty list.<br>
	 * <b>Note : </b> This method should never return {@code NULL}.
	 * 
	 * @return	List of {@link me.SyncWise.Android.Utilities.AppFragment AppFragment} objects, used to define fragments.
	 */
	protected List < AppFragment > additionalFragments () {
		// No additional fragments
		return new ArrayList < AppFragment > ();
	}
	
	/**
	 * Gets the fragment class.<br>
	 * Override this method if a other / modified class is needed.<br>
	 * <b>Note : </b> This method should never return {@code NULL}.
	 * 
	 * @return	Array of Class objects used to represent the fragment to instantiate.
	 */
	protected Class < ? > [] getFragmentClass () {
		// Default fragment class
		return new Class < ? > [] { DataManagementFragment.class };
	}
	
	/*
	 * Called after onRestoreInstanceState(Bundle), onRestart(), or onPause(), for your activity to start interacting with the user.
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	public void onResume () {
		// Superclass method call
		super.onResume ();
		// Check if there is an on going sync process
		if ( totalSet != 0 ) {
			// Display a progress dialog
			AppDialog.getInstance ().displayProgress ( this ,
					null ,
					getString ( R.string.loading_label ) );
			// Update progress
			AppDialog.getInstance ().setProgress ( objectsSet.size () * 100 / totalSet );
		} // End if
		// Check if there is an on going sync process
		else if ( totalGet != 0 ) {
			// Display a progress dialog
			AppDialog.getInstance ().displayProgress ( this ,
					null ,
					getString ( R.string.loading_label ) );
			// Update progress
			AppDialog.getInstance ().setProgress ( objectsGet.size () * 100 / totalGet );
		} // End else if
		else
			// Enable UI widgets
			enable ();
	}
	
	/*
	 * Called as part of the activity lifecycle when an activity is going into the background, but has not (yet) been killed.
	 *
	 * @see android.app.Activity#onPause()
	 */
	@Override
    protected void onPause () {
		// Superclass method call
		super.onPause ();
		// Remove any displayed baguette
		Baguette.remove ( this );
		// Dismiss any displayed dialogs (to avoid activity context leak)
		AppDialog.getInstance ().dismiss ();
	}
	
	/**
	 * Performs a validity check to ensure that the data management process can proceed.
	 * 
	 * @return	Boolean indication if the validity check succeeded (returns {@code true}) or failed (returns {@code false}).
	 */
	public boolean performValidyCheck () {
		// Check for network availability
		if ( ! Network.networkAvailability ( this ) ) {
			// Display alert dialog
			AppDialog.getInstance ().displayAlert ( this ,
					null ,
					getString ( R.string.no_network_connection_message ) ,
					AppDialog.ButtonsType.OK , new DialogInterface.OnClickListener () {
						@Override
						public void onClick ( DialogInterface dialog , int which ) {
							if ( getIntent ().getBooleanExtra ( DIRECT_SYNC , false ) ) {
								int prefixID = DatabaseUtils.getInstance ( DataManagementActivity.this).getDaoSession ().getUsersDao ().queryBuilder ()
										.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode (  DataManagementActivity.this) ) ,
												UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode (  DataManagementActivity.this) ) ).unique ().getPrefixID ();
							 List< ForceSyncJourneys> forceSyncJourneys =DatabaseUtils .getInstance (  DataManagementActivity.this).getDaoSession ().getForceSyncJourneysDao ().queryBuilder ()
										.where ( ForceSyncJourneysDao.Properties.JourneyCode.eq ( JourneysUtils.getJourneyCode ( prefixID ) )  
												    ).list()  ;
								
							 
							 if(  forceSyncJourneys==null || (forceSyncJourneys!=null&& forceSyncJourneys.size()==0 ))
								{Calendar now = Calendar.getInstance ();
								
									ForceSyncJourneys fj = new ForceSyncJourneys(null,
						    				JourneysUtils.getJourneyCode ( prefixID ), 
						    				DatabaseUtils.getCurrentUserCode ( DataManagementActivity.this ),
						    				2,
						    				null, 
						    				1, 
						    				now.getTime());
						    		DatabaseUtils.getInstance ( DataManagementActivity.this ).getDaoSession ().getForceSyncJourneysDao ().insert(fj);
					
								}
								
								setResult ( RESULT_OK );
								finish ();
							}
						}
			} );
			// Exit method
			return false;
		} // End if
		
		// Check if there is a valid link
		if ( ( (Spinner) findViewById ( R.id.spinner ) ).getSelectedItem () == null ) {
			// Indicate that an error occurred
			Baguette.showText ( this ,
					AppResources.getInstance ( this ).getString ( this , R.string.invalid_url_message ) ,
					Baguette.BackgroundColor.RED );
			// Exit method
			return false;
		} // End if
				
		// Otherwise method can proceed
		return true;
	}
	
	/**
	 * Called when a view has been clicked.<br>
	 * This method exports data to server.
	 * 
	 * @param view	The clicked view.
	 */
//	public void setData ( View view ) {
//		// Perform validity check
//		if ( ! performValidyCheck () )
//			// Do nothing
//			return;
//		
//		// Disable widgets
//		disable ();
//		
//		// Otherwise the URL is valid
//		// Display indeterminate progress
//		AppDialog.getInstance ().displayIndeterminateProgress ( this , null , getString ( R.string.connecting_label ) );
//		// Compute the URL string
//		url = ( (ConnectionSettings) ( (Spinner) findViewById ( R.id.spinner ) ).getSelectedItem () ).getConnectionSettingURL ();
//		// Try to connect to service
//		SyncHelper syncHelper = new SyncHelper ( ConnectionSettingsUtils.getConnectMethodPath ( url ) );
//		syncHelper.set ( this , new ArrayList < AbstractDao < ? , ? > > () , new ArrayList < ArrayList < Object > > () , DatabaseUtils.getCurrentUserCode ( this ) , DatabaseUtils.getCurrentCompanyCode ( this ) , this , RequestCode.PING );
//	}
//	
//	/**
//	 * Called when a view has been clicked.<br>
//	 * This method imports data to server.
//	 * 
//	 * @param view	The clicked view.
//	 */
//	public void getData ( View view ) {
//		// Perform validity check
//		if ( ! performValidyCheck () )
//			// Do nothing
//			return;
//		
//		// Check if the set then get feature is required
//		if ( ! setThenGet ) {
//			// Set flag
//			setThenGet = true;
//			// Set data
//			setData ( view );
//		} // End if
//		else {
//			// Disable widgets
//			disable ();
//			
//			// Otherwise the URL is valid
//			// Display indeterminate progress
//			AppDialog.getInstance ().displayIndeterminateProgress ( this , null , getString ( R.string.connecting_label ) );
//			// Compute the URL string
//			String url = "";
//			// Check if a direct sync is requested
//			if ( getIntent ().getBooleanExtra ( DIRECT_SYNC , false ) ) {
//				// Iterate over the connection settings
//				for ( ConnectionSettings connectionSetting : connections ) {
//					// Check if the current connection is of type GPRS 
//					if ( connectionSetting.getConnectionSettingType ().equals ( ConnectionSettingsUtils.Type.GPRS ) ) {
//						// Set the URL
//						url = connectionSetting.getConnectionSettingURL ();
//						// Exit loop
//						break;
//					} // End if
//				} // End for each
//			} // End if
//			else
//				// Set the selected URL
//				url = ( (ConnectionSettings) ( (Spinner) findViewById ( R.id.spinner ) ).getSelectedItem () ).getConnectionSettingURL ();
//			
//			// Try to connect to service
//			SyncHelper syncHelper = new SyncHelper ( ConnectionSettingsUtils.getConnectMethodPath ( url ) );
//			syncHelper.get ( this , DatabaseUtils.getInstance ( this ).getDaoSession ().getUsersDao () , new TypeToken < List < Users > > () {}.getType () , DatabaseUtils.getCurrentUserCode ( this ) , DatabaseUtils.getCurrentCompanyCode ( this ) , this , RequestCode.PING );
//		} // End else
//	}
	
	
	
	
	
	
	
	/**
	 * Called when a view has been clicked.<br>
	 * This method exports data to server.
	 * 
	 * @param view	The clicked view.
	 */
	public void setData ( View view ) {
		
		 
	//	Baguette.showText ( this , "Please Wait To Connecting" , Baguette.BackgroundColor.RED );
	
//		Thread	thread=  new Thread(){
//	        @Override
//	        public void run(){
//	            try {
//	                synchronized(this){
//	                    wait(3000);
//	                }
//	            }
//	            catch(InterruptedException ex){                    
//	            }
//
//	            // TODO              
//	        }
//	    };
//
//	    thread.start();         
	 
		// Perform validity check
		if ( ! performValidyCheck () )
			// Do nothing
			return;
		
		// Disable widgets
		disable ();
		// Display indeterminate progress
	//	AppDialog.getInstance ().displayIndeterminateProgress ( this , null , getString ( R.string.connecting_label ) );
	
		url = ( (ConnectionSettings) ( (Spinner) findViewById ( R.id.spinner ) ).getSelectedItem () ).getConnectionSettingURL ();
		Handler handler = new Handler();
		TaskCanceler taskCanceler;
		 
		DownloadTextTask d=new DownloadTextTask(DataManagementActivity.this);
		taskCanceler = new TaskCanceler(d);
		handler.postDelayed(taskCanceler, 32000);
	 		d.execute(ConnectionSettingsUtils.getConnectMethodPath1 (url)) ;
		 
		
//		 if( conn == false ) 
//		 {	
//			 
//				AppDialog.getInstance ().displayAlert(this , null , getString ( R.string.no_network_connection_message1 ) , AppDialog.ButtonsType.OK,null );
//			
//				// Invalid response
//				enable ();
//				return  ;
//		 }
		 enable ();
		}
	
	

	private boolean conn = false;
//	private InputStream OpenHttpConnection(String urlString) throws IOException
//	{
//	InputStream in = null;
//	int response = -1;
//	URL url = new URL(urlString);
//	URLConnection conn = url.openConnection();
//	if (!(conn instanceof HttpURLConnection))
//	throw new IOException("Not an HTTP connection");
//		try{
//		HttpURLConnection httpConn = (HttpURLConnection) conn;
//		httpConn.setAllowUserInteraction(false);
//		httpConn.setConnectTimeout(30000);
//		httpConn.setReadTimeout(30000);
//		httpConn.setInstanceFollowRedirects(true);
//		httpConn.setRequestMethod("GET");
//		httpConn.connect();
//		response = httpConn.getResponseCode();
//		if (response == HttpURLConnection.HTTP_OK) {
//		in = httpConn.getInputStream();
//	}
//	}
//	catch (Exception ex)
//	{
//	Log.d("Networking", ex.getLocalizedMessage());
//	throw new IOException("Error connecting");
//	}
//	return in;
//	}
	TrustManager[] trustAllCerts = new TrustManager[]{
		    new X509TrustManager() {
		        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
		            return null;
		        }
		        public void checkClientTrusted(
		            java.security.cert.X509Certificate[] certs, String authType) {
		        }
		        public void checkServerTrusted(
		            java.security.cert.X509Certificate[] certs, String authType) {
		        }
		    }
		};

 
	 final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
	        public boolean verify(String hostname, SSLSession session) {
	            return true;
	        }
	 };
	@SuppressWarnings("static-access")
	private InputStream OpenHttpConnection(String urlStr ) throws IOException
	{
	    URL url = new URL(urlStr);

    	if(urlStr.contains("https")){
	    
	    HttpsURLConnection conn = ( HttpsURLConnection ) url.openConnection();
	    try {
	    	
	    // Create the SSL connection
//	    HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());
//	    SSLContext sc  = SSLContext.getInstance("TLS");
//		sc.init(null, trustAllCerts, new java.security.SecureRandom());
//		conn.setHostnameVerifier(DO_NOT_VERIFY);
//	    conn.setSSLSocketFactory(sc.getSocketFactory());
	   
	    SSLContext sslcontext = SSLContext.getInstance("TLSv1");
		sslcontext.init(null, null, null);
		TLSSocketFactory NoSSLv3Factory = new TLSSocketFactory();
		conn.setDefaultSSLSocketFactory(NoSSLv3Factory);
		conn = (HttpsURLConnection) url.openConnection();
	    // Use this if you need SSL authentication
	   // String userpass = user + ":" + password;
	   // String basicAuth = "Basic " + Base64.encodeToString(userpass.getBytes(), Base64.DEFAULT);
	  //  conn.setRequestProperty("Authorization", basicAuth);

	    // set Timeout and method
	    conn.setReadTimeout(30000);
	    conn.setConnectTimeout(30000);
	    conn.setRequestMethod( "POST" );
	    conn.setDoInput(true);

	    // Add any data you wish to post here
	    conn.connect();
	    } catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return conn.getInputStream();
    	}else{
    		  HttpURLConnection conn = ( HttpURLConnection ) url.openConnection();
    		    
    		    	
    		    // Create the SSL connection
//    		    HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());
//    		    SSLContext sc  = SSLContext.getInstance("TLS");
//    			sc.init(null, trustAllCerts, new java.security.SecureRandom());
//    			conn.setHostnameVerifier(DO_NOT_VERIFY);
//    		    conn.setSSLSocketFactory(sc.getSocketFactory());

    		    // Use this if you need SSL authentication
    		   // String userpass = user + ":" + password;
    		   // String basicAuth = "Basic " + Base64.encodeToString(userpass.getBytes(), Base64.DEFAULT);
    		  //  conn.setRequestProperty("Authorization", basicAuth);

    		    // set Timeout and method
    		    conn.setReadTimeout(30000);
    		    conn.setConnectTimeout(30000);
    		    conn.setRequestMethod( "POST" );
    		    conn.setDoInput(true);

    		    // Add any data you wish to post here
    		    conn.connect();
    		 
    		    return conn.getInputStream();
    	    	
    	}
	   
	}   
	private void aa(){
	 
		SyncHelper syncHelper = new SyncHelper ( ConnectionSettingsUtils.getConnectMethodPath ( url ) );
		//syncHelper.set ( this , new ArrayList < AbstractDao < ? , ? > > () , new ArrayList < ArrayList < Object > > () , DatabaseUtils.getCurrentUserCode ( DataManagementActivity.this ), DatabaseUtils.getCurrentCompanyCode ( this ) , this , RequestCode.PING );
		syncHelper.set ( this , new ArrayList < AbstractDao < ? , ? > > () , new ArrayList < ArrayList < Object > > () , DatabaseUtils.getCurrentUserCode ( this ) , DatabaseUtils.getCurrentCompanyCode ( this ) , this , RequestCode.PING );
	}
	private void getSet(){
		SyncHelper syncHelper = new SyncHelper ( ConnectionSettingsUtils.getConnectMethodPath ( url ) );
		//syncHelper.get ( this , DatabaseUtils.getInstance ( this ).getDaoSession ().getUsersDao () , new TypeToken < List < Users > > () {}.getType () , DatabaseUtils.getCurrentUserCode ( this ), DatabaseUtils.getCurrentCompanyCode ( this ) , this , RequestCode.PING , 0 , null );
		syncHelper.get ( this , DatabaseUtils.getInstance ( this ).getDaoSession ().getUsersDao () , new TypeToken < List < Users > > () {}.getType () , DatabaseUtils.getCurrentUserCode ( this ) , DatabaseUtils.getCurrentCompanyCode ( this ) , this , RequestCode.PING );
		// Try to connect to service
//		SyncHelper syncHelper = new SyncHelper ( ConnectionSettingsUtils.getConnectMethodPath ( url ) );
////		syncHelper.get ( this , DatabaseUtils.getInstance ( this ).getDaoSession ().getUsersDao () , new TypeToken < List < Users > > () {}.getType () , DatabaseUtils.getCurrentUserCode ( this ) , this , RequestCode.PING , 0 , null );
	
	}
	private String DownloadText(String URL)
	{
		int BUFFER_SIZE = 2000;
		InputStream in = null;
		try {
			in = OpenHttpConnection(URL);
		} catch (IOException e) {
		Log.d("Networking", e.getLocalizedMessage());
		 setThenGet = false;
		 //conn = false;
		return "";
	}
	InputStreamReader isr = new InputStreamReader(in);
	int charRead;
	String str = "";
	char[] inputBuffer = new char[BUFFER_SIZE];
	try {
	while ((charRead = isr.read(inputBuffer))>0) {
	//---convert the chars to a String---
	String readString =
	String.copyValueOf(inputBuffer, 0, charRead);
	str += readString;
	inputBuffer = new char[BUFFER_SIZE];
	}
	in.close();
	} catch (IOException e) {
	Log.d("Networking", e.getLocalizedMessage());
	 setThenGet = false; 
//	 conn = false;
	 return "";
	}
	//conn =true;
	return str;
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
	        	setThenGet = false;
	   		 	conn = false;
	   		 //	AppDialog.getInstance ().displayAlert(context , null , getString ( R.string.no_network_connection_message1 ) , AppDialog.ButtonsType.OK,null );
			
	        }
	        }
	}
	
 
	private class DownloadTextTask extends AsyncTask<String, Void, String>  {
		private ProgressDialog dialog1;
		 private Activity activity;
		
		public DownloadTextTask(Activity activity) {
		    this.activity = activity;
		    this.dialog1 = new ProgressDialog(activity);
		}

		@Override
	protected void onPreExecute () {
		//disable();
		// Display indeterminate progress before starting a new thread
	//	ProgressDialog progressDialog = ProgressDialog.show(context, "Wait", "Downloading...");
            super.onPreExecute();
			  this.dialog1.setMessage(" Check Connection " );
			  this.dialog1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			  this.dialog1.show();
		//	Baguette.showText ( activity , "Please Wait To Connecting" , Baguette.BackgroundColor.RED );
			 	  }
	  
	@Override
	protected String doInBackground(String... urls) {
		
	return DownloadText(urls[0]);
	}
	
	@Override
	protected void onPostExecute(String result) {
		 if (this.dialog1.isShowing()) {
	           this.dialog1.dismiss();
	        }
		if(result.equals("Connected"))
			{
			conn = true;
		   // aa();
			Handler handler = new Handler();
			TaskCanceler taskCanceler;
			IsLisencedSet d = new IsLisencedSet(activity, url );
			taskCanceler = new TaskCanceler(d);
			handler.postDelayed(taskCanceler, 50000);
			d.execute() ;
			}
		else
		{
			   conn = false;
			   setThenGet = false; 
			   AppDialog.getInstance ().displayAlert(activity , null , getString ( R.string.no_network_connection_message1 ) , AppDialog.ButtonsType.OK,new DialogInterface.OnClickListener () {
					@Override
					public void onClick ( DialogInterface dialog , int which ) {
						if ( getIntent ().getBooleanExtra ( DIRECT_SYNC , false ) ) {
							int prefixID = DatabaseUtils.getInstance ( DataManagementActivity.this).getDaoSession ().getUsersDao ().queryBuilder ()
									.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode (  DataManagementActivity.this) ) ,
											UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode (  DataManagementActivity.this) ) ).unique ().getPrefixID ();
						 List< ForceSyncJourneys> forceSyncJourneys =DatabaseUtils .getInstance (  DataManagementActivity.this).getDaoSession ().getForceSyncJourneysDao ().queryBuilder ()
									.where ( ForceSyncJourneysDao.Properties.JourneyCode.eq ( JourneysUtils.getJourneyCode ( prefixID ) )  
											    ).list()  ;
							
						 
						 if(  forceSyncJourneys==null || (forceSyncJourneys!=null&& forceSyncJourneys.size()==0 ))
							{Calendar now = Calendar.getInstance ();
							
								ForceSyncJourneys fj = new ForceSyncJourneys(null,
					    				JourneysUtils.getJourneyCode ( prefixID ), 
					    				DatabaseUtils.getCurrentUserCode ( DataManagementActivity.this ),
					    				2,
					    				null, 
					    				1, 
					    				now.getTime());
					    		DatabaseUtils.getInstance ( DataManagementActivity.this ).getDaoSession ().getForceSyncJourneysDao ().insert(fj);
				
							}
							setResult ( RESULT_OK ); 
							finish ();
						}}
					} );	 			 
		}
		
		 
		}
	}
	
 
	private class DownloadTextTask1 extends AsyncTask<String, Void, String>  {
		private ProgressDialog dialog1;
		 private Activity activity;
		
		public DownloadTextTask1(Activity activity) {
		    this.activity = activity;
		    this.dialog1 = new ProgressDialog(activity);
		}

		@Override
	  protected void onPreExecute () {
			 super.onPreExecute();
			  this.dialog1.setMessage(" Check Connection " );
			  this.dialog1.setIndeterminate(false);
			  this.dialog1.setCancelable(false);
			  this.dialog1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			  this.dialog1.show();
			//Baguette.showText ( activity , "Please Wait To Connecting" , Baguette.BackgroundColor.RED );
		}
		@Override
		protected String doInBackground(String... urls) {
			//disable ();
			return DownloadText(urls[0]);
		}
	@Override
	protected void onPostExecute(String result) {
		 if (this.dialog1.isShowing()) {
	           this.dialog1.dismiss();
	        }
		//	dlg.dismiss();
		if(result.equals("Connected"))
			{
			
			conn = true;
			getSet();
			}
		else
		{	 
			   setThenGet = false; 
			   conn=false;
			   AppDialog.getInstance ().displayAlert(activity , null , getString ( R.string.no_network_connection_message1 ) , AppDialog.ButtonsType.OK,new DialogInterface.OnClickListener () {
					@Override
					public void onClick ( DialogInterface dialog , int which ) {
						if ( getIntent ().getBooleanExtra ( DIRECT_SYNC , false ) ) {
							int prefixID = DatabaseUtils.getInstance ( DataManagementActivity.this).getDaoSession ().getUsersDao ().queryBuilder ()
									.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode (  DataManagementActivity.this) ) ,
											UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode (  DataManagementActivity.this) ) ).unique ().getPrefixID ();
						 List< ForceSyncJourneys> forceSyncJourneys =DatabaseUtils .getInstance (  DataManagementActivity.this).getDaoSession ().getForceSyncJourneysDao ().queryBuilder ()
									.where ( ForceSyncJourneysDao.Properties.JourneyCode.eq ( JourneysUtils.getJourneyCode ( prefixID ) )  
											    ).list()  ;
							
						 
						 if(  forceSyncJourneys==null || (forceSyncJourneys!=null&& forceSyncJourneys.size()==0 ))
							{Calendar now = Calendar.getInstance ();
							
								ForceSyncJourneys fj = new ForceSyncJourneys(null,
					    				JourneysUtils.getJourneyCode ( prefixID ), 
					    				DatabaseUtils.getCurrentUserCode ( DataManagementActivity.this ),
					    				2,
					    				null, 
					    				1, 
					    				now.getTime());
					    		DatabaseUtils.getInstance ( DataManagementActivity.this ).getDaoSession ().getForceSyncJourneysDao ().insert(fj);
				
							}
							setResult ( RESULT_OK ); 
							finish ();
						}}
					} );
		 
	}
		}
	}
        	 
	/**
	 * Called when a view has been clicked.<br>
	 * This method imports data to server.
	 * 
	 * @param view	The clicked view.
	 */
	public void getData ( View view ) {
	//	Baguette.showText ( this , "Please Wait To Connecting" , Baguette.BackgroundColor.RED );
		
		
		url = ( (ConnectionSettings) ( (Spinner) findViewById ( R.id.spinner ) ).getSelectedItem () ).getConnectionSettingURL ();
		
		// Perform validity check
		if ( ! performValidyCheck () )
			// Do nothing
			return;
		//AppDialog.getInstance ().displayIndeterminateProgress ( this , null , getString ( R.string.connecting_label ) );
	
		// Disable widgets
		disable ();
		// Check if the set then get feature is required
		if ( ! setThenGet ) {
			// Set flag
			setThenGet = true;
			// Set data
			setData ( view );
		} // End if
		else {
			//AppDialog.getInstance ().displayAlert(this , null , getString ( R.string.no_network_connection_message1 ) , AppDialog.ButtonsType.OK,null );	
			// Disable widgets
			disable ();
			Handler handler = new Handler();
			TaskCanceler taskCanceler;
			
			DownloadTextTask1 d = new DownloadTextTask1(this);
			taskCanceler = new TaskCanceler(d);
			handler.postDelayed(taskCanceler, 30000);

			
		 
		    d.execute( ConnectionSettingsUtils.getConnectMethodPath1 (url)) ;
		 
				
			
			
//			 if(conn == false)
//			 {
//					AppDialog.getInstance ().displayAlert(this , null , getString ( R.string.no_network_connection_message1 ) , AppDialog.ButtonsType.OK,null );
//				 
//					 enable ();
//					return  ;
//				}
		
			 
			} // End else
		 enable ();
	}
	/**
	 * Enables all UI widgets.
	 */
	protected void enable () {
		// Enable all UI widgets
		findViewById ( R.id.spinner ).setEnabled ( true );
		( (TabPageIndicator) findViewById ( R.id.indicator ) ).setIndicatorEnabled ( true );
		( (CustomViewPager) findViewById ( R.id.pager ) ).setPagerEnabled ( true );
	}
	
	/**
	 * Disables all UI widgets.
	 */
	protected void disable () {
		// Disable all UI widgets
		findViewById ( R.id.spinner ).setEnabled ( false );
		( (TabPageIndicator) findViewById ( R.id.indicator ) ).setIndicatorEnabled ( false );
		( (CustomViewPager) findViewById ( R.id.pager ) ).setPagerEnabled ( false );
	}
	
	/**
	 * Called when a view has been clicked.<br>
	 * This method exports the corresponding database.<br>
	 * <b>THIS IS FOR TESTING PURPOSE</b>
	 * 
	 * @param view	The clicked view.
	 */
	public void exportDatabase ( View view ) {
		// Declare a string used to host the application name
		String appName = "N/A";
		try {
			// Retrieve the application name
			appName = getPackageManager ().getApplicationLabel ( getPackageManager ().getApplicationInfo ( getPackageName () , 0 ) ).toString ();
		} catch ( Exception exception ) {
			// Application name not found
		} // End of try-catch block
		
	    try {
	    	// Retrieve the external storage directory
	        File sdCard = Environment.getExternalStorageDirectory();
	        // Retrieve the data directory
	        File data = Environment.getDataDirectory();
	        // Check if the SD card is mounted and can be written to
	        if ( sdCard.canWrite () ) {
	        	
	        	Calendar today = Calendar.getInstance ();
	        	DecimalFormat twoDigits = new DecimalFormat ( "00" );
	        	
	        	String currentDBPath = "//data//" + getPackageName () + "//databases//SWDB";
	            String backupDBPath = appName + " ["
	            		+ today.get ( Calendar.YEAR ) + "-"
	            		+ twoDigits.format ( today.get ( Calendar.MONTH ) + 1 ) + "-"
	            		+ twoDigits.format ( today.get ( Calendar.DAY_OF_MONTH ) ) + " "
	            		+ twoDigits.format ( today.get ( Calendar.HOUR_OF_DAY ) ) + "'"
	            		+ twoDigits.format ( today.get ( Calendar.MINUTE ) ) + "''"
	            		+ twoDigits.format ( today.get ( Calendar.SECOND ) ) + "]";
	            File currentDB = new File( data , currentDBPath);
	            File _backupDB = new File ( sdCard + "//" + appName + " DB Backup" );
	            _backupDB.mkdirs();
	            File backupDB = new File ( _backupDB , backupDBPath);
	            	
                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                
    			// Indicate that no errors occurred
    			Baguette.showText ( this ,
    					"DB saved in : " + backupDB.toString() ,
    					Baguette.BackgroundColor.GREEN );
                Vibration.vibrate ( this );
	        } // End if
	        else
    			// Indicate that the SD card cannot be written to
    			Baguette.showText ( this ,
    					"BLA" , // Cannot write to SD card message
    					Baguette.BackgroundColor.GREEN );
                Vibration.vibrate ( this );
	    } catch ( Exception e ) {
			// Indicate that an error occurred
			Baguette.showText ( this ,
					"AN ERROR OCCURRED" ,
					Baguette.BackgroundColor.RED );
	    } // End of try-catch block
	}

	/**
	 * Determines if the sync progress has ended.<br>
	 * If so, a validation check is performed over the sent data, and a dialog is displayed accordingly.
	 */
	private void checkSetProgress () {
		// Check if the entities lists is valid
		if ( objectsSet == null ) {
			// Invalid list
			// Dismiss any displayed dialogs (to avoid activity context leak)
			AppDialog.getInstance ().dismiss ();
			// Enable UI widgets
			enable ();
		} // End if
		
		// Check if upload finished
		else if ( totalSet == objectsSet.size () ) {
			// Sync ended
			// Dismiss any displayed dialogs (to avoid activity context leak)
			AppDialog.getInstance ().dismiss ();
			// Check if all data is valid
			boolean valid = true;
			// Declare and initialize a string used to host the error message
			String errorMessage = getString ( R.string.sync_failed_message );
			// Iterate over all entities 
			for ( ArrayList < ArrayList < Object > > objects : objectsSet )
				// Check if list is valid
				if ( objects == null ) {
					// Invalid entities list
					valid = false;
					// Set the appropriate error message
					errorMessage += "\n" + getString ( R.string.web_service_export_error_message );
					break;
				} // End if

			// Update the sent objects (the is processed value should be updated in DB)
			try {
				// Begin transaction
				DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ().beginTransaction ();
				
				// Iterate over all entities list
				for ( ArrayList < ArrayList < Object > > entities : objectsSet ) {
					// Check if list is valid
					if ( entities == null )
						// Skip the current entities
						continue;
					// Iterate over each group
					for ( ArrayList < Object > group : entities ) 
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

			// Check if all entities are valid
			if ( ! valid ) {
				// Clear cached objects in session
				DatabaseUtils.getInstance ( this ).getDaoSession ().clear ();
			} // End else
			
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
				
				// Check if all entities are valid
				if ( valid ) {
					// Indicate that the save was successful
					Vibration.vibrate ( this );
					// Display alert dialog
					AppDialog.getInstance ().displayAlert ( this ,
							null ,
							getString ( R.string.sync_success_message ) ,
							AppDialog.ButtonsType.OK , new DialogInterface.OnClickListener () {
								@Override
								public void onClick ( DialogInterface dialog , int which ) {
									if ( getIntent ().getBooleanExtra ( DIRECT_SYNC , false ) ) {
										setResult ( RESULT_OK );
										finish ();
									}
								}
					} );
					// Enable UI widgets
					enable ();
					
					ConnectionSettings url = ( (ConnectionSettings) ( (Spinner) findViewById ( R.id.spinner ) ).getSelectedItem () );
					File folder = getDir ( ClientAssetsPicturesUtils.FOLDER_TEMP , MODE_PRIVATE );
					String files [] = folder.list ();
					ArrayList < String > pathFiles = new ArrayList < String > ();
					if ( files != null && files.length > 0 ) {
						for ( String path : files )
							pathFiles.add ( path );
						new UploadToServer ( folder.getAbsolutePath () , pathFiles , ClientAssetsPicturesDao.TABLENAME , url );
					}
					
					folder = getDir ( ClientDocumentImagesUtils.FOLDER_TEMP , MODE_PRIVATE );
					files = folder.list ();
					pathFiles = new ArrayList < String > ();
					if ( files != null && files.length > 0 ) {
						for ( String path : files )
							pathFiles.add ( path );
						new UploadToServer ( folder.getAbsolutePath () , pathFiles , ClientDocumentImagesDao.TABLENAME , url );
					}
					
					folder = getDir ( NewClientImagesUtils.FOLDER_TEMP , MODE_PRIVATE );
					files = folder.list ();
					pathFiles = new ArrayList < String > ();
					if ( files != null && files.length > 0 ) {
						for ( String path : files )
							pathFiles.add ( path );
						new UploadToServer ( folder.getAbsolutePath () , pathFiles , NewClientImagesDao.TABLENAME , url );
					}
				} // End if
			} catch ( Exception exception ) {
				// Invalid entities list
				valid = false;
			} finally {
				// End transaction
				DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ().endTransaction ();
			} // End try-catch-finally block

			// Check if at least one entities list is invalid
			if ( ! valid ) {
				// At least one invalid entities list
				// Sync failed
				// Display alert dialog
				AppDialog.getInstance ().displayAlert ( this ,
						null ,
						errorMessage ,
						AppDialog.ButtonsType.OK , new DialogInterface.OnClickListener () {
							@Override
							public void onClick ( DialogInterface dialog , int which ) {
								if ( getIntent ().getBooleanExtra ( DIRECT_SYNC , false ) ) {
									
									int prefixID = DatabaseUtils.getInstance ( DataManagementActivity.this).getDaoSession ().getUsersDao ().queryBuilder ()
											.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode (  DataManagementActivity.this) ) ,
													UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode (  DataManagementActivity.this) ) ).unique ().getPrefixID ();
								 List< ForceSyncJourneys> forceSyncJourneys =DatabaseUtils .getInstance (  DataManagementActivity.this).getDaoSession ().getForceSyncJourneysDao ().queryBuilder ()
											.where ( ForceSyncJourneysDao.Properties.JourneyCode.eq ( JourneysUtils.getJourneyCode ( prefixID ) )  
													    ).list()  ;
									
								 
								 if(  forceSyncJourneys==null || (forceSyncJourneys!=null&& forceSyncJourneys.size()==0 ))
									{Calendar now = Calendar.getInstance ();
									
										ForceSyncJourneys fj = new ForceSyncJourneys(null,
							    				JourneysUtils.getJourneyCode ( prefixID ), 
							    				DatabaseUtils.getCurrentUserCode ( DataManagementActivity.this ),
							    				2,
							    				null, 
							    				1, 
							    				now.getTime());
							    		DatabaseUtils.getInstance ( DataManagementActivity.this ).getDaoSession ().getForceSyncJourneysDao ().insert(fj);
						
									}
									setResult ( RESULT_OK );
									finish ();
								}
							}
				} );
				// Enable UI widgets
				enable ();
			} // End if
			// Otherwise all entities are valid
			// Check if the set then get feature is required
			else if ( setThenGet ) {
				// Get data
				getData ( null );
			} // End else
			
			// Reset flag
			setThenGet = false;
		} // End else if
	}

	/**
	 * Determines if the sync progress has ended.<br>
	 * If so, a validation check is performed over the retrieved data, and a dialog is displayed accordingly.
	 */
	private void checkGetProgress () {
		// Check if the entities lists is valid
		if ( objectsGet == null ) {
			// Invalid list
			// Dismiss any displayed dialogs (to avoid activity context leak)
			AppDialog.getInstance ().dismiss ();
			// Enable UI widgets
			enable ();
		} // End if
			
		// Check if download finished
		else if ( totalGet == objectsGet.size () ) {
			// Sync ended
			// Dismiss any displayed dialogs (to avoid activity context leak)
			AppDialog.getInstance ().dismiss ();
			// Check if all data is valid
			boolean valid = true;
			boolean missingUser = true;
			// Declare and initialize a string used to host the error message
			String errorMessage = getString ( R.string.sync_failed_message );
			// Declare and initialize a string used to host the user's division code
			String divisionCode = null;
			String divisionCo=null;
			// Retrieve the current user code
			String userCode = DatabaseUtils.getCurrentUserCode ( this );
			// Retrieve the current company code
			String companyCode = DatabaseUtils.getCurrentCompanyCode ( this );
			// Check if the abstract dao list is valid
			if ( tablesGet == null || tablesGet.size () != objectsGet.size () )
				// Invalid abstract dao list
				valid = false;
			// Iterate over all entities
			for ( String key : objectsGet.keySet () ) {
				// Retrieve the entities of the current table
				ArrayList < Object > entities = objectsGet.get ( key );
				// Check if the sync is valid
				if ( ! valid )
					// Exit loop
					break;
				// Check if list is valid
				else if ( entities == null ) {
					// Invalid entities list
					valid = false;
					// Set the appropriate error message
					errorMessage += "\n" + getString ( R.string.web_service_import_error_message );
					break;
				} // End else if
				// Check if this is the users list
				else if ( key.equals ( UsersDao.TABLENAME ) ) {
					// This is the users list
					// Check if the user is active
					// Iterate over all users
					for ( Object object : entities ) {
						// Cast the object to user
						Users user = (Users) object;
						// Check if the current user is the owner
						if ( user.getUserCode ().equals ( userCode ) && user.getCompanyCode ().equals ( companyCode ) ) {
							// Reset flag
							missingUser = false;
							// Check if the user is active
							if ( ! user.getUserStatus ().equals ( StatusUtils.isActive () ) ) {
								// Sync failed
								valid = false;
								// Set the appropriate error message
								errorMessage += "\n" + getString ( R.string.not_active_user_message );
							} // End if
							// Exit loop
							break;
						} // End if
					} // End for each
					// Check if the user is missing
					if ( missingUser ) {
						// Sync failed
						valid = false;
						// Set the appropriate error message
						errorMessage += "\n" + getString ( R.string.missing_user_error_message );
					} // End if
				} // End else if
			} // End for each
			// Check if all entities are valid
			if ( valid ) {
				// Continue with the checking
				// Retrieve the user division entities
				ArrayList < Object > userDivisions = objectsGet.get ( UserDivisionsDao.TABLENAME );
				// Retrieve the division entities
				ArrayList < Object > divisions = objectsGet.get ( DivisionsDao.TABLENAME );
				// Make sure the user is linked to exactly one division
				if ( userDivisions == null  || divisions == null || divisions.isEmpty () ) {//|| userDivisions.size () != 1
					// Invalid user divisions list
					valid = false;
					// Set the appropriate error message
					errorMessage += "\n" + getString ( R.string.user_division_error_message );
				} // End if
				else {
					boolean invalidDivisionLink = true;
					int count =0;
					int count1 =0;
				//	count=userDivisions.size();
					for(Object division :divisions)
					{
						Divisions d = (Divisions) division;
						for(Object userDivision :userDivisions)
						{	
							UserDivisions ud = (UserDivisions) userDivision;
						if( d.getCompanyCode ().equals ( companyCode ) && ud.getDivisionCode().equals(d.getDivisionCode()) && d.getParentCode()==null)
						{	// Invalid user divisions list
							count = count + 1;
							//invalidDivisionLink=false;
							divisionCo=ud.getDivisionCode();
							
						}// Exit loop
						
						
							 
							
//							if(!d.getCompanyCode ().equals ( companyCode ) && d.getParentCode() == null)// &&  !d.getCompanyCode ().equals ( companyCode ) && d.getParentCode() != null)
//								//	|| ( ud.getDivisionCode().equals( d.getDivisionCode() ) &&  !d.getCompanyCode ().equals ( companyCode ) && d.getParentCode() != null) )
//						   {	 
//								count1 = count1 + 1;
//							
//						    } 
							 
						
						}
						//if(invalidDivisionLink==false)
						//	break;
					}
					
				
					for(Object userDivision :userDivisions)
					{	
						UserDivisions ud = (UserDivisions) userDivision;
						 if( !ud.getCompanyCode ().equals ( companyCode ))// &&  !d.getCompanyCode ().equals ( companyCode ) )
					           {	 
									count1 = count1 + 1;
								} 
					}
					
					
					
					
					
					if(count > 1 || count == 0)
					{
						invalidDivisionLink=true;	
					}
					else{
						invalidDivisionLink=false;
					}
					if(count1>0 )
					{
						valid = false;
						// Set the appropriate error message
						errorMessage += "\n" + getString ( R.string.user_division_error_message1 );	
					}
					for(Object userDivision :userDivisions)
					{	
						UserDivisions ud = (UserDivisions) userDivision;
					    divisionCode = divisionCode + ","+ ud.getDivisionCode();
				     }
					
//					// Otherwise make sure the linked division is a main one
//					// Retrieve the division code
//					divisionCode = ( (UserDivisions) userDivisions.get ( 0 ) ).getDivisionCode ();
//					// Declare and initialize a boolean used to indicate if the user is not linked to a division
//					boolean invalidDivisionLink = true;
//					// Iterate over all divisions
//					for ( Object object : divisions ) {
//						// Cast the object to division
//						Divisions division = (Divisions) object;
//						// Check if the current division is the linked division and is main
//						if ( division.getCompanyCode ().equals ( companyCode ) && division.getDivisionCode ().equals ( divisionCode ) && division.getParentCode () == null ) {
//							// Reset the flag
//							invalidDivisionLink = false;
//							// Exit loop
//							break;
//						} // End if
//					} // End for each
					// Check if the division link is invalid
					if ( invalidDivisionLink ) {
						// Invalid user divisions list
						valid = false;
						// Set the appropriate error message
						errorMessage += "\n" + getString ( R.string.user_division_error_message );
					} // End if
				} // End else
			} // End if
			// Check if all entities are valid
			if ( valid ) {
				try {
					// Begin transaction
					DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ().beginTransaction ();
					
					// Iterate over all abstract dao list
					for ( AbstractDao < ? , ? > dao : tablesGet )
						// Delete all data
						dao.deleteAll ();
					
					// Initialize failed tables list if needed
					if ( failedTables == null )
						failedTables = new ArrayList < String > ();
					else
						// Clear the failed tables list
						failedTables.clear ();
					// Iterate over all entities list
					for ( String key : objectsGet.keySet () ) {
						// Retrieve the entities for the current table
						ArrayList < Object > entities = objectsGet.get ( key );
						// Clear the failed tables list
						failedTables.clear ();
						// Add the current table to the failed tables list
						// So that if an error occur, the table which caused the error can be identified
						failedTables.add ( key );
						// Iterate over each entity
						for ( Object entity : entities )
							// Insert data
							DatabaseUtils.getInstance ( this ).getDaoSession ().insert ( entity );
					} // End for each
					// Clear the failed tables list
					failedTables.clear ();
					failedTables = null;
						
					// Commit changes
					DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ().setTransactionSuccessful ();
					
					// Clear cached objects in session
					DatabaseUtils.getInstance ( this ).getDaoSession ().clear ();
					
					// Set the device owner's division code
				//	DatabaseUtils.setCurrentDivisionCode ( this , divisionCode );
					DatabaseUtils.setCurrentDivisionCode ( this , divisionCo  );
					// Clear variables
					totalGet = 0;
					objectsGet = null;
					tablesGet = null;
					
					// Indicate that the save was successful
					Vibration.vibrate ( this );
					
					// Display alert dialog
					AppDialog.getInstance ().displayAlert ( this ,
							null ,
							getString ( R.string.sync_success_message ) ,
							AppDialog.ButtonsType.OK , new DialogInterface.OnClickListener () {
								@Override
								public void onClick ( DialogInterface dialog , int which ) {
									int prefixID = DatabaseUtils.getInstance ( DataManagementActivity.this).getDaoSession ().getUsersDao ().queryBuilder ()
											.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode (DataManagementActivity. this) ) ,
													UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode (DataManagementActivity. this) ) ).unique ().getPrefixID ();
								 List< ForceSyncJourneys> forceSyncJourneys =DatabaseUtils .getInstance ( DataManagementActivity.this).getDaoSession ().getForceSyncJourneysDao ().queryBuilder ()
											.where ( ForceSyncJourneysDao.Properties.JourneyCode.eq ( JourneysUtils.getJourneyCode ( prefixID ) )  
													    ).list()  ;
									
								 
								 if(  forceSyncJourneys==null || (forceSyncJourneys!=null&& forceSyncJourneys.size()==0 ))
									{Calendar now = Calendar.getInstance ();
									
										ForceSyncJourneys fj = new ForceSyncJourneys(null,
							    				JourneysUtils.getJourneyCode ( prefixID ), 
							    				DatabaseUtils.getCurrentUserCode ( DataManagementActivity.this ),
							    				1,
							    				null, 
							    				1, 
							    				now.getTime());
							    		DatabaseUtils.getInstance ( DataManagementActivity.this ).getDaoSession ().getForceSyncJourneysDao ().insert(fj);
						
									}else{
										forceSyncJourneys.get(0).setSyncStatus( 1);
							    		forceSyncJourneys.get(0).setIsProcessed(1);
							    		DatabaseUtils.getInstance ( DataManagementActivity.this ).getDaoSession ()
							    		.getForceSyncJourneysDao ().update(forceSyncJourneys.get(0) );
									}
								}
					} );
					// Enable UI widgets
					enable ();
				} catch ( Exception exception ) {
					// Invalid entities list
					valid = false;
					// Set the appropriate error message
					errorMessage += "\n" + getString ( R.string.populate_data_error_message ) + "\n" + getString ( R.string.error_label ) + " : " + exception;
				} finally {
    				// End transaction
    				DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ().endTransaction ();
    			} // End try-catch-finally block
			} // End if
			// Check if at least one entities list is invalid
			if ( ! valid ) {
				// At least one invalid entities list
				// Clear all data
				totalGet = 0;
				objectsGet = null;
				tablesGet = null;
				// Sync failed
				// Check if there is at least one failed table
				if ( failedTables != null && ! failedTables.isEmpty () ) {
					// Add the first failed table to the error message
					errorMessage += "\n[" + failedTables.get ( 0 );
					// Iterate over all the remaining failed tables
					for ( int i = 1 ; i < failedTables.size () ; i ++ )
						// Add the failed table to the error message
						errorMessage += " , " + failedTables.get ( i );
					// End the error message
					errorMessage += "]";
				} // End if
				// Clear the failed tables list
				if ( failedTables != null ) {
					failedTables.clear ();
					failedTables = null;
				} // End if
				// Display alert dialog
				AppDialog.getInstance ().displayAlert ( this ,
						getString ( R.string.error_label ) ,
						errorMessage ,
						AppDialog.ButtonsType.OK , new DialogInterface.OnClickListener () {
							@Override
							public void onClick ( DialogInterface dialog , int which ) {
								if ( getIntent ().getBooleanExtra ( DIRECT_SYNC, false ) ) {
									setResult ( RESULT_OK );
									finish ();
								}
							}
				} );
				// Enable UI widgets
				enable ();
				
				int prefixID = DatabaseUtils.getInstance ( this).getDaoSession ().getUsersDao ().queryBuilder ()
						.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode ( this) ) ,
								UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( this) ) ).unique ().getPrefixID ();
			 List< ForceSyncJourneys> forceSyncJourneys =DatabaseUtils .getInstance ( this).getDaoSession ().getForceSyncJourneysDao ().queryBuilder ()
						.where ( ForceSyncJourneysDao.Properties.JourneyCode.eq ( JourneysUtils.getJourneyCode ( prefixID ) )  
								    ).list()  ;
				
			 
			 if(  forceSyncJourneys==null || (forceSyncJourneys!=null&& forceSyncJourneys.size()==0 ))
				{Calendar now = Calendar.getInstance ();
				
					ForceSyncJourneys fj = new ForceSyncJourneys(null,
		    				JourneysUtils.getJourneyCode ( prefixID ), 
		    				DatabaseUtils.getCurrentUserCode ( DataManagementActivity.this ),
		    				2,
		    				null, 
		    				1, 
		    				now.getTime());
		    		DatabaseUtils.getInstance ( DataManagementActivity.this ).getDaoSession ().getForceSyncJourneysDao ().insert(fj);
	
				}
			} // End if
			else {
				// Update the auto sync module 
				AutomaticSyncUtils.updateModule ( this );
			} // End else
		} // End if
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
		// Determine if this is a ping to the server
		if ( requestCode == RequestCode.PING ) {
			// Dismiss any displayed dialogs (to avoid activity context leak)
			AppDialog.getInstance ().dismiss ();
			// Retrieve sync tables
			Map < AbstractDao < ? , ? > , Type > tables = getSyncTables_Get ();
			// Check if the sync tables is valid
			if ( tables == null || tables.isEmpty () ) {
				// Sync failed, display alert
				AppDialog.getInstance ().displayAlert ( this ,
						null ,
						getString ( R.string.sync_failed_message ) ,
						AppDialog.ButtonsType.OK , new DialogInterface.OnClickListener () {
							@Override
							public void onClick ( DialogInterface dialog , int which ) {
								if ( getIntent ().getBooleanExtra ( DIRECT_SYNC, false ) ) {
									int prefixID = DatabaseUtils.getInstance ( DataManagementActivity.this).getDaoSession ().getUsersDao ().queryBuilder ()
											.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode (  DataManagementActivity.this) ) ,
													UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode (  DataManagementActivity.this) ) ).unique ().getPrefixID ();
								 List< ForceSyncJourneys> forceSyncJourneys =DatabaseUtils .getInstance (  DataManagementActivity.this).getDaoSession ().getForceSyncJourneysDao ().queryBuilder ()
											.where ( ForceSyncJourneysDao.Properties.JourneyCode.eq ( JourneysUtils.getJourneyCode ( prefixID ) )  
													    ).list()  ;
									
								 
								 if(  forceSyncJourneys==null || (forceSyncJourneys!=null&& forceSyncJourneys.size()==0 ))
									{Calendar now = Calendar.getInstance ();
									
										ForceSyncJourneys fj = new ForceSyncJourneys(null,
							    				JourneysUtils.getJourneyCode ( prefixID ), 
							    				DatabaseUtils.getCurrentUserCode ( DataManagementActivity.this ),
							    				2,
							    				null, 
							    				1, 
							    				now.getTime());
							    		DatabaseUtils.getInstance ( DataManagementActivity.this ).getDaoSession ().getForceSyncJourneysDao ().insert(fj);
						
									}
									setResult ( RESULT_OK );
									finish ();
								}
							}
				} );
				// Re-enable widgets
				enable ();
				// Exit method
				return;
			} // End if
			// Initialize abstract dao list
			tablesGet = new ArrayList < AbstractDao < ? , ? > > ( tables.keySet () );
			// Initialize objects list
			objectsGet = new HashMap < String , ArrayList < Object > > ();
			// Set the total number
			totalGet = tables.size ();
			// Compute the URL string
			String url = ( (ConnectionSettings) ( (Spinner) findViewById ( R.id.spinner ) ).getSelectedItem () ).getConnectionSettingURL ();
			// Retrieve the user code
			String userCode = DatabaseUtils.getCurrentUserCode ( this );
			// Retrieve the company code
			String companyCode = DatabaseUtils.getCurrentCompanyCode ( this );
			// Iterate over all sync tables
			for ( AbstractDao < ? , ? > _dao : tables.keySet () ) {
				// Retrieve the dao table data
				SyncHelper syncHelper = new SyncHelper ( ConnectionSettingsUtils.getGetMethodPath ( url ) );
				syncHelper.get ( this , _dao , tables.get ( _dao ) , userCode , companyCode , this , RequestCode.ON_GOING );
			} // End for each
			// Display a progress dialog
			AppDialog.getInstance ().displayProgress ( this ,
					null ,
					getString ( R.string.loading_label ) );
			// Update progress
			AppDialog.getInstance ().setProgress ( objectsGet.size () * 100 / totalGet );
		} // End if
		else {
			// Sync succeeded
			// Check if total is valid
			if ( totalGet == 0 )
				// Ignore sync
				return;
			// Initialize objects list if needed
			if ( objectsGet == null )
				objectsGet = new HashMap < String , ArrayList < Object > > ();
			// Add the retrieved users to the entities list
			objectsGet.put ( dao.getTablename () , entities );
			// Update progress
			AppDialog.getInstance ().setProgress ( objectsGet.size () * 100 / totalGet );
			// Check sync progress
			checkGetProgress ();
		} // End else
	}

	/*
	 * Call back method executed if the set sync process is a success.
	 *
	 * @see me.SyncWise.Android.Modules.Sync.SyncListener#onSetSuccess(int)
	 */
	@Override
	public void onSetSuccess ( final ArrayList < ArrayList < Object > > entities , int requestCode ) {
		// Determine if this is a ping to the server
		if ( requestCode == RequestCode.PING ) {
			// Dismiss any displayed dialogs (to avoid activity context leak)
			AppDialog.getInstance ().dismiss ();
			// Retrieve sync tables
			ArrayList < ArrayList < AbstractDao < ? , ? > > > tables = getSyncTables_Set ();
			// Check if the sync tables is valid
			if ( tables == null || tables.isEmpty () ) {
				// Sync failed, display alert
				AppDialog.getInstance ().displayAlert ( this ,
						null ,
						getString ( R.string.sync_failed_message ) ,
						AppDialog.ButtonsType.OK , new DialogInterface.OnClickListener () {
							@Override
							public void onClick ( DialogInterface dialog , int which ) {
								if ( getIntent ().getBooleanExtra ( DIRECT_SYNC , false ) ) {
									int prefixID = DatabaseUtils.getInstance ( DataManagementActivity.this).getDaoSession ().getUsersDao ().queryBuilder ()
											.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode (  DataManagementActivity.this) ) ,
													UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode (  DataManagementActivity.this) ) ).unique ().getPrefixID ();
								 List< ForceSyncJourneys> forceSyncJourneys =DatabaseUtils .getInstance (  DataManagementActivity.this).getDaoSession ().getForceSyncJourneysDao ().queryBuilder ()
											.where ( ForceSyncJourneysDao.Properties.JourneyCode.eq ( JourneysUtils.getJourneyCode ( prefixID ) )  
													    ).list()  ;
									
								 
								 if(  forceSyncJourneys==null || (forceSyncJourneys!=null&& forceSyncJourneys.size()==0 ))
									{Calendar now = Calendar.getInstance ();
									
										ForceSyncJourneys fj = new ForceSyncJourneys(null,
							    				JourneysUtils.getJourneyCode ( prefixID ), 
							    				DatabaseUtils.getCurrentUserCode ( DataManagementActivity.this ),
							    				2,
							    				null, 
							    				1, 
							    				now.getTime());
							    		DatabaseUtils.getInstance ( DataManagementActivity.this ).getDaoSession ().getForceSyncJourneysDao ().insert(fj);
						
									}
									setResult ( RESULT_OK );
									finish ();
								}
							}
				} );
				// Enable UI widgets
				enable ();
				// Exit method
				return;
			} // End if
			// Display a progress dialog
			AppDialog.getInstance ().displayProgress ( this ,
					null ,
					getString ( R.string.loading_label ) );
			// Compute the URL string
			String url = ( (ConnectionSettings) ( (Spinner) findViewById ( R.id.spinner ) ).getSelectedItem () ).getConnectionSettingURL ();
			// Send data asynchronously
			new SaveData ( ConnectionSettingsUtils.getSetMethodPath ( url ) , tables , this , this ).execute ();
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
			// Update progress
			AppDialog.getInstance ().setProgress ( objectsSet.size () * 100 / totalSet );
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
		// Sync failed
		// Determine if this is a ping to the server 
		if ( requestCode == RequestCode.PING  ) {
			// Dismiss any displayed dialogs (to avoid activity context leak)
			AppDialog.getInstance ().dismiss ();
			// Display alert dialog
			AppDialog.getInstance ().displayAlert ( this ,
					null ,
					getString ( R.string.server_not_reachable_message ) ,
					AppDialog.ButtonsType.OK , new DialogInterface.OnClickListener () {
						@Override
						public void onClick ( DialogInterface dialog , int which ) {
							if ( getIntent ().getBooleanExtra ( DIRECT_SYNC , false ) ) {
								int prefixID = DatabaseUtils.getInstance ( DataManagementActivity.this).getDaoSession ().getUsersDao ().queryBuilder ()
										.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode (  DataManagementActivity.this) ) ,
												UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode (  DataManagementActivity.this) ) ).unique ().getPrefixID ();
							 List< ForceSyncJourneys> forceSyncJourneys =DatabaseUtils .getInstance (  DataManagementActivity.this).getDaoSession ().getForceSyncJourneysDao ().queryBuilder ()
										.where ( ForceSyncJourneysDao.Properties.JourneyCode.eq ( JourneysUtils.getJourneyCode ( prefixID ) )  
												    ).list()  ;
								
							 
							 if(  forceSyncJourneys==null || (forceSyncJourneys!=null&& forceSyncJourneys.size()==0 ))
								{Calendar now = Calendar.getInstance ();
								
									ForceSyncJourneys fj = new ForceSyncJourneys(null,
						    				JourneysUtils.getJourneyCode ( prefixID ), 
						    				DatabaseUtils.getCurrentUserCode ( DataManagementActivity.this ),
						    				2,
						    				null, 
						    				1, 
						    				now.getTime());
						    		DatabaseUtils.getInstance ( DataManagementActivity.this ).getDaoSession ().getForceSyncJourneysDao ().insert(fj);
					
								}
								setResult ( RESULT_OK );
								finish ();
							}
						}
			} );
			// Re-enable widgets
			enable ();
			// Exit method
			return;
		} // End if
		// Check if total is valid
		if ( totalGet == 0 ) {
			// Dismiss any displayed dialogs (to avoid activity context leak)
			AppDialog.getInstance ().dismiss ();
			// Enable UI widgets
			enable ();
			// Ignore sync
			return;
		} // End if
		
		// Initialize objects list if needed
		if ( objectsGet == null )
			objectsGet = new HashMap < String , ArrayList < Object > > ();
		// Add null to the list
		objectsGet.put ( dao.getTablename () , null );
		// Initialize failed tables list if needed
		if ( failedTables == null )
			failedTables = new ArrayList < String > ();
		// Add the failed table to the list
		failedTables.add ( dao.getTablename () );
		// Update progress
		AppDialog.getInstance ().setProgress ( objectsGet.size () * 100 / totalGet );
		// Check sync progress
		checkGetProgress ();

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
			// Dismiss any displayed dialogs (to avoid activity context leak)
			AppDialog.getInstance ().dismiss ();
			// Display alert dialog
			AppDialog.getInstance ().displayAlert ( this ,
					null ,
					getString ( R.string.server_not_reachable_message ) ,
					AppDialog.ButtonsType.OK , new DialogInterface.OnClickListener () {
						@Override
						public void onClick ( DialogInterface dialog , int which ) {
							if ( getIntent ().getBooleanExtra ( DIRECT_SYNC , false ) ) {
								int prefixID = DatabaseUtils.getInstance ( DataManagementActivity.this).getDaoSession ().getUsersDao ().queryBuilder ()
										.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode (  DataManagementActivity.this) ) ,
												UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode (  DataManagementActivity.this) ) ).unique ().getPrefixID ();
							 List< ForceSyncJourneys> forceSyncJourneys =DatabaseUtils .getInstance (  DataManagementActivity.this).getDaoSession ().getForceSyncJourneysDao ().queryBuilder ()
										.where ( ForceSyncJourneysDao.Properties.JourneyCode.eq ( JourneysUtils.getJourneyCode ( prefixID ) )  
												    ).list()  ;
								
							 
							 if(  forceSyncJourneys==null || (forceSyncJourneys!=null&& forceSyncJourneys.size()==0 ))
								{Calendar now = Calendar.getInstance ();
								
									ForceSyncJourneys fj = new ForceSyncJourneys(null,
						    				JourneysUtils.getJourneyCode ( prefixID ), 
						    				DatabaseUtils.getCurrentUserCode ( DataManagementActivity.this ),
						    				2,
						    				null, 
						    				1, 
						    				now.getTime());
						    		DatabaseUtils.getInstance ( DataManagementActivity.this ).getDaoSession ().getForceSyncJourneysDao ().insert(fj);
					
								}
								setResult ( RESULT_OK );
								finish ();
							}
						}
			} );
			// Re-enable widgets
			enable ();
			// Exit method
			return;
		} // End if
		// Check if total is valid
		if ( totalSet == 0 ) {
			// Dismiss any displayed dialogs (to avoid activity context leak)
			AppDialog.getInstance ().dismiss ();
			// Enable UI widgets
			enable ();
			// Ignore sync
			return;
		} // End if
		// Initialize objects list if needed
		if ( objectsSet == null )
			objectsSet = new ArrayList < ArrayList < ArrayList < Object > > > ();
		// Add null to the list
		objectsSet.add ( null );
		// Update progress
		AppDialog.getInstance ().setProgress ( objectsSet.size () * 100 / totalSet );
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
		//SchemeRegistry schemeRegistry = new SchemeRegistry ();
		try {
		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
	    trustStore.load(null, null);

//	    SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
//	    sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
// 
//    	schemeRegistry.register (new Scheme ("http",PlainSocketFactory.getSocketFactory (), 8090));
//    
//    	schemeRegistry.register(new Scheme("https", sf, 443));

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
	
	private Activity activity;
	private ProgressDialog dialog1;
	private String url;

	
	public IsLisencedSet (final Activity activity, final String url ) {
	    this.url = url;
	   
	    this.activity = activity;
	}

	@Override
	protected void onPreExecute () {
		// Display indeterminate progress before starting a new thread
        super.onPreExecute();
        this.dialog1 = new ProgressDialog( activity );
		this.dialog1.setMessage( " Checking Connection " );
		this.dialog1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		this.dialog1.show();
	}
  
	@Override
	protected String doInBackground ( Void ... params ) {
		String userCode = DatabaseUtils.getCurrentUserCode ( activity );
	      // Retrieve the company code
	    String companyCode = DatabaseUtils.getCurrentCompanyCode ( activity );
		
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
		{       this.dialog1.dismiss();
			AppDialog.getInstance ().displayAlert ( activity ,
					null ,
					getString ( R.string.server_not_reachable_message   ),
					AppDialog.ButtonsType.OK , new DialogInterface.OnClickListener () {
						@Override
						public void onClick ( DialogInterface dialog , int which ) {
							if ( getIntent ().getBooleanExtra ( DIRECT_SYNC , false ) ) {
								int prefixID = DatabaseUtils.getInstance ( DataManagementActivity.this).getDaoSession ().getUsersDao ().queryBuilder ()
										.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode (  DataManagementActivity.this) ) ,
												UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode (  DataManagementActivity.this) ) ).unique ().getPrefixID ();
							 List< ForceSyncJourneys> forceSyncJourneys =DatabaseUtils .getInstance (  DataManagementActivity.this).getDaoSession ().getForceSyncJourneysDao ().queryBuilder ()
										.where ( ForceSyncJourneysDao.Properties.JourneyCode.eq ( JourneysUtils.getJourneyCode ( prefixID ) )  
												    ).list()  ;
								
							 
							 if(  forceSyncJourneys==null || (forceSyncJourneys!=null&& forceSyncJourneys.size()==0 ))
								{Calendar now = Calendar.getInstance ();
								
									ForceSyncJourneys fj = new ForceSyncJourneys(null,
						    				JourneysUtils.getJourneyCode ( prefixID ), 
						    				DatabaseUtils.getCurrentUserCode ( DataManagementActivity.this ),
						    				2,
						    				null, 
						    				1, 
						    				now.getTime());
						    		DatabaseUtils.getInstance ( DataManagementActivity.this ).getDaoSession ().getForceSyncJourneysDao ().insert(fj);
					
								}}
							enable ();

						}
			} );
		return;
		}
		 if (this.dialog1.isShowing()) 
	           this.dialog1.dismiss();
			// Dismiss any displayed dialogs (to avoid activity context leak)
	 
							// TODO Auto-generated method stub
							if ( answer  .equals("1" ) ) {
								try {
									aa();
								 
								}
								catch ( Exception ex) {
								 
								}
								finally {
								
								}
							}else{
								AppDialog.getInstance ().displayAlert ( activity ,
										null ,
										"this user is not lisenced " ,
										AppDialog.ButtonsType.OK , new DialogInterface.OnClickListener () {
											@Override
											public void onClick ( DialogInterface dialog , int which ) {
												 setThenGet = false; 
												   conn=false;

											}
								} );
							}
					 
	}
}
}

/**
 * AsyncTask Helper used to send data to the server asynchronously.
 * 
 * @author Elias
 *
 */
class SaveData extends AsyncTask < Void , Void , Void > {

	/**
	 * List of AbstractDao objects, which in turns references the main tables.
	 */
	private ArrayList < ArrayList < AbstractDao < ? , ? > > > tables;
	
	/**
	 * Interface definition for a callback to be invoked for various Synchronization events.
	 */
	private SyncListener syncListener ;
	
	/**
	 * String hosting the server path.
	 */
	private String url;
	
	/**
	 * Reference to the calling activity.<br>
	 * This is mainly used to display dialogs and execute queries.
	 */
	private Activity activity;
	
	/**
	 * Constructor.
	 * 
	 * @param url	Server path.
	 * @param tables	List of AbstractDao objects, which in turns references the main tables.
	 * @param syncListener	Interface definition for a callback to be invoked for various Synchronization events.
	 * @param activity	Reference to the calling activity.
	 */
	public SaveData ( final String url , final ArrayList < ArrayList < AbstractDao < ? , ? > > > tables , final SyncListener syncListener , final Activity activity ) {
		// Initialize attributes
		this.tables = tables;
		this.syncListener = syncListener;
		this.url = url;
		this.activity = activity;
	}
	
	/*
	 * Method to perform a computation on a background thread.
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Void doInBackground ( Void ... params ) {
		// Retrieve a reference to the dao session
		DaoSession daoSession = DatabaseUtils.getInstance ( activity ).getDaoSession ();
		// Set the total number of tables
		DataManagementActivity.totalSet = tables.size ();
		// Initialize the entities list
		DataManagementActivity.objectsSet = new ArrayList < ArrayList < ArrayList < Object > > > ();
		ArrayList < ArrayList < Object > > entities;
		// Iterate over all tables
		for ( ArrayList < AbstractDao < ? , ? > > group : tables ) {
			// Initialize the entities list
			entities = new ArrayList < ArrayList<Object> > ();
			for ( AbstractDao < ? , ? > dao : group ) {
				// Declare and initialize a list of objects
				ArrayList < Object > objects = null;
				// Check if dao has custom query
				QueryBuilder < ? > queryBuilder = CustomQueryBuilder.getCustomQueryBuilder ( activity , dao , daoSession );
				// Check if dao has custom result
				List < ? > customResult = CustomResult.getCustomResult ( activity , dao , daoSession );
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
				else if ( isProcessed != null ) {
					// Query entities based on IsProcessed field
					 if( dao instanceof SOSTrackerHeadersDao )
							objects = new ArrayList < Object > ( dao.queryBuilder ().where ( isProcessed.eq ( IsProcessedUtils.isNotSync () ) ).orderAsc(SOSTrackerHeadersDao.Properties.StampDate).limit(4).list ()   );
						
					 else
					objects = new ArrayList < Object > ( dao.queryBuilder ().where ( isProcessed.eq ( IsProcessedUtils.isNotSync () ) ).list () );
				}		
				else
					// Load all entities
					objects = new ArrayList < Object > ( dao.loadAll () );
				
				// Update entities accordingly
				SyncUtils.updateProcessedEntities ( activity , objects );
				// Add the objects to the entities list
				entities.add ( objects );
			} // End for each
			
			// Clear cached objects in session
			DatabaseUtils.getInstance ( activity ).getDaoSession ().clear ();
			
			// Send the dao table data
			SyncHelper syncHelper = new SyncHelper ( url );
			syncHelper.set ( activity , group , entities , DatabaseUtils.getCurrentUserCode ( activity ) , DatabaseUtils.getCurrentCompanyCode ( activity ) , syncListener , RequestCode.ON_GOING );
		} // End for each
		// Clear references
		tables = null;
		syncListener = null;
		activity = null;
		url = null;
		return null; 
	}
}