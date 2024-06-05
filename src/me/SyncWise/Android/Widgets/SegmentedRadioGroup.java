/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

/**
 * Copyright (C) 2011 Make Ramen, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.SyncWise.Android.Widgets;

import me.SyncWise.Android.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * <b>Segmented Buttons Group</b> implementation :<br>
 * Provides segmented control over a group of buttons, similarly to a radio group.
 * 
 * <br><br><h1>XML Attributes:</h1><br>
 * <ul>
 * <li><b>has_padding_between_children</b> : <em>Related Method</em> {@link #enablePaddingBetweenChildren(boolean)}</li>
 * <li><b>padding_between_children</b> : <em>Related Method</em> {@link #setPaddingBetweenChildren(int)}</li>
 * <li><b>padding_color</b> : <em>Related Method</em> {@link #setPaddingColor(Integer)}</li>
 * <li><b>equal_width</b> : <em>Related Method</em> {@link #enableEqualWidth(boolean)}</li>
 * <li><b>equal_height</b> : <em>Related Method</em> {@link #enableEqualHeight(boolean)}</li>
 * </ul>
 * 
 * @author Elias
 *
 */
public class SegmentedRadioGroup extends RadioGroup {

	/**
	 * Boolean indicating whether there is a padding between the children in the group.
	 */
	private boolean hasPaddingBetweenChildren;
	
	/**
	 * The size of the width between the children in this group, in pixels.
	 */
	private int paddingBetweenChildren;
	
	/**
	 * Integer hosting the padding color between children.
	 */
	private Integer paddingColor;
	
	/**
	 * Boolean indicating if children should have equal width
	 */
	private boolean equalWidth;
	
	/**
	 * Boolean indicating if children should have equal height
	 */
	private boolean equalHeight;
	
	
	/**
	 * Enables padding between the children in this group.
	 * 
	 * @param hasPadding	Boolean indicating if padding is enabled between children.
	 * @see #hasPaddingBetweenChildren()
	 * @see #setPaddingBetweenChildren(int)
	 */
	public void enablePaddingBetweenChildren ( final boolean hasPadding ) {
		// Set flag
		hasPaddingBetweenChildren = hasPadding;
	}
	
	/**
	 * Indicates if the padding feature between children is enabled.
	 * 
	 * @return	Boolean indicating if children are separated by padding.
	 * @see	#enablePaddingBetweenChildren(boolean)
	 * @see #setPaddingBetweenChildren(int)
	 */
	public boolean hasPaddingBetweenChildren () {
		// Return flag
		return hasPaddingBetweenChildren;
	}
	
	/**
	 * Sets the padding distance in pixels between children (regardless of the orientation).<br>
	 * If not set, the default padding distance is used.
	 * 
	 * @param padding	Integer holding the padding distance in pixels.
	 * @see #enablePaddingBetweenChildren(boolean)
	 * @see #getPaddingBetweenChildren()
	 */
	public void setPaddingBetweenChildren ( final int padding ) {
		// Check if the provided integer is valid
		if ( padding >= 0 )
			// Set the padding distance
			this.paddingBetweenChildren = padding;
	}
	
	/**
	 * Returns the padding distance in pixels between children (regardless of the orientation).
	 * 
	 * @return	Integer holding the padding distance in pixels.
	 * @see #enablePaddingBetweenChildren(boolean)
	 * @see #setPaddingBetweenChildren(int)
	 */
	public int getPaddingBetweenChildren () {
		// Return the padding value
		return paddingBetweenChildren;
	}
	
	/**
	 * Sets the padding color between children.<br>
	 * To remove the padding color, use {@code NULL}.
	 * 
	 * @param paddingColor	Integer hosting the padding color between children.
	 */
	public void setPaddingColor ( final Integer paddingColor ) {
		// Determine if the previous padding color is similar
		if ( ! this.paddingColor.equals ( paddingColor ) ) {
			// Set the padding color
			this.paddingColor = paddingColor;
			// Apply the new padding color
			setPaddingColor ();
		} // End if
	}
	
	/**
	 * Returns the padding color between children.<br>
	 * If none is set, {@code NULL} is returned.
	 * 
	 * @return	Integer hosting the padding color between children.
	 */
	public int getPaddingColor () {
		// Return the padding color
		return paddingColor;
	}
	
