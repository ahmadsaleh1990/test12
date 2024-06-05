/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Objective;

import java.util.Calendar;

import android.content.Intent;
import android.database.Cursor;
import android.database.MergeCursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.ListViewPosition;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.ClientPropertiesDao;
import me.SyncWise.Android.Database.Clients;
import me.SyncWise.Android.Database.ClientsDao;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.ObjectiveAchievementsDao;
import me.SyncWise.Android.Database.ObjectiveAchievementsUtils;
import me.SyncWise.Android.Database.ObjectiveAssignmentsDao;
import me.SyncWise.Android.Database.ObjectiveAssignmentsUtils;
import me.SyncWise.Android.Database.ObjectivePrioritiesDao;
import me.SyncWise.Android.Database.ObjectivesDao;
import me.SyncWise.Android.Database.ObjectivesUtils;
import me.SyncWise.Android.Modules.SearchList.SearchListActivity;

/**
 * List Activity of all the client objectives with search capabilities and additional actions/details, in addition to the number of pending objectives.
 * 
 * @author Elias
 * @sw.todo	<b>Client Objective List Activity Implementation :</b><br>
 * Simply add this class in the {@code AndroidManifest.xml} file.
 *
 */
public class ClientObjectiveListActivity extends SearchListActivity {

	/*
	 * Called when the activity is starting.
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate ( Bundle savedInstanceState ) {
		// Define the properties which applies for a filter
		addProperty ( ClientsDao.Properties.ClientName );
		addProperty ( ClientsDao.Properties.ClientCode );
		// Superclass method call
		super.onCreate ( savedInstanceState );
		// Change the title associated with this activity
		setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.clients_list_activity_title ) );
		// Enable fast scrolling
		getListView ().setFastScrollEnabled ( true );
		// Define the empty list view
		getListView ().setEmptyView ( findViewById ( R.id.empty_list_view ) );
		// Display empty list label
		( (TextView) findViewById ( R.id.empty_list_view ) ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.empty_clients_list_label ) );
	}
	
	/*
	 * Initializes the search list activity components.
	 *
	 * @see me.SyncWise.Android.Modules.SearchList.SearchListActivity#initializeComponents(android.os.Bundle)
	 */
	@Override
	protected void initializeComponents ( Bundle savedInstanceState ) {
		// Determine if a saved instance state is provided
		if ( savedInstanceState != null ) {
			// Restore the content provided by the saved instance state
			setSearchQuery ( savedInstanceState.getString ( SEARCH_QUERY ) );
			setListViewPosition ( (ListViewPosition) savedInstanceState.getSerializable ( LIST_VIEW_POSITION ) );
		} // End if
		// Determine if the list view has a valid adapter
		if ( getListAdapter () == null ) {
			// Populate the list by setting a new adapter
			new PopulateList ().execute ();
		} // End if
	}
	
	/**
	 * Computes the SQL search query based on the provided filter criteria.
	 * 
	 * @return	String holding the SQL search query.
	 */
	protected String computeSearchQuery () {
		// Declare and initialize a string to host the search SQL query
		StringBuilder _searchQuery = new StringBuilder ();
		// Check if the search criteria is valid and if there is at least one property to filter
		if ( ! TextUtils.isEmpty ( getSearchQuery () ) && getPropertiesCount () != 0 ) {
			// Escape the character ' to protect from SQL injection
			String searchQuery = getSearchQuery ().replace ( "'" , "''" );
			// Build the first search condition out of the first property
			_searchQuery.append ( getProperty ( 0 ).columnName ).append ( " LIKE '%" ).append ( searchQuery ).append ( "%'" );
			// Iterate over the remaining properties
			for ( int i = 1 ; i < getPropertiesCount () ; i ++ )
				// Build the remaining conditions
				_searchQuery.append ( " OR " ).append ( getProperty ( i ).columnName ).append ( " LIKE '%" ).append ( searchQuery ).append ( "%'" );
		} // End if
		// Return the computed search query
		return _searchQuery.toString ();
	}
	
	/*
	 * Called when the query text is changed by the user.
	 *
	 * @see me.SyncWise.Android.Modules.SearchList.SearchListActivity#onQueryTextChange(java.lang.String)
	 */
	@Override
	public boolean onQueryTextChange ( String newText ) {
		// Update the new search query
		setSearchQuery ( newText );
		// Filter the list asynchronously
		new FilterList ( newText ).execute ();
		// Indicate that the action was handled by the listener
		return true;
	}
	
