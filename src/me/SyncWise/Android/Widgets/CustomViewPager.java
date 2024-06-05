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

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Custom implementation of the {@link android.support.v4.view.ViewPager ViewPager} with an additional functionality.
 * 
 * @author Elias
 *
 */
public class CustomViewPager extends ViewPager {

	/**
	 * Flag used to indicate if the view pager is enabled or not.
	 */
	private boolean isEnabled;
	
	/**
	 * Constructor.
	 * 
	 * @param context	The application context.
	 */
	public CustomViewPager ( Context context ) {
		// Superclass method call
		super ( context );
		// View pager is enabled
		this.isEnabled = true;
	}

	/**
	 * Constructor.
	 * 
	 * @param context	The application context.
	 * @param attrs	{@link android.util.AttributeSet AttributeSet} object containing information retrieved from the compiled XML.
	 */
    public CustomViewPager ( Context context , AttributeSet attrs ) {
		// Superclass method call
        super ( context , attrs );
		// View pager is enabled
        this.isEnabled = true;
    }
	
    /*
     * Implement this method to handle touch screen motion events.
     *
     * @see android.support.v4.view.ViewPager#onTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onTouchEvent ( MotionEvent event ) {
    	try {
	    	// Check if the view pager is enabled
	        if ( this.isEnabled )
	        	// Superclass method call
	            return super.onTouchEvent ( event );
		} catch ( Exception exception ) {
			// Do nothing
		} // End of try-catch block
        // Otherwise, disabled all touch events
        return true;
    }

    /*
     * Implement this method to intercept all touch screen motion events.
     *
     * @see android.support.v4.view.ViewPager#onInterceptTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onInterceptTouchEvent ( MotionEvent event ) {
    	try {
	    	// Check if the view pager is enabled
	        if ( this.isEnabled )
	        	// Super class method call
	            return super.onInterceptTouchEvent ( event );
    	} catch ( Exception exception ) {
			// Do nothing
		} // End of try-catch block
        // Otherwise intercept all touch events
        return true;
    }

    /**
     * Setter - {@link #isEnabled}
     * 
     * @param isEnabled	Flag used to indicate if the view pager is enabled or not.
     */
    public void setPagerEnabled ( final boolean isEnabled ) {
    	// Set flag
        this.isEnabled = isEnabled;
    }
}