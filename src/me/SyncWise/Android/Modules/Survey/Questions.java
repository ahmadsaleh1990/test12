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

import android.annotation.SuppressLint;
import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.IsForcedUtils;
import me.SyncWise.Android.Database.SurveyImages;
import me.SyncWise.Android.Database.SurveyQuestions;
import me.SyncWise.Android.Database.SurveyQuestionsUtils;
import me.SyncWise.Android.Database.SurveyQuestionsUtils.Type;

/**
 * Class used to define a survey question.<br>
 * This class is different from (yet relies on) the GreenDao entity {@link me.SyncWise.Android.Database.SurveyQuestions SurveyQuestions}.
 * 
 * @author Elias
 *
 */
@SuppressLint("UseSparseArrays")
public class Questions implements Serializable {

	/**
	 * Default serial version ID.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Question GreenDao entity object.
	 */
	private final SurveyQuestions surveyQuestion;
	
	/**
	 * List of images used as display.
	 */
	private ArrayList < SurveyImages > surveyImages;
	
	/**
	 * Map used to host the question's options (if applicable).
	 */
	private HashMap < Integer , String > options;
	
	/**
	 * Map used to host the current question's sub-questions.
	 */
	private HashMap < Integer , Questions > subQuestions;

	/**
	 * Integer holding the currently displayed sub-question ID.
	 */
	private Integer currentSubQuestionID;
	
	/**
	 * Answer object used to host the current question's answer(s).
	 */
	private Answers answer;
	
	/**
	 * Answer object used to host the current question's previous answer(s).<br>
	 * This object is used to determine if the match is modified.
	 */
	private Answers previousAnswer;
	
	/**
	 * Constructor.
	 * 
	 * @param surveyQuestion	Question GreenDao entity object.
	 */
	public Questions ( final SurveyQuestions surveyQuestion ) {
		this.surveyQuestion = surveyQuestion;
	}
	
	/*
	 * Compares this instance with the specified object and indicates if they are equal.
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals ( Object object ) {
		// Flag used to indicate if the objects are equal
		boolean isEqual = false;
		// Check if the object is valid and instance of Call
		if ( object != null && object instanceof Questions )
			// Compare objects
			isEqual = surveyQuestion.getQuestionID () == ( (Questions) object ).getSurveyQuestion ().getQuestionID ();
		// Return flag
		return isEqual;
	}
	
	/*
	 * Returns an integer hash code for this object.
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode () {
		final int prime = 31;
		int result = 1;
		result = prime * result + ( ( surveyQuestion == null ) ? 0 : surveyQuestion.getQuestionID () );
		return result;
	}
	
	/**
	 * Getter - {@link #surveyQuestion}
	 * 
	 * @return	Question GreenDao entity object.
	 */
	public SurveyQuestions getSurveyQuestion () {
		return surveyQuestion;
	}
	
	/**
	 * Setter - {@link #surveyImages}
	 * 
	 * @return	Question GreenDao entity object.
	 */
	public void setSurveyImages ( final ArrayList < SurveyImages > surveyImages ) {
		this.surveyImages = surveyImages;
	}
	
	/**
	 * Getter - {@link #surveyImages}
	 * 
	 * @return	List of images used as display.
	 */
	public ArrayList < SurveyImages > getSurveyImages () {
		return surveyImages;
	}
	
	/**
	 * Getter - {@link #options}
	 * 
	 * @return Map used to host the question's options (if applicable).
	 */
	public HashMap < Integer , String > getOptions () {
		return options;
	}
	
	/**
	 * Setter - {@link #options}
	 * 
	 * @param options Map used to host the question's options (if applicable).
	 */
	public void setOptions ( final HashMap < Integer , String > options ) {
		this.options = options;
	}

	/**
	 * Determines if the current question is a sub-question.
	 * 
	 * @return	Boolean indicating if the current question is a sub-question.
	 */
	public boolean isSubQuestion () {
		// Determine if the question has a parent
		if ( surveyQuestion.getParentQuestionID () == null )
			// Indicate that the question is not a sub question
			return false;
		// Otherwise, indicate that the question is a sub question
		return true;
	}
	
