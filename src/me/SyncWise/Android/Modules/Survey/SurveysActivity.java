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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.DateTime;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.SurveyAnswersDao;
import me.SyncWise.Android.Database.SurveysDao;
import me.SyncWise.Android.Database.UsersDao;
import me.SyncWise.Android.Gson.BaseTimerListActivity;
import me.SyncWise.Android.SectionsList.MultipleListAdapter;
import me.SyncWise.Android.SectionsList.Section;
import me.SyncWise.Android.Widgets.Baguette;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

/**
 * TODO
 * 
 * @author Elias
 * 
 * TODO
 *
 */
public class SurveysActivity extends BaseTimerListActivity {

	/**
	 * Bundle key used to put/retrieve the activity title.
	 */
	public static final String ACTIVITY_TITLE = SurveysActivity.class.getName () + ".ACTIVITY_TITLE";
	
	/**
	 * Bundle key used to put/retrieve the survey type.
	 */
	public static final String SURVEY_TYPE = SurveysActivity.class.getName () + ".SURVEY_TYPE";
	
	/**
	 * Bundle key used to put/retrieve the is view flag.
	 */
	public static final String IS_VIEW = SurveysActivity.class.getName () + ".IS_VIEW";
	
	/**
	 * Bundle key used to put/retrieve the string resource id used for the baguette success messages.
	 */
	public static final String BAGUETTE_SUCCESS_ID = SurveysActivity.class.getName () + ".BAGUETTE_SUCCESS_ID";
	
	/**
	 * Bundle key used to put/retrieve the string resource id used for the baguette failure messages.
	 */
	public static final String BAGUETTE_FAILURE_ID = SurveysActivity.class.getName () + ".BAGUETTE_FAILURE_ID";
	
	/*
	 * Called when the activity is starting.
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate ( Bundle savedInstanceState ) {
		// Superclass method call	
		super.onCreate ( savedInstanceState );
		// Set the activity content from a layout resource.
		setContentView ( R.layout.surveys_activity );
		// Check if the activity title is defined in the calling intent
		String activityTitle = getIntent ().getStringExtra ( ACTIVITY_TITLE );
		// Change the title associated with this activity
		setTitle ( activityTitle == null ? AppResources.getInstance ( this ).getString ( this , R.string.surveys_activity_title ) : activityTitle );
		// Define the empty list view
		getListView ().setEmptyView ( findViewById ( R.id.empty_list_view ) );
		// Display empty list label
		( (TextView) findViewById ( R.id.empty_list_view ) ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.empty_surveys_list_label ) );
		
		// Determine if the list view has a valid adapter
		if ( getListAdapter () == null )
			// Populate the list by setting a new adapter
			new PopulateList ().execute ();
	}
	
	/*
	 * Called as part of the activity lifecycle when an activity is going into the background, but has not (yet) been killed.
	 *
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause () {
		// Superclass method call
		super.onPause ();
		// Remove any displayed baguette
		Baguette.remove ( this );
	}
	
	/*
	 * Initialize the contents of the Activity's standard options menu.
	 *
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
    @Override
    public boolean onCreateOptionsMenu ( Menu menu ) {
    	// Use the MenuInflater of this context to inflate a menu hierarchy from the specified XML resource
    	getMenuInflater ().inflate ( R.menu.action_bar , menu );
    	// Check the IS_VIEW flag
    	if ( ! getIntent ().getBooleanExtra ( IS_VIEW , false ) ) {
	    	// Enable the required menu items
	    	me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_add ) , R.string.add_label );
    	} // End if
    	// Display the menu
    	return true;
    }
    
    /*
     * This hook is called whenever an item in your options menu is selected.
     *
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected ( MenuItem menuItem ) {
    	// Determine if the action add is selected
    	if ( menuItem.getItemId () == R.id.action_add ) {
    		// Create a new intent to start a new activity
    		Intent intent = new Intent ( this , SurveyQuestionsActivity.class );
    		// Add the survey type to the intent
    		intent.putExtra ( SurveyQuestionsActivity.SURVEY_TYPE , getIntent ().getSerializableExtra ( SURVEY_TYPE ) );
    		// Add the activity title to the intent
    		intent.putExtra ( SurveyQuestionsActivity.ACTIVITY_TITLE , getIntent ().getStringExtra ( ACTIVITY_TITLE ) );
    		// Start the new activity
    		startActivityForResult ( intent , 1 );
    		// Consume event
    		return true;
    	} // End if
    	// Allow normal menu processing to proceed
    	return false;
    }

    /*
     * This method will be called when an item in the list is selected.
     *
     * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
     */
	@Override
    protected void onListItemClick ( ListView listView , View view , final int position , long id ) {
		// Retrieve a reference to the cursor positioned to the current raw
		Cursor cursor = (Cursor) listView.getItemAtPosition ( position );
		
		// Create a new intent to start a new activity
		Intent intent = new Intent ( this , SurveyQuestionsActivity.class );
		// Add the survey type to the intent
		intent.putExtra ( SurveyQuestionsActivity.SURVEY_TYPE , getIntent ().getSerializableExtra ( SURVEY_TYPE ) );
		// Add the survey id to the intent
		intent.putExtra ( SurveyQuestionsActivity.SURVEY_ID , cursor.getLong ( cursor.getColumnIndex ( SurveyAnswersDao.Properties.SurveyID.columnName ) ) );
		// Add the survey answer id to the intent
		intent.putExtra ( SurveyQuestionsActivity.SURVEY_ANSWER_ID , cursor.getLong ( cursor.getColumnIndex ( SurveyAnswersDao.Properties.SurveyAnswerID.columnName ) ) );
		// Add the activity title to the intent
		intent.putExtra ( SurveyQuestionsActivity.ACTIVITY_TITLE , getIntent ().getStringExtra ( ACTIVITY_TITLE ) );
		// Add the is view flag to the intent
		intent.putExtra ( SurveyQuestionsActivity.IS_VIEW , true );
		// Start the new activity
		startActivity ( intent );
	}
	
