/**
 * Copyright 2015 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.ClientsList.NewClient;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Vibration;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.IsProcessedUtils;
import me.SyncWise.Android.Database.NewClientImages;
import me.SyncWise.Android.Database.NewClientImagesDao;
import me.SyncWise.Android.Database.NewClientImagesUtils;
import me.SyncWise.Android.Database.NewClients;
import me.SyncWise.Android.Database.VisitsUtils;
import me.SyncWise.Android.Gson.BaseTimerActivity;
import me.SyncWise.Android.Widgets.Baguette;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;


public class NewClientActivity extends BaseTimerActivity {
	
	/**
	 * Bundle key used to put/retrieve the content of the pass code result.
	 */
	public static final String PASS_CODE_RESULT = NewClientActivity.class.getName () + ".PASS_CODE_RESULT";

	/**
	 * Bundle key used to put/retrieve the content of {@link #newClient}.
	 */
	public static final String NEW_CLIENT = NewClientActivity.class.getName () + ".NEW_CLIENT";
	
	/**
	 * A {@link me.SyncWise.Android.Database.NewClients NewClients} object used to host the new client details.
	 */
	private NewClients newClient;
	
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
		setContentView ( R.layout.new_client_activity );
		
		// Determine if a saved instance state is provided
		if ( savedInstanceState != null ) {
			newClient = (NewClients) savedInstanceState.getSerializable ( NEW_CLIENT );
		} // End if
		
		// Check if the new client is invalid
		if ( newClient == null ) {
			// Initialize the new client object
			newClient = new NewClients ();
			// Set the user , client , company and division codes
			newClient.setUserCode ( DatabaseUtils.getCurrentUserCode ( this ) );
			newClient.setClientCode ( String.valueOf ( VisitsUtils.getVisitID () ) );
			newClient.setCompanyCode ( DatabaseUtils.getCurrentCompanyCode ( this ) );
			newClient.setDivisionCode ( DatabaseUtils.getCurrentDivisionCode ( this ) );
			newClient.setIsProcessed ( IsProcessedUtils.isNotSync () );
			newClient.setStampDate ( Calendar.getInstance ().getTime () );
		} // End if
		
		// Change the title associated with this activity
		setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.new_client_activity_title ) );
	}
	
    /*
     * Called to retrieve per-instance state from an activity before being killed so that the state can be restored in onCreate(Bundle) or onRestoreInstanceState(Bundle).
     *
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    protected void onSaveInstanceState ( Bundle outState ) {
    	// Superclass method call
    	super.onSaveInstanceState ( outState );
    	// Save the content of displayFilter in the outState bundle
    	outState.putSerializable ( NEW_CLIENT , newClient );
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
	    // Enable the required menu items
    	me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_save ) , R.string.save_label );
    	me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_camera ) , R.string.camera_label );
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
    		// Retrieve the account number
    		newClient.setAccountNumber ( ( (EditText) findViewById ( R.id.new_client_account_number ) ).getText ().toString ().trim () );
    		newClient.setClientName ( ( (EditText) findViewById ( R.id.new_client_client_name ) ).getText ().toString ().trim () );
    		newClient.setClientAltName ( newClient.getClientName () );
    		newClient.setClientPhone ( ( (EditText) findViewById ( R.id.new_client_client_phone ) ).getText ().toString ().trim () );
    		newClient.setMobile ( ( (EditText) findViewById ( R.id.new_client_client_mobile ) ).getText ().toString ().trim () );
    		newClient.setFax ( ( (EditText) findViewById ( R.id.new_client_client_fax ) ).getText ().toString ().trim () );
    		newClient.setClientAddress ( ( (EditText) findViewById ( R.id.new_client_client_address ) ).getText ().toString ().trim () );
    		newClient.setEmail ( ( (EditText) findViewById ( R.id.new_client_client_email ) ).getText ().toString ().trim () );
    		newClient.setFinacialNumber ( ( (EditText) findViewById ( R.id.new_client_financial_number ) ).getText ().toString ().trim () );
    		newClient.setCNSS ( ( (EditText) findViewById ( R.id.new_client_cnss ) ).getText ().toString ().trim () );
    		newClient.setSyndicateNumber ( ( (EditText) findViewById ( R.id.new_client_syndicate_number ) ).getText ().toString ().trim () );
    		newClient.setLicenseNumber ( ( (EditText) findViewById ( R.id.new_client_license_number ) ).getText ().toString ().trim () );
    		// Insert new client
    		DatabaseUtils.getInstance ( this ).getDaoSession ().getNewClientsDao ().insert ( newClient );
    		// Indicate that the save was successful
    		Vibration.vibrate ( this );
	    	// Call this to set the result that your activity will return to its caller
			Intent intent = new Intent();
			intent.putExtra ( NEW_CLIENT , true );
			if ( getIntent ().getSerializableExtra ( PASS_CODE_RESULT ) != null )
				intent.putExtra ( PASS_CODE_RESULT , getIntent ().getSerializableExtra ( PASS_CODE_RESULT ) );
			setResult ( RESULT_OK , intent );
			// Finish this activity
    		finish ();
    		// Consume event
    		return true;
    	} // End if
    	// Determine if the action add is selected
    	else if ( menuItem.getItemId () == R.id.action_camera ) {
			// Create a new intent to start a new activity
			Intent intent = new Intent ( this , PicturesActivity.class );
			// Add the visit to the intent
			intent.putExtra ( PicturesActivity.CLIENT_CODE , newClient.getClientCode () );
			// Start the new activity
			startActivity ( intent );
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
							// Delete images
							List < NewClientImages > images = DatabaseUtils.getInstance ( NewClientActivity.this ).getDaoSession ().getNewClientImagesDao ().queryBuilder ()
									.where ( NewClientImagesDao.Properties.ClientCode.eq ( newClient.getClientCode () ) ).list ();
							// Make sure there is at least one image
							if ( ! images.isEmpty () ) {
								// Delete images from database
								DatabaseUtils.getInstance ( NewClientActivity.this ).getDaoSession ().getNewClientImagesDao ().deleteInTx ( images );
								// Open the private documents folder
								File folder = getDir ( NewClientImagesUtils.FOLDER , MODE_PRIVATE );
								// Open the private temporary documents folder
								File tempFolder = getDir ( NewClientImagesUtils.FOLDER_TEMP , MODE_PRIVATE );
								// Iterate over the images
								for ( NewClientImages image : images ) {
									// Get access to the picture
									File file = new File ( folder , image.getClientCode () + "--" + image.getLineID () + ".png" );
									// Delete the file
									file.delete ();
									// Get access to the temporary picture
									file = new File ( tempFolder , image.getClientCode () + "--" + image.getLineID () + ".png" );
									// Delete the file
									file.delete ();
								} // End for each
							} // End if
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
			newClient = null;
		} // End if
	}
	
}