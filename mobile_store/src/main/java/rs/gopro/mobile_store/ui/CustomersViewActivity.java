package rs.gopro.mobile_store.ui;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.ui.customlayout.ShowHideMasterLayout;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

public class CustomersViewActivity extends BaseActivity implements
	CustomersViewListFragment.Callbacks, CustomersViewDetailFragment.Callbacks {

	public static final String EXTRA_MASTER_URI = "rs.gopro.mobile_store.extra.MASTER_URI";

//	private static final String STATE_VIEW_TYPE = "view_type";
//	TODO this is implementation for multiple types in view, maybe should implement later on
//	private String mViewType;

	private Fragment customersViewFragmentDetail;
	private ShowHideMasterLayout mShowHideMasterLayout;

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

			customersViewFragmentDetail = fm
					.findFragmentById(R.id.fragment_customer_detail);
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
					mShowHideMasterLayout.showMaster(true,
							ShowHideMasterLayout.FLAG_IMMEDIATE);
				}
			}

		} else if (MobileStoreContract.Visits.CONTENT_ITEM_TYPE
				.equals(mimeType)) {
			// Load session details
			if (intent.hasExtra(EXTRA_MASTER_URI)) {
				if (!updateSurfaceOnly) {
					loadCustomersViewList(
							(Uri) intent.getParcelableExtra(EXTRA_MASTER_URI),
							MobileStoreContract.Customers.getCustomersId(uri));
					loadCustomersViewDetail(uri);
				}
			} else {
				if (!updateSurfaceOnly) {
					loadCustomersViewDetail(uri);
				}
			}
		}

		updateDetailBackground();
	}

	private void updateDetailBackground() {
		if (customersViewFragmentDetail == null) {
			findViewById(R.id.fragment_customer_detail)
					.setBackgroundResource(
							R.drawable.grey_frame_on_white_empty_sandbox);
		} else {
			findViewById(R.id.fragment_customer_detail)
					.setBackgroundResource(R.drawable.grey_frame_on_white);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (mShowHideMasterLayout != null
					&& !mShowHideMasterLayout.isMasterVisible()) {
				// If showing the detail view, pressing Up should show the
				// master pane.
				mShowHideMasterLayout.showMaster(true, 0);
				return true;
			}
			break;
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
		fragment.setArguments(BaseActivity
				.intentToFragmentArguments(new Intent(Intent.ACTION_VIEW,
						customersListUri)));
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment_customers_list, fragment).commit();
	}

	private void loadCustomersViewDetail(Uri customersDetailUri) {
		CustomersViewDetailFragment fragment = new CustomersViewDetailFragment();
		fragment.setArguments(BaseActivity
				.intentToFragmentArguments(new Intent(Intent.ACTION_VIEW,
						customersDetailUri)));
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment_customer_detail, fragment).commit();
		customersViewFragmentDetail = fragment;
		updateDetailBackground();

		// If loading session details in portrait, hide the master pane
		if (mShowHideMasterLayout != null) {
			mShowHideMasterLayout.showMaster(false, 0);
		}
	}

	@Override
	public boolean onCustomerSelected(String customerId) {
		loadCustomersViewDetail(MobileStoreContract.Visits.buildVisitUri(customerId));
		return true;
	}

	@Override
	public void onCustomerIdAvailable(String customerId) {	
	}
}