	/**
	 * Determines if the current question is forced (or optional).
	 * 
	 * @return Boolean indicating if the current question is forced or optional.
	 */
	public boolean isForced () {
		// Determine if the current question is forced or not
		return surveyQuestion.getIsForced () == IsForcedUtils.isForced () ? true : false;
	}
	
	/**
	 * Determines if the current question is forced (or optional).<br>
	 * This state does not only rely on the question itself, but may also rely on its sub-question (if applicable).
	 * 
	 * @return	Boolean indicating if the current question is forced or optional.
	 */
	public boolean isCompletelyForced () {
		// Determine if the forced attribute depends on the question only or include its sub-questions states
		if ( subQuestions == null || subQuestions.isEmpty () )
			// The attribute depends on the question only
			return isForced ();
		// Otherwise the attribute also depends on the sub-question
		// Retrieve a reference to the applicable sub-question
		Questions subQuestion = getApplicableSubQuestion ();
		// Check if the applicable sub question is valid
		if ( subQuestion == null )
			// The attribute depends on the question only
			return isForced ();
		// Otherwise return the sub question forced state
		// The attribute depends on the sub-question only
		return subQuestion.isForced ();
	}
	
	/**
	 * Determines if the current question can have multiple answers.
	 * 
	 * @return	Boolean indicating if the current question has multiple answers or not.
	 */
	public boolean isMultipleAnswers () {
		// Check if the question is an option question type
		if ( surveyQuestion.getQuestionTypeID () != 3 )
			// The question is not a multiple answers question
			return false;
		// Otherwise the question is an option question type
		// Check the is multiple answers field
		return surveyQuestion.getIsMultipleAnswer () == SurveyQuestionsUtils.isMultipleAnswers () ? true : false;
	}
	
	/**
	 * Determines if the current question is answered.
	 * 
	 * @return	Boolean indicating if the current question is answered or not.
	 */
	public boolean isAnswered () {
		// Determine if the current question has a valid answer
		if ( answer == null )
			// Question is not answered
			return false;
		// Otherwise determine if the answer is valid
		return answer.getCount () == 0 ? false : true;
	}
	
	/**
	 * Determines if the current question is answered, including its sub-question (if applicable).
	 * 
	 * @return	Boolean indicating if the current question (including its sub-question (if applicable)) is answered or not.
	 */
	public boolean isCompletelyAnswered () {
		// Determine if the current question has a valid sub-question list
		if ( subQuestions == null || subQuestions.isEmpty () )
			// Determine if the question itself is answered
			return isAnswered ();
		// Otherwise the sub-question list is valid
		// Retrieve the question's applicable sub-question (if any)
		Questions subQuestion = getApplicableSubQuestion ();
		// Check if the sub-question is valid
		if ( subQuestion == null )
			// No applicable sub-question
			// Determine if the question itself is answered
			return isAnswered ();
		// Otherwise the sub-question is valid
		// Return if the sub-question (including its sub-question (if applicable)) is answered
		return subQuestion.isCompletelyAnswered ();
	}
	
	/**
	 * Getter - {@link #answer}
	 * 
	 * @return Answer object used to host the current question's answer(s).
	 */
	public Answers getAnswer () {
		return answer;
	}
	
	/**
	 * Setter - {@link #answer}
	 * 
	 * @param answer Answer object used to host the current question's answer(s).
	 */
	public void setAnswer ( final Answers answer ) {
		this.answer = answer;
	}

	/**
	 * Getter - {@link #previousAnswer}
	 * 
	 * @return Answer Answer object used to host the current question's previous answer(s).
	 */
	public Answers getPreviousAnswer () {
		return previousAnswer;
	}
	
	/**
	 * Setter - {@link #previousAnswer}
	 * 
	 * @param previousAnswer Answer object used to host the current question's previous answer(s).
	 */
	public void setPreviousAnswer ( final Answers previousAnswer ) {
		this.previousAnswer = previousAnswer;
	}
	
