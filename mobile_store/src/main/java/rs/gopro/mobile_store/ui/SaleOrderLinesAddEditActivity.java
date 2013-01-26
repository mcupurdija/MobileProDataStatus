package rs.gopro.mobile_store.ui;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.ui.customlayout.ShowHideMasterLayout;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

public class SaleOrderLinesAddEditActivity extends BaseActivity {

	public static final String EXTRA_MASTER_URI = "rs.gopro.mobile_store.extra.MASTER_URI";

	private Fragment saleOrderAddEditLineFragment;
	private ShowHideMasterLayout mShowHideMasterLayout;

	public SaleOrderLinesAddEditActivity() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_sale_order_lines_add_edit);
		final FragmentManager fm = getSupportFragmentManager();

		// something about swipe at portrait orientation
		mShowHideMasterLayout = (ShowHideMasterLayout) findViewById(R.id.sales_order_lines_add_edit_master_layout);
		if (mShowHideMasterLayout != null) {
			mShowHideMasterLayout.setFlingToExposeMasterEnabled(true);
		}

		// routes data from intent that called this activity to business logic
		routeIntent(getIntent(), savedInstanceState != null);

		if (savedInstanceState != null) {
			// @TODO needs to handle this

			saleOrderAddEditLineFragment = fm
					.findFragmentById(R.id.fragment_sale_order_line_add_edit);
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

		if (MobileStoreContract.SaleOrderLines.CONTENT_TYPE.equals(mimeType)) {
			// Load a session list, hiding the tracks dropdown and the tabs
			if (!updateSurfaceOnly) {
//				loadSalesOrdersList(uri, null);
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
//					loadSalesOrdersList(
//							(Uri) intent.getParcelableExtra(EXTRA_MASTER_URI),
//							MobileStoreContract.SaleOrders.getSaleOrderId(uri));
//					loadSaleOrderLines(uri);
				}
			} else {
				if (!updateSurfaceOnly) {
//					loadSaleOrderLines(uri);
				}
			}
		}

		updateDetailBackground();
	}

	private void updateDetailBackground() {
		if (saleOrderAddEditLineFragment == null) {
			View view = findViewById(R.id.fragment_sale_order_line_add_edit);
			view.setBackgroundResource(R.drawable.grey_frame_on_white_empty_sandbox);
		} else {
			findViewById(R.id.fragment_sale_order_line_add_edit)
					.setBackgroundResource(R.drawable.grey_frame_on_white);
		}
	}

}
