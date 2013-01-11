package rs.gopro.mobile_store.ui;

import java.util.List;

import rs.gopro.mobile_store.R;


import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class MobileStoreSettingsActivity extends PreferenceActivity {
	
	
	@Override
	public void onBuildHeaders(List<Header> target) {
		loadHeadersFromResource(R.xml.header_settings_main, target);
		
	}

	

}
