package rs.gopro.mobile_store.ui;

import java.util.List;

import rs.gopro.mobile_store.R;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MobileStoreSettingsActivity extends PreferenceActivity {

	@Override
	public void onBuildHeaders(List<Header> target) {
		loadHeadersFromResource(R.xml.header_settings_main, target);

	}
	
	@Override
	protected boolean isValidFragment(String fragmentName) {
		return true;
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	   switch (item.getItemId()) {
	case android.R.id.home:
		finish();
        return true;
	}
		return super.onOptionsItemSelected(item);
	}

}
