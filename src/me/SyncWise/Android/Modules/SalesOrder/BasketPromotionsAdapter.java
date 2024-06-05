/**
 * Copyright 2015 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.SalesOrder;

import java.util.ArrayList;
import java.util.List;

import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

/**
 * A concrete BaseAdapter that is backed by an array of {@link me.SyncWise.Android.Modules.SalesInvoice.BasketPromotion BasketPromotion} objects.
 * 
 * @author Elias
 *
 */
public class BasketPromotionsAdapter extends BaseExpandableListAdapter {

	/**
	 * {@link android.view.LayoutInflater} used to instantiate a layout XML file into its corresponding View objects.
	 */
	private final LayoutInflater layoutInflater;
	
	/**
	 * XML Layout (layout) resource id for group views.
	 */
	private final int groupLayout;
	
	/**
	 * XML Layout (layout) resource id for child views.
	 */
	private final int childLayout;
	
	/**
	 * List of {@link me.SyncWise.Android.Modules.SalesInvoice.BasketPromotion BasketPromotion} objects used to define the basket promotions along with their placed quantities.
	 */
	private final List < BasketPromotion > basketPromotions;
	
	/**
	 * Nested class implementation of the view holder pattern used to keep references to children views in order to avoid unnecessary calls to {@link android.view.View#findViewById(int) findViewById(id)} on each row.
	 * 
	 * @author Elias
	 *
	 */
	public static class GroupViewHolder {
		public TextView description;
		public TextView quantity;
	}
	
	/**
	 * Nested class implementation of the view holder pattern used to keep references to children views in order to avoid unnecessary calls to {@link android.view.View#findViewById(int) findViewById(id)} on each row.
	 * 
	 * @author Elias
	 *
	 */
	public static class ChildViewHolder {
		public TextView description;
	}
	
	/**
	 * String used to host the quantity label
	 */
	private final String quantityLabel;
	
	/**
	 * Constructor.
	 * 
	 * @param context	The application context.
	 * @param groupLayout	XML Layout (layout) resource id for group views.
	 * @param childLayout	XML Layout (layout) resource id for child views.
	 * @param basketPromotions	List of {@link me.SyncWise.Android.Modules.SalesInvoice.BasketPromotion BasketPromotion} objects holding the basket promotions along with their placed quantities.
	 */
	public BasketPromotionsAdapter ( final Context context , final int groupLayout , final int childLayout , final List < BasketPromotion > basketPromotions ) {
		// Initialize attributes
		layoutInflater = LayoutInflater.from ( context );
		this.groupLayout = groupLayout;
		this.childLayout = childLayout;
		this.basketPromotions = basketPromotions;
		quantityLabel = AppResources.getInstance ( context ).getString ( context , R.string.quantity_label ) + " : ";
	}
	
	/*
	 * Gets the number of groups.
	 *
	 * @see android.widget.ExpandableListAdapter#getGroupCount()
	 */
	@Override
	public int getGroupCount () {
		// Return the number of basket promotions
		return basketPromotions.size ();
	}

	/*
	 * Gets the data associated with the given group.
	 *
	 * @see android.widget.ExpandableListAdapter#getGroup(int)
	 */
	@Override
	public Object getGroup ( int groupPosition ) {
		try {
			// Return the basket promotion at the indicated position
			return basketPromotions.get ( groupPosition );
		} catch ( Exception exception ) {
			// Invalid group position
			return null;
		} // End of try-catch block
	}
	
	/*
	 * Gets the ID for the group at the given position.
	 *
	 * @see android.widget.ExpandableListAdapter#getGroupId(int)
	 */
	@Override
	public long getGroupId ( int groupPosition ) {
		// Return the group position (since it is unique across groups)
		return groupPosition;
	}
	
	/*
	 * Gets the number of children in a specified group.
	 *
	 * @see android.widget.ExpandableListAdapter#getChildrenCount(int)
	 */
	@Override
	public int getChildrenCount ( int groupPosition ) {
		try {
			// Return the number of promotion details for the current basket
			return ( (BasketPromotion) getGroup ( groupPosition ) ).getPromotionDetails ().size ();
		} catch ( Exception exception ) {
			// Invalid group position
			return 0;
		} // End of try-catch block
	}

	/*
	 * Gets the data associated with the given child within the given group.
	 *
	 * @see android.widget.ExpandableListAdapter#getChild(int, int)
	 */
	@Override
	public Object getChild ( int groupPosition , int childPosition ) {
		try {
			// Return the current promotion detail for the current basket at the indicated position
			return ( (BasketPromotion) getGroup ( groupPosition ) ).getPromotionDetails ().get ( childPosition );
		} catch ( Exception exception ) {
			// Invalid group position
			return null;
		} // End of try-catch block
	}
	
