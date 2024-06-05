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
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.reflect.TypeToken;

import me.SyncWise.Android.AppAnimation;
import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.DateTime;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Text;
import me.SyncWise.Android.Vibration;

import me.SyncWise.Android.Database.AreaLevelsDao;

import me.SyncWise.Android.Database.AreasDao;

import me.SyncWise.Android.Database.ClientAreasDao;
import me.SyncWise.Android.Database.CycleCalls;
import me.SyncWise.Android.Database.CycleCallsDao;
import me.SyncWise.Android.Database.Clients;
import me.SyncWise.Android.Database.ClientsDao;
import me.SyncWise.Android.Database.Cycles;
import me.SyncWise.Android.Database.CyclesDao;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.EntityUtils;
import me.SyncWise.Android.Database.IsProcessedUtils;
import me.SyncWise.Android.Database.PermissionsUtils;
import me.SyncWise.Android.Database.StatusUtils;
import me.SyncWise.Android.Database.UserCycles;
import me.SyncWise.Android.Database.UserCyclesDao;
import me.SyncWise.Android.Gson.BaseTimerActivity;
import me.SyncWise.Android.Utilities.ActivityInstance;
import me.SyncWise.Android.Utilities.Cycle;
import me.SyncWise.Android.Widgets.Baguette;
import me.SyncWise.Android.Widgets.SegmentedRadioGroup;
import me.SyncWise.Android.Widgets.DSLV.DragSortController;
import me.SyncWise.Android.Widgets.DSLV.DragSortListView;
import me.SyncWise.Android.Widgets.DSLV.DragSortListView.DragSortListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.widget.HeaderViewListAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Activity implemented to edit and view cycle planning.
 * 
 * @author Elias
 * @sw.todo	<b>Cycle Planning Activity Implementation :</b><br>
 * <ul>
 * <li>If you want the default implementation, simply add this class in the {@code AndroidManifest.xml} file.<br>
 * AND disable activity recreation for orientation events by adding the following to the activity tag in the manifest file :<br>
 * {@code android:configChanges="orientation|screenSize"}
 * Do not forget to add permission {@code  <uses-permission android:name="android.permission.VIBRATE"/>} in the {@code AndroidManifest.xml} file.<br>
 * The default behavior is described below:
 * <ul>
 * <li>The cycle planning cannot be modified.</li>
 * <li>The maximum number of daily clients calls in the cycle is specified by the {@link me.SyncWise.Android.Database.PermissionsUtils#getMaxDailyCallsPerCycle(android.content.Context, String) PermissionsUtils.getMaxDailyCallsPerCycle} helper method.<br>
 * This number is <b>ONLY</b> applied if the user is trying to add additional calls, and not for the retrieved calls from the database. 
 * </ul></li>
 * </ul>
 * <br><em><h1>OR</h1></em><br>
 * In order to customize the module, please follow the steps below :
 * <ul>
 * <li>Extend this class.</li>
 * <li>Determine if the user can modify his/her cycle via the {@link #setModificationsEnabled(boolean)} method.</li>
 * <li>Do not forget to add your new class and the class {@link me.SyncWise.Android.Modules.CyclePlanning.AddCallActivity AddCallActivity} if users are allowed to build/modify their cycle in the {@code AndroidManifest.xml} file.
 * AND disable activity recreation (for the new class only) for orientation events by adding the following to the activity tag in the manifest file :<br>
 * {@code android:configChanges="orientation|screenSize"}</li>
 * </ul>
 *
 */
public class CyclePlanningActivity extends BaseTimerActivity implements OnClickListener , OnCheckedChangeListener , OnDragListener , DragSortListener {
	
	/**
	 * Flag used to indicate if the debug output is to be displayed or not.
	 */
    static boolean showDebugOutput = false;
	
    /**
     * Tag used to identify the source of a log message.
     */
    public static final String TAG = "CYCLE PLANNING";
    
	/**
	 * Constant integer holding the hover duration of the enter and exit animation triggered during the enter and exit drop area, in milliseconds.
	 */
	private static final int HOVER_ANIMATION_DURATION = 500;
	
	/**
	 * Boolean used to indicate if the activity is displayed on the screen.
	 */
	private boolean isDisplayed;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #isExpanded}.
	 */
	private static final String IS_EXPANDED = CyclePlanningActivity.class.getName () + ".IS_EXPANDED";
	
	/**
	 * Boolean used to indicate if the weeks and days tab are expanded / narrowed.
	 */
	private boolean isExpanded;
	
	/**
	 * Reference to the push cycle dialog.
	 */
	private Dialog pushCycleDialog;
	
	/**
	 * Reference to the drag sort list view used to host the cycle calls.
	 */
	private DragSortListView dslv;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #calls}.
	 */
	private static final String CALLS = CyclePlanningActivity.class.getName () + ".CALLS";
	
	/**
	 * List of {@link me.SyncWise.Android.Modules.CyclePlanning.Call Call} objects used to define the cycle calls.
	 */
	private ArrayList < Call > calls;
	
	/**
	 * Flag used to determine if there are modified calls to retrieve.
	 */
	private boolean retrieveModifiedCalls;
	
	/**
	 * List of {@link me.SyncWise.Android.Modules.CyclePlanning.Call Call} objects hosting the appropriate calls for the selected week and day.
	 */
	private ArrayList < Call > selectedCalls;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #selectedDay}.
	 */
	private static final String SELECTED_DAY = CyclePlanningActivity.class.getName () + ".SELECTED_DAY";
	
	/**
	 * String holding the selected day abbreviation.
	 */
	private String selectedDay;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #selectedWeek}.
	 */
	private static final String SELECTED_WEEK = CyclePlanningActivity.class.getName () + ".SELECTED_WEEK";
	
	/**
	 * Integer holding the selected week number.<br>
	 * This variable is BASED 1, meaning that the first week has the value 1 (and not 0).
	 */
	private int selectedWeek;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #currentWeek}.
	 */
	private static final String CURRENT_WEEK = CyclePlanningActivity.class.getName () + ".CURRENT_WEEK";
	
	/**
	 * Integer holding the current week number.<br>
	 * This variable is BASED 1, meaning that the first week has the value 1 (and not 0).
	 */
	private int currentWeek;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #draggedClients}.
	 */
	public static final String DRAGGED_CLIENTS = CyclePlanningActivity.class.getName () + ".DRAGGED_CLIENTS";
	
	/**
	 * Bundle key used to directly add selected clients.<br>
	 * This boolean is used if the user has pressed the save button, usually used if the drag and drop feature is disabled in android. 
	 */
	public static final String ADD_SELECTED_CLIENTS = CyclePlanningActivity.class.getName () + ".ADD_SELECTED_CLIENTS";
	
	/**
	 * List of strings holding the codes of the dragged and dropped clients.
	 */
	private ArrayList < String > draggedClients;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #activityForResultStarted}.
	 */
	public static final String ACTIVITY_FOR_RESULT_STARTED = CyclePlanningActivity.class.getName () + ".ACTIVITY_FOR_RESULT_STARTED";
	
	/**
	 * Flag used to indicate if an activity for a result is started.<br>
	 * This flag is mainly used in order not to start the {@link CyclePlanningActivity.DropClients} task twice.
	 */
	private boolean activityForResultStarted;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #isEdit}.
	 */
	private static final String IS_EDIT = CyclePlanningActivity.class.getName () + ".IS_EDIT";
	
	/**
	 * Flag used to indicate if the adapter is in the edit mode.<br>
	 * Mainly this flag is used to display / hide the drag handle accordingly in order to use the drag sort list view functionalities. 
	 */
	private boolean isEdit;
	
	/**
	 * Flag used to determine if the user is allowed to modify his/her cycle planning.
	 */
	private boolean isModificationsEnabled;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #maxDailyCallsPerCycle}.
	 */
	private static final String MAX_DAILY_CALLS_PER_CYCLE = CyclePlanningActivity.class.getName () + ".MAX_DAILY_CALLS_PER_CYCLE";
	
	/**
	 * Integer used to host the maximum number of daily calls per cycle.
	 */
	private int maxDailyCallsPerCycle;
	
	/**
	 * Setter - {@link #isModificationsEnabled}
	 * 
	 * @param modificationsEnabled	Boolean indicating if the user is allowed to modify his/her cycle planning.
	 */
	public void setModificationsEnabled ( final boolean modificationsEnabled ) {
		// Store flag
		isModificationsEnabled = modificationsEnabled;
		// Check if the activity is displayed
		if ( isDisplayed )
			// Since the activity is already displayed, refresh the action bar menus to apply the change
			invalidateOptionsMenu ();
	}
	
	/**
	 * Class that starts and stops item drags on a DragSortListView based on touch gestures.<br>
	 * This custom implementation forbids the user from removing/sorting items if the selected cycle call is NOT editable.
	 * 
	 * @author Elias
	 *
	 */
	private class CycleCallsDSLVController extends DragSortController {

		/**
		 * Constructor.
		 * 
		 * @param dslv	The DSLV instance.
		 */
		public CycleCallsDSLVController ( DragSortListView dslv ) {
			// Superclass method call
			super ( dslv , R.id.drag_handle , DragSortController.ON_DOWN , DragSortController.FLING_RIGHT_REMOVE );
			// Enable the remove mode
			setRemoveEnabled ( true );
			// Enable the sort mode
			setSortEnabled ( true );
			// Set the selection background color 
			setBackgroundColor ( getResources ().getColor ( R.color.PowderBlue ) );
		}
		
		/*
		 * Checks for the touch of an item's drag handle (specified by setDragHandleId(int)), and returns that item's position if a drag handle touch was detected.
		 *
		 * @see me.SyncWise.Android.Widgets.DSLV.DragSortController#dragHandleHitPosition(android.view.MotionEvent)
		 */
		@Override
		public int dragHandleHitPosition ( MotionEvent event ) {
			// Retrieve the drag handle hit position by making a superclass method call
			int result = super.dragHandleHitPosition ( event );
			// Check the result
			if ( result == MISS )
				// The result is unsuccessful
				return result;
			// Check if the retrieved position points to an editable cycle call
			else if ( ( (Call) dslv.getItemAtPosition ( result ) ).getEditable () )
				// The cycle call is editable, return the position
				return result;
			// Otherwise the retrieved position points to a fixed cycle call
			else
				// The cycle call is NOT editable, indicate that the result is unsuccessful
				return MISS;
		}
		
	}
	
	/**
	 * AsyncTask helper class used to retrieve the list of all available calls.
	 * 
	 * @author Elias
	 *
	 */
	private class RetrieveCalls extends AsyncTask < Void , Void , Void > {
		
		/*
		 * Runs on the UI thread before doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute () {
			// Display indeterminate progress dialog
			AppDialog.getInstance ().displayIndeterminateProgress ( CyclePlanningActivity.this , null , null );
		}
		
		/*
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@SuppressWarnings("unchecked")
		@SuppressLint("UseSparseArrays")
		@Override
		protected Void doInBackground ( Void ... params ) {
			// Retrieve the user code
			String userCode = DatabaseUtils.getCurrentUserCode ( CyclePlanningActivity.this );
			// Retrieve the company code
			String companyCode = DatabaseUtils.getCurrentCompanyCode ( CyclePlanningActivity.this );
			// Retrieve the maximum number of daily calls per cycle permission
			maxDailyCallsPerCycle = PermissionsUtils.getMaxDailyCallsPerCycle ( CyclePlanningActivity.this , userCode , companyCode );
			
			// Retrieve all the user cycles
			List < UserCycles > userCycles = DatabaseUtils.getInstance ( CyclePlanningActivity.this ).getDaoSession ().getUserCyclesDao ().queryBuilder ()
				.where ( UserCyclesDao.Properties.UserCode.eq ( userCode ) ).list ();
			// Get all the user cycle IDs
			List < Integer > userCycleIDs = new ArrayList < Integer > ();
			// Iterate over all user cycles
			for ( UserCycles userCycle : userCycles )
				// Add the current user cycle ID
				userCycleIDs.add ( userCycle.getCycleID () );
			
			// Retrieve all the cycles based on the retrieved user cycles
			List < Cycles > cycles = DatabaseUtils.getInstance ( CyclePlanningActivity.this ).getDaoSession ().getCyclesDao ().queryBuilder ()
				.where ( CyclesDao.Properties.CycleID.in ( userCycleIDs ) ).list ();
			// Map all the cycles to their cycle Ids
			Map < Integer , Cycles > _cycles = new HashMap < Integer , Cycles > ();
			// Iterate over all cycles
			for ( Cycles cycle : cycles )
				// Map the current cycle to its cycle ID 
				_cycles.put ( cycle.getCycleID () , cycle );
			// Declare and initialize a list of integers used to host the IDs of the editable cycles
			List < Integer > editableCycleIDs = new ArrayList < Integer > ();
			// Iterate over all cycles
			for ( Cycles cycle : cycles )
				// Determine if the cycle is editable
				if ( cycle.getCycleStatus () == StatusUtils.isEditable () )
					// Add the cycle ID to the list
					editableCycleIDs.add ( cycle.getCycleID () );
			
			// Retrieve a list of all available cycle calls based on the retrieved cycles
			List < CycleCalls > cycleCalls = DatabaseUtils.getInstance ( CyclePlanningActivity.this ).getDaoSession ().getCycleCallsDao ().queryBuilder ()
				.where ( CycleCallsDao.Properties.CycleCallStatus.eq ( StatusUtils.isAvailable () ) ,
						CycleCallsDao.Properties.CycleID.in ( _cycles.keySet () ) ).list ();
			// Get all the client codes
			List < String > clientCodes = new ArrayList < String > ();
			// Iterate over all cycle calls
			for ( CycleCalls cycleCall : cycleCalls ) {
				// Check if the client code is already added
				if ( ! clientCodes.contains ( cycleCall.getClientCode () ) )
					// Add the current client code
					clientCodes.add ( cycleCall.getClientCode () );
			} // End for each
			
			// Retrieve a list of the clients related to the retrieved cycle calls
			List < Clients > clients = EntityUtils.queryByGroup ( clientCodes ,
					DatabaseUtils.getInstance ( CyclePlanningActivity.this ).getDaoSession ().getClientsDao () ,
					ClientsDao.Properties.ClientCode );
			// Map the clients to their client codes
			Map < String , Clients > _clients = new HashMap < String , Clients > ();
			// Iterate over all clients
			for ( Clients client : clients )
				// Map the current client to its code
				_clients.put ( client.getClientCode () , client );
			
			// Retrieve the areas, along with their levels, related to retrieved cycle calls
			HashMap < String , HashMap < String , String > > areas = new HashMap < String , HashMap < String , String > > ();
			HashMap < String , String > areaLevels = new HashMap < String , String > ();
			// Compute the SQL string
			String SQL = "SELECT C." + ClientsDao.Properties.ClientCode.columnName + " , " +
					"AL." + AreaLevelsDao.Properties.AreaLevelCode.columnName + " , " +
					"AL." + AreaLevelsDao.Properties.AreaLevelDescription.columnName + " , " +
					"A." + AreasDao.Properties.AreaDescription.columnName + " " +
					"FROM " + ClientsDao.TABLENAME + " C INNER JOIN " + ClientAreasDao.TABLENAME + " CA " +
						"ON C." + ClientsDao.Properties.ClientCode.columnName + "  = CA." + ClientAreasDao.Properties.ClientCode.columnName + " " +
					"INNER JOIN " + AreasDao.TABLENAME + " A " +
						"ON A." + AreasDao.Properties.AreaCode.columnName + " = CA." + ClientAreasDao.Properties.AreaCode.columnName + " " +
					"INNER JOIN " + AreaLevelsDao.TABLENAME + " AL " +
						"ON AL." + AreaLevelsDao.Properties.AreaLevelCode.columnName + " = A." + AreasDao.Properties.AreaLevelCode.columnName + " " +
					"WHERE C." + ClientsDao.Properties.ClientCode.columnName + " IN ( " +
						"SELECT " + CycleCallsDao.Properties.ClientCode.columnName + " FROM " + CycleCallsDao.TABLENAME + " " +
						"WHERE " + CycleCallsDao.Properties.CycleID.columnName + " IN ( " +
							"SELECT " + UserCyclesDao.Properties.CycleID.columnName + " FROM " + UserCyclesDao.TABLENAME + " " +
							"WHERE " + UserCyclesDao.Properties.UserCode.columnName + " = ? ) " +
						"AND " + CycleCallsDao.Properties.CycleCallStatus.columnName + " = ? )";
			// Compute the selection arguments
			String selectionArguments [] = new String [] {
				userCode , String.valueOf ( StatusUtils.isAvailable () )	
			};
			// Query DB to retrieve the corresponding areas
			Cursor cursor = DatabaseUtils.getInstance ( CyclePlanningActivity.this ).getDaoSession ().getDatabase ()
					.rawQuery ( SQL , selectionArguments );
			// Move the cursor to the first raw
			if ( cursor.moveToFirst () ) {
				// Iterate over all the raws
				do {
					// Retrieve the client code
					String clientCode = cursor.getString ( cursor.getColumnIndex ( ClientsDao.Properties.ClientCode.columnName ) );
					// Retrieve the area level code
					String areaLevelCode = cursor.getString ( cursor.getColumnIndex ( AreaLevelsDao.Properties.AreaLevelCode.columnName ) );
					// Retrieve the area level description
					String areaLevelDescription = cursor.getString ( cursor.getColumnIndex ( AreaLevelsDao.Properties.AreaLevelDescription.columnName ) );
					// Retrieve the area description
					String areaDescription = cursor.getString ( cursor.getColumnIndex ( AreasDao.Properties.AreaDescription.columnName ) );
					
					// Check if the current area level is previously mapped
					if ( ! areaLevels.containsKey ( areaLevelCode ) )
						// Map the current area level description
						areaLevels.put ( areaLevelCode , areaLevelDescription );
					
					// Check if the current client code is previously mapped
					if ( areas.containsKey ( clientCode ) ) {
						// Retrieve the areas map
						HashMap < String , String > _areas = areas.get ( clientCode );
						// Check if the current area level is previously mapped
						if ( _areas.containsKey ( areaLevelCode ) )
							// Add the current area description
							_areas.put ( areaLevelCode , _areas.get ( areaLevelCode ) + " - " + areaDescription );
						else
							// Otherwise map the current area description
							_areas.put ( areaLevelCode , areaDescription );
					} // End if
					else {
						// Map the current area description
						HashMap < String , String > _areas = new HashMap < String , String > ();
						_areas.put ( areaLevelCode , areaDescription );
						// Map the current client
						areas.put ( clientCode , _areas );
					} // End else
				} while ( cursor.moveToNext () );
			} // End if
			// Clear cursor
			cursor.close ();
			cursor = null;

			// Declare and initialize a list for modified calls
			ArrayList < Call > modifiedCalls = new ArrayList < Call > ();
			// Check if there are modified calls
			if ( retrieveModifiedCalls )
				// Retrieve modified calls
				modifiedCalls = (ArrayList < Call >) ActivityInstance.readDataGson ( CyclePlanningActivity.this , CyclePlanningActivity.class.getName () , CALLS , new TypeToken < ArrayList < Call > > () {}.getType () );
			
			// Initialize the list of calls
			calls = new ArrayList < Call > ();
			// Iterate over all cycle calls
			for ( CycleCalls cycleCall : cycleCalls ) { // TODO
//				// Refresh the call object
//				DatabaseUtils.getInstance ( CyclePlanningActivity.this ).getDaoSession ().getCycleCallsDao ().refresh ( cycleCall );
//				// Create a new call using the current cycle call and the appropriate client object
//				Call call = new Call ( cycleCall , _clients.get ( cycleCall.getClientCode () ) );
//				// Check if the current call has any related areas
//				if ( areas.containsKey ( cycleCall.getClientCode () ) ) {
//					// Retrieve the areas map
//					HashMap < String , String > _areas = areas.get ( cycleCall.getClientCode () );
//					// Iterate over all the areas
//					for ( String areaLevelCode : _areas.keySet () )
//						// Add the related area(s) to the call
//						call.addArea ( areaLevels.get ( areaLevelCode ) , _areas.get ( areaLevelCode ) );
//				} // End if
//				// Check if the call is modified
//				if ( modifiedCalls.contains ( call ) ) {
//					// Retrieve the modified call
//					Call modifiedCall = modifiedCalls.get ( modifiedCalls.indexOf ( call ) );
//					// Add the modified call to the calls list
//					calls.add ( modifiedCall );
//					// Remove the modified call from the modified calls list
//					modifiedCalls.remove ( modifiedCall );
//				} // End if
//				else {
//					// Make the call editable accordingly, based on its cycle
//					if ( editableCycleIDs.contains ( cycleCall.getCycleID () ) )
//						call.setEditable ( true );
//					// Store the cycle name
//					call.setCycleName ( _cycles.get ( cycleCall.getCycleID () ).getCycleName () );
//					// Add the call to the list
//					calls.add ( call );
//				} // End else
			} // End for each
			// Add the remaining modified calls
			calls.addAll ( modifiedCalls );

			// Populate the list asynchronously
			populateList ();
			// Clear the task reference
			retrieveCalls = null;
			return null;
		}
		
		/*
		 * Runs on the UI thread after doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute ( Void arg ) {
			// Set a new adapter
			dslv.setAdapter ( new CyclePlanningAdapter ( CyclePlanningActivity.this , R.layout.cycle_planning_activity_item , selectedCalls , isEdit ) );
			// Dismiss dialog
			AppDialog.getInstance ().dismiss ();
		}
		
	}
	
	/**
	 * Reference to the calls list filling task.
	 */
	private static RetrieveCalls retrieveCalls;
	
	/**
	 * AsyncTask helper class used to populate the cycle planning list with the appropriate calls belonging to the selected day and week.
	 * 
	 * @author Elias
	 *
	 */
	private class PopulateList extends AsyncTask < Void , Void , Void > {
		
		/*
		 * Runs on the UI thread before doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute () {
			// Display indeterminate progress dialog
			AppDialog.getInstance ().displayIndeterminateProgress ( CyclePlanningActivity.this , null , null );
		}
		
		/*
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Void doInBackground ( Void ... params ) {
			// Populate the list asynchronously
			populateList ();
			return null;
		}
		
		/*
		 * Runs on the UI thread after doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute ( Void arg ) {
			// Check if the calls list is valid
			if ( calls == null )
				// Do nothing
				return;
			// Check if the list adapter is valid
			if ( dslv.getAdapter () == null )
				// Set a new adapter
				dslv.setAdapter ( new CyclePlanningAdapter ( CyclePlanningActivity.this , R.layout.cycle_planning_activity_item , selectedCalls , isEdit ) );
			else {
				// Set edit mode
				( (CyclePlanningAdapter) ( (HeaderViewListAdapter) dslv.getAdapter () ).getWrappedAdapter () ).setEdit ( isEdit );
				// Refresh list
				( (CyclePlanningAdapter) ( (HeaderViewListAdapter) dslv.getAdapter () ).getWrappedAdapter () ).notifyDataSetChanged ();
			} // End if
			// Dismiss dialog
			AppDialog.getInstance ().dismiss ();
		}
		
	}
	
	/**
	 * Enumeration used to define the various drop results that can occur.
	 * 
	 * @author Elias
	 *
	 */
	private static enum DropResultType {
		SUCCESS ,
		CLIENTS_REMOVED ,
		ALL_CLIENTS_REMOVED ,
		FAILURE ,
		ZERO_CYCLE ,
		MANY_CYCLES ,
		DAILY_CALLS_MAX_EXCEEDED ,
		DAILY_CALLS_MAX_REACHED
	}
	
	/**
	 * Class used to represent a client drag and drop result.<br>
	 * The result should have a type <em>(required)</em> and can hold some data <em>(optional)</em>.
	 * 
	 * @author Elias
	 *
	 */
	private class DropResult {
		
		/**
		 * An enumeration constant of {@link CyclePlanningActivity.DropResultType} indicating the drop result type.
		 */
		public DropResultType type;
		/**
		 * Reference to related data of the drop result.
		 */
		public Object data;
		
		/**
		 * Constructor
		 * 
		 * @param type	An enumeration constant of {@link CyclePlanningActivity.DropResultType} indicating the drop result type.
		 */
		public DropResult ( DropResultType type ) {
			// Initialize the drop result type
			this.type = type;
		}
		
	}
	
	/**
	 * AsyncTask helper class used to append to the cycle planning list all the dropped clients for the selected day and week, along all the necessary actions (i.e. update map ...).
	 * 
	 * @author Elias
	 *
	 */
	private class DropClients extends AsyncTask < Integer , Void , DropResult > {
		
		/*
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected DropResult doInBackground ( Integer ... params ) {
			// Declare and initialize a drop result (failure by default)
			DropResult dropResult = new DropResult ( DropResultType.FAILURE );
			// Determine if the list of dragged clients is valid
			if ( draggedClients == null || draggedClients.isEmpty () )
				// Do nothing
				return dropResult;
			// Otherwise there is at least one dragged client
			// Retrieve the list of dragged clients (group by 500 due to SQLite limitations)
			List < Clients > clients = EntityUtils.queryByGroup ( draggedClients ,
					DatabaseUtils.getInstance ( CyclePlanningActivity.this ).getDaoSession ().getClientsDao () ,
					ClientsDao.Properties.ClientCode );
			// Check if the retrieved list is empty
			if ( clients.isEmpty () )
				// Do nothing
				return dropResult;
			// Otherwise, the drop result is not a failure
			dropResult.type = DropResultType.SUCCESS;
			// Retrieve the client codes of the current (displayed) list of clients
			List < String > currentClientCodes = new ArrayList < String > ();
			for ( Call call : selectedCalls )
				currentClientCodes.add ( call.getClient () == null ? "" : call.getClient ().getClientCode () );
			// Determine if a dragged client is already in the current list (by comparing the client codes)
			// Iterate over the dragged clients
			for ( int i = 0 ; i < clients.size () ; i ++ )
				// Compare the client codes
				if ( currentClientCodes.contains ( clients.get ( i ).getClientCode () ) ) {
					// Remove the dragged client
					clients.remove ( i -- );
					// Indicate that a client has been removed
					dropResult.type = DropResultType.CLIENTS_REMOVED;
				} // End if
			// Check if all clients has been removed
			if ( clients.isEmpty () ) {
				// All clients has been removed
				dropResult.type = DropResultType.ALL_CLIENTS_REMOVED;
				return dropResult;
			} // End if
			
			// Map the clients to their client codes
			Map < String , Clients > _clients = new HashMap < String , Clients > ();
			// Iterate over all clients
			for ( Clients client : clients )
				// Map the current client to its client code
				_clients.put ( client.getClientCode () , client );
			
			// Declare and initialize an integer used to host the cycle id
			Integer cycleId = null;
			// Determine if the cycle id is provided
			if ( params.length == 1 && params [ 0 ] instanceof Integer ) {
				// Retrieve the appropriate user cycle from DB
				UserCycles userCycle = DatabaseUtils.getInstance ( CyclePlanningActivity.this ).getDaoSession ().getUserCyclesDao ().queryBuilder ()
					.where ( UserCyclesDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode ( CyclePlanningActivity.this ) ) ,
							UserCyclesDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( CyclePlanningActivity.this ) ) ,
							UserCyclesDao.Properties.CycleID.eq ( params [ 0 ] ) ).unique ();
				// Determine if the cycle is valid
				if ( userCycle != null )
					// Use the provided cycle
					cycleId = userCycle.getCycleID ();
			} // End if
			
			// Check if the cycle id is valid
			if ( cycleId == null ) {
				// Retrieve all the user cycles
				List < UserCycles > userCycles = DatabaseUtils.getInstance ( CyclePlanningActivity.this ).getDaoSession ().getUserCyclesDao ().queryBuilder ()
						.where ( UserCyclesDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode ( CyclePlanningActivity.this ) ) ,
								UserCyclesDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( CyclePlanningActivity.this ) ) ).list ();
				// Retrieve the cycle IDs of the user cycles above
				List < Integer > userCycleIDs = new ArrayList < Integer > ();
				for ( UserCycles userCycle : userCycles )
					userCycleIDs.add ( userCycle.getCycleID () );
				// Retrieve the list of cycles assigned to the user that are editable
				final List < me.SyncWise.Android.Database.Cycles > cycles = DatabaseUtils.getInstance ( CyclePlanningActivity.this ).getDaoSession ().getCyclesDao ().queryBuilder ()
						.where ( CyclesDao.Properties.CycleID.in ( userCycleIDs ) ,
								CyclesDao.Properties.CycleStatus.eq ( StatusUtils.isEditable () ) ).list ();
				// Check if there is at least one cycle
				if ( cycles.isEmpty () ) {
					// No valid cycles, cannot add clients
					dropResult.type = DropResultType.ZERO_CYCLE;
					return dropResult;
				} // End if
				// Check if there is exactly one cycle
				else if ( cycles.size () == 1 )
					// Use the available cycle
					cycleId = cycles.get ( 0 ).getCycleID ();
				// Otherwise there are many cycles
				else {
					// There are many cycles, let the user choose one
					dropResult.type = DropResultType.MANY_CYCLES;
					dropResult.data = cycles;
					return dropResult;
				} // End else
			} // End if
			
			// Compute the total number of calls of the selected cycle during the selected day, week and cycle
			int size = clients.size ();
			// Iterate over all the calls
			for ( Call call : calls )
				// Check if the current call is valid
				if ( call.getCycleCall ().getCycleCallStatus () == StatusUtils.isAvailable ()
						&& call.getCycleCall ().getDay ().equals ( selectedDay )
						&& call.getCycleCall ().getWeek () == selectedWeek
						&& call.getCycleCall ().getCycleID () == cycleId )
					// Increment the total number of calls
					size ++;
			// Determine if the maximum number of daily calls per cycle is valid (greater than 0)
			// AND if adding the dragged clients exceeds the total number of allowed daily calls per cycle
			if ( maxDailyCallsPerCycle > 0 && size > maxDailyCallsPerCycle ) {
				// Determine the number of clients that should be ignored
				int ignored = size - maxDailyCallsPerCycle;
				// Check if all dragged clients should be ignored
				if ( ignored == clients.size () ) {
					// The maximum number of allowed daily calls is already exceeded
					// All dragged clients are ignored
					dropResult.type = DropResultType.DAILY_CALLS_MAX_EXCEEDED;
					return dropResult;
				} // End if
				else {
					// The maximum of allowed daily calls has been reached
					dropResult.type = DropResultType.DAILY_CALLS_MAX_REACHED;
					// The last dragged clients should be removed
					// Iterate over all the dragged client codes (from last to first)
					for ( int i = draggedClients.size () - 1 ; i >= 0 ; i -- ) {
						// Check if more clients should be removed
						if ( ignored == 0 )
							// Exit loop
							break;
						// Check if the currently dragged clients is valid
						if ( _clients.containsKey ( draggedClients.get ( i ) ) ) {
							// Remove the current client
							clients.remove ( _clients.get ( draggedClients.get ( i ) ) );
							_clients.remove ( draggedClients.get ( i ) );
							// Decrement the number of clients that should be ignored
							ignored --;
						} // End if
					} // End for loop
				}
			} // End if
			
			// Determine the maximum cycle sequence in the current cycle, week and day
			int maxSequence = -1;
			// Iterate over all the selected calls
			for ( Call call : selectedCalls )
				// Check if the current call belongs to the required cycle
				// And if the current call sequence is greater than the maximum
				if ( call.getCycleCall ().getCycleID () == cycleId && maxSequence < call.getCycleCall ().getSequence () )
					// Set the new maximum value
					maxSequence = call.getCycleCall ().getSequence ();
			// At this point, for sure that there are no duplicated clients (that are not deleted)
			// Hence, look for deleted calls (obviously that are not saved in DB yet)
			// Iterate over all the calls
			for ( Call call : calls )
				// Check if the current call is identical (same cycle, week, day and client code) and deleted
				if ( call.getCycleCall ().getCycleID () == cycleId
						&& call.getCycleCall ().getDay ().equals ( selectedDay )
						&& call.getCycleCall ().getWeek () == selectedWeek
						&& _clients.containsKey ( call.getClientCode () )
						&& call.getCycleCall ().getCycleCallStatus () == StatusUtils.isDeleted () ) {
					// Make a backup before modifying
					call.backup ();
					// Flag as modified
					call.setModified ( true );
					// Modify the call status
					call.getCycleCall ().setCycleCallStatus ( StatusUtils.isAvailable () );
					// Modify the call sequence
					call.getCycleCall ().setSequence ( ++ maxSequence );
					// Remove the client object
					clients.remove ( _clients.get ( call.getClientCode () ) );
					_clients.remove ( call.getClientCode () );
				} // End if
			// Retrieve a reference to the cycle
			Cycles cycle = DatabaseUtils.getInstance ( CyclePlanningActivity.this ).getDaoSession ().getCyclesDao ().queryBuilder ()
				.where ( CyclesDao.Properties.CycleID.eq ( cycleId ) ).unique ();
			// Iterate over all remaining clients
			for ( Clients client : clients ) { // TODO
//				// Create a new call cycle object
//				CycleCalls callCycle = new CycleCalls ( null , cycleId , client.getClientCode () , selectedDay , selectedWeek , ++ maxSequence , null , StatusUtils.isAvailable () , IsProcessedUtils.isNotSync () , null );
//				// Create a new call object
//				Call call = new Call ( callCycle , client , true );
//				// Make the call editable
//				call.setEditable ( true );
//				// Set the cycle name
//				call.setCycleName ( cycle.getCycleName () );
//				// Retrieve the client areas
//				List < ClientAreas > clientAreas = DatabaseUtils.getInstance ( CyclePlanningActivity.this ).getDaoSession ().getClientAreasDao ().queryBuilder ()
//						.where ( ClientAreasDao.Properties.ClientCode.eq ( client.getClientCode () ) ).list ();
//				// Check if the client has areas
//				if ( ! clientAreas.isEmpty () ) {
//					// Retrieve the area codes
//					List < String > areaCodes = new ArrayList < String > ();
//					// Iterate over all client areas
//					for ( ClientAreas clientArea : clientAreas )
//						// Add the current area code
//						areaCodes.add ( clientArea.getAreaCode () );
//					// Retrieve the appropriate areas
//					List < Areas > areas = EntityUtils.queryByGroup ( areaCodes , 
//							DatabaseUtils.getInstance ( CyclePlanningActivity.this ).getDaoSession ().getAreasDao () ,
//							AreasDao.Properties.AreaCode );
//					// Compute the area descriptions mapped to their levels
//					HashMap < String , String > _areas = new HashMap < String , String > ();
//					// Iterate over all areas
//					for ( Areas area : areas ) {
//						// Check if the current area level is previously mapped
//						if ( _areas.containsKey ( area.getAreaLevelCode () ) )
//							// Add the current area description
//							_areas.put ( area.getAreaLevelCode () , _areas.get ( area.getAreaLevelCode () ) + " - " + area.getAreaDescription () );
//						else
//							// Otherwise map the current area description
//							_areas.put ( area.getAreaLevelCode () , area.getAreaDescription () );
//					} // End for each
//					// Retrieve the area levels
//					List < AreaLevels > areaLevels = EntityUtils.queryByGroup ( new ArrayList < String > ( _areas.keySet () ) ,
//							DatabaseUtils.getInstance ( CyclePlanningActivity.this ).getDaoSession ().getAreaLevelsDao () ,
//							AreaLevelsDao.Properties.AreaLevelCode );
//					// Iterate over all area levels
//					for ( AreaLevels areaLevel : areaLevels )
//						// Add the related area(s) to the call
//						call.addArea ( areaLevel.getAreaLevelDescription () , _areas.get ( areaLevel.getAreaLevelCode () ) );
//				} // End if
//				// Add the call to the list of calls
//				calls.add ( call );
			} // End for each
			// Populate the list asynchronously
			populateList ();
			// Return the drop result
			return dropResult;
		}
		
		/*
		 * Runs on the UI thread after doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute ( DropResult result ) {
			// If FAILURE, do nothing, else :
			// Check the result
			if ( result.type == DropResultType.SUCCESS || result.type == DropResultType.CLIENTS_REMOVED || result.type == DropResultType.DAILY_CALLS_MAX_REACHED )
				// Refresh list
				( (CyclePlanningAdapter) ( (HeaderViewListAdapter) dslv.getAdapter () ).getWrappedAdapter () ).notifyDataSetChanged ();
			// Determine if the dragged clients do not need another user intervention
			if ( result.type != DropResultType.MANY_CYCLES )
				// Reset the dragged clients list
				draggedClients = null;
			// Determine the drop result
			switch ( result.type ) {
			case CLIENTS_REMOVED:
				// Indicate that some clients were removed
				Baguette.showText ( CyclePlanningActivity.this ,
						AppResources.getInstance ( CyclePlanningActivity.this ).getString ( CyclePlanningActivity.this , R.string.some_clients_removed_message ) ,
						Baguette.BackgroundColor.BLUE );
				break;
			case ALL_CLIENTS_REMOVED:
				// Indicate that some clients were removed
				Baguette.showText ( CyclePlanningActivity.this ,
						AppResources.getInstance ( CyclePlanningActivity.this ).getString ( CyclePlanningActivity.this , R.string.all_clients_removed_message ) ,
						Baguette.BackgroundColor.BLUE );
				break;
			case ZERO_CYCLE:
				// Indicate that some clients were removed
				Baguette.showText ( CyclePlanningActivity.this ,
						AppResources.getInstance ( CyclePlanningActivity.this ).getString ( CyclePlanningActivity.this , R.string.no_cycle_error_message ) ,
						Baguette.BackgroundColor.RED );
				break;
			case MANY_CYCLES:
				// Retrieve the list of cycles from the result
				@SuppressWarnings("unchecked")
				final List < me.SyncWise.Android.Database.Cycles > cycles = (List < me.SyncWise.Android.Database.Cycles >) result.data;
				// Retrieve the cycle names in an array
				String cycleNames [] = new String [ cycles.size () ];
				for ( int i = 0 ; i < cycleNames.length ; i ++ )
					cycleNames [ i ] = cycles.get ( i ).getCycleName ();
				// Prompt the user to choose a cycle
				AppDialog.getInstance ().displayList ( CyclePlanningActivity.this ,
						AppResources.getInstance ( CyclePlanningActivity.this ).getString ( CyclePlanningActivity.this , R.string.cycle_list_dialog_title ) ,
						cycleNames ,
						AppDialog.Cancelable.BACK_BUTTON_AND_TOUCH ,
						new DialogInterface.OnClickListener () {
							@Override
							public void onClick ( DialogInterface dialog , int which ) {
								// Use the selected cycle for the dropped clients
								new DropClients ().execute ( cycles.get ( which ).getCycleID () );
							}
						} ,
						new DialogInterface.OnCancelListener () {
							@Override
							public void onCancel ( DialogInterface dialog ) {
								// The user has chosen not to choose a cycle for the dragged clients
								// Meaning he/she cannot add the dragged clients
								// Clear the dragged clients
								draggedClients.clear ();
								draggedClients = null;
							}
						});
				break;
			case DAILY_CALLS_MAX_REACHED:
				// Indicate that some clients were removed because the limit was reached
				Baguette.showText ( CyclePlanningActivity.this ,
						AppResources.getInstance ( CyclePlanningActivity.this ).getString ( CyclePlanningActivity.this , R.string.daily_calls_max_reached_message ) ,
						Baguette.BackgroundColor.RED );
				break;
			case DAILY_CALLS_MAX_EXCEEDED:
				// Indicate that all clients were removed because the limit is exceeded
				Baguette.showText ( CyclePlanningActivity.this ,
						AppResources.getInstance ( CyclePlanningActivity.this ).getString ( CyclePlanningActivity.this , R.string.daily_calls_max_exceeded_message ) ,
						Baguette.BackgroundColor.RED );
				break;
			} // End switch
		}
		
	}
	
	/**
	 * Populates the list of client for the selected day and week.<br>
	 * You still need to notify that the data set has been changed on the adapter side, or simply assign a new one using the list of clients after this methods executes.
	 */
	private void populateList () {
		// Check if the calls list is valid
		if ( calls == null )
			// Do nothing
			return;
		// Check if the selected calls list is valid
		if ( selectedCalls == null )
			// Initialize the selected calls list
			selectedCalls = new ArrayList < Call > ();
		else
			// Clear the selected calls list
			selectedCalls.clear ();
		
		// Iterate over all the calls
		for ( Call call : calls ) {
			// Check if the current call is valid
			if ( call.getCycleCall ().getCycleCallStatus () == StatusUtils.isAvailable ()
					&& call.getCycleCall ().getDay ().equals ( selectedDay )
					&& call.getCycleCall ().getWeek () == selectedWeek )
				// Add the current call to the list
				selectedCalls.add ( call );
		} // End for each
		
		// Sort the calls list based on the cycle sequence
		Collections.sort ( selectedCalls , new Comparator < Call > () {
			@Override
			public int compare ( Call call1 , Call call2 ) {
				// Compare the calls based on the cycle call sequence
				return call1.getCycleCall ().getSequence () - call2.getCycleCall ().getSequence ();
			}
		} );
	}
	
	/**
	 * AsyncTask helper class used to save all modifications done in the cycle planning asynchronously.
	 * 
	 * @author Elias
	 *
	 */
	private class Save extends AsyncTask < Void , Void , Boolean > {
		
		/*
		 * Runs on the UI thread before doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute () {
			// Display indeterminate progress dialog
			AppDialog.getInstance ().displayIndeterminateProgress ( CyclePlanningActivity.this , null , null );
		}
		
		/*
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Boolean doInBackground ( Void ... params ) {
			// Flag used to indicate whether an error occurred or not
			boolean error = false;
			// Declare and initialize a list used to hold the new / modified calls
			List < Call > modifications = new ArrayList < Call > ();
			// Iterate over all calls
			for ( Call call : calls )
				// Determine if the call is new or modified
				if ( call.isAdded () || call.isModified () )
					// Add the call cycle to the list
					modifications.add ( call );
			
			// Generate a stamp date
			Date stampDate = Calendar.getInstance ().getTime ();
			// Create a list to store the old stamp dates in case of a possible roll back operation
			List < Date > oldStampDates = new ArrayList < Date > ();
			// Create a list to store the old is processed fields in case of a possible roll back operation
			List < Integer > oldIsProcessed = new ArrayList < Integer > ();
			
			try {
				// Begin transaction
				DatabaseUtils.getInstance ( CyclePlanningActivity.this ).getDaoSession ().getDatabase ().beginTransaction ();

				// Iterate over all modifications
				for ( Call call :modifications ) {
					// Store the old stamp date
					oldStampDates.add ( call.getCycleCall ().getStampDate () );
					// Update the stamp date
					call.getCycleCall ().setStampDate ( (Date) stampDate.clone () );
					// Store the old is processed field
					oldIsProcessed.add ( call.getCycleCall ().getIsProcessed () );
					// Update the is processed field
					call.getCycleCall ().setIsProcessed ( IsProcessedUtils.isNotSync () );
					// Determine if the current cycle call is added or modified
					if ( call.isAdded () )
						// Insert the current cycle call
						DatabaseUtils.getInstance ( CyclePlanningActivity.this ).getDaoSession ().getCycleCallsDao ().insert ( call.getCycleCall () );
					else if ( call.isModified () )
						// Update the current cycle call
						DatabaseUtils.getInstance ( CyclePlanningActivity.this ).getDaoSession ().getCycleCallsDao ().update ( call.getCycleCall () );
				} // End for each
				
				// Commit changes
				DatabaseUtils.getInstance ( CyclePlanningActivity.this ).getDaoSession ().getDatabase ().setTransactionSuccessful ();
				
				// Now that all the added / modified calls are saved, clear their backups and flags
				for ( Call call : modifications ) {
					// Clear backup
					call.clearBackup ();
					// Clear flags
					call.setAdded ( false );
					call.setModified ( false );
				} // End for each
				
			} catch ( Exception exception ) {
				// Indicate that an error occurred
				error = true;
				// An error occurred
				// Restore all old stamp dates
				for ( int i = 0 ; i < oldStampDates.size () ; i ++ )
					// Restore old stamp date
					modifications.get ( i ).getCycleCall ().setStampDate ( oldStampDates.get ( i ) );
				// Restore all old is processed fields
				for ( int i = 0 ; i < oldIsProcessed.size () ; i ++ )
					// Restore old stamp date
					modifications.get ( i ).getCycleCall ().setIsProcessed ( oldIsProcessed.get ( i ) );
			} finally {
				// End transaction
				DatabaseUtils.getInstance ( CyclePlanningActivity.this ).getDaoSession ().getDatabase ().endTransaction ();
			} // End try-catch-finally block
			
			// Return the error state
			return error;
		}
		
		/*
		 * Runs on the UI thread after doInBooleanBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute ( Boolean error ) {
			// Dismiss dialog
			AppDialog.getInstance ().dismiss ();
			// Check if the edit mode is enabled
			if ( isEdit ) {
				// Disable edit mode
				isEdit = false;
				( (CyclePlanningAdapter) ( (HeaderViewListAdapter) dslv.getAdapter () ).getWrappedAdapter () ).setEdit ( isEdit );
				// Refresh list
				( (CyclePlanningAdapter) ( (HeaderViewListAdapter) dslv.getAdapter () ).getWrappedAdapter () ).notifyDataSetChanged ();
			} // End if
			// Determine if an error occurred
			if ( error )
				// Indicate that an error occurred
				Baguette.showText ( CyclePlanningActivity.this ,
						AppResources.getInstance ( CyclePlanningActivity.this ).getString ( CyclePlanningActivity.this , R.string.save_error_message ) ,
						Baguette.BackgroundColor.RED );
			else {
				// Indicate that no error occurred
				Baguette.showText ( CyclePlanningActivity.this ,
						AppResources.getInstance ( CyclePlanningActivity.this ).getString ( CyclePlanningActivity.this , R.string.save_success_message ) ,
						Baguette.BackgroundColor.GREEN );
				Vibration.vibrate ( CyclePlanningActivity.this );
			} // End else
		}
		
	}
	
	/*
	 * Called when the activity is starting.
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@SuppressLint("ClickableViewAccessibility") 
	@Override
	public void onCreate ( Bundle savedInstanceState ) {
		// Superclass method call
		super.onCreate ( savedInstanceState );
		// Set the activity content from a layout resource.
		setContentView ( R.layout.cycle_planning_activity );
		// Change the title associated with this activity
		setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.cycle_planning_activity_title ) );
		
		// By default, both weeks and days tabs are expanded (unless specified via the savedInstanceState bundle)
		isExpanded = true;
		
		// Set the default number of maximum daily calls per cycle
		maxDailyCallsPerCycle = Cycle.MAX_DAILY_CALLS_PER_CYCLE;
		// Retrieve the system date
		Calendar today = Calendar.getInstance ();
		// Retrieve the week number per cycle
		int weekNumberPerCycle = me.SyncWise.Android.Utilities.Cycle.getWeekNumberPerCycle ( this , DatabaseUtils.getCurrentUserCode ( this ) , DatabaseUtils.getCurrentCompanyCode ( this ) );
		// Retrieve the current week number
		currentWeek = me.SyncWise.Android.Utilities.Cycle.getWeekNumber ( weekNumberPerCycle );
		// Retrieve the current day number
		int dayNumber = today.get ( Calendar.DAY_OF_WEEK );
		// Set the action bar's subtitle
		getActionBar ().setSubtitle ( AppResources.getInstance ( this ).getString ( this , R.string.cycle_label ) + " " + me.SyncWise.Android.Utilities.Cycle.getCycleNumber ( weekNumberPerCycle ) );
		
		// Display empty list label
		( (TextView) findViewById ( R.id.empty_list_view ) ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.empty_calls_list_label ) );
		// Retrieve a reference to the DSLV
		dslv = (DragSortListView) findViewById ( R.id.dslv_cycle_plan );
		// Define the empty list view
		dslv.setEmptyView ( findViewById ( R.id.empty_list_view ) );
		// Register a drag event listener callback object for the dslv layout
		findViewById ( R.id.layout_dslv ).setOnDragListener ( this );
		// Register a re-order and remove event listener callback object for the dslv
		dslv.setDragSortListener ( this );
		
		// Declare and initialize a DSLV controller used to start and stop item drags
		CycleCallsDSLVController controller = new CycleCallsDSLVController ( dslv );
		// Assign the controller as the DSLV float view manager and the touch listener
		dslv.setFloatViewManager ( controller );
		dslv.setOnTouchListener ( controller );
		
		// Retrieve a reference to the group tabs
		SegmentedRadioGroup weeksTabs = (SegmentedRadioGroup) findViewById ( R.id.group_tabs );
		// Populate the weeks tabs
		for ( int i = 0 ; i < weekNumberPerCycle ; i ++ ) {
			// Build a segmented radio button
			RadioButton radioButton = SegmentedRadioGroup.buildChild ( this );
			// Set the week label
			radioButton.setText ( AppResources.getInstance ( this ).getString ( this , R.string.week_label ) + " " + ( i + 1 ) );
			// Determine if this week is the current week
			if ( i == currentWeek - 1 )
				// Change the week label color
				radioButton.setTextColor ( getResources ().getColorStateList ( R.color.current_date_radio_color ) );
			// Add the button to the group
			weeksTabs.addView ( radioButton );
		} // End for loop
		// Notify the group about the new views to properly draw them
		weeksTabs.notifyChildViewsChanged ();
		// Determine if there is exactly one week
		if ( weekNumberPerCycle == 1 )
			// No need to display a weeks tab, hide it
			findViewById ( R.id.layout_group_tabs ).setVisibility ( View.GONE );
		
		// Assign a click listener to the left and right arrows of the weeks tabs in order to scroll the horizontal scroll view to the left
		( (ImageView) findViewById ( R.id.arrow_left ) ).setOnClickListener ( this );
		( (ImageView) findViewById ( R.id.arrow_right ) ).setOnClickListener ( this );
		
		// Retrieve a reference to the days tabs
		SegmentedRadioGroup daysTabs = (SegmentedRadioGroup) findViewById ( R.id.group_days );
		// Populate the days tabs
		for ( int i = 0 ; i < 7 ; i ++ ) {
			// Build a segmented radio button
			RadioButton radioButton = SegmentedRadioGroup.buildChild ( this );
			// Set the day label
			// And change the text color for the current day label
			switch ( i ) {
			case 0:
				radioButton.setText ( Text.getVertical ( AppResources.getInstance ( this ).getString ( this , R.string.monday_abbreviation ) ) );
				radioButton.setTag ( getString ( R.string.monday_abbreviation ) );
				if ( dayNumber == Calendar.MONDAY )
					radioButton.setTextColor ( getResources ().getColorStateList ( R.color.current_date_radio_color ) );
				break;
			case 1:
				radioButton.setText ( Text.getVertical ( AppResources.getInstance ( this ).getString ( this , R.string.tuesday_abbreviation ) ) );
				radioButton.setTag ( getString ( R.string.tuesday_abbreviation ) );
				if ( dayNumber == Calendar.TUESDAY )
					radioButton.setTextColor ( getResources ().getColorStateList ( R.color.current_date_radio_color ) );
				break;
			case 2:
				radioButton.setText ( Text.getVertical ( AppResources.getInstance ( this ).getString ( this , R.string.wednesday_abbreviation ) ) );
				radioButton.setTag ( getString ( R.string.wednesday_abbreviation ) );
				if ( dayNumber == Calendar.WEDNESDAY )
					radioButton.setTextColor ( getResources ().getColorStateList ( R.color.current_date_radio_color ) );
				break;
			case 3:
				radioButton.setText ( Text.getVertical ( AppResources.getInstance ( this ).getString ( this , R.string.thursday_abbreviation ) ) );
				radioButton.setTag ( getString ( R.string.thursday_abbreviation ) );
				if ( dayNumber == Calendar.THURSDAY )
					radioButton.setTextColor ( getResources ().getColorStateList ( R.color.current_date_radio_color ) );
				break;
			case 4:
				radioButton.setText ( Text.getVertical ( AppResources.getInstance ( this ).getString ( this , R.string.friday_abbreviation ) ) );
				radioButton.setTag ( getString ( R.string.friday_abbreviation ) );
				if ( dayNumber == Calendar.FRIDAY )
					radioButton.setTextColor ( getResources ().getColorStateList ( R.color.current_date_radio_color ) );
				break;
			case 5:
				radioButton.setText ( Text.getVertical ( AppResources.getInstance ( this ).getString ( this , R.string.saturday_abbreviation ) ) );
				radioButton.setTag ( getString ( R.string.saturday_abbreviation ) );
				if ( dayNumber == Calendar.SATURDAY )
					radioButton.setTextColor ( getResources ().getColorStateList ( R.color.current_date_radio_color ) );
				break;
			case 6:
				radioButton.setText ( Text.getVertical ( AppResources.getInstance ( this ).getString ( this , R.string.sunday_abbreviation ) ) );
				radioButton.setTag ( getString ( R.string.sunday_abbreviation ) );
				if ( dayNumber == Calendar.SUNDAY )
					radioButton.setTextColor ( getResources ().getColorStateList ( R.color.current_date_radio_color ) );
				break;
			} // End of switch
			// Add the button to the group
			daysTabs.addView ( radioButton );
		} // End for loop
		// Notify the group about the new views to properly draw them
		daysTabs.notifyChildViewsChanged ();
		
		// Determine if a saved instance state is provided
		if ( savedInstanceState != null ) {
			// Restore the content provided by the saved instance state 
			selectedDay = savedInstanceState.getString ( SELECTED_DAY );
			selectedWeek = savedInstanceState.getInt ( SELECTED_WEEK , selectedWeek );
			currentWeek = savedInstanceState.getInt ( CURRENT_WEEK , currentWeek );
			retrieveModifiedCalls = savedInstanceState.getBoolean ( CALLS );
			draggedClients = savedInstanceState.getStringArrayList ( DRAGGED_CLIENTS );
			isEdit = savedInstanceState.getBoolean ( IS_EDIT );
			maxDailyCallsPerCycle = savedInstanceState.getInt ( MAX_DAILY_CALLS_PER_CYCLE );
			isExpanded = savedInstanceState.getBoolean ( IS_EXPANDED );
			activityForResultStarted = savedInstanceState.getBoolean ( ACTIVITY_FOR_RESULT_STARTED );
		} // End if
		
		// Check if the selected day and week values are valid
		if ( selectedDay == null || selectedWeek == 0 ) {
			// Retrieve the current week number
			selectedWeek = currentWeek;
			// Compute the current day
			switch ( today.get ( Calendar.DAY_OF_WEEK ) ) {
			case Calendar.MONDAY:
				selectedDay = getString ( R.string.monday_abbreviation );
				break;
			case Calendar.TUESDAY:
				selectedDay = getString ( R.string.tuesday_abbreviation );
				break;
			case Calendar.WEDNESDAY:
				selectedDay = getString ( R.string.wednesday_abbreviation );
				break;
			case Calendar.THURSDAY:
				selectedDay = getString ( R.string.thursday_abbreviation );
				break;
			case Calendar.FRIDAY:
				selectedDay = getString ( R.string.friday_abbreviation );
				break;
			case Calendar.SATURDAY:
				selectedDay = getString ( R.string.saturday_abbreviation );
				break;
			case Calendar.SUNDAY:
				selectedDay = getString ( R.string.sunday_abbreviation );
				break;
			} // End switch
		} // End if
		
		// Check the appropriate day and week
		( (RadioButton) weeksTabs.getChildAt ( selectedWeek - 1 ) ).setChecked ( true );
		if ( selectedDay.equals ( "MO" ) )
			( (RadioButton) daysTabs.getChildAt ( 0 ) ).setChecked ( true );
		else if ( selectedDay.equals ( "TU" ) )
			( (RadioButton) daysTabs.getChildAt ( 1 ) ).setChecked ( true );
		else if ( selectedDay.equals ( "WE" ) )
			( (RadioButton) daysTabs.getChildAt ( 2 ) ).setChecked ( true );
		else if ( selectedDay.equals ( "TH" ) )
			( (RadioButton) daysTabs.getChildAt ( 3 ) ).setChecked ( true );
		else if ( selectedDay.equals ( "FR" ) )
			( (RadioButton) daysTabs.getChildAt ( 4 ) ).setChecked ( true );
		else if ( selectedDay.equals ( "SA" ) )
			( (RadioButton) daysTabs.getChildAt ( 5 ) ).setChecked ( true );
		else if ( selectedDay.equals ( "SU" ) )
			( (RadioButton) daysTabs.getChildAt ( 6 ) ).setChecked ( true );
		
		// Display the starting and ending dates of the selected week of the cycle
		displayCurrentWeekDates ();
		
		// Check if the tabs are expanded
		if ( ! isExpanded ) {
			// Hide the tabs
			findViewById ( R.id.layout_header ).setVisibility ( View.GONE );
			findViewById ( R.id.scroll_view ).setVisibility ( View.GONE );
		} // End if
		
		// Handler used to execute a task (if any, see below) after a delay
		// The delay allows the screen to successfully fully draw all its components
		// This method is used because on some devices the segmented group children might have the same width
		Handler handler = null;

		// Retrieve all the available calls asynchronously (after a delay of 150 milliseconds)
		retrieveCalls = new RetrieveCalls ();
		handler = new Handler ();
		handler.postDelayed ( new Runnable() {
			@Override
			public void run () {
				retrieveCalls.execute ();
			}
		} , 150 );
		
		// Assign a checked change listener to the weeks and days tabs in order to display the appropriate list of calls
		weeksTabs.setOnCheckedChangeListener ( this );
		daysTabs.setOnCheckedChangeListener ( this );
		
	}
	
	/**
	 * Displays the starting and ending dates of the selected week of the cycle.
	 */
	private void displayCurrentWeekDates () {
		// Retrieve current date
		Calendar calendar = Calendar.getInstance ();
		// Add / deduce the required number of weeks, based on the selected week of the cycle
		calendar.add ( Calendar.WEEK_OF_MONTH , selectedWeek - currentWeek );
		// Display the start of the current week
		switch ( calendar.get ( Calendar.DAY_OF_WEEK ) ) {
		case Calendar.MONDAY:
			// Do nothing
			break;
		case Calendar.TUESDAY:
			// Go back one day (reach Monday)
			calendar.add ( Calendar.DATE , -1 );
			break;
		case Calendar.WEDNESDAY:
			// Go back 2 days (reach Monday)
			calendar.add ( Calendar.DATE , -2 );
			break;
		case Calendar.THURSDAY:
			// Go back 3 days (reach Monday)
			calendar.add ( Calendar.DATE , -3 );
			break;
		case Calendar.FRIDAY:
			// Go back 4 days (reach Monday)
			calendar.add ( Calendar.DATE , -4 );
			break;
		case Calendar.SATURDAY:
			// Go back 5 days (reach Monday)
			calendar.add ( Calendar.DATE , -5 );
			break;
		case Calendar.SUNDAY:
			// Go back 6 days (reach Monday)
			calendar.add ( Calendar.DATE , -6 );
			break;
		} // End of switch
		// Display the start of the current week
		( (TextView) findViewById ( R.id.label_date_from ) ).setText ( DateTime.getMonth ( this , calendar ) + " " + calendar.get ( Calendar.DAY_OF_MONTH ) + ", " + calendar.get ( Calendar.YEAR ) );
		// Add 6 days (reach Sunday)
		calendar.add ( Calendar.DATE , 6 );
		// Display the end of the current week
		( (TextView) findViewById ( R.id.label_date_to ) ).setText ( DateTime.getMonth ( this , calendar ) + " " + calendar.get ( Calendar.DAY_OF_MONTH ) + ", " + calendar.get ( Calendar.YEAR ) );
	}
	
	/*
	 * Called when an activity you launched exits.
	 *
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult ( int requestCode , int resultCode , Intent data ) {
		// Check if the activity result is valid
		if ( data == null || resultCode != RESULT_OK )
			// Invalid result, do nothing
			return;
		// Otherwise the activity result is valid
		// Extract the list of selected clients from the intent
		draggedClients = data.getStringArrayListExtra ( DRAGGED_CLIENTS );
		// Determine if the dragged clients should be directly added
		if ( data.getBooleanExtra ( ADD_SELECTED_CLIENTS , false ) )
			// Handle the selected clients asynchronously
			new DropClients ().execute ();
	}
	
	/*
	 * Called when a view has been clicked.
	 *
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick ( View view ) {
		// Determine if the view is either the left or right arrow
		if ( view.getId () == R.id.arrow_left || view.getId () == R.id.arrow_right ) {
			// scroll the horizontal scroll view to the left/right, depending on the current arrow direction
			( (HorizontalScrollView) findViewById ( R.id.horizontal_scroll_view ) )
				.arrowScroll ( view.getId () == R.id.arrow_left ? View.FOCUS_LEFT : View.FOCUS_RIGHT );
		} // End if
	}

	/*
	 * Called when the checked radio button has changed.
	 *
	 * @see android.widget.RadioGroup.OnCheckedChangeListener#onCheckedChanged(android.widget.RadioGroup, int)
	 */
	@Override
	public void onCheckedChanged ( RadioGroup group , int checkedId ) {
		// Determine the changed radio group
		if ( group.getId () == R.id.group_tabs ) {
			// New week selected, update it
			selectedWeek = ( (SegmentedRadioGroup) group ).getChildIndex ( checkedId ) + 1;
			// Display the starting and ending dates of the selected week of the cycle
			displayCurrentWeekDates ();
		} // End if
		else if ( group.getId () == R.id.group_days ) {
			// New day selected, update it
			selectedDay = group.findViewById ( checkedId ).getTag ().toString ();
		} // End if
		// Reset the edit mode
		isEdit = false;
		// Re populate the list
		new PopulateList ().execute ();
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
		// Determine if this activity has previously started an activity for a result
		if ( activityForResultStarted )
			// Clear flag
			activityForResultStarted = false;
		else
			// Check if there are any dropped clients
			new DropClients ().execute ();
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
		// Determine if the completion confirmation dialog is displayed
		if ( pushCycleDialog != null ) {
			// Remove the confirmation
			pushCycleDialog.dismiss ();
		} // End if
		// Remove any displayed baguette
		Baguette.remove ( this );
		// Dismiss any displayed dialogs (to avoid activity context leak)
		AppDialog.getInstance ().dismiss ();
	}
	
	/*
	 * Called when the activity has detected the user's press of the back key. 
	 *
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed () {
		// Determine if there are modifications
		if ( ! hasModifications () )
			// Superclass method call
			super.onBackPressed ();
		else
			// Otherwise, there is at least one modification
			// Display exit confirmation
			AppDialog.getInstance ().displayAlert ( this ,
					null ,
					AppResources.getInstance ( this ).getString ( this , R.string.discard_changes_confirmation_message ) ,
					AppDialog.ButtonsType.YesNo ,
					new DialogInterface.OnClickListener () {
						@Override
						public void onClick ( DialogInterface dialog , int which ) {
							// Determine the clicked button
							switch ( which ) {
							case DialogInterface.BUTTON_POSITIVE:
								// Finish activity
								finish ();
								break;
							case DialogInterface.BUTTON_NEGATIVE:
								// Dismiss dialog
								AppDialog.getInstance ().dismiss ();
								break;
							} // End switch
						}
					} );
	}
	
    /*
     * Called to retrieve per-instance state from an activity before being killed so that the state can be restored in onCreate(Bundle) or onRestoreInstanceState(Bundle).
     *
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    protected void onSaveInstanceState ( Bundle outState ) {
    	// Superclass method call
    	super.onSaveInstanceState ( outState );
    	// Save the content of selectedDay in the outState bundle
    	outState.putString ( SELECTED_DAY , selectedDay );
    	// Save the content of selectedWeek in the outState bundle
    	outState.putInt ( SELECTED_WEEK , selectedWeek );
    	// Save the content of currentWeek in the outState bundle
    	outState.putInt ( CURRENT_WEEK , currentWeek );    	
    	
    	// Track the modified calls
    	ArrayList < Call > modifiedCalls = new ArrayList < Call > ();
    	// Check if the cycle calls list is valid
    	if ( calls != null )
			// Iterate over all the cycle calls
			for ( Call call : calls ) {
				// Determine if the call is modified and has no backup (cannot determine the previous state)
				if ( call.isAdded () || ( call.isModified () && call.getBackup () == null ) )
					// Add the call to the list
					modifiedCalls.add ( call );
				// Determine if the call is modified and has a valid backup
				else if ( call.isModified () && call.getBackup () != null ) {
					// Determine if the call object and its backup are alike
					if ( EntityUtils.compare ( call.getCycleCall () , call.getBackup () ) ) {
						// The call object and its backup are alike, therefore there are no changes for this call
						// Clear the call backup
						call.clearBackup ();
						// Reset the modified flag
						call.setModified ( false );
					} // End if
					else
						// Otherwise there is at least one modification
						// Add the call to the list
						modifiedCalls.add ( call );
				} // End if
			} // End for each
		// Check if there is at least one modified call
		if ( ! modifiedCalls.isEmpty () ) {
			// Save the content of the modified calls using GSON
			ActivityInstance.saveDataGson ( this , CyclePlanningActivity.class.getName () , CALLS , modifiedCalls );
			// Indicate that there is saved calls data
			outState.putBoolean ( CALLS , true );
		} // End if
    	
    	// Save the content of draggedClients in the outState bundle
    	outState.putStringArrayList ( DRAGGED_CLIENTS , draggedClients );    	
    	// Save the content of isEdit in the outState bundle
    	outState.putBoolean ( IS_EDIT , isEdit );
    	// Save the content of maxDailyCallsPerCycle in the outState bundle
    	outState.putInt ( MAX_DAILY_CALLS_PER_CYCLE , maxDailyCallsPerCycle );    	
    	// Save the content of isExpanded in the outState bundle
    	outState.putBoolean ( IS_EXPANDED , isExpanded );
    	// Save the content of activityForResultStarted in the outState bundle
    	outState.putBoolean ( ACTIVITY_FOR_RESULT_STARTED , activityForResultStarted );
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
			View view = null;
			view = findViewById ( R.id.arrow_left );
			if ( view != null )
				view.setOnClickListener ( null );
			view = findViewById ( R.id.arrow_right );
			if ( view != null )
				view.setOnClickListener ( null );
			view = findViewById ( R.id.group_tabs );
			if ( view != null )
				( (SegmentedRadioGroup) view ).setOnCheckedChangeListener ( null );
			view = findViewById ( R.id.group_days );
			if ( view != null )
				( (SegmentedRadioGroup) view ).setOnCheckedChangeListener ( null );
			view = findViewById ( R.id.layout_dslv );
			if ( view != null )
				view.setOnDragListener ( null );
			view = null;
			if ( dslv != null ) {
				dslv.setDragSortListener ( null );
				dslv = null;
			} // End if
			calls = null;
			selectedDay = null;
			selectedCalls = null;
			draggedClients = null;
		} // End if
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
    	// Determine if modifications in the cycle are allowed
    	if ( isModificationsEnabled ) {
    		// Retrieve the week number per cycle
    		int weekNumberPerCycle = me.SyncWise.Android.Utilities.Cycle.getWeekNumberPerCycle ( this , DatabaseUtils.getCurrentUserCode ( this ) , DatabaseUtils.getCurrentCompanyCode ( this ) );
	    	// Enable the required menu items
	    	me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_add ) , R.string.add_label );
	    	me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_save ) , R.string.save_label );
	    	me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_edit ) , R.string.edit_label );
	    	if ( weekNumberPerCycle > 1 && PermissionsUtils.getAllowPushCycle ( this , DatabaseUtils.getCurrentUserCode ( this ) , DatabaseUtils.getCurrentCompanyCode ( this ) ) )
	    		me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_settings ) , R.string.settings_label );
	    	if ( isExpanded )
	    		me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_expand ) , R.string.expand_label );
	    	else
	    		me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_narrow ) , R.string.narrow_label );
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
    	// Determine if the action add is selected
    	if ( menuItem.getItemId () == R.id.action_add ) {
    		// Launch an activity for which you would like a result when it finished
    		activityForResultStarted = true;
    		startActivityForResult ( new Intent ( this , AddCallActivity.class ) , 0 );
    		// Consume event
    		return true;
    	} // End if
    	// Determine if the action edit is selected
    	else if ( menuItem.getItemId () == R.id.action_edit ) {
    		// Check if the list of selected calls is empty
    		if ( ! selectedCalls.isEmpty () ) {
	    		// Toggle the edit mode
	    		isEdit = ! isEdit;
	    		// Apply the edit mode on the drag sort list view side
	    		dslv.setDragEnabled ( isEdit );
	    		// Apply the edit mode on the adapter side
	    		( (CyclePlanningAdapter) ( (HeaderViewListAdapter) dslv.getAdapter () ).getWrappedAdapter () ).setEdit ( isEdit );
    		} // End if
    		// Consume event
    		return true;
    	} // End else if
    	// Determine if the action save is selected
    	else if ( menuItem.getItemId () == R.id.action_save ) {
    		// Determine if there are modifications
    		if ( ! hasModifications () )
				// Indicate that there are no new modifications
				Baguette.showText ( CyclePlanningActivity.this ,
						AppResources.getInstance ( CyclePlanningActivity.this ).getString ( CyclePlanningActivity.this , R.string.no_new_modifications_message ) ,
						Baguette.BackgroundColor.BLUE );
    		else
    			// Save the modifications
    			new Save ().execute ();
    		// Consume event
    		return true;
    	} // End else if
    	// Determine if the action expand is selected
    	else if ( menuItem.getItemId () == R.id.action_expand ) {
    		// Reset the flag
    		isExpanded = false;
    		// Refresh the menu items in the action bar
    		invalidateOptionsMenu ();
    		// Collapse the tabs
    		AppAnimation.collapse ( findViewById ( R.id.scroll_view ) , LayoutParams.WRAP_CONTENT , LayoutParams.WRAP_CONTENT , 500 , AppAnimation.Direction.HORIZONTAL , null );
			AppAnimation.collapse ( findViewById ( R.id.layout_header ) , LayoutParams.MATCH_PARENT , LayoutParams.WRAP_CONTENT , 500 , AppAnimation.Direction.VERTICAL , null );
    		// Consume event
    		return true;
    	} // End if
    	// Determine if the action narrow is selected
    	else if ( menuItem.getItemId () == R.id.action_narrow ) {
    		// Set the flag
    		isExpanded = true;
    		// Refresh the menu items in the action bar
    		invalidateOptionsMenu ();
    		// Collapse the tabs
    		AppAnimation.expand ( findViewById ( R.id.scroll_view ) , LayoutParams.WRAP_CONTENT , LayoutParams.WRAP_CONTENT , 500 , AppAnimation.Direction.HORIZONTAL );
			AppAnimation.expand ( findViewById ( R.id.layout_header ) , LayoutParams.MATCH_PARENT , LayoutParams.WRAP_CONTENT , 500 , AppAnimation.Direction.VERTICAL );
    		// Consume event
    		return true;
    	} // End if
    	// Determine if the action settings is selected
    	else if ( menuItem.getItemId () == R.id.action_settings ) {
    		// Display alert dialog
    		displayPushCycleDialog ();
    		// Consume event
    		return true;
    	} // End if
    	// Allow normal menu processing to proceed
    	return false;
    }
    
    /**
     * Displays a push cycle dialog used to push the cycle with the prompted number of cycle.  
     */
    @SuppressLint("InflateParams") private void displayPushCycleDialog () {
		// Retrieve the week number per cycle
		final int weekNumberPerCycle = me.SyncWise.Android.Utilities.Cycle.getWeekNumberPerCycle ( this , DatabaseUtils.getCurrentUserCode ( this ) , DatabaseUtils.getCurrentCompanyCode ( this ) );
		
		// Declare and initialize a dialog click listener
		DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener () {
			@Override
			public void onClick ( DialogInterface dialog , int which ) {
				// Determine the clicked button
				switch ( which ) {
				case DialogInterface.BUTTON_POSITIVE:
					// Retrieve the spinner selected item index
					int index = ( (Spinner) ( (Dialog) dialog ).findViewById ( R.id.spinner ) ).getSelectedItemPosition ();
					// Determine the number of weeks to push
					int weekNumber = index + 1;
					
					// Iterate over all the calls
					for ( int i = 0 ; i < calls.size () ; i ++ ) {
						// Check if the call is deleted
						if ( calls.get ( i ).getCycleCall ().getCycleCallStatus () != StatusUtils.isAvailable () )
							// Do nothing, skip the current iteration
							continue;
						// Check if the call is newly added
						if ( calls.get ( i ).isAdded () ) {
							// The call is added but not saved
							// Push the cycle week
							calls.get ( i ).getCycleCall ().setWeek ( ( calls.get ( i ).getCycleCall ().getWeek () - 1 + weekNumber ) % weekNumberPerCycle + 1 );
						} // End if
						else { // TODO
//							// Make a backup before modifying
//							calls.get ( i ).backup ();
//							// Flag as modified
//							calls.get ( i ).setModified ( true );
//							// Modify the call status
//							calls.get ( i ).getCycleCall ().setCycleCallStatus ( StatusUtils.isDeleted () );
//							
//							// Retrieve the previous cycle call object
//							CycleCalls _callCycle = calls.get ( i ).getCycleCall ();
//							// Create a new call cycle object
//							CycleCalls callCycle = new CycleCalls ( null , _callCycle.getCycleID () , _callCycle.getClientCode () , _callCycle.getDay () , _callCycle.getWeek () , _callCycle.getSequence () , null , StatusUtils.isAvailable () , IsProcessedUtils.isNotSync () , null );
//							// Create a new call object
//							Call call = new Call ( callCycle , calls.get ( i ).getClient () , true );
//							// Make the call editable
//							call.setEditable ( true );
//							// Set the cycle name
//							call.setCycleName ( calls.get ( i ).getCycleName () );
//							// Set the client areas
//							call.setAreas ( calls.get ( i ).getAreaLevels () , calls.get ( i ).getAreas () );
//							// Push the cycle week
//							call.getCycleCall ().setWeek ( ( call.getCycleCall ().getWeek () - 1 + weekNumber ) % weekNumberPerCycle + 1 );
//							// Add the call the calls list
//							calls.add ( 0 , call );
//							// Increment the loop counter
//							i ++;
						} // End if
					} // End for each
					
					// Display baguette message
					Baguette.showText ( CyclePlanningActivity.this ,
							AppResources.getInstance ( CyclePlanningActivity.this ).getString ( CyclePlanningActivity.this , 0 ) ,
							Baguette.BackgroundColor.GREEN );
					
					// Re populate the list
					new PopulateList ().execute ();
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					// Dismiss dialog
					pushCycleDialog.dismiss ();
					break;
				} // End switch
				// Clear dialog reference
				pushCycleDialog = null;
			}
		};
		
		// Declare and initialize an alert dialog builder
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder ( this );
		// Inflate the dialog view
		View view = getLayoutInflater ().inflate ( R.layout.cycle_planning_dialog_push_cycle , null );
		// Set the dialog view
		alertDialogBuilder.setView ( view );
		// By default, the alert dialog cannot be cancelled UNLESS it has no buttons
		alertDialogBuilder.setCancelable ( false );
		// Set the description
		alertDialogBuilder.setMessage ( AppResources.getInstance ( this ).getString ( this , R.string.select_week_number_to_push_label ) );
		// Map the positive and negative buttons
		alertDialogBuilder.setPositiveButton ( AppResources.getInstance ( this ).getString ( this , R.string.ok_label ) , onClickListener );
		alertDialogBuilder.setNegativeButton ( AppResources.getInstance ( this ).getString ( this , R.string.cancel_label ) , onClickListener );
		
		// Retrieve and compute the weeks label
		String weeksLabel = AppResources.getInstance ( this ).getString ( this , R.string.weeks_label ) + " : ";
		// Declare and initialize a list of string used to hold the week labels
		List < String > weekLabels = new ArrayList < String > ();
		// Iterate over the number of week number per cycle (minus 1)
		for ( int i = 1 ; i < weekNumberPerCycle ; i ++ )
			// Add the current week label
			weekLabels.add ( weeksLabel + String.valueOf ( i ) );
    	// Declare and initialize a new weeks adapter used to populate the weeks to push drop down list
		PushCycleAdapter weeksAdapter = new PushCycleAdapter ( this , android.R.layout.simple_spinner_item , weekLabels , weeksLabel.length () );
		// Sets the layout resource to create the drop down views
		weeksAdapter.setDropDownViewResource ( R.layout.data_management_activity_connection_item );
		// Set the spinner adapter
		( (Spinner) view.findViewById ( R.id.spinner ) ).setAdapter ( weeksAdapter );
		
		// Create and store the alert dialog
		pushCycleDialog = alertDialogBuilder.create ();
		// Display the alert dialog
		pushCycleDialog.show ();
    }
    
	/*
	 * Called when a drag event is dispatched to a view.
	 *
	 * @see android.view.View.OnDragListener#onDrag(android.view.View, android.view.DragEvent)
	 */
	@Override
	public boolean onDrag ( View view , DragEvent event ) {
		// Inspect the action value of this event
		switch ( event.getAction () ) {
		case DragEvent.ACTION_DRAG_STARTED:
			// Indicate that the drag event was handled successfully
			return true;
		case DragEvent.ACTION_DRAG_ENTERED:
			// Declare and initialize an alpha animation
			AlphaAnimation alphaEnter = new AlphaAnimation ( 1f , 0.25f );
			// Set the animation duration
			alphaEnter.setDuration ( HOVER_ANIMATION_DURATION );
			// Make the transformation performed by the animation persist when it is finished
			alphaEnter.setFillAfter ( true );
			// Start animation on the drag sort list view and its container
			findViewById ( R.id.layout_dslv ).startAnimation ( alphaEnter );
			// Indicate that the drag event was handled successfully
			return true;
		case DragEvent.ACTION_DROP:
			// Handle the dropped clients asynchronously
			new DropClients ().execute ();
			// Run the animation below
		case DragEvent.ACTION_DRAG_EXITED:
			// Declare and initialize an alpha animation
			AlphaAnimation alphaExit = new AlphaAnimation ( 0.25f , 1f );
			// Set the animation duration
			alphaExit.setDuration ( HOVER_ANIMATION_DURATION );
			// Make the transformation performed by the animation persist when it is finished
			alphaExit.setFillAfter ( true );
			// Start animation on the drag sort list view and its container
			findViewById ( R.id.layout_dslv ).startAnimation ( alphaExit );
			// Indicate that the drag event was handled successfully
			return true;
		case DragEvent.ACTION_DRAG_ENDED:
			// Check if the result was successful
			if ( ! event.getResult () ) {
				// Drop failed
				// Clear the dragged clients
				draggedClients.clear ();
				draggedClients = null;
			} // End if
			// Indicate that the drag event was handled successfully
			return true;
		} // End switch
		// Indicate that the drag event was not handled
		return false;
	}
	
	/*
	 * Register a drop event listener callback object for the dslv
	 *
	 * @see me.SyncWise.Android.Widgets.DSLV.DragSortListView.DropListener#drop(int, int)
	 */
	@Override
	public void drop ( int from , int to ) {
		// Determine if change occurred
		if ( from == to )
			// Do nothing
			return;
		// Otherwise a changed occurred
		// Retrieve the appropriate calls object
		Call callFrom = selectedCalls.get ( from );
		
    	// Display debug output if required
        if ( showDebugOutput ) {
			Call callTo = selectedCalls.get ( to );
			Log.d ( TAG , TAG + " DROP : FROM [" + from + "] : " + callFrom.getClientCode () );
			Log.d ( TAG , TAG + " DROP : TO [" + to + "] : " + callTo.getClientCode () );
        } // End if
		
		// Swap calls
		selectedCalls.remove ( callFrom );
		selectedCalls.add ( to , callFrom );
		
    	// Display debug output if required
        if ( showDebugOutput ) {
			Log.d ( TAG , TAG + " ACTION : CLIENT [" + callFrom.getClientCode () + "] DISPLACED FROM " + from + " TO " + to );
			Log.d ( TAG , TAG + " DECISION : FROM [" + from + "] IS " + ( from < to ? "LESS" : "GREATER" ) + " THEN TO [" + to + "]" );
			Log.d ( TAG , TAG + " ACTION : CHANGING CLIENT [" + callFrom.getClientCode () + "] SEQUENCE" );
        } // End if
		
		// Determine if the target location is greater (in the list) than the current location
		if ( from < to )
			// Loop over the clients in between the two locations
			for ( int i = to - 1 ; i >= from ; i -- ) {
				// Retrieve the appropriate call object
				Call call = selectedCalls.get ( i );
				
		    	// Display debug output if required
		        if ( showDebugOutput )
		        	Log.d ( TAG , TAG + " LOOP : ITERATING AT " + i + " ON CLIENT [" + call.getClientCode () + "]" );
				
				// Determine if the current call is from the same cycle of the initial call
				if ( call.getCycleCall ().getCycleID () == callFrom.getCycleCall ().getCycleID () ) {
			    	
					// Display debug output if required
			        if ( showDebugOutput )
			        	Log.d ( TAG , TAG + " ACTION : CHANGING CLIENT [" + callFrom.getClientCode () + "] SEQUENCE FROM " + callFrom.getCycleCall ().getSequence () + " TO " + call.getCycleCall ().getSequence () );
					
					// Calls are from the same cycle
					// Create a call object back (if not previously done)
					callFrom.backup ();
					// Set the modified flag
					callFrom.setModified ( true );
					// Modify the cycle sequence
					callFrom.getCycleCall ().setSequence ( call.getCycleCall ().getSequence () );
					// Exit loop
					break;
				} // End if
			} // End for loop
		// Otherwise the target location is smaller (in the list) than the current location
		else {
			// Loop over the clients in between the two locations
			for ( int i = to + 1 ; i <= from ; i ++ ) {
				// Retrieve the appropriate call object
				Call call = selectedCalls.get ( i );
				
		    	// Display debug output if required
		        if ( showDebugOutput )
		        	Log.d ( TAG , TAG + " LOOP : ITERATING AT " + i + " ON CLIENT [" + call.getClientCode () + "]" );
				
				// Determine if the current call is from the same cycle of the initial call
				if ( call.getCycleCall ().getCycleID () == callFrom.getCycleCall ().getCycleID () ) {
					
			    	// Display debug output if required
			        if ( showDebugOutput )
			        	Log.d ( TAG , TAG + " ACTION : CHANGING CLIENT [" + call.getClientCode () + "] SEQUENCE FROM " + callFrom.getCycleCall ().getSequence () + " TO " + call.getCycleCall ().getSequence () );
					
					// Calls are from the same cycle
					// Create a call object back (if not previously done)
					callFrom.backup ();
					// Set the modified flag
					callFrom.setModified ( true );
					// Modify the cycle sequence
					callFrom.getCycleCall ().setSequence ( call.getCycleCall ().getSequence () );
					// Exit loop
					break;
				} // End if
			} // End for loop
		} // End else
		
    	// Display debug output if required
        if ( showDebugOutput )
        	Log.d ( TAG , TAG + " ACTION : CHANGING IN BETWEEN CLIENTS SEQUENCE" );
		
		// Determine if the target location is greater (in the list) than the current location
		if ( from < to )
			// Loop over the clients in between the two locations
			for ( int i = to - 1 ; i >= from ; i -- ) {
				// Retrieve the appropriate call object
				Call call = selectedCalls.get ( i );
				
		    	// Display debug output if required
		        if ( showDebugOutput )
		        	Log.d ( TAG , TAG + " LOOP : ITERATING AT " + i + " ON CLIENT [" + call.getClientCode () + "]" );
				
				// Determine if the current call is from the same cycle of the initial call
				if ( call.getCycleCall ().getCycleID () != callFrom.getCycleCall ().getCycleID () )
					// Calls are not from the same cycle
					// Skip current call
					continue;
				
		    	// Display debug output if required
		        if ( showDebugOutput )
		        	Log.d ( TAG , TAG + " ACTION : CHANGING CLIENT [" + call.getClientCode () + "] SEQUENCE FROM " + call.getCycleCall ().getSequence () + " TO " + ( call.getCycleCall ().getSequence () - 1 ) );
				
				// Create a call object back (if not previously done)
				call.backup ();
				// Set the modified flag
				call.setModified ( true );
				// Modify the cycle sequence accordingly
				call.getCycleCall ().setSequence ( call.getCycleCall ().getSequence () - 1 );
			} // End for loop
		// Otherwise the target location is smaller (in the list) than the current location
		else
			// Loop over the clients in between the two locations
			for ( int i = to + 1 ; i <= from ; i ++ ) {
				// Retrieve the appropriate call object
				Call call = selectedCalls.get ( i );
				
		    	// Display debug output if required
		        if ( showDebugOutput )
		        	Log.d ( TAG , TAG + " LOOP : ITERATING AT " + i + " ON CLIENT [" + call.getClientCode () + "]" );
				
				// Determine if the current call is from the same cycle of the initial call
				if ( call.getCycleCall ().getCycleID () != callFrom.getCycleCall ().getCycleID () )
					// Calls are not from the same cycle
					// Skip current call
					continue;
				
		    	// Display debug output if required
		        if ( showDebugOutput )
		        	Log.d ( TAG , TAG + " ACTION : CHANGING CLIENT [" + call.getClientCode () + "] SEQUENCE FROM " + call.getCycleCall ().getSequence () + " TO " + ( call.getCycleCall ().getSequence () + 1 ) );
				
				// Create a call object back (if not previously done)
				call.backup ();
				// Set the modified flag
				call.setModified ( true );
				// Modify the cycle sequence accordingly
				call.getCycleCall ().setSequence ( call.getCycleCall ().getSequence () + 1 );
			} // End for loop
		// Refresh list
		( (CyclePlanningAdapter) ( (HeaderViewListAdapter) dslv.getAdapter () ).getWrappedAdapter () ).notifyDataSetChanged ();
	}

	/*
	 * Register a drag event listener callback object for the dslv
	 *
	 * @see me.SyncWise.Android.Widgets.DSLV.DragSortListView.DragListener#drag(int, int)
	 */
	@Override
	public void drag ( int from , int to ) {
		// Do nothing
	}

	/*
	 * Register a remove event listener callback object for the dslv
	 *
	 * @see me.SyncWise.Android.Widgets.DSLV.DragSortListView.RemoveListener#remove(int)
	 */
	@Override
	public void remove ( int which ) {
		// Modify the appropriate call cycle
		// Retrieve the appropriate call cycle
		Call call = selectedCalls.get ( which );
		// Determine if the call is added
		if ( call.isAdded () ) {
			// The call is added but not saved
			// Simply remove the call from the lists
			calls.remove ( call );
			selectedCalls.remove ( call );
		} // End if
		else {
			// The call is saved
			// Make a backup before modifying
			call.backup ();
			// Flag as modified
			call.setModified ( true );
			// Modify the call status
			call.getCycleCall ().setCycleCallStatus ( StatusUtils.isDeleted () );
		} // End if
		// Remove the call from the selected calls list only
		selectedCalls.remove ( call );
		// Refresh list
		( (CyclePlanningAdapter) ( (HeaderViewListAdapter) dslv.getAdapter () ).getWrappedAdapter () ).notifyDataSetChanged ();
	}
	
	/**
	 * Indicates whether the cycle planning has new / unsaved modifications.<br>
	 * This method iterates over list of all cycle calls and hence might be a heavy loading.
	 * 
	 * @return	Boolean stating if there are new modifications.
	 */
	private boolean hasModifications () {
		// Flag used to determine if there are modifications
		boolean flag = false;
		// Iterate over all the cycle calls
		for ( Call call : calls ) {
			// Determine if the call is modified and has no backup (cannot determine the previous state)
			if ( call.isAdded () || ( call.isModified () && call.getBackup () == null ) )
				// There is at least one modification
				flag = true;
			// Determine if the call is modified and has a valid backup
			else if ( call.isModified () && call.getBackup () != null ) {
				// Determine if the call object and its backup are alike
				if ( EntityUtils.compare ( call.getCycleCall () , call.getBackup () ) ) {
					// The call object and its backup are alike, therefore there are no changes for this call
					// Clear the call backup
					call.clearBackup ();
					// Reset the modified flag
					call.setModified ( false );
				} // End if
				else
					// Otherwise there is at least one modification
					flag = true;
			} // End if
		} // End for each
		// Return the flag state
		return flag;
	}
	
}