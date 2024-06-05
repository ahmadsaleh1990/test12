/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Widgets.QuickAction;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;

import android.widget.PopupWindow;
import android.content.Context;
import android.content.res.Resources;

/**
 * Custom popup window.
 * 
 * @author Lorensius W. L. T <lorenz@londatiga.net>
 *
 */
public class PopupWindows {
	
	protected Context context;
	protected PopupWindow window;
	protected View rootView;
	protected Drawable background = null;
	protected WindowManager windowManager;
	
	/**
	 * Constructor.
	 * 
	 * @param context Context
	 */
	public PopupWindows ( Context context ) {
		this.context	= context;
		window 	= new PopupWindow ( context );

		window.setTouchInterceptor ( new OnTouchListener () {
			public boolean onTouch ( View view , MotionEvent event ) {
				if ( event.getAction () == MotionEvent.ACTION_OUTSIDE ) {
					window.dismiss ();
					
					return true;
				}
				
				return false;
			}
		});

		windowManager = (WindowManager) context.getSystemService ( Context.WINDOW_SERVICE );
	}
	
	/**
	 * On dismiss
	 */
	protected void onDismiss () {		
	}
	
	/**
	 * On show
	 */
	protected void onShow () {		
	}

	/**
	 * On pre show
	 */
	protected void preShow ( Resources resources ) {
		if ( rootView == null ) 
			throw new IllegalStateException ( "setContentView was not called with a view to display." );
	
		onShow ();

		if ( background == null ) 
			window.setBackgroundDrawable ( new BitmapDrawable ( resources ) );
		else 
			window.setBackgroundDrawable ( background );

		window.setWidth ( WindowManager.LayoutParams.WRAP_CONTENT );
		window.setHeight  (WindowManager.LayoutParams.WRAP_CONTENT );
		window.setTouchable ( true );
		window.setFocusable ( true );
		window.setOutsideTouchable ( true );

		window.setContentView ( rootView );
	}

	/**
	 * Set background drawable.
	 * 
	 * @param background Background drawable
	 */
	public void setBackgroundDrawable ( Drawable background ) {
		this.background = background;
	}

	/**
	 * Set content view.
	 * 
	 * @param root Root view
	 */
	public void setContentView ( View root ) {
		rootView = root;
		
		window.setContentView ( root );
	}

	/**
	 * Set content view.
	 * 
	 * @param layoutResID Resource id
	 */
	public void setContentView ( int layoutResID ) {
		LayoutInflater inflator = (LayoutInflater) context.getSystemService ( Context.LAYOUT_INFLATER_SERVICE );
		
		setContentView ( inflator.inflate ( layoutResID , null ) );
	}

	/**
	 * Set listener on window dismissed.
	 * 
	 * @param listener
	 */
	public void setOnDismissListener ( PopupWindow.OnDismissListener listener ) {
		window.setOnDismissListener ( listener );  
	}

	/**
	 * Dismiss the popup window.
	 */
	public void dismiss () {
		window.dismiss ();
	}
}