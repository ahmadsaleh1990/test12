/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Widgets;

import me.SyncWise.Android.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * Number Picker Widget.
 * 
 * @author Elias
 *
 */
public class NumberPicker extends LinearLayout implements OnClickListener {

	/**
	 * Flag used to indicate if the number picker is enabled or not.
	 */
	private boolean isEnabled;
	
	/**
	 * Boolean used to indicate if the number has a fraction or not.
	 */
	private boolean isDecimal;
	public double value;
	/**
	 * Boolean used to indicate if the number is signed or not (can be negative or not).
	 */
	private boolean isSigned;
	
	/**
	 * Boolean used to indicate if the number is be selected upon focus.
	 */
	private boolean selectAllOnFocus;
	
	/**
	 * Reference to the number picker edit text.
	 */
	private EditText editText;
	
    /**
     * Setter - {@link #isEnabled}
     * 
     * @param isEnabled	Flag used to indicate if the number picker is enabled or not.
     */
    public void setEnabled ( final boolean isEnabled ) {
    	// Set flag
        this.isEnabled = isEnabled;
        // Refresh the enabled state
        refreshEnabledState ();
    }
    
    /**
     * Setter - {@link #isDecimal}
     * 
     * @param isDecimal	Boolean used to indicate if the number has a fraction or not.
     */
    public void setDecimal ( final boolean isDecimal ) {
    	// Set flag
        this.isDecimal = isDecimal;
        // Refresh the number picker edit text
        refreshEditText ();
    }
    
    /**
     * Setter - {@link #isSigned}
     * 
     * @param isSigned	Boolean used to indicate if the number is signed or not (can be negative or not).
     */
    public void setSigned ( final boolean isSigned ) {
    	// Set flag
        this.isSigned = isSigned;
        // Refresh the number picker edit text
        refreshEditText ();
    }
    
    /**
     * Setter - {@link #selectAllOnFocus}
     * 
     * @param selectAllOnFocus	Boolean used to indicate if the number is be selected upon focus.
     */
    public void setSelectAllOnFocus ( final boolean selectAllOnFocus ) {
    	// Set flag
        this.selectAllOnFocus = selectAllOnFocus;
        // Refresh the number picker edit text
        refreshEditText ();
    }
	
	/**
	 * Constructor.
	 * 
	 * @param context	The application context.
	 */
	public NumberPicker ( Context context ) {
		// Superclass method call
		super ( context );
		// Initialize number picker view
		initialize ();
	}
	
	/**
	 * Constructor (overloaded).
	 * 
	 * @param context	The application context.
	 * @param attrs	{@link android.util.AttributeSet AttributeSet} object containing information retrieved from the compiled XML.
	 */
	public NumberPicker ( Context context , AttributeSet attrs ) {
		// Superclass method call
		super ( context , attrs );
		try {
			// Check if the provided attribute set is valid
			if ( attrs != null ) {
				// Retrieve styled attribute information in this Context's theme
				TypedArray attributes = getContext ().obtainStyledAttributes ( attrs , R.styleable.NumberPicker , 0 , 0 );
				// Retrieve the 'isDecimal' style attribute
				isDecimal = attributes.getBoolean ( R.styleable.NumberPicker_isDecimal , isDecimal );
				// Retrieve the 'isDecimal' style attribute
				isSigned = attributes.getBoolean ( R.styleable.NumberPicker_isSigned , isSigned );
				// Retrieve the 'selectAllOnFocus' style attribute
				selectAllOnFocus = attributes.getBoolean ( R.styleable.NumberPicker_selectAllOnFocus , selectAllOnFocus );
				// Give back a previously retrieved array, for later re-use
				attributes.recycle ();
			} // End if
			// Initialize number picker view
			initialize ();
		} catch ( Exception exception ) {
			// Do nothing
		} // End of try-catch block
	}
	
	/**
	 * Gets the layout resource ID.
	 * 
	 * @return	Integer holding the layout resource ID.
	 */
	protected int getLayout () {
		// Return the layout resource ID
		return R.layout.number_picker;
	}
	
