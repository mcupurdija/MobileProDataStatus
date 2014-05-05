package rs.gopro.mobile_store.ui.fragment;

import static rs.gopro.mobile_store.util.LogUtils.makeLogTag;

import java.util.Calendar;
import java.util.Date;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.Visits;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.ui.BaseActivity;
import rs.gopro.mobile_store.util.ApplicationConstants;
import rs.gopro.mobile_store.util.DatePickerFragment;
import rs.gopro.mobile_store.util.DateUtils;
import rs.gopro.mobile_store.util.LogUtils;
import android.app.Activity;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

public class RecordVisitListFromMenuFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> { //, OnItemLongClickListener
	
	private static final String TAG = makeLogTag(RecordVisitListFromMenuFragment.class);

	private static final String STATE_SELECTED_ID = "selectedId";
	private static final String VISITS_DATE_FILTER = "DATE("+Tables.VISITS+"."+MobileStoreContract.Visits.VISIT_DATE+")=DATE(?)";
	private static final String ORDER_BY_VISITS_FILTER = "DATE("+"visits." + Visits.VISIT_DATE+")" + " DESC, visits." + Visits.ARRIVAL_TIME+" ASC";

	private Uri mVisitsUri;
//	private String mAction;
	private CursorAdapter mAdapter;
	private String mSelectedVisitId;
	private boolean mHasSetEmptyText = false;
	
