/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.CyclePlanning;

import java.util.ArrayList;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.ClientDivisionsDao;
import me.SyncWise.Android.Database.ClientsDao;
import me.SyncWise.Android.Modules.ClientsList.ClientsListActivity;
import me.SyncWise.Android.Modules.CyclePlanning.AddCallAdapter.ViewHolder;

/**
 * Activity implemented to add calls to the cycle planning.<br>
 * The selection / addition of additional clients is achieved via a drag and drop gesture.<br>
 * There is no need to modify/extend this class what so ever.
 * 
 * @author Elias
 *
 */
public class AddCallActivity extends ClientsListActivity implements OnItemLongClickListener {

	/**
	 * Boolean used to indicate if the activity is displayed on the screen.
	 */
	private boolean isDisplayed;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #selectedClients}.
	 */
	private static final String SELECTED_CLIENTS =  AddCallActivity.class.getName () + ".SELECTED_CLIENTS";
	
	/**
	 * List of strings holding the codes of the selected clients.
	 */
	private ArrayList < String > selectedClients;
	
	/**
	 * {@link android.os.AsyncTask AsyncTask} object used to select all clients.
	 */
	private static MarkAll markAll;
	
	/*
	 * Creates an image that the system displays during the drag and drop operation.
	 * 
	 * @see android.view.View.DragShadowBuilder
	 */
	private static class DragShadowBuilder extends android.view.View.DragShadowBuilder {
		/**
		 * Constructor.
		 * 
		 * @param view	A View. Any View in scope can be used.
		 */
		public DragShadowBuilder ( View view ) {
			// Super class method call
			super ( view );
		}

		/*
		 * Provides the metrics for the shadow image. These include the dimensions of the shadow image, and the point within that shadow that should be centered under the touch location while dragging.
		 *
		 * @see android.view.View.DragShadowBuilder#onProvideShadowMetrics(android.graphics.Point, android.graphics.Point)
		 */
		@Override
		public void onProvideShadowMetrics ( Point shadowSize , Point shadowTouchPoint ) {
			// Retrieve a reference to the view
			final View view = getView ();
			// Check if the view is valid
			if ( view != null ) {
				// Set the dimensions of the shadow relative to the dimensions of the View itself
				shadowSize.set ( view.getHeight () * 4 , view.getHeight () );
				// Center the shadow under the touch point
				shadowTouchPoint.set ( shadowSize.x / 2 , shadowSize.y / 2 );
			} // End if
		}
	}
	
	/*
	 * Called when the activity is starting.
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate ( Bundle savedInstanceState ) {
		// Display the activity as a pop up dialog with an action bar
		AppDialog.showAsDialog ( this , new AppDialog.Property ( 90 ) );
		// Enable extended window feature : indeterminate progress
 		requestWindowFeature ( Window.FEATURE_INDETERMINATE_PROGRESS );
		// Initialize attribute
 		selectedClients = new ArrayList < String > ();
		// Determine if a saved instance state is provided
		if ( savedInstanceState != null ) {
			// Restore the content provided by the saved instance state 
			selectedClients = (ArrayList < String >) savedInstanceState.getSerializable ( SELECTED_CLIENTS );
		} // End if
		// Superclass method call
		super.onCreate ( savedInstanceState );
		// Set the visibility of the indeterminate progress bar in the title
		setProgressBarIndeterminateVisibility ( false );
		// Register a callback to be invoked when an item in this AdapterView has been clicked and held
		getListView ().setOnItemLongClickListener ( this );
	}
	
	/*
	 * Builds a ListAdapter that will be set to the list.
	 *
	 * @see me.SyncWise.Android.Modules.SearchList.SearchCursorListActivity#populateList(android.database.Cursor)
	 */
	@Override
	protected ListAdapter populateList ( final Cursor cursor ) {
		// Initialize and return a client cursor adapter using the provided cursor
		return new AddCallAdapter ( this , cursor , R.layout.add_call_activity_item , selectedClients );
	}
	
	/**
	 * Displays the current number of selected clients in the action bar as a sub title.
	 */
	private void displaySelectedClientsNumber () {
		try {
			// Display the number of selected clients in the action bar
			switch ( selectedClients.size () ) {
			case 0:
				getActionBar ().setSubtitle ( AppResources.getInstance ( this ).getString ( this , R.string.no_selected_clients_message ) );
				break;
			case 1:
				getActionBar ().setSubtitle ( AppResources.getInstance ( this ).getString ( this , R.string.one_selected_client_message ) );
				break;
			default:
				getActionBar ().setSubtitle ( selectedClients.size () + " " + AppResources.getInstance ( this ).getString ( this , R.string.many_selected_clients_message ) );
				break;
			} // End switch
		} catch ( Exception exception ) {
			// Do nothing, do not display
		} // End of try-catch block
	}
	
