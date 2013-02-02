package rs.gopro.mobile_store.ui.fragment;

import java.util.Date;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract.SyncLogs;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.util.ApplicationConstants.SyncStatus;
import rs.gopro.mobile_store.util.DateUtils;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.ItemsSyncObject;
import rs.gopro.mobile_store.ws.model.PlannedVisitsToCustomersSyncObject;
import rs.gopro.mobile_store.ws.model.SyncResult;
import android.app.LoaderManager;
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
	private final int SYNC_PLANNED_VISIT_LOADER = 1;

	private CheckBoxPreference itemSyncCheckBox;
	private CheckBoxPreference plannedVisitSyncCheckBox;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.sync_settings);
		itemSyncCheckBox = (CheckBoxPreference) getPreferenceScreen().findPreference(getString(R.string.key_sync_check_box));
		itemSyncCheckBox.setOnPreferenceChangeListener(this);
		itemSyncCheckBox.setIcon(R.drawable.ic_action_refresh);
		plannedVisitSyncCheckBox = (CheckBoxPreference) getPreferenceScreen().findPreference(getString(R.string.key_sync_planned_visits_check_box));
		plannedVisitSyncCheckBox.setOnPreferenceChangeListener(this);

		setHasOptionsMenu(true);
		getLoaderManager().initLoader(SYNC_ITEM_LOADER, null, this);
		getLoaderManager().initLoader(SYNC_PLANNED_VISIT_LOADER, null, this);
	}

	@Override
	public void onResume() {
		super.onResume();
		IntentFilter navSyncFilter = new IntentFilter(ItemsSyncObject.BROADCAST_SYNC_ACTION);
		// registering broadcast receiver to listen BROADCAST_SYNC_ACTION
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(onNotice, navSyncFilter);

		IntentFilter plannedVisitSyncIntent = new IntentFilter(PlannedVisitsToCustomersSyncObject.BROADCAST_SYNC_ACTION);
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(onNotice, plannedVisitSyncIntent);
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
			syncAllActive();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (preference instanceof CheckBoxPreference) {
			Boolean status = (Boolean) newValue;
			if (status) {
				if (getString(R.string.key_sync_check_box).equalsIgnoreCase(preference.getKey())) {
					doItemsSync();
				} else if (getString(R.string.key_sync_planned_visits_check_box).equalsIgnoreCase(preference.getKey())) {
					doPlannedVisitSync();
				}
			}
		}
		return true;
	}

	public void syncAllActive() {
		if (itemSyncCheckBox.isChecked()) {
			doItemsSync();
		}
		if (plannedVisitSyncCheckBox.isChecked()) {
			doPlannedVisitSync();
		}
	}

	private BroadcastReceiver onNotice = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			SyncResult syncResult = intent.getParcelableExtra(NavisionSyncService.SYNC_RESULT);
			onSOAPResult(syncResult.getStatus(), syncResult.getResult(), intent.getAction());
		}
	};

	private void doPlannedVisitSync() {
		Intent intent = new Intent(getActivity(), NavisionSyncService.class);
		PlannedVisitsToCustomersSyncObject plannedVisitsToCustomersSyncObject = new PlannedVisitsToCustomersSyncObject("", "V.MAKEVIC", DateUtils.getWsDummyDate(), DateUtils.getWsDummyDate(), "");
		intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, plannedVisitsToCustomersSyncObject);
		getActivity().startService(intent);
	}

	private void doItemsSync() {
		Intent intent = new Intent(getActivity(), NavisionSyncService.class);
		ItemsSyncObject itemsSyncObject = new ItemsSyncObject(null, null, Integer.valueOf(1), null, DateUtils.getWsDummyDate());
		intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, itemsSyncObject);
		getActivity().startService(intent);

	}

	public void onSOAPResult(SyncStatus syncStatus, String result, String broadcastAction) {
		System.out.println("STATUS IS: " + syncStatus);
		if (syncStatus.equals(SyncStatus.SUCCESS)) {
			if (ItemsSyncObject.BROADCAST_SYNC_ACTION.equalsIgnoreCase(broadcastAction)) {
				itemSyncCheckBox.setSummary(getString(R.string.sync_summary_label) + " " + DateUtils.formatDbDate(new Date()));
			} else if (PlannedVisitsToCustomersSyncObject.BROADCAST_SYNC_ACTION.equalsIgnoreCase(broadcastAction)) {
				plannedVisitSyncCheckBox.setSummary(getString(R.string.sync_summary_label) + " " + DateUtils.formatDbDate(new Date()));
			}
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		switch (id) {
		case SYNC_ITEM_LOADER:
			return new CursorLoader(getActivity(), SyncLogs.CONTENT_URI, SyncLogsQuery.PROJECTION, SyncLogs.SYNC_OBJECT_ID + "=?  AND " + SyncLogs.SYNC_OBJECT_BATCH + " = (select MAX(" + SyncLogs.SYNC_OBJECT_BATCH + ") from "
					+ Tables.SYNC_LOGS + " where " + SyncLogs.SYNC_OBJECT_ID + "= ? AND " + SyncLogs.SYNC_OBJECT_STATUS + "='" + SyncStatus.SUCCESS + "')", new String[] { ItemsSyncObject.TAG, ItemsSyncObject.TAG }, null);

		case SYNC_PLANNED_VISIT_LOADER:
			return new CursorLoader(getActivity(), SyncLogs.CONTENT_URI, SyncLogsQuery.PROJECTION, SyncLogs.SYNC_OBJECT_ID + "=?  AND " + SyncLogs.SYNC_OBJECT_BATCH + " = (select MAX(" + SyncLogs.SYNC_OBJECT_BATCH + ") from "
					+ Tables.SYNC_LOGS + " where " + SyncLogs.SYNC_OBJECT_ID + "= ? AND " + SyncLogs.SYNC_OBJECT_STATUS + "='" + SyncStatus.SUCCESS + "')", new String[] { PlannedVisitsToCustomersSyncObject.TAG,
					PlannedVisitsToCustomersSyncObject.TAG }, null);

		default:
			return null;
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		if (data.moveToNext()) {
			String updatedDate = data.getString(SyncLogsQuery.UPDATED_DATE);
			switch (loader.getId()) {
			case SYNC_ITEM_LOADER:
				itemSyncCheckBox.setSummary(getString(R.string.sync_summary_label) + " " + updatedDate);
				break;
			case SYNC_PLANNED_VISIT_LOADER:
				plannedVisitSyncCheckBox.setSummary(getString(R.string.sync_summary_label) + " " + updatedDate);
			default:
				break;
			}

		}

	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {

	}

	public interface SyncLogsQuery {
		String[] PROJECTION = { BaseColumns._ID, SyncLogs.SYNC_OBJECT_NAME, SyncLogs.SYNC_OBJECT_ID, SyncLogs.SYNC_OBJECT_STATUS, SyncLogs.SYNC_OBJECT_BATCH, SyncLogs.CREATED_DATE, SyncLogs.CREATED_BY, SyncLogs.UPDATED_DATE,
				SyncLogs.UPDATED_BY };

		int _ID = 0;
		int SYNC_OBJECT_NAME = 1;
		int SYNC_OBJECT_ID = 2;
		int SYNC_OBJECT_STATUS = 3;
		int SYNC_OBJECT_BATCH = 4;
		int CREATED_DATE = 5;
		int CREATED_BY = 6;
		int UPDATED_DATE = 7;
		int UPDATED_BY = 8;
	}

}
