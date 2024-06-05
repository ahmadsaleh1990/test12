/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Objective;

import java.util.Calendar;

import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.DateTime;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.ObjectiveAchievementsDao;
import me.SyncWise.Android.Database.ObjectivePrioritiesDao;
import me.SyncWise.Android.Database.ObjectivesDao;
import me.SyncWise.Android.Database.ObjectivesUtils;
import me.SyncWise.Android.Database.ReasonsDao;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

/**
 * Adapter that exposes data from a Cursor to a ListView widget.
 * 
 * @author Elias
 *
 */
public class ClientObjectiveAdapter extends CursorAdapter {

	/**
	 * {@link android.view.LayoutInflater LayoutInflater} used to instantiate a layout XML file into its corresponding View objects.
	 */
	private final LayoutInflater layoutInflater;
	
	/**
	 * XML Layout (layout) resource id.
	 */
	private final int layout;
	
	/**
	 * Nested class implementation of the view holder pattern used to keep references to children views in order to avoid unnecessary calls to {@link android.view.View#findViewById(int) findViewById(id)} on each row.
	 * 
	 * @author Elias
	 *
	 */
	public static class ViewHolder {
		public boolean isPending;
		public boolean isFinal;
		public long objectiveID;
		public TextView data;
		public TextView description;
		public TextView note;
		public TextView reason;
		public ImageView status;
	}
	
	/**
	 * String used to hold the new line string.
	 */
	private final String newLine = "\n";
	
	/**
	 * String used to hold the date label.
	 */
	private final String dateLabel;
	
	/**
	 * String used to hold the priority label.
	 */
	private final String priorityLabel;
	
	/**
	 * String used to hold the not available label.
	 */
	private final String notAvailableLabel;
	
	/**
	 * String used to hold the description label.
	 */
	private final String descriptionLabel;

	/**
	 * String used to hold the note label.
	 */
	private final String noteLabel;
	
	/**
	 * String used to hold the note label.
	 */
	private final String reasonLabel;
	
	/**
	 * Integer used to hold the brown color value.
	 */
	private final int brownColor;
	
	/**
	 * Constructor.
	 * 
	 * @param context	The application context.
	 * @param cursor	Cursor holding a result set returned by a database query.
	 * @param layout	Integer holding the item layout resource id.
	 */
	public ClientObjectiveAdapter ( Context context , Cursor cursor , int layout ) {
		// Superclass method call
		super ( context , cursor , false );
		// Initialize the attributes
		layoutInflater = LayoutInflater.from ( context );
		this.layout = layout;
		dateLabel = AppResources.getInstance ( context ).getString ( context , R.string.date_label );
		priorityLabel = AppResources.getInstance ( context ).getString ( context , R.string.priority_label );
		notAvailableLabel = AppResources.getInstance ( context ).getString ( context , R.string.not_available_abbreviation );
		descriptionLabel = AppResources.getInstance ( context ).getString ( context , R.string.description_label );
		noteLabel = AppResources.getInstance ( context ).getString ( context , R.string.note_label );
		reasonLabel = AppResources.getInstance ( context ).getString ( context , R.string.reason_label );
    	brownColor = context.getResources ().getColor ( R.color.Brown );
	}
	
	/*
	 * Makes a new view to hold the data pointed to by cursor.
	 *
	 * @see android.widget.CursorAdapter#newView(android.content.Context, android.database.Cursor, android.view.ViewGroup)
	 */
	@Override
	public View newView ( Context context , Cursor cursor , ViewGroup parent ) {
		// Inflate and return a new view hierarchy from the specified xml resource
		return layoutInflater.inflate ( layout , null );
	}
	
