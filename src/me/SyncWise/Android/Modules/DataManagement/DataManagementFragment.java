/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.DataManagement;

import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Fragment used to represent a modular section for a database export or import.
 * 
 * @author Elias
 *
 */
public class DataManagementFragment extends Fragment {

	/**
	 * Bundle key used to put/retrieve data.
	 */
	public static final String LAYOUT = DataManagementFragment.class.getName () + ".LAYOUT";
	
	/*
	 * Called to have the fragment instantiate its user interface view.
	 *
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
    @Override
    public View onCreateView ( LayoutInflater inflater , ViewGroup container , Bundle savedInstanceState ) {
    	// Inflate and return the fragment's view
    	return inflater.inflate ( getArguments ().getInt ( LAYOUT ) , container , false );
    }
    
    /*
     * Called when the fragment's activity has been created and this fragment's view hierarchy instantiated.
     *
     * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
     */
	@Override
	public void onActivityCreated ( Bundle savedInstanceState ) {
		// Superclass method call
		super.onActivityCreated ( savedInstanceState );
	    // Determine the fragment layout
	    if ( getArguments ().getInt ( LAYOUT ) == R.layout.data_import_fragment ) {
	    	// Display the get button label
	    	( (Button) getView ().findViewById ( R.id.button_get_data ) ).setText ( AppResources.getInstance ( getActivity () ).getString ( getActivity () , R.string.import_label ) );
	    } // End if
	    else if ( getArguments ().getInt ( LAYOUT ) == R.layout.data_export_fragment ) {
	    	// Display the set button label
	    	( (Button) getView ().findViewById ( R.id.button_set_data ) ).setText ( AppResources.getInstance ( getActivity () ).getString ( getActivity () , R.string.export_label ) );
		} // End else if
	}
	
}