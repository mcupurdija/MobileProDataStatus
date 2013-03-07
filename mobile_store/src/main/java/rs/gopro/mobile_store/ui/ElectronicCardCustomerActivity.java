package rs.gopro.mobile_store.ui;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.ui.fragment.ElectronicCardCustomerFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class ElectronicCardCustomerActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Uri uri = getIntent().getData();
		if (uri == null) {
			return;
		}
		setContentView(R.layout.activity_el_card_customer);
		FragmentManager fragmentManager = getSupportFragmentManager();
		ElectronicCardCustomerFragment fragment = new ElectronicCardCustomerFragment();
		fragment.setArguments(BaseActivity.intentToFragmentArguments(new Intent(Intent.ACTION_VIEW, uri)));
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.replace(R.id.el_card_customer_content, fragment);
		transaction.commit();
	}
}
