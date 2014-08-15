package rs.gopro.mobile_store.ui.fragment;

import java.io.File;

import rs.gopro.mobile_store.R;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.Preference.OnPreferenceClickListener;
import android.widget.Toast;

public class NovaKarticaKupcaSettingsFragment extends PreferenceFragment {

	CheckBoxPreference nkkSwitch;
	CheckBoxPreference ksaSwitch;
	Preference saButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.nova_kartica_kupca_settings);

		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

		nkkSwitch = (CheckBoxPreference) getPreferenceScreen().findPreference(getString(R.string.key_nkk_switch));
		
		ksaSwitch = (CheckBoxPreference) getPreferenceScreen().findPreference(getString(R.string.key_ksa_switch));
		
		saButton = (Preference) getPreferenceScreen().findPreference(getString(R.string.key_sa_button));
		saButton.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				try {
					String cacheFolderPath = getActivity().getCacheDir().getPath() + "/aquery";
					File cacheFolder = new File(cacheFolderPath);
					if (cacheFolder.exists()) {
						File[] cachedImages = cacheFolder.listFiles();
						for (File file : cachedImages) {
							file.delete();
						}
						Toast.makeText(getActivity(), "Slike uspešno obrisane", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(getActivity(), "Nema keširanih slika", Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
				}
				return true;
			}
		});
	}
}
