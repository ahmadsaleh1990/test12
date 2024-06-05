/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android;

/**
 * Helper Class used to manage the application's notifications IDs.<br>
 * The main purpose of this class is to maintain a unique identifier for notifications within the application.<br><br>
 * <h1>NOTE :</h1><br>
 * It is the <b><em>developper</em></b>'s responsibility to maintain each notification identifier unique within the application !
 * 
 * @author Elias
 *
 */
public class AppNotifications {

	/**
	 * Returns the identifier for the <b>{@code GPS DISABLED}</b> notification.
	 * 
	 * @return	A notification identifier, (supposedly) unique within the application.
	 */
	public static int gpsDisabled () {
		return 0;
	}
	
	/**
	 * Returns the identifier for the <b>{@code Network DISABLED}</b> notification.
	 * 
	 * @return	A notification identifier, (supposedly) unique within the application.
	 */
	public static int networkDisabled () {
		return 1;
	}
	
	/**
	 * Returns the identifier for the <b>{@code Device Owned}</b> notification.
	 * 
	 * @return	A notification identifier, (supposedly) unique within the application.
	 */
	public static int deviceOwned () {
		return 10;
	}
	
	/**
	 * Returns the identifier for the <b>{@code New Message}</b> notification.
	 * 
	 * @return	A notification identifier, (supposedly) unique within the application.
	 */
	public static int newMessage () {
		return 20;
	}
	
}