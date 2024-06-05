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
import me.SyncWise.Android.AppToast;
import me.SyncWise.Android.AppVersion;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Modules.Sync.SyncActivity;
import me.SyncWise.Android.Widgets.SlideView;
import me.SyncWise.Android.Widgets.SlideView.OnSlideListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.BufferType;

/**
 * Activity used as a home screen displaying the company profile : company logo and description along with solution logo.
 * 
 * @author Elias
 * @sw.todo	<b>Company Profile Implementation :</b><br>
 * <ul>
 * <li>Extend this class.</li>
 * <li>Set the next activity to start using the {@link #setActivity(Class)} method in the {@link Activity#onCreate(Bundle)} method.</li>
 * <li>Assign the solution logo resource id (drawable). The ID of the image view is {@code R.id.icon_logo_solution}. <em>Optional but Recommended</em><br>
 * The width and height are automatically computed according to the current screen size / orientation.</li>
 * <li>Do not forget to add your new class in the {@code AndroidManifest.xml} file.</li>
 * </ul>
 */
public class CompanyProfileActivity extends Activity implements OnSlideListener {

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
	 * {@link java.lang.Class Class} activity reference.
	 */
	private Class < ? > activity;
	
	/**
	 * Setter - {@link #activity}
	 * 
	 * @param activity	{@link java.lang.Class Class} activity reference.
	 */
	public void setActivity ( final Class < ? > activity ) {		
		// Check if the provided class is valid
		if ( activity != null )
			// Store the new activity
			this.activity = activity;
	}
	
	/**
	 * Getter - {@link #activity}
	 * 
	 * @return	{@link java.lang.Class Class} activity reference.
	 */
	public Class < ? > getActivity () {
		// Return the activity class
		return activity;
	}
	
	/**
	 * Starts the new activity defined by {@link #activity} and finishes the current activity (if {@link #activity} is valid).
	 */
	public void startActivity () {
		try {
			// Start the new activity
			startActivity ( new Intent ( this , getActivity () ) );
			// Close this activity
			finish ();
		} catch ( Exception exception ) {
			// Invalid activity, display toast
			new AppToast ( this ).setGravity ( Gravity.BOTTOM )
				.setYOffset ( (int) TypedValue.applyDimension ( TypedValue.COMPLEX_UNIT_DIP , 40 , getResources ().getDisplayMetrics () ) )
				.setDuration ( Toast.LENGTH_LONG )
				.setIcon ( R.drawable.warning )
				.setText ( getString ( R.string.invalid_activity_message ) ).show ();
		} // End of try-catch block
	}
	
	/*
	 * Called when the activity is starting.
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@SuppressLint("DefaultLocale")
	@Override
	public void onCreate ( Bundle savedInstanceState ) {
		// Superclass method call	
		super.onCreate ( savedInstanceState );
		
		// Set the activity to start based on if the device is properly started (a user code should be stored)
		setActivity ( DatabaseUtils.getCurrentUserCode ( this ) == null ? SyncActivity.class : LoginActivity.class );
		
		// Determine if a saved instance state is provided
		if ( savedInstanceState != null ) {
			// Restore the content provided by the saved instance state 
			isCreated = savedInstanceState.getBoolean ( IS_CREATED , isCreated );
		} // End if
		
		// Hide title bar
		requestWindowFeature ( Window.FEATURE_NO_TITLE );
		// Hide status bar
		getWindow().setFlags ( WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN );
		// Set the activity content from a layout resource.
		setContentView ( R.layout.company_profile_activity );
		
		// Declare and initialize a point object used to store the screen size
		Point size = new Point();
		// Compute the screen size
		getWindowManager ().getDefaultDisplay ().getSize ( size );
		
		// Retrieve a reference to the solution logo
		ImageView solutionLogo = (ImageView) findViewById ( R.id.icon_logo_solution );
		// Assign the icon height
		solutionLogo.getLayoutParams ().height = size.y * 1 / 5;
		
		// Retrieve a reference to the company profile layout
		LinearLayout profileLayout = (LinearLayout) findViewById ( R.id.layout_company_profile );
		// Assign the layout height
		profileLayout.getLayoutParams ().height = size.y * 3 / 5;
		// Assign the layout width
		profileLayout.getLayoutParams ().width = size.x * 3 / 5;
		
		// Retrieve a reference to the company logo
		ImageView logoSyncWise = (ImageView) findViewById ( R.id.icon_logo );
		// Assign the logo with
		logoSyncWise.getLayoutParams ().width = profileLayout.getLayoutParams ().width - profileLayout.getPaddingLeft () - profileLayout.getPaddingRight ();
		// Assign the logo height
		logoSyncWise.getLayoutParams ().height = profileLayout.getLayoutParams ().height / 3;
		
		// Retrieve a reference to the company profile description
		TextView profileDescription = (TextView) findViewById ( R.id.label_profile_description );
		// Set the profile description
		profileDescription.setText ( AppResources.getInstance ( this ).getString ( this , R.string.company_profile_description ) );
		
		// Retrieve a reference to the slide view
		SlideView slideView = (SlideView) findViewById ( R.id.slide_view );
		// Set the slide view label
		slideView.setText ( AppResources.getInstance ( this ).getString ( this , R.string.start_label ).toUpperCase () );
		// Assign a slide listener to the slide view
		slideView.setOnSlideListener ( this );
		
		// Retrieve a reference to the application version label
		TextView appVersion = (TextView) findViewById ( R.id.label_app_version );
		// Apply the layout gravity attribute
		// Due to a bug, it cannot be applied in XML when using the include tag (and hence should be done programmatically)
		( (FrameLayout.LayoutParams) appVersion.getLayoutParams () ).gravity = Gravity.BOTTOM | Gravity.RIGHT;
		// Display the application version
		appVersion.setText ( AppVersion.getSpannableText ( this ) , BufferType.SPANNABLE );
		
		// Determine if this is the first activity creation or a re-creation
		if ( ! isCreated ) {
			// Set the flag
			isCreated = true;
			// Declare and initialize sliding animations
			Animation slideInLeft = AnimationUtils.loadAnimation ( this , R.anim.slide_in_left );
			Animation slideInRight = AnimationUtils.loadAnimation ( this , R.anim.slide_in_right );
			
			// Assign sliding animations to views
			solutionLogo.startAnimation ( slideInLeft );
			profileLayout.startAnimation ( slideInRight );
			slideView.startAnimation ( slideInLeft );
		} // End if
		
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
				AppResources.getInstance ( CompanyProfileActivity.this ).getString ( CompanyProfileActivity.this , R.string.exit_confirmation_message ) ,
				AppDialog.ButtonsType.YesNo ,
				new DialogInterface.OnClickListener () {
					@Override
					public void onClick ( DialogInterface dialog , int which ) {
						// Determine the clicked button
						switch ( which ) {
						case DialogInterface.BUTTON_POSITIVE:
							// Finish activity
							CompanyProfileActivity.this.finish ();
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
			isCreated = false;
			activity = null;
			( (SlideView) findViewById ( R.id.slide_view ) ).setOnSlideListener ( null );
		} // End if
    }

	/*
	 * Called when a slider has been slid.
	 *
	 * @see me.SyncWise.Android.Widgets.SlideView.OnSlideListener#onSlide()
	 */
	@Override
	public void onSlide ( SlideView slideView ) {
		// Start a new activity
		startActivity ();
	}
	
}