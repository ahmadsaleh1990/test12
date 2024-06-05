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
import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.text.TextUtils;

/**
 * Class used to represent a menu item in {@link me.SyncWise.Android.Modules.MenuList.MenuListActivity MenuListActivity}.<br>
 * A Menu Item has a :
 * <ul>
 * <li>An ID ({@link java.lang.Integer Integer}).</li>
 * <li>A title ({@link java.lang.String String}).</li>
 * <li>A description ({@link java.lang.String String}).</li>
 * <li>An icon ({@link android.graphics.drawable.Drawable Drawable}).</li>
 * <li>A help ({@link java.lang.String String}), accessible by long pressing a menu item.</li>
 * <li>A toast ({@link java.lang.String String}), displayed by clicking on the menu item (if a toast is set).</li>
 * <li>An activity (An object of {@link java.lang.Class Class} that references {@link android.app.Activity Activity}) to go to if the menu item is clicked (if an activity is assigned to the menu item).<br>
 * Once can specify how to launch that activity : start an activity normally or for a result.</li>
 * <li>A list of additional data (a map of keys and values) that will be attached to the intent before starting a new activity (if an activity is assigned to the menu item).<br>
 * Currently, <b>Strings</b> are supported.<br>
 * <b>Note :</b> In order to implement an additional data type, refer to the <a href="#future_work"><b>Future Work</b></a> section.</li>
 * <li>A task (An object of {@link java.lang.Class Class} that references {@link android.os.AsyncTask AsyncTask}) to perform a background task if the menu item is clicked (if a task is assigned to the menu item).<br>
 * It is the developer responsibility to determine what to do when the task performs successfully (or not).</li>
 * <li>A notification number, that can displayed in the upper right corner of the menu item.<br>
 * Currently, the notification number is only displayed if it different then <b>ZERO</b> (implementation in the adapter of the {@code MenuListActivity} activity.</li>
 * </ul>
 * <br><b>Note :</b><br>
 * The menu item actions priorities should be :
 * <ol>
 * <li>A toast (if valid).</li>
 * <li>A task (if valid).</li>
 * <li>An activity (if valid).</li>
 * </ol>
 * The actions listed above should be independent, meaning that if an error occurs during one of the actions, it should not be performed, i.e. no other actions are performed instead.
 *
 * @author Elias
 * @sw.futureWork	<b>Additional data types :</b><br>
 * In order to add / implement an additional data type that can be assigned to the intent before starting a new activity (if an activity is assigned to the menu item), you should :
 * <ol>
 * <li>Add the additional data type to the enumeration {@link me.SyncWise.Android.Modules.MenuList.MenuItem.AssignedDataType MenuItem.AssignedDataType}.</li>
 * <li>Add two attributes :<br>Two lists, one list of String used to hold the key, and the other list of the appropriate data type in question to hold the data.</li>
 * <li>Modify the {@link #putData(String, Object)} method :<br>Add an {@code if} statement to check if the provided data object is an instance of the data type in question.
 * <br>Make sure to check if the appropriate lists must be initialized prior to adding the key/data pair</li>
 * <li>Modify the {@link #getCount(AssignedDataType)} method :<br>Add an additional {@code case} to the {@code switch} statement, returning the size of the list holding the keys of the data type in question.</li>
 * <li>Modify the {@link #getKey(AssignedDataType, int)} method :<br>Add an additional {@code case} to the {@code switch} statement, returning the key element in list holding the keys of the data type in question at the provided position.</li>
 * <li>Modify the {@link #getData(AssignedDataType, int)} method :<br>Add an additional {@code case} to the {@code switch} statement, returning the data element in list holding the data of the data type in question at the provided position.</li>
 * </ol>
 */
public class MenuItem {
	
	/**
	 * ID.
	 */
	private Integer ID;
	
	/**
	 * title (string) resource id.
	 */
	private Integer titleId;
	
	/**
	 * icon (drawable) resource id.
	 */
	private Integer iconId;
	
	/**
	 * description (string) resource id.
	 */
	private Integer descriptionId;
	
	/**
	 * help (string) resource id.
	 */
	private Integer helpId;
	
	/**
	 * toast (string) resource id.
	 */
	private Integer toastId;
	
