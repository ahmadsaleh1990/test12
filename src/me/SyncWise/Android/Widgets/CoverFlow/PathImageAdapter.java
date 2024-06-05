/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Widgets.CoverFlow;

import java.io.File;
import java.io.FileInputStream;
import java.lang.ref.WeakReference;
import java.util.List;

import me.SyncWise.Android.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.util.SparseArray;

/**
 * This class is an adapter that provides base, abstract class for images adapter.<br>
 * The images are loaded based on a specified path on the device storage.
 * 
 * @author Elias
 *
 */
public class PathImageAdapter extends AbstractCoverFlowImageAdapter {

	/**
	 * Flag used to indicate if the debug output is to be displayed or not.
	 */
    private static boolean showDebugOutput = true;
	
    /**
     * Tag used to identify the source of a log message.
     */
    public static final String TAG = "PATH_IMAGE_ADAPTER";
    
    /**
     * The application context.
     */
    private final Context context;
    
    /**
     * Constant integer holding the drawable resource ID for the default image.<br>
     * This drawable is used in the case where the paht is not found / reachable.
     */
    private static final int DEFAULT_IMAGE_ID = R.drawable.no_available_image;
    
	/**
	 * Sparse array hosting the image paths mapped to their appropriate position in the cover flow view.
	 */
	private final SparseArray < String > pathMap = new SparseArray < String > ();
	
	/**
	 * Weak reference holding the default bitmap.
	 */
	private WeakReference < Bitmap > defaultBitmap;
	
	/**
	 * Constructor.
	 * 
	 * @param	The application context.
	 */
	public PathImageAdapter ( final Context context ) {
		// Superclass method call
		super ();
		// Initialize the attributes
		this.context = context;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param	The application context.
	 * @param paths	Array of strings hosting the image paths.
	 */
	public PathImageAdapter ( final Context context , final String paths [] ) {
		// Overloaded method call
		this ( context );
		// Add all the provided paths
		addAll ( paths );
	}
	
	/**
	 * Constructor.
	 * 
	 * @param	The application context.
	 * @param paths	List of strings hosting the image paths.
	 */
	public PathImageAdapter ( final Context context , final List < String > paths ) {
		// Overloaded method call
		this ( context );
		// Add all the provided paths
		addAll ( paths );
	}

	/**
	 * Adds the specified image path.
	 * 
	 * @param path	String hosting an image path.
	 */
	public void add ( final String path ) {
		// Add the path to the map
		
	}
	
	/**
	 * Adds the specified image paths.
	 * 
	 * @param paths	Array of strings hosting the image paths.
	 */
	public void addAll ( final String paths [] ) {
		// Check if the array is valid
		if ( paths == null )
			// Invalid array
			return;
		// Otherwise, populate the path map
		for ( int i = 0 ; i < paths.length ; i ++ )
			// Add the path to the map
			pathMap.put ( i , paths [ i ] );
	}
	
	/**
	 * Adds the specified image paths.
	 * 
	 * @param paths	List of strings hosting the image paths.
	 */
	public void addAll ( final List < String > paths ) {
		// Check if the list is valid
		if ( paths == null )
			// Invalid list
			return;
		// Otherwise, populate the path map
		for ( int i = 0 ; i < paths.size () ; i ++ )
			// Add the path to the map
			pathMap.put ( i , paths.get ( i ) );
	}
	
	/*
	 * How many items are in the data set represented by this Adapter.
	 *
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount () {
		// Return the number of paths
		return pathMap.size ();
	}
	
	/**
	 * Clears the path image adapter of all images.
	 */
	public void clear () {
		// Clear the path map
		pathMap.clear ();
	}
	
	/*
	 * Creates new bitmap as default image.
	 *
	 * @see me.SyncWise.Android.Widgets.CoverFlow.AbstractCoverFlowImageAdapter#getDefaultBitmap()
	 */
	@Override
	protected Bitmap getDefaultBitmap () {
		// Check if the weak reference to the default bitmap is valid
		if ( defaultBitmap != null ) {
			// Retrieve a reference to the default bitmap
			final Bitmap bitmap  = defaultBitmap.get ();
			// Check if the default bitmap is valid
			if ( bitmap == null && showDebugOutput )
				// Display debug output if required
				Log.d ( TAG , "getDefaultBitmap : Empty default bitmap reference." );
			else {
				// Display debug output if required
				if ( showDebugOutput )
					Log.d ( TAG , "getDefaultBitmap : Reusing default bitmap reference." );
				// Return the default bitmap
				return bitmap;
			} // End else
		} // End if
		// Display debug output if required
		else if ( defaultBitmap == null && showDebugOutput )
			Log.d ( TAG , "getDefaultBitmap : Invalid default bitmap reference." );
		// Declare and initialize the default bitmap
        final Bitmap defaultBitmap = ( (BitmapDrawable) context.getResources ().getDrawable ( DEFAULT_IMAGE_ID ) ).getBitmap ();
        // Save a weak reference to the default bitmap
		this.defaultBitmap = new WeakReference < Bitmap > ( defaultBitmap );
		// Display debug output if required
		if ( showDebugOutput )
			Log.d ( TAG , "getDefaultBitmap : Default bitmap created and weak reference stored." );
		// Return the default bitmap
		return defaultBitmap;
	}

	/*
	 * Creates new bitmap for the position specified.
	 *
	 * @see me.SyncWise.Android.Widgets.CoverFlow.AbstractCoverFlowImageAdapter#createBitmap(int)
	 */
	@Override
	protected Bitmap createBitmap ( int position ) {
		try {
			// Retrieve a file reference to the path
			File picture = new File ( pathMap.get ( position ) );
			// Decode the image size
            BitmapFactory.Options options = new BitmapFactory.Options ();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream ( new FileInputStream ( picture ) , null , options );
            // Compute the image sample size (scale)
            int width = options.outWidth;
            int height = options.outHeight;
            int scale = 1;
            // Keep iterating until the size is met
            while ( true ) {
            	// Check if the size is met
            	if ( width / 2 < this.width || height / 2 < this.height )
            		// Exit loop
            		break;
            	// Reduce width and height by half
                width /= 2;
                height /= 2;
                // Double scale
                scale *= 2;
            } // End while loop
            // Decode and return the image using the sample size
            options = new BitmapFactory.Options ();
            options.inSampleSize = scale;
            return BitmapFactory.decodeStream ( new FileInputStream ( picture ) , null , options );
		} catch ( Exception exception ) {
			// An error occurred
			return null;
		} // End of try-catch block
	}
    
}