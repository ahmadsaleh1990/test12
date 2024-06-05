/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.SearchList;

import java.util.ArrayList;
import java.util.List;

import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.ListViewPosition;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.DatabaseUtils;
import android.app.Activity;
import android.database.Cursor;
import android.database.MergeCursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.SearchView;

import de.greenrobot.dao.AbstractDao;

/**
 * A SearchListActivity implementation that uses a cursor as the list view source.<br>
 * This implementation works in conjunction with GreenDao, as the main table is provided as <b>AbstractDao</b>.
 * 
 * @author Elias
 * @sw.todo	<b>Search Cursor List Activity Implementation :</b><br>
 * <ul>
 * <li>Extend this class.</li>
 * <li>Define the properties used to apply the a filter on via the {@link #addProperty(de.greenrobot.dao.Property)} method in the {@link Activity#onCreate(Bundle)} method and before making the superclass call.</li>
 * <li>Set the database table used to retrieve the data from (via the cursor) via the {@link #setTable(AbstractDao)} or {@link #set} method in the {@link Activity#onCreate(Bundle)} method and before making the superclass call.</li>
 * <li>Set the selection condition used to filter the data, via the via the {@link #setSelection(String)} and {@link #setSelectionArguments(List)} methods in the {@link Activity#onCreate(Bundle)} method and before making the superclass call.</li>
 * <li>Set the list order used to sort the data, via the {@link #setOrder(String)} method in the {@link Activity#onCreate(Bundle)} method and before making the superclass call.</li>
 * <li>Override the method {@link #populateList(Cursor)} method, where you should return an adapter, using the provided cursor and a layout resource identifier for the list view items, that will be used to populate the list view widget.</li>
 * <li>Do not forget to add your new class in the {@code AndroidManifest.xml} file.</li>
 * </ul>
 * 
 * <b>NOTE : </b> If you happen to use {@link #setSelection(String)} or {@link #setSelectionArguments(ArrayList)} after the activity creation, make sure to call {@link #repopulateList()} in order to apply the new values.
 * 
 */
public abstract class SearchCursorListActivity extends SearchListActivity {

	/**
	 * Reference to the AbstractDao, which in turns references the main table.
	 */
	private AbstractDao < ? , ? > table;
	
	/**
	 * String hosting the select from statement of the main query.
	 */
	private String mainQuery;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #selection}.
	 */
	protected static final String SELECTION = SearchListActivity.class.getName () + ".SELECTION";
	
	/**
	 * String holding the selection condition : where statement.
	 */
	private String selection;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #selectionArguments}.
	 */
	protected static final String SELECTION_ARGUMENTS = SearchListActivity.class.getName () + ".SELECTION_ARGUMENTS";
	
	/**
	 * List holding the selection arguments.
	 */
	private ArrayList < String > selectionArguments;
	
	/**
	 * String holding the order condition : order by statement.
	 */
	private String order;
	
	/**
	 * Setter - {@link #table}
	 * 
	 * @param table	Base AbstractDao object representing the main table.
	 */
	public void setTable ( final AbstractDao < ? , ? > table ) {
		// Store the AbstractDao object
		this.table = table;
	}
	
	/**
	 * Getter - {@link #table}
	 * 
	 * @return	Base AbstractDao object representing the main table.
	 */
	public AbstractDao < ? , ? > getTable () {
		// Return the AbstractDao object
		return table;
	}
	
	/**
	 * Setter - {@link #mainQuery}
	 * 
	 * @param mainQuery	String hosting the select from statement of the main query.
	 */
	public void setMainQuery ( final String mainQuery ) {
		// Store the AbstractDao object
		this.mainQuery = mainQuery;
	}
	
	/**
	 * Getter - {@link #mainQuery}
	 * 
	 * @return	String hosting the select from statement of the main query.
	 */
	public String getMainQuery () {
		// Return the main query string
		return mainQuery;
	}
	
