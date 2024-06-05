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
 * Utilities class for generated GreenDao Entities, holding helper methods for additional functionality related to the status field.
 * 
 * @author Elias
 *
 */
public class StatusUtils {

	/**
	 * Returns the integer value for available status.
	 * 
	 * @return	Integer holding the status value.
	 * @see StatusUtils#isDeleted()
	 */
	public static int isAvailable () {
		return 1;
	}
	
	/**
	 * Returns the integer value for deleted status.
	 * 
	 * @return	Integer holding the status value.
	 * @see StatusUtils#isAvailable()
	 */
	public static int isDeleted () {
		return 2;
	}
	
	/**
	 * Returns the integer value for an editable cycle status.
	 * 
	 * @return	Integer holding the status value.
	 * @see StatusUtils#isNotEditable()
	 */
	public static int isEditable () {
		return 1;
	}
	
	/**
	 * Returns the integer value for a fixed cycle status.
	 * 
	 * @return	Integer holding the status value.
	 * @see StatusUtils#isEditable()
	 */
	public static int isNotEditable () {
		return 2;
	}
	
	/**
	 * Returns the integer value for an active user / client status.
	 * 
	 * @return	Integer holding the status value.
	 * @see StatusUtils#isInactive()
	 * @see StatusUtils#isBlocked()
	 */
	public static int isActive () {
		return 1;
	}
	
	/**
	 * Returns the integer value for an inactive user / client status.
	 * 
	 * @return	Integer holding the status value.
	 * @see StatusUtils#isActive()
	 * @see StatusUtils#isBlocked()
	 */
	public static int isInactive () {
		return 2;
	}
	
	/**
	 * Returns the integer value for a blocked user / client status.
	 * 
	 * @return	Integer holding the status value.
	 * @see StatusUtils#isInactive()
	 * @see StatusUtils#isActive()
	 */
	public static int isBlocked () {
		return 3;
	}
	
	/**
	 * Returns the integer value for a not sent message status.
	 * 
	 * @return	Integer holding the status value.
	 * @see StatusUtils#isUnread()
	 * @see StatusUtils#isRead()
	 */
	public static int isNotSent () {
		return 1;
	}
	
	/**
	 * Returns the integer value for an unread message status.
	 * 
	 * @return	Integer holding the status value.
	 * @see StatusUtils#isNotSent()
	 * @see StatusUtils#isRead()
	 */
	public static int isUnread () {
		return 2;
	}
	
	/**
	 * Returns the integer value for a read message status.
	 * 
	 * @return	Integer holding the status value.
	 * @see StatusUtils#isNotSent()
	 * @see StatusUtils#isUnread()
	 */
	public static int isRead () {
		return 3;
	}
	
}