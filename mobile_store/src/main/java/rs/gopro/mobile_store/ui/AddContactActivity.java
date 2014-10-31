package rs.gopro.mobile_store.ui;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.Contacts;
import rs.gopro.mobile_store.ui.components.CustomerAutocompleteCursorAdapter;
import rs.gopro.mobile_store.util.ApplicationConstants.SyncStatus;
import rs.gopro.mobile_store.util.DialogUtil;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.SetContactsSyncObject;
import rs.gopro.mobile_store.ws.model.SyncResult;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
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
import android.widget.Toast;

public class AddContactActivity extends BaseActivity implements LoaderCallbacks<Cursor> {

	public static final String TAG = "AddEditContactActivity";
	public static final String CONTACT_ID = "contact_id";

	private EditText primaryName;
	private EditText secondaryName;
	private EditText phone;
	private EditText mobilePhone;
	private EditText email;
	private AutoCompleteTextView companyNo;
	private CustomerAutocompleteCursorAdapter customerAutoCompleteAdapter;
	private Spinner department;
	private Spinner position;
	
	private ArrayAdapter<CharSequence> arrayDepartment;
	private ArrayAdapter<CharSequence> arrayPosition;

	String selectedContactId;	
	String selectedCompanyId;
	String selectedCompanyNo;
	
