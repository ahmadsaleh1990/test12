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
public class ShareOfShelfTrackerDetailsAdapter extends ArrayAdapter < Tracker > {
	
	/**
	 * {@link android.view.LayoutInflater} used to instantiate a layout XML file into its corresponding View objects.
	 */
	private final LayoutInflater layoutInflater;
	
	/**
	 * XML Layout (layout) resource id.
	 */
	private final int layout;
	
	/**
	 * Flag used to indicate if the edit texts are enabled.
	 */
	private final boolean isEnabled;
	
	/**
	 * Flag used to indicate if the note is enabled only.
	 */
	private final boolean isNoteOnly;
	
	/**
	 * Nested class implementation of the view holder pattern used to keep references to children views in order to avoid unnecessary calls to {@link android.view.View#findViewById(int) findViewById(id)} on each row.
	 * 
	 * @author Elias
	 *
	 */
	public static class ViewHolder {
		public Tracker tracker;
		public TextView item;
		public EditText editTextNotes;
		public TinyNumberPicker pickerShareOfShelf;
		public TinyNumberPicker pickerCompetitorShareOfShelf;
		public TinyNumberPicker pickerCategory;
		public TextView percentage;
		public TextView bdaMeter;
		public TextView bdaPercentage;
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
	public ShareOfShelfTrackerDetailsAdapter ( Context context , int layout , List < Tracker > trackers , boolean isEnabled , boolean isNoteOnly ) {
		// Superclass method call
		super ( context , layout , trackers );
		// Initialize the attributes
		layoutInflater = LayoutInflater.from ( context );
		this.layout = layout;
		decimalFormat = new DecimalFormat ( "#,##0.00" );
		this.isEnabled = isEnabled;
		this.isNoteOnly = isNoteOnly;
	}
	
	/**
	 * Getter - {@link #decimalFormat}
	 * 
	 * @return	A {@link java.text.DecimalFormat DecimalFormat} object used to format and display two digits after the decimal point.
	 */
	public DecimalFormat getDecimalFormat () {
		return decimalFormat;
	}
	
	public static void refreshOrderView ( final Context context , final View view , final DecimalFormat decimalFormat ) {
		// Retrieve the stored view holder
		ViewHolder viewHolder = (ViewHolder) view.getTag ();
		// Calculate the percentage
		double percentage = viewHolder.tracker.getCategory () == 0 || viewHolder.tracker.getShareOfShelf () == 0 ? 0 : viewHolder.tracker.getShareOfShelf () / viewHolder.tracker.getCategory () * 100.0;
		// Display the percentage
		viewHolder.percentage.setText ( decimalFormat.format ( percentage ) + " %" );
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
			// Retrieve a reference to the notes edit text
			viewHolder.editTextNotes = (EditText) convertView.findViewById ( R.id.edit_text_notes );
			// Enable vertical scrolling by properly handling touch events between the edit text and the list view
			viewHolder.editTextNotes.setOnTouchListener ( new View.OnTouchListener () {
			    public boolean onTouch ( final View view , final MotionEvent motionEvent ) {
			    	view.getParent ().requestDisallowInterceptTouchEvent ( true );
		            switch ( motionEvent.getAction () & MotionEvent.ACTION_MASK ) {
		                case MotionEvent.ACTION_UP:
		                	view.getParent ().requestDisallowInterceptTouchEvent ( false );
		                break;
		            }
			        return false;
			    }
			} );
			// Retrieve a reference to the share of shelf picker
			viewHolder.pickerShareOfShelf = (TinyNumberPicker) convertView.findViewById ( R.id.tiny_number_picker_share_of_shelf );
			// Retrieve a reference to the competitor share of shelf picker
			viewHolder.pickerCompetitorShareOfShelf = (TinyNumberPicker) convertView.findViewById ( R.id.tiny_number_picker_competitor_share_of_shelf );
			// Retrieve a reference to the category picker
			viewHolder.pickerCategory = (TinyNumberPicker) convertView.findViewById ( R.id.tiny_number_picker_category );
			// Retrieve a reference to the percentage label
			viewHolder.percentage = (TextView) convertView.findViewById ( R.id.label_percentage );
			// Retrieve a reference to the BDA meter label
			viewHolder.bdaMeter = (TextView) convertView.findViewById ( R.id.label_bda_meter );
			// Retrieve a reference to the BDA percentage label
			viewHolder.bdaPercentage = (TextView) convertView.findViewById ( R.id.label_bda_percentage );
			// Enabled the edit texts accordingly
			viewHolder.editTextNotes.setEnabled ( isEnabled );
			viewHolder.pickerShareOfShelf.setEnabled ( isEnabled && ! isNoteOnly );
			viewHolder.pickerCompetitorShareOfShelf.setEnabled ( isEnabled && ! isNoteOnly );
			viewHolder.pickerCategory.setEnabled ( isEnabled && ! isNoteOnly );
			
			// Store the view holder as tag
			convertView.setTag ( viewHolder );
		} // End if
		else
			// An inflated view is already provided, retrieve the stored view holder
			viewHolder = (ViewHolder) convertView.getTag ();

