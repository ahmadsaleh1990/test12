/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Database;

/**
 * Utilities class for generated GreenDao Entities, holding helper methods for additional functionality related to the {@link me.SyncWise.Android.Database.SurveyQuestions SurveyQuestions} objects.
 * 
 * @author Elias
 *
 */
public class SurveyQuestionsUtils {

	/**
	 * Helper class used to define the various values of a question type.
	 * 
	 * @author Elias
	 *
	 */
	public static class Type {
		
		/**
		 * Integer holding the free text question type
		 */
		public static final int FREE_TEXT = 1;
		
		/**
		 * Integer holding the number question type
		 */
		public static final int NUMBER = 2;
		
		/**
		 * Integer holding the option question type.<br>
		 * Whether the options question can have single or multiple answers is defined by another field (not the question type).
		 */
		public static final int OPTION = 3;
		
		/**
		 * Integer holding the date question type
		 */
		public static final int DATE = 4;
		
		/**
		 * Integer holding the selection of items question type
		 */
		public static final int SELECTION_ITEMS = 5;
		
	}
	
	/**
	 * Returns the integer value for not is multiple answers.
	 * 
	 * @return	Integer holding the multiple answers value.
	 */
	public static int isMultipleAnswers () {
		return 1;
	}
	
	/**
	 * Returns the integer value for is not multiple answers.
	 * 
	 * @return	Integer holding the multiple answers value.
	 */
	public static int isNotMultipleAnswers () {
		return 2;
	}
	
}