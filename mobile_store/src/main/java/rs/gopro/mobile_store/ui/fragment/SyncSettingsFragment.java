package rs.gopro.mobile_store.ui.fragment;

import java.util.Date;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract.SyncLogs;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.util.ApplicationConstants.SyncStatus;
import rs.gopro.mobile_store.util.DateUtils;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.ItemsSyncObject;
import rs.gopro.mobile_store.ws.model.SyncResult;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.provider.BaseColumns;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class SyncSettingsFragment extends PreferenceFragment implements OnPreferenceChangeListener, LoaderCallbacks<Cursor> {

	private final int SYNC_ITEM_LOADER = 0;
	
	private CheckBoxPreference itemSyncCheckBox;
	
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.sync_settings);
		itemSyncCheckBox = (CheckBoxPreference) getPreferenceScreen().findPreference(getString(R.string.key_sync_check_box));
		itemSyncCheckBox.setOnPreferenceChangeListener(this);
		itemSyncCheckBox.setIcon(R.drawable.ic_action_refresh);
		setHasOptionsMenu(true);
		getLoaderManager().initLoader(SYNC_ITEM_LOADER, null, this);

	}

	@Override
	public void onResume() {
		super.onResume();
		IntentFilter navSyncFilter = new IntentFilter(ItemsSyncObject.BROADCAST_SYNC_ACTION);
		//registering broadcast receiver to listen BROADCAST_SYNC_ACTION broadcast 
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(onNotice, navSyncFilter);
		//itemSyncCheckBox.setSummary(DateUtils.formatDbDate(new Date()));
		
	}
	
	@Override
	public void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(onNotice);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.sync_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.synchronize_all_items:
			doSync();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (preference instanceof CheckBoxPreference) {
			Boolean status = (Boolean) newValue;
			if (status) {
				doItemsSync();
			}
		}
		return true;
	}

	private void doSync() {
		if (itemSyncCheckBox.isChecked()) {
			doItemsSync();
		}
	}

	
	private BroadcastReceiver onNotice = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			SyncResult syncResult = intent.getParcelableExtra(NavisionSyncService.SYNC_RESULT);
			onSOAPResult(syncResult.getStatus(), syncResult.getResult());
		}
	};
	
	
	private void doItemsSync() {
		Intent intent = new Intent(getActivity(), NavisionSyncService.class);
		ItemsSyncObject itemsSyncObject = new ItemsSyncObject(null, null, Integer.valueOf(1), null, DateUtils.getWsDummyDate());
		intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, itemsSyncObject);
		getActivity().startService(intent);
		
	}

	public void onSOAPResult(SyncStatus syncStatus, String result) {
		if(syncStatus.equals(SyncStatus.SUCCESS)){
			itemSyncCheckBox.setSummary(DateUtils.formatDbDate(new Date()));
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getActivity(), SyncLogs.CONTENT_URI, SyncLogsQuery.PROJECTION, SyncLogs.SYNC_OBJECT_ID + "=?  AND "+SyncLogs.SYNC_OBJECT_BATCH+" = (select MAX("+SyncLogs.SYNC_OBJECT_BATCH+") from "+Tables.SYNC_LOGS+" where "+SyncLogs.SYNC_OBJECT_ID+"= ? AND "+SyncLogs.SYNC_OBJECT_STATUS+"='"+SyncStatus.SUCCESS+"')", new String[] {ItemsSyncObject.TAG, ItemsSyncObject.TAG}, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor data) {
		if(data.moveToNext()){
			String updatedDate = data.getString(SyncLogsQuery.UPDATED_DATE);
			itemSyncCheckBox.setSummary(updatedDate);
		}
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
	
		
	}

	
	public interface SyncLogsQuery{
		String[] PROJECTION  = {
				BaseColumns._ID, 
				SyncLogs.SYNC_OBJECT_NAME, 
				SyncLogs.SYNC_OBJECT_ID, 
				SyncLogs.SYNC_OBJECT_STATUS, 
				SyncLogs.SYNC_OBJECT_BATCH, 
				SyncLogs.CREATED_DATE, 
				SyncLogs.CREATED_BY, 
				SyncLogs.UPDATED_DATE, 
				SyncLogs.UPDATED_BY 
		};
		
		int _ID =  0 ; 
		int SYNC_OBJECT_NAME = 1 ; 
		int SYNC_OBJECT_ID = 2 ; 
		int SYNC_OBJECT_STATUS = 3 ; 
		int SYNC_OBJECT_BATCH = 4 ; 
		int CREATED_DATE = 5 ; 
		int CREATED_BY = 6 ; 
		int UPDATED_DATE = 7 ; 
		int UPDATED_BY = 8 ;
	}
	
}
