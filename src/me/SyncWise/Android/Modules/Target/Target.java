/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Target;

/**
 * Interface used to implement the target coverage.
 * 
 * @author Elias
 *
 */
public interface Target {

	/**
	 * Gets the target subject code.
	 * 
	 * @return	String holding the target subject code.
	 */
	public String getSubjectCode ();
	
	/**
	 * Gets the target subject description.
	 * 
	 * @return	String holding the target subject description.
	 */
	public String getSubjectDescription ();
	
	/**
	 * Gets the target subject icon ID.
	 * 
	 * @return	Integer holding the target icon resource ID.
	 */
	public int getSubjectIconID ();
	
	/**
	 * Gets the target description.
	 * 
	 * @return	String holding the target description.
	 */
	public String getTargetDescription ();
	
	/**
	 * Gets the target sub subject code.
	 * 
	 * @return	String holding the target sub subject code.
	 */
	public String getSubSubjectCode ();
	
	/**
	 * Gets the target sub subject description.
	 * 
	 * @return	String holding the target sub subject description.
	 */
	public String getSubSubjectDescription ();
	
	/**
	 * Gets the target sub subject icon ID.
	 * 
	 * @return	Integer holding the target icon resource ID.
	 */
	public int getSubSubjectIconID ();
	
	/**
	 * Gets the target coverage completion description.
	 * 
	 * @return	String holding the target coverage completion description.
	 */
	public String getCoverageDescription ();
	
	/**
	 * Gets the target coverage completion percentage.
	 * 
	 * @return	Integer holding the target coverage percentage.
	 */
	public int getCoverage ();
	
}