/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules;

import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.Users;
import me.SyncWise.Android.Database.UsersDao;
import me.SyncWise.Android.Modules.Journey.JourneyActivity;
import me.SyncWise.Android.Modules.MenuList.MenuItem;
import me.SyncWise.Android.Utilities.JourneyValidation;
import me.SyncWise.Android.Utilities.JourneyValidation.Action;
import me.SyncWise.Android.Utilities.JourneyValidation.CallBack;
import me.SyncWise.Android.Utilities.JourneyValidation.Result;
import me.SyncWise.Android.Widgets.Baguette;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;

/**
 * Activity implemented to display the application home screen.
 * 
 * @author Elias
 * @sw.todo	<b>Home Screen Activity Implementation :</b><br>
 * <ul>
 * <li>Extend this class.</li>
 * <li>Implement the {@link #populateCallMenu()} method, which should define the menu items using the {@link #addMenuItem(MenuItem)} method.</li>
 * <li>Do not forget to add your new class in the {@code AndroidManifest.xml} file.</li>
 * </ul>
 *
 */
public abstract class HomeScreenActivity extends me.SyncWise.Android.Modules.MenuList.MenuListActivity {

	/**
	 * Bundle key used to put/retrieve the content of {@link #isCreated}.
	 */
	private static final String IS_CREATED = CompanyProfileActivity.class.getName () + ".IS_CREATED";
	
	/**
	 * Boolean used to indicate if the activity has been previously created.<br>
	 * This flag is mainly used to determine if the current activity creation is the first or not (activity re-creation due to phone rotation for example).
	 */
	private boolean isCreated;
	
	/**
	 * AsyncTask helper class used to compute and display a welcoming message.
	 * 
	 * @author Elias
	 *
	 */
	private class Welcome extends AsyncTask < Void , Void , String > {
		
		/*
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected String doInBackground ( Void ... params ) {
			try {
				// Sleed thread for 1 second
				Thread.sleep ( 1000 );
			} catch ( InterruptedException e ) {
				// Do nothing
			}// End of try-catch block
			
			try {
				// Retrieve the current user object
				Users currentUser = DatabaseUtils.getInstance ( HomeScreenActivity.this ).getDaoSession ().getUsersDao ().queryBuilder ()
					.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode ( HomeScreenActivity.this ) ) ,
							UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( HomeScreenActivity.this ) ) )
					.unique ();
				// Check if the result is valid
				if ( currentUser == null )
					// Invalid user
					return null;
				// Compute and return welcome message
				return AppResources.getInstance ( HomeScreenActivity.this ).getString ( HomeScreenActivity.this , R.string.welcome_label )
						+ " " + currentUser.getUserName () + " !";
			} catch ( Exception exception ) {
				// Invalid user
				return null;
			} // End of try-catch block
		}
		
		/*
		 * Runs on the UI thread after doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute ( String welcomeMessage ) {
			// Display welcome message in baguette
			Baguette.showText ( HomeScreenActivity.this , welcomeMessage , Baguette.BackgroundColor.BLUE );
		}
		
	}
	
	/*
	 * Interface used to implement a helper method in order to generate / regenerate AsyncTask objects once they are executed, since they can only be executed once.
	 * 
	 * @see me.SyncWise.Android.Modules.MenuList.MenuItem.Task
	 */
	protected final MenuItem.Task journeyValidationTaskGenerator = new MenuItem.Task() {
		/*
		 * Gets the task class.
		 *
		 * @see me.SyncWise.Android.Modules.MenuList.MenuItem.Task#getTaskClass()
		 */
		@Override
		public Class < ? > getTaskClass () {
			// Return the task class
			return JourneyValidation.Task.class;
		}
		
		/*
		 * Generates an AsyncTask object.
		 *
		 * @see me.SyncWise.Android.Modules.MenuList.MenuItem.Task#getTask()
		 */
		@SuppressWarnings("rawtypes")
		@Override
		public AsyncTask getTask () {
			// Initialize and return a new journey validation task
			return new JourneyValidation.Task ( HomeScreenActivity.this , JourneyActivity.class , new Action ( new CallBack () {
				@Override
				public void perform ( Result result ) {
					// Determine if the result represents an error
					if ( Result.isError ( result ) )
						// Remove the task view
						slideOutTaskView ();
				}
			} ) );
		}
	};
	
	/*
	 * Called when the activity is starting.
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate ( Bundle savedInstanceState ) {
		
		// Populate home screen menu
		populateCallMenu ();
		
		// Set the activity content from a layout resource.
		setContentView ( R.layout.home_screen_activity );
		// Superclass method call	
		super.onCreate ( savedInstanceState );
		// Change the title associated with this activity
		setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.home_screen_activity_title ) );
		
		// Determine if a saved instance state is provided
		if ( savedInstanceState != null ) {
			// Restore the content provided by the saved instance state 
			isCreated = savedInstanceState.getBoolean ( IS_CREATED , isCreated );
		} // End if
		// Determine if this is the first activity creation or a re-creation
		if ( ! isCreated ) {
			// Set the flag
			isCreated = true;
			// Display welcome message
			new Welcome ().execute ();
		} // End if
	}
	
	/**
	 * Populate the call menu.
	 */
	protected abstract void populateCallMenu ();
	
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
	}
	
    /*
     * Called to retrieve per-instance state from an activity before being killed so that the state can be restored in onCreate(Bundle) or onRestoreInstanceState(Bundle).
     *
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    protected void onSaveInstanceState ( Bundle outState ) {
    	// Superclass method call
    	super.onSaveInstanceState ( outState );
    	// Save the content of isCreated in the outState bundle
    	outState.putBoolean ( IS_CREATED , isCreated );
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
				AppResources.getInstance ( this ).getString ( this , R.string.log_out_confirmation_message ) ,
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
	
}