package rs.gopro.mobile_store.ui;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.ui.fragment.SaleOrderAddEditLineFragment.Callbacks;
import rs.gopro.mobile_store.provider.MobileStoreContract.ElectronicCardCustomer;
import rs.gopro.mobile_store.ui.fragment.ElectronicCardCustomerFragment;
import rs.gopro.mobile_store.util.LogUtils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class CustomersViewDetailFragment extends Fragment implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = LogUtils.makeLogTag(CustomersViewDetailFragment.class);
	
	public static final String IS_IN_UPDATE_MODE = "IS_IN_UPDATE_MODE";
	
	private Uri mCustomerdetailUri;
	
	private TextView mCustomer_no;
	private EditText mName;
	private EditText mName2;
	private EditText mAddress;
	private TextView mCity;
	private EditText mPostCode;
	private EditText mPhone;
	private EditText mMobile;
	private EditText mEmail;
	private EditText mCompanyId;
	private EditText mPrimaryContactId;
	private EditText mVarRegNo;
	private TextView mCreditLimitLcy;
	private TextView mBalanceLcy;
	private TextView mBalanceDueLcy;
	private TextView mPaymentTermsCode;
	private TextView mPriority;
	private TextView mGlobalDimension;
	private TextView mChannelOran;
	private TextView mBlockedStatus;
	private TextView mSml;
	private TextView mInternalBalanceDueLcy;
	private TextView mAdoptedPotential;
	private TextView mFocusCustomer;
	private TextView mDivision;
	private TextView mNumberOfBlueCoat;
	private EditText mNumberOfGreyCoat;
	
	private ActionMode actionMode;
	private boolean isInUpdateMode = false;
	
	
    
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
        	Bundle arguments = getArguments();
         isInUpdateMode =	arguments.getBoolean(IS_IN_UPDATE_MODE);
        final Intent intent = BaseActivity.fragmentArgumentsToIntent(arguments);
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof Callbacks)) {
            throw new ClassCastException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
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
        mCustomer_no = (TextView) rootView.findViewById(R.id.customer_no_group_value);
        mName = (EditText) rootView.findViewById(R.id.customer_name_value);
        mName2 = (EditText) rootView.findViewById(R.id.customer_name2_value);
        mAddress = (EditText) rootView.findViewById(R.id.customer_address_value);
        //mCity = (TextView) rootView.findViewById(R.id.customer_city_value);
        mPostCode = (EditText) rootView.findViewById(R.id.customer_postal_code_value);
        mPhone = (EditText) rootView.findViewById(R.id.customer_phone_value);
        mMobile = (EditText) rootView.findViewById(R.id.customer_phone_mobile_value);
        mEmail = (EditText) rootView.findViewById(R.id.customer_email_value);
        mCompanyId = (EditText) rootView.findViewById(R.id.customer_company_id_value);
        mPrimaryContactId = (EditText) rootView.findViewById(R.id.customer_primary_contact_id_value);
        mVarRegNo = (EditText) rootView.findViewById(R.id.customer_vat_reg_no_value);
        mCreditLimitLcy = (TextView) rootView.findViewById(R.id.customer_credit_limit_lcy_value);
        mBalanceLcy = (TextView) rootView.findViewById(R.id.customer_balance_lcy_value);
        mBalanceDueLcy = (TextView) rootView.findViewById(R.id.customer_balance_due_lcy_value);
        mPaymentTermsCode = (TextView) rootView.findViewById(R.id.customer_payment_terms_code_value);
        mPriority = (TextView) rootView.findViewById(R.id.customer_priority_value);
        mGlobalDimension = (TextView) rootView.findViewById(R.id.customer_global_dimension_value);
        mChannelOran = (TextView) rootView.findViewById(R.id.customer_channel_oran_value);
        mBlockedStatus = (TextView) rootView.findViewById(R.id.customer_blocked_status_value);
        mSml = (TextView) rootView.findViewById(R.id.customer_sml_value);
        mInternalBalanceDueLcy = (TextView) rootView.findViewById(R.id.customer_internal_balance_due_lcy_value);
        mAdoptedPotential = (TextView) rootView.findViewById(R.id.customer_adopted_potential_value);
        mFocusCustomer = (TextView) rootView.findViewById(R.id.customer_focus_customer_value);
        mDivision = (TextView) rootView.findViewById(R.id.customer_division_value);
        mNumberOfBlueCoat = (TextView) rootView.findViewById(R.id.customer_blue_coat_value);
        mNumberOfGreyCoat = (EditText) rootView.findViewById(R.id.customer_gray_coat_value);
        if(isInUpdateMode){
        	setFocusable(true);
        	if(actionMode == null){
        		actionMode = getActivity().startActionMode(actionModeCallback);
        		System.out.println("CONTEXTUAL MENUYYYYY");
        	}
        	
        }else{
        setFocusable(false);
        }
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
		String nameString = cursor.getString(CustomerDetailQuery.NAME);
		String name2String = cursor.getString(CustomerDetailQuery.NAME_2);
		String addressString = cursor.getString(CustomerDetailQuery.ADDRESS);
		//String cityString = cursor.getString(CustomerDetailQuery.CITY);
		String postcodeString = cursor.getString(CustomerDetailQuery.POST_CODE);
		String phoneString = cursor.getString(CustomerDetailQuery.PHONE);
		String mobileString = cursor.getString(CustomerDetailQuery.MOBILE);
		String emailString = cursor.getString(CustomerDetailQuery.EMAIL);
		String companyidString =String.valueOf(cursor.getInt(CustomerDetailQuery.COMPANY_ID)) ;
		String primarycontactidString = String.valueOf(cursor.getInt(CustomerDetailQuery.PRIMARY_CONTACT_ID));
		String varregnoString = cursor.getString(CustomerDetailQuery.VAR_REG_NO);
		String creditlimitlcyString = String.valueOf(cursor.getDouble(CustomerDetailQuery.CREDIT_LIMIT_LCY));
		String balancelcyString = String.valueOf(cursor.getDouble(CustomerDetailQuery.BALANCE_LCY));
		String balanceduelcyString = String.valueOf(cursor.getDouble(CustomerDetailQuery.BALANCE_DUE_LCY));
		String internalbalanceduelcyString = String.valueOf(cursor.getDouble(CustomerDetailQuery.INTERNAL_BALANCE_DUE_LCY));
		String paymenttermscodeString = String.valueOf(cursor.getInt(CustomerDetailQuery.PAYMENT_TERMS_CODE));
		String priorityString = String.valueOf(cursor.getInt(CustomerDetailQuery.PRIORITY));
		String globaldimensionString = cursor.getString(CustomerDetailQuery.GLOBAL_DIMENSION);
		String channeloranString = cursor.getString(CustomerDetailQuery.CHANNEL_ORAN);
		String smlString = cursor.getString(CustomerDetailQuery.SML);
		String adoptedpotentialString = String.valueOf(cursor.getDouble(CustomerDetailQuery.ADOPTED_POTENTIAL));
		String focuscustomerString = cursor.getString(CustomerDetailQuery.FOCUS_CUSTOMER);
		String divisionString = cursor.getString(CustomerDetailQuery.DIVISION);
		String numberofbluecoatString = String.valueOf(cursor.getInt(CustomerDetailQuery.NUMBER_OF_BLUE_COAT));
		String numberofgreycoatString = String.valueOf(cursor.getInt(CustomerDetailQuery.NUMBER_OF_GREY_COAT));
		String blockedstatusString = cursor.getString(CustomerDetailQuery.BLOCKED_STATUS);
        
        mCustomer_no.setText(customernoString);
        mName.setText(nameString);
        mName2.setText(name2String);
        mAddress.setText(addressString);
        //mCity.setText(cityString);
        mPostCode.setText(postcodeString);
        mPhone.setText(phoneString);
        mMobile.setText(mobileString);
        mEmail.setText(emailString);
        mCompanyId.setText(companyidString);
        mPrimaryContactId.setText(primarycontactidString);
        mVarRegNo.setText(varregnoString);
        mCreditLimitLcy.setText(creditlimitlcyString);
        mBalanceLcy.setText(balancelcyString);
        mBalanceDueLcy.setText(balanceduelcyString);
        mPaymentTermsCode.setText(paymenttermscodeString);
        mPriority.setText(priorityString);
        mGlobalDimension.setText(globaldimensionString);
        mChannelOran.setText(channeloranString);
        mBlockedStatus.setText(blockedstatusString);
        mSml.setText(smlString);
        mInternalBalanceDueLcy.setText(internalbalanceduelcyString);
        mAdoptedPotential.setText(adoptedpotentialString);
        mFocusCustomer.setText(focuscustomerString);
        mDivision.setText(divisionString);
        mNumberOfBlueCoat.setText(numberofbluecoatString);
        mNumberOfGreyCoat.setText(numberofgreycoatString);

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
		System.out.println("LOADER IS FINIDES");
		buildUiFromCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}
	
	
	
	public void setFocusable(boolean disable){
		mAddress.setFocusable(disable);
		mName.setFocusable(disable);
		mName2.setFocusable(disable);
		mEmail.setFocusable(disable);
		mPostCode.setFocusable(disable);
		mMobile.setFocusable(disable);
		mCompanyId.setFocusable(disable);
		mPrimaryContactId.setFocusable(disable);
		mVarRegNo.setFocusable(disable);
		mPhone.setFocusable(disable);
		mNumberOfGreyCoat.setFocusable(disable);
	}
	
	
	private void saveForm(){
		ContentValues contentValues = new ContentValues();
		contentValues.put(Customers.ADDRESS, mAddress.getText().toString());
		contentValues.put(Customers.NAME, mName.getText().toString());
		contentValues.put(Customers.NAME_2, mName2.getText().toString());
		contentValues.put(Customers.EMAIL, mEmail.getText().toString());
		contentValues.put(Customers.POST_CODE, mPostCode.getText().toString());
		contentValues.put(Customers.MOBILE, mMobile.getText().toString());
		contentValues.put(Customers.COMPANY_ID, mCompanyId.getText().toString());
		contentValues.put(Customers.PRIMARY_CONTACT_ID, mPrimaryContactId.getText().toString());
		contentValues.put(Customers.VAR_REG_NO, mVarRegNo.getText().toString());
		contentValues.put(Customers.PHONE, mPhone.getText().toString());
		contentValues.put(Customers.NUMBER_OF_GREY_COAT, mNumberOfGreyCoat.getText().toString());
		getActivity().getContentResolver().update(mCustomerdetailUri, contentValues, null, null);
		
	}
	
	
