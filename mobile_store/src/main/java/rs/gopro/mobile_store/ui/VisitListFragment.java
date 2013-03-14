package rs.gopro.mobile_store.ui;

import static rs.gopro.mobile_store.util.LogUtils.makeLogTag;

import java.util.ArrayList;
import java.util.List;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.ui.widget.SimpleSelectionedListAdapter;
import rs.gopro.mobile_store.util.LogUtils;
import rs.gopro.mobile_store.util.UIUtils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class VisitListFragment extends ListFragment implements
		LoaderManager.LoaderCallbacks<Cursor>, OnItemLongClickListener {

	public static final String EXTRA_DATE_FILTER = "rs.gopro.mobile_store.extra.VISIT_DATE_FILTER";
	
	private static final String TAG = makeLogTag(VisitListFragment.class);
	
	private static final String STATE_SELECTED_ID = "selectedId";
	
	private String mDateFilter = null;
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
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			mSelectedVisitId = savedInstanceState.getString(STATE_SELECTED_ID);
		}

		reloadFromArguments(getArguments());
		
		LogUtils.LOGI(TAG, "Fragment created.");
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

        mDateFilter = intent.getStringExtra(EXTRA_DATE_FILTER);
        
        mVisitAdapter = new VisitsAdapter(getActivity());
        visitQueryToken = VisitsQuery._TOKEN;

        mSeparatorAdapter = new SimpleSelectionedListAdapter(getActivity(),
				R.layout.list_item_report_document_header, mVisitAdapter);
        
        setListAdapter(mSeparatorAdapter);

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
        listView.setOnItemLongClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!mHasSetEmptyText) {
            // Could be a bug, but calling this twice makes it become visible
            // when it shouldn't be visible.
            setEmptyText(getString(R.string.empty_visits));
            mHasSetEmptyText = true;
        }
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
	public boolean onItemLongClick(AdapterView<?> adapter, View view, int position, long arg3) {
    	return true;
	}
    
    
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		if (mDateFilter != null) {
			return new CursorLoader(getActivity(), mVisitsUri, VisitsQuery.PROJECTION, "DATE("+Tables.VISITS+"."+MobileStoreContract.Visits.VISIT_DATE+")>=DATE(?)", new String[] { mDateFilter },
	                MobileStoreContract.Visits.DEFAULT_SORT);
		} else {
			return new CursorLoader(getActivity(), mVisitsUri, VisitsQuery.PROJECTION, null, null,
	                MobileStoreContract.Visits.DEFAULT_SORT);
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (getActivity() == null) {
            return;
        }
        int token = loader.getId();
        if (token == VisitsQuery._TOKEN) {
        	List<SimpleSelectionedListAdapter.Section> sections = new ArrayList<SimpleSelectionedListAdapter.Section>();
        	cursor.moveToFirst();
    		long previouspostingDate = -1;
    		long postingDate;
    		while (!cursor.isAfterLast()) {
    			postingDate = UIUtils.getDateTime(
    					cursor.getString(VisitsQuery.VISIT_DATE)).getTime();
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
     * {@link CursorAdapter} that renders a {@link VisitsQuery}.
     */
    private class VisitsAdapter extends CursorAdapter {
        public VisitsAdapter(Context context) {
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
        	final String visit_id = String.valueOf(cursor.getInt(VisitsQuery._ID));
        	if (visit_id.equals(mSelectedVisitId)) {
        		view.setActivated(true);
        		mCallbacks.onVisitSelected(String.valueOf(cursor.getInt(VisitsQuery._ID)));
        	} else {
        		view.setActivated(false);
        	}
        	String customer_no = cursor.getString(VisitsQuery.CUSTOMER_NO);
        	String customer_name = cursor.getString(VisitsQuery.CUSTOMER_NAME);//  + cursor.getString(VisitsQuery.CUSTOMER_NAME2);
        	final int visit_type = cursor.getInt(VisitsQuery.VISIT_TYPE);
        	if (customer_no == null || customer_no.length() < 1) {
        		customer_no = "NEPOZNAT KUPAC";
        		customer_name = "-";
        	}
        	
        	String status = cursor.getString(VisitsQuery.VISIT_RESULT);
        	if (visit_type == 0) {
        		status = "PLAN";
        	} else {
        		status = "REALIZACIJA";
        	}

			((TextView) view.findViewById(R.id.visit_subtitle1)).setText(customer_no);
			((TextView) view.findViewById(R.id.visit_subtitle2)).setText(customer_name);
            ((TextView) view.findViewById(R.id.visit_title)).setText(
                    UIUtils.formatDate(UIUtils.getDateTime(cursor.getString(VisitsQuery.VISIT_DATE))));
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
	
	private interface VisitsQuery {
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
                MobileStoreContract.Visits.VISIT_TYPE
        };

        int _ID = 0;
        //int SALES_PERSON_ID = 1;
        //int CUSTOMER_ID = 2;
        int CUSTOMER_NO = 3;
        int CUSTOMER_NAME = 4;
        int CUSTOMER_NAME2 = 5;
        int VISIT_DATE = 6;
        int VISIT_RESULT = 7;
        int VISIT_TYPE = 8;
	}

	

	

}
