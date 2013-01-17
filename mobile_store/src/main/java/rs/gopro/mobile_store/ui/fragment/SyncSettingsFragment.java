package rs.gopro.mobile_store.ui.fragment;

import java.util.Date;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.util.ApplicationConstants;
import rs.gopro.mobile_store.util.DateUtils;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.ItemsSyncObject;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.Preference.OnPreferenceClickListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class SyncSettingsFragment extends PreferenceFragment implements OnPreferenceChangeListener {

	CheckBoxPreference itemSyncCheckBox;
	ProgressBar syncItemsProgresbar;
	private ResultReceiver mReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.sync_settings);
		itemSyncCheckBox = (CheckBoxPreference) getPreferenceScreen().findPreference(getString(R.string.key_sync_check_box));
		itemSyncCheckBox.setOnPreferenceChangeListener(this);
		itemSyncCheckBox.setIcon(R.drawable.ic_action_refresh);

		setHasOptionsMenu(true);

	}

	@Override
	public void onResume() {
		super.onResume();
		itemSyncCheckBox.setSummary(DateUtils.formatDbDate(new Date()));
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

	private void doItemsSync() {
		mReceiver = new ResultReceiver(new Handler()) {

			@Override
			protected void onReceiveResult(int resultCode, Bundle resultData) {
				if (resultCode == ApplicationConstants.SUCCESS) {
					if (resultData != null) {
						onSOAPResult(resultCode, resultData.getString(NavisionSyncService.SOAP_RESULT));
					} else {
						onSOAPResult(resultCode, null);
					}
				} else if (resultCode == ApplicationConstants.FAILURE) {
					if (resultData != null) {
						onSOAPResult(resultCode, resultData.getString(NavisionSyncService.SOAP_FAULT));
					} else {
						onSOAPResult(resultCode, null);
					}
				}

			}

		};
		Intent intent = new Intent(getActivity(), NavisionSyncService.class);
		ItemsSyncObject itemsSyncObject = new ItemsSyncObject(null, null, Integer.valueOf(1), null, DateUtils.getWsDummyDate());
		intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, itemsSyncObject);
		intent.putExtra(NavisionSyncService.EXTRA_RESULT_RECEIVER, getResultReceiver());
		getActivity().startService(intent);

	}

	public ResultReceiver getResultReceiver() {
		return mReceiver;
	}

	public void onSOAPResult(int code, String result) {
		System.out.println(result);
		//TODO Change negation in if statement
		if(ApplicationConstants.SUCCESS != code){
			itemSyncCheckBox.setSummary(DateUtils.formatDbDate(new Date()));
		}
	}
}
