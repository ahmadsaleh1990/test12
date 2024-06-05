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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

import me.SyncWise.Android.ActivityTransition;
import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.AppToast;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.ClientPropertiesDao;
import me.SyncWise.Android.Database.Clients;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.IsProcessedUtils;
import me.SyncWise.Android.Database.ObjectiveAchievements;
import me.SyncWise.Android.Database.ObjectiveAchievementsDao;
import me.SyncWise.Android.Database.ObjectiveAchievementsUtils;
import me.SyncWise.Android.Database.ObjectiveAssignmentsDao;
import me.SyncWise.Android.Database.ObjectiveAssignmentsUtils;
import me.SyncWise.Android.Database.ObjectivePrioritiesDao;
import me.SyncWise.Android.Database.ObjectivesDao;
import me.SyncWise.Android.Database.ObjectivesUtils;
import me.SyncWise.Android.Database.PermissionsUtils;
import me.SyncWise.Android.Database.ReasonsDao;
import me.SyncWise.Android.Database.ReasonsUtils;
import me.SyncWise.Android.Database.Visits;
import me.SyncWise.Android.Gson.BaseTimerListActivity;
import me.SyncWise.Android.Modules.AutoLogOff.AutoLogOffUtils;
import me.SyncWise.Android.Modules.Call.CallAction;
import me.SyncWise.Android.Modules.Call.CallMenuActivity;
import me.SyncWise.Android.Modules.Objective.ClientObjectiveAdapter.ViewHolder;
import me.SyncWise.Android.Modules.Reason.ReasonActivity;
import me.SyncWise.Android.Modules.SalesOrder.SalesOrderActivity;
import me.SyncWise.Android.SectionsList.MultipleListAdapter;
import me.SyncWise.Android.SectionsList.Section;
import me.SyncWise.Android.Utilities.ClientCard;
import me.SyncWise.Android.Utilities.EmptyListAdapter;
import me.SyncWise.Android.Widgets.Baguette;
import me.SyncWise.Android.Widgets.QuickAction.ActionItem;
import me.SyncWise.Android.Widgets.QuickAction.QuickAction;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

/**
 * Activity implemented in order to display and interact with the client's objectives.
 * 
 * @author Elias
 * @sw.todo	<b>Client Objective Activity Implementation :</b><br>
 * Simply add this class in the {@code AndroidManifest.xml} file.
 *
 */
public class ClientObjectiveActivity extends BaseTimerListActivity implements QuickAction.OnActionItemClickListener {

	/**
	 * Bundle key used to put/retrieve the content of the current client.
	 */
	public static final String CLIENT = ClientObjectiveActivity.class.getName () + ".CLIENT";
	public static final String ViewComplete = ClientObjectiveActivity.class.getName () + ".ViewComplete";
	/**
	 * Bundle key used to put/retrieve the content of the current visit.
	 */
	public static final String VISIT = ClientObjectiveActivity.class.getName () + ".VISIT";
	
	/**
	 * Bundle key used to put/retrieve the content of the is view flag.<br>
	 * This flag is used to determine if the user can edit / create objectives or only view them.
	 */
	public static final String IS_VIEW = ClientObjectiveActivity.class.getName () + ".IS_VIEW";
	
	/**
	 * Bundle key used to put/retrieve the content of the is view or add flag.<br>
	 * This flag is used to determine if the user can edit / create objectives or only view them.
	 */
	public static final String IS_VIEW_OR_ADD = ClientObjectiveActivity.class.getName () + ".IS_VIEW_OR_ADD";
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #completeObjective}.
	 */
	public static final String COMPLETE_OBJECTIVE = ClientObjectiveActivity.class.getName () + ".COMPLETE_OBJECTIVE";
	
	/**
	 * Saved state of the objective's note for the completion phase.
	 */
	private ObjectiveNote completeObjective;
	
	/**
	 * Reference to the objective confirmation dialog, used to provide an optional note and complete an objective.
	 */
	private Dialog objectiveConfirmationDialog;
	
	/**
	 * Constant integer holding the request code used to start an activity for result to create a new objective.
	 */
	private static final int REQUEST_CODE_NEW = 1;
	
	/**
	 * Constant integer holding the request code used to start an activity for result to cancel an existing objective.
	 */
	private static final int REQUEST_CODE_CANCEL = 2;
	
	/**
	 * Bundle key used to put/retrieve the content of the save success flag.<br>
	 * This flag is used to determine if the user successfully saved a new client objective.
	 */
	public static final String SAVE_SUCCESS = ClientObjectiveActivity.class.getName () + ".SAVE_SUCCESS";
	
	/**
	 * Bundle key used to put/retrieve the content of the display result flag.<br>
	 * This flag is used to determine if the result retrieved from the child activity should be displayed or not.
	 */
	public static final String DISPLAY_RESULT = NewClientObjectiveActivity.class.getName () + ".DISPLAY_RESULT";
	
	/**
	 * Reference to a {@link me.SyncWise.Android.Widgets.QuickAction.QuickAction QuickAction} used in this activity.<br>
	 * This widget is used for pending objectives with no status updates.
	 */
	private QuickAction quickAction_editOnly;
	
	/**
	 * Reference to a {@link me.SyncWise.Android.Widgets.QuickAction.QuickAction QuickAction} used in this activity.<br>
	 * This widget is used for pending objectives.
	 */
	private QuickAction quickAction_pending;
	
