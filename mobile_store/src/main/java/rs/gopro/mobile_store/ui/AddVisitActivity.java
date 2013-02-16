package rs.gopro.mobile_store.ui;

import java.text.DecimalFormat;
import java.util.Calendar;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.provider.MobileStoreContract.Visits;
import rs.gopro.mobile_store.util.DateUtils;
import rs.gopro.mobile_store.util.LogUtils;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TimePicker;

public class AddVisitActivity extends BaseActivity implements LoaderCallbacks<Cursor> {

	//private static final int EXIST_VISIT_LOADER = 3;
	public static final String VISIT_ID = "VISIT_ID";
	private static final int DEPARTURE_DATE_PICKER = 3;
	private static final int ARRIVAL_DATE_PICKER = 2;
	private static final int VISIT_DATE_PICKER = 1;
//	private static final String CUSTOMER_TEXT = "customer_text";
//	private AutoCompleteTextView salePersionAutocomplete;
	private AutoCompleteTextView customerAutoComplete;
	SimpleCursorAdapter customerCursorAdapter;
//	SimpleCursorAdapter salePersonCusrosAdapter;
	EditText visitDateEditText;
//	EditText lineNumberEditText;
//	EditText entryTypeEditText;
	EditText odometerEditText;
	EditText departureTimeEditText;
	EditText arrivalTimeEditText;
	Spinner visitResultEditText;
	ArrayAdapter<CharSequence> visitResultAdapter;
	EditText noteEditText;

//	OnDateSetListener depaertureDateSetListener;
	OnTimeSetListener depaertureTimeSetListener;
//	OnDateSetListener arrivalDateSetListener;
	OnTimeSetListener arrivalTimeSetListener;
	OnDateSetListener visitDateSetListener;
	

	String selectedVisitId;

	static String[] CUSTOMER_PROJECTION = new String[] { MobileStoreContract.Customers._ID, MobileStoreContract.Customers.NAME };
	static String[] SALE_PROJECTION = new String[] { MobileStoreContract.SaleOrders._ID, MobileStoreContract.SaleOrders.SALES_ORDER_NO };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String visitId = getIntent().getStringExtra(VISIT_ID);
		selectedVisitId = visitId;
		getSupportLoaderManager().initLoader(1, null, this);
		getSupportLoaderManager().initLoader(2, null, this);
		getSupportLoaderManager().initLoader(VisitsQuery._TOKEN, null, this);
		setContentView(R.layout.activity_add_visit);

		visitDateEditText = (EditText) findViewById(R.id.visit_date_input);
//		lineNumberEditText = (EditText) findViewById(R.id.line_number_input);
//		entryTypeEditText = (EditText) findViewById(R.id.entry_type_input);
		
		odometerEditText = (EditText) findViewById(R.id.odometer_input);
		departureTimeEditText = (EditText) findViewById(R.id.departure_time_input);
		arrivalTimeEditText = (EditText) findViewById(R.id.arrival_time_input);
		visitResultAdapter = ArrayAdapter.createFromResource(this, R.array.visit_result_type_array, android.R.layout.simple_spinner_item);
		visitResultAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		visitResultEditText = (Spinner) findViewById(R.id.visit_result_input);
		visitResultEditText.setAdapter(visitResultAdapter);
		noteEditText = (EditText) findViewById(R.id.note_input);

		// mode new
		if (selectedVisitId == null) {
			((TableRow) findViewById(R.id.arrival_time_row)).setVisibility(View.GONE);
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
				String date = DateUtils.formatDatePickerDate(year, monthOfYear, dayOfMonth);
				visitDateEditText.setText(date);

			}
		};
		
		depaertureTimeSetListener = new OnTimeSetListener() {
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				DecimalFormat hourMinutFormat = new DecimalFormat("00"); 
				departureTimeEditText.setText(hourMinutFormat.format(hourOfDay)+":"+hourMinutFormat.format(minute));
			}
		};

