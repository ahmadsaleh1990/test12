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
import java.util.List;

import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.AppToast;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Vibration;
import me.SyncWise.Android.Database.Clients;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.IsProcessedUtils;
import me.SyncWise.Android.Database.ObjectiveAchievements;
import me.SyncWise.Android.Database.ObjectiveAchievementsDao;
import me.SyncWise.Android.Database.ObjectiveAssignments;
import me.SyncWise.Android.Database.ObjectiveAssignmentsUtils;
import me.SyncWise.Android.Database.ObjectivePriorities;
import me.SyncWise.Android.Database.ObjectivePrioritiesDao;
import me.SyncWise.Android.Database.Objectives;
import me.SyncWise.Android.Database.ObjectivesDao;
import me.SyncWise.Android.Database.ObjectivesUtils;
import me.SyncWise.Android.Database.Reasons;
import me.SyncWise.Android.Database.ReasonsDao;
import me.SyncWise.Android.Database.Visits;
import me.SyncWise.Android.Gson.BaseTimerActivity;
import me.SyncWise.Android.Modules.Call.CallAction;
import me.SyncWise.Android.Widgets.Baguette;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.BufferType;

/**
 * Activity implemented in order to view / edit a client objective
 * 
 * @author Elias
 * @sw.todo	<b>New Client Objective Activity Implementation :</b><br>
 * Simply add this class in the {@code AndroidManifest.xml} file along with the permission :<br>
 * {@code <uses-permission android:name="android.permission.VIBRATE"/>}
 *
 */
public class NewClientObjectiveActivity extends BaseTimerActivity {

	/**
	 * Bundle key used to put/retrieve the content of the current client.
	 */
	public static final String CLIENT = NewClientObjectiveActivity.class.getName () + ".CLIENT";
	
	/**
	 * Bundle key used to put/retrieve the content of the current visit.
	 */
	public static final String VISIT = NewClientObjectiveActivity.class.getName () + ".VISIT";
	
	/**
	 * Bundle key used to put/retrieve the content of the current objective ID.
	 */
	public static final String OBJECTIVE_ID = NewClientObjectiveActivity.class.getName () + ".OBJECTIVE_ID";
	
	/**
	 * Bundle key used to put/retrieve the content of the is view flag.<br>
	 * This flag is used to determine if the user can edit / create objectives or only view the current objective.
	 */
	public static final String IS_VIEW = NewClientObjectiveActivity.class.getName () + ".IS_VIEW";	
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #objective}.
	 */
	private static final String OBJECTIVE = NewClientObjectiveActivity.class.getName () + ".OBJECTIVE";	
	
	/**
	 * Reference to the objective stored in DB (if previously saved)
	 */
	private Objectives objective;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #objectiveAchievement}.
	 */
	private static final String OBJECTIVE_ACHIEVEMENT = NewClientObjectiveActivity.class.getName () + ".OBJECTIVE_ACHIEVEMENT";
	
	/**
	 * Reference to the objective achievement.
	 */
	private ObjectiveAchievements objectiveAchievement;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #reason}.
	 */
	private static final String REASON = NewClientObjectiveActivity.class.getName () + ".REASON";
	
	/**
	 * Reference to the objective reason.
	 */
	private Reasons reason;
	
	/**
	 * Reference to the objective description edit text.
	 */
	private EditText objectiveDescription;
	
	/**
	 * Reference to the spinner holding the objective priorities drop down list.
	 */
	private Spinner prioritiesSpinner;
	
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
		setContentView ( R.layout.new_client_objective_activity );
		
