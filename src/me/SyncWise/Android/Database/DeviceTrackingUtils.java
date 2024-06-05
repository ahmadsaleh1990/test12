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
 * Utilities class for generated GreenDao {@link me.SyncWise.Android.Database.DeviceTracking DeviceTracking} Entities, holding helper methods for additional functionality related to the {@link DeviceTracking#setIsGPSEnabled(Integer) DeviceTracking.setIsGPSEnabled} and {@link DeviceTracking#setIsNetworkEnabled(Integer) DeviceTracking.setIsNetworkEnabled} fields.
 * 
 * @author Elias
 *
 */
public class DeviceTrackingUtils {

	/**
	 * Returns the integer value for enabled GPS status.
	 * 
	 * @return	Integer holding the GPS status value.
	 */
	public static int enabledGPS () {
		return 1;
	}
	
	/**
	 * Returns the integer value for disabled GPS status.
	 * 
	 * @return	Integer holding the GPS status value.
	 */
	public static int disabeldGPS () {
		return 2;
	}
	
	/**
	 * Returns the integer value for enabled network status.
	 * 
	 * @return	Integer holding the network status value.
	 */
	public static int enabledNetwork () {
		return 1;
	}
	
	/**
	 * Returns the integer value for disabled network status.
	 * 
	 * @return	Integer holding the network status value.
	 */
	public static int disabeldNetwork () {
		return 2;
	}
	
}
