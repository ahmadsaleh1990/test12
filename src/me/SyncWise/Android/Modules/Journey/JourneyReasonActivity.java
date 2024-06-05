/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Journey;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import me.SyncWise.Android.ActivityTransition;
import me.SyncWise.Android.AppAnimation;
import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.AppToast;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Vibration;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.IsProcessedUtils;
import me.SyncWise.Android.Database.JourneysUtils;
import me.SyncWise.Android.Database.Journeys;
import me.SyncWise.Android.Database.JourneysDao;
import me.SyncWise.Android.Database.Reasons;
import me.SyncWise.Android.Database.ReasonsDao;
import me.SyncWise.Android.Database.ReasonsUtils;
import me.SyncWise.Android.Database.StatusUtils;
import me.SyncWise.Android.Database.UsersDao;
import me.SyncWise.Android.Widgets.Baguette;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Activity implemented to prompt the user for missing days reasons.
 * 
 * @author Elias
 * @sw.todo	<b>Journey Reason Activity Implementation :</b><br>
 * Simply add this class in the {@code AndroidManifest.xml} file.
 *
 */
public class JourneyReasonActivity extends ListActivity implements OnItemLongClickListener {

	/**
	 * Constant integer holding the spinner animation duration in milliseconds.
	 */
	private static final int SPINNER_ANIMATION_DURATION = 250;
	
	/**
	 * Constant integer holding the multi-selection animation duration in milliseconds.
	 */
	private static final int MULTI_SELECTION_ANIMATION_DURATION = 500;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #activity}.
	 */
	public static final String ACTIVITY = JourneyReasonActivity.class.getName () + ".ACTIVITY";
	
	/**
	 * {@link java.lang.Class Class} activity reference.
	 */
	private Class < ? > activity;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #journeys}.
	 */
	private static final String JOURNEYS = JourneyReasonActivity.class.getName () + ".JOURNEYS";
	
	/**
	 * List holding the journey reasons.
	 */
	private ArrayList < JourneyReason > journeys;
	
	/**
	 * {@link me.SyncWise.Android.Modules.Journey.JourneyReason JourneyReason} reference to the selected journey.
	 */
	private JourneyReason selectedJourney;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #reasons}.
	 */
	private static final String REASONS = JourneyReasonActivity.class.getName () + ".REASONS";
	
	/**
	 * List holding all the available reasons used to validate missed days.
	 */
	private ArrayList < Reasons > reasons;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #displayReasonsList}.
	 */
	private static final String DISPLAY_REASONS_LIST = JourneyReasonActivity.class.getName () + ".DISPLAY_REASONS_LIST";
	
	/**
	 * Boolean used to indicate whether to display the reasons list.<br>
	 * This flag is used to re-display the list after an activity recreation ( caused by a device screen rotation, ...) 
	 */
	private boolean displayReasonsList;
	
	/**
	 * Reference to the reasons list save task.
	 */
	private static Save save;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #multiSelect}.
	 */
	private static final String MULTI_SELECT = JourneyReasonActivity.class.getName () + ".MULTI_SELECT";
	
	/**
	 * Flag used to indicate if the multi-selection feature is enabled or not.
	 */
	private boolean multiSelect;
	
	/**
	 * Getter - {@link #multiSelect}
	 * 
	 * @return	Flag used to indicate if the multi-selection feature is enabled or not.
	 */
	public boolean isMultiSelect () {
		// Return state state
		return multiSelect;
	}
	
	/**
	 * Starts the new activity defined by {@link #activity} and finishes the current activity (whether {@link #activity} is valid or not).
	 */
	private void startActivity () {
		try {
			// Start the new activity
			startActivity ( new Intent ( this , activity ) );
			// Finish activity
			finish ();
			// Specify an explicit transition animation to perform next
			ActivityTransition.SlideOutLeft ( this );
		} catch ( Exception exception ) {
			// Invalid activity, display toast
			new AppToast ( this ).setGravity ( Gravity.BOTTOM )
				.setYOffset ( (int) TypedValue.applyDimension ( TypedValue.COMPLEX_UNIT_DIP , 40 , getResources ().getDisplayMetrics () ) )
				.setDuration ( Toast.LENGTH_LONG )
				.setIcon ( R.drawable.warning )
				.setText ( getString ( R.string.invalid_activity_message ) ).show ();
		} finally {
			// Close this activity
			finish ();
		} // End of try-catch-finally block
	}
	
	/**
	 * AsyncTask helper class used to populate the journeys reasons list.
	 * 
	 * @author Elias
	 *
	 */
	private class PopulateList extends AsyncTask < Void , Void , Integer > {
		
		/**
		 * Integer holding the result value used to proceed with the activity.
		 */
		private static final int PROCEED = 0;
		
