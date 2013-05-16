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

	private EditText primaryName;
	private EditText secondaryName;

	private EditText phone;
	private EditText mobilePhone;
	private EditText email;
	private EditText companyNo;
	
	private EditText department;
	private EditText position;

	String selectedContactId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportLoaderManager().initLoader(0, null, this);
		selectedContactId = getIntent().getStringExtra(CONTACT_ID);
		System.out.println(" u ACT " +selectedContactId);
		
		setContentView(R.layout.activity_add_contact);

		primaryName = (EditText) findViewById(R.id.contact_name_input);
		secondaryName = (EditText) findViewById(R.id.contact_name_2_input);
		phone = (EditText) findViewById(R.id.contact_phone_input);
		mobilePhone = (EditText) findViewById(R.id.contact_mobile_phone_input);
		email = (EditText) findViewById(R.id.contact_email_input);
		companyNo = (EditText) findViewById(R.id.contact_company_no_input);
		department = (EditText) findViewById(R.id.contact_department_input);
		position = (EditText) findViewById(R.id.contact_position_input);
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
		data.moveToFirst();
		primaryName.setText(data.getString(ContactQuery.NAME));
		secondaryName.setText(data.getString(ContactQuery.NAME2));

		phone.setText(data.getString(ContactQuery.PHONE));
		mobilePhone.setText(data.getString(ContactQuery.MOBILE_PHONE));
		email.setText(data.getString(ContactQuery.EMAIL));
		companyNo.setText(data.getString(ContactQuery.COMPANY_NO));
		department.setText(data.getString(ContactQuery.DEPARTMENT));
		position.setText(data.getString(ContactQuery.POSITION));
	}

	private void submitForm() {
		ContentValues contentValues = new ContentValues();
		contentValues.put(Contacts.NAME, primaryName.getText().toString());
		contentValues.put(Contacts.NAME2, secondaryName.getText().toString());

		contentValues.put(Contacts.PHONE, phone.getText().toString());
		contentValues.put(Contacts.MOBILE_PHONE, mobilePhone.getText().toString());
		contentValues.put(Contacts.EMAIL, email.getText().toString());
		contentValues.put(Contacts.COMPANY_NO, companyNo.getText().toString());
		
		contentValues.put(Contacts.DEPARTMENT, department.getText().toString());
		contentValues.put(Contacts.POSITION, position.getText().toString());
		
		contentValues.put(Contacts.SALES_PERSON_ID, salesPersonId);		
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
		int POST_CODE = 7;
		int PHONE = 8;
		int MOBILE_PHONE = 9;
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
