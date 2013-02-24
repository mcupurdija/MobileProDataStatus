package rs.gopro.mobile_store.ui;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.CustomerTradeAgreemnt;
import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.provider.MobileStoreContract.ElectronicCardCustomer;
import rs.gopro.mobile_store.ui.customlayout.ShowHideMasterLayout;
import rs.gopro.mobile_store.util.DateUtils;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.CustomerAddressesSyncObject;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnLongClickListener;

public class CustomersViewActivity extends BaseActivity implements CustomersViewListFragment.Callbacks, CustomersViewDetailFragment.Callbacks {

	public static final String EXTRA_MASTER_URI = "rs.gopro.mobile_store.extra.MASTER_URI";

	// private static final String STATE_VIEW_TYPE = "view_type";
	// TODO this is implementation for multiple types in view, maybe should
	// implement later on
	// private String mViewType;

	private CustomersViewDetailFragment customersViewFragmentDetail;
	private ShowHideMasterLayout mShowHideMasterLayout;
	private String customerId;

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
		this.customerId = MobileStoreContract.Customers.getCustomersId(uri);
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
		case R.id.create_ecc_activity:
			Intent eccIntent = new Intent(Intent.ACTION_VIEW, ElectronicCardCustomer.buildUri(customerId));
			startActivity(eccIntent);
			return true;
		case R.id.create_cus_trade_agree_activity:
			Intent customerTradeAgreementIntent = new Intent(Intent.ACTION_VIEW, CustomerTradeAgreemnt.buildUri(customerId));
			startActivity(customerTradeAgreementIntent);
			return true;
		case R.id.sych_customer_address:
			doSync();
			return true;
		case R.id.edit_customers:
			loadCustomersViewDetail(MobileStoreContract.Customers.buildCustomersUri(customerId), true);

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
		this.customerId = customerId;
		loadCustomersViewDetail(MobileStoreContract.Customers.buildCustomersUri(customerId), false);
		return true;
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


	private void doSync() {
		Cursor cursor = getContentResolver().query(MobileStoreContract.Customers.buildCustomersUri(customerId), new String[] { Customers._ID, Customers.CUSTOMER_NO }, null, null, null);
		if (cursor.moveToFirst()) {
			String customerNo = cursor.getString(1);
			Intent syncAddressIntent = new Intent(this, NavisionSyncService.class);
			CustomerAddressesSyncObject addressesSyncObject = new CustomerAddressesSyncObject("", customerNo, "", DateUtils.getWsDummyDate());
			syncAddressIntent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, addressesSyncObject);
			startService(syncAddressIntent);
		}
	}

}