	/**
	 * Computes an appropriate cursor based on the provided table, selection and filter.
	 * 
	 * @return	Cursor	Cursor holding a result set returned by a database query.
	 */
	protected Cursor computeCursor () {
		// Retrieve the current date in the UNIX epoch format
		long today = Calendar.getInstance ().getTimeInMillis ();
		
		// Compute the SQL string
		String SQL = "SELECT C.* , ( SELECT COUNT (*) FROM ( SELECT DISTINCT O.* " +
		"FROM ( ( ( " + ObjectivesDao.TABLENAME + " O LEFT JOIN " + ObjectivePrioritiesDao.TABLENAME + " OP ON " +
				"O." + ObjectivesDao.Properties.ObjectivePriorityID.columnName + " = OP." + ObjectivePrioritiesDao.Properties.ObjectivePriorityID.columnName + " ) " +
				"INNER JOIN " + ObjectiveAssignmentsDao.TABLENAME + " OAS ON " +
				"O." + ObjectivesDao.Properties.ObjectiveID.columnName + " = OAS." + ObjectiveAssignmentsDao.Properties.ObjectiveID.columnName + " " + 
				"AND ( OAS." + ObjectiveAssignmentsDao.Properties.AssignmentType.columnName + " = ? OR OAS." + ObjectiveAssignmentsDao.Properties.AssignmentType.columnName + " = ? " +
				"OR ( OAS." + ObjectiveAssignmentsDao.Properties.AssignmentType.columnName + " = ? AND OAS." + ObjectiveAssignmentsDao.Properties.AssignmentCode.columnName + " = C." + ClientsDao.Properties.ClientCode.columnName + " ) " +
				"OR ( OAS." + ObjectiveAssignmentsDao.Properties.AssignmentType.columnName + " = ? AND OAS." + ObjectiveAssignmentsDao.Properties.AssignmentCode.columnName + " IN ( " +
				"SELECT CP." + ClientPropertiesDao.Properties.ClientPropertyValueCode.columnName + " FROM " + ClientPropertiesDao.TABLENAME + " CP WHERE CP." + ClientPropertiesDao.Properties.ClientCode.columnName + " = C." + ClientsDao.Properties.ClientCode.columnName + " ) ) ) ) " +
				"LEFT JOIN " + ObjectiveAchievementsDao.TABLENAME + " OAC ON " +
				"O." + ObjectivesDao.Properties.ObjectiveID.columnName + " = OAC." + ObjectiveAchievementsDao.Properties.ObjectiveID.columnName + " " +
				"AND OAC." + ObjectiveAchievementsDao.Properties.AchievementType.columnName + " = ? " +
				"AND OAC." + ObjectiveAchievementsDao.Properties.ClientCode.columnName + " = C." + ClientsDao.Properties.ClientCode.columnName + " ) " +
		"WHERE O." + ObjectivesDao.Properties.ObjectiveType.columnName + " = ? " +
				"AND OAC." + ObjectiveAchievementsDao.Properties.AchievementDate.columnName + " IS NULL " +
				"AND ( ( " + ObjectivesDao.Properties.StartDate.columnName + " IS NULL OR ? > " + ObjectivesDao.Properties.StartDate.columnName + " ) " +
				"AND ( " + ObjectivesDao.Properties.EndDate.columnName + " IS NULL OR ? < " + ObjectivesDao.Properties.EndDate.columnName + " ) ) ) ) " + ObjectivesDao.TABLENAME + " " +
		"FROM " + ClientsDao.TABLENAME + " C ";
		
		// Compute the 'order by' statement
		String orderBy = " ORDER BY " + ObjectivesDao.TABLENAME + " DESC , C." + ClientsDao.Properties.ClientName.columnName + " COLLATE NOCASE ASC";
		
		// Compute the SQL search query
		String _searchQuery = computeSearchQuery ();
		// Compute the selection statement
		String selection = TextUtils.isEmpty ( _searchQuery ) ? "" : "WHERE " + _searchQuery; 
		
		// Compute the selection arguments
		String selectionArguments [] = new String [] {
				String.valueOf ( ObjectiveAssignmentsUtils.Type.USER ) ,
				String.valueOf ( ObjectiveAssignmentsUtils.Type.GROUP_USERS ) ,
				String.valueOf ( ObjectiveAssignmentsUtils.Type.CLIENT ) ,
				String.valueOf ( ObjectiveAssignmentsUtils.Type.CLIENT_PROPERTIES ) ,
				String.valueOf ( ObjectiveAchievementsUtils.Type.CLIENT ) ,
				String.valueOf ( ObjectivesUtils.Type.CLIENT_OBJECTIVE ) ,
				String.valueOf ( today ) ,
				String.valueOf ( today )
		};
		
		// Declare and initialize a cursor in order to query DB
		Cursor cursor = DatabaseUtils.getInstance ( ClientObjectiveListActivity.this ).getDaoSession ().getDatabase ()
				.rawQuery ( SQL + selection + orderBy , selectionArguments );
		
		// Retrieve the cursor size
		int size = cursor.getCount ();
		// Check if the cursor size exceeds 1000 rows
		if ( size > 1000 ) {
			// Close the cursor
			cursor.close ();
			cursor = null;
			// Determine how many cursors (one cursor for every group of 1000 rows) are needed
			int number = (int) Math.ceil ( size / 1000.0 );
			// Declare and initialize an array of cursors
			Cursor cursors [] = new Cursor [ number ];
			// Iterate over the array of cursors
			for ( int i = 0 ; i < number ; i ++ )
				// Initialize the current cursor with the appropriate limit / offset
				cursors [ i ] = DatabaseUtils.getInstance ( ClientObjectiveListActivity.this ).getDaoSession ().getDatabase ()
					.rawQuery ( SQL + selection + orderBy + " LIMIT " + ( i * 1000 ) + ",1000" , selectionArguments );
			
			// Merge the array of cursors and return the result
			return new MergeCursor ( cursors );
		} // End if

		// Otherwise the cursor size does not exceed 1000 rows
		// Simply return the cursor
		return cursor;
	}
	
