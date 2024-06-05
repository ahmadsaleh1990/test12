/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.CyclePlanning;

import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.CheckBox;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Modules.ClientsList.ClientsCursorAdapter;

/**
 * Adapter that exposes data from a Cursor to a ListView widget.
 * 
 * @author Elias
 *
 */
public class AddCallAdapter extends ClientsCursorAdapter {
	
	/**
	 * List of strings holding the codes of the selected clients.
	 */
	private List < String > selectedClients;
	
	/**
	 * Nested class implementation of the view holder pattern used to keep references to children views in order to avoid unnecessary calls to {@link android.view.View#findViewById(int) findViewById(id)} on each row.
	 * 
	 * @author Elias
	 *
	 */
	static class ViewHolder extends ClientsCursorAdapter.ViewHolder {
		public CheckBox checkBox;
	}
	
	/**
	 * Constructor
	 * 
	 * @param context	The application context.
	 * @param cursor	Cursor holding a result set returned by a database query.
	 * @param layout	Integer holding the item layout resource id.
	 * @param selectedClients	List of strings hosting the selected clients.
	 */
	public AddCallAdapter ( Context context , Cursor cursor , int layout , List < String > selectedClients ) {
		// Superclass method call
		super ( context , cursor , layout );
		// Initialize attributes
		this.selectedClients = selectedClients;
	}

	/*
	 * Makes a new view to hold the data pointed to by cursor.
	 *
	 * @see android.widget.CursorAdapter#bindView(android.view.View, android.content.Context, android.database.Cursor)
	 */
	@Override
	public void bindView ( View view , Context context , Cursor cursor ) {
		// Superclass method call
		super.bindView ( view , context , cursor );
		
		// Declare and initialize a view holder
		ViewHolder viewHolder = new ViewHolder ();
		viewHolder.clientCode = ( (ClientsCursorAdapter.ViewHolder) view.getTag () ).clientCode;
		viewHolder.divisionCode = ( (ClientsCursorAdapter.ViewHolder) view.getTag () ).divisionCode;
		viewHolder.companyCode = ( (ClientsCursorAdapter.ViewHolder) view.getTag () ).companyCode;
		viewHolder.data = ( (ClientsCursorAdapter.ViewHolder) view.getTag () ).data;
		viewHolder.name = ( (ClientsCursorAdapter.ViewHolder) view.getTag () ).name;
		// Retrieve a reference to the check box
		viewHolder.checkBox = (CheckBox) view.findViewById ( R.id.checkbox );
		// Store the view holder as tag
		view.setTag ( viewHolder );
		
		// Check if the current client is selected and check the check box accordingly 
		viewHolder.checkBox.setChecked ( selectedClients.contains ( viewHolder.clientCode + "--" + viewHolder.divisionCode ) );
	}
	
}