	/*
	 * Called when an activity you launched exits, giving you the requestCode you started it with, the resultCode it returned, and any additional data from it.
	 *
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	protected void onActivityResult ( int requestCode , int resultCode , Intent data ) {
    	// Check if the result is successful and the intent is valid
    	if ( resultCode != RESULT_OK || data == null )
    		// Exit method
    		return;
    	
    	// Check if the survey was successfully saved
    	if ( data.getBooleanExtra ( SurveyQuestionsActivity.SURVEY_SUCCESSFULLY_SAVED , false ) ) {
        	// Check if the success save message is provided
    		if ( getIntent ().getSerializableExtra ( BAGUETTE_SUCCESS_ID ) != null )
				Baguette.showText ( this ,
						AppResources.getInstance ( this ).getString ( this , (Integer) getIntent ().getSerializableExtra ( BAGUETTE_SUCCESS_ID ) ) ,
						Baguette.BackgroundColor.GREEN );
			// Populate the list by setting a new adapter
			new PopulateList ().execute ();
    	} // End if
    	// Otherwise check if the survey was NOT successfully saved
    	// AND if the failure save message is provided
    	else if ( ! data.getBooleanExtra ( SurveyQuestionsActivity.SURVEY_SUCCESSFULLY_SAVED , false )
    			&& getIntent ().getSerializableExtra ( BAGUETTE_FAILURE_ID ) != null )
			Baguette.showText ( this ,
					AppResources.getInstance ( this ).getString ( this , (Integer) getIntent ().getSerializableExtra ( BAGUETTE_FAILURE_ID ) ) ,
					Baguette.BackgroundColor.RED );
	}
	
	/**
	 * AsyncTask helper class used to populate the sales order items list.
	 * 
	 * @author Elias
	 *
	 */
	private class PopulateList extends AsyncTask < Void , Void , Map < String , Cursor > > {
		
