package rs.gopro.mobile_store.ui.customlayout;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.ui.fragment.CustomerFragment;
import rs.gopro.mobile_store.ui.fragment.SaleOrderFragment;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;

public class CustomersLayout extends CustomLinearLayout {

	private static final String CUSTOMERS_SCHEME = "settings";
	private static final String CUSTOMERS_AUTHORITY = "customers";
	public static final Uri CUSTOMERS_URI = new Uri.Builder().scheme(CUSTOMERS_SCHEME).authority(CUSTOMERS_AUTHORITY).build();
	private Fragment fragment;
	
	public CustomersLayout(FragmentManager fragmentManager, Activity activity) {
		super(fragmentManager, activity);
	}

	@Override
	protected void inflateLayout(LayoutInflater layoutInflater) {
		if (fragment != null) return;
		View view = layoutInflater.inflate(R.layout.content_holder_customers, null);
		this.addView(view);
		FragmentTransaction tr = fragmentManager.beginTransaction();
		fragment = new CustomerFragment();
		tr.replace(R.id.customers_content, fragment);
		tr.commit();
		
	}
}
