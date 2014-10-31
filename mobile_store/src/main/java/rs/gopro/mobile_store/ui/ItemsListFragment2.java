package rs.gopro.mobile_store.ui;

import java.util.UUID;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.Items;
import rs.gopro.mobile_store.ui.fragment.ItemPreviewDialogFragment;
import rs.gopro.mobile_store.util.ApplicationConstants.SyncStatus;
import rs.gopro.mobile_store.util.DialogUtil;
import rs.gopro.mobile_store.util.SharedPreferencesUtil;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.ItemsNewSyncObject;
import rs.gopro.mobile_store.ws.model.ItemsSyncObject;
import rs.gopro.mobile_store.ws.model.SyncResult;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ItemsListFragment2 extends Fragment implements LoaderCallbacks<Cursor> {
	
	//private static String TAG = "ItemsListFragment";
	private static final String WEB_SERVICE_REQUEST_ID = "web_service_request_id";
	
	private String salesPersonNo, itemFilter, webServiceRequestId;
	
	private EditText searchText;
	private Button loadNewItems;
	private ProgressBar pbLoading;

	private CursorAdapter cursorAdapter;
	private ProgressDialog invoicesLoadProgressDialog;
	
	private BroadcastReceiver onNotice = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			SyncResult syncResult = intent.getParcelableExtra(NavisionSyncService.SYNC_RESULT);
			if (invoicesLoadProgressDialog != null && invoicesLoadProgressDialog.isShowing()) {
				invoicesLoadProgressDialog.dismiss();
			}
			// reset request id because it is finished
			webServiceRequestId = null;
			onSOAPResult(syncResult, intent.getAction());
		}
	};
	
	public void onSOAPResult(SyncResult syncResult, String broadcastAction) {
		if (syncResult.getStatus().equals(SyncStatus.SUCCESS)) {
			if (ItemsSyncObject.BROADCAST_SYNC_ACTION.equalsIgnoreCase(broadcastAction)) {
				DialogUtil.showInfoDialog(getActivity(), getResources().getString(R.string.dialog_title_sync_info), "Artikli uspešno ažurirani!");
			}
		} else {
			DialogUtil.showInfoErrorDialog(getActivity(), syncResult.getResult());
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		salesPersonNo = SharedPreferencesUtil.getSalePersonNo(getActivity());
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (savedInstanceState == null) {
			
			pbLoading = (ProgressBar) getActivity().findViewById(R.id.pbLoading);
			
			searchText = (EditText) getActivity().findViewById(R.id.input_search_items);
			searchText.setInputType(InputType.TYPE_CLASS_TEXT);
			searchText.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				}
				
				@Override
				public void afterTextChanged(Editable s) {
					
					itemFilter = s.toString();
					getLoaderManager().restartLoader(0, null, ItemsListFragment2.this);
				}
			});
			
			cursorAdapter = new ItemsAdapter(getActivity());
			ListView listView = (ListView) getActivity().findViewById(R.id.listViewItems);
			listView.setAdapter(cursorAdapter);
			
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					final Cursor cursor = (Cursor) cursorAdapter.getItem(position);
					final String itemNo = String.valueOf(cursor.getString(1));
					showDialog(MobileStoreContract.Items.buildItemNoUri(itemNo), itemNo);
				}
			});
			
			loadNewItems = (Button) getActivity().findViewById(R.id.items_sync_new_button);
			loadNewItems.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), NavisionSyncService.class);
					ItemsNewSyncObject itemsSyncObject = new ItemsNewSyncObject(null, null, Integer.valueOf(-1), salesPersonNo, null);
					itemsSyncObject.setResetTypeSignal(1);
					itemsSyncObject.setSessionId(webServiceRequestId = UUID.randomUUID().toString());
					intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, itemsSyncObject);
					getActivity().startService(intent);
					invoicesLoadProgressDialog = ProgressDialog.show(getActivity(), getActivity().getResources().getString(R.string.dialog_title_items_load), getActivity().getResources().getString(R.string.dialog_body_items_load), true, true);
				}
			});

			getLoaderManager().initLoader(0, null, this);
		} else {
			webServiceRequestId = savedInstanceState.getString(WEB_SERVICE_REQUEST_ID);
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		if (itemFilter != null && itemFilter.length() > 0) {
			return new CursorLoader(getActivity(), Items.buildAutocompleteSearchUri(itemFilter), ItemsQuery.PROJECTION, null, null, Items.DEFAULT_SORT);
		}
		return new CursorLoader(getActivity(), Items.CONTENT_URI, ItemsQuery.PROJECTION, null, null, Items.DEFAULT_SORT);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		cursorAdapter.swapCursor(data);
		pbLoading.setVisibility(View.GONE);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		cursorAdapter.swapCursor(null);
	}
	
	@Override
	public void onResume() {
		super.onResume();

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
	
	private class ItemsAdapter extends CursorAdapter {
		
        public ItemsAdapter(Context context) {
            super(context, null, false);
        }

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater.from(context);
			View v = inflater.inflate(R.layout.list_item_sale_order_block, parent, false);
			bindView(v, context, cursor);
			return v;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			
			TextView itemNo = (TextView) view.findViewById(R.id.block_time);
        	TextView itemDescription = (TextView) view.findViewById(R.id.block_title);
        	
        	itemNo.setText(cursor.getString(1));
        	itemDescription.setText(cursor.getString(2));
		}
	}

	private interface ItemsQuery {
		String[] PROJECTION = { BaseColumns._ID, Items.ITEM_NO, Items.DESCRIPTION };
	}
	
	private void showDialog(Uri itemUri, String itemId) {
		FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
		Fragment prev = getFragmentManager().findFragmentByTag("dialog");
		if (prev != null) {
			ft.remove(prev);
		}
		ft.addToBackStack(null);

		ItemPreviewDialogFragment fragment = new ItemPreviewDialogFragment();
		fragment.setArguments(BaseActivity.intentToFragmentArguments(new Intent(Intent.ACTION_VIEW, itemUri)));
		fragment.show(ft, "dialog");
	}
	
}