	/**
	 * Reference to a {@link me.SyncWise.Android.Widgets.QuickAction.QuickAction QuickAction} used in this activity.<br>
	 * This widget is used for pending objectives that are final (meaning that they cannot be modified because they have been defined from the BE).
	 */
	private QuickAction quickAction_pendingFinal;
	
	private Boolean viewComplete;
	/**
	 * Reference to a {@link me.SyncWise.Android.Widgets.QuickAction.QuickAction QuickAction} used in this activity.<br>
	 * This widget is used for finished objectives.
	 */
	private QuickAction quickAction_finished;
	
	/**
	 * Helper Class used to manage the activity's action items IDs for the quick action widgets.<br>
	 * The main purpose of this class is to maintain a unique identifier for action items within the activity.
	 * 
	 * @author Elias
	 *
	 */
	protected class ActionItemID {
		public static final int EDIT = 0;
		public static final int VIEW = 1;
		public static final int COMPLETE = 2;
		public static final int CANCEL = 3;
	}
	
	/**
	 * Performs all necessary setup in order to properly display the quick action widget.
	 */
    private void setupQuickAction () {
		// Initialize the quick action widgets
    	quickAction_editOnly = new QuickAction ( this , QuickAction.VERTICAL );
		quickAction_pending = new QuickAction ( this , QuickAction.VERTICAL );
		quickAction_pendingFinal = new QuickAction ( this , QuickAction.VERTICAL );
		quickAction_finished = new QuickAction ( this , QuickAction.VERTICAL );
		// Action Item : Edit
		ActionItem edit = new ActionItem ( ActionItemID.EDIT ,
				AppResources.getInstance ( this ).getString ( this , R.string.edit_label ) ,
				getResources ().getDrawable ( R.drawable.quick_action_edit ) );
		// Action Item : View
		ActionItem view = new ActionItem ( ActionItemID.VIEW ,
				AppResources.getInstance ( this ).getString ( this , R.string.view_label ) ,
				getResources ().getDrawable ( R.drawable.quick_action_search ) );
		// Action Item : Complete
		ActionItem complete = new ActionItem ( ActionItemID.COMPLETE ,
				AppResources.getInstance ( this ).getString ( this , R.string.complete_label ) ,
				getResources ().getDrawable ( R.drawable.quick_action_ok ) );
		// Action Item : Cancel
		ActionItem cancel = new ActionItem ( ActionItemID.CANCEL ,
				AppResources.getInstance ( this ).getString ( this , R.string.cancel_label ) ,
				getResources ().getDrawable ( R.drawable.quick_action_cancel ) );
    	// Populate the quick action widget with quick action items
		quickAction_editOnly.addActionItem ( edit );
		quickAction_pending.addActionItem ( edit );
		quickAction_pending.addActionItem ( complete );
		quickAction_pending.addActionItem ( cancel );
		quickAction_pendingFinal.addActionItem ( view );
		quickAction_pendingFinal.addActionItem ( complete );
		quickAction_pendingFinal.addActionItem ( cancel );
		quickAction_finished.addActionItem ( view );
		// Assign an action item click listener
		quickAction_editOnly.setOnActionItemClickListener ( this );
		quickAction_pending.setOnActionItemClickListener ( this );
		quickAction_pendingFinal.setOnActionItemClickListener ( this );
		quickAction_finished.setOnActionItemClickListener ( this );
    }
	
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
		setContentView ( R.layout.client_objective_activity );
		
