package rs.gopro.mobile_store.ui.fragment;

import java.util.Date;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.Visits;
import rs.gopro.mobile_store.ui.BaseActivity;
import rs.gopro.mobile_store.ui.RecordVisitsMultipaneActivity;
import rs.gopro.mobile_store.ui.dialog.EditDepartureVisitDialog;
import rs.gopro.mobile_store.ui.dialog.EditFieldDialog;
import rs.gopro.mobile_store.util.DateUtils;
import rs.gopro.mobile_store.util.LogUtils;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class RecordVisitDetailFragment extends Fragment implements
		LoaderManager.LoaderCallbacks<Cursor>, OnDismissListener {

	private static final String TAG = LogUtils.makeLogTag(RecordVisitDetailFragment.class);
	
	private Uri mVisitUri;
	private int visitId = -1;
    private TextView mCustomerNoName;
    private TextView mVisitDate;
    private TextView mOdometer;
    private TextView mRecordedTime;
    private TextView mNote;
    
    private Button mStartVisit;
    private Button mEndVisit;
	private int odometerValue = -1;
	private boolean isDialogValueSent = false;
    
	public interface Callbacks {
		public void onVisitIdAvailable(String visitId);
	}

	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onVisitIdAvailable(String visitId) {
		}
	};

	private Callbacks mCallbacks = sDummyCallbacks;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent = BaseActivity.fragmentArgumentsToIntent(getArguments());
        mVisitUri = intent.getData();
        if (mVisitUri == null) {
            return;
        }
    }
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mVisitUri == null) {
            return;
        }
        
        try {
        	visitId = Integer.valueOf(MobileStoreContract.Visits.getVisitId(mVisitUri));
        } catch (NumberFormatException ne) {
        	LogUtils.LOGE(TAG, "", ne);
        }

        
        
        // Start background query to load vendor details
        getLoaderManager().initLoader(VisitsQuery._TOKEN, null, this);
    }
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof Callbacks)) {
            throw new ClassCastException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }
	
    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = sDummyCallbacks;
    }
	
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_record_visit_detail_preview, null);
        mCustomerNoName = (TextView) rootView.findViewById(R.id.visit_customer_no_name);
        //mCustomerNo =  (TextView) rootView.findViewById(R.id.visit_customer_no);
        mVisitDate = (TextView) rootView.findViewById(R.id.visit_date);
        mOdometer = (TextView) rootView.findViewById(R.id.visit_odometer);
        mRecordedTime = (TextView) rootView.findViewById(R.id.visit_time);
        //mDepartureTime = (TextView) rootView.findViewById(R.id.visit_departure_time);
        mNote = (TextView) rootView.findViewById(R.id.visit_note);
        mStartVisit = (Button) rootView.findViewById(R.id.start_visit_button);
        mStartVisit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showArrivalDialog();
			}
		});
        mEndVisit = (Button) rootView.findViewById(R.id.end_visit_button);
        mEndVisit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDepartureDialog();
			}
		});
        return rootView; 
    }
	
    private void showArrivalDialog() {
    	EditFieldDialog dialog = new EditFieldDialog(RecordVisitsMultipaneActivity.RECORD_VISIT_ARRIVAL, "Evidencija dolaska", "Unesite kilometražu", InputType.TYPE_CLASS_NUMBER);
    	dialog.show(getActivity().getSupportFragmentManager(), "ARRIVAL_RECORD_DIALOG");		
	}
    
    private void showDepartureDialog() {
    	EditDepartureVisitDialog dialog = new EditDepartureVisitDialog();
    	dialog.show(getActivity().getSupportFragmentManager(), "DEPARTURE_RECORD_DIALOG");		
	}
    
    private void save() {
    	recordVisit(odometerValue);
		isDialogValueSent = false;
    }
    
    public boolean checkForRecordedVisit() {
		Cursor cursor = getActivity().getContentResolver().query(MobileStoreContract.Visits.CONTENT_URI, new String[] { MobileStoreContract.Visits._ID }, "visits._id=? and visits.visit_type=?", new String[] { String.valueOf(visitId), String.valueOf(1) }, null);
		
		if (cursor.moveToFirst()) {
			return true;
		}
		
		return false;
	}

    public boolean checkNotForRecordedVisit() {
		Cursor cursor = getActivity().getContentResolver().query(MobileStoreContract.Visits.CONTENT_URI, new String[] { MobileStoreContract.Visits._ID }, "visits._id=? and visits.visit_type=?", new String[] { String.valueOf(visitId), String.valueOf(0) }, null);
		
		if (cursor.moveToFirst()) {
			return true;
		}
		
		return false;
	}
    
	public boolean recordVisit(int odometer) {
		ContentValues cv = new ContentValues();
		
		//cv.putNull(MobileStoreContract.Visits.CUSTOMER_ID);
		if (odometer == -1) {
			cv.putNull(MobileStoreContract.Visits.ODOMETER);
		} else {
			cv.put(MobileStoreContract.Visits.ODOMETER, odometer);
		}
		//cv.put(MobileStoreContract.Visits.VISIT_RESULT, visitSubType);
		//cv.put(MobileStoreContract.Visits.VISIT_TYPE, 1);
		Date newDate = new Date();
		cv.put(Visits.VISIT_DATE, DateUtils.toDbDate(newDate));
		cv.put(Visits.ARRIVAL_TIME, DateUtils.toDbDate(newDate));
		//cv.put(Visits.DEPARTURE_TIME, DateUtils.toDbDate(newDate));
		getActivity().getContentResolver().update(MobileStoreContract.Visits.CONTENT_URI, cv, "visits._id=?", new String[] { String.valueOf(visitId) });
		
		return true;
	}
    
	public boolean recordVisit(int visit_result, String note) {
		ContentValues cv = new ContentValues();
		
		//cv.putNull(MobileStoreContract.Visits.CUSTOMER_ID);
//		if (odometer == -1) {
//			cv.putNull(MobileStoreContract.Visits.ODOMETER);
//		} else {
//			cv.put(MobileStoreContract.Visits.ODOMETER, odometer);
//		}
		cv.put(MobileStoreContract.Visits.VISIT_RESULT, visit_result);
		cv.put(MobileStoreContract.Visits.NOTE, note);
		cv.put(MobileStoreContract.Visits.VISIT_TYPE, 1);
		Date newDate = new Date();
//		cv.put(Visits.VISIT_DATE, DateUtils.toDbDate(newDate));
//		cv.put(Visits.ARRIVAL_TIME, DateUtils.toDbDate(newDate));
		cv.put(Visits.DEPARTURE_TIME, DateUtils.toDbDate(newDate));
		getActivity().getContentResolver().update(MobileStoreContract.Visits.CONTENT_URI, cv, "visits._id=?", new String[] { String.valueOf(visitId) });
		
		return true;
	}
	
    public void buildUiFromCursor(Cursor cursor) {
        if (getActivity() == null) {
            return;
        }

        if (!cursor.moveToFirst()) {
            return;
        }
        
        String customerNameString = cursor.getString(VisitsQuery.NAME);
        String customerNoString = cursor.getString(VisitsQuery.CUSTOMER_NO);
        
        if (customerNoString == null || customerNoString.length() < 1) {
        	customerNoString = "NEPOZNAT KUPAC";
        	customerNameString = "-";
    	} else {
    		customerNoString = customerNoString + " - " + customerNameString;
    	}
        mCustomerNoName.setText(customerNoString);
        
        String visitDate = "-";
        if (!cursor.isNull(VisitsQuery.VISIT_DATE)) { 
        	visitDate = DateUtils.formatDbDateForPresentation(cursor.getString(VisitsQuery.VISIT_DATE));
        }
        mVisitDate.setText(visitDate);
        
        String odometerVal = "-";
        if (!cursor.isNull(VisitsQuery.ODOMETER)) { 
        	odometerVal = String.valueOf(cursor.getInt(VisitsQuery.ODOMETER));
        }
        mOdometer.setText(odometerVal);
        
        String arrivalTime = "\\";
        if (!cursor.isNull(VisitsQuery.ARRIVAL_TIME)) { 
        	arrivalTime = DateUtils.formatDbTimeForPresentation(cursor.getString(VisitsQuery.ARRIVAL_TIME));
        }
        
        String departureTime = "\\";
        if (!cursor.isNull(VisitsQuery.DEPARTURE_TIME)) { 
        	departureTime = DateUtils.formatDbTimeForPresentation(cursor.getString(VisitsQuery.DEPARTURE_TIME));
        }
        mRecordedTime.setText(arrivalTime + " - " + departureTime);
        
        String note = "-";
        if (!cursor.isNull(VisitsQuery.NOTE)) {
        	note =  cursor.getString(VisitsQuery.NOTE);
        }
        mNote.setText(note);

        int visitId = cursor.getInt(VisitsQuery._ID);
        mCallbacks.onVisitIdAvailable(String.valueOf(visitId));
        
        LogUtils.LOGI(TAG, "Loaded visit id: " + String.valueOf(visitId));
    }
    
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle data) {
		return new CursorLoader(getActivity(), mVisitUri, VisitsQuery.PROJECTION, null, null,
                null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		buildUiFromCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}
	
	private interface VisitsQuery {
		int _TOKEN = 0x4;

        String[] PROJECTION = {
                BaseColumns._ID,
                MobileStoreContract.Visits.SALES_PERSON_ID,
                MobileStoreContract.Visits.CUSTOMER_ID,
                MobileStoreContract.Visits.VISIT_DATE,
                MobileStoreContract.Visits.CUSTOMER_NO,
                MobileStoreContract.Visits.NAME,
                MobileStoreContract.Visits.NAME_2,
                MobileStoreContract.Visits.ARRIVAL_TIME,
                MobileStoreContract.Visits.DEPARTURE_TIME,
                MobileStoreContract.Visits.ODOMETER,
                MobileStoreContract.Visits.NOTE
        };

        int _ID = 0;
//        int SALES_PERSON_ID = 1;
//        int CUSTOMER_ID = 2;
        int VISIT_DATE = 3;
        int CUSTOMER_NO = 4;
        int NAME = 5;
//        int NAME_2 = 6;
        int ARRIVAL_TIME = 7;
        int DEPARTURE_TIME = 8;
        int ODOMETER = 9;
        int NOTE = 10; 
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		if (isDialogValueSent) {
			if (!checkForRecordedVisit())  {
				save();
			}
		}
	}
}
