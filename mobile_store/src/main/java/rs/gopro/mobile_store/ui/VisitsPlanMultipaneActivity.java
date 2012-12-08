package rs.gopro.mobile_store.ui;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.ui.customlayout.ShowHideMasterLayout;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public class VisitsPlanMultipaneActivity extends FragmentActivity {

	private Fragment visitsPlanFragmentDetail;
	
	private ShowHideMasterLayout mShowHideMasterLayout;
	
	private boolean mInitialTabSelect = true;
	
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
        	//@TODO needs to handle this

        	visitsPlanFragmentDetail = fm.findFragmentById(R.id.fragment_visitsplan_detail);
            updateDetailBackground();
        }
        
        // This flag prevents onTabSelected from triggering extra master pane reloads
        // unless it's actually being triggered by the user (and not automatically by
        // the system)
        mInitialTabSelect = false;
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
	}
	
	private void updateDetailBackground() {
        if (visitsPlanFragmentDetail == null) {
            findViewById(R.id.fragment_visitsplan_detail).setBackgroundResource(
                    R.drawable.grey_frame_on_white_empty_sandbox);
        } else {
            findViewById(R.id.fragment_visitsplan_detail).setBackgroundResource(
                    R.drawable.grey_frame_on_white);
        }
    }
}
