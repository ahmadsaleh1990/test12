/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Objective;

import java.io.Serializable;

/**
 * Class used to represent an objective note.
 * 
 * @author Elias
 *
 */
public class ObjectiveNote implements Serializable {

	/**
	 * Default serial version ID.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Long used to host the objective ID.
	 */
	private final long objectiveID;
	
	/**
	 * String used to host the objective note.
	 */
	private final String note;
	
	/**
	 * Integer holding the start selection index.
	 */
	private final int selectionStart;
	
	/**
	 * Integer holding the end selection index.
	 */
	private final int selectionEnd;
	
	/**
	 * Getter - {@link #selectionStart}
	 * 
	 * @return Integer holding the start selection index.
	 */
	public int getSelectionStart () {
		return selectionStart;
	}
	
	/**
	 * Getter - {@link #selectionEnd}
	 * 
	 * @return Integer holding the end selection index.
	 */
	public int getSelectionEnd () {
		return selectionEnd;
	}
	
	/**
	 * Getter - {@link #objectiveID}
	 * 
	 * @return Long hosting the objective ID.
	 */
	public long getObjectiveID () {
		return objectiveID;
	}
	
	/**
	 * Getter - {@link #note}
	 * 
	 * @return String hosting the objective note.
	 */
	public String getNote () {
		return note;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param objectiveID	Long hosting the objective ID.
	 * @param note	String hosting the objective note.
	 * @param selectionStart	Integer holding the start selection index.
	 * @param selectionEnd	Integer holding the end selection index.
	 */
	public ObjectiveNote ( final long objectiveID , final String note , final int selectionStart , final int selectionEnd ) {
		this.objectiveID = objectiveID;
		this.note = note;
		this.selectionStart = selectionStart;
		this.selectionEnd = selectionEnd;
	}
	
}