		/**
		 * Integer holding the result value used to indicate an invalid journey.
		 */		
		private static final int INVALID_JOURNEY = -1;
		
		/**
		 * Integer holding the result value used to indicate that no reasons are found.
		 */
		private static final int NO_REASONS = -2;
		
		/*
		 * Runs on the UI thread before doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute () {
			// Display indeterminate progress dialog
			AppDialog.getInstance ().displayIndeterminateProgress ( JourneyReasonActivity.this , null , null );
		}
		
		/*
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Integer doInBackground ( Void ... params ) {
			// Declare and initialize a calendar object to represent yesterday's date
			Calendar yesterday = Calendar.getInstance ();
			yesterday.add ( Calendar.DATE , -1 );
			yesterday.set ( Calendar.HOUR_OF_DAY , 0 );
			yesterday.set ( Calendar.MINUTE , 0 );
			yesterday.set ( Calendar.SECOND , 0 );
			yesterday.set ( Calendar.MILLISECOND , 0 );
			// Retrieve from DB the opened journey
			Journeys openedjourney = DatabaseUtils.getInstance ( JourneyReasonActivity.this ).getDaoSession ().getJourneysDao ().queryBuilder ()
				.where ( JourneysDao.Properties.EndDate.isNull () ).unique ();
			// Check if the opened journey is valid and occurred before yesterday
			if ( openedjourney == null || openedjourney.getStartDate ().getTime () > yesterday.getTimeInMillis () )
				// Invalid opened journey
				return INVALID_JOURNEY;
			
			// Otherwise the opened journey is valid
			// Initialize the list of reasons
			reasons = new ArrayList < Reasons > ();
			// Query the list of appropriate reasons
			reasons.addAll ( DatabaseUtils.getInstance ( JourneyReasonActivity.this ).getDaoSession ().getReasonsDao ().queryBuilder ()
					.where ( ReasonsDao.Properties.ReasonType.eq ( ReasonsUtils.Type.MISSED_JOURNEY ) )
					.orderAsc ( ReasonsDao.Properties.ReasonName ).list () );
			// Check if the reasons list is valid
			if ( reasons.isEmpty () )
				// Invalid reasons list
				return NO_REASONS;
			// Add the default reason
			reasons.add ( 0 , new Reasons ( null , "---" , null , "---" , null , null , null , null , null , null ) );
			
			// Otherwise the list of reasons is valid
			// Initialize the journey reasons list
			journeys = new ArrayList < JourneyReason > ();
			// Declare and initialize a calendar object used to hold the last journey date
			Calendar lastJourney = Calendar.getInstance ();
			lastJourney.setTimeInMillis ( openedjourney.getStartDate ().getTime () );
			// Retrieve the user prefix ID
			int prefixID = DatabaseUtils.getInstance ( JourneyReasonActivity.this ).getDaoSession ().getUsersDao ().queryBuilder ()
					.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode ( JourneyReasonActivity.this ) ) ,
							UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( JourneyReasonActivity.this ) ) ).unique ().getPrefixID ();
			// Populate the journey reasons list
			while ( lastJourney.before ( yesterday ) ) {
				// Add a journey reason
				lastJourney.add ( Calendar.DATE , 1 );
				journeys.add ( new JourneyReason ( JourneyReasonActivity.this , prefixID , lastJourney ) );
			} // End while loop
			
			// Proceed
			return PROCEED;
		}
		
		/*
		 * Runs on the UI thread after doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute ( Integer result ) {
			// Check if the journey is valid
			if ( result == INVALID_JOURNEY )
				// Finish activity
				finish ();
			// Check if the list of reasons is valid
			else if ( result == NO_REASONS ) {
				// Finish activity
				finish ();
				// Display toast
				new AppToast ( JourneyReasonActivity.this ).setGravity ( Gravity.CENTER )
				.setDuration ( Toast.LENGTH_LONG )
				.setIcon ( R.drawable.warning )
				.setText ( AppResources.getInstance ( JourneyReasonActivity.this ).getString ( JourneyReasonActivity.this , R.string.no_reasons_message ) ).show ();
			} // End else if
			// Check if the activity can proceed
			else if ( result == PROCEED ) {
				// The activity can proceed
				try {
					populateList ();
				} catch ( Exception exception ) {
					// Mainly the activity is not displayed. Do nothing
				} // End of try-catch block
			} // End else if
			
			// Dismiss dialog
			AppDialog.getInstance ().dismiss ();
		}
		
	}
	
	/**
	 * AsyncTask helper class used to save the journeys reasons list.
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
			AppDialog.getInstance ().displayIndeterminateProgress ( JourneyReasonActivity.this , null , null );
		}
		
		/*
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Boolean doInBackground ( Void ... params ) {
			// Declare and initialize a calendar object to represent yesterday's date
			Calendar yesterday = Calendar.getInstance ();
			yesterday.add ( Calendar.DATE , -1 );
			yesterday.set ( Calendar.HOUR_OF_DAY , 0 );
			yesterday.set ( Calendar.MINUTE , 0 );
			yesterday.set ( Calendar.SECOND , 0 );
			yesterday.set ( Calendar.MILLISECOND , 0 );
			// Retrieve from DB the opened journey
			Journeys openedjourney = DatabaseUtils.getInstance ( JourneyReasonActivity.this ).getDaoSession ().getJourneysDao ().queryBuilder ()
				.where ( JourneysDao.Properties.EndDate.isNull () ).unique ();
			// Check if the opened journey is valid and occurred before yesterday
			if ( openedjourney == null || openedjourney.getStartDate ().getTime () > yesterday.getTimeInMillis () )
				// Invalid opened journey
				return false;
			
			// Flag used to indicate whether an error occurred or not
			boolean error = false;
			// Retrieve the user code
			String userCode = DatabaseUtils.getCurrentUserCode ( JourneyReasonActivity.this );
			// Retrieve the company code
			String companyCode = DatabaseUtils.getCurrentCompanyCode ( JourneyReasonActivity.this );
			
			try {
				// Begin transaction
				DatabaseUtils.getInstance ( JourneyReasonActivity.this ).getDaoSession ().getDatabase ().beginTransaction ();
				
				// Close the opened journey
				openedjourney.setEndDate ( Calendar.getInstance ().getTime () );
				openedjourney.setIsProcessed ( IsProcessedUtils.isNotSync () );
				DatabaseUtils.getInstance ( JourneyReasonActivity.this ).getDaoSession ().getJourneysDao ().update ( openedjourney );
				
				// Compute the number of journey reasons
				int size = journeys.size ();
				// Iterate over all journey reasons
				for ( int i = 0 ; i < size ; i ++ ) {
					// Retrieve current date
					Calendar today = Calendar.getInstance ();
					// Get the calendar object associated with the journey code
					Calendar journeyDate = JourneysUtils.getCalendar ( journeys.get ( i ).getJourneyCode () );
					// TODO : Details of a journey used to create a reason for a missed day
					// Create a new journey using the current journey code
					DatabaseUtils.getInstance ( JourneyReasonActivity.this ).getDaoSession ().getJourneysDao ().insert ( new Journeys ( null ,
							journeys.get ( i ).getJourneyCode () , // JourneyCode
							userCode , // UserCode
							DatabaseUtils.getCurrentDivisionCode ( JourneyReasonActivity.this ) , // DivisionCode
							companyCode , // CompanyCode
							journeyDate.getTime () , // StartDate
							i == ( size - 1 ) ? null :journeyDate.getTime () , // EndDate
							null , // VehicleCode
							StatusUtils.isDeleted () , // JourneyStatus
							journeys.get ( i ).getReasonCode () , // ReasonCode
							0.0 , // StartOdometer
							0.0 , // EndOdometer
							0.0 , // Distance
							IsProcessedUtils.isNotSync () , // IsProcessed
							today.getTime () ) ); // StampDate
				} // End for each
				
				// Commit changes
				DatabaseUtils.getInstance ( JourneyReasonActivity.this ).getDaoSession ().getDatabase ().setTransactionSuccessful ();
			} catch ( Exception exception ) {
				// Indicate that an error occurred
				error = true;
			} finally {
				// End transaction
				DatabaseUtils.getInstance ( JourneyReasonActivity.this ).getDaoSession ().getDatabase ().endTransaction ();
			} // End try-catch-finally block
				
			return error;
		}
		
		/*
		 * Runs on the UI thread after doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute ( Boolean error ) {
			// Determine if an error occurred
			if ( error )
				// Indicate that an error occurred
				Baguette.showText ( JourneyReasonActivity.this ,
						AppResources.getInstance ( JourneyReasonActivity.this ).getString ( JourneyReasonActivity.this , R.string.savin_reasons_failed_message ) ,
						Baguette.BackgroundColor.RED );
			else {
				// Otherwise no errors occurred
				Vibration.vibrate ( JourneyReasonActivity.this );
				// Check if the next activity is valid
				if ( activity != null )
					// Start the next activity
					startActivity ();
				else {
					// Finish activity
					finish ();
					// Specify an explicit transition animation to perform next
					ActivityTransition.SlideOutRight ( JourneyReasonActivity.this );
				} // End else
			} // End else
			// Clear task reference
			save = null;
			// Dismiss dialog
			AppDialog.getInstance ().dismiss ();
		}
		
	}
	
	/*
	 * Called when the activity is starting.
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate ( Bundle savedInstanceState ) {
		// Superclass method call
		super.onCreate ( savedInstanceState );
		// Set the activity content from a layout resource.
		setContentView ( R.layout.journey_reason_activity );
		// Change the title associated with this activity
		setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.journey_reason_activity_title ) );
		// Assign an item long click listener
		getListView ().setOnItemLongClickListener ( this );
		
		// Retrieve the activity from the intent
		activity = (Class < ? >) getIntent ().getSerializableExtra ( ACTIVITY );
		
		// Determine if a saved instance state is provided
		if ( savedInstanceState != null ) {
			// Restore the content provided by the saved instance state 
			journeys = (ArrayList < JourneyReason >) savedInstanceState.getSerializable ( JOURNEYS );
			reasons = (ArrayList < Reasons >) savedInstanceState.getSerializable ( REASONS );
			multiSelect = savedInstanceState.getBoolean ( MULTI_SELECT );
			displayReasonsList = savedInstanceState.getBoolean ( DISPLAY_REASONS_LIST );
		} // End if
		
		// Check if the list of journeys is valid
		if ( journeys == null || reasons == null )
			// Populate the list of journeys
			new PopulateList ().execute ();
		else {
			// Otherwise the list of journeys is valid
			populateList ();
		} // End else
	}
	
	/*
	 * Called after onRestoreInstanceState(Bundle), onRestart(), or onPause(), for your activity to start interacting with the user.
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume () {
		// Superclass method call
		super.onResume ();
		// Display the reasons list (if necessary)
		displayReasonsList ();
	}
	
    /*
     * This method will be called when an item in the list is selected.
     *
     * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
     */
	@Override
    protected void onListItemClick ( ListView listView , View view , int position , long id ) {
		// Check if the multi-selection feature is enabled
		if ( multiSelect ) {
			// The multi-selection feature is enabled
			// Retrieve a reference to the current journey reason
			JourneyReason journeyReason = (JourneyReason) getListView ().getItemAtPosition ( position );
			// Select / deselect the current journey reason accordingly
			journeyReason.setSelected ( ! journeyReason.getSelected () );
			( (JourneyReasonAdapter.ViewHolder) getListView ().getChildAt ( position - getListView ().getFirstVisiblePosition () ).getTag () ).checkBox.setChecked ( journeyReason.getSelected () );
			// Display the number of missing days
			displayMissedDaysNumber ();
		} // End if
		else {
			// The multi-selection feature is disabled
			// Check if the same item is clicked
			if ( selectedJourney == (JourneyReason) getListView ().getItemAtPosition ( position ) )
				// Do nothing
				return;
			
			// Check if the previous journey is valid
			if ( selectedJourney != null ) {
				// Mark the previous journey as not selected
				selectedJourney.setSelected ( false );
				// Check if the previous journey is visible on the list
				if ( journeys.indexOf ( selectedJourney ) >= getListView ().getFirstVisiblePosition ()
						&& journeys.indexOf ( selectedJourney ) <= getListView ().getLastVisiblePosition () ) {
					// Process the view : display the appropriate arrow and spinner
					( (JourneyReasonAdapter) getListAdapter () )
						.processView ( (JourneyReasonAdapter.ViewHolder) getListView ().getChildAt ( journeys.indexOf ( selectedJourney ) - getListView ().getFirstVisiblePosition () ).getTag () , selectedJourney );
					// Collapse the spinner
					AppAnimation.collapse ( ( (JourneyReasonAdapter.ViewHolder) getListView ().getChildAt ( journeys.indexOf ( selectedJourney ) - getListView ().getFirstVisiblePosition () ).getTag () ).spinner
							, LayoutParams.MATCH_PARENT , LayoutParams.WRAP_CONTENT , SPINNER_ANIMATION_DURATION , AppAnimation.Direction.VERTICAL , null );
				} // End if
			} // End if
			
			// Update the selected journey
			selectedJourney = (JourneyReason) getListView ().getItemAtPosition ( position );
			// Mark the selected journey as selected
			selectedJourney.setSelected ( true );
			// Process the view : display the appropriate arrow and spinner
			( (JourneyReasonAdapter) getListAdapter () )
				.processView ( (JourneyReasonAdapter.ViewHolder) getListView ().getChildAt ( position - getListView ().getFirstVisiblePosition () ).getTag () , selectedJourney );
			// Expand the spinner
			AppAnimation.expand ( ( (JourneyReasonAdapter.ViewHolder) getListView ().getChildAt ( position - getListView ().getFirstVisiblePosition () ).getTag () ).spinner
					 , LayoutParams.MATCH_PARENT , LayoutParams.WRAP_CONTENT , SPINNER_ANIMATION_DURATION , AppAnimation.Direction.VERTICAL );
		} // End else
	}
	
