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

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import me.SyncWise.Android.Gson.BaseTimerListActivity;
import me.SyncWise.Android.Modules.MenuList.MenuItem.AssignedDataType;
import me.SyncWise.Android.ActivityTransition;
import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.AppToast;
import me.SyncWise.Android.R;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity implemented to display a Menu (List).<br>
 * 
 * @author Elias
 * @sw.todo	<b>Company Profile Implementation :</b><br>
 * This activity can be reused (via inheritance) in order to display a menu list, in the following manner :<br>
 * <ul>
 * <li>Extend this class.</li>
 * <li>Inside the {@link #onCreate(Bundle)} method, create {@link me.SyncWise.Android.Modules.MenuList.MenuItem MenuList.MenuItem} objects and add them to the Menu using the {@link #addMenuItem(MenuItem)} method.<br>
 * <b>Attention !</b> the {@code MenuItem} objects must be added before making the superclass method call : <i>super.onCreate(savedInstanceState)</i> !</li>
 * <li>Do not forget to add your new class in the {@code AndroidManifest.xml} file.</li>
 * </ul>
 *
 */
public abstract class MenuListActivity extends BaseTimerListActivity implements OnItemLongClickListener {
	
	/**
	 * Integer holding the menu item layout resource id.<br>
	 * If not set, the default menu layout is applied : {@code R.layout.menu_list_activity_item}.
	 * @see #getMenuItemLayout()
	 */
	private Integer menuItemLayout;
	
	/**
	 * Flag used to indicate if an activity has been started or not.
	 */
	private boolean activityStarted;
	
	/**
	 * Flag used to indicate if a background task has been started or not.
	 */
	private boolean taskStarted;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #clearTaskView}.
	 */
	public static final String CLEAR_TASK_VIEW = MenuListActivity.class.getName () + ".CLEAR_TASK_VIEW";
	
	/**
	 * Flag used to indicate whether the task view should be cleared or not.
	 */
	private boolean clearTaskView;
	
	/**
	 * List holding the {@link me.SyncWise.Android.Modules.MenuList.MenuItem MenuItem} objects of the Menu List.
	 */
	private List < MenuItem > menuItems;
	
	/**
	 * Setter - {@link #menuItemLayout}<br>
	 * <b>Attention !</b> the menu item layout must be set before making the superclass method call : <i>super.onCreate(savedInstanceState)</i> !</li>
	 * 
	 * @param menuItemLayout	Integer holding the menu item layout resource id.
	 */
	public void setMenuItemLayout ( final int menuItemLayout ) {
		// Set the menu item layout
		this.menuItemLayout = menuItemLayout;
	}
	
	/**
	 * Getter - {@link #menuItemLayout}<br>
	 * If the menu item layout resource id is invalid, the default menu layout is returned : {@code R.layout.menu_list_activity_item}.
	 * 
	 * @return	Integer holding the menu item layout resource id.
	 */
	public int getMenuItemLayout () {
		// Check if the menu item layout is valid
		if ( menuItemLayout == null )
			// Return the default menu item layout
			return R.layout.menu_list_activity_item;
		// Otherwise the menu item layout is valid
		return menuItemLayout;
	}
	
	/**
	 * Retrieves the total number of {@link me.SyncWise.Android.Modules.MenuList.MenuItem} assigned to the Menu List.
	 * 
	 * @return	Integer representing the total number of menu items.
	 */
	public int getCount () {
		// Check if the list of menu items is valid
		if ( menuItems == null )
			// There are no menu items
			return 0;
		// Return the size of the menu items list
		return menuItems.size ();
	}
	
	/**
	 * Adds a {@link me.SyncWise.Android.Modules.MenuList.MenuItem} object at the end of the list.<br>
	 * <b>Attention !</b> the {@code MenuItem} objects must be added before making the superclass method call : <i>super.onCreate(savedInstanceState)</i> !</li>
	 * 
	 * @param menuItem	A {@link me.SyncWise.Android.Modules.MenuList.MenuItem} object to add to the menu list.
	 * @return	A reference to the provided {@link me.SyncWise.Android.Modules.MenuList.MenuItem} object, used to allow cascaded method calls.
	 */
	public MenuItem addMenuItem ( final MenuItem menuItem ) {
		// Check if the provided menu item is valid
		if ( menuItem != null ) {
			// Check if the list of menu items is valid
			if ( menuItems == null )
				// Initialize the list of menu items
				menuItems = new ArrayList < MenuItem > ();
			// Add the provided menu item to the list
			menuItems.add ( menuItem );
		} // End if
		// Allow for cascaded methods calls on the menu item
		return menuItem;
	}
	
	/**
	 * Gets the {@link me.SyncWise.Android.Modules.MenuList.MenuItem} object at the provided position.
	 * 
	 * @param position	Integer holding the position of the required menu item.
	 * @return	The {@link me.SyncWise.Android.Modules.MenuList.MenuItem} object at the provided position.<br>{@code NULL} is returned if the menu items list and/or the position is invalid.
	 */
	public MenuItem getMenuItem ( final int position ) {
		try {
			// Return the menu item at the provided position
			return menuItems.get ( position );
		} catch ( Exception exception ) {
			// The provided position is invalid /or/ there are no menu items
			return null;
		} // End of try-catch block
	}
	
	/**
	 * Validates the menu items stored in the list :<br>
	 * All invalid menu items are removed.
	 */
	private void validate () {
		// Determine if the list holding the menu items is valid
		if ( menuItems == null )
			// Initialize the list of menu items
			menuItems = new ArrayList < MenuItem > ();
		else {
			// Compute the size of the list of menu items
			int size = menuItems.size ();
			// Iterate over all the list of menu items
			for ( int i = 0 ; i < size ; i ++ )
				// Check if the menu item is valid
				if ( menuItems.get ( i ) == null ) {
					// Remove the invalid menu item
					menuItems.remove ( i );
					i --;
				} // End if
		} // End else
	}
	
	/**
	 * Notifies the attached observers that the underlying data has been changed and any View reflecting the data set should refresh itself.
	 */
	public void notifyDataSetChanged () {
		// Check if the list view has a valid adapter
		if ( getListAdapter () == null )
			// Do nothing
			return;
		// Check if the list view adapter is a BaseAdapter
		if ( ! ( getListAdapter () instanceof BaseAdapter ) )
			// Do nothing
			return;
		// Otherwise, notify Data Set Changed
		( (BaseAdapter) getListAdapter () ).notifyDataSetChanged ();
	}
	
	/*
	 * Called when the activity is starting.
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate ( Bundle savedInstanceState ) {
		// Superclass method call 
		super.onCreate ( savedInstanceState );
		
		// Determine if a saved instance state is provided
		if ( savedInstanceState != null ) {
			// Restore the content provided by the saved instance state 
			clearTaskView = savedInstanceState.getBoolean ( CLEAR_TASK_VIEW , clearTaskView );
		} // End if
		
		// Validate the list menu items
		validate ();
		// Determine if the Menu List has at least one menu item
		if ( getCount () == 0 )
			// Exit method
			return;
		// Compute the list adapter using the getMenuListAdapter method (uses sub classes implementations)
		ListAdapter listAdapter = getMenuListAdapter ( getMenuItemLayout () , menuItems );
		// Check if the list adapter is valid
		if ( listAdapter == null )
			// Initialize the list adapter using the MenuListAdapter
			listAdapter = new MenuListAdapter ( this , getMenuItemLayout () , menuItems );
		// Set the new menu list adapter to the list
		setListAdapter ( listAdapter );
		// Assign an item long click listener to provide help for every main menu item
		getListView ().setOnItemLongClickListener ( this );
	}
	
	/**
	 * Gets a new menu list adapter.<br>
	 * The default implementation returns null.<br>
	 * Subclasses can override this method to apply a custom list adapter.
	 * 
	 * @param layout	XML Layout (layout) resource id.
	 * @param menuItems	List of {@link me.SyncWise.Android.Modules.MenuList.MenuItem MenuItem} objects.
	 * @return	A {@link android.widget.ListAdapter ListAdapter} used to populate the menu items list view.
	 */
	protected ListAdapter getMenuListAdapter ( int layout , List < MenuItem > menuItems ) {
		// Default implementation
		return null;
	}
	
	/*
	 * Called after onRestoreInstanceState(Bundle), onRestart(), or onPause(), for your activity to start interacting with the user.
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	public void onResume () {
		// Superclass method call
		super.onResume ();
		// Clear the task view (if needed)
		clearTaskView ();
	}
	
	/**
	 * Determines if the item clicks are enabled.
	 * 
	 * @return	Boolean indicating if the item clicks are enabled.
	 */
	public boolean isItemClickEnabled () {
		return true;
	}
	
    /*
     * This method will be called when an item in the list is selected.
     *
     * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
     */
	@Override
    protected void onListItemClick ( ListView listView , View view , final int position , long id ) {
		// Determine if an activity has already been started
		if ( activityStarted || taskStarted )
			// Do nothing
			return;
		// Check if the item clicks are disabled
		if ( ! isItemClickEnabled () )
			// Do nothing
			return;
		// Determine if the menu item has a toast to display
		if ( menuItems.get ( position ).getToastId () != null )
			// Display toast
			Toast.makeText ( MenuListActivity.this , AppResources.getInstance ( MenuListActivity.this ).getString ( MenuListActivity.this , menuItems.get ( position ).getToastId () ) , Toast.LENGTH_LONG ).show ();
		// Determine if the menu item has an action
		else if ( menuItems.get ( position ).getAction () != null ) {
			// Execute action
			menuItems.get ( position ).getAction ().onClick ( this );
		} // End else if
		// Determine if the menu item has a task to start
		else if ( menuItems.get ( position ).getTask () != null ) {
			// Retrieve a reference to the activity's content layout 
			FrameLayout content = (FrameLayout) findViewById ( android.R.id.content );
			// Inflate a task view
			View taskView = getTaskView ();
			// Build an animation for the task view
			Animation taskAnimation = getTaskAnimationIn ();
			// Set an animation listener mainly used to start the task at the end of the animation
			taskAnimation.setAnimationListener ( getTaskAnimationIn_Listener ( menuItems.get ( position ) ) );
			// Add the task view to the content layout
			content.addView ( taskView );
			// Set flag
			taskStarted = true;
			// Start the task view animation
			taskView.startAnimation ( taskAnimation );
		} // End else if
		// Determine if the menu item has an activity to start
		else if ( menuItems.get ( position ).getActivity () != null ) {
			try {
				// Create a new intent to start a new activity
				Intent intent = new Intent ( MenuListActivity.this , menuItems.get ( position ).getActivity () );
				// Determine if there is data to put in the intent
				if ( menuItems.get ( position ).getCount () != 0 ) {
					// Iterate over all data of type String
					for ( int i = 0 ; i < menuItems.get ( position ).getCount ( AssignedDataType.STRING ) ; i ++ )
						// Add the data of type string to the intent
						intent.putExtra ( menuItems.get ( position ).getKey ( AssignedDataType.STRING , i ) , (String) menuItems.get ( position ).getData ( AssignedDataType.STRING , i ) );
					// Iterate over all data of type Serializable
					for ( int i = 0 ; i < menuItems.get ( position ).getCount ( AssignedDataType.SERIALIZABLE ) ; i ++ )
						// Add the data of type Serializable to the intent
						intent.putExtra ( menuItems.get ( position ).getKey ( AssignedDataType.SERIALIZABLE , i ) , (Serializable) menuItems.get ( position ).getData ( AssignedDataType.SERIALIZABLE , i ) );
					// TODO : Iterate ( and add to the intent ) over all data of newly added types (FUTURE WORK) here
				} // End if
				// Set flag
				activityStarted = true;
				// Determine if the activity should be started for a result
				if ( menuItems.get ( position ).getActivityForResult () )
					// Start the new activity
					startActivityForResult ( intent , menuItems.get ( position ).getRequestCode () );
				else
					// Start the new activity
					startActivity ( intent );
				// Specify an explicit transition animation to perform next
				ActivityTransition.SlideOutLeft ( this );
			} catch ( Exception exception ) {
				// Clear flag
				activityStarted = false;
				// Indicate that the activity cannot be displayed
				new AppToast ( MenuListActivity.this ).setIcon ( R.drawable.warning ).setText ( AppResources.getInstance ( MenuListActivity.this ).getString ( MenuListActivity.this , R.string.invalid_selection_label ) ).show ();
			} // End of try-catch block
		} // End else if
    }
	
    /*
     * Callback method to be invoked when an item in this view has been clicked and held.
     *
     * @see android.widget.AdapterView.OnItemLongClickListener#onItemLongClick(android.widget.AdapterView, android.view.View, int, long)
     */
	@Override
	public boolean onItemLongClick ( AdapterView < ? > parent , View view , int position , long id ) {
		// Determine if an activity has already been started
		if ( activityStarted || taskStarted )
			// Do nothing
			// Indicate that callback consumed the long click event
			return true;
		// Check if the menu item has a valid help resource id
		if ( menuItems.get ( position ).getHelpId () != null )
			// Display a help dialog
			AppDialog.getInstance ().displayAlert ( MenuListActivity.this ,
					AppResources.getInstance ( MenuListActivity.this ).getString ( MenuListActivity.this , menuItems.get ( position ).getTitleId () ) ,
					AppResources.getInstance ( MenuListActivity.this ).getString ( MenuListActivity.this , menuItems.get ( position ).getHelpId () ) ,
					AppDialog.ButtonsType.NONE , null );
		// Indicate that callback consumed the long click event
		return true;
	}
    
	/*
	 * Called as part of the activity lifecycle when an activity is going into the background, but has not (yet) been killed.
	 *
	 * @see android.app.Activity#onPause()
	 */
	@Override
    protected void onPause () {
		// Clear flags
		activityStarted = false;
		taskStarted = false;
		// Superclass method call
		super.onPause ();
		// Dismiss any displayed dialogs (to avoid activity context leak)
		AppDialog.getInstance ().dismiss ();
	}
	
	/*
	 * Called to retrieve per-instance state from an activity before being killed so that the state can be restored in onCreate(Bundle) or onRestoreInstanceState(Bundle).
	 *
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	protected void onSaveInstanceState ( Bundle outState ) {
		// Superclass method call
		super.onSaveInstanceState ( outState );
    	// Save the content of clearTaskView in the outState bundle
    	outState.putBoolean ( CLEAR_TASK_VIEW , clearTaskView );
	}
	
	/**
	 * Clears the task view from the main content layout if specified by the flag {@link #clearTaskView}.
	 */
	private void clearTaskView () {
		// Retrieve a reference to the task view
		View taskView = findViewById ( R.id.task_view );
		// Check if the task view should be cleared
		if ( clearTaskView && taskView != null && taskView.getParent () != null )
			// Remove the task view from the its parent
			( (ViewGroup) taskView.getParent () ).removeView ( taskView );
	}
	
	/**
	 * Slides out the task view to the top of the screen in an animated manner.
	 */
	public void slideOutTaskView () {
		// Clear flag
		taskStarted = false;
		// Retrieve a reference to the task view
		View taskView = findViewById ( R.id.task_view );
		// Check if the task view is valid
		if ( taskView != null )
			// Start the task view animation
			taskView.startAnimation ( getTaskAnimationOut () );
	}
	
	/*
	 * Called when you are no longer visible to the user.
	 *
	 * @see android.app.Activity#onStop()
	 */
	@Override
    public void onStop () {
    	// Superclass method call
		super.onStop ();
		// Determine if the activity is finishing
		if ( isFinishing () ) {
			if ( menuItems != null ) {
				menuItems.clear ();
				menuItems = null;
			} // End if
			getListView ().setOnItemLongClickListener ( null );
		} // End if
    }
	
	/**
	 * Inflates and returns a new task view.
	 * 
	 * @return	A {@link android.view.View View} used to inform that a background task is being performed asynchronously in the background.
	 */
	private View getTaskView () {
		// Inflate a view used to display the loading task
		View taskView = LayoutInflater.from ( this ).inflate ( R.layout.task_view , null );
		// Display the task label
		( (TextView) taskView.findViewById ( R.id.label_task ) ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.loading_label ) );
		// Return the task view
		return taskView;
	}
	
	/**
	 * Builds and returns a slide in animation.<br>
	 * The slide animation duration is set by {@code R.integer.default_activity_transition_duration}.
	 * 
	 * @return	An {@link android.view.animation.Animation Animation} used to perform a slide in from top animation.
	 */
	private Animation getTaskAnimationIn () {
		// Declare and initialize the slide in animation
		Animation in = new TranslateAnimation ( Animation.ABSOLUTE , 0 , Animation.ABSOLUTE , 0 , Animation.RELATIVE_TO_SELF , -1 , Animation.RELATIVE_TO_SELF , 0 );
		// Set the animation duration
		in.setDuration ( getResources ().getInteger ( R.integer.default_activity_transition_duration ) );
		// Return the animation
		return in;
	}

	/**
	 * Builds and returns a slide out animation.<br>
	 * The slide animation duration is set by {@code R.integer.default_activity_transition_duration}.
	 * 
	 * @return	An {@link android.view.animation.Animation Animation} used to perform a slide out to top animation.
	 */
	private Animation getTaskAnimationOut () {
		// Load the slide slide out animation
		Animation out = new TranslateAnimation ( Animation.ABSOLUTE , 0 , Animation.ABSOLUTE , 0 , Animation.RELATIVE_TO_SELF , 0 , Animation.RELATIVE_TO_SELF , -1 );
		// Set the animation duration
		out.setDuration ( getResources ().getInteger ( R.integer.default_activity_transition_duration ) );
		// Set an animation listener mainly used to remove the task view
		out.setAnimationListener ( new AnimationListener () {
			/*
			 * Notifies the start of the animation.
			 *
			 * @see android.view.animation.Animation.AnimationListener#onAnimationStart(android.view.animation.Animation)
			 */
			@Override
			public void onAnimationStart ( Animation animation ) {
				// Do nothing
			}
			
			/*
			 * Notifies the repetition of the animation.
			 *
			 * @see android.view.animation.Animation.AnimationListener#onAnimationRepeat(android.view.animation.Animation)
			 */
			@Override
			public void onAnimationRepeat ( Animation animation ) {
				// Do nothing
			}
			
			/*
			 * Notifies the end of the animation.
			 *
			 * @see android.view.animation.Animation.AnimationListener#onAnimationEnd(android.view.animation.Animation)
			 */
			@Override
			public void onAnimationEnd ( Animation animation ) {
				// Retrieve a reference to the task view
				final View taskView = findViewById ( R.id.task_view );
				// Check if the task view is valid
				if ( taskView == null )
					// Invalid task view
					return;
				// Hide the task view
				taskView.setVisibility ( View.GONE );
				// Declare and initialize a handler used to remove the task view
				Handler handler = new Handler ();
				// Execute a runnable in 200 milliseconds to make sure the animation ended
				handler.postDelayed ( new Runnable() {
					@Override
					public void run () {
						try {
							// Retrieve a reference to the task view
							final View taskView = findViewById ( R.id.task_view );
							// Check if the task view is valid
							if ( taskView != null && taskView.getParent () != null )
								// Remove the task view from the its parent
								( (ViewGroup) taskView.getParent () ).removeView ( taskView );
						} catch ( Exception exception ) {
							// Do nothing
						} // End of try-catch block
					}
				} , 200 );
			}
		} );
		// Return the animation
		return out;
	}
	
	/**
	 * Builds and returns an animation listener made for a slide in animation for task view.
	 * 
	 * @param menuItem	A {@link me.SyncWise.Android.Modules.MenuList.MenuItem MenuItem} object from the Menu List.
	 * @return	A {@link android.view.animation.Animation.AnimationListener AnimationListener}
	 */
	private AnimationListener getTaskAnimationIn_Listener ( final MenuItem menuItem ) {
		// Create and return an animation listener for a task animation in using 
		return new AnimationListener() {
			/*
			 * Notifies the start of the animation.
			 *
			 * @see android.view.animation.Animation.AnimationListener#onAnimationStart(android.view.animation.Animation)
			 */
			@Override
			public void onAnimationStart ( Animation animation ) {
				// Do nothing
			}
			
			/*
			 * Notifies the repetition of the animation.
			 *
			 * @see android.view.animation.Animation.AnimationListener#onAnimationRepeat(android.view.animation.Animation)
			 */
			@Override
			public void onAnimationRepeat ( Animation animation ) {
				// Do nothing
			}
			
			/*
			 * Notifies the end of the animation.
			 *
			 * @see android.view.animation.Animation.AnimationListener#onAnimationEnd(android.view.animation.Animation)
			 */
			@Override
			public void onAnimationEnd ( Animation animation ) {
				try {
			    	// Retrieve all the methods of the task
			    	Method methods [] = menuItem.getTaskClass ().getMethods ();
			    	// Declare a flag used to indicate if the task has been properly executed
			    	boolean executed = false;
			    	// Iterate over all methods
			    	for ( Method method : methods )
			    		// Check if this is the 'execute' method
			    		if ( method.getName ().equals ( "execute" ) && method.isVarArgs () ) {
			    			// Invoke the execute method in order to execute the task
			    			method.invoke ( menuItem.getTask () , new Object [] { null } );
			    			// Generate a new task
			    			menuItem.setTask ();
			    			// Set the appropriate flags
			    			clearTaskView = true;
			    			executed = true;
			    			break;
			    		} // End if
			    	// Check if the task has been successfully executed
			    	if ( ! executed )
			    		// An error occurred, throw an exception
			    		throw new Exception ();
				} catch ( Exception exception ) {
					// Clear flag
					taskStarted = false;
					// Start the task view animation
					findViewById ( R.id.task_view ).startAnimation ( getTaskAnimationOut () );
					// Indicate that the task cannot be started
					new AppToast ( MenuListActivity.this ).setIcon ( R.drawable.warning ).setText ( AppResources.getInstance ( MenuListActivity.this ).getString ( MenuListActivity.this , R.string.invalid_selection_label ) ).show ();
				} // End of try-catch block
			}
		};
	}
	 

	    
	  
}