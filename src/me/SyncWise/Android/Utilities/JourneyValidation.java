/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Utilities;

import java.util.Calendar;
import java.util.List;

import me.SyncWise.Android.ActivityTransition;
import me.SyncWise.Android.AppToast;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.IsProcessedUtils;
import me.SyncWise.Android.Database.JourneysUtils;
import me.SyncWise.Android.Database.Journeys;
import me.SyncWise.Android.Database.JourneysDao;
import me.SyncWise.Android.Database.StatusUtils;
import me.SyncWise.Android.Database.UsersDao;
import me.SyncWise.Android.Modules.Journey.JourneyReasonActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Utility class that provides a {@link JourneyValidation.Task Task} and its {@link JourneyValidation.Result Result} in order to validate a new journey request.
 * 
 * @author Elias
 * 
 */
public class JourneyValidation {

	/**
	 * Enumeration used to define the journey validation results.
	 * 
	 * @author Elias
	 * 
	 */
	public static enum Result {
		JOURNEY_CLOSED ,
		JOURNEY_OPENED ,
		JOURNEY_INVALID_START ,
		JOURNEY_INVALID_END ,
		JOURNEY_DUPLICATES ,
		ALL_JOURNEYS_CLOSED ,
		MANY_OPENED_JOURNEYS ,
		JOURNEY_CREATED ,
		LAST_OPENED_JOURNEY_INVALID_START ,
		MISSED_DAYS ,
		ERROR ;
		
		/**
		 * Determines if the provided result represents an error or not.<br>
		 * The result is NOT an error in the following cases only :
		 * <ul>
		 * <li>{@link #JOURNEY_OPENED}</li>
		 * <li>{@link #JOURNEY_CREATED}</li>
		 * <li>{@link #MISSED_DAYS}</li>
		 * </ul>
		 * 
		 * @param result	An enumeration constant of {@link me.SyncWise.Android.Utilities.JourneyValidation.Result Result} indicating the journey validation result.
		 * @return	Boolean indicating if the provided result is an error or not.
		 */
		public static boolean isError ( Result result ) {
			// Check if result is JOURNEY_OPENED, JOURNEY_CREATED or MISSED_DAYS
			if ( result == JOURNEY_OPENED || result == JOURNEY_CREATED || result == MISSED_DAYS )
				// The result is NOT an error
				return false;
			// Otherwise the result is an error
			return true;
		}
		
	}

	/**
	 * Interface used to provide an implementation of a call back method to execute after a journey validation.
	 * 
	 * @author Elias
	 * 
	 */
	public static interface CallBack {

		/**
		 * Call back method used to execute statements.
		 * 
		 * @param result	An enumeration constant of {@link me.SyncWise.Android.Utilities.JourneyValidation.Result Result} indicating the journey validation result.
		 */
		public void perform ( Result result );

	}
	
	/**
	 * Class used to specify a required action after the journey validation.<br>
	 * The action is performed in a specific case, either if an error occurred in general, or for a specific result indicated by {@link me.SyncWise.Android.Utilities.JourneyValidation.Result Result}.
	 * 
	 * @author Elias
	 * 
	 */
	public static class Action {
		
		/**
		 * Listener used to perform an action after a journey validation.
		 */
		private final CallBack callBack;

		/**
		 * Flag used to indicate if the current activity should be finished after the journey validation.
		 */
		private final boolean finish;
		
		/**
		 * {@link java.lang.Class} activity reference.
		 */
		private final Class < ? > activity;
		
		/**
		 * Flag used to determine whether to clear the top of the activity history / task if an activity should be started.
		 * 
		 * @see	android.content.Intent#FLAG_ACTIVITY_CLEAR_TOP Intent.FLAG_ACTIVITY_CLEAR_TOP
		 */
		private final boolean clearTop;
		
		/**
		 * Getter - {@link #callBack}
		 * 
		 * @return	Listener used to perform an action if an error occurs.
		 */
		public CallBack getCallBack () {
			// Return the call back listener
			return callBack;
		}

		/**
		 * Getter - {@link #finish}
		 * 
		 * @return Boolean used to indicate if the current activity should be finished after the journey validation.
		 */
		public boolean getFinish () {
			// Return the boolean state
			return finish;
		}
		