	/**
	 * Enables equal width among children in this group.<br>
	 * This feature is always enabled (cannot be disabled) if the orientation is set to vertical.
	 * 
	 * @param equalWidth	Boolean indicating whether to enable the equal width feature.
	 */
	public void enableEqualWidth ( final boolean equalWidth ) {
		// Set the flag
		this.equalWidth = equalWidth;
	}
	
	/**
	 * Indicates if the equal width feature among children is enabled.<br>
	 * The feature is always enabled (cannot be disabled) if the orientation is set to vertical.
	 * 
	 * @return	Boolean indicating if the equal width feature is enabled.
	 */
	public boolean hasEqualWidth () {
		// Return the flag content ORed with the vertical orientation status
		return equalWidth || ( getOrientation () == LinearLayout.VERTICAL );
	}
	
	/**
	 * Enables equal height among children in this group.
	 * 
	 * @param equalHeight	Boolean indicating whether to enable the equal height feature.
	 */
	public void enableEqualHeight ( final boolean equalHeight ) {
		// Set the flag
		this.equalHeight = equalHeight;
	}
	
	/**
	 * Indicates if the equal height feature among children is enabled.
	 * 
	 * @return	Boolean indicating if the equal height feature is enabled.
	 */
	public boolean hasEqualHeight () {
		// Return the flag
		return equalHeight;
	}
	
	/**
	 * Builds a radio button in order to add it to a segmented radio group.
	 * 
	 * @param context	The application context.
	 * @return	A radio button made ready to be added to a segmented radio group.
	 */
	public static RadioButton buildChild ( final Context context ) {
		// Declare and initialize a new radio button
		RadioButton radioButton = new RadioButton ( context );
		// Remove the button drawable
		radioButton.setButtonDrawable ( new StateListDrawable () );
		// Set the text appearance
		radioButton.setTextAppearance ( context , R.style.TextAppearance_Medium );
		// Make the button at most 1 line tall
		radioButton.setMaxLines ( 1 );
		// Center the text horizontally and vertically
		radioButton.setGravity ( Gravity.CENTER );
		// Set the text colors state list
		radioButton.setTextColor ( context.getResources ().getColorStateList ( R.color.radio_colors ) );
		// Return the radio button
		return radioButton;
	}
	
	/**
	 * Gets the child position inside the group based on its id.<br>
	 * If the provided id do not match any child id in the group, -1 is returned.
	 * 
	 * @param id	Long holding the child id.
	 * @return	The child position whose id matches, or -1 if none is found.
	 */
	public int getChildIndex ( final long id ) {
		// Declare and initialize an integer used to hold the child position
		int position = -1;
		// Iterate over all children in the group
		for ( int i = 0 ; i < super.getChildCount () ; i ++ )
			// Compare IDs
			if ( super.getChildAt ( i ).getId () == id ) {
				// Store the child's position
				position = i;
				break;
			} // End if
		// Return the computed position
		return position;
	}
	
	/**
	 * Sets text to the child inside the group at the specified position.<br>
	 * The child's width is adjusted if needed.
	 * 
	 * @param position	Integer holding the child position inside the group.
	 * @param text	String holding the text to be set.
	 */
	public void setText ( final int position , final String text ) {
		// Determine if the provided text is valid
		if ( text == null )
			// Invalid text
			return;
		try {
			// Set the provided text to the child at the provided position
			( (RadioButton) super.getChildAt ( position ) ).setText ( text );
			// Determine if the buttons' size needs adjustment
			changeButtonsSize ();
		} catch ( Exception exception ) {
			// The position is invalid, do nothing
		} // End of try-catch block
	}
	
	/**
	 * Constructor.
	 * 
	 * @param context	The application context.
	 */
	public SegmentedRadioGroup ( Context context ) {
		// Superclass method call
		super ( context );
	}

