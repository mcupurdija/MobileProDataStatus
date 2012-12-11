package rs.gopro.mobile_store.ui.fragment;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.provider.MobileStoreContract.Users;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.ui.LoginActivity.UsersQuery;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;

public class CustomerFragment extends ListFragment implements LoaderCallbacks<Cursor>, TextWatcher {

	SimpleCursorAdapter cursorAdapter;
	EditText searchText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int[] to = new int[] { android.R.id.empty, R.id.block_time, R.id.block_title, R.id.block_subtitle };
		Loader<Cursor> loader = getLoaderManager().initLoader(0, null, this);
		cursorAdapter = new SimpleCursorAdapter(getActivity().getApplicationContext(), R.layout.list_item_sale_order_block, null, CustomersQuery.PROJECTION, to, 0);
		cursorAdapter.setFilterQueryProvider(new FilterQueryProvider() {
			@Override
			public Cursor runQuery(CharSequence constraint) {
				// Cursor cursor =
				// getActivity().getContentResolver().query(Customers.buildNoUri(),
				// CustomersQuery.PROJECTION, Customers.NO + " like ? " , new
				// String[] {"%" + constraint.toString() + "%"},
				// MobileStoreContract.Customers.DEFAULT_SORT);
				Cursor cursor = getActivity().getContentResolver().query(Customers.buildSearchUri(constraint.toString()), CustomersQuery.PROJECTION, null, null, MobileStoreContract.Customers.DEFAULT_SORT);

				return cursor;
			}
		});

		setListAdapter(cursorAdapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = super.onCreateView(inflater, container, savedInstanceState);
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getLoaderManager().initLoader(0, null, this);
		searchText = (EditText) getActivity().findViewById(R.id.input_search_customers);
		searchText.addTextChangedListener(this);

	}

	@Override
	public void onPause() {
		super.onPause();

	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		CursorLoader cursorLoader = new CursorLoader(getActivity(), MobileStoreContract.Customers.CONTENT_URI, CustomersQuery.PROJECTION, null, null, MobileStoreContract.Customers.DEFAULT_SORT);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		cursorAdapter.swapCursor(data);

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		cursorAdapter.swapCursor(null);

	}

	private interface CustomersQuery {

		String[] PROJECTION = { BaseColumns._ID, MobileStoreContract.Customers.NO, MobileStoreContract.Customers.NAME, MobileStoreContract.Customers.PHONE

		};

		int _ID = 0;
		int NO = 1;
		int NAME = 2;
		int PHONE = 7;
	}

	@Override
	public void afterTextChanged(Editable s) {
		ListView listView = getListView();
		SimpleCursorAdapter adapter = (SimpleCursorAdapter) listView.getAdapter();
		adapter.swapCursor(null);
		adapter.getFilter().filter(s.toString());
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub

	}

}
