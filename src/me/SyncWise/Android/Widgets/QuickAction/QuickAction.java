/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Widgets.QuickAction;

import me.SyncWise.Android.R;
import android.content.Context;
import android.content.res.Resources;

import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ScrollView;
import android.widget.RelativeLayout;
import android.widget.PopupWindow.OnDismissListener;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;

import java.util.List;
import java.util.ArrayList;

/**
 * QuickAction dialog, shows action list as icon and text like the one in Gallery3D app. Currently supports vertical 
 * and horizontal layout.
 * 
 * @author Lorensius W. L. T <lorenz@londatiga.net>
 * 
 * Contributors:
 * - Kevin Peck <kevinwpeck@gmail.com>
 */
public class QuickAction extends PopupWindows implements OnDismissListener {
	
	private Object object;
	private View anchor;
	private View rootView;
	private ImageView arrowUp;
	private ImageView arrowDown;
	private LayoutInflater layoutInflater;
	private ViewGroup track;
	private ScrollView scroller;
	private OnActionItemClickListener itemClickListener;
	private OnDismissListener dismissListener;
	
	private List<ActionItem> actionItems = new ArrayList<ActionItem>();
	
	private boolean didAction;
	
	private int childPosition;
    private int insertPosition;
    private int animationStyle;
    private int orientation;
    private int rootWidth=0;
    // QuickAction Orientations
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    // QuickAction Animations
    public static final int ANIM_GROW_FROM_LEFT = 1;
	public static final int ANIM_GROW_FROM_RIGHT = 2;
	public static final int ANIM_GROW_FROM_CENTER = 3;
	public static final int ANIM_REFLECT = 4;
	public static final int ANIM_AUTO = 5;
	
    /**
     * Constructor for default vertical layout
     * 
     * @param context  Context
     */
    public QuickAction ( Context context ) {
        this ( context , VERTICAL );
    }

    /**
     * Constructor allowing orientation override
     * 
     * @param context    Context
     * @param orientation Layout orientation, can be vertical or horizontal
     */
    public QuickAction ( Context context , int orientation ) {
    	// Superclass method call
        super ( context );
        // Set the orientation
        this.orientation = orientation;
        // Initialize the layout inflater
        layoutInflater = (LayoutInflater) context.getSystemService ( Context.LAYOUT_INFLATER_SERVICE );
        // Apply the orientation
        if ( orientation == HORIZONTAL ) {
            setRootViewId ( R.layout.popup_horizontal );
        } // End if
        else {
            setRootViewId ( R.layout.popup_vertical );
        } // End else
        // Apply animation style
        animationStyle 	= ANIM_AUTO;
        // Reset the child position
        childPosition 	= 0;
    }

    /**
     * Get action item at an index
     * 
     * @param index  Index of item (position from callback)
     * 
     * @return  Action Item at the position
     */
    public ActionItem getActionItem ( int index ) {
        return actionItems.get ( index );
    }
    
	/**
	 * Set root view.
	 * 
	 * @param id Layout resource id
	 */
	public void setRootViewId ( int id ) {
		// Retrieve a reference to the root view
		rootView	= (ViewGroup) layoutInflater.inflate ( id , null );
		// Retrieve a reference to tracks holder
		track 		= (ViewGroup) rootView.findViewById ( R.id.tracks );
		// Retrieve a reference to the down arrow
		arrowDown 	= (ImageView) rootView.findViewById ( R.id.arrow_down );
		// Retrieve a reference to the up arrow
		arrowUp 	= (ImageView) rootView.findViewById ( R.id.arrow_up );
		// Retrieve a reference to the scroller
		scroller	= (ScrollView) rootView.findViewById ( R.id.scroller );
		// This was previously defined on show() method, moved here to prevent force close that occurred
		// when tapping fastly on a view to show quickaction dialog.
		// Thanx to zammbi (github.com/zammbi)
		rootView.setLayoutParams ( new LayoutParams ( LayoutParams.WRAP_CONTENT , LayoutParams.WRAP_CONTENT ) );
		// Set the custom layout
		setContentView ( rootView );
	}
	
	/**
	 * Set animation style
	 * 
	 * @param animationStyle animation style, default is set to ANIM_AUTO
	 */
	public void setAnimStyle ( int animationStyle ) {
		this.animationStyle = animationStyle;
	}
	
	/**
	 * Set listener for action item clicked.
	 * 
	 * @param listener Listener
	 */
	public void setOnActionItemClickListener ( OnActionItemClickListener listener ) {
		itemClickListener = listener;
	}
	
