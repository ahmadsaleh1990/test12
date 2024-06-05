/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.ClientsList;

import java.util.List;

import me.SyncWise.Android.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * A concrete BaseAdapter that is backed by an array of {@link me.SyncWise.Android.Modules.ClientsList.ClientFilter ClientFilter} objects.
 * 
 * @author Elias
 *
 */
public class ClientFilterAdapter extends BaseExpandableListAdapter {
	
	/**
	 * {@link android.view.LayoutInflater} used to instantiate a layout XML file into its corresponding View objects.
	 */
	private final LayoutInflater layoutInflater;
	
	/**
	 * XML Layout (layout) resource id for group views.
	 */
	private final int groupLayout;
	
	/**
	 * XML Layout (layout) resource id for child views.
	 */
	private final int childLayout;
	
	/**
	 * List of {@link me.SyncWise.Android.Modules.ClientsList.ClientFilter ClientFilter} objects used to define the client filters along with their selections.
	 */
	private final List < ClientFilter > clientFilters;
	
	/**
	 * Nested class implementation of the view holder pattern used to keep references to children views in order to avoid unnecessary calls to {@link android.view.View#findViewById(int) findViewById(id)} on each row.
	 * 
	 * @author Elias
	 *
	 */
	public static class GroupViewHolder {
		public TextView description;
	}
	
	/**
	 * Nested class implementation of the view holder pattern used to keep references to children views in order to avoid unnecessary calls to {@link android.view.View#findViewById(int) findViewById(id)} on each row.
	 * 
	 * @author Elias
	 *
	 */
	public static class ChildViewHolder {
		public TextView value;
		public CheckBox checkBox;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param context	The application context.
	 * @param groupLayout	XML Layout (layout) resource id for group views.
	 * @param childLayout	XML Layout (layout) resource id for child views.
	 * @param clientFilters	List of {@link me.SyncWise.Android.Modules.ClientsList.ClientFilter ClientFilter} objects holding the various client filters and their selections.
	 */
	public ClientFilterAdapter ( final Context context , final int groupLayout , final int childLayout , final List < ClientFilter > clientFilters ) {
		// Initialize attributes
		layoutInflater = LayoutInflater.from ( context );
		this.groupLayout = groupLayout;
		this.childLayout = childLayout;
		this.clientFilters = clientFilters;
	}
	
	/*
	 * Gets the number of groups.
	 *
	 * @see android.widget.ExpandableListAdapter#getGroupCount()
	 */
	@Override
	public int getGroupCount () {
		// Return the number of client filters
		return clientFilters.size ();
	}

	/*
	 * Gets the data associated with the given group.
	 *
	 * @see android.widget.ExpandableListAdapter#getGroup(int)
	 */
	@Override
	public Object getGroup ( int groupPosition ) {
		try {
			// Return the client filter at the indicated position
			return clientFilters.get ( groupPosition );
		} catch ( Exception exception ) {
			// Invalid group position
			return null;
		} // End of try-catch block
	}
	
	/*
	 * Gets the ID for the group at the given position.
	 *
	 * @see android.widget.ExpandableListAdapter#getGroupId(int)
	 */
	@Override
	public long getGroupId ( int groupPosition ) {
		// Return the group position (since it is unique across groups)
		return groupPosition;
	}
	
	/*
	 * Gets the number of children in a specified group.
	 *
	 * @see android.widget.ExpandableListAdapter#getChildrenCount(int)
	 */
	@Override
	public int getChildrenCount ( int groupPosition ) {
		try {
			// Return the number of filter values for the current client filter
			return ( (ClientFilter) getGroup ( groupPosition ) ).getCount ();
		} catch ( Exception exception ) {
			// Invalid group position
			return 0;
		} // End of try-catch block
	}

