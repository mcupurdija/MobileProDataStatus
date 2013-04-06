package rs.gopro.mobile_store.ui;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.provider.MobileStoreContract.Visits;
import rs.gopro.mobile_store.ui.components.CustomerAutocompleteCursorAdapter;
import rs.gopro.mobile_store.util.ApplicationConstants;
import rs.gopro.mobile_store.util.DateUtils;
import rs.gopro.mobile_store.util.DialogUtil;
import rs.gopro.mobile_store.util.LogUtils;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TimePicker;

public class AddVisitActivity extends BaseActivity implements LoaderCallbacks<Cursor> {

	private static final String TAG = "AddVisitActivity";
	
	public static final String VISIT_ID = "VISIT_ID";
	public static final String VISIT_TYPE = "VISIT_TYPE";
	private static final int DEPARTURE_DATE_PICKER = 3;
	private static final int ARRIVAL_DATE_PICKER = 2;
	private static final int VISIT_DATE_PICKER = 1;
	private AutoCompleteTextView customerAutoComplete;
	private CustomerAutocompleteCursorAdapter customerCursorAdapter;
	private EditText visitDateEditText;
	private EditText odometerEditText;
	private EditText departureTimeEditText;
	private EditText arrivalTimeEditText;
	private Spinner visitResultEditText;
	private ArrayAdapter<CharSequence> visitResultAdapter;
	private EditText noteEditText;

	private OnTimeSetListener depaertureTimeSetListener;
	private OnTimeSetListener arrivalTimeSetListener;
	private OnDateSetListener visitDateSetListener;

	private String selectedCustomerNo = null; 

	private String selectedVisitId;
	private String selectedVisitType;
	
	static String[] CUSTOMER_PROJECTION = new String[] { MobileStoreContract.Customers._ID, MobileStoreContract.Customers.NAME };
	static String[] SALE_PROJECTION = new String[] { MobileStoreContract.SaleOrders._ID, MobileStoreContract.SaleOrders.SALES_ORDER_NO };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String visitId = getIntent().getStringExtra(VISIT_ID);
		selectedVisitId = visitId;
		String visitType = getIntent().getStringExtra(VISIT_TYPE);
		selectedVisitType = visitType;
		getSupportLoaderManager().initLoader(1, null, this);
		getSupportLoaderManager().initLoader(2, null, this);
		getSupportLoaderManager().initLoader(VisitsQuery._TOKEN, null, this);
		setContentView(R.layout.activity_add_visit);

		visitDateEditText = (EditText) findViewById(R.id.visit_date_input);
		
		odometerEditText = (EditText) findViewById(R.id.odometer_input);
		departureTimeEditText = (EditText) findViewById(R.id.departure_time_input);
		arrivalTimeEditText = (EditText) findViewById(R.id.arrival_time_input);
		visitResultAdapter = ArrayAdapter.createFromResource(this, R.array.visit_result_type_array, android.R.layout.simple_spinner_item);
		visitResultAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		visitResultEditText = (Spinner) findViewById(R.id.visit_result_input);
		visitResultEditText.setAdapter(visitResultAdapter);
		noteEditText = (EditText) findViewById(R.id.note_input);

		// mode new
		if (selectedVisitId == null || selectedVisitType.equals("0")) {
			visitDateEditText.setText(DateUtils.getPickerDate(new Date()));
			arrivalTimeEditText.setText(DateUtils.getPickerTime(new Date()));
			((TableRow) findViewById(R.id.departure_time_row)).setVisibility(View.GONE);
			((TableRow) findViewById(R.id.odometer_row)).setVisibility(View.GONE);
			((TableRow) findViewById(R.id.visit_result_row)).setVisibility(View.GONE);
			((TableRow) findViewById(R.id.note_row)).setVisibility(View.GONE);
		}
		
