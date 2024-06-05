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
 * Utilities class for generated GreenDao Entities, holding helper methods for additional functionality related to the {@link me.SyncWise.Android.Database.Surveys Surveys} objects.
 * 
 * @author Elias
 *
 */
public class SurveysUtils {
	
	/**
	 * The folder name used to host the survey images.
	 */
	public static final String PICTURES_DIRECTORY = "SurveyImages";

	/**
	 * Helper class used to define the various values of a survey type.
	 * 
	 * @author Elias
	 *
	 */
	public static class Type {
		
		/**
		 * Integer holding the client survey type
		 */
		public static final int CLIENT_SURVEY = 1;
		
		/**
		 * Integer holding the user survey type
		 */
		public static final int USER_SURVEY = 2;
		
		/**
		 * Integer holding the supervisor survey type
		 */
		public static final int SUPERVISOR_SURVEY = 3;
		
		/**
		 * Integer holding the scoring sheet type
		 */
		public static final int SCORING_SHEET = 4;
		
	}
	
}