	/**
	 * Add action item
	 * 
	 * @param action  {@link ActionItem}
	 */
	public void addActionItem ( ActionItem action ) {
		// Add the new action to the list
		actionItems.add ( action );
		// Retrieve a reference to the action's title
		String title 	= action.getTitle ();
		// Retrieve a reference to the action's icon
		Drawable icon 	= action.getIcon ();
		// Declare and initialize a view holding the container of the action item
		View container;
		// Determine the orientation and inflate with the appropriate layout accordingly
		if ( orientation == HORIZONTAL ) {
            container = layoutInflater.inflate ( R.layout.action_item_horizontal , null );
        } // End if
		else {
            container = layoutInflater.inflate ( R.layout.action_item_vertical , null );
        } // End else
		// Retrieve a reference to the action item icon
		ImageView img 	= (ImageView) container.findViewById ( R.id.iv_icon );
		// Retrieve a reference to the action item text
		TextView text 	= (TextView) container.findViewById ( R.id.tv_title );
		// Determine if the action item has an icon
		if ( icon != null ) {
			// Display the icon
			img.setImageDrawable ( icon );
		} // End if
		else {
			// The action item has no icon
			img.setVisibility ( View.GONE );
		} // End else
		// Determine if the action item has a title
		if ( title != null ) {
			// Display the title
			text.setText ( title );
		} // End if
		else {
			// The action item has no title
			text.setVisibility ( View.GONE );
		} // End else
		// Store the position and ID
		final int position	=  childPosition;
		final int actionId 	= action.getActionId ();
		// Assign a click listener to the action item container
		container.setOnClickListener ( new OnClickListener () {
			public void onClick ( View view ) {
				// Check if the item click listener is valid
				if ( itemClickListener != null ) {
					// Perform action item click
                    itemClickListener.onItemClick ( QuickAction.this , anchor , object , position , actionId );
                } // End if
				// Check if the action item is NOT sticky (must be dismissed after a click)
                if ( ! getActionItem ( position ).isSticky () ) {
                	didAction = true;
                	// Dismiss the quick action
                    dismiss ();
                } // End if
			} // End of onClick
		} );
		// Make sure the action item container is enabled
		container.setFocusable ( true );
		container.setClickable ( true );
		// Check if a horizontal separator is needed
		if ( orientation == HORIZONTAL && childPosition != 0 ) {
			// Build a separator view
            View separator = layoutInflater.inflate ( R.layout.horiz_separator , null );
            // Set the layout parameters of the separator
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams ( LayoutParams.WRAP_CONTENT , LayoutParams.MATCH_PARENT );
            separator.setLayoutParams ( params );
            separator.setPadding ( 5 , 0 , 5 , 0 );
            // Add the separator to the quick action
            track.addView ( separator , insertPosition );
            insertPosition ++;
        } // End if
		// Add the action item to the quick action
		track.addView ( container , insertPosition );
		childPosition ++;
		insertPosition ++;
	}
	
	/**
	 * Determines if the current quick action contains an action item with the provided action ID.
	 * 
	 * @param actionId	Integer holding the required action ID.
	 * @return	Boolean indicating whether the quick action contains an action item with the provided action ID or not.
	 */
	public boolean containsActionItem ( final int actionId ) {
		// Check if the action items list is valid
		if ( actionItems == null )
			// Invalid list
			return false;
		// Iterate over all action items
		for ( ActionItem actionItem : actionItems )
			// Match action IDs
			if ( actionItem.getActionId () == actionId )
				// The quick action contains an action with the provided action ID
				return true;
		// Otherwise the quick action does NOT contain an action with the provided action ID
		return false;
	}
	
	/**
	 * Show quickaction popup. Popup is automatically positioned, on top or bottom of anchor view.
	 * 
	 */
	public void show ( View anchor , Object object , Resources resources ) {
		preShow ( resources );
		
		this.anchor = anchor;
		this.object = object;
		
		int xPos, yPos, arrowPos;
		
		didAction 			= false;
		
		int[] location 		= new int [2];
	
		anchor.getLocationOnScreen ( location );
		
		Rect anchorRect 	= new Rect ( location [0] , location [1] , location [0] + anchor.getWidth (), location [1] 
		                	+ anchor.getHeight () );

		//rootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		rootView.measure ( LayoutParams.WRAP_CONTENT , LayoutParams.WRAP_CONTENT );
	
		int rootHeight 		= rootView.getMeasuredHeight ();
		
		if ( rootWidth == 0 ) {
			rootWidth		= rootView.getMeasuredWidth ();
		}
		
		Point screenSize = new Point ();
		windowManager.getDefaultDisplay ().getSize ( screenSize );
		
		int screenWidth 	= screenSize.x;
		int screenHeight	= screenSize.y;
		
		//automatically get X coord of popup (top left)
		if ( ( anchorRect.left + rootWidth ) > screenWidth ) {
			xPos 		= anchorRect.left - (rootWidth-anchor.getWidth () );			
			xPos 		= ( xPos < 0 ) ? 0 : xPos;
			
			arrowPos 	= anchorRect.centerX () - xPos;
			
		} else {
			if ( anchor.getWidth () > rootWidth ) {
				xPos = anchorRect.centerX () - ( rootWidth / 2 );
			} else {
				xPos = anchorRect.left;
			}
			
			arrowPos = anchorRect.centerX () - xPos;
		}
		
		int dyTop			= anchorRect.top;
		int dyBottom		= screenHeight - anchorRect.bottom;

		boolean onTop		= ( dyTop > dyBottom ) ? true : false;

		if ( onTop ) {
			if ( rootHeight > dyTop ) {
				yPos 			= 15;
				LayoutParams l 	= scroller.getLayoutParams ();
				l.height		= dyTop - anchor.getHeight ();
			} else {
				yPos = anchorRect.top - rootHeight;
			}
		} else {
			yPos = anchorRect.bottom;
			
			if ( rootHeight > dyBottom ) { 
				LayoutParams l 	= scroller.getLayoutParams ();
				l.height		= dyBottom;
			}
		}
		
		showArrow ( ( ( onTop ) ? R.id.arrow_down : R.id.arrow_up ) , arrowPos );
		
		setAnimationStyle ( screenWidth , anchorRect.centerX () , onTop );
		
		window.showAtLocation ( anchor , Gravity.NO_GRAVITY , xPos , yPos );
	}
	
