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
import java.util.Calendar;
import java.util.HashMap;

import android.annotation.SuppressLint;

/**
 * Class used to define a survey answer.<br>
 * The class is customized in order to satisfy and meet all the questions types and conditions.
 * 
 * @author Elias
 *
 */
@SuppressLint("UseSparseArrays")
public class Answers implements Serializable {
	
	/**
	 * Default serial version ID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Map used to host question answer(s).
	 */
	private HashMap < Integer , String > answers;
	
	/**
	 * Map used to host the selection code(s).
	 */
	private HashMap < Integer , String > selectionCodes;
	
	/**
	 * Calendar object hosting the question's answer date.
	 */
	private Calendar answerDate;

	/**
	 * Constructor.
	 */
	public Answers () {
		// Initialize the answer date
		answerDate = Calendar.getInstance ();
	}
	
	/**
	 * Constructor.
	 * 
	 * @param answerDate	Calendar object hosting the question's answer date.
	 */
	public Answers ( final Calendar answerDate ) {
		// Initialize attribute
		this.answerDate = answerDate;
	}
	
	/**
	 * Initializes the answers map if necessary.
	 */
	private void initializeAnswers () {
		// Check if the answers map is valid
		if ( answers == null )
			// Initialize the answers map
			answers = new HashMap < Integer , String > ();
	}
	
	/**
	 * Initializes the selection codes map if necessary.
	 */
	private void initializeSelectionCodes () {
		// Check if the selection codes map is valid
		if ( selectionCodes == null )
			// Initialize the selection codes map
			selectionCodes = new HashMap < Integer , String > ();
	}
	
	/**
	 * Returns the total number of question answers.
	 * 
	 * @return	Integer holding the number of answers.
	 */
	public int getCount () {
		try {
			// Return the answers map size
			return answers.size ();
		} catch ( Exception exception ) {
			// Invalid answers map
			return 0;
		} // End of try-catch block
	}
	
	/**
	 * Gets the question answer, assuming the question can have only one answer.
	 * 
	 * @return	String holding the question answer.
	 */
	public String getAnswer () {
		// Return the (assumed) only answer in the map
		return getAnswer ( 0 );
	}
	
	/**
	 * Gets the question answer at the required position.
	 * 
	 * @param position	Integer holding the required answer position.
	 * @return	String holding the question answer at the indicated position.
	 */
	public String getAnswer ( final int position ) {
		try {
			// Return the answer at the indicated position
			return answers.get ( answers.keySet ().toArray () [ position ] );
		} catch ( Exception exception ) {
			// Invalid answers map
			return null;
		} // End of try-catch block
	}
	
	/**
	 * Gets the question selection code at the required position.
	 * 
	 * @param position	Integer holding the required selection code position.
	 * @return	String holding the question selection code at the indicated position.
	 */
	public String getSelectionCode ( final int position ) {
		try {
			// Return the selection code at the indicated position
			return selectionCodes.get ( selectionCodes.keySet ().toArray () [ position ] );
		} catch ( Exception exception ) {
			// Invalid selection codes map
			return null;
		} // End of try-catch block
	}
	
	/**
	 * Gets the question answer ID, assuming the question can have only one answer.
	 * 
	 * @return	Integer holding the answer ID.
	 */
	public Integer getAnswerID () {
		// Return the (assumed) only answer ID in the map
		return getAnswerID ( 0 );
	}
	
	/**
	 * Gets the question answer ID at the required position.
	 * 
	 * @param position	Integer holding the required answer ID position.
	 * @return	Integer holding the answer ID at the indicated position.
	 */
	public Integer getAnswerID ( final int position ) {
		try {
			// Return the answer at the indicated position
			return (Integer) answers.keySet ().toArray () [ position ];
		} catch ( Exception exception ) {
			// Invalid answers map
			return null;
		} // End of try-catch block
	}
	
	/**
	 * Getter - {@link #answers}
	 * 
	 * @return Map hosting question answer(s).
	 */
	public HashMap < Integer , String > getAnswers () {
		return answers;
	}
	
