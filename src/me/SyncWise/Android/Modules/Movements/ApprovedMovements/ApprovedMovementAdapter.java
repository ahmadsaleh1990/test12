/**
 * Copyright 2015 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Movements.ApprovedMovements;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView.BufferType;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.IsProcessedUtils;
import me.SyncWise.Android.Database.MovementHeadersDao;
import me.SyncWise.Android.Database.MovementHeadersUtils;
import me.SyncWise.Android.Database.StatusUtils;
import me.SyncWise.Android.Modules.Movements.MovementAdapter;
import me.SyncWise.Android.Modules.Movements.MovementSettings;

/**
 * Adapter that exposes data from a Cursor to a ListView widget.
 * 
 * @author Elias
 *
 */
public class ApprovedMovementAdapter extends MovementAdapter {

	/**
	 * String used to hold the pending label.
	 */
	private final String pendingLabel;
	
	/**
	 * String used to hold the loaded label.
	 */
	private final String loadedLabel;
	
	/**
	 * String used to hold the unloaded label.
	 */
	private final String unloadedLabel;
	
	/**
	 * Constructor
	 * 
	 * @param context	The application context.
	 * @param cursor	Cursor holding a result set returned by a database query.
	 * @param layout	Integer holding the item layout resource id.
	 * @param movementSettings	A {@link me.SyncWise.Android.Modules.Movements.MovementSettings MovementSettings} object.
	 */
	public ApprovedMovementAdapter ( Context context , Cursor cursor , int layout , MovementSettings movementSettings ) {
		// Superclass method call
		super ( context , cursor , layout , movementSettings );
		// Initialize the attributes
		pendingLabel = AppResources.getInstance ( context ).getString ( context , R.string.movement_status_pending_label );
		loadedLabel = AppResources.getInstance ( context ).getString ( context , R.string.movement_status_loaded_label );
		unloadedLabel = AppResources.getInstance ( context ).getString ( context , R.string.movement_status_unloaded_label );
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
		// Retrieve the view holder
		ViewHolder viewHolder = (ViewHolder) view.getTag ();
		
		// Determine if the current movement is deleted
		boolean isDeleted = StatusUtils.isDeleted () == cursor.getInt ( cursor.getColumnIndex ( MovementHeadersDao.Properties.MovementStatus.columnName ) );
		// Determine if the current movement is processed
		boolean isProcessed = IsProcessedUtils.isSync () == cursor.getInt ( cursor.getColumnIndex ( MovementHeadersDao.Properties.IsProcessed.columnName ) );
		// Determine if the current movement is applied
		boolean isApplied = IsProcessedUtils.isWebProcessed () != cursor.getInt ( cursor.getColumnIndex ( MovementHeadersDao.Properties.IsProcessed.columnName ) );
		// Display / hide the check box accordingly
		viewHolder.checkBox.setVisibility ( isProcessed ? View.VISIBLE : View.GONE );
		// Display / hide the sync icon accordingly
		viewHolder.sync.setVisibility ( ! isProcessed ? View.VISIBLE : View.GONE );
		// Determine if the current movement is final
		// A movement is final if it is applied (not pending)
		viewHolder.isFinal = isApplied;
		// Check if the movement is not deleted
		if ( ! isDeleted ) {
			// Build an additional spannable string
			SpannableString additional = null;
			// Check if the movement is pending
			if ( ! isApplied ) {
				// Build the pending label
				additional = new SpannableString ( statusLabel + pendingLabel );
				// Apply style span
				additional.setSpan ( new StyleSpan ( Typeface.BOLD ) , statusLabel.length () , additional.length () , 0 );
				// Apply foreground color span
				additional.setSpan ( new ForegroundColorSpan ( Color.BLUE ) , statusLabel.length () , additional.length () , 0 );
				// Display the movement data label
				viewHolder.data.setText ( TextUtils.concat ( viewHolder.data.getText () , newLine , additional ) , BufferType.SPANNABLE );
			} // End if
			else {
				// Build the applied label
				additional = new SpannableString ( statusLabel + ( movementSettings.getMovementType () == MovementHeadersUtils.Type.LOAD ? loadedLabel : unloadedLabel ) );
				// Apply style span
				additional.setSpan ( new StyleSpan ( Typeface.BOLD ) , statusLabel.length () , additional.length () , 0 );
				// Apply foreground color span
				additional.setSpan ( new ForegroundColorSpan ( Color.BLUE ) , statusLabel.length () , additional.length () , 0 );
				// Display the movement data label
				viewHolder.data.setText ( TextUtils.concat ( viewHolder.data.getText () , newLine , additional ) , BufferType.SPANNABLE );
			} // End if
		} // End if
	}

}