	/**
	 * integer used as notification for the menu item.
	 */
	private int notificationNumber;
	
	/**
	 * Flag used to determine if the icon is dimmed or not.<br>
	 * The icon is either displayed fully opaque or semi-transparent (75%).
	 */
	private boolean dimIcon;
	
	/**
	 * {@link java.lang.Class Class} activity reference.
	 */
	private Class <?> activity;
	
	/**
	 * Flag used to indicate if the activity (if any) is to be started for a result.
	 * @see #requestCode
	 * @see android.app.Activity#startActivityForResult(android.content.Intent, int)
	 */
	private boolean activityForResult;
	
	/**
	 * Integer holding the request code of the new activity to start.<br>
	 * The request code is used if and only if the activity should be started for a result.
	 * @see #activityForResult
	 * @see android.app.Activity#startActivityForResult(android.content.Intent, int)
	 */
	private Integer requestCode;
	
	/**
	 * {@link java.util.List List} holding the key value of an assigned string.
	 */
	private List < String > stringKey;
	
	/**
	 * {@link java.util.List List} holding the data value of an assigned string.
	 */
	private List < String > stringData;
	
	/**
	 * {@link java.util.List List} holding the key value of an assigned serializable.
	 */
	private List < String > serializableKey;
	
	/**
	 * {@link java.util.List List} holding the data value of an assigned serializable.
	 */
	private List < Serializable > serializableData;
	
	/**
	 * {@link me.SyncWise.Android.Modules.MenuList.Action Action} holding any customized action to execute upon clicking the menu item.
	 */
	private Action action;
	
	/**
	 * {@link java.lang.Class Class} task reference.
	 */
	private Class <?> taskClass;
	
	/**
	 * {@link android.os.AsyncTask AsyncTask} task reference.
	 */
	@SuppressWarnings("rawtypes")
	private AsyncTask task;
	
	/**
	 * Reference to a task generator, used to re-initialize the background task, mainly once it is executed.<br>
	 * If no generator is set and a task is set and executed, it will not execute anymore (an {@link android.os.AsyncTask AsyncTask} can only execute once).
	 */
	private Task taskGenerator;
	
	/**
	 * Enumeration used to define the various data types that can be assigned to a menu item (in order to attach them to an intent before starting a new activity (if an activity is assigned to the menu item).<br>
	 * In order to implement an additional data type, refer to the <b>Future Work</b> section in {@link me.SyncWise.Android.Modules.MenuList.MenuItem MenuItem}.
	 * @author Elias
	 *
	 */
	static enum AssignedDataType {
		STRING ,
		SERIALIZABLE
	}
	
	/**
	 * Interface used to implement a helper method in order to generate / regenerate {@link android.os.AsyncTask AsyncTask} objects once they are executed, since they can only be executed once.
	 * 
	 * @author Elias
	 *
	 */
	public static interface Task {
		
		/**
		 * Generates an {@link android.os.AsyncTask AsyncTask} object.
		 * 
		 * @return {@link android.os.AsyncTask AsyncTask} task reference.
		 */
		@SuppressWarnings("rawtypes")
		public AsyncTask getTask ();
		
		/**
		 * Gets the task class.
		 * 
		 * @return {@link java.lang.Class Class} task reference.
		 */
		public Class < ? > getTaskClass ();
		
	}
	
	/**
	 * Setter - {@link #ID}
	 * 
	 * @param ID	Integer holding the menu item ID.
	 * @return	Generic THIS (self) - Reference to the current {@link me.SyncWise.Android.Modules.MenuList.MenuItem MenuItem} object.
	 */
	public MenuItem setID ( final Integer ID ) {
		// Set the ID
		this.ID = ID;
		// Allow for cascaded methods calls
		return this;
	}
	
	/**
	 * Getter - {@link #ID}
	 * 
	 * @return	Integer holding the menu item ID.
	 */
	public Integer getID () {
		// Return the menu item ID
		return ID;
	}
	
	/**
	 * Determines if the current menu item has the specified ID.
	 * 
	 * @param ID	Integer holding the ID to check.
	 * @return	Boolean indicating if the current menu item owns the specified ID.
	 */
	public boolean isID ( final int ID ) {
		// Check if the menu item ID is valid
		if ( this.ID == null )
			// Invalid ID
			return false;
		// Otherwise the menu item ID is valid
		// Compare the IDs
		return this.ID == ID;
	}
	
