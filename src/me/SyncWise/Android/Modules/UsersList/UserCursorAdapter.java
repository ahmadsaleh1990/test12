package me.SyncWise.Android.Modules.UsersList;

import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;

import me.SyncWise.Android.Database.UsersDao;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class UserCursorAdapter extends CursorAdapter implements SectionIndexer {
	/**
	 * {@link android.view.LayoutInflater LayoutInflater} used to instantiate a layout XML file into its corresponding View objects.
	 */
	private final LayoutInflater layoutInflater;
	
	/**
	 * XML Layout (layout) resource id.
	 */
	private final int layout;
	
	/**
	 * Reference to AlphabetIndexter which provides a way to do fast indexing of large lists.
	 */
	private AlphabetIndexer alphabetIndexer;
	
	/**
	 * Nested class implementation of the view holder pattern used to keep references to children views in order to avoid unnecessary calls to {@link android.view.View#findViewById(int) findViewById(id)} on each row.
	 * 
	 * @author Ahmad
	 *
	 */
	public static class ViewHolder {
    	public String userName;
		public String userCode;
		public ImageView icon;
		
		public TextView _userName;
		public TextView _userCode;
		public TextView useraltname;
	}
	
	/**
	 * String used to hold the code label.
	 */
	protected final String codeLabel;
	
	
	/**
	 * String used to hold the item code label.
	 */
	private final String userCodeLabel;
	
	/**
	 * Constructor
	 * 
	 * @param context	The application context.
	 * @param cursor	Cursor holding a result set returned by a database query.
	 * @param layout	Integer holding the item layout resource id.
	 */
	public UserCursorAdapter ( Context context , Cursor cursor , int layout ) {
		// Superclass method call
		super ( context , cursor , false );
		// Initialize the alphabet indexer
		alphabetIndexer = new AlphabetIndexer ( cursor , cursor.getColumnIndex ( UsersDao.Properties.UserName.columnName ) , " 0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ" );
		// Initialize the attributes
		layoutInflater = LayoutInflater.from ( context );
		this.layout = layout;
		codeLabel = AppResources.getInstance ( context ).getString ( context , R.string.code_label );
		userCodeLabel = AppResources.getInstance ( context ).getString ( context , R.string.users_NANE_activity );
	}
	
	/*
	 * Makes a new view to hold the data pointed to by cursor.
	 *
	 * @see android.widget.CursorAdapter#newView(android.content.Context, android.database.Cursor, android.view.ViewGroup)
	 */
	@Override
	public View newView ( Context context , Cursor cursor , ViewGroup parent ) {
		// Inflate and return a new view hierarchy from the specified xml resource
		return layoutInflater.inflate ( layout , null );
	}
	
	/*
	 * Makes a new view to hold the data pointed to by cursor.
	 *
	 * @see android.widget.CursorAdapter#bindView(android.view.View, android.content.Context, android.database.Cursor)
	 */
	@Override
	public void bindView ( View view , Context context , Cursor cursor ) {
		// Declare a view holder
		ViewHolder viewHolder;
		// Check if a recycled view is provided
		if ( view.getTag () == null ) {
			// Initialize the view holder
			viewHolder = new ViewHolder ();
			// Retrieve a reference to the brochure Id
			viewHolder._userCode = (TextView) view.findViewById ( R.id.label_user_usercode );
			// Retrieve a reference to the brochure Name 
			viewHolder._userName = (TextView) view.findViewById ( R.id.label_user_name);
			// Retrieve a reference to the item Code
			viewHolder.useraltname = (TextView) view.findViewById ( R.id.label_user_useraltname);
			// Store the view holder as tag
			view.setTag ( viewHolder );
		} // End if
		else
			// An recycled view is already provided, retrieve the stored view holder
			viewHolder = (ViewHolder) view.getTag ();
		
		// Store the brochure ID
		viewHolder.userCode = cursor.getString ( cursor.getColumnIndex ( UsersDao.Properties.UserCode.columnName ) );
		// Build a spannable string out of the client code in order to apply various spans
		SpannableString userCode = new SpannableString ( codeLabel + " : " + viewHolder.userCode );
		// Apply style span
		userCode.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , codeLabel.length () , 0 );
		// Apply color span
		userCode.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , codeLabel.length () , 0 );
		// Display the brochure ID
		viewHolder._userCode.setText ( userCode , BufferType.SPANNABLE );
		// Display the brochure name
		
		// Store the itemCode
		viewHolder.userName = cursor.getString ( cursor.getColumnIndex ( UsersDao.Properties.UserName.columnName ) );
		// Build a spannable string out of the client code in order to apply various spans
		SpannableString itemCode = new SpannableString ( userCodeLabel + " : " + viewHolder.userName);
		// Apply style span
		itemCode.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , userCodeLabel.length () , 0 );
		// Apply color span
		itemCode.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , userCodeLabel.length () , 0 );
		// Display theitemCode
		viewHolder._userName.setText ( TextUtils.isEmpty ( viewHolder.userName ) ? "" : itemCode , BufferType.SPANNABLE );
		// Display the brochure name
		viewHolder.useraltname.setText ( cursor.getString ( cursor.getColumnIndex ( UsersDao.Properties.UserAltName.columnName ) ) );
	}
	
	/*
	 * Swap in a new Cursor, returning the old Cursor.
	 *
	 * @see android.widget.CursorAdapter#swapCursor(android.database.Cursor)
	 */
	@Override
	public Cursor swapCursor ( Cursor newCursor ) {
		// Assign the new cursor for the alphabet indexer
		alphabetIndexer.setCursor ( newCursor );
		// Superclass method call
		return super.swapCursor ( newCursor );
	}
	
	/*
	 * Provides the starting index in the list for a given section.
	 *
	 * @see android.widget.SectionIndexer#getPositionForSection(int)
	 */
	@Override
	public int getPositionForSection ( int section ) {
		// Perform a binary search or cache lookup to find the first row that matches a given section's starting letter
		return alphabetIndexer.getPositionForSection ( section );
	}

	/*
	 * This is a reverse mapping to fetch the section index for a given position in the list.
	 *
	 * @see android.widget.SectionIndexer#getSectionForPosition(int)
	 */
	@Override
	public int getSectionForPosition ( int position ) {
		// Returns the section index for a given position in the list by querying the item and comparing it with all items in the section array
		return alphabetIndexer.getSectionForPosition ( position );
	}

	/*
	 * This provides the list view with an array of section objects.
	 *
	 * @see android.widget.SectionIndexer#getSections()
	 */
	@Override
	public Object [] getSections () {
		// Returns the section array constructed from the alphabet provided in the constructor.
		return alphabetIndexer.getSections ();
	}
	
}