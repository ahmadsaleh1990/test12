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
 * Utilities class for generated GreenDao Entities, holding helper methods for additional functionality related to the is processed field.
 * 
 * @author Elias
 *
 */
public class IsProcessedUtils {
	
	/**
	 * Returns the integer value for web processed status.
	 * 
	 * @return	Integer holding the processed value.
	 */
	public static int isWebProcessed () {
		return 0;
	}
	
	/**
	 * Returns the integer value for not sync status.
	 * 
	 * @return	Integer holding the processed value.
	 */
	public static int isNotSync () {
		return 1;
	}
	
	/**
	 * Returns the integer value for sync status.
	 * 
	 * @return	Integer holding the processed value.
	 */
	public static int isSync () {
		return 2;
	}
	public static int isRejected () {
		return 0;
	}
}