    /*
     * Callback method to be invoked when an item in this view has been clicked and held.
     *
     * @see android.widget.AdapterView.OnItemLongClickListener#onItemLongClick(android.widget.AdapterView, android.view.View, int, long)
     */
	@Override
	public boolean onItemLongClick ( AdapterView < ? > parent , View view , int position , long id ) {
		// Check if the multi-selection feature is already enabled
		if ( ! multiSelect ) {
			// Enable the multi selection feature
			multiSelect = true;
			// Refresh action bar menus to apply the change
			invalidateOptionsMenu ();
			
			// Check if the previous journey is valid
			if ( selectedJourney != null ) {
				// Mark the previous journey as not selected
				selectedJourney.setSelected ( false );
				// Check if the previous journey is visible on the list
				if ( journeys.indexOf ( selectedJourney ) >= getListView ().getFirstVisiblePosition ()
						&& journeys.indexOf ( selectedJourney ) <= getListView ().getLastVisiblePosition () ) {
					// Process the view : display the appropriate arrow and spinner
					( (JourneyReasonAdapter) getListAdapter () )
						.processView ( (JourneyReasonAdapter.ViewHolder) getListView ().getChildAt ( journeys.indexOf ( selectedJourney ) - getListView ().getFirstVisiblePosition () ).getTag () , selectedJourney );
					// Collapse the spinner
					AppAnimation.collapse ( ( (JourneyReasonAdapter.ViewHolder) getListView ().getChildAt ( journeys.indexOf ( selectedJourney ) - getListView ().getFirstVisiblePosition () ).getTag () ).spinner
							, LayoutParams.MATCH_PARENT , LayoutParams.WRAP_CONTENT , SPINNER_ANIMATION_DURATION , AppAnimation.Direction.VERTICAL , null );
				} // End if
				// Clear the selected journey reference
				selectedJourney = null;
			} // End if
			
			// Iterate over all list view items
			for ( int i = 0 ; i < getListView ().getChildCount () ; i ++ ) {
				// Slide out the arrow
				( (JourneyReasonAdapter.ViewHolder) getListView ().getChildAt ( i ).getTag () )
					.arrow.startAnimation ( getAnimationSlideRight () );
				// Display the check box
				( (JourneyReasonAdapter.ViewHolder) getListView ().getChildAt ( i ).getTag () )
					.checkBox.setVisibility ( View.VISIBLE );
				( (JourneyReasonAdapter.ViewHolder) getListView ().getChildAt ( i ).getTag () )
					.checkBox.setLayoutParams ( new FrameLayout.LayoutParams ( FrameLayout.LayoutParams.WRAP_CONTENT , FrameLayout.LayoutParams.WRAP_CONTENT , Gravity.CENTER_VERTICAL | Gravity.RIGHT ) );
				// Slide in the check box
				( (JourneyReasonAdapter.ViewHolder) getListView ().getChildAt ( i ).getTag () )
					.checkBox.startAnimation ( getAnimationSlideLeft () );
			} // End for loop
			// Select the current journey reason
			( (JourneyReason) getListView ().getItemAtPosition ( position ) ).setSelected ( true );
			( (JourneyReasonAdapter.ViewHolder) getListView ().getChildAt ( position - getListView ().getFirstVisiblePosition () ).getTag () ).checkBox.setChecked ( true );
			// Display the number of missing days
			displayMissedDaysNumber ();
			
			// Indicate that callback consumed the long click event
			return true;
		} // End if
		// Indicate that callback did not consume the long click event
		return false;
	}
	
