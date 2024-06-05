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

import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.reflect.TypeToken;

import me.SyncWise.Android.AppAnimation;
import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.AppToast;
import me.SyncWise.Android.DateTime;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Vibration;
import me.SyncWise.Android.Database.ClientPropertiesDao;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.IsProcessedUtils;
import me.SyncWise.Android.Database.JourneysUtils;
import me.SyncWise.Android.Database.QuestionAnswers;
import me.SyncWise.Android.Database.QuestionAnswersDao;
import me.SyncWise.Android.Database.StatusUtils;
import me.SyncWise.Android.Database.SubQuestionAnswers;
import me.SyncWise.Android.Database.SubQuestionAnswersDao;
import me.SyncWise.Android.Database.SurveyAnswers;
import me.SyncWise.Android.Database.SurveyAnswersDao;
import me.SyncWise.Android.Database.SurveyAnswersUtils;
import me.SyncWise.Android.Database.SurveyAssignmentsDao;
import me.SyncWise.Android.Database.SurveyAssignmentsUtils;
import me.SyncWise.Android.Database.SurveyImages;
import me.SyncWise.Android.Database.SurveyImagesDao;
import me.SyncWise.Android.Database.SurveyQuestions;
import me.SyncWise.Android.Database.SurveyQuestionsDao;
import me.SyncWise.Android.Database.SurveyQuestionsUtils;
import me.SyncWise.Android.Database.SurveysDao;
import me.SyncWise.Android.Database.SurveysUtils;
import me.SyncWise.Android.Database.UsersDao;
import me.SyncWise.Android.Database.Visits;
import me.SyncWise.Android.Database.VisitsDao;
import me.SyncWise.Android.Gson.BaseTimerActivity;
import me.SyncWise.Android.Modules.Call.CallAction;
import me.SyncWise.Android.Modules.Call.CallMenuActivity;
import me.SyncWise.Android.Modules.Journey.Call;
import me.SyncWise.Android.Utilities.ActivityInstance;
import me.SyncWise.Android.Utilities.ExternalStorageFilter;
import me.SyncWise.Android.Widgets.Baguette;
import me.SyncWise.Android.Widgets.CustomScrollView;
import me.SyncWise.Android.Widgets.CoverFlow.CoverFlow;
import me.SyncWise.Android.Widgets.CoverFlow.PathImageAdapter;
import me.SyncWise.Android.Widgets.CoverFlow.ReflectingImageAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RadioGroup.OnCheckedChangeListener;

/**
 * Activity implemented in order display survey questions, for a survey at a time, for multiple surveys.
 * 
 * @author Elias
 *
 */
@SuppressLint("UseSparseArrays")
public class SurveyQuestionsActivity extends BaseTimerActivity implements OnItemSelectedListener {
	
	/**
	 * Duration in milliseconds of the questions' expand and collapse animations.
	 */
	private static final int ANIMATION_DURATION = 500;
	
	/**
	 * Duration in milliseconds of the post delay, used after the end of animations.
	 */
	private static final int POST_DELAY = 100;
	
	/**
	 * Bundle key used to put/retrieve the content of the current call.
	 */
	public static final String CALL = SurveyQuestionsActivity.class.getName () + ".CALL";
	
	/**
	 * Bundle key used to put/retrieve the content of the current visit.
	 */
	public static final String VISIT = SurveyQuestionsActivity.class.getName () + ".VISIT";
	
	/**
	 * Bundle key used to put/retrieve the activity title.
	 */
	public static final String ACTIVITY_TITLE = SurveyQuestionsActivity.class.getName () + ".ACTIVITY_TITLE";
	
	/**
	 * Bundle key used to put/retrieve the survey type.
	 */
	public static final String SURVEY_TYPE = SurveyQuestionsActivity.class.getName () + ".SURVEY_TYPE";
	
	/**
	 * Bundle key used to put/retrieve the loading flag of the last client surveys answers for the current day.
	 */
	public static final String LOAD_LAST_CLIENT_SURVEY_ANSWER_ID_OF_CURRENT_JOURNEY = SurveyQuestionsActivity.class.getName () + ".LOAD_LAST_CLIENT_SURVEY_ANSWER_ID_OF_CURRENT_JOURNEY";
	
	/**
	 * Bundle key used to put/retrieve the survey ID.
	 */
	public static final String SURVEY_ID = SurveyQuestionsActivity.class.getName () + ".SURVEY_ID";
	
	/**
	 * Bundle key used to put/retrieve the survey answer ID.
	 */
	public static final String SURVEY_ANSWER_ID = SurveyQuestionsActivity.class.getName () + ".SURVEY_ANSWER_ID";
	
	/**
	 * Bundle key used to put/retrieve the is view flag.
	 */
	public static final String IS_VIEW = SurveyQuestionsActivity.class.getName () + ".IS_VIEW";
	
	/**
	 * Bundle key used to put/retrieve the result of saving a survey.
	 */
	public static final String SURVEY_SUCCESSFULLY_SAVED = SurveyQuestionsActivity.class.getName () + ".SURVEY_SUCCESSFULLY_SAVED";
	
	/**
	 * {@link android.graphics.drawable.Drawable Drawable} hosting a bit map used for questions that are optional and not answered.
	 */
	private Drawable questionIcon;
	
	/**
	 * {@link android.graphics.drawable.Drawable Drawable} hosting a bit map used for questions that are forced and not answered.
	 */
	private Drawable questionForcedIcon;
	
	/**
	 * {@link android.graphics.drawable.Drawable Drawable} hosting a bit map used for questions that are answered.
	 */
	private Drawable questionAnsweredIcon;
	
	/**
	 * Reference to the spinner used to display the surveys.
	 */
	private Spinner spinner;
	
	/**
	 * Reference to the scroll view hosting the survey questions layout.
	 */
	private CustomScrollView scrollView;
	
	/**
	 * Reference to the linear layout hosting the survey questions.
	 */
	private LinearLayout surveyQuestionsLayout;
	
	/**
	 * {@link android.view.View.OnClickListener OnClickListener} used to catch any callback that should do nothing.
	 */
	private OnClickListener emptyClickListener;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #selectedSurvey}.
	 */
	private static final String SELECTED_SURVEY = SurveyQuestionsActivity.class.getName () + ".SELECTED_SURVEY";
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #selectedQuestion}.
	 */
	private static final String SELECTED_QUESTION = SurveyQuestionsActivity.class.getName () + ".SELECTED_QUESTION";
	
	/**
	 * Integer used to host the selected survey.
	 */
	private Integer selectedSurvey;
	
	/**
	 * Integer used to host the selected question.
	 */
	private Integer selectedQuestion;
	
	/**
	 * Flag used to indicate if the survey and question selections should be restored.
	 */
	private boolean restoreSelection;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #retrieveSurveys}.
	 */
	private static final String RETRIEVE_SURVEYS = SurveyQuestionsActivity.class.getName () + ".RETRIEVE_SURVEYS";
	
	/**
	 * Boolean used to indicate if there surveys that should be retrieved.
	 */
	private boolean retrieveSurveys;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #displayedQuestionCoverFlow}.
	 */
	private static final String DISPLAYED_QUESTION_COVER_FLOW = SurveyQuestionsActivity.class.getName () + ".DISPLAY_COVER_FLOW";
	
	/**
	 * Question object used to indicate if the survey images are displayed.
	 */
	private Questions displayedQuestionCoverFlow;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #surveys}.
	 */
	private static final String SURVEYS = SurveyQuestionsActivity.class.getName () + ".SURVEYS";
	
	/**
	 * List used to host the {@link me.SyncWise.Android.Modules.Survey.Surveys Surveys} objects.
	 */
	private ArrayList < Surveys > surveys;
	
	/**
	 * Nested class implementation of the view holder pattern used to keep references to children views in order to avoid unnecessary calls to {@link android.view.View#findViewById(int) findViewById(id)} on each row.
	 * 
	 * @author Elias
	 *
	 */
	public static class ViewHolder {
		public Questions question;
		public LinearLayout questionLayout;
		public ImageView questionIcon;
		public TextView questionDescription;
		public ImageButton questionImages;
		public LinearLayout questionAnswer;
		public LinearLayout subQuestion;
		public View separator;
	}
	
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
		setContentView ( R.layout.survey_questions_activity );
		// Check if the activity title is defined in the calling intent
		String activityTitle = getIntent ().getStringExtra ( ACTIVITY_TITLE );
		// Change the title associated with this activity
		setTitle ( activityTitle == null ? AppResources.getInstance ( this ).getString ( this , R.string.survey_questions_activity_title ) : activityTitle );
		
