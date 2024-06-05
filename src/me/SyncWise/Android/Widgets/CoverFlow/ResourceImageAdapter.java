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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.SyncWise.Android.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

/**
 * This class is an adapter that provides images from a fixed set of resource
 * ids. Bitmaps and ImageViews are kept as weak references so that they can be
 * cleared by garbage collection when not needed.
 * 
 */
public class ResourceImageAdapter extends AbstractCoverFlowImageAdapter {

    /** The Constant TAG. */
    private static final String TAG = ResourceImageAdapter.class.getSimpleName();

    /** The Constant DEFAULT_LIST_SIZE. */
    private static final int DEFAULT_LIST_SIZE = 20;

    /** The Constant IMAGE_RESOURCE_IDS. */
    private static final List<Integer> IMAGE_RESOURCE_IDS = new ArrayList<Integer>(DEFAULT_LIST_SIZE);

    /** The Constant DEFAULT_RESOURCE_LIST. */
    private static final int[] DEFAULT_RESOURCE_LIST = { R.drawable.boxes , R.drawable.boxes_1 , R.drawable.boxes_2 };

    /** The bitmap map. */
    private final Map<Integer, WeakReference<Bitmap>> bitmapMap = new HashMap<Integer, WeakReference<Bitmap>>();

	/**
	 * Weak reference holding the default bitmap.
	 */
	private WeakReference < Bitmap > defaultBitmap;
	
    /**
     * Constant integer holding the drawable resource ID for the default image.<br>
     * This drawable is used in the case where the paht is not found / reachable.
     */
    private static final int DEFAULT_IMAGE_ID = R.drawable.no_available_image;
    
    private final Context context;

    /**
     * Creates the adapter with default set of resource images.
     * 
     * @param context
     *            context
     */
    public ResourceImageAdapter(final Context context) {
        super();
        this.context = context;
        setResources(DEFAULT_RESOURCE_LIST);
    }

    /**
     * Replaces resources with those specified.
     * 
     * @param resourceIds
     *            array of ids of resources.
     */
    public final synchronized void setResources(final int[] resourceIds) {
        IMAGE_RESOURCE_IDS.clear();
        for (final int resourceId : resourceIds) {
            IMAGE_RESOURCE_IDS.add(resourceId);
        }
        notifyDataSetChanged();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public synchronized int getCount() {
        return IMAGE_RESOURCE_IDS.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.polidea.coverflow.AbstractCoverFlowImageAdapter#createBitmap(int)
     */
    @Override
    protected Bitmap createBitmap(final int position) {
        Log.v(TAG, "creating item " + position);
        final Bitmap bitmap = ((BitmapDrawable) context.getResources().getDrawable(IMAGE_RESOURCE_IDS.get(position)))
                .getBitmap();
        bitmapMap.put(position, new WeakReference<Bitmap>(bitmap));
        return bitmap;
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
			if ( bitmap == null )
				// Display debug output if required
				Log.d ( TAG , "getDefaultBitmap : Empty default bitmap reference." );
			else {
				// Display debug output
				Log.d ( TAG , "getDefaultBitmap : Reusing default bitmap reference." );
				// Return the default bitmap
				return bitmap;
			} // End else
		} // End if
		// Display debug output
		Log.d ( TAG , "getDefaultBitmap : Invalid default bitmap reference." );
		// Declare and initialize the default bitmap
        final Bitmap defaultBitmap = ( (BitmapDrawable) context.getResources ().getDrawable ( DEFAULT_IMAGE_ID ) ).getBitmap ();
        // Save a weak reference to the default bitmap
		this.defaultBitmap = new WeakReference < Bitmap > ( defaultBitmap );
		// Display debug output
		Log.d ( TAG , "getDefaultBitmap : Default bitmap created and weak reference stored." );
		// Return the default bitmap
		return defaultBitmap;
	}

}