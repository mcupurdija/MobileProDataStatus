package rs.gopro.mobile_store.ui.customlayout;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.ui.fragment.SentOrdersFragment;
import rs.gopro.mobile_store.ui.fragment.SentOrdersStatusMainViewFragment;
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

public class SentOrdersStatusLayout extends CustomLinearLayout {

	private static final String TAG = "SentOrdersStatusLayout";
	
	private static final String SENT_ORDERS_STATUS_SCHEME = "settings";
	private static final String SENT_ORDERS_STATUS_AUTHORITY = "sent_orders_status";
	public static final Uri SENT_ORDERS_STATUS_URI = new Uri.Builder().scheme(SENT_ORDERS_STATUS_SCHEME).authority(SENT_ORDERS_STATUS_AUTHORITY).build();

	private Fragment fragment;
	
	public SentOrdersStatusLayout(Context context) {
		super(context);
	}
	
	public SentOrdersStatusLayout(FragmentManager fragmentManager,
			Activity activity) {
		super(fragmentManager, activity);
	}

	@Override
	protected void inflateLayout(LayoutInflater layoutInflater) {
		if (fragment == null) {
			View view = layoutInflater.inflate(
					R.layout.content_holder_sale_orders_status, null);
			this.addView(view);
			FragmentTransaction tr = fragmentManager.beginTransaction();
			fragment = new SentOrdersStatusMainViewFragment();
			tr.replace(R.id.sent_orders_status_content, fragment);
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
