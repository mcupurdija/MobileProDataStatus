package rs.gopro.mobile_store.ui;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.ui.customlayout.ShowHideMasterLayout;
import rs.gopro.mobile_store.ui.fragment.SaleOrderAddEditLineFragment;
import rs.gopro.mobile_store.ui.fragment.SaleOrderLinesAddEditPreviewListFragment;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class SaleOrderLinesAddEditActivity extends BaseActivity implements
		SaleOrderLinesAddEditPreviewListFragment.Callbacks,
		SaleOrderAddEditLineFragment.Callbacks {

	public static final String EXTRA_MASTER_URI = "rs.gopro.mobile_store.extra.MASTER_URI";
	
	private Fragment saleOrderAddEditLineFragment;
	private ShowHideMasterLayout mShowHideMasterLayout;
	private Uri mUri;
	private String mAction;
	private String mActiveSaleOrderLine;
	
	public SaleOrderLinesAddEditActivity() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_sale_order_lines_add_edit);
		
		setTitle(getResources().getString(R.string.title_sale_order_lines_add_edit_activity));
		
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
//		mUri = uri;
		if (uri == null) {
			return;
		}
		
		mAction = intent.getAction();

		if (intent.hasExtra(Intent.EXTRA_TITLE)) {
			setTitle(intent.getStringExtra(Intent.EXTRA_TITLE));
		}

		String mimeType = getContentResolver().getType(uri);

		if (MobileStoreContract.SaleOrderLines.CONTENT_TYPE.equals(mimeType)) {
			// Load a session list, hiding the tracks dropdown and the tabs
			if (!updateSurfaceOnly) {
				loadSalesOrderLinesList(uri, null);
				if (mShowHideMasterLayout != null) {
					mShowHideMasterLayout.showMaster(true,
							ShowHideMasterLayout.FLAG_IMMEDIATE);
				}
			}

		} else if (MobileStoreContract.SaleOrderLines.CONTENT_ITEM_TYPE
				.equals(mimeType)) {
			// Load session details
			if (intent.hasExtra(EXTRA_MASTER_URI)) {
				if (!updateSurfaceOnly) {
					loadSalesOrderLinesList(
							(Uri) intent.getParcelableExtra(EXTRA_MASTER_URI), null);
					loadSaleOrderLineAddEdit(uri);
				}
			} else {
				if (!updateSurfaceOnly) {
					loadSaleOrderLineAddEdit(uri);
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

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	private void loadSalesOrderLinesList(Uri salesOrderLinesUri, String selectSaleOrderLineId) {
		mUri = salesOrderLinesUri;
		SaleOrderLinesAddEditPreviewListFragment fragment = new SaleOrderLinesAddEditPreviewListFragment();
		fragment.setSelectedSalesOrderLineId(selectSaleOrderLineId);
		fragment.setArguments(BaseActivity
				.intentToFragmentArguments(new Intent(Intent.ACTION_VIEW,
						salesOrderLinesUri)));
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment_sale_order_lines_lines_list, fragment).commit();
	}

	private void loadSaleOrderLineAddEdit(Uri addEditUri) {
		SaleOrderAddEditLineFragment fragment = new SaleOrderAddEditLineFragment();
		//mAction should be add or edit
		Intent lineIntent = new Intent(mAction, addEditUri);
		fragment.setArguments(BaseActivity
				.intentToFragmentArguments(lineIntent));
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment_sale_order_line_add_edit, fragment).commit();
		saleOrderAddEditLineFragment = fragment;
		updateDetailBackground();

		// If loading session details in portrait, hide the master pane
		if (mShowHideMasterLayout != null) {
			mShowHideMasterLayout.showMaster(false, 0);
		}
	}

	@Override
	public boolean onSaleOrderLineSelected(String saleOrderLineId) {
		loadSaleOrderLineAddEdit(MobileStoreContract.SaleOrderLines.buildSaleOrderLineUri(saleOrderLineId));
		return true;
	}

	@Override
	public void onSaleOrderLineLongClick(String saleOrderLineId) {
		return;
	}

	@Override
	public void onSaleOrderLineIdAvailable(String saleOrderLineId) {
		mActiveSaleOrderLine = saleOrderLineId;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater menuInflater = getMenuInflater();
	    menuInflater.inflate(R.menu.sale_order_lines_add_edit_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.delete_sale_order_line:
			if (mActiveSaleOrderLine != null) {
				getContentResolver().delete(mUri, MobileStoreContract.SaleOrderLines._ID+"=?", new String[] { mActiveSaleOrderLine });
			} 
			return true;
		case R.id.add_sale_order_line:
			ContentValues cv = new ContentValues();
			cv.put(MobileStoreContract.SaleOrderLines.SALE_ORDER_ID, MobileStoreContract.SaleOrderLines.getSaleOrderId(mUri));
			getContentResolver().insert(mUri, cv);

			return true;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onSaleOrderLineSaved() {
	
	}
}
