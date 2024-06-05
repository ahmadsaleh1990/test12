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

import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.SurveyAnswersDao;
import me.SyncWise.Android.Database.SurveysDao;
import me.SyncWise.Android.Database.UsersDao;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.TextView.BufferType;

/**
 * Adapter that exposes data from a Cursor to a ListView widget.
 * 
 * @author Elias
 *
 */
public class SurveysCursorAdapter extends CursorAdapter {

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
		public Long surveyID;
		public Long surveyAnswerID;
		public TextView description;
		public TextView user;
	}
	
	/**
	 * Constant integer holding the royal blue color.
	 */
	private final int royalBlueColor;
	
	/**
	 * String used to hold the new line character.
	 */
	private final String newLine = "\n";
	
	/**
	 * String used to host the user label.
	 */
	private final String userLabel;
	
	/**
	 * Constant integer holding the lawn green color.
	 */
	private final int lawnGreenColor;
	
	/**
	 * Constructor
	 * 
	 * @param context	The application context.
	 * @param cursor	Cursor holding a result set returned by a database query.
	 * @param layout	Integer holding the item layout resource id.
	 */
	public SurveysCursorAdapter ( Context context , Cursor cursor , int layout ) {
		// Superclass method call
		super ( context , cursor , false );
		// Initialize the attributes
		layoutInflater = LayoutInflater.from ( context );
		this.layout = layout;
		royalBlueColor = context.getResources ().getColor ( R.color.RoyalBlue );
		lawnGreenColor = context.getResources ().getColor ( R.color.LawnGreen );
		userLabel = AppResources.getInstance ( context ).getString ( context , R.string.user_label );
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
		ViewHolder viewHolder;
		// Check if a recycled view is provided
		if ( view.getTag () == null ) {
			// Initialize the view holder
			viewHolder = new ViewHolder ();
			// Retrieve a reference to the survey description label
			viewHolder.description = (TextView) view.findViewById ( R.id.label_survey_description );
			// Store the view holder as tag
			view.setTag ( viewHolder );
		} // End if
		else
			// An recycled view is already provided, retrieve the stored view holder
			viewHolder = (ViewHolder) view.getTag ();
		
		// Store the survey id
		viewHolder.surveyID = cursor.getLong ( cursor.getColumnIndex ( SurveyAnswersDao.Properties.SurveyID.columnName ) );
		// Store the survey answer id
		viewHolder.surveyAnswerID = cursor.getLong ( cursor.getColumnIndex ( SurveyAnswersDao.Properties.SurveyAnswerID.columnName ) );
		
		// Compute the survey answer index
		String index = ( cursor.getPosition () + 1 ) + ". ";
		// Retrieve the survey description
		String surveyDescription = cursor.getString ( cursor.getColumnIndex ( SurveysDao.Properties.SurveyDescription.columnName ) );
		// Retrieve the user name
		String userName = cursor.getString ( cursor.getColumnIndex ( UsersDao.Properties.UserName.columnName ) );
		// Build a spannable string out of the survey description and user name in order to apply various spans
		SpannableString description = new SpannableString ( index + surveyDescription + newLine + userLabel + " : " + userName );
		// Apply style span
		description.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , index.length () , 0 );
		// Apply color span
		description.setSpan ( new ForegroundColorSpan ( royalBlueColor ) , 0 , index.length () , 0 );
		// Apply style span
		description.setSpan ( new StyleSpan ( Typeface.BOLD ) , description.length () - userName.length () , description.length () , 0 );
		// Apply color span
		description.setSpan ( new ForegroundColorSpan ( lawnGreenColor ) , description.length () - userName.length () , description.length () , 0 );
		// Apply size span
		description.setSpan ( new RelativeSizeSpan ( 0.66f ) , description.length () - userName.length () - " : ".length () - userLabel.length () , description.length () , 0 );
		// Display the survey description
		viewHolder.description.setText ( description , BufferType.SPANNABLE );
	}
	
}