	/**
	 * Setter - {@link #titleId}
	 *
	 * @param titleId	title (string) resource id.
	 * @return	Generic THIS (self) - Reference to the current {@link me.SyncWise.Android.Modules.MenuList.MenuItem MenuItem} object.
	 */
	public MenuItem setTitleId ( final Integer titleId ) {
		// Set the title resource id
		this.titleId = titleId;
		// Allow for cascaded methods calls
		return this;
	}
	
	/**
	 * Getter - {@link #titleId}
	 * 
	 * @return	title (string) resource id.
	 */
	public Integer getTitleId () {
		// Return the title resource id
		return titleId;
	}
	
	/**
	 * Setter - {@link #iconId}
	 * 
	 * @param iconId	icon (drawable) resource id.
	 * @return	Generic THIS (self) - Reference to the current {@link me.SyncWise.Android.Modules.MenuList.MenuItem MenuItem} object.
	 */
	public MenuItem setIconId ( final Integer iconId ) {
		// Set the icon resource id
		this.iconId = iconId;
		// Allow for cascaded methods calls
		return this;
	}
	
	/**
	 * Getter - {@link #iconId}
	 * 
	 * @return	icon (drawable) resource id.
	 */
	public Integer getIconId () {
		// Return the icon resource id
		return iconId;
	}
	
	/**
	 * Setter - {@link #descriptionId}
	 * 
	 * @param descriptionId	description (string) resource id.
	 * @return	Generic THIS (self) - Reference to the current {@link me.SyncWise.Android.Modules.MenuList.MenuItem MenuItem} object.
	 */
	public MenuItem setDescriptionId ( final Integer descriptionId ) {
		// Set the description resource id
		this.descriptionId = descriptionId;
		// Allow for cascaded methods calls
		return this;
	}
	
	/**
	 * Getter - {@link #descriptionId}
	 * 
	 * @return	description (string) resource id.
	 */
	public Integer getDescriptionId () {
		// Return the description resource id
		return descriptionId;
	}
	
	/**
	 * Setter - {@link #helpId}
	 * 
	 * @param helpId	help (string) resource id.
	 * @return	Generic THIS (self) - Reference to the current {@link me.SyncWise.Android.Modules.MenuList.MenuItem MenuItem} object.
	 */
	public MenuItem setHelpId ( final Integer helpId ) {
		// Set the help resource id
		this.helpId = helpId;
		// Allow for cascaded methods calls
		return this;
	}
	
	/**
	 * Getter - {@link #helpId}
	 * 
	 * @return	help (string) resource id.
	 */
	public Integer getHelpId () {
		// Return the help resource id
		return helpId;
	}
	
	/**
	 * Setter - {@link #toastId}
	 * 
	 * @param toastId	toast (string) resource id.
	 * @return	Generic THIS (self) - Reference to the current {@link me.SyncWise.Android.Modules.MenuList.MenuItem MenuItem} object.
	 */
	public MenuItem setToastId ( final Integer toastId ) {
		// Set the toast resource id
		this.toastId = toastId;
		// Allow for cascaded methods calls
		return this;
	}
	
	/**
	 * Getter - {@link #toastId}
	 * 
	 * @return	toast (string) resource id.
	 */
	public Integer getToastId () {
		// Return the toast resource id
		return toastId;
	}
	
	/**
	 * Setter - {@link #notificationNumber}
	 * 
	 * @param notificationNumber	integer used as notification for the menu item.
	 * @return	Generic THIS (self) - Reference to the current {@link me.SyncWise.Android.Modules.MenuList.MenuItem MenuItem} object.
	 */
	public MenuItem setNotificationNumber ( final int notificationNumber ) {
		// Check if the notification number is non negative
		if ( notificationNumber >= 0 )
			// Set the notification number
			this.notificationNumber = notificationNumber;
		// Allow for cascaded methods calls
		return this;
	}
	
	/**
	 * Getter - {@link #notificationNumber}
	 * 
	 * @return	integer used as notification for the menu item.
	 */
	public int getNotificationNumber () {
		// Return the notification number
		return notificationNumber;
	}
	