	/**
	 * Indicates whether the question is valid by checking its answer(s).
	 * 
	 * @return	Boolean indicating if the question's answer is valid.
	 */
	public boolean isValid () {
		// Check if the answer is valid
		if ( ! isAnswered () )
			// Indicate that the question is valid
			return true;
		// Determine the question type
		switch ( surveyQuestion.getQuestionTypeID () ) {
		case SurveyQuestionsUtils.Type.NUMBER:
			// Declare and initialize an integer used to host the answer
			int _answer = 0;
			try {
				// Retrieve the answer
				_answer = Integer.parseInt ( answer.getAnswer () );
			} catch ( Exception exception ) {
				// Invalid number
				// Indicate that the question is NOT valid
				return false;
			} // End of try-catch block
			// Determine if the question has a maximum and the answer does not exceed it
			if ( surveyQuestion.getMaximumValue () != null && _answer > surveyQuestion.getMaximumValue () )
				// Indicate that the question is NOT valid
				return false;
			// Determine if the question has a maximum and the answer does not exceed it
			if ( surveyQuestion.getMinimumValue () != null && _answer < surveyQuestion.getMinimumValue () )
				// Indicate that the question is NOT valid
				return false;
			// Otherwise, indicate that the question is valid
			return true;
		default:
			// Indicate that the question is valid
			return true;
		} // End switch
	}
	
	/**
	 * Determines if the current question is valid by checking its answer(s), including its sub-question answer(s) (if applicable).
	 * 
	 * @return	Boolean indicating if the question's answer is valid (including its sub-question answer(s) (if applicable)).
	 */
	public boolean isCompletelyValid () {
		// Determine if the current question has a valid sub-question list
		if ( subQuestions == null || subQuestions.isEmpty () )
			// Determine if the question itself is valid
			return isValid ();
		// Otherwise the sub-question list is valid
		// Retrieve the question's applicable sub-question (if any)
		Questions subQuestion = getApplicableSubQuestion ();
		// Check if the sub-question is valid
		if ( subQuestion == null )
			// No applicable sub-question
			// Determine if the question itself is valid
			return isValid ();
		// Otherwise the sub-question is valid
		// Determine if the question itself is valid
		if ( isValid () )
			// Return if the sub-question (including its sub-question (if applicable)) is valid
			return subQuestion.isCompletelyValid ();
		// Otherwise the question itself is not valid
		return false;
	}
	
	/**
	 * Gets a validity warning message for the current question based on its question type.<br>
	 * <b>Note : </b> To actually determine whether the question is valid or not, use the {@link #isValid()} method.
	 * 
	 * @param context	The application context.
	 * @return	String holding the validity warning message.
	 */
	public String getValidityWarningMessage ( final Context context ) {
		// Determine the question type
		switch ( surveyQuestion.getQuestionTypeID () ) {
		case SurveyQuestionsUtils.Type.NUMBER:
			return AppResources.getInstance ( context ).getString ( context , R.string.survey_question_numeric_invalid_message );
		default:
			return "";
		} // End switch
	}
	
	/**
	 * Gets a validity warning message for the current question or its sub-question, depending on which question is not valid.<br>
	 * The validity warning is based on the invalid question's type.<br>
	 * <b>Note : </b> To actually determine whether the question is valid or not, use the {@link #isValid()} method.
	 * 
	 * @param context	The application context.
	 * @return	String holding the validity warning message.
	 */
	public String getRecursiveValidityWarningMessage ( final Context context ) {
		// Determine if the current question has a valid sub-question list
		if ( subQuestions == null || subQuestions.isEmpty () )
			// Return the current question's validity warning message
			return getValidityWarningMessage ( context );
		// Otherwise the sub-question list is valid
		// Retrieve the question's applicable sub-question (if any)
		Questions subQuestion = getApplicableSubQuestion ();
		// Check if the sub-question is valid
		if ( subQuestion == null )
			// No applicable sub-question
			// Return the current question's validity warning message
			return getValidityWarningMessage ( context );
		// Otherwise the sub-question is valid
		// Determine if the question itself is valid
		if ( isValid () )
			// Return the sub-question (including its sub-question (if applicable)) validity message
			return subQuestion.getRecursiveValidityWarningMessage ( context );
		// Otherwise return the current question's validity warning message
		return getValidityWarningMessage ( context );
	}
	
