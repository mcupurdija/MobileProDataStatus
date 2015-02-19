package rs.gopro.mobile_store.ui;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.ui.components.CitiesAutocompleteCursorAdapter;
import rs.gopro.mobile_store.ui.components.CustomerAutocompleteCursorAdapter;
import rs.gopro.mobile_store.util.DialogUtil;
import rs.gopro.mobile_store.util.LogUtils;
import rs.gopro.mobile_store.util.exceptions.PotentialCustomerValidationException;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.SetPotentialCustomersSyncObject;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;

public class AddPotentialCustomerActivity extends BaseActivity {

	private static final String TAG = "AddPotentialCustomerActivity";
	
	private String selectedLinkCustomerNo = null;
	
	private EditText name, name2, address, address2, phone, email, pib, mb;
	private AutoCompleteTextView cityPostcode, acCustomerLink;
	private Spinner customerType, customerPosition;
    
	private String city, postcode;
	private CustomerAutocompleteCursorAdapter customerAutocompleteCursorAdapter;
    private CitiesAutocompleteCursorAdapter citiesAutocompleteCursorAdapter;
    private Cursor cityCursorItem;
    private ArrayAdapter<CharSequence> customerTypeAdapter, customerPositionAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_potential_customer2);
		
		name = (EditText) findViewById(R.id.add_customer_name1);
		name.setFilters( new InputFilter[] { new InputFilter.LengthFilter(50)} );
		
		name2 = (EditText) findViewById(R.id.add_customer_name2);
		name2.setFilters( new InputFilter[] { new InputFilter.LengthFilter(50)} );
		
		address = (EditText) findViewById(R.id.add_customer_address_value);
		address.setFilters( new InputFilter[] { new InputFilter.LengthFilter(50)} );
		
		address2 = (EditText) findViewById(R.id.add_customer_address_value2);
		address2.setFilters( new InputFilter[] { new InputFilter.LengthFilter(50)} );
		
		cityPostcode = (AutoCompleteTextView) findViewById(R.id.add_customer_city);
		citiesAutocompleteCursorAdapter = new CitiesAutocompleteCursorAdapter(this, null);
		cityPostcode.setAdapter(citiesAutocompleteCursorAdapter);
		cityPostcode.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				cityCursorItem = (Cursor) citiesAutocompleteCursorAdapter.getItem(position);
				if (cityCursorItem != null) {
					postcode = cityCursorItem.getString(1);
					city = cityCursorItem.getString(2);
				}
			}
		});
		
		phone = (EditText) findViewById(R.id.add_customer_phone);
		phone.setFilters( new InputFilter[] { new InputFilter.LengthFilter(30)} );
		
		email = (EditText) findViewById(R.id.add_customer_email_value);
		email.setFilters( new InputFilter[] { new InputFilter.LengthFilter(80)} );
		
		pib = (EditText) findViewById(R.id.add_customer_company_vat_no);
		pib.setFilters( new InputFilter[] { new InputFilter.LengthFilter(9)} );
		
		mb = (EditText) findViewById(R.id.add_customer_company_id);
		mb.setFilters( new InputFilter[] { new InputFilter.LengthFilter(8)} );
		
		customerType = (Spinner) findViewById(R.id.contact_department_input);
		customerTypeAdapter = ArrayAdapter.createFromResource(this, R.array.customer_type_array, android.R.layout.simple_spinner_item);
		customerTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		customerType.setAdapter(customerTypeAdapter);
		
		customerPosition = (Spinner) findViewById(R.id.contact_position_input);
		customerPositionAdapter = ArrayAdapter.createFromResource(this, R.array.pozicija_title_array, android.R.layout.simple_spinner_item);
		customerPositionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		customerPosition.setAdapter(customerPositionAdapter);
		
		acCustomerLink = (AutoCompleteTextView) findViewById(R.id.acCustomerLink);
		customerAutocompleteCursorAdapter = new CustomerAutocompleteCursorAdapter(this, null);
		acCustomerLink.setAdapter(customerAutocompleteCursorAdapter);
		acCustomerLink.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Cursor cursor = (Cursor) customerAutocompleteCursorAdapter.getItem(position);
				selectedLinkCustomerNo = cursor.getString(1);
			}
		});
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		
		outState.putString("city", city);
		outState.putString("postcode", postcode);
		outState.putString("selectedLinkCustomerNo", selectedLinkCustomerNo);
		
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		
		city = savedInstanceState.getString("city", null);
		postcode = savedInstanceState.getString("postcode", null);
		selectedLinkCustomerNo = savedInstanceState.getString("selectedLinkCustomerNo", null);
		
		super.onRestoreInstanceState(savedInstanceState);
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
			case R.id.save_potential_customer_menu_option:
				try {
					submitForm();
				} catch (PotentialCustomerValidationException pe) {
					LogUtils.LOGE(TAG, "", pe);
					DialogUtil.showInfoDialog(this, "Greška pri unosu podataka", pe.getMessage());
					return false;
				}
				finish();
				return true;
			case R.id.cancel_save_potential_customer_menu_option:
				finish();
				return true;
			
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void submitForm() throws PotentialCustomerValidationException {
		ContentValues contentValues = new ContentValues();
		
		contentValues.put(Customers.NAME, name.getText().toString());
		if (name.getText().toString().trim().length() < 1) {
			name.requestFocus();
			throw new PotentialCustomerValidationException("Niste uneli naziv kupca!");
		}
		
		contentValues.put(Customers.NAME_2, name2.getText().toString());
		
		contentValues.put(Customers.ADDRESS, address.getText().toString());
//		if (address.getText().toString().trim().length() < 1) {
//			address.requestFocus();
//			throw new PotentialCustomerValidationException("Niste uneli adresu kupca!");
//		}
		
		contentValues.put(Customers.ADDRESS_2, address2.getText().toString());
		
		if (city == null || postcode == null) {
			cityPostcode.requestFocus();
			throw new PotentialCustomerValidationException("Niste odabrali grad!");
		}
		contentValues.put(Customers.CITY, city);
		contentValues.put(Customers.POST_CODE, postcode);
		
		contentValues.put(Customers.PHONE, phone.getText().toString());
		
		contentValues.put(Customers.EMAIL, email.getText().toString());
		
		/*
		if (pib.getText().toString().trim().length() < 1) {
			pib.requestFocus();
			throw new PotentialCustomerValidationException("Niste uneli PIB kupca!");
		}
		contentValues.put(Customers.VAT_REG_NO, pib.getText().toString());
		
		if (mb.getText().toString().trim().length() < 1) {
			mb.requestFocus();
			throw new PotentialCustomerValidationException("Niste uneli matični broj kupca!");
		}
		*/
		
		contentValues.put(Customers.COMPANY_ID, mb.getText().toString());
		
		contentValues.put(Customers.CUSTOMER_TYPE, customerType.getSelectedItemPosition());
		
		String[] customerPositionValueArray = getResources().getStringArray(R.array.pozicija_value_array);
		contentValues.put(Customers.CUSTOMER_POSITION, customerPositionValueArray[customerPosition.getSelectedItemPosition()]);
		
		if (selectedLinkCustomerNo != null) {
			contentValues.put(Customers.CUSTOMER_LINK, selectedLinkCustomerNo);
		} else {
			contentValues.putNull(Customers.CUSTOMER_LINK);
		}
		
		contentValues.put(Customers.SALES_PERSON_ID, salesPersonId);
		
		contentValues.putNull(MobileStoreContract.Customers.CUSTOMER_NO);
		
		Uri mUri = getContentResolver().insert(MobileStoreContract.Customers.CONTENT_URI, contentValues);
		sendPotentialCustomer(Integer.valueOf(Customers.getCustomersId(mUri)));
	}
	
	private void sendPotentialCustomer(int customerId) {
    	SetPotentialCustomersSyncObject potentialCustomersSyncObject = new SetPotentialCustomersSyncObject(customerId);
    	potentialCustomersSyncObject.setpPendingCustomerCreation(Integer.valueOf(0));
    	Intent intent = new Intent(this, NavisionSyncService.class);
		intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, potentialCustomersSyncObject);
		startService(intent);
	}

}
