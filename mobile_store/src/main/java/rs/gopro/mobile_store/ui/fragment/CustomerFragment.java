package rs.gopro.mobile_store.ui.fragment;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.ui.CustomersViewActivity;
import rs.gopro.mobile_store.util.LogUtils;
import rs.gopro.mobile_store.util.UIUtils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class CustomerFragment extends ListFragment implements LoaderCallbacks<Cursor>, TextWatcher, OnItemSelectedListener {
	private static String TAG = "CustomerFragment";
	private final static String DUMMY_SEARCH = "noNoOrName";
	
	private EditText searchText;
	private Spinner spinner;
	private CustomerAdapter customerCursorAdapter;
//	private String mSelectedCustomerId;
	private String customer_no = DUMMY_SEARCH;
	private int customer_status = -1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtils.LOGI(TAG, "on create: "+this.getId());
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		customerCursorAdapter = new CustomerAdapter(getActivity());
		setListAdapter(customerCursorAdapter);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (customerCursorAdapter != null) {
			customerCursorAdapter.swapCursor(null);
		}
		if (savedInstanceState == null) {
			getLoaderManager().initLoader(0, null, this);
			searchText = (EditText) getActivity().findViewById(R.id.input_search_customers);
			searchText.addTextChangedListener(this);
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.customer_block_status_array, android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner = (Spinner) getActivity().findViewById(R.id.customer_block_status_spinner);
			spinner.setOnItemSelectedListener(this);
			spinner.setAdapter(adapter);
		} else {
			getLoaderManager().restartLoader(0, null, this);
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// This is called when a new Loader needs to be created.  This
        // sample only has one Loader, so we don't care about the ID.
        // First, pick the base URI to use depending on whether we are
        // currently filtering.
        Uri baseUri;
//        String filter = null;
        if (customer_no.equals(DUMMY_SEARCH) && customer_status == -1) {
        	baseUri = MobileStoreContract.Customers.CONTENT_URI;
//        	filter = null;
        } else {
        	baseUri = Customers.buildCustomSearchUri(customer_no, String.valueOf(customer_status));
//        	filter = null;
        }
		
		CursorLoader cursorLoader = new CursorLoader(getActivity(),
				baseUri,
				CustomersQuery.PROJECTION, Customers.IS_ACTIVE+"=?", new String[] { "1" },
				Customers.DEFAULT_SORT);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		if (getActivity() == null) {
			return;
		}
		data.moveToFirst();
		customerCursorAdapter.changeCursor(data);

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		customerCursorAdapter.swapCursor(null);
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
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		activity.getContentResolver().registerContentObserver(MobileStoreContract.Customers.CONTENT_URI, true, mObserver);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		getActivity().getContentResolver().unregisterContentObserver(mObserver);
	}
	
	@Override
	public void afterTextChanged(Editable s) {
		// Called when the action bar search text has changed.  Update
        // the search filter, and restart the loader to do a new query
        // with this filter.
        String customer_no_new = !TextUtils.isEmpty(s.toString()) ? s.toString() : DUMMY_SEARCH;
        // Don't do anything if the filter hasn't actually changed.
        // Prevents restarting the loader when restoring state.
        if (customer_no == null && customer_no_new == null) {
        	customer_no = DUMMY_SEARCH;
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
		
		customer_status = position;
		
		getLoaderManager().restartLoader(0, null, this);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
	private class CustomerAdapter extends CursorAdapter {
		public CustomerAdapter(Context context) {
			super(context, null, false);
		}

		/** {@inheritDoc} */
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return getActivity().getLayoutInflater().inflate(R.layout.list_item_sale_order_block, parent, false);
		}

		/** {@inheritDoc} */
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			final String customer_id = String.valueOf(cursor.getInt(CustomersQuery.ID));
			String customer_no = "NOV KUPAC";
			if (!cursor.isNull(CustomersQuery.CUSTOMER_NO)) {
				customer_no = cursor.getString(CustomersQuery.CUSTOMER_NO);
			}
			final String customer_name = cursor.getString(CustomersQuery.NAME);
			final String customer_phone = cursor.getString(CustomersQuery.PHONE);
			
			final TextView timeView = (TextView) view.findViewById(R.id.block_time);
			final TextView titleView = (TextView) view.findViewById(R.id.block_title);
			final TextView subtitleView = (TextView) view.findViewById(R.id.block_subtitle);
			final View primaryTouchTargetView = view.findViewById(R.id.list_item_middle_container);
			
			primaryTouchTargetView.setOnLongClickListener(null);
			UIUtils.setActivatedCompat(primaryTouchTargetView, false);
			
			titleView.setText(customer_name);
			timeView.setText(customer_no);
			subtitleView.setText(customer_phone);
			
			primaryTouchTargetView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
//					mSelectedCustomerId = customer_id;
					final Uri customerListUri = MobileStoreContract.Customers.CONTENT_URI;
			        final Intent intent = new Intent(Intent.ACTION_VIEW, MobileStoreContract.Customers.buildCustomersUri(customer_id));
			        intent.putExtra(CustomersViewActivity.EXTRA_MASTER_URI, customerListUri);
			        startActivity(intent);
				}
			});
			
			primaryTouchTargetView.setEnabled(true);
		}
	}
	
	private interface CustomersQuery {
		String[] PROJECTION = { BaseColumns._ID, MobileStoreContract.Customers.CUSTOMER_NO, MobileStoreContract.Customers.NAME, MobileStoreContract.Customers.PHONE };

		int ID = 0;
		int CUSTOMER_NO = 1;
		int NAME = 2;
		int PHONE = 3;
	}
	
}
