/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Sync;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.Users;
import me.SyncWise.Android.Database.UsersDao;
import me.SyncWise.Android.Gson.CommonUtilities;
import me.SyncWise.Android.Modules.DataManagement.DataManagementActivity.RequestCode;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import de.greenrobot.dao.AbstractDao;

/**
 * Helper class implemented to perform get and set request from / to the server, asynchronously.
 * 
 * @author Elias
 *
 */
public class SyncHelper {
	
	/**
	 * String holding the url.
	 */
	private final String url;

	/**
	 * Constructor.
	 * 
	 * @param url	String holding the url.
	 */
	public SyncHelper ( final String url ) {
		// Set url
		this.url = url;
	}
	
	/**
	 * Gets data from the server.
	 * 
	 * @param context	The application context.
	 * @param dao	Reference to the AbstractDao, which in turns references the main table.
	 * @param type	Common interface implemented by all Java types, indicating the expected entities type.
	 * @param userCode	String holding the user code.
	 * @param companyCode	String holding the company code.
	 * @param syncListener	Interface definition for a callback to be invoked for various Synchronization events.
	 * @param requestCode	Integer holding the synchronization request code.
	 * @param executeInParallel	Boolean used to indicate whether to execute the background thread serially or in parallel.
	 */
	public void get ( final Context context , final AbstractDao < ? , ? > dao , final Type type , final String userCode , final String companyCode , final SyncListener syncListener , final int requestCode , final boolean executeInParallel ) {
		// Determine if the thread pool executer should be used
		if ( executeInParallel )
			getAsyncTaskGetter ( context , dao , type , userCode , companyCode , syncListener , requestCode ).executeOnExecutor ( AsyncTask.THREAD_POOL_EXECUTOR );
		else
			get ( context , dao , type , userCode , companyCode , syncListener , requestCode );
	}
	
	/**
	 * Gets data from the server.
	 * 
	 * @param context	The application context.
	 * @param dao	Reference to the AbstractDao, which in turns references the main table.
	 * @param type	Common interface implemented by all Java types, indicating the expected entities type.
	 * @param userCode	String holding the user code.
	 * @param companyCode	String holding the company code.
	 * @param syncListener	Interface definition for a callback to be invoked for various Synchronization events.
	 * @param requestCode	Integer holding the synchronization request code.
	 */
	public void get ( final Context context , final AbstractDao < ? , ? > dao , final Type type , final String userCode , final String companyCode , final SyncListener syncListener , final int requestCode ) {
		getAsyncTaskGetter ( context , dao , type , userCode , companyCode , syncListener , requestCode ).execute ();
	}
	public static long getToken( final Calendar calendar ) {
		// Declare and initialize a two digits formatter
    	DecimalFormat twoDigits = new DecimalFormat ( "00" );
		 	// Compute the visit id in the following format : YYYYMMDDHHMMSSSSS
    	StringBuilder visitId = new StringBuilder ();
    	visitId.append ( calendar.get ( Calendar.YEAR ) ); // YYYY
    	visitId.append ( twoDigits.format ( 1 + calendar.get ( Calendar.MONTH ) ) ); // MM
    	visitId.append ( twoDigits.format ( calendar.get ( Calendar.DATE ) ) ); // DD
    	visitId.append ( twoDigits.format ( calendar.get ( Calendar.HOUR_OF_DAY ) ) ); // HH
    	visitId.append ( twoDigits.format ( calendar.get ( Calendar.MINUTE ) ) ); // MM
      // SS
  
    	// Return the visit id
    	return Long.parseLong ( visitId.toString () );
	}
	private double getNumber ( String text ) {
		try {
			// Cast to double
			return Double.parseDouble ( text );
		} catch ( Exception exception ) {
			// Invalid text
			return 0;
		} // End of try-catch block
	}