		// Display empty list label
		( (TextView) findViewById ( R.id.empty_list_view ) ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.empty_questions_list_label ) );
		// Initialize question icons
		questionIcon = getResources ().getDrawable ( R.drawable.question_balloon_blue );
		questionForcedIcon = getResources ().getDrawable ( R.drawable.question_balloon_red );
		questionAnsweredIcon = getResources ().getDrawable ( R.drawable.ballon_check_green );
		// Initialize the empty click listener
		emptyClickListener = new OnClickListener() {
			@Override
			public void onClick ( View view ) {
				// Do nothing
			}
		};
		// Retrieve a reference to the surveys spinner
		spinner = (Spinner) findViewById ( R.id.spinner );
		// Register a callback to be invoked when an item in this AdapterView has been selected
		spinner.setOnItemSelectedListener ( this );
		// Retrieve a reference to the survey questions layout
		surveyQuestionsLayout = (LinearLayout) findViewById ( R.id.layout_questions );
		// Retrieve a reference to the scroll view layout
		scrollView = (CustomScrollView) surveyQuestionsLayout.getParent ();
		
		// Determine if a saved instance state is provided
		if ( savedInstanceState != null ) {
			// Restore the content provided by the saved instance state
			restoreSelection = true;
			selectedSurvey = savedInstanceState.getInt ( SELECTED_SURVEY );
			selectedQuestion = savedInstanceState.getInt ( SELECTED_QUESTION , -1 );
			selectedQuestion = selectedQuestion == -1 ? null : selectedQuestion;
			retrieveSurveys = savedInstanceState.getBoolean ( RETRIEVE_SURVEYS );
			displayedQuestionCoverFlow = (Questions) savedInstanceState.getSerializable ( DISPLAYED_QUESTION_COVER_FLOW );
		} // End if
		
		// Perform survey type check
		if ( ! checkSurveyType () )
			// Exit method
			return;
		
		// Check if the list of surveys is valid
		if ( surveys == null )
			// Retrieve the list of surveys asynchronously
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
	 * Called when the activity has detected the user's press of the back key. 
	 *
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed () {
    	// Check if the displayed question in the cover flow is valid
    	if ( displayedQuestionCoverFlow != null ) {
			// Clear displayed question reference
    		displayedQuestionCoverFlow = null;
			// Refresh the action bar
			invalidateOptionsMenu ();
			// Retrieve a reference to the cover flow
			CoverFlow coverFlow = (CoverFlow) findViewById ( R.id.coverflowReflect );
			// Check if the cover flow adapter is valid
			if ( coverFlow.getAdapter () != null ) {
				// Declare a PathImageAdapter object
				BaseAdapter mainAdapter = null;
				PathImageAdapter adapter = null;
				// Determine if the adapter is an instance of PathImageAdapter
				if ( coverFlow.getAdapter () instanceof PathImageAdapter ) {
					// Initialize the adapter
					adapter = (PathImageAdapter) coverFlow.getAdapter ();
					mainAdapter = adapter;
				} // End if
				// Determine if the adapter is an instance of ReflectingImageAdapter
				// AND if its linked adapter is an instance of PathImageAdapter
				else if ( coverFlow.getAdapter () instanceof ReflectingImageAdapter
						&& ( (ReflectingImageAdapter) coverFlow.getAdapter () ).getLinkedAdapter () instanceof PathImageAdapter ) {
					// Initialize the adapter
					mainAdapter = (BaseAdapter) coverFlow.getAdapter ();
					adapter = (PathImageAdapter) ( (ReflectingImageAdapter) mainAdapter ).getLinkedAdapter ();
				} // End else if
				// Check if the adapter is valid
				if ( adapter != null ) {
			        // Clear the cover flow adapter
					adapter.clear ();
					mainAdapter.notifyDataSetChanged ();
				} // End if
			} // End if
			// Hide the cover flow view
			coverFlow.setVisibility ( View.GONE );
			// Exit method
			return;
		} // End if
		// Flag used to determine if the surveys are empty
		boolean isEmpty = true;
		// Iterate over all surveys
		for ( Surveys survey : surveys ) {
			if ( survey.getQuestions () != null && ! survey.getQuestions ().isEmpty () ) {
				// There is at least one non empty survey
				// Set the flag
				isEmpty = false;
				// Exit loop
				break;
			} // End if
		} // End for each
		// Check if the surveys are empty
		if ( isEmpty ) {
			// There are no surveys
			// Superclass method call
			super.onBackPressed ();
			// Exit method
			return;
		} // End if
		// Flag used to determine if the user did not modify any survey
		boolean canGoBack = true;
		// Iterate over all the surveys
		for ( Surveys survey : surveys )
			// Check if the survey has at least one modified question
			if ( survey.isModified () ) {
				// Reset flag
				canGoBack = false;
				// Exit loop
				break;
			} // End if
		// Check if the activity can be closed
		if ( canGoBack == false ) {
			// Display confirmation dialog
			AppDialog.getInstance ().displayAlert ( this ,
					null ,
					AppResources.getInstance ( this ).getString ( this , R.string.discard_changes_confirmation_message ) ,
					AppDialog.ButtonsType.YesNo ,
					new DialogInterface.OnClickListener () {
						@Override
						public void onClick ( DialogInterface dialog , int which ) {
							// Determine the clicked button
							switch ( which ) {
							case DialogInterface.BUTTON_POSITIVE:
								// Finish activity
								finish ();
								break;
							case DialogInterface.BUTTON_NEGATIVE:
								// Dismiss dialog
								AppDialog.getInstance ().dismiss ();
								break;
							} // End switch
						}
					} );
			// Exit method
			return;
		} // End if
		// Superclass method call
		super.onBackPressed ();
		// Close activity
		this.finish ();
	} // End of onBackPressed
	
    /*
     * Called to retrieve per-instance state from an activity before being killed so that the state can be restored in onCreate(Bundle) or onRestoreInstanceState(Bundle).
     *
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    protected void onSaveInstanceState ( Bundle outState ) {
    	// Superclass method call
    	super.onSaveInstanceState ( outState );
    	// Save the content of selectedSurvey in the outState bundle
    	if ( selectedSurvey != null )
    		outState.putInt ( SELECTED_SURVEY , selectedSurvey );
    	// Save the content of selectedQuestion in the outState bundle
    	if ( selectedQuestion != null )
    		outState.putInt ( SELECTED_QUESTION , selectedQuestion );
    	
    	// Check if the surveys list is valid
    	if ( surveys != null ) {
			// Save the content of the surveys using GSON
			ActivityInstance.saveDataGson ( this , SurveyQuestionsActivity.class.getName () , SURVEYS , surveys );
			// Indicate that there is saved orders data
			outState.putBoolean ( RETRIEVE_SURVEYS , true );
    	} // End if
    	// Check if the displayed question in the cover flow is valid
    	if ( displayedQuestionCoverFlow != null )
			// Save the content of the displayedQuestionCoverFlow using GSON
			outState.putSerializable ( DISPLAYED_QUESTION_COVER_FLOW , displayedQuestionCoverFlow );
    }
	
	/*
	 * Called when you are no longer visible to the user.
	 *
	 * @see android.app.Activity#onStop()
	 */
	@Override
    public void onStop () {
    	// Superclass method call
		super.onStop ();
		// Determine if the activity is finishing
		if ( isFinishing () ) {
			questionIcon = null;
			questionForcedIcon = null;
			questionAnsweredIcon = null;
			if ( spinner != null )
				spinner.setOnItemSelectedListener ( null );
			spinner = null;
			if ( surveyQuestionsLayout != null )
				surveyQuestionsLayout.removeAllViews ();
			scrollView = null;
			surveyQuestionsLayout = null;
			surveys = null;
			selectedQuestion = null;
			selectedSurvey = null;
			emptyClickListener = null;
		} // End if
	}
    
	/**
	 * Performs a validity check to determine if the survey type is provided in the calling intent and is valid.
	 * Otherwise the current activity is finished and a warning toast is displayed.
	 * 
	 * @return	Boolean indicating if the checking succeeded or failed.
	 */
	private boolean checkSurveyType () {
		// Flag to check if the survey type is valid
		boolean valid = false;
		// Determine if the survey type is included in the calling intent
		Integer surveyType = (Integer) getIntent ().getSerializableExtra ( SURVEY_TYPE );
		// Check if the survey type is valid
		if ( surveyType != null )
			// Set flag
			valid = true;
		
		// Skip next step if the survey type is not valid
		if ( valid )
			try {
				// Reset flag
				valid = false;
				// Retrieve all the possible types fields
				Field surveyTypes [] = SurveysUtils.Type.class.getFields ();
				// Iterate over all the fields
				for ( Field field : surveyTypes )
					// Check if the current field match the provided survey type
					if ( field.getInt ( null ) == surveyType ) {
						// Set flag
						valid = true;
						// Exit loop
						break;
					} // End if
			} catch ( Exception exception ) {
				// Invalid survey type
				valid = false;
			} // End of try-catch block
		
		// Check if the survey type is valid
		if ( ! valid ) {
			// Invalid survey type
			// Indicate that the activity cannot be displayed
			new AppToast ( this ).setIcon ( R.drawable.warning ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.invalid_selection_label ) ).show ();
			// Finish activity
			finish ();
			// Indicate that the checking failed
			return false;
		} // End if
		// Otherwise the checking succeeded
		return true;
	}

	/*
	 * Callback method to be invoked when an item in this view has been selected.
	 *
	 * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemSelected ( AdapterView < ? > parent , View view , int position , long id ) {
		// Check if the question selection should be restored
		if ( ! restoreSelection ) {
			// Perform a question validity check
			if ( ! checkQuestionValidity () ) {
				// Reset selection
				parent.setSelection ( selectedSurvey );
				// Invalid question
				return;
			} // End if
			// Reset position
			selectedQuestion = null;
		} // End if
		// Check if the previously selected survey is valid AND has a valid questions list
		if ( selectedSurvey != null && surveys.get ( selectedSurvey ).getQuestions () != null )
			// Clear all previous questions' sub-questions IDs
			// Iterate over all the previous survey questions
			for ( Questions question : surveys.get ( selectedSurvey ).getQuestions () )
				// Clear the sub-question ID
				question.setCurrentSubQuestionID ( null );
		// Set the new position
		selectedSurvey = position;
		// Clear the previously displayed questions
		surveyQuestionsLayout.removeAllViews ();
		// Display / hide the empty view accordingly
		findViewById ( R.id.empty_list_view ).setVisibility ( surveys.get ( position ).getCount () == 0 ? View.VISIBLE : View.GONE );
		// Iterate over all the questions of the current survey
		for ( int i = 0 ; i < surveys.get ( position ).getCount () ; i ++ ) {
			// Retrieve a reference to the question index
			final int index = i;
			// Retrieve a reference to the selected question
			Questions question = surveys.get ( selectedSurvey ).getQuestion ( i );
			// Get the question's view
			View questionView = getView ( question , i );
			// Retrieve a reference to the question's view holder
			ViewHolder viewHolder = (ViewHolder) questionView.getTag ();
			// Ignore any click callback for the question answer layout
			viewHolder.questionAnswer.setOnClickListener ( emptyClickListener );
			// Ignore any click callback for the sub-question layout
			viewHolder.subQuestion.setOnClickListener ( emptyClickListener );
			// Register a callback to be invoked when this question view is clicked
			questionView.setOnClickListener ( new OnClickListener () {
				@Override
				public void onClick ( View view ) {
		    		// Hide the software keyboard
		            ( (InputMethodManager) getBaseContext ().getSystemService ( INPUT_METHOD_SERVICE ) ).hideSoftInputFromWindow ( view.getWindowToken (), 0 );
		            // Retrieve a reference to the questions view's view holder
		            ViewHolder viewHolder = (ViewHolder) view.getTag ();
		            // Declare and initialize an integer used to compute any delay
		            int delay = 0;
		            
		            // Determine if the previous and current selected questions are the same
		            if ( selectedQuestion != null && selectedQuestion == index ) {
		            	// Check the question answer layout visibility state
		            	if ( viewHolder.questionAnswer.getVisibility () == View.GONE ) {
		            		// Disable scroll view
		            		scrollView.setScrollViewEnabled ( false );
		            		// Expand the question answer layout
		    				AppAnimation.expand ( viewHolder.questionAnswer , LayoutParams.MATCH_PARENT , LayoutParams.WRAP_CONTENT , ANIMATION_DURATION , AppAnimation.Direction.VERTICAL );
		    				// Display the sub-question if applicable and compute its delay
		    				delay = displaySubQuestion ( index );
		    				delay = delay - ANIMATION_DURATION > 0 ? delay - ANIMATION_DURATION : 0;
		    				// Enable scroll view at the end of the animation
		    				scrollView.postDelayed ( new Runnable () {
								@Override
								public void run () {
				    				// Enable scroll view
				            		scrollView.setScrollViewEnabled ( true );
								}
							} , ANIMATION_DURATION + delay + POST_DELAY );
		            	} // End if
		            	else {
		            		// Perform a question validity check
		            		if ( ! checkQuestionValidity () )
		            			// Invalid question
		            			return;
		            		// Disable scroll view
		            		scrollView.setScrollViewEnabled ( false );
		            		// Collapse the question answer layout
		    				AppAnimation.collapse ( viewHolder.questionAnswer , LayoutParams.MATCH_PARENT , LayoutParams.WRAP_CONTENT , ANIMATION_DURATION , AppAnimation.Direction.VERTICAL , null );
		    				// Check if the sub-question layout is visible
		    				if ( viewHolder.subQuestion.getVisibility () == View.VISIBLE )
			            		// Collapse the sub-question layout
			    				AppAnimation.collapse ( viewHolder.subQuestion , LayoutParams.MATCH_PARENT , LayoutParams.WRAP_CONTENT , ANIMATION_DURATION , AppAnimation.Direction.VERTICAL , null );
		    				// Enable scroll view at the end of the animation
		    				scrollView.postDelayed ( new Runnable () {
								@Override
								public void run () {
				    				// Enable scroll view
				            		scrollView.setScrollViewEnabled ( true );
								}
							} , ANIMATION_DURATION + POST_DELAY );
		    				// Clear the view holder
		    				viewHolder = null;
		            	} // End else
		            } // End if
		            else {
		            	// Check if the previous question position is valid
		            	if ( selectedQuestion != null ) {
		            		// Perform a question validity check
		            		if ( ! checkQuestionValidity () )
		            			// Invalid question
		            			return;
		            		// Hide the previous question
		            		hideQuestion ( selectedQuestion );
		            	} // End if
		            	// Set the new selected question index
		            	selectedQuestion = index;
	            		// Disable scroll view
	            		scrollView.setScrollViewEnabled ( false );
	            		// Expand the question answer layout
	    				AppAnimation.expand ( viewHolder.questionAnswer , LayoutParams.MATCH_PARENT , LayoutParams.WRAP_CONTENT , ANIMATION_DURATION , AppAnimation.Direction.VERTICAL );
	    				// Display the sub-question if applicable and compute its delay
	    				delay = displaySubQuestion ( index );
	    				delay = delay - ANIMATION_DURATION > 0 ? delay - ANIMATION_DURATION : 0;
	    				// Enable scroll view at the end of the animation
	    				scrollView.postDelayed ( new Runnable () {
							@Override
							public void run () {
			    				// Enable scroll view
			            		scrollView.setScrollViewEnabled ( true );
							}
						} , ANIMATION_DURATION + delay + POST_DELAY );
		            } // End else
		            
					// Check if the view holder is valid
					if ( viewHolder != null )
						// Make sure the question is properly displayed
						properlyDisplayQuestion ( viewHolder , ANIMATION_DURATION + delay );
				}
			} );
			// Add the current question view to the survey questions layout
			surveyQuestionsLayout.addView ( questionView );
		} // End for loop
		// Determine if survey question should be selected
		if ( selectedQuestion != null )
			// Select the appropriate question by performing a click
			surveyQuestionsLayout.getChildAt ( selectedQuestion ).performClick ();
		// Check if the question selection should be restored
		if ( restoreSelection ) {
			// Reset flag
			restoreSelection = false;
			// Perform a question validity check
			checkQuestionValidity ();
		} // End if
	}

	/*
	 * Callback method to be invoked when the selection disappears from this view.
	 *
	 * @see android.widget.AdapterView.OnItemSelectedListener#onNothingSelected(android.widget.AdapterView)
	 */
	@Override
	public void onNothingSelected ( AdapterView < ? > parent ) {
		// Do nothing
	}
	
	/**
	 * Performs a question validity check and displays the appropriate warning message accordingly.
	 * 
	 * @return	Boolean indicating whether the question is valid or not.
	 */
	private boolean checkQuestionValidity () {
		try {
			// Retrieve a reference to the current survey question
			Questions question = surveys.get ( selectedSurvey ).getQuestion ( selectedQuestion );
			// Check if the question (including its sub-question(s)) is valid
			if ( ! question.isCompletelyValid () ) {
				// Display the appropriate warning message as baguette
				Baguette.showText ( this , question.getRecursiveValidityWarningMessage ( this ) , Baguette.BackgroundColor.RED );
				// Indicate that the question is NOT valid
				return false;
			} // End if
			// Indicate that the question is valid
			return true;
		} catch ( Exception exception ) {
			// Indicate that the question is valid
			return true;
		} // End of try-catch block
	}
	
	/**
	 * Properly displays the question. If a part of the question is hidden, the scroll view is adjusted accordingly.
	 * 
	 * @param viewHolder	ViewHodler of the current question.
	 * @param delay	Delay in milliseconds before performing the visibility check. This should be used if there are any ongoing animations.
	 */
	private void properlyDisplayQuestion ( final ViewHolder viewHolder , final int delay ) {
		// Retrieve a reference to the view holder
		final ViewHolder viewHolderReference = viewHolder;
		// Perform a delayed check to determine if the question layout is visible on screen
		scrollView.postDelayed ( new Runnable () {
			@Override
			public void run () {
				// Declare and initialize rect objects used to define the scroll view and question layout bounds
				Rect scrollViewBounds = new Rect ();
				Rect questionLayoutBounds = new Rect ();
				// Define the question layout bounds
				viewHolderReference.questionLayout.getHitRect ( questionLayoutBounds );
				// Define the scroll view bounds
				scrollView.getDrawingRect ( scrollViewBounds );
				// Determine if the question layout is visible (within the scroll view bounds)
				if ( ! scrollViewBounds.contains ( questionLayoutBounds ) ) {
				    // The question layout is NOT within the visible window
					// Scroll the scroll view accordingly
					scrollView.post ( new Runnable () {
							@Override
							public void run () {
								// Scroll the scroll view to bring the question layout at the top
								scrollView.smoothScrollTo ( 0 , viewHolderReference.questionLayout.getTop () );
							} // End of run
						} );
				} // End if
			} // End of run
		} , delay + POST_DELAY );
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
    	if ( ! getIntent ().getBooleanExtra ( IS_VIEW , false ) && surveys != null && ! surveys.isEmpty () ) {
	    	// Enable the required menu items
	    	me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_save ) , R.string.save_label );
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
    	// Determine if the action save is selected
    	if ( menuItem.getItemId () == R.id.action_save ) {
    		// Check if the current survey question is valid
    		if ( checkQuestionValidity () ) {
        		// Check if the surveys can be saved
        		Integer messageResourceID = canSave ();
        		// Check if there is an error message to display
        		if ( messageResourceID != null )
	    			// Indicate that the surveys cannot be saved using baguette
	    			Baguette.showText ( this ,
	    					AppResources.getInstance ( this ).getString ( this , messageResourceID ) ,
	    					Baguette.BackgroundColor.RED );
        		else
		    		// Save surveys answers asynchronously
		    		new Save ().execute ();
    		} // End if
    		// Consume event
    		return true;
    	} // End if
    	// Allow normal menu processing to proceed
    	return false;
    }
	
	/**
	 * Determines whether the current list of surveys can be saved or not.<br>
	 * In order to save the surveys, all the forced surveys, along with the optional surveys having at least one question answered, should have their forced questions answered.
	 * 
	 * @return	Integer holding the string resource ID hosting the error message, or {@code NULL} if there are no errors (can be saved).
	 */
	public Integer canSave () {
		// Determine if the surveys list is valid
		if ( surveys == null || surveys.isEmpty () )
			// Indicate that the user cannot save
			return R.string.cannot_save_empty_surveys_message;
		
		// Determine if the all the surveys are empty
		boolean emptySurveys = true;
		// Iterate over all surveys
		for ( Surveys survey : surveys )
			// Check if the survey have at least one question
			if ( survey.getCount () > 0 ) {
				// At least one survey is not empty
				emptySurveys = false;
				// Exit loop
				break;
			} // End if
		// Check if all the surveys are empty
		if ( emptySurveys )
			// Indicate that the user cannot save
			return R.string.cannot_save_unanswered_questions_message;
		
		// Flag used to determine if the user did not modify any survey
		boolean isModified = false;
		// Iterate over all the surveys
		for ( Surveys survey : surveys )
			// Check if the survey has at least one modified question
			if ( survey.isModified () ) {
				// Reset flag
				isModified = true;
				// Exit loop
				break;
			} // End if
		// Check if at least one survey is modified
		if ( ! isModified )
			// Indicate that the user cannot save
			return R.string.cannot_save_unmodified_questions_message;
		
		// Iterate over all surveys
		for ( Surveys survey : surveys )
			// Before checking if the completion percentage of the current survey
			// Check if the survey is forced, otherwise if it is optional but is not empty
			if ( survey.isForced () || survey.getCompletionPercentage () != 0 )
				// Determine if all forced questions are answered
				if ( ! survey.isForcedCompleted () )
					// At least one of the surveys is not complete
					// Indicate that the user cannot save
					return R.string.cannot_save_unanswered_questions_message;
		
		// Otherwise the user can save
		return null;
	}
	
	/**
	 * Hides the question at the indicated position by hiding its answer and sub-question layouts if they are not hidden already.
	 * 
	 * @param position	Integer holding the question position in the survey's questions list.
	 */
	private void hideQuestion ( final int position ) {
		try {
			// Retrieve a reference to question's view holder at the indicated position
			ViewHolder viewHolder = (ViewHolder) surveyQuestionsLayout.getChildAt ( position ).getTag ();
			// Hide the question's answer layout (if needed)
			if ( viewHolder.questionAnswer.getVisibility () != View.GONE )
				AppAnimation.collapse ( viewHolder.questionAnswer , LayoutParams.MATCH_PARENT , LayoutParams.WRAP_CONTENT , ANIMATION_DURATION , AppAnimation.Direction.VERTICAL , null );
			// Hide the question's sub-question layout (if needed)
			if ( viewHolder.subQuestion.getVisibility () != View.GONE )
				AppAnimation.collapse ( viewHolder.subQuestion , LayoutParams.MATCH_PARENT , LayoutParams.WRAP_CONTENT , ANIMATION_DURATION , AppAnimation.Direction.VERTICAL , null );
		} catch ( Exception exception ) {
			// Invalid surveys / questions list and / or position
		} // End of try-catch block
	}
	
	/**
	 * Refreshes the selected question from the selected journey.<br>
	 * The survey question state is re-evaluated, along with its sub-question.
	 */
	public void refresh () {
		// Check if the survey list, and the selected survey and question indexes are valid
		if ( surveys == null || selectedSurvey == null || selectedQuestion == null )
			// Invalid index(es)
			return;
		
		// Retrieve a reference to the selected question
		Questions question = surveys.get ( selectedSurvey ).getQuestion ( selectedQuestion );
		// Retrieve a reference to the question's view holder
		ViewHolder viewHolder = (ViewHolder) surveyQuestionsLayout.getChildAt ( selectedQuestion ).getTag ();
		
		// Determine if the answer is answered AND the question's icon is not appropriate
		// OR if the answer is forced, not answered AND the question's icon is not appropriate
		// OR if the answer is optional, not answered AND the question's icon is not appropriate
		if ( ( question.isCompletelyAnswered () && ! viewHolder.questionIcon.getDrawable().equals ( questionAnsweredIcon ) )
				|| ( ! question.isCompletelyAnswered () && question.isCompletelyForced () && ! viewHolder.questionIcon.getDrawable().equals ( questionForcedIcon ) )
				|| ( ! question.isCompletelyAnswered () && ! question.isCompletelyForced () && ! viewHolder.questionIcon.getDrawable().equals ( questionIcon ) ) ) {
			// Display the appropriate question icon
			viewHolder.questionIcon.setImageDrawable ( question.isCompletelyAnswered () ? questionAnsweredIcon : question.isCompletelyForced () ? questionForcedIcon : questionIcon );
			// Refresh the survey spinner card
			( (SurveysAdapter) spinner.getAdapter () ).notifyDataSetChanged ();
		} // End if
		
		// Display / hide the question's sub-question(s) as needed and compute its delay
		displaySubQuestion ( selectedQuestion , viewHolder );
	}
	
	/**
	 * Displays or hides the indicated question's sub-question.<br>
	 * In contrast with {@link #displaySubQuestion(int)}, this method performs a visibility check.
	 * 
	 * @param position	Integer holding the question position in the survey's questions list.
	 * @param viewHolderVisibilityCheck	ViewHolder reference of the required question, used to perform a visibility check on the question.
	 * @return	Number of milliseconds needed to display / hide the sub question.
	 */
	private int displaySubQuestion ( final int position , final ViewHolder viewHolderVisibilityCheck ) {
		// Display / hide the question's sub-question(s) as needed and compute its delay
		int delay = displaySubQuestion ( position );
		// Check if the visibility check is required AND if the delay is valid (non zero)
		if ( viewHolderVisibilityCheck != null && delay > 0 ) {
    		// Disable scroll view
    		scrollView.setScrollViewEnabled ( false );
			// Make sure the question is properly displayed
			properlyDisplayQuestion ( viewHolderVisibilityCheck , delay );
			// Enable scroll view at the end of the animation
			scrollView.postDelayed ( new Runnable () {
				@Override
				public void run () {
    				// Enable scroll view
            		scrollView.setScrollViewEnabled ( true );
				}
			} , delay + POST_DELAY );
		} // End if
		// Return the delay
		return delay;
	}
	
	/**
	 * Displays or hides the indicated question's sub-question.
	 * 
	 * @param position	Integer holding the question position in the survey's questions list.
	 * @return	Number of milliseconds needed to display / hide the sub question.
	 */
	private int displaySubQuestion ( final int position ) {
		// Retrieve a reference to the indicated question
		Questions question = surveys.get ( selectedSurvey ).getQuestion ( position );
		// Retrieve a reference the question view's view holder
		ViewHolder viewHolder = (ViewHolder) surveyQuestionsLayout.getChildAt ( position ).getTag ();
		// Get the question's sub-question if applicable
		Questions subQuestion = question.getApplicableSubQuestion ();
		
		// Determine if the question has no sub-question
		if ( subQuestion == null ) {
			// Check if the sub-question layout is not hidden already
			if ( viewHolder.subQuestion.getVisibility () != View.GONE ) {
				// Collapse the sub-question layout
				AppAnimation.collapse ( viewHolder.subQuestion , LayoutParams.MATCH_PARENT , LayoutParams.WRAP_CONTENT , ANIMATION_DURATION , AppAnimation.Direction.VERTICAL , null );
				// Exit method
				return ANIMATION_DURATION;
			} // End if
			// Exit method
			return 0;
		} // End if
		
		// Otherwise the question has a valid sub-question
		// Determine if the sub-question is already displayed
		if ( question.getCurrentSubQuestionID () != null && question.getCurrentSubQuestionID () == subQuestion.getSurveyQuestion ().getQuestionID () ) {
			// The sub-question is already selected
			// Check if the sub-question layout is not visible already
			if ( viewHolder.subQuestion.getVisibility () != View.VISIBLE ) {
				// Expand the sub-question layout
				AppAnimation.expand ( viewHolder.subQuestion , LayoutParams.MATCH_PARENT , LayoutParams.WRAP_CONTENT , ANIMATION_DURATION , AppAnimation.Direction.VERTICAL );
				// Exit method
				return ANIMATION_DURATION;
			} // End if
			// Exit method
			return 0;
		} // End if
		
		// Otherwise, the sub-question is new
		// Remove the previous sub-question (if any)
		viewHolder.subQuestion.removeAllViews ();
		
		// Inflate the appropriate sub-question view
		View view = null;
		switch ( subQuestion.getSurveyQuestion ().getQuestionTypeID () ) {
		case SurveyQuestionsUtils.Type.FREE_TEXT:
			view = getFreeTextView ( subQuestion , position );
			break;
		case SurveyQuestionsUtils.Type.NUMBER:
			view = getNumericView ( subQuestion , position );
			break;
		case SurveyQuestionsUtils.Type.OPTION:
			// Determine if the sub-question has multiple answers or not
			view = subQuestion.getSurveyQuestion ().getIsMultipleAnswer () == SurveyQuestionsUtils.isMultipleAnswers () ?
					getMultipleOptionView ( subQuestion , position ) : getSingleOptionView ( subQuestion , position );
			break;
		case SurveyQuestionsUtils.Type.DATE:
			view = getDateView ( subQuestion , position );
			break;
		case SurveyQuestionsUtils.Type.SELECTION_ITEMS:
			// TODO
			break;
		} // End switch
		
		// Check if the sub-question view is valid
		if ( view != null ) {
			// Update the current sub-question ID
			question.setCurrentSubQuestionID ( subQuestion.getSurveyQuestion ().getQuestionID () );
			// Retrieve a reference to the sub-question description label
			TextView subQuestionLabel = (TextView) view.findViewById ( R.id.label_subquestion_description );
			// Display the sub-question description
			subQuestionLabel.setVisibility ( View.VISIBLE );
			// Display the sub question description label
			subQuestionLabel.setText ( ( position + 1 ) + ".1\t" + subQuestion.getSurveyQuestion ().getQuestionDescription () );
			// Add the sub-question view to the current survey question layout
			viewHolder.subQuestion.addView ( view , new ViewGroup.LayoutParams ( ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.WRAP_CONTENT ) );
			// Check if the sub-question layout is already visible
			if ( viewHolder.subQuestion.getVisibility () != View.VISIBLE ) {
				// Expand the sub-question layout
				AppAnimation.expand ( viewHolder.subQuestion , LayoutParams.MATCH_PARENT , LayoutParams.WRAP_CONTENT , ANIMATION_DURATION , AppAnimation.Direction.VERTICAL );
				return ANIMATION_DURATION;
			} // End if
		} // End if
		else {
			// Update the current sub-question ID
			question.setCurrentSubQuestionID ( null );
			// Check if the sub-question layout is already hidden
			if ( viewHolder.subQuestion.getVisibility () != View.GONE ) {
				// Collapse the sub-question layout
				AppAnimation.collapse ( viewHolder.subQuestion , LayoutParams.MATCH_PARENT , LayoutParams.WRAP_CONTENT , ANIMATION_DURATION , AppAnimation.Direction.VERTICAL , null );
				return ANIMATION_DURATION;
			} // End if
		} // End else
		return 0;
	}
	
	/**
	 * Get a View that displays the data at the specified position in the survey's questions list.
	 * 
	 * @param question	Question object to display.
	 * @param position	Integer holding the question position in the survey's questions list.
	 * @return	A View corresponding to the question at the specified position.
	 */
	private View getView ( final Questions question , final int position ) {
		// A new view must be inflated
		View view = getLayoutInflater ().inflate ( R.layout.survey_questions_activity_item , null );
		// Declare and initialize a view holder
		ViewHolder viewHolder = new ViewHolder ();
		// Set the question reference
		viewHolder.question = question;
		// Retrieve a reference to the question layout
		viewHolder.questionLayout = (LinearLayout) view.findViewById ( R.id.layout_question );
		// Retrieve a reference to the question icon
		viewHolder.questionIcon = (ImageView) view.findViewById ( R.id.icon_question );
		// Retrieve a reference to the question description label
		viewHolder.questionDescription = (TextView) view.findViewById ( R.id.label_question_description );
		// Retrieve a reference to the question images button
		viewHolder.questionImages = (ImageButton) view.findViewById ( R.id.button_view_picture );
		// Retrieve a reference to the question's answer layout
		viewHolder.questionAnswer = (LinearLayout) view.findViewById ( R.id.layout_answer );
		// Retrieve a reference to the question's sub-question layout
		viewHolder.subQuestion = (LinearLayout) view.findViewById ( R.id.layout_subquestion );		
		// Retrieve a reference to the separator view
		viewHolder.separator = view.findViewById ( R.id.separator );
		// Store the view holder as tag
		view.setTag ( viewHolder );
		
		// Display the question description label
		viewHolder.questionDescription.setText ( String.valueOf ( position + 1 ) + ". " + question.getSurveyQuestion ().getQuestionDescription () );
		// Display / hide the view pictures button accordingly
		viewHolder.questionImages.setVisibility ( question.getSurveyImages ().isEmpty () ? View.GONE : View.VISIBLE );
		// Display the appropriate question icon
		viewHolder.questionIcon.setImageDrawable ( question.isCompletelyAnswered () ? questionAnsweredIcon : question.isCompletelyForced () ? questionForcedIcon : questionIcon );
		// Determine if the current question is the last survey question
		if ( surveys.get ( selectedSurvey ).isLastQuestion ( position ) )
			// Hide the separator view
			viewHolder.separator.setVisibility ( View.GONE );
		
		// Inflate the appropriate question answer view
		View answer = null;
		switch ( question.getSurveyQuestion ().getQuestionTypeID () ) {
		case SurveyQuestionsUtils.Type.FREE_TEXT:
			answer = getFreeTextView ( question , position );
			break;
		case SurveyQuestionsUtils.Type.NUMBER:
			answer = getNumericView ( question , position );
			break;
		case SurveyQuestionsUtils.Type.OPTION:
			// Determine if the question has multiple answers or not
			answer = question.getSurveyQuestion ().getIsMultipleAnswer () == SurveyQuestionsUtils.isMultipleAnswers () ?
					getMultipleOptionView ( question , position ) : getSingleOptionView ( question , position );
			break;
		case SurveyQuestionsUtils.Type.DATE:
			answer = getDateView ( question , position );
			break;
		case SurveyQuestionsUtils.Type.SELECTION_ITEMS:
			// TODO
			break;
		} // End switch
		
		// Check if the answer view is valid
		if ( answer != null )
			// Add the answer view to the question
			viewHolder.questionAnswer.addView ( answer , new ViewGroup.LayoutParams ( ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.WRAP_CONTENT ) );
		
		// Return the view
		return view;
	}
	
	/**
	 * Get a View that displays the <b>FREE TEXT</b> question at the specified position in the survey's questions list.
	 * 
	 * @param question	Question object to display.
	 * @param position	Integer holding the question position in the survey's questions list.
	 * @return	A View corresponding to the question at the specified position.
	 */
	private View getFreeTextView ( final Questions question , final int position ) {
		// A new view must be inflated
		View view = getLayoutInflater ().inflate ( R.layout.survey_questions_free_text , null );
		// Retrieve a reference to the edit text holding the free text answer
		EditText editText = (EditText) view.findViewById ( R.id.edittext_free_text_answer );
		// Set the maximum number of allowed characters
		editText.setFilters ( new InputFilter [] { new InputFilter.LengthFilter ( SurveyAnswersUtils.getAnswerMaxLength () ) } );
		// Set the edit text hint
		editText.setHint ( AppResources.getInstance ( this ).getString ( this , R.string.survey_question_free_text_hint ) );
		// Display the answer label
		editText.setText ( question.getAnswer () == null ? "" : question.getAnswer ().getAnswer () == null ? "" : question.getAnswer ().getAnswer ().toString () );
		// Disable the edit text if the is view flag is set
		editText.setEnabled ( ! getIntent ().getBooleanExtra ( IS_VIEW , false ) );
		// Add a TextWatcher in order to save the new answer whenever text changes
		editText.addTextChangedListener ( new TextWatcher () {
			
			/*
			 * Method called to notify that the text has been changed.
			 * 
			 * @see android.text.TextWatcher#onTextChanged(CharSequence, int, int, int)
			 */
			@Override
			public void onTextChanged ( CharSequence s , int start , int before , int count ) {
				// Trim the answer
				String answer = s.toString ().trim ();
				// Check if the answer is empty
				if ( answer.isEmpty () )
					// Clear the answer
					question.setAnswer ( null );
				// The answer is NOT empty
				else {
					// Check if the question has an answer
					if ( question.getAnswer () == null )
						// Initialize the question's answer
						question.setAnswer ( new Answers () );
					// Save the free text answer
					question.getAnswer ().answer ( s.toString () );
				} // End else
				// Refresh survey
				refresh ();
			}
			
			/*
			 * Method called to notify after the text is changed.
			 * 
			 * @see android.text.TextWatcher#beforeTextChanged(CharSequence, int, int, int)
			 */
			@Override
			public void beforeTextChanged ( CharSequence s , int start , int count , int after ) {
				// Do nothing
			}
			
			/*
			 * Method called to notify before the text is changed.
			 * 
			 * @see android.text.TextWatcher#afterTextChanged(Editable)
			 */
			@Override
			public void afterTextChanged ( Editable s ) {
				// Do nothing
			}
		} );
		// Return the view
		return view;
	}
	
	/**
	 * Get a View that displays the <b>NUMERIC</b> question at the specified position in the survey's questions list.
	 * 
	 * @param question	Question object to display.
	 * @param position	Integer holding the question position in the survey's questions list.
	 * @return	A View corresponding to the question at the specified position.
	 */
	private View getNumericView ( final Questions question , final int position ) {
		// A new view must be inflated
		View view = getLayoutInflater ().inflate ( R.layout.survey_questions_numeric , null );
		// Display the increment button label
		( (Button) view.findViewById ( R.id.button_increment ) ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.plus_sign ) );
		// Display the decrement button label
		( (Button) view.findViewById ( R.id.button_decrement ) ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.minus_sign ) );
		// Retrieve a reference to the options label
		TextView optionsLabel = (TextView) view.findViewById ( R.id.label_options );
		// Compute the options label text
		String _optionsLabel = "";
		// Determine if the question has a minimum value
		if ( question.getSurveyQuestion ().getMinimumValue () != null )
			// Set the options label
			_optionsLabel = AppResources.getInstance ( this ).getString ( this , R.string.survey_question_numeric_minimum_value_label ) + " : " + question.getSurveyQuestion ().getMinimumValue ();
		// Determine if the question has a maximum value
		if ( question.getSurveyQuestion ().getMaximumValue () != null )
			// Set the options label
			_optionsLabel = ( _optionsLabel.isEmpty () ? "" : _optionsLabel + "\n" ) +
				AppResources.getInstance ( this ).getString ( this , R.string.survey_question_numeric_maximum_value_label ) + " : " + question.getSurveyQuestion ().getMaximumValue ();
		// Set the options label
		optionsLabel.setText ( _optionsLabel );
		// Retrieve a reference to the edit text
		final EditText numberField = (EditText) view.findViewById ( R.id.edit_text_number );
		// Button click listener for the plus button
		( (Button) view.findViewById ( R.id.button_increment ) ).setOnClickListener ( new OnClickListener () {
			public void onClick ( View view ) {
				try {
					// Retrieve the number
					int number = Integer.parseInt ( numberField.getText ().toString () );
					// Increment the number
					++ number;
					// Display the number
					numberField.setText ( String.valueOf ( number ) );
				} catch ( Exception exception ) {
					// The number has invalid format
					// Set number as 0 or the minimum value allowed (the largest)
					numberField.setText ( String.valueOf ( question.getSurveyQuestion ().getMinimumValue () == null || 0 < question.getSurveyQuestion ().getMinimumValue () ?
							0 : question.getSurveyQuestion ().getMinimumValue () ) );
				} // End of try-catch block
			} // End of onClick
		} );
		// Button click listener for the minus button
		( (Button) view.findViewById ( R.id.button_decrement ) ).setOnClickListener ( new OnClickListener () {
			public void onClick ( View view ) {
				try {
					// Retrieve the number
					int number = Integer.parseInt ( numberField.getText ().toString () );
					// Decrement the number
					-- number;
					// Display the number
					numberField.setText ( String.valueOf ( number ) );
				} catch ( Exception exception ) {
					// The number has invalid format
					// Set number as 0 or the minimum value allowed (the largest)
					numberField.setText ( String.valueOf ( question.getSurveyQuestion ().getMinimumValue () == null || 0 < question.getSurveyQuestion ().getMinimumValue () ?
							0 : question.getSurveyQuestion ().getMinimumValue () ) );
				} // End of try-catch block
			} // End of onClick
		} );
		// Check if the question is answered
		if ( question.isAnswered () )
			// Display the answer
			numberField.setText ( question.getAnswer ().getAnswer () );
		else
			// Display the default number
			numberField.setText ( "0" );
		// Assign a text watcher to the number field
		numberField.addTextChangedListener ( new TextWatcher () {
			public void onTextChanged ( CharSequence s , int start , int before , int count ) {
				// Nothing
			} // End of onTextChanged 
			
			public void beforeTextChanged ( CharSequence s , int start , int count , int after ) {
				// Nothing
			} // End of beforeTextChanged
			
			public void afterTextChanged ( Editable s ) {
				// Declare and initialize a string used to host the answer
				String _answer = null;
				try {
					// Sanitize the answer
					_answer = String.valueOf ( Integer.parseInt ( numberField.getText ().toString () ) );
				} catch ( Exception exception ) {
					// The number has invalid format
					// Use the string as is (do not sanitize)
					_answer = numberField.getText ().toString ();
				} finally {
					// Check if the question has an answer
					if ( question.getAnswer () == null )
						// Initialize the question's answer
						question.setAnswer ( new Answers () );
					// Save the free text answer
					question.getAnswer ().answer ( _answer );
					// Refresh survey
					refresh ();
				} // End of try-catch-finally block
			} // End of afterTextChanged
		} );
		// Button click listener for the clear button
		view.findViewById ( R.id.button_clear ).setOnClickListener ( new OnClickListener () {
			public void onClick ( View view ) {
	    		// Hide the software keyboard
	            ( (InputMethodManager) getBaseContext ().getSystemService ( INPUT_METHOD_SERVICE ) ).hideSoftInputFromWindow ( view.getWindowToken (), 0 );
	            // Reset the number
				// Set number as 0 or the minimum value allowed (the largest)
				numberField.setText ( String.valueOf ( question.getSurveyQuestion ().getMinimumValue () == null || 0 < question.getSurveyQuestion ().getMinimumValue () ?
						0 : question.getSurveyQuestion ().getMinimumValue () ) );
		        // Clear the answer after a delay
				numberField.postDelayed ( new Runnable () {
					@Override
					public void run () {
						// Clear the answer
				        question.setAnswer ( null );
						// Refresh survey
						refresh ();
					}
				} , POST_DELAY );
				// Refresh survey
				refresh ();
			} // End of onClick
		} );
		// Disable all widgets if the is view flag is set
		boolean isView = getIntent ().getBooleanExtra ( IS_VIEW , false );
		view.findViewById ( R.id.button_increment ).setEnabled ( ! isView );
		view.findViewById ( R.id.button_decrement ).setEnabled ( ! isView );
		numberField.setEnabled ( ! isView );
		view.findViewById ( R.id.button_clear ).setVisibility ( isView ? View.GONE : View.VISIBLE );
		// Return the view
		return view;
	}
	
	/**
	 * Get a View that displays the <b>SINGLE OPTION</b> question at the specified position in the survey's questions list.
	 * 
	 * @param question	Question object to display.
	 * @param position	Integer holding the question position in the survey's questions list.
	 * @return	A View corresponding to the question at the specified position.
	 */
	private View getSingleOptionView ( final Questions question , final int position ) {
		// A new view must be inflated
		View view = getLayoutInflater ().inflate ( R.layout.survey_questions_options , null );
		// Retrieve a reference to the radio group
		final RadioGroup radioGroup = (RadioGroup) view.findViewById ( R.id.layout_options );
		// Retrieve a reference to the options label
		TextView optionsLabel = (TextView) view.findViewById ( R.id.label_options );
		// Set the options label
		optionsLabel.setText ( AppResources.getInstance ( this ).getString ( this , R.string.survey_question_options_answers_label ) );
		// Retrieve the option IDs
		final Object optionIDs [] = question.getOptions ().keySet ().toArray ();
		// Iterate over the list of options
		for ( int i = 0 ; i < optionIDs.length ; i ++ ) {
			// Declare and initialize a radio button used to represent the option
			RadioButton option = new RadioButton ( this );
			// Set the text appearance
			option.setTextAppearance ( this , R.style.TextAppearance_Medium );
			// Display the option label on the button
			option.setText ( question.getOptions ().get ( optionIDs [ i ] ) );
			// Add the radio button the radio group
			radioGroup.addView ( option );
		} // End for loop
		// Button click listener for the clear button
		view.findViewById ( R.id.button_clear ).setOnClickListener ( new OnClickListener () {
			public void onClick ( View view ) {
				// Clear the group buttons
				radioGroup.clearCheck ();
				// Clear the answer
				question.setAnswer ( null );
				// Refresh survey
				refresh ();
			} // End of onClick
		} );
		// Determine if the question is answered (excluding its sub questions)
		if ( question.isAnswered () ) {
			// Apply the answer by selecting the appropriate button
			// Retrieve the answer ID
			int answerID = question.getAnswer ().getAnswerID ();
			// Look for the option index
			int optionIndex = 0;
			// Iterate over all option IDs
			for ( int i = 0 ; i < optionIDs.length ; i ++ )
				// Match the option IDs
				if ( (Integer) optionIDs [ i ] == answerID ) {
					// Update the option index
					optionIndex = i;
					// Exit loop
					break;
				} // End if
			// Check the button with the same index
			radioGroup.check ( radioGroup.getChildAt ( optionIndex ).getId () );
		} // End if
		// Assign a checked change listener to the group holding the buttons (used to determine if a button is pressed)
		radioGroup.setOnCheckedChangeListener ( new OnCheckedChangeListener () {
			public void onCheckedChanged ( RadioGroup group , int checkedId ) {
				// Check if the checked ID is valid
				if ( checkedId == -1 )
					// Invalid ID
					return;
	    		// Hide the software keyboard
	            ( (InputMethodManager) getBaseContext ().getSystemService ( INPUT_METHOD_SERVICE ) ).hideSoftInputFromWindow ( group.getWindowToken (), 0 );
				// Save the answer based on the checked id
				// Retrieve the child index
				int selectedChildIndex = group.indexOfChild ( group.findViewById ( group.getCheckedRadioButtonId () ) );
				// Check if the question has an answer
				if ( question.getAnswer () == null )
					// Initialize the question's answer
					question.setAnswer ( new Answers () );
				// Otherwise the question is previously answered
				else
					// Clear the previous question answer
					question.getAnswer ().clear ();
				// Save the option answer
				question.getAnswer ().answer ( (Integer) optionIDs [ selectedChildIndex ] , question.getOptions ().get ( optionIDs [ selectedChildIndex ] ) );
				// Refresh survey
				refresh ();
			} // End of onCheckedChanged
		} );
		// Disable all widgets if the is view flag is set
		boolean isView = getIntent ().getBooleanExtra ( IS_VIEW , false );
		view.findViewById ( R.id.button_clear ).setVisibility ( isView ? View.GONE : View.VISIBLE );
		for ( int i = 0 ; i < radioGroup.getChildCount () ; i ++ )
			radioGroup.getChildAt ( i ).setEnabled ( ! isView );
		// Return the view
		return view;
	}
	
	/**
	 * Get a View that displays the <b>MULTIPLE OPTIONS</b> question at the specified position in the survey's questions list.
	 * 
	 * @param question	Question object to display.
	 * @param position	Integer holding the question position in the survey's questions list.
	 * @return	A View corresponding to the question at the specified position.
	 */
	private View getMultipleOptionView ( final Questions question , final int position ) {
		// A new view must be inflated
		View view = getLayoutInflater ().inflate ( R.layout.survey_questions_multiple_options , null );
		// Retrieve a reference to the check box layout
		final LinearLayout checkBoxLayout = (LinearLayout) view.findViewById ( R.id.layout_options );
		// Retrieve a reference to the options label
		TextView optionsLabel = (TextView) view.findViewById ( R.id.label_options );
		// Set the options label
		optionsLabel.setText ( AppResources.getInstance ( this ).getString ( this , R.string.survey_question_multiple_options_answers_label ) );
		// Retrieve the option IDs
		final Object optionIDs [] = question.getOptions ().keySet ().toArray ();
		// Iterate over the list of options
		for ( int i = 0 ; i < optionIDs.length ; i ++ ) {
			// Declare and initialize a check box used to represent the option
			CheckBox option = new CheckBox ( this );
			// Set the text appearance
			option.setTextAppearance ( this , R.style.TextAppearance_Medium );
			// Display the option label on the check box
			option.setText ( question.getOptions ().get ( optionIDs [ i ] ) );
			// Add the check box the layout
			checkBoxLayout.addView ( option );
		} // End for loop
		// Button click listener for the clear button
		view.findViewById ( R.id.button_clear ).setOnClickListener ( new OnClickListener () {
			public void onClick ( View view ) {
				// Iterate over all the group buttons
				for ( int i = 0 ; i < checkBoxLayout.getChildCount () ; i ++ )
					// Clear the button checked state
					( (CheckBox) checkBoxLayout.getChildAt ( i ) ).setChecked ( false );
				// Clear the answer
				question.setAnswer ( null );
				// Refresh survey
				refresh ();
			} // End of onClick
		} );
		// Determine if the question is answered (excluding its sub questions)
		if ( question.isAnswered () ) {
			// Apply the answer by selecting the appropriate check box
			// Retrieve the answer size
			int size = question.getAnswer ().getCount ();
			// Retrieve the answer IDs
			List < Integer > answerIDs = new ArrayList < Integer > ();
			// Iterate over all answers
			for ( int i = 0 ; i < size ; i ++ )
				// Store the answer ID
				answerIDs.add ( question.getAnswer ().getAnswerID ( i ) );
			// Iterate over all option IDs
			for ( int i = 0 ; i < optionIDs.length ; i ++ )
				// Match the option IDs
				if ( answerIDs.contains ( (Integer) optionIDs [ i ] ) )
					// Check the appropriate check box
					( (CheckBox) checkBoxLayout.getChildAt ( i ) ).setChecked ( true );
		} // End if
		// Declare and initialize a click listener to be used for every check box
		OnClickListener buttonListener = new OnClickListener () {
			@Override
			public void onClick ( View view ) {
				// Check if the clicked view is valid (a check box)
				if ( ! ( view instanceof CheckBox ) )
					// Invalid view, do nothing
					return;
	    		// Hide the software keyboard
	            ( (InputMethodManager) getBaseContext ().getSystemService ( INPUT_METHOD_SERVICE ) ).hideSoftInputFromWindow ( view.getWindowToken (), 0 );
				// Otherwise the view is a valid check box
				// Compute the answer ID
				int answerID = (Integer) optionIDs [ checkBoxLayout.indexOfChild ( view ) ];
				// Determine if the view has been checked
				if ( ( (CheckBox) view ).isChecked () ) {
					// Check box is checked
					// Check if the question has an answer
					if ( question.getAnswer () == null )
						// Initialize the question's answer
						question.setAnswer ( new Answers () );
					// Save the specified answer
					question.getAnswer ().answer ( answerID , question.getOptions ().get ( answerID ) );
				} // End if
				else {
					// Check box is NOT checked
					// Check if the question has an answer
					if ( question.getAnswer () != null ) {
						// Remove the answer wit the specified answer ID
						question.getAnswer ().remove ( answerID );
						// Check if the answer still holds at least one answer
						if ( question.getAnswer ().getCount () == 0 )
							// Clear answer
							question.setAnswer ( null );
					} // End if
				} // End else
				// Refresh survey
				refresh ();
			}
		};
		// Disable all widgets if the is view flag is set
		boolean isView = getIntent ().getBooleanExtra ( IS_VIEW , false );
		view.findViewById ( R.id.button_clear ).setVisibility ( isView ? View.GONE : View.VISIBLE );
		// Assign the click listener to every check box in the group
		// Iterate over all check boxes
		for ( int i = 0 ; i < checkBoxLayout.getChildCount () ; i ++ ) {
			// Assign the click listener
			checkBoxLayout.getChildAt ( i ).setOnClickListener ( buttonListener );
			// Enable the check box accordingly
			checkBoxLayout.setEnabled ( ! isView );
		} // End for loop
		// Return the view
		return view;
	}
	
	/**
	 * Get a View that displays the <b>DATE</b> question at the specified position in the survey's questions list.
	 * 
	 * @param question	Question object to display.
	 * @param position	Integer holding the question position in the survey's questions list.
	 * @return	A View corresponding to the question at the specified position.
	 */
	@SuppressLint("SimpleDateFormat")
	private View getDateView ( final Questions question , final int position ) {
		// A new view must be inflated
		View view = getLayoutInflater ().inflate ( R.layout.survey_questions_date , null );
		// Retrieve a reference to the date label
		final TextView dateLabel = (TextView) view.findViewById ( R.id.label_date_picker );
		// Retrieve a reference to the date picker button
		ImageButton datePickerButton = (ImageButton) view.findViewById ( R.id.button_date_picker );
		
		// Set the date picker hint
		dateLabel.setHint ( AppResources.getInstance ( this ).getString ( this , R.string.survey_question_date_hint ) );
		// Button click listener for the date picker button
		datePickerButton.setOnClickListener ( new OnClickListener () {
			@Override
			public void onClick ( View view ) {
				// Declare and initialize a calendar object used to host the default date
				// It should be the current date or the answer if the question is answered
				Calendar defaultDate = Calendar.getInstance ();
				// Check if the question is answered
				if ( question.isAnswered () ) {
					try {
						// Compute the answered date
						SimpleDateFormat dateFormat = new SimpleDateFormat ( "dd/MM/yyyy" );
						defaultDate.setTimeInMillis ( dateFormat.parse ( question.getAnswer ().getAnswer ().toString () ).getTime () );
					} catch ( Exception exception ) {
						// Invalid date format
						defaultDate = Calendar.getInstance ();
					} // End of try-catch block
				} // End if
	    		// Display a date picker dialog
				AppDialog.getInstance ().displayDatePicker ( SurveyQuestionsActivity.this , defaultDate.get ( Calendar.YEAR ) , defaultDate.get ( Calendar.MONTH ) , defaultDate.get ( Calendar.DAY_OF_MONTH ) , null , false , true ,
						new DatePickerDialog.OnDateSetListener () {
							@Override
							public void onDateSet ( DatePicker view , int year , int monthOfYear , int dayOfMonth ) {
								// Retrieve the selected date
								Calendar date = Calendar.getInstance ();
								date.set ( year , monthOfYear , dayOfMonth );
								// Check if the question has an answer
								if ( question.getAnswer () == null )
									// Initialize the question's answer
									question.setAnswer ( new Answers () );
								// Save the answer
								SimpleDateFormat dateFormat = new SimpleDateFormat ( "dd/MM/yyyy" );
								question.getAnswer ().answer ( dateFormat.format ( date.getTime () ) );
								// Display the answer
								dateLabel.setText ( DateTime.getFullDate ( SurveyQuestionsActivity.this , date ) );
								// Refresh survey
								refresh ();
							}
						} , null );
			}
		} );
		// Determine if the question is answered (excluding its sub questions)
		if ( question.isAnswered () ) {
			try {
				// Compute the answered date
				SimpleDateFormat dateFormat = new SimpleDateFormat ( "dd/MM/yyyy" );
				Calendar date = Calendar.getInstance ();
				date.setTimeInMillis ( dateFormat.parse ( question.getAnswer ().getAnswer ().toString () ).getTime () );
				// Display the answer
				dateLabel.setText ( DateTime.getFullDate ( SurveyQuestionsActivity.this , date ) );
			} catch ( Exception exception ) {
				// Invalid date format
				// Clear answer
				question.setAnswer ( null );
			} // End of try-catch block
		} // End if
		// Button click listener for the clear button
		view.findViewById ( R.id.button_clear ).setOnClickListener ( new OnClickListener () {
			public void onClick ( View view ) {
				// Reset the date label hint
				dateLabel.setText ( "" );
				// Clear the answer
				question.setAnswer ( null );
				// Refresh survey
				refresh ();
			} // End of onClick
		} );
		// Disable all widgets if the is view flag is set
		boolean isView = getIntent ().getBooleanExtra ( IS_VIEW , false );
		view.findViewById ( R.id.button_clear ).setVisibility ( isView ? View.GONE : View.VISIBLE );
		datePickerButton.setEnabled ( ! isView );
		// Return the view
		return view;
	}
	
	/**
	 * Called when a view has been clicked.<br>
	 * Handler used to display the question images.
	 * 
	 * @param view	The clicked view.
	 */
	public void viewPictures ( View view ) {
		// Retrieve a reference to the view holder
		ViewHolder viewHolder = (ViewHolder) ( (View) view.getParent ().getParent () ).getTag ();
		// Set the displayed question for the cover flow
		displayedQuestionCoverFlow = viewHolder.question;
		// Setup the cover flow view asynchronously
		new SetupCoverFlow ().execute ();
	}
	
	/**
	 * AsyncTask helper class used to populate the surveys list.
	 * 
	 * @author Elias
	 *
	 */
	private class PopulateList extends AsyncTask < Void , Void , Void > {
		
		/*
		 * Runs on the UI thread before doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute () {
			// Display indeterminate progress dialog
			AppDialog.getInstance ().displayIndeterminateProgress ( SurveyQuestionsActivity.this , null , null );
		}
		
		/*
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@SuppressWarnings("unchecked")
		@Override
		protected Void doInBackground ( Void ... params ) {
			try {
				
				// Check if there are surveys to retrieve
				if ( retrieveSurveys ) {
					surveys = (ArrayList < Surveys >) ActivityInstance.readDataGson ( SurveyQuestionsActivity.this , SurveyQuestionsActivity.class.getName () , SURVEYS , new TypeToken < ArrayList < Surveys > > () {}.getType () );;
					// Exit method
					return null;
				} // End if
				
				// Retrieve system date
				Calendar today = Calendar.getInstance ();
				// Retrieve the user code
				String userCode = DatabaseUtils.getCurrentUserCode ( SurveyQuestionsActivity.this );
				// Retrieve the company code
				String companyCode = DatabaseUtils.getCurrentCompanyCode ( SurveyQuestionsActivity.this );
				// Retrieve the client code
				String clientCode = ( (Call) getIntent ().getSerializableExtra ( CALL ) ).getClient ().getClientCode ();
				// Retrieve the client division code
				String divisionCode = ( (Call) getIntent ().getSerializableExtra ( CALL ) ).getClientDivision ().getDivisionCode ();
				// Retrieve the prefix ID
				int prefixID = DatabaseUtils.getInstance ( SurveyQuestionsActivity.this ).getDaoSession ().getUsersDao ().queryBuilder ()
						.where ( UsersDao.Properties.UserCode.eq ( userCode ) ,
								UsersDao.Properties.CompanyCode.eq ( companyCode ) ).unique ().getPrefixID ();
				// Declare and initialize a list used to hold the valid survey IDs
				List < Long > surveyIds = new ArrayList < Long > ();
				// Declare and initialize a list of surveys (not the GreenDao entities)
				ArrayList < Surveys > surveys = new ArrayList < Surveys > ();
				
				// Declare the necessary objects used to query DB
				String SQL = null;
				String selectionArguments [] = null;
				Cursor cursor = null;
				
				// Determine if there is a survey ID filter
				Long surveyIdFilter = getIntent ().getLongExtra ( SURVEY_ID , -1 );
				// Check if a survey id filter is provided
				if ( surveyIdFilter == -1 ) {
					
					// No filter is provided
					// Retrieve all the assigned surveys (based on the survey type)
					
					// Compute the SQL string
					SQL = "SELECT S.* FROM " + SurveysDao.TABLENAME + " S INNER JOIN " + SurveyAssignmentsDao.TABLENAME + " SA ON " +
							"S." + SurveysDao.Properties.SurveyID.columnName + " = SA." + SurveyAssignmentsDao.Properties.SurveyID.columnName + " " +
							"AND ( SA." + SurveyAssignmentsDao.Properties.AssignmentType.columnName + " = ? OR SA." + SurveyAssignmentsDao.Properties.AssignmentType.columnName + " = ? " +
								( (Integer) getIntent ().getSerializableExtra ( SURVEY_TYPE ) != SurveysUtils.Type.CLIENT_SURVEY && (Integer) getIntent ().getSerializableExtra ( SURVEY_TYPE ) != SurveysUtils.Type.SCORING_SHEET ? "" :
								"OR ( SA." + SurveyAssignmentsDao.Properties.AssignmentType.columnName + " = ? AND SA." + SurveyAssignmentsDao.Properties.AssignmentCode.columnName + " = ? ) " +
								"OR ( SA." + SurveyAssignmentsDao.Properties.AssignmentType.columnName + " = ? AND SA." + SurveyAssignmentsDao.Properties.AssignmentCode.columnName + " IN ( " +
								"SELECT CP." + ClientPropertiesDao.Properties.ClientPropertyValueCode.columnName + " FROM " + ClientPropertiesDao.TABLENAME + " CP WHERE CP." + ClientPropertiesDao.Properties.ClientCode.columnName + " = ? " +
										"AND CP." + ClientPropertiesDao.Properties.CompanyCode.columnName + " = ? AND CP." + ClientPropertiesDao.Properties.DivisionCode.columnName + " = ? ) ) " ) +
							" ) WHERE S." + SurveysDao.Properties.SurveyType.columnName + " = ? AND S." + SurveysDao.Properties.CompanyCode.columnName + " = ?";
					
					// Compute the selection arguments
					selectionArguments = (Integer) getIntent ().getSerializableExtra ( SURVEY_TYPE ) != SurveysUtils.Type.CLIENT_SURVEY && (Integer) getIntent ().getSerializableExtra ( SURVEY_TYPE ) != SurveysUtils.Type.SCORING_SHEET?
							new String [] {
								String.valueOf ( SurveyAssignmentsUtils.Type.USER ) ,
								String.valueOf ( SurveyAssignmentsUtils.Type.GROUP_USERS ) ,
								String.valueOf ( (Integer) getIntent ().getSerializableExtra ( SURVEY_TYPE ) ) ,
								companyCode
							} :
							new String [] {
								String.valueOf ( SurveyAssignmentsUtils.Type.USER ) ,
								String.valueOf ( SurveyAssignmentsUtils.Type.GROUP_USERS ) ,
								String.valueOf ( SurveyAssignmentsUtils.Type.CLIENT ) ,
								clientCode ,
								String.valueOf ( SurveyAssignmentsUtils.Type.CLIENT_PROPERTIES ) ,
								clientCode ,
								companyCode ,
								divisionCode ,
								String.valueOf ( (Integer) getIntent ().getSerializableExtra ( SURVEY_TYPE ) ) ,
								companyCode
							};
					
					// Retrieve the corresponding survey IDs
					cursor = DatabaseUtils.getInstance ( SurveyQuestionsActivity.this ).getDaoSession ().getDatabase ().rawQuery ( SQL , selectionArguments );
					// Move the cursor to the first row
					if ( cursor.moveToFirst () == true ) {
						do {
							// Retrieve the survey id
							Long surveyId = cursor.getLong ( cursor.getColumnIndex ( SurveysDao.Properties.SurveyID.columnName ) );
							// Check if the survey id is already added (avoid duplicates)
							if ( ! surveyIds.contains ( surveyId ) ) {
								// Store the survey id
								surveyIds.add ( surveyId );
								// Build a survey object out of the current raw and add it to the surveys list
								surveys.add ( new Surveys ( DatabaseUtils.getInstance ( SurveyQuestionsActivity.this ).getDaoSession ().getSurveysDao ().readEntity ( cursor , 0 ) ) );
							} // End if
						} while ( cursor.moveToNext () );
					} // End if
					// Close the cursor
					cursor.close ();
					cursor = null;
				
				} // End if
				else {
					
					// A filter is provided
					// Retrieve the filtered survey
					me.SyncWise.Android.Database.Surveys filteredSurvey = DatabaseUtils.getInstance ( SurveyQuestionsActivity.this ).getDaoSession ().getSurveysDao ().queryBuilder ()
						.where ( SurveysDao.Properties.SurveyID.eq ( surveyIdFilter ) ).unique ();
					// Check if the filtered survey is valid
					if ( filteredSurvey != null ) {
						// Store the survey id
						surveyIds.add ( filteredSurvey.getSurveyID () );
						// Build a survey object out of the filtered survey and add it to the surveys list
						surveys.add ( new Surveys ( filteredSurvey ) );
					} // End if
					
				} // End else

				// Compute the client code
				clientCode = null;
				switch ( (Integer) getIntent ().getSerializableExtra ( SURVEY_TYPE ) ) {
				case SurveysUtils.Type.CLIENT_SURVEY:
				case SurveysUtils.Type.SCORING_SHEET:
					clientCode = ( (Call) getIntent ().getSerializableExtra ( CALL ) ).getClient ().getClientCode ();
					break;
				case SurveysUtils.Type.USER_SURVEY:
				case SurveysUtils.Type.SUPERVISOR_SURVEY:
					clientCode = userCode;
					break;
				} // End of switch
				
				// Determine if the last survey answer ID should be loaded and is actually not loaded
				// AND if there is at least one survey
				if ( ! surveys.isEmpty () &&
						getIntent ().getBooleanExtra ( LOAD_LAST_CLIENT_SURVEY_ANSWER_ID_OF_CURRENT_JOURNEY , false ) &&
						getIntent ().getLongExtra ( SURVEY_ANSWER_ID , 0 ) == 0 ) {
					
					// Compute the SQL string
					SQL = "SELECT MAX ( " + SurveyAnswersDao.Properties.SurveyAnswerID.columnName + " ) " + SurveyAnswersDao.Properties.SurveyAnswerID.columnName + " " +
							"FROM " + SurveyAnswersDao.TABLENAME + " WHERE " +  SurveyAnswersDao.Properties.VisitID.columnName + " IN ( " +
							"SELECT " + VisitsDao.Properties.VisitID.columnName + " FROM " + VisitsDao.TABLENAME + " WHERE " + VisitsDao.Properties.JourneyCode.columnName + "=? AND " + VisitsDao.Properties.ClientCode.columnName + "=? )";
					
					// Compute the selection arguments
					selectionArguments = new String [] {
							JourneysUtils.getJourneyCode ( prefixID ) ,
							clientCode
					};
					
					// The last survey answer ID for the current journey should be loaded (if any)
					cursor = DatabaseUtils.getInstance ( SurveyQuestionsActivity.this ).getDaoSession ().getDatabase ().rawQuery ( SQL , selectionArguments );
					
					// Move the cursor to the first row
					if ( cursor.moveToFirst () == true ) {
						// Retrieve the survey answer ID
						Long surveyAnswerID = cursor.getLong ( 0 );
						// Check if the survey answer ID is valid
						if ( surveyAnswerID != null && surveyAnswerID != 0 )
							// Set the survey answer ID in the intent
							getIntent ().putExtra ( SURVEY_ANSWER_ID , surveyAnswerID );
					} // End if
					// Close the cursor
					cursor.close ();
					cursor = null;
				} // End if
				
				// Retrieve the survey answer ID
				Long surveyAnswerID = (Long) getIntent ().getSerializableExtra ( SURVEY_ANSWER_ID );
				
				// Iterate over all retrieved surveys
				for ( int i = 0 ; i < surveys.size () ; i ++ ) {
					
					// Compute the survey start date (if any)
					Calendar startDate = null;
					if ( surveys.get ( i ).getSurvey ().getStartDate () != null ) {
						startDate = Calendar.getInstance ();
						startDate.setTime ( surveys.get ( i ).getSurvey ().getStartDate () );
					} // End if
					// Compute the survey end date (if any)
					Calendar endDate = null;
					if ( surveys.get ( i ).getSurvey ().getEndDate () != null ) {
						endDate = Calendar.getInstance ();
						endDate.setTime ( surveys.get ( i ).getSurvey ().getEndDate () );
					} // End if
					// Determine if the survey can be displayed
					if ( ( startDate != null && today.before ( startDate ) ) 
							|| ( endDate != null && today.after ( endDate ) ) ) {
						// Survey must not be displayed
						// Remove it from the surveys list
						surveys.remove ( i -- );
						// Skip the current iteration
						continue;
					} // End if
					
					// Declare and initialize a list used to host the survey questions answers
					List < SurveyAnswers > answers = new ArrayList < SurveyAnswers > ();
					// Map all the answers to their question IDs
					HashMap < Integer , ArrayList < SurveyAnswers > > _answers = new HashMap < Integer , ArrayList<SurveyAnswers> > ();
					// Check if the survey answer ID is valid
					if ( surveyAnswerID != null ) {
						// Set the survey answer ID
						surveys.get ( i ).setSurveyAnswerID ( surveyAnswerID );
						// Retrieve the answers of the current survey (if any) that are NOT deleted
						answers.addAll ( DatabaseUtils.getInstance ( SurveyQuestionsActivity.this ).getDaoSession ().getSurveyAnswersDao ().queryBuilder ()
								.where ( SurveyAnswersDao.Properties.SurveyID.eq ( surveys.get ( i ).getSurvey ().getSurveyID () ) ,
										SurveyAnswersDao.Properties.SurveyType.eq ( (Integer) getIntent ().getSerializableExtra ( SURVEY_TYPE ) ) ,
										SurveyAnswersDao.Properties.SurveyAnswerID.eq ( surveyAnswerID ) ,
										SurveyAnswersDao.Properties.ClientCode.eq ( clientCode ) ,
										SurveyAnswersDao.Properties.AnswerStatus.eq ( StatusUtils.isAvailable () ) ).list () );
						// Iterate over all answers
						for ( SurveyAnswers answer : answers )
							// Check if the current question ID is previously mapped
							if ( _answers.containsKey ( answer.getQuestionID () ) )
								// Add the answer to the list
								_answers.get ( answer.getQuestionID () ).add ( answer );
							else {
								// Otherwise declare a new survey answers list
								ArrayList < SurveyAnswers > list = new ArrayList < SurveyAnswers > ();
								// Add the answer the list
								list.add ( answer );
								// Map the list to the question ID
								_answers.put ( answer.getQuestionID () , list );
							} // End else
					} // End if
					
					// Retrieve the questions of the current survey
					List < SurveyQuestions > questions = DatabaseUtils.getInstance ( SurveyQuestionsActivity.this ).getDaoSession ().getSurveyQuestionsDao ().queryBuilder ()
							.where ( SurveyQuestionsDao.Properties.SurveyID.eq ( surveys.get ( i ).getSurvey ().getSurveyID () ) ).orderAsc ( SurveyQuestionsDao.Properties.QuestionID ).list ();
					// Retrieve the question answers of the current survey
					List < QuestionAnswers > options = DatabaseUtils.getInstance ( SurveyQuestionsActivity.this ).getDaoSession ().getQuestionAnswersDao ().queryRaw ( 
							"WHERE " + QuestionAnswersDao.Properties.QuestionAnswerID.columnName + " IN " +
							"( SELECT " + SurveyQuestionsDao.Properties.QuestionAnswerID.columnName + " FROM " + SurveyQuestionsDao.TABLENAME + " " +
								"WHERE " + SurveyQuestionsDao.Properties.SurveyID.columnName + " = ? " +
								"AND " + SurveyQuestionsDao.Properties.QuestionAnswerID.columnName + " IS NOT NULL )" ,
							new String [] { String.valueOf ( surveys.get ( i ).getSurvey ().getSurveyID () ) } );
					// Retrieve the sub question answers of the current survey
					List < SubQuestionAnswers > subOptions = DatabaseUtils.getInstance ( SurveyQuestionsActivity.this ).getDaoSession ().getSubQuestionAnswersDao ().queryBuilder ()
							.where ( SubQuestionAnswersDao.Properties.SurveyID.eq ( surveys.get ( i ).getSurvey ().getSurveyID () ) ).list ();
					// Map the sub question answers to the question IDs
					Map < Integer , List < SubQuestionAnswers > > _subOptions = new HashMap < Integer , List < SubQuestionAnswers > > ();
					// Iterate over all sub question answers
					for ( SubQuestionAnswers subQuestionAnswer : subOptions ) {
						// Check if the current question ID is previously mapped
						if ( _subOptions.containsKey ( subQuestionAnswer.getQuestionID () ) )
							// Add the sub question answer to the list
							_subOptions.get ( subQuestionAnswer.getQuestionID () ).add ( subQuestionAnswer );
						else {
							// Otherwise, map the current question ID to a new sub question answers list
							List < SubQuestionAnswers > list = new ArrayList < SubQuestionAnswers > ();
							list.add ( subQuestionAnswer );
							_subOptions.put ( subQuestionAnswer.getQuestionID () , list );
						} // End else
					} // End for each
					
					// Iterate over all questions
					for ( int j = 0 ; j < questions.size () ; j ++ ) {
						// Declare and initialize a question
						Questions question = new Questions ( questions.get ( j ) );

						// Determine the question type
						switch ( question.getSurveyQuestion ().getQuestionTypeID () ) {
						case SurveyQuestionsUtils.Type.FREE_TEXT:
							// Do nothing
							break;
						case SurveyQuestionsUtils.Type.NUMBER:
							// Do nothing
							break;
						case SurveyQuestionsUtils.Type.OPTION:
							// Set the question options
							HashMap < Integer , String > questionOptions = new HashMap < Integer , String > ();
							// Iterate over all options
							for ( QuestionAnswers questionAnswer : options )
								// Check if the current option belong to the current question
								if ( questionAnswer.getQuestionAnswerID () == question.getSurveyQuestion ().getQuestionAnswerID () )
									// Add the current option to the current question
									questionOptions.put ( questionAnswer.getLineID () , questionAnswer.getQuestionAnswerDescription () );
							// Set the question options
							question.setOptions ( questionOptions );
							break;
						case SurveyQuestionsUtils.Type.DATE:
							// Do nothing
							break;
						case SurveyQuestionsUtils.Type.SELECTION_ITEMS:
							// TODO
							question = null;
							break;
						default:
							question = null;
						} // End switch

						// Check if the question has been initialized
						if ( question == null )
							// Invalid question, skip it
							continue;
						
						// Load the question's images
						question.setSurveyImages ( new ArrayList < SurveyImages > ( DatabaseUtils.getInstance ( SurveyQuestionsActivity.this ).getDaoSession ().getSurveyImagesDao ().queryBuilder ()
								.where ( SurveyImagesDao.Properties.SurveyID.eq ( question.getSurveyQuestion ().getSurveyID () ) ,
										SurveyImagesDao.Properties.QuestionID.eq ( question.getSurveyQuestion ().getQuestionID () ) ).list () ) );
						
						// Check if the question has answers
						if ( _answers.containsKey ( question.getSurveyQuestion ().getQuestionID () ) ) {
							// Retrieve the answers list
							ArrayList < SurveyAnswers > list = _answers.get ( question.getSurveyQuestion ().getQuestionID () );
							// Load the question's answers
							Calendar calendar = Calendar.getInstance ();
							calendar.setTimeInMillis ( list.get ( 0 ).getStampDate ().getTime () );
							Answers answer = new Answers ( calendar );
							Answers previousAnswer = new Answers ( calendar );
							
							// Declare and initialize a hash map to store the question answer(s)
							HashMap < Integer , String > answersMap = new HashMap < Integer , String > ();
							// Declare and initialize a hash map to store the question previous answer(s)
							HashMap < Integer , String > previousAnswersMap = new HashMap < Integer , String > ();
							// Declare and initialize a hash map to store the question selection code(s)
							HashMap < Integer , String > selectionCodesMap = new HashMap < Integer , String > ();
							// Determine the question type
							switch ( question.getSurveyQuestion ().getQuestionTypeID () ) {
							case SurveyQuestionsUtils.Type.FREE_TEXT:
							case SurveyQuestionsUtils.Type.NUMBER:
							case SurveyQuestionsUtils.Type.OPTION:
							case SurveyQuestionsUtils.Type.DATE:
								// Iterate over the survey answers list
								for ( SurveyAnswers surveyAnswer : list ) {
									// Add the survey answer to the map
									answersMap.put ( surveyAnswer.getLineID () , surveyAnswer.getAnswerDescription () );
									previousAnswersMap.put ( surveyAnswer.getLineID () , surveyAnswer.getAnswerDescription () );
								} // End for each
								// Set the question answer(s)
								answer.setAnswers ( answersMap );
								previousAnswer.setAnswers ( previousAnswersMap );
								question.setAnswer ( answer );
								question.setPreviousAnswer ( previousAnswer );
								break;
							case SurveyQuestionsUtils.Type.SELECTION_ITEMS:
								// Iterate over the survey answers list
								for ( SurveyAnswers surveyAnswer : list ) {
									// Add the survey answer to the maps
									answersMap.put ( surveyAnswer.getLineID () , surveyAnswer.getAnswerDescription () );
									previousAnswersMap.put ( surveyAnswer.getLineID () , surveyAnswer.getAnswerDescription () );
									selectionCodesMap.put ( surveyAnswer.getLineID () , surveyAnswer.getSelectionCode () );
								} // End for each
								// Set the question answer(s) and selection code(s)
								answer.setAnswers ( answersMap );
								answer.setSelectionCodes ( selectionCodesMap );
								previousAnswer.setAnswers ( previousAnswersMap );
								question.setAnswer ( answer );
								question.setPreviousAnswer ( previousAnswer );
								break;
							} // End switch
							
						} // End if
						
						// Check if the question is a sub question
						if ( question.getSurveyQuestion ().getParentQuestionID () == null )
							// The question is NOT is a sub question
							// Add the question to the survey
							surveys.get ( i ).addQuestion ( question );
						// Otherwise search for the parent question if the survey has at least one question
						else if ( surveys.get ( i ).getCount () > 0 ) {
							// The question is a sub question
							// Iterate over all questions
							// TODO : FOR NOW, THERE IS ONLY ONE LEVEL ONLY OF QUESTION PARENTS / CHILDREN
							for ( Questions parentQuestion : surveys.get ( i ).getQuestions () )
								// Check if the question id match the parent question id
								if ( parentQuestion.getSurveyQuestion ().getQuestionID () == question.getSurveyQuestion ().getParentQuestionID () ) {
									// Check if the parent question is an option question with single answers (no multiple answers)
									if ( parentQuestion.getSurveyQuestion ().getQuestionTypeID () == SurveyQuestionsUtils.Type.OPTION
											&& ! parentQuestion.isMultipleAnswers () ) {
										// Check if the sub-question has at least one sub question answer
										if ( _subOptions.containsKey ( question.getSurveyQuestion ().getQuestionID () ) )
											// Iterate over all sub question answers
											for ( SubQuestionAnswers subQuestionAnswer : _subOptions.get ( question.getSurveyQuestion ().getQuestionID () ) )
												// Add the question to the appropriate current parent question
												parentQuestion.addSubQuestion ( subQuestionAnswer.getParentLineID () , question );
									} // End if
									else
										// Otherwise the sub-question is either not an option question or an option question with multiple answers
										// The parent question of the current sub-question can only have one sub-question
										// Add the question to the appropriate current parent question
										parentQuestion.addSubQuestion ( 0 , question );
									// Exit the loop
									break;
								} // End if
						} // End else
					} // End for loop
					
				} // End for loop
				
				// Set the surveys list
				SurveyQuestionsActivity.this.surveys = surveys;
				
			} catch ( Exception exception ) {
				// Do nothing
			} // End of try-catch block
			return null;
		}
		
		/*
		 * Runs on the UI thread after doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute ( Void arg ) {
			// Dismiss dialog
			AppDialog.getInstance ().dismiss ();
			
			try {
				// Check if the surveys list is valid
				if ( surveys != null ) {
					// Set the spinner adapter
					spinner.setAdapter ( new SurveysAdapter ( SurveyQuestionsActivity.this , R.layout.survey_spinner_card_item , surveys ) );
					// Check if the selected survey index is valid
					if ( selectedSurvey != null )
						// Select the appropriate survey
						spinner.setSelection ( selectedSurvey );
					// Display / hide the empty view accordingly
					findViewById ( R.id.empty_list_view ).setVisibility ( surveys.isEmpty () ? View.VISIBLE : View.GONE );
		    		// Refresh the menu items in the action bar
		    		invalidateOptionsMenu ();
				} // End if
			} catch ( Exception exception ) {
				// Do nothing
			} // End of try-catch block
		}
		
	}
	
	/**
	 * AsyncTask helper class used to save the surveys.
	 * 
	 * @author Elias
	 *
	 */
	private class Save extends AsyncTask < Void , Void , Boolean > {
		
		/*
		 * Runs on the UI thread before doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute () {
			// Display indeterminate progress dialog
			AppDialog.getInstance ().displayIndeterminateProgress ( SurveyQuestionsActivity.this , null , null );
		}
		
		/*
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Boolean doInBackground ( Void ... params ) {
			try {
				
				// TODO : Handle Selection Questions
				
				// Check if the surveys list is valid
				if ( surveys == null || surveys.isEmpty () )
					// Invalid surveys list
					return false;
				
				// Flag used to indicate if the survey questions have previously saved answers
				boolean hasPreviousAnswers = surveys.get ( 0 ).getSurveyAnswerID () != null;
				
				// Check if the survey answer ID is provided (either all surveys have the same answer ID or none have one)
				if ( surveys.get ( 0 ).getSurveyAnswerID () == null ) {
					// Compute a new survey answer ID
					long surveyAnswerID = SurveyAnswersUtils.getSurveyAnswerID ();
					// All surveys should have NULL as survey answer ID
					// Iterate over all surveys to check if all survey answer IDs are NULL
					for ( Surveys survey : surveys )
						// Check if the survey answer ID is NOT NULL
						if ( survey.getSurveyAnswerID () != null )
							// Invalid survey answer ID
							return false;
						else
							// Set the new survey answer ID
							survey.setSurveyAnswerID ( surveyAnswerID );
				} // End if
				else {
					// All surveys should have the same survey answer ID
					// Iterate over all surveys to check if all survey answer IDs are identical
					for ( Surveys survey : surveys )
						// Check if the survey answer ID is NOT identical to the first survey's answer ID
						if ( survey.getSurveyAnswerID () == null || ! survey.getSurveyAnswerID ().equals ( surveys.get ( 0 ).getSurveyAnswerID () ) )
							// Invalid survey answer ID
							return false;
				} // End else
				
				// Retrieve the user code
				String userCode = DatabaseUtils.getCurrentUserCode ( SurveyQuestionsActivity.this );
				// Retrieve the company code
				String companyCode = DatabaseUtils.getCurrentCompanyCode ( SurveyQuestionsActivity.this );
				// Retrieve the division code
				String divisionCode = DatabaseUtils.getCurrentDivisionCode ( SurveyQuestionsActivity.this );
				// Retrieve the survey type
				int surveyType = (Integer) getIntent ().getSerializableExtra ( SURVEY_TYPE );
				// Compute the client code and visit ID
				String clientCode = null;
				Long visitID = null;
				switch ( surveyType ) {
				case SurveysUtils.Type.CLIENT_SURVEY:
				case SurveysUtils.Type.SCORING_SHEET:
					clientCode = ( (Call) getIntent ().getSerializableExtra ( CALL ) ).getClient ().getClientCode ();
					divisionCode = ( (Call) getIntent ().getSerializableExtra ( CALL ) ).getClientDivision ().getDivisionCode ();
					visitID = ( (Visits) getIntent ().getSerializableExtra ( VISIT ) ).getVisitID ();
					break;
				default:
					clientCode = userCode;
					break;
				} // End of switch
				
				// Declare and initialize a list used to host the survey answers to insert
				List < SurveyAnswers > inserts = new ArrayList < SurveyAnswers > ();
				// Declare and initialize a list used to host the survey answers to update
				List < SurveyAnswers > updates = new ArrayList < SurveyAnswers > ();
				
				// Determine if there are previous answers
				if ( ! hasPreviousAnswers ) {
					// There are no previous answers
					// Iterate over all surveys
					for ( Surveys survey : surveys ) {
						// Check if the questions list is valid
						if ( survey.getQuestions () == null )
							// Invalid questions list
							// Skip the current survey
							continue;
						// Iterate over all questions
						for ( Questions question : survey.getQuestions () ) {
							// Check if the question is answered
							if ( question.isAnswered () ) {
								// Iterate over all answers
								for ( int i = 0 ; i < question.getAnswer ().getCount () ; i ++ )
									// Compute a new survey answer and add it to the list
									inserts.add ( new SurveyAnswers ( null , // ID
											survey.getSurvey ().getSurveyID () , // SurveyID
											question.getSurveyQuestion ().getQuestionID () , // QuestionID
											question.getAnswer ().getAnswerID ( i ) , // LineID
											surveyType , // SurveyType
											survey.getSurveyAnswerID () , // SurveyAnswerID
											userCode , // UserCode
											clientCode , // ClientCode
											divisionCode , // DivisionCode
											companyCode, // CompanyCode
											visitID , // VisitID
											question.getAnswer ().getAnswer ( i ).toString () , // AnswerDescription
											StatusUtils.isAvailable () , // AnswerStatus
											question.getSurveyQuestion ().getQuestionDescription () , // QuestionDescription
											question.getSurveyQuestion ().getQuestionAltDescription () , // QuestionAltDescription
											null , // SelectionCode
											IsProcessedUtils.isNotSync () , // IsProcessed
											question.getAnswer ().getAnswerDate ().getTime () ) ); // StampDate
								// Determine if the question's sub-question is valid AND answered
								if ( question.getApplicableSubQuestion () != null && question.isCompletelyAnswered () ) {
									// Retrieve a reference to the applicable sub-question
									Questions subQuestion = question.getApplicableSubQuestion ();
									// Iterate over all answers
									for ( int i = 0 ; i < subQuestion.getAnswer ().getCount () ; i ++ )
										// Compute a new survey answer and add it to the list
										inserts.add ( new SurveyAnswers ( null , // ID
												survey.getSurvey ().getSurveyID () , // SurveyID
												subQuestion.getSurveyQuestion ().getQuestionID () , // QuestionID
												subQuestion.getAnswer ().getAnswerID ( i ) , // LineID
												surveyType , // SurveyType
												survey.getSurveyAnswerID () , // SurveyAnswerID
												userCode , // UserCode
												clientCode , // ClientCode
												divisionCode , // DivisionCode
												companyCode, // CompanyCode
												visitID , // VisitID
												subQuestion.getAnswer ().getAnswer ( i ).toString () , // AnswerDescription
												StatusUtils.isAvailable () , // AnswerStatus
												subQuestion.getSurveyQuestion ().getQuestionDescription () , // QuestionDescription
												subQuestion.getSurveyQuestion ().getQuestionAltDescription () , // QuestionAltDescription
												null , // SelectionCode
												IsProcessedUtils.isNotSync () , // IsProcessed
												subQuestion.getAnswer ().getAnswerDate ().getTime () ) ); // StampDate
								} // End if
							} // End if
						} // End for each
					} // End for each
				} // End if
				else {
					// There are previous answers
					// Iterate over all surveys
					for ( Surveys survey : surveys ) {
						// Check if the questions list is valid
						if ( survey.getQuestions () == null )
							// Invalid questions list
							// Skip the current survey
							continue;
						// Load all previous answers
						List < SurveyAnswers > previousAnswers = DatabaseUtils.getInstance ( SurveyQuestionsActivity.this ).getDaoSession ().getSurveyAnswersDao ().queryBuilder ()
								.where ( SurveyAnswersDao.Properties.SurveyID.eq ( survey.getSurvey ().getSurveyID () ) ,
										SurveyAnswersDao.Properties.SurveyType.eq ( surveyType ) ,
										SurveyAnswersDao.Properties.SurveyAnswerID.eq ( survey.getSurveyAnswerID () ) ,
										SurveyAnswersDao.Properties.ClientCode.eq ( clientCode ) ).list ();
						// Map all the answers to their question IDs
						HashMap < Integer , ArrayList < SurveyAnswers > > _answers = new HashMap < Integer , ArrayList<SurveyAnswers> > ();
						// Iterate over all answers
						for ( SurveyAnswers answer : previousAnswers )
							// Check if the current question ID is previously mapped
							if ( _answers.containsKey ( answer.getQuestionID () ) )
								// Add the answer to the list
								_answers.get ( answer.getQuestionID () ).add ( answer );
							else {
								// Otherwise declare a new survey answers list
								ArrayList < SurveyAnswers > list = new ArrayList < SurveyAnswers > ();
								// Add the answer the list
								list.add ( answer );
								// Map the list to the question ID
								_answers.put ( answer.getQuestionID () , list );
							} // End else
						// Iterate over all questions
						for ( Questions question : survey.getQuestions () ) {
							// Check if the current question is answered
							if ( question.isAnswered () ) {
								// The question is answered
								properlyAnswerQuestion ( survey , question , _answers.get ( question.getSurveyQuestion ().getQuestionID () ) , inserts , updates , surveyType , userCode , clientCode , visitID );
								// Iterate over all the question's sub-questions (if any)
								if ( question.getSubQuestions () != null && ! question. getSubQuestions ().isEmpty () ) {
									// Retrieve the applicable sub-question
									Questions applicableSubQuestion = question.getApplicableSubQuestion ();
									// Iterate over all the sub questions
									for ( Integer answerID : question.getSubQuestions ().keySet () ) {
										// Retrieve a reference to the sub-question
										Questions subQuestion = question.getSubQuestions ().get ( answerID );
										// Check if this is the applicable AND is answered
										if ( applicableSubQuestion != null && applicableSubQuestion.equals ( subQuestion ) && subQuestion.isAnswered () )
											// The sub-question is applicable and answered
											properlyAnswerQuestion ( survey , subQuestion , _answers.get ( subQuestion.getSurveyQuestion ().getQuestionID () ) , inserts , updates , surveyType , userCode , clientCode , visitID );
										else {
											// Otherwise, the sub-question is either not applicable, not answered, or both
											// Check if the current sub-question has previous answers
											if ( _answers.containsKey ( subQuestion.getSurveyQuestion ().getQuestionID () ) )
												// Delete any answer for this sub-question
												updates.addAll ( deleteAnswers ( _answers.get ( subQuestion.getSurveyQuestion ().getQuestionID () ) ) );
										} // End else
									} // For each
								} // End if
							} // End if
							else {
								// The question is NOT answered
								// Check if the current question has previous answers
								if ( _answers.containsKey ( question.getSurveyQuestion ().getQuestionID () ) )
									// Delete any answer for this question
									updates.addAll ( deleteAnswers ( _answers.get ( question.getSurveyQuestion ().getQuestionID () ) ) );
								// Delete any answer for any sub-question of this question
								// Iterate over all the question's sub-questions (if any)
								if ( question.getSubQuestions () != null && ! question. getSubQuestions ().isEmpty () ) {
									// Iterate over all the sub questions
									for ( Integer answerID : question.getSubQuestions ().keySet () ) {
										// Retrieve a reference to the sub-question
										Questions subQuestion = question.getSubQuestions ().get ( answerID );
										// Check if the current sub-question has previous answers
										if ( _answers.containsKey ( subQuestion.getSurveyQuestion ().getQuestionID () ) )
											// Delete any answer for this sub-question
											updates.addAll ( deleteAnswers ( _answers.get ( subQuestion.getSurveyQuestion ().getQuestionID () ) ) );
									} // End for each
								} // End if
							} // End else
						} // End for each
					} // End for each
				} // End else
				
				// Insert survey answers (if any)
				if ( ! inserts.isEmpty () )
					DatabaseUtils.getInstance ( SurveyQuestionsActivity.this ).getDaoSession ().getSurveyAnswersDao ().insertInTx ( inserts );
				// Update survey answers (if any)
				if ( ! updates.isEmpty () )
					DatabaseUtils.getInstance ( SurveyQuestionsActivity.this ).getDaoSession ().getSurveyAnswersDao ().updateInTx ( updates );
				
				// Determine the survey type
				switch ( surveyType ) {
				case SurveysUtils.Type.CLIENT_SURVEY:
				case SurveysUtils.Type.SCORING_SHEET:
					// Indicate that a survey was successfully filled during this visit
					CallAction.setCallActionStatus ( SurveyQuestionsActivity.this , ( (Visits) getIntent ().getSerializableExtra ( VISIT ) ).getVisitID () , CallAction.ID.SURVEY , true );
					break;
				} // End of switch
				
			} catch ( Exception exception ) {
				// Indicate that an error occurred
				return false;
			} // End of try-catch block
			return true;
		}
		
		/*
		 * Runs on the UI thread after doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute ( Boolean success ) {
			// Dismiss dialog
			AppDialog.getInstance ().dismiss ();
			
			try {
				// Check if the save was successful
				if ( success )
					// Indicate that the save was successful
					Vibration.vibrate ( SurveyQuestionsActivity.this );
				
				// Determine the survey type
				switch ( (Integer) getIntent ().getSerializableExtra ( SURVEY_TYPE ) ) {
				case SurveysUtils.Type.CLIENT_SURVEY:
				case SurveysUtils.Type.SCORING_SHEET:
					// Check if the save was successful
					if ( success ) {
			        	// Call this to set the result that your activity will return to its caller
			        	setResult ( RESULT_OK , new Intent ().putExtra ( CallMenuActivity.ACTION_RESULT_SUCCESS , success ) );
			    		// Finish this activity
			        	SurveyQuestionsActivity.this.finish ();
					} // End if
					break;
				default:
					// Check if the save was successful
					if ( success ) {
			        	// Call this to set the result that your activity will return to its caller
			        	setResult ( RESULT_OK , new Intent ().putExtra ( SURVEY_SUCCESSFULLY_SAVED , success ) );
			    		// Finish this activity
			        	SurveyQuestionsActivity.this.finish ();
					} // End if
					break;
				} // End of switch

			} catch ( Exception exception ) {
				// Do nothing
			} // End of try-catch block
		}
		
		/**
		 * Deletes all the NOT deleted answers in the provided {@code answers} list.<br>
		 * All the modified answered are returned in a list.
		 * 
		 * @param answers	List of survey questions answers to delete.
		 * @return	List of survey questions answers that were modified.
		 */
		private List < SurveyAnswers > deleteAnswers ( final List < SurveyAnswers > answers ) {
			// Declare and initialize a list used to host the survey answers to update
			List < SurveyAnswers > updates = new ArrayList < SurveyAnswers > ();
			// Retrieve the system date
			Date today = Calendar.getInstance ().getTime ();
			// Iterate over all the question's answers
			for ( SurveyAnswers answer : answers ) {
				// Check if the answer is NOT deleted
				if ( answer.getAnswerStatus () == StatusUtils.isAvailable () ) {
					// Delete the answer
					answer.setAnswerStatus ( StatusUtils.isDeleted () );
					// Update the is processed field
					answer.setIsProcessed ( IsProcessedUtils.isNotSync () );
					// Update the stamp date
					answer.setStampDate ( today );
					// Add the answer to the update list
					updates.add ( answer );
				} // End if
			} // End for each
			// Return the updated survey questions answers
			return updates;
		}
		
		/**
		 * Performs all the necessary actions to properly answer a survey question.
		 * 
		 * @param survey	The current {@link me.SyncWise.Android.Modules.Survey.Surveys Surveys} object whose question should be answered.
		 * @param question	The current survey {@link me.SyncWise.Android.Modules.Survey.Questions Questions} object that should be answered.
		 * @param previousAnswers	List of {@link me.SyncWise.Android.Database.SurveyAnswers SurveyAnswers} objects holding the previous answers of the current survey.
		 * @param inserts	List of {@link me.SyncWise.Android.Database.SurveyAnswers SurveyAnswers} objects holding references to all objects that should be inserted.
		 * @param updates	List of {@link me.SyncWise.Android.Database.SurveyAnswers SurveyAnswers} objects holding references to all objects that should be updated.
		 * @param surveyType	Integer hosting the survey type.
		 * @param userCode	String hosting the user code.
		 * @param clientCode	String hosting the client code.
		 * @param visitID	Long hosting the visit ID (if any).
		 */
		private void properlyAnswerQuestion ( final Surveys survey , final Questions question , final List < SurveyAnswers > previousAnswers ,
				final List < SurveyAnswers > inserts , final List < SurveyAnswers > updates , final int surveyType , final String userCode , final String clientCode , final Long visitID ) {
			// Retrieve the system date
			Date today = Calendar.getInstance ().getTime ();
			// Retrieve the company code
			String companyCode = DatabaseUtils.getCurrentCompanyCode ( SurveyQuestionsActivity.this );
			// Retrieve the division code
			String divisionCode = DatabaseUtils.getCurrentDivisionCode ( SurveyQuestionsActivity.this );
			try {
				// Try to set the call's division code
				divisionCode = ( (Call) getIntent ().getSerializableExtra ( CALL ) ).getClientDivision ().getDivisionCode ();
			} catch ( Exception exception ) {
				// Do nothing
				// The default value for the division code is set as the one stored in the main session
			} // End for try-catch block
			
			// Check if the current question has previous answers
			if ( previousAnswers != null ) {
				// The question has previous answers
				// Declare and initialize a list used to hold the answers to create
				List < Answers > createAnswers = new ArrayList < Answers > ();
				// Iterate over all answers
				for ( int i = 0 ; i < question.getAnswer ().getCount () ; i ++ ) {
					// Create a new Answer object
					Answers answer = new Answers ( question.getAnswer ().getAnswerDate () );
					if ( question.getAnswer ().getSelectionCode ( i ) != null )
						answer.answer ( question.getAnswer ().getAnswerID ( i ) , question.getAnswer ().getAnswer ( i ) , question.getAnswer ().getSelectionCode ( i ) );
					else
						answer.answer ( question.getAnswer ().getAnswerID ( i ) , question.getAnswer ().getAnswer ( i ) );
					// Add the answer to the list
					createAnswers.add ( answer );
				} // End for loop
				// Iterate over all the previous answers of the current question
				for ( SurveyAnswers previousAnswer : previousAnswers ) {
					// Declare a flag used to determine if the previous answer should be deleted
					boolean deletePreviousAnswer = true;
					// Iterate over all the answers
					for ( int k = 0 ; k < createAnswers.size () ; k ++ ) {
						// Check if the answers match
						if ( previousAnswer.getLineID () == createAnswers.get ( k ).getAnswerID () ) {
							// Reset flag
							deletePreviousAnswer = false;
							// Check if the answers differ
							if ( ! previousAnswer.getAnswerDescription ().equals ( createAnswers.get ( k ).getAnswer () ) ) {
								// Update the answer
								previousAnswer.setAnswerDescription ( createAnswers.get ( k ).getAnswer () );
								// Update the is processed field
								previousAnswer.setIsProcessed ( IsProcessedUtils.isNotSync () );
								// Make sure the answer is NOT deleted
								previousAnswer.setAnswerStatus ( StatusUtils.isAvailable () );
								// Update the stamp date
								previousAnswer.setStampDate ( today );
								// Add the answer to the update list
								updates.add ( previousAnswer );
							} // End if
							// Remove the answer from the list
							createAnswers.remove ( k );
							k --;
							// Exit loop
							break;
						} // End if
					} // End for each
					// Check if the previous answer should be deleted
					// AND is actually NOT deleted
					if ( deletePreviousAnswer && previousAnswer.getAnswerStatus () != StatusUtils.isDeleted () ) {
						// Delete the answer
						previousAnswer.setAnswerStatus ( StatusUtils.isDeleted () );
						// Update the is processed field
						previousAnswer.setIsProcessed ( IsProcessedUtils.isNotSync () );
						// Update the stamp date
						previousAnswer.setStampDate ( today );
						// Add the answer to the update list
						updates.add ( previousAnswer );
					} // End if
				} // End for each
				// Iterate over the remaining answers (that should be created)
				for ( Answers answer : createAnswers )
					// Compute a new survey answer and add it to the list
					inserts.add ( new SurveyAnswers ( null , // ID
							survey.getSurvey ().getSurveyID () , // SurveyID
							question.getSurveyQuestion ().getQuestionID () , // QuestionID
							answer.getAnswerID () , // LineID
							surveyType , // SurveyType
							survey.getSurveyAnswerID () , // SurveyAnswerID
							userCode , // UserCode
							clientCode , // ClientCode
							divisionCode , // DivisionCode
							companyCode, // CompanyCode
							visitID , // VisitID
							answer.getAnswer ().toString () , // AnswerDescription
							StatusUtils.isAvailable () , // AnswerStatus
							question.getSurveyQuestion ().getQuestionDescription () , // QuestionDescription
							question.getSurveyQuestion ().getQuestionAltDescription () , // QuestionAltDescription
							null , // SelectionCode
							IsProcessedUtils.isNotSync () , // IsProcessed
							question.getAnswer ().getAnswerDate ().getTime () ) ); // StampDate
			} // End if
			else {
				// The question does NOT have previous answers
				// Iterate over all answers
				for ( int i = 0 ; i < question.getAnswer ().getCount () ; i ++ )
					// Compute a new survey answer and add it to the list
					inserts.add ( new SurveyAnswers ( null , // ID
							survey.getSurvey ().getSurveyID () , // SurveyID
							question.getSurveyQuestion ().getQuestionID () , // QuestionID
							question.getAnswer ().getAnswerID ( i ) , // LineID
							surveyType , // SurveyType
							survey.getSurveyAnswerID () , // SurveyAnswerID
							userCode , // UserCode
							clientCode , // ClientCode
							divisionCode , // DivisionCode
							companyCode, // CompanyCode
							visitID , // VisitID
							question.getAnswer ().getAnswer ( i ).toString () , // AnswerDescription
							StatusUtils.isAvailable () , // AnswerStatus
							question.getSurveyQuestion ().getQuestionDescription () , // QuestionDescription
							question.getSurveyQuestion ().getQuestionAltDescription () , // QuestionAltDescription
							null , // SelectionCode
							IsProcessedUtils.isNotSync () , // IsProcessed
							question.getAnswer ().getAnswerDate ().getTime () ) ); // StampDate
			} // End else
		}
		
	}
	
	/**
	 * AsyncTask helper class used to display the question images.
	 * 
	 * @author Elias
	 *
	 */
	private class SetupCoverFlow extends AsyncTask < Void , Void , String [] > {
		
		/*
		 * Runs on the UI thread before doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute () {
			// Display indeterminate progress dialog
			AppDialog.getInstance ().displayIndeterminateProgress ( SurveyQuestionsActivity.this , null , null );
			// Retrieve a reference to the cover flow
			CoverFlow coverFlow = (CoverFlow) findViewById ( R.id.coverflowReflect );
			// Display the cover flow view
			coverFlow.setVisibility ( View.VISIBLE );
			
			// Declare and initialize a point object used to store the screen size
			Point size = new Point();
			// Compute the screen size
			getWindowManager ().getDefaultDisplay ().getSize ( size );
			// Determine which side is smaller
			int side = size.x > size.y ? size.y : size.x;
			// Compute the cover flow image size to be exactly the smallest side of the screen 
			
			// Set the cover flow image size
			coverFlow.setImageHeight ( side );
			coverFlow.setImageWidth ( side );
		}
		
		/*
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected String [] doInBackground ( Void ... params ) {
			try {
				// Check if the question is valid
				if ( displayedQuestionCoverFlow == null )
					// Do nothing
					return new String [] {};
				
				// Open the primary storage device folder
				File storagePrimary = Environment.getExternalStorageDirectory ();
				// Open the root storage folder
				File storageRoot = storagePrimary.getParentFile ();
				// Retrieve a list of other secondary storage
				File storageSecondaryArray [] = storageRoot.listFiles ( new ExternalStorageFilter () );
				
				// Determine what storage folder to use
				// Based on if they contain an item pictures directory inside a pictures directory
				// Priority is for secondary storage devices first
				File storage = pickStorage ( storagePrimary , storageSecondaryArray );
				// Open the required item pictures folder
				File folder = new File ( storage , "//" + Environment.DIRECTORY_PICTURES + "//" + SurveysUtils.PICTURES_DIRECTORY );
				// Check if the folder exists
				if ( ! folder.exists () )
					// Do nothing
					return new String [] {};
				
				// Retrieve the survey images
				List < SurveyImages > surveyImages = displayedQuestionCoverFlow.getSurveyImages ();
				// Declare and initialize a list used to host the images files
				List < File > files = new ArrayList < File > ();
				// Iterate over the survey images
				for ( SurveyImages surveyImage : surveyImages )
					// Build a file to the specific image
					files.add ( new File ( folder , surveyImage.getImageName () ) );
				// Otherwise return the item picture paths
				// Declare and initialize an array of strings
				String paths [] = new String [ files.size () ];
				// Iterate over all picture files
				for ( int i = 0 ; i < files.size () ; i ++ )
					// Add the current picture file path
					paths [ i ] = files.get ( i ).getAbsolutePath ();
				// Return the paths array
				return paths;
			} catch ( Exception exception ) {
				// Do nothing
				return new String [] {};
			} // End of try-catch block
		}
		
		/**
		 * Picks from within the storage list the first occurrence that contains a valid item pictures folder.<br>
		 * If none has the required folder, or if the list is empty / invalid, the default storage is returned.
		 * 
		 * @param defaultStorage	Reference to the default storage file to return, if the storage list is empty or has no storage with the required item pictures folder.
		 * @param storageList	Storage list hosting directories that might contain a valid item pictures folder.
		 * @return	Reference to a storage file hosting a valid item pictures folder.
		 */
		private File pickStorage ( final File defaultStorage , final File storageList [] ) {
			// Check if the storage list is valid
			if ( storageList == null )
				// Return the default storage
				return defaultStorage;
			// Iterate over the storage list
			for ( File storageSecondary : storageList ) {
				// Open the required item pictures folder
				File folder = new File ( storageSecondary , "//" + Environment.DIRECTORY_PICTURES + "//" + SurveysUtils.PICTURES_DIRECTORY );
				// Check if the folder exists
				if ( folder.exists () )
					// Return the current storage file
					return storageSecondary;
			} // End for each
			// Otherwise return the default storage
			return defaultStorage;
		}
		
		/*
		 * Runs on the UI thread after doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute ( String paths [] ) {
			// Dismiss dialog
			AppDialog.getInstance ().dismiss ();
			// Retrieve a reference to the cover flow
			CoverFlow coverFlow = (CoverFlow) findViewById ( R.id.coverflowReflect );
			// Disable rotation
			coverFlow.setMaxRotationAngle ( 0 );
			// Declare and initialize a new path image adapter to the cover flow view
			PathImageAdapter pathImageAdapter = new PathImageAdapter ( SurveyQuestionsActivity.this , paths );
			// Set the adapter
			coverFlow.setAdapter ( pathImageAdapter );
		}
		
	}
				
}