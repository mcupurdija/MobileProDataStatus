package rs.gopro.mobile_store.ui;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.VisitsColumns;
import rs.gopro.mobile_store.ui.customlayout.ShowHideMasterLayout;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class VisitsMultipaneActivity extends BaseActivity implements
		VisitListFragment.Callbacks, VisitDetailFragment.Callbacks {

	public static final String EXTRA_MASTER_URI = "rs.gopro.mobile_store.extra.MASTER_URI";

	private static final String STATE_VIEW_TYPE = "view_type";

	private String mViewType;

	private Fragment visitsPlanFragmentDetail;
	private ShowHideMasterLayout mShowHideMasterLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_visits);
		final FragmentManager fm = getSupportFragmentManager();

		// something about swipe at portrait orientation
		mShowHideMasterLayout = (ShowHideMasterLayout) findViewById(R.id.show_hide_master_layout);
		if (mShowHideMasterLayout != null) {
			mShowHideMasterLayout.setFlingToExposeMasterEnabled(true);
		}

		// routes data from intent that called this activity to business logic
		routeIntent(getIntent(), savedInstanceState != null);

		if (savedInstanceState != null) {
			// @TODO needs to handle this

			visitsPlanFragmentDetail = fm
					.findFragmentById(R.id.fragment_visitsplan_detail);
			updateDetailBackground();
		}
	}

	private void routeIntent(Intent intent, boolean updateSurfaceOnly) {
		// get URI from intent
		Uri uri = intent.getData();
		if (uri == null) {
			return;
		}

		if (intent.hasExtra(Intent.EXTRA_TITLE)) {
			setTitle(intent.getStringExtra(Intent.EXTRA_TITLE));
		}

		String mimeType = getContentResolver().getType(uri);

		if (MobileStoreContract.Visits.CONTENT_TYPE.equals(mimeType)) {
			// Load a session list, hiding the tracks dropdown and the tabs
			if (!updateSurfaceOnly) {
				loadVisitList(uri, null);
				if (mShowHideMasterLayout != null) {
					mShowHideMasterLayout.showMaster(true,
							ShowHideMasterLayout.FLAG_IMMEDIATE);
				}
			}

		} else if (MobileStoreContract.Visits.CONTENT_ITEM_TYPE
				.equals(mimeType)) {
			// Load session details
			if (intent.hasExtra(EXTRA_MASTER_URI)) {
				if (!updateSurfaceOnly) {
					loadVisitList(
							(Uri) intent.getParcelableExtra(EXTRA_MASTER_URI),
							MobileStoreContract.Visits.getVisitId(uri));
					loadVisitDetail(uri);
				}
			} else {
				if (!updateSurfaceOnly) {
					loadVisitDetail(uri);
				}
			}
		}

		updateDetailBackground();
	}

	private void updateDetailBackground() {
		if (visitsPlanFragmentDetail == null) {
			findViewById(R.id.fragment_visitsplan_detail)
					.setBackgroundResource(
							R.drawable.grey_frame_on_white_empty_sandbox);
		} else {
			findViewById(R.id.fragment_visitsplan_detail)
					.setBackgroundResource(R.drawable.grey_frame_on_white);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (mShowHideMasterLayout != null
					&& !mShowHideMasterLayout.isMasterVisible()) {
				// If showing the detail view, pressing Up should show the
				// master pane.
				mShowHideMasterLayout.showMaster(true, 0);
				return true;
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	private void loadVisitList(Uri visitsUri, String selectVisitId) {
		VisitListFragment fragment = new VisitListFragment();
		fragment.setSelectedVisitId(selectVisitId);
		fragment.setArguments(BaseActivity
				.intentToFragmentArguments(new Intent(Intent.ACTION_VIEW,
						visitsUri)));
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment_visitsplan_list, fragment).commit();
	}

	private void loadVisitDetail(Uri visitUri) {
		VisitDetailFragment fragment = new VisitDetailFragment();
		fragment.setArguments(BaseActivity
				.intentToFragmentArguments(new Intent(Intent.ACTION_VIEW,
						visitUri)));
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment_visitsplan_detail, fragment).commit();
		visitsPlanFragmentDetail = fragment;
		updateDetailBackground();

		// If loading session details in portrait, hide the master pane
		if (mShowHideMasterLayout != null) {
			mShowHideMasterLayout.showMaster(false, 0);
		}
	}

	@Override
	public boolean onVisitSelected(String visitsId) {
		loadVisitDetail(MobileStoreContract.Visits.buildVisitUri(visitsId));
		return true;
	}

	@Override
	public void onVisitIdAvailable(String visitId) {
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater menuInflater = getMenuInflater();
	    menuInflater.inflate(R.menu.plan_of_visits, menu);
		return super.onCreateOptionsMenu(menu);
	}
}
