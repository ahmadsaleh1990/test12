/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.ClientsList.ClientInfo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Vibration;
import me.SyncWise.Android.Database.ClientAvailabilities;
import me.SyncWise.Android.Database.ClientAvailabilitiesDao;
import me.SyncWise.Android.Database.ClientAvailabilitiesUtils;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.IsProcessedUtils;
import me.SyncWise.Android.Database.StatusUtils;
import me.SyncWise.Android.Widgets.Baguette;
import me.SyncWise.Android.Widgets.AvailabilityCalendar.AvailabilityCalendar;
import me.SyncWise.Android.Widgets.AvailabilityCalendar.AvailabilityCalendarView;
import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

/**
 * Fragment used to represent a modular section to display the client availability.
 * 
 * @author Elias
 * @sw.todo <b>Client Availability Implementation :</b><br>
 * Simply the permission below in the {@code AndroidManifest.xml} file :<br>
 * {@code <uses-permission android:name="android.permission.VIBRATE"/>}
 *
 */
@SuppressLint("UseSparseArrays")
public class ClientAvailabilityFragment extends Fragment {

	/**
	 * Bundle key used to put/retrieve data.
	 */
	public static final String LAYOUT = ClientAvailabilityFragment.class.getName () + ".LAYOUT";
	
	/*
	 * Called to do initial creation of a fragment.
	 *
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate ( Bundle savedInstanceState ) {
		// Superclass method call
	    super.onCreate ( savedInstanceState );
	    // Report that this fragment would like to participate in populating the options menu
        setHasOptionsMenu ( true );
	}
	
	/*
	 * Called to have the fragment instantiate its user interface view.
	 *
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
    @Override
    public View onCreateView ( LayoutInflater inflater , ViewGroup container , Bundle savedInstanceState ) {
    	// Inflate and return the fragment's view
    	return inflater.inflate ( getArguments ().getInt ( LAYOUT ) , container , false );
    }
	
    /*
     * Called when the fragment's activity has been created and this fragment's view hierarchy instantiated.
     *
     * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
     */
	@Override
	public void onActivityCreated ( Bundle savedInstanceState ) {
		// Superclass method call
		super.onActivityCreated ( savedInstanceState );
		// Check if the calendar slots map is initialized
		if ( ( (ClientInfoActivity) getActivity () ).getCalendarSlots () != null ) {
			// Clear all previous child views
			( (ScrollView) getView ().findViewById ( R.id.layout_client_availability ) ).removeAllViews ();
			// Display the availability calendar
			( (ScrollView) getView ().findViewById ( R.id.layout_client_availability ) ).addView ( new AvailabilityCalendarView ( getActivity () , ( (ClientInfoActivity) getActivity () ).getCalendarSlots () , null ) );
		} // End if
	}
    
	/*
	 * Initialize the contents of the Activity's standard options menu.
	 *
	 * @see android.support.v4.app.Fragment#onCreateOptionsMenu(android.view.Menu, android.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu ( Menu menu , MenuInflater menuInflater ) {
		// Superclass method call
		super.onCreateOptionsMenu ( menu , menuInflater );
		// Inflate a menu hierarchy from the specified XML resource
		menuInflater.inflate ( R.menu.action_bar , menu );
		
		// Retrieve a reference to the availability calendar view
		View view = ( (ScrollView) getView ().findViewById ( R.id.layout_client_availability ) ).getChildAt ( 0 );
		// Check if the view is valid and that there is at least one modified calendar slot
		if ( view != null && view instanceof AvailabilityCalendarView && ( (AvailabilityCalendarView) view ).isModified () )
			// Enable the required menu items
			me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_save ) );
	}
	
	/*
	 * This hook is called whenever an item in your options menu is selected.
	 *
	 * @see android.support.v4.app.Fragment#onOptionsItemSelected(android.view.MenuItem)
	 */
	public boolean onOptionsItemSelected ( MenuItem menuItem ) {
    	// Determine if the action add is selected
    	if ( menuItem.getItemId () == R.id.action_save ) {
    		// Retrieve a reference to the availability calendar view
    		AvailabilityCalendarView view = (AvailabilityCalendarView) ( (ScrollView) getView ().findViewById ( R.id.layout_client_availability ) ).getChildAt ( 0 );
    		// Check if the availability calendar view is valid
    		if ( view == null )
    			// Consume event
    			return true;
    		// Determine if there are modifications
    		if ( ! view.isModified () ) {
				// Indicate that there are no new modifications
				Baguette.showText ( getActivity () ,
						AppResources.getInstance ( getActivity () ).getString ( getActivity () , R.string.no_new_modifications_message ) ,
						Baguette.BackgroundColor.BLUE );
				// Consume event
				return true;
    		} // End if
    		
			// Save the modifications
			new Save ().execute ();
    		
			// Consume event
			return true;
		} // End else if
    	// Allow normal menu processing to proceed
    	return false;
	}
	
