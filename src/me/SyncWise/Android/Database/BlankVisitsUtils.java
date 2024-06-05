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
 * Utilities class for generated GreenDao Entities, holding helper methods for additional functionality related to the {@link me.SyncWise.Android.Database.BlankVisits BlankVisits} objects.
 * 
 * @author Elias
 *
 */
public class BlankVisitsUtils {

	/**
	 * Gets the maximum number of characters allowed concerning the blank visit note.
	 * 
	 * @return	Integer holding the maximum number of allowed characters.
	 */
	public static int getNoteMaxLength () {
		return 500;
	}
	
}