	/*
	 * Called after onRestoreInstanceState(Bundle), onRestart(), or onPause(), for your activity to start interacting with the user.
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	public void onResume () {
		// Indicate that the activity is displayed
		isDisplayed = true;
		// Superclass method call
		super.onResume ();
		// Display / update the number of selected clients
		displaySelectedClientsNumber ();
	}
	
	/*
	 * Called as part of the activity lifecycle when an activity is going into the background, but has not (yet) been killed.
	 *
	 * @see android.app.Activity#onPause()
	 */
	@Override
    protected void onPause () {
		// Indicate that the activity is NOT displayed
		isDisplayed = false;
		// Superclass method call
		super.onPause ();
	}
	
    /*
     * Called to retrieve per-instance state from an activity before being killed so that the state can be restored in onCreate(Bundle) or onRestoreInstanceState(Bundle).
     *
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    protected void onSaveInstanceState ( Bundle outState ) {
    	// Superclass method call
    	super.onSaveInstanceState ( outState );
    	// Save the content of selectedClientCodes in the outState bundle
    	outState.putSerializable ( SELECTED_CLIENTS , selectedClients );
    }
	
    /*
     * This method will be called when an item in the list is selected.
     *
     * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
     */
    @Override
    protected void onListItemClick ( ListView listView , View view , int position , long id ) {
    	// Retrieve the current client code and division code
    	String clientAndDivisionCode = ( (ViewHolder) view.getTag () ).clientCode + "--" + ( (ViewHolder) view.getTag () ).divisionCode;
    	// Determine if the currently selected client is previously selected
    	if ( selectedClients.contains ( clientAndDivisionCode ) ) {
    		// Un-select the current client
    		selectedClients.remove ( clientAndDivisionCode );
    		( (ViewHolder) view.getTag () ).checkBox.setChecked ( false );
    	} // End if
    	else {
    		// Select the current client
    		selectedClients.add ( clientAndDivisionCode );
    		( (ViewHolder) view.getTag () ).checkBox.setChecked ( true );
    	} // End else
    	// Update the clients number
    	displaySelectedClientsNumber ();
    }
    
    /*
     * Callback method to be invoked when an item in this view has been clicked and held.
     *
     * @see android.widget.AdapterView.OnItemLongClickListener#onItemLongClick(android.widget.AdapterView, android.view.View, int, long)
     */
	@Override
	public boolean onItemLongClick ( AdapterView < ? > parent , View view , int position , long id ) {
    	// Retrieve the current client code
    	String clientAndDivisionCode = ( (ViewHolder) view.getTag () ).clientCode + "--" + ( (ViewHolder) view.getTag () ).divisionCode;
    	// Determine if the currently selected client is previously selected
    	if ( ! selectedClients.contains ( clientAndDivisionCode ) )
    		// Add the current client to the list
    		selectedClients.add ( clientAndDivisionCode );
    	
		// Retrieve a reference to the view's view holder
		ViewHolder viewHolder = (ViewHolder) view.getTag ();
		// Hide the check box
		viewHolder.checkBox.setVisibility ( View.GONE );
		// Hide the client code
		viewHolder.data.setText ( "" );
		// Display the number of selected clients
		viewHolder.name.setText ( "x " + selectedClients.size () );
		// Build a drag shadow builder for the current view
		DragShadowBuilder dragShadowBuilder = new DragShadowBuilder ( view );
		// Starts a drag and drop operation
		view.startDrag ( null , dragShadowBuilder , null , 0 );
		
		// Call this to set the result that your activity will return to its caller
		setResult ( RESULT_OK , new Intent ().putExtra ( CyclePlanningActivity.DRAGGED_CLIENTS , selectedClients ) );
		// Finish this activity
		AddCallActivity.this.finish ();
		// Indicate that the callback consumed the long click
		return true;
	}
    
