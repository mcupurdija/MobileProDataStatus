package rs.gopro.mobile_store.ui;

import java.util.ArrayList;
import java.util.Arrays;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.ui.components.CitiesAutocompleteCursorAdapter;
import rs.gopro.mobile_store.ui.components.CustomerAutocompleteCursorAdapter;
import rs.gopro.mobile_store.util.ApplicationConstants.SyncStatus;
import rs.gopro.mobile_store.util.DialogUtil;
import rs.gopro.mobile_store.util.LogUtils;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.CustomerBalanceSyncObject;
import rs.gopro.mobile_store.ws.model.SetPotentialCustomersSyncObject;
import rs.gopro.mobile_store.ws.model.SyncResult;
import rs.gopro.mobile_store.ws.model.UpdateCustomerSyncObject;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

public class CustomersViewDetailFragment extends Fragment implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = LogUtils.makeLogTag(CustomersViewDetailFragment.class);
	
	public static final String IS_IN_UPDATE_MODE = "IS_IN_UPDATE_MODE";
	
	private String selectedLinkCustomerNo = null;
	
	private Uri mCustomerdetailUri;
	
	private TextView mCustomer_no;
	private EditText mName;
	private EditText mName2;
	private EditText mAddress;
	private TextView mCity;
	private AutoCompleteTextView mPostCode;
	private AutoCompleteTextView acCustomerLink;
	private EditText mPhone;
	private EditText mMobile;
	private EditText mEmail;
	private EditText mCompanyId;
	private EditText mPrimaryContactId;
	private EditText mVarRegNo;
	private TextView mBalanceLcy;
	private TextView mBalanceDueLcy;

	private Spinner mCustomerType;
	private Spinner mCustomerPosition;
	private EditText mAddress2;
	private TextView mBalanceCommission;
	private TextView mPaymentTermsCode;
	
	private TableRow customer_link_group;
	
	private ActionMode actionMode;
	private boolean isInUpdateMode = false;
	
	private CustomerAutocompleteCursorAdapter customerAutocompleteCursorAdapter;
	private CitiesAutocompleteCursorAdapter citiesAutocompleteCursorAdapter;
	private Cursor cityCursorItem;
	private ArrayAdapter<CharSequence> customerTypeAdapter, customerPositionAdapter;
	
	private BroadcastReceiver onNotice = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			SyncResult syncResult = intent.getParcelableExtra(NavisionSyncService.SYNC_RESULT);
			onSOAPResult(syncResult, intent.getAction());
		}
	};
	
	public void onSOAPResult(SyncResult syncResult, String broadcastAction) {
		if (CustomerBalanceSyncObject.BROADCAST_SYNC_ACTION.equalsIgnoreCase(broadcastAction)) {
			if (syncResult.getStatus().equals(SyncStatus.SUCCESS)) {
				CustomerBalanceSyncObject syncObject = (CustomerBalanceSyncObject) syncResult.getComplexResult();
				mBalanceCommission.setText(syncObject.getpBalanceCommission());
				mBalanceLcy.setText(syncObject.getpBalance());
				mBalanceDueLcy.setText(syncObject.getpBalanceDue());
			} else {
				mBalanceCommission.setText("-");
				mBalanceLcy.setText("-");
				mBalanceDueLcy.setText("-");
			}
		} else if (UpdateCustomerSyncObject.BROADCAST_SYNC_ACTION.equalsIgnoreCase(broadcastAction)) {
			if (syncResult.getStatus().equals(SyncStatus.SUCCESS)) {
				DialogUtil.showInfoDialog(getActivity(), getResources().getString(R.string.dialog_title_sync_info), "Zahtev uspešno poslat!");
			} else {
				DialogUtil.showInfoErrorDialog(getActivity(), syncResult.getResult());
			}
		}
	}
    
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
		isInUpdateMode = arguments.getBoolean(IS_IN_UPDATE_MODE);
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
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(onNotice);
    }

    @Override
	public void onResume() {
		super.onResume();
		IntentFilter customerBalanceAsCommissionSyncObject = new IntentFilter(CustomerBalanceSyncObject.BROADCAST_SYNC_ACTION);
    	LocalBroadcastManager.getInstance(getActivity()).registerReceiver(onNotice, customerBalanceAsCommissionSyncObject);
    	IntentFilter updateCustomersSyncObject = new IntentFilter(UpdateCustomerSyncObject.BROADCAST_SYNC_ACTION);
    	LocalBroadcastManager.getInstance(getActivity()).registerReceiver(onNotice, updateCustomersSyncObject);
	}

	@Override
    public void onDestroy() {
        super.onDestroy();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_customer_view_details, null);
        mCustomer_no = (TextView) rootView.findViewById(R.id.customer_no_group_value);
        mName = (EditText) rootView.findViewById(R.id.customer_name_value);
        mName2 = (EditText) rootView.findViewById(R.id.customer_name2_value);
        mAddress = (EditText) rootView.findViewById(R.id.customer_address_value);
        mCity = (TextView) rootView.findViewById(R.id.customer_city_value);
        
        citiesAutocompleteCursorAdapter = new CitiesAutocompleteCursorAdapter(getActivity(), null);
        mPostCode = (AutoCompleteTextView) rootView.findViewById(R.id.customer_postal_code_value);
        mPostCode.setAdapter(citiesAutocompleteCursorAdapter);
        mPostCode.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				cityCursorItem = (Cursor) citiesAutocompleteCursorAdapter.getItem(position);
			}
		});
        
        mPhone = (EditText) rootView.findViewById(R.id.customer_phone_value);
        mMobile = (EditText) rootView.findViewById(R.id.customer_phone_mobile_value);
        mEmail = (EditText) rootView.findViewById(R.id.customer_email_value);
        mCompanyId = (EditText) rootView.findViewById(R.id.customer_company_id_value);
        mPrimaryContactId = (EditText) rootView.findViewById(R.id.customer_primary_contact_id_value);
        mVarRegNo = (EditText) rootView.findViewById(R.id.customer_vat_reg_no_value);
        mBalanceLcy = (TextView) rootView.findViewById(R.id.customer_balance_lcy_value);
        mBalanceDueLcy = (TextView) rootView.findViewById(R.id.customer_balance_due_lcy_value);
        
        mCustomerType = (Spinner) rootView.findViewById(R.id.customer_type_value);
        customerTypeAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.customer_type_array, android.R.layout.simple_spinner_item);
        customerTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCustomerType.setAdapter(customerTypeAdapter);
        
        mCustomerPosition = (Spinner) rootView.findViewById(R.id.customer_position_value);
        customerPositionAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.pozicija_title_array, android.R.layout.simple_spinner_item);
		customerPositionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mCustomerPosition.setAdapter(customerPositionAdapter);
		
		customer_link_group = (TableRow) rootView.findViewById(R.id.customer_link_group);
		acCustomerLink = (AutoCompleteTextView) rootView.findViewById(R.id.acCustomerLink);
		customerAutocompleteCursorAdapter = new CustomerAutocompleteCursorAdapter(getActivity(), null);
		acCustomerLink.setAdapter(customerAutocompleteCursorAdapter);
		acCustomerLink.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Cursor cursor = (Cursor) customerAutocompleteCursorAdapter.getItem(position);
				selectedLinkCustomerNo = cursor.getString(1);
			}
		});
		
        mAddress2 = (EditText) rootView.findViewById(R.id.customer_address2_value);
        mBalanceCommission = (TextView) rootView.findViewById(R.id.customer_balance_commission_value);
        mPaymentTermsCode = (TextView) rootView.findViewById(R.id.customer_payment_terms_code_value);
        
        if (isInUpdateMode) {
        	setFocusable(true);
        	if(actionMode == null){
        		actionMode = getActivity().startActionMode(actionModeCallback);
        	}
        } else {
        	setFocusable(false);
        }
        return rootView;
    }
	
    public void buildUiFromCursor(Cursor cursor) {
        if (getActivity() == null) {
            return;
        }

        if (cursor.moveToFirst()) {
        	String customernoString = cursor.getString(CustomerDetailQuery.CUSTOMER_NO);
    		String nameString = cursor.getString(CustomerDetailQuery.NAME);
    		String name2String = cursor.getString(CustomerDetailQuery.NAME_2);
    		String addressString = cursor.getString(CustomerDetailQuery.ADDRESS);
    		String address2String = cursor.getString(CustomerDetailQuery.ADDRESS_2);
    		String cityString = cursor.getString(CustomerDetailQuery.CITY);
    		String postcodeString = cursor.getString(CustomerDetailQuery.POST_CODE);
    		String phoneString = cursor.getString(CustomerDetailQuery.PHONE);
    		String emailString = cursor.getString(CustomerDetailQuery.EMAIL);
    		String companyidString = String.valueOf(cursor.getString(CustomerDetailQuery.COMPANY_ID));
    		String varregnoString = cursor.getString(CustomerDetailQuery.VAT_REG_NO);
    		String paymenttermscodeString = cursor.getString(CustomerDetailQuery.PAYMENT_TERMS_CODE);
    		selectedLinkCustomerNo = cursor.getString(CustomerDetailQuery.CUSTOMER_LINK);
            
            mCustomer_no.setText(customernoString);
            mName.setText(nameString);
            mName2.setText(name2String);
            mAddress.setText(addressString);
            mAddress2.setText(address2String);
            mCity.setText(cityString);
            mPostCode.setText(postcodeString);
            mPhone.setText(phoneString);
            mEmail.setText(emailString);
            mCompanyId.setText(companyidString);
            mVarRegNo.setText(varregnoString);
            mPaymentTermsCode.setText(paymenttermscodeString);
            
            try {
            	mCustomerType.setSelection(cursor.getInt(CustomerDetailQuery.CUSTOMER_TYPE));
			} catch (Exception e) {
				LogUtils.LOGE(TAG, "CustomerTypeArray index out of bounds");
			}
            
            ArrayList<String> customerPositionList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.pozicija_value_array)));
            try {
            	mCustomerPosition.setSelection(customerPositionList.indexOf(cursor.getString(CustomerDetailQuery.CUSTOMER_POSITION)));
			} catch (Exception e) {
				LogUtils.LOGE(TAG, "CustomerPositionArray index out of bounds");
			}
            
            if (isPotentialCustomer(cursor.getInt(CustomerDetailQuery._ID))) {
            	if (selectedLinkCustomerNo != null) {
            		Cursor c = getActivity().getContentResolver().query(Customers.CONTENT_URI, new String[] { Customers.CUSTOMER_NO, Customers.NAME }, Customers.CUSTOMER_NO + "=?", new String[] { selectedLinkCustomerNo }, null);
    				if (c.moveToFirst()) {
    					acCustomerLink.setText(String.format("%s - %s", c.getString(0), c.getString(1)));
    				}
    				c.close();
				}
				customer_link_group.setVisibility(View.VISIBLE);
            } else {
            	customer_link_group.setVisibility(View.GONE);
			}
            
            azurirajCustomerBalanceAsCommission(customernoString);
            
            int customerId = cursor.getInt(CustomerDetailQuery._ID);
            mCallbacks.onCustomerIdAvailable(String.valueOf(customerId));
            LogUtils.LOGI(TAG, "Loaded customer id: " + String.valueOf(customerId));
        }
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
	
	public void setFocusable(boolean disable){
		mAddress.setFocusable(disable);
		mAddress2.setFocusable(disable);
		mName.setFocusable(disable);
		mName2.setFocusable(disable);
		mEmail.setFocusable(disable);
		mPostCode.setFocusable(disable);
		mMobile.setFocusable(disable);
		mCompanyId.setFocusable(disable);
		mVarRegNo.setFocusable(disable);
		mPrimaryContactId.setFocusable(disable);
		mVarRegNo.setFocusable(disable);
		mPhone.setFocusable(disable);
		mCustomerType.setClickable(disable);
		mCustomerPosition.setClickable(disable);
		acCustomerLink.setFocusable(disable);
	}
	
	private void saveForm(){
		ContentValues contentValues = new ContentValues();
		contentValues.put(Customers.ADDRESS, mAddress.getText().toString());
		contentValues.put(Customers.ADDRESS_2, mAddress2.getText().toString());
		contentValues.put(Customers.NAME, mName.getText().toString());
		contentValues.put(Customers.NAME_2, mName2.getText().toString());
		contentValues.put(Customers.EMAIL, mEmail.getText().toString());
		if (cityCursorItem != null) {
			contentValues.put(Customers.CITY, cityCursorItem.getString(2));
			contentValues.put(Customers.POST_CODE, cityCursorItem.getString(1));
		} else {
			contentValues.put(Customers.CITY, mCity.getText().toString());
			contentValues.put(Customers.POST_CODE, mPostCode.getText().toString());
		}
		contentValues.put(Customers.COMPANY_ID, mCompanyId.getText().toString());
		contentValues.put(Customers.VAT_REG_NO, mVarRegNo.getText().toString());
		contentValues.put(Customers.PHONE, mPhone.getText().toString());
		
		contentValues.put(Customers.CUSTOMER_TYPE, mCustomerType.getSelectedItemPosition());
		
		String[] customerPositionValueArray = getResources().getStringArray(R.array.pozicija_value_array);
		contentValues.put(Customers.CUSTOMER_POSITION, customerPositionValueArray[mCustomerPosition.getSelectedItemPosition()]);
		
		if (selectedLinkCustomerNo != null) {
			contentValues.put(Customers.CUSTOMER_LINK, selectedLinkCustomerNo);
		} else {
			contentValues.putNull(Customers.CUSTOMER_LINK);
		}
		
		int result = getActivity().getContentResolver().update(mCustomerdetailUri, contentValues, null, null);
		if (result > 0) {
			try {
				if (!isPotentialCustomer(Integer.valueOf(Customers.getCustomersId(mCustomerdetailUri)))) {
					updateCustomer(Integer.valueOf(MobileStoreContract.Customers.getCustomersId(mCustomerdetailUri)));
				} else {
					sendPotentialCustomer(Integer.valueOf(MobileStoreContract.Customers.getCustomersId(mCustomerdetailUri)));
				}
			} catch (Exception e) {
				LogUtils.LOGE(TAG, "Big problem!", e);
			}
		}
	}
	
	private boolean isPotentialCustomer(int customerId) {
		Cursor potentialCustomerCursor = getActivity().getContentResolver().query(MobileStoreContract.Customers.CONTENT_URI, new String[] {Customers.CONTACT_COMPANY_NO}, 
				"("+Customers.CONTACT_COMPANY_NO + " is null or " + Customers.CONTACT_COMPANY_NO + "='')" + " and " + Customers._ID + "=?" , new String[] { String.valueOf(customerId) }, null);
		
		if (potentialCustomerCursor.moveToFirst()) {
			return true;
		}
		potentialCustomerCursor.close();
		return false;
	}
	
	private void updateCustomer(int customerId) {    	
    	UpdateCustomerSyncObject updateCustomersSyncObject = new UpdateCustomerSyncObject(customerId);
    	Intent intent = new Intent(getActivity(), NavisionSyncService.class);
		intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, updateCustomersSyncObject);
		getActivity().startService(intent);	
	}
	
	private void sendPotentialCustomer(int customerId) {    	
    	SetPotentialCustomersSyncObject potentialCustomersSyncObject = new SetPotentialCustomersSyncObject(customerId);
    	// it will not send signal to create customer
    	potentialCustomersSyncObject.setpPendingCustomerCreation(Integer.valueOf(0));
    	Intent intent = new Intent(getActivity(), NavisionSyncService.class);
		intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, potentialCustomersSyncObject);
		getActivity().startService(intent);	
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
        		Customers._ID,
                Customers.CUSTOMER_NO,
                Customers.NAME,
                Customers.NAME_2,
                Customers.ADDRESS,
                Customers.ADDRESS_2,
                Customers.CITY,
                Customers.POST_CODE,
                Customers.PHONE,
                Customers.EMAIL,
                Customers.COMPANY_ID,
                Customers.VAT_REG_NO,
                Customers.PAYMENT_TERMS_CODE,
                Customers.CUSTOMER_TYPE,
                Customers.CUSTOMER_POSITION,
                Customers.CUSTOMER_LINK,
                Customers.CONTACT_COMPANY_NO
        };

        int _ID = 0;
        int CUSTOMER_NO = 1;
        int NAME = 2;
        int NAME_2 = 3;
        int ADDRESS = 4;
        int ADDRESS_2 = 5;
        int CITY = 6;
        int POST_CODE = 7;
        int PHONE = 8;
        int EMAIL = 9;
        int COMPANY_ID = 10;
        int VAT_REG_NO = 11;
        int PAYMENT_TERMS_CODE = 12;
        int CUSTOMER_TYPE = 13;
        int CUSTOMER_POSITION = 14;
        int CUSTOMER_LINK = 15;
        int CONTACT_COMPANY_NO = 16;
	}
	
	private void azurirajCustomerBalanceAsCommission(String salesPersonNo) {
		CustomerBalanceSyncObject customerBalanceAsCommissionSyncObject = new CustomerBalanceSyncObject(salesPersonNo, "", "", "");
		Intent syncCustomerBalanceAsCommission = new Intent(getActivity(), NavisionSyncService.class);
		syncCustomerBalanceAsCommission.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, customerBalanceAsCommissionSyncObject);
		getActivity().startService(syncCustomerBalanceAsCommission);
	}

}
