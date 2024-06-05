/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android;

import java.util.List;

import me.SyncWise.Android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;

/**
 * Helper Class used to display dialogs.
 * 
 * @author Elias
 *
 */
public class AppDialog {
	
	/**
	 * Class used to define a dialog activity property.<br>
	 * The dialog activity property defines the dialog size of the activity. It currently supports two types :
	 * <ul>
	 * <li>A fixed size for the width and height.</li>
	 * <li>A percentage (between 0 and 100) of the screen's size.</li>
	 * </ul>
	 * 
	 * @author Elias
	 *
	 */
	public static class Property {
		
		/**
		 * Double holding the dialog's size as a ratio based on the screen size : a percentage.
		 */
		private final Double percentage;
		
		/**
		 * Flag indicating if the window's height is to be wrapped to its content.
		 */
		private final Boolean wrapHeight;
		
		/**
		 * Integer holding the dialog's width.
		 */
		private final Integer width;
		
		/**
		 * Integer holding the dialog's height.
		 */
		private final Integer height;
		
		/**
		 * Determines if the current property represents the dialog size as a ratio / percentage of the screen's size.
		 * 
		 * @return	Boolean indicating if this property holds a percentage for the dialog's size.
		 */
		public boolean isPercentage () {
			return ( percentage == null || wrapHeight == null ? false : true );
		}
		
		/**
		 * Getter - {@link #percentage}
		 * 
		 * @return	Double holding the dialog's size a percentage / ratio of the screen's size, or {@code NULL} if not defined.
		 */
		public Double getPerentage () {
			return percentage;
		}
		
		/**
		 * Getter - {@link #wrapHeight}
		 * 
		 * @return	Boolean indicating if the window's height is to be wrapped to its content.
		 */
		public boolean getWrapHeight () {
			return wrapHeight;
		}
		
		/**
		 * Determines if the current property represents the dialog's size using a fixed width and height in pixels.
		 * 
		 * @return	Boolean indicating if this property holds a fixed size for the dialog.
		 */
		public boolean isSize () {
			return ( width == null || height == null ? false : true );
		}
		
		/**
		 * Getter - {@link #width}
		 * 
		 * @return	Integer holding the dialog's fixed width in pixels, or {@code NULL} if not defined.
		 */
		public Integer getWidth () {
			return width;
		}
		
		/**
		 * Getter - {@link #height}
		 * 
		 * @return	Integer holding the dialog's fixed height in pixels, or {@code NULL} if not defined.
		 */
		public Integer getHeight () {
			return height;
		}
		
		/**
		 * Constructor.<br>
		 * Defines the property as a percentage of the screen's size.<br>
		 * <b>Note</b> : By default, the height is not wrapped.
		 * 
		 * @param percentage	Double holding the dialog's size a percentage / ratio of the screen's size.
		 */
		public Property ( double percentage ) {
			// Do not wrap height
			this ( percentage , false );
		}
		
		/**
		 * Constructor.<br>
		 * Defines the property as a percentage of the screen's size.
		 * 
		 * @param percentage	Double holding the dialog's size a percentage / ratio of the screen's size.
		 * @param wrapHeight	Boolean indicating if the window's height is to be wrapped to its content.
		 */
		public Property ( double percentage , boolean wrapHeight ) {
			// Initialize attributes
			this.percentage = ( percentage < 0 || percentage > 100 ? null : percentage );
			this.wrapHeight = wrapHeight;
			this.width = null;
			this.height = null;
		}
		
		/**
		 * Constructor.<br>
		 * Defines the property as a fixed width and height.
		 * 
		 * @param width	Integer holding the dialog's fixed width in pixels.
		 * @param height	Integer holding the dialog's fixed height in pixels.
		 */
		public Property ( int width , int height ) {
			// Initialize attributes
			this.percentage = null;
			this.wrapHeight = null;
			this.width = width;
			this.height = height;
		}
		
	}
	
