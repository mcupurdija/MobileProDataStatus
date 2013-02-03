package rs.gopro.mobile_store.ui;

import static rs.gopro.mobile_store.util.LogUtils.makeLogTag;
import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.util.LogUtils;
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

public class SaleOrderLinesPreviewListFragment extends ListFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = makeLogTag(SaleOrderLinesPreviewListFragment.class);
	
	private static final String STATE_SELECTED_ID = "selectedId";
	
	private Uri mSaleOrderLinesUri;
	private CursorAdapter mAdapter;
	private String mSelectedSaleOrderId;
	private boolean mHasSetEmptyText = false;
	
	public interface Callbacks {
		public void onSaleOrderIdAvailable(String saleOrderId);
	}

	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onSaleOrderIdAvailable(String saleOrderId) {
		}
	};

	private Callbacks mCallbacks = sDummyCallbacks;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

        final Intent intent = BaseActivity.fragmentArgumentsToIntent(getArguments());
        mSaleOrderLinesUri = intent.getData();
        if (mSaleOrderLinesUri == null) {
            return;
        }
        
        reloadFromArguments(getArguments());
        
        LogUtils.LOGI(TAG, "Created SaleOrderLinesPreviewListFragment");
    }

    public void reloadFromArguments(Bundle arguments) {
    	// Teardown from previous arguments
        setListAdapter(null);

        // Load new arguments
        final Intent intent = BaseActivity.fragmentArgumentsToIntent(arguments);
        mSaleOrderLinesUri = intent.getData();
        final int saleOrdersQueryToken;

        if (mSaleOrderLinesUri == null) {
            return;
        }

        mAdapter = new SaleOrderLinesAdapter(getActivity());
        saleOrdersQueryToken = SaleOrderLinesQuery._TOKEN;

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
            setEmptyText(getString(R.string.empty_sale_order_lines));
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
    	// TODO no need for implementation, but can be cool if think something out, like popup
    }
    
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new CursorLoader(getActivity(), mSaleOrderLinesUri, SaleOrderLinesQuery.PROJECTION, null, null,
				MobileStoreContract.SaleOrderLines.DEFAULT_SORT);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (getActivity() == null) {
            return;
        }
        int token = loader.getId();
        if (token == SaleOrderLinesQuery._TOKEN) {
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
	 * @param id
	 */
	public void setSelectedSalesOrderId(String id) {
		mSelectedSaleOrderId = id;
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }
	
    /**
     * {@link CursorAdapter} that renders a {@link SaleOrderLinesQuery}.
     */
    private class SaleOrderLinesAdapter extends CursorAdapter {
        public SaleOrderLinesAdapter(Context context) {
            super(context, null, false);
        }

        /** {@inheritDoc} */
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return getActivity().getLayoutInflater().inflate(R.layout.list_item_sale_order_lines_preview,
                    parent, false);
        }

        /** {@inheritDoc} */
        @Override
        public void bindView(View view, Context context, Cursor cursor) {
//            UIUtils.setActivatedCompat(view, cursor.getString(VisitsQuery.VENDOR_ID)
//                    .equals(mSelectedVendorId));
        	//mSelectedSaleOrderId = cursor.getString(SaleOrderLinesQuery.SALE_ORDER_ID);
            view.setActivated(String.valueOf(cursor.getInt(SaleOrderLinesQuery.SALE_ORDER_ID))
                    .equals(mSelectedSaleOrderId));
            
            final TextView timeView = (TextView) view.findViewById(R.id.block_time);
            final TextView titleView = (TextView) view.findViewById(R.id.block_title);
            final TextView subtitleView = (TextView) view.findViewById(R.id.block_subtitle);
            String salesOrderDate = cursor.getString(SaleOrderLinesQuery.LINE_NO);
            String salesOrderNo = cursor.getString(SaleOrderLinesQuery.ITEM_NO) + " - " + cursor.getString(SaleOrderLinesQuery.DESCRIPTION);
            String salesOrderCust = "Količina: "+String.valueOf(cursor.getDouble(SaleOrderLinesQuery.QUANTITY)) + "  -  Cena:" + String.valueOf(cursor.getDouble(SaleOrderLinesQuery.PRICE_EUR)) + "  -  Popust:" + String.valueOf(cursor.getDouble(SaleOrderLinesQuery.REAL_DISCOUNT));
            timeView.setText(salesOrderDate);
            titleView.setText(salesOrderNo);
            subtitleView.setText(salesOrderCust);
            mCallbacks.onSaleOrderIdAvailable(cursor.getString(SaleOrderLinesQuery.SALE_ORDER_ID));
        }
    }
	
	private interface SaleOrderLinesQuery {
		int _TOKEN = 0x6;

        String[] PROJECTION = {
                BaseColumns._ID,
                MobileStoreContract.SaleOrderLines.SALE_ORDER_ID,
                MobileStoreContract.SaleOrderLines.ITEM_NO,
                MobileStoreContract.SaleOrderLines.DESCRIPTION,
                MobileStoreContract.SaleOrderLines.DESCRIPTION2,
                MobileStoreContract.SaleOrderLines.LINE_NO,
                MobileStoreContract.SaleOrderLines.QUANTITY,
                MobileStoreContract.SaleOrderLines.PRICE_EUR,
                MobileStoreContract.SaleOrderLines.REAL_DISCOUNT
        };

        int _ID = 0;
        int SALE_ORDER_ID = 1;
        int ITEM_NO = 2;
        int DESCRIPTION = 3;
        //int DESCRIPTION2 = 4;
        int LINE_NO = 5;
        int QUANTITY = 6;
        int PRICE_EUR = 7;
        int REAL_DISCOUNT = 8;
	}

}
