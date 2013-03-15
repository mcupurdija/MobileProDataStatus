package rs.gopro.mobile_store.ui.fragment;

import java.util.Date;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.provider.MobileStoreContract.SaleOrders;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.ui.SaleOrdersPreviewActivity;
import rs.gopro.mobile_store.util.ApplicationConstants;
import rs.gopro.mobile_store.util.UIUtils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter.FilterListener;
import android.widget.Spinner;
import android.widget.TextView;

public class SentOrdersFragment extends ListFragment implements LoaderCallbacks<Cursor>, TextWatcher, OnItemSelectedListener, FilterListener {
	
//	private String splitQuerySeparator = ";";
//	private SimpleSelectionedListAdapter adapter;
	private SaleOrdersListAdapter saleAdapter;
	private EditText searchText;
	private Spinner spinner;
	private String[] financialControlStatusOptions;
	private String[] docTypeOptions;
	private String customer_no = "noCustomer";
	private int document_status = -1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		saleAdapter = new SaleOrdersListAdapter(getActivity());
//		adapter = new SimpleSelectionedListAdapter(getActivity(), R.layout.list_item_sale_order_header, saleAdapter);
//
//		saleAdapter.setFilterQueryProvider(new FilterQueryProvider() {
//
//			@Override
//			public Cursor runQuery(CharSequence constraint) {
//				String[] queryStrings = constraint.toString().split(splitQuerySeparator);
//				Cursor cursor = null;
//				if (getActivity() != null) {
//					cursor = getActivity().getContentResolver().query(SentOrders.buildCustomSearchUri(queryStrings[0], queryStrings[1], ApplicationConstants.OrderType.SENT_ORDER.getType()), SaleOrderQuery.PROJECTION, null, null, SaleOrders.DEFAULT_SORT);
//				}
//				return cursor;
//			}
//		});

		setListAdapter(saleAdapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			return super.onCreateView(inflater, container, savedInstanceState);
		
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState == null) {
			getLoaderManager().initLoader(0, null, this);
			searchText = (EditText) getActivity().findViewById(R.id.input_search_sent_order);
			searchText.addTextChangedListener(this);
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.sale_order_block_status_filter_array, android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner = (Spinner) getActivity().findViewById(R.id.sent_order_status_spinner);
			spinner.setOnItemSelectedListener(this);
			spinner.setAdapter(adapter);
			financialControlStatusOptions = getResources().getStringArray(R.array.financial_control_status_array);
			docTypeOptions = getResources().getStringArray(R.array.sale_order_block_status_array);
		} else {
			getLoaderManager().restartLoader(0, null, this);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		activity.getContentResolver().registerContentObserver(MobileStoreContract.SentOrders.CONTENT_URI, true, mObserver);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		getActivity().getContentResolver().unregisterContentObserver(mObserver);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// This is called when a new Loader needs to be created.  This
        // sample only has one Loader, so we don't care about the ID.
        // First, pick the base URI to use depending on whether we are
        // currently filtering.
        Uri baseUri;
        String filter = null;
        if (customer_no.equals("noCustomer") && document_status == -1) {
        	baseUri = MobileStoreContract.SaleOrders.CONTENT_URI;
        	filter = Tables.SALE_ORDERS + "."
					+ SaleOrders.SALES_ORDER_NO + " is not null";
        } else {
        	baseUri = SaleOrders.buildCustomSearchUri(customer_no, String.valueOf(document_status), ApplicationConstants.OrderType.SENT_ORDER.getType());
        	filter = null;
        }
		
		CursorLoader cursorLoader = new CursorLoader(getActivity(),
				baseUri,
				SaleOrderQuery.PROJECTION, filter, null,
				SaleOrders.DEFAULT_SORT);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (getActivity() == null) {
			return;
		}
		cursor.moveToFirst();
		saleAdapter.changeCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}

	private final ContentObserver mObserver = new ContentObserver(new Handler()) {
		@Override
		public void onChange(boolean selfChange) {
			if (getActivity() == null) {
				return;
			}
			Loader<Cursor> loader = getLoaderManager().getLoader(0);
			if (loader != null) {
				loader.forceLoad();
			}
		}
	};

