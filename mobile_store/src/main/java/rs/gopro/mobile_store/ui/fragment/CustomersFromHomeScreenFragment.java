package rs.gopro.mobile_store.ui.fragment;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.ui.BaseActivity;
import rs.gopro.mobile_store.ui.CustomersViewDetailFragment;
import rs.gopro.mobile_store.ui.CustomersViewDetailFragment.Callbacks;

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
	private TextView mCity;
	private TextView mPost_code;
	private TextView mPhone;
	private TextView mMobile;
	private TextView mEmail;
	private TextView mNote;

	private TextView mNumberOfBlueCoat;
	private TextView mNumberOfGreyCoat;

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
		mCity = (TextView) rootView.findViewById(R.id.customer_city_value);
		mPost_code = (TextView) rootView.findViewById(R.id.customer_postal_code_value);
		mPhone = (TextView) rootView.findViewById(R.id.customer_phone_value);
		mMobile = (TextView) rootView.findViewById(R.id.customer_phone_mobile_value);
		mEmail = (TextView) rootView.findViewById(R.id.customer_email_value);
		mNote =  (TextView) rootView.findViewById(R.id.customer_note_value);
		mNumberOfBlueCoat = (TextView) rootView.findViewById(R.id.customer_blue_coat_value);
		mNumberOfGreyCoat = (TextView) rootView.findViewById(R.id.customer_gray_coat_value);
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
		String cityString = cursor.getString(CustomerDetailQuery.CITY);
		String postcodeString = cursor.getString(CustomerDetailQuery.POST_CODE);
		String phoneString = cursor.getString(CustomerDetailQuery.PHONE);
		String mobileString = cursor.getString(CustomerDetailQuery.MOBILE);
		String emailString = cursor.getString(CustomerDetailQuery.EMAIL);
		String numberOfBlueCoatString = "" + cursor.getInt(CustomerDetailQuery.NUMBER_OF_BLUE_COAT);
		String numberOfGreyCoatString = "" + cursor.getInt(CustomerDetailQuery.NUMBER_OF_GREY_COAT);

		mCustomer_no.setText(customernoString);
		mAddress.setText(addressString);
		mCity.setText(cityString);
		mPost_code.setText(postcodeString);
		mPhone.setText(phoneString);
		mMobile.setText(mobileString);
		mEmail.setText(emailString);
		mNumberOfBlueCoat.setText(numberOfBlueCoatString);
		mNumberOfGreyCoat.setText(numberOfGreyCoatString);
		
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

		String[] PROJECTION = { BaseColumns._ID, MobileStoreContract.Customers.CUSTOMER_NO, MobileStoreContract.Customers.NAME, MobileStoreContract.Customers.NAME_2, MobileStoreContract.Customers.ADDRESS,
				MobileStoreContract.Customers.CITY, MobileStoreContract.Customers.POST_CODE, MobileStoreContract.Customers.PHONE, MobileStoreContract.Customers.MOBILE, MobileStoreContract.Customers.EMAIL,
				MobileStoreContract.Customers.COMPANY_ID, MobileStoreContract.Customers.PRIMARY_CONTACT_ID, MobileStoreContract.Customers.VAR_REG_NO, MobileStoreContract.Customers.CREDIT_LIMIT_LCY,
				MobileStoreContract.Customers.BALANCE_LCY, MobileStoreContract.Customers.BALANCE_DUE_LCY, MobileStoreContract.Customers.PAYMENT_TERMS_CODE, MobileStoreContract.Customers.PRIORITY,
				MobileStoreContract.Customers.GLOBAL_DIMENSION, MobileStoreContract.Customers.CHANNEL_ORAN, MobileStoreContract.Customers.BLOCKED_STATUS, MobileStoreContract.Customers.SML,
				MobileStoreContract.Customers.INTERNAL_BALANCE_DUE_LCY, MobileStoreContract.Customers.ADOPTED_POTENTIAL, MobileStoreContract.Customers.FOCUS_CUSTOMER, MobileStoreContract.Customers.DIVISION,
				MobileStoreContract.Customers.NUMBER_OF_BLUE_COAT, MobileStoreContract.Customers.NUMBER_OF_GREY_COAT };

		int _ID = 0;
		int CUSTOMER_NO = 1;
		int NAME = 2;
		int NAME_2 = 3;
		int ADDRESS = 4;
		int CITY = 5;
		int POST_CODE = 6;
		int PHONE = 7;
		int MOBILE = 8;
		int EMAIL = 9;
		int COMPANY_ID = 10;
		int PRIMARY_CONTACT_ID = 11;
		int VAR_REG_NO = 12;
		int CREDIT_LIMIT_LCY = 13;
		int BALANCE_LCY = 14;
		int BALANCE_DUE_LCY = 15;
		int PAYMENT_TERMS_CODE = 16;
		int PRIORITY = 17;
		int GLOBAL_DIMENSION = 18;
		int CHANNEL_ORAN = 19;
		int BLOCKED_STATUS = 20;
		int SML = 21;
		int INTERNAL_BALANCE_DUE_LCY = 22;
		int ADOPTED_POTENTIAL = 23;
		int FOCUS_CUSTOMER = 24;
		int DIVISION = 25;
		int NUMBER_OF_BLUE_COAT = 26;
		int NUMBER_OF_GREY_COAT = 27;
	}
}
