/**
 * Copyright 2014 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.ClientsList.NewClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.AppToast;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.NewClientImages;
import me.SyncWise.Android.Database.NewClientImagesDao;
import me.SyncWise.Android.Database.NewClientImagesUtils;
import me.SyncWise.Android.Gson.BaseTimerActivity;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/**
 * TODO
 * 
 * @author Elias
 *
 */
public class PicturesActivity extends BaseTimerActivity {
	
	/**
	 * Bundle key used to put/retrieve the client code.
	 */
	public static final String CLIENT_CODE = PicturesActivity.class.getName () + ".CLIENT_CODE";
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #pictures}.
	 */
	public static final String PICTURES = PicturesActivity.class.getName() + ".PICTURES";
	
	/**
	 * List of strings hosting the path of every picture.
	 */
	private ArrayList < Picture > pictures;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #currentPictureIndex}.
	 */
	public static final String CURRENT_PICTURE_INDEX = PicturesActivity.class.getName() + ".CURRENT_PICTURE_INDEX";
	
	/**
	 * Integer hosting the currently displayed picture index.
	 */
	private Integer currentPictureIndex;
	
	/**
	 * Constant integer hosting the current button type.
	 */
	private final int TYPE_CURRENT = 0;
	
	/**
	 * Constant integer hosting the previous button type.
	 */
	private final int TYPE_PREVIOUS = 1;
	
	/**
	 * Constant integer hosting the next button type.
	 */
	private final int TYPE_NEXT = 2;
	
	/**
	 * A {android.graphics.Bitmap Bitmap} reference to the picture itself.
	 */
	private Bitmap bitmap;
	
	private ImageView imageView;
	
	/*
	 * Called when the activity is starting.
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Superclass method call
		super.onCreate ( savedInstanceState );
		// Set the activity content from a layout resource.
		setContentView ( R.layout.client_stock_picture_activity );
		// Change the title associated with this activity
		setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.pictures_title ) );
		// Check if the client code is valid
		if ( getIntent ().getSerializableExtra ( CLIENT_CODE ) == null ) {
			// Indicate that the activity cannot be displayed
			new AppToast ( this ).setIcon ( R.drawable.warning ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.invalid_selection_label ) ).show ();
			// Finish activity
			finish ();
			// Exit method
			return;
		} // End if
		// Determine if a saved instance state is provided
		if ( savedInstanceState != null ) {
			// Restore the content provided by the saved instance state
			currentPictureIndex = savedInstanceState.getInt ( CURRENT_PICTURE_INDEX );
			pictures = (ArrayList < Picture >) savedInstanceState.getSerializable ( PICTURES );
		} // End if
		else {
			// This is the activity's first run
			// Retrieve all the documents for the current visit
			List < NewClientImages > documents = DatabaseUtils.getInstance ( this ).getDaoSession ().getNewClientImagesDao ().queryBuilder ()
					.where ( NewClientImagesDao.Properties.ClientCode.eq ( getIntent ().getStringExtra ( CLIENT_CODE ) ) ).list ();
			// Initialize the pictures list
			pictures = new ArrayList < Picture > ();
			// Initialize the current picture index
			currentPictureIndex = 0;
			// Open the private documents folder
			File folder = getDir ( NewClientImagesUtils.FOLDER , MODE_PRIVATE );
			// Open the private temporary documents folder
			File tempFolder = getDir ( NewClientImagesUtils.FOLDER_TEMP , MODE_PRIVATE );
			// Iterate over all the documents
			for ( NewClientImages document : documents ) {
				// Get access to the document
				File image = new File ( folder , document.getClientCode () + "--" + document.getLineID () + ".png" );
				// Get access to the temporary document
				File tempImage = new File ( tempFolder , document.getClientCode () + "--" + document.getLineID () + ".png" );
				// Declare and initialize a picture reference to the current document
				Picture picture = new Picture ( document.getClientCode () , document.getLineID () , image.getAbsolutePath () , tempImage.getAbsolutePath () );
				// Add the picture the list
				pictures.add ( picture );
			} // End for
			// Check if the pictures list contains at least one picture
			if ( ! pictures.isEmpty () )
				// Set the current picture index to the last picture
				currentPictureIndex = pictures.size () - 1;
		} // End else
		
		imageView = (ImageView) findViewById(R.id.display);
		createImage();
		displayAccessiblityButtons ();
		displayPicturesNumber ();
	}
	
	/**
	 * Displays the pictures count, versus the currently displayed picture (if any) in the sub title.
	 */
	private void displayPicturesNumber () {
		// Check if the pictures list is valid or empty
		if ( pictures == null || pictures.isEmpty () )
			// No pictures
			getActionBar ().setSubtitle ( AppResources.getInstance ( this ).getString ( this , R.string.subtitle_no_pictures_label ) );
		// Otherwise check if the current picture index is valid
		else if ( currentPictureIndex == null || currentPictureIndex < 0 || currentPictureIndex > ( pictures.size () - 1 ) )
			// No pictures
			getActionBar ().setSubtitle ( AppResources.getInstance ( this ).getString ( this , R.string.subtitle_no_pictures_label ) );
		// Otherwise display the picture index versus count
		else
			// Display ratio
			getActionBar ().setSubtitle ( AppResources.getInstance ( this ).getString ( this , R.string.subtitle_pictures_label ) + " : " + ( currentPictureIndex + 1 ) + " / " + pictures.size () );
	}
	
