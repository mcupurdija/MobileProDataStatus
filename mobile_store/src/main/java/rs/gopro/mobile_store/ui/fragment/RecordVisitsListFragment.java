package rs.gopro.mobile_store.ui.fragment;

import static rs.gopro.mobile_store.util.LogUtils.makeLogTag;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.ui.BaseActivity;
import rs.gopro.mobile_store.ui.widget.SimpleSelectionedListAdapter;
import rs.gopro.mobile_store.util.LogUtils;
import rs.gopro.mobile_store.util.UIUtils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class RecordVisitsListFragment extends ListFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {

	public static final String EXTRA_DATE_FILTER = "rs.gopro.mobile_store.extra.RECOD_VISITS_DATE_FILTER";
	private static final String TAG = makeLogTag(RecordVisitsListFragment.class);
	private static final String STATE_SELECTED_ID = "selectedId";
//	private static final String VISITS_TYPE_FILTER = Tables.VISITS+"."+MobileStoreContract.Visits.VISIT_TYPE+"=?";
	private static final String VISITS_DATE_FILTER = "DATE("+Tables.VISITS+"."+MobileStoreContract.Visits.VISIT_DATE+")=DATE(?)";
	
	private String mDateFilterValue = null;
	private Uri mVisitsUri;
	private SimpleSelectionedListAdapter mSeparatorAdapter;
	private CursorAdapter mVisitAdapter;
	private String mSelectedVisitId;
	private boolean mHasSetEmptyText = false;
	
	public interface Callbacks {
        /** Return true to select (activate) the vendor in the list, false otherwise. */
        public boolean onVisitSelected(String visitId);
        
        /** Pass selected visitId and initialize  contextual menu */
        public void onVisitLongClick(String visitId, String visitType);
    }

    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public boolean onVisitSelected(String visitId) {
            return true;
        }

		@Override
		public void onVisitLongClick(String visitId, String visitType) {
		}
    };

    private Callbacks mCallbacks = sDummyCallbacks;
	
	public RecordVisitsListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			mSelectedVisitId = savedInstanceState.getString(STATE_SELECTED_ID);
		}

		reloadFromArguments(getArguments());
	}
	
    public void reloadFromArguments(Bundle arguments) {
        // Teardown from previous arguments
        setListAdapter(null);

        // Load new arguments
        final Intent intent = BaseActivity.fragmentArgumentsToIntent(arguments);
        mVisitsUri = intent.getData();
        final int visitQueryToken;

        if (mVisitsUri == null) {
            return;
        }

        mDateFilterValue = intent.getStringExtra(EXTRA_DATE_FILTER);
        
        mVisitAdapter = new RecordVisitsAdapter(getActivity());
        visitQueryToken = RecordVisitsQuery._TOKEN;

        mSeparatorAdapter = new SimpleSelectionedListAdapter(getActivity(),
				R.layout.list_item_report_document_header, mVisitAdapter);
        
        setListAdapter(mSeparatorAdapter);

        // Start background query to load vendors
        getLoaderManager().initLoader(visitQueryToken, null, this);
    }
	
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!mHasSetEmptyText) {
            setEmptyText(getString(R.string.empty_visits));
            mHasSetEmptyText = true;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof Callbacks)) {
        	LogUtils.LOGE(TAG, "Activity must implement fragment's callbacks.");
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mSelectedVisitId != null) {
            outState.putString(STATE_SELECTED_ID, mSelectedVisitId);
        }
    }

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		if (mDateFilterValue == null) {
			mDateFilterValue = rs.gopro.mobile_store.util.DateUtils.toDbDate(new Date());
		}
		return new CursorLoader(getActivity(), mVisitsUri, RecordVisitsQuery.PROJECTION, VISITS_DATE_FILTER, new String[] { mDateFilterValue },
                MobileStoreContract.Visits.DEFAULT_SORT);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (getActivity() == null) {
            return;
        }
        int token = loader.getId();
        if (token == RecordVisitsQuery._TOKEN) {
        	List<SimpleSelectionedListAdapter.Section> sections = new ArrayList<SimpleSelectionedListAdapter.Section>();
        	cursor.moveToFirst();
    		long previouspostingDate = -1;
    		long postingDate;
    		while (!cursor.isAfterLast()) {
    			postingDate = UIUtils.getDateTime(
    					cursor.getString(RecordVisitsQuery.VISIT_DATE)).getTime();
    			if (!UIUtils.isSameDay(previouspostingDate, postingDate)) {
    				String title = DateUtils.formatDateTime(getActivity(),
    						postingDate, DateUtils.FORMAT_ABBREV_MONTH
    								| DateUtils.FORMAT_SHOW_DATE
    								| DateUtils.FORMAT_SHOW_WEEKDAY);
    				sections.add(new SimpleSelectionedListAdapter.Section(cursor
    						.getPosition(), title));

    			}
    			previouspostingDate = postingDate;
    			cursor.moveToNext();
    		}
        	mVisitAdapter.changeCursor(cursor);
        	SimpleSelectionedListAdapter.Section[] dummy = new SimpleSelectionedListAdapter.Section[sections.size()];
        	mSeparatorAdapter.setSections(sections.toArray(dummy));
        } else {
            cursor.close();
        }

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mVisitAdapter.swapCursor(null);
	}

	/**
	 * Sets current position in list from caller.
	 * @param id
	 */
	public void setSelectedVisitId(String id) {
		mSelectedVisitId = id;
        if (mVisitAdapter != null) {
            mVisitAdapter.notifyDataSetChanged();
        }
    }
	
    /**
     * {@link CursorAdapter} that renders a {@link RecordVisitsQuery}.
     */
    private class RecordVisitsAdapter extends CursorAdapter {
        public RecordVisitsAdapter(Context context) {
            super(context, null, false);
        }

        /** {@inheritDoc} */
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return getActivity().getLayoutInflater().inflate(R.layout.list_item_visit,
                    parent, false);
        }

        /** {@inheritDoc} */
        @Override
        public void bindView(View view, Context context, Cursor cursor) {
//            UIUtils.setActivatedCompat(view, cursor.getString(VisitsQuery.VENDOR_ID)
//                    .equals(mSelectedVendorId));
        	final String visit_id = String.valueOf(cursor.getInt(RecordVisitsQuery._ID));
        	if (visit_id.equals(mSelectedVisitId)) {
        		view.setActivated(true);
        		mCallbacks.onVisitSelected(String.valueOf(cursor.getInt(RecordVisitsQuery._ID)));
        	} else {
        		view.setActivated(false);
        	}
        	String arrivalTime = "\\";
	        if (!cursor.isNull(RecordVisitsQuery.ARRIVAL_TIME)) { 
	        	arrivalTime = rs.gopro.mobile_store.util.DateUtils.formatDbTimeForPresentation(cursor.getString(RecordVisitsQuery.ARRIVAL_TIME));
	        }
	        
	        String departureTime = "\\";
	        if (!cursor.isNull(RecordVisitsQuery.DEPARTURE_TIME)) { 
	        	departureTime = rs.gopro.mobile_store.util.DateUtils.formatDbTimeForPresentation(cursor.getString(RecordVisitsQuery.DEPARTURE_TIME));
	        }
        	String customer_no = cursor.getString(RecordVisitsQuery.CUSTOMER_NO);
        	String customer_name = cursor.getString(RecordVisitsQuery.CUSTOMER_NAME);//  + cursor.getString(VisitsQuery.CUSTOMER_NAME2);
        	final int visit_type = cursor.getInt(RecordVisitsQuery.VISIT_TYPE);
        	if (customer_no == null || customer_no.length() < 1) {
        		customer_no = "NEPOZNAT KUPAC";
        		customer_name = "-";
        	}
        	int odometer = -1;
        	if (!cursor.isNull(RecordVisitsQuery.ODOMETER)) {
        		odometer = cursor.getInt(RecordVisitsQuery.ODOMETER);
        	}
        	int visit_result = -1;
        	if (!cursor.isNull(RecordVisitsQuery.VISIT_RESULT)) {
        		visit_result = cursor.getInt(RecordVisitsQuery.VISIT_RESULT);
        	}
        	String status = "";
        	if (visit_type == 0) {
        		status = "PLAN";
        	} else {
        		status = "REALIZACIJA";
        	}
        		
        	if (visit_result == 0) {
        		customer_no = "POČETAK DANA";
        	} else if (visit_result == 4) {
        		customer_no = "KRAJ DANA";
        	} else if (visit_result == 5) {
        		customer_no = "POVRATAK KUĆI";
        	}

			((TextView) view.findViewById(R.id.visit_subtitle1)).setText(customer_no + " " + customer_name);
			((TextView) view.findViewById(R.id.visit_subtitle2)).setText(odometer == -1 ? "-" : "Kilometraža: " + String.valueOf(odometer));
            ((TextView) view.findViewById(R.id.visit_title)).setText(arrivalTime + " - " + departureTime);
            ((TextView) view.findViewById(R.id.visit_subtitle1)).setText(customer_no);
            ((TextView) view.findViewById(R.id.visit_subtitle2)).setText(customer_name);
            ((TextView) view.findViewById(R.id.visit_status)).setText(status);
            
            view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
//			        final Cursor cursor = (Cursor) mVisitAdapter.getItem(position);
//			        String visitId = String.valueOf(cursor.getInt(VisitsQuery._ID));
			        if (mCallbacks.onVisitSelected(visit_id)) {
			            mSelectedVisitId = visit_id;
			            mVisitAdapter.notifyDataSetChanged();
			        }
				}
			});
            OnLongClickListener longClickListener = new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
				   String visitId = visit_id;
				   String visitType = String.valueOf(visit_type);
				   mCallbacks.onVisitLongClick(visitId, visitType);
				   v.setSelected(true);
				   return true;
				}
			};
            view.setOnLongClickListener(longClickListener);
            view.setEnabled(true);
        }
    }
	
	private interface RecordVisitsQuery {
		int _TOKEN = 0x1;

        String[] PROJECTION = {
                BaseColumns._ID,
                MobileStoreContract.Visits.SALES_PERSON_ID,
                MobileStoreContract.Visits.CUSTOMER_ID,
                MobileStoreContract.Visits.CUSTOMER_NO,
                MobileStoreContract.Visits.NAME,
                MobileStoreContract.Visits.NAME_2,
                MobileStoreContract.Visits.VISIT_DATE,
                MobileStoreContract.Visits.VISIT_RESULT,
                MobileStoreContract.Visits.VISIT_TYPE, 
                MobileStoreContract.Visits.ARRIVAL_TIME, 
                MobileStoreContract.Visits.DEPARTURE_TIME, 
                MobileStoreContract.Visits.ODOMETER
        };

        int _ID = 0;
        //int SALES_PERSON_ID = 1;
        //int CUSTOMER_ID = 2;
        int CUSTOMER_NO = 3;
        int CUSTOMER_NAME = 4;
//        int CUSTOMER_NAME2 = 5;
        int VISIT_DATE = 6;
        int VISIT_RESULT = 7;
        int VISIT_TYPE = 8;
        int ARRIVAL_TIME = 9;
		int DEPARTURE_TIME = 10;
		int ODOMETER = 11;
	}
}
