/**
 * Copyright 2015 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.ShareOfShelf;

import java.text.DecimalFormat;
import java.util.List;

import me.SyncWise.Android.R;
import me.SyncWise.Android.Widgets.TinyNumberPicker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

/**
 * A concrete BaseAdapter that is backed by an array of {@link me.SyncWise.Android.Modules.ShareOfShelf.Tracker Tracker} objects.
 * 
 * @author Elias
 *
 */
public class SOSBrandDetailsAdapter extends ArrayAdapter < SOSTracker  > {
	
	/**
	 * {@link android.view.LayoutInflater} used to instantiate a layout XML file into its corresponding View objects.
	 */
	private final LayoutInflater layoutInflater;
	
	/**
	 * XML Layout (layout) resource id.
	 */
	private final int layout;
	private boolean enable;
 
	
	/**
	 * Nested class implementation of the view holder pattern used to keep references to children views in order to avoid unnecessary calls to {@link android.view.View#findViewById(int) findViewById(id)} on each row.
	 * 
	 * @author Elias
	 *
	 */
	public static class ViewHolder {
		public SOSTracker tracker;
		public TextView item;
		public TextView brand;
		
		public TinyNumberPicker pickerShareOfShelf;
		 
	}
	
	/**
	 * A {@link java.text.DecimalFormat DecimalFormat} object used to format and display two digits after the decimal point.<br>
	 * It is mainly used to separate thousands with commas and display a fix rounding value.
	 */
	private final DecimalFormat decimalFormat;
	
	/**
	 * Constructor.
	 * 
	 * @param context	The application context.
	 * @param layout	XML Layout (layout) resource id.
	 * @param trackers	List of {@link me.SyncWise.Android.Modules.ShareOfShelf.Tracker Tracker} objects.
	 * @param isEnabled	Boolean used to indicate if the items are enabled.
	 * @param isNoteOnly	Boolean used to indicate if the notes are enabled only (no quantities).
	 */
	public SOSBrandDetailsAdapter ( Context context , int layout , List < SOSTracker > trackers ,Boolean enable  ) {
		// Superclass method call
		super ( context , layout , trackers );
		this.enable=enable;
		// Initialize the attributes
		layoutInflater = LayoutInflater.from ( context );
		this.layout = layout;
		decimalFormat = new DecimalFormat ( "#,##0.00" );
 
	}
	
	/**
	 * Getter - {@link #decimalFormat}
	 * 
	 * @return	A {@link java.text.DecimalFormat DecimalFormat} object used to format and display two digits after the decimal point.
	 */
	public DecimalFormat getDecimalFormat () {
		return decimalFormat;
	}
	
	public static void refreshOrderView ( final Context context , final View view  ) {
		// Retrieve the stored view holder
//		ViewHolder viewHolder = (ViewHolder) view.getTag ();
//		// Calculate the percentage
//		double percentage = viewHolder.tracker.getCategory () == 0 || viewHolder.tracker.getShareOfShelf () == 0 ? 0 : viewHolder.tracker.getShareOfShelf () / viewHolder.tracker.getCategory () * 100.0;
//		// Display the percentage
//		viewHolder.percentage.setText ( decimalFormat.format ( percentage ) + " %" );
	}
	
	/*
	 * Get a View that displays the data at the specified position in the data set.
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@SuppressLint("ClickableViewAccessibility") 
	@Override
	public View getView ( final int position , View convertView , ViewGroup parent ) {
		// Declare the row view holder
		ViewHolder viewHolder;
		// Check if an inflated view is provided
		if ( convertView == null ) {
			// A new view must be inflated
			convertView = layoutInflater.inflate ( layout , null );
			// Declare and initialize a view holder
			viewHolder = new ViewHolder ();
			// Retrieve a reference to the item label
			viewHolder.item = (TextView) convertView.findViewById ( R.id.label_item );
			viewHolder.brand = (TextView) convertView.findViewById ( R.id.label_brand );
		 	// Retrieve a reference to the share of shelf picker
			viewHolder.pickerShareOfShelf = (TinyNumberPicker) convertView.findViewById ( R.id.tiny_number_picker_share_of_shelf );
			
			// Enabled the edit texts accordingly
		 
		 
			// Store the view holder as tag
			convertView.setTag ( viewHolder );
		} // End if
		else
			// An inflated view is already provided, retrieve the stored view holder
			viewHolder = (ViewHolder) convertView.getTag ();

		// Set the current tracker
		viewHolder.tracker = getItem ( position );
		
		// Declare and initialize a tracker watcher
		SOSBrandWatcher trackerWatcher = null;
		// Display the brand name
		viewHolder.item.setText ( viewHolder.tracker.getBrandName ()  +"  ("+viewHolder.tracker.getHeadCompName()+")");
		viewHolder.brand.setText ( viewHolder.tracker.getBDAMeter ()+"");
		 viewHolder.pickerShareOfShelf.value=0.5;
		// Clear the previous text watcher
		clearTextWatcher ( viewHolder.pickerShareOfShelf.getEditText () );
		// Display the share of shelf in the picker
		viewHolder.pickerShareOfShelf.setNumber ( viewHolder.tracker.getCurrentShareOfShelf  () );
		// Assign a new text watcher
		trackerWatcher = new SOSBrandWatcher ( (SOSBrandDetailActivity) getContext () , convertView , getItem ( position ) , TrackerWatcher.Type.SHARE_OF_SHELF );
		viewHolder.pickerShareOfShelf.getEditText ().addTextChangedListener ( trackerWatcher );
		viewHolder.pickerShareOfShelf.getEditText ().setTag ( trackerWatcher );
		// Clear the edit text focus
		checkEditTextFocus ( viewHolder.pickerShareOfShelf.getEditText () );
	 
		refreshOrderView ( getContext () , convertView   );
	 
		//viewHolder.bdaMeter.setText ( decimalFormat.format ( viewHolder.tracker.getBDAMeter () ) );
 
		// Return the view
		return convertView;
	}
	
	/*
	 * Returns true if the item at the specified position is not a separator. (A separator is a non-selectable, non-clickable item).
	 *
	 * @see android.widget.BaseAdapter#isEnabled(int)
	 */
	@Override
	public boolean isEnabled ( int position ) {
		return false;
	}
	
	/**
	 * Clears the associated text watcher as tag from the provided edit text (if any).
	 * 
	 * @param editText	Edit text object to clear the text watcher from.
	 */
	private void clearTextWatcher ( final EditText editText ) {
		try {
			// Retrieve the edit text's tag
			Object tag = editText.getTag ();
			// Check if the tag is a text watcher
			if ( tag instanceof TextWatcher )
				// Remove the text watcher
				editText.removeTextChangedListener ( (TextWatcher) tag );
		} catch ( Exception exception ) {
			// Do nothing
		} // End of try-catch block
	}
	
	/**
	 * Checks the provided edit text focus and disables it as needed.
	 * 
	 * @param editText	Edit text object to clear focus if needed.
	 */
	private void checkEditTextFocus ( final EditText editText ) {
		// Check if the edit text has focus
		if ( editText.hasFocus () )
			// Clear focus by setting focus to another view
			( (Activity) getContext () ).findViewById ( R.id.view_clear_focus ).requestFocus ();
	}

}