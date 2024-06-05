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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppDialog.ButtonsType;
import me.SyncWise.Android.App;
import me.SyncWise.Android.AppFragmentPagerAdapter;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.Network;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Vibration;
import me.SyncWise.Android.Database.ClientBarcodes;
import me.SyncWise.Android.Database.ClientBarcodesDao;
import me.SyncWise.Android.Database.ClientDivisions;
import me.SyncWise.Android.Database.ClientDivisionsDao;
import me.SyncWise.Android.Database.Clients;
import me.SyncWise.Android.Database.ClientsDao;
import me.SyncWise.Android.Database.Companies;
import me.SyncWise.Android.Database.CompaniesDao;
import me.SyncWise.Android.Database.CycleCalls;
import me.SyncWise.Android.Database.CycleCallsDao;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.Divisions;
import me.SyncWise.Android.Database.DivisionsDao;
import me.SyncWise.Android.Database.ForceSyncJourneys;
import me.SyncWise.Android.Database.ForceSyncJourneysDao;
import me.SyncWise.Android.Database.JourneysUtils;
import me.SyncWise.Android.Database.PermissionsUtils;
import me.SyncWise.Android.Database.StatusUtils;
import me.SyncWise.Android.Database.TransactionHeadersUtils;
import me.SyncWise.Android.Database.UserPasswordsUtils;
import me.SyncWise.Android.Database.UsersDao;
import me.SyncWise.Android.Database.Visits;
import me.SyncWise.Android.Gson.BaseTimerFragmentActivity;
import me.SyncWise.Android.Modules.BarcodeIntegration.IntentIntegrator;
import me.SyncWise.Android.Modules.BarcodeIntegration.IntentResult;
import me.SyncWise.Android.Modules.Call.CallMenuActivity;
import me.SyncWise.Android.Modules.ClientsList.NewClient.NewClientActivity;
import me.SyncWise.Android.Modules.DataManagement.DataManagementActivity;

import me.SyncWise.Android.Utilities.AppFragment;
import me.SyncWise.Android.Utilities.Cycle;
import me.SyncWise.Android.Utilities.JourneyValidation;
import me.SyncWise.Android.Widgets.Baguette;
import me.SyncWise.Android.Widgets.ViewPagerIndicator.TabPageIndicator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
 
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Activity implemented to start a new journey and perform the available / allowed actions, such as visits ...
 * 
 * @author Elias
 * @sw.todo	<b>Journey Activity Implementation :</b><br>
 * Simply add this class in the {@code AndroidManifest.xml} file along with the permission :<br>
 * {@code <uses-permission android:name="android.permission.VIBRATE"/>}
 *
 */
public class JourneyActivity extends BaseTimerFragmentActivity {
	
