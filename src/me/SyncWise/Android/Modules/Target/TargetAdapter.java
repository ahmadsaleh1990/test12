/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Target;

import java.util.List;

import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * A concrete BaseAdapter that is backed by an array of {@link me.SyncWise.Android.Modules.Target Target} objects.
 * 
 * @author Elias
 *
 */
public class TargetAdapter extends ArrayAdapter < Target > {

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
		public TextView subjectCode;
		public TextView subjectDescription;
		public ImageView subjectIcon;
		public TextView subSubjectCode;
		public TextView subSubjectDescription;
		public ImageView subSubjectIcon;
		public TextView target;
		public ProgressBar progressBar;
		public TextView labelPercentage;
		public TextView coverage;
	}
	
	/**
	 * String used to host the percentage sign.
	 */
	private final String percentageSign;
	
	/**
	 * Constructor.
	 * 
	 * @param context	The application context.
	 * @param layout	XML Layout (layout) resource id.
	 * @param targets	List of {@link me.SyncWise.Android.Modules.Target Target} objects.
	 */
	public TargetAdapter ( Context context, int layout , List < Target > targets ) {
		// Superclass method call
		super ( context , layout , targets );
		// Initialize the attributes
		layoutInflater = LayoutInflater.from ( context );
		this.layout = layout;
		percentageSign = " " + AppResources.getInstance ( context ).getString ( context , R.string.percentage_sign );
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
			// Retrieve a reference to the subject icon
			viewHolder.subjectIcon = (ImageView) convertView.findViewById ( R.id.icon_targeted_subject );
			// Retrieve a reference to the subject code
			viewHolder.subjectCode = (TextView) convertView.findViewById ( R.id.label_subject_code );
			// Retrieve a reference to the subject description
			viewHolder.subjectDescription = (TextView) convertView.findViewById ( R.id.label_subject_description );
			// Retrieve a reference to the sub subject icon
			viewHolder.subSubjectIcon = (ImageView) convertView.findViewById ( R.id.icon_targeted_sub_subject );
			// Retrieve a reference to the sub subject code
			viewHolder.subSubjectCode = (TextView) convertView.findViewById ( R.id.label_sub_subject_code );
			// Retrieve a reference to the sub subject description
			viewHolder.subSubjectDescription = (TextView) convertView.findViewById ( R.id.label_sub_subject_description );
			// Retrieve a reference to the target label
			viewHolder.target = (TextView) convertView.findViewById ( R.id.label_target );
			// Retrieve a reference to the progress bar
			viewHolder.progressBar = (ProgressBar) convertView.findViewById ( R.id.progress_bar );
			// Retrieve a reference to the coverage label
			viewHolder.coverage = (TextView) convertView.findViewById ( R.id.label_coverage );
			// Retrieve a reference to the percentage label
			viewHolder.labelPercentage = (TextView) convertView.findViewById ( R.id.label_percentage );
			// Store the view holder as tag
			convertView.setTag ( viewHolder );
		} // End if
		else
			// An inflated view is already provided, retrieve the stored view holder
			viewHolder = (ViewHolder) convertView.getTag ();
		
		// Set the subject icon
		viewHolder.subjectIcon.setImageResource ( getItem ( position ).getSubjectIconID () );
		// Display the subject code
		viewHolder.subjectCode.setText ( getItem ( position ).getSubjectCode () );
		// Check if the subject description is valid
		if ( TextUtils.isEmpty ( getItem ( position ).getSubjectDescription () ) )
			// Hide the subject description
			viewHolder.subjectDescription.setVisibility ( View.GONE );
		else {
			// Display the subject description
			viewHolder.subjectDescription.setVisibility ( View.VISIBLE );
			viewHolder.subjectDescription.setText ( getItem ( position ).getSubjectDescription () );
		} // End else
		// Check if the sub subject icon is valid
		if ( getItem ( position ).getSubSubjectIconID () == 0 )
			// Hide the subject icon
			viewHolder.subSubjectIcon.setVisibility ( View.GONE );
		else {
			// Set the subject icon
			viewHolder.subSubjectIcon.setVisibility ( View.VISIBLE );
			viewHolder.subSubjectIcon.setImageResource ( getItem ( position ).getSubSubjectIconID () );
		} // End else
		// Check if the sub subject code is valid
		if ( TextUtils.isEmpty ( getItem ( position ).getSubSubjectCode () ) )
			// Hide the sub subject code
			viewHolder.subSubjectCode.setVisibility ( View.GONE );
		else {
			// Display the sub subject code
			viewHolder.subSubjectCode.setVisibility ( View.VISIBLE );
			viewHolder.subSubjectCode.setText ( getItem ( position ).getSubSubjectCode () );
		} // End else
		// Check if the sub subject description is valid
		if ( TextUtils.isEmpty ( getItem ( position ).getSubSubjectDescription () ) )
			// Hide the sub subject description
			viewHolder.subSubjectDescription.setVisibility ( View.GONE );
		else {
			// Display the sub subject description
			viewHolder.subSubjectDescription.setVisibility ( View.VISIBLE );
			viewHolder.subSubjectDescription.setText ( getItem ( position ).getSubSubjectDescription () );
		} // End else
		// Display the target label
		viewHolder.target.setText ( getItem ( position ).getTargetDescription () );
		// Display the coverage description
		viewHolder.coverage.setText ( getItem ( position ).getCoverageDescription () );
		// Set the coverage percentage
		viewHolder.progressBar.setProgress ( getItem ( position ).getCoverage () );
		// Display the coverage percentage
		viewHolder.labelPercentage.setText ( getItem ( position ).getCoverage () + percentageSign );
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
		// Indicate that the item is disabled
		return false;
	}
	
}