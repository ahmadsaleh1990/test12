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
 * Utilities class for generated GreenDao Entities, holding helper methods for additional functionality related to the source field.
 * 
 * @author Elias
 *
 */
public class SourceUtils {

	/**
	 * Returns the integer value for the back end source.
	 * 
	 * @return	Integer holding the source value.
	 * @see SourceUtils#isFromDevice()
	 */
	public static int isFromBackEnd () {
		return 1;
	}
	
	/**
	 * Returns the integer value for the device source.
	 * 
	 * @return	Integer holding the source value.
	 * @see SourceUtils#isFromBackEnd()
	 */
	public static int isFromDevice () {
		return 2;
	}
	
}