	/**
	 * Displays an activity as a pop up dialog with an action bar.<br>
	 * This method should be used in conjunction with the theme {@code DialogActivity} that must be applied in the manifest file as follows :<br>
	 * {@code android:theme="@style/DialogActivity"}
	 * 
	 * @param activity	Reference to an activity that should be displayed as a dialog.
	 * @param property	Property object used to define the dialog activity size.
	 */
	public static void showAsDialog ( final Activity activity , final Property property ) {
		// Check if the activity is valid
		if ( activity == null )
			// Invalid activity, exit method
			return;
	    //To show activity as dialog and dim the background, you need to declare android:theme="@style/PopupTheme" on for the chosen activity on the manifest
		// Enable extended window feature : action bar
		activity.requestWindowFeature ( Window.FEATURE_ACTION_BAR );
		// Dim the 
	    activity.getWindow ().setFlags ( WindowManager.LayoutParams.FLAG_DIM_BEHIND , WindowManager.LayoutParams.FLAG_DIM_BEHIND );
	    // Retrieve a reference to the layout parameters of the current window
	    LayoutParams params = activity.getWindow ().getAttributes (); 
	    // Apply the default height and width
	    params.height = LayoutParams.MATCH_PARENT;
	    params.width = LayoutParams.MATCH_PARENT;
	    // Determine if a valid size property is provided
	    if ( property != null && property.isSize () ) {
	    	// Apply the property's height and width
		    params.height = property.getHeight ();
		    params.width = property.getWidth ();
	    } // End if
	    // Determine if a valid percentage property is provided
	    else if ( property != null && property.isPercentage () ) {
			// Declare and initialize a point object used to store the screen size
			Point size = new Point();
			// Compute the screen size
			activity.getWindowManager ().getDefaultDisplay ().getSize ( size );
			// Compute and apply the property's height and width
		    params.height = ( property.getWrapHeight () ? LayoutParams.WRAP_CONTENT : (int) ( size.y * property.getPerentage () / 100 ) );
		    params.width = (int) ( size.x * property.getPerentage () / 100 );
	    } // End else if
	    // Make the the entire window fully opaque 
	    params.alpha = 1.0f; 
	    // Dim the background 50 %
	    params.dimAmount = 0.5f;
	    // Apply the new layout parameters to the current window
	    activity.getWindow().setAttributes ( ( android.view.WindowManager.LayoutParams ) params );
	}
	
	/**
	 * Enumeration used to define the various types of dialogs implemented in this class.
	 * 
	 * @author Elias
	 *
	 */
	private static enum DialogsType {
		NONE ,
		ALERT ,
		LIST ,
		PROGRESS ,
		DATE_PICKER ,
	}
	
	/**
	 * Enumeration constant used to indicate the type of the currently used dialog.
	 */
	private DialogsType dialogType;
	
	/**
	 * Enumeration used to define the various types of buttons used in dialogs.
	 * 
	 * @author Elias
	 *
	 */
	public enum ButtonsType {
		NONE ,
		OK ,
		OkCancel ,
		YesNo ,
		YesNoCancel ,
	}
	
	/**
	 * Enumeration used to define the various cancelable scenarios used in dialogs.
	 * 
	 * @author Elias
	 *
	 */
	public enum Cancelable {
		BACK_ONLY ,
		BACK_BUTTON_AND_TOUCH ,
		NOT_CANCELABLE
	}
	
	/**
	 * Reference to the {@link android.app.Dialog Dialog} object used.
	 */
	private Dialog dialog;
	
	/**
	 * Singleton instance of {@code AppDialog} initialized at the application startup.
	 */
	private static final AppDialog singleton = new AppDialog ();
	
    /**
     * Initializes and keeps track of only one class instance.
     *
     * @return	An instance of {@link me.SyncWise.Android.AppDialog AppDialog}.
     */
	public static AppDialog getInstance () {
		// Return the singleton instance
		return singleton;
	}
	
