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

import me.SyncWise.Android.R;
import android.R.color;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

/**
 * This adapter provides reflected images from linked adapter.
 * 
 * @author potiuk
 * 
 */
public class ReflectingImageAdapter extends AbstractCoverFlowImageAdapter {

    /** The Constant TAG. */
    private static final String TAG = ReflectingImageAdapter.class.getSimpleName();
	
    /** The linked adapter. */
    private final AbstractCoverFlowImageAdapter linkedAdapter;
    /**
     * Gap between the image and its reflection.
     */
    private float reflectionGap;

	/** The image reflection ratio. */
    private float imageReflectionRatio;
    
    /**
     * Constant integer holding the drawable resource ID for the default image.<br>
     * This drawable is used in the case where the paht is not found / reachable.
     */
    private static final int DEFAULT_IMAGE_ID = R.drawable.no_available_image;
    
	/**
	 * Weak reference holding the default bitmap.
	 */
	private WeakReference < Bitmap > defaultBitmap;
	
    /**
     * The application context.
     */
    private final Context context;

    /**
     * Sets the width ratio.
     * 
     * @param imageReflectionRatio
     *            the new width ratio
     */
    public void setWidthRatio(final float imageReflectionRatio) {
        this.imageReflectionRatio = imageReflectionRatio;
    }
    
	/**
	 * Getter - {@link #linkedAdapter}
	 * 
	 * @return	The linked adapter.
	 */
	public AbstractCoverFlowImageAdapter getLinkedAdapter () {
		return linkedAdapter;
	}

    /**
     * Creates reflecting adapter.
     * 
     * @param	The application context.
     * @param linkedAdapter
     *            adapter that provides images to get reflections
     */
    public ReflectingImageAdapter( final Context context , final AbstractCoverFlowImageAdapter linkedAdapter) {
        super();
		this.context = context;
        this.linkedAdapter = linkedAdapter;
    }

    /**
     * Sets the reflection gap.
     * 
     * @param reflectionGap
     *            the new reflection gap
     */
    public void setReflectionGap(final float reflectionGap) {
        this.reflectionGap = reflectionGap;
    }

    /**
     * Gets the reflection gap.
     * 
     * @return the reflection gap
     */
    public float getReflectionGap() {
        return reflectionGap;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.polidea.coverflow.AbstractCoverFlowImageAdapter#createBitmap(int)
     */
    @Override
    protected Bitmap createBitmap(final int position) {
        return createReflectedImages(linkedAdapter.getItem(position));
    }

    /**
     * Creates the reflected images.
     * 
     * @param originalImage
     *            the original image
     * @return true, if successful
     */
    public Bitmap createReflectedImages(final Bitmap originalImage) {
        final int width = originalImage.getWidth();
        final int height = originalImage.getHeight();
        final Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        final Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, (int) (height * imageReflectionRatio),
                width, (int) (height - height * imageReflectionRatio), matrix, false);
        final Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (int) (height + height * imageReflectionRatio),
                Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmapWithReflection);
        canvas.drawBitmap(originalImage, 0, 0, null);
        final Paint deafaultPaint = new Paint();
        deafaultPaint.setColor(color.transparent);
        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
        final Paint paint = new Paint();
        final LinearGradient shader = new LinearGradient(0, originalImage.getHeight(), 0,
                bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);
        return bitmapWithReflection;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        return linkedAdapter.getCount();
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
