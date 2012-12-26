package rs.gopro.mobile_store.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;

import rs.gopro.mobile_store.ui.widget.SimpleSelectionedListAdapter;
import rs.gopro.mobile_store.util.UIUtils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class InvoicesFragment  extends ListFragment implements LoaderCallbacks<Cursor> {

	private SimpleSelectionedListAdapter adapter;
	private InvoicesListAdapter invoicesAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		invoicesAdapter = new InvoicesListAdapter(getActivity());
		adapter = new SimpleSelectionedListAdapter(getActivity(), R.layout.list_item_sale_order_header, invoicesAdapter);
		setListAdapter(adapter);
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_list_with_empty_container, container, false);
		inflater.inflate(R.layout.content_empty_waiting_sync, (ViewGroup) rootView.findViewById(android.R.id.empty), true);
		rootView.setBackgroundColor(Color.WHITE);
		ListView listView = (ListView) rootView.findViewById(android.R.id.list);
		listView.setItemsCanFocus(true);
		listView.setCacheColorHint(Color.WHITE);
		listView.setSelector(android.R.color.transparent);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		activity.getContentResolver().registerContentObserver(MobileStoreContract.Invoices.CONTENT_URI, true, mObserver);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		getActivity().getContentResolver().unregisterContentObserver(mObserver);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		CursorLoader cursorLoader = new CursorLoader(getActivity(), MobileStoreContract.Invoices.CONTENT_URI, InvoicesQuery.PROJECTION, null, null, MobileStoreContract.Invoices.DEFAULT_SORT);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

		if (getActivity() == null) {
			return;
		}
		List<SimpleSelectionedListAdapter.Section> sections = new ArrayList<SimpleSelectionedListAdapter.Section>();
		cursor.moveToFirst();
		long previouspostingDate = -1;
		long postingDate;
		while (!cursor.isAfterLast()) {
			postingDate = UIUtils.getDate(cursor.getString(InvoicesQuery.POSTING_DATE)).getTime();
			if (!UIUtils.isSameDay(previouspostingDate, postingDate)) {
				String title = DateUtils.formatDateTime(getActivity(), postingDate, DateUtils.FORMAT_ABBREV_MONTH | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY);
				sections.add(new SimpleSelectionedListAdapter.Section(cursor.getPosition(), title));

			}
			previouspostingDate = postingDate;
			cursor.moveToNext();
		}
		invoicesAdapter.changeCursor(cursor);
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
	
	
	
	
	
	
	
	
	public class InvoicesListAdapter extends CursorAdapter {
		/** Flags used with {@link DateUtils#formatDateRange}. */
		private static final int TIME_FLAGS = DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_WEEKDAY;

		public InvoicesListAdapter(Context context) {
			super(context, null, false);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return getActivity().getLayoutInflater().inflate(R.layout.list_item_invoices_block, parent, false);
		}

		@Override
		public void bindView(View view, Context context, final Cursor cursor) {
			final Integer invoicesId = cursor.getInt(InvoicesQuery._ID);
			final String invoicesNo = cursor.getString(InvoicesQuery.NO);
			final Integer totalAmount = cursor.getInt(InvoicesQuery.TOTAL);
			final long dateOfCreation = UIUtils.getDate(cursor.getString(InvoicesQuery.CREATED_DATE)).getTime();
			final long postingDate = UIUtils.getDate(cursor.getString(InvoicesQuery.POSTING_DATE)).getTime();
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
					final Uri sessionsUri = MobileStoreContract.Invoices.buildInvoicesUri(invoicesId.toString());
					final Intent intent = new Intent(Intent.ACTION_VIEW, sessionsUri);
					// startActivity(intent);
				}
			};

			titleView.setText(invoicesNo);
			titleView.setTextColor(res.getColorStateList(R.color.body_text_1_positive));
			subtitleView.setText(getString(R.string.invoice_amount_total_template) + totalAmount);
			primaryTouchTargetView.setOnClickListener(allSessionsListener);
			subtitleView.setTextColor(res.getColorStateList(R.color.body_text_2));
			primaryTouchTargetView.setEnabled(true);
			timeView.setText(DateUtils.formatDateTime(context, postingDate, DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_12HOUR));
		}

	}
	
	private interface InvoicesQuery {

		String[] PROJECTION = { BaseColumns._ID, MobileStoreContract.Invoices.INVOICE_NO, MobileStoreContract.Invoices.CUSTOMER_ID, MobileStoreContract.Invoices.POSTING_DATE, MobileStoreContract.Invoices.SALES_PERSON_ID, MobileStoreContract.Invoices.DUE_DATE,
				MobileStoreContract.Invoices.TOTAL, MobileStoreContract.Invoices.TOTAL_LEFT, MobileStoreContract.Invoices.DUE_DATE_DAYS_LEFT, MobileStoreContract.Invoices.CREATED_DATE, MobileStoreContract.Invoices.CREATED_BY,
				MobileStoreContract.Invoices.UPDATED_DATE, MobileStoreContract.Invoices.UPDATED_BY

		};

		int _ID = 0;
		int NO = 1;
		int CUSTOMER_ID = 2;
		int POSTING_DATE = 3;
		int SALES_PERSON_ID = 4;
		int DUE_DATE = 5;
		int TOTAL = 6;
		int TOTAL_LEFT = 7;
		int DUE_DATE_DAYS_LEFT = 8;
		int CREATED_DATE = 9;
		int CREATED_BY = 10;
		int UPDATED_DATE = 11;
		int UPDATED_BY = 12;
	}


}