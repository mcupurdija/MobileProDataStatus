package rs.gopro.mobile_store.ui.fragment;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.licensing.LicenseData;
import rs.gopro.mobile_store.provider.MobileStoreContract.Licensing;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.util.ApplicationConstants.SyncStatus;
import rs.gopro.mobile_store.util.DateUtils;
import rs.gopro.mobile_store.util.DialogUtil;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.LicensingSyncObject;
import rs.gopro.mobile_store.ws.model.SyncResult;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

public class LicensingFragment extends PreferenceFragment implements OnPreferenceClickListener {

	private Preference activeLicense;
	private Preference lastSyncDate;
	private Preference sync;
	
	private ProgressDialog progressDialog;
	
	protected BroadcastReceiver onNotice = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			SyncResult syncResult = intent.getParcelableExtra(NavisionSyncService.SYNC_RESULT);
			dismissProgressDialog();
			onSOAPResult(syncResult, intent.getAction());
		}
	};
	
	public void onSOAPResult(SyncResult syncResult, String broadcastAction) {
		if (syncResult.getStatus().equals(SyncStatus.SUCCESS)) {
			if (LicensingSyncObject.BROADCAST_SYNC_ACTION.equalsIgnoreCase(broadcastAction)) {
				
				reloadData();
				Toast.makeText(getActivity(), R.string.license_sync_success, Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(getActivity(), R.string.dialog_title_error_in_sync, Toast.LENGTH_LONG).show();
			}
		} else {
			DialogUtil.showInfoErrorDialog(getActivity(), syncResult.getResult());
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.licensing);
		
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		
		activeLicense = (Preference) getPreferenceScreen().findPreference(getString(R.string.key_active_license));
		lastSyncDate = (Preference) getPreferenceScreen().findPreference(getString(R.string.key_last_sync_date));
		sync = (Preference) getPreferenceScreen().findPreference(getString(R.string.key_sync_license));
		
		sync.setOnPreferenceClickListener(this);
		
		reloadData();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(onNotice);
	}

	@Override
	public void onResume() {
		super.onResume();
		IntentFilter licensingSyncObject = new IntentFilter(LicensingSyncObject.BROADCAST_SYNC_ACTION);
    	LocalBroadcastManager.getInstance(getActivity()).registerReceiver(onNotice, licensingSyncObject);
	}
	
	@Override
	public boolean onPreferenceClick(Preference preference) {
		
		showProgressDialog();
		
		Intent intent = new Intent(getActivity(), NavisionSyncService.class);
		LicensingSyncObject licensingSyncObject = new LicensingSyncObject("");
		intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, licensingSyncObject);
		intent.putExtra(NavisionSyncService.EXTRA_WS_ALLOW_SYNC_OBJECT, true);
		getActivity().startService(intent);
		
		return true;
	}
	
	private void reloadData() {
		Cursor cursor = getActivity().getContentResolver().query(Licensing.CONTENT_URI, LicensingQuery.PROJECTION, Tables.LICENSING + "." + Licensing._ID + "=?", new String[] { "1" }, null);
		if (cursor.moveToFirst()) {
			
			String licenseNo = cursor.getString(LicensingQuery.LICENSE_NO);
			if (licenseNo != null && licenseNo.length() > 0) {
				activeLicense.setTitle(licenseNo);
			} else {
				activeLicense.setTitle(R.string.not_available);
			}
			
			try {
				lastSyncDate.setTitle(DateUtils.toUIfromDbDate(cursor.getString(LicensingQuery.LAST_SYNC_DATE)));
			} catch (Exception e) {
				lastSyncDate.setTitle(R.string.not_available);
			}
			
			if (licenseNo != null && licenseNo.length() > 0 && licenseNo.equals(LicenseData.getCurrentSerial())) {
				getActivity().getActionBar().setTitle(R.string.licensing_valid_title);
				getActivity().getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.background_tabs_diagonal_green));
			} else {
				getActivity().getActionBar().setTitle(R.string.licensing_not_valid_title);
				getActivity().getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.background_tabs_diagonal_red));
			}
			
		}
		cursor.close();
	}
	
	public void showProgressDialog() {
		progressDialog = ProgressDialog.show(getActivity(), getString(R.string.please_wait_title), getString(R.string.licence_syncing_title), true);
		progressDialog.setCancelable(false);
	}
	
	public void dismissProgressDialog() {
		if (progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}
	
	private interface LicensingQuery {

		String[] PROJECTION = {
				
			Licensing.LICENSE_NO,
			Licensing.LAST_SYNC_DATE
			
		};

		int LICENSE_NO = 0;
		int LAST_SYNC_DATE = 1;

	}

}
