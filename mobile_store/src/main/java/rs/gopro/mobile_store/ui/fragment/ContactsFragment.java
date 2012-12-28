package rs.gopro.mobile_store.ui.fragment;

import jcifs.dcerpc.msrpc.netdfs;
import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.Contacts;
import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.util.LogUtils;
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
import android.view.View;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;

public class ContactsFragment extends ListFragment implements LoaderCallbacks<Cursor>, TextWatcher {

	private static String TAG = "CustomerFragment";
	private EditText searchText;
	private CursorAdapter cursorAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtils.LOGI(TAG, "on create: " + this.getId());
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getLoaderManager().initLoader(0, null, this);
		int[] to = new int[] { android.R.id.empty, R.id.block_time, R.id.block_title, R.id.block_subtitle };
		cursorAdapter = new SimpleCursorAdapter(getActivity().getApplicationContext(), R.layout.list_item_sale_order_block, null, ContactsQuery.PROJECTION, to, 0);
		cursorAdapter.setFilterQueryProvider(new FilterQueryProvider() {
			@Override
			public Cursor runQuery(CharSequence constraint) {
				Cursor cursor = null;
				if (getActivity() != null) {
					cursor = getActivity().getContentResolver().query(Contacts.buildCustomSearchUri(constraint.toString()), ContactsQuery.PROJECTION, null, null, MobileStoreContract.Contacts.DEFAULT_SORT);
				}
				return cursor;
			}
		});
		System.out.println("ADAPTER SE SETUJE");
		setListAdapter(cursorAdapter);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState == null) {
			searchText = (EditText) getActivity().findViewById(R.id.input_search_contacts);
			searchText.addTextChangedListener(this);
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		CursorLoader cursorLoader = new CursorLoader(getActivity(), MobileStoreContract.Contacts.CONTENT_URI, ContactsQuery.PROJECTION, null, null, MobileStoreContract.Contacts.DEFAULT_SORT);
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

	private interface ContactsQuery {
		String[] PROJECTION = { BaseColumns._ID, MobileStoreContract.Contacts.CONTACT_NO, MobileStoreContract.Contacts.NAME, MobileStoreContract.Contacts.PHONE };
	}

	@Override
	public void afterTextChanged(Editable s) {
		cursorAdapter.swapCursor(null);
		cursorAdapter.getFilter().filter(s.toString());

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
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		System.out.println("Onclick");
	}

}
