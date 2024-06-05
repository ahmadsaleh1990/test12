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

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * This class is an adapter that provides base, abstract class for images
 * adapter.
 * 
 */
public abstract class AbstractCoverFlowImageAdapter extends BaseAdapter {

	/**
	 * String used to host the current adapter code.<br>
	 * It is used in conjunction with the bit map cache system.
	 */
	private String adapterCode;
	
    /** The Constant TAG. */
    private static final String TAG = AbstractCoverFlowImageAdapter.class.getSimpleName();

    /** The width. */
    protected float width = 0;

    /** The height. */
    protected float height = 0;

    /** The bitmap map. */
    private final Map<String, WeakReference<Bitmap>> bitmapMap = new HashMap<String, WeakReference<Bitmap>>();

    public AbstractCoverFlowImageAdapter() {
        super();
    }
    
    /**
     * Setter - {@link #adapterCode}
     * 
     * @param code	String hosting the new adapter code.
     */
    public void setAdapterCode ( final String adapterCode ) {
    	this.adapterCode = adapterCode;
    }

    /**
     * Set width for all pictures.
     * 
     * @param width
     *            picture height
     */
    public synchronized void setWidth(final float width) {
        this.width = width;
    }

    /**
     * Set height for all pictures.
     * 
     * @param height
     *            picture height
     */
    public synchronized void setHeight(final float height) {
        this.height = height;
    }
    
    /**
     * Adds a bitmap reference linked to the indicated position.
     * 
     * @author Elias
     * 
     * @param position	Integer holding the bitmap position in the cover flow view.
     * @param bitmap	Bitmap reference to add at the indicated position
     */
    public void addBitmap ( final int position , final Bitmap bitmap ) {
    	bitmapMap.put(adapterCode + position, new WeakReference<Bitmap>(bitmap));
    }

    /**
     * Clears the bit map cache used to reuse created bit maps.<br>
     * This method should be used before the refreshing the adapter in case the images are renewed so that the reused images do not overwrite.
     */
    public void clearCache () {
    	for ( String key : bitmapMap.keySet () )
    		bitmapMap.get ( key ).clear ();
    	bitmapMap.clear ();
    }
    
    /**
     * Get the data item associated with the specified position in the data set
     * 
     * @author Elias
     * 
     * @param	Integer holding the bitmap position in the cover flow view.
     */
    @Override
    public final Bitmap getItem(final int position) {
        final WeakReference<Bitmap> weakBitmapReference = bitmapMap.get(position);
        if (weakBitmapReference != null) {
            final Bitmap bitmap = weakBitmapReference.get();
            if (bitmap == null) {
                Log.v(TAG, "Empty bitmap reference at position: " + position + ":" + this);
            } else {
                Log.v(TAG, "Reusing bitmap item at position: " + position + ":" + this);
                return bitmap;
            }
        }
        Log.v(TAG, "Creating item at position: " + position + ":" + this);
        final Bitmap bitmap = createBitmap(position);
        // Check if the created bitmap is valid
        if ( bitmap != null ) {
        	// Store a weak reference of the created bitmap mapped to the current position
        	bitmapMap.put ( adapterCode + position , new WeakReference < Bitmap > ( bitmap ) );
        	// Display debug output
            Log.v ( TAG , "Created item at position: " + position + ":" + this );
            // Return the created bitmap
            return bitmap;
        } // End if
        // Otherwise store the default bitmap at the current position
        else {
        	// Retrieve a reference to the default bitmap
        	final Bitmap defaultBitmap = getDefaultBitmap ();
        	// Store a weak reference of the default bitmap mapped to the current position
        	bitmapMap.put ( adapterCode + position , new WeakReference < Bitmap > ( defaultBitmap ) );
        	// Display debug output
            Log.v ( TAG , "Default bitmap used at position: " + position + ":" + this );
            // Return the default bitmap
            return defaultBitmap;
        } // End else
    }

    /**
     * Creates new bitmap for the position specified.
     * 
     * @param position
     *            position
     * @return Bitmap created
     */
    protected abstract Bitmap createBitmap(int position);
    
    /**
     * Creates new bitmap as default image.
     * 
     * @author Elias
     * 
     * @return	Bitmap created.
     */
    protected abstract Bitmap getDefaultBitmap ();

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public final synchronized long getItemId(final int position) {
        return position;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getView(int, android.view.View,
     * android.view.ViewGroup)
     */
    @SuppressWarnings("deprecation")
	@Override
    public final synchronized ImageView getView(final int position, final View convertView, final ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            final Context context = parent.getContext();
            Log.v(TAG, "Creating Image view at position: " + position + ":" + this);
            imageView = new ImageView(context);
            imageView.setLayoutParams(new CoverFlow.LayoutParams((int) width, (int) height));
        } else {
            Log.v(TAG, "Reusing view at position: " + position + ":" + this);
            imageView = (ImageView) convertView;
        }
        imageView.setImageBitmap(getItem(position));
        return imageView;
    }

}