	/**
	 * Setter - {@link #dimIcon}
	 * 
	 * @param dimIcon	Boolean used to determine if the icon is dimmed or not.
	 * @return	Generic THIS (self) - Reference to the current {@link me.SyncWise.Android.Modules.MenuList.MenuItem MenuItem} object.
	 */
	public MenuItem setDimIcon ( final boolean dimIcon ) {
		// Set the dim icon flag
		this.dimIcon = dimIcon;
		// Allow for cascaded methods calls
		return this;
	}
	
	/**
	 * Getter - {@link #dimIcon}
	 * 
	 * @return	Boolean used to determine if the icon is dimmed or not.
	 */
	public boolean getDimIcon () {
		// Return the dim icon flag
		return dimIcon;
	}
	
	/**
	 * Setter - {@link #activity}
	 * 
	 * @param activity	{@link java.lang.Class Class} activity reference.
	 * @return	Generic THIS (self) - Reference to the current {@link me.SyncWise.Android.Modules.MenuList.MenuItem MenuItem} object.
	 */
	public MenuItem setActivity ( final Class <?> activity ) {
		// Check if the class object is valid
		if ( activity != null )
			// Set the activity
			this.activity = activity;
		// Allow for cascaded methods calls
		return this;
	}
	
	/**
	 * Getter - {@link #activity}
	 * 
	 * @return	{@link java.lang.Class Class} activity reference.
	 */
	public Class <?> getActivity () {
		// Return the activity class
		return activity;
	}
	
	/**
	 * Setter - {@link #activityForResult}<br>
	 * Setter - {@link #requestCode}
	 * 
	 * @param activityForResult	Flag used to indicate if the activity (if any) is to be started for a result.
	 * @param requestCode	Integer holding the request code of the new activity to start.
	 * @return	Generic THIS (self) - Reference to the current {@link me.SyncWise.Android.Modules.MenuList.MenuItem MenuItem} object.
	 */
	public MenuItem setActivityForResult ( final boolean activityForResult , final Integer requestCode ) {
		// Set the flag state
		this.activityForResult = activityForResult;
		// Save the request code
		this.requestCode = requestCode;
		// Allow for cascaded methods calls
		return this;
	}
	
	/**
	 * Getter - {@link #activityForResult}
	 * 
	 * @return	Flag used to indicate if the activity (if any) is to be started for a result.
	 */
	public boolean getActivityForResult () {
		// Return the flag state
		return activityForResult;
	}
	
	/**
	 * Getter - {@link #requestCode}
	 * 
	 * @return	Integer holding the request code of the new activity to start.
	 */
	public Integer getRequestCode () {
		// Return the request code or 0 if none is defined
		return ( requestCode == null ? 0 : requestCode );
	}
	
	/**
	 * Setter - {@link #taskGenerator}
	 * 
	 * @param taskGenerator	Reference to a task generator, used to re-initialize the background task, mainly once it is executed.
	 * @return	Generic THIS (self) - Reference to the current {@link me.SyncWise.Android.Modules.MenuList.MenuItem MenuItem} object.
	 */
	public MenuItem setTaskGenerator ( final Task taskGenerator ) {
		// Determine if the task generator is valid
		if ( taskGenerator != null )
			// Set the generator
			this.taskGenerator = taskGenerator;
		// Allow for cascaded methods calls
		return this;
	}
	
