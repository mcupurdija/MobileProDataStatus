package rs.gopro.mobile_store.ui;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.Items;
import rs.gopro.mobile_store.ui.SaleOrderLinesPreviewListFragment.Callbacks;
import rs.gopro.mobile_store.ui.fragment.ItemPreviewDialogFragment;
import rs.gopro.mobile_store.util.LogUtils;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.ItemQuantitySyncObject;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class ItemsListFragment extends ListFragment implements LoaderCallbacks<Cursor>, TextWatcher, OnItemSelectedListener {
	private static String TAG = "ItemsListFragment";
	private EditText searchText;
	private Spinner spinner;
	private String splitQuerySeparator = ";";
	private CursorAdapter cursorAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtils.LOGI(TAG, "on create: "+this.getId());
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		int[] to = new int[] { android.R.id.empty, R.id.block_time, R.id.block_title };
		cursorAdapter = new SimpleCursorAdapter(getActivity().getApplicationContext(), R.layout.list_item_sale_order_block, null, ItemsQuery.PROJECTION, to, 0);
		cursorAdapter.setFilterQueryProvider(new FilterQueryProvider() {
			@Override
			public Cursor runQuery(CharSequence constraint) {
				String[] queryStrings = constraint.toString().split(splitQuerySeparator);
				Cursor cursor = null;
				if (getActivity() != null) {
					cursor = getActivity().getContentResolver().query(Items.buildCustomSearchUri(queryStrings[0], queryStrings[1]), ItemsQuery.PROJECTION, null, null, MobileStoreContract.Items.DEFAULT_SORT);
				}
				return cursor;
			}
		});
		setListAdapter(cursorAdapter);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (cursorAdapter != null) {
			cursorAdapter.swapCursor(null);
		}
		if (savedInstanceState == null) {
			getLoaderManager().initLoader(0, null, this);
			searchText = (EditText) getActivity().findViewById(R.id.input_search_items);
			searchText.addTextChangedListener(this);
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.item_camp_status_array, android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner = (Spinner) getActivity().findViewById(R.id.items_camp_status_spinner);
			spinner.setOnItemSelectedListener(this);
			spinner.setAdapter(adapter);
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// CursorLoader cursorLoader = new CursorLoader(getActivity(),
		// MobileStoreContract.Items.CONTENT_URI, ItemsQuery.PROJECTION, null,
		// null, MobileStoreContract.Customers.DEFAULT_SORT);
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		cursorAdapter.swapCursor(null);

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		cursorAdapter.swapCursor(null);

	}
	
	@Override
	public void onPause() {
		super.onPause();
		System.out.println("ITEM FRAGMENT IDE U PAUZU");
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public void afterTextChanged(Editable s) {
		cursorAdapter.swapCursor(null);
		int statusId = spinner.getSelectedItemPosition();
		String queryString = s.toString() + splitQuerySeparator + statusId;
		cursorAdapter.getFilter().filter(queryString);
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
		String queryString = searchText.getText().toString() + splitQuerySeparator + position;
		cursorAdapter.getFilter().filter(queryString);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}

	private interface ItemsQuery {
		String[] PROJECTION = { BaseColumns._ID, Items.ITEM_NO, Items.DESCRIPTION, Items.CAMPAIGN_STATUS };
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		final Cursor cursor = (Cursor) cursorAdapter.getItem(position);
        final String itemId = String.valueOf(cursor.getString(1));
	   showDialog(MobileStoreContract.Items.buildItemUri(itemId), itemId); 
	}
	 
	
	private void showDialog(Uri itemUri, String itemId){
		 FragmentTransaction ft =  getActivity().getSupportFragmentManager().beginTransaction();
		    Fragment prev = getFragmentManager().findFragmentByTag("dialog");
		    if (prev != null) {
		        ft.remove(prev);
		    }
		    ft.addToBackStack(null);

		    // Create and show the dialog.
		   ItemPreviewDialogFragment fragment = new ItemPreviewDialogFragment();
		   fragment.setArguments(BaseActivity.intentToFragmentArguments(new Intent(Intent.ACTION_VIEW,itemUri)));
		    fragment.show(ft, "dialog");
	}
	
}
