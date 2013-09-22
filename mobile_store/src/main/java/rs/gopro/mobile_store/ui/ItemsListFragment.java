package rs.gopro.mobile_store.ui;

import java.util.UUID;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.Items;
import rs.gopro.mobile_store.provider.MobileStoreContract.SyncLogs;
import rs.gopro.mobile_store.ui.fragment.ItemPreviewDialogFragment;
import rs.gopro.mobile_store.util.ApplicationConstants.SyncStatus;
import rs.gopro.mobile_store.util.DateUtils;
import rs.gopro.mobile_store.util.DialogUtil;
import rs.gopro.mobile_store.util.LogUtils;
import rs.gopro.mobile_store.util.SharedPreferencesUtil;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.ItemsActionSyncObject;
import rs.gopro.mobile_store.ws.model.ItemsNewSyncObject;
import rs.gopro.mobile_store.ws.model.ItemsOverstockSyncObject;
import rs.gopro.mobile_store.ws.model.ItemsSyncObject;
import rs.gopro.mobile_store.ws.model.SyncResult;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.Spinner;

public class ItemsListFragment extends ListFragment implements LoaderCallbacks<Cursor>, TextWatcher, OnItemSelectedListener {
	private static String TAG = "ItemsListFragment";
	private static final String WEB_SERVICE_REQUEST_ID = "web_service_request_id";
	private EditText searchText;
	private Spinner spinner;
	private Button loadNewItems;
	private Button loadOverstock;
	private Button loadOnAction;
	private String splitQuerySeparator = ";";
	private CursorAdapter cursorAdapter;
//	private String salesPersonId;
	private String salesPersonNo;
	private ProgressDialog invoicesLoadProgressDialog;
	private String webServiceRequestId;
	
