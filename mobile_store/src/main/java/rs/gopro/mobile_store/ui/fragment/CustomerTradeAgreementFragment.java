package rs.gopro.mobile_store.ui.fragment;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract.CustomerTradeAgreemnt;
import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.ui.BaseActivity;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.CustomerTradeAgreementSyncObject;
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
import android.view.View;

public class CustomerTradeAgreementFragment extends ListFragment implements LoaderCallbacks<Cursor> {

	private CursorAdapter cursorAdapter;
	private Uri cusTradeAgrUri;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Intent intent = BaseActivity.fragmentArgumentsToIntent(getArguments());
		cusTradeAgrUri = intent.getData();
		if (cusTradeAgrUri == null) {
			return;
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getLoaderManager().initLoader(0, null, this);
		int[] to = new int[] { android.R.id.empty, R.id.cus_trade_agreement_customer_no, R.id.cus_trade_agreement_entry_type, R.id.cus_trade_agreement_code, R.id.cus_trade_agreement_minimum_quantity, R.id.cus_trade_agreement_starting_date,
				R.id.cus_trade_agreement_ending_date, R.id.cus_trade_agreement_actual_discount };
		cursorAdapter = new SimpleCursorAdapter(getActivity().getApplicationContext(), R.layout.list_item_cust_trade_agree, null, CustomerTradeAgreementQuery.PROJECTION, to, 0);

		View headerView = getActivity().getLayoutInflater().inflate(R.layout.list_header_cus_trade_agree, null, false);
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
		String customerId = CustomerTradeAgreemnt.getId(cusTradeAgrUri);
		return new CursorLoader(getActivity(), CustomerTradeAgreemnt.CONTENT_URI, CustomerTradeAgreementQuery.PROJECTION, Tables.CUSTOMER_TRADE_AGREEMENT + "." + CustomerTradeAgreemnt.CUSTOMER_ID + "=?", new String[] { customerId }, CustomerTradeAgreemnt.DEFAULT_SORT);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		cursorAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub

	}

	private void doSync() {
		String customerId = CustomerTradeAgreemnt.getId(cusTradeAgrUri);
		Cursor cursor = getActivity().getContentResolver().query(Customers.buildCustomersUri(customerId), new String[] { Customers.CUSTOMER_NO }, Customers._ID + "=?", new String[] { customerId }, null);
		String customerNO = null;
		if (cursor.moveToFirst()) {
			customerNO = cursor.getString(0);
		}
		Intent intent = new Intent(getActivity(), NavisionSyncService.class);
		System.out.println("#### CUS NO: "+customerNO);
		CustomerTradeAgreementSyncObject agreementSyncObject = new CustomerTradeAgreementSyncObject("",customerNO);
		intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, agreementSyncObject);
		getActivity().startService(intent);
	}

	private interface CustomerTradeAgreementQuery {
		String[] PROJECTION = new String[] { BaseColumns._ID, CustomerTradeAgreemnt.CUSTOMER_NO, CustomerTradeAgreemnt.ENTRY_TYPE, CustomerTradeAgreemnt.CODE, CustomerTradeAgreemnt.MINIMUM_QUANTITY, CustomerTradeAgreemnt.STARTING_DATE, CustomerTradeAgreemnt.ENDING_DATE,
				CustomerTradeAgreemnt.ACTUAL_DISCOUNT

		};

		int _ID = 0;
		int CUSTOMER_ID = 1;
		int ENTRY_TYPE = 2;
		int CODE = 3;
		int MINIMUM_QUANTITY = 4;
		int STARTING_DATE = 5;
		int ENDING_DATE = 6;
		int ACTUAL_DISCOUNT = 7;

	}
}
