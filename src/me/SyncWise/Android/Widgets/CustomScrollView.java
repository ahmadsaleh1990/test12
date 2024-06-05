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
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Custom implementation of the {@link android.widget.ScrollView ScrollView} with an additional functionality.
 * 
 * @author Elias
 *
 */
public class CustomScrollView extends ScrollView {

	/**
	 * Flag used to indicate if the scroll view is enabled or not.
	 */
	private boolean isEnabled;
	
	/**
	 * Constructor.
	 * 
	 * @param context	The application context.
	 */
	public CustomScrollView ( Context context ) {
		// Superclass method call
		super ( context );
		// Scroll view is enabled
		this.isEnabled = true;
	}

	/**
	 * Constructor.
	 * 
	 * @param context	The application context.
	 * @param attrs	{@link android.util.AttributeSet AttributeSet} object containing information retrieved from the compiled XML.
	 */
    public CustomScrollView ( Context context , AttributeSet attrs ) {
		// Superclass method call
        super ( context , attrs );
		// Scroll view is enabled
        this.isEnabled = true;
    }
	
    /**
     * Constructor.
     * 
     * @param context	The application context.
     * @param attrs	{@link android.util.AttributeSet AttributeSet} object containing information retrieved from the compiled XML.
     * @param defStyle	Integer holding the default style resource ID.
     */
	public CustomScrollView ( Context context , AttributeSet attrs , int defStyle ) {
		// Superclass method call
		super ( context , attrs , defStyle );
		// Scroll view is enabled
        this.isEnabled = true;
	}

	/*
	 * Implement this method to handle touch screen motion events.
	 *
	 * @see android.widget.ScrollView#onTouchEvent(android.view.MotionEvent)
	 */
    @Override
    public boolean onTouchEvent ( MotionEvent event ) {
    	try {
	    	// Check if the scroll view is enabled
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
     * @see android.widget.ScrollView#onInterceptTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onInterceptTouchEvent ( MotionEvent event ) {
    	try {
	    	// Check if the scroll view is enabled
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
     * @param isEnabled	Flag used to indicate if the scroll view is enabled or not.
     */
    public void setScrollViewEnabled ( final boolean isEnabled ) {
    	// Set flag
        this.isEnabled = isEnabled;
    }
	
}