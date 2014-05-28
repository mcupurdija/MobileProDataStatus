package rs.gopro.mobile_store.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import rs.gopro.mobile_store.R;
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
import rs.gopro.mobile_store.ws.model.SetPlannedVisitsToCustomersSyncObject;
import rs.gopro.mobile_store.ws.model.SyncResult;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class NoviPlanActivity extends BaseActivity implements LoaderCallbacks<Cursor> {

	private static final String VISITS_DATE_FILTER = "DATE(" + Tables.VISITS + "." + MobileStoreContract.Visits.VISIT_DATE + ")=DATE(?)";
	private static final String VISITS_FILTER_PLAN = Tables.VISITS + "." + MobileStoreContract.Visits.VISIT_TYPE + "=" + ApplicationConstants.VISIT_PLANNED;

	private CursorAdapter cursorAdapter;
	private CustomerAutocompleteCursorAdapter customerCursorAdapter;
	
	private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
	private SimpleDateFormat screenDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
	private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

	private Button bPreuzmi, bNovaPoseta, bPosalji;
	private Button datumInput, vremeInput;
	private ListView lvPlan;
	private ProgressBar pbPlan;
	private int selectedCustomerId;
	
	private Calendar calender = Calendar.getInstance();
	private Date odabraniDatum = new Date();
	
	private Menu menu;
	
	private BroadcastReceiver onNotice = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			SyncResult syncResult = intent.getParcelableExtra(NavisionSyncService.SYNC_RESULT);
			onSOAPResult(syncResult, intent.getAction());
		}
	};
	
	protected void onSOAPResult(SyncResult syncResult, String broadcastAction) {
		if (syncResult.getStatus().equals(SyncStatus.SUCCESS)) {
			if (SetPlannedVisitsToCustomersSyncObject.BROADCAST_SYNC_ACTION.equalsIgnoreCase(broadcastAction)) {
				Toast.makeText(getApplicationContext(), R.string.sync_success, Toast.LENGTH_LONG).show();
			}
		} else {
			Toast.makeText(getApplicationContext(), R.string.dialog_title_error_in_sync, Toast.LENGTH_LONG).show();
		}
		pbPlan.setVisibility(View.GONE);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_novi_plan);

		bPreuzmi = (Button) findViewById(R.id.bPreuzmi);
		bNovaPoseta = (Button) findViewById(R.id.bNovaPoseta);
		bPosalji = (Button) findViewById(R.id.bPosalji);
		lvPlan = (ListView) findViewById(R.id.lvPlan);
		pbPlan = (ProgressBar) findViewById(R.id.planProgressBar);

		cursorAdapter = new PlanAdapter(this);
		lvPlan.setAdapter(cursorAdapter);

		bPreuzmi.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			}
		});

		bNovaPoseta.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				addEditPlanDialog(-1, null, null, false, -1);
			}
		});

		bPosalji.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO ostaje da se vidi kako filtrirati one koje se salju
				SetPlannedVisitsToCustomersSyncObject visitsToCustomersSyncObject = new SetPlannedVisitsToCustomersSyncObject(odabraniDatum);
				Intent intent = new Intent(getApplicationContext(), NavisionSyncService.class);
				intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, visitsToCustomersSyncObject);
				startService(intent);
				pbPlan.setVisibility(View.VISIBLE);
			}
		});

		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.menu = menu;
		menu.add(0, Menu.FIRST, Menu.NONE, "Filtriraj po datumu")
				.setIcon(R.drawable.ic_action_kalendar)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			case Menu.FIRST:
				showDatePicker(1);
				return true;
			default:
				return false;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(onNotice);
	}

	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter plannedVisitsToCustomersSync = new IntentFilter(SetPlannedVisitsToCustomersSyncObject.BROADCAST_SYNC_ACTION);
    	LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, plannedVisitsToCustomersSync);
	}

	private void showDatePicker(int who) {
		DatePickerFragment date = new DatePickerFragment();

		Bundle args = new Bundle();
		args.putInt("year", calender.get(Calendar.YEAR));
		args.putInt("month", calender.get(Calendar.MONTH));
		args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
		date.setArguments(args);
		
		switch (who) {
			case 1:
				date.setCallBack(ondate);
				break;
			case 2:
				date.setCallBack(ondate2);
				break;
			default:
				break;
		}
		date.show(getSupportFragmentManager(), "Date Picker");
	}

	private OnDateSetListener ondate = new OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		    calender.set(Calendar.YEAR, year);
		    calender.set(Calendar.MONTH, monthOfYear);
		    calender.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			odabraniDatum = calender.getTime();
			
			menu.getItem(0).setTitle(screenDateFormat.format(odabraniDatum));

			getLoaderManager().destroyLoader(0);
			getLoaderManager().initLoader(0, null, NoviPlanActivity.this);
		}
	};
	
	private OnDateSetListener ondate2 = new OnDateSetListener() {
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
		args.putInt("minute", 0);
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
	
	private void addEditPlanDialog(int customerId, Date date, Date time, final boolean update, final int visitId) {
		final Dialog dialog = new Dialog(this);
		
		dialog.setContentView(R.layout.dialog_novi_plan);
		dialog.setTitle("Novi plan");
		
		final AutoCompleteTextView acKupac = (AutoCompleteTextView) dialog.findViewById(R.id.dialog_novi_plan_kupac_input);
		customerCursorAdapter = new CustomerAutocompleteCursorAdapter(this, null);
		acKupac.setAdapter(customerCursorAdapter);
		acKupac.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View arg1, int position, long id) {
				Cursor cursor = (Cursor) customerCursorAdapter.getItem(position);
				selectedCustomerId = cursor.getInt(0);
			}
		});
		datumInput = (Button) dialog.findViewById(R.id.dialog_novi_plan_datum_input);
		vremeInput = (Button) dialog.findViewById(R.id.dialog_novi_plan_vreme_input);
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
		if (date != null) {
			datumInput.setText(dateFormat.format(date));
		}
		if (time != null) {
			vremeInput.setText(timeFormat.format(time));
		}
		
		datumInput.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDatePicker(2);
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
				String datumInputText = datumInput.getText().toString();
				String vremeInputText = vremeInput.getText().toString();
				
				if (selectedCustomerId > 0) {
					if (!datumInputText.equals(getString(R.string.postavi_datum)) && !vremeInputText.equals(getString(R.string.postavi_vreme))) {
						try {
							Date selectedDate = dateTimeFormat.parse(datumInput.getText().toString() + " " + vremeInput.getText().toString());
							if (update) {
								noviPlan(selectedCustomerId, selectedDate, true, visitId);
							} else {
								noviPlan(selectedCustomerId, selectedDate, false, -1);
							}
						} catch (ParseException e) {
						}
						selectedCustomerId = 0;
						dialog.cancel();
					} else {
						datumInput.requestFocus();
						Toast.makeText(getApplicationContext(), R.string.title_unesite_vrednost, Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(getApplicationContext(), R.string.kupac_nije_izabran, Toast.LENGTH_LONG).show();
				}
			}
		});
		
		dialog.show();
	}
	
	private void noviPlan(int customerId, Date datum, boolean update, int visitId) {
		ContentValues cv = new ContentValues();
		cv.put(MobileStoreContract.Visits.SALES_PERSON_ID, salesPersonId);
		cv.put(MobileStoreContract.Visits.VISIT_DATE, DateUtils.toDbDate(datum));
		cv.put(MobileStoreContract.Visits.VISIT_TYPE, ApplicationConstants.VISIT_PLANNED);
		cv.putNull(MobileStoreContract.Visits.NOTE);
		cv.put(MobileStoreContract.Visits.IS_SENT, 0);
		cv.put(MobileStoreContract.Visits.CUSTOMER_ID, customerId);
		cv.put(MobileStoreContract.Visits.ENTRY_SUBTYPE, 0);
		cv.putNull(MobileStoreContract.Visits.ODOMETER);
		cv.put(MobileStoreContract.Visits.ARRIVAL_TIME, DateUtils.toDbDate(datum));
		cv.put(MobileStoreContract.Visits.VISIT_RESULT, ApplicationConstants.VISIT_TYPE_NO_CLOSURE);
		cv.put(MobileStoreContract.Visits.VISIT_STATUS, ApplicationConstants.VISIT_STATUS_NEW);

		if (update) {
			getContentResolver().update(MobileStoreContract.Visits.CONTENT_URI, cv, MobileStoreContract.Visits._ID + "=?", new String[] { String.valueOf(visitId) });
			Toast.makeText(this, R.string.plan_azuriran, Toast.LENGTH_LONG).show();
		} else {
			getContentResolver().insert(MobileStoreContract.Visits.CONTENT_URI, cv);
			Toast.makeText(this, R.string.plan_kreiran, Toast.LENGTH_LONG).show();
		}
	}
	
	private void izbrisiPlan(int visitId) {
		getContentResolver().delete(MobileStoreContract.Visits.CONTENT_URI, MobileStoreContract.Visits._ID + "=?", new String[] { String.valueOf(visitId) });
		Toast.makeText(this, R.string.plan_obrisan, Toast.LENGTH_LONG).show();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		return new CursorLoader(this, MobileStoreContract.Visits.CONTENT_URI,
				PlanQuery.PROJECTION, VISITS_DATE_FILTER + " AND " + VISITS_FILTER_PLAN,
				new String[] { DateUtils.toDbDate(odabraniDatum) }, MobileStoreContract.Visits.ARRIVAL_TIME);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (cursor != null) {
			cursorAdapter.swapCursor(cursor);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		if (cursorAdapter != null) {
			cursorAdapter.swapCursor(null);
		}
	}

	private class PlanAdapter extends CursorAdapter {
		public PlanAdapter(Context context) {
			super(context, null, false);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater.from(context);
			View v = inflater.inflate(R.layout.plan_stavka, parent, false);
			bindView(v, context, cursor);
			return v;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {

			TextView tvPlanNaslov = (TextView) view.findViewById(R.id.tvPlanNaslov);
			TextView tvPlanDatum = (TextView) view.findViewById(R.id.tvPlanDatum);
			TextView tvPlanVreme = (TextView) view.findViewById(R.id.tvPlanVreme);
			ImageView ivPlanEdit = (ImageView) view.findViewById(R.id.ivPlanEdit);
			ImageView ivPlanDelete = (ImageView) view.findViewById(R.id.ivPlanDelete);
			
			final int selectedVisitId = cursor.getInt(PlanQuery._ID);
			final int customerId = cursor.getInt(PlanQuery.CUSTOMER_ID);
			final String date = DateUtils.formatDbDateForPresentation(cursor.getString(PlanQuery.VISIT_DATE));
			final String time = DateUtils.formatDbTimeForPresentation(cursor.getString(PlanQuery.ARRIVAL_TIME));
			final String customerName = cursor.getString(PlanQuery.CUSTOMER_NAME);

			tvPlanNaslov.setText(cursor.getString(PlanQuery.CUSTOMER_NO) + " - " + customerName);
			tvPlanDatum.setText(date);
			tvPlanVreme.setText(time);

			ivPlanEdit.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Date datum = null;
					Date vreme = null;
					try {
						datum = screenDateFormat.parse(date);
						vreme = timeFormat.parse(time);
						addEditPlanDialog(customerId, datum, vreme, true, selectedVisitId);
					} catch (ParseException e) {
						LogUtils.LOGE("ERR", "DATE PARSE ERROR");
					}
				}
			});

			ivPlanDelete.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					ConfirmDialog(selectedVisitId, customerName);
				}
			});
		}
	}
	
	private void ConfirmDialog(final int id, final String name) {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
	    adb.setTitle(getString(R.string.potvrda_brisanja) + " [" + name + "]");

	    adb.setPositiveButton("Potvrdi", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	izbrisiPlan(id);
	        }
	    });

	    adb.setNegativeButton("Otkaži", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	dialog.dismiss();
	        }
	    });
	    adb.show();
	}

	private interface PlanQuery {

		String[] PROJECTION = { BaseColumns._ID,
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
				MobileStoreContract.Visits.VISIT_DATE };

		int _ID = 0;
		int CUSTOMER_ID = 1;
		int CUSTOMER_NO = 2;
		int CUSTOMER_NAME = 3;
//		int ENTRY_TYPE = 4;
//		int VISIT_STATUS = 5;
		int ARRIVAL_TIME = 6;
//		int DEPARTURE_TIME = 7;
//		int ODOMETER = 8;
//		int NOTE = 9;
//		int VISIT_RESULT = 10;
//		int IS_SENT = 11;
		int VISIT_DATE = 12;
	}

}