    /*
     * Called to retrieve per-instance state from an activity before being killed so that the state can be restored in onCreate(Bundle) or onRestoreInstanceState(Bundle).
     *
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    protected void onSaveInstanceState ( Bundle outState ) {
    	// Superclass method call
    	super.onSaveInstanceState ( outState );
    	// Save the content of currentPictureIndex in the outState bundle
    	outState.putInt ( CURRENT_PICTURE_INDEX , currentPictureIndex );
    	// Save the content of pictures in the outState bundle
    	outState.putSerializable ( PICTURES , pictures );    	
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
			currentPictureIndex = null;
			pictures = null;
			if ( bitmap != null ) {
				bitmap.recycle ();
				bitmap = null;
			}
		} // End if
	}
	
	/**
	 * Called when a view has been clicked.<br>
	 * Displays the next picture, if any.
	 * 
	 * @param view	The clicked view.
	 */
	public void getNext ( View view ) {
		// Display the next picture
		displayPicture ( TYPE_NEXT );
		// Display the appropriate accessibility buttons
		displayAccessiblityButtons ();
		// Update the picture number
		displayPicturesNumber ();
	}
	
	/**
	 * Called when a view has been clicked.<br>
	 * Displays the next picture, if any.
	 * 
	 * @param view	The clicked view.
	 */
	public void getPrevious ( View view ) {
		// Display the previous picture
		displayPicture ( TYPE_PREVIOUS );
		// Display the appropriate accessibility buttons
		displayAccessiblityButtons ();
		// Update the picture number
		displayPicturesNumber ();
	}
	
	/**
	 * Displays or hides the accessibility buttons, based on the pictures displayed and their number.
	 */
	private void displayAccessiblityButtons () {
		// Retrieve a reference to the delete button
		View delete = findViewById ( R.id.delete_picture );
		// Retrieve a reference to the next button
		View next = findViewById ( R.id.next );
		// Retrieve a reference to the previous button
		View preivous = findViewById ( R.id.previous );
		// Retrieve a reference to the rotate icon
		View rotate = findViewById ( R.id.rotate_picture_icon );
		// Check if the currently displayed picture index, and the pictures list are valid
		if ( currentPictureIndex == null || pictures == null || pictures.isEmpty () 
				|| currentPictureIndex < 0 || currentPictureIndex > ( pictures.size () - 1 ) ) {
			// Hide all buttons
			delete.setVisibility ( View.INVISIBLE );
			next.setVisibility ( View.INVISIBLE );
			preivous.setVisibility ( View.INVISIBLE );
			rotate.setVisibility ( View.INVISIBLE );
			// Exit method
			return;
		} // End if
		// Otherwise a valid picture is displayed
		// Display all buttons
		delete.setVisibility ( View.VISIBLE );
		next.setVisibility ( View.VISIBLE );
		preivous.setVisibility ( View.VISIBLE );
		rotate.setVisibility ( View.VISIBLE );
		// Check if there is exactly one picture
		if ( pictures.size () == 1 ) {
			// Hide the next and previous buttons
			preivous.setVisibility ( View.INVISIBLE );
			next.setVisibility ( View.INVISIBLE );
		} // End if
		// Check if the first button is displayed
		else if ( currentPictureIndex == 0 )
			// Hide the previous button
			preivous.setVisibility ( View.INVISIBLE );
		// Otherwise check if the last button is displayed
		else if ( currentPictureIndex == ( pictures.size () - 1 ) )
			// Hide the next button
			next.setVisibility ( View.INVISIBLE );
	}
	
