package me.SyncWise.Android.Modules.SalesInvoice;

import java.util.List;

import me.SyncWise.Android.R;
 


import android.content.Context;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
 

public class CashInvoiceAdapter extends ArrayAdapter < InvoiceInfo >  {
	
 
	/**
	 * {@link android.view.LayoutInflater} used to instantiate a layout XML file into its corresponding View objects.
	 */
	private final LayoutInflater layoutInflater;
	
	/**
	 * XML Layout (layout) resource id.
	 */
	private final int layout;
     
 	public static class ViewHolder {
 		public InvoiceInfo order;
 		public int position;
 		public CheckBox chkbox;
  
 	}
 	public CashInvoiceAdapter ( Context context , int layout , List < InvoiceInfo > salesInvoiceDetailItems ) {
		// Superclass method call
		super ( context , layout , salesInvoiceDetailItems );
		// Initialize the attributes
		layoutInflater = LayoutInflater.from ( context );
		this.layout = layout;
	}

	

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
           // Your Code
    	ViewHolder viewHolder;
    	if ( convertView == null ) {
			// A new view must be inflated
			convertView = layoutInflater.inflate ( layout , null );
			// Declare and initialize a view holder
			viewHolder = new ViewHolder ();
			// Retrieve a reference to the description label
			viewHolder.chkbox = (CheckBox) convertView.findViewById ( R.id.chkIos );
		
			// Retrieve a reference to the value label
			 
			// Store the view holder as tag
			convertView.setTag ( viewHolder );
		} // End if
		else
			// An inflated view is already provided, retrieve the stored view holder
			viewHolder = (ViewHolder) convertView.getTag ();
    	
    	// Set the current order
		viewHolder.order = getItem ( position );
		// Set the current position
		viewHolder.position = position;
	
    	viewHolder.chkbox.setOnCheckedChangeListener ( null );
		viewHolder.chkbox.setChecked ( viewHolder.order.getValue().equals("1") ? true : false );
    	//	viewHolder.chkbox.setOnCheckedChangeListener ( null );
		final InvoiceInfo finalOrder = viewHolder.order;
		

		
		
		
		viewHolder.chkbox.setOnCheckedChangeListener ( new OnCheckedChangeListener () {
			@Override
			public void onCheckedChanged ( CompoundButton buttonView , boolean isChecked ) {
				if ( isChecked  ) 
				{
					finalOrder.setValue("1");
			 
				 
			 
				}
				else if ( ! isChecked  ) {
					finalOrder.setValue("");
				 
				}
			 
			}
		} );
    	
    	
    	
    	
//            if(position==selected_position)
//            {
//            	viewHolder.chkbox.setChecked(true);
//            }
//            else
//            {
//            	viewHolder.chkbox.setChecked(false);
//            }

//            viewHolder.chkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    if(isChecked)
//                    {
//                        selected_position =  position;
//                    }
//                    else{
//                         selected_position = -1;
//                    }
//                    notifyDataSetChanged();
//                }
//            });
            return convertView;


        }
}
