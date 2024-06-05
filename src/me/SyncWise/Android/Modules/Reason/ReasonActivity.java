/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Reason;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.AppToast;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.Clients;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.Reasons;
import me.SyncWise.Android.Database.ReasonsDao;
import me.SyncWise.Android.Gson.BaseTimerListActivity;
import me.SyncWise.Android.Modules.Reason.ReasonAdapter;
import me.SyncWise.Android.Modules.Reason.ReasonAdapter.ViewHolder;
import me.SyncWise.Android.Utilities.ClientCard;
import me.SyncWise.Android.Widgets.Baguette;
import me.SyncWise.Android.Widgets.QuickAction.ActionItem;
import me.SyncWise.Android.Widgets.QuickAction.QuickAction;

/**
 * Activity implemented to prompt the user for a specific reason.
 * 
 * @author Elias
 * @sw.todo <b>Reason Activity Implementation :</b><br>
 * <ul>
 * <li>Add to the intent the activity title using {@link #ACTIVITY_TITLE} as key. <em>Optional</em></li>
 * <li>Add to the intent the activity sub title(s) in an array (depending on the reason type(s)) using {@link #ACTIVITY_SUB_TITLE} as key. <em>Optional</em></li>
 * <li>Add to the intent the reason type(s) in an array using {@link #REASON_TYPES} as key. <em>Required</em></li></li>
 * <li>Add to the intent the client object (if any) to display its name on a client card, otherwise the client card is hidden. <em>Optional</em></li>
 * <li>Add to the intent any extra (serializable) data that will be returned along with the reason code, under the {@link #EXTRA_DATA}. <em>Optional</em></li>
 * <li>Start this activity <b>FOR A RESULT</b>.</li>
 * <li>If the user has successfully chosen the required reason(s), an intent is returned as a result with the reason code(s), accessible using {@link #REASON_CODES} as key, in the same order of the sent reason types,
 *  along with the reason types themselves accessible using {@link #REASON_TYPES}.</li>
 * <li>Do not forget to add this class in the {@code AndroidManifest.xml} file.</li>
 * </ul>
 *
 */
public class ReasonActivity extends BaseTimerListActivity implements QuickAction.OnActionItemClickListener , QuickAction.OnDismissListener {
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #isCreated}.
	 */
	private static final String IS_CREATED = ReasonActivity.class.getName () + ".IS_CREATED";
	
	/**
	 * Boolean used to indicate if the activity has been previously created.<br>
	 * This flag is mainly used to determine if the current activity creation is the first or not (activity re-creation due to phone rotation for example).
	 */
	private boolean isCreated;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #displayDialog}.
	 */
	private static final String DISPLAY_DIALOG = ReasonActivity.class.getName () + ".DISPLAY_DIALOG";
	
	/**
	 * Boolean used to indicate if the dialog should be displayed or not.
	 */
	private boolean displayDialog;
	
	/**
	 * Reference to a {@link me.SyncWise.Android.Widgets.QuickAction.QuickAction QuickAction} used in this activity.
	 */
	private QuickAction quickAction;
	
	/**
	 * Bundle key used to display the activity title. 
	 */
	public static final String ACTIVITY_TITLE = ReasonActivity.class.getName () + ".ACTIVITY_TITLE";
	
	/**
	 * Bundle key used to display the activity sub title. 
	 */
	public static final String ACTIVITY_SUB_TITLE = ReasonActivity.class.getName () + ".ACTIVITY_SUB_TITLE";
	
	/**
	 * Bundle key used to display the reasons list of a specific type. 
	 */
	public static final String REASON_TYPES = ReasonActivity.class.getName () + ".REASON_TYPES";
	
	/**
	 * Bundle key used to retrieve the client object.
	 */
	public static final String CLIENT = ReasonActivity.class.getName () + ".CLIENT";
	
	/**
	 * List of {@link me.SyncWise.Android.Database.Reasons Reasons} objects for the user to provide an excuse.
	 */
	private ArrayList < Reasons > reasons;
	
	/**
	 * Bundle key used to send the code(s) of the selected reason(s) back to the calling activity. 
	 */
	public static final String REASON_CODES = ReasonActivity.class.getName () + ".REASON_CODES";
	
	/**
	 * List of strings used to host the selected reason codes.
	 */
	private ArrayList < String > reasonCodes;
	
	/**
	 * Bundle key used to retrieve/send extra data. 
	 */
	public static final String EXTRA_DATA = ReasonActivity.class.getName () + ".EXTRA_DATA";
	