	/*
	 * Makes a new view to hold the data pointed to by cursor.
	 *
	 * @see android.widget.CursorAdapter#bindView(android.view.View, android.content.Context, android.database.Cursor)
	 */
	@Override
	public void bindView ( View view , Context context , Cursor cursor ) {
		// Declare a view holder
		ViewHolder  viewHolder;
		// Check if a recycled view is provided
		if ( view.getTag () == null ) {
			// Initialize the view holder
			viewHolder = new ViewHolder ();
			// Retrieve a reference to the objective data label
			viewHolder.data = (TextView) view.findViewById ( R.id.label_objective_data );
			// Retrieve a reference to the objective description label
			viewHolder.description = (TextView) view.findViewById ( R.id.label_objective_description );
			// Retrieve a reference to the objective note label
			viewHolder.note = (TextView) view.findViewById ( R.id.label_objective_note );
			// Retrieve a reference to the objective reason label
			viewHolder.reason = (TextView) view.findViewById ( R.id.label_objective_reason );
			// Retrieve a reference to the objective status icon
			viewHolder.status = (ImageView) view.findViewById ( R.id.objective_status_icon );
			// Store the view holder as tag
			view.setTag ( viewHolder );
		} // End if
		else
			// An recycled view is already provided, retrieve the stored view holder
			viewHolder = (ViewHolder) view.getTag ();
		
		// Store the objective ID
		viewHolder.objectiveID = cursor.getLong ( cursor.getColumnIndex ( ObjectivesDao.Properties.ObjectiveID.columnName ) );
		
		// Retrieve the objective date
		Calendar objectiveDate_Calendar = Calendar.getInstance ();
		objectiveDate_Calendar.setTimeInMillis ( cursor.getLong ( cursor.getColumnIndex ( ObjectivesDao.Properties.StampDate.columnName ) ) );
		// Compute the objective date
		String objectiveDate_Str = DateTime.getFullDate ( context , objectiveDate_Calendar );
		// Retrieve the objective priority
		String objectivePriority_Str = cursor.getString ( cursor.getColumnIndex ( ObjectivePrioritiesDao.Properties.ObjectivePriorityDescription.columnName ) );
		objectivePriority_Str =  objectivePriority_Str == null ? notAvailableLabel : objectivePriority_Str;
		// Retrieve the objective description
		String objectiveDescription_Str = cursor.getString ( cursor.getColumnIndex ( ObjectivesDao.Properties.ObjectiveDescription.columnName ) );
		// Retrieve the objective note
		String objectiveNote_Str = cursor.getString ( cursor.getColumnIndex ( ObjectiveAchievementsDao.Properties.Notes.columnName ) );
		// Retrieve the reason name
		String reasonName_Str = cursor.getString ( cursor.getColumnIndex ( ReasonsDao.Properties.ReasonName.columnName ) );
		
		// Build a spannable string out of the objective date in order to apply various spans
		SpannableString objectiveDate = new SpannableString ( dateLabel + " : " + objectiveDate_Str );
		// Apply style span
		objectiveDate.setSpan ( new StyleSpan ( Typeface.BOLD ) ,
				dateLabel.length () + " : ".length () ,
				dateLabel.length () + " : ".length () + objectiveDate_Str.length () , 0 );
		// Apply color span
		objectiveDate.setSpan ( new ForegroundColorSpan ( brownColor ) ,
				dateLabel.length () + " : ".length () ,
				dateLabel.length () + " : ".length () + objectiveDate_Str.length () , 0 );
		
		// Build a spannable string out of the objective data in order to apply various spans
		SpannableString objectivePriority = new SpannableString ( priorityLabel + " : " + objectivePriority_Str );
		// Apply style span
		objectivePriority.setSpan ( new StyleSpan ( Typeface.BOLD ) ,
				priorityLabel.length () + " : ".length () ,
				priorityLabel.length () + " : ".length () + objectivePriority_Str.length () , 0 );
		// Apply color span
		objectivePriority.setSpan ( new ForegroundColorSpan ( brownColor ) ,
				priorityLabel.length () + " : ".length () ,
				priorityLabel.length () + " : ".length () + objectivePriority_Str.length () , 0 );
		// Display the objective data
		viewHolder.data.setText ( TextUtils.concat ( objectiveDate , newLine , objectivePriority ) , BufferType.SPANNABLE );
		
		// Build a spannable string out of the objective description in order to apply various spans
		SpannableString objectiveDescription = new SpannableString ( descriptionLabel + " : " + objectiveDescription_Str );
		// Apply style span
		objectiveDescription.setSpan ( new StyleSpan ( Typeface.BOLD ) ,
				descriptionLabel.length () + " : ".length () ,
				descriptionLabel.length () + " : ".length () + objectiveDescription_Str.length () , 0 );
		// Apply color span
		objectiveDescription.setSpan ( new ForegroundColorSpan ( brownColor ) ,
				descriptionLabel.length () + " : ".length () ,
				descriptionLabel.length () + " : ".length () + objectiveDescription_Str.length () , 0 );
		// Display the objective description
		viewHolder.description.setText ( objectiveDescription , BufferType.SPANNABLE );
		
		// Check if the objective has notes
		if ( ! TextUtils.isEmpty ( objectiveNote_Str ) ) {
			// Display the objective note label
			viewHolder.note.setVisibility ( View.VISIBLE );
			// Build a spannable string out of the objective note in order to apply various spans
			SpannableString objectiveNote = new SpannableString ( noteLabel + " : " + objectiveNote_Str );
			// Apply style span
			objectiveNote.setSpan ( new StyleSpan ( Typeface.BOLD ) ,
					noteLabel.length () + " : ".length () ,
					noteLabel.length () + " : ".length () + objectiveNote_Str.length () , 0 );
			// Apply color span
			objectiveNote.setSpan ( new ForegroundColorSpan ( brownColor ) ,
					noteLabel.length () + " : ".length () ,
					noteLabel.length () + " : ".length () + objectiveNote_Str.length () , 0 );
			// Display the objective note label
			viewHolder.note.setText ( objectiveNote , BufferType.SPANNABLE );
		} // End if
		else
			// Hide the objective note label
			viewHolder.note.setVisibility ( View.GONE );
		
		// Check if the objective has a reason
		if ( reasonName_Str != null ) {
			// Display the objective reason label
			viewHolder.reason.setVisibility ( View.VISIBLE );
			// Build a spannable string out of the reason name in order to apply various spans
			SpannableString objectiveReason = new SpannableString ( reasonLabel + " : " + reasonName_Str );
			// Apply style span
			objectiveReason.setSpan ( new StyleSpan ( Typeface.BOLD ) ,
					reasonLabel.length () + " : ".length () ,
					reasonLabel.length () + " : ".length () + reasonName_Str.length () , 0 );
			// Apply color span
			objectiveReason.setSpan ( new ForegroundColorSpan ( brownColor ) ,
					reasonLabel.length () + " : ".length () ,
					reasonLabel.length () + " : ".length () + reasonName_Str.length () , 0 );
			// Display the objective note label
			viewHolder.reason.setText ( objectiveReason , BufferType.SPANNABLE );
		} // End if
		else
			// Hide the objective reason label
			viewHolder.reason.setVisibility ( View.GONE );
		
		// Determine if the objective is final
		viewHolder.isFinal = cursor.getInt ( cursor.getColumnIndex ( ObjectivesDao.Properties.ObjectiveSource.columnName ) ) != ObjectivesUtils.Source.DEVICE;
		// Determine if the objective is pending
		viewHolder.isPending = cursor.isNull ( cursor.getColumnIndex ( ObjectiveAchievementsDao.Properties.AchievementDate.columnName ) );
		// Check if the objective is pending
		if ( viewHolder.isPending ) {
			// Objective is pending
			// Display the appropriate objective status icon
			viewHolder.status.setImageResource ( R.drawable.sync );
		} // End if
		else {
			// Determine if the objective is either completed or cancelled
			boolean completed = false;
			// Retrieve the reason code
			String reasonCode = cursor.getString ( cursor.getColumnIndex ( ObjectiveAchievementsDao.Properties.ReasonCode.columnName ) );
			// Check if the reason code is valid
			if ( reasonCode == null )
				// Indicate that the objective is completed
				completed = true;
			// Display the appropriate objective status icon
			viewHolder.status.setImageResource ( completed ? R.drawable.ok : R.drawable.cancel );
		} // End else
	}
	
}