	/**
	 * Constructor, made private for singleton implementation.
	 */
	private AppDialog () {
		// Empty Constructor
    }
	
	/**
	 * Displays the last used dialog (if any).
	 */
	public void display () {
		// Check if there is a previously displayed dialog
		if ( dialogType == DialogsType.NONE )
			// No previously display dialog to dismiss
			return;
		try {
			// Check if the last displayed dialog is still showing
			if ( ! dialog.isShowing () )
				// Display the dialog
				dialog.show ();
		} catch ( Exception exception ) {
			// Null Pointer Exception
		} finally {
			// Reset references
			dialog = null;
			dialogType = DialogsType.NONE;
		} // End of try-catch-finally block
	}
	
	/**
	 * Dismisses the currently displayed dialog (if any).
	 */
	public void dismiss () {
		// Check if there is a previously displayed dialog
		if ( dialogType == DialogsType.NONE )
			// No previously display dialog to dismiss
			return;
		try {
			// Dismiss the dialog
			dialog.dismiss ();
		} catch ( Exception exception ) {
			// Null Pointer Exception
		} finally {
			// Reset references
			dialog = null;
			dialogType = DialogsType.NONE;
		} // End of try-catch-finally block
	}
	
	/**
	 * Sets the enabled state of the indicated alert dialog button.
	 * 
	 * @param whichButton	The identifier of the button that should be returned.
	 * @param enabled	{@code True} if this view is enabled, {@code false} otherwise.
	 */
	public void setAlterButtonEnabled ( final int whichButton , final boolean enabled ) {
		// Check if there is a previously displayed dialog
		if ( dialogType == DialogsType.NONE )
			// No previously display dialog to dismiss
			return;
		// Check if the displayed dialog is an alert dialog
		if ( dialogType != DialogsType.ALERT || ! ( dialog instanceof AlertDialog ) )
			// The currently displayed dialog is NOT an alert dialog
			return;
		try {
			// Retrieve the indicated button 
			Button button = ( ( AlertDialog ) dialog ).getButton ( whichButton );
			// Set the enabled state
			button.setEnabled ( enabled );
		} catch ( Exception exception ) {
			// Null Pointer Exception
		} // End of try-catch block
	}
	
	/**
	 * Displays a list dialog.<br>
	 * The dialog title is optional (provide an empty string or {@code null}).
	 * 
	 * @param context	The application context.
	 * @param title	The list dialog title.
	 * @param items	List of items to display in the list dialog.
	 * @param cancelable	An enumeration constant of {@link me.SyncWise.Android.AppDialog.Cancelable AppDialog.Cancelable} indicating how the dialog can be cancelled.
	 * @param onClickListener	A click listener used to execute statements if a dialog item is selected.
	 * @param onCancelListener	A cancel listener used to execute statements if a dialog is dismissed.
	 */
	public void displayList ( final Context context , final String title , final List < String > items , final AppDialog.Cancelable cancelable , final DialogInterface.OnClickListener onClickListener , final DialogInterface.OnCancelListener onCancelListener ) {
		// Use array instead of list
		String _items [] = new String [ items.size () ];
		_items = items.toArray ( _items );
		// Display list
		displayList ( context , title , _items , cancelable , onClickListener , onCancelListener );
	}
	