	/**
	 * Reference to the reasons list population task.
	 */
	private static PopulateList populateList;
	
	/**
	 * Helper Class used to manage the activity's action items IDs for the quick action widgets.<br>
	 * The main purpose of this class is to maintain a unique identifier for action items within the activity.
	 * 
	 * @author Elias
	 *
	 */
	protected class ActionItemID {
		public static final int CONFIRM = 0;
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
		setContentView ( R.layout.reason_activity );
		// Compute the activity title
		String title = getIntent ().getStringExtra ( ACTIVITY_TITLE ) == null ? AppResources.getInstance ( this ).getString ( this , R.string.reason_activity_title ) : getIntent ().getStringExtra ( ACTIVITY_TITLE ) ;
		// Change the title associated with this activity
		setTitle ( title );
        // Perform the quick action setup
        setupQuickAction ();
		// Define the empty list view
		getListView ().setEmptyView ( findViewById ( R.id.empty_list_view ) );
		// Display empty list label
		( (TextView) findViewById ( R.id.empty_list_view ) ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.empty_reason_list_label ) );
        
		// Determine if a saved instance state is provided
		if ( savedInstanceState != null ) {
			// Restore the content provided by the saved instance state 
			reasonCodes = (ArrayList < String >) savedInstanceState.getSerializable ( REASON_CODES );
			isCreated = savedInstanceState.getBoolean ( IS_CREATED , isCreated );
			displayDialog = savedInstanceState.getBoolean ( DISPLAY_DIALOG , displayDialog );
		} // End if
		
		// Determine if the client object is valid
		if ( getIntent ().getSerializableExtra ( CLIENT ) == null )
			// Hide the client card layout
			findViewById ( R.id.layout_client_card ).setVisibility ( View.GONE );
		else
			// Initialize the client card
			ClientCard.initializeClientCard ( this , (Clients) getIntent ().getSerializableExtra ( CLIENT ) );
		
