package rs.gopro.mobile_store.ui;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.ui.fragment.CustomerTradeAgreementFragment;
import rs.gopro.mobile_store.ui.fragment.ElectronicCardCustomerFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class CustomerTradeAgreementActivity extends BaseActivity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Uri uri = getIntent().getData();
		if (uri == null) {
			return;
		}
		setContentView(R.layout.activity_customer_trade_agreement);
		FragmentManager fragmentManager = getSupportFragmentManager();
		CustomerTradeAgreementFragment fragment = new CustomerTradeAgreementFragment();
		fragment.setArguments(BaseActivity.intentToFragmentArguments(new Intent(Intent.ACTION_VIEW, uri)));
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.replace(R.id.customer_trade_agreement_content, fragment);
		transaction.commit();
	}
}
