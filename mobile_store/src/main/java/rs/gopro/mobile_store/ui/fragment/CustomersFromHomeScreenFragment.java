package rs.gopro.mobile_store.ui.fragment;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.ui.BaseActivity;
import rs.gopro.mobile_store.util.LogUtils;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CustomersFromHomeScreenFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = LogUtils.makeLogTag(CustomersFromHomeScreenFragment.class);

	private Uri mCustomerdetailUri;

	private TextView mCustomer_no;
	
	private TextView mAddress;
	private TextView mPost_code;
	private TextView mPhone;
	private TextView mEmail;

	public interface Callbacks {
		public void onCustomerIdAvailable(String customerId);
	}

	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onCustomerIdAvailable(String customerId) {
		}
	};

	private Callbacks mCallbacks = sDummyCallbacks;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Intent intent = BaseActivity.fragmentArgumentsToIntent(getArguments());
		mCustomerdetailUri = intent.getData();
		if (mCustomerdetailUri == null) {
			return;
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (mCustomerdetailUri == null) {
			return;
		}

		// Start background query to load vendor details
		getLoaderManager().initLoader(CustomerDetailQuery._TOKEN, null, this);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_customer_home_screen, null);
		mCustomer_no = (TextView) rootView.findViewById(R.id.customer_no_group_value);
		mAddress = (TextView) rootView.findViewById(R.id.customer_address_value);
		//mCity = (TextView) rootView.findViewById(R.id.customer_city_value);
		mPost_code = (TextView) rootView.findViewById(R.id.customer_postal_code_value);
		mPhone = (TextView) rootView.findViewById(R.id.customer_phone_value);
		mEmail = (TextView) rootView.findViewById(R.id.customer_email_value);
		return rootView;
	}

	public void buildUiFromCursor(Cursor cursor) {
		if (getActivity() == null) {
			return;
		}
		if (!cursor.moveToFirst()) {
			return;
		}
		String customernoString = cursor.getString(CustomerDetailQuery.CUSTOMER_NO);
		String addressString = cursor.getString(CustomerDetailQuery.ADDRESS);
		//String cityString = cursor.getString(CustomerDetailQuery.CITY);
		String postcodeString = cursor.getString(CustomerDetailQuery.POST_CODE);
		String phoneString = cursor.getString(CustomerDetailQuery.PHONE);
		String emailString = cursor.getString(CustomerDetailQuery.EMAIL);

		mCustomer_no.setText(customernoString);
		mAddress.setText(addressString);
		//mCity.setText(cityString);
		mPost_code.setText(postcodeString);
		mPhone.setText(phoneString);
		mEmail.setText(emailString);
		
		int customerId = cursor.getInt(CustomerDetailQuery._ID);
		LogUtils.LOGI(TAG, "Loaded customer id: " + String.valueOf(customerId));
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle data) {
		return new CursorLoader(getActivity(), mCustomerdetailUri, CustomerDetailQuery.PROJECTION, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		buildUiFromCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}
	
	
	@Override
	public void onOptionsMenuClosed(Menu menu) {
		super.onOptionsMenuClosed(menu);
	}
	

	private interface CustomerDetailQuery {
		int _TOKEN = 0x8;

		String[] PROJECTION = { BaseColumns._ID, MobileStoreContract.Customers.CUSTOMER_NO, MobileStoreContract.Customers.NAME, 
				MobileStoreContract.Customers.ADDRESS, MobileStoreContract.Customers.POST_CODE, 
				MobileStoreContract.Customers.PHONE, MobileStoreContract.Customers.EMAIL };

		int _ID = 0;
		int CUSTOMER_NO = 1;
		int NAME = 2;
		int ADDRESS = 3;
		int POST_CODE = 4;
		int PHONE = 5;
		int EMAIL = 6;
	}
}
