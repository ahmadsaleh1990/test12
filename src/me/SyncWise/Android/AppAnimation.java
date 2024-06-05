/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Transformation;

/**
 * Helper Class used to animate views.
 * 
 * @author Elias
 *
 */
public class AppAnimation {

	/**
	 * Enumeration used to define the animation direction.
	 * 
	 * @author Elias
	 *
	 */
	public static enum Direction {
		HORIZONTAL ,
		VERTICAL
	}
	
	/**
	 * Expands a view vertically or horizontally to the required height or width during the provided duration.<br>
	 * At the beginning of the animation, the visibility of the view is set to {@link android.view.View#VISIBLE VISIBLE}.
	 * 
	 * @param view	The view to expand.
	 * @param width	The required width in pixels <em>or</em> one of the {@link android.view.ViewGroup.LayoutParams LayoutParams} constants.
	 * @param height	The required height in pixels <em>or</em> one of the {@link android.view.ViewGroup.LayoutParams LayoutParams} constants.
	 * @param duration	The duration of the animation in milliseconds.
	 * @param direction	An enumeration constant of {@link me.SyncWise.Android.AppAnimation.Direction Direction} indicating the expand direction.
	 */
	public static void expand ( final View view , final int width , final int height , final long duration , final Direction direction ) {
		// Measure the view using the provided width and height
		view.measure ( width , height );
		// Retrieve the measured height and width
	    final int targetHeight = view.getMeasuredHeight ();
	    final int targetWidth = view.getMeasuredWidth ();
	    // Check the expand direction
	    if ( direction == Direction.VERTICAL )
		    // Reset the view's height
		    view.getLayoutParams ().height = 0;
	    else
	    	// Reset the view's width
	    	view.getLayoutParams ().width = 0;
	    // Make the view visible (the view will not be displayed because its height / width is 0)
	    view.setVisibility ( View.VISIBLE );
	    // Declare and initialize an animation to expand the view
	    Animation animation = new Animation () {
	    	/*
	    	 * Helper for getTransformation.
	    	 *
	    	 * @see android.view.animation.Animation#applyTransformation(float, android.view.animation.Transformation)
	    	 */
	        @Override
	        protected void applyTransformation ( float interpolatedTime , Transformation transformation ) {
	        	// Check the expand direction
	        	if ( direction == Direction.VERTICAL )
		        	// Set the view height
		        	view.getLayoutParams ().height = interpolatedTime == 1 ? height : (int) ( targetHeight * interpolatedTime );
	        	else
		        	// Set the view width
		        	view.getLayoutParams ().width = interpolatedTime == 1 ? width : (int) ( targetWidth * interpolatedTime );
	        	// schedule a layout pass of the view tree
	        	view.requestLayout ();
	        }

	        /*
	         * Indicates whether or not this animation will affect the bounds of the animated view.
	         *
	         * @see android.view.animation.Animation#willChangeBounds()
	         */
	        @Override
	        public boolean willChangeBounds () {
	            return true;
	        }
	    };
	    // Set the animation duration
	    animation.setDuration ( duration );
	    // Start the animation
	    view.startAnimation ( animation );
	}
	
	/**
	 * Collapses a view vertically or horizontally to 0 height or width during the provided duration.<br>
	 * At the end of the animation, the visibility of the view is set to {@link android.view.View#GONE GONE}.
	 * 
	 * @param view	The view to collapse.
	 * @param width	The initial width in pixels <em>or</em> one of the {@link android.view.ViewGroup.LayoutParams LayoutParams} constants.
	 * @param height	The initial height in pixels <em>or</em> one of the {@link android.view.ViewGroup.LayoutParams LayoutParams} constants.
	 * @param duration	The duration of the animation in milliseconds.
	 * @param direction	An enumeration constant of {@link me.SyncWise.Android.AppAnimation.Direction Direction} indicating the collapse direction.
	 * @param animationListner	A {link android.view.animation.Animation.AnimationListener AnimationListener} used to control the collapse animation.
	 */
	public static void collapse ( final View view , final int width , final int height , final long duration , final Direction direction , final AnimationListener animationListner ) {
		// Measure the view using the provided width and height
		view.measure ( width , height );
		// Retrieve the measured height and width
		final int initialHeight = view.getMeasuredHeight ();
		final int initialWidth = view.getMeasuredWidth ();
		// Make the view visible
		view.setVisibility ( View.VISIBLE );
		// Declare and initialize an animation to expand the view
	    Animation animation = new Animation () {
	    	/*
	    	 * Helper for getTransformation.
	    	 *
	    	 * @see android.view.animation.Animation#applyTransformation(float, android.view.animation.Transformation)
	    	 */
	        @Override
	        protected void applyTransformation ( float interpolatedTime , Transformation transformation ) {
	        	// Determine if the animation has ended
	            if ( interpolatedTime == 1 )
	            	// Hide the view
	                view.setVisibility ( View.GONE );
	            else {
		        	// Check the expand direction
		        	if ( direction == Direction.VERTICAL )
		            	// Set the view height
		                view.getLayoutParams ().height = initialHeight - (int) ( initialHeight * interpolatedTime );
		        	else
		            	// Set the view width
		                view.getLayoutParams ().width = initialWidth - (int) ( initialWidth * interpolatedTime );
	                // schedule a layout pass of the view tree
	                view.requestLayout ();
	            } // End else
	        }

	        /*
	         * Indicates whether or not this animation will affect the bounds of the animated view.
	         *
	         * @see android.view.animation.Animation#willChangeBounds()
	         */
	        @Override
	        public boolean willChangeBounds () {
	            return true;
	        }
	    };
	    // Set the animation duration
	    animation.setDuration ( duration );
	    // Check if the animation listener is valid
	    if ( animationListner != null )
	    	// Set the animation listener
	    	animation.setAnimationListener ( animationListner );
	    // Start the animation
	    view.startAnimation ( animation );
	}
	
}