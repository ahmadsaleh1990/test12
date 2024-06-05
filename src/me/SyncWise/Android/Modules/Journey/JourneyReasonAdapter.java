/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Journey;

import java.lang.ref.WeakReference;
import java.util.List;

import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.Reasons;

import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * A concrete BaseAdapter that is backed by an array of {@link me.SyncWise.Android.Modules.Journey.JourneyReason JourneyReason} objects.
 * 
 * @author Elias
 *
 */
public class JourneyReasonAdapter extends ArrayAdapter < JourneyReason > {

	/**
	 * Constant integer holding the status animation duration in milliseconds.
	 */
	public static final int STATUS_ANIMATION_DURATION = 250;
	
	/**
	 * Weak reference to the journey reason activity.
	 */
	private WeakReference < JourneyReasonActivity > activity;
	
	/**
	 * A {@link me.SyncWise.Android.Modules.Journey.ReasonAdapter ReasonAdapter} used to populate the spinner in each journey reason item.
	 */
	private final ReasonAdapter reasonAdapter;
	
	/**
	 * {@link android.view.LayoutInflater} used to instantiate a layout XML file into its corresponding View objects.
	 */
	private final LayoutInflater layoutInflater;
	
	/**
	 * XML Layout (layout) resource id.
	 */
	private final int layout;
	
	/**
	 * Getter - The list of reasons assigned to {@link #reasonAdapter}.<br>
	 * <b>Warning :</b> might be null !
	 * 
	 * @return	List of {@link me.SyncWise.Android.Database.Reasons Reasons} objects.
	 */
	public List < Reasons > getReasons () {
		// Return the list of reasons from the reason adapter
		return reasonAdapter.getReasons ();
	}
	
	/**
	 * Callback implementation to be invoked when a journey item in this view has been selected. 
	 * 
	 * @author Elias
	 *
	 */
	private class ReasonListener implements OnItemSelectedListener {
		
		/**
		 * Reference to the {@link me.SyncWise.Android.Modules.Journey.JourneyReasonAdapter.ViewHolder ViewHolder} pattern of the selected journey item.
		 */
		private final ViewHolder viewHolder;
		
		/**
		 * Reference to the {@link me.SyncWise.Android.Modules.Journey.JourneyReason JourneyReason} object of the selected journey item.
		 */
		private final JourneyReason journey;
		
		/**
		 * Constructor.
		 * 
		 * @param viewHolder	{@link me.SyncWise.Android.Modules.Journey.JourneyReasonAdapter.ViewHolder ViewHolder} pattern of the selected journey item.
		 * @param journey	{@link me.SyncWise.Android.Modules.Journey.JourneyReason JourneyReason} object of the selected journey item.
		 */
		public ReasonListener ( final ViewHolder viewHolder , final JourneyReason journey ) {
			this.journey = journey;
			this.viewHolder = viewHolder;
		}
		
		/*
		 * Callback method to be invoked when an item in this view has been selected.
		 *
		 * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android.widget.AdapterView, android.view.View, int, long)
		 */
		@Override
		public void onItemSelected ( AdapterView < ? > parent , View view , int position , long id ) {
			// Determine if both the selected reason in the spinner and the journey's reason are the same
			if ( ( journey.getReasonCode () == null && position == 0 )
					|| ( journey.getReasonCode () != null && journey.getReasonCode ().equals ( reasonAdapter.getReasons ().get ( position ).getReasonCode () ) ) )
				// Similar reasons, do nothing
				return;
			
			// Otherwise a NEW reason is selected
			// Determine if a valid reason is selected (the default reason at position 0 is not a valid reason)
			if ( position == 0 )
				// Clear the reason code
				journey.clearReasonCode ();
			else
				// Set the newly selected reason
				journey.setReasonCode ( reasonAdapter.getReasons ().get ( position ) );
			// Modify the journey status accordingly
			modifyStatus ( viewHolder , journey.hasReason () );
			// Modify the total number of missed days (if possible)
			if ( activity != null && activity.get () != null )
				activity.get ().displayMissedDaysNumber ();
		}
		
		/*
		 * Callback method to be invoked when the selection disappears from this view.
		 *
		 * @see android.widget.AdapterView.OnItemSelectedListener#onNothingSelected(android.widget.AdapterView)
		 */
		public void onNothingSelected ( AdapterView < ? > parent ) {
			// Do nothing
		}
		
