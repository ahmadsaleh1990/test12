/**
 * Copyright 2014 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Widgets;

import me.SyncWise.Android.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Number Picker Widget.
 * 
 * @author Elias
 *
 */
public class TinyNumberPicker extends NumberPicker {

	/**
	 * Float used to store the icon size.
	 */
	private float iconSize;
	
	/**
	 * Constructor.
	 * 
	 * @param context	The application context.
	 */
	public TinyNumberPicker ( Context context ) {
		// Superclass method call
		super ( context );
		// Initialize tiny number picker view
		initialize ();
	}
	
	/**
	 * Constructor (overloaded).
	 * 
	 * @param context	The application context.
	 * @param attrs	{@link android.util.AttributeSet AttributeSet} object containing information retrieved from the compiled XML.
	 */
	public TinyNumberPicker ( Context context , AttributeSet attrs ) {
		// Superclass method call
		super ( context , attrs );
		// Check if the provided attribute set is valid
		if ( attrs != null ) {
			// Retrieve styled attribute information in this Context's theme
			TypedArray attributes = getContext ().obtainStyledAttributes ( attrs , R.styleable.TinyNumberPicker , 0 , 0 );
			// Retrieve the 'iconWidth' style attribute
			iconSize = attributes.getDimension ( R.styleable.TinyNumberPicker_iconSize , -1 );
			// Give back a previously retrieved array, for later re-use
			attributes.recycle ();
		} // End if
		// Initialize tiny number picker view
		initialize ();
	}
	
	/*
	 * Gets the layout resource ID.
	 *
	 * @see me.SyncWise.Android.Widgets.NumberPicker#getLayout()
	 */
	@Override
	protected int getLayout () {
		// Return the layout resource ID
		return R.layout.tiny_number_picker;
	}
	
	/*
	 * Method used to to set the view's orientation.
	 *
	 * @see me.SyncWise.Android.Widgets.NumberPicker#setOrientation()
	 */
	@Override
	protected void setOrientation () {
		// Set the orientation of the current linear layout
		setOrientation ( LinearLayout.HORIZONTAL );
		// Set the gravity in order to center the buttons
		setGravity ( Gravity.CENTER );
	}
	
	/**
	 * Initializes the number picker view components.
	 */
	private void initialize () {
		try {
			// Check if icon size is valid
			if ( iconSize <= 0 )
				// Invalid icon size
				return;
			// Retrieve a reference to the increment and decrement buttons
			View increment = findViewById ( R.id.icon_increment );
			View decrement = findViewById ( R.id.icon_decrement );
			// Set the appropriate size
			increment.getLayoutParams ().width = (int) iconSize;
			increment.getLayoutParams ().height = (int) iconSize;
			decrement.getLayoutParams ().width = (int) iconSize;
			decrement.getLayoutParams ().height = (int) iconSize;
		} catch ( Exception exception ) {
			// Do nothing
		} // End of try-catch block
	}
	
}