	/**
	 * Determines if the current question uses a keyboard or not.
	 * 
	 * @return Boolean indicating if the current question uses a keyboard or not.
	 * @see #getEditTextId()
	 * @see #isKeyboardDependant()
	 */
	public boolean usesKeyboard () {
		// Determine the question type
		switch ( surveyQuestion.getQuestionTypeID () ) {
		case SurveyQuestionsUtils.Type.FREE_TEXT:
		case SurveyQuestionsUtils.Type.NUMBER:
			return true;
		default:
			return false;
		} // End switch
	}
	
	/**
	 * Determines if the current question depends on the keyboard or not.
	 * 
	 * @return	Boolean indicating if the current question depends on the keyboard or not.
	 * @see #getEditTextId()
	 * @see #usesKeyboard()
	 */
	public boolean isKeyboardDependant () {
		// Determine the question type
		switch ( surveyQuestion.getQuestionTypeID () ) {
		case SurveyQuestionsUtils.Type.FREE_TEXT:
			return true;
		default:
			return false;
		} // End switch
	}
	
	/**
	 * Gets the edit text view ID for the current question, or {@code NULL} if this question does not have any edit text.
	 * 
	 * @return	The edit text view ID, or {@code NULL} if none is found.
	 * @see #usesKeyboard()
	 * @see #isKeyboardDependant()
	 */
	public Integer getEditTextId () {
		// Determine the question type
		switch ( surveyQuestion.getQuestionTypeID () ) {
		case SurveyQuestionsUtils.Type.FREE_TEXT:
			return R.id.edittext_free_text_answer;
		case SurveyQuestionsUtils.Type.NUMBER:
			return R.id.edit_text_number;
		default:
			return null;
		} // End switch
	}
	
	/**
	 * Determines if the question is modified by comparing {@link #answer} and {@link #previousAnswer}.
	 * 
	 * @return	Boolean indicating if the question is modified or not.
	 */
	public boolean isModified () {
		// Check if both answers are NULL
		if ( answer == null && previousAnswer == null )
			// Answer is not modified
			return false;
		// Otherwise check if one of the answers is NULL
		else if ( answer == null || previousAnswer == null )
			// Answer is modified
			return true;
		// Otherwise both are not NULL
		// Check if both answers have the same size
		else if ( answer.getCount () != previousAnswer.getCount () )
			// Answer is modified
			return true;
		// Otherwise both have the same size
		// Compare both answers
		try {
			int size = answer.getCount ();
			// Iterate over the current answers
			for ( int i = 0 ; i < size ; i ++ ) {
				// Retrieve the current answer ID
				Integer answerID = answer.getAnswerID ( i );
				// Retrieve the current answer object
				String answerObject = answer.getAnswer ( i );
				// Retrieve the previous answer object
				String previousAnswerObject = previousAnswer.getAnswers ().get ( answerID );
				// Compare current answer with the previous answer
				if ( ! previousAnswerObject.equals ( answerObject ) )
					// Answer is modified
					return true;
			} // End for loop
		} catch ( Exception exception ) {
			// Answer is modified
			return true;
		} // End of try-catch block
		// Otherwise, the answer is not modified
		return false;
	}
	
	/**
	 * Determines if the question is modified by comparing {@link #answer} and {@link #previousAnswer}, including its sub-question (if applicable).
	 * 
	 * @return	Boolean indicating if the question (including its sub-question (if applicable)) is modified or not.
	 */
	public boolean isCompletelyModified () {
		// Determine if the current question has a valid sub-question list
		if ( subQuestions == null || subQuestions.isEmpty () )
			// Determine if the question itself is modified
			return isModified ();
		// Otherwise the sub-question list is valid
		// Retrieve the question's applicable sub-question (if any)
		Questions subQuestion = getApplicableSubQuestion ();
		// Check if the sub-question is valid
		if ( subQuestion == null )
			// No applicable sub-question
			// Determine if the question itself is modified
			return isModified ();
		// Otherwise the sub-question is valid
		// Determine if the question itself is modified
		if ( isModified () )
			// Indicate that the question is modified
			return true;
		// Return if the sub-question (including its sub-question (if applicable)) is modified
		return subQuestion.isModified ();
	}
	
