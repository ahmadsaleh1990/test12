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

import me.SyncWise.Android.R;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Helper Class used to display toasts.<br>
 * A toast has a :
 * <ul>
 * <li>An icon ({@link android.graphics.drawable.Drawable Drawable}).</li>
 * <li>A message ({@link java.lang.String String}).</li>
 * <li>A gravity ({@link android.view.Gravity Gravity})</li>
 * <li>A horizontal offset.</li>
 * <li>A vertical offset.</li>
 * <li>A duration ({@link android.widget.Toast#LENGTH_LONG LENGTH_LONG} or {@link android.widget.Toast#LENGTH_SHORT LENGTH_SHORT}).</li>
 * </ul>
 * The following default values are applied (if none are specified):
 * <ul>
 * <li>The gravity is set to {@link android.view.Gravity#CENTER_VERTICAL CENTER_VERTICAL}.</li>
 * <li>The duration is set to {@link android.widget.Toast#LENGTH_SHORT LENGTH_SHORT}.</li>
 * <li>Both horizontal and vertical offsets are set to 0.</li>
 * </ul>
 * @author Elias
 *
 */
public class AppToast {

	/**
	 * The application context.
	 */
	private Context context;
	
	/**
	 * Reference to the inflated toast view.
	 */
	private View toastView;
	
	/**
	 * Reference to the icon resource id used in the toast.
	 */
	private int iconId;
	
	/**
	 * Default {@link android.view.Gravity Gravity} value.
	 */
	private static final int DEFAULT_GRAVITY = Gravity.CENTER_VERTICAL;
	
	/**
	 * {@link android.view.Gravity Gravity} value.
	 */
	private int gravity;
	
	/**
	 * Default horizontal offset.
	 */
	private static final int DEFAULT_X_OFFSET = 0;
	
	/**
	 * Horizontal offset.
	 */
	private int xOffset;
	
	/**
	 * Default vertical offset.
	 */
	private static final int DEFAULT_Y_OFFSET = 0;
	
	/**
	 * Vertical offset.
	 */
	private int yOffset;
	
	/**
	 * Default toast duration.
	 * 
	 * @see android.widget.Toast#LENGTH_LONG
	 * @see android.widget.Toast#LENGTH_SHORT
	 */
	private static final int DEFAULT_DURATION = Toast.LENGTH_SHORT;
	
	/**
	 * Toast duration.
	 * 
	 * @see android.widget.Toast#LENGTH_LONG
	 * @see android.widget.Toast#LENGTH_SHORT
	 */
	private int duration;
	
	/**
	 * Constructor.
	 * 
	 * @param context	The application context.
	 */
	public AppToast ( final Context context ) {
		try {
			// Inflate the toast view with the custom toast layout
			toastView = LayoutInflater.from ( context ).inflate ( R.layout.app_toast , null );
			// Initialize attributes using the default values
			this.context = context;
			gravity = DEFAULT_GRAVITY;
			xOffset = DEFAULT_X_OFFSET;
			yOffset = DEFAULT_Y_OFFSET;
			duration = DEFAULT_DURATION;
		} catch ( Exception exception ) {
			// Error inflating view
		} // End of try-catch block
	}
	
	/**
	 * Sets the toast text - the {@link android.widget.TextView TextView} in the inflated toast view is directly set.
	 * 
	 * @param text	String used to set as the toast text.
	 * @return	Generic THIS (self) - Reference to the current {@link me.SyncWise.Android.AppToast AppToast} object.
	 */
	public AppToast setText ( final String text ) {
		// Determine if the toast view is valid
		if ( toastView == null )
			// Invalid toast view, exit method
			// Allow for cascaded methods calls
			return this;
		// Determine if the provided text is valid
		if ( text == null )
			// Invalid text, exit method
			// Allow for cascaded methods calls
			return this;
		try {
			// Otherwise, set the toast text
			( (TextView) toastView.findViewById ( R.id.label_toast ) ).setText ( text );
		} catch ( Exception exception ) {
			// Do nothing
		} // End of try-catch block
		// Allow for cascaded methods calls
		return this;
	}
	
	/**
	 * Returns the toast text - the text of the {@link android.widget.TextView TextView} in the inflated toast view is returned.
	 * 
	 * @return	String holding the toast text.
	 */
	public String getText () {
		try {
			// Return the assigned toast text
			return ( (TextView) toastView.findViewById ( R.id.label_toast ) ).getText ().toString ();
		} catch ( Exception exception ) {
			// Invalid toast view
			return null;
		} // End of try-catch block
	}
	
	/**
	 * Setter - {@link #iconId}<br>
	 * The icon resource id (if valid) is applied to the {@link android.widget.ImageView ImageView} in the inflated toast view.
	 * 
	 * @param iconId	icon (drawable) resource id.
	 * @return	Generic THIS (self) - Reference to the current {@link me.SyncWise.Android.AppToast AppToast} object.
	 */
	public AppToast setIcon ( final int iconId ) {
		// Determine if the toast view is valid
		if ( toastView == null )
			// Invalid toast view, exit method
			// Allow for cascaded methods calls
			return this;
		// Determine if the provided icon resource id is valid
		if ( iconId <= 0 )
			// Invalid icon id, exit method
			// Allow for cascaded methods calls
			return this;
		try {
			// Otherwise, set the toast icon
			( (ImageView) toastView.findViewById ( R.id.icon_toast ) ).setImageResource ( iconId );
			// Update the icon id
			this.iconId = iconId;
		} catch ( Exception exception ) {
			// Do nothing
		} // End of try-catch block
		// Allow for cascaded methods calls
		return this;
	} // End of try-catch block
	
	/**
	 * Getter - {@link #iconId}
	 * 
	 * @return	icon (drawable) resource id.
	 */
	public int getIcon () {
		// Return the assigned toast icon
		return iconId;
	}
	
	/**
	 * Setter - {@link #gravity}
	 * 
	 * @param gravity	{@link android.view.Gravity Gravity} value.
	 * @return	Generic THIS (self) - Reference to the current {@link me.SyncWise.Android.AppToast AppToast} object.
	 */
	public AppToast setGravity ( final int gravity ) {
		// Determine the provided gravity
		switch ( gravity ) {
		case Gravity.TOP:
		case Gravity.BOTTOM:
		case Gravity.RIGHT:
		case Gravity.LEFT:
		case Gravity.START:
		case Gravity.END:
		case Gravity.CENTER:
		case Gravity.CENTER_HORIZONTAL:
		case Gravity.CENTER_VERTICAL:
			// Set the new gravity
			this.gravity = gravity;
			break;
		default:
			// Invalid gravity, do nothing
			break;
		} // End switch
		// Allow for cascaded methods calls
		return this;
	}
	
	/**
	 * Getter - {@link #gravity}
	 * 
	 * @return	{@link android.view.Gravity Gravity} value.
	 */
	public int getGravity () {
		// Return the gravity
		return gravity;
	}
	
	/**
	 * Setter - {@link #xOffset}
	 * 
	 * @param xOffset	Horizontal offset.
	 * @return	Generic THIS (self) - Reference to the current {@link me.SyncWise.Android.AppToast AppToast} object.
	 */
	public AppToast setXOffset ( final int xOffset ) {
		// Set the new X offset
		this.xOffset = xOffset;
		// Allow for cascaded methods calls
		return this;
	}
	
	/**
	 * Getter - {@link #xOffset}
	 *
	 * @return	Horizontal offset.
	 */
	public int getXOffset () {
		// Return the X offset
		return xOffset;
	}
	
	/**
	 * Setter - {@link #yOffset}
	 * 
	 * @param yOffset	Vertical offset.
	 * @return	Generic THIS (self) - Reference to the current {@link me.SyncWise.Android.AppToast AppToast} object.
	 */
	public AppToast setYOffset ( final int yOffset ) {
		// Set the new Y offset
		this.yOffset = yOffset;
		// Allow for cascaded methods calls
		return this;
	}
	
	/**
	 * Getter - {@link #yOffset}
	 * 
	 * @return	Vertical offset.
	 */
	public int getYOffset () {
		// Return the Y offset
		return yOffset;
	}
	
	/**
	 * Setter - {@link #duration}
	 * 
	 * @param duration	Toast duration.
	 * @return	Generic THIS (self) - Reference to the current {@link me.SyncWise.Android.AppToast AppToast} object.
	 * @see android.widget.Toast#LENGTH_LONG
	 * @see android.widget.Toast#LENGTH_SHORT
	 */
	public AppToast setDuration ( final int duration ) {
		// Determine the provided duration
		switch ( duration ) {
		case Toast.LENGTH_LONG:
		case Toast.LENGTH_SHORT:
			// Set the new duration
			this.duration = duration;
			break;
		default:
			// Invalid duration, do nothing
			break;
		} // End switch
		// Allow for cascaded methods calls
		return this;
	}
	
	/**
	 * Getter - {@link #duration}
	 * 
	 * @return	Toast duration.
	 */
	public int getDuration () {
		// Return the duration
		return duration;
	}
	
	/**
	 * Displays the toast with the specified attributes :<br>
	 * <ul>
	 * <li>{@link #toastView}.</li>
	 * <li>{@link #gravity}.</li>
	 * <li>{@link #xOffset}.</li>
	 * <li>{@link #yOffset}.</li>
	 * <li>{@link #duration}.</li>
	 * </ul>
	 */
	public void show () {
		try {
			// Declare and initialize a toast object
			Toast toast = new Toast ( context.getApplicationContext () );
			// Set the toast's gravity
			toast.setGravity ( gravity , xOffset , yOffset );
			// Set the toast's duration
			toast.setDuration ( duration );
			// Set the toast's view
			toast.setView ( toastView );
			// Display the toast
			toast.show ();
		} catch ( Exception exception ) {
			// An error occurred, do nothing
		} // End of try-catch block
	}
	
}