	/**
	 * Displays the default image on the screen.
	 */
	private void displayDefaultImage () {
		// Set the default image resource ID
		( (ImageView) findViewById ( R.id.display ) ).setImageResource ( R.drawable.boxes_1 );
	}
	
	/**
	 * Gets a temporary file used to store the newly taken picture.<br>
	 * The picture is stored in {@code /sdcard/me.SyncWise.Android.UFood/image.tmp}
	 * 
	 * @return	A {@link java.io.File File} object used to host the newly taken picture.
	 */
	private File getTempFile () {
		// Create the SD card path along with a folder entitled as the application package name
		final File path = new File ( Environment.getExternalStorageDirectory () , getPackageName () );
		// Check if the folder exists
		if ( ! path.exists () )
			// Create all the required folders
			path.mkdirs ();
		// Create and return the temporary file
		return new File ( path , "image.tmp" );
	}
	
	/**
	 * Called when a view has been clicked.<br>
	 * Deletes the currently displayed picture.
	 * 
	 * @param view	The clicked view.
	 */
	public void deletePicture ( View view ) {
		// Check if the currently displayed picture index, and the pictures list are valid
		if ( currentPictureIndex == null || pictures == null || pictures.isEmpty () 
				|| currentPictureIndex < 0 || currentPictureIndex > ( pictures.size () - 1 ) )
			// Do nothing
			return;
		// Otherwise attempt to delete the picture
		try {
			// Get the displayed picture
			Picture picture = pictures.get ( currentPictureIndex );
			// Get access to the picture
			File file = new File ( picture.getPath () );
			// Delete the file
			file.delete ();
			// Get access to the temporary picture
			file = new File ( picture.getTempPath () );
			// Delete the file
			file.delete ();
			// Retrieve a reference to the document
			NewClientImages document = DatabaseUtils.getInstance ( this ).getDaoSession ().getNewClientImagesDao ().queryBuilder ()
					.where ( NewClientImagesDao.Properties.ClientCode.eq ( getIntent ().getStringExtra ( CLIENT_CODE ) ) ,
							NewClientImagesDao.Properties.LineID.eq ( picture.getLineID () ) ).unique ();
			// Check if the document is valid
			if ( document != null )
				// Delete the appropriate document
				DatabaseUtils.getInstance ( this ).getDaoSession ().delete ( document );
			// Remove the picture from the list
			pictures.remove ( picture );
			// Check if there is at least one picture remaining
			if ( pictures.isEmpty () )
				// Clear the image view
				displayDefaultImage ();
			else
				// Otherwise display a new picture
				// If the deleted picture was the first, display the new first picture, otherwise display the previous one
				displayPicture ( currentPictureIndex == 0 ? TYPE_CURRENT : TYPE_PREVIOUS );
			// Display the appropriate accessibility buttons
			displayAccessiblityButtons ();
			// Update the picture number
			displayPicturesNumber ();
		} catch ( Exception exception ) {
			// An error occurred
			Log.e ( "Delete Picture" , exception.getMessage () );
		} // End of try-catch block
	}
	
	/**
	 * Called when a view has been clicked.<br>
	 * Launches a camera application to take a picture.
	 * 
	 * @param view	The clicked view.
	 */
	public void takePicture ( View view ) {
		// Create an intent to start a new activity
		final Intent intent = new Intent ( MediaStore.ACTION_IMAGE_CAPTURE );
		// Indicate the picture file location
		intent.putExtra ( MediaStore.EXTRA_OUTPUT , Uri.fromFile ( getTempFile () ) );
		// Start the new activity for a result
		startActivityForResult ( intent , 1 );
	}
	
