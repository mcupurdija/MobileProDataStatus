package rs.gopro.mobile_store.ui.customlayout;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.ui.BaseActivity;
import rs.gopro.mobile_store.ui.RecordVisitsMultipaneActivity;
import rs.gopro.mobile_store.ui.fragment.RecordVisitListFromMenuFragment;
import rs.gopro.mobile_store.util.LogUtils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;

public class RecordVisitsLayout extends CustomLinearLayout {
	private static String TAG = "RecordVisitsLayout";
	public static final Uri VISITS_URI = MobileStoreContract.Visits.CONTENT_URI;
	private static final String RECORD_VISITS_SCHEME = "settings";
	private static final String RECORD_VISITS_AUTHORITY = "record_visits";
	public static final Uri RECORD_VISITS_URI = new Uri.Builder().scheme(RECORD_VISITS_SCHEME).authority(RECORD_VISITS_AUTHORITY).build();

	private Fragment fragment;

	public RecordVisitsLayout(Context context) {
		super(context);
	}
	
	public RecordVisitsLayout(FragmentManager fragmentManager, Activity activity) {
		super(fragmentManager, activity);
	}

	@Override
	protected void inflateLayout(LayoutInflater layoutInflater) {
		if (fragment == null) {
			View view = layoutInflater.inflate(R.layout.content_holder_record_visits, null);
			this.addView(view);
			FragmentTransaction tr = fragmentManager.beginTransaction();
			fragment = new RecordVisitListFromMenuFragment();
			Intent recordVisit = new Intent(RecordVisitsMultipaneActivity.RECORD_VISITS_INTENT, VISITS_URI); 
			fragment.setArguments(BaseActivity
					.intentToFragmentArguments(recordVisit));
			tr.replace(R.id.record_visits_content, fragment);
			tr.commit();
			LogUtils.LOGI(TAG, "inflateLayout"+fragment.getId());
		}
	}

	@Override
	public ActionMode.Callback getContextualActionBar(String identifier, String visitType) {
		//return new VisitContextualMenu(activity, identifier, visitType);
		return null;
	}
}
