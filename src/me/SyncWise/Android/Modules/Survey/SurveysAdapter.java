/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Survey;

import java.util.List;

import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

/**
 * A concrete BaseAdapter that is backed by an array of {@link me.SyncWise.Android.Modules.Survey.Surveys Surveys} objects.
 * 
 * @author Elias
 *
 */
public class SurveysAdapter extends ArrayAdapter < Surveys > {

	/**
	 * {@link android.view.LayoutInflater} used to instantiate a layout XML file into its corresponding View objects.
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
		public ImageView icon;
		public TextView label;
		public ImageView forcedIcon;
	}
	
	/**
	 * String holding the completed label.
	 */
	private final String completedLabel;
	
	
	/**
	 * String holding the percent sign.
	 */
	private final String percentSign;
	
	/**
	 * Constructor.
	 * 
	 * @param context	The application context.
	 * @param layout	XML Layout (layout) resource id.
	 * @param surveys	List of {@link me.SyncWise.Android.Modules.Survey.Surveys Surveys} objects.
	 */
	public SurveysAdapter ( Context context, int layout , List < Surveys > surveys ) {
		// Superclass method call
		super ( context , layout , surveys );
		// Initialize the attributes
		layoutInflater = LayoutInflater.from ( context );
		this.layout = layout;
		completedLabel = AppResources.getInstance ( context ).getString ( context , R.string.completed_label );
		percentSign = AppResources.getInstance ( context ).getString ( context , R.string.percentage_sign );
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
			// Retrieve a reference to the survey icon
			viewHolder.icon = (ImageView) convertView.findViewById ( R.id.icon_survey );
			// Retrieve a reference to the survey label
			viewHolder.label = (TextView) convertView.findViewById ( R.id.label_survey );
			// Retrieve a reference to the survey forced icon
			viewHolder.forcedIcon = (ImageView) convertView.findViewById ( R.id.icon_forced );
			// Store the view holder as tag
			convertView.setTag ( viewHolder );
		} // End if
		else
			// An inflated view is already provided, retrieve the stored view holder
			viewHolder = (ViewHolder) convertView.getTag ();

		// Build a spannable string out of the survey description and completion percentage in order to apply various spans
		SpannableString surveyLabel = new SpannableString ( getItem ( position ).getSurvey ().getSurveyDescription () + "\n" + completedLabel + " : " + ( (int) getItem ( position ).getCompletionPercentage () ) + " " + percentSign );
		// Apply style span
		surveyLabel.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , getItem ( position ).getSurvey ().getSurveyDescription ().length () , 0 );
		// Apply size span
		surveyLabel.setSpan ( new RelativeSizeSpan ( 0.66f ) , getItem ( position ).getSurvey ().getSurveyDescription ().length () , surveyLabel.length () , 0 );
		// Display the survey label
		viewHolder.label.setText ( surveyLabel , BufferType.SPANNABLE );
		// Display the appropriate survey icon
		viewHolder.icon.setImageResource ( getItem ( position ).getCompletionPercentage () == 100.0 ? R.drawable.ok : R.drawable.cancel );
		// Display the survey forced icon accordingly
		viewHolder.forcedIcon.setVisibility ( getItem ( position ).isForced () ? View.VISIBLE : View.GONE );
		
		// Return the view
		return convertView;
	}
	
	/*
	 * Get a View that displays in the drop down popup the data at the specified position in the data set.
	 *
	 * @see android.widget.ArrayAdapter#getDropDownView(int, android.view.View, android.view.ViewGroup)
	 */
	public View getDropDownView ( int position , View convertView , ViewGroup parent ) {
		// Return the view
		return getView ( position , convertView , parent );
	}
	
}