	protected BroadcastReceiver onNotice = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			SyncResult syncResult = intent.getParcelableExtra(NavisionSyncService.SYNC_RESULT);
			onSOAPResult(syncResult, intent.getAction());
		}
	};
	
	public void onSOAPResult(SyncResult syncResult, String broadcastAction) {
		if (syncResult.getStatus().equals(SyncStatus.SUCCESS)) {
			if (SetContactsSyncObject.BROADCAST_SYNC_ACTION.equalsIgnoreCase(broadcastAction)) {
				Toast.makeText(getApplicationContext(), "Kontakt je uspešno sinhronizovan", Toast.LENGTH_SHORT).show();
				finish();
			}
		} else {
			DialogUtil.showInfoDialog(this, getResources().getString(R.string.dialog_title_error_in_sync), syncResult.getResult());
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportLoaderManager().initLoader(0, null, this);
		selectedContactId = getIntent().getStringExtra(CONTACT_ID);
		Log.d(TAG, "Selected id: " + selectedContactId);
		
		setContentView(R.layout.activity_add_contact);

		primaryName = (EditText) findViewById(R.id.contact_name_input);
		secondaryName = (EditText) findViewById(R.id.contact_name_2_input);
		phone = (EditText) findViewById(R.id.contact_phone_input);
		mobilePhone = (EditText) findViewById(R.id.contact_mobile_phone_input);
		email = (EditText) findViewById(R.id.contact_email_input);
		companyNo = (AutoCompleteTextView) findViewById(R.id.contact_company_no_input);
		department = (Spinner) findViewById(R.id.contact_department_input);
		position = (Spinner) findViewById(R.id.contact_position_input);
		
		customerAutoCompleteAdapter = new CustomerAutocompleteCursorAdapter(this, null);
		companyNo.setAdapter(customerAutoCompleteAdapter);
		companyNo.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Cursor cursor = (Cursor) customerAutoCompleteAdapter.getItem(position);
				selectedCompanyId = cursor.getString(0);
				selectedCompanyNo = cursor.getString(4);
			}
		});
		
		arrayDepartment = ArrayAdapter.createFromResource(this, R.array.contact_department_array, android.R.layout.simple_spinner_item);
		arrayPosition = ArrayAdapter.createFromResource(this, R.array.contact_position_array, android.R.layout.simple_spinner_item);
		department.setAdapter(arrayDepartment);
		position.setAdapter(arrayPosition);
		department.setSelection(0);
		position.setSelection(0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.contact_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.submit_contact_form:
			submitForm();
			//finish();
			return true;
		case R.id.cancel_contact_form:
			finish();
			return true;
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		CursorLoader cursorLoader = null;
		if (selectedContactId != null) {
			cursorLoader = new CursorLoader(this, Contacts.buildContactsUri(selectedContactId), ContactQuery.PROJECTION, null, null, MobileStoreContract.Contacts.DEFAULT_SORT);
		}
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		loadUi(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}

	private void loadUi(Cursor data) {
		if (data.moveToFirst()) {
			primaryName.setText(data.getString(ContactQuery.NAME));
			secondaryName.setText(data.getString(ContactQuery.NAME2));

			phone.setText(data.getString(ContactQuery.PHONE));
			//mobilePhone.setText(data.getString(ContactQuery.MOBILE_PHONE));
			email.setText(data.getString(ContactQuery.EMAIL));
			
			String customerCompanyNo = data.getString(ContactQuery.COMPANY_NO);
			if (customerCompanyNo != null) {
				Cursor c = getContentResolver().query(MobileStoreContract.Customers.getCustomersByContactCompanyNo(data.getString(ContactQuery.COMPANY_NO)), new String[] {MobileStoreContract.Customers.CUSTOMER_NO, MobileStoreContract.Customers.NAME}, null, null, null);
	        	if (c.moveToFirst()) {
		        	final int codeIndex = c.getColumnIndexOrThrow(MobileStoreContract.Customers.CUSTOMER_NO);
		    		final int nameIndex = c.getColumnIndexOrThrow(MobileStoreContract.Customers.NAME);
		    		final String result = c.getString(codeIndex) + " - " + c.getString(nameIndex);
		    		companyNo.setText(result);
		    		
		    		selectedCompanyId = c.getString(codeIndex);
		    		selectedCompanyNo = customerCompanyNo;
	        	}
			}

			department.setSelection(data.getInt(ContactQuery.DEPARTMENT));
			position.setSelection(data.getInt(ContactQuery.POSITION));
		}
	}

	private void submitForm() {
		
		String name = primaryName.getText().toString();
		int depPos = department.getSelectedItemPosition(), posPos = position.getSelectedItemPosition();
		
		if (name.trim().length() == 0) {
			Toast.makeText(getApplicationContext(), getString(R.string.kontakt_validacija_ime), Toast.LENGTH_SHORT).show();
			primaryName.requestFocus();
		} else if (depPos == 0) {
			Toast.makeText(getApplicationContext(), getString(R.string.kontakt_validacija_odeljenje), Toast.LENGTH_SHORT).show();
			department.requestFocus();
		} else if (posPos == 0) {
			Toast.makeText(getApplicationContext(), getString(R.string.kontakt_validacija_pozicija), Toast.LENGTH_SHORT).show();
			position.requestFocus();
		} else {
			ContentValues contentValues = new ContentValues();
			contentValues.put(Contacts.NAME, name);
			contentValues.put(Contacts.NAME2, secondaryName.getText().toString());

			contentValues.put(Contacts.PHONE, phone.getText().toString());
			//contentValues.put(Contacts.MOBILE_PHONE, mobilePhone.getText().toString());
			contentValues.put(Contacts.EMAIL, email.getText().toString());
			if (companyNo.getText().toString().trim().length() > 0) {
				contentValues.put(Contacts.COMPANY_NO, selectedCompanyNo);
			} else {
				contentValues.putNull(Contacts.COMPANY_NO);
			}
			
			contentValues.put(Contacts.DEPARTMENT, depPos);
			contentValues.put(Contacts.POSITION, posPos);
			
			contentValues.put(Contacts.SALES_PERSON_ID, salesPersonId);	
			int currentContactId;
			if (selectedContactId != null) {
				getContentResolver().update(MobileStoreContract.Contacts.buildContactsUri(selectedContactId), contentValues, null, null);
				currentContactId = Integer.valueOf(selectedContactId);
			} else {
				Uri resultedUri = getContentResolver().insert(MobileStoreContract.Contacts.CONTENT_URI, contentValues);
				currentContactId = (int) ContentUris.parseId(resultedUri);
			}
			
			System.out.println("Contact ID: "+ currentContactId);
			sinhronizujKontakt(currentContactId);
		}
	}
	
	private void sinhronizujKontakt(int id) {
		Intent serviceIntent = new Intent(this, NavisionSyncService.class);
		SetContactsSyncObject setContactSyncObject = new SetContactsSyncObject(id);
		serviceIntent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, setContactSyncObject);
		startService(serviceIntent);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(onNotice);
	}

	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter setContactSyncObject = new IntentFilter(SetContactsSyncObject.BROADCAST_SYNC_ACTION);
    	LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, setContactSyncObject);
	}

	private interface ContactQuery {
		String[] PROJECTION = { Contacts._ID, Contacts.CONTACT_NO, Contacts.CONTACT_TYPE, Contacts.NAME, Contacts.NAME2, Contacts.ADDRESS,
				Contacts.CITY, Contacts.POST_CODE, Contacts.PHONE, Contacts.MOBILE_PHONE, Contacts.EMAIL, Contacts.COMPANY_NO,
				Contacts.COMPANY_ID, Contacts.VAT_REGISTRATION, Contacts.SALES_PERSON_ID, Contacts.DIVISION, Contacts.NUMBER_OF_BLUE_COAT,
				Contacts.NUMBER_OF_GREY_COAT, Contacts.JOB_TITLE,
				Contacts.DEPARTMENT, Contacts.POSITION};
//		int _ID = 0;
//		int CONTACT_NO = 1;
//		int CONTACT_TYPE = 2;
		int NAME = 3;
		int NAME2 = 4;
//		int ADDRESS = 5;
//		int CITY = 6;
//		int POST_CODE = 7;
		int PHONE = 8;
//		int MOBILE_PHONE = 9;
		int EMAIL = 10;
		int COMPANY_NO = 11;
//		int COMPANY_ID = 12;
//		int VAT_REGISTRATION = 13;
//		int SALES_PERSON_ID = 14;
//		int DIVISION = 15;
//		int NUMBER_OF_BLUE_COAT = 16;
//		int NUMBER_OF_GREY_COAT = 17;
//		int JOB_TITLE = 18;
		int DEPARTMENT = 19;
		int POSITION = 20;
	}

}
