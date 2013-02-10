package rs.gopro.mobile_store.ui.customlayout;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.ui.fragment.CustomerFragment;
import rs.gopro.mobile_store.ui.fragment.SentOrdersFragment;
import rs.gopro.mobile_store.util.LogUtils;
import android.app.Activity;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ActionMode.Callback;

public class SentOrdersLayout extends CustomLinearLayout {
	private static String TAG = "SentOrdersLayout";
	private static final String SENT_ORDERS_SCHEME = "settings";
	private static final String SENT_ORDERS_AUTHORITY = "sent_orders";
	public static final Uri SENT_ORDERS_URI = new Uri.Builder().scheme(SENT_ORDERS_SCHEME).authority(SENT_ORDERS_AUTHORITY).build();
	
	private Fragment fragment;
	
	
	public SentOrdersLayout(FragmentManager fragmentManager, Activity activity) {
		super(fragmentManager, activity);
		
	}

	@Override
	protected void inflateLayout(LayoutInflater layoutInflater) {
		if (fragment == null) {
			View view = layoutInflater.inflate(R.layout.content_holder_sent_orders, null);
			this.addView(view);
			FragmentTransaction tr = fragmentManager.beginTransaction();
			fragment = new SentOrdersFragment();
			tr.replace(R.id.sent_orders_content, fragment);
			tr.commit();
			LogUtils.LOGI(TAG, "inflateLayout"+fragment.getId());
		}

	}

	@Override
	public Callback getContextualActionBar(String identifier) {
		// TODO Auto-generated method stub
		return null;
	}

}
