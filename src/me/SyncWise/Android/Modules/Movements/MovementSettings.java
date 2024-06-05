/**
 * Copyright 2015 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Movements;

import java.io.Serializable;

/**
 * Class used to store movement settings.
 * 
 * @author Elias
 *
 */
public class MovementSettings implements Serializable {

	/**
	 * Default serial version ID.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Integer holding the movement type.
	 */
	private final int movementType;
	
	/**
	 * {@link java.lang.Class Class} activity reference.
	 */
	private Class < ? > detailsActivity = MovementDetailsActivity.class;

	/**
	 * Boolean used to indicate if the movement is editable.
	 */
	private boolean isEditable;
	
	/**
	 * Boolean used to indicate if the movement is deletable.
	 */
	private boolean isDeletable = true;
	
	/**
	 * Boolean used to indicate if the movement can have a reason.
	 */
	private boolean useReason;
	
	/**
	 * Boolean used to indicate if the reason is mandatory.
	 */
	private boolean enforceReason;
	
	/**
	 * Boolean used to indicate if the movement can have an expiry date.
	 */
	private boolean useExpiryDate;
	
	/**
	 * Boolean used to indicate if the expiry date is mandatory.
	 */
	private boolean enforceExpiryDate;
	
	/**
	 * Boolean used to determine if the suggestions are displayed.
	 */
	private boolean displaySuggestions;
	
	/**
	 * Boolean used to determine if the suggested reason should be displayed.
	 */
	private boolean displaySuggestionedReason;

	/**
	 * Boolean used to determine if the suggested expiry date should be displayed.
	 */
	private boolean displaySuggestionedExpiryDate;
	
	
	/**
	 * Getter - {@link #displaySuggestionedReason}
	 * 
	 * @return the displaySuggestionedReason
	 */
	public boolean isDisplaySuggestionedReason () {
		return displaySuggestionedReason;
	}

	
	/**
	 * Setter - {@link #displaySuggestionedReason}
	 * 
	 * @param displaySuggestionedReason the displaySuggestionedReason to set
	 */
	public void setDisplaySuggestionedReason ( boolean displaySuggestionedReason ) {
		this.displaySuggestionedReason = displaySuggestionedReason;
	}

	
	/**
	 * Getter - {@link #displaySuggestionedExpiryDate}
	 * 
	 * @return the displaySuggestionedExpiryDate
	 */
	public boolean isDisplaySuggestionedExpiryDate () {
		return displaySuggestionedExpiryDate;
	}

	
	/**
	 * Setter - {@link #displaySuggestionedExpiryDate}
	 * 
	 * @param displaySuggestionedExpiryDate the displaySuggestionedExpiryDate to set
	 */
	public void setDisplaySuggestionedExpiryDate ( boolean displaySuggestionedExpiryDate ) {
		this.displaySuggestionedExpiryDate = displaySuggestionedExpiryDate;
	}

	/**
	 * Boolean used to determine if the suggestions are to be enforced as limits.
	 */
	private boolean enforceSuggestionLimit;
	
	/**
	 * Boolean used to determine if the suggestions are color coded.
	 */
	private boolean colorCodedSuggestions;
	
	/**
	 * Constructor.
	 * 
	 * @param movementType	Integer holding the movement type.
	 */
	public MovementSettings ( final int movementType ) {
		// Initialize variables
		this.movementType = movementType;
	}
	
	/**
	 * Getter - {@link #useReason}
	 * 
	 * @return Boolean used to indicate if the movement can have a reason.
	 */
	public boolean isUseReason () {
		return useReason;
	}
	
	/**
	 * Setter - {@link #useReason}
	 * 
	 * @param useReason Boolean used to indicate if the movement can have a reason.
	 */
	public void setUseReason ( boolean useReason ) {
		this.useReason = useReason;
	}

	
	/**
	 * Getter - {@link #enforceReason}
	 * 
	 * @return Boolean used to indicate if the reason is mandatory.
	 */
	public boolean isEnforceReason () {
		return enforceReason;
	}
	
	/**
	 * Setter - {@link #enforceReason}
	 * 
	 * @param enforceReason Boolean used to indicate if the reason is mandatory.
	 */
	public void setEnforceReason ( boolean enforceReason ) {
		this.enforceReason = enforceReason;
	}
	
