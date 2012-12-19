package rs.gopro.mobile_store.ui.customlayout;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PlanOfVisitsLayout extends CustomLinearLayout {
	private static final String PLAN_OF_VISITS_SCHEME = "settings";
	private static final String PLAN_OF_VISITS_AUTHORITY = "plan_visits";
	public static final Uri PLAN_OF_VISITS_URI = new Uri.Builder().scheme(PLAN_OF_VISITS_SCHEME).authority(PLAN_OF_VISITS_AUTHORITY).build();

	private Button callMasterDetail;
	private Button callSaleOrders;
	
	public PlanOfVisitsLayout(FragmentManager fragmentManager, Activity activity) {
		super(fragmentManager, activity);
	}

	@Override
	protected void inflateLayout(LayoutInflater layoutInflater) {
		View view = layoutInflater.inflate(R.layout.content_holder_plan_of_visits, null);
		callMasterDetail = (Button) view.findViewById(R.id.plan_of_visits_view_test);
		callMasterDetail.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				 final Uri visitsUri = MobileStoreContract.Visits.CONTENT_URI;
                 final Intent intent = new Intent(Intent.ACTION_VIEW, visitsUri);
                 activity.startActivity(intent);
			}
		});
		callSaleOrders = (Button) view.findViewById(R.id.sale_orders_view_test);
		callSaleOrders.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				 final Uri saleOrdersUri = MobileStoreContract.SaleOrders.buildSaleOrdersListUri("V.MAKEVIC");
                 final Intent intent = new Intent(Intent.ACTION_VIEW, saleOrdersUri);
                 activity.startActivity(intent);
			}
		});
		this.addView(view);

	}
}