//		salePersionAutocomplete = (AutoCompleteTextView) findViewById(R.id.sale_person_autocomplete);
		customerAutoComplete = (AutoCompleteTextView) findViewById(R.id.customer_autocomplete);
		customerCursorAdapter = new CustomerSimpleCusrsorAdapter(this, android.R.layout.simple_dropdown_item_1line, null, CUSTOMER_PROJECTION, new int[] { android.R.id.empty, android.R.id.text1 }, 0);

		customerCursorAdapter.setFilterQueryProvider(new FilterQueryProvider() {
			@Override
			public Cursor runQuery(CharSequence constraint) {
				Cursor cursor = null;
				if (getContentResolver() != null && constraint != null) {
					cursor = getContentResolver().query(Customers.buildCustomSearchUri(constraint == null ? "" : constraint.toString(), "0"), CUSTOMER_PROJECTION, null, null, null);
				}
				return cursor;
			}
		});
		customerAutoComplete.setAdapter(customerCursorAdapter);

//		salePersonCusrosAdapter = new SalePersonCustomSimpleCurorAdapter(this, android.R.layout.simple_dropdown_item_1line, null, SALE_PROJECTION, new int[] { android.R.id.empty, android.R.id.text1 }, 0);
//		salePersonCusrosAdapter.setFilterQueryProvider(new FilterQueryProvider() {
//
//			@Override
//			public Cursor runQuery(CharSequence constraint) {
//				Cursor cursor = null;
//				if (getContentResolver() != null && constraint != null) {
//					cursor = getContentResolver().query(MobileStoreContract.SaleOrders.buildCustomSearchUri(constraint == null ? "" : constraint.toString(), "1"), SALE_PROJECTION, null, null, null);
//				}
//				return cursor;
//			}
//		});
//
//		salePersionAutocomplete.setAdapter(salePersonCusrosAdapter);

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
//		salePersionAutocomplete.addTextChangedListener(new CustomTextWathcer(salePersonCusrosAdapter));
		customerAutoComplete.addTextChangedListener(new CustomTextWathcer(customerCursorAdapter));
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
		customerAutoComplete.setText(data.getString(VisitsQuery.NAME));
		visitDateEditText.setText(DateUtils.formatDbDateForPresentation(data.getString(VisitsQuery.VISIT_DATE)));
//		lineNumberEditText.setText(data.getString(VisitsQuery.VISIT_DATE));
//		entryTypeEditText.setText(data.getString(VisitsQuery.ENTRY_TYPE));
		if (selectedVisitId != null) {
			odometerEditText.setText(data.getString(VisitsQuery.ODOMETER));
			departureTimeEditText.setText(DateUtils.formatDbTimeForPresentation(data.getString(VisitsQuery.DEPARTURE_TIME)));
			String visit_result = data.getString(VisitsQuery.VISIT_RESULT);
			if (visit_result != null) {
				int spinnerPosition = visitResultAdapter.getPosition(visit_result);
				if (spinnerPosition != -1) {
					visitResultEditText.setSelection(spinnerPosition);
				} else {
					LogUtils.LOGE("AddVisitActivity", "No position for value:"+visit_result);
				}
			}
			noteEditText.setText(data.getString(VisitsQuery.NOTE)==null ? "":data.getString(VisitsQuery.NOTE));
		}

	}

	class CustomTextWathcer implements TextWatcher {
		SimpleCursorAdapter adapter;

		public CustomTextWathcer(SimpleCursorAdapter adapter) {
			this.adapter = adapter;
		}

		@Override
		public void afterTextChanged(Editable s) {
			String queryString = s.toString();
			adapter.getFilter().filter(queryString);
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}
	}

	private class CustomerSimpleCusrsorAdapter extends SimpleCursorAdapter {

		public CustomerSimpleCusrsorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
			super(context, layout, c, from, to, flags);
		}

		@Override
		public CharSequence convertToString(Cursor cursor) {
			final int columnIndex = cursor.getColumnIndexOrThrow(MobileStoreContract.Customers.NAME);
			final String str = cursor.getString(columnIndex);
			return str;

		}

	}

//	private class SalePersonCustomSimpleCurorAdapter extends SimpleCursorAdapter {
//
//		public SalePersonCustomSimpleCurorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
//			super(context, layout, c, from, to, flags);
//		}
//
//		@Override
//		public CharSequence convertToString(Cursor cursor) {
//			final int columnIndex = cursor.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.SALES_ORDER_NO);
//			final String str = cursor.getString(columnIndex);
//			return str;
//
//		}
//
//	}

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
		Object object = customerAutoComplete.getAdapter().getItem(0);
		Cursor customerCursor = (Cursor) object;
		if (customerCursor != null) {
			customerPk = customerCursor.getInt(customerCursor.getColumnIndexOrThrow(MobileStoreContract.Customers._ID));
			String customerName = customerCursor.getString(customerCursor.getColumnIndexOrThrow(MobileStoreContract.Customers.NAME));
			System.out.println("Customer id is : " + customerPk + " name is :" + customerName);
		}