	/*
	 * This method will be called when an item in the list is selected.
	 *
	 * @see android.support.v4.app.ListFragment#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	@Override
	public void onListItemClick ( ListView listView , View view , int position , long id ) {
		// Retrieve a client object from the cursor
		Clients client = DatabaseUtils.getInstance ( this ).getDaoSession ().getClientsDao ().readEntity ( (Cursor) getListAdapter ().getItem ( position ) , 0 ); 
		// Create a new intent to start a new activity
		Intent intent = new Intent ( this , ClientObjectiveActivity.class );
		// Add the client object to the intent
		intent.putExtra ( ClientObjectiveActivity.CLIENT , client );
		// Add the IS VIEW flag to the intent
		intent.putExtra ( ClientObjectiveActivity.IS_VIEW , true );
		// Start the new activity
		startActivity ( intent );
	}
	
	/**
	 * AsyncTask helper class used to populate the list.
	 * 
	 * @author Elias
	 *
	 */
	private class PopulateList extends AsyncTask < Void , Void , Cursor > {
		
		/*
		 * Runs on the UI thread before doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute () {
			// Display indeterminate progress dialog
			AppDialog.getInstance ().displayIndeterminateProgress ( ClientObjectiveListActivity.this , null , null );
		}
		
		/*
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Cursor doInBackground ( Void ... params ) {
			// Compute and return a new cursor
			return computeCursor ();
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
			// Set the new adapter
			setListAdapter ( new ClientsCursorAdapter ( ClientObjectiveListActivity.this , cursor , R.layout.client_objective_list_activity_item ) );
			// Restore the list view position (if any)
			if ( getListViewPosition () != null ) {
				getListViewPosition ().restore ( getListView () );
				setListViewPosition ( null );
			} // End if
			// Enable the search view
			( (SearchView) findViewById ( R.id.search_view ) ).setEnabled ( true );
		}
		
	}
	
	/**
	 * AsyncTask helper class used to filter the list.
	 * 
	 * @author Elias
	 *
	 */
	private class FilterList extends AsyncTask < Void , Void , Cursor > {
		
		/**
		 * String holding the search query being applied on the list.
		 */
		private String searchQuery;
		
		/**
		 * Constructor.
		 * 
		 * @param searchQuery	String holding the search query being applied on the list.
		 */
		public FilterList ( String searchQuery ) {
			// Superclass method call
			super ();
			// Initialize attribute
			this.searchQuery = searchQuery;
		}
		
    	/*
    	 * Runs on the UI thread before doInBackground(Params...).
    	 * 
    	 * @see android.os.AsyncTask#onPreExecute()
    	 */
    	@Override
    	protected void onPreExecute () {
			// Set the visibility of the indeterminate progress bar in the title
			setProgressBarIndeterminateVisibility ( true );
    	}
		
		/*
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Cursor doInBackground ( Void ... params ) {
			// Compute and return a new cursor
			return computeCursor ();
		}
		
		/*
		 * Runs on the UI thread after doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute ( Cursor cursor ) {
			// Set the visibility of the indeterminate progress bar in the title
			setProgressBarIndeterminateVisibility ( false );
			// Check if the search query has changed
			if ( ! getSearchQuery ().equals ( searchQuery ) )
				// Do nothing, as another AsyncTask is executing and will return the appropriate result
				return;
			// Otherwise, determine if the list adapter is valid
			if ( getListAdapter () == null )
				// Invalid list adapter
				return;
			// Check if the list adapter is a CursorAdapter
			if ( ! ( getListAdapter () instanceof CursorAdapter ) )
				// Invalid list adapter
				return;
			// Set the new cursor
			( (CursorAdapter) getListAdapter () ).changeCursor ( cursor );
		}
		
	}
	
}