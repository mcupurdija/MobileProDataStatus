package rs.gopro.mobile_store.ui.fragment;

import java.util.Date;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.util.DateUtils;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.Preference.OnPreferenceClickListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class SyncSettingsFragment extends PreferenceFragment implements OnPreferenceChangeListener {

	CheckBoxPreference itemSyncCheckBox;

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
		return true;
	}

	
	private void doSync(){
		if(itemSyncCheckBox.isChecked()){
			System.out.println("Sync items");
		}
	}
}
