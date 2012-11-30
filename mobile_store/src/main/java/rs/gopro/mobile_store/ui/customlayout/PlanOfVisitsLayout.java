package rs.gopro.mobile_store.ui.customlayout;

import rs.gopro.mobile_store.R;
import android.app.Activity;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class PlanOfVisitsLayout extends CustomLinearLayout {
	private static final String PLAN_OF_VISITS_SCHEME = "settings";
	private static final String PLAN_OF_VISITS_AUTHORITY = "plan_visits";
	public static final Uri PLAN_OF_VISITS_URI = new Uri.Builder().scheme(PLAN_OF_VISITS_SCHEME).authority(PLAN_OF_VISITS_AUTHORITY).build();

	public PlanOfVisitsLayout(FragmentManager fragmentManager, Activity activity) {
		super(fragmentManager, activity);
	}

	@Override
	protected void inflateLayout(LayoutInflater layoutInflater) {
		View view = layoutInflater.inflate(R.layout.content_holder_plan_of_visits, null);
		this.addView(view);

	}
}
