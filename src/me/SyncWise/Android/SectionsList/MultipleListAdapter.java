/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.SectionsList;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;

/**
* A concrete BaseAdapter that is backed by a Map of {@link android.widget.Adapter Adapter} objects and {@link me.SyncWise.Android.SectionsList.Section Section} objects.
* 
* @author Elias
*
*/
public class MultipleListAdapter extends BaseAdapter {

	/**
	 * A {@link java.util.Map Map} used to keep track of all the sub adapters and their {@link me.SyncWise.Android.SectionsList.Section Section}s.
	 */
	public final Map < Section , Adapter > sections = new LinkedHashMap < Section , Adapter > ();
	
	/**
	 * A reference to {@link me.SyncWise.Android.SectionsList.SectionAdapter SectionAdapter} which is mainly :<br>
	 * A concrete BaseAdapter that is backed by an array of {@link me.SyncWise.Android.SectionsList.Section Section} objects.
	 */
	public final SectionAdapter sectionAdapter;
	
	/**
	 * Constant holding the item view type for sections.
	 */
	public final static int TYPE_SECTION_HEADER = 0;
	
	/**
	 * Constructor.
	 * 
	 * @param context	The application context.
	 */
	public MultipleListAdapter ( Context context ) {
		sectionAdapter = new SectionAdapter ( context );
	}
	
	/**
	 * Constructor.
	 * 
	 * @param context	The application context.
	 * @param sectionLayout	XML Layout (layout) resource id.
	 */
	public MultipleListAdapter ( Context context , final int sectionLayout ) {
		sectionAdapter = new SectionAdapter ( context , sectionLayout );
	}
	
	/**
	 * Adds an adapter along with its section.
	 * 
	 * @param section	A section used as a separator.
	 * @param adapter	A concrete BaseAdapter.
	 */
	public void addSection ( Section section , Adapter adapter ) {
		// Check if both the adapter and its section are valid
		if ( section == null || adapter == null )
			// Invalid arguments
			return;
		// Otherwise the arguments are valid
		// Determine if the section is already added
		if ( ! sections.containsKey ( section ) )
			// The section is not previously added, so added it now
			sectionAdapter.add ( section );
		// Add/Replace the new section/adapter
		sections.put ( section , adapter );
	}
	
	/**
	 * Gets the section with the specified position.
	 * 
	 * @param position	Integer holding the required section position.
	 * @return	Section at the specified position, or {@code NULL} if the position is invalid.
	 */
	public Section getSection ( int position ) {
		// Check if the position is valid
		if ( position < 0 || position >= sections.size () )
			// Invalid position
			return null;
		// Declare an integer to track the position
		int index = 0;
		// Iterate over all sections
		for ( Section section : this.sections.keySet () ) {
			// Check if the position match
			if ( position == index )
				// Return the current section
				return section;
			// Otherwise increment the index
			index ++;
		} // End for each
		// Invalid position
		return null;
	}
	
	/**
	 * Gets the adapter with the specified position.
	 * 
	 * @param position	Integer holding the required adapter position.
	 * @return	Adapter at the specified position, or {@code NULL} if the position is invalid.
	 */
	public Adapter getAdapter ( int position ) {
		// Check if the position is valid
		if ( position < 0 || position >= sections.size () )
			// Invalid position
			return null;
		// Declare an integer to track the position
		int index = 0;
		// Iterate over all sections
		for ( Object section : this.sections.keySet () ) {
			// Check if the position match
			if ( position == index )
				// Return the current adapter
				return sections.get ( section );
			// Otherwise increment the index
			index ++;
		} // End for each
		// Invalid position
		return null;
	}
	
	/**
	 * Clears all the sub adapters along with their sections.43
	 */
	public void clear () {
		// Iterate over all sections
		for ( Object section : this.sections.keySet () ) {
			// Get the current adapter
			Adapter adapter = sections.get ( section );
			// Determine if the adapter is an ArrayAdapter
			if ( adapter instanceof ArrayAdapter < ? > )
				// Clear the adapter list
				( (ArrayAdapter < ? >) adapter ).clear ();
			// Determine if the adapter is a CursorAdapter
			else if ( adapter instanceof CursorAdapter )
				// Clear the adapter cursor
				( (CursorAdapter) adapter ).changeCursor ( null );
		} // End for each
		// Clear the map of adapters and sections
		sections.clear ();
		// Clear the sections' adapter
		sectionAdapter.clear ();
	}
	
	/*
	 * Get the data item associated with the specified position in the data set.
	 *
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem ( int position ) {
		// Iterate over all sections
		for ( Object section : this.sections.keySet () ) {
			// Get the current adapter
			Adapter adapter = sections.get ( section );
			// Compute the total number of items : all the adapter's items and the separator item (section)
			int size = adapter.getCount () + 1;
			
			// Check if the required position lies inside this section
			// Determine if the required position points to the separator (section)
			if ( position == 0 )
				// Return the section
				return section;
			// Determine if the required position points to an item in the sub adapter
			if ( position < size )
				// Return the adapter's item
				return adapter.getItem ( position - 1 );

			// Otherwise the required position does not lie in this section
			position -= size;
		} // End for each
		// Invalid position
		return null;
	}
	
	/*
	 * How many items are in the data set represented by this Adapter.
	 *
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount () {
		// Compute the total number of items which consists of the number of items of each sub adapter along with their separator items (sections)
		int total = 0;
		// Iterate over all sections
		for ( Adapter adapter : this.sections.values () )
			// Add the total number of items
			total += adapter.getCount () + 1;
		// Return the total number of items
		return total;
	}

	/**
	 * Gets the total number of sections.
	 * 
	 * @return	Integer holding the total number of sections.
	 */
	public int getSectionsCount () {
		// Return the total number of sections
		return sections.size ();
	}
	
