/**
 * Copyright 2014 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.VisitNotes;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.AppToast;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.ClientDocumentImages;
import me.SyncWise.Android.Database.ClientDocumentImagesDao;
import me.SyncWise.Android.Database.ClientDocumentImagesUtils;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.Visits;
import me.SyncWise.Android.Database.VisitsUtils;
import me.SyncWise.Android.Gson.BaseTimerActivity;
import me.SyncWise.Android.Modules.Call.CallAction;
import me.SyncWise.Android.Modules.Call.CallMenuActivity;
import me.SyncWise.Android.Widgets.Baguette;
import me.SyncWise.Android.Widgets.LinedEditText;

/**
 * Activity implemented to input a visit note.
 * 
 * @author Elias
 * @sw.todo <b>Visit Note Activity Implementation :</b><br>
 * Simply add this class in the {@code AndroidManifest.xml} file along with the permission :<br>
 * {@code <uses-permission android:name="android.permission.VIBRATE"/>}<br>
 * <b>AND</b> define how the main window of the activity interacts with the window containing the on-screen soft keyboard by adding the following to the activity tag in the manifest file :<br>
 * {@code android:windowSoftInputMode="adjustNothing"}
 *
 */
public class VisitNoteActivity extends BaseTimerActivity {

	/**
	 * Bundle key used to put/retrieve the content of the current client.
	 */
	public static final String CLIENT = VisitNoteActivity.class.getName () + ".CLIENT";
	
	/**
	 * Bundle key used to put/retrieve the content of the current visit.
	 */
	public static final String VISIT = VisitNoteActivity.class.getName () + ".VISIT";
	
	/**
	 * Bundle key used to put/retrieve the content of the is view flag.<br>
	 * This flag is used to determine if the user can edit / create blank visits or only view the previous blank visits.
	 */
	public static final String IS_VIEW = VisitNoteActivity.class.getName () + ".IS_VIEW";
	
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
		setContentView ( R.layout.visit_note_activity );
		
