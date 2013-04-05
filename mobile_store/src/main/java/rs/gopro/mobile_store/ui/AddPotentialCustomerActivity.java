package rs.gopro.mobile_store.ui;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.util.LogUtils;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.SetPotentialCustomersSyncObject;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

public class AddPotentialCustomerActivity extends BaseActivity implements LoaderCallbacks<Cursor> {

	private static final String TAG = "AddPotentialCustomerActivity";
	
	private EditText primaryName;
	private EditText secondaryName;
	private EditText address;
	private EditText city;
	private EditText postCode;
	private EditText phone;
	private EditText mobilePhone;
	private EditText email;
//	private EditText companyNo;
	private EditText companyId;
	private EditText vatRegistration;
	private EditText global_dimension;
	private EditText numOfBlueCoat;
	private EditText numOfGrayCoat;
	
	private String mAction;
    private Uri mUri;
	
    private int customerId;
    private String newCustomerNo;
    
	public AddPotentialCustomerActivity() {
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		
//		selectedContactId = getIntent().getStringExtra(CONTACT_ID);
//		System.out.println(" u ACT " +selectedContactId);
		
		initComponents();
		
		routeIntent(getIntent(), savedInstanceState);
	}

	private void initComponents() {
		setContentView(R.layout.activity_add_potential_customer);

		primaryName = (EditText) findViewById(R.id.add_customer_name1);
		secondaryName = (EditText) findViewById(R.id.add_customer_name2);
		address = (EditText) findViewById(R.id.add_customer_address_value);
		city = (EditText) findViewById(R.id.add_customer_city);
		postCode = (EditText) findViewById(R.id.add_customer_postal_code);
		phone = (EditText) findViewById(R.id.add_customer_phone);
		mobilePhone = (EditText) findViewById(R.id.add_customer_mobile);
		email = (EditText) findViewById(R.id.add_customer_email_value);
		companyId = (EditText) findViewById(R.id.add_customer_company_id);
		
//		companyNo = (EditText) findViewById(R.id.contact_company_no_input);
		
		vatRegistration = (EditText) findViewById(R.id.add_customer_company_vat_no);
		global_dimension = (EditText) findViewById(R.id.add_customer_global_dimension);
		numOfBlueCoat = (EditText) findViewById(R.id.add_customer_blue_coats);
		numOfGrayCoat = (EditText) findViewById(R.id.add_customer_grey_coats);
	}

