package rs.gopro.mobile_store.ui;

import static rs.gopro.mobile_store.util.LogUtils.makeLogTag;
import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.ui.widget.MainContextualActionBarCallback;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class VisitListFromMenuFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>, OnItemLongClickListener {

	private static final String TAG = makeLogTag(VisitListFromMenuFragment.class);

	private static final String STATE_SELECTED_ID = "selectedId";

	private Uri mVisitsUri;
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
		final Intent intent = BaseActivity.fragmentArgumentsToIntent(arguments);
		mVisitsUri = intent.getData();
		final int visitQueryToken;

		if (mVisitsUri == null) {
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
		final Cursor cursor = (Cursor) mAdapter.getItem(position);
		String visitId = String.valueOf(cursor.getInt(VisitsQuery._ID));
		mSelectedVisitId = visitId;
		mAdapter.notifyDataSetChanged();
		final Uri visitsUri = MobileStoreContract.Visits.CONTENT_URI;
		final Intent intent = new Intent(Intent.ACTION_VIEW, MobileStoreContract.Visits.buildVisitUri(visitId));
		intent.putExtra(VisitsMultipaneActivity.EXTRA_MASTER_URI, visitsUri);
		startActivity(intent);
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
			view.setActivated(String.valueOf(cursor.getInt(VisitsQuery._ID)).equals(mSelectedVisitId));
			((TextView) view.findViewById(R.id.visit_title)).setText(UIUtils.formatDate(UIUtils.getDateTime(cursor.getString(VisitsQuery.VISIT_DATE))));
			String customer_no = cursor.getString(VisitsQuery.CUSTOMER_NO);
			String customer_name = cursor.getString(VisitsQuery.CUSTOMER_NAME);//  + cursor.getString(VisitsQuery.CUSTOMER_NAME2);
        	if (customer_no == null || customer_no.length() < 1) {
        		customer_no = "NEPOZNAT KUPAC";
        		customer_name = "-";
        	}
        	int visit_type = cursor.getInt(VisitsQuery.VISIT_TYPE);
        	String status = cursor.getString(VisitsQuery.VISIT_RESULT);
        	if (visit_type == 0) {
        		status = "PLAN";
        	} else {
        		status = "REALIZACIJA";
        	}
        	
			((TextView) view.findViewById(R.id.visit_subtitle1)).setText(customer_no);
			((TextView) view.findViewById(R.id.visit_subtitle2)).setText(customer_name);
			((TextView) view.findViewById(R.id.visit_status)).setText(status);
		}
	}

	private interface VisitsQuery {
		int _TOKEN = 0x1;

		String[] PROJECTION = { BaseColumns._ID, MobileStoreContract.Visits.SALES_PERSON_ID, MobileStoreContract.Visits.CUSTOMER_ID, MobileStoreContract.Visits.CUSTOMER_NO, MobileStoreContract.Visits.NAME, MobileStoreContract.Visits.NAME_2,
				MobileStoreContract.Visits.VISIT_DATE, MobileStoreContract.Visits.VISIT_RESULT, MobileStoreContract.Visits.VISIT_TYPE };

		int _ID = 0;
//		int SALES_PERSON_ID = 1;
//		int CUSTOMER_ID = 2;
		int CUSTOMER_NO = 3;
		int CUSTOMER_NAME = 4;
//		int CUSTOMER_NAME2 = 5;
		int VISIT_DATE = 6;
		int VISIT_RESULT = 7;
		int VISIT_TYPE = 8;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		final Cursor cursor = (Cursor) mAdapter.getItem(position);
		String visitId = String.valueOf(cursor.getInt(VisitsQuery._ID));
		String visitType = String.valueOf(cursor.getInt(VisitsQuery.VISIT_TYPE));
		actionBarCallback.onLongClickItem(visitId, visitType);
		return true;
	}

}
