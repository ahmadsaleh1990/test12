/**
 * Copyright 2014 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.VisitNotes;

import java.io.Serializable;

/**
 * Class used to represent a merchandising picture.
 * 
 * @author Elias
 *
 */
public class Picture implements Serializable {
	
	/**
	 * Default serial version ID.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Long used to host the picture visit ID
	 */
	private final Long visitID;
	
	/**
	 * Integer used to host the picture line ID.
	 */
	private final int lineID;
	
	/**
	 * String used to host the picture path on disk.
	 */
	private final String path;
	
	/**
	 * String used to host the temporary picture path on disk.
	 */
	private final String tempPath;
	
	/**
	 * Constructor.
	 * 
	 * @param visitID	Long used to host the picture visit ID.
	 * @param lineID	Integer used to host the picture line ID.
	 * @param path	String used to host the picture path on disk.
	 * @param tempPath	String used to host the temporary picture path on disk.
	 */
	public Picture ( final Long visitID , final int lineID , final String path , final String tempPath ) {
		// Set the picture id and path
		this.visitID = visitID;
		this.lineID = lineID;
		this.path = path;
		this.tempPath = tempPath;
	}
	
	/**
	 * Getter - {@link #visitID}
	 * 
	 * @return	Long used to host the picture visit ID.
	 */
	public Long getVisitID () {
		return visitID;
	}
	
	/**
	 * Getter - {@link #lineID}
	 * 
	 * @return	Integer used to host the picture line ID.
	 */
	public int getLineID () {
		return lineID;
	}
	
	/**
	 * Getter - {@link #path}
	 * 
	 * @return	String used to host the picture path on disk.
	 */
	public String getPath () {
		return path;
	}
	
	/**
	 * Getter - {@link #tempPath}
	 * 
	 * @return	String used to host the temporary picture path on disk.
	 */
	public String getTempPath () {
		return tempPath;
	}
	
}