	    /**
	     * Modifies the status icon in an animated manner :
	     * <ol>
	     * <li>The status icon is scaled down till it disappears.</li>
	     * <li>The status icon is modified based on the journey reason value.</li>
	     * <li>The status icon is scaled up till its original / initial size is restored.</li>
	     * </ol>
	     * 
	     * @param viewHolder	{@link me.SyncWise.Android.Modules.Journey.JourneyReasonAdapter.ViewHolder ViewHolder} pattern of the selected journey item.
	     * @param hasReason	Boolean indicating the journey reason validity.
	     */
	    private void modifyStatus ( final ViewHolder viewHolder  , final boolean hasReason ) {
	    	// Fully scale down the status icon
	        viewHolder.status.animate ().scaleX ( 0f ).scaleY ( 0f ).setDuration ( STATUS_ANIMATION_DURATION );
	        // Declare and initialize a handler, mainly used to scale the status icon back up after a delay
	        Handler handler = new Handler ();
	        // Execute the runnable after a delay
	        handler.postDelayed ( new Runnable() {
				@Override
				public void run () {
					// Modify the status icon accordingly
					viewHolder.status.setImageResource ( hasReason ? R.drawable.ok : R.drawable.cancel );
					// Fully scale up the status icon
					viewHolder.status.animate ().scaleX ( 1f ).scaleY ( 1f ).setDuration ( STATUS_ANIMATION_DURATION );
				}
			} , STATUS_ANIMATION_DURATION );
	    }
	}
	
	/**
	 * Nested class implementation of the view holder pattern used to keep references to children views in order to avoid unnecessary calls to {@link android.view.View#findViewById(int) findViewById(id)} on each row.
	 * 
	 * @author Elias
	 *
	 */
	public static class ViewHolder {
		public ImageView status;
		public TextView label;
		public Spinner spinner;
		public ImageView arrow;
		public CheckBox checkBox;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param activity	A reference to the calling {@link me.SyncWise.Android.Modules.Journey.JourneyReasonActivity JourneyReasonActivity}.
	 * @param layout	XML Layout (layout) resource id.
	 * @param journeys	List of {@link me.SyncWise.Android.Modules.Journey.JourneyReason Reason} objects.
	 * @param reasonAdapter	A {@link me.SyncWise.Android.Modules.Journey.ReasonAdapter ReasonAdapter} used to populate the spinners with reasons.
	 */
	public JourneyReasonAdapter ( JourneyReasonActivity activity, int layout , List < JourneyReason > journeys , ReasonAdapter reasonAdapter ) {
		// Superclass method call
		super ( activity , layout , journeys );
		// Initialize the attributes
		layoutInflater = LayoutInflater.from ( activity );
		this.layout = layout;
		this.reasonAdapter = reasonAdapter;
		this.activity = new WeakReference < JourneyReasonActivity > ( activity );
	}
	