		// Set the current tracker
		viewHolder.tracker = getItem ( position );
		
		// Declare and initialize a tracker watcher
		TrackerWatcher trackerWatcher = null;
		// Display the brand name
		viewHolder.item.setText ( viewHolder.tracker.getBrand ().getBrandDescription () );
		// Clear the previous text watcher
		clearTextWatcher ( viewHolder.editTextNotes );
		// Display the notes
		viewHolder.editTextNotes.setText ( viewHolder.tracker.getNotes () );
		// Assign a new text watcher
		NotesWatcher notesWatcher = new NotesWatcher ( getItem ( position ) );
		viewHolder.editTextNotes.addTextChangedListener ( notesWatcher );
		viewHolder.editTextNotes.setTag ( notesWatcher );
		// Clear the edit text focus
		checkEditTextFocus ( viewHolder.editTextNotes );
		// Clear the previous text watcher
		clearTextWatcher ( viewHolder.pickerShareOfShelf.getEditText () );
		// Display the share of shelf in the picker
		viewHolder.pickerShareOfShelf.setNumber ( viewHolder.tracker.getShareOfShelf () );
		// Assign a new text watcher
		trackerWatcher = new TrackerWatcher ( (ShareOfShelfTrackerDetailsActivity) getContext () , convertView , getItem ( position ) , TrackerWatcher.Type.SHARE_OF_SHELF );
		viewHolder.pickerShareOfShelf.getEditText ().addTextChangedListener ( trackerWatcher );
		viewHolder.pickerShareOfShelf.getEditText ().setTag ( trackerWatcher );
		// Clear the edit text focus
		checkEditTextFocus ( viewHolder.pickerShareOfShelf.getEditText () );
		// Clear the previous text watcher
		clearTextWatcher ( viewHolder.pickerCompetitorShareOfShelf.getEditText () );
		// Display the competitor share of shelf in the picker
		viewHolder.pickerCompetitorShareOfShelf.setNumber ( viewHolder.tracker.getCompetitorShareOfShelf () );
		// Assign a new text watcher
		trackerWatcher = new TrackerWatcher ( (ShareOfShelfTrackerDetailsActivity) getContext () , convertView , getItem ( position ) , TrackerWatcher.Type.COMPETITOR_SHARE_OF_SHELF );
		viewHolder.pickerCompetitorShareOfShelf.getEditText ().addTextChangedListener ( trackerWatcher );
		viewHolder.pickerCompetitorShareOfShelf.getEditText ().setTag ( trackerWatcher );
		// Clear the edit text focus
		checkEditTextFocus ( viewHolder.pickerCompetitorShareOfShelf.getEditText () );
		// Clear the previous text watcher
		clearTextWatcher ( viewHolder.pickerCategory.getEditText () );
		// Display the share of shelf in the picker
		viewHolder.pickerCategory.setNumber ( viewHolder.tracker.getCategory () );
		// Assign a new text watcher
		trackerWatcher = new TrackerWatcher ( (ShareOfShelfTrackerDetailsActivity) getContext () , convertView , getItem ( position ) , TrackerWatcher.Type.CATEGORY );
		viewHolder.pickerCategory.getEditText ().addTextChangedListener ( trackerWatcher );
		viewHolder.pickerCategory.getEditText ().setTag ( trackerWatcher );
		// Clear the edit text focus
		checkEditTextFocus ( viewHolder.pickerCategory.getEditText () );
		// Refresh the view
		refreshOrderView ( getContext () , convertView , decimalFormat );
		// Display the bda meter
		viewHolder.bdaMeter.setText ( decimalFormat.format ( viewHolder.tracker.getBDAMeter () ) );
		// Display the bda percentage
		viewHolder.bdaPercentage.setText ( decimalFormat.format ( viewHolder.tracker.getBDAPercentage () ) + " %" );
		
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