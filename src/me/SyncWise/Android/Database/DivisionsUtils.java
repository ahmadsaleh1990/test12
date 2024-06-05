/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Database;

import java.util.ArrayList;
import java.util.List;

/**
 * Utilities class for generated GreenDao Entities, holding helper methods for additional functionality related to the {@link me.SyncWise.Android.Database.Divisions Divisions} objects.
 * 
 * @author Elias
 *
 */
public class DivisionsUtils {

	/**
	 * Gets the list of parents among the provided list of divisions, for the indicated child.
	 * 
	 * @param child	Child division.
	 * @param divisions	List of divisions used to search the parents.
	 * @return	List holding the parents divisions.
	 * @see #getParents(List, List)
	 */
	public static List < Divisions > getParents ( final Divisions child , final List < Divisions > divisions ) {
		// Declare and initialize a list used to host the children
		List < Divisions > parents = new ArrayList < Divisions > ();
		// Check if the provided child and list of divisions are valid
		if ( child == null || child.getParentCode () == null || child.getCompanyCode () == null || divisions == null || divisions.isEmpty () )
			// Invalid arguments
			return parents;
		// Otherwise the provided child and list of divisions are valid
		// Iterate over the list of divisions
		for ( Divisions division : divisions )
			// Check if the current division is the child's parent
			if ( division.getDivisionCode ().equals ( child.getParentCode () ) && division.getCompanyCode ().equals ( child.getCompanyCode () ) ) {
				// Add the current division to the parents list
				parents.add ( division );
				// Compute and add the parents of this parent
				parents.addAll ( getParents ( division , divisions ) );
			} // End if
		// Return the parents list
		return parents;
	}
	
	/**
	 * Gets the list of children among the provided list of divisions, for the indicated list of parents.
	 * 
	 * @param children	List of children divisions.
	 * @param divisions	List of divisions used to search the parents.
	 * @return	List holding the parents divisions.
	 * @see #getParents(Divisions, List)
	 */
	public static List < Divisions > getParents ( final List < Divisions > children , final List < Divisions > divisions ) {
		// Declare and initialize a list used to host the parents
		List < Divisions > parents = new ArrayList < Divisions > ();
		// Check if the provided children and list of divisions are valid
		if ( children == null || children.isEmpty () || divisions == null || divisions.isEmpty () )
			// Invalid arguments
			return parents;
		// Otherwise the provided list of children and divisions are valid
		// Iterate over all the children
		for ( Divisions child : children ) {
			// Check if the child already exists in the list of parents
			if ( parents.contains ( child ) )
				// Skip the current child
				continue;
			// Otherwise, retrieve all the parents of the current child and add them to the list
			parents.addAll ( getParents ( child , divisions ) );
		} // End for each
		// Return the parents list
		return parents;
	}
	
	/**
	 * Get the main parent (that has no parent) among the provided list of divisions, for the indicated child.
	 * 
	 * @param child	Child division.
	 * @param divisions	List of divisions used to search the parents.
	 * @return	Reference to the main parent divisions, or {@code NULL} if none is found.
	 * @see #getParents(Divisions, List)
	 * @see #getParents(List, List)
	 */
	public static Divisions getMainParent ( final Divisions child , final List < Divisions > divisions ) {
		// Check if the indicated child division is a main parent itself
		if ( child.getParentCode () == null )
			// Return the main parent
			return child;
		// Retrieve the parents list
		List < Divisions > parents = getParents ( child , divisions );
		// Iterate over all parents
		for ( Divisions parent : parents )
			// Determine if the current parent is the main parent (has no parent)
			if ( parent.getParentCode () == null && parent.getCompanyCode ().equals ( child.getCompanyCode () ) )
				// Return the main parent
				return parent;
		// Otherwise there are no main parents among the parents list
		return null;
	}
	
	/**
	 * Gets the list of children among the provided list of divisions, for the indicated parent.
	 * 
	 * @param parent	Parent division.
	 * @param divisions	List of divisions used to search the children.
	 * @return	List holding the children divisions.
	 * @see #getChildren(List, List)
	 */
	public static List < Divisions > getChildren ( final Divisions parent , final List < Divisions > divisions ) {
		// Declare and initialize a list used to host the children
		List < Divisions > children = new ArrayList < Divisions > ();
		// Check if the provided parent and list of divisions are valid
		if ( parent == null || divisions == null || divisions.isEmpty () )
			// Invalid arguments
			return children;
		// Otherwise the provided parent and list of divisions are valid
		// Iterate over the list of divisions
		for ( Divisions division : divisions )
			// Check if the current division's parent matches
			if ( division.getParentCode () != null 
				&& division.getParentCode ().equals ( parent.getDivisionCode () )
				&& division.getCompanyCode ().equals ( parent.getCompanyCode () ) ) {
				// Add the current division to the children list
				children.add ( division );
				// Compute and add the children of this child
				children.addAll ( getChildren ( division , divisions ) );
			} // End if
		// Return the children list
		return children;
	}
	
	/**
	 * Gets the list of children among the provided list of divisions, for the indicated list of parents.
	 * 
	 * @param parents	List of parent divisions.
	 * @param divisions	List of divisions used to search the children.
	 * @return	List holding the children divisions.
	 * @see #getChildren(Divisions, List)
	 */
	public static List < Divisions > getChildren ( final List < Divisions > parents , final List < Divisions > divisions ) {
		// Declare and initialize a list used to host the children
		List < Divisions > children = new ArrayList < Divisions > ();
		// Check if the provided parent and list of divisions are valid
		if ( parents == null || parents.isEmpty () || divisions == null || divisions.isEmpty () )
			// Invalid arguments
			return children;
		// Otherwise the provided list of parents and divisions are valid
		// Iterate over all the parents
		for ( Divisions parent : parents ) {
			// Check if the parent already exists in the list of children
			if ( children.contains ( parent ) )
				// Skip the current parent
				continue;
			// Otherwise, retrieve all the children of the current parent and add them to the list
			children.addAll ( getChildren ( parent , divisions ) );
		} // End for each
		// Return the children list
		return children;
	}
	
}