	/*
	 * Get a View that displays the data at the specified position in the data set.
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView ( int position , View convertView , ViewGroup parent ) {
		// Declare the row view holder
		ViewHolder viewHolder;
		// Check if an inflated view is provided
		if ( convertView == null ) {
			// A new view must be inflated
			convertView = layoutInflater.inflate ( layout , null );
			// Declare and initialize a view holder
			viewHolder = new ViewHolder ();
			// Retrieve a reference to the reason status
			viewHolder.status = (ImageView) convertView.findViewById ( R.id.icon_reason_status );
			// Retrieve a reference to the calendar label
			viewHolder.label = (TextView) convertView.findViewById ( R.id.label_calendar );
			// Retrieve a reference to the spinner
			viewHolder.spinner = (Spinner) convertView.findViewById ( R.id.spinner );
			// Retrieve a reference to the arrow
			viewHolder.arrow = (ImageView) convertView.findViewById ( R.id.arrow_right );
			// Retrieve a reference to the check box
			viewHolder.checkBox = (CheckBox) convertView.findViewById ( R.id.checkbox );
			// Store the view holder as tag
			convertView.setTag ( viewHolder );
		} // End if
		else
			// An inflated view is already provided, retrieve the stored view holder
			viewHolder = (ViewHolder) convertView.getTag ();

		// Check if the current journey has a valid reason
		if ( getItem ( position ).hasReason () )
			// Set the status icon as true
			viewHolder.status.setImageResource ( R.drawable.ok );
		else
			// Set the status icon as false
			viewHolder.status.setImageResource ( R.drawable.cancel );
		// Set the calendar label
		viewHolder.label.setText ( getItem ( position ).getCalendarLabel () );
		// Check if the mutli-selection feature is enabled
		if ( activity != null && activity.get ().isMultiSelect () ) {
			// Display the check box
			viewHolder.checkBox.setVisibility ( View.VISIBLE );
			viewHolder.checkBox.clearAnimation ();
			viewHolder.checkBox.setLayoutParams ( new FrameLayout.LayoutParams ( FrameLayout.LayoutParams.WRAP_CONTENT , FrameLayout.LayoutParams.WRAP_CONTENT ,
			Gravity.CENTER_VERTICAL | Gravity.RIGHT ) );
			// Hide the arrow
			viewHolder.arrow.clearAnimation ();
			viewHolder.arrow.setVisibility ( View.GONE );
			// Check the check box accordingly
			viewHolder.checkBox.setChecked ( getItem ( position ).getSelected () );
			// Hide the spinner
			viewHolder.spinner.setVisibility ( View.GONE );
		} // End if
		else {
			// Hide the check box
			viewHolder.checkBox.setVisibility ( View.GONE );
			viewHolder.checkBox.clearAnimation ();
			viewHolder.checkBox.setLayoutParams ( new FrameLayout.LayoutParams ( 0 , 0 ) );
			// Display the arrow
			viewHolder.arrow.clearAnimation ();
			viewHolder.arrow.setVisibility ( View.VISIBLE );
			// Display / Hide the spinner accordingly (If the current journey is selected or not)
			viewHolder.spinner.setVisibility ( getItem ( position ).getSelected () ? View.VISIBLE : View.GONE );
		} // End else
		
		// Process the view : display the appropriate arrow and spinner
		processView ( viewHolder , getItem ( position ) );
		
		// Return the view
		return convertView;
	}
	
	/**
	 * Processes a view representing a journey via its {@link me.SyncWise.Android.Modules.Journey.JourneyReasonAdapter.ViewHolder ViewHolder} object and the {@link me.SyncWise.Android.Modules.Journey.JourneyReason JourneyReason} object itself.<br>
	 * The view is processed in the following manner :
	 * <ul>
	 * <li>If the journey is selected, the arrow is displayed in green while the spinner is initialized with the appropriate reason selected.</li>
	 * <li>If the journey is NOT selected, the arrow is displayed in black while the spinner is reset.</li>
	 * </ul>
	 * <b>Note :</b> The spinner visibility is not taken care of !
	 * 
	 * @param viewHolder	A {@link me.SyncWise.Android.Modules.Journey.JourneyReasonAdapter.ViewHolder ViewHolder} pattern holding all the children references of the journey view to process.
	 * @param journey	The {@link me.SyncWise.Android.Modules.Journey.JourneyReason JourneyReason} whose view should be processed.
	 */
	public void processView ( final ViewHolder viewHolder , JourneyReason journey ) {
		// Determine if both arguments are valid
		if ( journey == null || viewHolder == null || viewHolder.spinner == null || viewHolder.arrow == null )
			// Invalid argument(s)
			return;
		
		// Otherwise the arguments are valid
		// Check if the current journey is selected
		if ( journey.getSelected () ) {
			// Display the green arrow
			viewHolder.arrow.setImageResource ( R.drawable.arrow_right_green );
			// Set the spinner adapter
			viewHolder.spinner.setAdapter ( reasonAdapter );
			// Select the corresponding reason
			viewHolder.spinner.setSelection ( journey.hasReason () ? getSpinnerSelectedItemIndex ( journey.getReasonCode () ) : 0 );
			// Set the spinner listener
			viewHolder.spinner.setOnItemSelectedListener ( new ReasonListener ( viewHolder , journey ) );
		} // End if
		else {
			// Display the black arrow
			viewHolder.arrow.setImageResource ( R.drawable.arrow_right_black );
			// Set the spinner listener
			viewHolder.spinner.setOnItemSelectedListener ( null );
			// Set the spinner adapter
			viewHolder.spinner.setAdapter ( null );	
		} // End else
	}
	
	/**
	 * Returns the appropriate spinner selected item index that matches the provided reason code.
	 * 
	 * @param reasonCode	String holding the required reason code.
	 * @return	Integer holding the index of the selected spinner item.
	 */
	private int getSpinnerSelectedItemIndex ( final String reasonCode ) {
		// Check if the reason code is valid
		if ( TextUtils.isEmpty ( reasonCode ) )
			// Invalid reason code
			return 0;
		// Otherwise the reason code is valid
		// Iterate over all reasons
		for ( int i = 0 ; i < reasonAdapter.getReasons ().size () ; i ++ )
			// Match the reason codes
			if ( reasonAdapter.getReasons ().get ( i ).getReasonCode ().equals ( reasonCode ) )
				// Return the current reason's index
				return i;
		// Otherwise the reason with the provided reason code is not found
		return 0;
	}
	
}