	/*
	 * Called when the activity has detected the user's press of the back key. 
	 *
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed () {
		// Check if the multi-selection feature is enabled
		if ( multiSelect ) {
			// Disabled the multi-selection feature
			multiSelect = false;
			// Refresh action bar menus to apply the change
			invalidateOptionsMenu ();
			// Discard all selected journeys
			for ( JourneyReason journey : journeys )
				if ( journey.getSelected () )
					journey.setSelected ( false );
			// Display the number of missing days
			displayMissedDaysNumber ();
			// Iterate over all list view items
			for ( int i = 0 ; i < getListView ().getChildCount () ; i ++ ) {
				// Retrieve a reference to the check box
				final CheckBox checkBox = ( (JourneyReasonAdapter.ViewHolder) getListView ().getChildAt ( i ).getTag () ).checkBox;
				// Deselect the check box
				checkBox.setChecked ( false );
				// Slide out the check box
				Animation animation = getAnimationSlideRight ();
				animation.setAnimationListener ( new AnimationListener () {
					/*
					 * Notifies the start of the animation.
					 *
					 * @see android.view.animation.Animation.AnimationListener#onAnimationStart(android.view.animation.Animation)
					 */
					@Override
					public void onAnimationStart ( Animation animation ) {
						// Do nothing
					}
					