		// Check if both the visit and the client are valid
		if ( getIntent ().getSerializableExtra ( CLIENT ) == null || 
				( getIntent ().getSerializableExtra ( VISIT ) == null && ! getIntent ().getBooleanExtra ( IS_VIEW , false ) ) ) {
			// Indicate that the activity cannot be displayed
			new AppToast ( this ).setIcon ( R.drawable.warning ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.invalid_selection_label ) ).show ();
			// Finish activity
			finish ();
			return;
		} // End if
		
		// Change the title associated with this activity
		setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.visit_note_activity_title ) );
		// Retrieve a reference to the visit note edit text
		LinedEditText visitNote = (LinedEditText) findViewById ( R.id.edittext_visit_note );
		// Display the visit note hint
		visitNote.setHint ( AppResources.getInstance ( this ).getString ( this , R.string.visit_note_hint ) );
		// Set the maximum number of allowed characters
		visitNote.setFilters ( new InputFilter [] { new InputFilter.LengthFilter ( VisitsUtils.getNoteMaxLength () ) } );
		
		// Check the IS_VIEW flag
		if ( getIntent ().getBooleanExtra ( IS_VIEW , false ) )
			// Hide the visit note
			visitNote.setVisibility ( View.GONE );
		else
			// Display the visit note
			visitNote.setText ( ( (Visits) (Visits) getIntent ().getSerializableExtra ( VISIT ) ).getNote () );
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
	 * Initialize the contents of the Activity's standard options menu.
	 *
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
    @Override
    public boolean onCreateOptionsMenu ( Menu menu ) {
    	// Use the MenuInflater of this context to inflate a menu hierarchy from the specified XML resource
    	getMenuInflater ().inflate ( R.menu.action_bar , menu );
    	// Check the IS_VIEW flag
    	if ( ! getIntent ().getBooleanExtra ( IS_VIEW , false ) ) {
	    	// Enable the required menu items
	    	me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_save ) , R.string.save_label );
	    	me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_remove ) , R.string.delete_label );
	    	me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_camera ) , R.string.camera_label );
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
    	// Determine if the action save is selected
    	if ( menuItem.getItemId () == R.id.action_save ) {
    		// Check if there is at least one modification
    		if ( hasModifications () ) { 
				// Retrieve a reference to the visit object
				Visits visit = (Visits) getIntent ().getSerializableExtra ( VISIT );
				// Refresh the visit object
				DatabaseUtils.getInstance ( VisitNoteActivity.this ).getDaoSession ().refresh ( visit );
				// Set the visit note
				visit.setNote ( ( (LinedEditText) findViewById ( R.id.edittext_visit_note ) ).getText ().toString ().trim () );
				// Update the visit
				DatabaseUtils.getInstance ( VisitNoteActivity.this ).getDaoSession ().getVisitsDao ().update ( visit );
	    		// Indicate that a visit note was provided during this visit
	    		CallAction.setCallActionStatus ( VisitNoteActivity.this , ( (Visits) getIntent ().getSerializableExtra ( VISIT ) ).getVisitID () , CallAction.ID.VISIT_NOTE , true );
	        	// Call this to set the result that your activity will return to its caller
	        	setResult ( RESULT_OK , new Intent ().putExtra ( CallMenuActivity.ACTION_RESULT_SUCCESS , true ) );
	    		// Finish this activity
	        	VisitNoteActivity.this.finish ();
    		} // End if
    		else
				// Indicate that there are no new modifications
				Baguette.showText ( VisitNoteActivity.this ,
						AppResources.getInstance ( VisitNoteActivity.this ).getString ( VisitNoteActivity.this , R.string.no_new_modifications_message ) ,
						Baguette.BackgroundColor.BLUE );
    		// Consume event
    		return true;
    	} // End if
    	// Determine if the action add is selected
    	else if ( menuItem.getItemId () == R.id.action_camera ) {
			// Create a new intent to start a new activity
			Intent intent = new Intent ( this , PicturesActivity.class );
			// Add the visit to the intent
			intent.putExtra ( PicturesActivity.VISIT , getIntent ().getSerializableExtra ( VISIT ) );
			// Start the new activity
			startActivity ( intent );
    	} // End else if
    	// Determine if the action add is selected
    	else if ( menuItem.getItemId () == R.id.action_remove ) {
    		// Display delete confirmation
			AppDialog.getInstance ().displayAlert ( this ,
					null ,
					AppResources.getInstance ( this ).getString ( this , R.string.delete_confirmation_message ) ,
					AppDialog.ButtonsType.YesNo ,
					new DialogInterface.OnClickListener () {
						@Override
						public void onClick ( DialogInterface dialog , int which ) {
							// Determine the clicked button
							switch ( which ) {
							case DialogInterface.BUTTON_POSITIVE:
								// Retrieve a reference to the visit object
								final Visits visit = (Visits) getIntent ().getSerializableExtra ( VISIT );
			    				// Retrieve all the documents for the current visit
			    				final List < ClientDocumentImages > documents = DatabaseUtils.getInstance ( VisitNoteActivity.this ).getDaoSession ().getClientDocumentImagesDao ().queryBuilder ()
			    						.where ( ClientDocumentImagesDao.Properties.VisitID.eq ( visit.getVisitID () ) ).list ();
			    				// Delete the images
			    				DatabaseUtils.getInstance ( VisitNoteActivity.this ).getDaoSession ().getClientDocumentImagesDao ().deleteInTx ( documents );
			    				// Open the private documents folder
			    				final File folder = getDir ( ClientDocumentImagesUtils.FOLDER , MODE_PRIVATE );
			    				// Open the private temporary documents folder
			    				final File tempFolder = getDir ( ClientDocumentImagesUtils.FOLDER_TEMP , MODE_PRIVATE );
								// Refresh the visit object
								DatabaseUtils.getInstance ( VisitNoteActivity.this ).getDaoSession ().refresh ( visit );
								// Reset the visit note
								visit.setNote ( "" );
								// Update the visit
								DatabaseUtils.getInstance ( VisitNoteActivity.this ).getDaoSession ().getVisitsDao ().update ( visit );
					    		// Indicate that NO visit note was provided during this visit
					    		CallAction.setCallActionStatus ( VisitNoteActivity.this , ( (Visits) getIntent ().getSerializableExtra ( VISIT ) ).getVisitID () , CallAction.ID.VISIT_NOTE , false );
					        	// Call this to set the result that your activity will return to its caller
					        	setResult ( RESULT_OK , new Intent ().putExtra ( CallMenuActivity.SKIP_ACTION_RESULT , true ) );
					    		// Finish this activity
					        	VisitNoteActivity.this.finish ();
					        	// Run thread to delete files in the background
					        	new Thread ( new Runnable () {
					    			@Override
					    			public void run () {
					    				// Iterate over all the documents
					    				for ( ClientDocumentImages document : documents ) {
					    					// Get access to the document
					    					File image = new File ( folder , document.getVisitID () + "--" + document.getLineID () + ".png" );
					    					// Get access to the temporary document
					    					File tempImage = new File ( tempFolder , document.getVisitID () + "--" + document.getLineID () + ".png" );
					    					// Declare and initialize a picture reference to the current document
					    					Picture picture = new Picture ( document.getVisitID () , document.getLineID () , image.getAbsolutePath () , tempImage.getAbsolutePath () );
					    					// Get access to the picture
					    					File file = new File ( picture.getPath () );
					    					// Delete the file
					    					file.delete ();
					    					// Get access to the temporary picture
					    					file = new File ( picture.getTempPath () );
					    					// Delete the file
					    					file.delete ();
					    				} // End for each
					    			}
					    		} ).start ();
								break;
							case DialogInterface.BUTTON_NEGATIVE:
								// Dismiss dialog
								AppDialog.getInstance ().dismiss ();
								break;
							} // End switch
						}
					} );
    		// Consume event
    		return true;
    	} // End else if
    	// Allow normal menu processing to proceed
    	return false;
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
	
	/**
	 * Indicates whether the visit has new / unsaved modified note.
	 * 
	 * @return	Boolean stating if there are new modifications.
	 */
	private boolean hasModifications () {
    	// Check the IS_VIEW flag
    	if ( getIntent ().getBooleanExtra ( IS_VIEW , false ) )
    		// The IS_VIEW flag is set. There are no modifications whatsoever
    		return false;
    	// Otherwise compare the current and saved note
    	return ! ( (Visits) getIntent ().getSerializableExtra ( VISIT ) ).getNote ().trim ()
    			.equals ( ( (LinedEditText) findViewById ( R.id.edittext_visit_note ) ).getText ().toString ().trim () );
	}
	
}