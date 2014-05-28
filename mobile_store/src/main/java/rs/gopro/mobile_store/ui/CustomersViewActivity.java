package rs.gopro.mobile_store.ui;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.CustomerTradeAgreemnt;
import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.provider.MobileStoreContract.ElectronicCardCustomer;
import rs.gopro.mobile_store.ui.customlayout.ShowHideMasterLayout;
import rs.gopro.mobile_store.ui.widget.CustomerContextualMenu;
import rs.gopro.mobile_store.util.ApplicationConstants.SyncStatus;
import rs.gopro.mobile_store.util.DateUtils;
import rs.gopro.mobile_store.util.DialogUtil;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.CustomerAddressesSyncObject;
import rs.gopro.mobile_store.ws.model.CustomerSyncObject;
import rs.gopro.mobile_store.ws.model.GetContactsSyncObject;
import rs.gopro.mobile_store.ws.model.GetPotentialCustomerSyncObject;
import rs.gopro.mobile_store.ws.model.SetPotentialCustomersSyncObject;
import rs.gopro.mobile_store.ws.model.SyncResult;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class CustomersViewActivity extends BaseActivity implements CustomersViewListFragment.Callbacks, CustomersViewDetailFragment.Callbacks {

	public static final String EXTRA_MASTER_URI = "rs.gopro.mobile_store.extra.MASTER_URI";

	// private static final String STATE_VIEW_TYPE = "view_type";
	// TODO this is implementation for multiple types in view, maybe should
	// implement later on
	// private String mViewType;

	private CustomersViewDetailFragment customersViewFragmentDetail;
	private ShowHideMasterLayout mShowHideMasterLayout;
	private String customerId;
	ActionMode actionMod;
	private ProgressDialog syncProgressDialog;
	
	private BroadcastReceiver onNotice = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			SyncResult syncResult = intent.getParcelableExtra(NavisionSyncService.SYNC_RESULT);
			if (syncProgressDialog != null) {
				syncProgressDialog.dismiss();
			}
			onSOAPResult(syncResult, intent.getAction());
		}
	};
	
	public void onSOAPResult(SyncResult syncResult, String broadcastAction) {
		if (syncResult.getStatus().equals(SyncStatus.SUCCESS)) {
			if (GetContactsSyncObject.BROADCAST_SYNC_ACTION.equalsIgnoreCase(broadcastAction)) {
				DialogUtil.showInfoDialog(this, getResources().getString(R.string.dialog_title_sync_info), "Kontakti preuzeti!");
			} else if (CustomerAddressesSyncObject.BROADCAST_SYNC_ACTION.equalsIgnoreCase(broadcastAction)) {
				DialogUtil.showInfoDialog(this, getResources().getString(R.string.dialog_title_sync_info), "Adrese preuzete!");
			} else if (CustomerSyncObject.BROADCAST_SYNC_ACTION.equalsIgnoreCase(broadcastAction)) {
				// after this step there is sync potential and there is message
				//DialogUtil.showInfoDialog(this, getResources().getString(R.string.dialog_title_sync_info), "Kupci preuzeti!");
			} else if (SetPotentialCustomersSyncObject.BROADCAST_SYNC_ACTION.equalsIgnoreCase(broadcastAction)) {
				DialogUtil.showInfoDialog(this, getResources().getString(R.string.dialog_title_sync_info), "Potencijalni kupac uspesno poslat!");
			} else if (GetPotentialCustomerSyncObject.BROADCAST_SYNC_ACTION.equalsIgnoreCase(broadcastAction)) {
				DialogUtil.showInfoDialog(this, getResources().getString(R.string.dialog_title_sync_info), "Kupci i potencijalni kupci uspešno preuzeti!");
			}
		} else {
			DialogUtil.showInfoDialog(this, getResources().getString(R.string.dialog_title_error_in_sync), syncResult.getResult());
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_customers_view);
		final FragmentManager fm = getSupportFragmentManager();

		// something about swipe at portrait orientation
		mShowHideMasterLayout = (ShowHideMasterLayout) findViewById(R.id.show_hide_master_layout);
		if (mShowHideMasterLayout != null) {
			mShowHideMasterLayout.setFlingToExposeMasterEnabled(true);
		}

		// routes data from intent that called this activity to business logic
		routeIntent(getIntent(), savedInstanceState != null);

		if (savedInstanceState != null) {
			// @TODO needs to handle this

			customersViewFragmentDetail = (CustomersViewDetailFragment) fm.findFragmentById(R.id.fragment_customer_detail);
			updateDetailBackground();
		}
	}

	private void routeIntent(Intent intent, boolean updateSurfaceOnly) {
		// get URI from intent
		Uri uri = intent.getData();
		if (uri == null) {
			return;
		}
		
		if (intent.hasExtra(Intent.EXTRA_TITLE)) {
			setTitle(intent.getStringExtra(Intent.EXTRA_TITLE));
		}

		String mimeType = getContentResolver().getType(uri);

		if (MobileStoreContract.Customers.CONTENT_TYPE.equals(mimeType)) {
			// Load a session list, hiding the tracks dropdown and the tabs
			if (!updateSurfaceOnly) {
				loadCustomersViewList(uri, null);
				if (mShowHideMasterLayout != null) {
					mShowHideMasterLayout.showMaster(true, ShowHideMasterLayout.FLAG_IMMEDIATE);
				}
			}

		} else if (MobileStoreContract.Customers.CONTENT_ITEM_TYPE.equals(mimeType)) {
			this.customerId = MobileStoreContract.Customers.getCustomersId(uri);
			// Load session details
			if (intent.hasExtra(EXTRA_MASTER_URI)) {
				if (!updateSurfaceOnly) {
					loadCustomersViewList((Uri) intent.getParcelableExtra(EXTRA_MASTER_URI), MobileStoreContract.Customers.getCustomersId(uri));
					loadCustomersViewDetail(uri, false);
				}
			} else {
				if (!updateSurfaceOnly) {
					loadCustomersViewDetail(uri, false);
				}
			}
		}

		updateDetailBackground();
	}

	private void updateDetailBackground() {
		if (customersViewFragmentDetail == null) {
			findViewById(R.id.fragment_customer_detail).setBackgroundResource(R.drawable.grey_frame_on_white_empty_sandbox);
		} else {
			findViewById(R.id.fragment_customer_detail).setBackgroundResource(R.drawable.grey_frame_on_white);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (mShowHideMasterLayout != null && !mShowHideMasterLayout.isMasterVisible()) {
				// If showing the detail view, pressing Up should show the
				// master pane.
				mShowHideMasterLayout.showMaster(true, 0);
				return true;
			}
			break;
		case R.id.add_new_potential_customer:
			Intent potentialCustIntent = new Intent(Intent.ACTION_INSERT, Customers.CONTENT_URI);
			startActivity(potentialCustIntent);
			return true;
		case R.id.create_ecc_activity:
			if (customerId == null || customerId.length() < 1) {
				return true;
			}
			Intent eccIntent = new Intent(getApplicationContext(), NoviEkkPregled.class);
			eccIntent.putExtra("customerId", customerId);
			startActivity(eccIntent);
			/*Intent eccIntent = new Intent(Intent.ACTION_VIEW, ElectronicCardCustomer.buildUri(customerId));
			startActivity(eccIntent);*/
			return true;
		case R.id.create_cus_trade_agree_activity:
			if (customerId == null || customerId.length() < 1) {
				return true;
			}
			Intent customerTradeAgreementIntent = new Intent(Intent.ACTION_VIEW, CustomerTradeAgreemnt.buildUri(customerId));
			startActivity(customerTradeAgreementIntent);
			return true;
		case R.id.sync_all_customers:
			Intent intent = new Intent(this, NavisionSyncService.class);
			CustomerSyncObject syncObject = new CustomerSyncObject("", "", salesPersonNo, DateUtils.getWsDummyDate());
			intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT,syncObject);
			startService(intent);
			Intent intentPotentialCust = new Intent(this, NavisionSyncService.class);
			GetPotentialCustomerSyncObject potentialCustSyncObject = new GetPotentialCustomerSyncObject("", "", salesPersonNo, DateUtils.getWsDummyDate());
			intentPotentialCust.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT,potentialCustSyncObject);
			startService(intentPotentialCust);
			syncProgressDialog = ProgressDialog.show(this, getResources().getString(R.string.dialog_title_customers_receive), getResources().getString(R.string.dialog_body_customers_receive), true, true);
			return true;
		case R.id.edit_customers:
			if (customerId == null || customerId.length() < 1) {
				return true;
			}
			loadCustomersViewDetail(MobileStoreContract.Customers.buildCustomersUri(customerId), true);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	private void loadCustomersViewList(Uri customersListUri, String customerId) {
		CustomersViewListFragment fragment = new CustomersViewListFragment();
		fragment.setSelectedCustomerId(customerId);
		fragment.setArguments(BaseActivity.intentToFragmentArguments(new Intent(Intent.ACTION_VIEW, customersListUri)));
		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_customers_list, fragment).commit();
	}

	private void loadCustomersViewDetail(Uri customersDetailUri, boolean isInUpdateMode) {
		CustomersViewDetailFragment fragment = new CustomersViewDetailFragment();
		Bundle bundle = BaseActivity.intentToFragmentArguments(new Intent(Intent.ACTION_VIEW, customersDetailUri));
		bundle.putBoolean(CustomersViewDetailFragment.IS_IN_UPDATE_MODE, isInUpdateMode);
		fragment.setArguments(bundle);
		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_customer_detail, fragment).commit();
		customersViewFragmentDetail = fragment;
		updateDetailBackground();

		// If loading session details in portrait, hide the master pane
		if (mShowHideMasterLayout != null) {
			mShowHideMasterLayout.showMaster(false, 0);
		}
	}

	@Override
	public boolean onCustomerSelected(String customerId) {
		if(actionMod != null){
			actionMod.finish();
		}
		this.customerId = customerId;
		loadCustomersViewDetail(MobileStoreContract.Customers.buildCustomersUri(customerId), false);
		return true;
	}

	@Override
	public void onCustomerLongClick(String customerId) {
		CustomerContextualMenu	contextualMenu = new CustomerContextualMenu(this, customerId, syncProgressDialog);
	  	actionMod = startActionMode(contextualMenu);
	}
	
	@Override
	public void onCustomerIdAvailable(String customerId) {
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.customer_activity_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
    public void onResume() {
    	super.onResume();
    	IntentFilter contactsSyncObject = new IntentFilter(GetContactsSyncObject.BROADCAST_SYNC_ACTION);
    	LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, contactsSyncObject);
    	IntentFilter addressesSyncObject = new IntentFilter(CustomerAddressesSyncObject.BROADCAST_SYNC_ACTION);
    	LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, addressesSyncObject);
    	IntentFilter customerSyncObject = new IntentFilter(CustomerSyncObject.BROADCAST_SYNC_ACTION);
    	LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, customerSyncObject);
    	IntentFilter potentialCustomerSyncObject = new IntentFilter(SetPotentialCustomersSyncObject.BROADCAST_SYNC_ACTION);
    	LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, potentialCustomerSyncObject);
    	IntentFilter getPotentialCustomersSyncObject = new IntentFilter(GetPotentialCustomerSyncObject.BROADCAST_SYNC_ACTION);
    	LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, getPotentialCustomersSyncObject);
    }
    
    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onNotice);
    }
	
}