private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
		
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public void onDestroyActionMode(ActionMode mode) {
		  setFocusable(false);
		  mode = null;
		//	getLoaderManager().restartLoader(0, null, this);
		   getLoaderManager().restartLoader(CustomerDetailQuery._TOKEN, null, CustomersViewDetailFragment.this);
	   
		}
		
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.cutomer_contextual_action_bar, menu);
			return true;
		}
		
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			System.out.println("AKCIJA: "+item.getItemId());
			switch (item.getItemId()) {
			case R.id.save_customer_changes :
				System.out.println("SNIMA");
				saveForm();
				mode.finish();
				return true;

			default:
				break;
			}
			return false;
		}
	};


 public void	onEditButtonClick(){
		setFocusable(true);
	   // actionMode = getActivity().startActionMode(actionModeCallback);
	}
	
	
		
	

	
	
	
	private interface CustomerDetailQuery {
		int _TOKEN = 0x8;

        String[] PROJECTION = {
                BaseColumns._ID,
                MobileStoreContract.Customers.CUSTOMER_NO,
                MobileStoreContract.Customers.NAME,
                MobileStoreContract.Customers.NAME_2,
                MobileStoreContract.Customers.ADDRESS,
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
        int POST_CODE = 5;
        int PHONE = 6;
        int MOBILE = 7;
        int EMAIL = 8;
        int COMPANY_ID = 9;
        int PRIMARY_CONTACT_ID = 10;
        int VAR_REG_NO = 11;
        int CREDIT_LIMIT_LCY = 12;
        int BALANCE_LCY = 13;
        int BALANCE_DUE_LCY = 14;
        int PAYMENT_TERMS_CODE = 15;
        int PRIORITY = 16;
        int GLOBAL_DIMENSION = 17;
        int CHANNEL_ORAN = 18;
        int BLOCKED_STATUS = 19;
        int SML = 20;
        int INTERNAL_BALANCE_DUE_LCY = 21;
        int ADOPTED_POTENTIAL = 22;
        int FOCUS_CUSTOMER = 23;
        int DIVISION = 24;
        int NUMBER_OF_BLUE_COAT = 25;
        int NUMBER_OF_GREY_COAT = 26; 
	}




}
