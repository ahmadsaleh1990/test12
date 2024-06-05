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

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import me.SyncWise.Android.Database.IsForcedUtils;

/**
 * Class used to define a survey.<br>
 * This class is different from (yet relies on) the GreenDao entity {@link me.SyncWise.Android.Database.Surveys Surveys}.
 * 
 * @author Elias
 *
 */
public class Surveys implements Serializable {

	/**
	 * Default serial version ID.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Survey GreenDao entity object.
	 */
	private final me.SyncWise.Android.Database.Surveys survey;
	
	/**
	 * Long used to host the survey answer ID in the following format : YYYYMMDDHHMMSSSSS
	 */
	private Long surveyAnswerID;
	
	/**
	 * List used to host the current survey's questions.
	 */
	private ArrayList < Questions > questions;
	
	/**
	 * Constructor.
	 * 
	 * @param survey	Survey GreenDao entity object.
	 */
	public Surveys ( final me.SyncWise.Android.Database.Surveys survey ) {
		this.survey = survey;
	}
	
	/**
	 * Getter - {@link #survey}
	 * 
	 * @return	Survey GreenDao entity object.
	 */
	public me.SyncWise.Android.Database.Surveys getSurvey () {
		return survey;
	}
	
	/**
	 * Getter - {@link #surveyAnswerID}
	 * 
	 * @return Long hosting the survey answer ID in the following format : YYYYMMDDHHMMSSSSS
	 */
	public Long getSurveyAnswerID () {
		return surveyAnswerID;
	}
	
	/**
	 * Setter - {@link #surveyAnswerID}
	 * 
	 * @param surveyAnswerID Long hosting the survey answer ID in the following format : YYYYMMDDHHMMSSSSS
	 */
	public void setSurveyAnswerID ( final Long surveyAnswerID ) {
		this.surveyAnswerID = surveyAnswerID;
	}

	/**
	 * Setter - {@link #surveyAnswerID}
	 * 
	 * @param calendar Calendar object used to compute the survey answer ID.
	 */
	public void setSurveyAnswerID ( final Calendar calendar ) {
		// Declare and initialize a two digits formatter
    	DecimalFormat twoDigits = new DecimalFormat ( "00" );
		// Declare and initialize a 3 digits formatter
    	DecimalFormat threeDigits = new DecimalFormat ( "000" );
    	// Compute the survey answer id in the following format : YYYYMMDDHHMMSSSSS
    	StringBuilder surveyAnswerID = new StringBuilder ();
    	surveyAnswerID.append ( calendar.get ( Calendar.YEAR ) ); // YYYY
    	surveyAnswerID.append ( twoDigits.format ( 1 + calendar.get ( Calendar.MONTH ) ) ); // MM
    	surveyAnswerID.append ( twoDigits.format ( calendar.get ( Calendar.DATE ) ) ); // DD
    	surveyAnswerID.append ( twoDigits.format ( calendar.get ( Calendar.HOUR_OF_DAY ) ) ); // HH
    	surveyAnswerID.append ( twoDigits.format ( calendar.get ( Calendar.MINUTE ) ) ); // MM
    	surveyAnswerID.append ( twoDigits.format ( calendar.get ( Calendar.SECOND ) ) ); // SS
    	surveyAnswerID.append ( threeDigits.format ( calendar.get ( Calendar.MILLISECOND ) ) ); // SSS
    	// Store the survey answer id
    	this.surveyAnswerID = Long.parseLong ( surveyAnswerID.toString () );
	}
	
	/**
	 * Setter - {@link #surveyAnswerID}
	 * 
	 * Today's date is used to compute the survey answer ID.
	 */
	public void setSurveyAnswerID () {
		// Compute the survey answer ID using the current date
		setSurveyAnswerID ( Calendar.getInstance () );
	}
	
	/**
	 * Initializes the questions list if necessary.
	 */
	private void initializeQuestions () {
		// Check if the questions list is valid
		if ( questions == null )
			// Initialize the questions list
			questions = new ArrayList < Questions > ();
	}
	
	/**
	 * Sorts the survey questions based on their IDs.
	 */
	public void sortQuestions () {
		// Check if the questions list is valid and contains at least two questions
		if ( questions != null && questions.size () > 1 )
			// Sort the survey questions based on their IDs
			Collections.sort ( questions , new Comparator < Questions > () {
				@Override
				public int compare ( Questions question1 , Questions question2 ) {
					// Compare the survey questions based on their IDs
					return question1.getSurveyQuestion ().getQuestionID () - question2.getSurveyQuestion ().getQuestionID ();
				}
			} );
	}
	
	/**
	 * Getter - {@link #questions}
	 * 
	 * @return List used to host the current survey's questions.
	 */
	public ArrayList < Questions > getQuestions () {
		return questions;
	}
	
	/**
	 * Setter - {@link #questions}
	 * 
	 * @param questions	List used to host the current survey's questions.
	 */
	public void setQuestions ( final ArrayList < Questions > questions ) {
		this.questions = questions;
	}