	/**
	 * Setter - {@link #task}
	 * Setter - {@link #taskClass}
	 * 
	 * <br><b>Note :</b><br>This method is used to set a task once. A task cannot be executed more than once.<br>
	 * In order to keep using the same task, assign a task generator via the {@link #setTaskGenerator(Task)} and use {@link #setTask()} instead.
	 * 
	 * @param task	{@link android.os.AsyncTask AsyncTask} task reference.
	 * @param taskClass	{@link java.lang.Class Class} task reference.
	 * @return	Generic THIS (self) - Reference to the current {@link me.SyncWise.Android.Modules.MenuList.MenuItem MenuItem} object.
	 */
	@SuppressWarnings("rawtypes")
	public MenuItem setTask ( final AsyncTask task , final Class <?> taskClass ) {
		// Check if the task object is valid
		if ( task == null || taskClass == null )
			// Invalid task. Allow for cascaded methods calls
			return this;
		// Otherwise the task is valid
		// Determine if the provided class is an AsyncTask
		// Flag used to indicate if the fragment is an indirect child of Fragment
		boolean isAsyncTask = false;
		// Get the class's parent
		Class < ? > parent = taskClass.getSuperclass ();
		// Iterate over all the class's parents
		while ( parent != null ) {
			// Check if the parent is AsyncTask
			if ( parent.equals ( AsyncTask.class ) ) {
				// Set the flag
				isAsyncTask = true;
				// Exit loop
				break;
			} // End if
			// Otherwise the parent is not AsyncTask, keep looping
			parent = parent.getSuperclass ();
		} // End for loop
		// Check if the provided class is an AsyncTask
		if ( ! isAsyncTask )
			// Invalid task. Allow for cascaded methods calls
			return this;
		// Check that task is an instance of the provided task class
		if ( ! taskClass.isInstance ( task ) )
			// Invalid task. Allow for cascaded methods calls
			return this;
		// Set the task
		this.task = task;
		this.taskClass = taskClass;
		// Allow for cascaded methods calls
		return this;
	}
	
	/**
	 * Generates a new task using the assigned task generator.
	 * 
	 * @return	Generic THIS (self) - Reference to the current {@link me.SyncWise.Android.Modules.MenuList.MenuItem MenuItem} object.
	 * @see #taskGenerator
	 * @see #setTaskGenerator(Task)
	 */
	public MenuItem setTask () {
		// Check if the generator is valid
		if ( taskGenerator != null )
			// Generate a new task
			setTask ( taskGenerator.getTask () , taskGenerator.getTaskClass () );
		// Allow for cascaded methods calls
		return this;
	}
	
	/**
	 * Clears the task object and the task class.
	 */
	public void clearTask () {
		// Clear the task and its class
		task = null;
		taskClass = null;
	}
	
	/**
	 * Getter - {@link #task}
	 * 
	 * @return	{@link android.os.AsyncTask AsyncTask} task reference.
	 */
	@SuppressWarnings("rawtypes")
	public AsyncTask getTask () {
		// Return the task
		return task;
	}
	
	/**
	 * Getter - {@link #taskClass}
	 * 
	 * @return	{@link java.lang.Class Class} task reference.
	 */
	public Class <?> getTaskClass () {
		// Return the task class
		return taskClass;
	}
	
	/**
	 * Setter - {@link #action}
	 * 
	 * @param action	{@link me.SyncWise.Android.Modules.MenuList.Action Action} holding any customized action to execute upon clicking the menu item.
	 * @return	Generic THIS (self) - Reference to the current {@link me.SyncWise.Android.Modules.MenuList.MenuItem MenuItem} object.
	 */
	public MenuItem setAction ( final Action action ) {
		// Set the provided action
		this.action = action;
		// Allow for cascaded methods calls
		return this;
	}
	
	/**
	 * Getter - {@link #action}
	 * 
	 * @return	{@link me.SyncWise.Android.Modules.MenuList.Action Action} holding any customized action to execute upon clicking the menu item.
	 */
	public Action getAction () {
		// Return the action
		return action;
	}
	
	/**
	 * Inserts an additional data (a pair of key and value) to be attached to the intent before starting an activity (if one is assigned).
	 * 
	 * @param key	Key ({@link java.lang.String String}) used as identifier for the added string.
	 * @param data	The data object to add. 
	 * @return	Generic THIS (self) - Reference to the current {@link me.SyncWise.Android.Modules.MenuList.MenuItem MenuItem} object.
	 */
	public MenuItem putData ( final String key , final Object data ) {
		// Check if the arguments are valid
		if ( TextUtils.isEmpty ( key ) || data == null )
			// One of or all the arguments are invalid, exit method
			// Allow for cascaded methods calls
			return this;
			
		// Determine the object type
		
		// Check if the data is a string
		if ( data instanceof String ) {
			// Determine if the list holding the string keys is valid
			if ( stringKey == null ) {
				// There are no additional strings assigned, initialize the list of keys and data strings
				stringKey = new ArrayList < String > ();
				stringData = new ArrayList < String > ();
			} // End if
			// Assign the provided key
			stringKey.add ( key );
			// Assign the provided data
			stringData.add ( (String) data );
		} // End if
		// Check if the data is a serializable
		else if ( data instanceof Serializable ) {
			// Determine if the list holding the serializable keys is valid
			if ( serializableKey == null ) {
				// There are no additional serializables assigned, initialize the list of keys and serializables
				serializableKey = new ArrayList < String > ();
				serializableData = new ArrayList < Serializable > ();
			} // End if
			// Assign the provided key
			serializableKey.add ( key );
			// Assign the provided data
			serializableData.add ( (Serializable) data );
		} // End else if
		
		
		// Allow for cascaded methods calls
		return this;
	}
	