		// Check if the reason type array is valid
		if ( getIntent ().getStringArrayExtra ( REASON_TYPES ) == null || getIntent ().getStringArrayExtra ( REASON_TYPES ).length == 0 ) {
			// Invalid reason type array
			// Indicate that the activity cannot be initialized
			new AppToast ( this ).setIcon ( R.drawable.warning ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.invalid_selection_label ) ).show ();
			// Exit method
			return;
		} // End if
		
		// Determine if this is the first activity creation or a re-creation
		if ( ! isCreated ) {
			// Set the flags
			isCreated = true;
			displayDialog = true;
			// Display alert message to prompt the user for reasons
			displayDialog ();
		} // End if
		
		// Retrieve all the reasons asynchronously
		populateList = new PopulateList ();
		populateList.execute ();
	}
	
	/**
	 * Displayed the appropriate sub title (based on the currently displayed reason type).
	 */
	private void displaySubTitle () {
		// Declare and initialize a string used to host the sub title to display
		String subTitle = null;
		// Retrieve a reference to the reason types array
		String reasonTypes [] = getIntent ().getStringArrayExtra ( REASON_TYPES );

		// Compute the number of selected reason codes
		int reasonCodesSize = reasonCodes == null ? 0 : reasonCodes.size ();
		// Compute the number of reason types
		int reasonTypesSize = reasonTypes.length;
		
		// Compute the pre sub title
		String preSubTitle = "[ " + reasonCodesSize + " / " + reasonTypesSize + " ] " ;
		
		// Retrieve a reference to the activity sub titles array
		String subTitles [] = getIntent ().getStringArrayExtra ( ACTIVITY_SUB_TITLE );
		
		// Check if the array is valid
		if ( subTitles == null || subTitles.length == 0 ) {
			// Invalid array
			// Clear the action bar's subtitle
			getActionBar ().setSubtitle ( preSubTitle );
			// Exit method
			return;
		} // End if
		
		try {
			// Check if there is a valid reason type to display
			if ( reasonTypesSize != reasonCodesSize )
				// Get the sub title to display
				subTitle = subTitles [ reasonCodesSize ];
			else
				// No sub title
				subTitle = "";
			// Set the action bar's subtitle
			getActionBar ().setSubtitle ( preSubTitle + subTitle );
		} catch ( Exception exception ) {
			// Invalid array
			// Clear the action bar's subtitle
			getActionBar ().setSubtitle ( preSubTitle );
		} // End of try-catch block
	}
	
	/**
	 * Performs all necessary setup in order to properly display the quick action widget.
	 */
    private void setupQuickAction () {
		// Initialize the quick action widget
		quickAction = new QuickAction ( this , QuickAction.VERTICAL );
		// Action Item : Confirm
		ActionItem confirm = new ActionItem ( ActionItemID.CONFIRM ,
				AppResources.getInstance ( this ).getString ( this , R.string.quick_action_confirm_label ) ,
				getResources ().getDrawable ( R.drawable.quick_action_ok ) );
    	// Populate the quick action widget with quick action items
		quickAction.addActionItem ( confirm );
		// Assign an action item click listener
		quickAction.setOnActionItemClickListener ( this );
		// Assign a quick action dismiss listener
		quickAction.setOnDismissListener ( this );
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
		// Check if the dialog should be displayed
		if ( displayDialog )
			// Display alert message to prompt the user for reasons
			displayDialog ();
	}
    
	/**
	 * Displays a dialog informing the user of the number of required reasons to provide.
	 */
	private void displayDialog () {
		// Check if there is at least two reasons or more
		if ( getIntent ().getStringArrayExtra ( REASON_TYPES ).length > 1 )
			// Display alert message to prompt the user for reasons
			AppDialog.getInstance ().displayAlert ( this ,
					null ,
					AppResources.getInstance ( this ).getString ( this , R.string.enter_reson_message )
						+ " : " + getIntent ().getStringArrayExtra ( REASON_TYPES ).length ,
					AppDialog.ButtonsType.OK , new DialogInterface.OnClickListener () {
						@Override
						public void onClick ( DialogInterface dialog , int which ) {
							// Clear flag
							displayDialog = false;
							// Dismiss dialog
							AppDialog.getInstance ().dismiss ();
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
    	// Save the content of  in the outState bundle
    	outState.putSerializable ( REASON_CODES , reasonCodes );
    	// Save the content of isCreated in the outState bundle
    	outState.putBoolean ( IS_CREATED , isCreated );
    	// Save the content of isCreated in the outState bundle
    	outState.putBoolean ( DISPLAY_DIALOG , displayDialog );
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
	 * Called when the activity has detected the user's press of the back key. 
	 *
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed () {
		// Determine if there user selected at least one reason
		if ( reasonCodes == null || reasonCodes.isEmpty () )
			// Superclass method call
			super.onBackPressed ();
		else
			// Otherwise the user selected at least one reason
			// Display confirmation dialog
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
     * This method will be called when an item in the list is selected.
     *
     * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
     */
	@Override
    protected void onListItemClick ( ListView listView , View view , final int position , long id ) {
		// Retrieve a reference to the view's tag
		ViewHolder viewHolder = (ViewHolder) view.getTag ();
		// Indicate that the current reason is selected
		viewHolder.selection.setImageResource ( R.drawable.selection_checked );
		viewHolder.arrow.setImageResource ( R.drawable.arrow_right_green );
		// Show the quick action widget over the current view
		quickAction.show ( view , listView.getItemAtPosition ( position ) , getResources () );
	}
	
	/*
	 * Callback method to be invoked when an action item in this QuickAction has been clicked.
	 *
	 * @see me.SyncWise.Android.Widgets.QuickAction.QuickAction.OnActionItemClickListener#onItemClick(me.SyncWise.Android.Widgets.QuickAction.QuickAction, int, int)
	 */
	@Override
	public void onItemClick ( QuickAction source , View anchor , Object object , int pos , int actionId ) {
		// Determine the clicked action item
		switch ( actionId ) {
		case ActionItemID.CONFIRM:
			// Add the reason code
			addReasonCode ( ( (Reasons) object ).getReasonCode () );
			// Determine next move
			if ( moveToNext () ) {
				// Retrieve all the next reasons asynchronously
				populateList = new PopulateList ();
				populateList.execute ();
			} // End if
			else {
	        	// Call this to set the result that your activity will return to its caller
	        	setResult ( RESULT_OK , new Intent ().putExtra ( REASON_CODES , reasonCodes.toArray ( new String [ reasonCodes.size () ] ) )
	        			.putExtra ( REASON_TYPES , getIntent ().getStringArrayExtra ( REASON_TYPES ) )
	        			.putExtra ( EXTRA_DATA , getIntent ().getSerializableExtra ( EXTRA_DATA ) ) );
	        	// Finish this activity
	        	ReasonActivity.this.finish ();
			} // End else
			break;
		} // End of switch
	}
    
	/*
	 * This method will be invoked when the quick action is dismissed.
	 *
	 * @see me.SyncWise.Android.Widgets.QuickAction.QuickAction.OnDismissListener#onDismiss(me.SyncWise.Android.Widgets.QuickAction.QuickAction, android.view.View)
	 */
	@Override
	public void onDismiss ( QuickAction source , View anchor ) {
		// Retrieve a reference to the anchor view's tag
		ViewHolder viewHolder = (ViewHolder) anchor.getTag ();
		// Indicate that the current reason is de-selected
		viewHolder.selection.setImageResource ( R.drawable.selection );
		viewHolder.arrow.setImageResource ( R.drawable.arrow_right_black );
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
			quickAction = null;
			reasons = null;
			reasonCodes = null;
			isCreated = false;
		} // End if
	}
	
	/**
	 * Adds the provided reason code to the {@link #reasonCodes} list after validation checks.
	 * 
	 * @param reasonCode	String hosting the reason code to add.
	 */
	private void addReasonCode ( final String reasonCode ) {
		// Check if the reason code is valid
		if ( reasonCode == null )
			// Do nothing
			return;
		// Check if the reason codes list is valid
		if ( reasonCodes == null )
			// Initialize the list
			reasonCodes = new ArrayList < String > ();
		// Add the reason code to the list
		reasonCodes.add ( reasonCode );
	}
	
	/**
	 * Performs a check in order to decide whether to move to the next reason or simply finish the activity.
	 * 
	 * @return	Boolean indicating if the next reason can be displayed.
	 */
	private boolean moveToNext () {
		// Retrieve a reference to the reason types array
		String reasonTypes [] = getIntent ().getStringArrayExtra ( REASON_TYPES );
		// Compute the number of selected reason codes
		int reasonCodesSize = reasonCodes == null ? 0 : reasonCodes.size ();
		// Compute the number of reason types
		int reasonTypesSize = reasonTypes.length;
		// Check if there is a valid reason type to display
		if ( reasonTypesSize != reasonCodesSize )
			// Indicate that the module can move to the next reason
			return true;
		// Otherwise indicate that the module cannot move to the next reason
		return false;
	}
	
	/**
	 * AsyncTask helper class used to populate the reasons list with the appropriate reasons.
	 * 
	 * @author Elias
	 *
	 */
	private class PopulateList extends AsyncTask < Void , Void , Void > {
		
		/*
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Void doInBackground ( Void ... params ) {
			// Initialize the list of reasons
			reasons = new ArrayList < Reasons > ();
			
			// Declare and initialize a string used to host the next reason type to load
			String reasonType = null;
			// Retrieve a reference to the reason types array
			String reasonTypes [] = getIntent ().getStringArrayExtra ( REASON_TYPES );
			// Compute the number of selected reason codes
			int reasonCodesSize = reasonCodes == null ? 0 : reasonCodes.size ();
			// Compute the number of reason types
			int reasonTypesSize = reasonTypes.length;
			// Check if there is a valid reason type to display
			if ( reasonTypesSize != reasonCodesSize )
				// Get the next reason type to load
				reasonType = reasonTypes [ reasonCodesSize ];
			
			// Retrieve all the reasons of the specified type
			reasons.addAll ( DatabaseUtils.getInstance ( ReasonActivity.this ).getDaoSession ().getReasonsDao ().queryBuilder ()
					.where ( ReasonsDao.Properties.ReasonType.eq ( reasonType == null ? "" : reasonType ) ).orderAsc ( ReasonsDao.Properties.ReasonName ).list () );
			// Clear the task reference
			populateList = null;
			return null;
		}
		
		/*
		 * Runs on the UI thread after doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute ( Void arg ) {
			// Display the appropriate sub title
			displaySubTitle ();
			// Determine if the list of reasons is valid
			if ( reasons.isEmpty () ) {
				// Invalid reasons list, indicate so
				Baguette.showText ( ReasonActivity.this ,
						AppResources.getInstance ( ReasonActivity.this ).getString ( ReasonActivity.this , R.string.empty_reason_list_message ) ,
						Baguette.BackgroundColor.RED );
				// Exit method
				return;
			} // End if
			// Otherwise the reasons list is valid
			// Set a new list adapter
			setListAdapter ( new ReasonAdapter ( ReasonActivity.this , R.layout.reason_item , reasons ) );
		}
	}
	
}