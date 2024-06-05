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
 * Utilities class for generated GreenDao Entities, holding helper methods for additional functionality related to the {@link me.SyncWise.Android.Database.ObjectiveAchievementsUtils ObjectiveAchievementsUtils} objects.
 * 
 * @author Elias
 *
 */
public class ObjectiveAchievementsUtils {

	/**
	 * Gets the maximum number of characters allowed concerning the objective achievement note.
	 * 
	 * @return	Integer holding the maximum number of allowed characters.
	 */
	public static int getNoteMaxLength () {
		return 500;
	}
	
	/**
	 * Helper class used to define the various values of an objective achievement type.
	 * 
	 * @author Elias
	 *
	 */
	public static class Type {
		
		/**
		 * Integer holding the user objective achievement type.
		 */
		public static final int USER = 1;
		
		/**
		 * Integer holding the client objective achievement type.
		 */
		public static final int CLIENT = 2;
		
	}

	/**
	 * Helper class used to define the various values of an objective achievement status.
	 * 
	 * @author Elias
	 *
	 */
	public static class Status {
		
		/**
		 * Integer holding the completed objective achievement status.
		 */
		public static final int COMPLETED = 1;
		
		/**
		 * Integer holding the cancelled objective achievement status.
		 */
		public static final int CANCELLED = 2;
		
	}
	
}