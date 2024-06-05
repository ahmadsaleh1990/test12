/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.FilterDivisions;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.Divisions;
import me.SyncWise.Android.Widgets.TreeView.TreeNode;
import me.SyncWise.Android.Widgets.TreeView.TreeViewAdapter;

/**
 * A concrete BaseAdapter that is backed by an array of {@link java.lang.Object Object} objects.
 * 
 * @author Elias
 *
 */
public class DivisionsAdapter extends TreeViewAdapter {

	/**
	 * List of strings used to host the selected divisions codes.
	 */
	private final List < String > selectedDivisions;
	
	/**
	 * Nested class implementation of the view holder pattern used to keep references to children views in order to avoid unnecessary calls to {@link android.view.View#findViewById(int) findViewById(id)} on each row.
	 * 
	 * @author Elias
	 *
	 */
	public static class ViewHolder extends TreeViewAdapter.ViewHolder {
		public TextView label;
		public CheckBox checkBox;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param context	The application context.
	 * @param layout	XML Layout (layout) resource id.
	 * @param treeNodes	List of {@link me.SyncWise.Android.Widgets.TreeView.TreeNode TreeNode} objects.
	 */
	public DivisionsAdapter ( Context context, int layout , List < TreeNode > treeNodes , List < String > selectedDivisions ) {
		// Superclass method call
		super ( context , layout , treeNodes );
		// Initialize attribute
		this.selectedDivisions = selectedDivisions;
	}
	
	/*
	 * Get a View that displays the data at the specified position in the data set.
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView ( int position , View convertView , ViewGroup parent ) {
		// Super class method call
		convertView = super.getView ( position , convertView , parent );
		// Declare and initialize a view holder
		ViewHolder viewHolder = new ViewHolder ();
		viewHolder.treeNode = ( (TreeViewAdapter.ViewHolder) convertView.getTag () ).treeNode;
		viewHolder.treeNodeView = ( (TreeViewAdapter.ViewHolder) convertView.getTag () ).treeNodeView;
		viewHolder.branchKnob = ( (TreeViewAdapter.ViewHolder) convertView.getTag () ).branchKnob;
		// Store the view holder as tag
		convertView.setTag ( viewHolder );
		
		// Retrieve a reference to the division object
		Divisions division = (Divisions) viewHolder.treeNode.getData ();
		
		// Retrieve a reference to the division name label
		viewHolder.label = (TextView) convertView.findViewById ( R.id.label_division_name );
		// Retrieve a reference to the check box
		viewHolder.checkBox = (CheckBox) convertView.findViewById ( R.id.checkbox );
		
		// Display the division name
		viewHolder.label.setText ( division.getDivisionName () );
		// Check the check box accordingly
		viewHolder.checkBox.setChecked ( selectedDivisions.contains ( division.getDivisionCode () ) );
		
		// Return the view
		return convertView;
	}
	
}