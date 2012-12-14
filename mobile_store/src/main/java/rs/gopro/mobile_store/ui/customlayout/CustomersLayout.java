package rs.gopro.mobile_store.ui.customlayout;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.ui.fragment.CustomerFragment;
import rs.gopro.mobile_store.ui.fragment.SaleOrderFragment;
import android.app.Activity;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;

public class CustomersLayout extends CustomLinearLayout {

	private static final String CUSTOMERS_SCHEME = "settings";
	private static final String CUSTOMERS_AUTHORITY = "customers";
	public static final Uri CUSTOMERS_URI = new Uri.Builder().scheme(CUSTOMERS_SCHEME).authority(CUSTOMERS_AUTHORITY).build();

	public CustomersLayout(FragmentManager fragmentManager, Activity activity) {
		super(fragmentManager, activity);
	}

	@Override
	protected void inflateLayout(LayoutInflater layoutInflater) {
		View view = layoutInflater.inflate(R.layout.content_holder_customers, null);

		Fragment fragment = fragmentManager.findFragmentById(R.id.customers_content);
		if (fragment == null) {
			FragmentTransaction tr = fragmentManager.beginTransaction();
			fragment = new CustomerFragment();
			tr.add(R.id.customers_content, fragment);
			tr.commit();
		}

		this.addView(view);

	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
	}

}