	/**
	 * Gets the quick action's anchor
	 * 
	 * @return	Reference holding the quick actions' anchor view.
	 */
	public View getView () {
		return anchor;
	}
	
	/**
	 * Set animation style
	 * 
	 * @param screenWidth screen width
	 * @param requestedX distance from left edge
	 * @param onTop flag to indicate where the popup should be displayed. Set TRUE if displayed on top of anchor view
	 * 		  and vice versa
	 */
	private void setAnimationStyle ( int screenWidth , int requestedX , boolean onTop ) {
		int arrowPos = requestedX - arrowUp.getMeasuredWidth () / 2;

		switch ( animationStyle ) {
		case ANIM_GROW_FROM_LEFT:
			window.setAnimationStyle ( ( onTop ) ? R.style.Animations_PopUpMenu_Left : R.style.Animations_PopDownMenu_Left );
			break;
					
		case ANIM_GROW_FROM_RIGHT:
			window.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Right : R.style.Animations_PopDownMenu_Right );
			break;
					
		case ANIM_GROW_FROM_CENTER:
			window.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center : R.style.Animations_PopDownMenu_Center );
		break;
			
		case ANIM_REFLECT:
			window.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Reflect : R.style.Animations_PopDownMenu_Reflect );
		break;
		
		case ANIM_AUTO:
			if ( arrowPos <= screenWidth / 4 ) {
				window.setAnimationStyle ( ( onTop ) ? R.style.Animations_PopUpMenu_Left : R.style.Animations_PopDownMenu_Left );
			} else if ( arrowPos > screenWidth / 4 && arrowPos < 3 * ( screenWidth / 4 ) ) {
				window.setAnimationStyle ( ( onTop ) ? R.style.Animations_PopUpMenu_Center : R.style.Animations_PopDownMenu_Center );
			} else {
				window.setAnimationStyle ( ( onTop ) ? R.style.Animations_PopUpMenu_Right : R.style.Animations_PopDownMenu_Right );
			}
					
			break;
		}
	}
	
	/**
	 * Show arrow
	 * 
	 * @param whichArrow arrow type resource id
	 * @param requestedX distance from left screen
	 */
	private void showArrow ( int whichArrow, int requestedX ) {
        final View showArrow = ( whichArrow == R.id.arrow_up ) ? arrowUp : arrowDown;
        final View hideArrow = ( whichArrow == R.id.arrow_up ) ? arrowDown : arrowUp;

        final int arrowWidth = arrowUp.getMeasuredWidth ();

        showArrow.setVisibility ( View.VISIBLE );
        
        ViewGroup.MarginLayoutParams param = ( ViewGroup.MarginLayoutParams ) showArrow.getLayoutParams ();
       
        param.leftMargin = requestedX - arrowWidth / 2;
        
        hideArrow.setVisibility ( View.INVISIBLE );
    }
	
	/**
	 * Set listener for window dismissed. This listener will only be fired if the quicakction dialog is dismissed
	 * by clicking outside the dialog or clicking on sticky item.
	 */
	public void setOnDismissListener ( QuickAction.OnDismissListener listener ) {
		setOnDismissListener ( this );
		
		dismissListener = listener;
	}
	
	@Override
	public void onDismiss () {
		if ( ! didAction && dismissListener != null ) {
			dismissListener.onDismiss ( QuickAction.this , anchor );
		}
	}
	
	/**
	 * Listener for item click
	 *
	 */
	public interface OnActionItemClickListener {
		public abstract void onItemClick ( QuickAction source , View anchor , Object object , int pos , int actionId );
	}
	
	/**
	 * Listener for window dismiss
	 * 
	 */
	public interface OnDismissListener {
		public abstract void onDismiss ( QuickAction source , View anchor );
	}
}