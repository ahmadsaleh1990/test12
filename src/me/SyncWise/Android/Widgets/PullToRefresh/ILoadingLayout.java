/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Widgets.PullToRefresh;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

public interface ILoadingLayout {

	/**
	 * Set the Last Updated Text. This displayed under the main label when Pulling
	 * 
	 * @param label	Label to set
	 */
	public void setLastUpdatedLabel ( CharSequence label );

	/**
	 * Set the drawable used in the loading layout. This is the same as calling <code>setLoadingDrawable(drawable, Mode.BOTH)</code>
	 * 
	 * @param drawable	Drawable to display
	 */
	public void setLoadingDrawable ( Drawable drawable );

	/**
	 * Set Text to show when the Widget is being Pulled <code>setPullLabel(releaseLabel, Mode.BOTH)</code>
	 * 
	 * @param pullLabel	CharSequence to display
	 */
	public void setPullLabel ( CharSequence pullLabel );

	/**
	 * Set Text to show when the Widget is refreshing <code>setRefreshingLabel(releaseLabel, Mode.BOTH)</code>
	 * 
	 * @param refreshingLabel	CharSequence to display
	 */
	public void setRefreshingLabel ( CharSequence refreshingLabel );

	/**
	 * Set Text to show when the Widget is being pulled, and will refresh when released. This is the same as calling <code>setReleaseLabel(releaseLabel, Mode.BOTH)</code>
	 * 
	 * @param releaseLabel	CharSequence to display
	 */
	public void setReleaseLabel ( CharSequence releaseLabel );

	/**
	 * Set's the Sets the typeface and style in which the text should be displayed.
	 * Please see {@link android.widget.TextView#setTypeface(Typeface)
	 * 
	 * @param typeface	Type face object used to specify the type face and intrinsic style of a point. 
	 * @see	TextView#setTypeface(Typeface).
	 */
	public void setTextTypeface ( Typeface typeface );

}
