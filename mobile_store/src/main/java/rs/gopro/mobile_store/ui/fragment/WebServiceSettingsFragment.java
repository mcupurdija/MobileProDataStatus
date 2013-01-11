package rs.gopro.mobile_store.ui.fragment;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.util.ApplicationConstants;
import rs.gopro.mobile_store.util.SharedPreferencesUtil;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

public class WebServiceSettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

	EditTextPreference editTextPreference;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.ws_settings);

		editTextPreference = (EditTextPreference) getPreferenceScreen().findPreference("ws_entry_point");

	}

	@Override
	public void onResume() {
		super.onResume();
		String userRole = SharedPreferencesUtil.getUserRole(getActivity().getApplicationContext());
		if (ApplicationConstants.USER_ROLE.USER.name().equalsIgnoreCase(userRole)) {
			editTextPreference.setEnabled(false);
		}
		// Set up a listener whenever a key changes
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		// Unregister the listener whenever a key changes
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {

		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		Preference preference = findPreference(key);
		System.out.println("xxx");
		if (preference instanceof EditTextPreference) {
			System.out.println("USAO");
			EditTextPreference editTextPreference = (EditTextPreference) preference;
			editTextPreference.setSummary(editTextPreference.getText());
		}

	}
}