		/**
		 * Getter - {@link #activity}
		 * 
		 * @return {@link java.lang.Class} activity reference.
		 */
		public Class < ? > getActivity () {
			// Return the activity
			return activity;
		}
		
		/**
		 * Getter - {@link #clearTop}
		 * 
		 * @return	Flag used to determine whether to clear the top of the activity history / task if an activity should be started.
		 * @see	android.content.Intent#FLAG_ACTIVITY_CLEAR_TOP Intent.FLAG_ACTIVITY_CLEAR_TOP
		 */
		public boolean getClearTop () {
			// Return the flag state
			return clearTop;
		}
		
		/**
		 * Constructor.
		 * 
		 * @param callBack	Listener used to perform an action after a journey validation.
		 */
		public Action ( final CallBack callBack ) {
			// Initialize attributes
			this.callBack = callBack;
			this.finish = false;
			this.activity = null;
			this.clearTop = false;
		}
		
		/**
		 * Constructor.
		 * 
		 * @param finish	Flag used to indicate if the current activity should be finished after the journey validation.
		 */
		public Action ( final boolean finish ) {
			// Initialize attributes
			this.callBack = null;
			this.finish = finish;
			this.activity = null;
			this.clearTop = false;
		}
		
		/**
		 * Constructor.
		 * 
		 * @param activity	{@link java.lang.Class} activity reference, started after the journey validation.
		 * @param clearTop	Flag used to determine whether to clear the top of the activity history / task.
		 */
		public Action ( final Class < ? > activity , final boolean clearTop ) {
			// Initialize attributes
			this.callBack = null;
			this.finish = false;
			this.activity = activity;
			this.clearTop = clearTop;
		}
		
	}

	/**
	 * AsyncTask helper class used to validate a journey request.<br>
	 * The following validation checks are performed :
	 * <ol>
	 * <li>The journeys with today's journey code are retrieved :
	 * <ul>
	 * <li>Today's journey started before <em>now</em> : {@link JourneyValidation.Result#JOURNEY_INVALID_START JOURNEY_INVALID_START}</li>
	 * <li>Today's journey is not closed : {@link JourneyValidation.Result#JOURNEY_OPENED JOURNEY_OPENED}</li>
	 * <li>Today's journey ended before <em>now</em> : {@link JourneyValidation.Result#JOURNEY_INVALID_END JOURNEY_INVALID_END}</li>
	 * <li>Today's journey is closed : {@link JourneyValidation.Result#JOURNEY_CLOSED JOURNEY_CLOSED}</li>
	 * <li>There are multiple journeys for today : {@link JourneyValidation.Result#JOURNEY_DUPLICATES JOURNEY_DUPLICATES}</li>
	 * </ul>
	 * </li>
	 * <li>The total number of journeys is retrieved. If there are none, a new one is created : {@link JourneyValidation.Result#JOURNEY_CREATED JOURNEY_CREATED}</li>
	 * <li>The opened journeys from the oldest to the newest are retrieved :
	 * <ul>
	 * <li>The last opened journey started before <em>now</em> : {@link JourneyValidation.Result#LAST_OPENED_JOURNEY_INVALID_START LAST_OPENED_JOURNEY_INVALID_START}</li>
	 * <li>The last opened journey is for yesterday, it is closed and a new one is created : {@link JourneyValidation.Result#JOURNEY_CREATED JOURNEY_CREATED}</li>
	 * <li>The last opened journey occurred before yesterday : {@link JourneyValidation.Result#MISSED_DAYS MISSED_DAYS}</li>
	 * <li>There are multiple opened (and not closed) journeys : {@link JourneyValidation.Result#MANY_OPENED_JOURNEYS MANY_OPENED_JOURNEYS}</li>
	 * <li>There are no opened journey, all journeys are closed : {@link JourneyValidation.Result#ALL_JOURNEYS_CLOSED ALL_JOURNEYS_CLOSED}</li>
	 * </ul>
	 * </li>
	 * An {@link me.SyncWise.Android.Utilities.JourneyValidation.Action} can be assigned to the journey validation, for it to be performed after the journey validation ends.<br>
	 * The action is executed in all cases of the journey validation {@link me.SyncWise.Android.Utilities.JourneyValidation.Result} except for :
	 * <ul>
	 * <li>{@link me.SyncWise.Android.Utilities.JourneyValidation.Result#MISSED_DAYS MISSED_DAYS}, where the {@link me.SyncWise.Android.Modules.Journey.JourneyReasonActivity JourneyReasonActivity} is started.<br>
	 * Only in the case where, an error occurs during the start of the activity, the action is performed (the result is set to {@link me.SyncWise.Android.Utilities.JourneyValidation.Result#ERROR ERROR}).</li>
	 * <li>{@link me.SyncWise.Android.Utilities.JourneyValidation.Result#JOURNEY_OPENED JOURNEY_OPENED} or {@link me.SyncWise.Android.Utilities.JourneyValidation.Result#JOURNEY_CREATED JOURNEY_CREATED}, where the specified {@link me.SyncWise.Android.Utilities.JourneyValidation.Task#activity activity} is started.<br>
	 * Only in the case where, an activity is not specified or an error occurs during the start of that activity, the action is performed (the result is set to {@link me.SyncWise.Android.Utilities.JourneyValidation.Result#ERROR ERROR}).</li>
	 * 
	 * @author Elias
	 * 
	 */
	public static class Task extends AsyncTask < Void , Void , Result > {

