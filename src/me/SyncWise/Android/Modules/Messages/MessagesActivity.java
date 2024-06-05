package me.SyncWise.Android.Modules.Messages;

import java.util.Calendar;

import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Vibration;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.IsProcessedUtils;
import me.SyncWise.Android.Database.MsgNote;
import me.SyncWise.Android.Database.VisitsUtils;
import me.SyncWise.Android.Modules.Journey.Call;
import me.SyncWise.Android.Widgets.Baguette;
import android.app.Activity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Menu;
import android.widget.EditText;

public class MessagesActivity extends Activity {
	
	/**
	 * Bundle key used to put/retrieve the content of the current call.
	 */
	public static final String CALL = MessagesActivity.class.getName () + ".CALL";
	
	@Override
	public boolean onCreateOptionsMenu ( Menu menu ) {
		getMenuInflater ().inflate ( R.menu.action_bar , menu );
    	// Enable the required menu items
    	me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_save ) , R.string.save_label );
    	// Display the menu
    	return true;
	}
	
    @Override
    public boolean onOptionsItemSelected ( android.view.MenuItem menuItem ) {
    	if ( menuItem.getItemId () == R.id.action_save ) {
    		if ( validate () ) {
    			Calendar now = Calendar.getInstance ();
    			Call call = (Call) getIntent ().getSerializableExtra ( CALL );
    			Long id = VisitsUtils.getVisitID ( now );
    			String userCode = DatabaseUtils.getCurrentUserCode ( MessagesActivity.this );
    			String companyCode = DatabaseUtils.getCurrentCompanyCode ( MessagesActivity.this );
    			String divisionCode = DatabaseUtils.getCurrentDivisionCode ( MessagesActivity.this );
    			try {
    				DatabaseUtils.getInstance ( MessagesActivity.this ).getDaoSession ().getMsgNoteDao ().insert ( new MsgNote ( null , // ID
    						String.valueOf ( id ) , // MsgNoteCode
    						userCode , // UserCode
    						call != null ? call.getClientDivision ().getDivisionCode () : divisionCode , // DivisionCode
    						call != null ? call.getClientDivision ().getClientCode () : null , // ClientCode
    						call != null ? call.getClientDivision ().getCompanyCode () : companyCode , // CompanyCode
    						now.getTime () , // NoteDate
    						( (EditText) findViewById ( R.id.message_description ) ).getText ().toString ().trim () , // MsgBody
    						IsProcessedUtils.isNotSync () , // IsProcessed
    						now.getTime () ) ); // StampDate
	    			Vibration.vibrate ( MessagesActivity.this );
    	        	// Call this to set the result that your activity will return to its caller
    	        	setResult ( RESULT_OK );
    				finish ();
    			} catch ( Exception e ) {
    				Baguette.showText(MessagesActivity.this, "Message(s) not Saved.", Baguette.BackgroundColor.RED);
				}
    		} else
    			Baguette.showText ( MessagesActivity.this , getString ( R.string.messages_activity_invalid_message ) , Baguette.BackgroundColor.RED );
    	}
    	return false;
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView ( R.layout.messages_activity );
    	setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.messages_title ) );
		// Set the maximum number of allowed characters
    	( (EditText) findViewById ( R.id.message_description ) ).setFilters ( new InputFilter [] { new InputFilter.LengthFilter ( 100 ) } );
	}

	private boolean validate () {
		return ! ( (EditText) findViewById ( R.id.message_description ) ).getText ().toString ().trim ().isEmpty ();
	}

}