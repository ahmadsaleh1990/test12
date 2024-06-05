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
 * Utilities class for generated GreenDao Entities, holding helper methods for additional functionality related to the {@link me.SyncWise.Android.Database.TargetAssignments TargetAssignments} objects.
 * 
 * @author Elias
 *
 */
public class TargetAssignmentsUtils {

	/**
	 * Helper class used to define the various values of an target assignment.
	 * 
	 * @author Elias
	 *
	 */
	public static class Type {
		
		/**
		 * Integer holding the user target assignment type
		 */
		public static final int USER = 1;
		
		/**
		 * Integer holding the users group target assignment type
		 */
		public static final int GROUP_USERS = 2;
		
		/**
		 * Integer holding the client target assignment type
		 */
		public static final int CLIENT = 3;
		
		/**
		 * Integer holding the client properties target assignment type
		 */
		public static final int CLIENT_PROPERTIES = 4;
		
	}
	
}