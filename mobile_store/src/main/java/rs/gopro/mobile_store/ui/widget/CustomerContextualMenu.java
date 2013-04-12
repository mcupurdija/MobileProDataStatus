package rs.gopro.mobile_store.ui.widget;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.util.DateUtils;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.CustomerAddressesSyncObject;
import rs.gopro.mobile_store.ws.model.GetContactsSyncObject;
import rs.gopro.mobile_store.ws.model.SetPotentialCustomersSyncObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class CustomerContextualMenu implements ActionMode.Callback {
	
	private static final int CREATE_CUSTOMER_FROM_POTENTIAL = 0;
	String customerId;
	Activity activity;
	ProgressDialog syncProgressDialog;

	public CustomerContextualMenu(Activity activity, String customerId, ProgressDialog syncProgressDialog) {
		this.customerId = customerId;
		this.activity = activity;
		this.syncProgressDialog = syncProgressDialog;
	}
	
	public CustomerContextualMenu() {
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.sync_potential_customer:
			if (isPotentialCustomer(Integer.valueOf(customerId))) {
				sendPotentialCustomer(Integer.valueOf(customerId));
			}
			mode.finish();
			return true;
		case R.id.sync_customer_address:
			//syncProgressDialog = ProgressDialog.show(activity, activity.getResources().getString(R.string.dialog_title_item_price_qty_load), activity.getResources().getString(R.string.dialog_body_item_price_qty_load), true, true);
			doSyncAddresses();
			mode.finish();
			return true;
		case R.id.sync_customer_contacts:
			//syncProgressDialog = ProgressDialog.show(activity, activity.getResources().getString(R.string.dialog_title_item_price_qty_load), activity.getResources().getString(R.string.dialog_body_item_price_qty_load), true, true);
			Cursor cursor = activity.getContentResolver().query(MobileStoreContract.Customers.buildCustomersUri(customerId), new String[] { Customers._ID, Customers.CONTACT_COMPANY_NO }, null, null, null);
			if (cursor.moveToFirst()) {
				String potentialCustomerNo = cursor.getString(1);
				Intent syncAddressIntent = new Intent(activity, NavisionSyncService.class);
				GetContactsSyncObject contactsSyncObject = new GetContactsSyncObject("", "", potentialCustomerNo, "", DateUtils.getWsDummyDate());
				syncAddressIntent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, contactsSyncObject);
				activity.startService(syncAddressIntent);
			}
			cursor.close();
			mode.finish();
			return true;
		default:
			break;
		}
		return false;
	}

	private boolean isPotentialCustomer(int customerId) {
		Cursor potentialCustomerCursor = activity.getContentResolver().query(MobileStoreContract.Customers.CONTENT_URI, new String[] {Customers.CONTACT_COMPANY_NO}, 
				"("+Customers.CONTACT_COMPANY_NO + " is null or " + Customers.CONTACT_COMPANY_NO + "='')" + " and " + Customers._ID + "=?" , new String[] { String.valueOf(customerId) }, null);
		
		if (potentialCustomerCursor.moveToFirst()) {
			return true;
		}
		potentialCustomerCursor.close();
		return false;
	}
	
	private void sendPotentialCustomer(int customerId) {    	
    	SetPotentialCustomersSyncObject potentialCustomersSyncObject = new SetPotentialCustomersSyncObject(customerId);
    	// it will not send signal to create customer
    	potentialCustomersSyncObject.setpPendingCustomerCreation(Integer.valueOf(CREATE_CUSTOMER_FROM_POTENTIAL));
    	Intent intent = new Intent(activity, NavisionSyncService.class);
		intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, potentialCustomersSyncObject);
		activity.startService(intent);	
	}
	
	private void doSyncAddresses() {
		Cursor cursor = activity.getContentResolver().query(MobileStoreContract.Customers.buildCustomersUri(customerId), new String[] { Customers._ID, Customers.CUSTOMER_NO }, null, null, null);
		if (cursor.moveToFirst()) {
			String customerNo = cursor.getString(1);
			Intent syncAddressIntent = new Intent(activity, NavisionSyncService.class);
			CustomerAddressesSyncObject addressesSyncObject = new CustomerAddressesSyncObject("", customerNo, "", DateUtils.getWsDummyDate());
			syncAddressIntent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, addressesSyncObject);
			activity.startService(syncAddressIntent);
		}
		cursor.close();
	}
	
	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		 // Inflate a menu resource providing context menu items
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.customer_long_click, menu);
        return true;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		return false;
	}

}
