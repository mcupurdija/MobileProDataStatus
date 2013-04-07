package rs.gopro.mobile_store.ui.customlayout;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.ui.fragment.PlanAndTurnoverFragment;
import rs.gopro.mobile_store.util.LogUtils;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;

public class PlanAndTurnoverLayout extends CustomLinearLayout {

	private static final String TAG = "PlanAndTurnoverLayout";
	
	private static final String PLAN_AND_TURNOVER_SCHEME = "settings";
	private static final String PLAN_AND_TURNOVER_AUTHORITY = "plan_and_turnover";
	public static final Uri PLAN_AND_TURNOVER_URI = new Uri.Builder().scheme(PLAN_AND_TURNOVER_SCHEME).authority(PLAN_AND_TURNOVER_AUTHORITY).build();

	private Fragment fragment;
	
	public PlanAndTurnoverLayout(Context context) {
		super(context);
	}
	
	public PlanAndTurnoverLayout(FragmentManager fragmentManager,
			Activity activity) {
		super(fragmentManager, activity);
	}

	@Override
	protected void inflateLayout(LayoutInflater layoutInflater) {
		if (fragment == null) {
			View view = layoutInflater.inflate(
					R.layout.content_holder_plan_and_turnover, null);
			this.addView(view);
			FragmentTransaction tr = fragmentManager.beginTransaction();
			fragment = new PlanAndTurnoverFragment();
			tr.replace(R.id.plan_and_turnover_content, fragment);
			tr.commit();
			LogUtils.LOGI(TAG, "inflateLayout" + fragment.getId());
		}
	}

	@Override
	public ActionMode.Callback getContextualActionBar(String identifier,
			String visitType) {
		return null;
	}

}