	/**
	 * Getter - {@link #useExpiryDate}
	 * 
	 * @return Boolean used to indicate if the movement can have an expiry date.
	 */
	public boolean isUseExpiryDate () {
		return useExpiryDate;
	}
	
	/**
	 * Setter - {@link #useExpiryDate}
	 * 
	 * @param useExpiryDate Boolean used to indicate if the movement can have an expiry date.
	 */
	public void setUseExpiryDate ( boolean useExpiryDate ) {
		this.useExpiryDate = useExpiryDate;
	}
	
	/**
	 * Getter - {@link #enforceExpiryDate}
	 * 
	 * @return Boolean used to indicate if the expiry date is mandatory.
	 */
	public boolean isEnforceExpiryDate () {
		return enforceExpiryDate;
	}
	
	/**
	 * Setter - {@link #enforceExpiryDate}
	 * 
	 * @param enforceExpiryDate Boolean used to indicate if the expiry date is mandatory.
	 */
	public void setEnforceExpiryDate ( boolean enforceExpiryDate ) {
		this.enforceExpiryDate = enforceExpiryDate;
	}
	
	/**
	 * Getter - {@link #detailsActivity}
	 * 
	 * @return {@link java.lang.Class Class} activity reference..
	 */
	public Class < ? > getDetailsActivity () {
		return detailsActivity;
	}
	
	/**
	 * Setter - {@link #detailsActivity}
	 * 
	 * @param detailsActivity {@link java.lang.Class Class} activity reference.
	 */
	public void setDetailsActivity ( final Class < ? > detailsActivity ) {
		this.detailsActivity = detailsActivity;
	}
	
	/**
	 * Getter - {@link #isEditable}
	 * 
	 * @return Boolean used to indicate if the movement is editable.
	 */
	public boolean isEditable () {
		return isEditable;
	}
	
	/**
	 * Setter - {@link #isEditable}
	 * 
	 * @param isEditable Boolean used to indicate if the movement is editable.
	 */
	public void setEditable ( final boolean isEditable ) {
		this.isEditable = isEditable;
	}
	
	/**
	 * Getter - {@link #isDeletable}
	 * 
	 * @return Boolean used to indicate if the movement is deletable.
	 */
	public boolean isDeletable () {
		return isDeletable;
	}
	
	/**
	 * Setter - {@link #isDeletable}
	 * 
	 * @param isEditable Boolean used to indicate if the movement is deletable.
	 */
	public void setDeletable ( final boolean isDeletable ) {
		this.isDeletable = isDeletable;
	}
	
	/**
	 * Getter - {@link #enforceSuggestionLimit}
	 * 
	 * @return Boolean used to determine if the suggestions are to be enforced as limits.
	 */
	public boolean isEnforceSuggestionLimit () {
		return enforceSuggestionLimit;
	}
	
	/**
	 * Setter - {@link #enforceSuggestionLimit}
	 * 
	 * @param isEditable Boolean used to determine if the suggestions are to be enforced as limits.
	 */
	public void setEnforceSuggestionLimit ( final boolean enforceSuggestionLimit ) {
		this.enforceSuggestionLimit = enforceSuggestionLimit;
	}
	
	/**
	 * Getter - {@link #displaySuggestions}
	 * 
	 * @return Boolean used to determine if the suggestions are displayed.
	 */
	public boolean isDisplaySuggestions () {
		return displaySuggestions;
	}
	
	/**
	 * Setter - {@link #displaySuggestions}
	 * 
	 * @param isEditable Boolean used to determine if the suggestions are displayed.
	 */
	public void setDisplaySuggestions ( final boolean displaySuggestions ) {
		this.displaySuggestions = displaySuggestions;
	}
	
	/**
	 * Getter - {@link #colorCodedSuggestions}
	 * 
	 * @return Boolean used to determine if the suggestions are color coded.
	 */
	public boolean isColorCodedSuggestions () {
		return colorCodedSuggestions;
	}
	
	/**
	 * Setter - {@link #colorCodedSuggestions}
	 * 
	 * @param Boolean used to determine if the suggestions are color coded.
	 */
	public void setColorCodedSuggestions ( final boolean colorCodedSuggestions ) {
		this.colorCodedSuggestions = colorCodedSuggestions;
	}
	
	/**
	 * Getter - {@link #movementType}
	 * 
	 * @return Integer holding the movement type.
	 */
	public int getMovementType () {
		return movementType;
	}
	
}