	/**
	 * Setter - {@link #selection}
	 * 
	 * @param selection String holding the where statement.
	 */
	public void setSelection ( final String selection ) {
		// Store the where statement
		this.selection = selection;
	}
	
	/**
	 * Getter - {@link #selection}
	 * 
	 * @return	String holding the where statement.
	 */
	public String getSelection () {
		// Return the where statement
		return selection;
	}
	
	/**
	 * Setter - {@link #selectionArguments}
	 * 
	 * @param selectionArguments	List of strings holding the where arguments.
	 */
	public void setSelectionArguments ( final ArrayList < String > selectionArguments ) {
		// Store the where arguments list
		this.selectionArguments = selectionArguments;
	}
	
	/**
	 * Setter - {@link #order}
	 * 
	 * @param order	String holding the order condition : order by statement.
	 */
	public void setOrder ( final String order ) {
		// Store the order by statement
		this.order = order;
	}
	
	/*
	 * Called when the activity is starting.
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate ( Bundle savedInstanceState ) {
		// Enable extended window feature : indeterminate progress
 		requestWindowFeature ( Window.FEATURE_INDETERMINATE_PROGRESS );
 		// Superclass method call
 		super.onCreate ( savedInstanceState );
		// Set the visibility of the indeterminate progress bar in the title
		setProgressBarIndeterminateVisibility ( false );
	}
	
	/*
	 * Initializes the search list activity components.
	 *
	 * @see me.SyncWise.Android.Modules.SearchList.SearchListActivity#initializeComponents(android.os.Bundle)
	 */
	@Override
	protected void initializeComponents ( Bundle savedInstanceState ) {
		// Check if the main table or main query is valid
		if ( table == null && mainQuery == null )
			// Invalid table and main query
			return;
		// Determine if a saved instance state is provided
		if ( savedInstanceState != null ) {
			// Restore the content provided by the saved instance state
			setSearchQuery ( savedInstanceState.getString ( SEARCH_QUERY ) );
			setListViewPosition ( (ListViewPosition) savedInstanceState.getSerializable ( LIST_VIEW_POSITION ) );
			selection = savedInstanceState.getString ( SELECTION );
			selectionArguments = savedInstanceState.getStringArrayList ( SELECTION_ARGUMENTS );
		} // End if
		// Determine if the list view has a valid adapter
		if ( getListAdapter () == null ) {
			// Populate the list by setting a new adapter
			new PopulateList ().execute ();
		} // End if
	}
	