	private void routeIntent(Intent intent, Bundle savedInstanceState) {
		// get action from intent
		mAction = intent.getAction();
		if (mAction == null) {
			LogUtils.LOGE(TAG, "Activity called without action!");
			return;
		}
		// get URI from intent
		mUri = intent.getData();
		if (mUri == null) {
			LogUtils.LOGE(TAG, "Activity called without URI!");
			return;
		}
		
		// check action and route from there
		if (Intent.ACTION_INSERT.equals(mAction)) {
			ContentValues contentValues = new ContentValues();
			contentValues.putNull(MobileStoreContract.Customers.CUSTOMER_NO); // this is signal that customer is potential
			mUri = getContentResolver().insert(MobileStoreContract.Customers.CONTENT_URI, contentValues);
			String customer_id = MobileStoreContract.Customers.getCustomersId(mUri);
			newCustomerNo = "CUST/"+salesPersonNo+"-"+customer_id;
		}
		
		getSupportLoaderManager().initLoader(0, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(this, mUri, PotentialCustomerQuery.PROJECTION, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		loadUi(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
	    menuInflater.inflate(R.menu.potential_customer_add_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.cancel_save_potential_customer_menu_option:
			if (Intent.ACTION_INSERT.equals(mAction)) {
				getContentResolver().delete(MobileStoreContract.Customers.CONTENT_URI, Tables.CUSTOMERS+"._ID=?", new String[] { String.valueOf(customerId) });
			}
			finish();
			return true;
		case R.id.save_potential_customer_menu_option:
			submitForm();
			finish();
			return true;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void submitForm() {
		ContentValues contentValues = new ContentValues();
		contentValues.put(Customers.NAME, primaryName.getText().toString());
		contentValues.put(Customers.NAME_2, secondaryName.getText().toString());
		contentValues.put(Customers.ADDRESS, address.getText().toString());
		contentValues.put(Customers.CITY, city.getText().toString());
		contentValues.put(Customers.POST_CODE, postCode.getText().toString());
		contentValues.put(Customers.PHONE, phone.getText().toString());
		contentValues.put(Customers.MOBILE, mobilePhone.getText().toString());
		contentValues.put(Customers.EMAIL, email.getText().toString());
		contentValues.put(Customers.COMPANY_ID,companyId.getText().toString());
//		contentValues.put(Customers.COMPANY_NO, companyNo.getText().toString());
		contentValues.put(Customers.VAT_REG_NO, vatRegistration.getText().toString());
		contentValues.put(Customers.SALES_PERSON_ID, salesPersonId);
		contentValues.put(Customers.GLOBAL_DIMENSION, global_dimension.getText().toString());
		contentValues.put(Customers.NUMBER_OF_BLUE_COAT, numOfBlueCoat.getText().toString().length() < 1  ? "0":numOfBlueCoat.getText().toString());
		contentValues.put(Customers.NUMBER_OF_GREY_COAT,numOfGrayCoat.getText().toString().length() < 1  ? "0":numOfGrayCoat.getText().toString());

		if (Intent.ACTION_INSERT.equals(mAction)) {
			//contentValues.put(Customers.CUSTOMER_NO, newCustomerNo);
		}
		
		int result = getContentResolver().update(MobileStoreContract.Customers.CONTENT_URI, contentValues, Tables.CUSTOMERS + "._ID=?", new String[] { String.valueOf(customerId) });
		
		if (result > 0) {
			sendPotentialCustomer(customerId);
		}
	}
	
	private void sendPotentialCustomer(int customerId) {    	
    	SetPotentialCustomersSyncObject potentialCustomersSyncObject = new SetPotentialCustomersSyncObject(customerId);
    	potentialCustomersSyncObject.setpPendingCustomerCreation(Integer.valueOf(1));
    	Intent intent = new Intent(this, NavisionSyncService.class);
		intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, potentialCustomersSyncObject);
		startService(intent);	
	}

	private void loadUi(Cursor data) {
		if (data.moveToFirst()) {
			primaryName.setText(data.getString(PotentialCustomerQuery.NAME));
			secondaryName.setText(data.getString(PotentialCustomerQuery.NAME_2));
			address.setText(data.getString(PotentialCustomerQuery.ADDRESS));
			city.setText(data.getString(PotentialCustomerQuery.CITY));
			postCode.setText(data.getString(PotentialCustomerQuery.POST_CODE));
			phone.setText(data.getString(PotentialCustomerQuery.PHONE));
			mobilePhone.setText(data.getString(PotentialCustomerQuery.MOBILE));
			email.setText(data.getString(PotentialCustomerQuery.EMAIL));
			//companyNo.setText(data.getString(PotentialCustomerQuery.COMPANY_NO));
			companyId.setText(data.getString(PotentialCustomerQuery.COMPANY_ID));
			vatRegistration.setText(data.getString(PotentialCustomerQuery.VAT_REG_NO));
			global_dimension.setText(data.getString(PotentialCustomerQuery.GLOBAL_DIMENSION));
			numOfBlueCoat.setText(data.getString(PotentialCustomerQuery.NUMBER_OF_BLUE_COAT));
			numOfGrayCoat.setText(data.getString(PotentialCustomerQuery.NUMBER_OF_GREY_COAT));
			customerId = data.getInt(PotentialCustomerQuery.ID);
		} else {
			LogUtils.LOGE(TAG, "No data to load!");
		}		
	}
	
	private interface PotentialCustomerQuery {
		String[] PROJECTION = {
				MobileStoreContract.Customers.CUSTOMER_NO,
				MobileStoreContract.Customers.NAME, 
				MobileStoreContract.Customers.NAME_2,
				MobileStoreContract.Customers.ADDRESS,
				MobileStoreContract.Customers.CITY,

				MobileStoreContract.Customers.PHONE, 
				MobileStoreContract.Customers.MOBILE, 

				MobileStoreContract.Customers.SALES_PERSON_ID, 
				MobileStoreContract.Customers.VAT_REG_NO,
				MobileStoreContract.Customers.POST_CODE,
				MobileStoreContract.Customers.EMAIL, 
				MobileStoreContract.Customers.COMPANY_ID, 
				MobileStoreContract.Customers.GLOBAL_DIMENSION,
				MobileStoreContract.Customers.NUMBER_OF_BLUE_COAT, 
				MobileStoreContract.Customers.NUMBER_OF_GREY_COAT,
				MobileStoreContract.Customers._ID
        };
		
		int CUSTOMER_NO = 0;
		int NAME = 1; 
		int NAME_2 = 2;
		int ADDRESS = 3;
		int CITY = 4;

		int PHONE = 5; 
		int MOBILE = 6; 

		int SALES_PERSON_ID = 7; 
		int VAT_REG_NO = 8;
		int POST_CODE = 9;
		int EMAIL = 10; 
		int COMPANY_ID = 11; 
		int GLOBAL_DIMENSION = 12;
		int NUMBER_OF_BLUE_COAT = 13; 
		int NUMBER_OF_GREY_COAT = 14;
		int ID = 15;
	}

}