	/**
	 * Retrieves the number of assigned data of the provided type.
	 * 
	 * @param	dataType	The {@link me.SyncWise.Android.Modules.MenuList.MenuItem.AssignedDataType MenuItem.AssignedDataType} of the data in question.
	 * @return	Integer representing the number of assigned data of the provided type.
	 */
	public int getCount ( final AssignedDataType dataType ) {
		try {
			// Determine the assigned data type
			switch ( dataType ) {
			case STRING:
				// Return the size of the list holding the keys
				return stringKey.size ();
			case SERIALIZABLE:
				// Return the size of the list holding the keys
				return serializableKey.size ();
			default:
				// Invalid assigned data type
				return 0;
			} // End switch
		} catch ( Exception exception ) {
			// The list holding the keys is not initialized (NULL)
			return 0;
		} // End of try-catch block
	} // End of getDataCount
	
	/**
	 * Retrieves the <b>TOTAL</b> number of assigned data, regardless of their types.
	 * 
	 * @return	Integer representing the <b>TOTAL</b> number of assigned data, regardless of their types.
	 */
	public int getCount () {
		// Declare and initialize an integer used to hold the total number of assigned data
		int total = 0;
		// Retrieve an array holding all the constants of the data type enumeration
		AssignedDataType dataTypes [] = AssignedDataType.values ();
		// Iterate over all the constants of the data types
		for ( AssignedDataType dataType : dataTypes )
			// Compute the number of assigned data for the current type
			total += getCount ( dataType );
		// Return the total number of assigned data
		return total;
	}
	
	/**
	 * Retrieves the key of the provided data type at the provided position.
	 * 
	 * @param dataType	An enumeration constant of {@link me.SyncWise.Android.Modules.MenuList.MenuItem.AssignedDataType MenuItem.AssignedDataType} indicating the required data type.
	 * @param position	Integer holding the position of the required key.
	 * @return	String object representing the key of the required data type and the provided position.<br>{@code NULL} is returned if the data type and/or the position is invalid.
	 */
	public String getKey ( final AssignedDataType dataType , final int position ) {
		try {
			// Determine the assigned data type
			switch ( dataType ) {
			case STRING:
				// Return the key at the provided position
				return stringKey.get ( position );
			case SERIALIZABLE:
				// Return the key at the provided position
				return serializableKey.get ( position );
			default:
				// Invalid assigned data type
				return null;
			} // End switch
		} catch ( Exception exception ) {
			// The provided position is invalid /or/ there are no data of the provided data type
			return null;
		} // End of try-catch block
	}
	
	/**
	 * Retrieves the data value of the provided data type at the provided position.
	 * 
	 * @param dataType	An enumeration constant of {@link me.SyncWise.Android.Modules.MenuList.MenuItem.AssignedDataType MenuItem.AssignedDataType} indicating the required data type.
	 * @param position	Integer holding the position of the required data value.
	 * @return	Object representing the data value of the required data type and the provided position.<br>{@code NULL} is returned if the data type and/or the position is invalid.
	 */
	public Object getData ( final AssignedDataType dataType , final int position ) {
		try {
			// Determine the assigned data type
			switch ( dataType ) {
			case STRING:
				// Return the data at the provided position
				return stringData.get ( position );
			case SERIALIZABLE:
				// Return the data at the provided position
				return serializableData.get ( position );
			default:
				// Invalid assigned data type
				return null;
			} // End switch
		} catch ( Exception exception ) {
			// The provided position is invalid /or/ there are no data of the provided data type
			return null;
		} // End of try-catch block
	}
	
}