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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppDialog.ButtonsType;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.Divisions;
import me.SyncWise.Android.Widgets.TreeView.TreeNode;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Activity used as divisions filter, used to select / de-select / filter divisions.
 * 
 * @author Elias
 * @sw.todo <b>Filter Divisions Activity Implementation :</b><br>
 * <ul>
 * <li>Add to the intent the divisions list using {@link #DIVISIONS} as key. <em>Required</em></li>
 * <li>Add to the intent the selected divisions codes list using {@link #SELECTED_DIVISIONS} as key. <em>Optional</em></li>
 * <li>Start this activity <b>FOR A RESULT</b>.</li>
 * <li>If the user has successfully filtered the divisions, an intent is returned as a result with the selected divisions list, accessible using {@link #SELECTED_DIVISIONS} as key.</li>
 * <li>Do not forget to add this class in the {@code AndroidManifest.xml} file.</li>
 * </ul>
 *
 */
public class FilterDivisionsActivity extends ListActivity {

	/**
	 * Bundle key used to put/retrieve the divisions list.
	 */
	public static final String DIVISIONS = FilterDivisionsActivity.class.getName () + ".TREE_NODES";
	
	/**
	 * Bundle key used to put/retrieve the selected divisions list.
	 */
	public static final String SELECTED_DIVISIONS = FilterDivisionsActivity.class.getName () + ".SELECTED_DIVISIONS";
	
	/**
	 * List of strings hosting the selected division codes.
	 */
	private ArrayList < String > selectedDivisions;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #treeNodes}.
	 */
	private static final String TREE_NODES = FilterDivisionsActivity.class.getName () + ".TREE_NODES";
	
	/**
	 * List of {@link me.SyncWise.Android.Widgets.TreeView.TreeNode TreeNode} objects used to define the tree view list items.
	 */
	private ArrayList < TreeNode > treeNodes;
	
	/**
	 * List of {@link me.SyncWise.Android.Widgets.TreeView.TreeNode TreeNode} objects holding ALL the tree views.<br>
	 * In contrast with the {@link #treeNodes} list, this list hosts all the tree nodes.
	 */
	private ArrayList < TreeNode > treeViews;
	
	/**
	 * Reference to the orders list population task.
	 */
	private static PopulateList populateList;
	
	/*
	 * Called when the activity is starting.
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate ( Bundle savedInstanceState ) {
		// Display the activity as a pop up dialog with an action bar
		AppDialog.showAsDialog ( this , new AppDialog.Property ( 95 , true ) );
		// Superclass method call	
		super.onCreate ( savedInstanceState );
		// Change the title associated with this activity
		setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.filter_divisions_activity_title ) );
		// Set the activity content from a layout resource.
		setContentView ( R.layout.filter_divisions_activity );
		// Define the empty list view
		getListView ().setEmptyView ( findViewById ( R.id.empty_list_view ) );
		// Display empty list label
		( (TextView) findViewById ( R.id.empty_list_view ) ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.empty_divisions_list_label ) );
		
		// Determine if a saved instance state is provided
		if ( savedInstanceState != null ) {
			// Restore the content provided by the saved instance state
			treeNodes = (ArrayList < TreeNode >) savedInstanceState.getSerializable ( TREE_NODES );
			selectedDivisions = savedInstanceState.getStringArrayList ( SELECTED_DIVISIONS );
		} // End if
		
		// Check if the tree nodes list is valid
		if ( treeNodes == null && populateList == null ) {
			// Populate the tree nodes asynchronously
			populateList = new PopulateList ();
			populateList.execute ();
		} // End if
		else if ( treeNodes != null ) {
			// Iterate over all main (level 0) tree nodes
			for ( TreeNode treeNode : treeNodes )
				// Populate the tree views list
				populateTreeViews ( treeNode );
			// Set a new adapter
			setListAdapter ( new DivisionsAdapter ( this , R.layout.filter_divisions_activity_items , treeNodes , selectedDivisions ) );
		} // End else
	}
	
    /*
     * Called to retrieve per-instance state from an activity before being killed so that the state can be restored in onCreate(Bundle) or onRestoreInstanceState(Bundle).
     *
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    protected void onSaveInstanceState ( Bundle outState ) {
    	// Superclass method call
    	super.onSaveInstanceState ( outState );
    	// Save the content of treeNodes in the outState bundle
    	outState.putSerializable ( TREE_NODES , treeNodes );
    	// Save the content of selectedDivisions in the outState bundle
    	outState.putStringArrayList ( SELECTED_DIVISIONS , selectedDivisions );
    }
	
    /*
     * This method will be called when an item in the list is selected.
     *
     * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
     */
	@Override
    protected void onListItemClick ( ListView listView , View view , final int position , long id ) {
		// Retrieve a reference to the selected tree node
		TreeNode treeNode = ( (DivisionsAdapter.ViewHolder) view.getTag () ).treeNode;
		// Retrieve a reference to the selected division
		Divisions division = (Divisions) treeNode.getData ();
		// Determine whether to check or un-check the appropriate children
		Boolean checkChildren = null;
		// Add / remove the selected division accordingly
		if ( selectedDivisions.contains ( division.getDivisionCode () ) ) {
			selectedDivisions.remove ( division.getDivisionCode () );
			checkChildren = false; 
		} // End if
		else {
			selectedDivisions.add ( division.getDivisionCode () );
			checkChildren = true;
		} // End else
		// Check the children tree nodes accordingly
		checkChildren ( treeNode , checkChildren );
		// Check the parents accordingly
		checkParents ( treeNode );
		// Refresh the tree view
		( (DivisionsAdapter) getListAdapter () ).notifyDataSetChanged ();
	}
	
	/**
	 * Check all the direct and indirect children of the provided tree node based on the provided flag.<br>
	 * <b>Note : </b> This method does not refresh the list view !
	 * 
	 * @param treeNode	A Tree Node object whose children are to be checked accordingly.
	 * @param checkChildren	Flag used to indicate whether to check or un-check the tree node's children.
	 */
	private void checkChildren ( final TreeNode treeNode , final boolean checkChildren ) {
		// Check if the provided tree node is valid
		if ( treeNode == null )
			// Invalid tree node
			return;
		// Check if the current child has children
		if ( treeNode.hasChildren () )
			// Iterate over all the children
			for ( int i = 0 ; i < treeNode.getChildren ().size () ; i ++ ) {
				// Retrieve a reference to the division
				Divisions division = (Divisions) treeNode.getChildren ().get ( i ).getData ();
				// Add / remove the division from the selected divisions codes lists accordingly
				if ( checkChildren && ! selectedDivisions.contains ( division.getDivisionCode () ) )
					selectedDivisions.add ( division.getDivisionCode () );
				else if ( ! checkChildren )
					selectedDivisions.remove ( division.getDivisionCode () );
				// Check if the current child has children
				if ( treeNode.getChildren ().get ( i ).hasChildren () )
					// Check the child's children accordingly
					checkChildren ( treeNode.getChildren ().get ( i ) , checkChildren );
			} // End for loop
	}
	
	/**
	 * Check all the tree node's parents based on their children states (selected or not).
	 * 
	 * @param treeNode	The tree node whose parents are to be checked accordingly.
	 */
	private void checkParents ( final TreeNode treeNode ) {
		// Compute the tree node parent index in the tree views list
		int index = treeNode.getParentId () == null ? -1 : treeViews.indexOf ( new TreeNode ( treeNode.getParentId () , null ) );
		// Retrieve a reference to the tree node parent
		TreeNode parent = index == -1 ? null : treeViews.get ( index );
		// Check if the parent is valid
		if ( parent == null )
			// Invalid parent
			return;
		// Otherwise the parent is valid
		// Retrieve a reference to the parent's division
		Divisions parentDivision = (Divisions) parent.getData ();
		// Check if at least one of the children (divisions) is NOT selected
		boolean checkParent = true;
		// Iterate over all the parent's children
		for ( TreeNode child : parent.getChildren () ) {
			// Retrieve a reference to the current division
			Divisions division = (Divisions) child.getData ();
			// Determine if the division is selected
			if ( ! selectedDivisions.contains ( division.getDivisionCode () ) ) {
				// The division is NOT selected
				// Reset flag
				checkParent = false;
				// Exit loop
				break;
			} // End if
		} // End for each
		// Check the parent accordingly
		if ( checkParent && ! selectedDivisions.contains ( parentDivision.getDivisionCode () ) )
			selectedDivisions.add ( parentDivision.getDivisionCode () );
		else if ( ! checkParent )
			selectedDivisions.remove ( parentDivision.getDivisionCode () );
	}
	
	/*
	 * Initialize the contents of the Activity's standard options menu.
	 *
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
    @Override
    public boolean onCreateOptionsMenu ( Menu menu ) {
    	// Use the MenuInflater of this context to inflate a menu hierarchy from the specified XML resource
    	getMenuInflater ().inflate ( R.menu.action_bar , menu );
    	// Enable the required menu items
    	me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_save ) );
    	me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_mark_all ) );
    	me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_unmark_all ) );
		// Display the menu
        return true;
    }
    
    /*
     * This hook is called whenever an item in your options menu is selected.
     *
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
	@Override
    public boolean onOptionsItemSelected ( MenuItem menuItem ) {
    	// Determine if the action list is selected
    	if ( menuItem.getItemId () == R.id.action_save ) {
    		// Check if there is at least one division
    		if ( selectedDivisions.isEmpty () ) {
        		// Display warning
        		AppDialog.getInstance ().displayAlert ( FilterDivisionsActivity.this ,
        				null ,
        				AppResources.getInstance ( FilterDivisionsActivity.this ).getString ( FilterDivisionsActivity.this , R.string.no_divisions_selected_message ) ,
        				ButtonsType.OK ,
        				null );
        		// Consume event
        		return true;
    		} // End if
        	// Call this to set the result that your activity will return to its caller
        	setResult ( RESULT_OK , new Intent ().putExtra ( FilterDivisionsActivity.SELECTED_DIVISIONS , selectedDivisions ) );
	    	// Finish this activity
	    	finish ();
    		// Consume event
    		return true;
    	} // End if
    	// Determine if the action mark all is selected
    	else if ( menuItem.getItemId () == R.id.action_mark_all ) {
    		// Iterate over all the tree nodes (parents)
    		for ( TreeNode treeNode : treeNodes ) {
    			// Retrieve a reference to the selected division
    			Divisions division = (Divisions) treeNode.getData ();
    			// Add the selected division accordingly
    			if ( ! selectedDivisions.contains ( division.getDivisionCode () ) )
    				selectedDivisions.add ( division.getDivisionCode () );
    			// Check the children tree nodes accordingly
    			checkChildren ( treeNode , true );
    		} // End for each
			// Refresh the tree view
			( (DivisionsAdapter) getListAdapter () ).notifyDataSetChanged ();
    		// Consume event
    		return true;
    	} // End if
    	// Determine if the action un-mark all is selected
    	else if ( menuItem.getItemId () == R.id.action_unmark_all ) {
    		// Iterate over all the tree nodes (parents)
    		for ( TreeNode treeNode : treeNodes ) {
    			// Retrieve a reference to the selected division
    			Divisions division = (Divisions) treeNode.getData ();
    			// Remove the selected division accordingly
    			if ( selectedDivisions.contains ( division.getDivisionCode () ) )
    				selectedDivisions.remove ( division.getDivisionCode () );
    			// Check the children tree nodes accordingly
    			checkChildren ( treeNode , false );
    		} // End for each
			// Refresh the tree view
			( (DivisionsAdapter) getListAdapter () ).notifyDataSetChanged ();
    		// Consume event
    		return true;
    	} // End if
    	// Allow normal menu processing to proceed
    	return false;
    }
    
    /**
     * Populates the {@link #treeNodes} list using the divisions list from the intent.
     */
	@SuppressWarnings("unchecked")
	private void populateTreeNodes () {
		// Retrieve the divisions list from the intent
		ArrayList < Divisions > divisions = (ArrayList < Divisions >) getIntent ().getSerializableExtra ( DIVISIONS );
		// Check if the divisions list is valid
		if ( divisions == null )
			// Initialize the divisions list
			divisions = new ArrayList < Divisions > ();
		// Map the divisions to their division codes
		Map < String , Divisions > _divisions = new HashMap < String , Divisions > ();
		// Map each division to its parent code
		Map < String , List < Divisions > > children = new HashMap < String , List < Divisions > > ();
		// Iterate over all divisions
		for ( Divisions division : divisions ) {
			// Map the current division to its division code
			_divisions.put ( division.getDivisionCode () , division );
			// Check if the current division has a valid parent code
			if ( division.getParentCode () == null )
				continue;
			// Check if the current parent code is already mapped
			if ( children.containsKey ( division.getParentCode () ) )
				// Parent code already mapped, add the current division to its children list
				children.get ( division.getParentCode () ).add ( division );
			else {
				// Parent code NOT mapped
				// Declare and initialize a children list
				List < Divisions > list = new ArrayList < Divisions > ();
				// Add the current division to the list
				list.add ( division );
				// Map the children list to the parent code
				children.put ( division.getParentCode () , list );
			} // End if
		} // End for each
		// Keep track of the last valid tree node id
		int ids = 0;
		// Initialize the tree nodes list
		treeNodes = new ArrayList < TreeNode > ();
		// Populate the tree nodes list with the main (level 0) divisions (meaning parent divisions)
		// Iterate over all divisions
		for ( Divisions division : divisions )
			// Check if the current division has a valid parent division
			if ( division.getParentCode () == null || ! _divisions.containsKey ( division.getParentCode () ) ) {
				// The current division does NOT have a valid parent division
				// Declare and initialize a tree node for the current division
				TreeNode treeNode = new TreeNode ( ids ++ , division );
				// Set the tree node level
				treeNode.setLevel ( 0 );
				// Add the tree node to the list
				treeNodes.add ( treeNode );
			} // End if
		// Iterate over all the tree nodes
		for ( TreeNode treeNode : treeNodes )
			// Populate the current tree node's children list
			ids = populateTreeNodeChildren ( ids , treeNode , children );
	}
	
	/**
	 * Populates the current tree node children list (if it has any), based on divisions mapped to their parent codes.
	 * 
	 * @param ids	Integer holding the last valid tree node Id.
	 * @param treeNode	The tree node parent used to populate its children list.
	 * @param children	Map holding division parent codes as key, mapped to the relative list of children divisions.
	 * @return	Integer holding the last valid tree node Id after the children list generation.
	 */
	private int populateTreeNodeChildren ( int ids , TreeNode treeNode , Map < String , List < Divisions > > children ) {
		// Check if the arguments are valid
		if ( treeNode == null || children == null )
			// Invalid arguments
			return ids;
		// Retrieve the parent code (the current division's code)
		String parentCode = ( (Divisions) treeNode.getData () ).getDivisionCode ();
		// Check if the current tree node has children (meaning if there are divisions having the current division's code as parent)
		if ( ! children.containsKey ( parentCode ) )
			// The current tree node does NOT have children
			return ids;
		// Otherwise, the current tree node has children
		// Declare and initialize a list used to host the tree node children
		ArrayList < TreeNode > treeNodes = new ArrayList < TreeNode > ();
		// Get the list of divisions
		List < Divisions > divisions = children.get ( parentCode );
		// Iterate over the divisions
		for ( int i = 0 ; i < divisions.size () ; i ++ ) {
			TreeNode child = new TreeNode ( ids ++ , divisions.get ( i ) );
			child.setLevel ( treeNode.getLevel () + 1 );
			child.setParentId ( treeNode.getID () );
			treeNodes.add ( child );
		} // End for loop
		// Set the list of children to the parent tree node
		treeNode.setChildren ( treeNodes );
		// Iterate over each child
		for ( TreeNode child : treeNodes )
			// Populate the current tree node children
			ids = populateTreeNodeChildren ( ids , child , children );
		return ids;
	}
    
	/*
	 * Called when you are no longer visible to the user.
	 *
	 * @see android.app.Activity#onStop()
	 */
	@Override
    protected void onStop () {
    	// Superclass method call
		super.onStop ();
		// Determine if the activity is finishing
		if ( isFinishing () ) {
			selectedDivisions = null;
			treeNodes = null;
			treeViews = null;
		} // End if
	}
	
	/**
	 * Populates the {@link #treeViews} list using the provided tree node object.
	 * 
	 * @param treeNode	A tree node object used to populate the {@link #treeViews} list.
	 */
	private void populateTreeViews ( final TreeNode treeNode ) {
		// Initialize the tree views list if needed
		if ( treeViews == null )
			treeViews = new ArrayList < TreeNode > ();
		// Check if the tree nodes list is valid
		if ( treeNodes == null )
			// Invalid list
			return;
		// Otherwise the tree nodes list is valid
		// Add the tree node to the list
		treeViews.add ( treeNode );
		// Check if the tree node has children
		if ( treeNode.hasChildren () )
			// Iterate over all the children
			for ( int i = 0 ; i < treeNode.getChildren ().size () ; i ++ )
				// Populate the tree views
				populateTreeViews ( treeNode.getChildren ().get ( i ) );
	}
	
	/**
	 * AsyncTask helper class used to populate the divisions / tree nodes list.
	 * 
	 * @author Elias
	 *
	 */
	private class PopulateList extends AsyncTask < Void , Void , Void > {
		
		/*
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@SuppressWarnings("unchecked")
		@Override
		protected Void doInBackground ( Void ... params ) {
			// Retrieve the selected divisions codes list from the intent
			selectedDivisions = (ArrayList < String >) getIntent ().getSerializableExtra ( SELECTED_DIVISIONS );
			// Check if the selected divisions list is valid
			if ( selectedDivisions == null )
				// Initialize the selected divisions codes list
				selectedDivisions = new ArrayList < String > ();
		    // Populate the tree nodes list
			populateTreeNodes ();
			// Iterate over all main (level 0) tree nodes
			for ( TreeNode treeNode : treeNodes )
				// Populate the tree views list
				populateTreeViews ( treeNode );
			// Clear the task reference
			populateList = null;
			return null;
		}
		
		/*
		 * Runs on the UI thread after doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute ( Void arg ) {
			// Check if the tree nodes list is valid
			if ( treeNodes != null )
				// Set a new adapter
				setListAdapter ( new DivisionsAdapter ( FilterDivisionsActivity.this , R.layout.filter_divisions_activity_items , treeNodes , selectedDivisions ) );
		}
		
	}
	
}