	private BroadcastReceiver onNotice = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			SyncResult syncResult = intent.getParcelableExtra(NavisionSyncService.SYNC_RESULT);
			invoicesLoadProgressDialog.dismiss();
			// reset request id because it is finished
			webServiceRequestId = null;
			onSOAPResult(syncResult, intent.getAction());
		}
	};
	
	public void onSOAPResult(SyncResult syncResult, String broadcastAction) {
		if (syncResult.getStatus().equals(SyncStatus.SUCCESS)) {
			if (ItemsSyncObject.BROADCAST_SYNC_ACTION.equalsIgnoreCase(broadcastAction)) {
				// TODO some nice info here
//				SalesDocumentsSyncObject syncObject = (SalesDocumentsSyncObject) syncResult.getComplexResult();
				DialogUtil.showInfoDialog(getActivity(), getResources().getString(R.string.dialog_title_sync_info), "Artikli uspešno ažurirani!");
			}
		} else {
			AlertDialog alertDialog = new AlertDialog.Builder(
					getActivity()).create();

		    // Setting Dialog Title
		    alertDialog.setTitle(getResources().getString(R.string.dialog_title_error_in_sync));
		    // Setting Dialog Message
		    alertDialog.setMessage(syncResult.getResult());
		    // Setting Icon to Dialog
		    alertDialog.setIcon(R.drawable.ic_launcher);
		    // Setting OK Button
		    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            	// Write your code here to execute after dialog closed
	            }
		    });
		
		    // Showing Alert Message
		    alertDialog.show();
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtils.LOGI(TAG, "on create: "+this.getId());
//		salesPersonId = SharedPreferencesUtil.getSalePersonId(getActivity());
		salesPersonNo = SharedPreferencesUtil.getSalePersonNo(getActivity());
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
			searchText.setInputType(InputType.TYPE_CLASS_TEXT);
			searchText.addTextChangedListener(this);
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.item_camp_status_array, android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner = (Spinner) getActivity().findViewById(R.id.items_camp_status_spinner);
			spinner.setOnItemSelectedListener(this);
			spinner.setAdapter(adapter);
			
			loadNewItems = (Button) getActivity().findViewById(R.id.items_sync_new_button);
			loadNewItems.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), NavisionSyncService.class);
					ItemsNewSyncObject itemsSyncObject = new ItemsNewSyncObject(null, null, Integer.valueOf(0), salesPersonNo, null);
					itemsSyncObject.setResetTypeSignal(1);
					itemsSyncObject.setSessionId(webServiceRequestId = UUID.randomUUID().toString());
					intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, itemsSyncObject);
					getActivity().startService(intent);
					invoicesLoadProgressDialog = ProgressDialog.show(getActivity(), getActivity().getResources().getString(R.string.dialog_title_items_load), getActivity().getResources().getString(R.string.dialog_body_items_load), true, true);
				}
			});
			
			loadOverstock = (Button) getActivity().findViewById(R.id.items_sync_overstock_button);
			loadOverstock.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), NavisionSyncService.class);
					ItemsOverstockSyncObject itemsSyncObject = new ItemsOverstockSyncObject(null, null, Integer.valueOf(1), salesPersonNo, DateUtils.getWsDummyDate());
					itemsSyncObject.setSessionId(webServiceRequestId = UUID.randomUUID().toString());
					itemsSyncObject.setResetTypeSignal(1);
					intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, itemsSyncObject);
					getActivity().startService(intent);
					invoicesLoadProgressDialog = ProgressDialog.show(getActivity(), getActivity().getResources().getString(R.string.dialog_title_items_load), getActivity().getResources().getString(R.string.dialog_body_items_load), true, true);
				}
			});
			loadOnAction = (Button) getActivity().findViewById(R.id.items_sync_on_action_button);
			loadOnAction.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), NavisionSyncService.class);
					ItemsActionSyncObject itemsSyncObject = new ItemsActionSyncObject(null, null, Integer.valueOf(2), salesPersonNo, DateUtils.getWsDummyDate());
					itemsSyncObject.setResetTypeSignal(2);
					itemsSyncObject.setSessionId(webServiceRequestId = UUID.randomUUID().toString());
					intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, itemsSyncObject);
					getActivity().startService(intent);
					invoicesLoadProgressDialog = ProgressDialog.show(getActivity(), getActivity().getResources().getString(R.string.dialog_title_items_load), getActivity().getResources().getString(R.string.dialog_body_items_load), true, true);
				}
			});
		} else {
			webServiceRequestId = savedInstanceState.getString(WEB_SERVICE_REQUEST_ID);
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
	public void onResume() {
		super.onResume();
		// check is service call is finished or is in process so we can shut down progress bar
		if (webServiceRequestId != null) {
			Cursor syncLog = getActivity().getContentResolver().query(SyncLogs.CONTENT_URI, new String[] { SyncLogs.SYNC_OBJECT_STATUS }, SyncLogs.SYNC_OBJECT_NAME+"=?", new String[] { webServiceRequestId }, null);
			if (syncLog.moveToFirst()) {
				String status = syncLog.getString(0);
				if (invoicesLoadProgressDialog != null && invoicesLoadProgressDialog.isShowing() && !status.equals(SyncStatus.IN_PROCCESS.toString())) {
					invoicesLoadProgressDialog.dismiss();
					if (status.equals(SyncStatus.SUCCESS.toString())) {
						DialogUtil.showInfoDialog(getActivity(), getResources().getString(R.string.dialog_title_sync_info), "Artikli uspešno ažurirani!");
					} else {
						DialogUtil.showInfoDialog(getActivity(), getResources().getString(R.string.dialog_title_error_in_sync), "Artikli nisu uspešno ažurirani!");
					}
				}
			}
		}
		IntentFilter itemsSyncObject = new IntentFilter(ItemsSyncObject.BROADCAST_SYNC_ACTION);
    	LocalBroadcastManager.getInstance(getActivity()).registerReceiver(onNotice, itemsSyncObject);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(onNotice);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(WEB_SERVICE_REQUEST_ID, webServiceRequestId);
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
		final String itemNo = String.valueOf(cursor.getString(1));
		showDialog(MobileStoreContract.Items.buildItemNoUri(itemNo), itemNo);
	}
	 
	
	private void showDialog(Uri itemUri, String itemId) {
		FragmentTransaction ft = getActivity().getSupportFragmentManager()
				.beginTransaction();
		Fragment prev = getFragmentManager().findFragmentByTag("dialog");
		if (prev != null) {
			ft.remove(prev);
		}
		ft.addToBackStack(null);

		// Create and show the dialog.
		ItemPreviewDialogFragment fragment = new ItemPreviewDialogFragment();
		fragment.setArguments(BaseActivity
				.intentToFragmentArguments(new Intent(Intent.ACTION_VIEW,
						itemUri)));
		fragment.show(ft, "dialog");
	}
	
}
