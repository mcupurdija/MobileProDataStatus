package rs.gopro.mobile_store.ui.fragment;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.ui.BaseActivity;
import rs.gopro.mobile_store.ui.RecordVisitsMultipaneActivity;
import rs.gopro.mobile_store.ui.dialog.EditDepartureVisitDialog;
import rs.gopro.mobile_store.ui.dialog.EditFieldDialog;
import rs.gopro.mobile_store.util.ApplicationConstants;
import rs.gopro.mobile_store.util.DateUtils;
import rs.gopro.mobile_store.util.LogUtils;
import android.app.Activity;
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
		LoaderManager.LoaderCallbacks<Cursor> { //, OnDismissListener 

	private static final String TAG = LogUtils.makeLogTag(RecordVisitDetailFragment.class);
	
	private Uri mVisitUri;
    private TextView mCustomerNoName;
    private TextView mVisitDate;
    private TextView mOdometer;
    private TextView mRecordedTime;
    private TextView mNote;
    
    private Button mStartVisit;
    private Button mEndVisit;
    
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
        
//        try {
//        	visitId = Integer.valueOf(MobileStoreContract.Visits.getVisitId(mVisitUri));
//        } catch (NumberFormatException ne) {
//        	LogUtils.LOGE(TAG, "", ne);
//        }

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
        
        int visit_status = -1;
        if (!cursor.isNull(VisitsQuery.VISIT_STATUS)) {
        	visit_status =  cursor.getInt(VisitsQuery.VISIT_STATUS);
        }
        
        switch (visit_status) {
		case ApplicationConstants.VISIT_STATUS_NEW:
			mStartVisit.setEnabled(true);
			mEndVisit.setEnabled(false);
			break;
		case ApplicationConstants.VISIT_STATUS_STARTED:
			mStartVisit.setEnabled(false);
			mEndVisit.setEnabled(true);
			break;
		case ApplicationConstants.VISIT_STATUS_FINISHED:
			mStartVisit.setEnabled(false);
			mEndVisit.setEnabled(false);
			break;
		default:
			break;
		}
        
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
                MobileStoreContract.Visits.NOTE,
                MobileStoreContract.Visits.VISIT_STATUS
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
        int VISIT_STATUS = 11;
	}
}
