/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Widgets.TreeView;

import java.util.ArrayList;
import java.util.List;

import me.SyncWise.Android.R;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * A concrete BaseAdapter that is backed by an array of {@link java.lang.Object Object} objects.
 * 
 * @author Elias
 *
 */
public class TreeViewAdapter extends ArrayAdapter < TreeNode > {

	/**
	 * {@link android.view.LayoutInflater} used to instantiate a layout XML file into its corresponding View objects.
	 */
	private final LayoutInflater layoutInflater;
	
	/**
	 * XML Layout (layout) resource id.
	 */
	private final int layout;
	
	/**
	 * Constant integer holding the padding value in pixels.<br>
	 * This value is used multiple times : depends on the current item level.
	 */
	private final int PADDING;
	
	/**
	 * Nested class implementation of the view holder pattern used to keep references to children views in order to avoid unnecessary calls to {@link android.view.View#findViewById(int) findViewById(id)} on each row.
	 * 
	 * @author Elias
	 *
	 */
	public static class ViewHolder {
		public TreeNode treeNode;
		public LinearLayout treeNodeView;
		public ImageView branchKnob;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param context	The application context.
	 * @param layout	XML Layout (layout) resource id.
	 * @param treeNodes	List of {@link me.SyncWise.Android.Widgets.TreeView.TreeNode TreeNode} objects.
	 */
	public TreeViewAdapter ( Context context, int layout , List < TreeNode > treeNodes ) {
		// Superclass method call
		super ( context , layout , treeNodes );
		// Initialize the attributes
		layoutInflater = LayoutInflater.from ( context );
		this.layout = layout;
		PADDING = (int) TypedValue.applyDimension ( TypedValue.COMPLEX_UNIT_DIP , 30 , context.getResources ().getDisplayMetrics () );
	}
	
	/*
	 * How many items are in the data set represented by this Adapter.
	 *
	 * @see android.widget.ArrayAdapter#getCount()
	 */
	@Override
	public int getCount () {
		// Compute the total number of level 0 tree nodes
		int size = super.getCount ();
		int count = 0;
		// Iterate over all tree nodes
		for ( int i = 0 ; i < size ; i ++ )
			// Compute the tree node count for the current level 0 tree node
			count += getItem ( i ).getCount ();
		return count;
	}
	
	/**
	 * Gets the tree node at the specified position.<br>
	 * The provided position is the tree node's position in the list view (and not in the array list).
	 * 
	 * @param position	Integer holding the tree node's list view position.
	 * @return	The {@link me.SyncWise.Android.Widgets.TreeView.TreeNode TreeNode} object at the specified position, or {@code NULL} if invalid.
	 */
	public TreeNode getTreeNode ( int position ) {
		// Check if the position is valid
		if ( position < 0 )
			// Invalid position
			return null;
		// Compute the number of level 0 tree nodes
		int size = super.getCount ();
		// Compute the total number of tree nodes
		int totalCount = 0;
		// Iterate over all level 0 tree nodes
		for ( int i = 0 ; i < size ; i ++ ) {
			// Compute the current tree node count
			int currentCount = getItem ( i ).getCount ();
			// Check if the required tree node is the current parent tree node
			if ( position == totalCount )
				// Return the current parent tree node
				return getItem ( i );
			// Check if the required tree node lies in the current tree node
			if ( position < ( totalCount + currentCount ) )
				// The tree node lies in the current tree node
				return getTreeNodeChild ( getItem ( i ) , position - totalCount - 1 );
			// Compute the tree node count for the current level 0 tree node
			totalCount += currentCount;
		} // End for loop
		// Invalid position
		return null;
	}
	
