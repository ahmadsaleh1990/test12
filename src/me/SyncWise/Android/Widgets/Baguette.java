/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Widgets;

import me.SyncWise.Android.R;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * <b>Baguette</b> is a class used to display a message at the top of the application screen, under the status bar, in a simple layout.<br>
 * The <b>Baguette</b> is displayed with an enter and exit animations. There is no need to add any view in the activity layout.<br>
 * Simply use the {@link #showText(Activity, String, BackgroundColor)} method to display a <b>Baguette</b>.<br>
 * <br>
 * <b>Note :</b><br>
 * <ul>
 * <li>The <b>Baguette</b> duration is based on the number of words contained in the message.</li>
 * <li><b>Baguette</b> does not support message queuing !</li>
 * <li>Currently, the display of multi <b>Baguette</b> is not supported, if more than one <b>Baguette</b> is requested, only the first is displayed.</li>
 * </ul>
 * 
 * @author Elias
 *
 */
public class Baguette {
	
	/**
	 * Constant integer holding the baguette in and out animation duration.
	 */
	private static final int ANIMATION_DURATION = 500;
	
	/**
	 * Enumeration used to define the various baguette background colors.
	 * 
	 * @author Elias
	 *
	 */
	public static enum BackgroundColor {
		GREEN ,
		RED ,
		BLUE
	}
	
	/**
	 * Displays text in a baguette using in and out animations at the top of the main application screen under the status bar.<br>
	 * If the any of the provided arguments is invalid, no baguette is displayed.
	 * 
	 * @param activity	Reference to the activity to display the baguette in.
	 * @param message	String holding the message to display.
	 * @param color		An enumeration constant of {@link Baguette.BackgroundColor} indicating the baguette background color to use.
	 */
	public static void showText ( final Activity activity , final String message , final BackgroundColor color ) {
		// Determine if the arguments are valid
		if ( activity == null || TextUtils.isEmpty ( message ) || color == null )
			// One or more invalid argument
			return;
		// Determine if there is a baguette currently displayed
		if ( activity.findViewById ( R.id.baguette ) != null )
			// There is a baguette displayed, do nothing
			return;
		// Retrieve a reference to the activity's content layout 
		final FrameLayout content = (FrameLayout) activity.findViewById ( android.R.id.content );
		// Declare and initialize a text view used to display the text
		final TextView baguette = new TextView ( activity );
		// Set the baguette ID
		baguette.setId ( R.id.baguette );
		// Set the baguette text gravity
		baguette.setGravity ( Gravity.CENTER );
		// Set the baguette text style
		baguette.setTypeface ( baguette.getTypeface () , Typeface.BOLD );
		// Set the baguette text
		baguette.setText ( message );
		// Set the baguette text color
		baguette.setTextColor ( Color.BLACK );
		// Set the baguette text appearance
		baguette.setTextAppearance ( activity , R.style.TextAppearance_Medium );
		// Set the baguette layout parameters and display it at the top of the content layout
		baguette.setLayoutParams ( new FrameLayout.LayoutParams ( FrameLayout.LayoutParams.MATCH_PARENT , FrameLayout.LayoutParams.WRAP_CONTENT , Gravity.TOP ) );
		// Set the baguette inner padding
		int padding = (int) TypedValue.applyDimension ( TypedValue.COMPLEX_UNIT_DIP , 5 , activity.getResources ().getDisplayMetrics () );
		baguette.setPadding ( padding , padding , padding , padding );
		// Set the baguette background color as provided
		switch ( color ) {
		case GREEN:
			baguette.setBackgroundColor ( Color.parseColor ( "#ff99cc00" ) );
			break;
		case RED:
			baguette.setBackgroundColor ( Color.parseColor ( "#ffff4444" ) );
			break;
		case BLUE:
			baguette.setBackgroundColor ( Color.parseColor ( "#ff33b5e5" ) );
			break;
		}
		// Compute the baguette duration based on the text content
		// Determine the number of words
		int size = message.split ( " " ).length;
		// For the first 5 words provide 1500 milliseconds
		int duration = 2000;
		size = ( size < 5 ? 0 : size - 5 );
		// For every additional 5 words, add 1000 milliseconds
		while ( size > 0 ) {
			size -= 5;
			duration += 1500;
		} // End while loop
		// Build an animation for the baguette
		Animation animation = getAnimation ( duration );
		// Set an animation listener mainly used to remove the baguette after its animation ends
		animation.setAnimationListener ( new AnimationListener() {
			
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
				// Hide the baguette
				baguette.setVisibility ( View.GONE );
				// Declare and initialize a handler used to remove the baguette
				Handler handler = new Handler ();
				// Execute a runnable in 200 milliseconds to make sure the animation ended
				handler.postDelayed ( new Runnable() {
					@Override
					public void run () {
						try {
							// Retrieve a reference to the baguette's parent
							ViewGroup parent = (ViewGroup) baguette.getParent ();
							// Remove the baguette from the its parent
							parent.removeView ( baguette );
						} catch ( Exception exception ) {
							// Do nothing
						} // End of try-catch block
					}
				} , 200 );
			}
		} );
		// Add the baguette to the content layout
		content.addView ( baguette );
		// Start baguette animation set
		baguette.startAnimation ( animation );
	}
	
	/**
	 * Builds and returns a slide in/out animation.
	 * 
	 * @return	An {@link android.view.animation.AnimationSet AnimationSet} combined with the necessary animations to perform a slide in/out effect.
	 */
	private static AnimationSet getAnimation ( final int duration ) {
		// Declare and initialize the slide in animation
		Animation in = new TranslateAnimation ( Animation.ABSOLUTE , 0 , Animation.ABSOLUTE , 0 , Animation.RELATIVE_TO_SELF , -1 , Animation.RELATIVE_TO_SELF , 0 );
		// Set the animation duration
		in.setDuration ( ANIMATION_DURATION );
		// Load the slide out top animation
		Animation out = new TranslateAnimation ( Animation.ABSOLUTE , 0 , Animation.ABSOLUTE , 0 , Animation.RELATIVE_TO_SELF , 0 , Animation.RELATIVE_TO_SELF , -1 );
		// Set the animation duration
		out.setDuration ( ANIMATION_DURATION );
		// Set the out animation starting offset for it to start after the in animation by a specific duration
		out.setStartOffset ( in.getDuration () + duration );
		// Declare and initialize an animation set used to host both in and out animations
		AnimationSet rootSet = new AnimationSet ( true );
		// Add both animations
		rootSet.addAnimation ( in );
		rootSet.addAnimation ( out );
		// Return the animation set
		return rootSet;
	}
	
	/**
	 * Removes any previously displayed baguette from an activity.
	 * 
	 * @param activity	Reference to the activity to remove any displayed baguette from.
	 */
	public static void remove ( final Activity activity ) {
		// Determine if the activity is valid
		if ( activity == null )
			// Invalid activity
			return;
		// Declare and initialize a text view reference to host a baguette (if available)
		TextView baguette = null;
		try {
			// Search for an available baguette
			baguette = (TextView) activity.findViewById ( R.id.baguette );
			// Determine if the baguette reference is valid
			if ( baguette != null && baguette.getParent () != null ) {
				// Clear the baguette animation
				baguette.clearAnimation ();
				// Remove the baguette from the activity
				( (ViewGroup) baguette.getParent () ).removeView ( baguette );
			} // End if
		} catch ( Exception exception ) {
			// Do nothing
		} // End of try-catch block
	}
	
}