package me.SyncWise.Android.Modules.Summary;

import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.VisitsDao;
 
import android.content.Context;
import android.database.Cursor;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class VisitNoteAdapter extends CursorAdapter{

	/**
	 * {@link android.view.LayoutInflater LayoutInflater} used to instantiate a layout XML file into its corresponding View objects.
	 */
	private final LayoutInflater layoutInflater;
	
	/**
	 * XML Layout (layout) resource id.
	 */
	private final int layout;
	/**
	 * String used to host the new line character.
	 */
	private final String newLine = "\n";
	/**
	 * Nested class implementation of the view holder pattern used to keep references to children views in order to avoid unnecessary calls to {@link android.view.View#findViewById(int) findViewById(id)} on each row.
	 * 
	 * @author Ahmad
	 *
	 */
	public static class ViewHolder {
		public long visitID;
		public TextView	contactsCount;
	}
	
	/**
	 * String used to hold the blank visit contacts count label.
	 */
//	private final String blankVisitContactsCountLabel;
	/**
	 * String used to hold the blank visit contacts count label.
	 */
	private final String noteLabel;
	/**
	 * Integer used to hold the brown color value.
	 */
	private final int brownColor;
	
	/**
	 * Constructor
	 * 
	 * @param context	The application context.
	 * @param cursor	Cursor holding a result set returned by a database query.
	 * @param layout	Integer holding the item layout resource id.
	 */
	public VisitNoteAdapter ( Context context , Cursor cursor , int layout ) {
		// Superclass method call
		super ( context , cursor , false );
		// Initialize the attributes
		layoutInflater = LayoutInflater.from ( context );
		this.layout = layout;
	//	blankVisitContactsCountLabel = AppResources.getInstance ( context ).getString ( context , R.string.visit_note_label );
		brownColor = context.getResources ().getColor ( R.color.Brown );
		noteLabel=AppResources.getInstance ( context ).getString ( context , R.string.note_visit_contacts_label );
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
			// Retrieve a reference to the blank visit contacts count label
			viewHolder.contactsCount = (TextView) view.findViewById ( R.id.label_blank_visit_contacts_count );
			// Store the view holder as tag
			view.setTag ( viewHolder );
		} // End if
		else
			// An recycled view is already provided, retrieve the stored view holder
			viewHolder = (ViewHolder) view.getTag ();
	
		// Store the visit ID
		viewHolder.visitID = cursor.getLong ( cursor.getColumnIndex ( "_id" ) );
	
//		// Compute the blank visit contacts count label
 	//	String label = blankVisitContactsCountLabel ;
//		// Retrieve the blank visit contacts count
 		String note = cursor.getString ( cursor.getColumnIndex (VisitsDao.Properties.Note.columnName) );
 //		String reasonName=cursor.getString(cursor.getColumnIndex("reasonName"));
//		// Build a spannable string out of the summary description in order to apply various spans
 	//	SpannableString contactsCount = new SpannableString ( label + reasonName + newLine );
//		// Apply foreground color span
 //		contactsCount.setSpan ( new ForegroundColorSpan ( brownColor ) , label.length () , contactsCount.length () , 0 );
 		SpannableString contactsCount1 = new SpannableString ( noteLabel+note + newLine );
//		// Apply foreground color span
 		contactsCount1.setSpan ( new ForegroundColorSpan ( brownColor ) , noteLabel.length () , contactsCount1.length () , 0 );
 	
 		viewHolder.contactsCount.setText (TextUtils.concat ( contactsCount1) , BufferType.SPANNABLE );
	}
	
}