	/**
	 * Gets the tree node at the required position from the provided tree node parent.
	 * 
	 * @param parent	The parent tree node to retrieve the required tree node from.
	 * @param position	Integer holding the tree node's position within the parent's children tree nodes. 
	 * @return	The {@link me.SyncWise.Android.Widgets.TreeView.TreeNode TreeNode} object at the specified position, or {@code NULL} if invalid.
	 */
	private TreeNode getTreeNodeChild ( TreeNode parent , int position ) {
		// Check if the tree node is valid
		if ( parent == null || position < 0 )
			// Invalid tree node OR position
			return null;
		// Compute the total number of tree nodes
		int totalCount = 0;
		// Retrieve the tree node's children
		ArrayList < TreeNode > children = parent.getChildren ();
		// Iterate over all the children
		for ( int i = 0 ; i < children.size () ; i ++ ) {
			// Compute the current tree node count
			int currentCount = children.get ( i ).getCount ();
			// Check if the required tree node is the current child tree node
			if ( position == totalCount )
				// Return the current tree node
				return children.get ( i );
			// Check if the required tree node lies in the current parent tree node
			else if ( position < ( totalCount + currentCount ) )
				// The tree node lies in the current tree node
				return getTreeNodeChild ( children.get ( i ) , position - totalCount - 1 );
			// Compute the tree node count for the current level 0 tree node
			totalCount += currentCount;
		} // End for loop
		// Invalid position
		return null;
	}
	
	/*
	 * Get a View that displays the data at the specified position in the data set.
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView ( int position , View convertView , ViewGroup parent ) {
		// Declare the row view holder
		ViewHolder viewHolder;
		// Check if an inflated view is provided
		if ( convertView == null ) {
			// A new view must be inflated
			convertView = layoutInflater.inflate ( layout , null );
			// Declare and initialize a view holder
			viewHolder = new ViewHolder ();
			// Retrieve a reference to the tree node view
			viewHolder.treeNodeView = (LinearLayout) convertView.findViewById ( R.id.tree_node_view );
			// Retrieve a reference to the branch knob icon
			viewHolder.branchKnob = (ImageView) convertView.findViewById ( R.id.branch_knob );
			// Store the view holder as tag
			convertView.setTag ( viewHolder );
		} // End if
		else
			// An inflated view is already provided, retrieve the stored view holder
			viewHolder = (ViewHolder) convertView.getTag ();
		
		// Retrieve a reference to the current tree node
		final TreeNode treeNode = getTreeNode ( position );
		// Store a reference of the tree node
		viewHolder.treeNode = treeNode;
		
		// Set the branch knob accordingly
		if ( treeNode.isParent () && treeNode.isExpanded () )
			viewHolder.branchKnob.setImageResource ( R.drawable.branch_knob_collapse );
		else if ( treeNode.isParent () && treeNode.isCollapsed () )
			viewHolder.branchKnob.setImageResource ( R.drawable.branch_knob_expand );
		else
			viewHolder.branchKnob.setImageResource ( R.drawable.branch_knob_grayed_out );
		// Set the level padding according to the tree node level
		viewHolder.treeNodeView.setPadding ( viewHolder.treeNodeView.getPaddingRight ()
				+ treeNode.getLevel () * PADDING ,
				viewHolder.treeNodeView.getPaddingTop () ,
				viewHolder.treeNodeView.getPaddingRight () ,
				viewHolder.treeNodeView.getPaddingBottom () );
		// Set a click listener on the branch knob
		viewHolder.branchKnob.setOnClickListener ( treeNode.hasChildren () ? new OnClickListener () {
			@Override
			public void onClick ( View view ) {
				// Expand / collapse the tree node accordingly
				treeNode.setExpanded ( ! treeNode.isExpanded () );
				// Refresh list view
				notifyDataSetChanged ();
			}
		} : null );
		
		// Return the view
		return convertView;
	}
	
	/*
	 * Returns true if the item at the specified position is not a separator. (A separator is a non-selectable, non-clickable item).
	 *
	 * @see android.widget.BaseAdapter#isEnabled(int)
	 */
	@Override
	public boolean isEnabled ( int position ) {
		// Determine if the tree node at the specified position is enabled
		return getTreeNode ( position ).isEnabled ();
	}
	
}