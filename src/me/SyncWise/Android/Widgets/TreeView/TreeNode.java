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

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class used to represent a tree node for the Tree View widget.
 * 
 * @author Elias
 *
 */
public class TreeNode implements Serializable {

	/**
	 * Default serial version ID.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Integer used to hold the tree node id, in order to differentiate the current tree node.
	 */
	private final int id;
	 
	/**
	 * Integer used to hold the parent tree node.
	 */
	private Integer parentId;
	
	/**
	 * Integer used to hold the tree node level.
	 */
	private int level;
	
	/**
	 * Flag used to determine if the current tree node is expanded or collapsed.
	 */
	private boolean expanded;
	
	/**
	 * Flag used to determine if the current tree node is enabled or disabled.
	 */
	private boolean disabled;
	
	/**
	 * A {@link java.io.Serializable Serializable} object used to host data.
	 */
	private final Serializable data;
	
	/**
	 * A list of {@link TreeNode} objects having the current tree node as parent.
	 */
	private ArrayList < TreeNode > children;
	
	/**
	 * Setter - {@link #id}
	 * 
	 * @return	Integer used to hold the tree node id, in order to differentiate the current tree node.
	 */
	public int getID () {
		return id;
	}
	
	/*
	 * Compares this instance with the specified object and indicates if they are equal.
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals ( Object object ) {
		// Flag used to indicate if the objects are equal
		boolean isEqual = false;
		// Check if the object is valid and instance of Call
		if ( object != null && object instanceof TreeNode )
			// Compare objects
			isEqual = id == ( (TreeNode) object ).getID ();
		// Return flag
		return isEqual;
	}
	
	/*
	 * Returns an integer hash code for this object.
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode () {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}
	
	/**
	 * Setter - {@link #parentId}
	 * 
	 * @param parentId	Integer used to hold the parent tree node.
	 */
	public void setParentId ( final Integer parentId ) {
		this.parentId = parentId;
	}
	
	/**
	 * Getter - {@link #parentId}
	 * 
	 * @return	Integer used to hold the parent tree node.
	 */
	public Integer getParentId () {
		return parentId;
	}
	
	/**
	 * Setter - {@link #level}
	 * 
	 * @param level	Integer used to hold the tree node level.
	 */
	public void setLevel ( final int level ) {
		this.level = level;
	}
	
	/**
	 * Getter - {@link #level}
	 * 
	 * @return	Integer used to hold the tree node level.
	 */
	public int getLevel () {
		return level;
	}
	
	/**
	 * Setter - {@link #expanded}
	 * 
	 * @param expanded	Flag used to determine if the current tree node is expanded or not.
	 */
	public void setExpanded ( final boolean expanded ) {
		this.expanded = expanded;
	}
	
	/**
	 * Getter - {@link #expanded}
	 * 
	 * @return	Flag used to determine if the current tree node is expanded or not.
	 */
	public boolean isExpanded () {
		return expanded;
	}
	
	/**
	 * Setter - {@link #expanded}
	 * 
	 * @param collapsed	Flag used to determine if the current tree node is collapsed or not.
	 */
	public void setCollapsed ( final boolean collapsed ) {
		this.expanded = ! collapsed;
	}
	
	/**
	 * Getter - {@link #expanded}
	 * 
	 * @return	Flag used to determine if the current tree node is collapsed or not.
	 */
	public boolean isCollapsed () {
		return ! expanded;
	}
	
	/**
	 * Setter - {@link #disabled}
	 * 
	 * @param enabled	Flag used to determine if the current tree node is enabled or not.
	 */
	public void setEnabled ( final boolean enabled ) {
		this.disabled = ! enabled;
	}
	
	/**
	 * Getter - {@link #disabled}
	 * 
	 * @return	Flag used to determine if the current tree node is enabled or not.
	 */
	public boolean isEnabled () {
		return ! disabled;
	}
	
	/**
	 * Setter - {@link #disabled}
	 * 
	 * @param disabled	Flag used to determine if the current tree node is disabled or not.
	 */
	public void setDisabled ( final boolean disabled ) {
		this.disabled = disabled;
	}
	
	/**
	 * Getter - {@link #disabled}
	 * 
	 * @return	Flag used to determine if the current tree node is disabled or not.
	 */
	public boolean isDisabled () {
		return disabled;
	}
	
	/**
	 * Getter - {@link #data}
	 * 
	 * @return	A {@link java.io.Serializable Serializable} object used to host data.
	 */
	public Serializable getData () {
		return data;
	}
	
	/**
	 * Indicates whether the current tree node is a parent tree node.
	 * 
	 * @return	Boolean indicating whether the current tree node is a parent tree node or not.
	 */
	public boolean isParent () {
		// Check if the children list is valid
		if ( children == null || children.isEmpty () )
			return false;
		return true;
	}
	
	/**
	 * Indicates whether the current tree node has children tree nodes.
	 * 
	 * @return	Boolean indicating whether the current tree node has children tree nodes or not.
	 */
	public boolean hasChildren () {
		// Check if the children list is valid
		if ( children == null || children.isEmpty () )
			return false;
		return true;
	}
	
	/**
	 * Setter - {@link #children}
	 * 
	 * @param children	A list of {@link TreeNode} objects having the current tree node as parent.
	 */
	public void setChildren ( final ArrayList < TreeNode > children ) {
		this.children = children;
	}
	
	/**
	 * Getter - {@link #children}
	 * 
	 * @return	A list of {@link TreeNode} objects having the current tree node as parent.
	 */
	public ArrayList < TreeNode > getChildren () {
		return children;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param id	Integer used to hold the tree node id, in order to differentiate the current tree node.
	 * @param data	A {@link java.io.Serializable Serializable} object used to host data.
	 */
	public TreeNode ( final int id , final Serializable data ) {
		// Initialize attributes
		this.id = id;
		this.data = data;
	}
	
	/**
	 * Gets the tree nodes count.<br>
	 * The parent (current) tree node is always counted.<br>
	 * The children tree nodes (and their children if any) are counted if and only if the current node is expanded (similarly for the sub children).
	 * 
	 * @return	Integer holding the number of expanded tree nodes.
	 */
	public int getCount () {
		// Declare and initialize the tree nodes count
		int count = 1;
		// Check if the tree node is expanded
		// AND if the tree node has children
		if ( expanded && children != null )
			// Iterate over all children
			for ( TreeNode treeNode : children )
				// Compute and add the children's count
				count += treeNode.getCount ();
		// Return the tree nodes count
		return count;
	}
	
}