	/**
	 * Gets the child invoice reference.
	 * 
	 * @param groupPosition	Integer holding the group index.
	 * @param childPosition	Integer holding the child index.
	 * @return	A {@link .SyncWise.Android.Modules.SalesInvoice.Invoice Invoice} reference object used to compute and display various information for the promotion detail.
	 */
	public Order getChildInvoiceReference ( int groupPosition , int childPosition ) {
		try {
			// Return the invoice for the current promotion detail of the basket at the indicated position
			return ( (BasketPromotion) getGroup ( groupPosition ) ).getInvoices ().get ( childPosition );
		} catch ( Exception exception ) {
			// Invalid group position
			return null;
		} // End of try-catch block
	}
 
	/*
	 * Gets the ID for the given child within the given group.
	 *
	 * @see android.widget.ExpandableListAdapter#getChildId(int, int)
	 */
	@Override
	public long getChildId ( int groupPosition , int childPosition ) {
	
		return childPosition;
	}

	/*
	 * Indicates whether the child and group IDs are stable across changes to the underlying.
	 *
	 * @see android.widget.ExpandableListAdapter#hasStableIds()
	 */
	@Override
	public boolean hasStableIds () {
		// Indicate that the same ID always refers to the same object
		return true;
	}
	
	/*
	 * Whether the child at the specified position is selectable.
	 *
	 * @see android.widget.ExpandableListAdapter#isChildSelectable(int, int)
	 */
	@Override
	public boolean isChildSelectable ( int groupPosition , int childPosition ) {
		// Indicate that the child is NOT selectable
		return false;
	}
	
	/*
	 * Gets a View that displays the given group.
	 *
	 * @see android.widget.ExpandableListAdapter#getGroupView(int, boolean, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getGroupView ( int groupPosition , boolean isExpanded , View convertView , ViewGroup parent ) {
		// Declare the row view holder
		GroupViewHolder viewHolder;
		// Check if an inflated view is provided
		if ( convertView == null ) {
			// A new view must be inflated
			convertView = layoutInflater.inflate ( groupLayout , null );
			// Declare and initialize a view holder
			viewHolder = new GroupViewHolder ();
			// Retrieve a reference to the basket description label
			viewHolder.description = (TextView) convertView.findViewById ( R.id.label_basket );
			// Retrieve a reference to the basket quantity label
			viewHolder.quantity = (TextView) convertView.findViewById ( R.id.label_basket_quantity );
			// Store the view holder as tag
			convertView.setTag ( viewHolder );
		} // End if
		else
			// An inflated view is already provided, retrieve the stored view holder
			viewHolder = (GroupViewHolder) convertView.getTag ();
		
		// Retrieve the basket object
		BasketPromotion basketPromotion = (BasketPromotion) getGroup ( groupPosition );
		// Display the basket description
		viewHolder.description.setText ( basketPromotion.getPromotionHeader ().getPromotionName () );
		// Display the basket quantity
		viewHolder.quantity.setText ( quantityLabel + String.valueOf ( basketPromotion.getQuantity () ) );
		
		// Return the view
		return convertView;
	}
 
	/*
	 * Gets a View that displays the data for the given child within the given group.
	 *
	 * @see android.widget.ExpandableListAdapter#getChildView(int, int, boolean, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getChildView ( int groupPosition , int childPosition , boolean isLastChild , View convertView , ViewGroup parent ) {
		// Declare the row view holder
		ChildViewHolder viewHolder;
		// Check if an inflated view is provided
		if ( convertView == null ) {
			// A new view must be inflated
			convertView = layoutInflater.inflate ( childLayout , null );
			// Declare and initialize a view holder
			viewHolder = new ChildViewHolder ();
			// Retrieve a reference to the item description label
			viewHolder.description = (TextView) convertView.findViewById ( R.id.label_item_description );
			// Store the view holder as tag
			convertView.setTag ( viewHolder );
		} // End if
		else
			// An inflated view is already provided, retrieve the stored view holder
			viewHolder = (ChildViewHolder) convertView.getTag ();
		
		// Retrieve the basket object
		BasketPromotion basketPromotion = (BasketPromotion) getGroup ( groupPosition );
		// Display the item description
		viewHolder.description.setText ( basketPromotion.getInvoices ().get ( childPosition ).getItem ().getItemName () );
		
		// Return the view
		return convertView;
	}
	
}