	/**
	 * Method used to to set the view's orientation.
	 */
	protected void setOrientation () {
		// Set the orientation of the current linear layout
		setOrientation ( LinearLayout.VERTICAL );
	}
	
	/**
	 * Initializes the number picker view components.
	 */
	private void initialize () {
		// Set the orientation of the current linear layout
		setOrientation ();
		// Inflate the number picker view using the current linear layout as parent
		LayoutInflater.from ( getContext () ).inflate ( getLayout () , this );
		// Enable the number picker view
		isEnabled = true;
		value=1;
		// Retrieve a reference to the number picker edit text
		editText = (EditText) findViewById ( R.id.numberpicker_input );
        // Refresh the number picker edit text
        refreshEditText ();
		// Sets the string value of the number picker
		editText.setText ( "0" );
		// Register a callback to be invoked when the increment and decrement icons are clicked
		findViewById ( R.id.icon_increment ).setOnClickListener ( this );
		findViewById ( R.id.icon_decrement ).setOnClickListener ( this );
	}
	
	/**
	 * Enables / Disables the number picker view accordingly.
	 */
	private void refreshEnabledState () {
        // Iterate over all the linear layout children
        for ( int i = 0 ; i < getChildCount () ; i ++ )
        	// Enable the current child accordingly
        	getChildAt ( i ).setEnabled ( isEnabled );
	}
	
	/**
	 * Sets the appropriate number picker edit text properties.
	 */
	private void refreshEditText () {
		// Compute the raw input type
		int rawInputTupe = InputType.TYPE_CLASS_NUMBER;
		// Check if the number has a fraction
		if ( isDecimal )
			// Apply the appropriate flag
			rawInputTupe = rawInputTupe | InputType.TYPE_NUMBER_FLAG_DECIMAL;
		// Check if the number is signed
		if ( isSigned )
			// Apply the appropriate flag
			rawInputTupe = rawInputTupe | InputType.TYPE_NUMBER_FLAG_SIGNED;
		// Apply the content type integer of the edit text
		editText.setRawInputType ( rawInputTupe );
		// Apply the whole selection upon focus of the edit text
		editText.setSelectAllOnFocus ( selectAllOnFocus );
	}
	
	/**
	 * Display an error on the number picker edit text.
	 * 
	 * @param error	String holding the error message, or {@code NULL} if there is no message.
	 */
	public void displayError ( final String error ) {
		// Display error on field
		editText.setError ( error == null ? "" : error );
	}
	
	/**
	 * Hides the number picker edit text error.
	 */
	public void hideError () {
		// Hide error
		editText.setError ( null );
	}
	
	/*
	 * Called when a view has been clicked.
	 *
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick ( View view ) {
		// Check if the view is either the increment or decrement icon
		if ( view == null || ( view.getId () != R.id.icon_increment && view.getId () != R.id.icon_decrement ) )
			// Invalid view, do nothing
			return;
		// Otherwise the view is valid and is either the increment or decrement icon
		// Retrieve the current edit text content
		String oldContent = editText.getText ().toString ();
		// Check if the old content is an empty string
		if ( oldContent != null && oldContent.isEmpty () )
			// Set the old content as zero
			oldContent = "0";
		
		try {
			// Cast the content to double
			double content = Double.valueOf ( oldContent );
			// Increment / decrement the content
			content = view.getId () == R.id.icon_increment ? content + value : content - value; 
			
			// Set the new content
			setNumber ( content );
		} catch ( Exception exception ) {
			// Do nothing
		} // End of try-catch block
	}
	
	/**
	 * Sets the number picker value.
	 * 
	 * @param number	The number picker value to be set.
	 */
	public void setNumber ( final double number ) {
		// Check if the new number is negative while the number picker is NOT signed
		if ( ! isSigned && number < 0 )
			// Do nothing
			return;
		
		// Check if the edit text is valid
		if ( editText == null )
			// Invalid reference
			return;
		
		// Determine if the number picker value has a fraction
		if ( isDecimal )
			// Set the new number picker content
			editText.setText ( String.valueOf ( number ) );
		else
			// Set the new number picker content
			editText.setText ( String.valueOf ( (int) number ) );
	}
	
