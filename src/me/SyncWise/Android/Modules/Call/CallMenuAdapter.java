/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Call;

import java.util.List;

import me.SyncWise.Android.R;
import me.SyncWise.Android.Modules.MenuList.MenuItem;
import me.SyncWise.Android.Modules.MenuList.MenuListAdapter;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

/**
 * A concrete BaseAdapter that is backed by an array of {@link me.SyncWise.Android.Modules.MenuList.MenuItem MenuItem} objects.
 * 
 * @author Elias
 *
 */
public class CallMenuAdapter extends MenuListAdapter {

	/**
	 * Constructor.
	 * 
	 * @param context	The application context.
	 * @param layout	XML Layout (layout) resource id.
	 * @param menuItems	List of {@link me.SyncWise.Android.Modules.MenuList.MenuItem MenuItem} objects.
	 */
	public CallMenuAdapter ( Context context , int layout , List < MenuItem > menuItems ) {
		// Superclass method call
		super ( context , layout , menuItems );
	}

	/**
	 * Nested class implementation of the view holder pattern used to keep references to children views in order to avoid unnecessary calls to {@link android.view.View#findViewById(int) findViewById(id)} on each row.
	 * 
	 * @author Elias
	 *
	 */
	static class ViewHolder extends MenuListAdapter.ViewHolder {
		public ImageView arrow;
		public CheckBox checkBox;
		public ImageView disabled;
	}
	
	/*
	 * Get a View that displays the data at the specified position in the data set.
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView ( int position , View convertView , ViewGroup parent ) {
		// Superclass method call
		convertView = super.getView ( position , convertView , parent );
		
		// Declare and initialize a view holder
		ViewHolder viewHolder = new ViewHolder ();
		viewHolder.icon = ( (MenuListAdapter.ViewHolder) convertView.getTag () ).icon;
		viewHolder.title = ( (MenuListAdapter.ViewHolder) convertView.getTag () ).title;
		viewHolder.description = ( (MenuListAdapter.ViewHolder) convertView.getTag () ).description;
		viewHolder.notification = ( (MenuListAdapter.ViewHolder) convertView.getTag () ).notification;
		// Retrieve a reference to the arrow
		viewHolder.arrow = (ImageView) convertView.findViewById ( R.id.arrow_right );
		// Retrieve a reference to the check box
		viewHolder.checkBox = (CheckBox) convertView.findViewById ( R.id.checkbox );
		// Retrieve a reference to the disabled icon
		viewHolder.disabled = (ImageView) convertView.findViewById ( R.id.icon_disabled );
		// Store the view holder as tag
		convertView.setTag ( viewHolder );
		
		// Display / hide the arrow accordingly
		viewHolder.arrow.setVisibility ( ( (CallMenuItem) getItem ( position ) ).isDisabled () ? View.GONE : ( (CallMenuItem) getItem ( position ) ).getActionPerformed () ? View.GONE : View.VISIBLE );
		// Display / hide the check box accordingly
		viewHolder.checkBox.setVisibility ( ( (CallMenuItem) getItem ( position ) ).isDisabled () ? View.GONE : ( (CallMenuItem) getItem ( position ) ).getActionPerformed () ? View.VISIBLE : View.GONE );
		// Display / hide the disabled icon accordingly
		viewHolder.disabled.setVisibility ( ( (CallMenuItem) getItem ( position ) ).isDisabled () ? View.VISIBLE : View.GONE );
		// Return the view
		return convertView;
	}
	
}