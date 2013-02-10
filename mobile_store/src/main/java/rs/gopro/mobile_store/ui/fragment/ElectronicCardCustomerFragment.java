package rs.gopro.mobile_store.ui.fragment;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.Contacts;
import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.provider.MobileStoreContract.ElectronicCardCustomer;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.ui.BaseActivity;
import rs.gopro.mobile_store.util.DateUtils;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.ElectronicCardCustomerSyncObject;
import rs.gopro.mobile_store.ws.model.ItemsSyncObject;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class ElectronicCardCustomerFragment extends ListFragment implements LoaderCallbacks<Cursor> {
	
	public static final String EXTRA_MASTER_URI = "rs.gopro.mobile_store.extra.MASTER_URI";

	private CursorAdapter cursorAdapter;
	private Uri eccUri;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Intent intent = BaseActivity.fragmentArgumentsToIntent(getArguments());
		eccUri = intent.getData();
		if (eccUri == null) {
			return;
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getLoaderManager().initLoader(0, null, this);
		int[] to = new int[] { android.R.id.empty, R.id.el_card_customer_no, R.id.el_card_item_no, R.id.el_card_jan_qty, R.id.el_card_feb_qty, R.id.el_card_mart_qty, R.id.el_card_apr_qty, R.id.el_card_may_qty, R.id.el_card_june_qty, R.id.el_card_july_qty,
				R.id.el_card_aug_qty, R.id.el_card_sep_qty, R.id.el_card_oct_qty, R.id.el_card_nov_qty, R.id.el_card_dec_qty, R.id.el_card_total_sale_curr_qty, R.id.el_card_total_sale_prior_qty, R.id.el_card_turnover_curr_qty,
				R.id.el_card_turnover_prior_qty, R.id.el_card_sales_line_counts_curr_qty, R.id.el_card_sales_line_counts_prior_qty };
		cursorAdapter = new SimpleCursorAdapter(getActivity().getApplicationContext(), R.layout.list_item_el_card_customer, null, ElectronicCardCustomerQuery.PROJECTION, to, 0);

		View headerView = getActivity().getLayoutInflater().inflate(R.layout.list_header_el_card_customer, null, false);
		getListView().addHeaderView(headerView);
		getListView().setDivider(null);
		view.setBackgroundColor(Color.WHITE);
		setListAdapter(cursorAdapter);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		doSync();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String customerId = ElectronicCardCustomer.getECCId(eccUri);
		return new CursorLoader(getActivity(), ElectronicCardCustomer.CONTENT_URI, ElectronicCardCustomerQuery.PROJECTION, ElectronicCardCustomer.CUSTOMER_ID + "=?", new String[] { customerId }, ElectronicCardCustomer.DEFAULT_SORT);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		if(data.moveToFirst()){
			cursorAdapter.swapCursor(data);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		cursorAdapter.swapCursor(null);

	}

	private void doSync() {
		String customerId = ElectronicCardCustomer.getECCId(eccUri);
		Cursor cursor = getActivity().getContentResolver().query(Customers.buildCustomersUri(customerId), new String[]{Customers.CUSTOMER_NO}, Customers._ID + "=?" ,new String[]{customerId}, null);
		String customerNO = null;
		if(cursor.moveToFirst()){
			customerNO = cursor.getString(0);
		}
		Intent intent = new Intent(getActivity(), NavisionSyncService.class);
		ElectronicCardCustomerSyncObject electronicCardCustomerSyncObject = new ElectronicCardCustomerSyncObject("", customerNO, "", "");
		intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, electronicCardCustomerSyncObject);
		getActivity().startService(intent);

	}

	private interface ElectronicCardCustomerQuery {
		String[] PROJECTION = new String[] {
				Tables.ELECTRONIC_CARD_CUSTOMER+"."+BaseColumns._ID,
				Tables.CUSTOMERS+"."+ElectronicCardCustomer.CUSTOMER_NO,
				Tables.ITEMS+"."+ElectronicCardCustomer.ITEM_NO,
				Tables.ELECTRONIC_CARD_CUSTOMER+"."+ElectronicCardCustomer.JANUARY_QTY,
				Tables.ELECTRONIC_CARD_CUSTOMER+"."+ElectronicCardCustomer.FEBRUARY_QTY,
				Tables.ELECTRONIC_CARD_CUSTOMER+"."+ElectronicCardCustomer.MARCH_QTY,
				Tables.ELECTRONIC_CARD_CUSTOMER+"."+ElectronicCardCustomer.APRIL_QTY,
				Tables.ELECTRONIC_CARD_CUSTOMER+"."+ElectronicCardCustomer.MAY_QTY,
				Tables.ELECTRONIC_CARD_CUSTOMER+"."+ElectronicCardCustomer.JUNE_QTY,
				Tables.ELECTRONIC_CARD_CUSTOMER+"."+ElectronicCardCustomer.JULY_QTY,
				Tables.ELECTRONIC_CARD_CUSTOMER+"."+ElectronicCardCustomer.AUGUST_QTY,
				Tables.ELECTRONIC_CARD_CUSTOMER+"."+ElectronicCardCustomer.SEPTEMBER_QTY,
				Tables.ELECTRONIC_CARD_CUSTOMER+"."+ElectronicCardCustomer.OCTOBER_QTY,
				Tables.ELECTRONIC_CARD_CUSTOMER+"."+ElectronicCardCustomer.NOVEMBER_QTY,
				Tables.ELECTRONIC_CARD_CUSTOMER+"."+ElectronicCardCustomer.DECEMBER_QTY,
				Tables.ELECTRONIC_CARD_CUSTOMER+"."+ElectronicCardCustomer.TOTAL_SALE_QTY_CURRENT_YEAR,
				Tables.ELECTRONIC_CARD_CUSTOMER+"."+ElectronicCardCustomer.TOTAL_SALE_QTY_PRIOR_YEAR,
				Tables.ELECTRONIC_CARD_CUSTOMER+"."+ElectronicCardCustomer.TOTAL_TURNOVER_CURRENT_YEAR,
				Tables.ELECTRONIC_CARD_CUSTOMER+"."+ElectronicCardCustomer.TOTAL_TURNOVER_PRIOR_YEAR,
				Tables.ELECTRONIC_CARD_CUSTOMER+"."+ElectronicCardCustomer.SALES_LINE_COUNTS_CURRENT_YEAR,
				Tables.ELECTRONIC_CARD_CUSTOMER+"."+ElectronicCardCustomer.SALES_LINE_COUNTS_PRIOR_YEAR,};

		int _ID = 0;
		int CUSTOMER_ID = 1;
		int ITEM_ID = 2;
		int JANUARY_QTY = 3;
		int FEBRUARY_QTY = 4;
		int MARCH_QTY = 5;
		int APRIL_QTY = 6;
		int MAY_QTY = 7;
		int JUNE_QTY = 8;
		int JULY_QTY = 9;
		int AUGUST_QTY = 10;
		int SEPTEMBER_QTY = 11;
		int OCTOBER_QTY = 12;
		int NOVEMBER_QTY = 13;
		int DECEMBER_QTY = 14;
		int TOTAL_SALE_QTY_CURRENT_YEAR = 15;
		int TOTAL_SALE_QTY_PRIOR_YEAR = 16;
		int TOTAL_TURNOVER_CURRENT_YEAR = 17;
		int TOTAL_TURNOVER_PRIOR_YEAR = 18;
		int SALES_LINE_COUNTS_CURRENT_YEAR = 19;
		int SALES_LINE_COUNTS_PRIOR_YEAR = 20;
	}

}