	/**
	 * Adds a question to the survey questions list.
	 * 
	 * @param question	Question object to add to the current survey.
	 */
	public void addQuestion ( final Questions question ) {
		// Check if the provided question is valid
		if ( question != null ) {
			// Initialize the questions list if needed
			initializeQuestions ();
			// Add the question to the list
			questions.add ( question );
		} // End if
	}
	
	/**
	 * Adds a questions list to the survey questions list.
	 * 
	 * @param questions	Question list to add to the current survey.
	 */
	public void addQuestions ( final ArrayList < Questions > questions ) {
		// Check if the provided questions are valid
		if ( questions != null && ! questions.isEmpty () ) {
			// Initialize the questions list if needed
			initializeQuestions ();
			// Add the questions to the list
			this.questions.addAll ( questions );
		} // End if
	}
	
	/**
	 * Returns the total number of survey questions.
	 * 
	 * @return	Integer holding the number of questions.
	 */
	public int getCount () {
		try {
			// Return the questions list size
			return questions.size ();
		} catch ( Exception exception ) {
			// Invalid questions list
			return 0;
		} // End of try-catch block
	}
	
	/**
	 * Gets the survey question at the provided position.
	 * 
	 * @param position	Integer holding the required question position.
	 * @return	The survey question at the indicated position, or {@code NULL} if invalid.
	 */
	public Questions getQuestion ( final int position ) {
		try {
			// Return the appropriate question
			return questions.get ( position );
		} catch ( Exception exception ) {
			// Invalid questions list or position
			return null;
		}
	}
	
	/**
	 * Determines if the survey question at the provided position is the last question in the survey.
	 * 
	 * @param position	Integer holding the required question position.
	 * @return	Boolean indicating if the survey question at the provided position is the last question in the survey.
	 */
	public boolean isLastQuestion ( final int position ) {
		try {
			// Determine if the provided position is the last one
			return position == questions.size () - 1 ? true : false;
		} catch ( Exception Exception ) {
			// Invalid question's list or position
			return false;
		} // End of try-catch block
	}
	
	/**
	 * Determines if the survey's questions are modified (at least one question should be modified).
	 * 
	 * @return	Boolean indicating if the survey's question are modified.
	 */
	public boolean isModified () {
		// Check if the questions list is valid
		if ( questions == null )
			// The survey cannot be modified
			return false;
		// Otherwise the question list is valid
		// Iterate over all questions
		for ( Questions question : questions )
			// Check if the current question (and its sub-questions (if applicable))
			if ( question.isCompletelyModified () )
				// Indicate that the survey is modified
				return true;
		// Otherwise the survey is NOT modified
		return false;
	}
	
	/**
	 * Gets the percentage value indicating the survey completion.
	 * 
	 * @return	Double hosting the survey completion percentage.
	 */
	public double getCompletionPercentage () {
		try {
			// Compute the number of answered questions
			int answeredQuestions = 0;
			// Iterate over all questions
			for ( Questions question : questions )
				// Check if the question is answered (including its sub question)
				if ( question.isCompletelyAnswered () )
					// The question is answered
					// Increment the number of answered questions
					answeredQuestions ++;
			// Compute and return the completion percentage
			return (double) answeredQuestions / getCount () * 100;
		} catch ( Exception exception ) {
			// Invalid questions list 
			return 0;
		} // End of try-catch block
	}
	
	/**
	 * Determines if the all the survey's questions are fully answered.
	 * 
	 * @return	Boolean indicating if all the survey's questions are fully answered.
	 */
	public boolean isCompleted () {
		// Check if the completion percentage is 100 %
		return getCompletionPercentage () == 100.0 ? true : false;
	}
	
	/**
	 * Determines if the all the survey's <b>FORCED</b> questions are fully answered.
	 * 
	 * @return	Boolean indicating if all the survey's <b>FORCED</b> questions are fully answered.
	 */
	public boolean isForcedCompleted () {
		// Flag used indicate if all forced questions are answered
		boolean isForcedCompeleted = true;
		// Iterate over all questions
		for ( Questions question : questions ) {
			// Determine if the question is NOT forced (including its sub question)
			if ( ! question.isCompletelyForced () )
				// Skip question
				continue;
			// Check if the question is NOT answered (including its sub question)
			if ( ! question.isCompletelyAnswered () ) {
				// The question is NOT answered
				isForcedCompeleted = false;
				// Exit loop
				break;
			} // End if
		} // End for each
		// Return the flag
		return isForcedCompeleted;
	}
	
	/**
	 * Determines if the survey is forced or optional.
	 * 
	 * @return	Boolean indicating if the survey is forced or optional.
	 */
	public boolean isForced () {
		// Check the if forced value of the survey
		if ( survey.getIsForced () == IsForcedUtils.isForced () )
			// Indicate that the survey is forced
			return true;
		// Otherwise the survey is optional
		return false;
	}
	
}