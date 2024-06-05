/**
 * Copyright 2015 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Movements;

import android.os.Bundle;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R; 
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.MovementHeadersUtils;
import me.SyncWise.Android.Database.PermissionsUtils;
import me.SyncWise.Android.Modules.MenuList.MenuItem;
import me.SyncWise.Android.Modules.Movements.ApprovedMovements.ApprovedMovementActivity;
import me.SyncWise.Android.Modules.VehiclesStock.VehiclesStockListActivity;
import me.SyncWise.Android.Widgets.Baguette;

/**
 * Activity implemented to display the movement modules.
 * 
 * @author Elias -- Ahmad
 * @sw.todo	<b>Movement Activity Implementation :</b><br>
 * <ul>
 * <li>Extend this class.</li>
 * <li>Implement the {@link #populateCallMenu()} method, which should define the menu items using the {@link #addMenuItem(MenuItem)} method.</li>
 * <li>Do not forget to add your new class in the {@code AndroidManifest.xml} file.</li>
 * </ul>
 *
 */
public class MovementListActivity extends me.SyncWise.Android.Modules.MenuList.MenuListActivity {
	
	/*
	 * Called when the activity is starting.
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate ( Bundle savedInstanceState ) {
		
		// Populate home screen menu
		populateCallMenu ();
		
		// Set the activity content from a layout resource.
		setContentView ( R.layout.home_screen_activity );
		// Superclass method call	
		super.onCreate ( savedInstanceState );
		// Change the title associated with this activity
		setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.movement_activity_title ) );
	}
	
	/**
	 * Populate the call menu.
	 */
	protected void populateCallMenu () {
		String userCode = DatabaseUtils.getCurrentUserCode ( this );
		String companyCode = DatabaseUtils.getCurrentCompanyCode ( this );
		addMenuItem ( new MenuItem ()
			.setTitleId ( R.string.vehicles_stock_title )
			.setDescriptionId ( R.string.vehicles_stock_description )
			.setIconId ( R.drawable.stock_transportation )
			.setActivity ( VehiclesStockListActivity.class ) );
		MovementSettings loadRequestSettings = new MovementSettings ( MovementHeadersUtils.Type.LOAD_REQUEST );
		loadRequestSettings.setEditable ( true );
		loadRequestSettings.setDisplaySuggestions ( true );
		addMenuItem ( new MenuItem ()
			.setTitleId ( R.string.load_request_title )
			.setDescriptionId ( R.string.load_request_description )
			.setIconId ( R.drawable.boxes_6 )
			.setActivity ( MovementActivity.class )
			.putData ( MovementActivity.MOVEMENT_SETTINGS , loadRequestSettings ) );
		
		MovementSettings unloadRequestSettings = new MovementSettings ( MovementHeadersUtils.Type.UNLOAD_REQUEST );
		unloadRequestSettings.setEditable ( true );
		unloadRequestSettings.setUseExpiryDate ( true );
		unloadRequestSettings.setUseReason ( true );
		unloadRequestSettings.setEnforceExpiryDate ( PermissionsUtils.getEnforceUnloadRequestExpiryDate ( this , userCode , companyCode ) );
		unloadRequestSettings.setEnforceReason ( PermissionsUtils.getEnforceUnloadRequestReason ( this , userCode , companyCode ) );
		addMenuItem ( new MenuItem ()
			.setTitleId ( R.string.unload_request_title )
			.setDescriptionId ( R.string.unload_request_description )
			.setIconId ( R.drawable.boxes_5 )
			.setActivity ( MovementActivity.class )
			.putData ( MovementActivity.MOVEMENT_SETTINGS , unloadRequestSettings ) );
		
		if ( PermissionsUtils.getEnablePhysicalDirectLoad ( MovementListActivity.this ,   DatabaseUtils.getCurrentUserCode ( MovementListActivity.this ) ,  DatabaseUtils.getCurrentCompanyCode(MovementListActivity.this) ) ) {
			
			MovementSettings physicalDirectLoadSettings = new MovementSettings ( MovementHeadersUtils.Type.Physical_Direct_Load );
			physicalDirectLoadSettings.setEditable ( true );
			physicalDirectLoadSettings.setDisplaySuggestions ( true ); 
			addMenuItem ( new MenuItem ()
				.setTitleId ( R.string.physical_direct_load_title )
				.setDescriptionId ( R.string.physical_direct_load_description )
				.setIconId ( R.drawable.boxes_7 )
				.setActivity ( MovementActivity.class )
				.putData ( MovementActivity.MOVEMENT_SETTINGS , physicalDirectLoadSettings ) );
			}
			if ( PermissionsUtils.getEnablePhysicalDirectUnload ( MovementListActivity.this ,   DatabaseUtils.getCurrentUserCode ( MovementListActivity.this ) ,  DatabaseUtils.getCurrentCompanyCode(MovementListActivity.this) ) ) {
					
			MovementSettings unloadPhysicalDirectLoadSettings = new MovementSettings ( MovementHeadersUtils.Type.PHYSICAL_DIRECT_UNLOAD );
			unloadPhysicalDirectLoadSettings.setEditable ( true );
			unloadPhysicalDirectLoadSettings.setDisplaySuggestions ( true ); 
			unloadPhysicalDirectLoadSettings.setUseExpiryDate ( true );
			unloadPhysicalDirectLoadSettings.setUseReason ( true );
			unloadPhysicalDirectLoadSettings.setEnforceExpiryDate ( PermissionsUtils.getEnforceUnloadRequestExpiryDate ( this , userCode , companyCode ) );
			unloadPhysicalDirectLoadSettings.setEnforceReason ( PermissionsUtils.getEnforceUnloadRequestReason ( this , userCode , companyCode ) );
		 	addMenuItem ( new MenuItem ()
				.setTitleId ( R.string.physical_direct_unload_title )
				.setDescriptionId ( R.string.physical_direct_unload_description )
				.setIconId ( R.drawable.boxes_6 )
				.setActivity ( MovementActivity.class )
				.putData ( MovementActivity.MOVEMENT_SETTINGS , unloadPhysicalDirectLoadSettings ) );
			
			}
			
		MovementSettings loadSettings = new MovementSettings ( MovementHeadersUtils.Type.LOAD );
		loadSettings.setDisplaySuggestions ( true );
		loadSettings.setDisplaySuggestionedExpiryDate ( true );
		loadSettings.setDisplaySuggestionedReason ( true );
		loadSettings.setColorCodedSuggestions ( true );
		loadSettings.setUseExpiryDate ( true );
		loadSettings.setUseReason ( true );
		loadSettings.setEnforceSuggestionLimit ( true );
		loadSettings.setEnforceExpiryDate ( PermissionsUtils.getEnforceLoadExpiryDate ( this , userCode , companyCode ) );
		loadSettings.setEnforceReason ( PermissionsUtils.getEnforceLoadReason ( this , userCode , companyCode ) );
		addMenuItem ( new MenuItem ()
			.setTitleId ( R.string.load_title )
			.setDescriptionId ( R.string.load_description )
			.setIconId ( R.drawable.boxes_4 )
			.setActivity ( ApprovedMovementActivity.class )
			.putData ( MovementActivity.MOVEMENT_SETTINGS , loadSettings ) );
		MovementSettings unloadSettings = new MovementSettings ( MovementHeadersUtils.Type.UNLOAD );
		unloadSettings.setDisplaySuggestions ( true );
		unloadSettings.setDisplaySuggestionedExpiryDate ( true );
		unloadSettings.setDisplaySuggestionedReason ( true );
		unloadSettings.setColorCodedSuggestions ( true );
		unloadSettings.setUseExpiryDate ( true );
		unloadSettings.setUseReason ( true );
		unloadSettings.setEnforceSuggestionLimit ( true );
		unloadSettings.setEnforceExpiryDate ( PermissionsUtils.getEnforceUnloadExpiryDate ( this , userCode , companyCode ) );
		unloadSettings.setEnforceReason ( PermissionsUtils.getEnforceUnloadReason ( this , userCode , companyCode ) );
		addMenuItem ( new MenuItem ()
			.setTitleId ( R.string.unload_title )
			.setDescriptionId ( R.string.unload_description )
			.setIconId ( R.drawable.boxes_3 )
			.setActivity ( ApprovedMovementActivity.class )
			.putData ( MovementActivity.MOVEMENT_SETTINGS , unloadSettings ) );
		MovementSettings reconciliationSettings = new MovementSettings ( MovementHeadersUtils.Type.STOCK_RECONCILIATION );
		reconciliationSettings.setUseExpiryDate ( true );
		reconciliationSettings.setEnforceExpiryDate ( PermissionsUtils.getEnforceStockReconciliationExpiryDate ( this , userCode , companyCode ) );
		reconciliationSettings.setDeletable ( false );
		addMenuItem ( new MenuItem ()
			.setTitleId ( R.string.stock_reconciliation_title )
			.setDescriptionId ( R.string.stock_reconciliation_description )
			.setIconId ( R.drawable.vehicle )
			.setActivity ( MovementActivity.class )
			.putData ( MovementActivity.MOVEMENT_SETTINGS , reconciliationSettings ) );
		// FAKE
		MovementSettings stockManagementSettings = new MovementSettings ( MovementHeadersUtils.Type.STOCK_MANAGEMENT );
		stockManagementSettings.setDeletable ( false );
		stockManagementSettings.setUseExpiryDate ( true );
		stockManagementSettings.setUseReason ( true );
		stockManagementSettings.setEnforceReason ( true );
		stockManagementSettings.setEnforceSuggestionLimit ( true );
		stockManagementSettings.setDisplaySuggestions ( true );
		addMenuItem ( new MenuItem ()
			.setTitleId ( R.string.stock_management_title )
			.setDescriptionId ( R.string.stock_management_description )
			.setIconId ( R.drawable.vehicle_2 )
			.setActivity ( MovementActivity.class )
			.putData ( MovementActivity.MOVEMENT_SETTINGS , stockManagementSettings ) );
		MovementSettings vanTransferSettings = new MovementSettings ( MovementHeadersUtils.Type.VAN_TRANSFER );
		vanTransferSettings.setDeletable ( false );
		vanTransferSettings.setUseExpiryDate ( true );
		vanTransferSettings.setUseReason ( true );
		addMenuItem ( new MenuItem ()
			.setTitleId ( R.string.van_transfer_title )
			.setDescriptionId ( R.string.van_transfer_description )
			.setIconId ( R.drawable.vehicle_3 )
			.setActivity ( MovementActivity.class )
			.putData ( MovementActivity.MOVEMENT_SETTINGS , vanTransferSettings ) );
		MovementSettings vanStockCountSettings = new MovementSettings ( MovementHeadersUtils.Type.VAN_STOCK_COUNT );
		vanStockCountSettings.setDeletable ( false );
		vanStockCountSettings.setUseExpiryDate ( true );
		vanStockCountSettings.setUseReason ( true );
		addMenuItem ( new MenuItem ()
			.setTitleId ( R.string.van_stock_count_title )
			.setDescriptionId ( R.string.van_stock_count_description )
			.setIconId ( R.drawable.boxes_9 )
			.setActivity ( MovementActivity.class )
			.putData ( MovementActivity.MOVEMENT_SETTINGS , vanStockCountSettings ) );
	}
	
	/*
	 * Called as part of the activity lifecycle when an activity is going into the background, but has not (yet) been killed.
	 *
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause () {
		// Superclass method call
		super.onPause ();
		// Remove any displayed baguette
		Baguette.remove ( this );
	}
	
}