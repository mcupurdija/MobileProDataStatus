package rs.gopro.mobile_store.ui;

import java.util.Date;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.ui.fragment.CustomersFromHomeScreenFragment;
import rs.gopro.mobile_store.ui.fragment.VisitListFromHomeScreenFragment;
import rs.gopro.mobile_store.util.DateUtils;
import rs.gopro.mobile_store.util.SharedPreferencesUtil;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HomeScreenActivity extends BaseActivity implements OnClickListener, VisitListFromHomeScreenFragment.Callbacks {
	private static final String TAG = "HomeScreenActivity";
	private static final String SESSION_PREFS = "SessionPrefs";
	public static final Uri VISITS_URI = MobileStoreContract.Visits.buildDateOfVisitUri(DateUtils.formatDbDate(new Date()));

	VisitListFromHomeScreenFragment firstSectionFragment;
	private Fragment customersViewFragmentDetail;
	Button nextButton;

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState
	 *            If the activity is being re-initialized after previously being
	 *            shut down then this Bundle contains the data it most recently
	 *            supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it
	 *            is null.</b>
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "Loga activity created.");
		//load default shared preferences for ws settings
		PreferenceManager.setDefaultValues(this, R.xml.ws_settings,false);
		setContentView(R.layout.activity_home_screen);
		boolean isUserLogged = SharedPreferencesUtil.isUserLoggedIn(getApplicationContext());
		if (!isUserLogged) {
			Intent loginScreen = new Intent(this, LoginActivity.class);
			startActivity(loginScreen);
		}

		final FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fm.beginTransaction();
		firstSectionFragment = (VisitListFromHomeScreenFragment) fm.findFragmentById(R.id.first_section_fragment);
		if (firstSectionFragment == null) {
			firstSectionFragment = new VisitListFromHomeScreenFragment();
			firstSectionFragment.setArguments(BaseActivity.intentToFragmentArguments(new Intent(Intent.ACTION_VIEW, VISITS_URI)));
			fragmentTransaction.add(R.id.first_section_fragment, firstSectionFragment);
		}
		fragmentTransaction.commit();
		updateDetailBackground();
	}

	@Override
	public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
		return super.onCreateView(parent, name, context, attrs);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(SESSION_PREFS);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_menu, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		SharedPreferencesUtil.cleanSessionPrefs(getApplicationContext());

	}

	private void loadCustomersViewDetail(Uri customersDetailUri) {
		CustomersFromHomeScreenFragment fragment = new CustomersFromHomeScreenFragment();
		fragment.setArguments(BaseActivity.intentToFragmentArguments(new Intent(Intent.ACTION_VIEW, customersDetailUri)));
		getSupportFragmentManager().beginTransaction().replace(R.id.second_section_fragment, fragment).commit();
		customersViewFragmentDetail = fragment;
		updateDetailBackground();

	}

	private void updateDetailBackground() {
		if (customersViewFragmentDetail == null) {
			findViewById(R.id.second_section_fragment).setBackgroundResource(R.drawable.grey_frame_on_white_empty_sandbox);
		} else {
			findViewById(R.id.second_section_fragment).setBackgroundResource(R.drawable.grey_frame_on_white);
		}
	}

	@Override
	public boolean onVisitSelected(String customerId) {
		loadCustomersViewDetail(MobileStoreContract.Customers.buildCustomersUri(customerId));
		return true;
	}
}