	/*
	 * Gets the data associated with the given child within the given group.
	 *
	 * @see android.widget.ExpandableListAdapter#getChild(int, int)
	 */
	@Override
	public Object getChild ( int groupPosition , int childPosition ) {
		try {
			// Return the current filter value for the current client filter at the indicated position
			return ( (ClientFilter) getGroup ( groupPosition ) ).getChild ( childPosition );
		} catch ( Exception exception ) {
			// Invalid group position
			return null;
		} // End of try-catch block
	}

	/*
	 * Gets the ID for the given child within the given group.
	 *
	 * @see android.widget.ExpandableListAdapter#getChildId(int, int)
	 */
	@Override
	public long getChildId ( int groupPosition , int childPosition ) {
		// Return the child position (since it is unique across children within the group)
		return childPosition;
	}

	/*
	 * Indicates whether the child and group IDs are stable across changes to the underlying.
	 *
	 * @see android.widget.ExpandableListAdapter#hasStableIds()
	 */
	@Override
	public boolean hasStableIds () {
		// Indicate that the same ID always refers to the same object
		return true;
	}
	
	/*
	 * Whether the child at the specified position is selectable.
	 *
	 * @see android.widget.ExpandableListAdapter#isChildSelectable(int, int)
	 */
	@Override
	public boolean isChildSelectable ( int groupPosition , int childPosition ) {
		// Indicate that the child is selectable
		return true;
	}
	
	/*
	 * Gets a View that displays the given group.
	 *
	 * @see android.widget.ExpandableListAdapter#getGroupView(int, boolean, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getGroupView ( int groupPosition , boolean isExpanded , View convertView , ViewGroup parent ) {
		// Declare the row view holder
		GroupViewHolder viewHolder;
		// Check if an inflated view is provided
		if ( convertView == null ) {
			// A new view must be inflated
			convertView = layoutInflater.inflate ( groupLayout , null );
			// Declare and initialize a view holder
			viewHolder = new GroupViewHolder ();
			// Retrieve a reference to the client filter description
			viewHolder.description = (TextView) convertView.findViewById ( R.id.label_client_filter_description );
			// Store the view holder as tag
			convertView.setTag ( viewHolder );
		} // End if
		else
			// An inflated view is already provided, retrieve the stored view holder
			viewHolder = (GroupViewHolder) convertView.getTag ();
		
		// Display the client filter description
		viewHolder.description.setText ( ( (ClientFilter) getGroup ( groupPosition ) ).getDescription () );
				
		// Return the view
		return convertView;
	}
	
	/*
	 * Gets a View that displays the data for the given child within the given group.
	 *
	 * @see android.widget.ExpandableListAdapter#getChildView(int, int, boolean, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getChildView ( int groupPosition , int childPosition , boolean isLastChild , View convertView , ViewGroup parent ) {
		// Declare the row view holder
		ChildViewHolder viewHolder;
		// Check if an inflated view is provided
		if ( convertView == null ) {
			// A new view must be inflated
			convertView = layoutInflater.inflate ( childLayout , null );
			// Declare and initialize a view holder
			viewHolder = new ChildViewHolder ();
			// Retrieve a reference to the client filter value
			viewHolder.value = (TextView) convertView.findViewById ( R.id.label_client_filter_value );
			// Retrieve a reference to the check box
			viewHolder.checkBox = (CheckBox) convertView.findViewById ( R.id.checkbox );
			// Store the view holder as tag
			convertView.setTag ( viewHolder );
		} // End if
		else
			// An inflated view is already provided, retrieve the stored view holder
			viewHolder = (ChildViewHolder) convertView.getTag ();
		
		// Display the client filter value
		viewHolder.value.setText ( ( (ClientFilter) getGroup ( groupPosition ) ).getChildDescription ( childPosition ) );
		// Check the check box accordingly
		viewHolder.checkBox.setChecked ( ( (ClientFilter) getGroup ( groupPosition ) ).isFilterSelected ( childPosition ) );
		
		// Return the view
		return convertView;
	}
	
}