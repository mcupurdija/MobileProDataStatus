package rs.gopro.mobile_store.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.gps.GpsModel;
import rs.gopro.mobile_store.gps.HttpPostRequest;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.ui.components.CustomerAutocompleteCursorAdapter;
import rs.gopro.mobile_store.util.ApplicationConstants;
import rs.gopro.mobile_store.util.ApplicationConstants.SyncStatus;
import rs.gopro.mobile_store.util.DatePickerFragment;
import rs.gopro.mobile_store.util.DateUtils;
import rs.gopro.mobile_store.util.LogUtils;
import rs.gopro.mobile_store.util.TimePickerFragment;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.SetRealizedVisitsToCustomersSyncObject;
import rs.gopro.mobile_store.ws.model.SyncResult;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;

public class NovaRealizacijaActivity extends BaseActivity implements LoaderCallbacks<Cursor>, LocationListener {

	private static final String VISITS_DATE_FILTER = "DATE(" + Tables.VISITS + "." + MobileStoreContract.Visits.VISIT_DATE + ")=DATE(?)";
	private static final String VISITS_FILTER_REALIZACIJA = Tables.VISITS + "." + MobileStoreContract.Visits.VISIT_TYPE + "=" + ApplicationConstants.VISIT_RECORDED;
	private static final String VISITS_FILTER_PLAN = Tables.VISITS + "." + MobileStoreContract.Visits.VISIT_TYPE + "=" + ApplicationConstants.VISIT_PLANNED;
	private static final String VISITS_FILTER_POCETAK_DANA = Tables.VISITS + "." + MobileStoreContract.Visits.VISIT_RESULT + "=" + ApplicationConstants.VISIT_TYPE_START_DAY;
	private static final String VISITS_FILTER_KRAJ_DANA = Tables.VISITS + "." + MobileStoreContract.Visits.VISIT_RESULT + "=" + ApplicationConstants.VISIT_TYPE_END_DAY;
	private static final String VISITS_FILTER_POVRATAK_KUCI = Tables.VISITS + "." + MobileStoreContract.Visits.VISIT_RESULT + "=" + ApplicationConstants.VISIT_TYPE_BACK_HOME;
	private static final String VISITS_FILTER_IS_VISIT_OPEN = Tables.VISITS + "." + MobileStoreContract.Visits.VISIT_STATUS + "=" + ApplicationConstants.VISIT_STATUS_STARTED;
	
	private CursorAdapter cursorAdapter, cursorAdapterPlan;
	private CustomerAutocompleteCursorAdapter customerCursorAdapter;
	
	private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
	private SimpleDateFormat screenDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
	private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
	private SimpleDateFormat shortTimeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
	
	private Button bZapocniDan, bNovaRealizacija, bPauza, bZavrsiDan, bPovratakKuci;
	private Button datumInput, vremeInput;
	private ListView lvRealizacija, lvPlan;
	private int selectedCustomerId;
	
	private Calendar calender = Calendar.getInstance();
	
	private static final Integer TIME_INTERVAL = 60 * 1000;
	private static final Integer DISTANCE = 0;
	private LocationManager locationManager;
	
	private Gson gson;
	private GpsModel gpsModel;
	