	/**
	 * Sets the availability calendar view using the provided calendar slots map and adds it to its parent view group.
	 * 
	 * @param calendarSlots	Map of {@link me.SyncWise.Android.Widgets.AvailabilityCalendar.AvailabilityCalendar AvailabilityCalendar} objects hosting the availability slots of the current availability calendar view.
	 * @param clientAvailabilities	List of {@link me.SyncWise.Android.Database.ClientAvailabilities ClientAvailabilities} objects hosting the client schedule.
	 */
	public HashMap < Integer , ArrayList < AvailabilityCalendar > > setAvailabilityCalendarView ( final HashMap < Integer , ArrayList < AvailabilityCalendar > > calendarSlots , final List < ClientAvailabilities > clientAvailabilities ) {
		// Clear all previous child views
		( (ScrollView) getView ().findViewById ( R.id.layout_client_availability ) ).removeAllViews ();
		// Declare and initialize an availability calendar
		AvailabilityCalendarView view = new AvailabilityCalendarView ( getActivity () , ( (ClientInfoActivity) getActivity () ).getCalendarSlots () , clientAvailabilities );
		// Display the availability calendar
		( (ScrollView) getView ().findViewById ( R.id.layout_client_availability ) ).addView ( view );
		// Return the calendar slots map
		return view.getCalendarSlots ();
	}
	