	/**
	 * Setter - {@link #answers}<br>
	 * The answer date is not modified.
	 * 
	 * @param answers Map hosting question answer(s).
	 */
	public void setAnswers ( final HashMap < Integer , String > answers ) {
		this.answers = answers;
	}
	
	/**
	 * Getter - {@link #selectionCodes}
	 * 
	 * @return Map hosting the selection code(s).
	 */
	public HashMap < Integer , String > getSelectionCodes () {
		return selectionCodes;
	}
	
	/**
	 * Setter - {@link #selectionCodes}
	 * 
	 * @param selectionCodes Map hosting the selection code(s).
	 */
	public void setSelectionCodes ( HashMap < Integer , String > selectionCodes ) {
		this.selectionCodes = selectionCodes;
	}

	/**
	 * Stores the question answer (assuming the question can have only one answer).<br>
	 * The answer date is refreshed.
	 * 
	 * @param answer	The question's answer to store.
	 */
	public void answer ( final String answer ) {
		// Check if the provided answer is valid
		if ( answer != null ) {
			// Initialize the answers map if needed
			initializeAnswers ();
			// Add the answer to the map
			answers.put ( 0 , answer );
			// Initialize the answer date
			answerDate = Calendar.getInstance ();
		} // End if
	}
	
	/**
	 * Stores the question answer.<br>
	 * The answer date is refreshed.
	 * 
	 * @param answerID	The question's answer ID to store.
	 * @param answer	The question's answer to store.
	 */
	public void answer ( final Integer answerID , final String answer ) {
		// Check if the provided answer, along with its ID, is valid
		if ( answer != null && answerID != null ) {
			// Initialize the answers map if needed
			initializeAnswers ();
			// Add the answer to the map
			answers.put ( answerID , answer );
			// Initialize the answer date
			answerDate = Calendar.getInstance ();
		} // End if
	}
	
	/**
	 * Stores the question answer along with its selection code.<br>
	 * The answer date is refreshed.
	 * 
	 * @param answerID	The question's answer ID to store.
	 * @param answer	The question's answer to store.
	 * @param selectionCode	The question's selection code to store.
	 */
	public void answer ( final Integer answerID , final String answer , final String selectionCode ) {
		// Check if the provided answer, along with its ID and selection code, is valid
		if ( selectionCode != null && answer != null && answerID != null ) {
			// Initialize the answers map if needed
			initializeAnswers ();
			// Initialize the selection codes map if needed
			initializeSelectionCodes ();
			// Add the answer to the map
			answers.put ( answerID , answer );
			// Add the selection code to the map
			selectionCodes.put ( answerID , selectionCode );
			// Initialize the answer date
			answerDate = Calendar.getInstance ();
		} // End if
	}
	
	/**
	 * Removes the question answer and/or selection code for the specified answer ID.<br>
	 * The answer date is refreshed.
	 * 
	 * @param answerID	The question's answer ID to remove.
	 */
	public void remove ( final Integer answerID ) {
		// Check if the provided answer ID is valid
		if ( answerID == null )
			// Invalid answer ID
			return;
		// Check if the answers map is valid
		if ( answers != null && ! answers.isEmpty () ) {
			// Remove the answer from the map
			answers.remove ( answerID );
			// Initialize the answer date
			answerDate = Calendar.getInstance ();
		} // End if
		// Check if the selection codes map is valid
		if ( selectionCodes != null && ! selectionCodes.isEmpty () ) {
			// Remove the selection code from the map
			selectionCodes.remove ( answerID );
			// Initialize the answer date
			answerDate = Calendar.getInstance ();
		} // End if
	}
	
	/**
	 * Removes / clears all the answers and/or selection codes.<br>
	 * The answer date is not modified.
	 */
	public void clear () {
		// Check if the answers map is valid
		if ( answers != null )
			// Clear the answers map
			answers.clear ();
		// Check if the selection codes map is valid
		if ( selectionCodes != null )
			// Clear the selection codes map
			selectionCodes.clear ();
	}
	
	/**
	 * Getter - {@link #answerDate}
	 * 
	 * @return Calendar object hosting the question's answer date.
	 */
	public Calendar getAnswerDate () {
		return answerDate;
	}
	
}