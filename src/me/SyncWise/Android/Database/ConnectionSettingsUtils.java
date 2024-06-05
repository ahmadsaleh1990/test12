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

import android.annotation.SuppressLint;

/**
 * Utilities class for generated GreenDao Entities, holding helper methods for additional functionality related to the {@link me.SyncWise.Android.Database.ConnectionSettings ConnectionSettings} objects.
 * 
 * @author Elias
 *
 */
@SuppressLint("DefaultLocale") public class ConnectionSettingsUtils {

	/**
	 * String holding the set method.<br>
	 * This method is used to send data to the server.
	 */
	private static final String SET_METHOD_PATH = "setMethod.php";
	
	/**
	 * String holding the get method.<br>
	 * This method is used to receive data from the server.
	 */
	private static final String GET_METHOD_PATH = "getMethod.php";
	
	/**
	 * String holding the connect method.<br>
	 * This method is used to check connectivity to the server.
	 */
	private static final String CONNECT_METHOD_PATH = "connect.php";
	/**
	 * String holding the connect method.<br>
	 * This method is used to check connectivity to the server.
	 */
	private static final String LISENCE_METHOD_PATH = "IsLisenced.php";
	/**
	 * String holding the register method.<br>
	 * This method is used to send the device's GCM registration Id to the server.
	 */
	private static final String REGISTER_METHOD_PATH = "register.php";
	
	/**
	 * String holding the upload files to server.<br>
	 */
	private static final String UPLOAD_FILES = "uploadFiles.php";
	
	/**
	 * String holding the upload files to server.<br>
	 */
	private static final String CONFIRM_FILES = "confirmFiles.php";
	
	/**
	 * Helper class used to define the various values of a connection setting type.
	 * 
	 * @author Elias
	 *
	 */
	public static class Type {
		
		/**
		 * String holding the GPRS type.
		 */
		public static final String GPRS = "TABLET_GPRS";
		
		/**
		 * String holding the WIRELESS type.
		 */
		public static final String WIRELESS = "TABLET_WIRELESS";
		
	}
	
	/**
	 * Gets the set method path.<br>
	 * If a url is provided, the method path is properly appended to the url.
	 * 
	 * @param url	String holding the url, if the set method path should be appended.
	 * @return	The set method path, appended to the url (if provided).
	 */
	public static String getSetMethodPath ( final String url ) {
		// Check if the url is valid
		if ( url == null )
			return SET_METHOD_PATH;
		// Otherwise append the set method path to the url
		// Check if a front slash is already added
		return url+ ( url.endsWith ( "/" ) ? "" : "/" ) + SET_METHOD_PATH; 
	}
	
	/**
	 * Gets the get method path.<br>
	 * If a url is provided, the method path is properly appended to the url.
	 * 
	 * @param url	String holding the url, if the get method path should be appended.
	 * @return	The get method path, appended to the url (if provided).
	 */
	public static String getGetMethodPath ( final String url ) {
		// Check if the url is valid
		if ( url == null )
			return GET_METHOD_PATH;
		// Otherwise append the get method path to the url
		// Check if a front slash is already added
		return url+ ( url.endsWith ( "/" ) ? "" : "/" ) + GET_METHOD_PATH; 
	}
	
	/**
	 * Gets the connect method path.<br>
	 * If a url is provided, the method path is properly appended to the url.
	 * 
	 * @param url	String holding the url, if the connect method path should be appended.
	 * @return	The connect method path, appended to the url (if provided).
	 */
	public static String getConnectMethodPath ( final String url ) {
		// Check if the url is valid
		if ( url == null )
			return CONNECT_METHOD_PATH;
		// Otherwise append the connect method path to the url
		// Check if a front slash is already added
		return url + ( url.endsWith ( "/" ) ? "" : "/" ) + CONNECT_METHOD_PATH; 
	}
	
	/**
	 * Gets the register method path.<br>
	 * If a url is provided, the method path is properly appended to the url.
	 * 
	 * @param url	String holding the url, if the register method path should be appended.
	 * @return	The register method path, appended to the url (if provided).
	 */
	public static String getRegisterMethodPath ( final String url ) {
		// Check if the url is valid
		if ( url == null )
			return REGISTER_METHOD_PATH;
		// Otherwise append the register method path to the url
		// Check if a front slash is already added
		return url + ( url.endsWith ( "/" ) ? "" : "/" ) + REGISTER_METHOD_PATH; 
	}
	
	public static String uploadFiles ( final String url ) {
		// Check if the url is valid
		if ( url == null )
			return UPLOAD_FILES;
		// Otherwise append the XML handler method path to the url
		// Check if a front slash is already added
		return url  + ( url.endsWith ( "/" ) ? "" : "/" ) + UPLOAD_FILES; 
	}
	
	public static String confirmFiles ( final String url ) {
		// Check if the url is valid
		if ( url == null )
			return CONFIRM_FILES;
		// Otherwise append the XML handler method path to the url
		// Check if a front slash is already added
		return url  + ( url.endsWith ( "/" ) ? "" : "/" ) + CONFIRM_FILES; 
	}
	/**
	 * Gets the connect method path.<br>
	 * If a url is provided, the method path is properly appended to the url.
	 * 
	 * @param url	String holding the url, if the connect method path should be appended.
	 * @return	The connect method path, appended to the url (if provided).
	 */
	public static String getConnectMethodPath1 ( final String url ) {
		// Check if the url is valid
		if ( url == null )
			return CONNECT_METHOD_PATH;
		// Otherwise append the connect method path to the url
		// Check if a front slash is already added
		return url  + ( url.endsWith ( "/" ) ? "" : "/" ) + "IsConnected.php"; 
	}
	/**
	 * Gets the connect method path.<br>
	 * If a url is provided, the method path is properly appended to the url.
	 * 
	 * @param url	String holding the url, if the connect method path should be appended.
	 * @return	The connect method path, appended to the url (if provided).
	 */
	public static String getIsLisencedMethodPath ( final String url ) {
		// Check if the url is valid
		if ( url == null )
			return LISENCE_METHOD_PATH;
		// Otherwise append the connect method path to the url
		// Check if a front slash is already added
		return url + ( url.endsWith ( "/" ) ? "" : "/" ) + LISENCE_METHOD_PATH; 
	}
}