	private Button realizacijaDatumFilter;
	private Calendar calender = Calendar.getInstance();
	private Date odabraniDatum = new Date();
	private final int visitQueryToken = RecordedVisitsQuery._TOKEN;

//	private MainContextualActionBarCallback actionBarCallback;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			mSelectedVisitId = savedInstanceState.getString(STATE_SELECTED_ID);
		}
		
		reloadFromArguments(getArguments());
		
		LogUtils.LOGI(TAG, "Fragment created");
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return super.onCreateView(inflater, container, savedInstanceState);
	}



	public void reloadFromArguments(Bundle arguments) {
		// Teardown from previous arguments
		setListAdapter(null);

		// Load new arguments
		Intent intent = BaseActivity.fragmentArgumentsToIntent(arguments);
		mVisitsUri = intent.getData();
//		mAction = intent.getAction();
		

		if (mVisitsUri == null) {
			LogUtils.LOGE(TAG, "Uri or action is null!");
			return;
		}

		mAdapter = new RecordedVisitsAdapter(getActivity());

		setListAdapter(mAdapter);

		// Start background query to load vendors
		getLoaderManager().initLoader(visitQueryToken, null, this);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		view.setBackgroundColor(Color.WHITE);
		final ListView listView = getListView();
		listView.setSelector(android.R.color.transparent);
		listView.setCacheColorHint(Color.WHITE);
		//listView.setOnItemLongClickListener(this);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (savedInstanceState != null) {
			getLoaderManager().restartLoader(RecordedVisitsQuery._TOKEN, null, this);
		}
		
		if (!mHasSetEmptyText) {
			// Could be a bug, but calling this twice makes it become visible
			// when it shouldn't be visible.
			setEmptyText(getString(R.string.empty_recorded_visits));
			mHasSetEmptyText = true;
		}
		
		realizacijaDatumFilter = (Button) getActivity().findViewById(R.id.realizacijaDatumFilter);
		realizacijaDatumFilter.setText(DateUtils.toUIDate(odabraniDatum));
		realizacijaDatumFilter.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDatePicker();
			}
		});
	}
	
	private void showDatePicker() {
		DatePickerFragment date = new DatePickerFragment();

		Bundle args = new Bundle();
		args.putInt("year", calender.get(Calendar.YEAR));
		args.putInt("month", calender.get(Calendar.MONTH));
		args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
		date.setArguments(args);
		
		date.setCallBack(ondate);
		date.show(getActivity().getSupportFragmentManager(), "Date Picker");
	}

	private OnDateSetListener ondate = new OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		    calender.set(Calendar.YEAR, year);
		    calender.set(Calendar.MONTH, monthOfYear);
		    calender.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		    odabraniDatum = calender.getTime();
		    realizacijaDatumFilter.setText(DateUtils.toUIDate(odabraniDatum));
		    
		    getLoaderManager().destroyLoader(visitQueryToken);
			getLoaderManager().initLoader(visitQueryToken, null, RecordVisitListFromMenuFragment.this);
		}
	};

	private final ContentObserver mObserver = new ContentObserver(new Handler()) {
		@Override
		public void onChange(boolean selfChange) {
			if (getActivity() == null) {
				return;
			}
			Loader<Cursor> loader = getLoaderManager().getLoader(RecordedVisitsQuery._TOKEN);
			if (loader != null) {
				loader.forceLoad();
			}
		}
	};
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
//		actionBarCallback = (MainContextualActionBarCallback) activity;
		activity.getContentResolver().registerContentObserver(MobileStoreContract.Visits.CONTENT_URI, true, mObserver);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		getActivity().getContentResolver().unregisterContentObserver(mObserver);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mSelectedVisitId != null) {
			outState.putString(STATE_SELECTED_ID, mSelectedVisitId);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new CursorLoader(getActivity(), mVisitsUri, RecordedVisitsQuery.PROJECTION, VISITS_DATE_FILTER, new String[] { DateUtils.toDbDate(odabraniDatum) }, ORDER_BY_VISITS_FILTER);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (getActivity() == null) {
			return;
		}
		int token = loader.getId();
		if (token == RecordedVisitsQuery._TOKEN) {
			mAdapter.swapCursor(cursor);
		} else {
			cursor.close();
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
	}

	/**
	 * Sets current position in list from caller.
	 * 
	 * @param id
	 */
	public void setSelectedVisitId(String id) {
		mSelectedVisitId = id;
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
	}
	
	public void infoDijalog(int visitId) {
		
		final Dialog dialog = new Dialog(getActivity());
		dialog.setContentView(R.layout.dialog_realizacija_info);
		
		Date datum = null;
		String naslov = null, kupacSifra = null, kupacNaziv = null, vremed = "", vremeo = null, vreme = null, kilometraza = null, poruka = null;
		
		TextView tvRealizacijaInfoDatum = (TextView) dialog.findViewById(R.id.tvRealizacijaInfoDatum);
		TextView tvRealizacijaInfoVreme = (TextView) dialog.findViewById(R.id.tvRealizacijaInfoVreme);
		TextView tvRealizacijaInfoKilometraza = (TextView) dialog.findViewById(R.id.tvRealizacijaInfoKilometraza);
		TextView tvRealizacijaInfoPoruka = (TextView) dialog.findViewById(R.id.tvRealizacijaInfoPoruka);

		Cursor cursor = getActivity().getContentResolver().query(MobileStoreContract.Visits.CONTENT_URI, RecordedVisitsQuery.PROJECTION, "visits._id=?", new String[] { String.valueOf(visitId) }, null);
		cursor.moveToFirst();
		
		int tip = cursor.getInt(RecordedVisitsQuery.VISIT_RESULT);
		kilometraza = cursor.getString(RecordedVisitsQuery.ODOMETER);
		
		try {
			kupacSifra = cursor.getString(RecordedVisitsQuery.CUSTOMER_NO);
			kupacNaziv = cursor.getString(RecordedVisitsQuery.CUSTOMER_NAME);
			datum = DateUtils.getLocalDbDate(cursor.getString(RecordedVisitsQuery.VISIT_DATE));
			vremeo = DateUtils.formatDbTimeForPresentation(cursor.getString(RecordedVisitsQuery.ARRIVAL_TIME));
			vremed = DateUtils.formatDbTimeForPresentation(cursor.getString(RecordedVisitsQuery.DEPARTURE_TIME));
			poruka = cursor.getString(RecordedVisitsQuery.NOTE);
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
		tvRealizacijaInfoDatum.setText(getString(R.string.visit_date_label_title) + " " + DateUtils.toDbDate(datum));
		tvRealizacijaInfoVreme.setText(getString(R.string.visit_time_label_title) + " " + vreme);
		tvRealizacijaInfoKilometraza.setText(getString(R.string.visit_odometer_label_title) + " " + kilometraza);
		if (poruka != null) {
			tvRealizacijaInfoPoruka.setVisibility(View.VISIBLE);
			tvRealizacijaInfoPoruka.setText(getString(R.string.visit_note_label_title) + " " + poruka);
		}
		
		dialog.show();
	}

	/**
	 * {@link CursorAdapter} that renders a {@link RecordedVisitsQuery}.
	 */
	private class RecordedVisitsAdapter extends CursorAdapter {
		
		public RecordedVisitsAdapter(Context context) {
			super(context, null, false);
		}

		/** {@inheritDoc} */
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return getActivity().getLayoutInflater().inflate(R.layout.list_item_visit, parent, false);
		}

		/** {@inheritDoc} */
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			// UIUtils.setActivatedCompat(view,
			// cursor.getString(VisitsQuery.VENDOR_ID)
			// .equals(mSelectedVendorId));
			final String visit_id = String.valueOf(cursor.getInt(RecordedVisitsQuery._ID));
			view.setActivated(visit_id.equals(mSelectedVisitId));
			String arrivalTime = "\\";
	        if (!cursor.isNull(RecordedVisitsQuery.ARRIVAL_TIME)) { 
	        	arrivalTime = DateUtils.formatDbTimeForPresentation(cursor.getString(RecordedVisitsQuery.ARRIVAL_TIME));
	        }
	        
	        String departureTime = "\\";
	        if (!cursor.isNull(RecordedVisitsQuery.DEPARTURE_TIME)) { 
	        	departureTime = DateUtils.formatDbTimeForPresentation(cursor.getString(RecordedVisitsQuery.DEPARTURE_TIME));
	        }
	        
//	        String visit_date = UIUtils.formatDate(UIUtils.getDateTime(cursor.getString(VisitsQuery.VISIT_DATE)));
			
	        ((TextView) view.findViewById(R.id.visit_title)).setText(arrivalTime + " - " + departureTime);
			String customer_no = cursor.getString(RecordedVisitsQuery.CUSTOMER_NO);
			String customer_name = cursor.getString(RecordedVisitsQuery.CUSTOMER_NAME);//  + cursor.getString(VisitsQuery.CUSTOMER_NAME2);
        	if (customer_no == null || customer_no.length() < 1) {
        		customer_no = "NEPOZNAT KUPAC";
        		customer_name = "-";
        	}
        	final int visit_type = cursor.getInt(RecordedVisitsQuery.VISIT_TYPE);
        	int odometer = -1;
        	if (!cursor.isNull(RecordedVisitsQuery.ODOMETER)) {
        		odometer = cursor.getInt(RecordedVisitsQuery.ODOMETER);
        	}
        	int visit_result = -1;
        	if (!cursor.isNull(RecordedVisitsQuery.VISIT_RESULT)) {
        		visit_result = cursor.getInt(RecordedVisitsQuery.VISIT_RESULT);
        	}
        	String status = "";
        	if (visit_type == ApplicationConstants.VISIT_PLANNED) {
        		status = "PLAN";
        	} else {
        		status = "REALIZACIJA";
        	}
        		
        	if (visit_result == ApplicationConstants.VISIT_TYPE_START_DAY) {
        		customer_no = "POČETAK DANA";
        	} else if (visit_result == ApplicationConstants.VISIT_TYPE_END_DAY) {
        		customer_no = "KRAJ DANA";
        	} else if (visit_result == ApplicationConstants.VISIT_TYPE_BACK_HOME) {
        		customer_no = "POVRATAK KUĆI";
        	} else if (visit_result == ApplicationConstants.VISIT_TYPE_PAUSE) {
        		customer_no = "ODMOR/OSTALO";
        	}
        	
			((TextView) view.findViewById(R.id.visit_subtitle1)).setText(customer_no + " " + customer_name);
			((TextView) view.findViewById(R.id.visit_subtitle2)).setText(odometer == -1 ? "-" : "Kilometraža: " + String.valueOf(odometer));
			((TextView) view.findViewById(R.id.visit_status)).setText(status);
			
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mSelectedVisitId = visit_id;
					/*mAdapter.notifyDataSetChanged();
					final Uri visitsUri = MobileStoreContract.Visits.CONTENT_URI;
					// mAction is from main menu sent, and it is in par with expected actions in activities
					final Intent intent = new Intent(RecordVisitsMultipaneActivity.RECORD_VISITS_INTENT, MobileStoreContract.Visits.buildVisitUri(visit_id));
					intent.putExtra(RecordVisitsMultipaneActivity.EXTRA_MASTER_URI, visitsUri);
					startActivity(intent);*/
					
					infoDijalog(Integer.parseInt(mSelectedVisitId));
				}
			});
			
			view.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