	/**
	 * Gets an AsyncTask object properly initialized to get data from the server.<br>
	 * <b>Note : </b>The AsyncTask object is NOT executed !
	 * 
	 * @param context	The application context.
	 * @param dao	Reference to the AbstractDao, which in turns references the main table.
	 * @param type	Common interface implemented by all Java types, indicating the expected entities type.
	 * @param userCode	String holding the user code.
	 * @param companyCode	String holding the company code.
	 * @param syncListener	Interface definition for a callback to be invoked for various Synchronization events.
	 * @param requestCode	Integer holding the synchronization request code.
	 * @return	{@link android.os.AsyncTask AsyncTask} object used to get GreenDao entities from the server.
	 */
	private AsyncTask < Void , Void , ArrayList < Object > > getAsyncTaskGetter ( final Context context , final AbstractDao < ? , ? > dao , final Type type , final String userCode , final String companyCode , final SyncListener syncListener , final int requestCode ) {
		/**
		 * AsyncTask helper class used to asynchronously get an entities list from the server.
		 * 
		 * @author Elias
		 *
		 */
		return new AsyncTask < Void , Void , ArrayList < Object > > () {
	    	/*
	    	 * Runs on the UI thread before doInBackground(Params...).
	    	 * 
	    	 * @see android.os.AsyncTask#onPreExecute()
	    	 */
			@Override
			protected ArrayList < Object > doInBackground ( Void ... params ) {
				// Check if the provided arguments are valid
				if ( context == null || dao == null || type == null || userCode == null || companyCode == null || syncListener == null )
					return null;
				
				// Declare and initialize a list used to host the entities
				ArrayList < Object > entities = new ArrayList < Object > ();
				// Declare and initialize a map used to host the Http post arguments
				Map < String , String > arguments = new HashMap < String , String > ();
				// Add the user code argument
				arguments.put ( UsersDao.Properties.UserCode.columnName , userCode );
				// Add the company code argument
				arguments.put ( UsersDao.Properties.CompanyCode.columnName , companyCode );
				// Add the page argument
				arguments.put("page", String.valueOf(0));
				// Add the table argument
				arguments.put ( SyncUtils.TABLE , dao.getTablename () );
				Calendar today = Calendar.getInstance ();
				Users user=null;
				if(DatabaseUtils.getCurrentUserCode ( context )!=null)
					user = DatabaseUtils.getInstance ( context ).getDaoSession ().getUsersDao ().queryBuilder ()
						.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode ( context ) ) ,
								UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( context ) ) ).unique ();
				int  prefix=99999;
				if(user!=null)
				   prefix=user.getPrefixID();
				String token1=getToken(today)+""+prefix;
				BigDecimal nbr= new BigDecimal(1568799847652146L);
				BigDecimal nbr1= new BigDecimal(token1);
				BigDecimal token2=    nbr1.multiply(  nbr)  ;
				arguments.put ( "AuthToken" , token2.toString()+"" );
				// Add the checksum argument
				arguments.put ( SyncUtils.CHECKSUM , SyncUtils.getArgumentsChecksum ( context , arguments ) );
				// Fire the http post request and retrieve the http response
				HttpResponse response = SyncUtils.post ( url , arguments ,context);
				// Check if the response is valid
				if ( response == null )
					// Invalid response
					return null;
				try {
			//		 Log.e("Response", "Error parsing data Response " + response.toString());
					// Retrieve the string response
					String json = EntityUtils.toString ( response.getEntity () );
				//	 Log.e("json", "  " + json.toString());
					// Check if an error occurred
					if ( json.equalsIgnoreCase ( SyncUtils.ERROR ) )
						// An error occurred
						return null;
					// Build a get Gson object
					Gson gson = CommonUtilities.buildGetGson ();
		    		// Deserialize the specified Json into an object of the specified type
		    		entities = gson.fromJson ( json , type );
				} catch ( ParseException exception ) {
					return null;
				} catch ( IOException exception ) {
					return null;
				} catch ( JsonSyntaxException exception ) {
					 Log.e("JSON Parser", "Error parsing data " + exception.toString());
					return null;
				} catch ( JsonParseException exception) {
					return null;
				} // End of try-catch block

