package rs.gopro.mobile_store.ui.customlayout;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.ui.BaseActivity;
import rs.gopro.mobile_store.ui.VisitListFromMenuFragment;
import rs.gopro.mobile_store.ui.widget.VisitContextualMenu;
import rs.gopro.mobile_store.util.LogUtils;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;

public class PlanOfVisitsLayout extends CustomLinearLayout {
	private static String TAG = "PlanOfVisitsLayout";
	public static final Uri VISITS_URI = MobileStoreContract.Visits.CONTENT_URI;
	private static final String PLAN_OF_VISITS_SCHEME = "settings";
	private static final String PLAN_OF_VISITS_AUTHORITY = "plan_visits";
	public static final Uri PLAN_OF_VISITS_URI = new Uri.Builder().scheme(PLAN_OF_VISITS_SCHEME).authority(PLAN_OF_VISITS_AUTHORITY).build();

	private Fragment fragment;

	
	public PlanOfVisitsLayout(FragmentManager fragmentManager, Activity activity) {
		super(fragmentManager, activity);
	}

	@Override
	protected void inflateLayout(LayoutInflater layoutInflater) {
		if (fragment == null) {
			View view = layoutInflater.inflate(R.layout.content_holder_plan_of_visits, null);
			this.addView(view);
			FragmentTransaction tr = fragmentManager.beginTransaction();
			fragment = new VisitListFromMenuFragment();
			fragment.setArguments(BaseActivity
					.intentToFragmentArguments(new Intent(Intent.ACTION_VIEW,
							VISITS_URI)));
			tr.replace(R.id.plan_of_visits_content, fragment);
			tr.commit();
			LogUtils.LOGI(TAG, "inflateLayout"+fragment.getId());
		}
	}

	@Override
	public ActionMode.Callback getContextualActionBar(String identifier, String visitType) {
		
		return new VisitContextualMenu(activity, identifier, visitType);
	}
	
//	View view = layoutInflater.inflate(R.layout.content_holder_plan_of_visits, null);
//	callMasterDetail = (Button) view.findViewById(R.id.plan_of_visits_view_test);
//	callMasterDetail.setOnClickListener(new OnClickListener() {			
//		@Override
//		public void onClick(View v) {
//			 final Uri visitsUri = MobileStoreContract.Visits.CONTENT_URI;
//             final Intent intent = new Intent(Intent.ACTION_VIEW, visitsUri);
//             activity.startActivity(intent);
//		}
//	});
//	callSaleOrders = (Button) view.findViewById(R.id.sale_orders_view_test);
//	callSaleOrders.setOnClickListener(new OnClickListener() {			
//		@Override
//		public void onClick(View v) {
//			 final Uri saleOrdersUri = MobileStoreContract.SaleOrders.buildSaleOrdersListUri("1");
//             final Intent intent = new Intent(Intent.ACTION_VIEW, saleOrdersUri);
//             activity.startActivity(intent);
//		}
//	});
//	this.addView(view);
}