	private BroadcastReceiver onNotice = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			SyncResult syncResult = intent.getParcelableExtra(NavisionSyncService.SYNC_RESULT);
			onSOAPResult(syncResult, intent.getAction());
		}
	};

	protected void onSOAPResult(SyncResult syncResult, String broadcastAction) {
		if (syncResult.getStatus().equals(SyncStatus.FAILURE)) {
			Toast.makeText(getApplicationContext(), R.string.error_internet_status, Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nova_realizacija);
		getActionBar().setTitle(getString(R.string.dialog_title_record_visit) + " " + screenDateFormat.format(new Date()));
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		gson = new Gson();
		
		bZapocniDan = (Button) findViewById(R.id.bZapocniDan);
		bNovaRealizacija = (Button) findViewById(R.id.bNovaRealizacija);
		bPauza = (Button) findViewById(R.id.bPauza);
		bZavrsiDan = (Button) findViewById(R.id.bZavrsiDan);
		bPovratakKuci = (Button) findViewById(R.id.bPovratakKuci);
		lvRealizacija = (ListView) findViewById(R.id.lvRealizacija);
		lvPlan = (ListView) findViewById(R.id.lvPlan);
		
		cursorAdapter = new VisitsAdapter(this);
		lvRealizacija.setAdapter(cursorAdapter);
		
		cursorAdapterPlan = new PlanAdapter(this);
		lvPlan.setAdapter(cursorAdapterPlan);
		
		bZapocniDan.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if (pocetakDanaZabelezen()) {
					Toast.makeText(getApplicationContext(), R.string.pocetak_dana_zabelezen, Toast.LENGTH_LONG).show();
				} else {
					kilometrazaDijalog(ApplicationConstants.VISIT_TYPE_START_DAY);
				}
			}
		});
		
		bNovaRealizacija.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!pocetakDanaZabelezen()) {
					Toast.makeText(getApplicationContext(), R.string.pocetak_dana_nije_zabelezen, Toast.LENGTH_LONG).show();
				} else if (postojiOtvorenaPoseta()) {
					Toast.makeText(getApplicationContext(), R.string.postoji_otvorena_poseta, Toast.LENGTH_LONG).show();
				} else if (krajDanaZabelezen()) {
					Toast.makeText(getApplicationContext(), R.string.kraj_dana_zabelezen, Toast.LENGTH_LONG).show();
				} else {
					novaRealizacijaDijalog(-1, null, -1);
				}
			}
		});
		
		bPauza.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if (!pocetakDanaZabelezen()) {
					Toast.makeText(getApplicationContext(), R.string.pocetak_dana_nije_zabelezen, Toast.LENGTH_LONG).show();
				} else if (postojiOtvorenaPoseta()) {
					Toast.makeText(getApplicationContext(), R.string.postoji_otvorena_poseta, Toast.LENGTH_LONG).show();
				} else if (krajDanaZabelezen()) {
					Toast.makeText(getApplicationContext(), R.string.kraj_dana_zabelezen, Toast.LENGTH_LONG).show();
				} else {
					kilometrazaDijalog(ApplicationConstants.VISIT_TYPE_PAUSE);
				}
			}
		});
		
		bZavrsiDan.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if (!pocetakDanaZabelezen()) {
					Toast.makeText(getApplicationContext(), R.string.pocetak_dana_nije_zabelezen, Toast.LENGTH_LONG).show();
				} else if (postojiOtvorenaPoseta()) {
					Toast.makeText(getApplicationContext(), R.string.postoji_otvorena_poseta, Toast.LENGTH_LONG).show();
				} else if (krajDanaZabelezen()) {
					Toast.makeText(getApplicationContext(), R.string.kraj_dana_zabelezen, Toast.LENGTH_LONG).show();
				} else {
					kilometrazaDijalog(ApplicationConstants.VISIT_TYPE_END_DAY);
				}
			}
		});
		
		bPovratakKuci.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if (!pocetakDanaZabelezen()) {
					Toast.makeText(getApplicationContext(), R.string.pocetak_dana_nije_zabelezen, Toast.LENGTH_LONG).show();
				} else if (!krajDanaZabelezen()) {
					Toast.makeText(getApplicationContext(), R.string.kraj_dana_nije_zabelezen, Toast.LENGTH_LONG).show();
				} else if (povratakKuciZabelezen()) {
					Toast.makeText(getApplicationContext(), R.string.dolazak_kuci_zabelezen, Toast.LENGTH_LONG).show();
				} else {
					kilometrazaDijalog(ApplicationConstants.VISIT_TYPE_BACK_HOME);
				}
			}
		});
		
		getLoaderManager().initLoader(0, null, this);
		getLoaderManager().initLoader(1, null, this);
	}
	
	private HashMap<String, String> requestLocationUpdate() {
		Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		HashMap<String, String> map = new HashMap<>();
		map.put("lat", String.valueOf(location.getLatitude()));
		map.put("lon", String.valueOf(location.getLatitude()));
		map.put("acc", String.valueOf(location.getAccuracy()));
		return map;
	}
	
	private void postGpsData(String kupac, String tip, Date vreme) {
		gpsModel = new GpsModel();
		HashMap<String, String> mapa = requestLocationUpdate();
		gpsModel.setSalesPersonNo(salesPersonNo);
		gpsModel.setBuyerName(kupac);
		gpsModel.setEntryType(tip);
		gpsModel.setLatitude(mapa.get("lat"));
		gpsModel.setLongitude(mapa.get("lon"));
		gpsModel.setAccuracy(mapa.get("acc"));
		gpsModel.setDate(screenDateFormat.format(new Date()));
		gpsModel.setTime(shortTimeFormat.format(vreme));
		
		System.out.println(gson.toJson(gpsModel));
		
		Thread thread = new Thread(new Runnable(){
		    @Override
		    public void run() {
		        try {
		        	HttpPostRequest.postData(gson.toJson(gpsModel));
		        } catch (Exception e) {
		        	Log.e("NovaRealizacijaActivity", e.getMessage());
		        }
		    }
		});
		thread.start(); 
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(onNotice);
		
		//locationManager.removeUpdates(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter stRealizedVisitsToCustomersSyncObject = new IntentFilter(SetRealizedVisitsToCustomersSyncObject.BROADCAST_SYNC_ACTION);
    	LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, stRealizedVisitsToCustomersSyncObject);
    	
    	//locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TIME_INTERVAL, DISTANCE, this);
	}
	
	
	private void showDatePicker() {
		DatePickerFragment date = new DatePickerFragment();

		Bundle args = new Bundle();
		args.putInt("year", calender.get(Calendar.YEAR));
		args.putInt("month", calender.get(Calendar.MONTH));
		args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
		date.setArguments(args);
		
		date.setCallBack(ondate);
		date.show(getSupportFragmentManager(), "Date Picker");
	}

	private OnDateSetListener ondate = new OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		    calender.set(Calendar.YEAR, year);
		    calender.set(Calendar.MONTH, monthOfYear);
		    calender.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		    datumInput.setText(dateFormat.format(calender.getTime()));
		}
	};
	
	private void showTimePicker() {
		TimePickerFragment time = new TimePickerFragment();
		
		Bundle args = new Bundle();
		args.putInt("hour", calender.get(Calendar.HOUR_OF_DAY));
		args.putInt("minute", calender.get(Calendar.MINUTE));
		time.setArguments(args);

		time.setCallBack(ontime);
		time.show(getSupportFragmentManager(), "Time Picker");
	}
	
	private OnTimeSetListener ontime = new OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			calender.set(Calendar.HOUR_OF_DAY, hourOfDay);
		    calender.set(Calendar.MINUTE, minute);
		    vremeInput.setText(timeFormat.format(calender.getTime()));
		}
	};
	
	private boolean pocetakDanaZabelezen() {
		boolean signal = false;
		Cursor cursor = getContentResolver().query(MobileStoreContract.Visits.CONTENT_URI, new String[] { MobileStoreContract.Visits._ID }, VISITS_DATE_FILTER + " AND " + VISITS_FILTER_POCETAK_DANA, new String[] { rs.gopro.mobile_store.util.DateUtils.toDbDate(new Date()) }, null);
		if (cursor.moveToFirst()) {
			signal = true;
		}
		cursor.close();
		return signal;
	}
	
	private boolean krajDanaZabelezen() {
		boolean signal = false;
		Cursor cursor = getContentResolver().query(MobileStoreContract.Visits.CONTENT_URI, new String[] { MobileStoreContract.Visits._ID }, VISITS_DATE_FILTER + " AND " + VISITS_FILTER_KRAJ_DANA, new String[] { rs.gopro.mobile_store.util.DateUtils.toDbDate(new Date()) }, null);
		if (cursor.moveToFirst()) {
			signal = true;
		}
		cursor.close();
		return signal;
	}
	
	private boolean povratakKuciZabelezen() {
		boolean signal = false;
		Cursor cursor = getContentResolver().query(MobileStoreContract.Visits.CONTENT_URI, new String[] { MobileStoreContract.Visits._ID }, VISITS_DATE_FILTER + " AND " + VISITS_FILTER_POVRATAK_KUCI, new String[] { rs.gopro.mobile_store.util.DateUtils.toDbDate(new Date()) }, null);
		if (cursor.moveToFirst()) {
			signal = true;
		}
		cursor.close();
		return signal;
	}
	
	private boolean postojiOtvorenaPoseta() {
		boolean signal = false;
		Cursor cursor = getContentResolver().query(MobileStoreContract.Visits.CONTENT_URI, new String[] { MobileStoreContract.Visits._ID }, VISITS_DATE_FILTER + " AND " + VISITS_FILTER_IS_VISIT_OPEN, new String[] { rs.gopro.mobile_store.util.DateUtils.toDbDate(new Date()) }, null);
		if (cursor.moveToFirst()) {
			signal = true;
		}
		cursor.close();
		return signal;
	}
	
	private ContentValues commonContentValues() {
		Date newDate = new Date();
		ContentValues cv = new ContentValues();
		cv.put(MobileStoreContract.Visits.SALES_PERSON_ID, salesPersonId);
		cv.put(MobileStoreContract.Visits.VISIT_DATE, DateUtils.toDbDate(newDate));
		cv.put(MobileStoreContract.Visits.VISIT_TYPE, ApplicationConstants.VISIT_RECORDED);
		cv.put(MobileStoreContract.Visits.ENTRY_SUBTYPE, 0);
		cv.putNull(MobileStoreContract.Visits.NOTE);
		cv.put(MobileStoreContract.Visits.VISIT_STATUS, ApplicationConstants.VISIT_STATUS_FINISHED);
		cv.put(MobileStoreContract.Visits.IS_SENT, 0);
		return cv;
	}
	
	private void zapocniDan(String kilometraza) {
		Date newDate = new Date();
		ContentValues cv = commonContentValues();
		cv.putNull(MobileStoreContract.Visits.CUSTOMER_ID);
		cv.put(MobileStoreContract.Visits.ODOMETER, kilometraza);
		cv.put(MobileStoreContract.Visits.ARRIVAL_TIME, DateUtils.toDbDate(newDate));
		cv.put(MobileStoreContract.Visits.DEPARTURE_TIME, DateUtils.toDbDate(newDate));
		cv.put(MobileStoreContract.Visits.VISIT_RESULT, ApplicationConstants.VISIT_TYPE_START_DAY);

		Uri result = getContentResolver().insert(MobileStoreContract.Visits.CONTENT_URI, cv);
		sendRecordedVisit(ContentUris.parseId(result));
	}
	
	private void novaRealizacija(String kilometraza, int customerId, Date arrivalDate) {
		ContentValues cv = commonContentValues();
		cv.put(MobileStoreContract.Visits.CUSTOMER_ID, customerId);
		cv.put(MobileStoreContract.Visits.ODOMETER, kilometraza);
		cv.put(MobileStoreContract.Visits.ARRIVAL_TIME, DateUtils.toDbDate(arrivalDate));
		cv.put(MobileStoreContract.Visits.VISIT_RESULT, ApplicationConstants.VISIT_TYPE_NO_CLOSURE);
		cv.put(MobileStoreContract.Visits.VISIT_STATUS, ApplicationConstants.VISIT_STATUS_STARTED);

		Uri result = getContentResolver().insert(MobileStoreContract.Visits.CONTENT_URI, cv);
		sendRecordedVisit(ContentUris.parseId(result));
	}
	
	private void krajRealizacije(String visitId, int rezultat, String beleska) {
		Date newDate = new Date();
		ContentValues cv = commonContentValues();
		if (rezultat == 0) {
			cv.put(MobileStoreContract.Visits.VISIT_RESULT, ApplicationConstants.VISIT_TYPE_CLOSURE);
		}
		cv.put(MobileStoreContract.Visits.NOTE, beleska);
		cv.put(MobileStoreContract.Visits.DEPARTURE_TIME, DateUtils.toDbDate(newDate));
		
		int status = getContentResolver().update(MobileStoreContract.Visits.CONTENT_URI, cv, "visits._id=?", new String[] { visitId });
		if (status > 0) {
			Toast.makeText(getApplicationContext(), R.string.kraj_realizacije_zabelezen, Toast.LENGTH_LONG).show();
		}
		sendRecordedVisit(Long.valueOf(visitId));
	}
	
	private void pauza(String kilometraza) {
		Date newDate = new Date();
		ContentValues cv = commonContentValues();
		cv.putNull(MobileStoreContract.Visits.CUSTOMER_ID);
		cv.put(MobileStoreContract.Visits.ODOMETER, kilometraza);
		cv.put(MobileStoreContract.Visits.ARRIVAL_TIME, DateUtils.toDbDate(newDate));
		cv.put(MobileStoreContract.Visits.VISIT_RESULT, ApplicationConstants.VISIT_TYPE_PAUSE);
		cv.put(MobileStoreContract.Visits.VISIT_STATUS, ApplicationConstants.VISIT_STATUS_STARTED);

		Uri result = getContentResolver().insert(MobileStoreContract.Visits.CONTENT_URI, cv);
		sendRecordedVisit(ContentUris.parseId(result));
	}
	
	private void krajPauze(String visitId, int rezultat, String beleska) {
		Date newDate = new Date();
		ContentValues cv = commonContentValues();
		cv.put(MobileStoreContract.Visits.ENTRY_SUBTYPE, rezultat);
		cv.put(MobileStoreContract.Visits.NOTE, beleska);
		cv.put(MobileStoreContract.Visits.DEPARTURE_TIME, DateUtils.toDbDate(newDate));
		
		int status = getContentResolver().update(MobileStoreContract.Visits.CONTENT_URI, cv, "visits._id=?", new String[] { visitId });
		if (status > 0) {
			Toast.makeText(getApplicationContext(), R.string.kraj_pauze_zabelezen, Toast.LENGTH_LONG).show();
		}
		sendRecordedVisit(Long.valueOf(visitId));
	}
	
	private void zavrsiDan(String kilometraza) {
		Date newDate = new Date();
		ContentValues cv = commonContentValues();
		cv.putNull(MobileStoreContract.Visits.CUSTOMER_ID);
		cv.put(MobileStoreContract.Visits.ODOMETER, kilometraza);
		cv.put(MobileStoreContract.Visits.ARRIVAL_TIME, DateUtils.toDbDate(newDate));
		cv.put(MobileStoreContract.Visits.DEPARTURE_TIME, DateUtils.toDbDate(newDate));
		cv.put(MobileStoreContract.Visits.VISIT_RESULT, ApplicationConstants.VISIT_TYPE_END_DAY);

		Uri result = getContentResolver().insert(MobileStoreContract.Visits.CONTENT_URI, cv);
		sendRecordedVisit(ContentUris.parseId(result));
	}
	
	private void povratakKuci(String kilometraza) {
		Date newDate = new Date();
		ContentValues cv = commonContentValues();
		cv.putNull(MobileStoreContract.Visits.CUSTOMER_ID);
		cv.put(MobileStoreContract.Visits.ODOMETER, kilometraza);
		cv.put(MobileStoreContract.Visits.ARRIVAL_TIME, DateUtils.toDbDate(newDate));
		cv.put(MobileStoreContract.Visits.DEPARTURE_TIME, DateUtils.toDbDate(newDate));
		cv.put(MobileStoreContract.Visits.VISIT_RESULT, ApplicationConstants.VISIT_TYPE_BACK_HOME);
		

		Uri result = getContentResolver().insert(MobileStoreContract.Visits.CONTENT_URI, cv);
		sendRecordedVisit(ContentUris.parseId(result));
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		switch (id) {
		case 0:
			return new CursorLoader(this, MobileStoreContract.Visits.CONTENT_URI, VisitsQuery.PROJECTION, VISITS_DATE_FILTER + " AND " + VISITS_FILTER_REALIZACIJA, new String[] { DateUtils.toDbDate(new Date()) }, null);
		case 1:
			return new CursorLoader(this, MobileStoreContract.Visits.CONTENT_URI, VisitsQuery.PROJECTION, VISITS_DATE_FILTER + " AND " + VISITS_FILTER_PLAN, new String[] { DateUtils.toDbDate(new Date()) }, MobileStoreContract.Visits.ARRIVAL_TIME);
		default:
			return new CursorLoader(this, MobileStoreContract.Visits.CONTENT_URI, VisitsQuery.PROJECTION, VISITS_DATE_FILTER + " AND " + VISITS_FILTER_REALIZACIJA, new String[] { DateUtils.toDbDate(new Date()) }, null);
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if(cursor != null) {
			switch (loader.getId()) {
				case 0:
					if (cursorAdapter != null) {
						cursorAdapter.swapCursor(cursor);
					}
					break;
				case 1:
					if (cursorAdapterPlan != null) {
						cursorAdapterPlan.swapCursor(cursor);
					}
					break;
				default:
					cursorAdapter.swapCursor(cursor);
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		switch (loader.getId()) {
			case 0:
				if (cursorAdapter != null) {
					cursorAdapter.swapCursor(null);
				}
				break;
			case 1:
				if (cursorAdapterPlan != null) {
					cursorAdapterPlan.swapCursor(null);
				}
				break;
			default:
				if (cursorAdapter != null) {
					cursorAdapter.swapCursor(null);
				}
		}
	}
	
	public void kilometrazaDijalog(int tip) {
		final Dialog dialog = new Dialog(this);
		final int tipRealizacije = tip;
		dialog.setContentView(R.layout.dialog_kilometraza);
		
		final EditText etLozinka = (EditText) dialog.findViewById(R.id.dialog_kilometraza_input);
		Button bDialogOk = (Button) dialog.findViewById(R.id.dialogButtonOK);
		
		switch (tip) {
			case 1:
				dialog.setTitle("Početak dana");
				break;
			case 4:
				dialog.setTitle("Pauza");
				break;
			case 5:
				dialog.setTitle("Kraj dana");
				break;
			case 6:
				dialog.setTitle("Povratak kući");
				break;
			default:
				break;
		}
		
		bDialogOk.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String km = etLozinka.getText().toString();
				if (km.trim().length() > 0) {
					switch (tipRealizacije) {
						case 1:
							zapocniDan(km);
							break;
						case 4:
							pauza(km);
							break;
						case 5:
							zavrsiDan(km);
							break;
						case 6:
							povratakKuci(km);
							break;
						default:
							break;
					}
					dialog.dismiss();
				} else {
					Toast.makeText(getApplicationContext(), R.string.title_unesite_vrednost, Toast.LENGTH_LONG).show();
				}
			}
		});
		dialog.show();
	}
	
	public void novaRealizacijaDijalog(int customerId, Date time, final int visitId) {
		final Dialog dialog = new Dialog(this);
		
		dialog.setContentView(R.layout.dialog_nova_realizacija);
		dialog.setTitle("Nova realizacija");
		
		AutoCompleteTextView acKupac = (AutoCompleteTextView) dialog.findViewById(R.id.dialog_nova_realizacija_kupac_input);
		customerCursorAdapter = new CustomerAutocompleteCursorAdapter(this, null);
		acKupac.setAdapter(customerCursorAdapter);
		acKupac.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View arg1, int position, long id) {
				Cursor cursor = (Cursor) customerCursorAdapter.getItem(position);
				selectedCustomerId = cursor.getInt(0);
			}
		});
		datumInput = (Button) dialog.findViewById(R.id.dialog_nova_realizacija_datum_input);
		vremeInput = (Button) dialog.findViewById(R.id.dialog_nova_realizacija_vreme_input);
		final EditText kilometrazaInput = (EditText) dialog.findViewById(R.id.dialog_nova_realizacija_kilometraza_input);
		Button bDialogOk = (Button) dialog.findViewById(R.id.dialogButtonOK);
		
		if (customerId != -1) {
			Cursor c = getContentResolver().query(MobileStoreContract.Customers.buildCustomersUri(String.valueOf(customerId)), new String[] {MobileStoreContract.Customers.CUSTOMER_NO, MobileStoreContract.Customers.NAME}, null, null, null);
        	if (c.moveToFirst()) {
	        	final int codeIndex = c.getColumnIndexOrThrow(MobileStoreContract.Customers.CUSTOMER_NO);
	    		final int nameIndex = c.getColumnIndexOrThrow(MobileStoreContract.Customers.NAME);
	    		final String result = c.getString(codeIndex) + " - " + c.getString(nameIndex);
	    		acKupac.setText(result);
        	}
        	selectedCustomerId = customerId;
		}
		datumInput.setText(dateFormat.format(new Date()));
		if (time != null) {
			vremeInput.setText(timeFormat.format(time));
			kilometrazaInput.requestFocus();
		} else {
			vremeInput.setText(timeFormat.format(new Date()));
		}	
		
		datumInput.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				showDatePicker();
			}
		});
		
		vremeInput.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showTimePicker();
			}
		});

		bDialogOk.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String km = kilometrazaInput.getText().toString();
				
				if (selectedCustomerId > 0) {
					if (km.trim().length() > 0) {
						try {
							Date selectedDate = dateTimeFormat.parse(datumInput.getText().toString() + " " + vremeInput.getText().toString());
							novaRealizacija(km, selectedCustomerId, selectedDate);
							if (visitId != -1) {
								//getContentResolver().delete(MobileStoreContract.Visits.CONTENT_URI, MobileStoreContract.Visits._ID + "=?", new String[] { String.valueOf(visitId) });
							}
						} catch (Exception e) {
						}
						selectedCustomerId = 0;
						dialog.dismiss();
					} else {
						kilometrazaInput.requestFocus();
						Toast.makeText(getApplicationContext(), R.string.title_unesite_vrednost, Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(getApplicationContext(), R.string.kupac_nije_izabran, Toast.LENGTH_LONG).show();
				}
			}
		});
		dialog.show();
	}
	
	public void krajRealizacijeDijalog(final String visitId, final int tip) {
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.dialog_kraj_realizacije);
		
		final Spinner rezultat = (Spinner) dialog.findViewById(R.id.dialog_kraj_realizacije_rezultat_spinner);
		final EditText beleskaInput = (EditText) dialog.findViewById(R.id.dialog_kraj_realizacije_beleska_input);
		Button bDialogOk = (Button) dialog.findViewById(R.id.dialogButtonOK);
		ArrayAdapter<CharSequence> adapter = null;
		
		switch (tip) {
			case 3:
				dialog.setTitle("Kraj realizacije");
				adapter = ArrayAdapter.createFromResource(this, R.array.record_visit_result_type_array_v2, android.R.layout.simple_spinner_item);
				break;
			case 4:
				dialog.setTitle("Kraj pauze");
				adapter = ArrayAdapter.createFromResource(this, R.array.visit_subtype, android.R.layout.simple_spinner_item);
				break;
			default:
				break;
		}
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		rezultat.setAdapter(adapter);
		rezultat.setSelection(0);
		
		bDialogOk.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String beleska = beleskaInput.getText().toString();
				switch (tip) {
					case 3:
						krajRealizacije(visitId, rezultat.getSelectedItemPosition(), beleska);
						break;
					case 4:
						krajPauze(visitId, rezultat.getSelectedItemPosition(), beleska);
						break;
					default:
						break;
				}
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	
	public void infoDijalog(int visitId) {
		
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.dialog_realizacija_info);
		
		Date datum = null;
		String naslov = null, kupacSifra = null, kupacNaziv = null, vremed = "", vremeo = null, vreme = null, kilometraza = null, poruka = null;
		
		TextView tvRealizacijaInfoDatum = (TextView) dialog.findViewById(R.id.tvRealizacijaInfoDatum);
		TextView tvRealizacijaInfoVreme = (TextView) dialog.findViewById(R.id.tvRealizacijaInfoVreme);
		TextView tvRealizacijaInfoKilometraza = (TextView) dialog.findViewById(R.id.tvRealizacijaInfoKilometraza);
		TextView tvRealizacijaInfoPoruka = (TextView) dialog.findViewById(R.id.tvRealizacijaInfoPoruka);

		Cursor cursor = getContentResolver().query(MobileStoreContract.Visits.CONTENT_URI, VisitsQuery.PROJECTION, "visits._id=?", new String[] { String.valueOf(visitId) }, null);
		cursor.moveToFirst();
		
		int tip = cursor.getInt(VisitsQuery.VISIT_RESULT);
		kilometraza = cursor.getString(VisitsQuery.ODOMETER);
		
		try {
			kupacSifra = cursor.getString(VisitsQuery.CUSTOMER_NO);
			kupacNaziv = cursor.getString(VisitsQuery.CUSTOMER_NAME);
			datum = dateTimeFormat.parse(cursor.getString(VisitsQuery.VISIT_DATE));
			vremeo = timeFormat.format(dateTimeFormat.parse(cursor.getString(VisitsQuery.ARRIVAL_TIME)));
			vremed = timeFormat.format(dateTimeFormat.parse(cursor.getString(VisitsQuery.DEPARTURE_TIME)));
			poruka = cursor.getString(VisitsQuery.NOTE);
		} catch (Exception e) {
		}
		
		switch (tip) {
		case 1:
			naslov = "POČETAK DANA";
			vreme = vremeo;
			break;
		case 2:
			naslov = kupacSifra + " - " + kupacNaziv;
			vreme = vremeo + " - " + vremed;
			break;
		case 3:
			naslov = kupacSifra + " - " + kupacNaziv;
			vreme = vremeo + " - " + vremed;
			break;
		case 4:
			naslov = "PAUZA";
			vreme = vremeo + " - " + vremed;
			break;
		case 5:
			naslov = "KRAJ DANA";
			vreme = vremeo;
			break;
		case 6:
			naslov = "POVRATAK KUĆI";
			vreme = vremeo;
			break;
		default:
			break;
		}
		
		dialog.setTitle(naslov);
		tvRealizacijaInfoDatum.setText(getString(R.string.visit_date_label_title) + " " + dateFormat.format(datum));
		tvRealizacijaInfoVreme.setText(getString(R.string.visit_time_label_title) + " " + vreme);
		tvRealizacijaInfoKilometraza.setText(getString(R.string.visit_odometer_label_title) + " " + kilometraza);
		if (poruka != null) {
			tvRealizacijaInfoPoruka.setVisibility(View.VISIBLE);
			tvRealizacijaInfoPoruka.setText(getString(R.string.visit_note_label_title) + " " + poruka);
		}
		
		dialog.show();
	}
	
	public void sendRecordedVisit(long visitId) {
		SetRealizedVisitsToCustomersSyncObject visitsToCustomersSyncObject = new SetRealizedVisitsToCustomersSyncObject((int) visitId);
    	Intent intent = new Intent(this, NavisionSyncService.class);
		intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, visitsToCustomersSyncObject);
		startService(intent);
	}
	
	private class VisitsAdapter extends CursorAdapter {
        public VisitsAdapter(Context context) {
            super(context, null, false);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
        	LayoutInflater inflater = LayoutInflater.from(context);
			View v = inflater.inflate(R.layout.realizacija_stavka, parent, false);
			bindView(v, context, cursor);
			return v;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
        	
        	RelativeLayout layoutRealizacijaInfo = (RelativeLayout) view.findViewById(R.id.layoutRealizacijaInfo);
        	TextView tvRealizacijaNaslov = (TextView) view.findViewById(R.id.tvRealizacijaNaslov);
        	TextView tvRealizacijaKilometraza = (TextView) view.findViewById(R.id.tvRealizacijaKilometraza);
        	TextView tvRealizacijaVreme = (TextView) view.findViewById(R.id.tvRealizacijaVreme);
        	ImageView ivRealizacijaZavrsi = (ImageView) view.findViewById(R.id.ivRealizacijaZavrsi);
        	ImageView ivRealizacijaStatus = (ImageView) view.findViewById(R.id.ivRealizacijaStatus);
        	String arrivalTime = null, departureTime = null, odometer = null;
        	RelativeLayout layoutRealizacijaZavrsi = (RelativeLayout) view.findViewById(R.id.layoutRealizacijaZavrsi);
        	
        	final int selectedVisitId = cursor.getInt(VisitsQuery._ID);
        	final int selectedType = cursor.getInt(VisitsQuery.VISIT_RESULT);
        	
        	try {
        		odometer = cursor.getString(VisitsQuery.ODOMETER) + " km";
        		arrivalTime = DateUtils.formatDbTimeForPresentation(cursor.getString(VisitsQuery.ARRIVAL_TIME));
        		departureTime = DateUtils.formatDbTimeForPresentation(cursor.getString(VisitsQuery.DEPARTURE_TIME));
			} catch (Exception e) {
			}
        	
        	int status = cursor.getInt(VisitsQuery.VISIT_STATUS);
        	int poslato = cursor.getInt(VisitsQuery.IS_SENT);
        	
        	switch (selectedType) {
			case 1:
				tvRealizacijaNaslov.setText("POČETAK DANA");
				tvRealizacijaVreme.setText(arrivalTime);
				break;
			case 2:
				tvRealizacijaNaslov.setText(cursor.getString(VisitsQuery.CUSTOMER_NO) + " - " + cursor.getString(VisitsQuery.CUSTOMER_NAME));
				tvRealizacijaVreme.setText(arrivalTime + " - " + departureTime);
				break;
			case 3:
				tvRealizacijaNaslov.setText(cursor.getString(VisitsQuery.CUSTOMER_NO) + " - " + cursor.getString(VisitsQuery.CUSTOMER_NAME));
				tvRealizacijaVreme.setText(arrivalTime + " - " + departureTime);
				break;
			case 4:
				tvRealizacijaNaslov.setText("PAUZA");
				tvRealizacijaVreme.setText(arrivalTime + " - " + departureTime);
				break;
			case 5:
				tvRealizacijaNaslov.setText("KRAJ DANA");
				tvRealizacijaVreme.setText(arrivalTime);
				break;
			case 6:
				tvRealizacijaNaslov.setText("POVRATAK KUĆI");
				tvRealizacijaVreme.setText(arrivalTime);
				break;
			default:
				break;
			}
        	
        	layoutRealizacijaInfo.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					infoDijalog(selectedVisitId);
				}
			});
        	
        	tvRealizacijaKilometraza.setText(odometer);
        	if (status != ApplicationConstants.VISIT_STATUS_STARTED) {
        		layoutRealizacijaZavrsi.setVisibility(View.GONE);
			} else {
				layoutRealizacijaZavrsi.setVisibility(View.VISIBLE);
				ivRealizacijaZavrsi.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						krajRealizacijeDijalog(String.valueOf(selectedVisitId), selectedType);
					}
				});
			}
        	if (poslato == 1) {
        		ivRealizacijaStatus.setImageResource(R.drawable.ic_status_ok);
			} else {
				ivRealizacijaStatus.setImageResource(R.drawable.ic_status_bad);
				ivRealizacijaStatus.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						sendRecordedVisit(selectedVisitId);
					}
				});
			}

        }
    }
	
	private class PlanAdapter extends CursorAdapter {
        public PlanAdapter(Context context) {
            super(context, null, false);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
        	LayoutInflater inflater = LayoutInflater.from(context);
			View v = inflater.inflate(R.layout.realizacija_plan_stavka, parent, false);
			bindView(v, context, cursor);
			return v;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
        	
        	TextView tvPlanNaslov = (TextView) view.findViewById(R.id.tvPlanNaslov);
        	TextView tvPlanDatum = (TextView) view.findViewById(R.id.tvPlanDatum);
        	TextView tvPlanVreme = (TextView) view.findViewById(R.id.tvPlanVreme);
        	ImageView ivPlanRealizuj = (ImageView) view.findViewById(R.id.ivPlanRealizuj);
        	
        	final String date = DateUtils.formatDbDateForPresentation(cursor.getString(VisitsQuery.VISIT_DATE));
			final String time = DateUtils.formatDbTimeForPresentation(cursor.getString(VisitsQuery.ARRIVAL_TIME));
			
			final int visitId = cursor.getInt(VisitsQuery._ID);
			final int customerId = cursor.getInt(VisitsQuery.CUSTOMER_ID);
        	
        	tvPlanNaslov.setText(cursor.getString(VisitsQuery.CUSTOMER_NO) + " - " + cursor.getString(VisitsQuery.CUSTOMER_NAME));
        	tvPlanDatum.setText("PLAN " + date);
			tvPlanVreme.setText(time);
        	
        	ivPlanRealizuj.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Date vreme = null;
					try {
						vreme = shortTimeFormat.parse(time);
						
						if (!pocetakDanaZabelezen()) {
							Toast.makeText(getApplicationContext(), R.string.pocetak_dana_nije_zabelezen, Toast.LENGTH_LONG).show();
						} else if (postojiOtvorenaPoseta()) {
							Toast.makeText(getApplicationContext(), R.string.postoji_otvorena_poseta, Toast.LENGTH_LONG).show();
						} else if (krajDanaZabelezen()) {
							Toast.makeText(getApplicationContext(), R.string.kraj_dana_zabelezen, Toast.LENGTH_LONG).show();
						} else {
							novaRealizacijaDijalog(customerId, vreme, visitId);
						}
					} catch (ParseException e) {
						LogUtils.LOGE("ERR", "DATE PARSE ERROR");
					}
				}
			});
        	

        }
    }
	
	private interface VisitsQuery {

        String[] PROJECTION = {
                BaseColumns._ID,
                MobileStoreContract.Visits.CUSTOMER_ID,
                MobileStoreContract.Customers.CUSTOMER_NO,
                MobileStoreContract.Customers.NAME,
                MobileStoreContract.Visits.ENTRY_TYPE,
                MobileStoreContract.Visits.VISIT_STATUS,
                MobileStoreContract.Visits.ARRIVAL_TIME, 
                MobileStoreContract.Visits.DEPARTURE_TIME,
                MobileStoreContract.Visits.ODOMETER,
                MobileStoreContract.Visits.NOTE,
                MobileStoreContract.Visits.VISIT_RESULT,
                MobileStoreContract.Visits.IS_SENT,
                MobileStoreContract.Visits.VISIT_DATE
        };

        int _ID = 0;
        int CUSTOMER_ID = 1;
        int CUSTOMER_NO = 2;
        int CUSTOMER_NAME = 3;
//      int ENTRY_TYPE = 4;
        int VISIT_STATUS = 5;
		int ARRIVAL_TIME = 6;
		int DEPARTURE_TIME = 7;
		int ODOMETER = 8;
		int NOTE = 9;
		int VISIT_RESULT = 10;
		int IS_SENT = 11;
		int VISIT_DATE = 12;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

}
