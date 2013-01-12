package rs.gopro.mobile_store.ui.fragment;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.R.string;
import rs.gopro.mobile_store.util.ApplicationConstants;
import rs.gopro.mobile_store.util.SharedPreferencesUtil;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

public class WebServiceSettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

	EditTextPreference endPointPref;
	EditTextPreference wsSchemaPref;
	EditTextPreference wsNavisionCodeunitPref;
	EditTextPreference wsServerAddress;
	EditTextPreference company;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.ws_settings);
		endPointPref = (EditTextPreference) getPreferenceScreen().findPreference(getString(R.string.key_ws_entry_point));
		wsSchemaPref = (EditTextPreference) getPreferenceScreen().findPreference(getString(R.string.key_ws_schema));
		wsNavisionCodeunitPref = (EditTextPreference) getPreferenceScreen().findPreference(getString(R.string.key_ws_navisition_codeunit));
		wsServerAddress = (EditTextPreference) getPreferenceScreen().findPreference(getString(R.string.key_ws_server_address));
		company = (EditTextPreference) getPreferenceScreen().findPreference(getString(R.string.key_company));
	}

	@Override
	public void onResume() {
		super.onResume();
		String userRole = SharedPreferencesUtil.getUserRole(getActivity().getApplicationContext());
		if (ApplicationConstants.USER_ROLE.USER.name().equalsIgnoreCase(userRole)) {
			endPointPref.setEnabled(false);
			wsSchemaPref.setEnabled(false);
			wsNavisionCodeunitPref.setEnabled(false);
			company.setEnabled(false);
		}
		SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();
		endPointPref.setSummary(getSpannableWithChangedColor(sharedPreferences.getString(getString(R.string.key_ws_entry_point), null)));
		wsSchemaPref.setSummary(getSpannableWithChangedColor(sharedPreferences.getString(getString(R.string.key_ws_schema), null)));
		wsNavisionCodeunitPref.setSummary(getSpannableWithChangedColor(sharedPreferences.getString(getString(R.string.key_ws_navisition_codeunit), null)));
		wsServerAddress.setSummary(getSpannableWithChangedColor(sharedPreferences.getString(getString(R.string.key_ws_server_address), null)));
		company.setSummary(getSpannableWithChangedColor(sharedPreferences.getString(getString(R.string.key_company), null)));

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
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		Preference preference = findPreference(key);
		if (preference instanceof EditTextPreference) {
			EditTextPreference editTextPreference = (EditTextPreference) preference;
			editTextPreference.setSummary(getSpannableWithChangedColor(editTextPreference.getText()));
		}

	}

	private Spannable getSpannableWithChangedColor(String text) {
		Spannable spannable = new SpannableString(text);
		spannable.setSpan(new ForegroundColorSpan(Color.GRAY), 0, spannable.length(), 0);
		return spannable;
	}
}
