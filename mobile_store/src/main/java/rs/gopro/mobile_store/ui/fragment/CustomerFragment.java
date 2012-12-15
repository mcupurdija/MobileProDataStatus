package rs.gopro.mobile_store.ui.fragment;

import org.json.JSONObject;
import org.json.JSONStringer;

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
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class CustomerFragment extends ListFragment implements LoaderCallbacks<Cursor>, TextWatcher, OnItemSelectedListener {

	private EditText searchText;
	private Spinner spinner;
	private String splitQuerySeparator = ";";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CursorAdapter cursorAdapter = null;
		int[] to = new int[] { android.R.id.empty, R.id.block_time, R.id.block_title, R.id.block_subtitle };
		Loader<Cursor> loader = getLoaderManager().initLoader(0, null, this);
		cursorAdapter = new SimpleCursorAdapter(getActivity().getApplicationContext(), R.layout.list_item_sale_order_block, null, CustomersQuery.PROJECTION, to, 0);
		cursorAdapter.setFilterQueryProvider(new FilterQueryProvider() {
			@Override
			public Cursor runQuery(CharSequence constraint) {
				String[] queryStrings = constraint.toString().split(splitQuerySeparator);
				Cursor cursor = getActivity().getContentResolver().query(Customers.buildCustomSearchUri(queryStrings[0], queryStrings[1]), CustomersQuery.PROJECTION, null, null, MobileStoreContract.Customers.DEFAULT_SORT);
				return cursor;
			}
		});
		setListAdapter(cursorAdapter);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getListAdapter() != null) {

			((SimpleCursorAdapter) getListView().getAdapter()).swapCursor(null);
		}
		getLoaderManager().initLoader(0, null, this);
		searchText = (EditText) getActivity().findViewById(R.id.input_search_customers);
		searchText.addTextChangedListener(this);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.customer_block_status_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner = (Spinner) getActivity().findViewById(R.id.customer_block_status_spinner);
		spinner.setOnItemSelectedListener(this);
		spinner.setAdapter(adapter);

	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// CursorLoader cursorLoader = new CursorLoader(getActivity(),
		// MobileStoreContract.Customers.CONTENT_URI, CustomersQuery.PROJECTION,
		// null, null, MobileStoreContract.Customers.DEFAULT_SORT);
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		((SimpleCursorAdapter) getListAdapter()).swapCursor(null);

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		((SimpleCursorAdapter) getListAdapter()).changeCursor(null);

	}

	@Override
	public void afterTextChanged(Editable s) {
		ListView listView = getListView();
		SimpleCursorAdapter adapter = (SimpleCursorAdapter) listView.getAdapter();
		adapter.swapCursor(null);
		int statusId = spinner.getSelectedItemPosition();
		String queryString = s.toString() + splitQuerySeparator + statusId;
		adapter.getFilter().filter(queryString);

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		SimpleCursorAdapter adapter = (SimpleCursorAdapter) getListAdapter();
		adapter.swapCursor(null);
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
		ListView listView = getListView();
		SimpleCursorAdapter adapter = (SimpleCursorAdapter) listView.getAdapter();
		adapter.swapCursor(null);
		String queryString = searchText.getText().toString() + splitQuerySeparator + position;
		adapter.getFilter().filter(queryString);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}

	private interface CustomersQuery {
		String[] PROJECTION = { BaseColumns._ID, MobileStoreContract.Customers.NO, MobileStoreContract.Customers.NAME, MobileStoreContract.Customers.PHONE };
	}

}