				// Return the retrieved (or not) list of entities
				return entities;
			}
			/*
			 * Method to perform a computation on a background thread.
			 * 
			 * @see android.os.AsyncTask#doInBackground(Params[])
			 */
            protected void onPostExecute ( final ArrayList < Object > entities ) {
            	// Perform pre finish call back
            	syncListener.onPreFinish ( requestCode );
            	
            	// Check if the entities list is valid
            	if ( entities != null )
            		// Valid result
            		syncListener.onGetSuccess ( dao , entities , requestCode );
            	else
            		// Invalid result
            		syncListener.onGetFailure ( dao , requestCode );
            	
            	// Perform post finish call back
            	syncListener.onPostFinish ( requestCode );
            }
		};
	}
	
	/**
	 * Sets data from the server.
	 * 
	 * @param context	The application context.
	 * @param daos	List of AbstractDao objects, which in turns references the main tables.
	 * @param entities	List of entities lists that should be set.
	 * @param userCode	String holding the user code.
	 * @param companyCode	String holding the company code.
	 * @param syncListener	Interface definition for a callback to be invoked for various Synchronization events.
	 * @param requestCode	Integer holding the synchronization request code.
	 * @param executeInParallel	Boolean used to indicate whether to execute the background thread serially or in parallel.
	 */
	public void set ( final Context context , final ArrayList < AbstractDao < ? , ? > > daos , final ArrayList < ArrayList < Object > > entities , final String userCode , final String companyCode , final SyncListener syncListener , final int requestCode , final boolean executeInParallel ) {
		// Determine if the thread pool executer should be used
		if ( executeInParallel )
			getAsyncTaskSetter ( context , daos , entities , userCode , companyCode , syncListener , requestCode ).executeOnExecutor ( AsyncTask.THREAD_POOL_EXECUTOR );
		else
			set ( context , daos , entities , userCode , companyCode , syncListener , requestCode );
	}
	
	/**
	 * Sets data from the server.
	 * 
	 * @param context	The application context.
	 * @param daos	List of AbstractDao objects, which in turns references the main tables.
	 * @param entities	List of entities lists that should be set.
	 * @param userCode	String holding the user code.
	 * @param companyCode	String holding the company code.
	 * @param syncListener	Interface definition for a callback to be invoked for various Synchronization events.
	 * @param requestCode	Integer holding the synchronization request code.
	 */
	public void set ( final Context context , final ArrayList < AbstractDao < ? , ? > > daos , final ArrayList < ArrayList < Object > > entities , final String userCode , final String companyCode , final SyncListener syncListener , final int requestCode ) {
		getAsyncTaskSetter ( context , daos , entities , userCode , companyCode , syncListener , requestCode ).execute ();
	}
	
	/**
	 * Gets an AsyncTask object properly initialized to set data from the server.<br>
	 * <b>Note : </b>The AsyncTask object is NOT executed !
	 * 
	 * @param context	The application context.
	 * @param daos	List of AbstractDao objects, which in turns references the main tables.
	 * @param entities	List of entities lists that should be set.
	 * @param userCode	String holding the user code.
	 * @param companyCode	String holding the company code.
	 * @param syncListener	Interface definition for a callback to be invoked for various Synchronization events.
	 * @param requestCode	Integer holding the synchronization request code.
	 */
	public AsyncTask < Void , Void , Boolean > getAsyncTaskSetter ( final Context context , final ArrayList < AbstractDao < ? , ? > > daos , final ArrayList < ArrayList < Object > > entities , final String userCode , final String companyCode , final SyncListener syncListener , final int requestCode ) {
		/**
		 * AsyncTask helper class used to asynchronously get an entities list from the server.
		 * 
		 * @author Elias
		 *
		 */
		return new AsyncTask < Void , Void , Boolean > () {
	    	/*
	    	 * Runs on the UI thread before doInBackground(Params...).
	    	 * 
	    	 * @see android.os.AsyncTask#onPreExecute()
	    	 */
			@Override
			protected Boolean doInBackground ( Void ... params ) {
				// Check if the provided arguments are valid
				if ( context == null || daos == null || entities == null || syncListener == null || daos.size () != entities.size () )
					return Boolean.FALSE;
				
				// Build a set Gson object
				Gson gson = CommonUtilities.buildSetGson ();
	    		
				// Declare and initialize a map used to host the Http post arguments
				Map < String , String > arguments = new LinkedHashMap < String , String > ();
				List < String > tables = new ArrayList < String > ();
				List < String > json = new ArrayList < String > ();
				String log="";
				log+="UserCode"+" "+userCode+"\n";
				// Add the user code argument
				arguments.put ( UsersDao.Properties.UserCode.columnName , userCode );
				// Add the company code argument
				arguments.put ( UsersDao.Properties.CompanyCode.columnName , companyCode );
				log+="CompanyCode"+" "+companyCode+"\n";
				// Compute the table string
				String table = "";
				// Iterate over all daos
				for ( int i = 0 ; i < daos.size () ; i ++ ) {
					// Add the current table
					table += daos.get ( i ).getTablename () + ",";
					// Add the entities to the lists
					tables.add ( daos.get ( i ).getTablename () );
					json.add ( gson.toJson ( entities.get ( i ) ) );
				} // End for loop
				// Remove the last comma (if needed)
				if ( ! table.isEmpty () )
					table = table.substring ( 0 , table.length () - 1 );
			
				log+="TABLE"+" "+table+"\n";
				// Add the table argument
				arguments.put ( SyncUtils.TABLE , table );
				// Add the tables count argument
				arguments.put ( SyncUtils.TABLES_COUNT , String.valueOf ( daos.size () ) );
				log+="TABLES_COUNT"+" "+String.valueOf ( daos.size () )+"\n";
				Calendar today = Calendar.getInstance ();
				Users user=null;
				if(DatabaseUtils.getCurrentUserCode ( context )!=null)
					user = DatabaseUtils.getInstance ( context ).getDaoSession ().getUsersDao ().queryBuilder ()
						.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode ( context ) ) ,
								UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( context ) ) ).unique ();
							int  prefix=99999;
				if(user!=null)
				   prefix=user.getPrefixID();
				String token1=getToken(today)+""+prefix;
				BigDecimal nbr= new BigDecimal(1568799847652146L);
				BigDecimal nbr1= new BigDecimal(token1);
				BigDecimal token2=    nbr1.multiply(  nbr)  ;
				arguments.put ( "AuthToken" , token2.toString()+"" );
				log+="AuthToken"+" "+token2.toString()+"\n";
				// Iterate over the lists
				for ( int i = 0 ; i < tables.size () ; i ++ )
					// Add the json arguments
				{	arguments.put ( tables.get ( i ) , json.get ( i ) );
				log+=  tables.get ( i )  +" "+json.get ( i )+"\n";
				}Calendar now = Calendar.getInstance ();
				 //Log.e("logss", "  " + log);
				// generateNoteOnSD1(  now.getTime().toString(), log);
				// Add the checksum arguments
				arguments.put ( SyncUtils.CHECKSUM , SyncUtils.getArgumentsChecksum ( context , arguments ) );
				// Fire the http post request and retrieve the http response
				HttpResponse response = SyncUtils.post ( url , arguments , context );
				// Check if the response is valid
				if ( response == null )
					// Invalid response
					return Boolean.FALSE;
				try {
					// Retrieve the string response
					String result = EntityUtils.toString ( response.getEntity () );
				 	 Log.e("Response", "  " + result.toString());
					// Check if an error occurred
					if ( requestCode == RequestCode.PING )
						// Ping successful
						return Boolean.TRUE;
					else if ( ! result.equalsIgnoreCase ( SyncUtils.SUCCESS ) )
						// An error occurred
						return Boolean.FALSE;
					else
						// Data successfully sent
						return Boolean.TRUE;
				} catch ( ParseException exception ) {
					 Log.e("ParseException", "  " + exception.toString());
						
				} catch ( IOException exception ) {
					 Log.e("IOException", "  " + exception.toString());
				} catch ( JsonSyntaxException exception ) {
					 Log.e("JsonSyntaxException", "  " + exception.toString());
				} // End of try-catch block
	
				// An error occurred
				return Boolean.FALSE;
			}
			/*
			 * Method to perform a computation on a background thread.
			 * 
			 * @see android.os.AsyncTask#doInBackground(Params[])
			 */
	        protected void onPostExecute ( final Boolean success ) {
	        	// Perform pre finish call back
	        	syncListener.onPreFinish ( requestCode );
	        	
	        	// Check if the result is a success
	        	if ( success != null && success == true )
	        		// Valid result
	        		syncListener.onSetSuccess ( entities , requestCode );
	        	else
	        		// Invalid result
	        		syncListener.onSetFailure ( requestCode );
	        	
	        	// Perform post finish call back
	        	syncListener.onPostFinish ( requestCode );
	        }
		};
	}
	public void generateNoteOnSD1(  String sFileName, String sBody) {
	    try {
	        File root = new File(Environment.getExternalStorageDirectory(), "Transaction");
	        if (!root.exists()) {
	            root.mkdirs();
	        }
	        File gpxfile = new File(root, sFileName+".txt");
	        FileOutputStream fOut = new FileOutputStream(gpxfile,true);
	        OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
	        myOutWriter.append(sBody);
	        myOutWriter.close();
	        fOut.close();
	        
	        //FileWriter writer = new FileWriter(gpxfile);
	       // writer.append(sBody);
	       // writer.flush();
	       // writer.close();
	      //  Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
}