//		Cursor salePersonCursor = (Cursor) salePersionAutocomplete.getAdapter().getItem(0);
//		if (salePersonCursor != null) {
//			Integer id = salePersonCursor.getInt(salePersonCursor.getColumnIndexOrThrow(MobileStoreContract.SaleOrders._ID));
//			String no = salePersonCursor.getString(salePersonCursor.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.SALES_ORDER_NO));
//		}
		ContentValues contentValues = new ContentValues();
		contentValues.put(Visits.SALES_PERSON_ID, 1);
		contentValues.put(Visits.VISIT_DATE, DateUtils.formatPickerInputForDb(visitDateEditText.getText().toString()));
		contentValues.put(Visits.CUSTOMER_ID, customerPk);
		contentValues.put(MobileStoreContract.Visits.VISIT_TYPE, "0");
		contentValues.put(MobileStoreContract.Visits.IS_SENT, Integer.valueOf(0));
//		contentValues.put(Visits.LINE_NO, lineNumberEditText.getText().toString());
//		contentValues.put(Visits.ENTRY_TYPE, entryTypeEditText.getText().toString());
		if (selectedVisitId != null) {
			contentValues.put(Visits.ODOMETER, odometerEditText.getText().toString());
			contentValues.put(Visits.DEPARTURE_TIME, DateUtils.formatPickerTimeInputForDb(departureTimeEditText.getText().toString()));
			contentValues.put(Visits.ARRIVAL_TIME, DateUtils.formatPickerTimeInputForDb(arrivalTimeEditText.getText().toString()));
			contentValues.put(Visits.VISIT_RESULT, visitResultEditText.getSelectedItem().toString());
			contentValues.put(Visits.NOTE, noteEditText.getText().toString());
		}
		String currentVisitId = null;
		if (selectedVisitId != null) {

			getContentResolver().update(MobileStoreContract.Visits.buildVisitUri(selectedVisitId), contentValues, null, null);
			currentVisitId = selectedVisitId;

		} else {
			Uri resultedUri = getContentResolver().insert(MobileStoreContract.Visits.CONTENT_URI, contentValues);
			currentVisitId = resultedUri.getPathSegments().get(1);
		}

		/*Intent returnIntent = new Intent();
		returnIntent.putExtra(VISIT_ID, currentVisitId);*/
		// setResult(RESULT_OK,returnIntent);

//		final Uri visitsUri = MobileStoreContract.Visits.CONTENT_URI;
//		final Intent intent = new Intent(Intent.ACTION_VIEW, MobileStoreContract.Visits.buildVisitUri(currentVisitId));
//		intent.putExtra(VisitsMultipaneActivity.EXTRA_MASTER_URI, visitsUri);
//		startActivity(intent);
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

		String[] PROJECTION = { BaseColumns._ID, MobileStoreContract.Visits.SALES_PERSON_ID, MobileStoreContract.Visits.CUSTOMER_ID, MobileStoreContract.Visits.VISIT_DATE, MobileStoreContract.Visits.CUSTOMER_NO, MobileStoreContract.Visits.NAME,
				MobileStoreContract.Visits.LINE_NO, MobileStoreContract.Visits.ENTRY_TYPE, MobileStoreContract.Visits.ARRIVAL_TIME, MobileStoreContract.Visits.DEPARTURE_TIME, MobileStoreContract.Visits.ODOMETER, MobileStoreContract.Visits.NOTE,
				MobileStoreContract.Visits.VISIT_RESULT, };

		//int _ID = 0;
		//int SALES_PERSON_ID = 1;
		//int CUSTOMER_ID = 2;
		int VISIT_DATE = 3;
		//int CUSTOMER_NO = 4;
		int NAME = 5;
		//int LINE_NO = 6;
		//int ENTRY_TYPE = 7;
		int ARRIVAL_TIME = 8;
		int DEPARTURE_TIME = 9;
		int ODOMETER = 10;
		int NOTE = 11;
		int VISIT_RESULT = 12;
	}
}