	/**
	 * Gets the number picker value.
	 * 
	 * @return	The number picker value, or {@code 0} if invalid.
	 */
	public double getNumber () {
		try {
			// Cast and return the content of the number picker to double
			return Double.valueOf ( editText.getText ().toString () );
		} catch ( Exception exception ) {
			// Invalid number
			return 0;
		} // End of try-catch block
	}
	
	/**
	 * Determines if the number picker content is valid.
	 * 
	 * @return	Boolean indicating if the number picker content is valid.
	 */
	public boolean isValid () {
		try {
			// Cast and return the content of the number picker to double
			Double.valueOf ( editText.getText ().toString () );
			// Indicate that the number is valid
			return true;
		} catch ( Exception exception ) {
			// Invalid number
			return false;
		} // End of try-catch block
	}
	
	/**
	 * Sets the number picker value.
	 * 
	 * @param text	The number picker value to be set.
	 */
	public void setText ( final String text) {
		// Check of the text is valid
		if ( text != null )
			// Set the new number picker content
			editText.setText ( text );
	}
	
	/**
	 * Gets the string representation of the number picker value.
	 * 
	 * @return	The string representation of the number picker value, or {@code NULL} if invalid.
	 */
	public String getText () {
		// Check if the edit text reference is valid
		if ( editText != null )
			// Return the edit text content
			return editText.getText ().toString ();
		// Otherwise the edit text reference is invalid
		return null;
	}
	
	/**
	 * Getter - {@link #editText}
	 * 
	 * @return	Reference to the number picker edit text.
	 */
	public EditText getEditText () {
		return editText;
	}
	
	/*
	 * Hook allowing a view to generate a representation of its internal state that can later be used to create a new instance with that same state.
	 *
	 * @see android.view.View#onSaveInstanceState()
	 */
	@Override
	protected Parcelable onSaveInstanceState() {
		// Superclass method call
		Parcelable superState = super.onSaveInstanceState();
		// Save the current view instance state and return the saved state
		return new SavedState ( superState , editText.getText ().toString () , isEnabled , isDecimal , isSigned , selectAllOnFocus );
	}
	
	/*
	 * Hook allowing a view to re-apply a representation of its internal state that had previously been generated by onSaveInstanceState(). 
	 *
	 * @see android.view.View#onRestoreInstanceState(android.os.Parcelable)
	 */
    @Override
    protected void onRestoreInstanceState ( Parcelable state ) {
    	// Cast the parcelable object to SavedState in order to retrieve the saved instance state
        SavedState savedState = (SavedState) state;
        // Superclass method call
        super.onRestoreInstanceState ( savedState.getSuperState () );
        // Restore the view instance state
        editText.setText ( savedState.number );
        isEnabled = savedState.isEnabled;
        isDecimal = savedState.isDecimal;
        isSigned = savedState.isSigned;
        selectAllOnFocus = savedState.selectAllOnFocus;
        // Refresh the enabled state
        refreshEnabledState ();
    }
	
    /*
     * Called by saveHierarchyState(android.util.SparseArray) to store the state for this view and its children.
     *
     * @see android.view.ViewGroup#dispatchSaveInstanceState(android.util.SparseArray)
     */
    @Override
    protected void dispatchSaveInstanceState ( SparseArray < Parcelable > container ) {
    	// This custom view save its own instance state
    	// Ensure the children views don't save their state as well.
        super.dispatchFreezeSelfOnly ( container );
    }
    
    /*
     * Called by restoreHierarchyState(android.util.SparseArray) to retrieve the state for this view and its children.
     *
     * @see android.view.ViewGroup#dispatchRestoreInstanceState(android.util.SparseArray)
     */
    @Override
    protected void dispatchRestoreInstanceState ( SparseArray < Parcelable > container ) {
    	// This custom view restore its own instance state.
    	// Ensure the children views don't restore their state as well.
        super.dispatchThawSelfOnly ( container );
    }
    
