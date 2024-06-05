/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.MenuList;

import java.util.List;

import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A concrete BaseAdapter that is backed by an array of {@link me.SyncWise.Android.Modules.MenuList.MenuItem MenuItem} objects.
 * 
 * @author Elias
 *
 */
public class MenuListAdapter extends ArrayAdapter < MenuItem > {
	
	/**
	 * {@link android.view.LayoutInflater LayoutInflater} used to instantiate a layout XML file into its corresponding View objects.
	 */
	private final LayoutInflater layoutInflater;
	
	/**
	 * XML Layout (layout) resource id.
	 */
	private final int layout;
	
	/**
	 * Nested class implementation of the view holder pattern used to keep references to children views in order to avoid unnecessary calls to {@link android.view.View#findViewById(int) findViewById(id)} on each row.
	 * 
	 * @author Elias
	 *
	 */
	public static class ViewHolder {
		public ImageView icon;
		public TextView title;
		public TextView description;
		public TextView notification;
	}

	/**
	 * Constructor.
	 * 
	 * @param context	The application context.
	 * @param layout	XML Layout (layout) resource id.
	 * @param menuItems	List of {@link me.SyncWise.Android.Modules.MenuList.MenuItem MenuItem} objects.
	 */
	public MenuListAdapter ( Context context, int layout , List < MenuItem > menuItems ) {
		// Superclass method call
		super ( context , layout , menuItems );
		// Initialize the attributes
		layoutInflater = LayoutInflater.from ( context );
		this.layout = layout;
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
			// Initialize the view holder
			viewHolder = new ViewHolder ();
			// Retrieve a reference to the icon
			viewHolder.icon = (ImageView) convertView.findViewById ( R.id.icon_menu_item );
			// Retrieve a reference to the title
			viewHolder.title = (TextView) convertView.findViewById ( R.id.label_menu_title );
			// Retrieve a reference to the description
			viewHolder.description = (TextView) convertView.findViewById ( R.id.label_menu_description );
			// Retrieve a reference to the notification number
			viewHolder.notification = (TextView) convertView.findViewById ( R.id.label_notification_number );
			// Store the view holder as tag
			convertView.setTag ( viewHolder );
		} // End if
		else
			// An inflated view is already provided, retrieve the stored view holder
			viewHolder = (ViewHolder) convertView.getTag ();
		
		// Set the title text
		viewHolder.title.setText ( AppResources.getInstance ( getContext () ).getString ( getContext () , getItem ( position ).getTitleId () ) );
		// Set the description text
		viewHolder.description.setText ( AppResources.getInstance ( getContext () ).getString ( getContext () , getItem ( position ).getDescriptionId () ) );
		// Set the icon drawable
		viewHolder.icon.setImageResource ( getItem ( position ).getIconId () == null ? R.drawable.menu : getItem ( position ).getIconId () );
		// Set the icon alpha accordingly
		viewHolder.icon.setAlpha ( getItem ( position ).getDimIcon () ? 0.5f : 1f );
		// Determine if there is a valid notification number
		if ( getItem ( position ).getNotificationNumber () == 0 )
			// Hide the notification number
			viewHolder.notification.setVisibility ( View.GONE );
		// Determine if the notification number is greater than 99
		else if ( getItem ( position ).getNotificationNumber () > 99 ) {
			// Indicate a large number of notifications
			// Display the notification number
			viewHolder.notification.setVisibility ( View.VISIBLE );
			viewHolder.notification.setText ( ".." );
		} // End else if
		else {
			// Display the notification number
			viewHolder.notification.setVisibility ( View.VISIBLE );
			viewHolder.notification.setText ( String.valueOf ( getItem ( position ).getNotificationNumber () ) );
		} // End else
		// Return the view
		return convertView;
	}
	
}