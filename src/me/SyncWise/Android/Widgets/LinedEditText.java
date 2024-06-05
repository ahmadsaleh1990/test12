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
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Custom implementation of {@link android.widget.EditText EditText} which draws lines between each line of text that is displayed.
 * 
 * @author Elias
 *
 */
public class LinedEditText extends EditText {

	/**
	 * Constant integer holding the default color of the lines drawn under the text.
	 */
	public static final int DEFAULT_LINE_COLOR = Color.parseColor ( "#800000FF" );
	
	/**
	 * {@link android.graphics.Rect Rect} object used to define the bounds of each line of text.<br>
	 * This object is used to draw a line under a line of text.
	 */
    private Rect rect;
    
    /**
     * {@link android.graphics.Paint Paint} object used to paint a line under each line of text.
     */
    private Paint paint;
	
    /**
     * Sets the color of the drawn lines.
     * 
     * @param color	Integer holding the color.
     */
    public void setLineColor ( int color ) {
    	// Set the color used to draw lines
    	paint.setColor ( color );
    }
    
    /**
     * Constructor (overloaded).
     * 
     * @param context	The application context.
     */
    public LinedEditText ( Context context ) {
    	// Superclass method call
    	super ( context );
    	// Perform initialization
    	initialize ();
    }
    
    /**
     * Constructor (overloaded).
     * 
     * @param context	The application context.
     * @param attrs	{@link android.util.AttributeSet AttributeSet} object containing information retrieved from the compiled XML.
     */
    public LinedEditText ( Context context , AttributeSet attrs ) {
    	// Superclass method call
    	super ( context , attrs );
    	// Perform initialization
    	initialize ();
    	// Check if the provided attribute set is valid
    	if ( attrs != null ) {
    		// Retrieve styled attribute information in this Context's theme
    		TypedArray attributes = getContext ().obtainStyledAttributes ( attrs , R.styleable.LinedEditText , 0 , 0 );
    		// Retrieve the 'line_color' style attribute
    		int color = attributes.getColor ( R.styleable.LinedEditText_line_color , DEFAULT_LINE_COLOR );
    		// Check if the retrieved color is different than the default value
    		if ( color != DEFAULT_LINE_COLOR )
    			// Set the new line color
    	        paint.setColor ( color );
			// Give back a previously retrieved array, for later re-use
			attributes.recycle ();
    	} // End if
    }
    
    /**
     * Performs the required initialization required to draw a line under each line of text.
     */
    private void initialize () {
    	// Initialize attributes
        rect = new Rect ();
        paint = new Paint ();
        // Define the paint style
        paint.setStyle ( Paint.Style.STROKE );
        // Define the paint color
        paint.setColor ( DEFAULT_LINE_COLOR );
    }
    
    /*
     * Implement this to do your drawing.
     *
     * @see android.widget.TextView#onDraw(android.graphics.Canvas)
     */
    @Override
    protected void onDraw ( Canvas canvas ) {
    	// Get the number of lines
        int count = getLineCount();
        // Iterate over all the lines
        for (int line = 0; line < count ; line ++) {
        	// Retrieve the Y-coordinate of the base line for the current line
        	int baseline = getLineBounds ( line , rect );
        	// Draw a line one dip unit below the baseline
        	canvas.drawLine ( rect.left , baseline + 1 , rect.right , baseline + 1 , paint );
        } // End for loop
    	// Superclass method call
        super.onDraw ( canvas );
    }
    
}