	/**
	 * Displays the next or previous picture, depending on the provided type.
	 * 
	 * @param type	Integer holding the command type (either next, previous or current).
	 */
	private void displayPicture ( final int type ) {
		// Check if the currently displayed picture index, and the pictures list are valid
		if ( currentPictureIndex == null || pictures == null || pictures.isEmpty () )
			// Do nothing
			return;
		// Store a temporary value of the index
		int tempIndex = currentPictureIndex;
		// Determine the command type
		switch ( type ) {
		case TYPE_NEXT:
			// Increment the index
			tempIndex ++;
			break;
		case TYPE_PREVIOUS:
			// Decrement the index
			tempIndex --;
			break;
		case TYPE_CURRENT:
		default:
			break;
		} // End switch case
		// Validate the index
		if ( tempIndex < 0 || tempIndex > ( pictures.size () - 1 ) )
			// Do nothing
			return;
		// Otherwise the index is valid
		// Set the new value
		currentPictureIndex = tempIndex;
		// Display the appropriate image
		createImage ();
	}
	
	/**
	 * Creates the image of the currently selected picture and displays it in the image view.
	 */
	private void createImage () {
		// Check if the currently displayed picture index, and the pictures list are valid
		if ( currentPictureIndex == null || pictures == null || pictures.isEmpty () 
				|| currentPictureIndex < 0 || currentPictureIndex > ( pictures.size () - 1 ) )
			// Do nothing
			return;
		// Get access to the currently selected picture
		File picture = new File ( pictures.get ( currentPictureIndex ).getPath () );
		// Check if the picture exists
		if ( ! picture.exists () ) {
			// The picture is not existent
			// Display the default picture
			displayDefaultImage ();
			// Exit method
			return;
		} // End if
		
		// Check if the bitmap hosts a previous picture
		if ( bitmap != null ) {
			// Recycle the bitmap
			bitmap.recycle ();
			// Clear its reference
			bitmap = null;
		} // End if
		// Decode the picture
		bitmap = BitmapFactory.decodeFile ( pictures.get ( currentPictureIndex ).getPath () );
		// Display the bit map
		imageView.setImageBitmap ( bitmap );
	}
	
	/**
	 * Called when a view has been clicked.<br>
	 * Rotates the currently displayed picture to the right 90 degrees.
	 * 
	 * @param view	The clicked view.
	 */
	public void rotatePicture ( View view ) {
		// Check if the currently displayed picture index, and the pictures list are valid
		if ( currentPictureIndex == null || pictures == null || pictures.isEmpty () 
				|| currentPictureIndex < 0 || currentPictureIndex > ( pictures.size () - 1 ) )
			// Do nothing
			return;
		// Get access to the currently selected picture
		File picture = new File ( pictures.get ( currentPictureIndex ).getPath () );
		// Check if the picture exists
		if ( ! picture.exists () ) 
			// Exit method
			return;
		
		// Get the bit map width and height
		int bmpWidth = bitmap.getWidth ();
		int bmpHeight = bitmap.getHeight ();
		// Declare and initialize a matrix used to rotate the image
		Matrix matrix = new Matrix ();
		// Define the rotation as 90 degrees to the right
		matrix.postRotate ( 90 );
		// Create the modified bit map
		Bitmap resizedBitmap = Bitmap.createBitmap ( bitmap , 0 , 0 , bmpWidth , bmpHeight , matrix , true );
		// Check if the bit map is valid
		if ( bitmap != null ) {
			bitmap.recycle ();
			bitmap = null;
			System.gc ();
		} // End if
		// Switch bit maps
		bitmap = resizedBitmap;
		resizedBitmap = null;
		// Open the image file
		File image = new File ( pictures.get ( currentPictureIndex ).getPath () );
		File tempImage = new File ( pictures.get ( currentPictureIndex ).getTempPath () );
		try {
			// Overwrite the image file with the rotated on the main path
			OutputStream fOut = null;
			fOut = new FileOutputStream ( image );
			bitmap.compress ( Bitmap.CompressFormat.PNG , 85 , fOut );
			fOut.flush ();
			fOut.close ();
			// Overwrite the image file with the rotated on the main path
			fOut = null;
			fOut = new FileOutputStream ( tempImage );
			bitmap.compress ( Bitmap.CompressFormat.PNG , 85 , fOut );
			fOut.flush ();
			fOut.close ();
		} catch ( FileNotFoundException exception ) {
			exception.printStackTrace ();
		} catch ( IOException exception ) {
			exception.printStackTrace ();
		} // End of try-catch block
		// Refresh the picture
		displayPicture ( TYPE_CURRENT );
	}