	/**
	 * Displays a list dialog.<br>
	 * The dialog title is optional (provide an empty string or {@code null}).
	 * 
	 * @param context	The application context.
	 * @param title	The list dialog title.
	 * @param items	Array of items to display in the list dialog.
	 * @param cancelable	An enumeration constant of {@link me.SyncWise.Android.AppDialog.Cancelable AppDialog.Cancelable} indicating how the dialog can be cancelled.
	 * @param onClickListener	A click listener used to execute statements if a dialog item is selected.
	 * @param onCancelListener	A cancel listener used to execute statements if a dialog is dismissed.
	 */
	public void displayList ( final Context context , final String title , final String [] items , final AppDialog.Cancelable cancelable , final DialogInterface.OnClickListener onClickListener , final DialogInterface.OnCancelListener onCancelListener ) {
		// Determine if there the items array is valid
		if ( items == null || items.length == 0 )
			// Invalid items array
			return;
		try {
			// Dismiss any previous dialogs
			dismiss ();
			// Set the appropriate dialog type
			dialogType = DialogsType.LIST;
			// Declare and initialize an alert dialog builder
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder ( context );
			// Determine if the list dialog can be cancelled
			alertDialogBuilder.setCancelable ( cancelable == AppDialog.Cancelable.NOT_CANCELABLE ? false : true );
			// Determine if a title is provided
			if ( ! TextUtils.isEmpty ( title ) )
				// Set the title
				alertDialogBuilder.setTitle ( title );
			// Assign items to the list dialog along with a click listener
			alertDialogBuilder.setItems ( items , onClickListener );
			// Assign a cancel listener
			alertDialogBuilder.setOnCancelListener ( onCancelListener );
			// Create and store the alert dialog
			dialog = alertDialogBuilder.create ();
			// Set whether this dialog is canceled when touched outside the window's bounds
			dialog.setCanceledOnTouchOutside ( cancelable == AppDialog.Cancelable.BACK_BUTTON_AND_TOUCH );
			// Display the alert dialog
			dialog.show ();
		} catch ( Exception exception ) {
			// An error occurred and the dialog is not displayed, reset references
			dialog = null;
			dialogType = DialogsType.NONE;
		} // End of try-catch block
	}
	
	/**
	 * Displays an alert dialog.<br>
	 * The dialog title and description are optional (provide an empty string or {@code null}).
	 * 
	 * @param context	The application context.
	 * @param title	The alert dialog title.
	 * @param description	The alert dialog description.
	 * @param buttonsType	An enumeration constant of {@link me.SyncWise.Android.AppDialog.ButtonsType AppDialog.ButtonsType} indicating the buttons to display.
	 * @param onClickListener	A click listener used to execute statements if a dialog button is clicked.
	 */
	public void displayAlert ( final Context context , final String title , final String description , final AppDialog.ButtonsType buttonsType , final DialogInterface.OnClickListener onClickListener ) {
		try {
			// Dismiss any previous dialogs
			dismiss ();
			// Set the appropriate dialog type
			dialogType = DialogsType.ALERT;
			// Declare a flag used to determine if the dialog is cancelable
			boolean cancelable = false;
			// Declare and initialize an alert dialog builder
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder ( context );
			// By default, the alert dialog cannot be cancelled UNLESS it has no buttons
			alertDialogBuilder.setCancelable ( false );
			// Determine if a title is provided
			if ( ! TextUtils.isEmpty ( title ) )
				// Set the title
				alertDialogBuilder.setTitle ( title );
			// Determine if a description is provided
			if ( ! TextUtils.isEmpty ( description ) )
				// Set the description
				alertDialogBuilder.setMessage ( description );
			// Determine the buttons type
			switch ( buttonsType ) {
			case OK:
				// Map the neutral button
				alertDialogBuilder.setNeutralButton ( AppResources.getInstance ( context ).getString ( context , R.string.ok_label ) , onClickListener );
				break;
			case OkCancel:
				// Map the positive and negative buttons
				alertDialogBuilder.setPositiveButton ( AppResources.getInstance ( context ).getString ( context , R.string.ok_label ) , onClickListener );
				alertDialogBuilder.setNegativeButton ( AppResources.getInstance ( context ).getString ( context , R.string.cancel_label ) , onClickListener );
				break;
			case YesNo:
				// Map the positive and negative buttons
				alertDialogBuilder.setPositiveButton ( AppResources.getInstance ( context ).getString ( context , R.string.yes_label ) , onClickListener );
				alertDialogBuilder.setNegativeButton ( AppResources.getInstance ( context ).getString ( context , R.string.no_label ) , onClickListener );
				break;
			case YesNoCancel:
				// Map the positive, negative and neutral buttons
				alertDialogBuilder.setPositiveButton ( AppResources.getInstance ( context ).getString ( context , R.string.yes_label ) , onClickListener );
				alertDialogBuilder.setNegativeButton ( AppResources.getInstance ( context ).getString ( context , R.string.no_label ) , onClickListener );
				alertDialogBuilder.setNeutralButton ( AppResources.getInstance ( context ).getString ( context , R.string.cancel_label ) , onClickListener );
				break;
			case NONE:
			default:
				// The alert dialog has no buttons and hence can be cancelled
				cancelable = true;
				alertDialogBuilder.setCancelable ( true );
				break;
			} // End switch
			// Create and store the alert dialog
			dialog = alertDialogBuilder.create ();
			// Set whether this dialog is canceled when touched outside the window's bounds
			if ( cancelable )
				dialog.setCanceledOnTouchOutside ( true );
			// Display the alert dialog
			dialog.show ();
		} catch ( Exception exception ) {
			// An error occurred and the dialog is not displayed, reset references
			dialog = null;
			dialogType = DialogsType.NONE;
		} // End of try-catch block
	}
	
