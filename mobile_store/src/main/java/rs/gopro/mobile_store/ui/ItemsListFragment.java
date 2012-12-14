package rs.gopro.mobile_store.ui;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.provider.MobileStoreContract.Items;

import android.content.ClipData.Item;
import android.database.Cursor;
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

public class ItemsListFragment extends ListFragment implements LoaderCallbacks<Cursor>, TextWatcher, OnItemSelectedListener {

	EditText searchText;
	Spinner spinner;
	String splitQuerySeparator = ";";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CursorAdapter cursorAdapter = null;
		int[] to = new int[] { android.R.id.empty, R.id.block_time, R.id.block_title/*, R.id.block_subtitle*/ };
		getLoaderManager().initLoader(0, null, this);
		cursorAdapter = new SimpleCursorAdapter(getActivity().getApplicationContext(), R.layout.list_item_sale_order_block, null, ItemsQuery.PROJECTION, to, 0);
		cursorAdapter.setFilterQueryProvider(new FilterQueryProvider() {
			@Override
			public Cursor runQuery(CharSequence constraint) {
				System.out.println("RUN QUERY");
				String[] queryStrings = constraint.toString().split(splitQuerySeparator);
				Cursor cursor = getActivity().getContentResolver().query(Items.buildCustomSearchUri(queryStrings[0], queryStrings[1]), ItemsQuery.PROJECTION, null, null, MobileStoreContract.Customers.DEFAULT_SORT);
				return cursor;
			}
		});
		setListAdapter(cursorAdapter);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getListAdapter() != null) {
			((SimpleCursorAdapter) getListView().getAdapter()).swapCursor(null);
		}
		getLoaderManager().initLoader(0, null, this);
		searchText = (EditText) getActivity().findViewById(R.id.input_search_items);
		searchText.addTextChangedListener(this);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.item_camp_status_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner = (Spinner) getActivity().findViewById(R.id.items_camp_status_spinner);
		spinner.setOnItemSelectedListener(this);
		spinner.setAdapter(adapter);

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {

		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		CursorLoader cursorLoader = new CursorLoader(getActivity(), MobileStoreContract.Items.CONTENT_URI, ItemsQuery.PROJECTION, null, null, MobileStoreContract.Customers.DEFAULT_SORT);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		((SimpleCursorAdapter) getListAdapter()).swapCursor(null);

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		((SimpleCursorAdapter) getListAdapter()).swapCursor(null);

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
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

	private interface ItemsQuery {
		String[] PROJECTION = { BaseColumns._ID, Items.NO, Items.DESCRIPTION, Items.CAMPAIGN_STATUS };
	}

}
