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
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * <b>Slide View widget.</b><br>
 * A widget representing the <em>slide to unlock</em> feature. In order to use it, simply add it in the layout XML file.<br>
 * Implement the {@link OnSlideListener} in order to handle slide events, and control them by locking or unlocking the slide view. 
 * 
 * <br><br><h1>XML Attributes:</h1><br>
 * <ul>
 * <li><b>locked</b> : <em>Related Method</em> {@link #lock()}</li>
 * <li><b>locked</b> : <em>Related Method</em> {@link #unlock()}</li>
 * <li><b>text</b> : <em>Related Method</em> {@link #setText(String)}</li>
 * </ul>
 * 
 * @author Elias
 *
 */
public class SlideView extends LinearLayout implements OnTouchListener , AnimationListener {

	/**
	 * Constant integer holding the sliding animation duration in milliseconds.
	 */
	public static final int SLIDING_ANIMATION_DURATION = 500;
	
	/**
	 * Integer holding the computed left margin of the slider.<br>
	 * This is not the effective left margin, conditions are applied on the layout left margin directly.
	 */
	private int leftMargin;
	
	/**
	 * Integer holding the previous raw X position of the touch event.<br>
	 * Used to compute the different between the current and previous raw X positions : the distance.
	 */
	private int previousRawX;
	
	/**
	 * Boolean used to indicate if the slide view is locked (has been completely slid).
	 */
	private boolean lock;
	
	/**
	 * Flag used to determine if the slide view should be unlocked after the slide event events.<br>
	 * This flag is used because the slide view should be unlocked during slide events.
	 */
	private boolean postUnlock;
	
	/**
	 * Boolean used to determine if the slide view is being slid.
	 */
	private boolean isSliding;
	
	/**
	 * Boolean used to determine if the {@link OnSlideListener} call back is directly executed when the slider reaches the end of the track or when the slider is released at the end of the track.
	 */
	private boolean triggerOnRelease;
	
	/**
	 * Reference to the slider image view.
	 */
	private ImageView slider;
	
	/**
	 * Reference to the slide view label. 
	 */
	private TextView label;
	
	/**
	 * Reference to the inner slide view layout.
	 */
	private FrameLayout innerLayout;
	
	/**
	 * Interface definition for a callback to be invoked when a slider is slid.
	 * 
	 * @author Elias
	 *
	 */
	public static interface OnSlideListener {
		
		/**
		 * Called when a slider has been slid.
		 * 
		 * @param slideView	The SlideView that was slid.
		 */
		abstract void onSlide ( final SlideView slideView );
		
	}
	
	/**
	 * Listener used to dispatch sliding events.
	 */
	private OnSlideListener onSlideListener;
	
	/**
	 * Register a callback to be invoked when this slide view is slid.
	 * 
	 * @param listener	The callback that will run.
	 */
	public void setOnSlideListener ( final OnSlideListener listener ) {
		// Set the on slide listener
		this.onSlideListener = listener;
	}
	
	/**
	 * Indicates if the slide view is currently being slid.
	 * 
	 * @return	Boolean indicating if the slide view is being slid.
	 */
	public boolean isSliding () {
		// Return flag
		return isSliding;
	}
	
	/**
	 * Indicates if the slide view is locked or not.<br>
	 * If the slide view is locked, it means that the call back method provided by the listener has been executed and will not be executed again.
	 * 
	 * @return	Boolean indicating whether the slide view is locked or not.
	 */
	public boolean isLocked () {
		// Return the lock flag
		return lock;
	}
	
	/**
	 * Locks the slide view.<br>
	 * Sliding the view will not invoke the call back method.
	 */
	public void lock () {
		// Set the flag
		lock = true;
	}
	
	/**
	 * Unlock the slide view.<br>
	 * Sliding the view will invoke the call back method.
	 */
	public void unlock () {
		// Check if the slide view is currently sliding
		if ( isSliding )
			// Flag the slide view to be unlocked at the end of the slide event
			postUnlock = true;
		else
			// Reset the flag
			lock = false;
	}
	
	/**
	 * Post unlocks the slide view :<br>
	 * The slide view will unlock (if it is locked) after the sliding events end.
	 */
	private void postUnlock () {
		// Check if the slide view should post unlock
		if ( postUnlock && ( ! isSliding ) ) {
			// Reset flag
			postUnlock = false;
			// Unlock slide view
			lock = false;
		} // End if
	}
	
	/**
	 * Setter - {@link #triggerOnRelease}
	 * 
	 * @param triggerOnRelease	Boolean indicating if the listener call back is directly executed at the end of the track of after the slider is released.
	 */
	public void setTriggerOnRelease ( final boolean triggerOnRelease ) {
		// Set the flag
		this.triggerOnRelease = triggerOnRelease;
	}
	
	/**
	 * Getter - {@link #triggerOnRelease}
	 * 
	 * @return	Boolean indicating if the listener call back is directly executed at the end of the track of after the slider is released.
	 */
	public boolean getTriggerOnRelease () {
		// Return the flag
		return triggerOnRelease;
	}
	
	/**
	 * Sets the text on the slide view.
	 * 
	 * @param text	String to be displayed.
	 */
	public void setText ( final String text ) {
		// Check if the provided text is valid
		if ( text != null && label != null ) {
			// Store the text as spannable string in order to apply span sections
			SpannableString string = new SpannableString ( text );
			// Assign the italic style
			string.setSpan ( new StyleSpan ( Typeface.ITALIC ) , 0 , string.length () , 0 );
			// Set the text on the slide view
			label.setText ( string );
		} // End if
	}
	
	/**
	 * Gets the text on the slide view.
	 * 
	 * @return	String displayed on the slide view.
	 */
	public String getText () {
		// Check if the slide view label reference is valid
		if ( label != null )
			// Return the displayed text on the slide view
			return label.getText ().toString ();
		// Invalid label reference
		return "";
	}
	
	/**
	 * Constructor.
	 * 
	 * @param context	The application context.
	 */
	public SlideView ( Context context ) {
		// Superclass method call
		super ( context );
		// Initialize slide view
		initialize ();
	}

	/**
	 * Constructor (overloaded).
	 * 
	 * @param context	The application context.
	 * @param attrs	{@link android.util.AttributeSet AttributeSet} object containing information retrieved from the compiled XML.
	 */
	public SlideView ( Context context , AttributeSet attrs ) {
		// Superclass method call
		super ( context , attrs );
		// Initialize slide view
		initialize ();
		// Check if the provided attribute set is valid
		if ( attrs != null ) {
			// Retrieve styled attribute information in this Context's theme
			TypedArray attributes = getContext ().obtainStyledAttributes ( attrs , R.styleable.SlideView , 0 , 0 );
			// Retrieve the 'locked' style attribute
			lock = attributes.getBoolean ( R.styleable.SlideView_locked , lock );
			// Retrieve the 'text' style attribute
			String text = attributes.getString ( R.styleable.SlideView_text );
			// Set the text
			setText ( text );
			// Give back a previously retrieved array, for later re-use
			attributes.recycle ();
		} // End if
	}

	/**
	 * Initializes the slide view components.
	 */
	private void initialize () {
		// Inflate the slide view using the current linear layout as parent
		LayoutInflater.from ( getContext () ).inflate ( R.layout.slide_view , this );
		// Set the gravity of the current linear layout
		setGravity ( Gravity.CENTER );
		// Retrieve a reference to the slide view label
		label = (TextView) findViewById ( R.id.label_slide_view );
		// Retrieve a reference to the slider
		slider = (ImageView) findViewById ( R.id.icon_slider );
		// Set the slider touch listener
		slider.setOnTouchListener ( this );
		// Retrieve a reference to the inner slide view layout
		innerLayout = (FrameLayout) findViewById ( R.id.inner_layout_slider );
	}
	
	/*
	 * Finalize inflating a view from XML.
	 *
	 * @see android.view.View#onFinishInflate()
	 */
    @Override
    protected void onFinishInflate () {
    	// Superclass method call
        super.onFinishInflate();
    }

    /*
     * // End switch
     *
     * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
     */
	@Override
	public boolean onTouch ( View view , MotionEvent event ) {
		// Retrieve the margin layout parameters of the slider
		ViewGroup.MarginLayoutParams sliderLayoutParams = (MarginLayoutParams) slider.getLayoutParams ();
		// Determine the event action
		switch ( event.getAction () ) {
		case MotionEvent.ACTION_DOWN:
			// Touch event started, set flag
			isSliding = true;
			// Store the current raw X position for later use
			previousRawX = (int) event.getRawX ();
			// Update the total left margin
			leftMargin = sliderLayoutParams.leftMargin;
			break;
		case MotionEvent.ACTION_UP:
			// Touch event ended, reset flag
			isSliding = false;
			// Invoke call back if the slider reached the end of the track and if slider release is required
			if ( triggerOnRelease && ( leftMargin > innerLayout.getWidth () - slider.getWidth () ) )
				onSlide ();
			// Check if the slide view should post unlock
			postUnlock ();
			// Animate the slider back to its initial position
			slider.startAnimation ( getAnimation ( sliderLayoutParams.leftMargin ) );
			break;
		case MotionEvent.ACTION_MOVE:
			// Ongoing touch event
			// Add to the slider the displaced distance
			leftMargin += (int) event.getRawX () - previousRawX;
			// Store the current raw X position for later use
			previousRawX = (int) event.getRawX ();
			// Determine if the left margin is invalid
			if ( leftMargin < 0 )
				// Invalid left margin
				sliderLayoutParams.leftMargin = 0;
			else if ( leftMargin > innerLayout.getWidth () - slider.getWidth () ) {
				// Invalid left margin
				sliderLayoutParams.leftMargin = innerLayout.getWidth () - slider.getWidth ();
				// Invoke call back if slider release is NOT required
				if ( ! triggerOnRelease )
					onSlide ();
			} // End else
			else
				// Valid left margin
				sliderLayoutParams.leftMargin = leftMargin;
			
			// Compute the ratio of the current slider left margin over one third of the remaining empty space in the slider
			float ratio = ( innerLayout.getWidth () - slider.getWidth () - (float) leftMargin * 3f ) / ( innerLayout.getWidth () - slider.getWidth () );
			// Determine if the ratio is between 0 and 1 (inclusive)
			if ( ratio >= 0 && ratio <= 1 )
				// Set the ratio as the slide view text alpha
				label.setAlpha ( ratio );
			// Assign the layout parameters to apply the new left margin value
			slider.setLayoutParams ( sliderLayoutParams );
			break;
		} // End switch
		return true;
	}
	
	/**
	 * Properly invokes the call back method provided by the assigned listener (if any).
	 */
	private void onSlide () {
		// Check if the on slide listener is valid
		if ( onSlideListener == null )
			// Invalid listener
			return;
		// Otherwise the slide listener is valid
		// Determine if the slide view is locked
		if ( ! lock ) {
			// Lock the slide view so that the listener is executed only once
			lock = true;
			try {
				// Invoke the call back method
				onSlideListener.onSlide ( this );
			} catch ( Exception exception ) {
				// Do nothing
			} // End of try-catch block
		} // End if
	}
	
	/**
	 * Builds and returns a translate animation used to animate the slider back to its initial position.
	 * The translate animation duration is set by {@link #SLIDING_ANIMATION_DURATION}.
	 * 
	 * @param currentX	Integer holding the current position of the slider relative to its parent (left margin).
	 * @return	An {@link android.view.animation.Animation Animation} defined to translate the slider to its initial position.
	 */
	private Animation getAnimation ( final int currentX ) {
		// Declare and initialize a translation animation
		TranslateAnimation translate = new TranslateAnimation ( 0 , - currentX , 0 , 0 );
		// Set an accelerate and decelerate interpolator in order to animate a sliding effect
		translate.setInterpolator ( new AccelerateDecelerateInterpolator () );
		// Set the animation duration
		translate.setDuration ( SLIDING_ANIMATION_DURATION );
		
		// Set an animation listener mainly used to block touch events during the animation
		translate.setAnimationListener ( this );
		
		// Return the translate animation
		return translate;
	}

	/*
	 * Notifies the start of the animation.
	 *
	 * @see android.view.animation.Animation.AnimationListener#onAnimationStart(android.view.animation.Animation)
	 */
	@Override
	public void onAnimationStart ( Animation animation ) {
		// Remove the touch listener (touch event are not handled during the animation)
		slider.setOnTouchListener ( null );
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
		// Retrieve the margin layout parameters of the slider
		ViewGroup.MarginLayoutParams sliderLayoutParams = (MarginLayoutParams) slider.getLayoutParams ();
		// Reset the left margin
		sliderLayoutParams.leftMargin = 0;
		// Assign the layout parameters to apply the new left margin value
		slider.setLayoutParams ( sliderLayoutParams );
		// Set the slider touch listener
		slider.setOnTouchListener ( this );
		
		// Declare and initialize an alpha animation in order to restore the slide view text alpha
		AlphaAnimation alpha = new AlphaAnimation ( label.getAlpha () , 1 );
		// Set the animation duration
		alpha.setDuration ( 300 );
		// Make the transformation performed by the animation persist when it is finished
		alpha.setFillAfter ( true );
		// Restore the slide view text alpha
		label.setAlpha ( 1f );
		label.startAnimation ( alpha );
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
		return new SavedState ( superState , lock , getText () , triggerOnRelease );
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
        lock = savedState.getLock ();
        setText ( savedState.getText () );
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
		 * @see SlideView#setText(String)
		 * @see SlideView#getText()
		 */
		private String text;
		
		/**
		 * @see SlideView#lock
		 */
		private boolean lock;
		
		/**
		 * @see SlideView#triggerOnRelease
		 */
		private boolean triggerOnRelease;

		/**
		 * Constructor
		 * 
		 * @param superState	Parcelable object holding the view's parent saved state. 
		 * @param lock	Boolean holding the lock state.
		 * @param text	String holding the slide view text.
		 */
		private SavedState ( Parcelable superState , boolean lock , String text , boolean triggerOnRelease ) {
			// Superclass method call
			super ( superState );
			// Initialize attributes
			this.lock = lock;
			this.text = text;
			this.triggerOnRelease = triggerOnRelease;
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
			lock = in.readByte () == 1;
			text = in.readString ();
			triggerOnRelease = in.readByte () == 1;
		}

		/**
		 * Getter - {@link #lock}
		 * 
		 * @return	Boolean holding the lock state.
		 * @see SlideView#lock
		 */
		public boolean getLock () {
			// Return the lock state
			return lock;
		}
		
		/**
		 * Getter - Slide View Text
		 * 
		 * @return	String holding the slide view text.
		 * @see SlideView#setText(String)
		 * @see SlideView#getText()
		 */
		public String getText () {
			// Return the slide view text
			return text;
		}
		
		/**
		 * Getter - {@link #triggerOnRelease}
		 * 
		 * @return	Boolean holding the trigger on release flag state.
		 * @see SlideView#triggerOnRelease
		 */
		public boolean getTriggerOnRelease () {
			// Return the flag state
			return triggerOnRelease;
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
			destination.writeByte ( (byte) ( lock ? 1 : 0 ) );
			destination.writeString ( text );
			destination.writeByte ( (byte) ( triggerOnRelease ? 1 : 0 ) );
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