		/*
		 * Check the following :
		 * - If the client object is invalid
		 * - OR :
		 * 		- If the visit object is invalid
		 * 		- AND if the objective ID is invalid
		 * 		  (At least the client object and one of two objects above should be valid)
		 */
		if ( getIntent ().getSerializableExtra ( CLIENT ) == null ) {
			// Indicate that the activity cannot be displayed
			new AppToast ( this ).setIcon ( R.drawable.warning ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.invalid_selection_label ) ).show ();
			// Finish activity
			finish ();
			return;
		} // End if
		
		// Change the title associated with this activity
		setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.new_client_objective_activity_title ) );
		
		// Retrieve a reference to the objective description edit text
		objectiveDescription = (EditText) findViewById ( R.id.edittext_objective );
		// Set the maximum number of allowed characters
		objectiveDescription.setFilters ( new InputFilter [] { new InputFilter.LengthFilter ( ObjectivesUtils.getDescriptionMaxLength () ) } );
		// Display the summary selection label
		( (TextView) findViewById ( R.id.objective_priority_selection_label ) ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.objective_priority_selection_label ) );
		// Display the objective description hint
		objectiveDescription.setHint ( AppResources.getInstance ( this ).getString ( this , R.string.objective_description_hint ) );
		
		// Determine if a saved instance state is provided
		if ( savedInstanceState != null ) {
			// Restore the content provided by the saved instance state
			objective = (Objectives) savedInstanceState.getSerializable ( OBJECTIVE );
			objectiveAchievement = (ObjectiveAchievements) savedInstanceState.getSerializable ( OBJECTIVE_ACHIEVEMENT );
			reason = (Reasons) savedInstanceState.getSerializable ( REASON );
		} // End if
		
		// Retrieve a reference to the spinner
		prioritiesSpinner = (Spinner) findViewById ( R.id.spinner );
		// Retrieve the objective priorities
		List < ObjectivePriorities > objectivePriorities = DatabaseUtils.getInstance ( this ).getDaoSession ().getObjectivePrioritiesDao ().queryBuilder ()
				.orderDesc ( ObjectivePrioritiesDao.Properties.ObjectivePriorityID ).list ();
		// Declare and initialize a new objective priorities adapter used to populate objective priorities drop down list
		ObjectivePrioritiesAdapter prioritiesAdapter = new ObjectivePrioritiesAdapter ( this , android.R.layout.simple_spinner_item , objectivePriorities );
		// Sets the layout resource to create the drop down views
		prioritiesAdapter.setDropDownViewResource ( R.layout.new_client_objective_activity_priority_item );
    	// Set the spinner adapter
		prioritiesSpinner.setAdapter ( prioritiesAdapter );
		
		// Check if the view mode is enabled
		if ( getIntent ().getBooleanExtra ( IS_VIEW , false ) ) {
			// Disable the objective description
			objectiveDescription.setEnabled ( false );
			// Disable the priorities drop down list
			prioritiesSpinner.setEnabled ( false );
		} // End if
		
		// Check if the objective ID is provided
		// AND the objective object is not initialized
		if ( getIntent ().getSerializableExtra ( OBJECTIVE_ID ) != null && objective == null )
			// Populate the objective and its achievement (if any)
			new PopulateObjective ().execute ();
		else
			// Display any additional data if any
			displayAdditionalData ();
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
		// Remove any displayed baguette
		Baguette.remove ( this );
	}
	
    /*
     * Called to retrieve per-instance state from an activity before being killed so that the state can be restored in onCreate(Bundle) or onRestoreInstanceState(Bundle).
     *
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    protected void onSaveInstanceState ( Bundle outState ) {
    	// Superclass method call
    	super.onSaveInstanceState ( outState );
    	// Save the content of objective in the outState bundle
    	outState.putSerializable ( OBJECTIVE , objective );
    	// Save the content of objectiveAchievement in the outState bundle
    	outState.putSerializable ( OBJECTIVE_ACHIEVEMENT , objectiveAchievement );
    	// Save the content of reason in the outState bundle
    	outState.putSerializable ( REASON , reason );
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
			objectiveDescription = null;
			prioritiesSpinner = null;
			objective = null;
			objectiveAchievement = null;
			reason = null;
		} // End if
	}
	
	/**
	 * Indicates whether the client objective has new / unsaved modifications.<br>
	 * This method checks the objective description (edit text) and priority (spinner).
	 * 
	 * @return	Boolean stating if there are new modifications.
	 */
	private boolean hasModifications () {
    	// Determine if the view mode is enabled
    	if ( getIntent ().getBooleanExtra ( IS_VIEW , false ) )
    		// There view mode is enabled, and hence no modifications are allowed
    		return false;
    		
		// Retrieve the objective description
		String objectiveDescription = NewClientObjectiveActivity.this.objectiveDescription.getText ().toString ().trim ();
		// Retrieve the selection objective priority (might be NULL)
		ObjectivePriorities priority = (ObjectivePriorities) prioritiesSpinner.getSelectedItem ();
		// Retrieve the priority ID
		Integer priorityID = priority == null ? null : priority.getObjectivePriorityID ();
		
		// Determine if there a previously saved objective
		if ( objective == null )
			// Check if there is a valid objective description
			return objectiveDescription.isEmpty () ? false : true;
		else {
			// Otherwise, check if the current objective is really modified
			if ( objective.getObjectiveDescription ().equals ( objectiveDescription ) && objective.getObjectivePriorityID () == priorityID )
				// There are no modifications
				return false;
			else
				// There is at least one modification
				return true;
		} // End else
	}
	
	/**
	 * Displays any additional data, including the objective note and / or the objective reason.
	 */
	private void displayAdditionalData () {
		// Declare and initialize a char sequence used to host any additional data
		CharSequence additionalData = null;
		// Compute the brown color
		int brownColor = getResources ().getColor ( R.color.Brown );
		
		// Determine if the objective has a note
		if ( objectiveAchievement != null && ! TextUtils.isEmpty ( objectiveAchievement.getNotes () ) ) {
			// Retrieve the note label
			String noteLabel = AppResources.getInstance ( NewClientObjectiveActivity.this ).getString ( NewClientObjectiveActivity.this , R.string.note_label );
			// Build a spannable string out of the objective note in order to apply various spans
			SpannableString objectiveNote = new SpannableString ( noteLabel + " : " + objectiveAchievement.getNotes () );
			// Apply color span
			objectiveNote.setSpan ( new ForegroundColorSpan ( brownColor ) ,
					noteLabel.length () + " : ".length () ,
					noteLabel.length () + " : ".length () + objectiveAchievement.getNotes ().length () , 0 );
			// Add the objective note as an additional data
			additionalData = additionalData == null ? objectiveNote : TextUtils.concat ( additionalData , "\n" , objectiveNote ) ;
		} // End if
		
		// Determine if the objective has a reason
		if ( reason != null ) {
			// Retrieve the reason label
			String reasonLabel = AppResources.getInstance ( NewClientObjectiveActivity.this ).getString ( NewClientObjectiveActivity.this , R.string.reason_label );
			// Build a spannable string out of the reason name in order to apply various spans
			SpannableString reasonName = new SpannableString ( reasonLabel + " : " + reason.getReasonName () );
			// Apply color span
			reasonName.setSpan ( new ForegroundColorSpan ( brownColor ) ,
					reasonLabel.length () + " : ".length () ,
					reasonLabel.length () + " : ".length () + reason.getReasonName ().length () , 0 );
			// Add the objective note as an additional data
			additionalData = additionalData == null ? reasonName : TextUtils.concat ( additionalData , "\n" , reasonName ) ;
		} // End if
		
		// Check if the additional data is valid
		if ( additionalData != null ) {
			// Retrieve a reference to the additional data label
			TextView additionalDataLabel = (TextView) findViewById ( R.id.label_objective_additional_data );
			// Display the additional data label
			additionalDataLabel.setVisibility ( View.VISIBLE );
			// Display the additional data
			additionalDataLabel.setText ( additionalData , BufferType.SPANNABLE );
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
    	// Determine if the view mode is enabled
    	if ( ! getIntent ().getBooleanExtra ( IS_VIEW , false ) )
	    	// Enable the required menu items
	    	me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_save ) , R.string.save_label );
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
    	if ( menuItem.getItemId () == R.id.action_save ) {
    		// Hide the software keyboard
            ( (InputMethodManager) getBaseContext ().getSystemService ( INPUT_METHOD_SERVICE ) ).hideSoftInputFromWindow ( objectiveDescription.getWindowToken () , 0 );
    		// Retrieve the objective description
    		String objectiveDescription = NewClientObjectiveActivity.this.objectiveDescription.getText ().toString ().trim ();
    		// Check if there is a valid objective description
    		if ( objectiveDescription.isEmpty () ) {
    			// Invalid objective description
    			// Indicate so using Baguette
    			Baguette.showText ( this ,
    					AppResources.getInstance ( this ).getString ( this , R.string.no_client_objective_description_message ) ,
    					Baguette.BackgroundColor.RED );
        		// Consume event
        		return true;
    		} // End if
    		
			// Flag used to indicate whether an error occurred or not
			boolean error = false;
    		// Retrieve system date
    		Calendar today = Calendar.getInstance ();
    		Calendar endDate = Calendar.getInstance ();
    		endDate.add(Calendar.DATE, 100);
    		// Retrieve the selection objective priority (might be NULL)
    		ObjectivePriorities priority = (ObjectivePriorities) prioritiesSpinner.getSelectedItem ();
    		// Retrieve the priority ID
    		Integer priorityID = priority == null ? null : priority.getObjectivePriorityID ();
    		
    		// Otherwise the objective description is valid
    		// Check if the objective object is valid
    		if ( objective != null ) {
    			// Check if the objective source is NOT web
    			if ( objective.getObjectiveSource () == ObjectivesUtils.Source.WEB ) {
        			// Objective cannot be modified
        			// Indicate so using Baguette
        			Baguette.showText ( this ,
        					AppResources.getInstance ( this ).getString ( this , R.string.cannot_edit_web_client_objective_message ) ,
        					Baguette.BackgroundColor.RED );
            		// Consume event
            		return true;
    			} // End if
    			// Check if the objective is finished
    			if ( objectiveAchievement != null ) {
        			// Objective cannot be modified
        			// Indicate so using Baguette
        			Baguette.showText ( this ,
        					AppResources.getInstance ( this ).getString ( this , R.string.cannot_edit_finished_client_objective_message ) ,
        					Baguette.BackgroundColor.RED );
            		// Consume event
            		return true;
    			} // End if
    			// Otherwise, check if the current objective is really modified
    			if ( ! hasModifications () ) {
    				// Indicate that there are no new modifications
    				Baguette.showText ( this ,
    						AppResources.getInstance ( this ).getString ( this , R.string.no_new_modifications_message ) ,
    						Baguette.BackgroundColor.BLUE );
            		// Consume event
            		return true;
    			} // End if
    			
				try {
	    			
	    			// Otherwise update the current objective object
	    			objective.setObjectivePriorityID ( priorityID );
	    			objective.setObjectiveDescription ( objectiveDescription );
	    			objective.setObjectiveAltDescription ( objectiveDescription );
	    			objective.setIsProcessed ( IsProcessedUtils.isNotSync () );
	    			// Update the object in DB
	    			DatabaseUtils.getInstance ( this ).getDaoSession ().getObjectivesDao ().update ( objective );
	    			
					// Indicate that the save was successful
					Vibration.vibrate ( this );
				} catch ( Exception exception ) {
					// Indicate that an error occurred
					error = true;
				} // End try-catch block
    			
    		} // End if
    		else {
    		
        		// Determine if there are modifications
        		if ( ! hasModifications () )
    				// Indicate that there are no new modifications
    				Baguette.showText ( this ,
    						AppResources.getInstance ( this ).getString ( this , R.string.no_new_modifications_message ) ,
    						Baguette.BackgroundColor.BLUE );
    			
    			// This is a NEW objective
	    		// Declare and initialize an objective object
	    		Objectives objective = new Objectives ( null , // ID
	    				ObjectivesUtils.getObjectiveID ( today ) , // ObjectiveID
	    				ObjectivesUtils.Type.CLIENT_OBJECTIVE , // ObjectiveType
	    				ObjectivesUtils.Source.DEVICE , // ObjectiveSource
	    				getIntent ().getSerializableExtra ( VISIT ) == null ?
	    						null :
	    						( (Visits) getIntent ().getSerializableExtra ( VISIT ) ).getVisitID () , // VisitID
	    				priority == null ? null : priority.getObjectivePriorityID () , // ObjectivePriorityID
	    				objectiveDescription , // ObjectiveDescription
	    				objectiveDescription , // ObjectiveAltDescription
	    				today.getTime () , // StartDate
	    				endDate.getTime() , // EndDate
	    				IsProcessedUtils.isNotSync () , // IsProcessed
	    				today.getTime () ); // StampDate
	    		// Declare and initialize an objective assignment object
	    		ObjectiveAssignments objectiveAssignment = new ObjectiveAssignments ( null , // ID
	    				objective.getObjectiveID () , // ObjectiveID
	    				( (Clients) getIntent ().getSerializableExtra ( CLIENT ) ).getClientCode () , // AssignmentCode
	    				ObjectiveAssignmentsUtils.Type.CLIENT , // AssignmentType
	    				DatabaseUtils.getCurrentUserCode ( this ) , // Assignment Source
	    				//TODO: add DivisionCode, and CompanyCode jose13.1
	    				DatabaseUtils.getCurrentDivisionCode(NewClientObjectiveActivity.this) ,//DivisionCode
						DatabaseUtils.getCurrentCompanyCode( NewClientObjectiveActivity.this ), // CompanyCode
	    				IsProcessedUtils.isNotSync () , // IsProcessed
	    				today.getTime () ); // StampDate
				try {
					// Begin transaction
					DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ().beginTransaction ();
					
		    		// Insert into DB the objective
					DatabaseUtils.getInstance ( this ).getDaoSession ().getObjectivesDao ().insert ( objective );
					// Insert into DB the objective assignment
					DatabaseUtils.getInstance ( this ).getDaoSession ().getObjectiveAssignmentsDao ().insert ( objectiveAssignment );
					// Update visit history
//jose13.1					VisitHistoryUpdate.updateStatistics ( this , objectiveAssignment.getAssignmentCode () , VisitHistoryUpdate.Category.OBJECTIVE , VisitHistoryUpdate.UpdateType.INCREASE );
					
					// Commit changes
					DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ().setTransactionSuccessful ();
					
					// Check if the visit object is valid
					if ( getIntent ().getSerializableExtra ( VISIT ) != null )
			    		// Indicate that an objective was successfully created during this visit
			    		CallAction.setCallActionStatus ( this , ( (Visits) getIntent ().getSerializableExtra ( VISIT ) ).getVisitID () , CallAction.ID.OBJECTIVE , true );
					
					// Indicate that the save was successful
					Vibration.vibrate ( this );
				} catch ( Exception exception ) {
					// Indicate that an error occurred
					error = true;
				} finally {
					// End transaction
					DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ().endTransaction ();
				} // End try-catch-finally block
			
    		} // End else
			
        	// Call this to set the result that your activity will return to its caller
        	setResult ( RESULT_OK , new Intent ().putExtra ( ClientObjectiveActivity.DISPLAY_RESULT , true ).putExtra ( ClientObjectiveActivity.SAVE_SUCCESS , ! error ) );
    		// Finish this activity
        	finish ();
			
    		// Consume event
    		return true;
    	} // End if
    	// Allow normal menu processing to proceed
    	return false;
    }
    
	/**
	 * AsyncTask helper class used to populate the client objective (if any).
	 * 
	 * @author Elias
	 *
	 */
	private class PopulateObjective extends AsyncTask < Void , Void , Void > {
		
		/*
		 * Runs on the UI thread before doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute () {
			// Display indeterminate progress dialog
			AppDialog.getInstance ().displayIndeterminateProgress ( NewClientObjectiveActivity.this , null , null );
		}
		
		/*
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Void doInBackground ( Void ... params ) {
			// Retrieve a reference to the objective ID
			Long objectiveID = (Long) getIntent ().getSerializableExtra ( OBJECTIVE_ID );
			// Check if the objective ID is valid
			if ( objectiveID == null )
				// Do nothing
				return null;
			// Search for the corresponding objective
			objective = DatabaseUtils.getInstance ( NewClientObjectiveActivity.this ).getDaoSession ().getObjectivesDao ().queryBuilder ()
					.where ( ObjectivesDao.Properties.ObjectiveID.eq ( objectiveID ) ).unique ();
			// Search for the corresponding objective achievement
			objectiveAchievement = DatabaseUtils.getInstance ( NewClientObjectiveActivity.this ).getDaoSession ().getObjectiveAchievementsDao ().queryBuilder ()
					.where ( ObjectiveAchievementsDao.Properties.ObjectiveID.eq ( objectiveID ) ,
							ObjectiveAchievementsDao.Properties.ClientCode.eq ( ( (Clients) getIntent ().getSerializableExtra ( CLIENT ) ).getClientCode () ) ).unique ();
			// Check if the objective achievement is valid
			// AND if it has a valid reason code
			if ( objectiveAchievement != null && objectiveAchievement.getReasonCode () != null )
				// Retrieve the appropriate objective reason
				reason = DatabaseUtils.getInstance ( NewClientObjectiveActivity.this ).getDaoSession ().getReasonsDao ().queryBuilder ()
						.where ( ReasonsDao.Properties.ReasonCode.eq ( objectiveAchievement.getReasonCode () ) ).unique ();
			return null;
		}
		
		/*
		 * Runs on the UI thread after doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute ( Void arg ) {
			// Dismiss dialog
			AppDialog.getInstance ().dismiss ();
			try {
				// Check if the objective references is valid
				if ( objective != null ) {
					// Display the objective description
					objectiveDescription.setText ( objective.getObjectiveDescription () );
					// Iterate over all objective priorities
					int size = prioritiesSpinner.getCount ();
					for ( int i = 0 ; i < size ; i ++ )
						// Check if the current objective priority belongs to the objective
						if ( ( (ObjectivePriorities) prioritiesSpinner.getItemAtPosition ( i ) ).getObjectivePriorityID ().equals ( objective.getObjectivePriorityID () ) ) {
							// Select the current objective priority
							prioritiesSpinner.setSelection ( i );
							// Exit loop
							break;
						} // End if
				} // End if
				
				// Display any additional data if any
				displayAdditionalData ();
			} catch ( Exception exception ) {
				// Do nothing
			} // End of try-catch block
		}
		
	}
    
}