	/*
	 * Returns the number of types of Views that will be created by getView(int, View, ViewGroup).
	 *
	 * @see android.widget.BaseAdapter#getViewTypeCount()
	 */
	@Override
	public int getViewTypeCount () {
		// Compute the total number of item view types which consists of the number of item view types of each sub adapter plus 1 for the separator items
		int total = 1;
		// Iterate over all sections
		for ( Adapter adapter : this.sections.values () )
			// Add the total number of item view types
			total += adapter.getViewTypeCount ();
		// Return the total number of items view types
		return total;
	}
	
	/*
	 * Get the type of View that will be created by getView(int, View, ViewGroup) for the specified item.
	 *
	 * @see android.widget.BaseAdapter#getItemViewType(int)
	 */
	@Override
	public int getItemViewType ( int position ) {
		int type = 1;
		// Iterate over all sections
		for ( Object section : this.sections.keySet () ) {
			// Get the current adapter
			Adapter adapter = sections.get ( section );
			// Compute the number of items for the current adapter (including the separator item / section)
			int size = adapter.getCount () + 1;
			
			// Check if the required position lies inside this section
			// Determine if the required position points to the separator (section)
			if ( position == 0 )
				// Return the separator (section) item view type
				return TYPE_SECTION_HEADER;
			// Determine if the required position points to an item in the sub adapter
			if ( position < size )
				// Return the adapter's item view type
				return type + adapter.getItemViewType ( position - 1 );

			// Otherwise the required position does not lie in this section
			position -= size;
			type += adapter.getViewTypeCount ();
		} // End for each
		// Invalid position
		return -1;
	}
	
	/*
	 * Returns true if the item at the specified position is not a separator.
	 *
	 * @see android.widget.BaseAdapter#isEnabled(int)
	 */
	@Override
	public boolean isEnabled ( int position ) {
		// Iterate over all sections
		for ( Object section : this.sections.keySet () ) {
			// Get the current adapter
			Adapter adapter = sections.get ( section );
			// Compute the number of items for the current adapter (including the separator item / section)
			int size = adapter.getCount () + 1;
			
			// Check if the required position lies inside this section
			// Determine if the required position points to the separator (section)
			if ( position == 0 )
				// Separators are disabled
				return false;
			// Determine if the required position points to an item in the sub adapter
			if ( position < size ) {
				// Determine if the required item is enabled if the adapter is an instance of BaseAdapter
				// Otherwise, the item is NOT enabled
				return adapter instanceof BaseAdapter ? ( (BaseAdapter) adapter ).isEnabled ( position - 1 ) : false;
			} // End if

			// Otherwise the required position does not lie in this section
			position -= size;
		} // End for each
		// Invalid position
		return false;
	}
	
	/*
	 * Get a View that displays the data at the specified position in the data set.
	 *
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView ( int position , View convertView , ViewGroup parent ) {
		// Declare and initialize an integer used to keep track of the section index
		int sectionIndex = 0;
		// Iterate over all sections
		for ( Object section : this.sections.keySet () ) {
			// Get the current adapter
			Adapter adapter = sections.get ( section );
			// Compute the number of items for the current adapter (including the separator item / section)
			int size = adapter.getCount () + 1;
			
			// Check if the required position lies inside this section
			// Determine if the required position points to the separator (section)
			if ( position == 0 )
				// Get and return the separator (section) item view
				return sectionAdapter.getView ( sectionIndex , convertView , parent );
			if ( position < size )
				// Get and return the adapter's item view
				return adapter.getView ( position - 1 , convertView , parent );

			// Otherwise the required position does not lie in this section
			position -= size;
			sectionIndex ++;
		} // End for each
		// Invalid position
		return null;
	}

	/*
	 * Get the row id associated with the specified position in the list
	 *
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId ( int position ) {
		// Use the item's position as ID
		return position;
	}
	
	/**
	 * Computes and return the separator item view position of the provided section index.<br>
	 * The result is used in combination with {@link android.widget.ListView#setSelection(int) ListView.setSelection(int)}.
	 * 
	 * @param section
	 */
	public int setSelection ( int section ) {
		// Declare and initialize an integer used to keep track of the section index
		int sectionIndex = 0;
		// Compute the separator item view position
		int position = 0;
		// Iterate over all sections
		for ( Object _section : this.sections.keySet () ) {
			// Determine if the required section is reached
			if ( sectionIndex < section ) {
				// Section not reached
				sectionIndex ++;
				// Get the current adapter
				Adapter adapter = sections.get ( _section );
				// Compute and add the number of items for the current adapter (including the separator item / section)
				position += adapter.getCount () + 1;
			} // End if
			else
				// Section reached, exit loop
				break;
		} // End for each
		// Return the separator item view position
		return position;
	}

}