		// Check if both the visit and the client are valid
		if ( getIntent ().getSerializableExtra ( CLIENT ) == null 
				|| ( getIntent ().getSerializableExtra ( VISIT ) == null && ! getIntent ().getBooleanExtra ( IS_VIEW , false ) && ! getIntent ().getBooleanExtra ( IS_VIEW_OR_ADD , false ) ) ) {
			// Indicate that the activity cannot be displayed
			new AppToast ( this ).setIcon ( R.drawable.warning ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.invalid_selection_label ) ).show ();
			// Finish activity
			finish ();
			return;
		} // End if
		viewComplete=	getIntent().getBooleanExtra(ViewComplete, false);
		// Determine if a saved instance state is provided
		if ( savedInstanceState != null ) {
			// Restore the content provided by the saved instance state
			completeObjective = (ObjectiveNote) savedInstanceState.getSerializable ( COMPLETE_OBJECTIVE );
		} // End if
		
		// Change the title associated with this activity
		setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.client_objective_activity_title ) );
		// Initialize the client card
		ClientCard.initializeClientCard ( this , (Clients) getIntent ().getSerializableExtra ( CLIENT ) );
        // Perform the quick action setup
        setupQuickAction ();
        // Retrieve all the client objectives asynchronously
        new PopulateList ().execute ();
	}
	
	/*
	 * Called after onRestoreInstanceState(Bundle), onRestart(), or onPause(), for your activity to start interacting with the user.
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	public void onResume () {
		// Superclass method call
		super.onResume ();
		// Determine if an objective completion dialog should be displayed
		if ( completeObjective != null )
			// Display a completion confirmation dialog
			displayObjectiveCompletionConfirmationDialog ( completeObjective.getObjectiveID () , completeObjective.getNote () , completeObjective.getSelectionStart () , completeObjective.getSelectionEnd () );
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
		// Determine if the completion confirmation dialog is displayed
		if ( objectiveConfirmationDialog != null ) {
			// Retrieve a reference to the objective note
			EditText editText = (EditText) objectiveConfirmationDialog.findViewById ( R.id.edittext_objective_notes );
			// Initialize an objective note
			completeObjective = new ObjectiveNote ( (Long) editText.getTag () , editText.getText ().toString () , editText.getSelectionStart () , editText.getSelectionEnd () );
			// Remove the completion confirmation
			objectiveConfirmationDialog.dismiss ();
		} // End if
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
    	// Save the content of completeObjective in the outState bundle
    	outState.putSerializable ( COMPLETE_OBJECTIVE , completeObjective );
    }
	
	/*
	 * Called when the activity has detected the user's press of the back key. 
	 *
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed () {
    	// Call this to set the result that your activity will return to its caller
    	setResult ( RESULT_OK , new Intent ().putExtra ( CallMenuActivity.SKIP_ACTION_RESULT , true ) );
    	// Superclass method call
    	super.onBackPressed ();
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
			quickAction_finished = null;
			quickAction_pendingFinal = null;
			quickAction_pending = null;
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
    	// Determine if the is view flag is set
    	if ( ! getIntent ().getBooleanExtra ( IS_VIEW , false ) )
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
    		Intent intent = new Intent ( this , NewClientObjectiveActivity.class );
    		// Add the client object to the intent
    		intent.putExtra ( NewClientObjectiveActivity.CLIENT , getIntent ().getSerializableExtra ( CLIENT ) );
    		// Add the visit object to the intent
    		intent.putExtra ( NewClientObjectiveActivity.VISIT , getIntent ().getSerializableExtra ( VISIT ) );
    		// Launch an activity for which you would like a result when it finished
    		startActivityForResult ( intent , REQUEST_CODE_NEW );
    		// Consume event
    		return true;
    	} // End if
    	// Allow normal menu processing to proceed
    	return false;
    }
    
	/*
	 * This method will be called when an item in the list is selected.
	 *
	 * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	@Override
	protected void onListItemClick ( ListView listView , View view , int position , long id ) {
		// Retrieve a reference to the view holder
		ViewHolder viewHolder = (ViewHolder) view.getTag ();
		
		// Check if the is view flag is set
		if ( getIntent ().getBooleanExtra ( IS_VIEW , false ) ) {
			// Consider all objective as final
			// Display the appropriate quick action widget
			quickAction_finished.show ( view , viewHolder.objectiveID , getResources () );
			// Exit method
			return;
		} // End if

		// Determine if the objective is pending or not, and if it is final or not
		if ( viewHolder.isPending && viewHolder.isFinal ) {
			// The objective is pending and is final
			// Determine if status updates are allowed
			if ( getIntent ().getBooleanExtra ( IS_VIEW_OR_ADD , false ) )
				// Display the appropriate quick action widget
				quickAction_finished.show ( view , viewHolder.objectiveID , getResources () );
			else
				// Display the appropriate quick action widget
				quickAction_pendingFinal.show ( view , viewHolder.objectiveID , getResources () );
		} // End if
		else if ( viewHolder.isPending ) {
			// The objective is pending and is NOT final
			// Determine if status updates are allowed
			if ( getIntent ().getBooleanExtra ( IS_VIEW_OR_ADD , false ) )
				// Display the appropriate quick action widget
				quickAction_editOnly.show ( view , viewHolder.objectiveID , getResources () );
			else
				// Display the appropriate quick action widget
				quickAction_pending.show ( view , viewHolder.objectiveID , getResources () );
		} // End else if
		else
			// The objective is NOT pending
			// Display the appropriate quick action widget
			quickAction_finished.show ( view , viewHolder.objectiveID , getResources () );
	}
	
	/*
	 * Callback method to be invoked when an action item in this QuickAction has been clicked.
	 *
	 * @see me.SyncWise.Android.Widgets.QuickAction.QuickAction.OnActionItemClickListener#onItemClick(me.SyncWise.Android.Widgets.QuickAction.QuickAction, int, int)
	 */
	@Override
	public void onItemClick ( QuickAction source , View anchor , Object object , int pos , int actionId ) {
		// Create a new intent to start a new activity
		Intent intent = null;
		// Determine the clicked action item
		switch ( actionId ) {
		case ActionItemID.VIEW:
			// Create a new intent to start a new activity
			intent = new Intent ( this , NewClientObjectiveActivity.class );
			// Add the client object to the intent
			intent.putExtra ( NewClientObjectiveActivity.CLIENT , getIntent ().getSerializableExtra ( CLIENT ) );
			// Add the objective ID to the intent
			intent.putExtra ( NewClientObjectiveActivity.OBJECTIVE_ID , (Long) object );
			// Add the is view flag to the intent
			intent.putExtra ( NewClientObjectiveActivity.IS_VIEW , true );
			// Start the new activity
			startActivity ( intent );
			// Specify an explicit transition animation to perform next
			ActivityTransition.SlideOutLeft ( this );
			break;
		case ActionItemID.EDIT:
			// Create a new intent to start a new activity
			intent = new Intent ( this , NewClientObjectiveActivity.class );
			// Add the client object to the intent
			intent.putExtra ( NewClientObjectiveActivity.CLIENT , getIntent ().getSerializableExtra ( CLIENT ) );
			// Add the objective ID to the intent
			intent.putExtra ( NewClientObjectiveActivity.OBJECTIVE_ID , (Long) object );
			// Start the new activity
			startActivityForResult ( intent , REQUEST_CODE_NEW );
			// Specify an explicit transition animation to perform next
			ActivityTransition.SlideOutLeft ( this );
			break;
		case ActionItemID.COMPLETE:
			// Retrieve the objective ID
			final Long objectiveID = (Long) object;
			// Display a completion confirmation dialog
			displayObjectiveCompletionConfirmationDialog ( objectiveID , null , 0 , 0 );
			break;
		case ActionItemID.CANCEL:
			// Prompt the user for a client objective cancellation reason
			// Create a new intent to start a new activity
			intent = new Intent ( this , ReasonActivity.class );
			// Add the client object to the intent
			intent.putExtra ( ReasonActivity.CLIENT , getIntent ().getSerializableExtra ( CLIENT ) );
			// Add the reason type to the intent
			intent.putExtra ( ReasonActivity.REASON_TYPES , new String [] { ReasonsUtils.Type.CANCEL_CLIENT_OBJECTIVE } );
			// Add the activity sub title to the intent
			intent.putExtra ( ReasonActivity.ACTIVITY_SUB_TITLE , new String [] { AppResources.getInstance ( this ).getString ( this , R.string.client_objective_cancellation_label ) } );
			// Add the objective ID as extra data to the intent
			intent.putExtra ( ReasonActivity.EXTRA_DATA , (Long) object );
			// Start the new activity
			startActivityForResult ( intent , REQUEST_CODE_CANCEL );
			// Specify an explicit transition animation to perform next
			ActivityTransition.SlideOutLeft ( this );
			break;
		} // End of switch
	}
	
	/**
	 * Displays a confirmation dialog used to provide an optional note in order to complete  
	 * 
	 * @param objectiveID	Long hosting the objective ID.
	 * @param note	String hosting the objective note, or {@code NULL} if none.
	 * @param selectionStart	Integer holding the start selection index of the objective's note.
	 * @param selectionEnd	Integer holding the end selection index of the objective's note.
	 */
	private void displayObjectiveCompletionConfirmationDialog ( final long objectiveID , final String note , final int selectionStart , final int selectionEnd ) {
		// Declare and initialize a dialog click listener
		DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener () {
			@Override
			public void onClick ( DialogInterface dialog , int which ) {
				// Clear objective saved state
				completeObjective = null;
				// Determine the clicked button
				switch ( which ) {
				case DialogInterface.BUTTON_POSITIVE:
					// Retrieve system date
					Calendar today = Calendar.getInstance ();
					// Declare and initialize an objective achievement object
					ObjectiveAchievements objectiveAchievement = new ObjectiveAchievements ( null , // ID
							objectiveID , // ObjectiveID
							ObjectiveAchievementsUtils.Type.CLIENT , // AchievementType
							DatabaseUtils.getCurrentUserCode ( ClientObjectiveActivity.this ) , // UserCode
							( (Clients) getIntent ().getSerializableExtra ( CLIENT ) ).getClientCode () , // ClientCode
							//TODO: add DivisionCode, and CompanyCode jose13.1
							DatabaseUtils.getCurrentDivisionCode(ClientObjectiveActivity.this) ,//DivisionCode
							DatabaseUtils.getCurrentCompanyCode( ClientObjectiveActivity.this ) ,//CompanyCode
							( (Visits) getIntent ().getSerializableExtra ( VISIT ) ).getVisitID () , // VisitID
							ObjectiveAchievementsUtils.Status.COMPLETED , // ObjectiveAchievement
							today.getTime () , // AchievementDate
							null , // ReasonCode
							( (EditText) ( (Dialog) dialog ).findViewById ( R.id.edittext_objective_notes ) ).getText ().toString ().trim () , // Notes
							IsProcessedUtils.isNotSync () , // IsProcessed
							today.getTime () ); // StampDate
					// Insert the objective achievement into DB
					DatabaseUtils.getInstance ( ClientObjectiveActivity.this ).getDaoSession ().getObjectiveAchievementsDao ().insert ( objectiveAchievement );
					// Update visit history
				//VisitHistoryUpdate.updateStatistics ( ClientObjectiveActivity.this , objectiveAchievement.getClientCode () , VisitHistoryUpdate.Category.OBJECTIVE , VisitHistoryUpdate.UpdateType.INCREASE );
		    		// Indicate that an objective achievement was successfully created during this visit
		    		CallAction.setCallActionStatus ( ClientObjectiveActivity.this , ( (Visits) getIntent ().getSerializableExtra ( VISIT ) ).getVisitID () , CallAction.ID.OBJECTIVE , true );
			    	// Refresh list
			    	new PopulateList ().execute ();
					// Display baguette message
					Baguette.showText ( ClientObjectiveActivity.this ,
							AppResources.getInstance ( ClientObjectiveActivity.this ).getString ( ClientObjectiveActivity.this , R.string.client_objective_completed_successfully_message ) ,
							Baguette.BackgroundColor.GREEN );
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					// Dismiss dialog
					objectiveConfirmationDialog.dismiss ();
					break;
				} // End switch
				// Clear dialog reference
				objectiveConfirmationDialog = null;
			}
		};
		
		// Declare and initialize an alert dialog builder
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder ( this );
		// Inflate the dialog view
		View view = getLayoutInflater ().inflate ( R.layout.client_objective_dialog_confirmation , null );
		// Set the dialog view
		alertDialogBuilder.setView ( view );
		// By default, the alert dialog cannot be cancelled UNLESS it has no buttons
		alertDialogBuilder.setCancelable ( false );
		// Set the description
		alertDialogBuilder.setMessage ( AppResources.getInstance ( ClientObjectiveActivity.this )
				.getString ( ClientObjectiveActivity.this , R.string.complete_client_objective_confirmation ) );
		// Map the positive and negative buttons
		alertDialogBuilder.setPositiveButton ( AppResources.getInstance ( this ).getString ( this , R.string.yes_label ) , onClickListener );
		alertDialogBuilder.setNegativeButton ( AppResources.getInstance ( this ).getString ( this , R.string.no_label ) , onClickListener );
		
		// Retrieve a reference to the edit text
		EditText editText = (EditText) view.findViewById ( R.id.edittext_objective_notes );
		// Set the objective ID as tag
		editText.setTag ( objectiveID );
		// Set the maximum number of allowed characters
		editText.setFilters ( new InputFilter [] { new InputFilter.LengthFilter ( ObjectiveAchievementsUtils.getNoteMaxLength () ) } );
		// Display the objective note hint
		editText.setHint ( AppResources.getInstance ( this ).getString ( this , R.string.objective_note_hint ) );
		// Determine if a note is provided
		if ( note != null ) {
			// Set the objective note
			editText.setText ( note );
			// Set the text selection
			editText.setSelection ( selectionStart , selectionEnd );
		} // End if
		
		// Create and store the alert dialog
		objectiveConfirmationDialog = alertDialogBuilder.create ();
		// Display the alert dialog
		objectiveConfirmationDialog.show ();
		
		// Determine if the completion note is enforced
		if ( PermissionsUtils.getEnforceObjectiveCompletionNote ( this , DatabaseUtils.getCurrentUserCode ( this ) , DatabaseUtils.getCurrentCompanyCode( this ) ) ) {
			// Check if the current note is empty
			if ( editText.getText ().toString ().trim ().isEmpty () )
				// Disable the positive button
				( (AlertDialog) objectiveConfirmationDialog) .getButton ( AlertDialog.BUTTON_POSITIVE ).setEnabled ( false );
			// Add a TextWatcher in order to modify the enabled state of the positive button accordingly
			editText.addTextChangedListener ( new TextWatcher () {
				
				/*
				 * Method called to notify that the text has been changed.
				 * 
				 * @see android.text.TextWatcher#onTextChanged(CharSequence, int, int, int)
				 */
				@Override
				public void onTextChanged ( CharSequence s , int start , int before , int count ) {
					// Trim the note
					String note = s.toString ().trim ();
					// Retrieve a reference to the positive button
					Button positiveButton = ( (AlertDialog) objectiveConfirmationDialog) .getButton ( AlertDialog.BUTTON_POSITIVE );
					// Check if the note is empty AND the button is enabled
					if ( note.isEmpty () && positiveButton.isEnabled () )
						// Disable the positive button
						positiveButton.setEnabled ( false );
					// Otherwise check if the note is NOT empty AND the button is disabled
					else if ( ! note.isEmpty () && ! positiveButton.isEnabled () )
						// Enable the positive button
						positiveButton.setEnabled ( true );
				}
				
				/*
				 * Method called to notify after the text is changed.
				 * 
				 * @see android.text.TextWatcher#beforeTextChanged(CharSequence, int, int, int)
				 */
				@Override
				public void beforeTextChanged ( CharSequence s , int start , int count , int after ) {
					// Do nothing
				}
				
				/*
				 * Method called to notify before the text is changed.
				 * 
				 * @see android.text.TextWatcher#afterTextChanged(Editable)
				 */
				@Override
				public void afterTextChanged ( Editable s ) {
					// Do nothing
				}
			} );
		} // End if
	}
	
	/*
	 * Called when an activity you launched exits, giving you the requestCode you started it with, the resultCode it returned, and any additional data from it.
	 *
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	protected void onActivityResult ( int requestCode , int resultCode , Intent data ) {
    	// Check if the result is successful and the intent is valid
    	if ( resultCode != RESULT_OK || data == null )
    		// Exit method
    		return;
    	
    	// Determine the request code
    	switch ( requestCode ) {
		case REQUEST_CODE_NEW:
	    	// Determine if the result should be displayed
	    	if ( data.getBooleanExtra ( DISPLAY_RESULT , false ) ) {
				// Display baguette message
				Baguette.showText ( this ,
						AppResources.getInstance ( this ).getString ( this ,
						data.getBooleanExtra ( SAVE_SUCCESS , false ) ? R.string.client_objective_save_success_message : R.string.client_objective_save_failure_message ) ,
						data.getBooleanExtra ( SAVE_SUCCESS , false ) ? Baguette.BackgroundColor.GREEN : Baguette.BackgroundColor.RED );
	    	} // End if
	    	// Refresh list
	    	new PopulateList ().execute ();
			break;
		case REQUEST_CODE_CANCEL:
			// Retrieve system date
			Calendar today = Calendar.getInstance ();
			// Retrieve the objective ID
			Long objectiveID = (Long) data.getSerializableExtra ( ReasonActivity.EXTRA_DATA );
			// Retrieve the reason code
			String reasonCode = data.getStringArrayExtra ( ReasonActivity.REASON_CODES ) [ 0 ];
			// Declare and initialize an objective achievement object
			ObjectiveAchievements objectiveAchievement = new ObjectiveAchievements ( null , // ID
					objectiveID , // ObjectiveID
					ObjectiveAchievementsUtils.Type.CLIENT , // AchievementType
					DatabaseUtils.getCurrentUserCode ( this ) , // UserCode
					( (Clients) getIntent ().getSerializableExtra ( CLIENT ) ).getClientCode () , // ClientCode
					//TODO: add DivisionCode, and CompanyCode jose13.1
					DatabaseUtils.getCurrentDivisionCode(ClientObjectiveActivity.this) ,//DivisionCode
					DatabaseUtils.getCurrentCompanyCode( ClientObjectiveActivity.this ) ,//CompanyCode
					( (Visits) getIntent ().getSerializableExtra ( VISIT ) ).getVisitID () , // VisitID
					ObjectiveAchievementsUtils.Status.CANCELLED , // ObjectiveAchievement
					today.getTime () , // AchievementDate
					reasonCode , // ReasonCode
					null , // Notes
					IsProcessedUtils.isNotSync () , // IsProcessed
					today.getTime () ); // StampDate
			// Insert the objective achievement into DB
			DatabaseUtils.getInstance ( this ).getDaoSession ().getObjectiveAchievementsDao ().insert ( objectiveAchievement );
    		// Indicate that an objective achievement was successfully created during this visit
    		CallAction.setCallActionStatus ( this , ( (Visits) getIntent ().getSerializableExtra ( VISIT ) ).getVisitID () , CallAction.ID.OBJECTIVE , true );
	    	// Refresh list
	    	new PopulateList ().execute ();
			// Display baguette message
			Baguette.showText ( this ,
					AppResources.getInstance ( this ).getString ( this , R.string.client_objective_cancelled_successfully_message ) ,
					Baguette.BackgroundColor.GREEN );
			break;
		} // End switch
	}
	
	/**
	 * AsyncTask helper class used to populate the client objectives list.
	 * 
	 * @author Elias
	 *
	 */
	private class PopulateList extends AsyncTask < Void , Void , LinkedHashMap < Section , Cursor > > {
		
		/**
		 * String used to host the empty list view label.
		 */
		private String emptyLabel;
		
		/*
		 * Runs on the UI thread before doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute () {
			// Display indeterminate progress dialog
			AppDialog.getInstance ().displayIndeterminateProgress ( ClientObjectiveActivity.this , null , null );
		}
		
		/*
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected LinkedHashMap < Section , Cursor > doInBackground ( Void ... params ) {
			try {
				// Initialize the empty label
				emptyLabel = AppResources.getInstance ( ClientObjectiveActivity.this ).getString ( ClientObjectiveActivity.this , R.string.empty_client_objectives_list_label );
				// Retrieve the current date in the UNIX epoch format
				long today = Calendar.getInstance ().getTimeInMillis ();
				
				// Compute the SQL string
				String SQL = "SELECT DISTINCT O.* , OP." + ObjectivePrioritiesDao.Properties.ObjectivePriorityDescription.columnName + " , " +
						"OAC." + ObjectiveAchievementsDao.Properties.AchievementDate.columnName + " , " +
						"OAC." + ObjectiveAchievementsDao.Properties.Notes.columnName + " , " +
						"OAC." + ObjectiveAchievementsDao.Properties.ReasonCode.columnName + " , " +
						"R." + ReasonsDao.Properties.ReasonName.columnName + " " +
				"FROM ( ( ( " + ObjectivesDao.TABLENAME + " O LEFT JOIN " + ObjectivePrioritiesDao.TABLENAME + " OP ON " +
						"O." + ObjectivesDao.Properties.ObjectivePriorityID.columnName + " = OP." + ObjectivePrioritiesDao.Properties.ObjectivePriorityID.columnName + " ) " +
						"INNER JOIN " + ObjectiveAssignmentsDao.TABLENAME + " OAS ON " +
						"O." + ObjectivesDao.Properties.ObjectiveID.columnName + " = OAS." + ObjectiveAssignmentsDao.Properties.ObjectiveID.columnName + " " + 
						"AND ( OAS." + ObjectiveAssignmentsDao.Properties.AssignmentType.columnName + " = ? OR OAS." + ObjectiveAssignmentsDao.Properties.AssignmentType.columnName + " = ? " +
						"OR ( OAS." + ObjectiveAssignmentsDao.Properties.AssignmentType.columnName + " = ? AND OAS." + ObjectiveAssignmentsDao.Properties.AssignmentCode.columnName + " = ? ) " +
						"OR ( OAS." + ObjectiveAssignmentsDao.Properties.AssignmentType.columnName + " = ? AND OAS." + ObjectiveAssignmentsDao.Properties.AssignmentCode.columnName + " IN ( " +
						"SELECT CP." + ClientPropertiesDao.Properties.ClientPropertyValueCode.columnName + " FROM " + ClientPropertiesDao.TABLENAME + " CP WHERE CP." + ClientPropertiesDao.Properties.ClientCode.columnName + " = ? ) ) ) ) " +
						"LEFT JOIN " + ObjectiveAchievementsDao.TABLENAME + " OAC ON " +
						"O." + ObjectivesDao.Properties.ObjectiveID.columnName + " = OAC." + ObjectiveAchievementsDao.Properties.ObjectiveID.columnName + " " +
						"AND OAC." + ObjectiveAchievementsDao.Properties.AchievementType.columnName + " = ? " +
						"AND OAC." + ObjectiveAchievementsDao.Properties.ClientCode.columnName + " = ? ) " +
						"LEFT JOIN " + ReasonsDao.TABLENAME + " R ON " +
						"OAC." + ObjectiveAchievementsDao.Properties.ReasonCode.columnName + " = R." + ReasonsDao.Properties.ReasonCode.columnName + " " +
						"WHERE O." + ObjectivesDao.Properties.ObjectiveType.columnName + " = ? " +
						"AND ( ( " + ObjectivesDao.Properties.StartDate.columnName + " IS NULL OR ? > " + ObjectivesDao.Properties.StartDate.columnName + " ) " +
						"AND ( " + ObjectivesDao.Properties.EndDate.columnName + " IS NULL OR ? < " + ObjectivesDao.Properties.EndDate.columnName + " ) )" ;
				
				// Compute the 'order by' statement
				String orderBy = " ORDER BY O." + ObjectivesDao.Properties.ObjectivePriorityID.columnName + " DESC , O." + ObjectivesDao.Properties.StampDate.columnName + " DESC";
		
				// Compute the selection arguments
				String selectionArguments [] = new String [] {
						String.valueOf ( ObjectiveAssignmentsUtils.Type.USER ) ,
						String.valueOf ( ObjectiveAssignmentsUtils.Type.GROUP_USERS ) ,
						String.valueOf ( ObjectiveAssignmentsUtils.Type.CLIENT ) ,
						( (Clients) getIntent ().getSerializableExtra ( CLIENT ) ).getClientCode () ,
						String.valueOf ( ObjectiveAssignmentsUtils.Type.CLIENT_PROPERTIES ) ,
						( (Clients) getIntent ().getSerializableExtra ( CLIENT ) ).getClientCode () ,
						String.valueOf ( ObjectiveAchievementsUtils.Type.CLIENT ) ,
						( (Clients) getIntent ().getSerializableExtra ( CLIENT ) ).getClientCode () ,
						String.valueOf ( ObjectivesUtils.Type.CLIENT_OBJECTIVE ) ,
						String.valueOf ( today ) ,
						String.valueOf ( today )
				};
				
				
				
				/*
				 * Declare and initialize a map of cursors.
				 * Mainly, this map is used to host 3 cursors, one for each objective status :
				 * - Pending
				 * - Completed
				 * - Cancelled
				 */
				LinkedHashMap < Section , Cursor > cursors = new LinkedHashMap < Section , Cursor > ();
				Cursor cursor = null;
			
				
			
				
				
				cursor = DatabaseUtils.getInstance ( ClientObjectiveActivity.this ).getDaoSession ().getDatabase ().rawQuery (
						SQL + " AND OAC." + ObjectiveAchievementsDao.Properties.AchievementDate.columnName + " IS NULL" + orderBy ,
						selectionArguments );
				// Check if the cursor is empty
//				if ( cursor.getCount () == 0 ) {
//					// Close the cursor
//					cursor.close ();
//					cursor = null;
//				} // End if 
				
				Calendar today1 = Calendar.getInstance ();
				 
				Double endDate = PermissionsUtils.getForceObjectiveEnd ( ClientObjectiveActivity.this ,   DatabaseUtils.getCurrentUserCode ( ClientObjectiveActivity.this ) ,  DatabaseUtils.getCurrentCompanyCode(ClientObjectiveActivity.this) )  ;
	    			
				 
				ArrayList < Long > objectiveID = new ArrayList < Long > ();
				if(cursor!=null){
				// Move the cursor to the first raw
				if ( cursor.moveToFirst () ) {
					// Iterate over all raws
					do {
					  Long ID =cursor.getLong ( cursor.getColumnIndex ( ObjectivesDao.Properties.ObjectiveID.columnName ) ); //cursor.getLong( 0) ;
					  Long date=cursor.getLong ( cursor.getColumnIndex ( ObjectivesDao.Properties.StartDate.columnName )) ;
					  Calendar c = Calendar.getInstance ();
					  c.setTimeInMillis(date); 
				//	  Date date1 = new Date(date);
					 // int d = calculateDifference( today1 , c.getTime() ) ;
					//   if( calculateDifference( today1 , c.getTime() ) > endDate )
			//		  int d =calculateDifference(today1,c.getTime() );// daysBetween( today1 , c ) ;
					   if( calculateDifference( today1 , c.getTime()   ) > endDate )
					  
					  objectiveID.add(ID); 
											 
					} while ( cursor.moveToNext () );
				} // End if
				// Close the cursor
				}
				cursor.close ();
				cursor = null;
				
				String obid ="";
				for(Long l : objectiveID)
					obid=l + ",";
				if(obid != "")
				{
			      obid = obid.substring(0,obid.length() - 1);
			
				  obid=" AND  O." + ObjectiveAchievementsDao.Properties.ObjectiveID.columnName+" NOT IN ( "+obid+" ) ";
				}
				// Query DB in order to retrieve the Pending objectives
				cursor = DatabaseUtils.getInstance ( ClientObjectiveActivity.this ).getDaoSession ().getDatabase ().rawQuery (
						SQL + " AND OAC." + ObjectiveAchievementsDao.Properties.AchievementDate.columnName + " IS NULL" + obid + orderBy ,
						selectionArguments );
				// Check if the cursor is empty
				if ( cursor.getCount () == 0 ) {
					// Close the cursor
					cursor.close ();
					cursor = null;
				} // End if
				// Add the cursor to the list
				cursors.put ( new Section ( AppResources.getInstance ( ClientObjectiveActivity.this ).getString ( ClientObjectiveActivity.this , R.string.pending_client_objectives_label ) , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
						cursor );
				if ( viewComplete == true ){
				// Query DB in order to retrieve the Completed objectives
				cursor = DatabaseUtils.getInstance ( ClientObjectiveActivity.this ).getDaoSession ().getDatabase ().rawQuery (
						SQL + " AND OAC." + ObjectiveAchievementsDao.Properties.AchievementDate.columnName + " IS NOT NULL " +
							  " AND OAC." + ObjectiveAchievementsDao.Properties.ReasonCode.columnName + " IS NULL" + orderBy ,
						selectionArguments );
				// Check if the cursor is empty
				if ( cursor.getCount () == 0 ) {
					// Close the cursor
					cursor.close ();
					cursor = null;
				} // End if
				// Add the cursor to the list
				cursors.put ( new Section ( AppResources.getInstance ( ClientObjectiveActivity.this ).getString ( ClientObjectiveActivity.this , R.string.completed_client_objectives_label ) , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 255 , 0 ) , null ) ,
						cursor );
			  }
				
				// Query DB in order to retrieve the Cancelled objectives
				cursor = DatabaseUtils.getInstance ( ClientObjectiveActivity.this ).getDaoSession ().getDatabase ().rawQuery (
						SQL + " AND OAC." + ObjectiveAchievementsDao.Properties.AchievementDate.columnName + " IS NOT NULL " +
								"AND OAC." + ObjectiveAchievementsDao.Properties.ReasonCode.columnName + " IS NOT NULL" + orderBy ,
						selectionArguments );
				// Check if the cursor is empty
				if ( cursor.getCount () == 0 ) {
					// Close the cursor
					cursor.close ();
					cursor = null;
				} // End if
				// Add the cursor to the list
				cursors.put ( new Section ( AppResources.getInstance ( ClientObjectiveActivity.this ).getString ( ClientObjectiveActivity.this , R.string.cancelled_client_objectives_label ) , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 255 , 0 , 0 ) , null ) ,
						cursor );
				
				// Return the cursors
				return cursors;
			} catch ( Exception exception ) {
			
				// Do nothing
			} // End of try-catch block
			return null;
		}
		
		/*
		 * Runs on the UI thread after doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute ( LinkedHashMap < Section , Cursor > cursors ) {
			// Dismiss dialog
			AppDialog.getInstance ().dismiss ();
			// Check if the result is valid
			if ( cursors == null ) {
				// Do nothing
				emptyLabel = null;
				return;
			} // End if
			
			try {
				// Declare and initialize a multiple list adapter
				MultipleListAdapter adapter = new MultipleListAdapter ( ClientObjectiveActivity.this );
				// Iterate over all the cursors
				for ( Section section : cursors.keySet () ) {
					// Declare and initialize an adapter for the current section
					Adapter _adapter = null;
					// Check if the current cursor is valid
					if ( cursors.get ( section ) == null )
						// Indicate that there are no objectives
						_adapter = new EmptyListAdapter ( ClientObjectiveActivity.this , emptyLabel );
					else
						// Declare and initialize a new client objective adapter
						_adapter = new ClientObjectiveAdapter ( ClientObjectiveActivity.this , cursors.get ( section ) , R.layout.client_objective_activity_item );
					// Add the new daily call adapter
					adapter.addSection ( section , _adapter );
				} // End for each
				
				// Set the list adapter
				setListAdapter ( adapter );
				
			} catch ( Exception exception ) {
				// Do nothing
			} finally {
				emptyLabel = null;
			} // End of try-catch-finally block
		}
		
	}
	
	private int calculateDifference(Calendar nowDate, Date endDate)
	{
	    int tempDifference = 0;
	    int difference = 0;
	    Date a = nowDate.getTime();
	    Date b = endDate;
	    
	    Calendar earlier = Calendar.getInstance();
	    Calendar later = Calendar.getInstance();

	   // if (a.compareTo(b) < 0)
	   // {
	        earlier.setTime(a);
	        later.setTime(b);
	   // }
	   // else
	   // {
	     //   earlier.setTime(b);
	 //       later.setTime(a);
	 //   }

	    while (earlier.get(Calendar.YEAR) != later.get(Calendar.YEAR))
	    {
	        tempDifference = 365 * (later.get(Calendar.YEAR) - earlier.get(Calendar.YEAR));
	        difference += tempDifference;

	        earlier.add(Calendar.DAY_OF_YEAR, tempDifference);
	    }

	    if (earlier.get(Calendar.DAY_OF_YEAR) != later.get(Calendar.DAY_OF_YEAR))
	    {
	        tempDifference = later.get(Calendar.DAY_OF_YEAR) - earlier.get(Calendar.DAY_OF_YEAR);
	        difference += tempDifference;

	        earlier.add(Calendar.DAY_OF_YEAR, tempDifference);
	    }
	    Calendar t =Calendar.getInstance();
	    t.setTime(endDate);
	   if(t.after(nowDate))
	    	return difference;
	  return -1*difference;
	}
	public static long daysBetween(Calendar startDate, Calendar endDate) {
	    long end = endDate.getTimeInMillis();
	    long start = startDate.getTimeInMillis();
	 //   return TimeUnit.MILLISECONDS.toDays(Math.abs(end - start));
	   return   (Long) (( start   - end) / (1000 * 60 * 60 * 24));
	}
}