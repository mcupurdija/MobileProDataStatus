package rs.gopro.mobile_store.ui;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CustomersViewDetailFragment extends Fragment implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = LogUtils.makeLogTag(CustomersViewDetailFragment.class);
	
	private Uri mCustomerdetailUri;
	
	private TextView mCustomer_no;
	private TextView mName;
	private TextView mName_2;
	private TextView mAddress;
	private TextView mCity;
	private TextView mPost_code;
	private TextView mPhone;
	private TextView mMobile;
	private TextView mEmail;
	private TextView mCompany_id;
	private TextView mPrimary_contact_id;
	private TextView mVar_reg_no;
	private TextView mCredit_limit_lcy;
	private TextView mBalance_lcy;
	private TextView mBalance_due_lcy;
	private TextView mPayment_terms_code;
	private TextView mPriority;
	private TextView mGlobal_dimension;
	private TextView mChannel_oran;
	private TextView mBlocked_status;
	private TextView mSml;
	private TextView mInternal_balance_due_lcy;
	private TextView mAdopted_potential;
	private TextView mFocus_customer;
	private TextView mDivision;
	private TextView mNumber_of_blue_coat;
	private TextView mNumber_of_grey_coat;
    
	public interface Callbacks {
		public void onCustomerIdAvailable(String visitId);
	}

	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onCustomerIdAvailable(String visitId) {
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
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_visit_details, null);
        mCustomerName = (TextView) rootView.findViewById(R.id.visit_customer_name);
        mCustomerNo = (TextView) rootView.findViewById(R.id.visit_customer_no);
        mVisitDate = (TextView) rootView.findViewById(R.id.visit_date);
        mOdometer = (TextView) rootView.findViewById(R.id.visit_odometer_value);
        mArrivalTime = (TextView) rootView.findViewById(R.id.visit_arrival_time);
        mDepartureTime = (TextView) rootView.findViewById(R.id.visit_departure_time);
        mNote = (TextView) rootView.findViewById(R.id.visit_note);
        return rootView;
    }
	
    public void buildUiFromCursor(Cursor cursor) {
        if (getActivity() == null) {
            return;
        }

        if (!cursor.moveToFirst()) {
            return;
        }
        
        String customerNameString = cursor.getString(CustomerDetailQuery.NAME) + " " + cursor.getString(CustomerDetailQuery.NAME_2);
        mCustomerName.setText(customerNameString);
        
        String customerNoString = cursor.getString(CustomerDetailQuery.CUSTOMER_NO);
        mCustomerNo.setText(customerNoString);
        
        String visitDate = cursor.getString(CustomerDetailQuery.VISIT_DATE);
        mVisitDate.setText(visitDate);
        
        int odometerVal = cursor.getInt(CustomerDetailQuery.ODOMETER);
        mOdometer.setText(String.valueOf(odometerVal));
        
        String arrivalTime = cursor.getString(CustomerDetailQuery.ARRIVAL_TIME);
        mArrivalTime.setText(arrivalTime);
        
        String departureTime = cursor.getString(CustomerDetailQuery.DEPARTURE_TIME);
        mDepartureTime.setText(departureTime);
        
        String note = cursor.getString(CustomerDetailQuery.NOTE);
        mNote.setText(note);

        int visitId = cursor.getInt(CustomerDetailQuery._ID);
        mCallbacks.onCustomerIdAvailable(String.valueOf(visitId));
        
        LogUtils.LOGI(TAG, "Loaded visit id: " + String.valueOf(visitId));
    }
    
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle data) {
		return new CursorLoader(getActivity(), mCustomerdetailUri, CustomerDetailQuery.PROJECTION, null, null,
                null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		buildUiFromCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}
	
	private interface CustomerDetailQuery {
		int _TOKEN = 0x8;

        String[] PROJECTION = {
                BaseColumns._ID,
                MobileStoreContract.Customers.CUSTOMER_NO,
                MobileStoreContract.Customers.NAME,
                MobileStoreContract.Customers.NAME_2,
                MobileStoreContract.Customers.ADDRESS,
                MobileStoreContract.Customers.CITY,
                MobileStoreContract.Customers.POST_CODE,
                MobileStoreContract.Customers.PHONE,
                MobileStoreContract.Customers.MOBILE,
                MobileStoreContract.Customers.EMAIL,
                MobileStoreContract.Customers.COMPANY_ID,
                MobileStoreContract.Customers.PRIMARY_CONTACT_ID,
                MobileStoreContract.Customers.VAR_REG_NO,
                MobileStoreContract.Customers.CREDIT_LIMIT_LCY,
                MobileStoreContract.Customers.BALANCE_LCY,
                MobileStoreContract.Customers.BALANCE_DUE_LCY,
                MobileStoreContract.Customers.PAYMENT_TERMS_CODE,
                MobileStoreContract.Customers.PRIORITY,
                MobileStoreContract.Customers.GLOBAL_DIMENSION,
                MobileStoreContract.Customers.CHANNEL_ORAN,
                MobileStoreContract.Customers.BLOCKED_STATUS,
                MobileStoreContract.Customers.SML,
                MobileStoreContract.Customers.INTERNAL_BALANCE_DUE_LCY,
                MobileStoreContract.Customers.ADOPTED_POTENTIAL,
                MobileStoreContract.Customers.FOCUS_CUSTOMER,
                MobileStoreContract.Customers.DIVISION,
                MobileStoreContract.Customers.NUMBER_OF_BLUE_COAT,
                MobileStoreContract.Customers.NUMBER_OF_GREY_COAT
        };

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