	/**
	 * Constructor (overloaded).
	 * 
	 * @param context	The application context.
	 * @param attrs	{@link android.util.AttributeSet AttributeSet} object containing information retrieved from the compiled XML.
	 */
	public SegmentedRadioGroup ( Context context , AttributeSet attrs) {
		// Superclass method call
		super ( context , attrs );
		// Check if the provided attribute set is valid
		if ( attrs != null ) {
			// Retrieve styled attribute information in this Context's theme
			TypedArray attributes = getContext ().obtainStyledAttributes ( attrs , R.styleable.SegmentedRadioGroup , 0 , 0 );
			// Retrieve the 'has_padding_between_children' style attribute
			hasPaddingBetweenChildren = attributes.getBoolean ( R.styleable.SegmentedRadioGroup_has_padding_between_children , false );
			// Retrieve the 'padding_between_children' style attribute
			paddingBetweenChildren = Math.max ( 1 , attributes.getDimensionPixelSize ( R.styleable.SegmentedRadioGroup_padding_between_children ,
					getContext ().getResources ().getDimensionPixelSize ( R.dimen.srg_padding_between_childreen ) ) );
			// Retrieve the 'equal_width' style attribute
			equalWidth = attributes.getBoolean ( R.styleable.SegmentedRadioGroup_equal_width , false );
			// Retrieve the 'equalHeight' style attribute
			equalHeight = attributes.getBoolean ( R.styleable.SegmentedRadioGroup_equal_height , false );
			// Retrieve the 'padding_color' style attribute
			paddingColor = attributes.getColor ( R.styleable.SegmentedRadioGroup_padding_color , Integer.MAX_VALUE );
			paddingColor = ( paddingColor == Integer.MAX_VALUE ? null : paddingColor );
			// Give back a previously retrieved array, for later re-use
			attributes.recycle ();
		} // End if
		// Assign the required padding color (if any)
		// If none is required, no need to remove the current background because none is set yet
		if ( paddingColor != null )
			setPaddingColor ();
	}

	/*
	 * Finalize inflating a view from XML.
	 * 
	 * @see android.widget.RadioGroup#onFinishInflate()
	 */
	@Override
	protected void onFinishInflate () {
		// Superclass method call
		super.onFinishInflate ();
		// Finalize inflating a segmented radio group by changing some of the buttons properties
		finalizeButtons ();
	}

	/*
	 * Called when the current Window of the activity gains or loses focus.
	 *
	 * @see android.view.View#onWindowFocusChanged(boolean)
	 */
	@Override
	public void onWindowFocusChanged ( boolean hasWindowFocus ) {
		// Super class method call
		super.onWindowFocusChanged ( hasWindowFocus );
		// Modify the buttons size (if needed)
		changeButtonsSize ();
	}
	
	/**
	 * Notifies that there are new/updated child views in the group, in order to properly redraw them after applying some properties modifications.<br>
	 * <em>Used in code if the buttons were added programmatically.</em>
	 */
	public void notifyChildViewsChanged () {
		// Finalize building a segmented radio group by changing some of the buttons properties
		finalizeButtons ();
	}
	
	/**
	 * Performs all necessary properties modifications for the buttons in the group.
	 */
	private void finalizeButtons () {
		// Change the buttons images
		changeButtonsImages ();
		// Change the buttons margins
		changeButtonsMargin ();
		// Change the buttons size
		changeButtonsSize ();
	}
	
	/**
	 * Sets the padding color between children.<br>
	 * The padding color consists of the background color of the view group.
	 */
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private void setPaddingColor () {
		// Declare a gradient drawable that will be used as the group background
		// A gradient drawable is used instead of a color drawable to be able to set a corner radius
		GradientDrawable background = null;
		// Determine if the padding color is valid
		if ( paddingColor != null ) {
			// Initialize the gradient drawable that will be used as the group background
			// A gradient drawable is used instead of a color drawable to be able to set a corner radius
			background = new GradientDrawable ();
			// Set the drawable color
			// The background color should only be visible between the buttons (padding between chidlren)
			background.setColor ( paddingColor );
			// Set the drawable cornors radius
			background.setCornerRadius ( 20 );
		} // End if
		// Determine the android version
		if ( android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN )
			// Set the drawable as the group's background (or remove the background)
			this.setBackground ( background );
		else
			// Set the drawable as the group's background (or remove the background)
			this.setBackgroundDrawable ( background );
	}
	
	
	/**
	 * Sets the children's background to appropriate (left, middle, right or whole) segmented buttons.
	 */
	private void changeButtonsImages () {
		// Compute the number of children in the group
		int count = super.getChildCount ();
		// Determine if there is at least two children in a horizontal group
		if ( count > 1 && getOrientation () == LinearLayout.HORIZONTAL ) {
			// Set the left segmented button background
			super.getChildAt ( 0 ).setBackgroundResource ( R.drawable.segment_radio_left );
			// Set the middle segmented buttons background
			for ( int i = 1 ; i < count - 1 ; i++ )
				super.getChildAt ( i ).setBackgroundResource ( R.drawable.segment_radio_middle );
			// Set the right segmented button background
			super.getChildAt ( count - 1 ).setBackgroundResource ( R.drawable.segment_radio_right );
		} // End if
		// Otherwise determine if there is at least two children in a vertical group
		else if ( count > 1 && getOrientation () == LinearLayout.VERTICAL ) {
			// Set the top segmented button background
			super.getChildAt ( 0 ).setBackgroundResource ( R.drawable.segment_radio_top );
			// Set the middle (vertical) segmented buttons background
			for ( int i = 1 ; i < count - 1 ; i++ )
				super.getChildAt ( i ).setBackgroundResource ( R.drawable.segment_radio_middle_vertical );
			// Set the bottom segmented button background
			super.getChildAt ( count - 1 ).setBackgroundResource ( R.drawable.segment_radio_bottom );
		} // End if
		// Otherwise determine if there is exactly one child
		else if ( count == 1 ) {
			// Set the segmented button background
			super.getChildAt ( 0 ).setBackgroundResource ( R.drawable.segment_button );
		} // End else if
	}
	
