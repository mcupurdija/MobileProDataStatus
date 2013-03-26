package rs.gopro.mobile_store.ui;

import static rs.gopro.mobile_store.util.LogUtils.makeLogTag;
import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.ui.widget.MainContextualActionBarCallback;
import rs.gopro.mobile_store.util.DateUtils;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class VisitListFromMenuFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> { //, OnItemLongClickListener
	
	private static final String TAG = makeLogTag(VisitListFromMenuFragment.class);

	private static final String STATE_SELECTED_ID = "selectedId";
//	private static final String VISITS_DATE_FILTER = "DATE("+Tables.VISITS+"."+MobileStoreContract.Visits.VISIT_DATE+")=DATE(?)";

	private Uri mVisitsUri;
//	private String mAction;
	private CursorAdapter mAdapter;
	private String mSelectedVisitId;
	private boolean mHasSetEmptyText = false;

	private MainContextualActionBarCallback actionBarCallback;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			mSelectedVisitId = savedInstanceState.getString(STATE_SELECTED_ID);
		}
		
		reloadFromArguments(getArguments());
		
		LogUtils.LOGI(TAG, "Fragment created");
	}

	public void reloadFromArguments(Bundle arguments) {
		// Teardown from previous arguments
		setListAdapter(null);

		// Load new arguments
		Intent intent = BaseActivity.fragmentArgumentsToIntent(arguments);
		mVisitsUri = intent.getData();
//		mAction = intent.getAction();
		final int visitQueryToken;

		if (mVisitsUri == null) {
			LogUtils.LOGE(TAG, "Uri or action is null!");
			return;
		}

		mAdapter = new VisitsAdapter(getActivity());
		visitQueryToken = VisitsQuery._TOKEN;

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
		actionBarCallback = (MainContextualActionBarCallback) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();
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
		return new CursorLoader(getActivity(), mVisitsUri, VisitsQuery.PROJECTION, null, null, MobileStoreContract.Visits.DEFAULT_SORT);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (getActivity() == null) {
			return;
		}
		int token = loader.getId();
		if (token == VisitsQuery._TOKEN) {
			mAdapter.changeCursor(cursor);
		} else {
			cursor.close();
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
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
			return getActivity().getLayoutInflater().inflate(R.layout.list_item_visit, parent, false);
		}

		/** {@inheritDoc} */
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			// UIUtils.setActivatedCompat(view,
			// cursor.getString(VisitsQuery.VENDOR_ID)
			// .equals(mSelectedVendorId));
			final String visit_id = String.valueOf(cursor.getInt(VisitsQuery._ID));
			view.setActivated(visit_id.equals(mSelectedVisitId));
			String arrivalTime = "\\";
	        if (!cursor.isNull(VisitsQuery.ARRIVAL_TIME)) { 
	        	arrivalTime = DateUtils.formatDbTimeForPresentation(cursor.getString(VisitsQuery.ARRIVAL_TIME));
	        }
	        
	        String departureTime = "\\";
	        if (!cursor.isNull(VisitsQuery.DEPARTURE_TIME)) { 
	        	departureTime = DateUtils.formatDbTimeForPresentation(cursor.getString(VisitsQuery.DEPARTURE_TIME));
	        }
			String visit_date = UIUtils.formatDate(UIUtils.getDateTime(cursor.getString(VisitsQuery.VISIT_DATE)));
			((TextView) view.findViewById(R.id.visit_title)).setText(visit_date + "  " + arrivalTime + " - " + departureTime);
			String customer_no = cursor.getString(VisitsQuery.CUSTOMER_NO);
			String customer_name = cursor.getString(VisitsQuery.CUSTOMER_NAME);//  + cursor.getString(VisitsQuery.CUSTOMER_NAME2);

        	if (customer_no == null || customer_no.length() < 1) {
        		customer_no = "NEPOZNAT KUPAC";
        		customer_name = "-";
        	}
        	final int visit_type = cursor.getInt(VisitsQuery.VISIT_TYPE);
        	int visit_result = -1;
        	if (!cursor.isNull(VisitsQuery.VISIT_RESULT)) {
        		visit_result = cursor.getInt(VisitsQuery.VISIT_RESULT);
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
        	
			((TextView) view.findViewById(R.id.visit_subtitle1)).setText(customer_no);
			((TextView) view.findViewById(R.id.visit_subtitle2)).setText(customer_name);
			((TextView) view.findViewById(R.id.visit_status)).setText(status);
			
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mSelectedVisitId = visit_id;
					mAdapter.notifyDataSetChanged();
					final Uri visitsUri = MobileStoreContract.Visits.CONTENT_URI;
					// mAction is from main menu sent, and it is in par with expected actions in activities
					final Intent intent = new Intent(Intent.ACTION_VIEW, MobileStoreContract.Visits.buildVisitUri(visit_id));
					intent.putExtra(VisitsMultipaneActivity.EXTRA_MASTER_URI, visitsUri);
					startActivity(intent);	
				}
			});
			
			view.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					String visitId = visit_id;
					String visitType = String.valueOf(visit_type);
					actionBarCallback.onLongClickItem(visitId, visitType);
					return true;
				}
			});
			view.setEnabled(true);
		}
	}

	private interface VisitsQuery {
		int _TOKEN = 0x1;

		String[] PROJECTION = { BaseColumns._ID, MobileStoreContract.Visits.SALES_PERSON_ID, MobileStoreContract.Visits.CUSTOMER_ID, MobileStoreContract.Visits.CUSTOMER_NO, MobileStoreContract.Visits.NAME, MobileStoreContract.Visits.NAME_2,
				MobileStoreContract.Visits.VISIT_DATE, MobileStoreContract.Visits.VISIT_RESULT, MobileStoreContract.Visits.VISIT_TYPE, MobileStoreContract.Visits.ARRIVAL_TIME, MobileStoreContract.Visits.DEPARTURE_TIME };

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
	}

//	@Override
//	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
//		return true;
//	}

}
