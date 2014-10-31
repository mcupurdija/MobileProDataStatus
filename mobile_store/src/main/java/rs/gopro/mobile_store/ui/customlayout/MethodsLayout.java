package rs.gopro.mobile_store.ui.customlayout;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.ui.fragment.MethodsFragment;
import rs.gopro.mobile_store.ui.widget.MethodContextualActionBar;
import rs.gopro.mobile_store.util.LogUtils;
import android.app.Activity;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ActionMode.Callback;
import android.view.LayoutInflater;
import android.view.View;

public class MethodsLayout extends CustomLinearLayout {
	private static String TAG = "MethodsLayout";
	private static final String METHODS_SCHEME = "settings";
	private static final String METHODS_AUTHORITY = "methods";
	public static final Uri METHODS_URI = new Uri.Builder().scheme(METHODS_SCHEME).authority(METHODS_AUTHORITY).build();
	private Fragment fragment;

	public MethodsLayout(FragmentManager fragmentManager, Activity activity) {
		super(fragmentManager, activity);
	}

	@Override
	protected void inflateLayout(LayoutInflater layoutInflater) {
		if(fragment == null){
			View view = layoutInflater.inflate(R.layout.content_holder_methods, null);
			this.addView(view);
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			fragment = new MethodsFragment();
			transaction.replace(R.id.methods_content, fragment);
			transaction.commit();
			LogUtils.LOGI(TAG, "inflateLayout"+fragment.getId());
		}
	}

	@Override
	public Callback getContextualActionBar(String identifier, String type) {
		return new MethodContextualActionBar(activity, identifier);
	}

}
