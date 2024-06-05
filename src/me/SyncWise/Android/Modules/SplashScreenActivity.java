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

import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.AppToast;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.DatabaseHelper;
import me.SyncWise.Android.Database.DatabaseUtils;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Activity used as splash screen.
 * 
 * @author Elias
 * @sw.todo	<b>Splash Screen Implementation :</b><br>
 * <ul>
 * <li>If you want the default implementation, simply add this class in the {@code AndroidManifest.xml} file.<br>
 * The default behavior is described below:
 * <ul>
 * <li>The next activity is set as {@code CompanyProfileActivity}</li>
 * <li>No initial loading is performed.</li>
 * </ul></li>
 * </ul>
 * <br><em><h1>OR</h1></em><br>
 * In order to customize the module, please follow the steps below :
 * <ul>
 * <li>Extend this class.</li>
 * <li>Set the next activity to start using the {@link #setActivity(Class)} method :<br>
 * <ul>
 * <li>In the {@link Activity#onCreate(Bundle)} method, <b>AFTER</b> making the superclass call. <em>Recommended</em></li>
 * <li>In the background thread via the {@link #initalLoading()} method. <em>NOT Recommended ! The activity may have lost its instance while the AsyncTask helper saves the activity which will not be saved.</em></li>
 * </ul></li>
 * <li>Implement the {@link #initialLoading} method. Its content is executed on a separate thread during loading.
 * <li>Do not forget to add your new class in the {@code AndroidManifest.xml} file.</li>
 * </ul>
 */
public class SplashScreenActivity extends Activity implements AnimationListener {
	
	/**
	 * Boolean used to indicate if the activity is displayed on the screen.
	 */
	private boolean isDisplayed;
	
	/**
	 * AsyncTask helper class used to perform initial background work of the application.
	 * 
	 * @author Elias
	 * @sw.todo	<b>Additional background work :</b><br>The background execution of this helper class is extended through the implementation of the {@link SplashScreenActivity#initialLoading} method.<br>
	 * The content of this method is executed in the background thread.
	 *
	 */
	private class InitialLoading extends AsyncTask < Void , Void , Void > {

		/**
		 * Boolean used to indicate if the background thread has finished executing.
		 */
		private boolean isFinished;
		
		/**
		 * Indicates if the background thread of the AsyncTask object has finished executing.<br>
		 * This flag is used instead of {@link android.os.AsyncTask.Status AsyncTask.Status} because {@link android.os.AsyncTask.Status#FINISHED AsyncTask.Status#FINISHED} is set after {@link android.os.AsyncTask#onPostExecute(Object) onPostExecute(Object)} has finished executing,
		 *  in contrast to this flag which is set after {@link android.os.AsyncTask#doInBackground(Object...) AsyncTask#doInBackground(Object...)} has finished executing.
		 * 
		 * @return	Boolean indicating if the background thread has finished executing.
		 */
		public boolean isFinished () {
			// Return the AsyncTask background thread status
			return isFinished;
		}
		
		/*
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Void doInBackground ( Void ... params ) {
			// Initialize the AppResources helper
			AppResources.getInstance ( SplashScreenActivity.this );
			// Initialize the Database Utilities
			DatabaseUtils.getInstance ( SplashScreenActivity.this );
			// Execute additional loading
			initalLoading ();
			return null;
		}
		
		/*
		 * Runs on the UI thread after doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
	    protected void onPostExecute ( Void arg ) {
			// Indicate that the background thread is finished
			isFinished = true;
	        // Determine if the next activity can be started
			checkInitialLoading ();
	    }
		
	}
	
	/**
	 * {@link android.os.AsyncTask AsyncTask} object used to do initial loading in the background.
	 */
	private static InitialLoading initialLoading;
	
	/**
	 * Method executed in the background thread (could be implemented in subclasses if needed).<br>
	 * <b>Note :</b> This method is executed only once in the background thread (events such as rotation, incoming call and menu button press have no effect).<br>
	 * Default implementation is an empty method : does nothing.
	 */
	protected void initalLoading () {};
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #animationStarted}.
	 */
	private static final String ANIMATION_STARTED = SplashScreenActivity.class.getName () + ".ANIMATION_STARTED";
	
	/**
	 * Boolean used to indicate if the animation started.
	 */
	private boolean animationStarted;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #animationEnded}.
	 */
	private static final String ANIMATION_ENDED = SplashScreenActivity.class.getName () + ".ANIMATION_ENDED";
	
	/**
	 * Boolean used to indicate id the animation ended.
	 */
	private boolean animationEnded;
	
	/**
	 * Constant integer holding the splash animation duration in milliseconds.
	 */
	public static final int SPLASH_ANIMATION_DURATION = 3500;
	
	/**
	 * {@link java.lang.Class} activity reference.
	 */
	private Class < ? > activity;
	
	/**
	 * Setter - {@link #activity}
	 * 
	 * @param activity	{@link java.lang.Class} activity reference.
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
	 * @return	{@link java.lang.Class} activity reference.
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
	private void FixDatabase() {
	    DatabaseHelper DH = new DatabaseHelper(this);
	    
	    DH.AddField("Items", "IsSerializable", "INTEGER");
        DH.CreateTableSOSBrands()  ;
	    DH.CreateTableSOSCategories()  ;
	    DH.CreateTableSOSCategoryBrands ();
	    DH.CreateTableSOSTargetAssignment() ;
	    DH.CreateTableSOSTrackerHeaders();
	    DH.CreateTableSOSTrackerDetails();
	    DH.CreateTableSOSTargetDetails();
	    DH.CreateTableSOSTargetHeaders();
	    DH.AddField("SOSTrackerDetails", "HeadCompCode", "TEXT");
	    DH.AddField("SOSTargetDetails", "HeadCompCode", "TEXT");
	    DH.AddField("SOSTargetDetails", "HeadCompName", "TEXT");
	    DH.AddField("Prices", "UnitTaxAmount", "REAL");
	    DH.AddField("Prices", "CaseTaxAmount", "REAL");
	    DH.AddField("TransactionDetails", "TaxAmountBig", "REAL");
	    DH.AddField("TransactionDetails", "TaxAmountMedium", "REAL");
	    DH.AddField("TransactionDetails", "TaxAmountSmall", "REAL");
	    DH.AddField("TransactionDetails", "TotalExiceAmount", "REAL");
	    DH.CreateTableDuoUsers() ;
	    DH.AddField("Divisions", "ApplyFreeExiceTax", "INTEGER");
	    DH.AddField("Divisions", "ApplyExiceTax", "INTEGER");
	    DH.CreateTableMSLDivisions() ;
	    DH.CreateTableNewSkuListDivisions() ;
	    DH.CreateTableClientNewSkuList() ;
	    DH.AddField("TransactionDetails", "ItemMSL", "INTEGER");
	    DH.CreateTableTransactionDetailsMissedMSL() ;
	    DH.CreateTableDailyTargetAchievements() ;
	    DH.CreateTableUserVersions() ;
	    DH.AddField("Printers", "Status", "INTEGER");
	    DH.AddField("SOSTargetDetails", "ClientCode", "TEXT");
	    DH.CreateTableUserSpecialPriceLists();
	    
	    
	    
	    DH.CreateTableForceSyncJourneys();
	    DH.AddField("ForceSyncJourneys", "PassCode", "TEXT");
	    
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
		
		// Set the activity to start
		setActivity ( CompanyProfileActivity.class );
		FixDatabase();
		// Determine if a saved instance state is provided
		if ( savedInstanceState != null ) {
			// Restore the content provided by the saved instance state 
			animationStarted = savedInstanceState.getBoolean ( ANIMATION_STARTED , animationStarted );
			animationEnded = savedInstanceState.getBoolean ( ANIMATION_ENDED , animationEnded );
		} // End if
		
		// Hide title bar
		requestWindowFeature ( Window.FEATURE_NO_TITLE );
		// Hide status bar
		getWindow().setFlags ( WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN );
		// Set the activity content from a layout resource.
		setContentView ( R.layout.splash_screen_activity );
		
		// Declare and initialize a point object used to store the screen size
		Point size = new Point();
		// Compute the screen size
		getWindowManager ().getDefaultDisplay ().getSize ( size );
		// Compute the splash icon width
		int width = size.x * 2 / 3;
		// Compute the splash icon height
		int height = size.y * 2 / 3;
		
		// Retrieve a reference to the splash icon
		ImageView splashIcon = (ImageView) findViewById ( R.id.icon_splash );
		// Assign the new splash icon height
		splashIcon.getLayoutParams ().height = height;
		// Assign the new splash icon width
		splashIcon.getLayoutParams ().width = width;
		
		// Determine if the animation has previously started and never ended
		if ( animationStarted && ! animationEnded )
			// The animation will not start again and hence has ended
			animationEnded = true;
		// Determine if the animation has previously started
		if ( ! animationStarted ) {
			// Indicate that the animation started
			animationStarted = true;
			// Build an splash animation set
			AnimationSet animationSet = getAnimation ();
			// Set an animation listener used to determine when the animation stop (to display the progress bar)
			animationSet.setAnimationListener ( this );
			// Start animation on the splash icon
			splashIcon.startAnimation ( animationSet );
		} // End if
		else
			// Display the progress bar
			findViewById ( R.id.progress_bar ).setVisibility ( View.VISIBLE );
		
		// Determine if an initial loading AsyncTask has started
		if ( initialLoading == null ) {
			// Initialize the initial loading AsyncTask
			initialLoading = new InitialLoading ();
			// Execute the initial loading AsyncTask
			initialLoading.execute ();
		} // End if
	}

	/*
	 * Called after onRestoreInstanceState(Bundle), onRestart(), or onPause(), for your activity to start interacting with the user.
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume () {
		// Indicate that the activity is displayed
		isDisplayed = true;
		// Superclass method call
		super.onResume ();
        // Determine if the next activity can be started
		checkInitialLoading ();
	}
	
	/*
	 * Called as part of the activity lifecycle when an activity is going into the background, but has not (yet) been killed.
	 *
	 * @see android.app.Activity#onPause()
	 */
	@Override
    protected void onPause () {
		// Indicate that the activity is NOT displayed
		isDisplayed = false;
		// Superclass method call
		super.onPause ();
	}
	
    /*
     * Called to retrieve per-instance state from an activity before being killed so that the state can be restored in onCreate(Bundle) or onRestoreInstanceState(Bundle).
     *
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    protected void onSaveInstanceState ( Bundle outState ) {
    	// Superclass method call
    	super.onSaveInstanceState ( outState );
    	// Save the content of animationStarted in the outState bundle
    	outState.putBoolean ( ANIMATION_STARTED , animationStarted );
    	// Save the content of animationEnded in the outState bundle
    	outState.putBoolean ( ANIMATION_ENDED , animationEnded );
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
			initialLoading = null;
			activity = null;
		} // End if
    }
    
    /**
     * Determines if the initial loaded and/or the animation ended.
     * The following cases apply:<br>
     * <ul>
     * <li>Both the animation and the initial loading ended : the progress bar is hidden and a new activity is started (if valid).</li>
     * <li>The animation ended and the initial loading is either running or pending : the progress bar displayed.</li>
     * <li>Otherwise, nothing happens.</li>
     * </ul>
     */
    private synchronized void checkInitialLoading () {
    	// Determine if the activity is display
    	if ( ! isDisplayed )
    		// The activity is not display, do nothing
    		return;
    	// Otherwise the activity is displayed
    	// Determine if both the animation and the initial loading ended
    	if ( animationEnded && initialLoading.isFinished () ) {
    		// Hide the progress bar
    		findViewById ( R.id.progress_bar ).setVisibility ( View.GONE );
    		// Start a new activity
    		startActivity ();
    	} // End if
    	// Determine if the animation ended (the initial loading is either pending or running)
    	else if ( animationEnded )
    		// Display the progress bar
    		findViewById ( R.id.progress_bar ).setVisibility ( View.VISIBLE );
    }
    
	/**
	 * Builds and returns a bouncing splash animation.<br>
	 * The splash animation duration is set by {@link #SPLASH_ANIMATION_DURATION}.
	 * 
	 * @return	An {@link android.view.animation.AnimationSet} combined with the necessary animations to perform a bouncing splash animation.
	 */
	private AnimationSet getAnimation () {
		// Declare and initialize the main animation set
		AnimationSet rootSet = new AnimationSet ( true );
		// Set a bounce interpolator in order to add a bouncing effect at the end
		rootSet.setInterpolator ( new BounceInterpolator () );
		
		// Declare and initialize a scale animation
		ScaleAnimation scale = new ScaleAnimation ( 0 , 1 , 0 , 1 , ScaleAnimation.RELATIVE_TO_SELF , 0.5f , ScaleAnimation.RELATIVE_TO_SELF , 0.5f );
		// Clear the starting offset for the animation to start at the beginning
		scale.setStartOffset ( 0 );
		// Set the animation duration
		scale.setDuration ( SPLASH_ANIMATION_DURATION );
		// Make the transformation performed by the animation persist when it is finished
		scale.setFillAfter ( true );
		
		// Add the translation animation to the main animation set
		rootSet.addAnimation ( scale );
		
		// Declare and initialize a translation animation
		TranslateAnimation translate = new TranslateAnimation ( 0 , 0 , -400 , 0 );
		// Clear the starting offset for the animation to start at the beginning
		translate.setStartOffset ( 0 );
		// Set the animation duration
		translate.setDuration( SPLASH_ANIMATION_DURATION );
		// Make the transformation performed by the animation persist when it is finished
		translate.setFillAfter ( true );

		
		// Declare and initialize a child animation set
		AnimationSet childSet = new AnimationSet ( true );
		// Clear the starting offset for the animation set to start at the beginning
		childSet.setStartOffset ( 0 );
		// Add the scale animation to the child animation set
		childSet.addAnimation ( translate );
		// Set a bounce interpolator in order to add a bouncing effect at the end
		childSet.setInterpolator ( new BounceInterpolator () );
		// Add the child animation set to the main animation set
		rootSet.addAnimation ( childSet );
		
		// Return the main animation set
		return rootSet;
	}
	
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
	
	/*
	 * Notifies the end of the animation.
	 *
	 * @see android.view.animation.Animation.AnimationListener#onAnimationEnd(android.view.animation.Animation)
	 */
	@Override
	public void onAnimationEnd ( Animation animation ) {
		// Indicate that the animation ended
		animationEnded = true;
        // Determine if the next activity can be started
		checkInitialLoading ();
	}
	
}