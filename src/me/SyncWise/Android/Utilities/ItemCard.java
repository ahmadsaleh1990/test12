/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Utilities;

import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.Items;
import android.app.Activity;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

/**
 * Utility class that provides helper methods related to the item card.
 * 
 * @author Elias
 *
 */
public class ItemCard {
	public static final String ITEM_PICTURES_DIRECTORY = "Syncwise";
	/**
	 * String holding the default value used to indicate whether or not the user is allowed to use item barcodes.
	 */
	public static final String DEFAULT_ITEM_BARCODES = "N";

	/**
	 * Initializes the item card using the provided item object.
	 * <ul>
	 * <li>The item name is displayed.</li>
	 * </ul>
	 * 
	 * @param activity	Reference to the activity that contains the item card.
	 * @param item	{@link me.SyncWise.Android.Database.Items Items} object holding a reference to the current item.
	 */
	public static void initializeItemCard ( final Activity activity , final Items item ) {
		try {
			// Retrieve a reference to the layout item card
			FrameLayout layout = (FrameLayout) activity.findViewById ( R.id.layout_item_card );
			
			// Initialize the code label
			String codeLabel = AppResources.getInstance ( activity ).getString ( activity , R.string.code_label );
			
			// Build a spannable string out of the item name and code in order to apply various spans
			SpannableString itemData = new SpannableString ( item.getItemName () + "\n" + codeLabel + " : " + item.getItemCode () );
			// Apply color span
			itemData.setSpan ( new ForegroundColorSpan ( Color.BLUE ) , itemData.length () - item.getItemCode ().length () , itemData.length () , 0 );
			
			// Display the item data
			( (TextView) layout.findViewById ( R.id.label_item_name ) ).setText ( itemData , BufferType.SPANNABLE );
		} catch ( Exception exception ) {
			// Do nothing
		} // End of try-catch block
	}
	
}