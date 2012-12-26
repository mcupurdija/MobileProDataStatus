package rs.gopro.mobile_store.ui.customlayout;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.ui.fragment.CustomerFragment;
import rs.gopro.mobile_store.ui.fragment.InvoicesFragment;
import rs.gopro.mobile_store.ui.fragment.SaleOrderFragment;
import rs.gopro.mobile_store.util.LogUtils;
import android.app.Activity;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;

public class InvoicesLayout extends CustomLinearLayout {

	private static String TAG = "InvoicesLayout";
	private static final String INVOICES_SCHEME = "settings";
	private static final String INVOICES_AUTHORITY = "invoices";
	public static final Uri INVOICES_URI = new Uri.Builder().scheme(INVOICES_SCHEME).authority(INVOICES_AUTHORITY).build();
	private Fragment fragment;

	public InvoicesLayout(FragmentManager fragmentManager, Activity activity) {
		super(fragmentManager, activity);
	}

	@Override
	protected void inflateLayout(LayoutInflater layoutInflater) {
		if (fragment == null) {
			View view = layoutInflater.inflate(R.layout.content_holder_invoices, null);
			this.addView(view);
			FragmentTransaction tr = fragmentManager.beginTransaction();
			fragment = new InvoicesFragment();
			tr.replace(R.id.invoices_content, fragment);
			tr.commit();
			LogUtils.LOGI(TAG, "inflateLayout" + fragment.getId());
		}
	}

	@Override
	public ActionMode.Callback getContextualActionBar(String identifier) {
		// TODO Auto-generated method stub
		return null;
	}

}
