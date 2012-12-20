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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_customer_view_details, null);
        mCustomer_no = (TextView) rootView.findViewById(R.id.customer_no);
        mName = (TextView) rootView.findViewById(R.id.name);
        mName_2 = (TextView) rootView.findViewById(R.id.name_2);
        mAddress = (TextView) rootView.findViewById(R.id.address);
        mCity = (TextView) rootView.findViewById(R.id.city);
        mPost_code = (TextView) rootView.findViewById(R.id.post_code);
        mPhone = (TextView) rootView.findViewById(R.id.phone);
        mMobile = (TextView) rootView.findViewById(R.id.mobile);
        mEmail = (TextView) rootView.findViewById(R.id.email);
        mCompany_id = (TextView) rootView.findViewById(R.id.company_id);
        mPrimary_contact_id = (TextView) rootView.findViewById(R.id.primary_contact_id);
        mVar_reg_no = (TextView) rootView.findViewById(R.id.var_reg_no);
        mCredit_limit_lcy = (TextView) rootView.findViewById(R.id.credit_limit_lcy);
        mBalance_lcy = (TextView) rootView.findViewById(R.id.balance_lcy);
        mBalance_due_lcy = (TextView) rootView.findViewById(R.id.balance_due_lcy);
        mPayment_terms_code = (TextView) rootView.findViewById(R.id.payment_terms_code);
        mPriority = (TextView) rootView.findViewById(R.id.priority);
        mGlobal_dimension = (TextView) rootView.findViewById(R.id.global_dimension);
        mChannel_oran = (TextView) rootView.findViewById(R.id.channel_oran);
        mBlocked_status = (TextView) rootView.findViewById(R.id.blocked_status);
        mSml = (TextView) rootView.findViewById(R.id.sml);
        mInternal_balance_due_lcy = (TextView) rootView.findViewById(R.id.internal_balance_due_lcy);
        mAdopted_potential = (TextView) rootView.findViewById(R.id.adopted_potential);
        mFocus_customer = (TextView) rootView.findViewById(R.id.focus_customer);
        mDivision = (TextView) rootView.findViewById(R.id.division);
        mNumber_of_blue_coat = (TextView) rootView.findViewById(R.id.number_of_blue_coat);
        mNumber_of_grey_coat = (TextView) rootView.findViewById(R.id.number_of_grey_coat);
        return rootView;
    }
	
    public void buildUiFromCursor(Cursor cursor) {
        if (getActivity() == null) {
            return;
        }

        if (!cursor.moveToFirst()) {
            return;
        }
        
        String customernoString = "Br.                            " + cursor.getString(CustomerDetailQuery.CUSTOMER_NO);
        String nameString = "Ime                            " + cursor.getString(CustomerDetailQuery.NAME);
        String name2String = "Ime 2                          " + cursor.getString(CustomerDetailQuery.NAME_2);
        String addressString = "Adresa                         " + cursor.getString(CustomerDetailQuery.ADDRESS);
        String cityString = "Grad                           " + cursor.getString(CustomerDetailQuery.CITY);
        String postcodeString = "Poštanski broj                 " + cursor.getString(CustomerDetailQuery.POST_CODE);
        String phoneString = "Br. telefona                   " + cursor.getString(CustomerDetailQuery.PHONE);
        String mobileString = "Br. Mobilnog                   " + cursor.getString(CustomerDetailQuery.MOBILE);
        String emailString = "E-pošta                        " + cursor.getString(CustomerDetailQuery.EMAIL);
        String companyidString = "Maticni broj                   " + cursor.getInt(CustomerDetailQuery.COMPANY_ID);
        String primarycontactidString = "Br. primarnog kontakta         " + cursor.getInt(CustomerDetailQuery.PRIMARY_CONTACT_ID);
        String varregnoString = "Poreski broj                   " + cursor.getString(CustomerDetailQuery.VAR_REG_NO);
        String creditlimitlcyString = "Kreditni limit (LVT)           " + String.valueOf(cursor.getDouble(CustomerDetailQuery.CREDIT_LIMIT_LCY));
        String balancelcyString = "Saldo (LVT)                    " + String.valueOf(cursor.getDouble(CustomerDetailQuery.BALANCE_LCY));
        String balanceduelcyString = "Dospeli saldo (LVT)            " + String.valueOf(cursor.getDouble(CustomerDetailQuery.BALANCE_DUE_LCY));
        String paymenttermscodeString = "Šifra uslova placanja          " + cursor.getInt(CustomerDetailQuery.PAYMENT_TERMS_CODE);
        String priorityString = "Prioritet                      " + cursor.getInt(CustomerDetailQuery.PRIORITY);
        String globaldimensionString = "Branša                         " + cursor.getString(CustomerDetailQuery.GLOBAL_DIMENSION);
        String channeloranString = "Kanal (ORAN)                   " + cursor.getString(CustomerDetailQuery.CHANNEL_ORAN);
        String blockedstatusString = "Status kupca za isporuke       " + cursor.getString(CustomerDetailQuery.BLOCKED_STATUS);
        String smlString = "SML                            " + cursor.getString(CustomerDetailQuery.SML);
        String internalbalanceduelcyString = "Dospeli saldo preko i.v. (LVT) " + String.valueOf(cursor.getDouble(CustomerDetailQuery.INTERNAL_BALANCE_DUE_LCY));
        String adoptedpotentialString = "Usvojeni potencijal            " + String.valueOf(cursor.getDouble(CustomerDetailQuery.ADOPTED_POTENTIAL));
        String focuscustomerString = "Fokus kupac                    " + cursor.getString(CustomerDetailQuery.FOCUS_CUSTOMER);
        String divisionString = "Divizija                       " + cursor.getString(CustomerDetailQuery.DIVISION);
        String numberofbluecoatString = "Br. plavih mantila             " + cursor.getInt(CustomerDetailQuery.NUMBER_OF_BLUE_COAT);
        String numberofgreycoatString = "Br. Sivih mantila              " + cursor.getInt(CustomerDetailQuery.NUMBER_OF_GREY_COAT);
        
        mCustomer_no.setText(customernoString);
        mName.setText(nameString);
        mName_2.setText(name2String);
        mAddress.setText(addressString);
        mCity.setText(cityString);
        mPost_code.setText(postcodeString);
        mPhone.setText(phoneString);
        mMobile.setText(mobileString);
        mEmail.setText(emailString);
        mCompany_id.setText(companyidString);
        mPrimary_contact_id.setText(primarycontactidString);
        mVar_reg_no.setText(varregnoString);
        mCredit_limit_lcy.setText(creditlimitlcyString);
        mBalance_lcy.setText(balancelcyString);
        mBalance_due_lcy.setText(balanceduelcyString);
        mPayment_terms_code.setText(paymenttermscodeString);
        mPriority.setText(priorityString);
        mGlobal_dimension.setText(globaldimensionString);
        mChannel_oran.setText(channeloranString);
        mBlocked_status.setText(blockedstatusString);
        mSml.setText(smlString);
        mInternal_balance_due_lcy.setText(internalbalanceduelcyString);
        mAdopted_potential.setText(adoptedpotentialString);
        mFocus_customer.setText(focuscustomerString);
        mDivision.setText(divisionString);
        mNumber_of_blue_coat.setText(numberofbluecoatString);
        mNumber_of_grey_coat.setText(numberofgreycoatString);


        int customerId = cursor.getInt(CustomerDetailQuery._ID);
        mCallbacks.onCustomerIdAvailable(String.valueOf(customerId));
        
        LogUtils.LOGI(TAG, "Loaded customer id: " + String.valueOf(customerId));
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
