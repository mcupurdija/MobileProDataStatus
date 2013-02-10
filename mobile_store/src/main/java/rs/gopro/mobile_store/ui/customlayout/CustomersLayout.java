package rs.gopro.mobile_store.ui.customlayout;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.ui.fragment.CustomerFragment;
import rs.gopro.mobile_store.util.DateUtils;
import rs.gopro.mobile_store.util.LogUtils;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.CustomerSyncObject;
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

public class CustomersLayout extends CustomLinearLayout {
	private static String TAG = "CustomersLayout";
	private static final String CUSTOMERS_SCHEME = "settings";
	private static final String CUSTOMERS_AUTHORITY = "customers";
	public static final Uri CUSTOMERS_URI = new Uri.Builder().scheme(CUSTOMERS_SCHEME).authority(CUSTOMERS_AUTHORITY).build();
	private Fragment fragment;
	
	public CustomersLayout(Context context) {
		super(context);
	}
	
	public CustomersLayout(FragmentManager fragmentManager, Activity activity) {
		super(fragmentManager, activity);
	}

	@Override
	protected void inflateLayout(LayoutInflater layoutInflater) {
		if (fragment == null) {
			View view = layoutInflater.inflate(R.layout.content_holder_customers, null);
			this.addView(view);
			FragmentTransaction tr = fragmentManager.beginTransaction();
			fragment = new CustomerFragment();
			tr.replace(R.id.customers_content, fragment);
			tr.commit();
			LogUtils.LOGI(TAG, "inflateLayout"+fragment.getId());
		}
	}

	@Override
	public ActionMode.Callback getContextualActionBar(String identifier) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void doSynchronization() {
		Intent intent = new Intent(activity, NavisionSyncService.class);
		CustomerSyncObject syncObject = new CustomerSyncObject("", "","", DateUtils.getWsDummyDate());
		intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT,syncObject);
		activity.startService(intent);
	}
}