	/**
	 * Class used to saved / restore the view instance state.
	 * 
	 * @author Elias
	 *
	 */
	protected static class SavedState extends BaseSavedState {

		/**
		 * @see NumberPicker#editText
		 */
		private String number;
		
		/**
		 * @see NumberPicker#isEnabled
		 */
		private boolean isEnabled;
		
		/**
		 * @see NumberPicker#isDecimal
		 */
		private boolean isDecimal;
		
		/**
		 * @see NumberPicker#isSigned
		 */
		private boolean isSigned;
		
		/**
		 * @see NumberPicker#selectAllOnFocus
		 */
		private boolean selectAllOnFocus;

		/**
		 * Constructor
		 * 
		 * @param superState	Parcelable object holding the view's parent saved state. 
		 * @param number	String holding the number picker edit text content.
		 * @param isEnabled	Flag used to indicate if the number picker is enabled or not.
		 * @param isDecimal	Boolean used to indicate if the number has a fraction or not.
		 * @param isSigned	Boolean used to indicate if the number is signed or not (can be negative or not).
		 * @param selectAllOnFocus	Boolean used to indicate if the number is be selected upon focus.
		 */
		private SavedState ( Parcelable superState , String number , boolean isEnabled , boolean isDecimal , boolean isSigned , boolean selectAllOnFocus ) {
			// Superclass method call
			super ( superState );
			// Initialize attributes
			this.number = number;
			this.isEnabled = isEnabled;
			this.isDecimal = isDecimal;
			this.isSigned = isSigned;
			this.selectAllOnFocus = selectAllOnFocus;
		}

		/**
		 * Constructor
		 * 
		 * @param in	Parcel holding the view's saved state.
		 */
		private SavedState ( Parcel in ) {
			// Superclass method call
			super ( in );
			// Read attributes from parcel
			// The order should be preserved (while reading/writing from/in parcel)
			number = in.readString ();
			isEnabled = in.readByte () == 1;
			isDecimal = in.readByte () == 1;
			isSigned = in.readByte () == 1;
			selectAllOnFocus = in.readByte () == 1;
		}

		/*
		 * Flatten this object in to a Parcel.
		 *
		 * @see android.view.AbsSavedState#writeToParcel(android.os.Parcel, int)
		 */
		@Override
		public void writeToParcel ( Parcel destination , int flags ) {
			// Superclass method call
			super.writeToParcel ( destination , flags );
			// Write attributes in Parcel
			// The order should be preserved (while reading/writing from/in parcel)
			destination.writeString ( number );
			destination.writeByte ( (byte) ( isEnabled ? 1 : 0 ) );
			destination.writeByte ( (byte) ( isDecimal ? 1 : 0 ) );
			destination.writeByte ( (byte) ( isSigned ? 1 : 0 ) );
			destination.writeByte ( (byte) ( selectAllOnFocus ? 1 : 0 ) );
		}

		/**
		 * Interface implementation provided as a public CREATOR field that generates instances of the Parcelable class from a Parcel. 
		 */
		public static final Parcelable.Creator < SavedState > CREATOR = new Creator < SavedState > () {

			/*
			 * Create a new instance of the Parcelable class, instantiating it from the given Parcel whose data had previously been written by Parcelable.writeToParcel().
			 *
			 * @see android.os.Parcelable.Creator#createFromParcel(android.os.Parcel)
			 */
			public SavedState createFromParcel ( Parcel in ) {
				// Build and return an instance of the Parcelable class
				return new SavedState ( in );
			}

			/*
			 * Create a new array of the Parcelable class.
			 *
			 * @see android.os.Parcelable.Creator#newArray(int)
			 */
			public SavedState [] newArray ( int size ) {
				// Build and return an array of the Parcelable class
				return new SavedState [ size ];
			}

		};

	}
	
}
