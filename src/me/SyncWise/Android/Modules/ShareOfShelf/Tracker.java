/**
 * Copyright 2015 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.ShareOfShelf;

import java.io.Serializable;

import android.content.Context;
import android.text.TextUtils;

import me.SyncWise.Android.Database.Brands;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.PermissionsUtils;

/**
 * Class used to represent share of shelf tracker.
 * 
 * @author Elias
 *
 */
public class Tracker implements Serializable {

	/**
	 * Default serial version ID.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Reference to the brand object.
	 */
	private final Brands brand;
	
	/**
	 * Double used to host the previous share of shelf value.
	 */
	private Double previousShareOfShelf;
	
	/**
	 * Double used to host the current share of shelf value.
	 */
	private Double currentShareOfShelf;
	
	/**
	 * Double used to host the previous competitor share of shelf value.
	 */
	private Double previousCompetitorShareOfShelf;
	
	/**
	 * Double used to host the current competitor share of shelf value.
	 */
	private Double currentCompetitorShareOfShelf;
	
	/**
	 * Double used to host the previous category value.
	 */
	private Double previousCategory;

	/**
	 * Double used to host the current category value.
	 */
	private Double currentCategory;
	
	/**
	 * String used to host the previous notes.
	 */
	private String previousNotes = "";

	/**
	 * String used to host the current notes.
	 */
	private String currentNotes = "";
	
	/**
	 * Double used to host  the BDA meter.
	 */
	private double bdaMeter;
	
	/**
	 * Double used to host the BDA percentage.
	 */
	private double bdaPercentage;
	