		visitDateEditText.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				showDialog(VISIT_DATE_PICKER);
				return false;
			}
		});
		arrivalTimeEditText.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				showDialog(ARRIVAL_DATE_PICKER);
				return false;
			}
		});
		departureTimeEditText.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				showDialog(DEPARTURE_DATE_PICKER);
				return false;
			}
		});

		arrivalTimeSetListener = new OnTimeSetListener() {
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				DecimalFormat hourMinutFormat = new DecimalFormat("00"); 
				arrivalTimeEditText.setText(hourMinutFormat.format(hourOfDay)+":"+hourMinutFormat.format(minute));
			}
		};
		
		visitDateSetListener = new OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				Calendar input = Calendar.getInstance();
				input.set(year, monthOfYear, dayOfMonth);
				
				Calendar today = Calendar.getInstance();
				
				if (org.apache.commons.lang3.time.DateUtils.isSameDay(input, today)) {
					String date = DateUtils.formatDatePickerDate(year, monthOfYear, dayOfMonth);
					visitDateEditText.setText(date);
				} else if (input.before(today)) {
				   DialogUtil.showInfoDialog(AddVisitActivity.this, "Greska", "Ne mozete planirati unazad!");
				} else {
					String date = DateUtils.formatDatePickerDate(year, monthOfYear, dayOfMonth);
					visitDateEditText.setText(date);
				}
			}
		};
		
		depaertureTimeSetListener = new OnTimeSetListener() {
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				DecimalFormat hourMinutFormat = new DecimalFormat("00"); 
				departureTimeEditText.setText(hourMinutFormat.format(hourOfDay)+":"+hourMinutFormat.format(minute));
			}
		};

		customerAutoComplete = (AutoCompleteTextView) findViewById(R.id.customer_autocomplete);
		customerCursorAdapter = new CustomerAutocompleteCursorAdapter(this, null); 
		customerAutoComplete.setAdapter(customerCursorAdapter);
		customerAutoComplete.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View arg1, int position, long id) {
				Cursor cursor = (Cursor)parent.getAdapter().getItem(position);
				int customerNoPosition = cursor.getColumnIndexOrThrow(MobileStoreContract.Customers.CUSTOMER_NO);
				selectedCustomerNo = cursor.getString(customerNoPosition);
			}
		});

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
//		salePersionAutocomplete.addTextChangedListener(new CustomTextWathcer(salePersonCusrosAdapter));
//		customerAutoComplete.addTextChangedListener(new CustomTextWathcer(customerCursorAdapter));
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.plan_of_visits_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		switch (id) {
		case VisitsQuery._TOKEN:
			CursorLoader cursorLoader = null;
			if (selectedVisitId != null) {
				cursorLoader = new CursorLoader(this, Visits.buildVisitUri(selectedVisitId), VisitsQuery.PROJECTION, null, null, MobileStoreContract.Visits.DEFAULT_SORT);
			}
			return cursorLoader;

		default:
			break;
		}
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		switch (loader.getId()) {
		case VisitsQuery._TOKEN:
			loadUI(data);
		default:
			data.close();
			break;
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}

	private void loadUI(Cursor data) {
		data.moveToFirst();
		arrivalTimeEditText.setText(DateUtils.formatDbTimeForPresentation(data.getString(VisitsQuery.ARRIVAL_TIME)));
//		customerAutoComplete.setText(data.getString(VisitsQuery.NAME));
		final int codeIndex = data.getColumnIndexOrThrow(MobileStoreContract.Customers.CUSTOMER_NO);
		final int nameIndex = data.getColumnIndexOrThrow(MobileStoreContract.Customers.NAME);
		final String result = data.getString(codeIndex) + " - " + data.getString(nameIndex);
		selectedCustomerNo = data.getString(codeIndex);
		CustomerAutocompleteCursorAdapter dummyAdapter = null;
		customerAutoComplete.setAdapter(dummyAdapter);
		customerAutoComplete.setText(result);
		customerCursorAdapter.setIdForTitle(result, data.getInt(data.getColumnIndexOrThrow(MobileStoreContract.Customers._ID)));
		customerAutoComplete.setAdapter(customerCursorAdapter);
		visitDateEditText.setText(DateUtils.formatDbDateForPresentation(data.getString(VisitsQuery.VISIT_DATE)));
//		lineNumberEditText.setText(data.getString(VisitsQuery.VISIT_DATE));
//		entryTypeEditText.setText(data.getString(VisitsQuery.ENTRY_TYPE));
		if (selectedVisitId != null) {
			odometerEditText.setText(data.getString(VisitsQuery.ODOMETER));
			departureTimeEditText.setText(DateUtils.formatDbTimeForPresentation(data.getString(VisitsQuery.DEPARTURE_TIME)));
			int visit_result_id = -1;
			if (!data.isNull(VisitsQuery.VISIT_RESULT)) {
				visit_result_id = data.getInt(VisitsQuery.VISIT_RESULT);
			}
			//String visit_result = data.getString(VisitsQuery.VISIT_RESULT);
			if (visit_result_id != -1) {
				int spinnerPosition = visit_result_id;
				if (spinnerPosition != -1) {
					visitResultEditText.setSelection(spinnerPosition);
				} else {
					LogUtils.LOGE("AddVisitActivity", "No position for value:"+visit_result_id);
				}
			}
			noteEditText.setText(data.getString(VisitsQuery.NOTE)==null ? "":data.getString(VisitsQuery.NOTE));
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.submit_visit_form:
			submitForm();
			finish();
			return true;
		case R.id.canel_visit_form:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void submitForm() {
		Integer customerPk = null;
		
		String customer_auto_complete = customerAutoComplete.getText().toString().trim();
		if (customerCursorAdapter.getIdForTitle(customer_auto_complete) != -1) {
			//Cursor customerItemCursor = (Cursor) customerAutoCompleteAdapter.getItem(customerAutoCompleteAdapter.getIdForTitle(customer_auto_complete));
			int customer_id = customerCursorAdapter.getIdForTitle(customer_auto_complete);//customerItemCursor.getInt(customerItemCursor.getColumnIndexOrThrow(MobileStoreContract.Customers._ID));
			customerPk = Integer.valueOf(customer_id);
		} else {
			LogUtils.LOGE(TAG, "Kupac nije izabran!");
		}

		ContentValues contentValues = new ContentValues();
		contentValues.put(Visits.SALES_PERSON_ID, salesPersonId);
		contentValues.put(Visits.VISIT_DATE, DateUtils.formatPickerInputForDb(visitDateEditText.getText().toString()));
		if (customerPk == null) {
			contentValues.putNull(Visits.CUSTOMER_ID);
		} else {
			Cursor isPotentialCustomer = getContentResolver().query(MobileStoreContract.Customers.CONTENT_URI, new String[] {Customers.CONTACT_COMPANY_NO}, 
					"("+Customers.CONTACT_COMPANY_NO + " is null or " + Customers.CONTACT_COMPANY_NO + "='')" + " and " + Customers._ID + "=?" , new String[] { String.valueOf(customerPk) }, null);
			if (isPotentialCustomer.moveToFirst()) {
				contentValues.put(Visits.POTENTIAL_CUSTOMER, 1);
			} else {
				contentValues.put(Visits.POTENTIAL_CUSTOMER, 0);
			}
			isPotentialCustomer.close();
			contentValues.put(Visits.CUSTOMER_ID, customerPk);		
		}
		
		contentValues.put(Visits.VISIT_TYPE, ApplicationConstants.VISIT_PLANNED);
		contentValues.put(MobileStoreContract.Visits.IS_SENT, Integer.valueOf(0));
		contentValues.put(Visits.ARRIVAL_TIME, DateUtils.formatPickerTimeInputForDb(arrivalTimeEditText.getText().toString()));
		contentValues.putNull(Visits.ODOMETER);
		contentValues.put(Visits.VISIT_STATUS, ApplicationConstants.VISIT_STATUS_NEW);

		if (selectedVisitType != null && selectedVisitType.equals("1")) { // selectedVisitId != null
			contentValues.put(Visits.ODOMETER, odometerEditText.getText().toString());
			contentValues.put(Visits.DEPARTURE_TIME, DateUtils.formatPickerTimeInputForDb(departureTimeEditText.getText().toString()));
			contentValues.put(Visits.VISIT_RESULT, visitResultEditText.getSelectedItemPosition());
			contentValues.put(Visits.NOTE, noteEditText.getText().toString());
			contentValues.put(Visits.VISIT_TYPE, ApplicationConstants.VISIT_RECORDED);
			contentValues.put(Visits.VISIT_STATUS, ApplicationConstants.VISIT_STATUS_FINISHED);
		}
		String currentVisitId = null;
		if (selectedVisitId != null) {

			getContentResolver().update(MobileStoreContract.Visits.buildVisitUri(selectedVisitId), contentValues, null, null);
			currentVisitId = selectedVisitId;

		} else {
			Uri resultedUri = getContentResolver().insert(MobileStoreContract.Visits.CONTENT_URI, contentValues);
			currentVisitId = resultedUri.getPathSegments().get(1);
		}

		LogUtils.LOGI(TAG, "Current visit id: "+currentVisitId);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Calendar calendar = Calendar.getInstance();
		switch (id) {
		case ARRIVAL_DATE_PICKER:
			//DatePickerDialog arrivalDatePicker = new DatePickerDialog(this, arrivalDateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
			TimePickerDialog arrivalTime = new TimePickerDialog(this, arrivalTimeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
			return arrivalTime;
		case VISIT_DATE_PICKER:
			DatePickerDialog visitDatePicker = new DatePickerDialog(this, visitDateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
			return visitDatePicker;
		case DEPARTURE_DATE_PICKER:
			//DatePickerDialog departureDatePicker = new DatePickerDialog(this, depaertureDateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
			TimePickerDialog departureTime = new TimePickerDialog(this, depaertureTimeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
			return departureTime;

		}
		return super.onCreateDialog(id);
	}

	private interface VisitsQuery {
		int _TOKEN = 3;

		String[] PROJECTION = { BaseColumns._ID, MobileStoreContract.Visits.SALES_PERSON_ID, MobileStoreContract.Visits.CUSTOMER_ID, MobileStoreContract.Visits.VISIT_DATE, MobileStoreContract.Customers.CUSTOMER_NO, MobileStoreContract.Customers.NAME,
				MobileStoreContract.Visits.LINE_NO, MobileStoreContract.Visits.ENTRY_TYPE, MobileStoreContract.Visits.ARRIVAL_TIME, MobileStoreContract.Visits.DEPARTURE_TIME, MobileStoreContract.Visits.ODOMETER, MobileStoreContract.Visits.NOTE,
				MobileStoreContract.Visits.VISIT_RESULT, };

		//int _ID = 0;
		//int SALES_PERSON_ID = 1;
		//int CUSTOMER_ID = 2;
		int VISIT_DATE = 3;
		//int CUSTOMER_NO = 4;
//		int NAME = 5;
		//int LINE_NO = 6;
		//int ENTRY_TYPE = 7;
		int ARRIVAL_TIME = 8;
		int DEPARTURE_TIME = 9;
		int ODOMETER = 10;
		int NOTE = 11;
		int VISIT_RESULT = 12;
	}
}