		/**
		 * The application context.
		 */
		private Context context;

		/**
		 * Reference to the calling activity context.
		 */
		private Context callingActivity;

		/**
		 * {@link java.lang.Class Class} activity reference.
		 */
		private Class < ? > activity;

		/**
		 * {@link me.SyncWise.Android.Utilities.JourneyValidation.Action Action} object used to determine the action to perform after the journey validation.
		 */
		private Action action;

		/**
		 * Constructor.
		 * 
		 * @param context	The application context.
		 * @param activity	{@link java.lang.Class Class} activity reference, started if the validation is a success, and passed to the {@link me.SyncWise.Android.Modules.Journey.JourneyReasonActivity JourneyReasonActivity} if the validation is a failure.
		 * @param action	{@link me.SyncWise.Android.Utilities.JourneyValidation.Action Action} object used to determine the action to perform after the journey validation.
		 */
		public Task ( final Context context , final Class < ? > activity , Action action ) {
			// Superclass method call
			super ();
			// Initialize the calling activity context
			this.callingActivity = context;
			// Initialize the application context
			this.context = context.getApplicationContext ();
			// Initialize the activity to start after a successful validation (if needed)
			this.activity = activity;
			// Initialize the action to perform after the journey validation
			this.action = action;
		}

		/*
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Result doInBackground ( Void ... params ) {
			// Retrieve the user prefix ID
			int prefixID = DatabaseUtils.getInstance ( context ).getDaoSession ().getUsersDao ().queryBuilder ()
					.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode ( context ) ) ,
							UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( context ) ) ).unique ().getPrefixID ();
			// Retrieve from DB the journey with today's journey code
			List < Journeys > currentJourney = DatabaseUtils
					.getInstance ( context )
					.getDaoSession ()
					.getJourneysDao ()
					.queryBuilder ()
					.where ( JourneysDao.Properties.JourneyCode.eq ( JourneysUtils.getJourneyCode ( prefixID ) ) )
					.list ();

			// Check if the current journey exists
			if ( currentJourney.size () == 1 ) {
				// Check if the current journey has started with a valid start date
				if ( currentJourney.get ( 0 ).getStartDate ().getTime () > Calendar.getInstance ().getTimeInMillis () )
					// INVALID request
					return Result.JOURNEY_INVALID_START;
				// Otherwise the journey has a valid start date
				// Check if the journey is NOT closed
				else if ( currentJourney.get ( 0 ).getEndDate () == null )
					// The journey is previously created
					return Result.JOURNEY_OPENED;
				// Otherwise the journey is closed
				// Check if the current journey has ended with a valid end date
				else if ( currentJourney.get ( 0 ).getEndDate ().getTime () > Calendar.getInstance ().getTimeInMillis () )
					// INVALID request
					return Result.JOURNEY_INVALID_END;
				else
					// The journey is closed
					return Result.JOURNEY_CLOSED;
			} // End if
				// Otherwise the list should be empty
			else if ( ! currentJourney.isEmpty () )
				// There are multiple journeys for the same day
				return Result.JOURNEY_DUPLICATES;

			// Determine if there are no journeys what so ever
			if ( DatabaseUtils.getInstance ( context ).getDaoSession ().getJourneysDao ().count () == 0 ) {
				// There are no journeys, create a new one
				createJourney ( prefixID );
				return Result.JOURNEY_CREATED;
			} // End if

			// Retrieve from DB all the opened journeys from the oldest to the newest
			List < Journeys > openedjourneys = DatabaseUtils
					.getInstance ( context ).getDaoSession ().getJourneysDao ()
					.queryBuilder ()
					.where ( JourneysDao.Properties.EndDate.isNull () )
					.orderAsc ( JourneysDao.Properties.JourneyCode ).list ();

			// Check if an opened journey exists
			if ( openedjourneys.size () == 1 ) {
				// Retrieve the start date
				Calendar startDate = Calendar.getInstance ();
				startDate.setTimeInMillis ( openedjourneys.get ( 0 ).getStartDate ().getTime () );
				// Retrieve today's date
				Calendar today = Calendar.getInstance ();
				// Determine if the start day occurred before today
				if ( today.before ( startDate ) )
					// INVALID request
					return Result.LAST_OPENED_JOURNEY_INVALID_START;
				// Compute the number of days between the two dates
				int days = 0;
				while ( today.get ( Calendar.YEAR ) != startDate.get ( Calendar.YEAR ) || today.get ( Calendar.DAY_OF_YEAR ) != startDate.get ( Calendar.DAY_OF_YEAR ) ) {
					days ++ ;
					startDate.add ( Calendar.DATE , 1 );
				} // End while loop
					// Determine if there is 1 day difference
				if ( days == 1 ) {
					// The last opened journey is yesterday
					// Close yesterday's journey
					openedjourneys.get ( 0 ).setEndDate ( Calendar.getInstance ().getTime () );
					openedjourneys.get ( 0 ).setIsProcessed ( IsProcessedUtils.isNotSync () );
					DatabaseUtils.getInstance ( context ).getDaoSession ().getJourneysDao ().update ( openedjourneys.get ( 0 ) );
					// Create a new journey
					createJourney ( prefixID );
					return Result.JOURNEY_CREATED;
				} // End if
					// Otherwise there is at least one missing day
				return Result.MISSED_DAYS;
			} // End if
			else if ( ! openedjourneys.isEmpty () )
				// There are multiple opened (and not closed) journeys
				return Result.MANY_OPENED_JOURNEYS;

			// Otherwise ALL the journeys are closed
			return Result.ALL_JOURNEYS_CLOSED;
		}

		/*
		 * Runs on the UI thread after doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute ( Result result ) {
			// Check the journey validation result
			switch ( result ) {
			case JOURNEY_OPENED:
			case JOURNEY_CREATED:
				try {
					// Check if the next activity is valid
					if ( activity != null ) {
						// Start a new activity (if needed)
						callingActivity.startActivity ( new Intent ( context , activity ) );
						// Specify an explicit transition animation to perform next
						ActivityTransition.SlideOutTop ( (Activity) callingActivity );
						// Clear objects
						clear ();
						// Exit method
						return;
					} // End if
				} catch ( Exception exception ) {
					// Invalid activity, display toast
					displayWarningToast ( context.getString ( R.string.invalid_activity_message ) );
					result = Result.ERROR;
				} // End of try-catch block
				break;
			case JOURNEY_CLOSED:
				// The current journey is closed, display toast
				displayWarningToast ( context.getString ( R.string.journey_closed_message ) );
				break;
			case JOURNEY_INVALID_START:
				// The current journey has an invalid start date, display toast
				displayWarningToast ( context.getString ( R.string.journey_invalid_start_message ) );
				break;
			case JOURNEY_INVALID_END:
				// The current journey has an invalid end date, display toast
				displayWarningToast ( context.getString ( R.string.journey_invalid_end_message ) );
				break;
			case JOURNEY_DUPLICATES:
				// The current journey is duplicated, display toast
				displayWarningToast ( context.getString ( R.string.journey_duplicates_message ) );
				break;
			case ALL_JOURNEYS_CLOSED:
				// The current journey is closed, display toast
				displayWarningToast ( context.getString ( R.string.journey_closed_message ) );
				break;
			case MANY_OPENED_JOURNEYS:
				// There are many opened journeys, display toast
				displayWarningToast ( context.getString ( R.string.many_opened_journeys_message ) );
				break;
			case LAST_OPENED_JOURNEY_INVALID_START:
				// The last opened journey has an invalid start date, display toast
				displayWarningToast ( context.getString ( R.string.last_opened_journey_invalid_start_message ) );
				break;
			case MISSED_DAYS:
				try {
					// Create a new intent to start a new activity
					Intent intent = new Intent ( context , JourneyReasonActivity.class );
					// Check if the next activity is valid
					if ( activity != null )
						// Add the next activity to the intent
						intent.putExtra ( JourneyReasonActivity.ACTIVITY , activity );
					// Start a new activity
					callingActivity.startActivity ( intent );
					// Specify an explicit transition animation to perform next
					ActivityTransition.SlideOutTop ( (Activity) callingActivity );
					// Clear objects
					clear ();
					// Exit method
					return;
				} catch ( Exception exception ) {
					// Invalid activity, display toast
					displayWarningToast ( context.getString ( R.string.invalid_activity_message ) );
					result = Result.ERROR;
				} // End of try-catch block
				break;
			} // End switch

			// Now that the journey validation ended, determine if there is a valid action action to perform
			if ( action != null  ) {
				// Perform action
				try {
					// Finish activity (if required)
					if ( action.getFinish () ) {
						// Finish activity
						( (Activity) callingActivity ).finish ();
						// Specify an explicit transition animation to perform next
						ActivityTransition .SlideOutLeft ( (Activity) callingActivity );
					} // End if
					// Start a new activity (if required)
					else if ( action.getActivity () != null ) {
						// Create a new intent to start a new activity
						Intent intent = new Intent ( context , action.getActivity () );
						// Check if the task history should be cleared
						if ( action.getClearTop () )
							// Clear the top of task history
							intent.setFlags ( Intent.FLAG_ACTIVITY_CLEAR_TOP );
						// Start a new activity
						callingActivity.startActivity ( intent );
					} // End else if
					// Execute call back (if required)
					else if ( action.getCallBack () != null )
						// Perform specified action
						action.getCallBack ().perform ( result );
				} catch ( Exception exception ) {
					// Invalid activity, display toast
					displayWarningToast ( context.getString ( R.string.invalid_activity_message ) );
				} // End of try-catch block
			} // End if

			// Clear objects
			clear ();
		}

		/**
		 * Clears objects references.
		 */
		public void clear () {
			// Clear objects
			context = null;
			callingActivity = null;
			activity = null;
		}
		