	@Override
	public void afterTextChanged(Editable s) {
		// Called when the action bar search text has changed.  Update
        // the search filter, and restart the loader to do a new query
        // with this filter.
        String customer_no_new = !TextUtils.isEmpty(s.toString()) ? s.toString() : "noCustomer";
        // Don't do anything if the filter hasn't actually changed.
        // Prevents restarting the loader when restoring state.
        if (customer_no == null && customer_no_new == null) {
        	customer_no = "noCustomer";
            return;
        }
        if (customer_no != null && customer_no.equals(customer_no_new)) {
            return;
        }
        customer_no = customer_no_new;
        getLoaderManager().restartLoader(0, null, this);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
		int document_status_new = position;
		
		document_status = document_status_new == 0 ? -1 : document_status_new-1; // -1 because 0 means options
		
		getLoaderManager().restartLoader(0, null, this);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	@Override
	public void onFilterComplete(int count) {
	}

	public class SaleOrdersListAdapter extends CursorAdapter {
		/** Flags used with {@link DateUtils#formatDateRange}. */
//		private static final int TIME_FLAGS = DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_WEEKDAY;

		public SaleOrdersListAdapter(Context context) {
			super(context, null, false);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return getActivity().getLayoutInflater().inflate(R.layout.list_item_invoices_block, parent, false);
		}

		@Override
		public void bindView(View view, Context context, final Cursor cursor) {
			final Integer saleOrderId = cursor.getInt(SaleOrderQuery._ID);
			final String saleOrderNo = cursor.getString(SaleOrderQuery.NO);
			final String customerNo = cursor.getString(SaleOrderQuery.CUSTOMER_NO);
			final String customerName = cursor.getString(SaleOrderQuery.CUSTOMER_NAME);
//			final Integer totalAmount = cursor.getInt(SaleOrderQuery.TOTAL);
			final long orderDate = UIUtils.getDateTime(cursor.getString(SaleOrderQuery.ORDER_DATE)).getTime();
			final int financial_control_status = cursor.getInt(SaleOrderQuery.FIN_CONTROL_STATUS);
			final int document_type = cursor.getInt(SaleOrderQuery.DOCUMENT_TYPE);
			
			final TextView timeView = (TextView) view.findViewById(R.id.block_time);
			final TextView titleView = (TextView) view.findViewById(R.id.block_title);
			final TextView title2View = (TextView) view.findViewById(R.id.block_status);
			final TextView subtitleView = (TextView) view.findViewById(R.id.block_subtitle);
			final View primaryTouchTargetView = view.findViewById(R.id.list_item_middle_container);

			final Resources res = getResources();

			primaryTouchTargetView.setOnLongClickListener(null);
			UIUtils.setActivatedCompat(primaryTouchTargetView, false);

			View.OnClickListener allSessionsListener = new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					final Uri saleOrdersUri = MobileStoreContract.SaleOrders.CONTENT_URI;
                    final Intent intent = new Intent(Intent.ACTION_VIEW, MobileStoreContract.SaleOrderLines.buildSaleOrderLinesUri(String.valueOf(saleOrderId.intValue())));
                    intent.putExtra(SaleOrdersPreviewActivity.EXTRA_MASTER_URI, saleOrdersUri);
                    startActivity(intent);
				}
			};

			titleView.setText(customerNo+"-"+customerName);
			titleView.setTextColor(res.getColorStateList(R.color.body_text_1_positive));
			title2View.setText(docTypeOptions[document_type]);
			title2View.setTextColor(res.getColorStateList(R.color.body_text_2));
			subtitleView.setText(saleOrderNo+" - "+rs.gopro.mobile_store.util.DateUtils.toUIDate(new Date(orderDate)));
			primaryTouchTargetView.setOnClickListener(allSessionsListener);
			subtitleView.setTextColor(res.getColorStateList(R.color.body_text_2));
			primaryTouchTargetView.setEnabled(true);
			timeView.setText("Status:\n"+financialControlStatusOptions[financial_control_status]); //DateUtils.formatDateTime(context, orderDate, DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_12HOUR)
		}

	}

	private interface SaleOrderQuery {

		String[] PROJECTION = { Tables.SALE_ORDERS+"."+BaseColumns._ID, Tables.SALE_ORDERS+"."+SaleOrders.SALES_ORDER_DEVICE_NO, Tables.SALE_ORDERS+"."+SaleOrders.ORDER_DATE, Tables.SALE_ORDERS+"."+SaleOrders.TOTAL, Tables.CUSTOMERS+"."+Customers.CUSTOMER_NO, Tables.CUSTOMERS+"."+Customers.NAME, Tables.SALE_ORDERS+"."+SaleOrders.FIN_CONTROL_STATUS, Tables.SALE_ORDERS+"."+SaleOrders.DOCUMENT_TYPE  };

		int _ID = 0;
		int NO = 1;
		int ORDER_DATE = 2;
//		int TOTAL = 3;
		int CUSTOMER_NO = 4;
		int CUSTOMER_NAME = 5;
		int FIN_CONTROL_STATUS = 6;
		int DOCUMENT_TYPE = 7;
	}

}