	/**
	 * Displays a date picker dialog.
	 * 
	 * @param context	The application context.
	 * @param year	The year to display.
	 * @param monthOfYear	The month to display.
	 * @param dayOfMonth	The day to display.
	 * @param minDate	The minimum date to apply, or {@code NULL}.
	 * @param isCalendar	Boolean indicating if a calendar is shown inside the date picker view (used to select the date).
	 * @param isSpinners	Boolean indicating if spinners are shown inside the date picker view (used to select the date).
	 * @param callBack	{@link android.app.DatePickerDialog.OnDateSetListener DatePickerDialog.OnDateSetListener} call back method applied to the date picker dialog.
	 */
	public void displayDatePicker ( final Context context , final int year , final int monthOfYear , final int dayOfMonth , final Long minDate , final boolean isCalendar , final boolean isSpinners , final DatePickerDialog.OnDateSetListener callBack , final DialogInterface.OnClickListener onClearListener ) {
		try {
			// Dismiss any previous dialogs
			dismiss ();
			// Set the appropriate dialog type
			dialogType = DialogsType.DATE_PICKER;
			// Declare and initialize a date picker dialog
			DatePickerDialog datePickerDialog = new DatePickerDialog ( context , callBack , year , monthOfYear , dayOfMonth );
			// Check if a minimum date is provided
			if ( minDate != null )
				// Set the minimum date
				datePickerDialog.getDatePicker ().setMinDate ( minDate );
			// Determine if the calendar view must be displayed
			datePickerDialog.getDatePicker ().setCalendarViewShown ( isCalendar );
			// Determine if the spinners view must be displayed
			datePickerDialog.getDatePicker ().setSpinnersShown ( isSpinners );
			// Check if a clear listener is provided
			if ( onClearListener != null )
				// Set the neutral button as clear 
				datePickerDialog.setButton ( DialogInterface.BUTTON_NEUTRAL , AppResources.getInstance ( context ).getString ( context , R.string.clear_label ) , onClearListener );
			// Store the date picker dialog
			dialog = datePickerDialog;
			// Display the date picker dialog
			dialog.show ();
		} catch ( Exception exception ) {
			// An error occurred and the dialog is not displayed, reset references
			dialog = null;
			dialogType = DialogsType.NONE;
		} // End of try-catch block
	}
	
	public void displayTimePicker ( final Context context , final int hourOfDay , final int minute , final boolean is24HourView ,final TimePickerDialog.OnTimeSetListener callBack ) {
		try {
			// Dismiss any previous dialogs
			dismiss ();
			// Set the appropriate dialog type
			dialogType = DialogsType.DATE_PICKER;
			// Declare and initialize a time picker dialog
			TimePickerDialog timePickerDialog = new TimePickerDialog(context, callBack, hourOfDay, minute, is24HourView);

			// Store the date picker dialog
			dialog = timePickerDialog;
			// Display the date picker dialog
			dialog.show ();
		} catch ( Exception exception ) {
			// An error occurred and the dialog is not displayed, reset references
			dialog = null;
			dialogType = DialogsType.NONE;
		} // End of try-catch block
	}
	
