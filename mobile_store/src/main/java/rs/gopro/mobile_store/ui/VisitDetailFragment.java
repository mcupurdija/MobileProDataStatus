package rs.gopro.mobile_store.ui;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.util.LogUtils;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class VisitDetailFragment extends Fragment implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = LogUtils.makeLogTag(VisitDetailFragment.class);
	
	private Uri mVisitUri;
	
    private TextView mCustomerName;
    private TextView mCustomerNo;
    private TextView mVisitDate;
    private TextView mOdometer;
    private TextView mArrivalTime;
    private TextView mDepartureTime;
    private TextView mNote;
    
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

        // Start background query to load vendor details
        getLoaderManager().initLoader(VisitsQuery._TOKEN, null, this);
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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.id.fragment_visitsplan_detail, null);
        mCustomerName = (TextView) rootView.findViewById(R.id.visit_customer_name);
        mCustomerNo = (TextView) rootView.findViewById(R.id.visit_customer_no);
        mVisitDate = (TextView) rootView.findViewById(R.id.visit_date);
        mOdometer = (TextView) rootView.findViewById(R.id.visit_odometer_value);
        mArrivalTime = (TextView) rootView.findViewById(R.id.visit_arrival_time);
        mDepartureTime = (TextView) rootView.findViewById(R.id.visit_departure_time);
        mNote = (TextView) rootView.findViewById(R.id.visit_note);
        return rootView;
    }
	
    public void buildUiFromCursor(Cursor cursor) {
        if (getActivity() == null) {
            return;
        }

        if (!cursor.moveToFirst()) {
            return;
        }
        
        String customerNameString = cursor.getString(VisitsQuery.NAME) + " " + cursor.getString(VisitsQuery.NAME_2);
        mCustomerName.setText(customerNameString);
        
        String customerNoString = cursor.getString(VisitsQuery.CUSTOMER_NO);
        mCustomerNo.setText(customerNoString);
        
        String visitDate = cursor.getString(VisitsQuery.VISIT_DATE);
        mVisitDate.setText(visitDate);
        
        int odometerVal = cursor.getInt(VisitsQuery.ODOMETER);
        mOdometer.setText(String.valueOf(odometerVal));
        
        String arrivalTime = cursor.getString(VisitsQuery.ARRIVAL_TIME);
        mArrivalTime.setText(arrivalTime);
        
        String departureTime = cursor.getString(VisitsQuery.DEPARTURE_TIME);
        mDepartureTime.setText(departureTime);
        
        String note = cursor.getString(VisitsQuery.NOTE);
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
        int SALES_PERSON_ID = 1;
        int CUSTOMER_ID = 2;
        int VISIT_DATE = 3;
        int CUSTOMER_NO = 4;
        int NAME = 5;
        int NAME_2 = 6;
        int ARRIVAL_TIME = 7;
        int DEPARTURE_TIME = 8;
        int ODOMETER = 9;
        int NOTE = 10; 
	}
}