	public Bitmap getResizedBitmap ( Bitmap bm , int newHeight , int newWidth ) {
		int width = bm.getWidth ();
		int height = bm.getHeight ();
		float scaleWidth = ( (float) newWidth ) / width;
		float scaleHeight = ( (float) newHeight ) / height;
		// create a matrix for the manipulation
		Matrix matrix = new Matrix ();
		// resize the bit map
		matrix.postScale ( scaleWidth , scaleHeight );
		// recreate the new Bitmap
		Bitmap resizedBitmap = Bitmap.createBitmap ( bm , 0 , 0 , width , height , matrix , false );
		return resizedBitmap;
	}

	/*
	 * Called when an activity you launched exits.
	 *
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult ( int requestCode , int resultCode , Intent data ) {
		if ( resultCode == RESULT_OK ) {
			Calendar now = Calendar.getInstance ();
			int maxID = 0;
			String clientCode = getIntent ().getStringExtra ( CLIENT_CODE );
			List < NewClientImages > clientPictures = DatabaseUtils.getInstance ( this ).getDaoSession ().getNewClientImagesDao ().queryBuilder ()
					.where ( NewClientImagesDao.Properties.ClientCode.eq ( clientCode ) )
					.orderDesc ( NewClientImagesDao.Properties.LineID ).limit ( 1 ).list ();
			if ( ! clientPictures.isEmpty () )
				maxID = clientPictures.get ( 0 ).getLineID () + 1;
			String filename = clientCode + "--" + maxID;
			NewClientImages document = new NewClientImages ( null , // ID
					clientCode , // ClientCode
					DatabaseUtils.getCurrentCompanyCode ( this ) , // CompanyCode
					DatabaseUtils.getCurrentDivisionCode ( this ) , // DivisionCode
					maxID , // LineID
					now.getTime () ); // StampDate
			// Open the private documents folder
			File folder = getDir ( NewClientImagesUtils.FOLDER , MODE_PRIVATE );
			File tempFolder = getDir ( NewClientImagesUtils.FOLDER_TEMP , MODE_PRIVATE );
			File image = new File ( folder , filename + ".png" );
			File tempImage = new File ( tempFolder , filename + ".png" );
			final File file = getTempFile ();
			Bitmap captureBmp;
			try {
				captureBmp = Media.getBitmap ( getContentResolver () , Uri.fromFile ( file ) );
				// do whatever you want with the bitmap (Resize, Rename, Add
				// To Gallery, etc)
				captureBmp = getResizedBitmap ( captureBmp , 500 , 700 );
				OutputStream fOut = null;
				fOut = new FileOutputStream ( image );
				captureBmp.compress ( Bitmap.CompressFormat.PNG , 85 , fOut );
				fOut.flush ();
				fOut.close ();
			} catch ( FileNotFoundException e ) {
				e.printStackTrace ();
			} catch ( IOException e ) {
				e.printStackTrace ();
			}
			
			try {
                FileChannel src = new FileInputStream(image).getChannel();
                FileChannel dst = new FileOutputStream(tempImage).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
			} catch ( Exception exception ) {
			}
			
			DatabaseUtils.getInstance ( this ).getDaoSession ().insert ( document );
			Picture picture = new Picture ( document.getClientCode () , document.getLineID () , image.getAbsolutePath () , tempImage.getAbsolutePath () );
			pictures.add ( picture );
			currentPictureIndex = pictures.size () - 1;
			createImage ();
			displayAccessiblityButtons ();
			// Update the picture number
			displayPicturesNumber ();
		}
	}

}