					/*
					 * Notifies the repetition of the animation.
					 *
					 * @see android.view.animation.Animation.AnimationListener#onAnimationRepeat(android.view.animation.Animation)
					 */
					@Override
					public void onAnimationRepeat ( Animation animation ) {
						// Do nothing
					}
					
					/*
					 * Notifies the end of the animation.
					 *
					 * @see android.view.animation.Animation.AnimationListener#onAnimationEnd(android.view.animation.Animation)
					 */
					@Override
					public void onAnimationEnd ( Animation animation ) {
						// Hide the check box
						checkBox.setLayoutParams ( new FrameLayout.LayoutParams ( 0 , 0 ) );
					}
				} );
				checkBox.startAnimation ( animation );
				// Display the arrow
				( (JourneyReasonAdapter.ViewHolder) getListView ().getChildAt ( i ).getTag () )
					.arrow.setVisibility ( View.VISIBLE );
				// Make sure the black arrow is displayed
				( (JourneyReasonAdapter.ViewHolder) getListView ().getChildAt ( i ).getTag () )
					.arrow.setImageResource ( R.drawable.arrow_right_black );
				// Slide in the arrow
				( (JourneyReasonAdapter.ViewHolder) getListView ().getChildAt ( i ).getTag () )
					.arrow.startAnimation ( getAnimationSlideLeft () );
			} // End for loop
		} // End if
		else
			// Superclass method call
			super.onBackPressed ();
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
	
    /*
     * Called to retrieve per-instance state from an activity before being killed so that the state can be restored in onCreate(Bundle) or onRestoreInstanceState(Bundle).
     *
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    protected void onSaveInstanceState ( Bundle outState ) {
    	// Superclass method call
    	super.onSaveInstanceState ( outState );
    	// Save the content of journeys in the outState bundle
    	outState.putSerializable ( JOURNEYS , journeys );
    	// Save the content of selectedReason in the outState bundle
    	outState.putSerializable ( REASONS , reasons );
    	// Save the content of multiSelect in the outState bundle
    	outState.putSerializable ( MULTI_SELECT , multiSelect );
    	// Save the content of displayReasonsList in the outState bundle
    	outState.putSerializable ( DISPLAY_REASONS_LIST , displayReasonsList );
    }
	
	/*
	 * Called when you are no longer visible to the user.
	 *
	 * @see android.app.Activity#onStop()
	 */
	@Override
    protected void onStop () {
    	// Superclass method call
		super.onStop ();
		// Determine if the activity is finishing
		if ( isFinishing () ) {
			activity = null;
			journeys = null;
			reasons = null;
			selectedJourney = null;
			getListView ().setOnItemLongClickListener ( null );
		} // End if
	}
    
	/**
	 * Populates the list view by setting a new adapter using the list of journeys.<br>
	 * The reference to the selected journey is computed and number of missed days is displayed.
	 */
	private void populateList () {
		// Check if the list of journeys and reasons are valid
		if ( journeys == null || reasons == null )
			// Invalid list(s)
			return;
		// Refer to the selected journey if any
		setSelectedJourney ();
		// Declare and initialize a new reason adapter used to populate the spinners
		ReasonAdapter reasonAdapter = new ReasonAdapter ( JourneyReasonActivity.this , android.R.layout.simple_spinner_item , reasons );
		// Sets the layout resource to create the drop down views
		reasonAdapter.setDropDownViewResource ( R.layout.journey_reason_activity_reason_item );
		// Set a new adapter using the current list of journeys
		setListAdapter ( new JourneyReasonAdapter ( JourneyReasonActivity.this , R.layout.journey_reason_activity_item , journeys , reasonAdapter ) );
		// Display the number of missing days
		displayMissedDaysNumber ();
	}
	
	/**
	 * Stores a reference to the first selected journey.<br>
	 * If none is selected, the reference to the selected journey is cleared.
	 */
	private void setSelectedJourney () {
		// Check if the list of journeys is valid
		if ( journeys != null )
			// Iterate over all journeys
			for ( JourneyReason journey : journeys )
				// Check if the current journey is selected
				if ( journey.getSelected () ) {
					// Set the journey reference
					selectedJourney = journey;
					return;
				} // End if
		// Otherwise no journey is selected
		// Clear the reference
		selectedJourney = null;
	}
	
	/**
	 * Displays the total number of missed days without valid reasons in the action bar as sub title.
	 */
	public void displayMissedDaysNumber () {
		// Compute the number of missed journeys with no reasons
		int missed = 0;
		// Compute the number of selected journeys
		int selected = 0;
		// Check if the list of journeys is valid
		if ( journeys != null )
			// Iterate over all journeys
			for ( JourneyReason journey : journeys ) {
				// Check if the journey has a valid reason
				if ( ! journey.hasReason () )
					// Increment the number of missed journeys
					missed ++;
				// Check if the joureny is selected
				if ( journey.getSelected () )
					// Increment the number of selected journeys
					selected ++;
			} // End for each
		// Display the appropriate subtitle
		getActionBar ().setSubtitle ( multiSelect ? "( " + selected + " " + getString ( R.string.selected_label ) + ")" : String.valueOf ( missed ) );
	}
	
	/**
	 * Determines if the journey reasons can be saved.<br>
	 * This condition is met if and only if all journeys have a valid reason each.
	 * 
	 * @return	Boolean indicating if the journey reasons are valid and hence can be saved.
	 */
	private boolean canSave () {
		// Check if the list of journeys is valid
		if ( journeys == null )
			// No reasons can be saved
			return false;
		// Iterate over all journeys
		for ( JourneyReason journey : journeys )
			// Check if the journey has a valid reason
			if ( ! journey.hasReason () )
				// There is at least one invalid reason
				return false;
		// Otherwise all reasons are valid
		return true;
	}
	
	/**
	 * Displays the reasons list via a dialog, if necessary.<br>
	 * Whether the reasons list should be displayed or not is determined by {@link #displayReasonsList}.
	 */
	private void displayReasonsList () {
		// Determine if the reasons list should be displayed
		if ( displayReasonsList == false )
			// Do nothing
			return;
		// Retrieve the reason descriptions in an array (excluding the first reason which is not a valid reason)
		final List < Reasons > _reasons = new ArrayList < Reasons > ();
		_reasons.addAll ( ( (JourneyReasonAdapter) getListAdapter () ).getReasons () );
		_reasons.remove ( 0 );
		String reasons [] = new String [ _reasons.size () ];
		for ( int i = 0 ; i < reasons.length ; i ++ )
			reasons [ i ] = _reasons.get ( i ).getReasonName ();
		// Prompt the user to choose a reason
		AppDialog.getInstance ().displayList ( JourneyReasonActivity.this ,
				AppResources.getInstance ( JourneyReasonActivity.this ).getString ( JourneyReasonActivity.this , R.string.reason_list_dialog_title ) , 
				reasons ,
				AppDialog.Cancelable.BACK_BUTTON_AND_TOUCH ,
				new DialogInterface.OnClickListener () {
					@Override
					public void onClick ( DialogInterface dialog , int which ) {
						// Retrieve the selected reason
						Reasons reason = _reasons.get ( which );
						// Set the reason code to all selected journey reasons
						// Iterate over all journey reason
						for ( int i = 0 ; i < journeys.size () ; i ++ )
							// Check if the journey reason is selected
							if ( journeys.get ( i ).getSelected () ) {
								// Set the journey reason code
								journeys.get ( i ).setReasonCode ( reason );
								// De-select the journey reason
								journeys.get ( i ).setSelected ( false );
								// Check if the previous journey is visible on the list
								if ( i >= getListView ().getFirstVisiblePosition () && i <= getListView ().getLastVisiblePosition () ) {
									// Retrieve a reference to the check box
									final CheckBox checkBox = ( (JourneyReasonAdapter.ViewHolder) getListView ().getChildAt ( i - getListView ().getFirstVisiblePosition () ).getTag () ).checkBox;
									// Refresh the check box
									checkBox.setChecked ( journeys.get ( i ).getSelected () );
									// Retrieve a reference to the status icon
									final ImageView status = ( (JourneyReasonAdapter.ViewHolder) getListView ().getChildAt ( i - getListView ().getFirstVisiblePosition () ).getTag () ).status;
							    	// Fully scale down the status icon
							        status.animate ().scaleX ( 0f ).scaleY ( 0f ).setDuration ( JourneyReasonAdapter.STATUS_ANIMATION_DURATION );
							        // Declare and initialize a handler, mainly used to scale the status icon back up after a delay
							        Handler handler = new Handler ();
							        // Execute the runnable after a delay
							        handler.postDelayed ( new Runnable() {
										@Override
										public void run () {
											// Modify the status icon accordingly
											status.setImageResource ( R.drawable.ok );
											// Fully scale up the status icon
											status.animate ().scaleX ( 1f ).scaleY ( 1f ).setDuration ( JourneyReasonAdapter.STATUS_ANIMATION_DURATION );
										}
									} , JourneyReasonAdapter.STATUS_ANIMATION_DURATION );
								} // End if
							} // End if
						// Update the number of selected days
						displayMissedDaysNumber ();
					}
				} ,
				new DialogInterface.OnCancelListener () {
					@Override
					public void onCancel ( DialogInterface dialog ) {
						// Clear flag
						displayReasonsList = false;
					}
				} );
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
		// Enable the save menu item
		me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_save ) , R.string.save_label );
		// Check if multi-selection feature is enabled
		if ( multiSelect ) {
			// Enable the required menu items
			me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_list ) , R.string.list_label );
			me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_mark_all ) , R.string.mark_label );
			me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_unmark_all ) , R.string.unmark_label );
		} // End if
		else
			me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_info ) , R.string.info_label );
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
    	// Determine if the action save is selected
    	// AND no previous saving task is launched
    	if ( menuItem.getItemId () == R.id.action_save && save == null ) {
    		// Disable the menu item
    		menuItem.setEnabled ( false );
        	// Determine if the journey reasons can be saved
        	if ( canSave () ) {
        		// Save the journey reasons asynchronously
        		save = new Save ();
        		save.execute ();
        	} // End if
        	else
				// Indicate that not all reasons are valid
				Baguette.showText ( this ,
						AppResources.getInstance ( this ).getString ( this , R.string.missing_reasons_message ) ,
						Baguette.BackgroundColor.RED );
    		// Enable the menu item
    		menuItem.setEnabled ( true );
    		// Consume event
    		return true;
    	} // End if
    	// Determine if the action info is selected
    	else if ( menuItem.getItemId () == R.id.action_info ) {
    		// Display info dialog
    		AppDialog.getInstance ().displayAlert ( this ,
    				null ,
    				AppResources.getInstance ( this ).getString ( this , R.string.journey_reason_info_message ) ,
    				AppDialog.ButtonsType.NONE ,
    				null );
    		// Consume event
    		return true;
    	} // End if
    	// Determine if the action list is selected
    	else if ( menuItem.getItemId () == R.id.action_list ) {
    		// Set the flag
    		displayReasonsList = true;
    		// Display the reasons list
    		displayReasonsList ();
    		// Consume event
    		return true;
    	} // End else if
    	// Determine if the action list is mark all
    	else if ( menuItem.getItemId () == R.id.action_mark_all ) {
			// Select all journeys
			for ( JourneyReason journey : journeys )
				if ( ! journey.getSelected () )
					journey.setSelected ( true );
			// Display the number of missing days
			displayMissedDaysNumber ();
			// Refresh list
			( (JourneyReasonAdapter) getListAdapter () ).notifyDataSetChanged ();
    		// Consume event
    		return true;
    	} // End else if
    	// Determine if the action list is unmark all
    	else if ( menuItem.getItemId () == R.id.action_unmark_all ) {
			// Discard all selected journeys
			for ( JourneyReason journey : journeys )
				if ( journey.getSelected () )
					journey.setSelected ( false );
			// Display the number of missing days
			displayMissedDaysNumber ();
			// Refresh list
			( (JourneyReasonAdapter) getListAdapter () ).notifyDataSetChanged ();
    		// Consume event
    		return true;
    	} // End else if
    	// Allow normal menu processing to proceed
    	return false;
    }
    
    /**
     * Builds and returns a sliding animation from right to <b>left</b>.<br>
     * The splash animation duration is set by {@link #MULTI_SELECTION_ANIMATION_DURATION}.
     * 
     * @return	An {@link android.view.animation.Animation} object used to perform a sliding animation.
     */
    private Animation getAnimationSlideLeft () {
		// Declare and initialize a translation animation
    	TranslateAnimation translate = new TranslateAnimation ( Animation.RELATIVE_TO_SELF , 3f , Animation.RELATIVE_TO_SELF , 0f , Animation.RELATIVE_TO_SELF , 0f , Animation.RELATIVE_TO_SELF , 0f );
		// Clear the starting offset for the animation to start at the beginning
		translate.setStartOffset ( 0 );
		// Set the animation duration
		translate.setDuration( MULTI_SELECTION_ANIMATION_DURATION );
		// Return the animation
		return translate;
    }
    
    /**
     * Builds and returns a sliding animation from left to <b>right</b>.<br>
     * The splash animation duration is set by {@link #MULTI_SELECTION_ANIMATION_DURATION}.
     * 
     * @return	An {@link android.view.animation.Animation} object used to perform a sliding animation.
     */
    private Animation getAnimationSlideRight () {
		// Declare and initialize a translation animation
    	TranslateAnimation translate = new TranslateAnimation ( Animation.RELATIVE_TO_SELF , 0f , Animation.RELATIVE_TO_SELF , 3f , Animation.RELATIVE_TO_SELF , 0f , Animation.RELATIVE_TO_SELF , 0f );
		// Clear the starting offset for the animation to start at the beginning
		translate.setStartOffset ( 0 );
		// Set the animation duration
		translate.setDuration( MULTI_SELECTION_ANIMATION_DURATION );
		// Make the transformation performed by the animation persist when it is finished
		translate.setFillAfter ( true );
		// Return the animation
		return translate;
    }
    
}