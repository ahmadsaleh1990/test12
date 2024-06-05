/**
 * Copyright 2014 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Messages;

import java.text.DecimalFormat;
import java.util.Calendar;

import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.ClientsDao;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.MsgHeaderDao;
import me.SyncWise.Android.Database.MsgNoteDao;
import me.SyncWise.Android.Modules.Journey.Call;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;


public class MessagesListActivity extends ListActivity {
	
	/**
	 * Bundle key used to put/retrieve the content of the current call.
	 */
	public static final String CALL = MessagesListActivity.class.getName () + ".CALL";
	
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
		setContentView ( R.layout.messages_list_activity );
		
		// Change the title associated with this activity
		setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.messages_title ) );
		// Define the empty list view
		( (ListView) findViewById ( android.R.id.list ) ).setEmptyView ( findViewById ( R.id.empty_list_view ) );
		// Display empty list label
		( (TextView) findViewById ( R.id.empty_list_view ) ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.empty_notes_list_label ) );
		
		// Retrieve all the messages asynchronously (if any)
		new PopulateMessages ().execute ();
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
		// Dismiss any displayed dialogs (to avoid activity context leak)
		AppDialog.getInstance ().dismiss ();
	}
	
	/*
	 * Initialize the contents of the Activity's standard options menu.
	 *
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
    @Override
    public boolean onCreateOptionsMenu ( Menu menu ) {
    	// Use the MenuInflater of this context to inflate a menu hierarchy from the specified XML resource
    	getMenuInflater ().inflate ( R.menu.action_bar , menu );
    	// Enable the required menu items
    	me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_add ) , R.string.add_label );
    	// Display the menu
    	return true;
    }
    
    /*
     * This hook is called whenever an item in your options menu is selected.
     *
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected ( MenuItem menuItem ) {
    	// Determine if the action add is selected
    	if ( menuItem.getItemId () == R.id.action_add ) {
    		// Create a new intent to start a new activity
    		Intent intent = new Intent ( this , MessagesActivity.class );
    		// Add the call (if any)
    		intent.putExtra ( MessagesActivity.CALL , getIntent ().getSerializableExtra ( CALL ) );
    		// Start a new activity
    		startActivityForResult ( intent , 0 );
    		// Consume event
    		return true;
    	} // End if
    	// Allow normal menu processing to proceed
    	return false;
    }
    
	/*
	 * Called when an activity you launched exits, giving you the requestCode you started it with, the resultCode it returned, and any additional data from it.
	 *
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	protected void onActivityResult ( int requestCode , int resultCode , Intent data ) {
    	// Check if the result is successful and the intent is valid
    	if ( resultCode != RESULT_OK )
    		// Exit method
    		return;
    	
		// Retrieve all the messages asynchronously (if any)
		new PopulateMessages ().execute ();
    }
    
	/**
	 * AsyncTask helper class used to populate the visit notes.
	 * 
	 * @author Elias
	 *
	 */
	private class PopulateMessages extends AsyncTask < Void , Void , Cursor > {
		
		/*
		 * Runs on the UI thread before doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute () {
			// Display indeterminate progress dialog
			AppDialog.getInstance ().displayIndeterminateProgress ( MessagesListActivity.this , null , null );
		}
		
		/*
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Cursor doInBackground ( Void ... params ) {
			// Retrieve the start of the day
			Calendar calendar = Calendar.getInstance ();
			// Retrieve the call
			Call call = (Call) getIntent ().getSerializableExtra ( CALL );
	    	// Declare and initialize a two digits string formatter
	    	DecimalFormat twoDigits = new DecimalFormat ( "00" );
			// Compute today's date string
			String today = calendar.get ( Calendar.YEAR ) + "-" + twoDigits.format ( calendar.get ( Calendar.MONTH ) + 1 ) + "-" + twoDigits.format ( calendar.get ( Calendar.DAY_OF_MONTH ) );
			// Build the SQL string
			String SQL = "SELECT M." + MsgHeaderDao.Properties.Id.columnName + " " + MsgHeaderDao.Properties.Id.columnName + " , " +
					"M." + MsgHeaderDao.Properties.MsgCode.columnName + " Code , " +
					"C." + ClientsDao.Properties.ClientName.columnName + " " + ClientsDao.Properties.ClientName.columnName + " , " +
					"M." + MsgHeaderDao.Properties.MsgTitle.columnName + " Title , " +
					"M." + MsgHeaderDao.Properties.MsgBody.columnName + " Body , " +
					"1 Type , " +
					"M." + MsgHeaderDao.Properties.MsgDate.columnName + " StampDate " +
					"FROM " + MsgHeaderDao.TABLENAME + " M " + 
					"LEFT JOIN " + ClientsDao.TABLENAME + " C  ON C." + ClientsDao.Properties.ClientCode.columnName + " = M." + MsgHeaderDao.Properties.SubjectCode.columnName + " " +
					"AND C." + ClientsDao.Properties.CompanyCode.columnName + " = M." + MsgHeaderDao.Properties.CompanyCode.columnName + " AND " + MsgHeaderDao.Properties.MsgType.columnName + " = 2 " +
					"WHERE strftime ( '%Y-%m-%d' , M." + MsgHeaderDao.Properties.EndDate.columnName + " / 1000 , 'unixepoch' , 'localtime' ) >= ? " +
					( call != null ? "AND ( C." + ClientsDao.Properties.ClientCode.columnName + " = '" + call.getClient ().getClientCode () + "' AND C." + ClientsDao.Properties.CompanyCode.columnName + " = '" + call.getClient ().getCompanyCode () + "' ) OR " + MsgHeaderDao.Properties.MsgType.columnName + " = 1 " : "" ) +
					"UNION " + 
					"SELECT N." + MsgNoteDao.Properties.Id.columnName + " " + MsgHeaderDao.Properties.Id.columnName + " , " +
					"N." + MsgNoteDao.Properties.MsgNoteCode.columnName + " Code , " +
					"C." + ClientsDao.Properties.ClientName.columnName + " " + ClientsDao.Properties.ClientName.columnName + " , " +
					"'' Title , " +
					"N." + MsgNoteDao.Properties.MsgBody.columnName + " Body , " +
					"2 Type , " +
					"N." + MsgNoteDao.Properties.StampDate.columnName + " StampDate " +
					"FROM " + MsgNoteDao.TABLENAME + " N " + 
					"LEFT JOIN " + ClientsDao.TABLENAME + " C  ON C." + ClientsDao.Properties.ClientCode.columnName + " = N." + MsgNoteDao.Properties.ClientCode.columnName + " " +
					"AND C." + ClientsDao.Properties.CompanyCode.columnName + " = N." + MsgNoteDao.Properties.CompanyCode.columnName + " " +
					( call != null ? "WHERE ( C." + ClientsDao.Properties.ClientCode.columnName + " = '" + call.getClient ().getClientCode () + "' AND C." + ClientsDao.Properties.CompanyCode.columnName + " = '" + call.getClient ().getCompanyCode () + "' ) OR C." + ClientsDao.Properties.ClientCode.columnName + " IS NULL " : "" ) +
					"ORDER BY StampDate DESC";
			// Initialize and return a cursor in order to query DB
			return DatabaseUtils.getInstance ( MessagesListActivity.this ).getDaoSession ().getDatabase ()
					.rawQuery ( SQL , new String [] { today } );
		}
		
		/*
		 * Runs on the UI thread after doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute ( Cursor cursor ) {
			// Dismiss dialog
			AppDialog.getInstance ().dismiss ();
			// Check if the cursor is valid
			if ( cursor != null )
				// Set the new adapter
				setListAdapter ( new MessagesAdapter ( MessagesListActivity.this , cursor , R.layout.messages_list_activity_item ) );
		}
		
	}

}