	/**
	 * Object used to host the required fields to start the visit later on.
	 */
	protected PasscodeResult passcodeResult;
	protected String message;
	/**
	 * Boolean used to determine whether to display the passcode UI or not.
	 */
	protected boolean displayPasscode;
	
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
		setContentView ( R.layout.journey_activity );
		// Change the title associated with this activity
		setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.journey_activity_title ) );
		// Apply the view pager indicator theme
		setTheme ( R.style.Theme_PageIndicatorDefaults );
		Intent i = new Intent(this, GPSTracker.class);
        startService(i);
		// Declare and initialize a list used to host the fragments descriptions
		ArrayList < AppFragment > fragments = new ArrayList < AppFragment > ();
		// Define the required fragments
		fragments.add ( new AppFragment ( AppResources.getInstance ( this ).getString ( this , R.string.daily_visits ) , R.layout.journey_daily_visits_fragment ) );
		fragments.add ( new AppFragment ( AppResources.getInstance ( this ).getString ( this , R.string.weekly_visits ) , R.layout.journey_weekly_visits_fragment ) );
		fragments.add ( new AppFragment ( AppResources.getInstance ( this ).getString ( this , R.string.out_of_route_visits ) , R.layout.journey_out_of_route_visits ) );
		
		// Retrieve a reference to the indicator
		TabPageIndicator indicator = (TabPageIndicator) findViewById ( R.id.indicator );
		// Retrieve a reference to the view pager
		ViewPager viewPager = (ViewPager) findViewById ( R.id.pager );
		
		// Set the fragment pager adapter
		viewPager.setAdapter ( new AppFragmentPagerAdapter ( getSupportFragmentManager() , fragments , new Class < ? > [] { JourneyFragment_Daily.class , JourneyFragment_Weekly.class , JourneyFragment_OutOfRoute.class } ) );
		// Link the view pager to the indicator
		indicator.setViewPager ( viewPager );
	}
	@SuppressWarnings("unused")
	public void showDialog(final Context context)
	{

		LayoutInflater li = LayoutInflater.from(context);
		View promptsView = li.inflate(R.layout.prompts, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(promptsView);

		final EditText userInput = (EditText) promptsView 	.findViewById(R.id.editTextDialogUserInput);

		// set dialog message
		alertDialogBuilder
			.setCancelable(false)
			.setPositiveButton("OK",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
				// get user input and set it to result
				// edit text
				//result.setText(userInput.getText());
			    	String tt=userInput.getText().toString();
			    	String user=DatabaseUtils.getCurrentUserCode(context);
			    	String company=DatabaseUtils.getCurrentCompanyCode(context);
			    	 	if( UserPasswordsUtils.validateTimePasswordUsers12345  ( JourneyActivity.this  , UserPasswordsUtils.TransactionHeaderType.TRANSACTION , TransactionHeadersUtils.Type.SALES_INVOICE , userInput.getText().toString().trim(), DatabaseUtils.getCurrentUserCode ( JourneyActivity.this  )  )){
					    	
			    		
			    		int prefixID = DatabaseUtils.getInstance ( JourneyActivity.this ).getDaoSession ().getUsersDao ().queryBuilder ()
								.where (  UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode ( JourneyActivity.this ) ) 
										, UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode(  JourneyActivity.this ) )).unique ().getPrefixID ();
			    		Calendar now = Calendar.getInstance ();
			    		List< ForceSyncJourneys> forceSyncJourneys =DatabaseUtils .getInstance ( JourneyActivity. this).getDaoSession ().getForceSyncJourneysDao ().queryBuilder ()
			    				.where ( ForceSyncJourneysDao.Properties.JourneyCode.eq ( JourneysUtils.getJourneyCode ( prefixID ) )  
			    						    ).list()  ;
			    		
			    		if(forceSyncJourneys!=null   &&forceSyncJourneys.size()>0 ){
			    			forceSyncJourneys.get(0).setPassCode(userInput.getText().toString());
				    		forceSyncJourneys.get(0).setIsProcessed(1);
				    		DatabaseUtils.getInstance ( JourneyActivity.this ).getDaoSession ()
				    		.getForceSyncJourneysDao ().update(forceSyncJourneys.get(0) );
					    	
			    			}else{
			    				ForceSyncJourneys fj = new ForceSyncJourneys(null,
					    				JourneysUtils.getJourneyCode ( prefixID ), 
					    				DatabaseUtils.getCurrentUserCode ( JourneyActivity.this ),
					    				2,
					    				userInput.getText().toString(), 
					    				1, 
					    				now.getTime());
					    		DatabaseUtils.getInstance ( JourneyActivity.this ).getDaoSession ().getForceSyncJourneysDao ().insert(fj);
					    	 		
			    		}
			    	 dialog.cancel();
			    	}else{
			    		Baguette.showText (  (Activity) context , " wrong password" , Baguette.BackgroundColor.RED );
			    		showDialog((Activity) context);

			    	}
			    }
			  })
			.setNegativeButton("Cancel",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
				JourneyActivity.this.finish();
			    	dialog.cancel();
			    }
			  });

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();

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
		// Validate the current journey
		// TODO : A NEW JOURNEY HAS BEEN CREATED DURING THE JOURNEY ! THE LIST SHOULD BE REFRESHED, OR EVEN BETTER THE ACTIVITY CLOSED (TO DISPLAY THE NEXT WEEK IF NEEDED, TO ASK REASONS FOR REMAINING VISITS !
		new JourneyValidation.Task ( this , null , null ).execute (); // new Action ( true )
	if(PermissionsUtils.getEnable_Force_Daily_Sync(this, DatabaseUtils.getCurrentUserCode ( this), DatabaseUtils.getCurrentCompanyCode ( this) )){
		int prefixID = DatabaseUtils.getInstance ( this).getDaoSession ().getUsersDao ().queryBuilder ()
				.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode ( this) ) ,
						UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( this) ) ).unique ().getPrefixID ();
	 List< ForceSyncJourneys> forceSyncJourneys =DatabaseUtils .getInstance ( this).getDaoSession ().getForceSyncJourneysDao ().queryBuilder ()
				.where ( ForceSyncJourneysDao.Properties.JourneyCode.eq ( JourneysUtils.getJourneyCode ( prefixID ) )  
						    ).list()  ;
		
	 
	 if(  forceSyncJourneys!=null && forceSyncJourneys.size()>0 )
		{
			 if(forceSyncJourneys.get(0).getSyncStatus()==1){
				 
			 }else 	 if(forceSyncJourneys.get(0).getSyncStatus()==2 && forceSyncJourneys.get(0).getPassCode()!=null){
			  
			 }
			 else{
			
				 AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(  this);

	                // Set the title and message
	                alertDialogBuilder.setTitle("Force Sync");
	                alertDialogBuilder.setMessage("Force Sync");

	                // Add a "Yes" button and define its click action
	                alertDialogBuilder.setPositiveButton("Sync Again", new DialogInterface.OnClickListener() {
	                    @Override
	                    public void onClick(DialogInterface dialog, int which) {
	                    	if ( Network.networkAvailability ( JourneyActivity. this ) ) {
	            			 	Intent intent = new Intent ( JourneyActivity. this ,   App.getDataManagementActivity () );
	            				// Indicate that this is a direct sync from the login screen
	            				intent.putExtra ( DataManagementActivity.DIRECT_SYNC , true );
	            			 
	            				// Start the activity for a result
	            				startActivity ( intent   );
	            				JourneyActivity. this.finish();
	            				return;}
	            				else{
	            					 AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(JourneyActivity.  this);

	            		                // Set the title and message
	            		                alertDialogBuilder.setTitle("No connection");
	            		                alertDialogBuilder.setMessage("No connection");

	            		                // Add a "Yes" button and define its click action
	            		                alertDialogBuilder.setPositiveButton("Sync Again", new DialogInterface.OnClickListener() {
	            		                    @Override
	            		                    public void onClick(DialogInterface dialog, int which) {
	            		                    	if ( Network.networkAvailability ( JourneyActivity.this ) ) {
	            		                    	Intent intent = new Intent ( JourneyActivity.this ,   App.getDataManagementActivity () );
	            		        				// Indicate that this is a direct sync from the login screen
	            		        				intent.putExtra ( DataManagementActivity.DIRECT_SYNC , true );
	            		        			 
	            		        				// Start the activity for a result
	            		        				startActivity ( intent   );
	            		        				JourneyActivity.this.finish();
	            		        				return;
	            		                    	}else{
	            		                    		showDialog(JourneyActivity.this);
	            		                    	}
	            		                    }
	            		                });

	            		                // Add a "No" button and define its click action
	            		                alertDialogBuilder.setNegativeButton("PassWord", new DialogInterface.OnClickListener() {
	            		                    @Override
	            		                    public void onClick(DialogInterface dialog, int which) {
	            		                   	 showDialog(JourneyActivity.this);
	            		                        dialog.dismiss();
	            		                    }
	            		                });
	            		                alertDialogBuilder.setCancelable(false);
	            		                // Create and show the AlertDialog
	            		                AlertDialog alertDialog = alertDialogBuilder.create();
	            		                alertDialog.show();
	            				}
	                    }
	                });

	                // Add a "No" button and define its click action
	                alertDialogBuilder.setNegativeButton("PassWord", new DialogInterface.OnClickListener() {
	                    @Override
	                    public void onClick(DialogInterface dialog, int which) {
	                   	 showDialog(JourneyActivity.this);
	                        dialog.dismiss();
	                    }
	                });
	                alertDialogBuilder.setCancelable(false);
	                // Create and show the AlertDialog
	                AlertDialog alertDialog = alertDialogBuilder.create();
	                alertDialog.show();
			
			 }
		
			}else{
				if ( Network.networkAvailability ( this ) ) {
			 	Intent intent = new Intent ( this ,   App.getDataManagementActivity () );
				// Indicate that this is a direct sync from the login screen
				intent.putExtra ( DataManagementActivity.DIRECT_SYNC , true );
			 
				// Start the activity for a result
				startActivity ( intent   );
				this.finish();
				return;}
				else{
					 AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(  this);

		                // Set the title and message
		                alertDialogBuilder.setTitle("No connection");
		                alertDialogBuilder.setMessage("No connection");

		                // Add a "Yes" button and define its click action
		                alertDialogBuilder.setPositiveButton("Sync Again", new DialogInterface.OnClickListener() {
		                    @Override
		                    public void onClick(DialogInterface dialog, int which) {
		                    	if ( Network.networkAvailability ( JourneyActivity.this ) ) {
		                    	Intent intent = new Intent ( JourneyActivity.this ,   App.getDataManagementActivity () );
		        				// Indicate that this is a direct sync from the login screen
		        				intent.putExtra ( DataManagementActivity.DIRECT_SYNC , true );
		        			 
		        				// Start the activity for a result
		        				startActivity ( intent   );
		        				JourneyActivity.this.finish();
		        				return;
		                    	}else{
		                    		showDialog(JourneyActivity.this);
		                    	}
		                    }
		                });

		                // Add a "No" button and define its click action
		                alertDialogBuilder.setNegativeButton("PassWord", new DialogInterface.OnClickListener() {
		                    @Override
		                    public void onClick(DialogInterface dialog, int which) {
		                   	 showDialog(JourneyActivity.this);
		                        dialog.dismiss();
		                    }
		                });
		                alertDialogBuilder.setCancelable(false);
		                // Create and show the AlertDialog
		                AlertDialog alertDialog = alertDialogBuilder.create();
		                alertDialog.show();
				}
			}
	}
	}
	
	/*
	 * Initialize the contents of the Activity's standard options menu.
	 *
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
    @Override
    public boolean onCreateOptionsMenu ( Menu menu ) {
    	// Check if a note is undergoing modifications
    	if ( displayPasscode )
    		// Hide the menu
            return false;
    	
    	// Use the MenuInflater of this context to inflate a menu hierarchy from the specified XML resource
    	getMenuInflater ().inflate ( R.menu.action_bar , menu );
    	// Enable the required menu items
    	me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_add_contact ) );
		// Determine if user should scan a client barcode to perform a visit
		if ( PermissionsUtils.getClientBarcodes ( this , DatabaseUtils.getCurrentUserCode ( this ) , DatabaseUtils.getCurrentCompanyCode ( this ) ) ) {
	    	// Enable the required menu items
	    	me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_barcode ) );
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
    	// Determine if the action barcode is selected
    	if ( menuItem.getItemId () == R.id.action_barcode ) {
//    		// Declare and initialize a barcode intent integrator
//    		IntentIntegrator integrator = new IntentIntegrator ( this );
//    		// Initiate a barcode scan
//    		integrator.initiateScan();
    		
    		// Declare and initialize an alert dialog builder
    		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder ( this );
    		// By default, the alert dialog cannot be cancelled UNLESS it has no buttons
    		alertDialogBuilder.setCancelable ( false );
    		// Create view
    		final EditText editText = new EditText ( this );
    		editText.setId ( 1 );
    		editText.setInputType ( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD );
    		// Create click listener
    		DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener () {
    			@Override
    			public void onClick ( DialogInterface dialog , int which ) {
    				// Determine the clicked button
    				switch ( which ) {
    				case DialogInterface.BUTTON_POSITIVE:
    	    			// Perform a client barcode scan
    	    			new ClientBarcodeScan ( editText.getText ().toString () ).execute ();
    				case DialogInterface.BUTTON_NEGATIVE:
    		    		// Hide the software keyboard
    		            ( (InputMethodManager) getBaseContext ().getSystemService ( INPUT_METHOD_SERVICE ) ).hideSoftInputFromWindow ( editText.getWindowToken (), 0 );
    					// Dismiss dialog
    					AppDialog.getInstance ().dismiss ();
    					break;
    				} // End switch
    			}
    		};
    		// Setup dialog
    		alertDialogBuilder.setTitle ( R.string.barcode_hint );
    		alertDialogBuilder.setView ( editText );
    		alertDialogBuilder.setPositiveButton ( AppResources.getInstance ( this ).getString ( this , R.string.ok_label ) , onClickListener );
    		alertDialogBuilder.setNegativeButton ( AppResources.getInstance ( this ).getString ( this , R.string.cancel_label ) , onClickListener );
    		alertDialogBuilder.create ();
    		alertDialogBuilder.show ();
    		
    		// Consume event
    		return true;
    	} // End if
    	// Determine if the action add contact is selected
    	else if ( menuItem.getItemId () == R.id.action_add_contact ) {
    		// Start the new client activity
    		Intent intent = new Intent ( this , NewClientActivity.class );
    		startActivity ( intent );
    		// Consume event
    		return true;
    	} // End else if
    	// Allow normal menu processing to proceed
    	return false;
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
    	
    	// Parse the activity result to an barcode intent integration result
    	IntentResult scanResult = IntentIntegrator.parseActivityResult ( requestCode , resultCode , data );
    	// Check if the barcode scan result is valid
    	if ( scanResult != null) {
    		// Determine if the scanned barcode is valid
    		if ( ! TextUtils.isEmpty ( scanResult.getContents () ) )
    			// Perform a client barcode scan
    			new ClientBarcodeScan ( scanResult.getContents () ).execute ();
    		// Exit method
    		return;
    	} // End if
    	
    	// Superclass method call
		super.onActivityResult ( requestCode , resultCode , data );
    	// Check if the result is successful and the intent is valid
    	if ( resultCode != RESULT_OK || data == null )
    		// Exit method
    		return;
    	
    	// Determine the request code
    	switch ( requestCode & 0xffff ) {
		case JourneyFragment.REQUEST_CODE_VISIT:
		case JourneyFragment.REQUEST_CODE_COLLECTION:
		case JourneyFragment.REQUEST_CODE_CANCEL:
			// Compute the visit duration
			String duration = "";
			// Check if the intent contains a visit object
			if ( (Visits) data.getSerializableExtra ( CallMenuActivity.VISIT ) != null ) {
				// Retrieve a reference to the visit
				Visits visit = (Visits) data.getSerializableExtra ( CallMenuActivity.VISIT );
				// Compute the visit duration in milliseconds
				long milliseconds = visit.getEndDate ().getTime () - visit.getStartDate ().getTime ();
				// Compute the number of hours
				long hours = milliseconds / 1000 / 60 / 60;
				// Compute the number of minutes
				long minutes = ( milliseconds / 1000 / 60 ) % 60;
				// Compute the number of seconds
				long seconds = ( milliseconds / 1000 ) %  60;
				// Declare and initialize a two digits formatter
				DecimalFormat twoDigits = new DecimalFormat ( "00" );
				// Compute the duration label
				duration = "\n" + AppResources.getInstance ( this ).getString ( this , R.string.duration_label )
						+ " - " + twoDigits.format ( hours ) + ":" + twoDigits.format ( minutes ) + "'" + twoDigits.format ( seconds );
			} // End if
			// Compute baguette message
			String message = ( requestCode & 0xffff ) == JourneyFragment.REQUEST_CODE_VISIT || ( requestCode & 0xffff ) == JourneyFragment.REQUEST_CODE_COLLECTION ?
					AppResources.getInstance ( this ).getString ( this , R.string.visit_ended_message ) + duration :
					AppResources.getInstance ( this ).getString ( this , R.string.visit_cancelled_message );
			// Indicate that the visit has ended / was cancelled
			Baguette.showText ( this , message , Baguette.BackgroundColor.GREEN );
			// Indicate that the visit has ended / was cancelled
			Vibration.vibrate ( this );
			break;
		} // End switch
    	
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
	 * Called when the activity has detected the user's press of the back key. 
	 *
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed () {
		// Determine if the passcode is undergoing any modifications
		if ( displayPasscode ) {
			// Reset flag
			displayPasscode = false;
			// Retrieve a reference to the tertiary view
			View tertiary = findViewById ( R.id.layout_passcode );
			// Hide the tertiary view
			tertiary.startAnimation ( getViewAnimationOut ( tertiary ) );
    		// Refresh the action bar
    		invalidateOptionsMenu ();
    		// Exit method
    		return;
		} // End else if
		// Superclass method call
		super.onBackPressed ();
	}
	
	/**
	 * Builds and returns a slide in from top animation.<br>
	 * The slide animation duration is set by {@code R.integer.default_activity_transition_duration}.
	 * 
	 * @return	An {@link android.view.animation.Animation Animation} used to perform a slide in from top animation.
	 */
	private Animation getViewAnimationIn () {
		// Declare and initialize the slide in animation
		Animation in = new TranslateAnimation ( Animation.ABSOLUTE , 0 , Animation.ABSOLUTE , 0 , Animation.RELATIVE_TO_SELF , -1 , Animation.RELATIVE_TO_SELF , 0 );
		// Set the animation duration
		in.setDuration ( getResources ().getInteger ( R.integer.default_activity_transition_duration ) );
		// Return the animation
		return in;
	}
	
	/**
	 * Builds and returns a slide out to bottom animation.<br>
	 * The slide animation duration is set by {@code R.integer.default_activity_transition_duration}.
	 * 
	 * @param view	Reference to the view to hide after the animation ends.
	 * @return	An {@link android.view.animation.Animation Animation} used to perform a slide out to bottom animation.
	 */
	private Animation getViewAnimationOut ( final View view ) {
		// Declare and initialize the slide in animation
		Animation out = new TranslateAnimation ( Animation.ABSOLUTE , 0 , Animation.ABSOLUTE , 0 , Animation.RELATIVE_TO_SELF , 0 , Animation.RELATIVE_TO_SELF , -1 );
		// Set the animation duration
		out.setDuration ( getResources ().getInteger ( R.integer.default_activity_transition_duration ) );
		// Set an animation listener mainly used to remove the view after it is slid out
		out.setAnimationListener ( new AnimationListener () {
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
			
			@Override
			public void onAnimationEnd ( Animation animation ) {
				// Hide the view
				view.setVisibility ( View.GONE );
			}
		} );
		// Return the animation
		return out;
	}
	
	/**
	 * Determines if the list item clicks are enabled or not.
	 * 
	 * @return	Boolean indicating if the list item clicks are enabled or not.
	 */
	public boolean isClickEnabled () {
		return ! displayPasscode;
	}
	
	/**
	 * Prompts for a pass code.
	 * 
	 * @param passcodeResult	Object used to host the required fields to start the visit later on.
	 */
	public void promptPasscode ( PasscodeResult passcodeResult ) {
		// Set flag
		displayPasscode = true;
		// Store fields
		this.passcodeResult = passcodeResult;
		// Build the message
		String message = AppResources.getInstance ( this ).getString ( this , R.string.not_available_abbreviation );
		if ( passcodeResult.getType () == PasscodeResult.TYPE_OUT_OF_ROUTE )
			message = AppResources.getInstance ( this ).getString ( this , R.string.journey_passcode_required_message );
		else if ( passcodeResult.getType () == PasscodeResult.TYPE_USER_ACCOUNT )
			message = AppResources.getInstance ( this ).getString ( this , R.string.user_account_passcode_required_message );		
	
		// Initialize the tertiary view
		initializeTertiaryView ( message );
		// Retrieve a reference to the tertiary view
		View tertiaryView = findViewById ( R.id.layout_passcode );
		// Display the tertiary view
		tertiaryView.setVisibility ( View.VISIBLE );
		// Animate the tertiary view
		tertiaryView.startAnimation ( getViewAnimationIn() );
		// Refresh the action bar
		invalidateOptionsMenu ();
	}
	/**
	 * Prompts for a pass code.
	 * 
	 * @param passcodeResult	Object used to host the required fields to start the visit later on.
	 */
	public void promptPasscode1 ( String passcodeResult ) {
		// Set flag
		displayPasscode = true;
		// Store fields
        this.message="This Client Has Unpaid Cash Invoice Please Collect Or Contact the Administrator for a Passcode"+"\n"+ passcodeResult ;
	 
		// Initialize the tertiary view
		initializeTertiaryView ( message );
		// Retrieve a reference to the tertiary view
		View tertiaryView = findViewById ( R.id.layout_passcode );
		// Display the tertiary view
		tertiaryView.setVisibility ( View.VISIBLE );
		// Animate the tertiary view
		tertiaryView.startAnimation ( getViewAnimationIn() );
		// Refresh the action bar
		invalidateOptionsMenu ();
	}
	
	/**
	 * Initializes the passcode (tertiary) view.
	 * 
	 * @param message	String hosting the message to display for the user regarding the passcode.
	 */
	protected void initializeTertiaryView ( String message ) {
		// Retrieve a reference to the parent view
		View parent = findViewById ( R.id.layout_passcode );
		// Retrieve a reference to the passcode edit text
		EditText passcodeEditText = (EditText) parent.findViewById ( R.id.edittext_passcode );		
		// Retrieve a reference to the passcode title label
		TextView titleLabel = (TextView) parent.findViewById ( R.id.label_passcode );
		// Retrieve a reference to the passcode message label
		TextView messageLabel = (TextView) parent.findViewById ( R.id.label_passcode_message );
		
		// Display the title
		titleLabel.setText ( AppResources.getInstance ( this ).getString ( this , R.string.passcode_label ) );
		// Check if a message is provided
		if ( message != null ) {
			// Display the passcode title label
			messageLabel.setText ( message );
			// Clear any previous entries
			passcodeEditText.setText ( "" );
		} // End if
		// Enable the save icon
		parent.findViewById ( R.id.icon_save_passcode ).setEnabled ( true );
		// Enable the edit text
		passcodeEditText.setEnabled ( true );
		// Display the field hint
		passcodeEditText.setHint ( AppResources.getInstance ( this ).getString ( this , R.string.movement_passcode_hint ) );
	}
	
	/**
	 * Called when a view has been clicked.<br>
	 * The passcode is saved.
	 * 
	 * @param view	The clicked view.
	 */
	public void updatePasscode ( View view ) {
		// Determine if the passcode is undergoing any modifications
		if ( ! displayPasscode )
			// No modifications
			return;
		
		// Retrieve a reference to the tertiary view
		View tertiary = findViewById ( R.id.layout_passcode );
		// Retrieve a reference to the passcode edit text
		EditText passcodeEditText = (EditText) tertiary.findViewById ( R.id.edittext_passcode );
		// Store the passcode
		String passcode = passcodeEditText.getText ().toString ().trim ();
		if(this.message==null){
		// Validate pass code
		if ( passcodeResult.getType () == PasscodeResult.TYPE_OUT_OF_ROUTE && ! UserPasswordsUtils.validateRegularPassword ( this , passcode ) ) {
			// Indicate that the passcode is not valid
			Baguette.showText ( this ,
					AppResources.getInstance ( this ).getString ( this , R.string.regular_passcode_invalid_message ) ,
					Baguette.BackgroundColor.RED );
			// Exit method
			return;
		} // End if
		else if ( passcodeResult.getType () == PasscodeResult.TYPE_USER_ACCOUNT && ! UserPasswordsUtils.validateTimePasswordUsers  ( this , UserPasswordsUtils.TransactionHeaderType.TRANSACTION , TransactionHeadersUtils.Type.SALES_INVOICE , passcode, DatabaseUtils.getCurrentUserCode ( this )  ) ) {
			// Indicate that the passcode is not valid
			Baguette.showText ( this ,
					AppResources.getInstance ( this ).getString ( this , R.string.time_passcode_invalid_message ) ,
					Baguette.BackgroundColor.RED );
			// Exit method
			return;
		} // End else if
		else if ( passcodeResult.getType () != PasscodeResult.TYPE_OUT_OF_ROUTE && passcodeResult.getType () != PasscodeResult.TYPE_USER_ACCOUNT ) {
			// Indicate that the type is not valid
			Baguette.showText ( this ,
					AppResources.getInstance ( this ).getString ( this , R.string.invalid_selection_label ) ,
					Baguette.BackgroundColor.RED );
			// Exit method
			return;
		} // End else if
		
		// Reset flag
		displayPasscode = false;
		
		// Retrieve a reference of the view pager (which has the app fragment pager adapter linked to)
		// The view pager is used to retrieve a reference to the daily fragment, via its tag
		ViewPager viewPager = (ViewPager) findViewById ( R.id.pager );
		// Retrieve a reference to the app fragment pager adapter
		// This adapter can compute the expected daily fragment tag so it can be referenced (via its tag)
		AppFragmentPagerAdapter adapter = viewPager != null ? (AppFragmentPagerAdapter) viewPager.getAdapter () : null;
		// Compute the daily section (having index 0) tag
		String tag = adapter != null ? adapter.getFragmentName ( viewPager , passcodeResult.getFragmentIndex () ) : null;
		// Retrieve a reference to the fragment
		JourneyFragment journeyFragment = (JourneyFragment) getSupportFragmentManager ().findFragmentByTag ( tag == null ? "" : tag );
		// Simulate visit click
		journeyFragment.preVisitClientCall ( passcodeResult.getCall () , passcodeResult.isEnforceVisitType () , passcodeResult.getVisitType () , passcodeResult.getBarcodeReason () , true );
		
		// Disable the save icon
		tertiary.findViewById ( R.id.icon_save_passcode ).setEnabled ( false );
		// Hide the software keyboard
        ( (InputMethodManager) getBaseContext ().getSystemService ( INPUT_METHOD_SERVICE ) ).hideSoftInputFromWindow ( tertiary.getWindowToken (), 0 );
        
		// Hide the tertiary view
        tertiary.startAnimation ( getViewAnimationOut ( tertiary ) );
		// Indicate that the save was successful
		Vibration.vibrate ( this );
		// Refresh the action bar
		invalidateOptionsMenu ();
		}else{
			
			if(!UserPasswordsUtils.validateRegularPassword(this,passcode)) //UserPasswordsUtils.validateTimePasswordUsers  ( this , UserPasswordsUtils.TransactionHeaderType.MOVEMENT , MovementHeadersUtils.Type.LOAD , passcode, DatabaseUtils.getCurrentUserCode ( this )  ))
			{			
			Baguette.showText ( this ,
					AppResources.getInstance ( this ).getString ( this , R.string.time_passcode_invalid_message ) ,
				Baguette.BackgroundColor.GREEN );
			return;
			}
			String SQL = null;
		 	String selectionArguments [] = null ;
			SQL = " UPDATE TransactionHeaders set TransactionPasswordCode= ?"  +
				  " where RemainingAmount > 0 and TransactionPasswordCode is null " +
				  " and clientCode not in(select AccountNumber from UserAccountNumbers  where userCode = ? )" +
				  " and ClientCode in (select distinct ClientCode from Clients where ClientType = 1 and CompanyCode = ? " +
				  " and TransactionStatus = 1 and TransactionType = 2 and CompanyCode = ?)";
	
		 	// Compute the selection arguments
				selectionArguments = new String [] {	   
						String.valueOf ( passcode ),
						String.valueOf ( DatabaseUtils.getCurrentUserCode (   JourneyActivity.this ) ),
						String.valueOf ( DatabaseUtils.getCurrentCompanyCode( JourneyActivity.this ) ),
						String.valueOf ( DatabaseUtils.getCurrentCompanyCode( JourneyActivity.this ) )
				};
				DatabaseUtils.getInstance ( JourneyActivity.this ).getDaoSession ().getDatabase ().execSQL (SQL,selectionArguments );
				SQL=    "    update TransactionHeaders set TransactionPasswordCode= ? " +
						"    where  RemainingAmount > 0 " +
						"	 and TransactionPasswordCode is null  and Info1=1 " +
						"	 and ClientCode in (select distinct ClientCode from Clients where ClientType = 2 and CompanyCode = ? " +
						"	  )   and TransactionStatus = 1   and TransactionType = 2 and CompanyCode = ?";
				// Compute the selection arguments
				selectionArguments = new String [] {	   
						String.valueOf ( passcode ),
						String.valueOf ( DatabaseUtils.getCurrentCompanyCode( JourneyActivity.this ) ),
						String.valueOf ( DatabaseUtils.getCurrentCompanyCode( JourneyActivity.this ) )
				};
				DatabaseUtils.getInstance ( JourneyActivity.this ).getDaoSession ().getDatabase ().execSQL (SQL,selectionArguments );
				
			// Disable the save icon
			tertiary.findViewById ( R.id.icon_save_passcode ).setEnabled ( false );
			// Hide the software keyboard
	        ( (InputMethodManager) getBaseContext ().getSystemService ( INPUT_METHOD_SERVICE ) ).hideSoftInputFromWindow ( tertiary.getWindowToken (), 0 );
	        
			// Hide the tertiary view
	        //tertiary.startAnimation ( getViewAnimationOut ( tertiary ) );
	      //  tertiary.setVisibility ( View.GONE );
			// Indicate that the save was successful
			Vibration.vibrate ( this );
			// Refresh the action bar
			invalidateOptionsMenu ();
			this.message=null;
			this.onBackPressed ();
			// Exit method
			return;
		}
	}
	
	/**
	 * AsyncTask helper class used to search for the scanned client barcode.
	 * 
	 * @author Elias
	 *
	 */
	protected class ClientBarcodeScan extends AsyncTask < Void , Void , Void > {
		
		/**
		 * String holding the scanned barcode.
		 */
		private String barcode;
		
		/**
		 * Flag used to indicate if the barcode was not found.
		 */
		private boolean barcodeNotFound;
		
		/**
		 * Flag used to indicate if an error occurred.
		 */
		private boolean error;
		
		/**
		 * Reference to the journey fragment of weekly calls.
		 */
		private JourneyFragment_Weekly weeklySection;
		
		/**
		 * Reference to the unscheduled call to perform.
		 */
		private Call unscheduledCall;
		
		/**
		 * Reference to the client whose barcode was scanned. 
		 */
		private Clients client;
		
		/**
		 * Reference to the client's division whose barcode was scanned..
		 */
		private ClientDivisions clientDivision;
		
		/**
		 * Constructor.
		 * 
		 * @param barcode	String holding the scanned barcode.
		 */
		public ClientBarcodeScan ( String barcode ) {
			// Superclass method call
			super ();
			// Initialize attribute
			this.barcode = barcode;
		}
		
		/*
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Void doInBackground ( Void ... params ) {
			try {
				// Check if the barcode is valid
				if ( barcode == null )
					// Invalid barcode
					return null;
				// Otherwise the barcode is valid
				// Search for the appropriate client barcode
				ClientBarcodes clientBarcode = DatabaseUtils.getInstance ( JourneyActivity.this ).getDaoSession ().getClientBarcodesDao ().queryBuilder ()
						.where ( ClientBarcodesDao.Properties.ClientBarcode.eq ( barcode ) ).unique ();
				// Check if the client barcode is valid
				if ( clientBarcode == null ) {
					// Set flag
					barcodeNotFound = true;
					// Barcode not found
					return null;
				} // End if
				
				// Retrieve the week number per cycle
				int weekNumberPerCycle = Cycle.getWeekNumberPerCycle ( JourneyActivity.this , DatabaseUtils.getCurrentUserCode ( JourneyActivity.this ) , DatabaseUtils.getCurrentCompanyCode ( JourneyActivity.this ) );
				// Retrieve the current week number
				int week = Cycle.getWeekNumber ( weekNumberPerCycle );
				// Compute the current day string
				Calendar today = Calendar.getInstance ();
				String todayStr = null;
				switch ( today.get ( Calendar.DAY_OF_WEEK ) ) {
				case Calendar.MONDAY:
					todayStr = getString ( R.string.monday_abbreviation );
					break;
				case Calendar.TUESDAY:
					todayStr = getString ( R.string.tuesday_abbreviation );
					break;
				case Calendar.WEDNESDAY:
					todayStr = getString ( R.string.wednesday_abbreviation );
					break;
				case Calendar.THURSDAY:
					todayStr = getString ( R.string.thursday_abbreviation );
					break;
				case Calendar.FRIDAY:
					todayStr = getString ( R.string.friday_abbreviation );
					break;
				case Calendar.SATURDAY:
					todayStr = getString ( R.string.saturday_abbreviation );
					break;
				case Calendar.SUNDAY:
					todayStr = getString ( R.string.sunday_abbreviation );
					break;
				} // End switch
				
				// Retrieve the appropriate client
				client = DatabaseUtils.getInstance ( JourneyActivity.this ).getDaoSession ().getClientsDao ().queryBuilder ()
						.where ( ClientsDao.Properties.ClientCode.eq ( clientBarcode.getClientCode () ) ,
								ClientsDao.Properties.CompanyCode.eq ( clientBarcode.getCompanyCode () ) ).unique ();
				
				// Retrieve the appropriate client division
				clientDivision = DatabaseUtils.getInstance ( JourneyActivity.this ).getDaoSession ().getClientDivisionsDao ().queryBuilder ()
						.where ( ClientDivisionsDao.Properties.ClientCode.eq ( clientBarcode.getClientCode () ) ,
								ClientDivisionsDao.Properties.CompanyCode.eq ( clientBarcode.getCompanyCode () ) ,
								ClientDivisionsDao.Properties.DivisionCode.eq ( clientBarcode.getDivisionCode () ) ).unique ();
				
				// Retrieve all the cycle calls for the current client
				List < CycleCalls > calls = DatabaseUtils.getInstance ( JourneyActivity.this ).getDaoSession ().getCycleCallsDao ().queryBuilder ()
						.where ( CycleCallsDao.Properties.ClientCode.eq ( client.getClientCode () ) ,
								CycleCallsDao.Properties.CompanyCode.eq ( client.getCompanyCode () ) ,
								CycleCallsDao.Properties.DivisionCode.eq ( clientDivision.getDivisionCode () ) ,
								CycleCallsDao.Properties.CycleCallStatus.eq ( StatusUtils.isAvailable () ) ,
								CycleCallsDao.Properties.Day.eq ( todayStr ) ,
								CycleCallsDao.Properties.Week.eq ( week ) ).list ();
				
				// Retrieve the appropriate company
				Companies company = DatabaseUtils.getInstance ( JourneyActivity.this ).getDaoSession ().getCompaniesDao ().queryBuilder ()
						.where ( CompaniesDao.Properties.CompanyCode.eq ( client.getCompanyCode () ) ).unique ();
				
				// Retrieve the appropriate division
				Divisions division = DatabaseUtils.getInstance ( JourneyActivity.this ).getDaoSession ().getDivisionsDao ().queryBuilder ()
						.where ( DivisionsDao.Properties.CompanyCode.eq ( clientDivision.getCompanyCode () ) ,
								DivisionsDao.Properties.DivisionCode.eq ( clientDivision.getDivisionCode () ) ).unique ();
				
				// Set the company and division names
				client.setCompanyName ( company.getCompanyName () );
				client.setDivisionCode ( division.getDivisionCode () );
				client.setDivisionName ( division.getDivisionName () );
				
				// Retrieve a reference of the view pager (which has the app fragment pager adapter linked to)
				// The view pager is used to retrieve a reference to the daily fragment, via its tag
				ViewPager viewPager = (ViewPager) findViewById ( R.id.pager );
				// Retrieve a reference to the app fragment pager adapter
				// This adapter can compute the expected daily fragment tag so it can be referenced (via its tag)
				AppFragmentPagerAdapter adapter = viewPager != null ? (AppFragmentPagerAdapter) viewPager.getAdapter () : null;
				// Compute the daily section (having index 0) tag
				String tag = adapter != null ? adapter.getFragmentName ( viewPager , 1 ) : null;
				// Retrieve a reference to the journey fragment of weekly calls based on its tag
				weeklySection = (JourneyFragment_Weekly) getSupportFragmentManager ().findFragmentByTag ( tag == null ? "" : tag );
				
				if ( calls.isEmpty () ) {
					// The client is NOT scheduled for today
					// Retrieve the user prefix ID
					int prefixID = DatabaseUtils.getInstance ( JourneyActivity.this ).getDaoSession ().getUsersDao ().queryBuilder ()
							.where (  UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode ( JourneyActivity.this ) ) 
									, UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode(  JourneyActivity.this ) )).unique ().getPrefixID ();
					// Compute the day's journey code
					String journeyCode = JourneysUtils.getJourneyCode ( prefixID );
					// Create a new call based on the selected client
					unscheduledCall = new Call ( "" , client , clientDivision );
					// Set the journey code
					unscheduledCall.setJourneyCode ( journeyCode );
					// Set the current day
					unscheduledCall.setDay ( todayStr );
				} // End if
				
				return null;
			} catch ( Exception exception ) {
				// An error occurred
				error = true;
			} // End of try-catch block
			return null;
		}
		
		/*
		 * Runs on the UI thread after doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute ( Void arg ) {
			try {
				// Check if an error occurred
				if ( error )
					// Indicate that an error occurred
					Baguette.showText ( JourneyActivity.this , AppResources.getInstance ( JourneyActivity.this ).getString ( JourneyActivity.this , R.string.client_barcode_scan_error_message ) , Baguette.BackgroundColor.RED );				
				// Check if the barcode was not found
				else if ( barcodeNotFound )
					// Indicate that the barcode was not found
//					Baguette.showText ( JourneyActivity.this , AppResources.getInstance ( JourneyActivity.this ).getString ( JourneyActivity.this , R.string.client_barcode_not_found_message ) , Baguette.BackgroundColor.BLUE );
					AppDialog.getInstance ().displayAlert ( JourneyActivity.this ,
							AppResources.getInstance ( JourneyActivity.this ).getString ( JourneyActivity.this , R.string.warning_label ) , 
							AppResources.getInstance ( JourneyActivity.this ).getString ( JourneyActivity.this , R.string.client_barcode_not_found_message ) , 
							ButtonsType.OK , null );
				// Check if the call is a scheduled call
				else if ( weeklySection != null && unscheduledCall == null && client != null && clientDivision != null )
					// Perform a scheduled client call
					weeklySection.performScheduledBarcodedClientCall ( client , clientDivision );
				// check if the call is unscheduled
				else if ( weeklySection != null && unscheduledCall != null && client != null && clientDivision != null )
					// Perform an unscheduled client call
					weeklySection.performUnscheduledBarcodedClientCall ( unscheduledCall );
			} catch ( Exception exception ) {
				// An error occurred
				// Indicate that an error occurred
				Baguette.showText ( JourneyActivity.this , AppResources.getInstance ( JourneyActivity.this ).getString ( JourneyActivity.this , R.string.client_barcode_scan_error_message ) , Baguette.BackgroundColor.RED );
			} finally {
				// Clear variables
				barcode = null;
				weeklySection = null;
				unscheduledCall = null;
			} // End of try-catch-fially block
		}
		
	}
	
}