	/**
	 * Getter - {@link #brand}
	 * 
	 * @return	Reference to the brand object.
	 */
	public Brands getBrand () {
		return brand;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param brand	Reference to the brand object.
	 */
	public Tracker ( final Brands brand ) {
		// Initialize the attributes
		this.brand = brand;
	}

	/**
	 * Getter - {@link #previousShareOfShelf}
	 * 
	 * @return	Double used to host the previous share of shelf value.
	 */
	public Double getPreviousShareOfShelf () {
		return previousShareOfShelf;
	}

	/**
	 * Setter - {@link #previousShareOfShelf}
	 * 
	 * @param previousShareOfShelf	Double used to host the previous share of shelf value.
	 */
	public void setPreviousShareOfShelf ( final Double previousShareOfShelf ) {
		if ( previousShareOfShelf >= 0 && previousShareOfShelf <= 100 )
			this.previousShareOfShelf = previousShareOfShelf;
	}

	/**
	 * Getter - {@link #currentShareOfShelf}
	 * 
	 * @return	Double used to host the current share of shelf value.
	 */
	public Double getCurrentShareOfShelf () {
		return currentShareOfShelf;
	}

	/**
	 * Setter - {@link #currentShareOfShelf}
	 * 
	 * @param currentShareOfShelf	Double used to host the current share of shelf value.
	 */
	public void setCurrentShareOfShelf ( final Double currentShareOfShelf ) {
		this.currentShareOfShelf = currentShareOfShelf;
	}
	
	/**
	 * Getter - {@link #previousCompetitorShareOfShelf}
	 * 
	 * @return	Double used to host the previous competitor share of shelf value.
	 */
	public Double getPreviousCompetitorShareOfShelf () {
		return previousCompetitorShareOfShelf;
	}

	/**
	 * Setter - {@link #previousCompetitorShareOfShelf}
	 * 
	 * @param previousShareOfShelf	Double used to host the previous competitor share of shelf value.
	 */
	public void setPreviousCompetitorShareOfShelf ( final Double previousCompetitorShareOfShelf ) {
		if ( previousCompetitorShareOfShelf >= 0 && previousCompetitorShareOfShelf <= 100 )
			this.previousCompetitorShareOfShelf = previousCompetitorShareOfShelf;
	}

	/**
	 * Getter - {@link #currentCompetitorShareOfShelf}
	 * 
	 * @return	Double used to host the current competitor share of shelf value.
	 */
	public Double getCurrentCompetitorShareOfShelf () {
		return currentCompetitorShareOfShelf;
	}

	/**
	 * Setter - {@link #currentCompetitorShareOfShelf}
	 * 
	 * @param currentShareOfShelf	Double used to host the current competitor share of shelf value.
	 */
	public void setCurrentCompetitorShareOfShelf ( final Double currentCompetitorShareOfShelf ) {
		this.currentCompetitorShareOfShelf = currentCompetitorShareOfShelf;
	}

	/**
	 * Getter - {@link #previousCategory}
	 * 
	 * @return	Double used to host the previous category value.
	 */
	public Double getPreviousCategory () {
		return previousCategory;
	}

	/**
	 * Setter - {@link #previousCategory}
	 * 
	 * @param previousCategory	Double used to host the previous category value.
	 */
	public void setPreviousCategory ( final Double previousCategory ) {
		if ( previousCategory >= 0 && previousCategory <= 100 )
			this.previousCategory = previousCategory;
	}

	/**
	 * Getter - {@link #currentCategory}
	 * 
	 * @return	Double used to host the current category value.
	 */
	public Double getCurrentCategory () {
		return currentCategory;
	}

	/**
	 * Setter - {@link #currentCategory}
	 * 
	 * @param currentCategory	Double used to host the current category value.
	 */
	public void setCurrentCategory ( final Double currentCategory ) {
		this.currentCategory = currentCategory;
	}
	
	/**
	 * Getter - {@link #previousNotes}
	 * 
	 * @return	String used to host the previous notes.
	 */
	public String getPreviousNotes () {
		return previousNotes;
	}

	/**
	 * Setter - {@link #previousNotes}
	 * 
	 * @param currentNotes	String used to host the previous notes.
	 */
	public void setPreviousNotes ( final String previousNotes ) {
		this.previousNotes = previousNotes;
	}
	
	/**
	 * Getter - {@link #currentNotes}
	 * 
	 * @return	String used to host the current notes.
	 */
	public String getCurrentNotes () {
		return currentNotes;
	}

	/**
	 * Setter - {@link #currentNotes}
	 * 
	 * @param currentNotes	String used to host the current notes.
	 */
	public void setCurrentNotes ( final String currentNotes ) {
		this.currentNotes = currentNotes;
	}
	
	/**
	 * Returns the applied share of shelf.<br>
	 * If there is a current value, it is returned first, otherwise the previous value is returned.<br>
	 * If none is provided, the value zero is returned.
	 * 
	 * @return	The applicable share of shelf value.
	 */
	public Double getShareOfShelf () {
		return	currentShareOfShelf != null ? currentShareOfShelf : previousShareOfShelf != null ? previousShareOfShelf : 0;
	}
	
	/**
	 * Returns the applied competitor share of shelf.<br>
	 * If there is a current value, it is returned first, otherwise the previous value is returned.<br>
	 * If none is provided, the value zero is returned.
	 * 
	 * @return	The applicable competitor share of shelf value.
	 */
	public Double getCompetitorShareOfShelf () {
		return	currentCompetitorShareOfShelf != null ? currentCompetitorShareOfShelf : previousCompetitorShareOfShelf != null ? previousCompetitorShareOfShelf : 0;
	}
	
	/**
	 * Returns the applied category.<br>
	 * If there is a current value, it is returned first, otherwise the previous value is returned.<br>
	 * If none is provided, the value zero is returned.
	 * 
	 * @return	The applicable category value.
	 */
	public Double getCategory () {
		return	currentCategory != null ? currentCategory : previousCategory != null ? previousCategory : 0;
	}
	
	/**
	 * Returns the applied notes.<br>
	 * If there is a current value, it is returned first, otherwise the previous value is returned.
	 * 
	 * @return	The applicable notes.
	 */
	public String getNotes () {
		return	TextUtils.isEmpty ( currentNotes.trim () ) ? previousNotes : currentNotes;
	}
	
	/**
	 * Indicates whether the current share of shelf tracker is modified or not.
	 * 
	 * @return Flag used to determine if the current share of shelf tracker is modified.
	 */
	public boolean isModified (Context c, String userCode,String companyCode) {
		// Declare flag
		boolean modified = false;
		// Check if the current category is valid
		if ( currentCategory != null )
			// Make sure the previous value is valid
			// And compare both values
			if ( previousCategory != null && ! previousCategory.equals ( currentCategory ) )
				// Indicate that the tracker is modified
				modified = true;
		// Check if the current share of shelf is valid
		if ( currentShareOfShelf != null )
			// Make sure the previous value is valid
			// And compare both values
			if ( previousShareOfShelf != null && ! previousShareOfShelf.equals ( currentShareOfShelf ) )
				// Indicate that the tracker is modified
				modified = true;
		// Check if the current competitor share of shelf is valid
		if ( currentCompetitorShareOfShelf != null )
			// Make sure the previous value is valid
			// And compare both values
			if ( previousCompetitorShareOfShelf != null && ! previousCompetitorShareOfShelf.equals ( currentCompetitorShareOfShelf ) )
				// Indicate that the tracker is modified
				modified = true;
		// Compare both previous and current notes
		if ( ! previousNotes.trim ().equalsIgnoreCase ( currentNotes.trim () ) )
			// Indicate that the tracker is modified
			modified = true;
 
		if(PermissionsUtils.getFillAllShareOfShalve(c, userCode, companyCode)){
		if(currentCategory ==null || currentCategory==0)
			modified=false;
		if(currentShareOfShelf ==null || currentShareOfShelf==0)
			modified=false;
		if(currentCompetitorShareOfShelf ==null || currentCompetitorShareOfShelf==0)
			modified=false;
		}
		// Return the flag content
		return modified;
	}
	
	/**
	 * Getter - {@link #bdaMeter}
	 * 
	 * @return	Double used to host the BDA meter.
	 */
	public double getBDAMeter () {
		return bdaMeter;
	}
	
	/**
	 * Setter - {@link #bdaMeter}
	 * 
	 * @param dbaMeter	Double used to host the BDA meter.
	 */
	public void setBDAMeter ( final double bdaMeter ) {
		this.bdaMeter = bdaMeter;
	}
	
	/**
	 * Getter - {@link #bdaPercentage}
	 * 
	 * @return	Double used to host the BDA percentage.
	 */
	public double getBDAPercentage () {
		return bdaPercentage;
	}
	
	/**
	 * Setter - {@link #bdaPercentage}
	 * 
	 * @param dbaMeter	Double used to host the BDA percentage.
	 */
	public void setBDAPercentage ( final double bdaPercentage ) {
		this.bdaPercentage = bdaPercentage;
	}
	
}