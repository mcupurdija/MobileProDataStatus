package rs.gopro.mobile_store.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.SaleOrders;
import rs.gopro.mobile_store.provider.MobileStoreContract.SentOrders;
import rs.gopro.mobile_store.ui.SaleOrdersPreviewActivity;
import rs.gopro.mobile_store.ui.widget.SimpleSelectionedListAdapter;
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
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Filter.FilterListener;

public class SentOrdersFragment extends ListFragment implements LoaderCallbacks<Cursor>, TextWatcher, OnItemSelectedListener, FilterListener {
	
	private String splitQuerySeparator = ";";
	private SimpleSelectionedListAdapter adapter;
	private SaleOrdersListAdapter saleAdapter;
	private EditText searchText;
	private Spinner spinner;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		saleAdapter = new SaleOrdersListAdapter(getActivity());
		adapter = new SimpleSelectionedListAdapter(getActivity(), R.layout.list_item_sale_order_header, saleAdapter);

		saleAdapter.setFilterQueryProvider(new FilterQueryProvider() {

			@Override
			public Cursor runQuery(CharSequence constraint) {
				String[] queryStrings = constraint.toString().split(splitQuerySeparator);
				Cursor cursor = null;
				if (getActivity() != null) {
					cursor = getActivity().getContentResolver().query(SentOrders.buildCustomSearchUri(queryStrings[0], queryStrings[1], ApplicationConstants.OrderType.SENT_ORDER.getType()), SaleOrderQuery.PROJECTION, null, null, SaleOrders.DEFAULT_SORT);
				}
				return cursor;
			}
		});

		setListAdapter(adapter);
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
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.sale_order_block_status_array, android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner = (Spinner) getActivity().findViewById(R.id.sent_order_status_spinner);
			spinner.setOnItemSelectedListener(this);
			spinner.setAdapter(adapter);
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
		// CursorLoader cursorLoader = new CursorLoader(getActivity(),
		// MobileStoreContract.SaleOrders.CONTENT_URI,
		// SaleOrderQuery.PROJECTION, null, null,
		// MobileStoreContract.SaleOrders.DEFAULT_SORT);
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

		if (getActivity() == null) {
			return;
		}
		List<SimpleSelectionedListAdapter.Section> sections = new ArrayList<SimpleSelectionedListAdapter.Section>();
		cursor.moveToFirst();
		long previousOrderDate = -1;
		long orderDate;
		while (!cursor.isAfterLast()) {
			orderDate = UIUtils.getDateTime(cursor.getString(SaleOrderQuery.ORDER_DATE)).getTime();
			if (!UIUtils.isSameDay(previousOrderDate, orderDate)) {
				String title = DateUtils.formatDateTime(getActivity(), orderDate, DateUtils.FORMAT_ABBREV_MONTH | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY);
				sections.add(new SimpleSelectionedListAdapter.Section(cursor.getPosition(), title));

			}
			previousOrderDate = orderDate;
			cursor.moveToNext();
		}
		saleAdapter.changeCursor(cursor);
		SimpleSelectionedListAdapter.Section[] dummy = new SimpleSelectionedListAdapter.Section[sections.size()];
		adapter.setSections(sections.toArray(dummy));
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub

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
		int statusId = spinner.getSelectedItemPosition();
		String queryString = s.toString() + splitQuerySeparator + statusId;
		saleAdapter.getFilter().filter(queryString);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
		String queryString = searchText.getText().toString() + splitQuerySeparator + position;
		saleAdapter.getFilter().filter(queryString);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFilterComplete(int count) {
		// TODO Auto-generated method stub

	}

	public class SaleOrdersListAdapter extends CursorAdapter {
		/** Flags used with {@link DateUtils#formatDateRange}. */
		private static final int TIME_FLAGS = DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_WEEKDAY;

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
			final Integer totalAmount = cursor.getInt(SaleOrderQuery.TOTAL);
			final long orderDate = UIUtils.getDateTime(cursor.getString(SaleOrderQuery.ORDER_DATE)).getTime();

			final TextView timeView = (TextView) view.findViewById(R.id.block_time);
			final TextView titleView = (TextView) view.findViewById(R.id.block_title);
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

			titleView.setText(saleOrderNo);
			titleView.setTextColor(res.getColorStateList(R.color.body_text_1_positive));
			subtitleView.setText(getString(R.string.sale_order_amount_total_template) + totalAmount);
			primaryTouchTargetView.setOnClickListener(allSessionsListener);
			subtitleView.setTextColor(res.getColorStateList(R.color.body_text_2));
			primaryTouchTargetView.setEnabled(true);
			timeView.setText(DateUtils.formatDateTime(context, orderDate, DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_12HOUR));
		}

	}

	private interface SaleOrderQuery {

		String[] PROJECTION = { BaseColumns._ID, SaleOrders.SALES_ORDER_NO, SaleOrders.ORDER_DATE, SaleOrders.TOTAL };

		int _ID = 0;
		int NO = 1;
		int ORDER_DATE = 2;
		int TOTAL = 3;
	}

}