	/**
	 * AsyncTask helper class used to save all modifications done in the client availability asynchronously.
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
			AppDialog.getInstance ().displayIndeterminateProgress ( getActivity () , null , null );
		}
		
		/*
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Boolean doInBackground ( Void ... params ) {
    		// Check if the client has a valid division
    		if ( ( (ClientInfoActivity) getActivity () ).getClientDivisions () == null || ( (ClientInfoActivity) getActivity () ).getClientDivisions ().isEmpty () )
    			// The client is not linked to a division
    			return true;
			
			// Retrieve the user code
			String userCode = DatabaseUtils.getCurrentUserCode ( getActivity () );
			// Retrieve the company code
			String companyCode = DatabaseUtils.getCurrentCompanyCode ( getActivity () );
			// Retrieve the division code
			String divisionCode = ( (ClientInfoActivity) getActivity () ).getClientDivisions ().get ( 0 ).getDivisionCode ();
			// Retrieve system date
			Calendar today = Calendar.getInstance ();
			// Flag used to indicate whether an error occurred or not
			boolean error = false;
			
    		try {
				// Begin transaction
				DatabaseUtils.getInstance ( getActivity () ).getDaoSession ().getDatabase ().beginTransaction ();

				// Retrieve the week days abbreviations
				String mondayAbbreviation = getActivity ().getString ( R.string.monday_abbreviation );
				String tuesdayAbbreviation = getActivity ().getString ( R.string.tuesday_abbreviation );
				String wednesdayAbbreviation = getActivity ().getString ( R.string.wednesday_abbreviation );
				String thursdayAbbreviation = getActivity ().getString ( R.string.thursday_abbreviation );
				String fridayAbbreviation = getActivity ().getString ( R.string.friday_abbreviation );
				String saturdayAbbreviation = getActivity ().getString ( R.string.saturday_abbreviation );
				String sundayAbbreviation = getActivity ().getString ( R.string.sunday_abbreviation );
				
				// Retrieve a reference to the availability calendar view
				AvailabilityCalendarView view = (AvailabilityCalendarView) ( (ScrollView) getView ().findViewById ( R.id.layout_client_availability ) ).getChildAt ( 0 );
				
				// Retrieve all the client availability for the current client that are NOT deleted
				List < ClientAvailabilities > clientAvailabilities = DatabaseUtils.getInstance ( getActivity () ).getDaoSession ().getClientAvailabilitiesDao ().queryBuilder ()
						.where ( ClientAvailabilitiesDao.Properties.ClientCode.eq ( ( (ClientInfoActivity) getActivity () ).getClient ().getClientCode () ) ,
								ClientAvailabilitiesDao.Properties.AvailabilityStatus.eq ( StatusUtils.isAvailable () ) ).list ();
				// Map the client availabilities by day
				HashMap < Integer , ArrayList < ClientAvailabilities > > _clientAvailabilities = new HashMap < Integer , ArrayList < ClientAvailabilities > > ();
				// Iterate over all client availabilities
				for ( ClientAvailabilities clientAvailability : clientAvailabilities ) {
					// Determine the current client availability day
					int day = 0;
					if ( clientAvailability.getDay ().equals ( mondayAbbreviation ) )
						day = Calendar.MONDAY;
					else if ( clientAvailability.getDay ().equals ( tuesdayAbbreviation ) )
						day = Calendar.TUESDAY;
					else if ( clientAvailability.getDay ().equals ( wednesdayAbbreviation ) )
						day = Calendar.WEDNESDAY;
					else if ( clientAvailability.getDay ().equals ( thursdayAbbreviation ) )
						day = Calendar.THURSDAY;
					else if ( clientAvailability.getDay ().equals ( fridayAbbreviation ) )
						day = Calendar.FRIDAY;
					else if ( clientAvailability.getDay ().equals ( saturdayAbbreviation ) )
						day = Calendar.SATURDAY;
					else if ( clientAvailability.getDay ().equals ( sundayAbbreviation ) )
						day = Calendar.SUNDAY;
					else
						// Invalid day
						continue;
					
					// Check if the current day is already mapped
					if ( _clientAvailabilities.containsKey ( day ) )
						// Add the current client availability to the list
						_clientAvailabilities.get ( day ).add ( clientAvailability );
					else {
						// Declare and initialize the client availabilities list for the current day
						ArrayList < ClientAvailabilities > list = new ArrayList < ClientAvailabilities > ();
						// Add the current client availability to the list
						list.add ( clientAvailability );
						// Map the list to the current day
						_clientAvailabilities.put ( day , list );
					} // End else
				} // End for each
				
				// Declare and initialize a list of availability slots used to host the modified calendar slots
				ArrayList < AvailabilityCalendar > modifiedSlots = new ArrayList < AvailabilityCalendar > ();
				
				// Iterate over all calendar slots
				for ( Integer day : view.getCalendarSlots ().keySet () ) {
					// Determine the slot day
					String slotDay = null;
					switch ( day ) {
					case Calendar.MONDAY:
						slotDay = mondayAbbreviation;
						break;
					case Calendar.TUESDAY:
						slotDay = tuesdayAbbreviation;
						break;
					case Calendar.WEDNESDAY:
						slotDay = wednesdayAbbreviation;
						break;
					case Calendar.THURSDAY:
						slotDay = thursdayAbbreviation;
						break;
					case Calendar.FRIDAY:
						slotDay = fridayAbbreviation;
						break;
					case Calendar.SATURDAY:
						slotDay = saturdayAbbreviation;
						break;
					case Calendar.SUNDAY:
						slotDay = sundayAbbreviation;
						break;
					default:
						// Invalid day
						continue;
					} // End switch
					// Iterate over all calendar slots
					for ( AvailabilityCalendar slot : view.getCalendarSlots ().get ( day ) )
						// Determine if the slot is modified and should be added
						if ( slot.isModified () && slot.isSelected () ) {
							// Set the slot starting and ending times
							Calendar start = Calendar.getInstance ();
							start.set ( today.get ( Calendar.YEAR ) , today.get ( Calendar.MONTH ) , today.get ( Calendar.DAY_OF_MONTH ) , slot.getFromHour () , slot.getFromMinute () , 0 );
							start.set ( Calendar.MILLISECOND , 0 );
							Calendar end = Calendar.getInstance ();
							end.set ( today.get ( Calendar.YEAR ) , today.get ( Calendar.MONTH ) , today.get ( Calendar.DAY_OF_MONTH ) , slot.getToHour () , slot.getToMinute () , 0 );
							end.set ( Calendar.MILLISECOND , 0 );
							// Add the current slot to the modified slots
							modifiedSlots.add ( slot );
							// Flag used to determine whether to skip the creation of a new client availability or not
							boolean skipCreation = false;
							// Determine if there is at least one client availability for the current day
							if ( _clientAvailabilities.containsKey ( day ) ) {
								// Retrieve the client availabilities list of the current day
								ArrayList < ClientAvailabilities > list = _clientAvailabilities.get ( day );
								// Set the availability starting and ending times
								Calendar availabilityStart = Calendar.getInstance ();
								Calendar availabilityEnd = Calendar.getInstance ();
								// Iterate over all the client availabilities
								for ( ClientAvailabilities clientAvailability : list ) {
									// Check if the current client availability is not deleted
									if ( clientAvailability.getAvailabilityStatus () == StatusUtils.isDeleted () )
										// The current client availability is deleted, skip it
										continue;
									// Set the availability starting and ending times
									availabilityStart.setTimeInMillis ( clientAvailability.getStartTime ().getTime () );
									availabilityStart.set ( today.get ( Calendar.YEAR ) , today.get ( Calendar.MONTH ) , today.get ( Calendar.DAY_OF_MONTH ) );
									availabilityEnd.setTimeInMillis ( clientAvailability.getEndTime ().getTime () );
									availabilityEnd.set ( today.get ( Calendar.YEAR ) , today.get ( Calendar.MONTH ) , today.get ( Calendar.DAY_OF_MONTH ) );
									// Determine if the slot end time matches or proceeds the availability start time while the slot start time precedes the availability start time
									if ( end.compareTo ( availabilityStart ) != -1 && start.compareTo ( availabilityStart ) == -1 ) {
										// Extend the availability start time
										availabilityStart.setTimeInMillis ( start.getTimeInMillis () );
										clientAvailability.setStartTime ( availabilityStart.getTime () );
										// Update the current availability 
										clientAvailability.setIsProcessed ( IsProcessedUtils.isNotSync () );
										DatabaseUtils.getInstance ( getActivity () ).getDaoSession ().getClientAvailabilitiesDao ().update ( clientAvailability );
										// Skip current outer iteration
										skipCreation = true;
										break;
									} // End if
									// Determine if the slot start time matches or precedes the availability end time while the slot end time proceeds the availability end time
									else if ( start.compareTo ( availabilityEnd ) != 1 && end.compareTo ( availabilityEnd ) == 1 ) {
										// Extend the availability end time 
										availabilityEnd.setTimeInMillis ( end.getTimeInMillis () );
										clientAvailability.setEndTime ( availabilityEnd.getTime () );
										// Update the current availability
										clientAvailability.setIsProcessed ( IsProcessedUtils.isNotSync () );
										DatabaseUtils.getInstance ( getActivity () ).getDaoSession ().getClientAvailabilitiesDao ().update ( clientAvailability );
										// Skip current outer iteration
										skipCreation = true;
										break;
									} // End else if
								} // End for each
							} // End if

							// Check if the current iteration should be skipped
							if ( skipCreation )
								continue;
							// Otherwise, a new client availability should be created
							// Declare and initialize a new client availability
							ClientAvailabilities clientAvailability = new ClientAvailabilities ( null , // ID
									userCode , // UserCode
									( (ClientInfoActivity) getActivity () ).getClient ().getClientCode () , // ClientCode
									divisionCode , // DivisionCode
									companyCode , // CompanyCode
									slotDay , // Day
									ClientAvailabilitiesUtils.getAvailabilityID ( today ) , // AvailabilityID
									start.getTime () , // StartTime
									end.getTime () , // EndTime
									StatusUtils.isAvailable () , // AvailabilityStatus
									IsProcessedUtils.isNotSync () , // IsProcessed
									today.getTime () ); // StampDate
							// Update the current date (to avoid PK constraint violation)
							today.setTimeInMillis ( today.getTimeInMillis () + 1 );
							// Insert the new client availability
							DatabaseUtils.getInstance ( getActivity () ).getDaoSession ().getClientAvailabilitiesDao ().insert ( clientAvailability );
							// Check if the current day is already mapped
							if ( _clientAvailabilities.containsKey ( day ) )
								// Add the current client availability to the list
								_clientAvailabilities.get ( day ).add ( clientAvailability );
							else {
								// Declare and initialize the client availabilities list for the current day
								ArrayList < ClientAvailabilities > list = new ArrayList < ClientAvailabilities > ();
								// Add the current client availability to the list
								list.add ( clientAvailability );
								// Map the list to the current day
								_clientAvailabilities.put ( day , list );
							} // End else
						} // End if
							
						// Otherwise determine if the slot is modified and should be removed
						else if ( slot.isModified () && ! slot.isSelected () ) {
							// Set the slot starting and ending times
							Calendar start = Calendar.getInstance ();
							start.set ( today.get ( Calendar.YEAR ) , today.get ( Calendar.MONTH ) , today.get ( Calendar.DAY_OF_MONTH ) , slot.getFromHour () , slot.getFromMinute () , 0 );
							start.set ( Calendar.MILLISECOND , 0 );
							Calendar end = Calendar.getInstance ();
							end.set ( today.get ( Calendar.YEAR ) , today.get ( Calendar.MONTH ) , today.get ( Calendar.DAY_OF_MONTH ) , slot.getToHour () , slot.getToMinute () , 0 );
							end.set ( Calendar.MILLISECOND , 0 );
							// Add the current slot to the modified slots
							modifiedSlots.add ( slot );
							// Retrieve the client availabilities list of the current day
							ArrayList < ClientAvailabilities > list = _clientAvailabilities.get ( day );
							// Set the availability starting and ending times
							Calendar availabilityStart = Calendar.getInstance ();
							Calendar availabilityEnd = Calendar.getInstance ();
							// Iterate over all the client availabilities
							for ( int i = 0 ; i < list.size () ; i ++ ) {
								// Retrieve a reference to the current client availability
								ClientAvailabilities clientAvailability = list.get ( i );
								// Set the availability starting and ending times
								availabilityStart.setTimeInMillis ( clientAvailability.getStartTime ().getTime () );
								availabilityStart.set ( today.get ( Calendar.YEAR ) , today.get ( Calendar.MONTH ) , today.get ( Calendar.DAY_OF_MONTH ) );
								availabilityEnd.setTimeInMillis ( clientAvailability.getEndTime ().getTime () );
								availabilityEnd.set ( today.get ( Calendar.YEAR ) , today.get ( Calendar.MONTH ) , today.get ( Calendar.DAY_OF_MONTH ) );
								// Determine if the slot start time matches the availability start time while the slot end time precedes the availability end time
								if ( start.compareTo ( availabilityStart ) == 0 && end.compareTo ( availabilityEnd ) == -1 ) {
									// Shrink the availability start time 
									availabilityStart.setTimeInMillis ( end.getTimeInMillis () );
									clientAvailability.setStartTime ( availabilityStart.getTime () );
									// Update the current availability
									clientAvailability.setIsProcessed ( IsProcessedUtils.isNotSync () );
									DatabaseUtils.getInstance ( getActivity () ).getDaoSession ().getClientAvailabilitiesDao ().update ( clientAvailability );
									// Exit for loop
									break;
								} // End if
								// Determine if the slot end time matches the availability end time while the slot start time proceeds the availability start time 
								else if ( end.compareTo ( availabilityEnd ) == 0 && start.compareTo ( availabilityStart ) == 1 ) {
									// Shrink the availability start time 
									availabilityEnd.setTimeInMillis ( start.getTimeInMillis () );
									clientAvailability.setEndTime ( availabilityEnd.getTime () );
									// Update the current availability
									clientAvailability.setIsProcessed ( IsProcessedUtils.isNotSync () );
									DatabaseUtils.getInstance ( getActivity () ).getDaoSession ().getClientAvailabilitiesDao ().update ( clientAvailability );
									// Exit for loop
									break;
								} // End else if
								// Determine if the slot is in between the availability start and end times
								else if ( start.after ( availabilityStart ) && end.before ( availabilityEnd ) ) {
									// The current client availability should be split
									// Declare and initialize a new client availability representing the lower part
									ClientAvailabilities lowerAvailability = new ClientAvailabilities ( null , // 
											userCode , // UserCode
											( (ClientInfoActivity) getActivity () ).getClient ().getClientCode () , // ClientCode
											divisionCode , // DivisionCode
											companyCode , // CompanyCode
											slotDay , // Day
											ClientAvailabilitiesUtils.getAvailabilityID ( today ) , // AvailabilityID
											end.getTime () , // StartTime
											availabilityEnd.getTime () , // EndTime
											StatusUtils.isAvailable () , // AvailabilityStatus
											IsProcessedUtils.isNotSync () , // IsProcessed
											today.getTime () ); // StampDate
									// Update the current date (to avoid PK constraint violation)
									today.setTimeInMillis ( today.getTimeInMillis () + 1 );
									// Insert the new client availability
									DatabaseUtils.getInstance ( getActivity () ).getDaoSession ().getClientAvailabilitiesDao ().insert ( lowerAvailability );
									// Add the new client availability to the list
									list.add ( lowerAvailability );
									// Update the client availability to be the upper part
									clientAvailability.setEndTime ( start.getTime () );
									clientAvailability.setIsProcessed ( IsProcessedUtils.isNotSync () );
									DatabaseUtils.getInstance ( getActivity () ).getDaoSession ().getClientAvailabilitiesDao ().update ( clientAvailability );
									// Exit for loop
									break;
								} // End else if
								// Determine if the availability is in between the slot start and end times
								else if ( start.compareTo ( availabilityStart ) != 1 && end.compareTo ( availabilityEnd ) != -1 ) {
									// The current client availability should be removed
									// Delete the current client availability
									clientAvailability.setAvailabilityStatus ( StatusUtils.isDeleted () );
									clientAvailability.setIsProcessed ( IsProcessedUtils.isNotSync () );
									DatabaseUtils.getInstance ( getActivity () ).getDaoSession ().getClientAvailabilitiesDao ().update ( clientAvailability );
									// Remove the current client availability from the list
									list.remove ( i );
									// Exit for loop
									break;
								} // End else if
							} // End for each
						} // End else if
				} // End for each
				
				// Commit changes
				DatabaseUtils.getInstance ( getActivity () ).getDaoSession ().getDatabase ().setTransactionSuccessful ();
				
				// Iterate over all the modified slots
				for ( AvailabilityCalendar slot : modifiedSlots )
					// Update the slot
					slot.setPreviouslySelected ( slot.isSelected () );
    		} catch ( Exception exception ) {
				// Indicate that an error occurred
				error = true;
			} finally {
				// End transaction
				DatabaseUtils.getInstance ( getActivity () ).getDaoSession ().getDatabase ().endTransaction ();
			} // End of try-catch-finally block

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
			
			try {
				// Determine if an error occurred
				if ( error )
					// Indicate that an error occurred
					Baguette.showText ( getActivity () ,
							AppResources.getInstance ( getActivity () ).getString ( getActivity () , R.string.client_availability_save_failure_message ) ,
							Baguette.BackgroundColor.RED );
				else {
					// Indicate that the save was successful
					Vibration.vibrate ( getActivity () );
					// Indicate that the save was successful
					Baguette.showText ( getActivity () ,
							AppResources.getInstance ( getActivity () ).getString ( getActivity () , R.string.client_availability_save_success_message ) ,
							Baguette.BackgroundColor.GREEN );
				} // End else
			} catch ( Exception exception ) {
				// The activity ended.
				// Do nothing
			} // End of try-catch block
		}
		
	}
	
}