		/**
		 * Inserts a new (opened) journey into DB using today's date.
		 * 
		 * @param prefixID	Integer holding the user prefix ID.
		 */
		private void createJourney ( final int prefixID ) {
			// TODO : Details of a brand new journey
			// Insert a new journey
			DatabaseUtils.getInstance ( context ).getDaoSession ()
					.getJourneysDao ().insert ( new Journeys ( null , // 
							JourneysUtils.getJourneyCode ( prefixID ) , // JourneyCode
							DatabaseUtils.getCurrentUserCode ( context ) , // UserCode
							DatabaseUtils.getCurrentDivisionCode ( context ) , // DivisionCode
							DatabaseUtils.getCurrentCompanyCode ( context ) , // CompanyCode
							Calendar.getInstance ().getTime () , // StartDate
							null , // EndDate
							null , // VehicleCode
							StatusUtils.isAvailable () , // JourneyStatus
							null , // ReasonCode
							0.0 , // StartOdometer
							0.0 , // EndOdometer
							0.0 , // Distance
							IsProcessedUtils.isNotSync () , // IsProcessed
							Calendar.getInstance ().getTime () ) ); // StampDate
		}

		/**
		 * Displays a toast with a warning icon and the provided message.
		 * 
		 * @param message	String holding the toast message.
		 */
		private void displayWarningToast ( String message ) {
			// Create and display a new toast using the provided message and a warning icon
			new AppToast ( context )
			.setGravity ( Gravity.BOTTOM )
			.setYOffset ( (int) TypedValue.applyDimension ( TypedValue.COMPLEX_UNIT_DIP , 40 , context.getResources ().getDisplayMetrics () ) )
			.setDuration ( Toast.LENGTH_LONG )
			.setIcon ( R.drawable.warning )
			.setText ( ( message == null ? "" : message ) )
			.show ();
		}
		
	}

}