	/**
	 * Displays a progress dialog using a spinner.<br>
	 * The dialog title and description are optional (provide an empty string or {@code null}).
	 * 
	 * @param context	The application context.
	 * @param title	The progress dialog title.
	 * @param description	The progress dialog description.
	 */
	public void displayIndeterminateProgress ( final Context context , final String title , final String description ) {
		try {
			// Dismiss any previous dialogs
			dismiss ();
			// Set the appropriate dialog type
			dialogType = DialogsType.PROGRESS;
			// Declare and initialize a progress dialog
			ProgressDialog progressDialog = new ProgressDialog ( context );
			// Define the progress dialog nature to be similar to a spinner
			progressDialog.setProgressStyle ( ProgressDialog.STYLE_SPINNER );
			// By default, the progress dialog cannot be cancelled due to its nature (removed when the progress ends)
			progressDialog.setCancelable ( false );
			// Determine if a title is provided
			if ( ! TextUtils.isEmpty ( title ) )
				// Set the title
				progressDialog.setTitle ( title );
			// Determine if a description is provided
			if ( ! TextUtils.isEmpty ( description ) )
				// Set the description
				progressDialog.setMessage ( description );
			// Store the progress dialog
			dialog = progressDialog;
			// Display the progress dialog
			dialog.show ();
		} catch ( Exception exception ) {
			// An error occurred and the dialog is not displayed, reset references
			dialog = null;
			dialogType = DialogsType.NONE;
		} // End of try-catch block
	}
	
	/**
	 * Displays a progress dialog using a progress bar.<br>
	 * The dialog title and description are optional (provide an empty string or {@code null}).<br>
	 * The progress of the displayed dialog can be updated using the  method.
	 * 
	 * @param context	The application context.
	 * @param title	The progress dialog title.
	 * @param description	The progress dialog description.
	 */
	public void displayProgress ( final Context context , final String title , final String description ) {
		try {
			// Dismiss any previous dialogs
			dismiss ();
			// Set the appropriate dialog type
			dialogType = DialogsType.PROGRESS;
			// Declare and initialize a progress dialog
			ProgressDialog progressDialog = new ProgressDialog ( context );
			// Define the progress dialog nature to be horizontal
			progressDialog.setProgressStyle ( ProgressDialog.STYLE_HORIZONTAL );
			// By default, the progress dialog cannot be cancelled due to its nature (removed when the progress ends)
			progressDialog.setCancelable ( false );
			// Determine if a title is provided
			if ( ! TextUtils.isEmpty ( title ) )
				// Set the title
				progressDialog.setTitle ( title );
			// Determine if a description is provided
			if ( ! TextUtils.isEmpty ( description ) )
				// Set the description
				progressDialog.setMessage ( description );
			// Store the progress dialog
			dialog = progressDialog;
			// Display the progress dialog
			dialog.show ();
		} catch ( Exception exception ) {
			// An error occurred and the dialog is not displayed, reset references
			dialog = null;
			dialogType = DialogsType.NONE;
		} // End of try-catch block
	}

	/**
	 * Sets the progress of the showing progress dialog (if any).
	 *
	 * @param progress	Integer representing the progress to set.
	 */
	public void setProgress ( final int progress ) {
		// Check if the currently displayed dialog has progress
		if ( dialogType != DialogsType.PROGRESS )
			// There is no progress dialog to set
			return;
		try {
			// Display the provided progress
			( (ProgressDialog) dialog ).setProgress ( progress );
		} catch ( Exception exception ) {
			// Could not update progress 
		} // End of try-catch block
	}

}