	/**
	 * Sets the children's margins (if specified) according to their positions (left, middle or right).
	 */
	private void changeButtonsMargin () {
		// Determine if padding between children is required
		if ( ! hasPaddingBetweenChildren )
			// Exit method
			return;
		// Compute the number of children in the group
		int count = super.getChildCount ();
		// Determine if there is at least one child
		if ( count >= 1 ) {
			// Iterate over all the children (except the last one)
			for ( int i = 0 ; i < count - 1 ; i++ ) {
				// Retrieve the margin layout parameters of the current child
				MarginLayoutParams margins = (MarginLayoutParams) super.getChildAt ( i ).getLayoutParams ();
				// Set the right margin for horizontal orientation and the bottom margin for vertical orientation (clear other margins)
				margins.setMargins ( 0 , 0 , ( getOrientation () == LinearLayout.HORIZONTAL ? paddingBetweenChildren : 0 ) , ( getOrientation () == LinearLayout.VERTICAL ? paddingBetweenChildren : 0 ) );
				// Apply the modifications done on the margin layout parameters
				super.getChildAt ( i ).setLayoutParams ( margins );
			} // End for loop
			// Retrieve the margin layout parameters of the last child
			MarginLayoutParams margins = (MarginLayoutParams) super.getChildAt ( count - 1 ).getLayoutParams ();
			// Clear all margins
			margins.setMargins ( 0 , 0 , 0 , 0 );
			// Apply the modifications done on the margin layout parameters
			super.getChildAt ( count - 1 ).setLayoutParams ( margins );
		} // End if
	}
	
	/**
	 * Sets the children's width/height if explicitly required via XML or the orientation is set to {@link android.widget.LinearLayout#VERTICAL LinearLayout#VERTICAL}.<br>
	 * This scenario is specially required in the vertical orientation due to the fact that the width of each child depends on its text content.<br>
	 * This method should be called after all XML and background inflation to be properly performed. 
	 */
	private void changeButtonsSize () {
		// Determine if the orientation is set to VERTICAL /or/ if the buttons should have equal width or height
		if ( getOrientation () == LinearLayout.VERTICAL || equalWidth || equalHeight ) {
			// Compute the number of children in the group
			int count = super.getChildCount ();
			// Determine if there is at least two children
			if ( count < 2 )
				// There is exactly one child or none at all
				return;
			// Declare and initialize 2 integers to hold the maximum width and height
			int maxWidth = 0;
			int maxHeight = 0;
			// Iterate over all the children
			for ( int i = 0 ; i < count ; i++ ) {
				// Check if the current child has a width greater than the maximum width
				if ( super.getChildAt ( i ).getWidth () > maxWidth )
					// Set the new maximum width
					maxWidth = super.getChildAt ( i ).getWidth ();
				// Check if the current child has a height greater than the maximum height
				if ( super.getChildAt ( i ).getHeight () > maxHeight )
					// Set the new maximum height
					maxHeight = super.getChildAt ( i ).getHeight ();
			} // End for loop
			// Iterate over all the children
			for ( int i = 0 ; i < count ; i++ ) {
				// Determine if the children's width should be equal /or/ if the orientation is set to vertical
				if ( getOrientation () == LinearLayout.VERTICAL || equalWidth )
					// Apply the maximum width on the current child
					super.getChildAt ( i ).setMinimumWidth ( maxWidth );
				// Determine if the children's height should be equal
				if ( equalHeight )
					// Apply the maximum height on the current child
					super.getChildAt ( i ).setMinimumHeight ( maxHeight );
			} // End for loop
		} // End if
	}
	
}