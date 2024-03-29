package rs.gopro.mobile_store.ui;

import static rs.gopro.mobile_store.util.LogUtils.makeLogTag;
import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.util.LogUtils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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

public class CustomersViewListFragment extends ListFragment implements
		LoaderManager.LoaderCallbacks<Cursor>, OnItemLongClickListener {

	private static final String TAG = makeLogTag(CustomersViewListFragment.class);
//	private static final String STANDARD_FILTER = Tables.CUSTOMERS + "." + MobileStoreContract.Customers.SALES_PERSON_ID+"=?";

	private static final String STATE_SELECTED_ID = "selectedId";
	
	private Uri mCustomersListUri;
	private CursorAdapter mAdapter;
	private String mSelectedCustomerId;
	private boolean mHasSetEmptyText = false;
//	private String salesPersonId = "-1";
	
	public interface Callbacks {
        /** Return true to select (activate) the vendor in the list, false otherwise. */
        public boolean onCustomerSelected(String customerId);
        
        public void onCustomerLongClick(String customerId);
    }

    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public boolean onCustomerSelected(String customerId) {
            return true;
        }

		@Override
		public void onCustomerLongClick(String customerId) {
		}
    };

    private Callbacks mCallbacks = sDummyCallbacks;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        salesPersonId = SharedPreferencesUtil.getSalePersonId(getActivity());
        
        if (savedInstanceState != null) {
            mSelectedCustomerId = savedInstanceState.getString(STATE_SELECTED_ID);
        }

        reloadFromArguments(getArguments());
        
        LogUtils.LOGI(TAG, "Fragment created.");
    }

    public void reloadFromArguments(Bundle arguments) {
        // Tear-down from previous arguments
        setListAdapter(null);

        // Load new arguments
        final Intent intent = BaseActivity.fragmentArgumentsToIntent(arguments);
        mCustomersListUri = intent.getData();
        final int customersListQueryToken;

        if (mCustomersListUri == null) {
            return;
        }

        mAdapter = new CustomersListViewAdapter(getActivity());
        customersListQueryToken = CustomersQuery._TOKEN;

        setListAdapter(mAdapter);

        // Start background query to load vendors
        getLoaderManager().initLoader(customersListQueryToken, null, this);
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
            setEmptyText(getString(R.string.empty_customers));
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
        if (mSelectedCustomerId != null) {
            outState.putString(STATE_SELECTED_ID, mSelectedCustomerId);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        final Cursor cursor = (Cursor) mAdapter.getItem(position);
        String customerId = String.valueOf(cursor.getInt(CustomersQuery._ID));
        if (mCallbacks.onCustomerSelected(customerId)) {
            mSelectedCustomerId = customerId;
            mAdapter.notifyDataSetChanged();
        }
    }
    
    @Override
    public boolean onItemLongClick(AdapterView<?> adapter, View view, int position,
    		long arg3) {
    	Cursor cursor = (Cursor) adapter.getItemAtPosition(position);
		String customerId = String.valueOf(cursor.getInt(CustomersQuery._ID));
		mCallbacks.onCustomerLongClick(customerId);
		view.setSelected(true);
		return true;
    }
    
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new CursorLoader(getActivity(), mCustomersListUri, CustomersQuery.PROJECTION, Customers.IS_ACTIVE+"=?", new String[] { "1" }, //STANDARD_FILTER, new String[] { salesPersonId }
                MobileStoreContract.Customers.DEFAULT_SORT);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (getActivity() == null) {
            return;
        }
        int token = loader.getId();
        if (token == CustomersQuery._TOKEN) {
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
	 * @param id
	 */
	public void setSelectedCustomerId(String id) {
		mSelectedCustomerId = id;
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }
	
    /**
     * {@link CursorAdapter} that renders a {@link CustomersQuery}.
     */
    private class CustomersListViewAdapter extends CursorAdapter {
        public CustomersListViewAdapter(Context context) {
            super(context, null, false);
        }

        /** {@inheritDoc} */
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return getActivity().getLayoutInflater().inflate(R.layout.list_item_customer_view,
                    parent, false);
        }

        /** {@inheritDoc} */
        @Override
        public void bindView(View view, Context context, Cursor cursor) {
//            UIUtils.setActivatedCompat(view, cursor.getString(VisitsQuery.VENDOR_ID)
//                    .equals(mSelectedVendorId));
            view.setActivated(String.valueOf(cursor.getInt(CustomersQuery._ID))
                    .equals(mSelectedCustomerId));
            String customer_no = "NOV KUPAC";
			if (!cursor.isNull(CustomersQuery.CUSTOMER_NO)) {
				customer_no = cursor.getString(CustomersQuery.CUSTOMER_NO);
			}
            ((TextView) view.findViewById(R.id.customer_title)).setText(customer_no + " - " + cursor.getString(CustomersQuery.CUSTOMER_NAME));
            ((TextView) view.findViewById(R.id.customer_subtitle)).setText(String.format("%s %s", cursor.getString(CustomersQuery.CUSTOMER_POST_CODE), cursor.getString(CustomersQuery.CUSTOMER_CITY)));
        }
    }
	
	private interface CustomersQuery {
		int _TOKEN = 0x7;

        String[] PROJECTION = {
        		Customers._ID,
                Customers.CUSTOMER_NO, 
                Customers.NAME, 
                Customers.CITY,
                Customers.POST_CODE
        };

        int _ID = 0;
        int CUSTOMER_NO = 1;
        int CUSTOMER_NAME = 2;
        int CUSTOMER_CITY = 3;
        int CUSTOMER_POST_CODE = 4;
	}

}
