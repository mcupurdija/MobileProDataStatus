package rs.gopro.mobile_store.ui;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.Contacts;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class AddContactActivity extends BaseActivity implements LoaderCallbacks<Cursor> {

	public static final String CONTACT_ID = "contact_id";

	private Spinner contactTypeSpinner;
	private EditText primaryName;
	private EditText secondaryName;
	private EditText address;
	private EditText city;
	private EditText postCode;
	private EditText phone;
	private EditText mobilePhone;
	private EditText email;
	private EditText companyNo;
	private EditText companyId;
	private EditText vatRegistration;
	private EditText division;
	private EditText numOfBlueCoat;
	private EditText numOfGrayCoat;
	private EditText jobTitle;

	String selectedContactId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportLoaderManager().initLoader(0, null, this);
		selectedContactId = getIntent().getStringExtra(CONTACT_ID);
		System.out.println(" u ACT " +selectedContactId);
		
		setContentView(R.layout.activity_add_contact);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.contact_type_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		contactTypeSpinner = (Spinner) findViewById(R.id.contact_type_sppiner);
		contactTypeSpinner.setAdapter(adapter);
		primaryName = (EditText) findViewById(R.id.contact_name_input);
		secondaryName = (EditText) findViewById(R.id.contact_name_2_input);
		address = (EditText) findViewById(R.id.contact_address_input);
		city = (EditText) findViewById(R.id.contact_city_input);
		postCode = (EditText) findViewById(R.id.contact_post_code_input);
		phone = (EditText) findViewById(R.id.contact_phone_input);
		mobilePhone = (EditText) findViewById(R.id.contact_mobile_phone_input);
		email = (EditText) findViewById(R.id.contact_email_input);
		companyId = (EditText) findViewById(R.id.contact_company_id_input);
		companyNo = (EditText) findViewById(R.id.contact_company_no_input);
		vatRegistration = (EditText) findViewById(R.id.contact_vat_registration_input);
		division = (EditText) findViewById(R.id.contact_division_input);
		numOfBlueCoat = (EditText) findViewById(R.id.contact_number_of_blue_coat_input);
		numOfGrayCoat = (EditText) findViewById(R.id.contact_number_of_grey_coat_input);
		jobTitle = (EditText) findViewById(R.id.contact_job_title_input);

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
			finish();
			break;
		case R.id.cancel_contact_form:
			finish();
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
		data.moveToFirst();
		contactTypeSpinner.setSelected(true);
		contactTypeSpinner.setSelection(data.getInt(ContactQuery.CONTACT_TYPE));
		primaryName.setText(data.getString(ContactQuery.NAME));
		secondaryName.setText(data.getString(ContactQuery.NAME2));
		address.setText(data.getString(ContactQuery.ADDRESS));
		city.setText(data.getString(ContactQuery.CITY));
		postCode.setText(data.getString(ContactQuery.POST_CODE));
		phone.setText(data.getString(ContactQuery.PHONE));
		mobilePhone.setText(data.getString(ContactQuery.MOBILE_PHONE));
		email.setText(data.getString(ContactQuery.EMAIL));
		companyNo.setText(data.getString(ContactQuery.COMPANY_NO));
		companyId.setText(data.getString(ContactQuery.COMPANY_ID));
		vatRegistration.setText(data.getString(ContactQuery.VAT_REGISTRATION));
		division.setText(data.getString(ContactQuery.DIVISION));
		numOfBlueCoat.setText(data.getString(ContactQuery.NUMBER_OF_BLUE_COAT));
		numOfGrayCoat.setText(data.getString(ContactQuery.NUMBER_OF_GREY_COAT));
		jobTitle.setText(data.getString(ContactQuery.JOB_TITLE));
		

	}

	private void submitForm() {
		ContentValues contentValues = new ContentValues();
		contentValues.put(Contacts.CONTACT_TYPE,contactTypeSpinner.getSelectedItemPosition());
		contentValues.put(Contacts.NAME, primaryName.getText().toString());
		contentValues.put(Contacts.NAME2, secondaryName.getText().toString());
		contentValues.put(Contacts.ADDRESS, address.getText().toString());
		contentValues.put(Contacts.CITY, city.getText().toString());
		contentValues.put(Contacts.POST_CODE, postCode.getText().toString());
		contentValues.put(Contacts.PHONE, phone.getText().toString());
		contentValues.put(Contacts.MOBILE_PHONE, mobilePhone.getText().toString());
		contentValues.put(Contacts.EMAIL, email.getText().toString());
		contentValues.put(Contacts.COMPANY_ID,companyId.getText().toString());
		contentValues.put(Contacts.COMPANY_NO, companyNo.getText().toString());
		contentValues.put(Contacts.VAT_REGISTRATION, vatRegistration.getText().toString());
		contentValues.put(Contacts.SALES_PERSON_ID, "1");
		contentValues.put(Contacts.DIVISION, division.getText().toString());
		contentValues.put(Contacts.NUMBER_OF_BLUE_COAT, numOfBlueCoat.getText().toString());
		contentValues.put(Contacts.NUMBER_OF_GREY_COAT,numOfGrayCoat.getText().toString());
		contentValues.put(Contacts.JOB_TITLE, jobTitle.getText().toString());		
		String currentContactId;
		if (selectedContactId != null) {
			getContentResolver().update(MobileStoreContract.Contacts.buildContactsUri(selectedContactId), contentValues, null, null);
			currentContactId = selectedContactId;
		} else {
			Uri resultedUri = getContentResolver().insert(MobileStoreContract.Contacts.CONTENT_URI, contentValues);
			currentContactId = MobileStoreContract.Contacts.getContactsId(resultedUri);
		}
		
		System.out.println("NOV ID JE "+ currentContactId);

	}

	private interface ContactQuery {
		String[] PROJECTION = { MobileStoreContract.Contacts._ID, MobileStoreContract.Contacts.CONTACT_NO, MobileStoreContract.Contacts.CONTACT_TYPE, MobileStoreContract.Contacts.NAME, MobileStoreContract.Contacts.NAME2, MobileStoreContract.Contacts.ADDRESS,
				MobileStoreContract.Contacts.CITY, MobileStoreContract.Contacts.POST_CODE, MobileStoreContract.Contacts.PHONE, MobileStoreContract.Contacts.MOBILE_PHONE, MobileStoreContract.Contacts.EMAIL, MobileStoreContract.Contacts.COMPANY_NO,
				MobileStoreContract.Contacts.COMPANY_ID, MobileStoreContract.Contacts.VAT_REGISTRATION, MobileStoreContract.Contacts.SALES_PERSON_ID, MobileStoreContract.Contacts.DIVISION, MobileStoreContract.Contacts.NUMBER_OF_BLUE_COAT,
				MobileStoreContract.Contacts.NUMBER_OF_GREY_COAT, MobileStoreContract.Contacts.JOB_TITLE, };
		int _ID = 0;
		int CONTACT_NO = 1;
		int CONTACT_TYPE = 2;
		int NAME = 3;
		int NAME2 = 4;
		int ADDRESS = 5;
		int CITY = 6;
		int POST_CODE = 7;
		int PHONE = 8;
		int MOBILE_PHONE = 9;
		int EMAIL = 10;
		int COMPANY_NO = 11;
		int COMPANY_ID = 12;
		int VAT_REGISTRATION = 13;
		int SALES_PERSON_ID = 14;
		int DIVISION = 15;
		int NUMBER_OF_BLUE_COAT = 16;
		int NUMBER_OF_GREY_COAT = 17;
		int JOB_TITLE = 18;

	}

}
