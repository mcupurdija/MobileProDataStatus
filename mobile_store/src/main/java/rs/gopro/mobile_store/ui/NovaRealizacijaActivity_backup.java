package rs.gopro.mobile_store.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.provider.MobileStoreContract.Visits;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.ui.components.CustomerAutocompleteCursorAdapter;
import rs.gopro.mobile_store.ui.dialog.AddressSelectDialog;
import rs.gopro.mobile_store.ui.dialog.AddressSelectDialog.AddressSelectDialogListener;
import rs.gopro.mobile_store.util.ApplicationConstants;
import rs.gopro.mobile_store.util.ApplicationConstants.SyncStatus;
import rs.gopro.mobile_store.util.DatePickerFragment;
import rs.gopro.mobile_store.util.DateUtils;
import rs.gopro.mobile_store.util.LogUtils;
import rs.gopro.mobile_store.util.TimePickerFragment;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.SetRealizedVisitsToCustomersSyncObject;
import rs.gopro.mobile_store.ws.model.SyncResult;
import android.app.AlertDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.BaseColumns;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class NovaRealizacijaActivity_backup extends BaseActivity implements LoaderCallbacks<Cursor>, LocationListener, AddressSelectDialogListener {

	private static final String VISITS_DATE_FILTER = "DATE(" + Tables.VISITS + "." + MobileStoreContract.Visits.VISIT_DATE + ")=DATE(?)";
	private static final String VISITS_FILTER_REALIZACIJA = Tables.VISITS + "." + MobileStoreContract.Visits.VISIT_TYPE + "=" + ApplicationConstants.VISIT_RECORDED;
	private static final String VISITS_FILTER_PLAN = Tables.VISITS + "." + MobileStoreContract.Visits.VISIT_TYPE + "=" + ApplicationConstants.VISIT_PLANNED;
	private static final String VISITS_FILTER_POCETAK_DANA = Tables.VISITS + "." + MobileStoreContract.Visits.VISIT_RESULT + "=" + ApplicationConstants.VISIT_TYPE_START_DAY;
	private static final String VISITS_FILTER_KRAJ_DANA = Tables.VISITS + "." + MobileStoreContract.Visits.VISIT_RESULT + "=" + ApplicationConstants.VISIT_TYPE_END_DAY;
	private static final String VISITS_FILTER_POVRATAK_KUCI = Tables.VISITS + "." + MobileStoreContract.Visits.VISIT_RESULT + "=" + ApplicationConstants.VISIT_TYPE_BACK_HOME;
	private static final String VISITS_FILTER_IS_VISIT_OPEN = Tables.VISITS + "." + MobileStoreContract.Visits.VISIT_STATUS + "=" + ApplicationConstants.VISIT_STATUS_STARTED;
	private static final String NOT_AVAILABLE = "n/a";
	
	private static final int NEW_SALE_ORDER_REQUEST_CODE = 2;
	
	private CursorAdapter cursorAdapter, cursorAdapterPlan;
	private CustomerAutocompleteCursorAdapter customerCursorAdapter;
	
	private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
	private SimpleDateFormat screenDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
	private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
	private SimpleDateFormat shortTimeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
	
	private Date danasnjiDatum = new Date();
	
	private Button bZapocniDan, bNovaRealizacija, bPauza, bZavrsiDan, bPovratakKuci;
	private Button datumInput, vremeInput, customerAddress;
	private TextView tvGpsIskljucen;
	private ListView lvRealizacija, lvPlan;
	private int selectedCustomerId;
	private String selectedCustomerNo, selectedAddressNo;
	
	private Calendar calender = Calendar.getInstance();
	
	private static final int TIME_INTERVAL = 1 * 60 * 1000;
	private static final int DISTANCE = 0;
	private LocationManager locationManager;
	
	private ContentValues cv;
	private Location currentBestLocation;
	private Handler handler = new Handler();
	private Runnable runnable;
	private Dialog gpsDialog;
	private AlertDialog alertDialog;
	private Menu menu;
	
	private Vibrator vibe;
	private long lastClickTime = 0;
	
	//private Gson gson;
	//private GpsModel gpsModel;
	
	private BroadcastReceiver onNotice = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			SyncResult syncResult = intent.getParcelableExtra(NavisionSyncService.SYNC_RESULT);
			onSOAPResult(syncResult, intent.getAction());
		}
	};

	protected void onSOAPResult(SyncResult syncResult, String broadcastAction) {
		if (syncResult.getStatus().equals(SyncStatus.FAILURE)) {
			if (!alertDialog.isShowing()) {
				prikaziDijalog(syncResult.getResult());
			}
			//DialogUtil.showInfoDialog(this, getResources().getString(R.string.dialog_title_error_in_sync), syncResult.getResult());
		}
	}
	
	private void prikaziDijalog(String greska) {
		alertDialog.setTitle(R.string.dialog_title_error_in_sync);
	    alertDialog.setMessage(greska);
	    alertDialog.setIcon(R.drawable.ic_launcher);
	    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				alertDialog.dismiss();
			}
		});
	    alertDialog.show();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nova_realizacija);
		getActionBar().setTitle(getString(R.string.dialog_title_record_visit) + " " + screenDateFormat.format(new Date()));
		
		//gson = new Gson();
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		alertDialog = new AlertDialog.Builder(this).create();
		
		bZapocniDan = (Button) findViewById(R.id.bZapocniDan);
		bNovaRealizacija = (Button) findViewById(R.id.bNovaRealizacija);
		bPauza = (Button) findViewById(R.id.bPauza);
		bZavrsiDan = (Button) findViewById(R.id.bZavrsiDan);
		bPovratakKuci = (Button) findViewById(R.id.bPovratakKuci);
		tvGpsIskljucen = (TextView) findViewById(R.id.tvGpsIskljucen);
		lvRealizacija = (ListView) findViewById(R.id.lvRealizacija);
		lvPlan = (ListView) findViewById(R.id.lvPlan);
		
		cursorAdapter = new VisitsAdapter(this);
		lvRealizacija.setAdapter(cursorAdapter);
		
		cursorAdapterPlan = new PlanAdapter(this);
		lvPlan.setAdapter(cursorAdapterPlan);
		
		if (!checkGpsEnabled()) {
			tvGpsIskljucen.setVisibility(View.VISIBLE);
		}
		
		runnable = new Runnable() {
			
			@Override
			public void run() {
				dialogGps();
			}
		};
		
		bZapocniDan.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				localyticsSession.tagEvent("DUGME ZAPOCNI DAN");
				
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
				
				localyticsSession.tagEvent("DUGME NOVA REALIZACIJA");
				
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
				
				localyticsSession.tagEvent("DUGME PAUZA");
				
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
				
				localyticsSession.tagEvent("DUGME ZAVRSI DAN");
				
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
				
				localyticsSession.tagEvent("DUGME POVRATAK KUCI");
				
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
		
		tvGpsIskljucen.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);  
				startActivity(gpsOptionsIntent);
			}
		});
		
		getLoaderManager().initLoader(0, null, this);
		getLoaderManager().initLoader(1, null, this);
	}
	
	private boolean checkGpsEnabled() {
		LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);;
	    return mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
	
	private boolean checkGpsRecent() {
		if (checkGpsEnabled()) {
			long timeDelta = System.currentTimeMillis() - currentBestLocation.getTime();
			if (Math.abs(timeDelta) < 30000) {
				System.out.println("# GPS RECENT");
				return true;
			}
			System.out.println("# GPS NOT RECENT");
			return false;
		}
		return true;
	}
	
	/** Determines whether one Location reading is better than the current Location fix
	  * @param location  The new Location that you want to evaluate
	  * @param currentBestLocation  The current Location fix, to which you want to compare the new one
	  */
	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
	    if (currentBestLocation == null) {
	        return true;
	    }

	    // Check whether the new location fix is newer or older
	    long timeDelta = location.getTime() - currentBestLocation.getTime();
	    boolean isSignificantlyNewer = timeDelta > TIME_INTERVAL;
	    boolean isSignificantlyOlder = timeDelta < -TIME_INTERVAL;
	    boolean isNewer = timeDelta > 0;

	    // If it's been more than two minutes since the current location, use the new location
	    // because the user has likely moved
	    if (isSignificantlyNewer) {
	        return true;
	    // If the new location is more than two minutes older, it must be worse
	    } else if (isSignificantlyOlder) {
	        return false;
	    }

	    // Check whether the new location fix is more or less accurate
	    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

	    // Check if the old and new location are from the same provider
	    boolean isFromSameProvider = isSameProvider(location.getProvider(),
	            currentBestLocation.getProvider());

	    // Determine location quality using a combination of timeliness and accuracy
	    if (isMoreAccurate) {
	        return true;
	    } else if (isNewer && !isLessAccurate) {
	        return true;
	    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
	        return true;
	    }
	    return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
	    if (provider1 == null) {
	      return provider2 == null;
	    }
	    return provider1.equals(provider2);
	}
	
	/*private void postGpsData(String kupac, String tip, Date vreme) {
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
	}*/
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		this.menu = menu;
		
		MenuItem gpsStatus = menu.add("");
		gpsStatus.setIcon(R.drawable.ic_gps_status_bad);
		gpsStatus.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		
		/*MenuItem sinhronizujStavke = menu.add("Sinhronizuj");
		sinhronizujStavke.setIcon(R.drawable.ic_action_sync);
		sinhronizujStavke.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);*/
		
		return super.onCreateOptionsMenu(menu);
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
    	
    	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TIME_INTERVAL, DISTANCE, this);
    	currentBestLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    	
    	try {
    		menu.getItem(0).setIcon(R.drawable.ic_gps_status_bad);
    		
    		Date trenutniDatum = new Date();
    		if (dateFormat.parse(dateFormat.format(trenutniDatum)).compareTo(dateFormat.parse(dateFormat.format(danasnjiDatum))) != 0) {
    			danasnjiDatum = trenutniDatum;
    			getActionBar().setTitle(getString(R.string.dialog_title_record_visit) + " " + screenDateFormat.format(new Date()));
        		getLoaderManager().restartLoader(0, null, this);
            	getLoaderManager().restartLoader(1, null, this);
    		}
		} catch (Exception e) {
		}
	}
	
	@Override
	protected void onDestroy() {
		locationManager.removeUpdates(this);
		super.onStop();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		if (hasFocus) {
			
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TIME_INTERVAL, DISTANCE, this);
			
			if (checkGpsEnabled()) {
				tvGpsIskljucen.setVisibility(View.INVISIBLE);
			} else {
				tvGpsIskljucen.setVisibility(View.VISIBLE);
			}
		}
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
		
		cv = new ContentValues();
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
		cv = commonContentValues();
		cv.putNull(MobileStoreContract.Visits.CUSTOMER_ID);
		cv.put(MobileStoreContract.Visits.ODOMETER, kilometraza);
		cv.put(MobileStoreContract.Visits.ARRIVAL_TIME, DateUtils.toDbDate(newDate));
		cv.put(MobileStoreContract.Visits.DEPARTURE_TIME, DateUtils.toDbDate(newDate));
		cv.put(MobileStoreContract.Visits.VISIT_RESULT, ApplicationConstants.VISIT_TYPE_START_DAY);
		
		cv.put(MobileStoreContract.Visits.LATITUDE, currentBestLocation.getLatitude());
		cv.put(MobileStoreContract.Visits.LONGITUDE, currentBestLocation.getLongitude());
		cv.put(MobileStoreContract.Visits.ACCURACY, currentBestLocation.getAccuracy());
		
		if (checkGpsRecent()) {
			System.out.println("# GPS RECENT SENDING VISIT");
			getContentResolver().insert(MobileStoreContract.Visits.CONTENT_URI, cv);
			sendRecordedVisit(); // ContentUris.parseId(result)
		} else {
			System.out.println("# GPS NOT RECENT SHOWING DIALOG");
			cv.put(MobileStoreContract.Visits.LATITUDE, NOT_AVAILABLE);
			cv.put(MobileStoreContract.Visits.LONGITUDE, NOT_AVAILABLE);
			cv.put(MobileStoreContract.Visits.ACCURACY, NOT_AVAILABLE);
			handler.post(runnable);
		}
	}
	
	private void novaRealizacija(String kilometraza, int customerId, Date arrivalDate, boolean isValidLocation) {
		cv = commonContentValues();
		if (customerId == 0) {
			cv.putNull(Visits.CUSTOMER_ID);
		} else {
			Cursor isPotentialCustomer = getContentResolver().query(MobileStoreContract.Customers.CONTENT_URI, new String[] {Customers.CONTACT_COMPANY_NO}, 
					"("+Customers.CONTACT_COMPANY_NO + " IS NULL OR " + Customers.CONTACT_COMPANY_NO + "='')" + " AND " + Customers._ID + "=?" , new String[] { String.valueOf(customerId) }, null);
			if (isPotentialCustomer.moveToFirst()) {
				cv.put(Visits.POTENTIAL_CUSTOMER, 1);
			}
			isPotentialCustomer.close();
			cv.put(Visits.CUSTOMER_ID, customerId);		
		}
		cv.put(MobileStoreContract.Visits.ODOMETER, kilometraza);
		cv.put(MobileStoreContract.Visits.ARRIVAL_TIME, DateUtils.toDbDate(arrivalDate));
		cv.put(MobileStoreContract.Visits.VISIT_RESULT, ApplicationConstants.VISIT_TYPE_NO_CLOSURE);
		cv.put(MobileStoreContract.Visits.VISIT_STATUS, ApplicationConstants.VISIT_STATUS_STARTED);
		cv.put(MobileStoreContract.Visits.ADDRESS_NO, selectedAddressNo);
		if (isValidLocation) {
			cv.put(MobileStoreContract.Visits.VALID_LOCATION, 1);
		}

		cv.put(MobileStoreContract.Visits.LATITUDE, currentBestLocation.getLatitude());
		cv.put(MobileStoreContract.Visits.LONGITUDE, currentBestLocation.getLongitude());
		cv.put(MobileStoreContract.Visits.ACCURACY, currentBestLocation.getAccuracy());
		
		if (checkGpsRecent()) {
			System.out.println("# GPS RECENT SENDING VISIT");
			getContentResolver().insert(MobileStoreContract.Visits.CONTENT_URI, cv);
			sendRecordedVisit();
		} else {
			System.out.println("# GPS NOT RECENT SHOWING DIALOG");
			cv.put(MobileStoreContract.Visits.LATITUDE, NOT_AVAILABLE);
			cv.put(MobileStoreContract.Visits.LONGITUDE, NOT_AVAILABLE);
			cv.put(MobileStoreContract.Visits.ACCURACY, NOT_AVAILABLE);
			handler.post(runnable);
		}
	}
	
	private void krajRealizacije(String visitId, int rezultat, String beleska) {
		Date newDate = new Date();
		cv = commonContentValues();
		if (rezultat == 0) {
			cv.put(MobileStoreContract.Visits.VISIT_RESULT, ApplicationConstants.VISIT_TYPE_CLOSURE);
		}
		cv.put(MobileStoreContract.Visits.NOTE, beleska);
		cv.put(MobileStoreContract.Visits.DEPARTURE_TIME, DateUtils.toDbDate(newDate));
		
		cv.put(MobileStoreContract.Visits.LATITUDE, currentBestLocation.getLatitude());
		cv.put(MobileStoreContract.Visits.LONGITUDE, currentBestLocation.getLongitude());
		cv.put(MobileStoreContract.Visits.ACCURACY, currentBestLocation.getAccuracy());
		
		if (checkGpsRecent()) {
			System.out.println("# GPS RECENT SENDING VISIT");
			int status = getContentResolver().update(MobileStoreContract.Visits.CONTENT_URI, cv, "visits._id=?", new String[] { visitId });
			if (status > 0) {
				Toast.makeText(getApplicationContext(), R.string.kraj_realizacije_zabelezen, Toast.LENGTH_LONG).show();
			}
			sendRecordedVisit();
		} else {
			System.out.println("# GPS NOT RECENT SHOWING DIALOG");
			cv.put(MobileStoreContract.Visits.LATITUDE, NOT_AVAILABLE);
			cv.put(MobileStoreContract.Visits.LONGITUDE, NOT_AVAILABLE);
			cv.put(MobileStoreContract.Visits.ACCURACY, NOT_AVAILABLE);
			handler.post(runnable);
		}
	}
	
	private void pauza(final String kilometraza) {
		Date newDate = new Date();
		cv = commonContentValues();
		cv.putNull(MobileStoreContract.Visits.CUSTOMER_ID);
		cv.put(MobileStoreContract.Visits.ODOMETER, kilometraza);
		cv.put(MobileStoreContract.Visits.ARRIVAL_TIME, DateUtils.toDbDate(newDate));
		cv.put(MobileStoreContract.Visits.VISIT_RESULT, ApplicationConstants.VISIT_TYPE_PAUSE);
		cv.put(MobileStoreContract.Visits.VISIT_STATUS, ApplicationConstants.VISIT_STATUS_STARTED);
		
		cv.put(MobileStoreContract.Visits.LATITUDE, currentBestLocation.getLatitude());
		cv.put(MobileStoreContract.Visits.LONGITUDE, currentBestLocation.getLongitude());
		cv.put(MobileStoreContract.Visits.ACCURACY, currentBestLocation.getAccuracy());

		if (checkGpsRecent()) {
			System.out.println("# GPS RECENT SENDING VISIT");
			getContentResolver().insert(MobileStoreContract.Visits.CONTENT_URI, cv);
			sendRecordedVisit();
		} else {
			System.out.println("# GPS NOT RECENT SHOWING DIALOG");
			cv.put(MobileStoreContract.Visits.LATITUDE, NOT_AVAILABLE);
			cv.put(MobileStoreContract.Visits.LONGITUDE, NOT_AVAILABLE);
			cv.put(MobileStoreContract.Visits.ACCURACY, NOT_AVAILABLE);
			handler.post(runnable);
		}
	}
	
	private void krajPauze(String visitId, int rezultat, String beleska) {
		Date newDate = new Date();
		cv = commonContentValues();
		cv.put(MobileStoreContract.Visits.ENTRY_SUBTYPE, rezultat);
		cv.put(MobileStoreContract.Visits.NOTE, beleska);
		cv.put(MobileStoreContract.Visits.DEPARTURE_TIME, DateUtils.toDbDate(newDate));
		
		cv.put(MobileStoreContract.Visits.LATITUDE, currentBestLocation.getLatitude());
		cv.put(MobileStoreContract.Visits.LONGITUDE, currentBestLocation.getLongitude());
		cv.put(MobileStoreContract.Visits.ACCURACY, currentBestLocation.getAccuracy());
		
		if (checkGpsRecent()) {
			System.out.println("# GPS RECENT SENDING VISIT");
			int status = getContentResolver().update(MobileStoreContract.Visits.CONTENT_URI, cv, "visits._id=?", new String[] { visitId });
			if (status > 0) {
				Toast.makeText(getApplicationContext(), R.string.kraj_pauze_zabelezen, Toast.LENGTH_LONG).show();
			}
			sendRecordedVisit();
		} else {
			System.out.println("# GPS NOT RECENT SHOWING DIALOG");
			cv.put(MobileStoreContract.Visits.LATITUDE, NOT_AVAILABLE);
			cv.put(MobileStoreContract.Visits.LONGITUDE, NOT_AVAILABLE);
			cv.put(MobileStoreContract.Visits.ACCURACY, NOT_AVAILABLE);
			handler.post(runnable);
		}
	}
	
	private void zavrsiDan(String kilometraza) {
		Date newDate = new Date();
		cv = commonContentValues();
		cv.putNull(MobileStoreContract.Visits.CUSTOMER_ID);
		cv.put(MobileStoreContract.Visits.ODOMETER, kilometraza);
		cv.put(MobileStoreContract.Visits.ARRIVAL_TIME, DateUtils.toDbDate(newDate));
		cv.put(MobileStoreContract.Visits.DEPARTURE_TIME, DateUtils.toDbDate(newDate));
		cv.put(MobileStoreContract.Visits.VISIT_RESULT, ApplicationConstants.VISIT_TYPE_END_DAY);
		
		cv.put(MobileStoreContract.Visits.LATITUDE, currentBestLocation.getLatitude());
		cv.put(MobileStoreContract.Visits.LONGITUDE, currentBestLocation.getLongitude());
		cv.put(MobileStoreContract.Visits.ACCURACY, currentBestLocation.getAccuracy());
		
		if (checkGpsRecent()) {
			System.out.println("# GPS RECENT SENDING VISIT");
			getContentResolver().insert(MobileStoreContract.Visits.CONTENT_URI, cv);
			sendRecordedVisit();
		} else {
			System.out.println("# GPS NOT RECENT SHOWING DIALOG");
			cv.put(MobileStoreContract.Visits.LATITUDE, NOT_AVAILABLE);
			cv.put(MobileStoreContract.Visits.LONGITUDE, NOT_AVAILABLE);
			cv.put(MobileStoreContract.Visits.ACCURACY, NOT_AVAILABLE);
			handler.post(runnable);
		}
	}
	
	private void povratakKuci(String kilometraza) {
		Date newDate = new Date();
		cv = commonContentValues();
		cv.putNull(MobileStoreContract.Visits.CUSTOMER_ID);
		cv.put(MobileStoreContract.Visits.ODOMETER, kilometraza);
		cv.put(MobileStoreContract.Visits.ARRIVAL_TIME, DateUtils.toDbDate(newDate));
		cv.put(MobileStoreContract.Visits.DEPARTURE_TIME, DateUtils.toDbDate(newDate));
		cv.put(MobileStoreContract.Visits.VISIT_RESULT, ApplicationConstants.VISIT_TYPE_BACK_HOME);
		
		cv.put(MobileStoreContract.Visits.LATITUDE, currentBestLocation.getLatitude());
		cv.put(MobileStoreContract.Visits.LONGITUDE, currentBestLocation.getLongitude());
		cv.put(MobileStoreContract.Visits.ACCURACY, currentBestLocation.getAccuracy());
		
		if (checkGpsRecent()) {
			System.out.println("# GPS RECENT SENDING VISIT");
			getContentResolver().insert(MobileStoreContract.Visits.CONTENT_URI, cv);
			sendRecordedVisit();
		} else {
			System.out.println("# GPS NOT RECENT SHOWING DIALOG");
			cv.put(MobileStoreContract.Visits.LATITUDE, NOT_AVAILABLE);
			cv.put(MobileStoreContract.Visits.LONGITUDE, NOT_AVAILABLE);
			cv.put(MobileStoreContract.Visits.ACCURACY, NOT_AVAILABLE);
			handler.post(runnable);
		}
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		switch (id) {
		case 0:
			return new CursorLoader(this, MobileStoreContract.Visits.CONTENT_URI, VisitsQuery.PROJECTION, VISITS_DATE_FILTER + " AND " + VISITS_FILTER_REALIZACIJA, new String[] { DateUtils.toDbDate(danasnjiDatum) }, null);
		case 1:
			return new CursorLoader(this, MobileStoreContract.Visits.CONTENT_URI, VisitsQuery.PROJECTION, VISITS_DATE_FILTER + " AND " + VISITS_FILTER_PLAN, new String[] { DateUtils.toDbDate(danasnjiDatum) }, MobileStoreContract.Visits.ARRIVAL_TIME);
		default:
			return new CursorLoader(this, MobileStoreContract.Visits.CONTENT_URI, VisitsQuery.PROJECTION, VISITS_DATE_FILTER + " AND " + VISITS_FILTER_REALIZACIJA, new String[] { DateUtils.toDbDate(danasnjiDatum) }, null);
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if(cursor != null) {
			switch (loader.getId()) {
				case 0:
					if (cursorAdapter != null) {
						cursorAdapter.swapCursor(cursor);
						lvRealizacija.setSelection(lvRealizacija.getAdapter().getCount()-1);
					}
					break;
				case 1:
					if (cursorAdapterPlan != null) {
						cursorAdapterPlan.swapCursor(cursor);
					}
					break;
				default:
					break;
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		switch (loader.getId()) {
			case 0:
				cursorAdapter.swapCursor(null);
				break;
			case 1:
				cursorAdapterPlan.swapCursor(null);
				break;
			default:
				break;
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
		
		customerAddress = (Button) dialog.findViewById(R.id.dialog_nova_realizacija_adresa_input);
		
		acKupac.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View arg1, int position, long id) {
				Cursor cursor = (Cursor) customerCursorAdapter.getItem(position);
				selectedCustomerId = cursor.getInt(0);
				selectedCustomerNo = cursor.getString(1);
				
				prikazAdreseKupca();
			}
		});
		
		customerAddress.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialogAdresaKupca();
			}
		});
		
		datumInput = (Button) dialog.findViewById(R.id.dialog_nova_realizacija_datum_input);
		vremeInput = (Button) dialog.findViewById(R.id.dialog_nova_realizacija_vreme_input);
		final EditText kilometrazaInput = (EditText) dialog.findViewById(R.id.dialog_nova_realizacija_kilometraza_input);
		final CheckBox validLocation = (CheckBox) dialog.findViewById(R.id.dialog_nova_realizacija_valid_location);
		Button bDialogOk = (Button) dialog.findViewById(R.id.dialogButtonOK);
		
		if (customerId != -1) {
			Cursor c = getContentResolver().query(MobileStoreContract.Customers.buildCustomersUri(String.valueOf(customerId)), new String[] {MobileStoreContract.Customers.CUSTOMER_NO, MobileStoreContract.Customers.NAME}, null, null, null);
        	if (c.moveToFirst()) {
	        	final int codeIndex = c.getColumnIndexOrThrow(MobileStoreContract.Customers.CUSTOMER_NO);
	    		final int nameIndex = c.getColumnIndexOrThrow(MobileStoreContract.Customers.NAME);
	    		final String result = c.getString(codeIndex) + " - " + c.getString(nameIndex);
	    		acKupac.setText(result);
	    		selectedCustomerNo = c.getString(codeIndex);
        	}
        	selectedCustomerId = customerId;
        	prikazAdreseKupca();
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
				boolean isValid = validLocation.isChecked();
				
				if (selectedCustomerId > 0) {
					if (km.trim().length() > 0) {
						try {
							// POLJA DATUM I VREME
							//Date selectedDate = dateTimeFormat.parse(datumInput.getText().toString() + " " + vremeInput.getText().toString());
							//novaRealizacija(km, selectedCustomerId, selectedDate);
							novaRealizacija(km, selectedCustomerId, new Date(), isValid);
							if (visitId != -1) {
								// BRISANJE PLANA
								getContentResolver().delete(MobileStoreContract.Visits.CONTENT_URI, MobileStoreContract.Visits._ID + "=?", new String[] { String.valueOf(visitId) });
							}
						} catch (Exception e) {
						}
						selectedCustomerId = 0;
						selectedCustomerNo = null;
						dialog.dismiss();
					} else {
						kilometrazaInput.requestFocus();
						Toast.makeText(getApplicationContext(), R.string.title_unesite_vrednost, Toast.LENGTH_LONG).show();
					}
				} else {
					if (km.trim().length() > 0) {
						try {
							// POLJA DATUM I VREME
							//Date selectedDate = dateTimeFormat.parse(datumInput.getText().toString() + " " + vremeInput.getText().toString());
							//novaRealizacija(km, selectedCustomerId, selectedDate);
							novaRealizacija(km, 0, new Date(), isValid);
							if (visitId != -1) {
								// BRISANJE PLANA
								getContentResolver().delete(MobileStoreContract.Visits.CONTENT_URI, MobileStoreContract.Visits._ID + "=?", new String[] { String.valueOf(visitId) });
							}
						} catch (Exception e) {
						}
						selectedCustomerId = 0;
						selectedCustomerNo = null;
						dialog.dismiss();
					} else {
						kilometrazaInput.requestFocus();
						Toast.makeText(getApplicationContext(), R.string.title_unesite_vrednost, Toast.LENGTH_LONG).show();
					}
				}
			}
		});
		dialog.show();
	}
	
	private void prikazAdreseKupca() {
		if (selectedCustomerNo != null) {
			Cursor addressDefaultCursor = getContentResolver().query(MobileStoreContract.Customers.CONTENT_URI, DefaultCustomerAddressQuery.PROJECTION, Tables.CUSTOMERS+"." + Customers.CUSTOMER_NO + "=?", new String[] { selectedCustomerNo }, null);
			if (addressDefaultCursor != null && addressDefaultCursor.moveToFirst()) {
				String address = addressDefaultCursor.getString(DefaultCustomerAddressQuery.ADDRESS);
				String city = addressDefaultCursor.getString(DefaultCustomerAddressQuery.CITY);
				String post_code = addressDefaultCursor.getString(DefaultCustomerAddressQuery.POST_CODE);
				customerAddress.setText(String.format("%s, %s %s", address, post_code, city));
			}
			if (addressDefaultCursor != null && !addressDefaultCursor.isClosed()) {
				addressDefaultCursor.close();
			}
		} else {
			customerAddress.setText("");
		}
		selectedAddressNo = null;
	}
	
	public void krajRealizacijeDijalog(final String visitId, final int tip) {
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.dialog_kraj_realizacije);
		
		final Spinner rezultat = (Spinner) dialog.findViewById(R.id.dialog_kraj_realizacije_rezultat_spinner);
		final EditText beleskaInput = (EditText) dialog.findViewById(R.id.dialog_kraj_realizacije_beleska_input);
		Button bDialogOk = (Button) dialog.findViewById(R.id.dialogButtonOK);
		ArrayAdapter<CharSequence> adapter = null;
		
		beleskaInput.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {	
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() > 250) {
					beleskaInput.setText(beleskaInput.getText().toString().substring(0, 250));
					beleskaInput.setSelection(beleskaInput.getText().length());
					Toast.makeText(NovaRealizacijaActivity_backup.this, "Moguće je uneti maksimalno 250 karaktera", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
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
			if (kupacSifra != null) {
				naslov = kupacSifra + " - " + kupacNaziv;
			} else {
				naslov = "NEPOZNAT KUPAC";
			}
			vreme = vremeo + " - " + vremed;
			break;
		case 3:
			if (kupacSifra != null) {
				naslov = kupacSifra + " - " + kupacNaziv;
			} else {
				naslov = "NEPOZNAT KUPAC";
			}
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
	
	public void sendRecordedVisit() {

		Cursor cursor = getContentResolver().query(MobileStoreContract.Visits.CONTENT_URI, VisitsQuery.PROJECTION, VISITS_DATE_FILTER + " AND " + VISITS_FILTER_REALIZACIJA + " AND " + "visits.is_sent=0", new String[] { DateUtils.toDbDate(new Date()) }, null);
		if (cursor.moveToFirst()) {
			do {
				SetRealizedVisitsToCustomersSyncObject visitsToCustomersSyncObject = new SetRealizedVisitsToCustomersSyncObject(cursor.getInt(VisitsQuery._ID));
		    	Intent intent = new Intent(this, NavisionSyncService.class);
				intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, visitsToCustomersSyncObject);
				startService(intent);
		    } while (cursor.moveToNext());
		}
	}
	
	private void dialogGps() {
		gpsDialog = new Dialog(this);
		gpsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		gpsDialog.setCancelable(false);
		gpsDialog.setCanceledOnTouchOutside(false);
		gpsDialog.setContentView(R.layout.dialog_gps);
		
		gpsDialog.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				return true;
			}
		});
		
		final ProgressBar mProgressBar = (ProgressBar) gpsDialog.findViewById(R.id.pbGpsDialog);
		CountDownTimer mCountDownTimer;

		mProgressBar.setProgress(30);
		mCountDownTimer = new CountDownTimer(30000, 1000) {

			int i = 30;
			
	        @Override
	        public void onTick(long millisUntilFinished) {
	            mProgressBar.setProgress(i--);
	        }

	        @Override
	        public void onFinish() {
	        	try {
	        		gpsDialog.dismiss();
	        		/*
		        	currentBestLocation.setLatitude(0d);
					currentBestLocation.setLongitude(0d);
					currentBestLocation.setAccuracy(0f);
		        	System.out.println("# GPS DIALOG SENDING VISITS WITH BLANK DATA");
		        	sendRecordedVisit(0);
		        	*/
				} catch (Exception e) {
				}
	        	
	        }
	    };
	    mCountDownTimer.start();
	    gpsDialog.show();
	}
	
	private void dialogAdresaKupca() {
		if (selectedCustomerNo == null) {
			Toast.makeText(this, "Kupac nije izabran!", Toast.LENGTH_SHORT).show();
			return;
		}
		
		AddressSelectDialog addressSelectDialog = new AddressSelectDialog();
		Intent tempIntent = new Intent(Intent.ACTION_VIEW, null);
		tempIntent.putExtra(AddressSelectDialog.EXTRA_CUSTOMER_NO, selectedCustomerNo);
		//tempIntent.putExtra(AddressSelectDialog.EXTRA_DIALOG_ID, SHIPPING_ADDRESS_SELECTOR);
		
		Cursor addressDefaultCursor = getContentResolver().query(MobileStoreContract.Customers.CONTENT_URI, DefaultCustomerAddressQuery.PROJECTION, Tables.CUSTOMERS+"." + Customers.CUSTOMER_NO + "=?", new String[] { String.valueOf( selectedCustomerNo )  }, null);
		if (addressDefaultCursor != null && addressDefaultCursor.moveToFirst()) {
			String address = addressDefaultCursor.getString(DefaultCustomerAddressQuery.ADDRESS);
			String city = addressDefaultCursor.getString(DefaultCustomerAddressQuery.CITY);
			String post_code = addressDefaultCursor.getString(DefaultCustomerAddressQuery.POST_CODE);
			
			addressSelectDialog.setDefault_address(address);
			addressSelectDialog.setDefault_city(city);
			addressSelectDialog.setDefault_post_code(post_code);
			
			customerAddress.setText(String.format("%s, %s %s", address, post_code, city));
		}
		if (addressDefaultCursor != null && !addressDefaultCursor.isClosed()) {
			addressDefaultCursor.close();
		}
		
		addressSelectDialog.setArguments(BaseActivity.intentToFragmentArguments(tempIntent));
		addressSelectDialog.show(getSupportFragmentManager(), "ADDRESS_DIALOG_SHIPPING");
		
		
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
        	ImageView ivRealizacijaStatus = (ImageView) view.findViewById(R.id.ivRealizacijaStatus);
        	String arrivalTime = null, departureTime = null, odometer = null;
        	RelativeLayout layoutRealizacijaKarticaKupca = (RelativeLayout) view.findViewById(R.id.layoutRealizacijaKarticaKupca);
        	RelativeLayout layoutRealizacijaPorudzbina = (RelativeLayout) view.findViewById(R.id.layoutRealizacijaPorudzbina);
        	RelativeLayout layoutRealizacijaZavrsi = (RelativeLayout) view.findViewById(R.id.layoutRealizacijaZavrsi);
        	RelativeLayout layoutRealizacijaStatus = (RelativeLayout) view.findViewById(R.id.layoutRealizacijaStatus);
        	
        	final int selectedVisitId = cursor.getInt(VisitsQuery._ID);
        	final int selectedType = cursor.getInt(VisitsQuery.VISIT_RESULT);
        	final int customerId = cursor.getInt(VisitsQuery.CUSTOMER_ID);
        	String customerNo = cursor.getString(VisitsQuery.CUSTOMER_NO);
        	
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
				layoutRealizacijaKarticaKupca.setVisibility(View.GONE);
        		layoutRealizacijaPorudzbina.setVisibility(View.GONE);
				break;
			case 2:
				if (customerNo != null) {
					tvRealizacijaNaslov.setText(cursor.getString(VisitsQuery.CUSTOMER_NO) + " - " + cursor.getString(VisitsQuery.CUSTOMER_NAME));
				} else {
					tvRealizacijaNaslov.setText("NEPOZNAT KUPAC");
				}
				tvRealizacijaVreme.setText(arrivalTime + " - " + departureTime);
				layoutRealizacijaKarticaKupca.setVisibility(View.VISIBLE);
        		layoutRealizacijaPorudzbina.setVisibility(View.VISIBLE);
				break;
			case 3:
				if (customerNo != null) {
					tvRealizacijaNaslov.setText(cursor.getString(VisitsQuery.CUSTOMER_NO) + " - " + cursor.getString(VisitsQuery.CUSTOMER_NAME));
				} else {
					tvRealizacijaNaslov.setText("NEPOZNAT KUPAC");
				}
				tvRealizacijaVreme.setText(arrivalTime + " - " + departureTime);
				layoutRealizacijaKarticaKupca.setVisibility(View.VISIBLE);
        		layoutRealizacijaPorudzbina.setVisibility(View.VISIBLE);
				break;
			case 4:
				tvRealizacijaNaslov.setText("PAUZA");
				tvRealizacijaVreme.setText(arrivalTime + " - " + departureTime);
				layoutRealizacijaKarticaKupca.setVisibility(View.GONE);
        		layoutRealizacijaPorudzbina.setVisibility(View.GONE);
				break;
			case 5:
				tvRealizacijaNaslov.setText("KRAJ DANA");
				tvRealizacijaVreme.setText(arrivalTime);
				layoutRealizacijaKarticaKupca.setVisibility(View.GONE);
        		layoutRealizacijaPorudzbina.setVisibility(View.GONE);
				break;
			case 6:
				tvRealizacijaNaslov.setText("POVRATAK KUĆI");
				tvRealizacijaVreme.setText(arrivalTime);
				layoutRealizacijaKarticaKupca.setVisibility(View.GONE);
        		layoutRealizacijaPorudzbina.setVisibility(View.GONE);
				break;
			default:
				break;
			}
        	
        	tvRealizacijaKilometraza.setText(odometer);
        	layoutRealizacijaInfo.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					infoDijalog(selectedVisitId);
				}
			});
        	layoutRealizacijaKarticaKupca.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					final Uri customerListUri = MobileStoreContract.Customers.CONTENT_URI;
			        final Intent intent = new Intent(Intent.ACTION_VIEW, MobileStoreContract.Customers.buildCustomersUri(String.valueOf(customerId)));
			        intent.putExtra(CustomersViewActivity.EXTRA_MASTER_URI, customerListUri);
			        startActivity(intent);
				}
			});
        	layoutRealizacijaPorudzbina.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent newSaleOrderIntent = new Intent(Intent.ACTION_INSERT, MobileStoreContract.SaleOrders.CONTENT_URI);
					newSaleOrderIntent.putExtra("rs.gopro.mobile_store.extra.CUSTOMER_ID", customerId);
					startActivityForResult(newSaleOrderIntent, NEW_SALE_ORDER_REQUEST_CODE);
				}
			});
        	layoutRealizacijaZavrsi.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					krajRealizacijeDijalog(String.valueOf(selectedVisitId), selectedType);
				}
			});
        	
        	if (status != ApplicationConstants.VISIT_STATUS_STARTED) {
        		layoutRealizacijaZavrsi.setVisibility(View.GONE);
			} else {
				layoutRealizacijaZavrsi.setVisibility(View.VISIBLE);
			}
        	if (poslato == 1) {
        		ivRealizacijaStatus.setImageResource(R.drawable.ic_status_ok);
        		layoutRealizacijaStatus.setOnClickListener(null);
			} else {
				ivRealizacijaStatus.setImageResource(R.drawable.ic_status_bad);
				layoutRealizacijaStatus.setOnClickListener(new View.OnClickListener() {
					
					boolean prviKlik = true;
					@Override
					public void onClick(View v) {
						
						if (prviKlik) {
							lastClickTime = SystemClock.elapsedRealtime();
							prviKlik = false;
							return;
						}
						if (SystemClock.elapsedRealtime() - lastClickTime < 5000) {
							vibe.vibrate(100);
					        return;
					    }
					    lastClickTime = SystemClock.elapsedRealtime();
						sendRecordedVisit();
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
        	RelativeLayout layoutPlanRealizacijaKarticaKupca = (RelativeLayout) view.findViewById(R.id.layoutPlanRealizacijaKarticaKupca);
        	RelativeLayout layoutPlanRealizacijaPorudzbina = (RelativeLayout) view.findViewById(R.id.layoutPlanRealizacijaPorudzbina);
        	RelativeLayout layoutPlanRealizacija = (RelativeLayout) view.findViewById(R.id.layoutPlanRealizacija);
        	
        	final String date = DateUtils.formatDbDateForPresentation(cursor.getString(VisitsQuery.VISIT_DATE));
			final String time = DateUtils.formatDbTimeForPresentation(cursor.getString(VisitsQuery.ARRIVAL_TIME));
			
			final int visitId = cursor.getInt(VisitsQuery._ID);
			final int customerId = cursor.getInt(VisitsQuery.CUSTOMER_ID);
        	
        	tvPlanNaslov.setText(cursor.getString(VisitsQuery.CUSTOMER_NO) + " - " + cursor.getString(VisitsQuery.CUSTOMER_NAME));
        	tvPlanDatum.setText("PLAN " + date);
			tvPlanVreme.setText(time);
			
			layoutPlanRealizacijaKarticaKupca.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					final Uri customerListUri = MobileStoreContract.Customers.CONTENT_URI;
			        final Intent intent = new Intent(Intent.ACTION_VIEW, MobileStoreContract.Customers.buildCustomersUri(String.valueOf(customerId)));
			        intent.putExtra(CustomersViewActivity.EXTRA_MASTER_URI, customerListUri);
			        startActivity(intent);
				}
			});
			layoutPlanRealizacijaPorudzbina.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent newSaleOrderIntent = new Intent(Intent.ACTION_INSERT, MobileStoreContract.SaleOrders.CONTENT_URI);
					newSaleOrderIntent.putExtra("rs.gopro.mobile_store.extra.CUSTOMER_ID", customerId);
					startActivityForResult(newSaleOrderIntent, NEW_SALE_ORDER_REQUEST_CODE);
				}
			});
			layoutPlanRealizacija.setOnClickListener(new View.OnClickListener() {
				
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
	
	private interface DefaultCustomerAddressQuery {

		String[] PROJECTION = {
				BaseColumns._ID,
				MobileStoreContract.Customers.ADDRESS,
				MobileStoreContract.Customers.CITY,
				MobileStoreContract.Customers.POST_CODE
				};

//		int _ID = 0;
		int ADDRESS = 1;
		int CITY = 2;
		int POST_CODE = 3;
	}

	@Override
	public void onLocationChanged(Location location) {
		menu.getItem(0).setIcon(R.drawable.ic_gps_status_good);
		
		if (isBetterLocation(location, currentBestLocation)) {
			currentBestLocation = location;
		}
		if (cv != null) {
			cv.put(MobileStoreContract.Visits.LATITUDE, currentBestLocation.getLatitude());
			cv.put(MobileStoreContract.Visits.LONGITUDE, currentBestLocation.getLongitude());
			cv.put(MobileStoreContract.Visits.ACCURACY, currentBestLocation.getAccuracy());
			// TODO
			/*
			ContentValues[] cvArray = new ContentValues[1];
			cvArray[0] = cv;
			getContentResolver().bulkInsert(MobileStoreContract.Visits.CONTENT_URI, cvArray);
			*/
		}
		
		if (gpsDialog != null && gpsDialog.isShowing()) {
			gpsDialog.dismiss();
		}
		System.out.println("# GPS NEW DATA REMOVING DIALOG");
		handler.removeCallbacks(runnable);
		
		Cursor cursor = getContentResolver().query(MobileStoreContract.Visits.CONTENT_URI, VisitsQuery.PROJECTION, VISITS_DATE_FILTER + " AND " + VISITS_FILTER_REALIZACIJA + " AND " + "visits.is_sent=0", new String[] { DateUtils.toDbDate(new Date()) }, null);
		if (cursor.getCount() > 0) {
			System.out.println("# GPS NEW DATA SENDING VISITS (" + cursor.getCount() + ")");
			sendRecordedVisit();
		} else {
			System.out.println("# GPS NEW DATA NOTHING TO SEND");
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public void onAddressSelected(int dialogId, int address_id, String address,
			String address_no, String city, String post_code, String phone_no,
			String contact) {
		customerAddress.setText(String.format("%s, %s %s", address, post_code, city));
		selectedAddressNo = address_no;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == NEW_SALE_ORDER_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				String saleOrderId = data.getStringExtra("saleOrderId");
				Uri saleOrdersUri = MobileStoreContract.SaleOrders.CONTENT_URI;
                Intent intent = new Intent(Intent.ACTION_VIEW, MobileStoreContract.SaleOrderLines.buildSaleOrderLinesUri(String.valueOf(saleOrderId)));
                intent.putExtra(SaleOrdersPreviewActivity.EXTRA_MASTER_URI, saleOrdersUri);
                startActivity(intent);
			}
		}
	}

}