//					String visitId = visit_id;
//					String visitType = String.valueOf(visit_type);
//					actionBarCallback.onLongClickItem(visitId, visitType);
					return true;
				}
			});
			view.setEnabled(true);
		}
	}

	private interface RecordedVisitsQuery {
		int _TOKEN = 0x1234;

		String[] PROJECTION = { 
				BaseColumns._ID, 
				MobileStoreContract.Visits.SALES_PERSON_ID, 
				MobileStoreContract.Visits.CUSTOMER_ID, 
				MobileStoreContract.Customers.CUSTOMER_NO, 
				MobileStoreContract.Customers.NAME, 
				MobileStoreContract.Customers.NAME_2,
				MobileStoreContract.Visits.VISIT_DATE, 
				MobileStoreContract.Visits.VISIT_RESULT, 
				MobileStoreContract.Visits.VISIT_TYPE, 
				MobileStoreContract.Visits.ARRIVAL_TIME, 
				MobileStoreContract.Visits.DEPARTURE_TIME, 
				MobileStoreContract.Visits.ODOMETER,
				MobileStoreContract.Visits.NOTE 
		};

		int _ID = 0;
//		int SALES_PERSON_ID = 1;
//		int CUSTOMER_ID = 2;
		int CUSTOMER_NO = 3;
		int CUSTOMER_NAME = 4;
//		int CUSTOMER_NAME2 = 5;
		int VISIT_DATE = 6;
		int VISIT_RESULT = 7;
		int VISIT_TYPE = 8;
		int ARRIVAL_TIME = 9;
		int DEPARTURE_TIME = 10;
		int ODOMETER = 11;
		int NOTE = 12;
	}

//	@Override
//	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
//		return true;
//	}

}