	/**
	 * Initializes the sub-questions list if necessary.
	 */
	private void initializeSubQuestions () {
		// Check if the sub-questions list is valid
		if ( subQuestions == null )
			// Initialize the sub-questions list
			subQuestions = new HashMap < Integer , Questions > ();
	}
	
	/**
	 * Getter - {@link #subQuestions}
	 * 
	 * @return List used to host the current question's sub-questions.
	 */
	public HashMap < Integer , Questions > getSubQuestions () {
		return subQuestions;
	}
	
	/**
	 * Setter - {@link #subQuestions}
	 * 
	 * @param subQuestions List used to host the current question's sub-questions.
	 */
	public void setSubQuestions ( final HashMap < Integer , Questions > subQuestions ) {
		this.subQuestions = subQuestions;
	}
	
	/**
	 * Adds a sub-question to current question, assigned to the provided answer ID.
	 * 
	 * @param answerID	Integer holding the assigned answer ID.
	 * @param subQuestion	Question object to add as sub-question to the current question.
	 */
	public void addSubQuestion ( final Integer answerID , final Questions subQuestion ) {
		// Check if the provided sub-question is valid
		if ( subQuestion != null ) {
			// Initialize the sub-questions list if needed
			initializeSubQuestions ();
			// Add the sub-question to the list
			subQuestions.put ( answerID , subQuestion );
		} // End if
	}
	
	/**
	 * Gets the applicable sub question for the current question.<br>
	 * Multiple factors are checked / required to have a valid sub question :
	 * <ul>
	 * <li>The question should be answered.</li>
	 * <li>The question should have a valid list of sub-questions.</li>
	 * <li>The question answer should have an assigned sub-question <em>(if applicable).</em></li>
	 * </ul>
	 * 
	 * @return	Applicable question object, or {@code NULL} if none.
	 */
	public Questions getApplicableSubQuestion () {
		// Check if the sub-questions list is valid
		// OR if the the current question is answered
		if ( ! isAnswered () || subQuestions == null || subQuestions.isEmpty () )
			// No applicable sub question
			return null;
		// Otherwise, look for the appropriate sub question
		return subQuestionLookUp ();
	}
	
	/**
	 * Method used to look for the appropriate sub question based on the parent question (and parent answer if applicable).
	 * 
	 * @return	Appropriate question object based on the parent question (and parent answer if applicable), or {@code NULL} if invalid.
	 */
	private Questions subQuestionLookUp () {
		try {
			// Determine the current question's type
			switch ( surveyQuestion.getQuestionTypeID () ) {
			case Type.FREE_TEXT:
			case Type.NUMBER:
			case Type.DATE:
			case Type.SELECTION_ITEMS:
				// Free text, number, date and selection of items questions can have one sub-question
				return subQuestions.get ( subQuestions.keySet ().toArray () [ 0 ] );
			case Type.OPTION:
				// Determine if the question can have multiple answers
				if ( isMultipleAnswers () )
					// Option questions with multiple answers can have one sub-question
					return subQuestions.get ( subQuestions.keySet ().toArray () [ 0 ] );
				// Otherwise the question has a single answer
				// Retrieve and return the sub-question assigned to current question answer ID
				return subQuestions.get ( answer.getAnswerID () );
			} // End switch
			// Invalid question type
			return null;
		} catch ( Exception exception ) {
			// Invalid sub-question list and/or question type and/or question answer
			return null;
		} // End of try-catch block
	}
	
	/**
	 * Getter - {@link #currentSubQuestionID}
	 * 
	 * @return Integer holding the currently displayed sub-question ID.
	 */
	public Integer getCurrentSubQuestionID () {
		return currentSubQuestionID;
	}
	
	/**
	 * Setter - {@link #currentSubQuestionID}
	 * 
	 * @param currentSubQuestionID Integer holding the currently displayed sub-question ID.
	 */
	public void setCurrentSubQuestionID ( Integer currentSubQuestionID ) {
		this.currentSubQuestionID = currentSubQuestionID;
	}
	
}