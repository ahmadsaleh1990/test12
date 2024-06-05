/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Call;

import me.SyncWise.Android.Modules.MenuList.MenuItem;

/**
 * Class used to represent a call menu item in {@link me.SyncWise.Android.Modules.Call.CallMenuActivity CallMenuActivity}.<br>
 * In addition to all the features of the {@link me.SyncWise.Android.Modules.MenuList.MenuItem MenuItem} class, this class has a :
 * <ul>
 * <li>A flag indicating if the action is performed or not.</li>
 * <li>A baguette message if the call action is a success ({@link java.lang.String String}).</li>
 * <li>A baguette message if the call action is a failure ({@link java.lang.String String}).</li>
 * </ul>
 * 
 * @author Elias
 *
 */
public class CallMenuItem extends MenuItem {

	/**
	 * Flag indicating whether the action is performed or not.
	 */
	private boolean actionPerformed;
	
	/**
	 * baguette success (string) resource id.
	 */
	private Integer baguetteSuccessId;
	
	/**
	 * baguette failure (string) resource id.
	 */
	private Integer baguetteFailureId;
	
	/**
	 * Flag used to indicate if the current call menu item is disabled.
	 */
	private boolean disabled;
	
	/*
	 * Boolean used to determine if the icon is dimmed or not.
	 *
	 * @see me.SyncWise.Android.Modules.MenuList.MenuItem#getDimIcon()
	 */
	@Override
	public boolean getDimIcon () {
		// Return the dim icon flag or the disabled item state
		return super.getDimIcon () || disabled;
	}
	
	/**
	 * Setter - {@link #actionPerformed}
	 * 
	 * @param actionPerformed	Boolean indicating whether the action is performed or not.
	 * @return	Generic THIS (self) - Reference to the current {@link me.SyncWise.Android.Modules.Call.CallMenuItem CallMenuItem} object.
	 */
	public CallMenuItem setActionPerformed ( final boolean actionPerformed ) {
		// Set the action performed flag
		this.actionPerformed = actionPerformed;
		// Allow for cascaded methods calls
		return this;
	}
	
	/**
	 * Getter - {@link #actionPerformed}
	 * 
	 * @return	Boolean indicating whether the action is performed or not.
	 */
	public boolean getActionPerformed () {
		// Return the action performed flag
		return actionPerformed;
	}
	
	/**
	 * Setter - {@link #baguetteSuccessId}
	 *
	 * @param baguetteSuccessId	baguette success (string) resource id.
	 * @return	Generic THIS (self) - Reference to the current {@link me.SyncWise.Android.Modules.Call.CallMenuItem CallMenuItem} object.
	 */
	public CallMenuItem setBaguetteSuccessId ( final Integer baguetteSuccessId ) {
		// Set the baguette success resource id
		this.baguetteSuccessId = baguetteSuccessId;
		// Allow for cascaded methods calls
		return this;
	}
	
	/**
	 * Getter - {@link #baguetteSuccessId}
	 * 
	 * @return	baguette success (string) resource id.
	 */
	public Integer getBaguetteSuccessId () {
		// Return the baguette success resource id
		return baguetteSuccessId;
	}
	
	/**
	 * Setter - {@link #baguetteFailureId}
	 *
	 * @param baguetteFailureId	baguette failure (string) resource id.
	 * @return	Generic THIS (self) - Reference to the current {@link me.SyncWise.Android.Modules.Call.CallMenuItem CallMenuItem} object.
	 */
	public CallMenuItem setBaguetteFailreId ( final Integer baguetteFailureId ) {
		// Set the baguette failure resource id
		this.baguetteFailureId = baguetteFailureId;
		// Allow for cascaded methods calls
		return this;
	}
	
	/**
	 * Getter - {@link #baguetteFailureId}
	 * 
	 * @return	baguette failure (string) resource id.
	 */
	public Integer getBaguetteFailreId () {
		// Return the baguette failure resource id
		return baguetteFailureId;
	}
	
	/**
	 * Setter - {@link #disabled}
	 *
	 * @param disabled	Flag used to indicate if the current call menu item is disabled.
	 * @return	Generic THIS (self) - Reference to the current {@link me.SyncWise.Android.Modules.Call.CallMenuItem CallMenuItem} object.
	 */
	public CallMenuItem setDisabled ( final boolean disabled ) {
		// Set the baguette disabled flag
		this.disabled = disabled;
		// Allow for cascaded methods calls
		return this;
	}
	
	/**
	 * Getter - {@link #disabled}
	 * 
	 * @return	Flag used to indicate if the current call menu item is disabled.
	 */
	public boolean isDisabled () {
		// Return the baguette disabled flag
		return disabled;
	}
	
}