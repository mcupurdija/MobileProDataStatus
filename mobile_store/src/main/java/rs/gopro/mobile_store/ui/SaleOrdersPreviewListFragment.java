package rs.gopro.mobile_store.ui;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
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
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import static rs.gopro.mobile_store.util.LogUtils.makeLogTag;

public class SaleOrdersPreviewListFragment extends ListFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = makeLogTag(SaleOrdersPreviewListFragment.class);
	
	private static final String STATE_SELECTED_ID = "selectedId";
	
	private Uri mSaleOrdersUri;
	private CursorAdapter mAdapter;
	private String mSelectedSaleOrderId;
	private boolean mHasSetEmptyText = false;
	
	private TextView mHeader1;
	private TextView mHeader2;
	private TextView mHeader3;
	private TextView mHeader4;
	
	public interface Callbacks {
        /** Return true to select (activate) the vendor in the list, false otherwise. */
        public boolean onSaleOrderSelected(String saleOrderId);
    }

    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public boolean onSaleOrderSelected(String saleOrderId) {
            return true;
        }
    };

    private Callbacks mCallbacks = sDummyCallbacks;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mSelectedSaleOrderId = savedInstanceState.getString(STATE_SELECTED_ID);
        }

        reloadFromArguments(getArguments());
    }

    public void reloadFromArguments(Bundle arguments) {
        // Teardown from previous arguments
        setListAdapter(null);

        // Load new arguments
        final Intent intent = BaseActivity.fragmentArgumentsToIntent(arguments);
        mSaleOrdersUri = intent.getData();
        final int saleOrdersQueryToken;

        if (mSaleOrdersUri == null) {
            return;
        }

        mAdapter = new SaleOrdersAdapter(getActivity());
        saleOrdersQueryToken = SaleOrdersQuery._TOKEN;

        setListAdapter(mAdapter);

        // Start background query to load vendors
        getLoaderManager().initLoader(saleOrdersQueryToken, null, this);
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setBackgroundColor(Color.WHITE);
        final ListView listView = getListView();
        listView.setSelector(android.R.color.transparent);
        listView.setCacheColorHint(Color.WHITE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!mHasSetEmptyText) {
            // Could be a bug, but calling this twice makes it become visible
            // when it shouldn't be visible.
            setEmptyText(getString(R.string.empty_sale_orders));
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
        if (mSelectedSaleOrderId != null) {
            outState.putString(STATE_SELECTED_ID, mSelectedSaleOrderId);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        final Cursor cursor = (Cursor) mAdapter.getItem(position);
        
//        mHeader1 = (TextView) v.findViewById(R.id.fragment_sale_order_lines_list_header1);
//        mHeader2 = (TextView) v.findViewById(R.id.fragment_sale_order_lines_list_header2);
//        mHeader3 = (TextView) v.findViewById(R.id.fragment_sale_order_lines_list_header3);
//        mHeader4 = (TextView) v.findViewById(R.id.fragment_sale_order_lines_list_header4);
//           
//        String salesOrderDate = cursor.getString(SaleOrdersQuery.ORDER_DATE);
//        String salesOrderNo = cursor.getString(SaleOrdersQuery.SALES_ORDER_NO);
//        String customer = cursor.getString(SaleOrdersQuery.CUSTOMER_NO) + " " + cursor.getString(SaleOrdersQuery.CUSTOMER_NAME) + " " + cursor.getString(SaleOrdersQuery.CUSTOMER_NAME2);
//        
//        mHeader1.setText(salesOrderDate + " - " + salesOrderNo);
//        mHeader2.setText(customer);
        
        String saleOrderId = String.valueOf(cursor.getInt(SaleOrdersQuery._ID));
        if (mCallbacks.onSaleOrderSelected(saleOrderId)) {
            mSelectedSaleOrderId = saleOrderId;
            mAdapter.notifyDataSetChanged();
        }
    }
    
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new CursorLoader(getActivity(), mSaleOrdersUri, SaleOrdersQuery.PROJECTION, null, null,
				MobileStoreContract.SaleOrders.DEFAULT_SORT);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (getActivity() == null) {
            return;
        }
        
        int token = loader.getId();
        if (token == SaleOrdersQuery._TOKEN) {
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
	public void setSelectedSalesOrderId(String id) {
		mSelectedSaleOrderId = id;
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }
	
    /**
     * {@link CursorAdapter} that renders a {@link SaleOrdersQuery}.
     */
    private class SaleOrdersAdapter extends CursorAdapter {
        public SaleOrdersAdapter(Context context) {
            super(context, null, false);
        }

        /** {@inheritDoc} */
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return getActivity().getLayoutInflater().inflate(R.layout.list_item_sale_orders_preview,
                    parent, false);
        }

        /** {@inheritDoc} */
        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            view.setActivated(String.valueOf(cursor.getInt(SaleOrdersQuery._ID))
                    .equals(mSelectedSaleOrderId));
            final TextView timeView = (TextView) view.findViewById(R.id.block_time);
            final TextView titleView = (TextView) view.findViewById(R.id.block_title);
            final TextView subtitleView = (TextView) view.findViewById(R.id.block_subtitle);
            String salesOrderDate = cursor.getString(SaleOrdersQuery.ORDER_DATE);
            String salesOrderFormatDate = UIUtils.formatDate(UIUtils.getDate(salesOrderDate));
            String salesOrderNo = cursor.getString(SaleOrdersQuery.SALES_ORDER_NO);
            String salesOrderCust = cursor.getString(SaleOrdersQuery.CUSTOMER_NO) + " " + cursor.getString(SaleOrdersQuery.CUSTOMER_NAME) + " " + cursor.getString(SaleOrdersQuery.CUSTOMER_NAME2);
            timeView.setText(salesOrderFormatDate);
            titleView.setText(salesOrderNo);
            subtitleView.setText(salesOrderCust);
        }
    }
	
	private interface SaleOrdersQuery {
		int _TOKEN = 0x5;

        String[] PROJECTION = {
                BaseColumns._ID,
                MobileStoreContract.SaleOrders.SALES_PERSON_ID,
                MobileStoreContract.SaleOrders.SALES_ORDER_NO,
                MobileStoreContract.SaleOrders.CUSTOMER_ID,
                MobileStoreContract.SaleOrders.CUSTOMER_NO,
                MobileStoreContract.SaleOrders.NAME,
                MobileStoreContract.SaleOrders.NAME_2,
                MobileStoreContract.SaleOrders.ORDER_DATE
        };

        int _ID = 0;
        int SALES_PERSON_ID = 1;
        int SALES_ORDER_NO = 2;
        int CUSTOMER_ID = 3;
        int CUSTOMER_NO = 4;
        int CUSTOMER_NAME = 5;
        int CUSTOMER_NAME2 = 6;
        int ORDER_DATE = 7;
	}

}