    /*
     * Called to retrieve per-instance state from an activity before being killed so that the state can be restored in onCreate(Bundle) or onRestoreInstanceState(Bundle).
     *
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    protected void onSaveInstanceState ( Bundle outState ) {
    	// Superclass method call
    	super.onSaveInstanceState ( outState );
    	// Save the content of selection in the outState bundle
    	outState.putString ( SELECTION , selection );
    	// Save the content of selectionArguments in the outState bundle
    	outState.putStringArrayList ( SELECTION_ARGUMENTS , selectionArguments );
    }
	
	/*
	 * Called when you are no longer visible to the user.
	 *
	 * @see android.app.Activity#onStop()
	 */
	@Override
	public void onStop () {
    	// Superclass method call
		super.onStop ();
		// Determine if the activity is finishing
		if ( isFinishing () ) {
			table = null;
			mainQuery = null;
			selection = null;
			selectionArguments = null;
			order = null;
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
			_searchQuery.append ( getPropertyPrefix () ).append ( getProperty ( 0 ).columnName ).append ( " LIKE '%" ).append ( searchQuery ).append ( "%'" );
			// Iterate over the remaining properties
			for ( int i = 1 ; i < getPropertiesCount () ; i ++ )
				// Build the remaining conditions
				_searchQuery.append ( " OR " ).append ( getPropertyPrefix () ).append ( getProperty ( i ).columnName ).append ( " LIKE '%" ).append ( searchQuery ).append ( "%'" );
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
	 * Computes an appropriate cursor based on the provided table, selection and filter.<br>
	 * <b>Warning !</b> At least one of the variables {@link #table} and {@link #mainQuery} should be valid before making a call to this method.
	 * 
	 * @return	Cursor	Cursor holding a result set returned by a database query.
	 */
	protected Cursor computeCursor () {
		// Define the selection arguments
		String [] _selectionArguments = null;
		if ( selectionArguments == null || selectionArguments.isEmpty () ) {
			// Do nothing
		} // End if
		else {
			// Store the list of string arguments as an array of strings
			_selectionArguments = new String [ selectionArguments.size () ];
			_selectionArguments = selectionArguments.toArray ( _selectionArguments );
		} // End else
		
		// Compute the SQL search query
		String _searchQuery = computeSearchQuery ();
		// Compute the selection statement
		String _selection = ( TextUtils.isEmpty ( _searchQuery ) ? selection : ( TextUtils.isEmpty ( selection ) ? _searchQuery : "(" + selection + ") AND (" + _searchQuery + ")" ) );
		if ( ! TextUtils.isEmpty ( _selection == null ? null : _selection.trim () ) && table == null && mainQuery != null ) 
			_selection = " WHERE " + _selection;
		
		// Declare and initialize a cursor in order to query DB
		Cursor cursor = null;
		// Check if the main table is provided
		if ( table != null )
			// Query the given table, returning a Cursor over the result set
			cursor = DatabaseUtils.getInstance ( SearchCursorListActivity.this ).getDaoSession ().getDatabase ()
				.query ( table.getTablename () , table.getAllColumns () , _selection , _selectionArguments , null , null , order );
		// Otherwise check if the main table is provided
		else if ( mainQuery != null )
			// Execute the main query, returning a Cursor over the result set
			cursor = DatabaseUtils.getInstance ( SearchCursorListActivity.this ).getDaoSession ().getDatabase ()
					.rawQuery ( mainQuery + " " + _selection + " " + ( TextUtils.isEmpty ( order ) ? "" : " ORDER BY " + order ) ,
							_selectionArguments );
		
		// Check if the cursor is valid
		if ( cursor == null )
			// Invalid cursor
			return null;
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
			for ( int i = 0 ; i < number ; i ++ ) {
				// Check if the main table is provided
				if ( table != null )
					// Initialize the current cursor with the appropriate limit / offset
					cursors [ i ] = DatabaseUtils.getInstance ( SearchCursorListActivity.this ).getDaoSession ().getDatabase ()
						.query ( table.getTablename () , table.getAllColumns () , _selection , _selectionArguments , null , null , order , ( i * 1000 ) + ",1000" );
				// Otherwise check if the main table is provided
				else if ( mainQuery != null )
					// Initialize the current cursor with the appropriate limit / offset
					cursors [ i ] = DatabaseUtils.getInstance ( SearchCursorListActivity.this ).getDaoSession ().getDatabase ()
						.rawQuery ( mainQuery + " " + _selection + " " + ( TextUtils.isEmpty ( order ) ? "" : " ORDER BY " + order ) + " LIMIT " + ( i * 1000 ) + ",1000" ,
								_selectionArguments );
			} // End for loop
			// Merge the array of cursors and return the result
			return new MergeCursor ( cursors );
		} // End if

		// Otherwise the cursor size does not exceed 1000 rows
		// Simply return the cursor
		return cursor;
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
			AppDialog.getInstance ().displayIndeterminateProgress ( SearchCursorListActivity.this , null , null );
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
			setListAdapter ( populateList ( cursor ) );
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
	 * Builds a ListAdapter that will be set to the list.<br>
	 * This method should be implemented by subclasses, using the provided cursor.
	 * 
	 * @param cursor	Cursor holding a result set returned by a database query.
	 * @return	A ListAdapter that will be set to the list view.
	 */
	protected abstract ListAdapter populateList ( final Cursor cursor );
	
	/**
	 * Populates the list from scratch, using the provided data.
	 */
	protected void repopulateList () {
		// Populate the list by setting a new adapter
		new PopulateList ().execute ();
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