		/*
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Map < String , Cursor > doInBackground ( Void ... params ) {
			try {
				// Compute the SQL string
				String SQL = "SELECT SUBSTR ( CAST ( " + SurveyAnswersDao.Properties.SurveyAnswerID.columnName + " AS TEXT ) , 0 , 9 ) SurveyDate " +
						"FROM " + SurveyAnswersDao.TABLENAME + " " +
						"WHERE " + SurveyAnswersDao.Properties.SurveyType.columnName + " = ? " +
						"GROUP BY SurveyDate";
				// Compute the selection arguments
				String selectionArguments [] = new String [] {
						String.valueOf ( getIntent ().getIntExtra ( SURVEY_TYPE , -1 ) )
				};
				
				// Declare and initialize a cursor in order to query DB
				Cursor cursor = DatabaseUtils.getInstance ( SurveysActivity.this ).getDaoSession ().getDatabase ().rawQuery ( SQL , selectionArguments );
				
				// Declare and initialize a list of strings, used to store the various survey answers dates
				List < String > surveyDates = new ArrayList < String > ();
				// Move the cursor to the first raw
				if ( cursor.moveToFirst () ) {
					// Iterate over all raws
					do {
						// Retrieve and add the survey date
						surveyDates.add ( cursor.getString ( 0 ) );
					} while ( cursor.moveToNext () );
				} // End if
				// Close the cursor
				cursor.close ();
				cursor = null;
				
				// Compute the SQL string
				SQL = "SELECT SA." + SurveyAnswersDao.Properties.Id.columnName + " , SA." + SurveyAnswersDao.Properties.SurveyID.columnName + " , " +
						"SA." + SurveyAnswersDao.Properties.SurveyAnswerID.columnName + " , U." + UsersDao.Properties.UserName.columnName + " , " +
						"S." + SurveysDao.Properties.SurveyDescription.columnName + " " +
						"FROM ( " + SurveyAnswersDao.TABLENAME + " SA INNER JOIN " + SurveysDao.TABLENAME + " S " +
						"ON SA." + SurveyAnswersDao.Properties.SurveyID.columnName + " = S." + SurveysDao.Properties.SurveyID.columnName + " ) " +
						"INNER JOIN " + UsersDao.TABLENAME + " U ON SA." + SurveyAnswersDao.Properties.UserCode.columnName + " = U." + UsersDao.Properties.UserCode.columnName + " " +
						"WHERE SUBSTR ( CAST ( " + SurveyAnswersDao.Properties.SurveyAnswerID.columnName + " AS TEXT ) , 0 , 9 ) = ? " +
						"AND SA." + SurveyAnswersDao.Properties.SurveyType.columnName + " = ? " +
						"GROUP BY SA." + SurveyAnswersDao.Properties.SurveyID.columnName + " , SA." + SurveyAnswersDao.Properties.SurveyAnswerID.columnName + " " +
						"ORDER BY SA." + SurveyAnswersDao.Properties.SurveyAnswerID.columnName + " DESC";
				
				// Compute the selection arguments (it holds two arguments : the first 8 digits (YYYYMMDD) of the survey answer id) and the survey type
				selectionArguments = new String [ 2 ];
				selectionArguments [ 1 ] = String.valueOf ( getIntent ().getIntExtra ( SURVEY_TYPE , -1 ) );
				
				// Declare and initialize a map of cursors mapped to strings (their date representation)
				Map < String , Cursor > cursors = new LinkedHashMap < String , Cursor > ();
				// Iterate over all dates
				for ( String surveyDate : surveyDates ) {
					// Compute a calendar object out of the survey date (YYYYMMDD)
					Calendar date = Calendar.getInstance ();
					SimpleDateFormat dateFormat = new SimpleDateFormat ( "yyyyMMdd" );
					date.setTime ( dateFormat.parse ( surveyDate ) );
					// Set the survey date as selection argument
					selectionArguments [ 0 ] = surveyDate;
					// Map a new cursor to its date
					cursors.put ( DateTime.getFullDate ( SurveysActivity.this , date ) ,
							DatabaseUtils.getInstance ( SurveysActivity.this ).getDaoSession ().getDatabase ()
								.rawQuery ( SQL , selectionArguments ) );
				} // End for each
				
				// Iterate over all cursors
				for ( String date : cursors.keySet () )
					// Execute internal computations
					cursors.get ( date ).getCount ();
				
				// Return the cursors map
				return cursors;
			} catch ( Exception exception ) {
				// An error occurred
				return null;
			} // End of try-catch block
		}
	
		/*
		 * Runs on the UI thread after doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute ( Map < String , Cursor > cursors ) {
			// Check if the result is valid
			if ( cursors == null )
				// Invalid result
				return;
			// Declare and initialize a multiple list adapter
			MultipleListAdapter adapter = new MultipleListAdapter ( SurveysActivity.this );
			// Iterate over all cursors
			for ( String date : cursors.keySet () )
				// Add a new sales order adapter using the current cursor
				adapter.addSection ( new Section ( date , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
						new SurveysCursorAdapter ( SurveysActivity.this , cursors.get ( date ) , R.layout.surveys_activity_item ) );
			// Set the list adapter
			setListAdapter ( adapter );
		}
		
	}
		
}