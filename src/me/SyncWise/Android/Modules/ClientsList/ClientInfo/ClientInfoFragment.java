/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.ClientsList.ClientInfo;

import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Fragment used to represent a modular section to display the client information.
 * 
 * @author Elias
 *
 */
public class ClientInfoFragment extends ListFragment {

	/**
	 * Bundle key used to put/retrieve data.
	 */
	public static final String LAYOUT = ClientInfoFragment.class.getName () + ".LAYOUT";
	
	/*
	 * Called to do initial creation of a fragment.
	 *
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate ( Bundle savedInstanceState ) {
		// Superclass method call
	    super.onCreate ( savedInstanceState );
	    // Report that this fragment would like to participate in populating the options menu
        setHasOptionsMenu ( true );
	}
	
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
		// Define the empty list view
		getListView ().setEmptyView ( getView ().findViewById ( R.id.empty_list_view ) );
		// Display empty list label
		( (TextView) getView ().findViewById ( R.id.empty_list_view ) ).setText ( AppResources.getInstance ( getActivity () ).getString ( getActivity () , R.string.empty_client_info_list_label ) );
		// Set the list adapter
		setListAdapter ( ( (ClientInfoActivity) getActivity () ).getClientInfoAdapter ( null , null ) );
	}
    
}