	/*
	 * Initialize the contents of the Activity's standard options menu.
	 *
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
    @Override
    public boolean onCreateOptionsMenu ( Menu menu ) {
    	// Superclass method call
    	super.onCreateOptionsMenu ( menu );
		// Determine if the client filter list is currently displayed
		if ( ! displayFilter ) {
	    	// Enable the required menu items
	    	me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_mark_all ) , R.string.mark_label );
	    	me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_unmark_all ) , R.string.unmark_label );
	    	me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_save ) , R.string.save_label );
		} // End if
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
    	// Determine if the client filter list is currently displayed
    	// AND if the action save or unmark is selected
    	if ( displayFilter 
    			&& ( menuItem.getItemId () == R.id.action_unmark_all || menuItem.getItemId () == R.id.action_save ) )
    		// Superclass method call
    		return super.onOptionsItemSelected ( menuItem );
    	// Otherwise determine if the client filter list is currently NOT displayed
    	// AND if the action list is selected
    	if ( ! displayFilter && menuItem.getItemId () == R.id.action_list )
    		// Superclass method call
    		return super.onOptionsItemSelected ( menuItem );
    	
    	// Determine if the action mark is selected
    	if ( menuItem.getItemId () == R.id.action_mark_all ) {
    		// Check if previous actions ended
    		if ( markAll == null ) {
	    		// Mark all clients asynchronously
	    		markAll = new MarkAll ();
	    		markAll.execute ();
    		} // End if
    		// Consume event
    		return true;
    	} // End if
    	// Determine if the action unmark is selected
    	else if ( menuItem.getItemId () == R.id.action_unmark_all ) {
    		// Check if previous actions ended
    		if ( markAll == null ) {
	    		// Clear list of selected clients
    			selectedClients.clear ();
	    		// Refresh list view
	    		( (CursorAdapter) getListAdapter () ).notifyDataSetChanged ();
    		} // End if
    		// Update the number of selected clients
    		displaySelectedClientsNumber ();
    		// Consume event
    		return true;
    	} // End else if
    	// Determine if the action unmark is save
    	else if ( menuItem.getItemId () == R.id.action_save ) {
    		// Call this to set the result that your activity will return to its caller
    		setResult ( RESULT_OK , new Intent ().putExtra ( CyclePlanningActivity.DRAGGED_CLIENTS , selectedClients ).putExtra ( CyclePlanningActivity.ADD_SELECTED_CLIENTS , true ) );
    		// Finish this activity
    		AddCallActivity.this.finish ();
    		// Consume event
    		return true;
    	} // End else if
    	// Allow normal menu processing to proceed
    	return false;
    }
    
	/**
	 * AsyncTask helper class used to mark all clients by adding all the available client codes the list.
	 * 
	 * @author Elias
	 *
	 */
    private class MarkAll extends AsyncTask < Void , Void , Void > {
    	
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
		protected Void doInBackground ( Void ... params ) {
			// Retrieve the list adapter
			ListAdapter adpater = getListAdapter ();
			// Check if the list adapter is a valid instance of AddCallAdapter
			if ( adpater == null || ! ( adpater instanceof AddCallAdapter ) )
				// Invalid adapter, do nothing
				return null;
			// Cast the adapter
			AddCallAdapter addCallAdapter = (AddCallAdapter) adpater;
			// Iterate over all the rows
			for ( int i = 0 ; i < addCallAdapter.getCount () ; i ++ ) {
				// Position the internal cursor index
				Cursor cursor = (Cursor) addCallAdapter.getItem ( i );
				// Retrieve the client and division code
				String clientAndDivisionCode = cursor.getString ( cursor.getColumnIndex ( ClientsDao.Properties.ClientCode.columnName ) )
						+ "--" + cursor.getString ( cursor.getColumnIndex ( ClientDivisionsDao.Properties.DivisionCode.columnName ) );
				// Check if the client code is already added
				if ( ! selectedClients.contains ( clientAndDivisionCode ) )
					// Add the client code to the selected codes list
					selectedClients.add ( clientAndDivisionCode );
			} // End for loop
			return null;
		}
    	
		/*
		 * Runs on the UI thread after doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
	    protected void onPostExecute ( Void arg ) {
			// Determine if the current activity is displayed
			if ( isDisplayed ) {
				// Set the visibility of the indeterminate progress bar in the title
				setProgressBarIndeterminateVisibility ( false );
	    		// Refresh list view
	    		( (CursorAdapter) getListAdapter () ).notifyDataSetChanged ();
	    		// Update the number of selected clients
	    		displaySelectedClientsNumber ();
			} // End if
			// Clear the task reference
			markAll = null;
	    }
		
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
			selectedClients = null;
			getListView ().setOnItemLongClickListener ( null );
		} // End if
	}
    
}