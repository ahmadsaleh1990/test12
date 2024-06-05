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

import java.lang.reflect.Field;
import java.util.List;

import me.SyncWise.Android.Utilities.AppFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

/**
 * General Implementation of {@link android.support.v4.app.FragmentPagerAdapter FragmentPagerAdapter} that represents each page as a Fragment that is persistently kept in the fragment manager as long as the user can return to the page.<br>
 * The supplied fragment class to the constructor should be a direct (or indirect) child of {@link android.support.v4.app.Fragment Fragment}.
 * 
 * @author Elias
 *
 */
public class AppFragmentPagerAdapter extends FragmentPagerAdapter {
	
	/**
	 * Array of {@link java.lang.Class Class} {@link android.support.v4.app.Fragment Fragment} references.
	 */
	private final Class < ? > fragments [];
	
	/**
	 * Array of {@link me.SyncWise.Android.Utilities.AppFragment AppFragment} objects holding the fragments details.
	 */
	private final List < AppFragment > fragmentsDetails;
	
	/**
	 * Constructor.
	 * 
	 * @param fragmentManager	Interface for interacting with Fragment objects inside of an Activity.
	 * @param fragmentsDetails	Array of {@link java.lang.Class Class} {@link android.support.v4.app.Fragment Fragment} references.
	 * @param fragments	Array of {@link java.lang.Class Class} objects holding the fragments references.
	 */
	public AppFragmentPagerAdapter ( FragmentManager fragmentManager , final List < AppFragment > fragmentsDetails , final Class < ? > fragments [] ) {
		// Superclass method call
		super ( fragmentManager );
		// Check if both arrays are valid
		if ( fragmentsDetails == null || fragments == null ) {
			// Invalid arguments
			this.fragmentsDetails = null;
			this.fragments = null;
			return;
		} // End if
		// Otherwise both arrays are valid
		// The arrays should not be empty
		if ( fragmentsDetails.isEmpty () || fragments.length == 0 ) {
			// Invalid arguments
			this.fragmentsDetails = null;
			this.fragments = null;
			return;
		} // End if
		// The array of fragments should either have the same size of the fragments details array or have exactly one element
		if ( fragments.length != fragmentsDetails.size () && fragments.length != 1 ) {
			// Invalid arguments
			this.fragmentsDetails = null;
			this.fragments = null;
			return;
		} // End if
		
		// Otherwise, the arguments and their sizes are valid
		// Determine if the provided classes are all fragments
		// Flag used to indicate if a fragment is an indirect child of Fragment
		boolean isFragment = false;
		// Flag used to indicate if a fragment has a bundle key called LAYOUT used to send the layout as argument
		boolean hasLayoutBundleKey = false;
		// Iterate over all fragments
		for ( Class < ? > fragment : fragments ) {
			// Clear the flags
			isFragment = false;
			hasLayoutBundleKey = false;
			// Get the class's parent
			Class < ? > parent = fragment.getSuperclass ();
			// Iterate over all the class's parents
			while ( parent != null ) {
				// Check if the parent is Fragment
				if ( parent.equals ( Fragment.class ) ) {
					// Set the flag
					isFragment = true;
					// Exit loop
					break;
				} // End if
				// Otherwise the parent is not Fragment, keep looping
				parent = parent.getSuperclass ();
			} // End while loop
			// Check if the current fragment is an indirect child of Fragment
			if ( ! isFragment )
				// Invalid argument
				break;
			try {
				// Retrieve a reference to the bundle key for LAYOUT
				Field field = fragment.getField ( "LAYOUT" );
				// Make sure the field is a string
				if ( ! field.getType ().equals ( String.class ) )
					// Invalid field type
					throw new NoSuchFieldException ();
				// Otherwise the field is valid
				hasLayoutBundleKey = true;
			} catch ( NoSuchFieldException exception ) {
				// The field is not declared or is not a string, do nothing
			} // End of try-catch exception
			// Check if the current fragment has a bundle key for LAYOUT
			if ( ! hasLayoutBundleKey )
				// Invalid argument
				break;
		} // End for each
		// Initialize attributes
		this.fragmentsDetails = ( isFragment && hasLayoutBundleKey ? fragmentsDetails : null );
		this.fragments = ( isFragment && hasLayoutBundleKey ? fragments : null );
	}
	
	/*
	 * Return the Fragment associated with a specified position.
	 *
	 * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
	 */
	@Override
	public Fragment getItem ( int position ) {
		try {
			// Compute the fragment index in the array of fragments
			int index = ( fragments.length == 1 ? 0 : position );
			// Declare and initialize a new fragment
			Fragment fragment = (Fragment) fragments [ index ].newInstance ();
			// Declare and initialize a bundle to supply arguments to the fragment
			Bundle args = new Bundle ();
			// Retrieve a reference to the bundle key of LAYOUT
			Field bundleKey = fragments [ index ].getField ( "LAYOUT" );
			// Store the fragment layout resource id
			args.putInt ( (String) bundleKey.get ( null ) , fragmentsDetails.get ( position ).getLayout () );
			// Set the fragment arguments
			fragment.setArguments ( args );
			// Return the new fragment
			return fragment;
		} catch ( Exception exception ) {
			// Invalid fragment
			// This exception should not occur due to the integrity check in the constructor
			return null;
		} // End of try-catch block
	}
	
	/*
	 * This method may be called by the ViewPager to obtain a title string to describe the specified page.
	 *
	 * @see android.support.v4.view.PagerAdapter#getPageTitle(int)
	 */
    @Override
    public CharSequence getPageTitle ( int position ) {
    	try {
    		// Return the appropriate fragment title
    		return fragmentsDetails.get ( position ).getTitle ();
    	} catch ( Exception exception ) {
			// Invalid array or size
    		return "";
		} // End of try-catch block
    }
	
    /*
     * Return the number of views available.
     *
     * @see android.support.v4.view.PagerAdapter#getCount()
     */
    @Override
    public int getCount () {
    	try {
    		// Return the number of fragments
    		return fragmentsDetails.size ();
    	} catch ( Exception exception ) {
			// Invalid array
    		return 0;
		} // End of try-catch block
    }
	
    /**
     * Gets the fragment's name having the specified index.<br>
     * {@code NULL} is returned if the container view, the fragment's index 
     * 
     * @param view	A reference to the {@link android.support.v4.view.ViewPager ViewPager} Layout manager view.
     * @param index	Integer holding the fragment's index.
     * @return	String holding the fragment's name, or {@code NULL} if it is invalid.
     */
    public String getFragmentName ( final View view , final int index ) {
    	// Check if the view is valid
    	if ( view == null )
    		// Invalid view
    		return null;
    	// Otherwise determine if the index is valid
    	if ( fragmentsDetails != null && index >= 0 && index < fragmentsDetails.size () )
    		// Compute and return the fragment name
    		return "android:switcher:" + view.getId () + ":" + index;
    	// Otherwise the index is invalid
    	return null;
    }
    
}