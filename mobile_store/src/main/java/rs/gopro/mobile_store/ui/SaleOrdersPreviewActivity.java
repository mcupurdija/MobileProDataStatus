package rs.gopro.mobile_store.ui;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.ui.customlayout.ShowHideMasterLayout;
import rs.gopro.mobile_store.ui.SaleOrdersPreviewListFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

public class SaleOrdersPreviewActivity extends BaseActivity implements
		SaleOrdersPreviewListFragment.Callbacks, VisitDetailFragment.Callbacks {

	public static final String EXTRA_MASTER_URI = "rs.gopro.mobile_store.extra.MASTER_URI";

	private static final String STATE_VIEW_TYPE = "view_type";

	private String mViewType;

	private Fragment saleOrderLinesFragment;
	private ShowHideMasterLayout mShowHideMasterLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_visits);
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

			saleOrderLinesFragment = fm
					.findFragmentById(R.id.fragment_sale_order_lines_list);
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

		if (MobileStoreContract.SaleOrders.CONTENT_TYPE.equals(mimeType)) {
			// Load a session list, hiding the tracks dropdown and the tabs
			if (!updateSurfaceOnly) {
				loadSalesOrdersList(uri, null);
				if (mShowHideMasterLayout != null) {
					mShowHideMasterLayout.showMaster(true,
							ShowHideMasterLayout.FLAG_IMMEDIATE);
				}
			}

		} else if (MobileStoreContract.SaleOrderLines.CONTENT_TYPE
				.equals(mimeType)) {
			// Load session details
			if (intent.hasExtra(EXTRA_MASTER_URI)) {
				if (!updateSurfaceOnly) {
					loadSalesOrdersList(
							(Uri) intent.getParcelableExtra(EXTRA_MASTER_URI),
							MobileStoreContract.SaleOrders.getSaleOrderId(uri));
					loadSaleOrderLines(uri);
				}
			} else {
				if (!updateSurfaceOnly) {
					loadSaleOrderLines(uri);
				}
			}
		}

		updateDetailBackground();
	}

	private void updateDetailBackground() {
		if (saleOrderLinesFragment == null) {
			findViewById(R.id.fragment_visitsplan_detail)
					.setBackgroundResource(
							R.drawable.grey_frame_on_white_empty_sandbox);
		} else {
			findViewById(R.id.fragment_visitsplan_detail)
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

	private void loadSalesOrdersList(Uri salesOrdersUri, String selectVisitId) {
		SaleOrdersPreviewListFragment fragment = new SaleOrdersPreviewListFragment();
		fragment.setSelectedSalesOrderId(selectVisitId);
		fragment.setArguments(BaseActivity
				.intentToFragmentArguments(new Intent(Intent.ACTION_VIEW,
						salesOrdersUri)));
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment_sale_orders_list, fragment).commit();
	}

	private void loadSaleOrderLines(Uri visitUri) {
		VisitDetailFragment fragment = new VisitDetailFragment();
		fragment.setArguments(BaseActivity
				.intentToFragmentArguments(new Intent(Intent.ACTION_VIEW,
						visitUri)));
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment_sale_order_lines_list, fragment).commit();
		saleOrderLinesFragment = fragment;
		updateDetailBackground();

		// If loading session details in portrait, hide the master pane
		if (mShowHideMasterLayout != null) {
			mShowHideMasterLayout.showMaster(false, 0);
		}
	}
	
	@Override
	public boolean onSaleOrderSelected(String saleOrderId) {
		loadSaleOrderLines(MobileStoreContract.Visits.buildVisitUri(saleOrderId));
		return true;
	}

	@Override
	public void onVisitIdAvailable(String visitId) {
	}
}
